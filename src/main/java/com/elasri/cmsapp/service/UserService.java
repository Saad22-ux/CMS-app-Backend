package com.elasri.cmsapp.service;

import com.elasri.cmsapp.model.User;
import org.springframework.stereotype.Service;
import org.w3c.dom.*;

import java.util.*;

@Service
public class UserService {
    private final String xmlPath = "src/main/resources/data/users.xml";

    private final XmlService xmlService;
    private final XPathService xpathService;
    private final XsltService xsltService;

    public UserService(XsltService xsltService, XmlService xmlService, XPathService xpathService) {
        this.xsltService = xsltService;
        this.xmlService = xmlService;
        this.xpathService = xpathService;
    }

    // ======================
    // READ ALL
    // ======================
    public List<User> getAllUsers() throws Exception {
        Document doc = xmlService.loadXml(xmlPath);
        NodeList nodes = xpathService.evaluate(doc, "//user");

        List<User> users = new ArrayList<>();
        for (int i = 0; i < nodes.getLength(); i++) {
            users.add(mapToUser((Element) nodes.item(i)));
        }
        return users;
    }

    // ======================
    // READ ONE
    // ======================
    public User getUserById(int id) throws Exception {
        Document doc = xmlService.loadXml(xmlPath);
        NodeList nodes = xpathService.evaluate(doc, "//user[@id='" + id + "']");

        if (nodes.getLength() == 0) return null;

        return mapToUser((Element) nodes.item(0));
    }

    // ======================
    // CREATE (CORRIGÉ)
    // ======================

    // 1. Nouvelle logique pour trouver l'ID max
    private int generateId(Document doc) throws Exception {
        // On récupère tous les attributs ID des users
        NodeList idNodes = xpathService.evaluate(doc, "//user/@id");
        int maxId = 0;

        for (int i = 0; i < idNodes.getLength(); i++) {
            try {
                int currentId = Integer.parseInt(idNodes.item(i).getNodeValue());
                if (currentId > maxId) {
                    maxId = currentId;
                }
            } catch (NumberFormatException e) {
                // Ignore les IDs non numériques
            }
        }
        return maxId + 1;
    }

    // 2. Modifié pour retourner User et assigner l'ID correctement
    public User addUser(User user) throws Exception {
        Document doc = xmlService.loadXml(xmlPath);
        Element root = doc.getDocumentElement();

        // GÉNÉRATION DE L'ID AVANT LA CRÉATION XML
        int newId = generateId(doc);
        user.setId(newId);

        Element userEl = doc.createElement("user");
        // Utilisation du nouvel ID généré
        userEl.setAttribute("id", String.valueOf(newId));
        userEl.setAttribute("role", user.getRole());

        append(doc, userEl, "name", user.getName());
        append(doc, userEl, "email", user.getEmail());

        if (user.getCourseIds() != null && !user.getCourseIds().isEmpty()) {
            Element coursesEl = doc.createElement("courses");
            for (Integer cid : user.getCourseIds()) {
                Element ref = doc.createElement("courseRef");
                ref.setAttribute("id", String.valueOf(cid));
                coursesEl.appendChild(ref);
            }
            userEl.appendChild(coursesEl);
        }

        root.appendChild(userEl);
        xmlService.saveXml(doc, xmlPath);

        // Retourne l'utilisateur avec son nouvel ID correct
        return user;
    }

    // ======================
    // UPDATE
    // ======================
    public boolean updateUser(User user) throws Exception {
        Document doc = xmlService.loadXml(xmlPath);
        NodeList nodes = xpathService.evaluate(doc, "//user[@id='" + user.getId() + "']");

        if (nodes.getLength() == 0) return false;

        Element e = (Element) nodes.item(0);

        // Mise à jour attributs
        e.setAttribute("role", user.getRole());

        // Mise à jour Name et Email
        NodeList nameList = e.getElementsByTagName("name");
        if(nameList.getLength() > 0) nameList.item(0).setTextContent(user.getName());

        NodeList emailList = e.getElementsByTagName("email");
        if(emailList.getLength() > 0) emailList.item(0).setTextContent(user.getEmail());

        // update courses : Supprimer l'ancien noeud <courses>
        NodeList oldCourses = e.getElementsByTagName("courses");
        if (oldCourses.getLength() > 0) {
            e.removeChild(oldCourses.item(0));
        }

        // Créer le nouveau noeud <courses> si nécessaire
        if (user.getCourseIds() != null && !user.getCourseIds().isEmpty()) {
            Element coursesEl = doc.createElement("courses");
            for (Integer cid : user.getCourseIds()) {
                Element ref = doc.createElement("courseRef");
                ref.setAttribute("id", String.valueOf(cid));
                coursesEl.appendChild(ref);
            }
            e.appendChild(coursesEl);
        } else {
            // Assurer que la liste n'est pas null pour le retour Java
            user.setCourseIds(new ArrayList<>());
        }

        xmlService.saveXml(doc, xmlPath);
        return true;
    }

    // ======================
    // DELETE
    // ======================
    public boolean deleteUser(int id) throws Exception {
        Document doc = xmlService.loadXml(xmlPath);
        NodeList nodes = xpathService.evaluate(doc, "//user[@id='" + id + "']");

        if (nodes.getLength() == 0) return false;

        Node node = nodes.item(0);
        node.getParentNode().removeChild(node);

        xmlService.saveXml(doc, xmlPath);
        return true;
    }

    // ======================
    // HTML GENERATION
    // ======================
    public String generateUsersHtml() throws Exception {
        return xsltService.transform(xmlPath, "src/main/resources/xslt/users.xsl");
    }

    public String generateUserHtml(int id) throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("userId", String.valueOf(id));
        return xsltService.transformWithParams(xmlPath, "src/main/resources/xslt/user-detail.xsl", params);
    }

    // ======================
    // UTILS
    // ======================
    private User mapToUser(Element e) {
        User u = new User();
        u.setId(Integer.parseInt(e.getAttribute("id")));
        u.setRole(e.getAttribute("role"));

        // Sécurisation pour éviter NullPointerException si balise manquante
        NodeList names = e.getElementsByTagName("name");
        if (names.getLength() > 0) u.setName(names.item(0).getTextContent());

        NodeList emails = e.getElementsByTagName("email");
        if (emails.getLength() > 0) u.setEmail(emails.item(0).getTextContent());

        List<Integer> ids = new ArrayList<>();
        NodeList refs = e.getElementsByTagName("courseRef");
        for (int i = 0; i < refs.getLength(); i++) {
            Element ref = (Element) refs.item(i);
            try {
                ids.add(Integer.parseInt(ref.getAttribute("id")));
            } catch (NumberFormatException ex) {
                // ignore
            }
        }
        u.setCourseIds(ids);
        return u;
    }

    private void append(Document doc, Element parent, String tag, String value) {
        Element el = doc.createElement(tag);
        el.setTextContent(value);
        parent.appendChild(el);
    }
}
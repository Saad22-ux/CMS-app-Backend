package com.elasri.cmsapp.service;

import com.elasri.cmsapp.model.User;
import org.springframework.stereotype.Service;
import org.w3c.dom.*;

import java.util.*;
@Service
public class UserService {
    private final String xmlPath =
            "src/main/resources/data/users.xml";

    private XmlService xmlService;
    private XPathService xpathService;
    private XsltService xsltService;

    public UserService(XsltService xsltService, XmlService xmlService,  XPathService xpathService) {
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
        NodeList nodes = xpathService.evaluate(
                doc, "//user[@id='" + id + "']");

        if (nodes.getLength() == 0) return null;

        return mapToUser((Element) nodes.item(0));
    }

    // ======================
    // CREATE
    // ======================
    private int generateId(Document doc) throws Exception {
        NodeList nodes = xpathService.evaluate(doc, "//course");
        return nodes.getLength() + 1;
    }

    public void addUser(User user) throws Exception {

        Document doc = xmlService.loadXml(xmlPath);
        Element root = doc.getDocumentElement();

        Element userEl = doc.createElement("user");
        userEl.setAttribute("id", String.valueOf(user.getId()));
        userEl.setAttribute("role", user.getRole());

        append(doc, userEl, "name", user.getName());
        append(doc, userEl, "email", user.getEmail());

        if (user.getCourseIds() != null) {
            Element coursesEl = doc.createElement("courses");

            for (Integer cid : user.getCourseIds()) {
                Element ref = doc.createElement("courseRef");
                ref.setAttribute("id", String.valueOf(cid));
                coursesEl.appendChild(ref);
            }
            userEl.appendChild(coursesEl);
        }

        root.appendChild(userEl);
        user.setId(generateId(doc));
        xmlService.saveXml(doc, xmlPath);
    }

    // ======================
    // UPDATE
    // ======================
    public boolean updateUser(User user) throws Exception {

        Document doc = xmlService.loadXml(xmlPath);
        NodeList nodes = xpathService.evaluate(
                doc, "//user[@id='" + user.getId() + "']");

        if (nodes.getLength() == 0) return false;

        Element e = (Element) nodes.item(0);
        e.setAttribute("role", user.getRole());

        e.getElementsByTagName("name").item(0)
                .setTextContent(user.getName());
        e.getElementsByTagName("email").item(0)
                .setTextContent(user.getEmail());

        // update courses
        NodeList oldCourses = e.getElementsByTagName("courses");
        if (oldCourses.getLength() > 0) {
            e.removeChild(oldCourses.item(0));
        }

        if (user.getCourseIds() != null) {
            Element coursesEl = doc.createElement("courses");
            for (Integer cid : user.getCourseIds()) {
                Element ref = doc.createElement("courseRef");
                ref.setAttribute("id", String.valueOf(cid));
                coursesEl.appendChild(ref);
            }
            e.appendChild(coursesEl);
        }

        xmlService.saveXml(doc, xmlPath);
        return true;
    }

    // ======================
    // DELETE
    // ======================
    public boolean deleteUser(int id) throws Exception {

        Document doc = xmlService.loadXml(xmlPath);
        NodeList nodes = xpathService.evaluate(
                doc, "//user[@id='" + id + "']");

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
        return xsltService.transform(
                xmlPath,
                "src/main/resources/xslt/users.xsl"
        );
    }

    public String generateUserHtml(int id) throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("userId", String.valueOf(id));

        return xsltService.transformWithParams(
                xmlPath,
                "src/main/resources/xslt/user-detail.xsl",
                params
        );
    }

    // ======================
    // UTILS
    // ======================
    private User mapToUser(Element e) {

        User u = new User();
        u.setId(Integer.parseInt(e.getAttribute("id")));
        u.setRole(e.getAttribute("role"));
        u.setName(e.getElementsByTagName("name")
                .item(0).getTextContent());
        u.setEmail(e.getElementsByTagName("email")
                .item(0).getTextContent());

        List<Integer> ids = new ArrayList<>();
        NodeList refs = e.getElementsByTagName("courseRef");
        for (int i = 0; i < refs.getLength(); i++) {
            Element ref = (Element) refs.item(i);
            ids.add(Integer.parseInt(ref.getAttribute("id")));
        }
        u.setCourseIds(ids);

        return u;
    }

    private void append(Document doc, Element parent,
                        String tag, String value) {
        Element el = doc.createElement(tag);
        el.setTextContent(value);
        parent.appendChild(el);
    }
}

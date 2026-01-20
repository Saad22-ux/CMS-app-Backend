package com.elasri.cmsapp.service;

import com.elasri.cmsapp.model.Professor;
import org.springframework.stereotype.Service;
import org.w3c.dom.*;

import java.util.*;

@Service
public class ProfessorService {
    private final String xmlPath = "src/main/resources/data/professors.xml";

    private final XmlService xmlService;
    private final XPathService xpathService;
    private final XsltService xsltService;

    public ProfessorService(XmlService xmlService, XPathService xpathService, XsltService xsltService) {
        this.xmlService = xmlService;
        this.xpathService = xpathService;
        this.xsltService = xsltService;
    }

    public List<Professor> getAll() throws Exception {
        Document doc = xmlService.loadXml(xmlPath);
        NodeList nodes = xpathService.evaluate(doc, "//professor");

        List<Professor> list = new ArrayList<>();
        for (int i = 0; i < nodes.getLength(); i++) {
            list.add(map((Element) nodes.item(i)));
        }
        return list;
    }

    public Professor getById(int id) throws Exception {
        Document doc = xmlService.loadXml(xmlPath);
        NodeList nodes = xpathService.evaluate(doc, "//professor[@id='" + id + "']");

        if (nodes.getLength() == 0) return null;
        return map((Element) nodes.item(0));
    }

    // ======================
    // CREATE (CORRIGÉ)
    // ======================

    // 1. Calculer le véritable MAX ID
    private int generateId(Document doc) throws Exception {
        NodeList idNodes = xpathService.evaluate(doc, "//professor/@id");
        int maxId = 0;

        for (int i = 0; i < idNodes.getLength(); i++) {
            try {
                int currentId = Integer.parseInt(idNodes.item(i).getNodeValue());
                if (currentId > maxId) {
                    maxId = currentId;
                }
            } catch (NumberFormatException e) {
                // Ignore errors
            }
        }
        return maxId + 1;
    }

    // 2. Retourner l'objet Professor
    public Professor add(Professor p) throws Exception {
        Document doc = xmlService.loadXml(xmlPath);
        Element root = doc.getDocumentElement();

        // Générer l'ID AVANT la construction XML
        int newId = generateId(doc);
        p.setId(newId);

        Element el = doc.createElement("professor");
        el.setAttribute("id", String.valueOf(newId));

        append(doc, el, "name", p.getName());
        append(doc, el, "bio", p.getBio());

        Element skills = doc.createElement("skills");
        if (p.getSkills() != null) {
            for (String s : p.getSkills()) {
                append(doc, skills, "skill", s);
            }
        }

        Element pubs = doc.createElement("publications");
        if (p.getPublications() != null) {
            p.getPublications().forEach((title, year) -> {
                Element pub = doc.createElement("publication");
                pub.setAttribute("year", year);
                pub.setTextContent(title);
                pubs.appendChild(pub);
            });
        }

        el.appendChild(skills);
        el.appendChild(pubs);
        root.appendChild(el);

        xmlService.saveXml(doc, xmlPath);
        // Validation optionnelle (peut ralentir l'ajout si le XSD est lourd)
        // xmlService.validateXml(xmlPath, "src/main/resources/schemas/professors.xsd");

        return p; // Retourne le prof avec l'ID correct
    }

    public boolean update(Professor p) throws Exception {
        Document doc = xmlService.loadXml(xmlPath);
        NodeList nodes = xpathService.evaluate(doc, "//professor[@id='" + p.getId() + "']");

        if (nodes.getLength() == 0) return false;

        Element e = (Element) nodes.item(0);

        // Update basic fields
        NodeList names = e.getElementsByTagName("name");
        if(names.getLength() > 0) names.item(0).setTextContent(p.getName());

        NodeList bios = e.getElementsByTagName("bio");
        if(bios.getLength() > 0) bios.item(0).setTextContent(p.getBio());

        // Replace lists safely
        if (p.getSkills() != null) {
            replaceList(doc, e, "skills", "skill", p.getSkills());
        }
        if (p.getPublications() != null) {
            replacePublications(doc, e, p.getPublications());
        }

        xmlService.saveXml(doc, xmlPath);
        // xmlService.validateXml(xmlPath, "src/main/resources/schemas/professors.xsd");
        return true;
    }

    public boolean delete(int id) throws Exception {
        Document doc = xmlService.loadXml(xmlPath);
        NodeList nodes = xpathService.evaluate(doc, "//professor[@id='" + id + "']");

        if (nodes.getLength() == 0) return false;

        Node node = nodes.item(0);
        node.getParentNode().removeChild(node);

        xmlService.saveXml(doc, xmlPath);
        // xmlService.validateXml(xmlPath, "src/main/resources/schemas/professors.xsd");
        return true;
    }

    public String generateHtml() throws Exception {
        return xsltService.transform(xmlPath, "src/main/resources/xslt/professors.xsl");
    }

    // ======================
    // UTILS
    // ======================
    private Professor map(Element e) {
        Professor p = new Professor();
        p.setId(Integer.parseInt(e.getAttribute("id")));
        p.setName(text(e, "name"));
        p.setBio(text(e, "bio"));

        List<String> skills = new ArrayList<>();
        NodeList sk = e.getElementsByTagName("skill");
        for (int i = 0; i < sk.getLength(); i++) {
            skills.add(sk.item(i).getTextContent());
        }
        p.setSkills(skills);

        Map<String, String> pubs = new LinkedHashMap<>();
        NodeList pub = e.getElementsByTagName("publication");
        for (int i = 0; i < pub.getLength(); i++) {
            Element pe = (Element) pub.item(i);
            pubs.put(pe.getTextContent(), pe.getAttribute("year"));
        }
        p.setPublications(pubs);

        return p;
    }

    private void append(Document doc, Element parent, String tag, String value) {
        Element el = doc.createElement(tag);
        el.setTextContent(value);
        parent.appendChild(el);
    }

    private String text(Element e, String tag) {
        NodeList list = e.getElementsByTagName(tag);
        return (list.getLength() > 0) ? list.item(0).getTextContent() : "";
    }

    private void replaceList(Document doc, Element parent, String container, String child, List<String> values) {
        NodeList oldList = parent.getElementsByTagName(container);
        if (oldList.getLength() > 0) {
            parent.removeChild(oldList.item(0));
        }

        Element root = doc.createElement(container);
        for (String v : values) {
            append(doc, root, child, v);
        }
        parent.appendChild(root);
    }

    private void replacePublications(Document doc, Element parent, Map<String, String> pubs) {
        NodeList oldList = parent.getElementsByTagName("publications");
        if (oldList.getLength() > 0) {
            parent.removeChild(oldList.item(0));
        }

        Element root = doc.createElement("publications");
        pubs.forEach((title, year) -> {
            Element pub = doc.createElement("publication");
            pub.setAttribute("year", year);
            pub.setTextContent(title);
            root.appendChild(pub);
        });
        parent.appendChild(root);
    }
}
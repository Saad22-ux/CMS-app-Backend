package com.elasri.cmsapp.service;


import com.elasri.cmsapp.model.Professor;
import org.springframework.stereotype.Service;
import org.w3c.dom.*;

import java.util.*;

@Service
public class ProfessorService {
    private final String xmlPath = "src/main/resources/data/professors.xml";

    private XmlService xmlService;
    private XPathService xpathService;
    private XsltService xsltService;

    public ProfessorService(XmlService xmlService,  XPathService xpathService, XsltService xsltService) {
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
        NodeList nodes = xpathService.evaluate(
                doc, "//professor[@id='" + id + "']");

        if (nodes.getLength() == 0) return null;
        return map((Element) nodes.item(0));
    }

    public void add(Professor p) throws Exception {

        Document doc = xmlService.loadXml(xmlPath);
        Element root = doc.getDocumentElement();

        p.setId(generateId(doc));

        Element el = doc.createElement("professor");
        el.setAttribute("id", String.valueOf(p.getId()));

        append(doc, el, "name", p.getName());
        append(doc, el, "bio", p.getBio());

        Element skills = doc.createElement("skills");
        for (String s : p.getSkills()) {
            append(doc, skills, "skill", s);
        }

        Element pubs = doc.createElement("publications");
        p.getPublications().forEach((title, year) -> {
            Element pub = doc.createElement("publication");
            pub.setAttribute("year", year);
            pub.setTextContent(title);
            pubs.appendChild(pub);
        });

        el.appendChild(skills);
        el.appendChild(pubs);
        root.appendChild(el);

        xmlService.saveXml(doc, xmlPath);
        xmlService.validateXml(xmlPath,
                "src/main/resources/schemas/professors.xsd");
    }

    public boolean update(Professor p) throws Exception {

        Document doc = xmlService.loadXml(xmlPath);
        NodeList nodes = xpathService.evaluate(
                doc, "//professor[@id='" + p.getId() + "']");

        if (nodes.getLength() == 0) return false;

        Element e = (Element) nodes.item(0);

        e.getElementsByTagName("name").item(0)
                .setTextContent(p.getName());
        e.getElementsByTagName("bio").item(0)
                .setTextContent(p.getBio());

        replaceList(doc, e, "skills", "skill", p.getSkills());
        replacePublications(doc, e, p.getPublications());

        xmlService.saveXml(doc, xmlPath);
        xmlService.validateXml(xmlPath,
                "src/main/resources/schemas/professors.xsd");
        return true;
    }

    public boolean delete(int id) throws Exception {

        Document doc = xmlService.loadXml(xmlPath);
        NodeList nodes = xpathService.evaluate(
                doc, "//professor[@id='" + id + "']");

        if (nodes.getLength() == 0) return false;

        Node node = nodes.item(0);
        node.getParentNode().removeChild(node);
        xmlService.saveXml(doc, xmlPath);
        xmlService.validateXml(xmlPath,
                "src/main/resources/schemas/professors.xsd");
        return true;
    }

    public String generateHtml() throws Exception {
        return xsltService.transform(
                xmlPath,
                "src/main/resources/xslt/professors.xsl"
        );
    }

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

    private int generateId(Document doc) throws Exception {
        NodeList nodes = xpathService.evaluate(doc, "//professor");
        return nodes.getLength() + 1;
    }

    private void append(Document doc, Element parent,
                        String tag, String value) {
        Element el = doc.createElement(tag);
        el.setTextContent(value);
        parent.appendChild(el);
    }

    private String text(Element e, String tag) {
        return e.getElementsByTagName(tag)
                .item(0).getTextContent();
    }

    private void replaceList(Document doc, Element parent,
                             String container, String child,
                             List<String> values) {

        Node old = parent.getElementsByTagName(container).item(0);
        parent.removeChild(old);

        Element root = doc.createElement(container);
        for (String v : values) {
            append(doc, root, child, v);
        }
        parent.appendChild(root);
    }

    private void replacePublications(Document doc, Element parent,
                                     Map<String, String> pubs) {

        Node old = parent.getElementsByTagName("publications").item(0);
        parent.removeChild(old);

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

package com.elasri.cmsapp.service;


import com.elasri.cmsapp.model.Professor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.*;

import java.util.*;

@Service
public class ProfessorService {
    private final String xmlPath = "src/main/resources/data/professor.xml";

    private XmlService xmlService;
    private XPathService xpathService;
    private XsltService xsltService;

    public ProfessorService(XmlService xmlService,  XPathService xpathService, XsltService xsltService) {
        this.xmlService = xmlService;
        this.xpathService = xpathService;
        this.xsltService = xsltService;
    }

    public Professor getProfessor() throws Exception {

        Document doc = xmlService.loadXml(xmlPath);
        Professor p = new Professor();

        p.setBio(xpathService.string(doc, "/professor/bio"));

        // Skills
        NodeList skills =
                xpathService.evaluate(doc, "/professor/skills/skill");

        List<String> skillList = new ArrayList<>();
        for (int i = 0; i < skills.getLength(); i++) {
            skillList.add(skills.item(i).getTextContent());
        }
        p.setSkills(skillList);

        // Publications
        NodeList pubs =
                xpathService.evaluate(doc, "/professor/publications/publication");

        Map<String, String> publications = new LinkedHashMap<>();
        for (int i = 0; i < pubs.getLength(); i++) {
            Element e = (Element) pubs.item(i);
            publications.put(
                    e.getTextContent(),
                    e.getAttribute("year")
            );
        }
        p.setPublications(publications);

        return p;
    }

    // ============================
    // UPDATE Professor
    // ============================
    public void updateProfessor(Professor p) throws Exception {

        Document doc = xmlService.loadXml(xmlPath);

        // Update bio
        Node bioNode = xpathService.node(doc, "/professor/bio");
        bioNode.setTextContent(p.getBio());

        // Update skills
        Node skillsNode = xpathService.node(doc, "/professor/skills");
        removeChildren(skillsNode);

        for (String s : p.getSkills()) {
            Element el = doc.createElement("skill");
            el.setTextContent(s);
            skillsNode.appendChild(el);
        }

        // Update publications
        Node pubsNode = xpathService.node(doc, "/professor/publications");
        removeChildren(pubsNode);

        p.getPublications().forEach((title, year) -> {
            Element pub = doc.createElement("publication");
            pub.setAttribute("year", year);
            pub.setTextContent(title);
            pubsNode.appendChild(pub);
        });

        xmlService.saveXml(doc, xmlPath);
    }

    // ============================
    // HTML Generation
    // ============================
    public String generateHtml() throws Exception {
        return xsltService.transform(
                xmlPath,
                "src/main/resources/xslt/professor.xsl"
        );
    }

    // ============================
    private void removeChildren(Node node) {
        while (node.hasChildNodes()) {
            node.removeChild(node.getFirstChild());
        }
    }
}

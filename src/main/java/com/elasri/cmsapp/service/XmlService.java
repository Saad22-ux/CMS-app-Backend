package com.elasri.cmsapp.service;

import org.springframework.stereotype.Service;
import org.w3c.dom.Node;

import org.w3c.dom.Document;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.nio.file.Files;

@Service
public class XmlService {
    public Document loadXml(String path) throws Exception {

        File file = new File(path);

        if (!file.exists()) {
            file.getParentFile().mkdirs();
            file.createNewFile();

            String root = path.contains("courses") ? "courses" :
                    path.contains("users") ? "users" :
                            "professor";

            String content = "<?xml version=\"1.0\"?>\n<" + root + "></" + root + ">";
            Files.write(file.toPath(), content.getBytes());
        }

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        return factory.newDocumentBuilder().parse(file);
    }

    public void saveXml(Document document, String path) throws Exception {
        javax.xml.transform.Transformer transformer =
                javax.xml.transform.TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(javax.xml.transform.OutputKeys.INDENT, "yes");
        transformer.transform(
                new javax.xml.transform.dom.DOMSource((Node) document),
                new javax.xml.transform.stream.StreamResult(new File(path))
        );
    }

    public void validateXml(String xmlPath, String xsdPath)
            throws Exception {

        SchemaFactory factory =
                SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

        Schema schema = factory.newSchema(new File(xsdPath));
        Validator validator = schema.newValidator();

        validator.validate(new StreamSource(new File(xmlPath)));
    }

}

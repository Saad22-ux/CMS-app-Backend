package com.elasri.cmsapp.service;

import org.springframework.stereotype.Service;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.StringWriter;
import java.util.Map;

@Service
public class XsltService {
    public String transform(String xmlPath, String xslPath) throws Exception {

        Source xml = new StreamSource(new File(xmlPath));
        Source xsl = new StreamSource(new File(xslPath));

        TransformerFactory factory = new net.sf.saxon.TransformerFactoryImpl();
        Transformer transformer = factory.newTransformer(xsl);

        StringWriter writer = new StringWriter();
        transformer.transform(xml, new StreamResult(writer));

        return writer.toString();
    }

    public String transformWithParams(
            String xmlPath,
            String xslPath,
            Map<String, String> params) throws Exception {

        Source xml = new StreamSource(new File(xmlPath));
        Source xsl = new StreamSource(new File(xslPath));

        TransformerFactory factory =
                new net.sf.saxon.TransformerFactoryImpl();

        Transformer transformer = factory.newTransformer(xsl);

        for (String key : params.keySet()) {
            transformer.setParameter(key, params.get(key));
        }

        StringWriter writer = new StringWriter();
        transformer.transform(xml, new StreamResult(writer));

        return writer.toString();
    }
}

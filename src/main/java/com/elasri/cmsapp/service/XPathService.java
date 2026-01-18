package com.elasri.cmsapp.service;

import org.springframework.stereotype.Service;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.w3c.dom.Document;

import javax.xml.xpath.*;

@Service
public class XPathService {

    private final XPath xpath = XPathFactory.newInstance().newXPath();

    public NodeList evaluate(Document doc, String expression) throws Exception {
        XPath xpath = XPathFactory.newInstance().newXPath();
        return (NodeList) xpath.evaluate(expression, doc, XPathConstants.NODESET);
    }

    public String string(Document doc, String expression)
            throws XPathExpressionException {

        XPathExpression expr = xpath.compile(expression);
        return expr.evaluate(doc);
    }

    public Node node(Document doc, String expression)
            throws XPathExpressionException {

        XPathExpression expr = xpath.compile(expression);
        return (Node) expr.evaluate(doc, XPathConstants.NODE);
    }
}

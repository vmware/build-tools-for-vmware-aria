package com.vmware.pscoe.iac.artifact.common.utils;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public final class XmlUtilities {
    
    private XmlUtilities() {

    }

    public final static Document initializeXmlFile(File xmlFile) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(xmlFile);
            document.normalize();
            return document;
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new RuntimeException(String.format("An error occurred while initializing xml file for %s : %s %n", xmlFile.getName(),
			e.getMessage()));
        }

    }
}

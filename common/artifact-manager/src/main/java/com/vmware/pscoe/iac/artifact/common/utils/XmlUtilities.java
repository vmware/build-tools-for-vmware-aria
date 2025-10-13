package com.vmware.pscoe.iac.artifact.common.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;

/*-
 * #%L
 * artifact-manager
 * %%
 * Copyright (C) 2023 - 2025 VMware
 * %%
 * Build Tools for VMware Aria
 * Copyright 2023 VMware, Inc.
 * 
 * This product is licensed to you under the BSD-2 license (the "License"). You may not use this product except in compliance with the BSD-2 License.
 * 
 * This product may include a number of subcomponents with separate copyright notices and license terms. Your use of these subcomponents is subject to the terms and conditions of the subcomponent's license, as noted in the LICENSE file.
 * #L%
 */

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public final class XmlUtilities {
    
    private XmlUtilities() {

    }

    public final static Element initializeXmlFile(File xmlFile) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(xmlFile);
            Element documentElement = document.getDocumentElement();
            documentElement.normalize();
            return documentElement;
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new RuntimeException(String.format("An error occurred while initializing xml file for %s : %s %n", xmlFile.getName(),
			e.getMessage()));
        }
    }

    public final static String readXmlFileAsString(File xmlFile) throws IOException {
        FileReader fileReader = new FileReader(xmlFile);
        BufferedReader bufReader = new BufferedReader(fileReader);
        StringBuilder sb = new StringBuilder();
        String line;

        while ((line = bufReader.readLine()) != null) {
            sb.append(line).append("\n"); // Append each line and a newline character
        }

        bufReader.close();
        return sb.toString();
    }

    public final static void writeToXmlFile(File file, String xmlContent) {

        try {

            // Create a FileWriter to write to the file
            FileWriter fileWriter = new FileWriter(file);

            // Create a BufferedWriter for efficient writing
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            // Write the XML content string to the file
            bufferedWriter.write(xmlContent);

            // Close the BufferedWriter to flush and release resources
            bufferedWriter.close();

            System.out.println("XML content successfully written to " + file.getPath());

        } catch (IOException e) {
            System.err.println("Error writing XML to file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public final static Node findTagInXmlDocument(Node node, String tagName) {

        Node result = null;

        if (node.getNodeName().equals(tagName)) {
            result = node;
            return result;
        }

        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node child = childNodes.item(i);
            result = findTagInXmlDocument(child, tagName);
            if (result != null) {
                break;
            }
        }

        return result;
    }
}

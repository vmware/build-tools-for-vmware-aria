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
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public final class XmlUtilities {
    
    private XmlUtilities() {

    }

    /**
     * Initializes the xml file by getting the first element(tag) of the xml file
     * @param xmlFile the input xml file
     * @return an object of type Element in order to have access to attributes
     */
    public static Element initializeXmlFile(File xmlFile) {
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

    /**
     * @param xmlFile the input xml file
     * @return the string content of the xml file
     */
    public static String readXmlFileAsString(File xmlFile) throws IOException {
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

    /**
     * Writes string content to an xml file
     * @param file the file to write to
     * @param xmlContent the xml content as string
     */
    public static void writeToXmlFile(File file, String xmlContent) {

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
            throw new RuntimeException("Error writing XML to file: " + e.getMessage());
        }
    }

    /**
     * Sets certain tags with certain attributes
     * @param xmlFile the xml file about to be edited
     * @param tagName the tag name where attribute changes will happen
     * @param valuesToSet map with values. The keys of the main map are the values of the attributes to search by, each child map is a key value pair for the attributes wanted to be set
     */
    public static void setAttributesInXmlFile(File xmlFile, String tagName, HashMap<String, HashMap<String, String>> valuesToSet) {

        Element document = initializeXmlFile(xmlFile);

        Node tag = findTagInXmlDocument(document, tagName);
        NodeList childNodes = tag.getChildNodes();

        int childNodesLength = 0;

        for (int i = 0; i < childNodes.getLength(); i++) {
            Node child = childNodes.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                childNodesLength++;
            }
        } 

        if (childNodesLength != valuesToSet.size()) {
            throw new InvalidParameterException("Number of provided values does not match the number of tags found");
        }

        for (int i = 0; i < childNodes.getLength(); i++) {
            Node child = childNodes.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) child;
                NamedNodeMap attributeElements = element.getAttributes();
                List<Node> attributes = new ArrayList<Node>();
                for (int attributeIndex = 0; attributeIndex < attributeElements.getLength(); attributeIndex++) {
                    Node nextAttribute = attributeElements.item(attributeIndex);
                    attributes.add(nextAttribute);
                }

                String[] hashMapKeys = valuesToSet.keySet().toArray(new String[0]);
                for (String key : hashMapKeys) {
                    Optional<Node> searchAttribute = attributes.stream().filter(attr -> attr.getNodeValue().equals(key)).findFirst();
                    if (searchAttribute.isPresent()) {
                        HashMap<String, String> childMap = valuesToSet.get(key);
                        String[] childMapKeys = childMap.keySet().toArray(new String[0]);
                        for (String childKey : childMapKeys) {
                            element.setAttribute(childKey, childMap.get(childKey));
                        }
                        break;
                    }
                }
            }
        }
        saveXmlDocument(xmlFile, document);
    }

    /**
     * Saves changes of an xml file
     * @param xmlFile the xml file to save
     * @param document the initialized xml file
     */
    public static void saveXmlDocument(File xmlFile, Element document) {

        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(xmlFile);
            transformer.transform(source, result);
            System.out.println("XML document saved successfully");
        } catch (TransformerException e) {
            throw new RuntimeException("Saving document " + document.getNodeName() + " failed. Reason: " + e);
        }
       
    }

    /**
     * Finds node by tag name
     * @param node the parent node to search under
     * @param tagName the tag name to be searched
     * @return the found node or null
     */
    public static Node findTagInXmlDocument(Node node, String tagName) {

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

    /**
     * Gets all tags with specified name which contain attributes
     * @param document the initialized xml file
     * @param tagName the tag name of the tag needed to be found
     * @return a list of the tags found which contain attributes
     */
    public static List<Element> getAllTagsWithAttributes(Element document, String tagName) {

        NodeList tagNodes = document.getElementsByTagName(tagName);
        List<Element> tags = new ArrayList<>();

        for (int i = 0; i < tagNodes.getLength(); i++) {
            Node node = tagNodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                tags.add((Element) node);
            }

        }

        return tags;
    }

    /**
     * Gets all tags with specified tag name which have certain attribute
     * @param document the initialized xml document
     * @param tagName the name of the tags to be found
     * @param attributeName the name of the attribute
     * @return a list of tags with attributes
     */
    public static List<Element> getAllTagsWithAttribute(Element document, String tagName, String attributeName) {

        NodeList tagNodes = document.getElementsByTagName(tagName);
        List<Element> tags = new ArrayList<>();

        for (int i = 0; i < tagNodes.getLength(); i++) {
            Node node = tagNodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                if (element.hasAttribute(attributeName)) {
                    tags.add((Element) node);
                }
            }

        }

        return tags;
    }

    /**
     * Gets all tags with specified tag name which do not attributes
     * @param document the initialized xml document
     * @param tagName the name of the tags to be found
     * @return a list of tags without attributes
     */
    public static Node[] getAllTagsWithoutAttributes(Element document, String tagName) {

        NodeList tagNodes = document.getElementsByTagName("*");

        List<Node> result = new ArrayList<>();
        for (int i = 0; i < tagNodes.getLength(); i++) {
            Node node = tagNodes.item(i);
            if (node.getNodeName().equals(tagName)) {
                result.add(node);
            }
        }
        return result.toArray(new Node[0]);
    }

    /**
     * Gets the values of certain attribute for a list of tags
     * @param tags the tags to take the attrubute values from
     * @param attributeName the name of the attribute
     * @return a list of values of the attributes
     */
    public static List<String> getAllValuesOfAttributeOfTag(Element[] tags, String attributeName) {

        List<String> result = new ArrayList<>();
        for (int i = 0; i < tags.length; i++) {
            if (!tags[i].hasAttribute(attributeName)) {
                continue;
            }
            result.add(tags[i].getAttribute(attributeName));
        }

        return result;
    }

    /**
     * Gets all tags with specified tag name which are in the xml document
     * @param document the initialized xml document
     * @param tagName the name of the tags to be found
     * @return a list of tags found
     */
    public static Element[] getChildrenTagsOfTag(Node document, String tagName) throws Exception {

        List<Element> children = new ArrayList<>();

        Node tag = findTagInXmlDocument(document, tagName);

        if (tag == null) {
            throw new RuntimeException("Tag with name '" + tagName + "' not found in document");
        }

        NodeList childNodes = tag.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node child = childNodes.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                children.add((Element) child);
            }
        }

        return children.toArray(new Element[0]);
    }

    /**
     * Gets the text content from a list of tags
     * @param tags the tag list
     * @return a list of strings representing the text between the opening and closing tag
     */
    public static List<String> getTextContentBetweenTags(Node[] tags) {

        List<String> result = new ArrayList<>();
        for (int i = 0; i < tags.length; i++) {
            result.add(tags[i].getTextContent());
        }

        return result;
    }
}

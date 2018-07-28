package com.acl.updater.file;


import com.acl.updater.utils.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;

/**
 * Update Metada file
 */
public class MetadataFileUpdater implements FileUpdater {

     private static final Logger LOGGER = LogManager.getLogger(MetadataFileUpdater.class);

    private static final String DC_DESCRIPTION = "dc:description";
    private static final String METADATA_OPF = "metadata.opf";
    //Path to the file to Read
    private String filePath;

    public MetadataFileUpdater(String filePath) {
        this.filePath = filePath;
    }

    /**
     * This method update one metadata file
     * @param filePath path to the document to update
     */
    private void updateDocument(String filePath) {
        LOGGER.info("Going to update {}", filePath);

        try {
            final Document document = getDocument(filePath);

            if (document == null) {
                LOGGER.warn(" Document is ull");
                return;
            }
            final Element racine = document.getDocumentElement();

            if (racine == null) {
                LOGGER.info("Racine is null");
                return;
            }
            NodeList nodeList = racine.getChildNodes();

            for( int i = 0; i < nodeList.getLength(); i++) {
                if(nodeList.item(i).getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) nodeList.item(i);

                    updateDescription(element);
                    break;
                }
            }

            save(document, filePath);
        } catch (Exception e) {
            LOGGER.error("Error during update document, ",e);
        }
    }

    /**
     * This method generate a Document from the metadata file
     * @param filePath Path to the document to read
     * @return Return the Object corresponding to the docuùent
     * @throws ParserConfigurationException can"t read document
     * @throws SAXException can"t read document
     * @throws IOException can"t read document
     */
    private Document getDocument(String filePath) throws ParserConfigurationException, SAXException, IOException {
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        final DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(new File(filePath));
    }

    /**
     * Update description line, change html format
     * to utf8 format
     * @param element Element to update
     */
    private void updateDescription(Element element) {
        Element elementFind = findElement(element);
        if (elementFind == null) {
            return;
        }
        LOGGER.info("Text find : {}", elementFind.getTextContent());
        //Convertion HTML
        String newText = Utils.html2text(elementFind.getTextContent());
        LOGGER.info("New Text, {}", newText);
        findElement(element).setTextContent(newText);
    }

    /**
     * Find specify sub element in the object element
     * @param element Element where the research must be done
     * @return Element find
     */
    private Element findElement(final Element element) {
        return (Element) element.getElementsByTagName(DC_DESCRIPTION).item(0);
    }

    /**
     * Update metadata file, write the new version with html information removed
     * @param documentToSave Document to save
     * @param filePath File path of the document to save
     * @throws TransformerConfigurationException can't write document
     * @throws TransformerException can't write document
     */
    private void save(Document documentToSave, String filePath) throws TransformerConfigurationException, TransformerException {
        LOGGER.info("Starting Save data {} ",filePath);
        final TransformerFactory transformerFactory = TransformerFactory.newInstance();
        final Transformer transformer = transformerFactory.newTransformer();
        final DOMSource source = new DOMSource(documentToSave);
        final StreamResult sortie = new StreamResult(new File(filePath));

        //prologue
        transformer.setOutputProperty(OutputKeys.VERSION, "1.0");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");

        //formatage
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        //transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

        //sortie
        transformer.transform(source, sortie);
    }

    /**
     * Update the directory where filepath has been given to the constructor
     */
    public void updateDirectory () {
        File directory = new File(filePath);

        updateDirectory(directory);
    }

    /**
     * Real method that update the directory, check in
     * all the folder and sub folder in the directory
     * @param repertoire Direcrtory where the research must be donne
     */
    private void updateDirectory(File repertoire) {
        if (repertoire.exists() && repertoire.isDirectory()) {
            File[] fichiers = repertoire.listFiles();
            if (fichiers != null) {
                for (File fichier : fichiers) {
                  updateDirectory(fichier);
                }
            } else {
                LOGGER.info("Erreur de lecture.");
            }
        } else if (repertoire.exists() && repertoire.isFile()
                && repertoire.getAbsolutePath().endsWith(METADATA_OPF)) {

            updateDocument(repertoire.getPath());
        }
    }
}

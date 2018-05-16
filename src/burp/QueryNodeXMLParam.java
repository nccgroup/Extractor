/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package burp;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author nbidron
 */
public class QueryNodeXMLParam extends queryNode {
    public QueryNodeXMLParam(queryNode parent) {
        super("XML Param", parent);
        settingsNumber = 2;
        settingName[0] = "XML node name:";
        settingName[1] = "XML node parameter:";
    }
    
    private DocumentBuilder getsafeDB() throws ParserConfigurationException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        String FEATURE = null;
        // This is the PRIMARY defense. If DTDs (doctypes) are disallowed, almost all XML entity attacks are prevented
        // Xerces 2 only - http://xerces.apache.org/xerces2-j/features.html#disallow-doctype-decl
        FEATURE = "http://apache.org/xml/features/disallow-doctype-decl";
        dbf.setFeature(FEATURE, true);

        // If you can't completely disable DTDs, then at least do the following:
        // Xerces 1 - http://xerces.apache.org/xerces-j/features.html#external-general-entities
        // Xerces 2 - http://xerces.apache.org/xerces2-j/features.html#external-general-entities
        // JDK7+ - http://xml.org/sax/features/external-general-entities    
        FEATURE = "http://xml.org/sax/features/external-general-entities";
        dbf.setFeature(FEATURE, false);

        // Xerces 1 - http://xerces.apache.org/xerces-j/features.html#external-parameter-entities
        // Xerces 2 - http://xerces.apache.org/xerces2-j/features.html#external-parameter-entities
        // JDK7+ - http://xml.org/sax/features/external-parameter-entities    
        FEATURE = "http://xml.org/sax/features/external-parameter-entities";
        dbf.setFeature(FEATURE, false);

        // Disable external DTDs as well
        FEATURE = "http://apache.org/xml/features/nonvalidating/load-external-dtd";
        dbf.setFeature(FEATURE, false);

        // and these as well, per Timothy Morgan's 2014 paper: "XML Schema, DTD, and Entity Attacks"
        dbf.setXIncludeAware(false);
        dbf.setExpandEntityReferences(false);

        return dbf.newDocumentBuilder();
    }
    
    @Override
    public byte[] unpackData(byte[] data) {
        byte[] byteOrigMessageContent;
        InputStream is;
        is = new ByteArrayInputStream(data);
        try{
            DocumentBuilder dBuilder = getsafeDB();
            Document doc = dBuilder.parse(is);
            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName(setting[0]);
            if(nList.getLength() > 0){
                if (nList.getLength() == 1) {
                    NamedNodeMap map = nList.item(0).getAttributes();
                    Node paramNode= map.getNamedItem(setting[1]);
                    if(paramNode != null) {
                        byteOrigMessageContent = paramNode.getTextContent().getBytes();
                    } else {
                        Globals.callbacks.printOutput("No parameter " + setting[1] + " for  XML node "+setting[0]+" found.");
                        byteOrigMessageContent = "noData".getBytes();
                    }
                } else {
                    Globals.callbacks.printOutput("more than one XML node "+setting[0]+" found. Used first occurence.");
                    NamedNodeMap map = nList.item(0).getAttributes();
                    Node paramNode= map.getNamedItem(setting[1]);
                    if(paramNode != null) {
                        byteOrigMessageContent = paramNode.getTextContent().getBytes();
                    } else {
                        Globals.callbacks.printOutput("No parameter " + setting[1] + " for  XML node "+setting[0]+" found.");
                        byteOrigMessageContent = "noData".getBytes();
                    }
                }
            }else {
                Globals.callbacks.printOutput("No XML node "+setting[0]+" found.");
                byteOrigMessageContent = ("No XML node "+setting[0]+" found.").getBytes();
            }
        } catch (IOException | ParserConfigurationException | DOMException | SAXException e) {
            Globals.callbacks.printError(" Error processing XML node "+setting[0]+".");
            byteOrigMessageContent = "noData".getBytes();
        }
        return byteOrigMessageContent;
    }
    
    @Override
    public byte[] repackData(byte[] context, byte[] data) {
        String xmldata = Globals.helpers.bytesToString(context);
        InputStream is = new ByteArrayInputStream(context);

        try { //copy payload to XML structure and rewrite xml to the query
            DocumentBuilder dBuilder = getsafeDB();
            Document doc = dBuilder.parse(is);
            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName(setting[0]);
            NamedNodeMap map = nList.item(0).getAttributes();
            Node paramNode= map.getNamedItem(setting[1]);
            paramNode.setTextContent(Globals.helpers.bytesToString(data));

            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(doc), new StreamResult(writer));
            xmldata = writer.getBuffer().toString();
        } 
        catch (IOException | IllegalArgumentException | ParserConfigurationException | TransformerException | DOMException | SAXException e) {
            Globals.callbacks.printError("XML packing error: ");
            Globals.callbacks.printError(e.getMessage());
            xmldata = Globals.helpers.bytesToString(context);
        }
    return xmldata.getBytes();
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package burp;

/**
 *
 * @author nbidron
 */
public class Globals {
    static public IBurpExtenderCallbacks callbacks;
    static public IExtensionHelpers helpers;
    static public boolean longNames;    
    static public queryNode[] nodes;
    
    static public void init() {
        nodes = new queryNode[]{
        new QueryNodeRequestBody(null), // !!! don't change the first 2 lines !!!
        new QueryNodeRequestHeaders(null),
        new QueryNodeBase64(null),
        new QueryNodeUrlDecode(null),
        new QueryNodeGzip(null),
        new QueryNodeBinToHex(null),
        new QueryNodeHexToBin(null),
        new QueryNodeUrlParameter(null),
        new QueryNodeBodyParameter(null),
        new QueryNodeCookie(null),
        new QueryNodeHeaderFlag(null),
        new QueryNodeXML(null),
        new QueryNodeXMLParam(null),
        new QueryNodeJson(null),
        new QueryNodeAND(null),
        new QueryNodeOR(null),
        new QueryNodeXOR(null),
        new QueryNodeRegex(null),
        new QueryNodeIdentity(null)//, //insert your own node types after this line, don't forget to put back the coma on this line
        //new QueryNodeXXX(null) 
    };
    }
    
    
}

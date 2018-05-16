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
public class QueryNodeUrlDecode extends queryNode{
    
    public QueryNodeUrlDecode(queryNode parent) {
        super("URL Decode", parent);
        settingsNumber = 0;
    }
    
    @Override
    public byte[] unpackData(byte[] data) {
        return Globals.helpers.urlDecode(data);
    }
    
    @Override
    public byte[] repackData(byte[] context, byte[] data) {
        return Globals.helpers.urlEncode(data);   
    }
    
}

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
public class QueryNodeBase64 extends queryNode{
    
    public QueryNodeBase64(queryNode parent) {
        super("Base64", parent);
        settingsNumber = 0;
    }
    
    @Override
    public byte[] unpackData(byte[] data) {
        return Globals.helpers.base64Decode(data);
    }
    
    @Override
    public byte[] repackData(byte[] context, byte[] data) {
        return Globals.helpers.base64Encode(data).getBytes();    
    }
    
}

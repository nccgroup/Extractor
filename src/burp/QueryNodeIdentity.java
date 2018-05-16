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
public class QueryNodeIdentity extends queryNode{
    
    public QueryNodeIdentity(queryNode parent) {
        super("Identity", parent);
        settingsNumber = 0;
    }
    
    @Override
    public byte[] unpackData(byte[] data) {
        return data;
    }
    
    @Override
    public byte[] repackData(byte[] context, byte[] data) {
        return data;    
    }
    
}

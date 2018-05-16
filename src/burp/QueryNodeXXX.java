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
public class QueryNodeXXX extends queryNode{
    
    public QueryNodeXXX(queryNode parent) {
        super("Name of the type of node (e.g.: Url Parameter)", parent);
        settingsNumber = 2; //you can have up to 10 here
        settingName[0] = "Setting name 1:";
        settingName[1] = "Setting name 2:"; //add more of these depending on the number of settings you have
    }
    
    @Override
    public byte[] unpackData(byte[] data) {
        if(Globals.helpers.getRequestParameter(data, setting[0]) != null) {
            return Globals.helpers.getRequestParameter(data, setting[0]).getValue().getBytes();                
        }else {
            return data;
        }
    }
    
    @Override
    public byte[] repackData(byte[] context, byte[] data) {
        byte[] newContext = context;
        return newContext;    
    }
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package burp;

import java.util.List;

/**
 *
 * @author nbidron
 */
public class QueryNodeHeaderFlag extends queryNode{
    
    public QueryNodeHeaderFlag(queryNode parent) {
        super("header flag", parent);
        settingsNumber = 1;
        settingName[0] = "Header flag name:";
    }
    
    @Override
    public byte[] unpackData(byte[] data) {
        byte[] fakeData = (Globals.helpers.bytesToString(data) + "\r\n\r\n").getBytes(); //this is because we process on headers where we already lost the body of the request
        List<String> headers =  Globals.helpers.analyzeRequest(fakeData).getHeaders();               
        int headerIndex = -1;
        for(int i = 0; i < headers.size(); ++i) {
            if(headers.get(i).startsWith(setting[0])) {
                headerIndex = i;
                break;
            } 
        }
        if(0 <= headerIndex){ //found header
            String header = headers.get(headerIndex).substring((setting[0] + ": ").length());
            return header.getBytes();        
        }else {
            return "header not found".getBytes();
        }
    }
    
    @Override
    public byte[] repackData(byte[] context, byte[] data) {
        byte[] fakeData = (Globals.helpers.bytesToString(context) + "\r\n\r\n").getBytes(); //this is because we process on headers where we already lost the body of the request
        List<String> headers =  Globals.helpers.analyzeRequest(fakeData).getHeaders();               
        int headerIndex = -1;
        for(int i = 0; i < headers.size(); ++i) {
            if(headers.get(i).startsWith(setting[0])) {
                headerIndex = i;
                break;
            } 
        }

        if(0 <= headerIndex){ //found header
            headers.set(headerIndex, setting[0] + ": " + Globals.helpers.bytesToString(data));       
        }else {
            return context;
        }

        byte[] newContext = Globals.helpers.buildHttpMessage(headers, "".getBytes());            
        String tmpNewContext = Globals.helpers.bytesToString(newContext);
        tmpNewContext = tmpNewContext.substring(0, tmpNewContext.length() - 4); //removing the \r\n\r\n that was added at the end of the query when we rebuilt it.
        return tmpNewContext.getBytes();   
    }
    
}

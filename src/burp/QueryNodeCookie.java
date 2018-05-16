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
public class QueryNodeCookie extends queryNode{
    
    public QueryNodeCookie(queryNode parent) {
        super("cookie", parent);
        settingsNumber = 1;
        settingName[0] = "Cookie name:";
    }
    
    @Override
    public byte[] unpackData(byte[] data) {
        byte[] fakeData = (Globals.helpers.bytesToString(data) + "\r\n\r\n").getBytes(); //this is because we process on headers where we already lost the body of the request
        List<String> headers =  Globals.helpers.analyzeRequest(fakeData).getHeaders();
        int cookieIndex = -1;
        for(int i = 0; i < headers.size(); ++i) {
            if(headers.get(i).startsWith("Cookie:")) {
                cookieIndex = i;
                break;
            } 
        }
        if(0 <= cookieIndex){ //found cookies
            String cookies = headers.get(cookieIndex).substring("Cookie: ".length());
            String[] cookieList = cookies.split("; ");
            for(int i = 0; i < cookieList.length; ++i) {
                if(cookieList[i].startsWith(setting[0]+"=")){
                    return cookieList[i].split("=", 2)[1].getBytes();
                }
            }

            return ("cookie " + setting[0] + " not found").getBytes();        
        }else {
            return "cookies not found".getBytes();
        }
    }
    
    @Override
    public byte[] repackData(byte[] context, byte[] data) {
        byte[] fakeData = (Globals.helpers.bytesToString(context) + "\r\n\r\n").getBytes(); //this is because we process on headers where we already lost the body of the request
        List<String> headers =  Globals.helpers.analyzeRequest(fakeData).getHeaders();
        int cookieIndex = -1;
        for(int i = 0; i < headers.size(); ++i) {
            if(headers.get(i).startsWith("Cookie:")) {
                cookieIndex = i;
                break;
            } 
        }

        if(0 <= cookieIndex){ //found cookies
            String cookies = headers.get(cookieIndex).substring("Cookie: ".length());
            String[] cookieList = cookies.split("; ");
            cookies = "Cookie: ";
            for(int i = 0; i < cookieList.length; ++i) {
                if(cookieList[i].startsWith(setting[0]+"=")){
                    cookieList[i]=setting[0]+"="+Globals.helpers.bytesToString(data);
                }
                cookies = cookies + cookieList[i] + "; ";
            }
            cookies = cookies.substring(0, cookies.length() - 2);
            headers.set(cookieIndex, cookies);
        }else {
            return context;
        }

        byte[] newContext = Globals.helpers.buildHttpMessage(headers, "".getBytes());            
        String tmpNewContext = Globals.helpers.bytesToString(newContext);
        tmpNewContext = tmpNewContext.substring(0, tmpNewContext.length() - 4); //removing the \r\n\r\n that was added at the end of the query when we rebuilt it.
        return tmpNewContext.getBytes();
    }
    
}

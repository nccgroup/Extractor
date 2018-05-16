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
public class QueryNodeRequestBody extends queryNode{
    
    public QueryNodeRequestBody(queryNode parent) {
        super("Body", parent);
        settingsNumber = 0;
    }
    
    @Override
    public byte[] unpackData(byte[] data) {
        if((data != null) && (data.length > 0)) {
            String strMessage = new String(data);	
            String[] strHeadersAndContent = strMessage.split("\\r\\n\\r\\n",2);
            return strHeadersAndContent[1].getBytes();
        }
        return "no data".getBytes();
    }
    
    @Override
    public byte[] repackData(byte[] context, byte[] data) {
        String strData = new String(data);
        String strContext = new String(context);
        String[] strHeadersAndContent = strContext.split("\\r\\n\\r\\n",2);
        return (strHeadersAndContent[0] + "\r\n\r\n" + strData).getBytes();   
    }
    
}

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
public class QueryNodeBodyParameter extends queryNode{
    
    public QueryNodeBodyParameter(queryNode parent) {
        super("body parameter", parent);
        settingsNumber = 2;
        settingName[0] = "Parameter Name:";
        settingName[1] = "Boundary String:";
    }
    
    @Override
    public byte[] unpackData(byte[] data) {
        String dataString = Globals.helpers.bytesToString(data);
        String[] dataList = dataString.split("--"+setting[1]);
        for(int i = 1; i < dataList.length; ++i) { //start at 1 as index 0 is either empty or not part of the data we want
            String[] lineList = dataList[i].split("\r\n",3); 
            if(lineList.length > 1) {
                String[] paramList = lineList[1].split("; ");
                for(int j = 0; j < paramList.length; ++j) {
                    if(paramList[j].startsWith("name=\""+setting[0]+"\"")){
                        return dataList[i].split("\r\n\r\n", 2)[1].getBytes();
                    }
                } 
            }
        }
        return "no data".getBytes();
    }
    
    @Override
    public byte[] repackData(byte[] context, byte[] data) {
        String contextString = Globals.helpers.bytesToString(context);
        String[] dataList = contextString.split("--"+setting[1]);
        for(int i = 1; i < dataList.length; ++i) { //start at 1 as index 0 is either empty or not part of the data we want
            String[] lineList = dataList[i].split("\r\n",3);
            if(lineList.length > 1) {
                String[] paramList = lineList[1].split("; ");
                for(int j = 0; j < paramList.length; ++j) {
                    if(paramList[j].startsWith("name=\""+setting[0]+"\"")){
                        String[] finalList = dataList[i].split("\r\n\r\n", 2);
                        finalList[1] = Globals.helpers.bytesToString(data);
                        dataList[i] = finalList[0] + "\r\n\r\n" + finalList[1];
                        String finalData = "";
                        for(int k = 0; k < (dataList.length); ++k) { 
                            finalData += dataList[k] + "--"+setting[1];
                        }
                        return finalData.getBytes(); 
                    }
                }  
            }
        }
        return context;
    }
    
}

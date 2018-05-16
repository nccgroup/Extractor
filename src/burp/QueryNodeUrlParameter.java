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
public class QueryNodeUrlParameter extends queryNode {
    
    public QueryNodeUrlParameter(queryNode parent) {
        super("url parameter", parent);
        settingsNumber = 1;
        settingName[0] = "URL parameter name:";
    }
    
    @Override
    public byte[] unpackData(byte[] data) {
        if((data != null) && (data.length > 0)) {
            String dataString = Globals.helpers.bytesToString(data);
            
            //prepare data:
            String[] multiLineList = dataString.split("\r\n",2); //just in case we're dealing with all the headers
            String urlData;
            String[] urlDataList = multiLineList[0].split(" ", 3);
            if(urlDataList.length > 1) {
                urlData = urlDataList[1]; //we don't care about the verb here
            }else {
                urlData = urlDataList[0];
            }

            if(!(urlData.contains("?"))) { //in case we just have the list of url parameters and not a real url containing a ?
                urlData = "?" + urlData;
            }

            String[] dataList = urlData.split("\\?",2);
            String[] paramList = dataList[1].split("&");

            for(int i = 0; i < paramList.length; ++i) {
                if(paramList[i].startsWith(setting[0]+"=")){
                    return paramList[i].split("=", 2)[1].getBytes();
                }
            } 
        }
        return "no data".getBytes();
    }
    
    @Override
    public byte[] repackData(byte[] context, byte[] data) {
        String contextString = Globals.helpers.bytesToString(context);
        boolean verbPresent;
        boolean containsQuestionMark;
        //prepare data:
        String[] multiLineList = contextString.split("\r\n",2); //just in case we're dealing with all the headers
        String urlData;
        String[] urlDataList = multiLineList[0].split(" ", 3);
        if(urlDataList.length > 1) {
            urlData = urlDataList[1]; //removing verb and other info here
            verbPresent = true;
        }else {
            urlData = urlDataList[0];
            verbPresent = false;
        }
        
        if(!(urlData.contains("?"))) { //in case we just have the list of url parameters and not a real url containing a ?
            urlData = "?" + urlData;
            containsQuestionMark = false;
        } else {
            containsQuestionMark = true;
        }
        
        String[] dataList = urlData.split("\\?",2);
        String[] paramList = dataList[1].split("&"); //2);

        for(int i = 0; i < paramList.length; ++i) {
            if(paramList[i].startsWith(setting[0]+"=")){
                paramList[i]= setting[0] + "=" + Globals.helpers.bytesToString(data);
                
                String reconstructParamList = paramList[0];
                for(int j = 1; j < paramList.length; ++j) {
                    reconstructParamList += "&" + paramList[j]; 
                }
                
                if(containsQuestionMark) {
                    reconstructParamList = dataList[0] + "?" + reconstructParamList;// + dataList[1];
                }
                
                if(verbPresent) {
                    reconstructParamList = urlDataList[0] + " " + reconstructParamList;
                    if (urlDataList.length == 3) {
                        reconstructParamList = reconstructParamList + " " + urlDataList[2];
                    }
                }
                
                if (multiLineList.length == 2) {
                    reconstructParamList = reconstructParamList + "\n" + multiLineList[1];
                }
                
                
                return reconstructParamList.getBytes();
            }
        } 
        return context;
    }
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package burp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 *
 * @author nbidron
 */
public class QueryNodeGzip extends queryNode{
    
    public QueryNodeGzip(queryNode parent) {
        super("gzip", parent);
        settingsNumber = 0;
    }
    
    private class OversizedZippedDataException extends Exception {
        public OversizedZippedDataException(String message) {
            super(message);
        }
    }
   
    @Override
    public byte[] unpackData(byte[] data) {
        int len = 0;
        byte[] buffer = new byte[8192]; //size does not really matter here (we are using that just to get the size of the decoded gzipped data)
        byte[] buffer2 = data;
        try {
            //getting total length of the gzipped data
            ByteArrayInputStream textis = new ByteArrayInputStream(data);
            GZIPInputStream gis = new GZIPInputStream(textis);
            int tmplen = 0;
            while((tmplen >= 0) && (len < 10000000)) { //we don't want a zip bomb to get through so we arbitrarily limit to 10M
                tmplen = gis.read(buffer);
                if(tmplen > 0) {
                    len += tmplen;    
                }
            }
            gis.close();
            //we got the length
            
            //throw if we got a len greater or equal to 10M
            if(len >= 10000000) {
                throw new OversizedZippedDataException("gzipped data over 10M");
            }

            //now creating a big enough buffer and ungzipping to it
            if((len > 0) && (len < 10000000)) {
                buffer2 = new byte[len];
                ByteArrayInputStream textis2 = new ByteArrayInputStream(data);
                GZIPInputStream gis2 = new GZIPInputStream(textis2);
                int i;
                for(i = 0; i < len; i++) {
                    buffer2[i] = (byte)gis2.read();
                }
                gis2.close();
            }
            //done
        } catch (IOException | OversizedZippedDataException e) {
            //e.printStackTrace();
            Globals.callbacks.printError("gzip unpack error: ");
            Globals.callbacks.printError(e.getMessage());
            buffer2 = "noData".getBytes();
        }
        
        return buffer2; 
    }
    
    @Override
    public byte[] repackData(byte[] context, byte[] data) {
        byte[] result = "noData".getBytes();
        try { //gzip encode the payload
            ByteArrayInputStream textis = new ByteArrayInputStream(data);
            ByteArrayOutputStream fos = new ByteArrayOutputStream();
            GZIPOutputStream gzipOS = new GZIPOutputStream(fos);
            byte[] buffer = new byte[data.length];
            int len;
            while((len=textis.read(buffer)) != -1){
                gzipOS.write(buffer, 0, len);
            }

            gzipOS.close();
            textis.close();

            result = fos.toByteArray();
            fos.close();
        } catch (IOException e) {
            Globals.callbacks.printError("gzip packing error: ");
            Globals.callbacks.printError(e.getMessage());
        }
        return result;    
    }
    
}

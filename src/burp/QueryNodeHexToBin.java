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
public class QueryNodeHexToBin extends queryNode{
    
    public QueryNodeHexToBin(queryNode parent) {
        super("hex to bin", parent);
        settingsNumber = 0;
    }
    
    @Override
    public byte[] unpackData(byte[] data) {
        if(data.length %2 == 0) {
            byte[] result = new byte[data.length / 2];
            String tmpData = Globals.helpers.bytesToString(data);
            for (int i = 0; i < result.length; ++i) {
                result[i] = (byte) ((Character.digit(tmpData.charAt(i*2), 16) << 4) + Character.digit(tmpData.charAt(i*2+1), 16));
            }
            return result;
        }else {
            return "noData".getBytes();
        }
    }
    
    @Override
    public byte[] repackData(byte[] context, byte[] data) {
        byte[] result = new byte[data.length * 2];
        for (int i = 0; i < data.length; ++i) {
            byte[] tmpByte = new byte[1];
            tmpByte[0] = data[i];
            String hexStr = Integer.toString(tmpByte[0] & 0xff,16);
            if((tmpByte[0]& 0xff) < 16 ) { //this happens for values under 0x0F
                hexStr = "0" + hexStr;
            }
            byte[] tmpresult = hexStr.getBytes();
            result[i*2] = tmpresult[0];
            result[i*2+1] = tmpresult[1];
        }
        return result;  
    }
    
}

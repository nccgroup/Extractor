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
public class QueryNodeXOR extends queryNode{
    
    public QueryNodeXOR(queryNode parent) {
        super("XOR", parent);
        settingsNumber = 1;
        settingName[0] = "XOR with (in hex):";
    }
    
    @Override
    public byte[] unpackData(byte[] data) {
        if(setting[0].length() %2 == 0) { //convert setting (which should be in hex form) to byte[]
            byte[] settingByte = new byte[setting[0].length() / 2];
            for (int i = 0; i < settingByte.length; ++i) {
                settingByte[i] = (byte) ((Character.digit(setting[0].charAt(i*2), 16) << 4) + Character.digit(setting[0].charAt(i*2+1), 16));
            }

            byte[] result = new byte[data.length];
            for (int i = 0; i < data.length; ++i) {
                result[i] = (byte) (data[i] ^ settingByte[i% settingByte.length]); 
            }
            return result;

        }else {
            return "noData".getBytes();
        }
    }
    
    @Override
    public byte[] repackData(byte[] context, byte[] data) {
        if(setting[0].length() %2 == 0) { //convert setting (which should be in hex form) to byte[]
            byte[] settingByte = new byte[setting[0].length() / 2];
            for (int i = 0; i < settingByte.length; ++i) {
                settingByte[i] = (byte) ((Character.digit(setting[0].charAt(i*2), 16) << 4) + Character.digit(setting[0].charAt(i*2+1), 16));
            }

            byte[] result = new byte[data.length];
            for (int i = 0; i < data.length; ++i) {
                result[i] = (byte) (data[i] ^ settingByte[i% settingByte.length]); 
            }
            return result;

        }else {
            return data;
        }    
    }
    
}

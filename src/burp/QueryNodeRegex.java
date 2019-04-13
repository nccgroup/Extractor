/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package burp;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Rotem Bar
 */
public class QueryNodeRegex extends queryNode{

    public QueryNodeRegex(queryNode parent) {
        super("REGEX", parent);
        settingsNumber = 1; //you can have up to 10 here
        settingName[0] = "REGEX";
    }
    
    @Override
    public byte[] unpackData(byte[] context) {
        String contextString = Globals.helpers.bytesToString(context);

        Pattern pattern = Pattern.compile("(.*)(" + setting[0] + ")(.*)");
        Matcher matcher = pattern.matcher(contextString);
        
        if (matcher.find() && matcher.groupCount() == 4)
        {
            // String prefix = c.substring(0, matcher.start(3));
            // String suffix = c.substring(matcher.end(3));
            String d = matcher.group(3);
            
            return d.getBytes();
        }

        return "noData".getBytes();
        
    }
    
    @Override
    public byte[] repackData(byte[] context, byte[] data) {
        String contextString = Globals.helpers.bytesToString(context);
        String dataString = Globals.helpers.bytesToString(data);

        Pattern pattern = Pattern.compile("(.*)(" + setting[0] + ")(.*)");
        Matcher matcher = pattern.matcher(contextString);
        
        if (matcher.find() && matcher.groupCount() == 4)
        {
            String prefix = contextString.substring(0, matcher.start(3));
            String suffix = contextString.substring(matcher.end(3));
            
            String fullData = prefix + dataString.toString() + suffix;

            return fullData.getBytes();
        }

        return "noData".getBytes();
    }
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package burp;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 *
 * @author nbidron
 */
public class QueryNodeJson extends queryNode{
    
    public QueryNodeJson(queryNode parent) {
        super("Json", parent);
        settingsNumber = 2;
        settingName[0] = "Name:";
        settingName[1] = "Index (optional):";
    }
    
    @Override
    public byte[] unpackData(byte[] data) {        
        InputStream is;
        is = new ByteArrayInputStream(data);
        
        try {
            JsonParser parser = new JsonParser();
            JsonElement content = parser.parse(new InputStreamReader(is));
            JsonObject jsonObject = content.getAsJsonObject();
            
            
            if (setting[1].matches("\\d")) { //here we assume we have an array
                int index = Integer.parseInt(setting[1].trim());
                JsonArray jArray = jsonObject.getAsJsonArray(setting[0]);
                JsonElement element = jArray.get(index);
                if(element.isJsonObject()) {
                    return element.getAsJsonObject().toString().getBytes();
                } else if(element.isJsonArray()) {
                    return element.getAsJsonArray().toString().getBytes();
                } else if(element.isJsonPrimitive()) {
                    return element.getAsJsonPrimitive().toString().getBytes();
                } else {
                    return "no data".getBytes();
                }
            } else {
                JsonElement element = jsonObject.get(setting[0]);
                if(element.isJsonObject()) {
                    return jsonObject.getAsJsonObject(setting[0]).toString().getBytes();
                } else if(element.isJsonArray()) {
                    return jsonObject.getAsJsonArray(setting[0]).toString().getBytes();
                } else if(element.isJsonPrimitive()) {
                    return jsonObject.getAsJsonPrimitive(setting[0]).toString().getBytes();
                } else {
                    return "no data".getBytes();
                }
            }
            
        } catch(NumberFormatException ex) {
            Globals.callbacks.printError(ex.getMessage());
            Globals.callbacks.printError("Json Node: index isn't a number");
        } catch(JsonIOException | JsonSyntaxException ex) {
            Globals.callbacks.printError(ex.getMessage());
        }


        return "no data".getBytes();
    }
    
    @Override
    public byte[] repackData(byte[] context, byte[] data) {
        InputStream is;
        is = new ByteArrayInputStream(context);
        
        try {
            JsonParser parser = new JsonParser();
            JsonElement content = parser.parse(new InputStreamReader(is));
            JsonObject jsonObject = content.getAsJsonObject();
            JsonParser tmpParser = new JsonParser();
            
            if (setting[1].matches("\\d")) { //here we assume we have an array
                int index = Integer.parseInt(setting[1].trim());
                JsonArray jArray = jsonObject.getAsJsonArray(setting[0]);
                JsonElement element = jArray.get(index);
                if(element.isJsonObject()) {
                    JsonObject objFromData = tmpParser.parse(Globals.helpers.bytesToString(data)).getAsJsonObject();
                    jArray.set(index, objFromData);
                    jsonObject.add(setting[0], jArray);
                    return jsonObject.toString().getBytes();
                } else if(element.isJsonArray()) {
                    JsonArray objFromData = tmpParser.parse(Globals.helpers.bytesToString(data)).getAsJsonArray();
                    jArray.set(index, objFromData);
                    jsonObject.add(setting[0], jArray);
                    return jsonObject.toString().getBytes();
                } else if(element.isJsonPrimitive()) {
                    JsonElement objFromData = tmpParser.parse(Globals.helpers.bytesToString(data)).getAsJsonPrimitive();
                    jArray.set(index, objFromData);
                    jsonObject.add(setting[0], jArray);
                    return jsonObject.toString().getBytes();
                } else {
                    return context;
                }
            } else {
                JsonElement element = jsonObject.get(setting[0]);
                if(element.isJsonObject()) {
                    JsonObject objFromData = tmpParser.parse(Globals.helpers.bytesToString(data)).getAsJsonObject();
                    jsonObject.add(setting[0], objFromData);
                    return jsonObject.toString().getBytes();
                } else if(element.isJsonArray()) {
                    JsonArray objFromData = tmpParser.parse(Globals.helpers.bytesToString(data)).getAsJsonArray();
                    jsonObject.add(setting[0], objFromData);
                    return jsonObject.toString().getBytes();
                } else if(element.isJsonPrimitive()) {
                    JsonElement objFromData = tmpParser.parse(Globals.helpers.bytesToString(data)).getAsJsonPrimitive();
                    jsonObject.add(setting[0], objFromData);
                    return jsonObject.toString().getBytes();
                } else {
                    return context;
                }
            }
            
        } catch(NumberFormatException ex) {
            Globals.callbacks.printError(ex.getMessage());
            Globals.callbacks.printError("Json Node: index isn't a number");
        } catch(JsonIOException | JsonSyntaxException ex) {
            Globals.callbacks.printError(ex.getMessage());
        }
        return context;    
    }
    
}

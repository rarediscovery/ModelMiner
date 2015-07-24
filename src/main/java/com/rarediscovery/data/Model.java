/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rarediscovery.data;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Temitope Ajagbe
 */
public class Model {
    String id;
    Map<String, String> attributes;
    Map<String, String> units;

    public Model(String id) {
        this.id = id;
        attributes = new HashMap<>();
        units = new HashMap<>();
    }

    public String getAttributeValue(String key)
    {
       if (key == null) return "";
       
       if (attributes.get(key) == null) return "";
       
       String compound = attributes.get(key);
       String[] items = compound.split(" ");
       
       return items[0];
       
    }
    
    public String getAttributeUnit(String key)
    {
       if (key == null) return "";
       
       if (attributes.get(key.trim()) == null) return "";
       
       return units.get(key.trim());
    }
    
    public String getAttributeValueWithUnit(String key)
    {
       if (key == null) return "";
       
       if (attributes.get(key) == null) return "";
       
       if (units.get(key)== null) 
       {  
           return attributes.get(key);
       }
       
       return attributes.get(key) + " " + units.get(key);
       
    }
    
    public String getID() {
        return id;
    }

    public Set<String> getAttributeNames() {
        return attributes.keySet();
    }

    public void add(String k, String v) {
        attributes.put(k.trim(), v);
    }
    
     public void add(String k, String v, String unitOfMeasurement) {
        attributes.put(k.trim(), v);
        units.put(k.trim(), unitOfMeasurement);
    }

    public String get(String key) {
        return attributes.get(key);
    }

    @Override
    public String toString() {
        
        StringBuffer buffer = new StringBuffer();
        buffer.append("{");
        buffer.append("id :" + getID() + ";");
        
        for (String word : getAttributeNames()) {
            buffer.append(word + " : " + attributes.get(word) + ";");
        }
        buffer.append("}");
        return buffer.toString();
    }

    public String[] getAttributesValues(String[] dataArray) 
    {
       String[] values = new String[dataArray.length];
       
       for(int q=0;q< dataArray.length;q++)
       {
          values[q] = attributes.get(dataArray[q]);
       }
       
       return values;
    }
    
}

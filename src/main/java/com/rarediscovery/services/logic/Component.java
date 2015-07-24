/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rarediscovery.services.logic;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Temitope Ajagbe
 */
public class Component 
{
    String name;
    Map<String,String> attributes;
    
    public Component() 
    {
        attributes = new HashMap<>();
    }
    
    public Component name(String n)
    {
      this.name = n;   
      return this;
    }
    
    public Component addValue(String key, String value)
    {
      this.attributes.put(key, value);   
      return this;
    }

    public String getName() {
        return name;
    }

    public Set<String> getAttributeKeys() 
    {
        return attributes.keySet();
    }
    
    public String getAttributeValue(String v)
    {
      return attributes.get(v);
    }
    
        
}

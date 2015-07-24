/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rarediscovery.data;

import com.rarediscovery.services.logic.Functions;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Temitope Ajagbe
 */
public class Models {
    
    public enum As{
         Numbers,
         Strings;
    }
    
    List<String> sortedlist;
    Map<String, Model> modelMap;

    public Models() 
    {
        this.modelMap = new HashMap<>();
    } 

    public String[] getModelAttributeNames() 
    {
        
        Iterator<String> it = modelMap.keySet().iterator();
        if (it.hasNext()) 
        {
            String key = it.next();
            Model m = modelMap.get(key);
            return m.getAttributeNames().toArray(new String[0]);
        }
        return new String[]{"", "", ""};
    }

    public void addModel(Model m) {
        if (m != null) {
            //list.add(m);
            modelMap.put(m.getID(), m);
        }
    }

    public void addModels(List<Model> ms) {
        if (ms != null) {
            //list.addAll(ms);
            for (Model m : ms) {
                modelMap.put(m.getID(), m);
            }
        }
    }

    public Model get(String id) {
        return modelMap.get(id);
    }

    public List<Model> getAll() 
    {
        List<Model> list = new ArrayList<>();
        for (Model d : modelMap.values()) {
            list.add(d);
        }
        return list;
    }

    public List<Model> getModels(String...arrayOfIDs) 
    {
        List<Model> list = new ArrayList<>();
        if (arrayOfIDs == null) 
        {
           return list;
        }
        
        for (String s : arrayOfIDs) 
        {
            if (modelMap.get(s) != null) {
                list.add(modelMap.get(s));
            }
        }
        return list;
    }
    
    
    public List<Model> getModels(List<String> l) 
    {
        List<Model> list = new ArrayList<>();
        for (String s : l) 
        {
            if (modelMap.get(s) != null) {
                list.add(modelMap.get(s));
            }
        }
        return list;
    }

    public List<String> getSortedList() 
    {
        List<String> sids = Functions.forEachItemIn(getAll()).apply(Functions.ModelIDSelector());
        Collections.sort(sids);
        return sids;
    }
    
    public class NumberComparator implements Comparator<Model> 
        {
            String attribute;

            public void setAttribute(String attr) {
                this.attribute = attr;
            }
            
            public String getAttribute() 
            {
              return attribute;
            }
            
            @Override
            public int compare(Model a, Model b) 
            {
              float aValue =0;
              float bValue =0;
                
              try
              {
                String aRaw = a.get(getAttribute());
                aValue = Float.parseFloat(aRaw);
                
                String bRaw = b.get(getAttribute());
                bValue = Float.parseFloat(bRaw);
                
                // DEBUG
                //Functions.log(aValue +" - " + bValue);
                
              }catch(NumberFormatException nfe)
              {
                  Functions.log("Error ::" + a.get(getAttribute()) + " --> "+ aValue 
                          +  " ; " + b.get(getAttribute())+ " --> " + bValue);
              }
                
              if (aValue > bValue)
              {
                return 1;
              }
                
              if (aValue < bValue)
              {
                return -1;
              }
              
              return 0;  
            }
        };
    
    public class StringComparator implements Comparator<Model> 
        {
            String attribute;

            public void setAttribute(String attr) {
                this.attribute = attr;
            }
            
            public String getAttribute() 
            {
              return attribute;
            }
            
            @Override
            public int compare(Model a, Model b) 
            {
                String aValue = a.get(getAttribute());
                String bValue = b.get(getAttribute());
               
               return  aValue.compareTo(bValue); 
            }
        };
     
    public List<Model> sortModelsByAttribute(final String attribute, As criteria)
    {
        List<Model> l = getAll();
        List<Model> clone = new ArrayList<Model>();
        
        for (Model m : l)
        {
            if (m.get(attribute).trim().length() != 0)
            {
               clone.add(m);
            }
        }
        
        switch(criteria)
        {
            case Numbers:
                
                NumberComparator numberComparator = new NumberComparator();
                numberComparator.setAttribute(attribute);
                
                Collections.sort(clone, numberComparator);
                break;
                
            case Strings:
                
                StringComparator stringComparator = new StringComparator();
                stringComparator.setAttribute(attribute);
                Collections.sort(clone, stringComparator);

                break;
        }
        return clone;
    }
    
}

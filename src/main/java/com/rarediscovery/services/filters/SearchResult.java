/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rarediscovery.services.filters;

import com.rarediscovery.services.logic.Functions;
import static com.rarediscovery.services.logic.Functions.join;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 *
 * @author usaa_developer
 */
public class SearchResult 
{
    SearchRequest request;
    Map<String,List<Integer>> items;
    String[] lineNumberedTexts;
    

    public SearchResult(String[] lnt) 
    {
        items = new HashMap<>();
        lineNumberedTexts = lnt;
    }

    public void setRequest(SearchRequest req) {
        this.request = req;
    }

    
    private String[] getLineNumberedTexts() {
        return lineNumberedTexts;
    }
    
    public void add(String k , int v)
    {
        if ( k == null || v < 0) {return ;}
        
        if (items.get(k) == null )
        {
           items.put(k, new ArrayList<Integer>());
        }
        
        items.get(k).add(v);
    }
    
    public List<Integer> get(String k)
    {
       return items.get(k) == null ? Collections.EMPTY_LIST : items.get(k);
    }
    
    public String getResultFor(String k)
    {
       StringBuffer  buffer = new StringBuffer();
       
       if (k == null || get(k) == null){ return "" ;}
       
       for(int i : get(k))
       {
          buffer.append(getLineNumberedTexts()[i] + "\n");
       }
       return buffer.toString();
    }
    public Set<String> getSearchKeys()
    {
       return items.keySet();
    }
    
    public class Models
    {
        //List<Model> list;
        Map<String,Model> modelMap;

        public Models() {
            //this.list = new ArrayList<>();
            this.modelMap = new HashMap<>();
        }
       
        public String[] getColumnNames()
        {
            Iterator<String> it = modelMap.keySet().iterator();
            if (it.hasNext())
            {
                String key = it.next();
                Model m = modelMap.get(key);
                return m.getAttributeNames().toArray(new String[0]);
            }
            return new String[]{"","",""};
        }
        
        public void addModel(Model m)
        {
           if (m != null)
           {
              //list.add(m);
              modelMap.put(m.getID(), m);
           }
        }
        
         public void addModels(List<Model> ms)
        {
           if (ms != null)
           {
              //list.addAll(ms);
               for(Model m: ms)
               {
                 modelMap.put(m.getID(), m);
               }
           }
        }

        public Model get(String id)
        {
           return modelMap.get(id);
        }
         
        public List<Model> getAll() {
            
            List<Model> list = new ArrayList<>();
            for(Model d : modelMap.values())
            {
              list.add(d);
            }
            return list;
        }
        
        public List<Model> getModels(List<String> l) {
            
            List<Model> list = new ArrayList<>();
            for(String s : l)
            {
                if (modelMap.get(s) != null)
                {
                  list.add(modelMap.get(s));
                }
            }
            return list;
        }
         
    }
    
    public class Model
    {
        String id;
        Map<String,String> attributes;
        
        public Model(String id) {
            this.id = id;
            attributes = new HashMap<>();
        }

        public String getID() {
            return id;
        }

        public Set<String> getAttributeNames() {
            return attributes.keySet();
        }
      
        public void add(String k,String v)
        {
            attributes.put(k, v);
        }

        public String get(String key)
        {
          return attributes.get(key);
        }
         
        @Override
        public String toString() {
            
            StringBuffer buffer = new StringBuffer();
            buffer.append("{");
            buffer.append("id :" + getID()+ ";");
            for(String word : getAttributeNames())
            {
               buffer.append(word + " : " + attributes.get(word)+ ";");
            }
            
            buffer.append("}");
            return buffer.toString(); 
        }
                
    }
    
    /**
     * Generate Models for the Search request <br>
     * @return Collection of Models
     */
    public Models buildModels()
    {
         Models listOfModels = new Models();
                 
        // For each group of data item
        for(String key: getSearchKeys())
        {
            log("Search Key : " + key + "  - Number of Matches Found : "+ get(key).size());
            log("************************************************");
            
            // How many categories are we expecting per line ? 
            int number_of_models = request.getCategories();
                       
            // List of matched items for the search string = items.get(k)
            // Get Model IDs 
            for(int lineNumber : items.get(key))
            {
                Model[] models  = addDataToModel(lineNumber, number_of_models);
                listOfModels.addModels(Arrays.asList(models));
            }            
        }   
        
        return listOfModels;
        
    }

    protected Model[] addDataToModel(int lineNumber, int number_of_models) 
    {
         Model[] models = createModels(number_of_models, lineNumber);
        
         
        // Get all attributes of this model as specified by the SearchRequest
        for(int c = lineNumber+ request.getStartLine();c< lineNumber+request.getStopLine();c++)
        {
            // Get Element[i] for all Models
            
            String t = getLineNumberedTexts()[c];
            String[] dataFields = Functions.deflate(t.trim()).split(" ");
            int data_offset = dataFields.length - number_of_models;
            
            if (dataFields.length < number_of_models)
            {
                continue;
            }
            
            for(int vIndex=0;vIndex<number_of_models;vIndex++)
            {
                models[vIndex].add(join(dataFields, 0,data_offset-1), dataFields[data_offset + vIndex]);
            }
        }
        
        return models;
    }

    protected void debugModels(List<Model> listOfModels) {
        // **************************
        // Debug
        for(Model md : listOfModels)
        {
            log(md.toString());
        }
    }

    protected Model[] createModels(final int N, int index) 
    {
        /********************
        * // Get Model data
        ********************/
        Model[] models = new Model[N];
        
        // Locate the record or line where the fields names are defined
        String columnNamesRecord = getLineNumberedTexts()[index+ request.getFieldColumn()];
        String[] columnFields = Functions.deflate(columnNamesRecord).split(" ");
        int offset = columnFields.length - N;
        
        for(int mIndex=0;mIndex<N;mIndex++)
        {
            models[mIndex] = new Model(columnFields[offset + mIndex]);
            // log(models[mIndex].getID());
        }
        return models;
    }
    
    private static void log(String from) {
        System.out.println(from);
    }
}

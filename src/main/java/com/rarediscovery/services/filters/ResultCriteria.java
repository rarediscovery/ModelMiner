/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rarediscovery.services.filters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

/**
 *
 * @author Temitope Ajagbe
 */
public class ResultCriteria 
{
    private 
            Integer 
            startFrom , stopAt ,         /*      */
            fieldIdentifierRecord,       /* Where to find field Identifier */
            uniqueEntities;              /* How many reocrds per line  , defualt is 1 */
    
    // May be non-contiguos
    private List<Integer> recordsToSkip;

    public ResultCriteria() 
    {
        recordsToSkip = new ArrayList<>();
        uniqueEntities = 1;
    }
    
     public int readFrom() {
        return startFrom;
    }

    public int stopAt() {
        return stopAt;
    }

    public Integer getFieldIdentifierRecord() {
        return fieldIdentifierRecord;
    }

    public int getUniqueEntities() {
        return uniqueEntities;
    }

    public ResultCriteria uniqueEntities(int c) 
    {
        this.uniqueEntities = c;
        return this;
    }
    
    public ResultCriteria fieldIdentifierRecord(int fc) 
    {
        this.fieldIdentifierRecord = fc;
        return this;
    }

    public ResultCriteria startReadingFrom(int from) {
        this.startFrom = from;
        return this;
    }

    public ResultCriteria stopReadingAt(int to) {
        this.stopAt = to;
        return this;
    }
    
    public ResultCriteria skip(int v) 
    {
        this.recordsToSkip.add(v);
        return this;
    }

    public List<Integer> recordsToSkip() 
    {
        Collections.sort(recordsToSkip);
        return recordsToSkip;
    }
    
    public boolean hasFieldIdentifier(){
      return fieldIdentifierRecord == null ? false : true;
    }

    public void buildFromConfig(Properties properties, String key) 
             throws NumberFormatException
    {
  
        String from = properties.getProperty(key + ".From");
        String to = properties.getProperty(key + ".To");
        String skip = properties.getProperty(key + ".Skip");
        
        int startFrom= 0, endAt=0;
        if (from != null){ startFrom = Integer.parseInt(from);}
        if (to != null){ endAt = Integer.parseInt(to);}
        
        startReadingFrom(startFrom).stopReadingAt(endAt);
        // Format for skip lines expected ; x,y,z,w
        if (skip != null)
        {
            String[] linesToSkip = skip.split(",");
            for(String k: linesToSkip)
            {
                skip(Integer.parseInt(k));
            }
        }
        
        String field = properties.getProperty(key + ".FieldColumn");
        if (field != null)
        {
            fieldIdentifierRecord(Integer.parseInt(field));
        } 
        
        String entitiesPerRecord = properties.getProperty(key + ".EntitiesPerRecord");
        if (entitiesPerRecord != null)
        {
            uniqueEntities(Integer.parseInt(entitiesPerRecord));
        }
    }
    
}

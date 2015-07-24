/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rarediscovery.services.filters;

import com.rarediscovery.data.Models;
import com.rarediscovery.data.Model;
import com.rarediscovery.services.logic.Functions;
import static com.rarediscovery.services.logic.Functions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;


/**
 *
 * @author usaa_developer
 */
public class SearchResult 
{
    SearchQuery searchQuery;
    List<Integer> result;
    String[] lineNumberedTexts;
    

    public SearchResult(String[] lnt) 
    {
        result = new ArrayList<>();
        lineNumberedTexts = lnt;
    }

    public void queryIs(SearchQuery req) {
        this.searchQuery = req;
    }

    public SearchQuery getQuery() 
    {
        return searchQuery;
    }
    
    private String[] getLineNumberedTexts() {
        return lineNumberedTexts;
    }
    
    public void add( int v)
    {
        if (  v < 0) {return ;}
        result.add(v);
    }
    
    public List<Integer> getResult()
    {
       return result;
    }
    
        
    /**
     * Generate Models for the Search searchQuery <br>
     * @return Collection of Models
     */
    public Models buildStreamInformationModels()
    {
         Models listOfModels = new Models();
                   
        // How many categories are we expecting per line ? 
        int entitiesToCreate = searchQuery.getCriteria().getUniqueEntities();

        // List of matched result for the search string = result.get(k)
        // Get Model IDs 
        for(int lineNumber : result)
        {
            Model[] models  = createEntities(lineNumber, entitiesToCreate);
            listOfModels.addModels(Arrays.asList(models));
        }            
          
        return listOfModels;
    }

    
     /**
     * Generate Models for the Search searchQuery <br>
     * @return Collection of Models
     */
    public Models buildCustomModels(TextParser parser)
    {
        Models customModels = new Models();
       
        // List of matched result for the search string = result.get(k)
        // Get Model IDs 
        for(int lineNumber : result)
        {
            int start =lineNumber + getQuery().getCriteria().readFrom();
            int stop = lineNumber + getQuery().getCriteria().stopAt();

            List<Integer> skipList = new ArrayList<>();
            for(int x=0;x<getQuery().getCriteria().recordsToSkip().size();x++)
            {
                skipList.add(lineNumber + getQuery().getCriteria().recordsToSkip().get(x));
            }
                  
            String[] attributes = null ;
            if (getQuery().getCriteria().hasFieldIdentifier())
            {
                int fieldNames = lineNumber + getQuery().getCriteria().getFieldIdentifierRecord();
                
                String f = deflate(getLineNumberedTexts()[fieldNames]);
                log(" Fields names : " + f);
                
                attributes = f.split(" ");
            }

            String data = joinWithGaps(
                            getLineNumberedTexts(), 
                            start, stop, 
                            skipList,
                            RemoveCarriageReturns , RemoveDoubleSpaces);
            
            String skippedData = Functions.joinSelection(getLineNumberedTexts(),skipList);
            
            //Debug
            log(data);
            
            parser.setData(data);
            parser.setHeader(attributes);
            parser.setSkippedData(skippedData);
            
            List<Model> models = parser.apply();
            
            //log("# of Models found :: " + models.size());
            customModels.addModels(models);
        }    

        log("Total # of Models found :: " + customModels.getAll().size());
          
        
        return customModels;
    }
    
    /**
     * This method builds the relationship between the attributes found 
     * and how to relate them as valid property
     * 
     * 
     * @param lineNumber
     * @param numberOfModels
     * @return 
     */
    protected Model[] createEntities(int lineNumber, int numberOfModels) 
    {
         Model[] models = createModels(numberOfModels, lineNumber);
        
        int nextGap = -1;
        List<Integer> gaps = searchQuery.getCriteria().recordsToSkip();
        Iterator<Integer> gapsIterator = gaps.iterator();
        
        if ( gaps.iterator().hasNext())
        {
            nextGap =  gapsIterator.next();
        }
         
         
        // Get all attributes of this model as specified by the SearchQuery
        int startIndex = lineNumber+ searchQuery.getCriteria().readFrom();
        int stopIndex  = lineNumber+searchQuery.getCriteria().stopAt();
        
        for(int c = startIndex; c <= stopIndex; c++)
        {
             String t = getLineNumberedTexts()[c];
            
             //Skip ignore line
            if ( c == lineNumber + nextGap)
            {
                 //@ignore
                 //log("*** "+ dataArray[i]);
                                  
                if (! gaps.isEmpty() && gapsIterator.hasNext())
                {
                   nextGap =  gapsIterator.next();
                }
                
                //Debug
                //log(" -- IGNORE--  " + t);
                continue;
            }
            
            // Get Element[i] for all Models
            
           
            String[] dataFields = deflate(t.trim()).split(" ");
            int data_offset = dataFields.length - numberOfModels;
            
            if (dataFields.length < numberOfModels)
            {
                continue;
            }
            
            for(int vIndex=0;vIndex<numberOfModels;vIndex++)
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
        String columnNamesRecord = getLineNumberedTexts()[index+ searchQuery.getCriteria().getFieldIdentifierRecord()];
        String[] columnFields = deflate(columnNamesRecord).split(" ");
        int offset = columnFields.length - N;
        
        for(int mIndex=0;mIndex<N;mIndex++)
        {
            models[mIndex] = new Model(columnFields[offset + mIndex]);
            // log(models[mIndex].getID());
        }
        return models;
    }

   
    
   

    
}

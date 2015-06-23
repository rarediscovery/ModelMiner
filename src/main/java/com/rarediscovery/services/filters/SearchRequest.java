/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rarediscovery.services.filters;

/**
 *
 * @author usaa_developer
 */
public class SearchRequest 
{
    private int startLine , stopLine ,
            fieldColumn,
            categories;
    boolean singleModelProperty;
    
    private String 
            searchValue;
    

    public SearchRequest(String newValue) 
    {
        this.searchValue = newValue;
    }

    public SearchRequest setCategories(int c) 
    {
        this.categories = c;
        return this;
    }
    
    public SearchRequest setFieldColumn(int fc) 
    {
        this.fieldColumn = fc;
        return this;
    }

    public SearchRequest readFrom(int from) {
        this.startLine = from;
        return this;
    }

    public SearchRequest readTo(int to) {
        this.stopLine = to;
        return this;
    }

    public String getSearchValue() {
        return searchValue;
    }

    public int getStartLine() {
        return startLine;
    }

    public int getStopLine() {
        return stopLine;
    }

    public int getFieldColumn() {
        return fieldColumn;
    }

    public int getCategories() {
        return categories;
    }

    public boolean isSingleModelProperty() {
        return singleModelProperty;
    }
  
    
}

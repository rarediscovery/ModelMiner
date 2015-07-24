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
public class SearchQuery 
{
    private String keyword;
    private ResultCriteria criteria;
    
    private SearchQuery() 
    {        
    }

    public SearchQuery(String newValue) 
    {
        this();
        this.keyword = newValue;
    }
    
    public String getKeyword() 
    {
        return keyword;
    }

   
    public ResultCriteria newCriteria() 
    {
        this.criteria = new ResultCriteria();
        return this.criteria;
    }

    public ResultCriteria getCriteria() {
        return criteria;
    }
    
}

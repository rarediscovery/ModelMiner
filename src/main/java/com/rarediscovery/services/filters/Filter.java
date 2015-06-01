/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rarediscovery.services.filters;

import java.util.List;

/**
 *
 * @author usaa_developer
 */
interface Filter {
    
    enum FilterState
    {
       Continue,  // I have found a start tag , 
                  // I will need to check more tokens to establish a match or no match
       Matched ,
       NoMatchFound
       ;
       public boolean is(FilterState fs){
         return fs != null && fs.equals(this);
       }
    }
  
    public FilterState filter(String text);
    public FilterState filterAll(List<String> allText);
    public String filteredText();
}

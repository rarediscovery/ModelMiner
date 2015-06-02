/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rarediscovery.services.logic;

/**
 *
 * @author usaa_developer
 */
public class Functions {
    
    /**
     * Remove consecutive empty characters from a line item
     * 
     * @param data
     * @return Deflated string
     */
    public static String deflate(String data)
    {
        int dataLength = data.length();
        StringBuffer buffer = new StringBuffer();
        
        int symbolCount =0 ;
        char targetSymbol = ' ', currentSymbol = 0;

        int i =0;
        while(i< dataLength-1)
        {
           if (data.charAt(i) == targetSymbol && data.charAt(i+1) == targetSymbol)
           {
              i++;
              continue;
           }
                     
           buffer.append(data.charAt(i));
           i++;
        }

        return buffer.toString();
    }
}

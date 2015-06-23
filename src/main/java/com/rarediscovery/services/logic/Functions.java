/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rarediscovery.services.logic;

import java.awt.FileDialog;
import java.io.File;
import java.io.FilenameFilter;
import java.util.List;
import javax.swing.JFrame;

/**
 *
 * @author usaa_developer
 */
public class Functions {
    
    private static String NL = "\n";
     
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
    
     public static String join(String[] array,int from, int to)
     {
         if (array == null || array.length == 0 || to < from) { return "";}
         
         StringBuffer buffer = new StringBuffer();
         for(int i=from;from < array.length && i <= to; i++)
         {
           buffer.append( array[i] + " ");
         }
         
         return buffer.toString();
     }
     
    /**
     * 
     * @param fileExtension
     * @return The name of file selected <br> 
     */
    public static String getUserSelectedFile(final String fileExtension)
    {
        FileDialog dialog = new FileDialog(new JFrame());
        dialog.setFilenameFilter(
                new FilenameFilter() 
                {
                    @Override
                    public boolean accept(File dir, String name)
                    {
                        return name.endsWith("."+fileExtension);
                    }
                });

        dialog.setVisible(true);
        return dialog.getDirectory() + dialog.getFile();
    }
      
      
    public static void log(String s)
    {
        System.out.println(s);
    }
    
    public static void logList(List<String> ls)
    {
        StringBuffer buffer = new StringBuffer();
        for(String item: ls)
        {
           buffer.append(item + NL);
        }
        log(buffer.toString());
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rarediscovery.services.logic;

import com.rarediscovery.services.filters.Driver;
import com.rarediscovery.services.filters.SearchResult;
import java.awt.FileDialog;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

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
    
    /**
     *  FileName,
     *  Map<worsksheetName,WorksheetData>
     *  WorksheetData  = columnName[] , dataGrid[][]
     * 
     * , Map<String,String[][]> tabTodataGridMap
     */
    public static void saveAsExcelWorkbook(
            String filename, 
            final Map<String,List<String>> sheetToColumns,
            final SearchResult.Models models,
            DefaultListModel reportAttributes )
    {
         
      Workbook wb = new HSSFWorkbook();
      FileOutputStream fileOut;
      try 
      {
            fileOut = new FileOutputStream(filename);
            
            //CreationHelper createHelper = wb.getCreationHelper();
            
            
            // Create worksheets
            int sheetCount = sheetToColumns.size();
            Sheet[] sheets = new Sheet[sheetCount];
            int i = 0;
            for(String sheetName : sheetToColumns.keySet())
            {  
               sheets[i] = wb.createSheet(" " + sheetName);
               String[] reportHeaders = sheetToColumns.get(sheetName).toArray(new String[0]);
               addContent(sheets[i],reportHeaders, models ,reportAttributes );
               i++;
            }
                       
            /*
            // Create a row and put some cells in it. Rows are 0 based.
            Row row = sheet1.createRow((short)0);
            // Create a cell and put a value in it.
            Cell cell = row.createCell(0);
            cell.setCellValue(1);

            // Or do it on one line.
            row.createCell(1).setCellValue(1.2);
            row.createCell(2).setCellValue(createHelper.createRichTextString("This is a string"));
            row.createCell(3).setCellValue(true);
            */
            
            wb.write(fileOut);
            fileOut.close();
            
            Functions.log(" Completed generating excel report ! - " + filename);
      
      } catch (FileNotFoundException ex) 
      {
            Logger.getLogger(Driver.class.getName()).log(Level.SEVERE, null, ex);
      } catch (IOException ex) {
            Logger.getLogger(Driver.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void addContent(
            Sheet sheet,                                    // Sheet to be updated
            String[] columnHeaders,                         // Selected columns
            final SearchResult.Models models,               // Available models in memory
            final DefaultListModel reportAttributes)        // List of attributes selected to be reported
    {
        
         // Create Headers
        Row header = sheet.createRow((short)0);
        header.createCell(0).setCellValue("Index" );
        header.createCell(1).setCellValue("Component Name");
        for(int i =0;i< columnHeaders.length ;i++)
        {
            
            header.createCell(2+i).setCellValue(columnHeaders[i]);
        }
        
        int i=1;
        Enumeration<String> ram = reportAttributes.elements();
        while(ram.hasMoreElements())
        {
           Row row = sheet.createRow(i);
           row.createCell(0).setCellValue(""+i );
           String component = ram.nextElement(); 
           row.createCell(1).setCellValue(component);
        
           int k =0;
           for(String modelName : columnHeaders)
           {
              row.createCell(2+k).setCellValue( models.get(modelName).get(component));
              k++;
           }
          
           i++;
        }
    }
}

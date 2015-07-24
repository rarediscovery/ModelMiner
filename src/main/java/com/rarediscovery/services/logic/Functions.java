/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rarediscovery.services.logic;

import com.rarediscovery.app.client.Driver;
import com.rarediscovery.data.Model;
import com.rarediscovery.data.Models;
import com.rarediscovery.services.filters.TextParser;
import java.awt.FileDialog;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

/**
 *
 * @author Temitope Ajagbe
 */
public class Functions {
    
    public static final String 
                                NewLine =  "\n",
                                LineFeed = "\r",
                                TokenSplitter = "#";
    

    public static void addRowData(final String[][] grid, String[] dataArray, int row, int column) {
        for(int i=0; i <  dataArray.length ;i++)
        {
           grid[row][column+i] = dataArray[i];
        }
    }

    public static void addColumnData(final String[][] grid, String[] dataArray, int row, int column) {
        
        for(int i=0; i <  dataArray.length ;i++)
        {
           grid[row+i][column] = dataArray[i];
        }
    }


    
    public static class EachItem<In>
    {
      List<In> list ;
      
      public EachItem(List<In> l) 
      {
         this.list = l;
      }
      
      public EachItem(Set<In> l) 
      {
         this.list = new ArrayList<>();
         for(In item: l)
         {
           this.list.add(item);
         }
      }
      
      public <Out> List<Out> apply(Functions.DataFilter<In,Out> filter)
      {
        List<Out> temp  = new ArrayList<>();
          
         for(In item : list)
         {
             if (item == null){ continue;}
             temp.add(filter.apply(item));
         }
         
         return temp;
      }
      
      public List<In> get()
      {
         return list;
      }
    }
    
    public static <T> EachItem forEachItemIn(List<T> items)
    {
       return new EachItem(items);
    }
     
     public static <T> EachItem forEachItemIn(Set<T> items)
    {
       return new EachItem(items);
    }
    
    public static <T> EachItem forEachItemIn(DefaultComboBoxModel<String> list)
    {
        List<String> items = new ArrayList<>();
        
        for(int i=0;i< list.getSize();i++)
        {
            items.add(list.getElementAt(i));
        }
       return new EachItem(items);
    }
      
    public static <T> EachItem forEachItemIn(DefaultListModel<String> list)
    {
        List<String> items = new ArrayList<>();
        
        for(int i=0;i< list.getSize();i++)
        {
            items.add(list.getElementAt(i));
        }
       return new EachItem(items);
    }
    
     public static <T> EachItem forEachItemIn(Properties list)
    {
        List<String> items = new ArrayList<>();
        
        for(Object k: list.keySet())
        {
            items.add((String)k);
        }
       return new EachItem(items);
    }
    
     public static Functions.DataFilter<String,String> Print()
     { 
       return new Functions.DataFilter<String,String>() 
       {
           @Override
           public String apply(String data) {
             log(data);
             return data;
            };
        };
     }
     
     public static Functions.DataFilter<String,String> addToList(final DefaultComboBoxModel<String> list)
     { 
       return new Functions.DataFilter<String,String>() 
       {
           @Override
           public String apply(String data) {
             list.addElement(data);
             return "";
            };
        };
     }
     
      public static Functions.DataFilter<String,String> addToList(final DefaultListModel<String> list)
     { 
       return new Functions.DataFilter<String,String>() 
       {
           @Override
           public String apply(String data) {
             list.addElement(data);
             return "";
            };
        };
     }
      
     public static Functions.DataFilter<String,String> addToList(final List<String> list)
     { 
       return new Functions.DataFilter<String,String>() 
       {
           @Override
           public String apply(String data) {
             list.add(data);
             return "";
            };
        };
     }
     
    public static Functions.DataFilter<Model, String> ModelIDSelector() 
    {
          return new Functions.DataFilter<Model,String>() 
          {

            @Override
            public String apply(Model data) 
            {
                return data.getID();
            }
        }; 
     }
    
     /*
     String stringIamLookingFor ="Query";
        List<String> l = new ArrayList<>();
        for( Object pk : properties.keySet())
        {
            String key = (String) pk;
          if (key.startsWith(stringIamLookingFor))
          {
             l.add(key);
          }
        }
     
    */
    
    /**
     * Remove consecutive empty characters from a line item
     * 
     * @param data
     * @return Deflated string
     */
    public static String deflate(String data)
    {
        
        // Bad data input will not be processed
        if (data == null) { return data ;}
                
        char targetSymbol = ' ', currentSymbol = 0;
        
        // Base Cases : Length 1 or 2
        int dataLength = data.length();
        
        if (dataLength == 1)
        { 
            return data ;
        }
        
        if (   dataLength == 2 
            && data.charAt(0) == data.charAt(1) 
            && data.charAt(0) == targetSymbol)
        {
            return " ";
        }
                
        StringBuffer buffer = new StringBuffer();
       
        int i =0,j=1;
        while(j< dataLength)
        {
           if ( data.charAt(i) == targetSymbol 
             && data.charAt(i) == data.charAt(j))
           {
              i++;j++;
              continue;    //Skip the duplicate
           }
                     
           buffer.append(data.charAt(i));
           i++;
           j++;
        }

        buffer.append(data.charAt(i));
        return buffer.toString().trim();
    }
    
    public interface DataFilter<TIn,TOut>
    {
         public TOut apply(TIn data);
    }
    
     /**
     * Takes a array of String and concatenates items from start-index to finish-index <br>
     * 
     * @param dataArray
     * @param startIndex
     * @param finishIndex
     * @return 
     */
     public static String joinWithGaps(
             final String[] dataArray,
             int startIndex, int finishIndex, 
             List<Integer> skipList,
             DataFilter...filters)
     {
         // Sanitize
         if (dataArray == null || dataArray.length == 0 || finishIndex < startIndex) { return "";}
         
         // Create buffer for final result
         StringBuffer buffer = new StringBuffer();
         
         // Handle items to skip
         int nextGap = startIndex -1;   // Never possible 
         int skips = skipList.size();
         int skipIndex = 0;
         
         
         // Join all items from start to end or till we get to the end of the array
         for(  int i=startIndex;         /* Begin */
                   i < dataArray.length  /* Exit Condition */
                   && i <= finishIndex;  
                   i++)                 /* Begin */
         {
            //Skip ignore line
            if (skips > 0 && skipIndex < skips)
            {                  
                nextGap = skipList.get(skipIndex);
                if (nextGap == i)
                {
                   skipIndex++;
                   continue;
                }
            }
             
            String v = dataArray[i];
             
            // Apply Filters
            if (filters.length > 0) 
            {
              for(DataFilter<String,String> dataFilter: filters)
              {
                  v = dataFilter.apply(v);
              }
            }
             
             buffer.append( v + " ");
         }
         
         return buffer.toString();
     }
    
    
    /**
     * Takes a array of String and concatenates items from start-index to finish-index <br>
     * 
     * @param dataArray
     * @param startIndex
     * @param finishIndex
     * @return 
     */
     public static String join(final String[] dataArray,int startIndex, int finishIndex, DataFilter<String,String>...filters)
     {
         // Sanitize
         if (dataArray == null || dataArray.length == 0 || finishIndex < startIndex) { return "";}
         
         // Create buffer
         StringBuffer buffer = new StringBuffer();
         
         // Join all items from start to end or till we get to the end of the array
         for(  int i=startIndex;         /* Begin */
                   i < dataArray.length  /* Exit Condition */
                   && i <= finishIndex;  
                   i++)                 /* Begin */
         {
             String v = dataArray[i];
             if (filters.length > 0) 
             {
                for(DataFilter<String,String> dataFilter: filters)
                {
                   v = dataFilter.apply(v);
                }
             }
             buffer.append( v + " ");
         }
         
         return buffer.toString();
     }
     
     
    public static String joinSelection(String[] sourceDataArray, List<Integer> list) 
    {
        StringBuffer buffer = new StringBuffer();
        for(Integer lineNumber : list)
        {
             if (lineNumber < 0 || lineNumber >= sourceDataArray.length)
             {
                continue;
             }
             
             String v = deflate(sourceDataArray[lineNumber]);
             buffer.append(v + " # ");
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
                        return name.endsWith("."+fileExtension.toLowerCase())
                             || name.endsWith("."+fileExtension.toUpperCase());
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
           buffer.append(item + NewLine);
        }
        log(buffer.toString());
    }
    
    
    
    public static void save(WorkPad w)
    {
       if (w == null) {return;}
       
        try 
        {
           if (w.outputStream == null)
           {
              w.workbook.close();
           } else
           {
              w.save();
           }
           
        } catch (IOException ex) {
            Logger.getLogger(Functions.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    public static WorkPad createOrUpdateWookbook(String fileName)
    {
          WorkPad workPad = null;
          Workbook wb = null;
        
          try 
          { 
              File file = new File(fileName);
              if (file.exists() && file.isFile())
              {
                  FileInputStream fileInputStream = new FileInputStream(file);
                  wb = new HSSFWorkbook(fileInputStream);                  
                  workPad = new WorkPad(wb).setInputStream(fileInputStream);
                          
              }else
              {
                
                  wb = new HSSFWorkbook();                  
                  workPad = new WorkPad(wb);
                 
              }
              
              FileOutputStream fileOutputStream = new FileOutputStream(file);
              workPad.setOutputStream(fileOutputStream);
                                 
             } catch (FileNotFoundException ex) {
                 Logger.getLogger(Functions.class.getName()).log(Level.SEVERE, null, ex);
             } catch (IOException ex) {
            Logger.getLogger(Functions.class.getName()).log(Level.SEVERE, null, ex);
        }
         
        return workPad;
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
            final Models models,
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
            fileOut.flush();
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
            final Models models,               // Available models in memory
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
    
    public static DataFilter RemoveCarriageReturns = new DataFilter<String,String>() 
        {

            @Override
            public String apply(String data) 
            {
              return data.replace("\r", "");
            }
        };
        
    public static DataFilter RemoveDoubleSpaces = new DataFilter<String,String>() {

            @Override
            public String apply(String data) 
            {
              return deflate(data);
            }
        };
    
    
     public static TextParser OTConverterParser = new TextParser() 
        {

            int VALUE =0, UNIT=1;
            String sourceData;
            String skippedData;
            
            @Override
            public List<Model> apply() 
            {
                List<Model> result = new ArrayList<>();
                
                Model m = parseSkippedData();
                                
                String[] sd = sourceData.split("=");
                int howMany = sd.length;
                
                String keyOrValue = sd[0];
                for(int i=1;i< howMany;i++)
                {
                   String[] kv = sd[i].trim().split(" ");
                   
                   
                   //case 1: Just one key 
                   if (kv.length == 1)
                   {
                      m.add(keyOrValue, kv[VALUE]);
                   }
                  
                   //case 2: has value , unit and anotherKey
                   if (kv.length >= 2)
                   {
                      m.add(keyOrValue, kv[VALUE], kv[UNIT]);
                      
                      keyOrValue = Functions.join(kv, 2, kv.length);
                     
                   }
                }
                
                //DEBUG
                //Functions.forEachItemIn(m.getAttributeNames()).apply(Functions.Print());
                
                result.add(m);
                return result;
            }

        protected Model parseSkippedData() throws NumberFormatException {
            //DEBUG
            //log(" *** SKPIPPED *** "+ NewLine + skippedData);
            String[] tokens = skippedData.split("#");
            
            Model m = new Model(tokens[4].trim());
            
            String[] initialValue = tokens[2].trim().split(" ");
            String[] finalValue = tokens[3].trim().split(" ");
            
            m.add("STEP", m.getID());
            
            // More Data
            String[] hDelta = {"Delta Bed Length", "Delta MOLE % NH3", "Delta TEMP, C"," Delta XI"};
            String[] hInitValue = {"Bed Length", "MOLE % NH3", "TEMP, C"," XI"};
            for(int j=0;j< initialValue.length;j++)
            {
                
                float iv = Float.parseFloat(initialValue[j]);
                float fv = Float.parseFloat(finalValue[j]);
                
                float delta = fv - iv;
                
                m.add(hDelta[j], ""+delta);  /* */
                m.add(hInitValue[j], ""+iv);
                
            }
            return m;
        }

            @Override
            public void setData(String data) {
              sourceData = data;
            }

            @Override
            public void setHeader(String[] header) {
                
            }

            @Override
            public void setSkippedData(String data) {
                skippedData = data;
            }
        };
     
        public static TextParser ShiftConverterParser = new TextParser() 
        {
            String sourceData;
            String[] headerData;
            String skippedData;

            @Override
            public List<Model> apply() 
            {
                List<Model> result = new ArrayList<>();
                
                if ( sourceData == null) return result;
                
                String[] sd = sourceData.split(" ");
                int j=0;
                while(j < sd.length)
                {
                    Model m = new Model(""+ Math.random());
                    for(int i=0;i< headerData.length;i++)
                    {
                       m.add(headerData[i], sd[j]);
                       j++;
                    }
                    
                    // check for validity
                    boolean error = false;
                    try{
                        Float.parseFloat(m.get(headerData[headerData.length-1]));
                    }catch(NumberFormatException nfe)
                    {
                        error = true;
                    }
                    
                    if(! error)
                    {
                      result.add(m);
                    }
                }
                
                log(" Completed Processing Shift Converter Data");
                
                return result;
            }

            @Override
            public void setData(String data) {
              sourceData = data;
            }

            @Override
            public void setHeader(String[] header) {
                headerData = header;
            }
            
            @Override
            public void setSkippedData(String data) {
                skippedData = data;
            }
    };
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rarediscovery.services.logic;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

/**
 *
 * @author Temitope Ajagbe
 */
public class WorkPad {
    
    FileOutputStream outputStream;
    FileInputStream inputStream;
    Workbook workbook;
    private boolean alignRight;
    private boolean bold;
    private boolean backgroundColor;

    public WorkPad(Workbook w) {
        this.workbook = w;
    }

    public WorkPad setInputStream(FileInputStream inputStream) {
        this.inputStream = inputStream;
        return this;
    }

    public WorkPad setOutputStream(FileOutputStream outputStream) {
        this.outputStream = outputStream;
        return this;
    }

    public FileInputStream getInputStream() {
        return inputStream;
    }

    public FileOutputStream getOutputStream() {
        return outputStream;
    }

    public Workbook getWorkbook() {
        return workbook;
    }

    public Sheet addWorksheet(String sheetName) {
        if (workbook == null) {
            return null;
        }
        ;
        Sheet worksheet = workbook.getSheet(sheetName);
        if (worksheet == null) {
            worksheet = workbook.createSheet(sheetName);
        }
        return worksheet;
    }

    public void save() {
        try {
            workbook.write(outputStream);
            outputStream.close();
            outputStream.flush();
            ;
        } catch (IOException ex) {
            Logger.getLogger(Functions.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * 
     * Add a column of data to a worksheet
     * 
     * @param sheetName
     * @param dataArray
     * 
     * @param startingRow 
     * @param dataColumn
     */
    public void addColumnData(String sheetName ,String[] dataArray ,  int startingRow,int dataColumn ) 
    {
        Sheet s = addWorksheet(sheetName);
        
        CellStyle style = applySelectedStyle();
                
        for(int r =0 ;r < dataArray.length;r++)
        {
             Row row =  null;
            // When item requested is out of range of available rows
            if ( s.getLastRowNum() < startingRow + r)
            {
                row = s.createRow(startingRow + r);        
               
            }else
            {
               row = s.getRow(startingRow + r);
            }
         
            row.createCell(dataColumn).setCellValue(dataArray[r]);
            row.setRowStyle(style);
        }
    }

    public WorkPad colonOn()
    {
         backgroundColor = true;
         return this;
    }
    
    public WorkPad colonOff()
    {
         backgroundColor = false;
         return this;
    }
    
    public WorkPad alignRight()
    {
         alignRight = true;
         return this;
    }
    
    public WorkPad alignLeft()
    {
         alignRight = false;
         return this;
    }
    
    public WorkPad bold()
    {
         bold = true;
         return this;
    }
    
    public WorkPad plain()
    {
         bold = false;
         return this;
    }
    
    public void addRowData(String sheetName, String[] dataArray,int rowIndex,int column) 
    {
        Sheet s = addWorksheet(sheetName);
        Row row =  null;
         
        // When item requested is out of range of available rows
        if ( s.getLastRowNum() > rowIndex)
        {
           row = s.getRow(rowIndex);       
        }else
        {
           row = s.createRow(rowIndex); 
        }
        
        CellStyle style = applySelectedStyle();
        
        for(int r =0 ;r < dataArray.length;r++)
        {
            row.createCell(column + r).setCellValue(dataArray[r]);
            row.getCell(column + r).setCellStyle(style);
        }
        //font.setBold(false);
    }

    protected CellStyle applySelectedStyle() 
    {
        Font font = workbook.createFont();
        if (bold)
        {
            font.setBold(true);
        }else{
            font.setBold(false);
        }
        
        CellStyle style = workbook.createCellStyle();
        style.setFont(font);
        
        if (alignRight)
        {
            style.setAlignment(CellStyle.ALIGN_RIGHT);
        }else
        {
            style.setAlignment(CellStyle.ALIGN_LEFT);
        }
        
        if (backgroundColor){
            style.setFillBackgroundColor(IndexedColors.ORANGE.getIndex());
            style.setFillPattern(CellStyle.BIG_SPOTS);
        }else
        {
            style.setFillBackgroundColor(IndexedColors.WHITE.getIndex());
        }
        
        return style;
    }
    
}

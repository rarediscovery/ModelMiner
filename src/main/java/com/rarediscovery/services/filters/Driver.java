/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rarediscovery.services.filters;

import com.rarediscovery.services.logic.TextReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

/**
 *
 * @author usaa_developer
 */
public class Driver 
{
    
    public static void main(String[] args) 
    {
      //testSearchFilter();
     
     
        
    }

    protected static void testSearchFilter() {
        //log(new StringFilter("is", "to").fromInputString("This is so good to ignore"));
        
        TextReader reader = new TextReader(new File("C:\\Users\\usaa_developer\\Desktop\\Reader Project\\sample.pdf"));
        String msg  = reader.convertPDFToString();
             
        //testAPI(msg);
        
        //String t11 = new StringFilter().given(t1).selectManyAttributes("STREAM ID","  ENTHALPY");
        //String t12 = new StringFilter().given(t1).selectManyAttributes("ENTHALPY" , "TEMPERATURE" , "PRESSURE");
        //String t12 = new StringFilter().given(t1).selectManyAttributes("STREAM ID", "ENTHALPY MM KCAL/HR" , "TEMPERATURE" , "PRESSURE");
        //String t12 = new StringFilter().given(msg).selectManyAttributes("STREAM ID", "ENTHALPY MM KCAL/HR" , "TEMPERATURE" , "PRESSURE","HYDROGEN" , "NITROGEN " , "OXYGEN ");
        // String t12 = new StringFilter().given(msg).selectManyAttributes("STEP","CRITICAL TEMPERATURE, C","CRITICAL PRESSURE, KGF/CM2" ,"INLET GAS FLOW, ACFM");
        
        /*
        String t12 = new StringFilter()
        .given(msg)
        .selectManyAttributes(
        "STEP",
        "EQUILIBRIUM CONSTANT @ OUTLET TEMP",
        "% N2 CONVERTED AT EQUILIBRIUM",
        "OUTLET CONV OF N2","OUTLET TEMP ");
        */
        String[] searchItems = new String[]
        {
            "CNTRLD, SPEC, ADJSTD",//0
            "% N2 CONVERTED AT EQUILIBRIUM",//1
            "EQUILIBRIUM CONSTANT @ OUTLET TEMP",//2
            "* * * * * * * * * * * * * * * * * * * * * * * * *",//3
            "PROCESS FLOW STREAM RECORD" , //2,12,1
            "CENTRIFUGAL COMPRESSOR INLET AND OUTLET CONDITIONS"  //5

        };
        
        SearchRequest  request = new SearchRequest(searchItems[4]);
        request
                .readFrom(2)
                .readTo(12)
                .setFieldColumn(1)
                .setCategories(4);
        
        SearchResult result = new StringFilter()
                .given(msg)
                .find(request);
        
        result.debugModels(result.buildModels().getAll());
    }

    protected static void testAPI(String msg) 
    {
        
         /*
           String longText = 
            "It is is is is is is is is a common weakness among traditional communication protocols to be vulnerable to\n" +
            "impersonation attacks. Every time this sort of protocol is executed, the system degradates\n" +
            "because of the threat of an eavesdropper listening in on the communication. Zero Knowledge\n" +
            "Protocols, presented by Goldwasser, Micali, and Rackoff, are an improvement on\n" +
            "these situations. The objective is to obtain a system in which it is possible for a prover to\n" +
            "convince a verifier of his knowledge of a certain secret without disclosing any information\n" +
            "except the validity of his claim. This article will cover the basics of zero knowledge systems,\n" +
            "explaining the main properties and characteristics. A series of examples, in growing level\n" +
            "of difficulty, will also be presented, to see the main areas of application of such protocols."
        ;
        */
        
        String t1 = new StringFilter("1", "X....0\r").fromInputString(msg);
        String t2 = new StringFilter("                   1", "X....0....X....0\r").fromInputString(t1);
        String t3 = new StringFilter("1", "-----------\r").fromInputString(t2);
        String t4 = new StringFilter("                   1", "-----------------------\r").fromInputString(t3);
        String t5 = new StringFilter("1", null).removeLine(t4);
        String t6 = new StringFilter("                   1         2 ", null).removeLine(t5);
        //String t7 = new StringFilter("AMMSYN", " C\r").select(t6);
        //String t7 = new StringFilter("STREAM", "           ETHANE").selectInclusive(t6);
        //String t8 = new StringFilter("EQUILIBRIUM", "OUTLET CONV").selectPair(t7);
        //String t9 = new StringFilter(" STREAM ", " FLOW RATE").selectPair(t6);
        //String t10 = new StringFilter(" HYDROGEN  ", " NITROGEN").selectPairAttribute(t6);
    }

    private static void log(String from) {
        System.out.println(from);
    }
}

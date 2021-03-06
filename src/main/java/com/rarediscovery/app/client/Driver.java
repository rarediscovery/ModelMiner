/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rarediscovery.app.client;

import com.rarediscovery.data.Model;
import com.rarediscovery.data.Models;
import com.rarediscovery.services.filters.ResultCriteria;
import com.rarediscovery.services.filters.SearchQuery;
import com.rarediscovery.services.filters.SearchResult;
import com.rarediscovery.services.filters.StringFilter;
import com.rarediscovery.services.filters.TextParser;
import com.rarediscovery.services.logic.Functions;
import com.rarediscovery.services.logic.TextReader;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author usaa_developer
 */
public class Driver 
{
    
    public static void main(String[] args) 
    {
      testSearchFilter();    
    }

    protected static void testSearchFilter() {
        //log(new StringFilter("is", "to").fromInputString("This is so good to ignore"));
        
        TextReader reader = new TextReader(new File("C:\\Users\\usaa_developer\\Desktop\\Reader Project\\sample.pdf"));
        String msg  = reader.convertPDFToString();
             
        //testAPI(msg);
        
        
        SearchQuery query = new SearchQuery("NH3 CONVERTER TEMP AND N2 PROFILE");
        ResultCriteria criteria = query.newCriteria();
        criteria
                .startReadingFrom(-3).stopReadingAt(6)
                .skip(0).skip(1).skip(2).skip(3).skip(6)
                .fieldIdentifierRecord(1);
        
       /*
          SearchQuery query = new SearchQuery("SHIFT CONVERTER TEMP AND CO PROFILE");
          ResultCriteria criteria = query.newCriteria();
          criteria.startReadingFrom(2).stopReadingAt(19).fieldIdentifierRecord(1).skip(0) ;  
       */
       
        SearchResult result = new StringFilter()
                        .given(msg).execute(query);
        
        //result.debugModels(result.buildStreamInformationModels().getAll());
        Models models = result.buildCustomModels(Functions.OTConverterParser);
        
        Functions.forEachItemIn(Arrays.asList(models.getModelAttributeNames())).apply(Functions.Print());
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

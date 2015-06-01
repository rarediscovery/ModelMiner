/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rarediscovery.services.filters;

import com.rarediscovery.services.logic.TextReader;
import java.io.File;

/**
 *
 * @author usaa_developer
 */
public class Driver {
    public static void main(String[] args) {
        
        //log(new StringFilter("is", "to").fromInputString("This is so good to ignore"));
        
        TextReader reader = new TextReader(new File("C:\\Users\\usaa_developer\\Desktop\\Reader Project\\sample.pdf"));
        String msg  = reader.convertPDFToString();
         
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
        //String t7 = new StringFilter("AMMSYN", " C\r").select(t6);
        log(t6);
    }

    private static void log(String from) {
        System.out.println(from);
    }
}

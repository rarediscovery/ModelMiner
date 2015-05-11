/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rarediscovery.services.model;

import com.rarediscovery.services.logic.Delimiters;
import com.rarediscovery.services.logic.Pair;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author usaa_developer
 */
public class Stream {
    
    String id;
    Map<Integer, Pair> data;
    final Indexer clock;

    private static final String[] 
            attributes = {
                        "EQUILIBRIUM CONSTANT @ OUTLET TEMP",
                        "% N2 CONVERTED AT EQUILIBRIUM",
                        "EQUILIBRIUM TEMPERATURE,C",
                        "BED LENGTH",
                        "PRESS DROP",
                        "OUTLET CONV OF N2",
                        "OUTLET TEMP"
                       };
    
    class Indexer {

        int i = 0;

        public Indexer() {
            i = 0;
        }

        public Indexer(int initValue) {
            i = initValue;
        }

        public int next() {
            return i++;
        }

        public int get() {
            return i;
        }
    }

    public String getId() {
        return id;
    }

    public Stream(String uid) {
        id = uid;
        data = new HashMap<Integer, Pair>();
        clock = new Indexer();
    }

    public void add(String key, String value) {
        Pair p = new Pair(clock.next(), key, value);
        data.put(p.getIndex(), p);
    }

    public String[] getParameters() {
       return attributes;
    }

    public void printAttributeData() {
        print(getAttributeData());
    }

    public String getAttributeData() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("STREAM ID " + id + Delimiters.TAB);
        for (int index : data.keySet()) {
            buffer.append(data.get(index).getValue() + Delimiters.TAB);
        }
        return buffer.toString();
    }

    private static void print(String msg) {
        System.out.println(msg);
    }

    public String getColumnData() {
        StringBuffer buffer = new StringBuffer();
        for (int index : data.keySet()) {
            buffer.append(Delimiters.TAB + data.get(index).getKey() + Delimiters.TAB + Delimiters.TAB + data.get(index).getValue() + Delimiters.NewLine);
        }
        return buffer.toString();
    }
    
}

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
public class Pair {
    
    int index;
    String key;
    String value;

    public Pair(int id, String k, String v) {
        index = id;
        key = k;
        value = v;
    }

    public int getIndex() {
        return index;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
    
}

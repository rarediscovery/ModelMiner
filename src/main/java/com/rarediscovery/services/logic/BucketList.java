/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rarediscovery.services.logic;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author usaa_developer
 */
public class BucketList {
    int size;
    Map<Integer, String> content;

    public BucketList() {
        size = 0;
        content = new HashMap<>();
    }

    public int add(String payload) {
        content.put(size++, payload);
        return size;
    }

    public int getSize() {
        return size;
    }

    public Collection<String> get() {
        return content.values();
    }
    
}

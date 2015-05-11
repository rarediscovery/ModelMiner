/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rarediscovery.services.ui;

import com.rarediscovery.services.logic.Filter;
import com.rarediscovery.services.model.DataPage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.text.JTextComponent;

/**
 *
 * @author usaa_developer
 */
public class PageIO {
    
    Map<String, JTextComponent> inputSource;
    Map<String, JTextComponent> outputTarget;
    
    Map<String, Object> data;

    public PageIO() {
        
        inputSource = new HashMap<>();
        outputTarget = new HashMap<>();
        
        data = new HashMap<>();
    }

    public void addData(String key, Object source) {
        this.data.put(key, source);
    }

    public Object getData(String key) {
        return this.data.get(key);
    }

    public void registerInput(String key, JTextComponent source) {
        this.inputSource.put(key, source);
    }

    public void registerOutput(String key, JTextComponent source) {
        this.outputTarget.put(key, source);
    }

    public void changeValue(String key, String value) {
        if (this.inputSource.get(key) == null) {
            return;
        }
        this.inputSource.get(key).setText(value);
    }

    /**
     * Get Data from any input source that is registered with pageIO <br>
     *
     * @param key
     * @return
     */
    public String getValue(String key) {
        if (this.inputSource.get(key) == null) {
            return "";
        }
        return this.inputSource.get(key).getText();
    }

    public int getIntegerValue(String key) {
        
        Object o = getData(key);
         
        if (o instanceof Integer) {
            return (int) o ;
        }
        
        if (o instanceof String) {
            return Integer.valueOf((String)o);
        }
        
        return Integer.MAX_VALUE;
       
    }

    public String getStringValue(String key) {
        Object o = getData(key);
        if (o instanceof String) {
            return (String) o;
        }
        return null;
    }

    public void show(String key, String msg) {
        this.outputTarget.get(key).setText(msg);
    }

    public int displayPage(int index, Filter filter) 
    {
        List<DataPage> dataPages = (List<DataPage>) getData("list.of.data.page");
        int pageIndex = Math.abs(index % dataPages.size());
        show("content.info", dataPages.get(pageIndex).get(filter));
        
        return pageIndex;
    }
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rarediscovery.services.filters;

import com.rarediscovery.data.Model;
import com.rarediscovery.services.logic.Functions;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Tmeitope Ajagbe
 */
class DataParser implements TextParser {
    
    String data;
    String[] header;

    public String getData() {
        return data;
    }

    public String[] getHeader() {
        return header;
    }

    @Override
    public void setData(String data) {
        this.data = data;
    }

    @Override
    public void setHeader(String[] header) {
        this.header = header;
    }

    @Override
    public List<Model> apply() {
        return discoverModelsFromAttributes(getHeader(), getData());
    }

    private List<Model> discoverModelsFromAttributes(String[] attributes, String data) {
        List<Model> models = new ArrayList<>();
        if (attributes == null || attributes.length == 0) {
            return models;
        }
        String[] dataArray = data.split(" ");
        int q = 0;
        //DEBUG
        Functions.log("*********  convertToModel()    *************");
        Functions.log(data);
        for (int j = 0; j < dataArray.length; j += attributes.length) {
            Model m = new Model("" + Math.random());
            for (int i = 0; i < attributes.length; i++) {
                m.add(attributes[i], dataArray[j + i]);
            }
            models.add(m);
            q++;
        }
        return models;
    }
    
}

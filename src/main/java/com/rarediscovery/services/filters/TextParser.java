/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rarediscovery.services.filters;

import com.rarediscovery.data.Model;
import java.util.List;

/**
 *
 * @author Temitope Ajagbe
 */
public interface TextParser {

    List<Model> apply();

    void setData(String data);

    void setHeader(String[] header);
    
    void setSkippedData(String data);
    
}

/*
 *
 */
package com.rarediscovery.app.client;

import com.rarediscovery.ui.GenericWindow;
import com.rarediscovery.ui.TemplateView;
import com.rarediscovery.widgets.ModelManager;
import com.rarediscovery.widgets.WorksheetPanel;

/**
 *
 * @author Temitope Ajagbe
 */
public class Simulator extends GenericWindow {
 
    
    public static void main(String[] args) 
    {
        String[] sampleList = {"Stream 1" ,"Stream 5",
                                "Stream 2" ,"Stream 7",
                                "Stream F1" ,"Stream 9",
                                "Stream G1" ,"Stream 8",
                                "Stream X1" ,"Stream 4"};
        
        Simulator app = new Simulator();
        app.setTitle("Plant Model Analyzer - 1.0 ");
        WorksheetPanel display = new WorksheetPanel(null);
        app.showContentAs(  
                new TemplateView()
                        // create a left bar
                        .addLeftSideBar(
                                new ModelManager()
                                        //.useComponents(Arrays.asList(sampleList))
                                        //.useStreams(Arrays.asList(sampleList))
                                        .useDisplayPanel(display)
                                        )
                        .addContent(display));
    }
}
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rarediscovery.services.ui;

/**
 *
 * @author usaa_developer
 */
public class SimulatorAppWindow extends GenericWindow {
    
    int count;   
    
    public static void main(String[] args) {
        SimulatorAppWindow app = new SimulatorAppWindow();
        app.setTitle("Plant Model Analyzer - 1.0 ");
    }

    public SimulatorAppWindow() {
        super();
        buildUserInterface();
    }
    
   
   protected void buildUserInterface() 
   {
       getWindow().getContentPane().add(new ViewerWithMenu());
       refresh();
    }
  
}
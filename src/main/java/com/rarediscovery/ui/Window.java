/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rarediscovery.ui;

import com.rarediscovery.services.ui.PageIO;
import java.awt.BorderLayout;
import java.awt.LayoutManager;
import javax.swing.JInternalFrame;

/**
 *
 * @author usaa_developer
 */
public class Window {
    
    final JInternalFrame window;
    String title;
    int id;
    PageIO pageIO;
    
    LayoutManager lm;

    public Window() {
        lm = new BorderLayout();
        this.window = new JInternalFrame();
        this.window.setLayout(lm);
        //this.window.setDefaultCloseOperation(JInternalFrame.EXIT_ON_CLOSE);
        //this.window.setOpaque(true);
        this.window.setSize(400, 300);
        this.window.setResizable(true);
        this.window.setClosable(true);
    } //this.window.setDefaultCloseOperation(JInternalFrame.EXIT_ON_CLOSE);
    //this.window.setOpaque(true);

    public JInternalFrame get() {
        return window;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Window setTitle(String title) {
        this.title = title;
        return this;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public PageIO getPageIO() {
      
        return pageIO;
    }
    
}
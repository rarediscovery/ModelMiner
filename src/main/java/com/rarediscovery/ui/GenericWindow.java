/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rarediscovery.ui;

import com.rarediscovery.services.ui.PageIO;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author Temitope Ajagbe
 */
public class GenericWindow 
{
    final static Dimension MINI = new Dimension(1200, 600);
    protected JFrame applicationWindow;
    final PageIO pageIO;
    String title ;
    JPanel content;

    public GenericWindow() {
        pageIO = new PageIO();
        applicationWindow = createApplication("");
    }


   protected void buildUserInterface() {}

    public PageIO useIO() {
        return pageIO;
    }

    public JFrame getWindow() {
        return applicationWindow;
    }

    public GenericWindow setTitle(String title) {
        this.title = title;
        this.getWindow().setTitle(title);
        return this;
    }

     private JFrame createApplication(String title)
    {
	JFrame aFrame = new JFrame(title);
        
                
        aFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);		
	aFrame.getContentPane().setLayout(new BorderLayout());
        
        //center on the screen
        Dimension screenSize =  Toolkit.getDefaultToolkit().getScreenSize();
        int newY = (int) (screenSize.getHeight() - MINI.getHeight() / 2);
        int newX = (int) (screenSize.getWidth() - MINI.getWidth() / 2);
        //aFrame.setLocation(new Point(newX,newY));
        
        aFrame.setPreferredSize(MINI);
        aFrame.setVisible(true);
        //aFrame.setLocationRelativeTo(null);
        aFrame.pack();
       
        return aFrame;
   }
     
    public void showContentAs(JPanel jp)
    {
        content = jp;
        getWindow().getContentPane().add(content);
        refresh();
    }
    
    public GenericWindow refresh()
    {
       if (content != null)
       {
         content.repaint();
       } 
       this.getWindow().pack();
       this.getWindow().repaint();
       return this;
    }
}

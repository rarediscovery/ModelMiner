/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rarediscovery.services.ui;


import com.rarediscovery.services.logic.Behavior;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.LayoutManager;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

/**
 *
 * @author usaa_developer
 */
public class Group {
    
    JPanel canvas;
    LayoutManager lm;
    PageIO io;

    
    public enum IO{
        Input,
        Output,
        InputOutput,
        ReadOnly;
    }
    
    
    public static class ID
    {
        String value;
        IO usage;
        
        final static ID NONE = new ID("-", IO.ReadOnly);
        
        public ID(String v,IO io) {
            this.value = v;
            this.usage = io;
        }
        
       public static ID of(String key,IO io){
         return new ID(key,io);
       }
       
       public static ID None(){
         return NONE;
       }
    }
    
    public Group() 
    {
           FlowLayout flow = new FlowLayout(FlowLayout.LEFT);
           flow.setVgap(1);
           lm = flow;
           canvas = new JPanel(lm);
           canvas.setBackground(Color.WHITE);
           //canvas.setMinimumSize(new Dimension(1,1));
    }
    
    public Group(final PageIO pageio)
    {
       this();
       io = pageio;
    }

    private PageIO io() {
        return io;
    }
    
    
    public Group centralize(){
        lm = new FlowLayout(FlowLayout.CENTER);
        
        canvas.setLayout(lm);
        canvas.setAlignmentX(Component.CENTER_ALIGNMENT);
        return this;
    }
    
     public Group vertical()
    {
       
        lm = new BoxLayout(canvas, BoxLayout.Y_AXIS);
        canvas.setLayout(lm); 
        
        //lm = new GridLayout(0, 1);
        //canvas.setLayout(lm);
       return this;
    }
     
    public Group alignTop(){
        canvas.setAlignmentY(Component.TOP_ALIGNMENT);
        return this;
    } 
     
    public Group title(String title){
        canvas.setBorder(BorderFactory.createTitledBorder(title));
        return this;
    }
    
    public Group size(int w,int h)
    {
        canvas.setPreferredSize(new Dimension(w, h));
        return this;
    }
    
    
   
    
    public Group add(JComponent jcomp)
    {
       canvas.add(jcomp);
       return this;
    }

    public Group addLabel(ID id,String value){
        JLabel t = new JLabel(value);
        canvas.add(t) ;
       // io().registerInput(id.value, t);
        return this;
    }
    
    public Group addTextInput(ID id,String value){
        JTextField t = new JTextField(value);
        canvas.add(t) ;
        
        mapToIO(id, t);
        
        return this;
    }

   
    
    public Group addButton(String value , Behavior b){
        canvas.add(new JButton(value)) ;return this;
    }
    
    public Group addTextDocument(ID id,String value , Behavior b){
        JTextArea t = new JTextArea(value);
        JScrollPane scrollPane = new JScrollPane(t);
       
        mapToIO(id, t);

        canvas.add(scrollPane) ;return this;
    }
    
    public Group add(Group grp)
    {
       canvas.add(grp.getCanvas());
       return this;
    }
    public JPanel getCanvas() {
        return canvas;
    }
    
    public Group color(Color c){
        canvas.setBackground(c);
      return this;
    }
    
     protected void mapToIO(ID id, JTextComponent t) {
        if (id.usage == IO.Input )
        {
            io().registerInput(id.value, t);
        }else if (id.usage == IO.Output || id.usage == IO.ReadOnly)
        {
            io().registerOutput(id.value, t);
        }else if (id.usage == IO.InputOutput)
        {
            io().registerInput(id.value, t);
            io().registerOutput(id.value, t);
        }
    }
     
    public static String repeat(String value , int times)
    {
        StringBuffer buffer = new StringBuffer();
        
        for (int k=0;k< times;k++){
           buffer.append(value);
        }
        return buffer.toString();
    }
}

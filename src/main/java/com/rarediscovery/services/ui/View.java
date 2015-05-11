/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rarediscovery.services.ui;

import java.awt.FlowLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 *
 * @author usaa_developer
 */
class View 
{
    String name;
    JPanel panel;
    LayoutManager layoutManager;
    Map<String, JComboBox<String>> selections;
    Map<String, JTextField> inputs;
    Map<String, JTextArea> documents;

    public View() 
    {
        layoutManager = new FlowLayout();
        panel = new JPanel();
        
        selections = new HashMap<>();
        inputs = new HashMap<>();
        documents = new HashMap<>();
    }

    public View(String name) {
        this();
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public View addInput(String labelName) {
        JLabel label = new JLabel(labelName);
        JTextField textField = new JTextField(50);
        panel.add(label);
        panel.add(textField);
        
        inputs.put(labelName, textField);
        return this;
    }

    public View addButton(String labelName, final Runnable runnable) {
        JButton button = new JButton(labelName);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if (runnable != null) {
                    runnable.run();
                }
            }
        });
        panel.add(button);
        return this;
    }

    public View addDocument(String labelName, final Runnable runnable) {
        JLabel label = new JLabel(labelName);
        
        JTextArea  textArea = new JTextArea(60,100);
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setAutoscrolls(true);        
        
        panel.add(label);
        panel.add(scrollPane);
        
        documents.put(labelName, textArea);
        return this;
    }
    public View addSelection(String labelName, List<String> itemValues) {
        
        JLabel label = new JLabel(labelName);
        panel.add(label);
        JComboBox<String> itemsContainer = new JComboBox<>();
        DefaultComboBoxModel<String> defaultModel = 
                new DefaultComboBoxModel<String>(itemValues.toArray(new String[0]));
        itemsContainer.setModel(defaultModel);
        panel.add(itemsContainer);
        
        selections.put(labelName, itemsContainer);
        return this;
    }

    public JPanel getPanel() {
        return panel;
    }
    
}

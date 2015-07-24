/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rarediscovery.widgets;

import com.rarediscovery.services.logic.Functions;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

/**
 *
 * @author Temitope Ajagbe
 */
class ListHandler extends KeyAdapter {
    
    JComboBox<String> ui_ListDelegate;
    List<String> dataList;
    DefaultComboBoxModel<String> model;
    DefaultComboBoxModel<String> resultModel;
    StringBuffer searchWord;

    public ListHandler(JComboBox<String> list) {
        
        
        this.ui_ListDelegate = list;
        this.ui_ListDelegate.setEditable(true);
        this.ui_ListDelegate.getEditor().getEditorComponent().addKeyListener(this);
        
        //
        dataList = new ArrayList<>();
        int totalItemsCount = ui_ListDelegate.getModel().getSize();
        
        model = new DefaultComboBoxModel<>();
        resultModel = new DefaultComboBoxModel<>();
        
        for (int i = 0; i < totalItemsCount; i++) {
            dataList.add(ui_ListDelegate.getItemAt(i));
            model.addElement(ui_ListDelegate.getItemAt(i));
        }
        
        buildCompleteList();
        Functions.log(" Data List size : " + dataList.size());
        searchWord = new StringBuffer();
    } //

        public void clearQuery() {
        this.searchWord.setLength(0);
    }

    public String getSelectedItem() {
        return (ui_ListDelegate.getSelectedItem() != null) ? (String) ui_ListDelegate.getSelectedItem() : (String) resultModel.getSelectedItem();
    }

    public String removeItem(String value)
    {
        if (model.getIndexOf(value) > -1)
        {
           model.removeElement(value);
        }
        
        return value;
    }
    
    
    public int addItem(String value)
    {
        if (model.getIndexOf(value) < 0){
            model.addElement(value);
        }
        
        return model.getSize();
    }
    
    public String getCurrentText()
    {
       return searchWord.toString();
    }
    
    
    @Override
    public void keyReleased(KeyEvent ke) 
    {
        switch (ke.getKeyCode()) 
        {
            case KeyEvent.VK_ENTER:
                searchWord.setLength(0);
                break;
                
            case KeyEvent.VK_BACK_SPACE:
                String currentContent = searchWord.toString().trim().toLowerCase();
                int length = currentContent.length();
                if (length > 0) {
                    currentContent = currentContent.substring(0, length - 1);
                    searchWord.setLength(0);
                    searchWord.append(currentContent);
                }
                break;
                
            case KeyEvent.VK_SHIFT:
                break;
            case KeyEvent.VK_DOWN:
                break;
            default:
                searchWord.append(ke.getKeyChar());
        }
        Functions.log(searchWord.toString());
        updateUI();
    }

    protected void updateUI() 
    {
        if (!searchWord.toString().isEmpty()) 
        {
            resultModel.removeAllElements();
            
            String lookingFor = searchWord.toString().trim();
            for (String data : dataList) {
                if (data.startsWith(lookingFor)) {
                    resultModel.addElement(data);
                }
            }
            this.ui_ListDelegate.setModel(resultModel);
        } else {
            buildCompleteList();
        }
        ui_ListDelegate.showPopup();
    }

    protected void buildCompleteList() {
        // Reload list if last search does not include all elements
        resultModel.removeAllElements();
        for (String data : dataList) {
            resultModel.addElement(data);
        }
    }
    
}

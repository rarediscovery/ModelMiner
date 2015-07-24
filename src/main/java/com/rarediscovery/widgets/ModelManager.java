/*
 * FileName   : 
 * Created on : 
 */
package com.rarediscovery.widgets;

import com.rarediscovery.data.Model;
import com.rarediscovery.data.Models;
import com.rarediscovery.services.filters.ResultCriteria;
import com.rarediscovery.services.filters.SearchQuery;
import com.rarediscovery.services.filters.SearchResult;
import com.rarediscovery.services.filters.StringFilter;
import com.rarediscovery.services.logic.Functions;
import static com.rarediscovery.services.logic.Functions.log;
import com.rarediscovery.services.logic.TextReader;
import com.rarediscovery.services.logic.WorkPad;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;

/**
 *
 * @author Temitope Ajagbe
 */
public class ModelManager extends javax.swing.JPanel {

    protected List<String>  allComponentsList, 
                            selectedComponentsList , 
                            groupList;
    
    private DefaultListModel<String> model_AllComponentsList , model_selectedComponentsList, model_selectedStream;
    private DefaultComboBoxModel<String> model_groupList , model_allStreamsList, model_sessionList , model_queryList;
    private Map<String,List<String>> groupToStreamsMap;
    
    Models streamDataModels, otherDataModels;
    
    Properties properties;
    WorksheetPanel displayPanel;
    String datasource;
    
    Dimension WidgetSize = new Dimension(320, 550);
    ListHandler handler;
   
    
    /**
     * Creates new form ModelManager
     */
    public ModelManager() {
        
        initComponents();
        
        this.setPreferredSize(WidgetSize);
        
        // 
        groupToStreamsMap = new HashMap<>();
        
        // Initialize lists
        model_AllComponentsList = new DefaultListModel<>();
        model_selectedComponentsList = new DefaultListModel<>();
        model_selectedStream = new DefaultListModel<>();
    
        
        // Initialize group and streams
        model_groupList = new DefaultComboBoxModel<>();
        model_allStreamsList = new DefaultComboBoxModel<>();
        model_sessionList = new DefaultComboBoxModel<>();
        model_queryList = new DefaultComboBoxModel<>();
        
        // Wire models to UI
        ui_AllComponentsList.setModel(model_AllComponentsList);
        ui_selectedComponentsList.setModel(model_selectedComponentsList);
        ui_groupList.setModel(model_groupList);
        ui_allStreamsList.setModel(model_allStreamsList);
        
        ui_selectedStreams.setModel(model_selectedStream);
        ui_QueryList.setModel(model_queryList);
      
        
        properties = readEntriesFromRepository("repo.properties");
        
        String[] groups = properties.getProperty("Groups.Name").split(",");
       
        // ---------------------------------------
        // Debug
        //Functions.forEachItemIn(properties.keySet()).apply(Functions.Print());
        Functions.forEachItemIn(Arrays.asList(groups)).apply(Functions.addToList(model_groupList));
       
        int qs = Integer.parseInt(properties.getProperty("Queries"));
        
        for(int i=0;i< qs;i++)
        {
           model_queryList.addElement(properties.getProperty("Query."+i+".Name"));
        }
       
    }

    public ModelManager useDisplayPanel(WorksheetPanel displayPanel) 
    {
        this.displayPanel = displayPanel;
        return this;
    }
    
    
    /**
     * Accept a ui_ListDelegate of items as the source ui_ListDelegate <br>
     * 
     * @param source
     * @return 
     */
    public ModelManager useComponents(List<String> source)
    {
        if (source == null || source.isEmpty()) { return this; }
        
        // Sort the input ui_ListDelegate
        Collections.sort(source);
        // Clear existing ui_ListDelegate
        model_AllComponentsList.removeAllElements();
        
        for(String item : source)
        {
           model_AllComponentsList.addElement(item);
        }
        
        return this;
    }

    /**
     * Accept a ui_ListDelegate of items as the source ui_ListDelegate <br>
     * 
     * @param source
     * @return 
     */
    public ModelManager useStreams(List<String> source)
    {
        if (source == null || source.isEmpty()) { return this; }
        
        // Sort the input ui_ListDelegate
        Collections.sort(source);
        // Clear existing ui_ListDelegate
        model_allStreamsList.removeAllElements();
        
        for(String item : source)
        {
           model_allStreamsList.addElement(item);
        }
        
        return this;
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        ui_dataLoadingSelection = new javax.swing.ButtonGroup();
        jPanel11 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        row0 = new javax.swing.JPanel();
        loadDataFromFileButton = new javax.swing.JButton();
        ui_StatusLabel = new javax.swing.JLabel();
        row3 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        row4 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        ui_AllComponentsList = new javax.swing.JList();
        jScrollPane2 = new javax.swing.JScrollPane();
        ui_selectedComponentsList = new javax.swing.JList();
        ui_componentEditor = new javax.swing.JPanel();
        jPanel12 = new javax.swing.JPanel();
        moveSelectedToTargetButton = new javax.swing.JButton();
        ui_customAttributeText = new javax.swing.JTextField();
        jPanel14 = new javax.swing.JPanel();
        removeSelectedFromTargetButton = new javax.swing.JButton();
        addCustomAttributeButton = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        ui_allStreamsList = new javax.swing.JComboBox();
        jPanel1 = new javax.swing.JPanel();
        addStreamToGroupButton = new javax.swing.JButton();
        removeStreamFromGroupButton = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        ui_groupList = new javax.swing.JComboBox();
        jPanel8 = new javax.swing.JPanel();
        row1_right = new javax.swing.JScrollPane();
        ui_selectedStreams = new javax.swing.JList();
        row1_left = new javax.swing.JPanel();
        addToGroup = new javax.swing.JButton();
        removeFromGroup = new javax.swing.JButton();
        saveConfigurationButton = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel10 = new javax.swing.JPanel();
        ui_QueryList = new javax.swing.JComboBox();
        jPanel7 = new javax.swing.JPanel();
        exportToExcelButton = new javax.swing.JButton();

        setBorder(javax.swing.BorderFactory.createTitledBorder(null, "", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP, null, new java.awt.Color(255, 153, 0)));
        setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.Y_AXIS));

        jPanel11.setBackground(new java.awt.Color(0, 102, 102));
        jPanel11.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 102, 0));
        jLabel3.setText("Control Panel");
        jPanel11.add(jLabel3);

        add(jPanel11);

        row0.setBackground(new java.awt.Color(204, 204, 204));
        row0.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        row0.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        loadDataFromFileButton.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        loadDataFromFileButton.setForeground(new java.awt.Color(0, 102, 102));
        loadDataFromFileButton.setText("Load Source PDF");
        loadDataFromFileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadDataFromFileButtonActionPerformed(evt);
            }
        });
        row0.add(loadDataFromFileButton);

        ui_StatusLabel.setFont(new java.awt.Font("Tahoma", 1, 10)); // NOI18N
        ui_StatusLabel.setText("No File Selected !");
        row0.add(ui_StatusLabel);

        add(row0);

        row3.setBackground(new java.awt.Color(0, 102, 102));
        row3.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jLabel2.setForeground(new java.awt.Color(0, 204, 204));
        jLabel2.setText("Component Names");
        row3.add(jLabel2);

        add(row3);

        row4.setLayout(new java.awt.GridLayout(1, 0));

        ui_AllComponentsList.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        ui_AllComponentsList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane1.setViewportView(ui_AllComponentsList);

        row4.add(jScrollPane1);

        ui_selectedComponentsList.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        ui_selectedComponentsList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane2.setViewportView(ui_selectedComponentsList);

        row4.add(jScrollPane2);

        add(row4);

        ui_componentEditor.setLayout(new java.awt.GridLayout(1, 2));

        jPanel12.setLayout(new java.awt.GridLayout(2, 0));

        moveSelectedToTargetButton.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        moveSelectedToTargetButton.setText(">>");
        moveSelectedToTargetButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                moveSelectedToTargetButtonActionPerformed(evt);
            }
        });
        jPanel12.add(moveSelectedToTargetButton);

        ui_customAttributeText.setColumns(12);
        ui_customAttributeText.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jPanel12.add(ui_customAttributeText);

        ui_componentEditor.add(jPanel12);

        jPanel14.setLayout(new java.awt.GridLayout(2, 0));

        removeSelectedFromTargetButton.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        removeSelectedFromTargetButton.setText("<<");
        removeSelectedFromTargetButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeSelectedFromTargetButtonActionPerformed(evt);
            }
        });
        jPanel14.add(removeSelectedFromTargetButton);

        addCustomAttributeButton.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        addCustomAttributeButton.setText("Add Custom Attribute");
        addCustomAttributeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addCustomAttributeButtonActionPerformed(evt);
            }
        });
        jPanel14.add(addCustomAttributeButton);

        ui_componentEditor.add(jPanel14);

        add(ui_componentEditor);

        jPanel2.setBackground(new java.awt.Color(0, 102, 102));
        jPanel2.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jLabel4.setForeground(new java.awt.Color(0, 204, 204));
        jLabel4.setText("Available Streams");
        jPanel2.add(jLabel4);

        add(jPanel2);

        jPanel6.setLayout(new java.awt.GridLayout());

        ui_allStreamsList.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        ui_allStreamsList.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        ui_allStreamsList.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                ui_allStreamsListItemStateChanged(evt);
            }
        });
        ui_allStreamsList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ui_allStreamsListActionPerformed(evt);
            }
        });
        ui_allStreamsList.addInputMethodListener(new java.awt.event.InputMethodListener() {
            public void caretPositionChanged(java.awt.event.InputMethodEvent evt) {
            }
            public void inputMethodTextChanged(java.awt.event.InputMethodEvent evt) {
                ui_allStreamsListInputMethodTextChanged(evt);
            }
        });
        ui_allStreamsList.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                ui_allStreamsListKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                ui_allStreamsListKeyTyped(evt);
            }
        });
        jPanel6.add(ui_allStreamsList);

        add(jPanel6);

        jPanel1.setForeground(new java.awt.Color(0, 153, 153));
        jPanel1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        addStreamToGroupButton.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        addStreamToGroupButton.setText("Add Stream");
        addStreamToGroupButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addStreamToGroupButtonActionPerformed(evt);
            }
        });
        jPanel1.add(addStreamToGroupButton);

        removeStreamFromGroupButton.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        removeStreamFromGroupButton.setText("Remove Stream");
        removeStreamFromGroupButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeStreamFromGroupButtonActionPerformed(evt);
            }
        });
        jPanel1.add(removeStreamFromGroupButton);

        add(jPanel1);

        jPanel3.setBackground(new java.awt.Color(0, 102, 102));
        jPanel3.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jLabel5.setForeground(new java.awt.Color(0, 204, 204));
        jLabel5.setText("Report Tab");
        jPanel3.add(jLabel5);

        add(jPanel3);

        jPanel9.setLayout(new java.awt.GridLayout());

        ui_groupList.setEditable(true);
        ui_groupList.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        ui_groupList.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        ui_groupList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ui_groupListActionPerformed(evt);
            }
        });
        ui_groupList.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                ui_groupListKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                ui_groupListKeyReleased(evt);
            }
        });
        jPanel9.add(ui_groupList);

        add(jPanel9);

        jPanel8.setLayout(new java.awt.GridLayout(1, 2));

        ui_selectedStreams.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        ui_selectedStreams.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        row1_right.setViewportView(ui_selectedStreams);

        jPanel8.add(row1_right);

        row1_left.setLayout(new java.awt.GridLayout(3, 0));

        addToGroup.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        addToGroup.setText("Add Tab");
        addToGroup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addToGroupActionPerformed(evt);
            }
        });
        row1_left.add(addToGroup);

        removeFromGroup.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        removeFromGroup.setText("Remove Tab");
        removeFromGroup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeFromGroupActionPerformed(evt);
            }
        });
        row1_left.add(removeFromGroup);

        saveConfigurationButton.setFont(new java.awt.Font("Tahoma", 1, 10)); // NOI18N
        saveConfigurationButton.setText("Save");
        saveConfigurationButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveConfigurationButtonActionPerformed(evt);
            }
        });
        row1_left.add(saveConfigurationButton);

        jPanel8.add(row1_left);

        add(jPanel8);

        jPanel5.setBackground(new java.awt.Color(0, 102, 102));
        jPanel5.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jLabel1.setForeground(new java.awt.Color(0, 204, 204));
        jLabel1.setText("More Relevant Data");
        jPanel5.add(jLabel1);

        add(jPanel5);

        jPanel10.setLayout(new java.awt.GridLayout());

        ui_QueryList.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        ui_QueryList.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        ui_QueryList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ui_QueryListActionPerformed(evt);
            }
        });
        jPanel10.add(ui_QueryList);

        add(jPanel10);

        jPanel7.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        exportToExcelButton.setFont(new java.awt.Font("Tahoma", 1, 10)); // NOI18N
        exportToExcelButton.setForeground(new java.awt.Color(0, 102, 102));
        exportToExcelButton.setText("Export To Excel");
        exportToExcelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportToExcelButtonActionPerformed(evt);
            }
        });
        jPanel7.add(exportToExcelButton);

        add(jPanel7);
    }// </editor-fold>//GEN-END:initComponents

    private void moveSelectedToTargetButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_moveSelectedToTargetButtonActionPerformed
        
        
        
        // Remove from source and add to Target
        List<String> selectedItems = ui_AllComponentsList.getSelectedValuesList();
        for(String item : selectedItems)
        {
           model_AllComponentsList.removeElement(item);
           model_selectedComponentsList.addElement(item);
        }
    }//GEN-LAST:event_moveSelectedToTargetButtonActionPerformed

    private void addToGroupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addToGroupActionPerformed
        
        addEntryToGroup();
        
    }//GEN-LAST:event_addToGroupActionPerformed

    protected void addEntryToGroup() {
        // Add unique entry
        String groupName = (String) ui_groupList.getEditor().getItem();
        if (groupName.length() > 0)
        {
            if (model_groupList.getIndexOf(groupName) < 0)
            {
                model_groupList.addElement(groupName);
                
                //Clear entry box
                //ui_groupNewEntry.setText("");
            }
        }
    }

    private void removeFromGroupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeFromGroupActionPerformed
       
        //
        if (model_groupList.getSize() > 0 )
        {
          String selectedValue = (String) model_groupList.getSelectedItem();
          model_groupList.removeElement(selectedValue);
          
          // Push the removed item back to entry box
          //ui_groupNewEntry.setText(selectedValue);
        }
    }//GEN-LAST:event_removeFromGroupActionPerformed

    private void removeSelectedFromTargetButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeSelectedFromTargetButtonActionPerformed
        
        
        
        // Remove from source and add to Target
        List<String> selectedItems = ui_selectedComponentsList.getSelectedValuesList();
        for(String item : selectedItems)
        {
           model_selectedComponentsList.removeElement(item);
           model_AllComponentsList.addElement(item);
        }
    }//GEN-LAST:event_removeSelectedFromTargetButtonActionPerformed

    private void addStreamToGroupButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addStreamToGroupButtonActionPerformed

        
        String s =handler.getSelectedItem();
        
        if (! model_selectedStream.contains(s))
        {
            model_selectedStream.addElement(s);
            
            showDataOnPreviewGrid();
        }
        
        
        handler.clearQuery();
    }//GEN-LAST:event_addStreamToGroupButtonActionPerformed

    private void ui_groupListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ui_groupListActionPerformed
       
        refreshGroupToStreamMapping();
        
        showDataOnPreviewGrid();
    }//GEN-LAST:event_ui_groupListActionPerformed

    private void saveConfigurationButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveConfigurationButtonActionPerformed
        
        String groupName = (String) model_groupList.getSelectedItem();
        
        
        if ( groupName != null)
        {
           if (groupToStreamsMap.get(groupName) == null)
           {
              groupToStreamsMap.put(groupName, new ArrayList<String>());
           }
           
           
           // ********************************************************
           // Add item to the list without duplication
           // ********************************************************
           
            Enumeration<String> elements = model_selectedStream.elements();
            while (elements.hasMoreElements())
            {
                String item = elements.nextElement();
                if (groupToStreamsMap.get(groupName).indexOf(item) < 0)
                {
                  groupToStreamsMap.get(groupName).add(item);
                }
            }
            
            // ********************************************************
            // Display status information
            // ********************************************************
            
            ui_StatusLabel.setText(groupName + " saved !");
        }
        
    }//GEN-LAST:event_saveConfigurationButtonActionPerformed

    protected void saveRepo() throws HeadlessException {
        String DB= "repo.properties";
        
        String configName = JOptionPane.showInputDialog(
                this,
                "Enter desired name for this Configuration ? \n ");
        //DEBUG
        log("***** "+ configName);
        if (configName == null || configName.trim().length() == 0)
        {
            configName = "Session -"+ new Date().toGMTString();
        }
        
        try
        {
            
            FileWriter writer = new FileWriter(DB,true);
            BufferedWriter bufferedWriter = new BufferedWriter(writer);
            
            bufferedWriter.append(configName).append(" = ");
            
            for(String group : groupToStreamsMap.keySet())
            {
                String s = buildJSON(group, groupToStreamsMap.get(group));
                bufferedWriter.append(s).append(" | ");
            }
            
            bufferedWriter.newLine();
            bufferedWriter.flush();
            bufferedWriter.close();
            
        } catch (FileNotFoundException ex)
        {
            //
        } catch (IOException ex)
        {
            //
        }
    }

    private void loadDataFromFileButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadDataFromFileButtonActionPerformed
        
       
           String suggestedFile = Functions.getUserSelectedFile("pdf | PDF ");
           if (! suggestedFile.isEmpty() && suggestedFile.endsWith(".pdf"))
           {
               datasource =  suggestedFile; 
               
               buildStreamDataModels(datasource);
               
               ui_StatusLabel.setText("PDF File Selected and Loaded ");
               ui_StatusLabel.setForeground(Color.yellow);
           }
    }//GEN-LAST:event_loadDataFromFileButtonActionPerformed

    private void addCustomAttributeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addCustomAttributeButtonActionPerformed
        //if (ui_customAttributeText.getText().trim().length()> 0)
        //{
             model_selectedComponentsList.addElement(ui_customAttributeText.getText());
        //}
    }//GEN-LAST:event_addCustomAttributeButtonActionPerformed

    private void ui_groupListKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_ui_groupListKeyReleased
      
       
    }//GEN-LAST:event_ui_groupListKeyReleased

    private void ui_groupListKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_ui_groupListKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_ui_groupListKeyPressed

    private void ui_allStreamsListKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_ui_allStreamsListKeyPressed
        String search = (String) ((JComboBox)evt.getSource()).getEditor().getItem();
        log(search);
    }//GEN-LAST:event_ui_allStreamsListKeyPressed

    private void ui_allStreamsListKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_ui_allStreamsListKeyTyped
      
        String search = (String) ((JComboBox<String>)evt.getSource()).getSelectedItem();
        log(search);       
       
    }//GEN-LAST:event_ui_allStreamsListKeyTyped

    private void ui_allStreamsListInputMethodTextChanged(java.awt.event.InputMethodEvent evt) {//GEN-FIRST:event_ui_allStreamsListInputMethodTextChanged
      
    }//GEN-LAST:event_ui_allStreamsListInputMethodTextChanged

    private void ui_allStreamsListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ui_allStreamsListActionPerformed
       
        
    }//GEN-LAST:event_ui_allStreamsListActionPerformed

    private void ui_allStreamsListItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_ui_allStreamsListItemStateChanged
        
    }//GEN-LAST:event_ui_allStreamsListItemStateChanged

    private void removeStreamFromGroupButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeStreamFromGroupButtonActionPerformed
        
        int indexToRemove = ui_selectedStreams.getSelectedIndex();
        // + 
        log("Remove item "+ indexToRemove);
        if (indexToRemove >-1)
        {
           model_selectedStream.removeElementAt(indexToRemove);
           
           showDataOnPreviewGrid();
        }
    }//GEN-LAST:event_removeStreamFromGroupButtonActionPerformed

    private void exportToExcelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportToExcelButtonActionPerformed
        
        if (model_groupList.getSize() < 1){ return ;}
        
        String selectedFile = Functions.getUserSelectedFile(".XLS");
        
        if (selectedFile == null  || selectedFile.length() < 1) 
        {
            displayStatus("No Valid file was selected !! ");
            return ;
        }
        
        List<String> listOfTabs = new ArrayList<>();
        List<String> listOfSelectedStreams = new ArrayList<>();
        List<String> listOfSelectedComponents = new ArrayList<>();
        
        // Transform all model object to list
        
        Functions.forEachItemIn(model_groupList).apply(Functions.addToList(listOfTabs));
        Functions.forEachItemIn(model_selectedComponentsList).apply(Functions.addToList(listOfSelectedComponents));
          
        // Prepare elements to save
        List<String> column1 = new ArrayList<>();
        List<String> column2 = new ArrayList<>();
        List<String> keys = new ArrayList<>();
        
        for(String v: listOfSelectedComponents)
        {
            keys.add(v);
            
            String[] vs = v.split(" ");
            column1.add(vs[0]);
            
            String rest = " ";
            if (vs.length > 1)
            {
               rest = Functions.join(vs, 1, vs.length);
            }
            column2.add(rest);
        }
        
        String[] keyArray = keys.toArray(new String[0]);
        String[] dataArray1 = column1.toArray(new String[0]);
        String[] dataArray2 = column2.toArray(new String[0]);
         
        
        
        WorkPad excelWorkbook = Functions.createOrUpdateWookbook(selectedFile);
      
        for(String groupName : listOfTabs)
        {
           int column =1 , row =1;
            
            // HEADER
            // **********
            
            List<String> headers = new ArrayList<>();
            headers.add("Index");
            headers.add("Component Name");
            headers.add(" ");

              /*   */
            int dataValueOffset = headers.size();
            // Get list of selected Streams
            listOfSelectedStreams = groupToStreamsMap.get(groupName);
            
            headers.addAll(listOfSelectedStreams);
           
            excelWorkbook
                    .bold().alignRight()
                    .addRowData(groupName , headers.toArray(new String[0]) ,row, column);
            
            // COMPONENT NAMES
            // ********************
            
            excelWorkbook.addColumnData(groupName, dataArray1 , row+1,  column  );
            excelWorkbook.addColumnData(groupName, dataArray2 , row+1 , column+1 );
            
            // Add Stream Data
            // *****************************
            
           
            
            int i=0;
            for(String stream : listOfSelectedStreams)
            {
               //Get data
               Model model = streamDataModels.get(stream);
               
               String[] values = model.getAttributesValues(keyArray);
               excelWorkbook
                       .alignRight()
                       .addColumnData(groupName, values ,  row+1 ,column+dataValueOffset+i );
               i++;  /* Move to next stream */
            }
        }
        
        Functions.save(excelWorkbook);
        
        displayStatus(" Data has been saved to "+ selectedFile);
    }//GEN-LAST:event_exportToExcelButtonActionPerformed

    protected void displayStatus(String msg) {
        ui_StatusLabel.setText( msg);
    }

    private void ui_QueryListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ui_QueryListActionPerformed
       
        if (datasource == null) { return ;}
        
        String selection = (String) model_queryList.getSelectedItem();
        log(selection);
        
        // Build Specific query for the selected function
        int index = model_queryList.getIndexOf(selection);
        SearchQuery query = buildQueryFromSelection(index);       
        SearchResult result = runQuery(datasource, query);
        Models resultSet = null; 
        
        // Which file should get this update
        // -------------------------------------------------------------
        String selectedFile = Functions.getUserSelectedFile(".xls");
        if (selectedFile == null  || selectedFile.length() < 1) { return ;}
        
        //Functions.logList(Arrays.asList(resultSet.getModelAttributeNames()));
        
        if (selection.startsWith("Shift Converter"))
        {
           resultSet = result.buildCustomModels(Functions.ShiftConverterParser);
           ShiftConverterParser(selectedFile, selection, resultSet);
        }
        else
        {
            resultSet = result.buildCustomModels(Functions.OTConverterParser);
           OTConverterParser(selectedFile, selection, resultSet);
        }
    }//GEN-LAST:event_ui_QueryListActionPerformed

    protected void showDataOnPreviewGrid() 
    {
        if (displayPanel == null) { return ;}
        
        int MAX_ROWS = 51,  MAX_COLUMNS = 21;
        
        String[][] grid = new String[MAX_ROWS][MAX_COLUMNS];
        
        /* Number of Worksheets */
        List<String> listOfTabs = new ArrayList<>();
        
        List<String> listOfSelectedStreams = new ArrayList<>();
        List<String> listOfAttributes = new ArrayList<>();
        
        // Transform all model object to list
        
        Functions.forEachItemIn(model_groupList).apply(Functions.addToList(listOfTabs));
        Functions.forEachItemIn(model_selectedStream).apply(Functions.addToList(listOfSelectedStreams));
        Functions.forEachItemIn(model_selectedComponentsList).apply(Functions.addToList(listOfAttributes));
        
        String currentTab = (String) model_groupList.getSelectedItem() ;
        
        // Prepare elements to save
        
        List<String> keys = new ArrayList<>();
        List<String> column1 = new ArrayList<>();
        List<String> column2 = new ArrayList<>();
        
        // Split compound attributes to separate columns
        // *********************************************
        for(String v: listOfAttributes)
        {
            keys.add(v.trim());
            
            String[] vs = v.split(" ");
            column1.add(vs[0]);
            
            String rest = " ";
            if (vs.length > 1)
            {
                rest = Functions.join(vs, 1, vs.length);
            }
            column2.add(rest);
        }
        
        String[] keyArray = keys.toArray(new String[0]);
        String[] componentNameColumn1 = column1.toArray(new String[0]);
        String[] componentNameColumn2 = column2.toArray(new String[0]);
        
        List<String> headers = new ArrayList<>();
        
        headers.add(" Index ");
        headers.add(" Component Name "); 
        headers.add(" ");   /* LEave empty column between attribute and stream data */
        
        /*   */
        int dataValueOffset = headers.size();
        headers.addAll(listOfSelectedStreams);
        
        // ***************************** 
        int gridColumn =1 , gridRow =1;

        // HEADER
        Functions.addRowData(grid , headers.toArray(new String[0]) ,gridRow, gridColumn);

        // COMPONENT NAMES
        Functions.addColumnData(grid, componentNameColumn1 , gridRow+1,  gridColumn  );
        Functions.addColumnData(grid, componentNameColumn2 , gridRow+1 , gridColumn+1 );

        // Add Stream Data

        int i=0;
        for(String stream : listOfSelectedStreams)
        {
            //Get data
            Model model = streamDataModels.get(stream);

            if (model == null) { continue ;}
            
            String[] values = model.getAttributesValues(keyArray);
            Functions.addColumnData(grid, values ,  gridRow+1 , gridColumn+dataValueOffset + i );
            i++;  /* Move to next stream */
        }
                
        displayPanel.loadData(grid);
    }

    public void OTConverterParser(String selectedFile, String selection, Models resultSet)
    {
    
         WorkPad excelWorkbook = Functions.createOrUpdateWookbook(selectedFile);
         
         int dataRowOffset = 1;
         
        // Write Header
        excelWorkbook.addRowData("OT Converter" , new String[]{ selection } ,dataRowOffset, 10);
        String[] attributes = resultSet.getModelAttributeNames();
        
        excelWorkbook.bold()
                .alignRight().bold().addRowData("OT Converter" ,attributes , dataRowOffset+1, 10);
        
        int i=0;
        for(Model m : resultSet.sortModelsByAttribute("STEP", Models.As.Strings))
        {
           String[] values = m.getAttributesValues(attributes);
           excelWorkbook.plain().alignRight().addRowData("OT Converter" ,values , dataRowOffset+2+i, 10);
           i++;
        }
        
        Functions.save(excelWorkbook);        
        displayStatus(" Data Saved !");
        
     return;
    }
    
    protected void ShiftConverterParser(String selectedFile, String selection, Models resultSet) 
    {
        WorkPad excelWorkbook = Functions.createOrUpdateWookbook(selectedFile);
        // Write Header
        excelWorkbook.addRowData("HTS" , new String[]{ selection } ,2, 10);
        excelWorkbook.addRowData("HTS" , resultSet.getModelAttributeNames() ,3, 10);
        excelWorkbook.addRowData("LTS" , new String[]{ selection } ,2, 10);
        excelWorkbook.addRowData("LTS" , resultSet.getModelAttributeNames() ,3, 10);
        
        // Write Records
        String[] attr = resultSet.getModelAttributeNames();
        //int i=0, j=1,k=1;
        // Split HTS from LTS
        int dataRowOffset = 4;
        
        List<Model> masterList = resultSet.sortModelsByAttribute("TEMP,F", Models.As.Numbers);
        float lastValue = 0;
        boolean flip = false;
        int offset = 0;
        for(int i=0;i<masterList.size();i++)
        {
            Model m = masterList.get(i);
            
            if (flip == false)
            {
                if (m.get("FEET") != null)
                {
                   float f = Float.parseFloat(m.get("FEET"));
                   
                   // Skip buggy lines
                   if (f % 2 > 0 ){ continue;}
                   
                   if (lastValue <= f)
                   {
                     excelWorkbook
                             .plain().alignRight()
                             .addRowData("LTS" , m.getAttributesValues(attr) ,i+dataRowOffset, 10);
                     lastValue =f;
                   }else
                   {
                       flip = true;
                       offset= i;
                   }
               }
            }
            
            if (flip)
            {
               excelWorkbook
                       .alignRight().plain()
                       .addRowData("HTS" , m.getAttributesValues(attr) ,i+dataRowOffset-offset, 10);
            }    
              
        }
        
        Functions.save(excelWorkbook);
        displayStatus(" Data saved !!" );
        
    }

    public void buildStreamDataModels(String datasource)
    {
          String queryValue = properties.getProperty("Query.Stream.SearchKey"); 
          log( " Query to Execute : "+ queryValue);
       
          SearchQuery  query = new SearchQuery(queryValue);
          
          ResultCriteria criteria = query.newCriteria();
          criteria.buildFromConfig(properties , "Query.Stream");
          
          SearchResult result = runQuery(datasource, query);
          streamDataModels = result.buildStreamInformationModels();
          
          // ---------------------------------------------------------- //
          // Collect Available streams
          Model firstModel = null ;        
       
          model_allStreamsList.removeAllElements();
          for (String s: streamDataModels.getSortedList())
          {
              model_allStreamsList.addElement(s);
              if (firstModel == null)
              {
                 firstModel = streamDataModels.get(s);
              }
          }
        
          handler = new ListHandler(ui_allStreamsList);
        
          // Build Attributes List
          buildAttributes(firstModel);
    }
    
    public SearchResult runQuery(String file,SearchQuery  query)
    {
        TextReader reader = new TextReader(new File(file));
        String msg  = reader.convertPDFToString();
        SearchResult result = new StringFilter().given(msg).execute(query);
      
        return result;
    }
     
     
    public SearchQuery buildQueryFromSelection(int index)
    {
        
        if (datasource == null) 
        {
            log(" Data has not been loaded from source PDF !");
            return null;
        }
        
       String PREFIX = "Query."+index;
       String key = PREFIX + ".SearchKey";
        
       log(key);
        
       // DEBUG 
       String queryValue = properties.getProperty(key);
       log( " Query to Execute : "+ queryValue);
       
       // Construct the Search Request
       // *****************************
       SearchQuery  query = new SearchQuery(queryValue);
       ResultCriteria criteria = query.newCriteria();
       criteria.buildFromConfig(properties , PREFIX);
             
      return query;
    }

    /**
     * For a given session key
     * @return 
     */
    private Map<String,List<String>> buildModelsFromSessionHistory() 
    {
        Map<String,List<String>> groupToStreamMaping = new HashMap<>();
        
        String sessionKey = (String) model_sessionList.getSelectedItem();
        String data = properties.getProperty(sessionKey);
        
        String[] dataObjects = data.split("\\|");
        for(String d : dataObjects)
        {
          Functions.log(d);
          // Look for item in the curly brackets
          int left = d.indexOf("{");
          
          if (left <= 0)
          {
              continue;
          }
          
          String group = d.substring(0,left);
        
          int right = d.indexOf("}");
          String[] streams = d.substring(left,right).split(",");
          
          //
          groupToStreamMaping.put(group, Arrays.asList(streams));
        }
        
        
        return groupToStreamMaping;
    }
    
    private Properties readEntriesFromRepository(String repositoryName)
    {
        Properties properties = new Properties();
         
        try 
        {
            properties.load(new FileReader(repositoryName));
           
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ModelManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ModelManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    
        return properties;
    }
   
    protected void buildAttributes(Model firstModel) 
    {
        // Clear the ui_ListDelegate
        
        
        // Show Model Attributes
        List<String> sortedAttributes = new ArrayList() ;
        for(String attribute: firstModel.getAttributeNames())
        {
            sortedAttributes.add(attribute);
            // streamAttributesModel.addElement(attribute); 
        }
        
        model_AllComponentsList.addElement("     "); 
        Collections.sort(sortedAttributes);
        
        for(String s: sortedAttributes)
        {
            model_AllComponentsList.addElement(s);
        }
    }

    protected String refreshGroupToStreamMapping() 
    {
        
        // Update group to Stream mapping
        if (model_groupList.getSize() == 0 || ui_groupList.getSelectedItem() == null) 
        {
            return "";
        }
        
        String g = (String) ui_groupList.getSelectedItem();
       
        // Check for memory copy of the mapping selected
        List<String> gList = groupToStreamsMap.get(g);
        if (gList == null ) 
        {
            gList = new ArrayList<String>();
            groupToStreamsMap.put(g, gList);
        }
        
        // **********************************************
        // Remove all entries from selection list
        // ***********************************************
        model_selectedStream.removeAllElements();
        
        Functions.forEachItemIn(gList).apply(Functions.addToList(model_selectedStream));
        
        //Display items as JSON
        String w=buildJSON(g, gList);
        
        log(w);
        
        return w;
    }

    protected String buildJSON( String g, List<String> gList) 
    {
        int i=0;
        StringBuffer buffer = new StringBuffer();
        
        for(String item: gList)
        {
            if (i > 0)
            {
                buffer.append(" , "+item);
            }else
            {
                buffer.append(item);
            }
            
            i++;
        }
        return String.format(" %s { %s }", g, buffer.toString());
       
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addCustomAttributeButton;
    private javax.swing.JButton addStreamToGroupButton;
    private javax.swing.JButton addToGroup;
    private javax.swing.JButton exportToExcelButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JButton loadDataFromFileButton;
    private javax.swing.JButton moveSelectedToTargetButton;
    private javax.swing.JButton removeFromGroup;
    private javax.swing.JButton removeSelectedFromTargetButton;
    private javax.swing.JButton removeStreamFromGroupButton;
    private javax.swing.JPanel row0;
    private javax.swing.JPanel row1_left;
    private javax.swing.JScrollPane row1_right;
    private javax.swing.JPanel row3;
    private javax.swing.JPanel row4;
    private javax.swing.JButton saveConfigurationButton;
    private javax.swing.JList ui_AllComponentsList;
    private javax.swing.JComboBox ui_QueryList;
    private javax.swing.JLabel ui_StatusLabel;
    private javax.swing.JComboBox ui_allStreamsList;
    private javax.swing.JPanel ui_componentEditor;
    private javax.swing.JTextField ui_customAttributeText;
    private javax.swing.ButtonGroup ui_dataLoadingSelection;
    private javax.swing.JComboBox ui_groupList;
    private javax.swing.JList ui_selectedComponentsList;
    private javax.swing.JList ui_selectedStreams;
    // End of variables declaration//GEN-END:variables

    
}

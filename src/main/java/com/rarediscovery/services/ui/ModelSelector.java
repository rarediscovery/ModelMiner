/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rarediscovery.services.ui;

import com.rarediscovery.services.filters.SearchRequest;

import com.rarediscovery.services.filters.SearchResult;
import com.rarediscovery.services.filters.SearchResult.Model;
import com.rarediscovery.services.filters.SearchResult.Models;
import com.rarediscovery.services.filters.StringFilter;
import com.rarediscovery.services.logic.Functions;
import static com.rarediscovery.services.logic.Functions.log;
import com.rarediscovery.services.logic.TextReader;
import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

/**
 *
 * @author usaa_developer
 */
public class ModelSelector extends javax.swing.JPanel {

    DefaultListModel<String> 
            selectedStreamModel , 
            streamAttributesModel,
            reportingAttributesModel;
    
    DefaultComboBoxModel<String>  
            streamModel , subsystemModel;
    
    Models inMemoryModels;
    TableModel reportModel;
     String[] columnNamesPrefix = { "Index" , "Name" };
    
    Map<String, List<String>> subsystemStreamMap;
    
    
    /**
     * Creates new form ModelSelector
     */
    public ModelSelector() 
    {
        initComponents();
        initializeModels();
    }
    
     private void initializeModels() 
     {
        streamModel = new DefaultComboBoxModel<>();
        subsystemModel = new DefaultComboBoxModel<>();
        
        selectedStreamModel = new DefaultListModel<>();
        streamAttributesModel = new DefaultListModel<>();
        reportingAttributesModel = new DefaultListModel<>();
                
        
        streamsComboBox.setModel(streamModel);
        subsystemComboBox.setModel(subsystemModel);
        selectedStreamList.setModel(selectedStreamModel);
        streamAttributesList.setModel(streamAttributesModel);
        reportingAttributesList.setModel(reportingAttributesModel);
         
        subsystemStreamMap = new HashMap<>();
    }

      private void loadModelInMemory() 
      {
        // Indicate we are just about to load data
        visualIndicatorForDataLoadLabel.setText(" Not Loaded ");
        
        // Read PDf and parse as Text
        TextReader reader = new TextReader(new File(inputFileLabel.getText()));
        String msg  = reader.convertPDFToString();
        
        // Construct the Search Request
        SearchRequest  request = new SearchRequest("PROCESS FLOW STREAM RECORD");
        request.readFrom(2)
               .readTo(30) //12
               .setFieldColumn(1)
               .setCategories(4);
        
        SearchResult result = new StringFilter()
                .given(msg)
                .find(request);
        
         // Save the models in memory
        inMemoryModels = result.buildModels();
        
        // Process the result
        Model firstModel = null ;
        for (Model m : inMemoryModels.getAll())
        {
            if (firstModel == null)
            {
              firstModel = m;
            }
            streamModel.addElement(m.getID());
        }
        
        // Show Model Attributes
        for(String attribute: firstModel.getAttributeNames())
        {
           streamAttributesModel.addElement(attribute); 
        }
        
        // Indicate that the data is properly loaded
        visualIndicatorForDataLoadLabel.setText(" Data Loaded ");
        visualIndicatorForDataLoadLabel.setForeground(Color.GREEN);
        
        buildReportViewer();
      }

    protected void buildReportViewer() 
    {
        List<Model> models = inMemoryModels.getModels(listStreamsMappedToSubsystem());
        Object[][] dataGrid = convertModelToDataGrid(models);
        
        // No data loaded , exit
        if (dataGrid.length == 0) { return ;}
        
        reportModel = new DefaultTableModel(dataGrid,  createGridHeader(dataGrid[0].length, models));
        
        reportingTable.setModel(reportModel);
        reportingTable.setAutoCreateRowSorter(true);
    }
      
    private Object[][] convertModelToDataGrid(final List<Model> models) 
    {
        // Total number of models in the selection list
        /*
        Model[] models = new Model[selectedStreamModel.size()];
        // Get all the stream ids
        Enumeration<String> selectedStreamsEnum = selectedStreamModel.elements();
        int j=0;
        while(selectedStreamsEnum.hasMoreElements())
        {
        models[j] = inMemoryModels.get(selectedStreamsEnum.nextElement());
        log(" ..." + models[j].getID());
        j++;
        }
         */
       
        
        if (models.size() == 0)
        {
           return new String[][]{};
        }
              
        //topItem.getAttributeNames().
        int rowCount = reportingAttributesModel.size() +1,
            columnCount = columnNamesPrefix.length+models.size();
              
        String[][] dataGrid = new String [rowCount][columnCount];
         
        int i=0;
        
        Enumeration<String> ram = reportingAttributesModel.elements();
        while(ram.hasMoreElements())
        {
           String[] row = new String[dataGrid[0].length];
           row[0] = ""+i ;
           row[1] = ram.nextElement();
        
           for(int k=0;k<models.size();k++)
           {
              row[2+k] = models.get(k).get(row[1]);
           }
           dataGrid[i] = row;
           i++;
        }
        
        log(" Convert to Grid");
        
        return dataGrid;
    }

    protected String[] createGridHeader(int columnCount, List<Model> models) 
    {
        // Create grid header
        String[] currentColumnNames = new String[columnCount];
        for(int j=0;j< columnNamesPrefix.length;j++)
        {
            currentColumnNames[j] = columnNamesPrefix[j];
        }
        
        for(int j=0;j<models.size();j++)
        {
            currentColumnNames[columnNamesPrefix.length+j] = models.get(j).getID();
        }
        
        return currentColumnNames;
    }
    
    /**
     * Get a list of streams that are mapped to this Subsystem <br>
     * @return  List<String>
     */
    protected List<String> listStreamsMappedToSubsystem() 
    {
        List<String> list = new ArrayList<>();
        
        //Clear current list
        selectedStreamModel.removeAllElements();;
        
        // Load streams names for selected Sub-System
        String selectedSubsystem =  (String) subsystemComboBox.getSelectedItem();
        if (selectedSubsystem == null) {
            selectedStreamModel.removeAllElements();
            return list ;
        }
        
        List<String> listOfStreams = subsystemStreamMap.get(selectedSubsystem);
        if (listOfStreams == null || listOfStreams.isEmpty()) 
        {
            selectedStreamModel.removeAllElements();
            return list;
        }
        //Update the Processing UnitSelection List
        for(String li : listOfStreams)
        {
            selectedStreamModel.addElement(li);
            list.add(li);
        }
        
        return list;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        subsystemInput = new javax.swing.JTextField();
        addProcessingUnitButton = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        streamsComboBox = new javax.swing.JComboBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        selectedStreamList = new javax.swing.JList();
        modelComboList = new javax.swing.JComboBox();
        jLabel6 = new javax.swing.JLabel();
        subsystemComboBox = new javax.swing.JComboBox();
        removeSubsystemButton = new javax.swing.JButton();
        addStreamToSubSystemButton = new javax.swing.JButton();
        clearSelectedStreamsButton = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        streamAttributesList = new javax.swing.JList();
        jLabel4 = new javax.swing.JLabel();
        subsystemName = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        reportingAttributesList = new javax.swing.JList();
        jLabel7 = new javax.swing.JLabel();
        pickAttributesButton = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        exportReportButton = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        jScrollPane2 = new javax.swing.JScrollPane();
        reportingTable = new javax.swing.JTable();
        refreshButton = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        selectInputFileButton = new javax.swing.JButton();
        inputFileLabel = new javax.swing.JLabel();
        visualIndicatorForDataLoadLabel = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();

        jPanel1.setBackground(new java.awt.Color(204, 204, 204));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Model", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Corbel", 0, 18), new java.awt.Color(102, 102, 102))); // NOI18N

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel1.setText("Define Sub-System");

        subsystemInput.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                subsystemInputActionPerformed(evt);
            }
        });
        subsystemInput.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                subsystemInputKeyReleased(evt);
            }
        });

        addProcessingUnitButton.setText(" + ");
        addProcessingUnitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addProcessingUnitButtonActionPerformed(evt);
            }
        });

        jLabel2.setText("Selected Sub-System");

        jLabel3.setText("Streams");

        streamsComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        selectedStreamList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane1.setViewportView(selectedStreamList);

        modelComboList.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel6.setText("Load Existing Model");

        subsystemComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        subsystemComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                subsystemComboBoxActionPerformed(evt);
            }
        });

        removeSubsystemButton.setText(" - ");

        addStreamToSubSystemButton.setText(" + ");
        addStreamToSubSystemButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addStreamToSubSystemButtonActionPerformed(evt);
            }
        });

        clearSelectedStreamsButton.setText("Clear");
        clearSelectedStreamsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearSelectedStreamsButtonActionPerformed(evt);
            }
        });

        streamAttributesList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane4.setViewportView(streamAttributesList);

        jLabel4.setText("Stream Attributes");

        subsystemName.setBackground(new java.awt.Color(153, 153, 255));
        subsystemName.setForeground(new java.awt.Color(255, 255, 255));
        subsystemName.setText("...");
        subsystemName.setToolTipText("");
        subsystemName.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        reportingAttributesList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane5.setViewportView(reportingAttributesList);

        jLabel7.setText("Reporting Attributes");

        pickAttributesButton.setText(">>");
        pickAttributesButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pickAttributesButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 322, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                            .addComponent(streamsComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 229, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(addStreamToSubSystemButton, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addComponent(jLabel3))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel4)
                                        .addGap(0, 0, Short.MAX_VALUE))
                                    .addComponent(jScrollPane4))
                                .addGap(2, 2, 2)
                                .addComponent(pickAttributesButton))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(239, 239, 239)
                                .addComponent(clearSelectedStreamsButton, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel1)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(addProcessingUnitButton))
                                    .addComponent(subsystemInput, javax.swing.GroupLayout.PREFERRED_SIZE, 226, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 26, Short.MAX_VALUE)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel2)
                                        .addGap(114, 114, 114)
                                        .addComponent(removeSubsystemButton))
                                    .addComponent(subsystemComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 258, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 107, Short.MAX_VALUE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(modelComboList, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel7)
                            .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(subsystemName, javax.swing.GroupLayout.PREFERRED_SIZE, 510, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jLabel1))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel2)
                                .addComponent(removeSubsystemButton))
                            .addComponent(addProcessingUnitButton, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(subsystemInput, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(subsystemComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(subsystemName)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(27, 27, 27)
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(pickAttributesButton))
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(5, 5, 5)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(streamsComboBox)
                            .addComponent(addStreamToSubSystemButton)
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(192, 192, 192)
                .addComponent(clearSelectedStreamsButton, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(modelComboList, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(109, 109, 109))
        );

        jPanel2.setBackground(new java.awt.Color(204, 204, 204));
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Report", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Corbel", 1, 14), new java.awt.Color(51, 51, 51))); // NOI18N

        exportReportButton.setText("Export");
        exportReportButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportReportButtonActionPerformed(evt);
            }
        });

        reportingTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4", "Title 5", "Title 6", "Title 7", "Title 8", "Title 9"
            }
        ));
        jScrollPane2.setViewportView(reportingTable);

        jScrollPane3.setViewportView(jScrollPane2);

        refreshButton.setText("Refresh");
        refreshButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 817, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(exportReportButton, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(refreshButton, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(refreshButton, javax.swing.GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE)
                    .addComponent(exportReportButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 657, Short.MAX_VALUE))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Data ", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Corbel", 0, 18), java.awt.Color.black)); // NOI18N

        selectInputFileButton.setText("Select PDF Input File");
        selectInputFileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectInputFileButtonActionPerformed(evt);
            }
        });

        inputFileLabel.setText("/");

        visualIndicatorForDataLoadLabel.setFont(new java.awt.Font("Tahoma", 1, 10)); // NOI18N
        visualIndicatorForDataLoadLabel.setForeground(new java.awt.Color(255, 0, 0));
        visualIndicatorForDataLoadLabel.setText("- Not Loaded -");

        jLabel5.setText("Status");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addComponent(selectInputFileButton))
                .addGap(21, 21, 21)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(inputFileLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 645, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(visualIndicatorForDataLoadLabel))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(selectInputFileButton)
                    .addComponent(inputFileLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(visualIndicatorForDataLoadLabel)
                    .addComponent(jLabel5))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 46, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 262, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void exportReportButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportReportButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_exportReportButtonActionPerformed

    private void selectInputFileButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectInputFileButtonActionPerformed
       inputFileLabel.setText(Functions.getUserSelectedFile("pdf | PDF "));
       
       loadModelInMemory();
        //soureHistoryFilesListModel.addElement(fileToLoad.getText());
    }//GEN-LAST:event_selectInputFileButtonActionPerformed

    private void subsystemInputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_subsystemInputActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_subsystemInputActionPerformed

    private void addProcessingUnitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addProcessingUnitButtonActionPerformed
       
        String value = subsystemInput.getText();
        if (value.trim().length() > 1 && subsystemModel.getIndexOf(value) < 0)
        {
            subsystemModel.addElement(value);
        }
    }//GEN-LAST:event_addProcessingUnitButtonActionPerformed

    private void addStreamToSubSystemButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addStreamToSubSystemButtonActionPerformed
        
        String subsystem = (String) subsystemComboBox.getSelectedItem();
        String stream = (String) streamsComboBox.getSelectedItem();
            
        if (subsystem != null && stream != null)
        {
           if (subsystemStreamMap.get(subsystem) == null)
           {
               subsystemStreamMap.put(subsystem,new ArrayList());
           }
           
           subsystemStreamMap.get(subsystem).add(stream);
           listStreamsMappedToSubsystem();
        }
    }//GEN-LAST:event_addStreamToSubSystemButtonActionPerformed

    private void subsystemComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_subsystemComboBoxActionPerformed
       
        listStreamsMappedToSubsystem();
       
       // Rebuild grid data using all the stream IDs in selectedStreamList
       
    }//GEN-LAST:event_subsystemComboBoxActionPerformed

    private void subsystemInputKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_subsystemInputKeyReleased
        subsystemName.setText(subsystemInput.getText());
    }//GEN-LAST:event_subsystemInputKeyReleased

    private void clearSelectedStreamsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearSelectedStreamsButtonActionPerformed
      
        String selectedStream = (String) subsystemComboBox.getSelectedItem();
        
        subsystemStreamMap.get(selectedStream).clear();
        
        listStreamsMappedToSubsystem();
       
    }//GEN-LAST:event_clearSelectedStreamsButtonActionPerformed

    private void pickAttributesButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pickAttributesButtonActionPerformed
        if ( ! streamAttributesList.isSelectionEmpty())
        {
            String value = (String) streamAttributesList.getSelectedValue();
            reportingAttributesModel.addElement(value);
        }
    }//GEN-LAST:event_pickAttributesButtonActionPerformed

    private void refreshButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshButtonActionPerformed
       buildReportViewer();
    }//GEN-LAST:event_refreshButtonActionPerformed

   


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addProcessingUnitButton;
    private javax.swing.JButton addStreamToSubSystemButton;
    private javax.swing.JButton clearSelectedStreamsButton;
    private javax.swing.JButton exportReportButton;
    private javax.swing.JLabel inputFileLabel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JComboBox modelComboList;
    private javax.swing.JButton pickAttributesButton;
    private javax.swing.JButton refreshButton;
    private javax.swing.JButton removeSubsystemButton;
    private javax.swing.JList reportingAttributesList;
    private javax.swing.JTable reportingTable;
    private javax.swing.JButton selectInputFileButton;
    private javax.swing.JList selectedStreamList;
    private javax.swing.JList streamAttributesList;
    private javax.swing.JComboBox streamsComboBox;
    private javax.swing.JComboBox subsystemComboBox;
    private javax.swing.JTextField subsystemInput;
    private javax.swing.JLabel subsystemName;
    private javax.swing.JLabel visualIndicatorForDataLoadLabel;
    // End of variables declaration//GEN-END:variables

   
   

   
}

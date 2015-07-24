/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rarediscovery.services.ui;

import com.rarediscovery.data.Model;
import com.rarediscovery.data.Models;
import com.rarediscovery.services.filters.ResultCriteria;
import com.rarediscovery.services.filters.SearchQuery;
import com.rarediscovery.services.filters.SearchResult;
import com.rarediscovery.services.filters.StringFilter;
import com.rarediscovery.services.logic.Functions;
import static com.rarediscovery.services.logic.Functions.log;
import com.rarediscovery.services.logic.TextReader;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

/**
 *
 * @author usaa_developer
 */
public class ViewerWithMenu extends javax.swing.JPanel {

    DefaultListModel<String> 
            selectedStreamModel , 
            streamAttributesModel,
            reportingAttributesModel
            ;
    
    DefaultComboBoxModel<String>  
            streamModel , subsystemModel,
            repositoryModel;
    
    Models inMemoryModels;
    TableModel reportModel;
    String[] columnNamesPrefix = { "Index" , "Name" };
    
    Repo applicationRepo;
    
    Map<String, List<String>> subsystemStreamMap;
    
    List<String> repository;
    
    /**
     * Creates new form ViewerWithMenu
     */
    public ViewerWithMenu() 
    {
        
        initComponents();
        initializeModels();
        
        applicationRepo = loadRepository();
    }
      
    /**
     * This method initializes the components
     */ 
    private void initializeModels() 
    {
        streamModel = new DefaultComboBoxModel<>();
        subsystemModel = new DefaultComboBoxModel<>();
        repositoryModel = new DefaultComboBoxModel<>();
        
        selectedStreamModel = new DefaultListModel<>();
        streamAttributesModel = new DefaultListModel<>();
        reportingAttributesModel = new DefaultListModel<>();
        
        repositoryComboBox.setModel(repositoryModel);
        streamsComboBox.setModel(streamModel);
        subsystemComboBox.setModel(subsystemModel);
        
        ActionListener whatToDoWhenItemIsAdded = new ActionListener() 
        {
            @Override
            public void actionPerformed(ActionEvent ae) 
            {
                JComboBox jcb =  (JComboBox) ae.getSource();
                String t = (String) jcb.getEditor().getItem();
                Functions.log(t);
            }
        };
        
        
        subsystemComboBox.addActionListener(whatToDoWhenItemIsAdded);
        
        selectedStreamList.setModel(selectedStreamModel);
        streamAttributesList.setModel(streamAttributesModel);
        reportingAttributesList.setModel(reportingAttributesModel);
        
        
        reportingTable.setVisible(false);
         
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
        SearchQuery  query = new SearchQuery("PROCESS FLOW STREAM RECORD");
        ResultCriteria criteria = query.newCriteria();
        
        criteria.startReadingFrom(2).stopReadingAt(30)
                .skip(13).skip(14)
                .fieldIdentifierRecord(1)
                .uniqueEntities(4);
        
        SearchResult result = new StringFilter()
                .given(msg)
                .execute(query);
        
         // Save the models in memory
        inMemoryModels = result.buildStreamInformationModels();
        
        // Process the result
        Model firstModel = null ;        
        List<String> sids = Functions.forEachItemIn(inMemoryModels.getAll()).apply(Functions.ModelIDSelector());
        Collections.sort(sids);
        
        
        for (String s: sids)
        {
           streamModel.addElement(s);
           if (firstModel == null)
           {
              firstModel = inMemoryModels.get(s);
           }
        }
        
       
        populateStreamAttributeList(firstModel); 
        
        // Indicate that the data is properly loaded
        visualIndicatorForDataLoadLabel.setText(" Data Loaded ");
        visualIndicatorForDataLoadLabel.setForeground(Color.GREEN);
        
        buildReportViewer();
      }

    protected void populateStreamAttributeList(Model firstModel) 
    {
        // Clear the list
        streamAttributesModel.removeAllElements();
        
        // Show Model Attributes
        List<String> sortedAttributes = new ArrayList() ;
        for(String attribute: firstModel.getAttributeNames())
        {
            sortedAttributes.add(attribute);
            // streamAttributesModel.addElement(attribute); 
        }
        
        streamAttributesModel.addElement("     "); 
        Collections.sort(sortedAttributes);
        
        for(String s: sortedAttributes)
        {
            streamAttributesModel.addElement(s);
        }
    }

    protected void buildReportViewer() 
    {
        if ( ! reportingTable.isVisible())
        {
            reportingTable.setVisible(true);
        }
        
        
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
                
        if (models == null || models.size() == 0)
        {
           return new String[][]{};
        }
              
        int rowCount = reportingAttributesModel.size() +1,
            columnCount = columnNamesPrefix.length+models.size();
              
        String[][] dataGrid = new String [rowCount][columnCount];
         
        int i=0;
        
        Enumeration<String> ram = reportingAttributesModel.elements();
        while(ram.hasMoreElements())
        {
           String[] row = new String[dataGrid[0].length];
           row[0] = ""+i ;
           String component = ram.nextElement(); 
           row[1] = component;
        
           for(int k=0;k<models.size();k++)
           {
              row[2+k] = models.get(k).get(component);
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
    
    private void analyzeAndLoadData() 
    {
        SwingUtilities.invokeLater(
           new Runnable() {

            @Override
            public void run() {
               loadModelInMemory();
        
              refreshButton.setEnabled(true);
              exportReportButton.setEnabled(true);
            }
        });
          
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        selectDatasourceGroup = new javax.swing.ButtonGroup();
        workbenchPanel = new javax.swing.JPanel();
        StatusPanel = new javax.swing.JPanel();
        useFileSourceRadioButton = new javax.swing.JRadioButton();
        loadFileButton = new javax.swing.JButton();
        useDatabaseSourceRadioButton = new javax.swing.JRadioButton();
        repositoryComboBox = new javax.swing.JComboBox();
        visualIndicatorForDataLoadLabel = new javax.swing.JLabel();
        inputFileLabel = new javax.swing.JLabel();
        modelPanel = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        streamsComboBox = new javax.swing.JComboBox();
        subSystemPanel = new javax.swing.JPanel();
        subsystemComboBox = new javax.swing.JComboBox();
        addProcessingUnitButton = new javax.swing.JButton();
        removeSubsystemButton = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        streamInputSelection = new javax.swing.JTextField();
        addStreamToSubSystemButton = new javax.swing.JButton();
        removeStreamFromSubsystem = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        selectedStreamList = new javax.swing.JList();
        ConfigurationSettings = new javax.swing.JPanel();
        saveConfiguration = new javax.swing.JButton();
        loadConfigurationFromRepo = new javax.swing.JButton();
        CustomAttributePanel = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        customAttributeText = new javax.swing.JTextField();
        addCustomAtributeButton = new javax.swing.JButton();
        ReportPreviewPanel = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        reportingTable = new javax.swing.JTable();
        refreshButton = new javax.swing.JButton();
        exportReportButton = new javax.swing.JButton();
        generateChart = new javax.swing.JButton();
        ReportConfigurationPanel = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        streamAttributesList = new javax.swing.JList();
        jPanel9 = new javax.swing.JPanel();
        addAllStreamsToReportingAttributes = new javax.swing.JButton();
        removeStreamFromReportingAttributes = new javax.swing.JButton();
        addStreamToReportingAttributes = new javax.swing.JButton();
        removeAllStreamsFromReportingAttributes = new javax.swing.JButton();
        jPanel10 = new javax.swing.JPanel();
        reportingAttributesList = new javax.swing.JList();

        workbenchPanel.setBackground(new java.awt.Color(102, 102, 102));
        workbenchPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Workbench", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Comic Sans MS", 0, 14), new java.awt.Color(255, 204, 0))); // NOI18N

        StatusPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        StatusPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        selectDatasourceGroup.add(useFileSourceRadioButton);
        useFileSourceRadioButton.setSelected(true);
        useFileSourceRadioButton.setText("Select PDF File");
        useFileSourceRadioButton.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                useFileSourceRadioButtonStateChanged(evt);
            }
        });
        useFileSourceRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                useFileSourceRadioButtonActionPerformed(evt);
            }
        });
        StatusPanel.add(useFileSourceRadioButton);

        loadFileButton.setText("Get File");
        loadFileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadFileButtonActionPerformed(evt);
            }
        });
        StatusPanel.add(loadFileButton);

        selectDatasourceGroup.add(useDatabaseSourceRadioButton);
        useDatabaseSourceRadioButton.setText("Load From Database");
        useDatabaseSourceRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                useDatabaseSourceRadioButtonActionPerformed(evt);
            }
        });
        StatusPanel.add(useDatabaseSourceRadioButton);

        repositoryComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        repositoryComboBox.setEnabled(false);
        repositoryComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                repositoryComboBoxItemStateChanged(evt);
            }
        });
        repositoryComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                repositoryComboBoxActionPerformed(evt);
            }
        });
        StatusPanel.add(repositoryComboBox);

        visualIndicatorForDataLoadLabel.setText("Not Loaded");
        StatusPanel.add(visualIndicatorForDataLoadLabel);

        inputFileLabel.setText("/");
        StatusPanel.add(inputFileLabel);

        modelPanel.setBackground(new java.awt.Color(102, 102, 102));
        modelPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Models", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Comic Sans MS", 0, 14), new java.awt.Color(255, 204, 0))); // NOI18N

        jLabel2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel2.setText("Sub-System");

        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Streams");

        streamsComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        streamsComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                streamsComboBoxActionPerformed(evt);
            }
        });

        subSystemPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        subsystemComboBox.setEditable(true);
        subsystemComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        subsystemComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                subsystemComboBoxActionPerformed(evt);
            }
        });
        subSystemPanel.add(subsystemComboBox);

        addProcessingUnitButton.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        addProcessingUnitButton.setText(" Add ");
        addProcessingUnitButton.setToolTipText("");
        addProcessingUnitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addProcessingUnitButtonActionPerformed(evt);
            }
        });
        subSystemPanel.add(addProcessingUnitButton);

        removeSubsystemButton.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        removeSubsystemButton.setText("Remove");
        removeSubsystemButton.setEnabled(false);
        removeSubsystemButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeSubsystemButtonActionPerformed(evt);
            }
        });
        subSystemPanel.add(removeSubsystemButton);

        streamInputSelection.setColumns(10);
        jPanel4.add(streamInputSelection);

        addStreamToSubSystemButton.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        addStreamToSubSystemButton.setText(" Add Stream");
        addStreamToSubSystemButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addStreamToSubSystemButtonActionPerformed(evt);
            }
        });
        jPanel4.add(addStreamToSubSystemButton);

        removeStreamFromSubsystem.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        removeStreamFromSubsystem.setText("Remove");
        removeStreamFromSubsystem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeStreamFromSubsystemActionPerformed(evt);
            }
        });
        jPanel4.add(removeStreamFromSubsystem);

        selectedStreamList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane1.setViewportView(selectedStreamList);

        saveConfiguration.setText("Save Configuration");
        saveConfiguration.setFocusable(false);
        saveConfiguration.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        saveConfiguration.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        saveConfiguration.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveConfigurationActionPerformed(evt);
            }
        });
        ConfigurationSettings.add(saveConfiguration);

        loadConfigurationFromRepo.setText("Load Configuration");
        loadConfigurationFromRepo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadConfigurationFromRepoActionPerformed(evt);
            }
        });
        ConfigurationSettings.add(loadConfigurationFromRepo);

        CustomAttributePanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jLabel3.setText("Custom Attribute");
        CustomAttributePanel.add(jLabel3);

        customAttributeText.setColumns(15);
        CustomAttributePanel.add(customAttributeText);

        addCustomAtributeButton.setText("Add");
        addCustomAtributeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addCustomAtributeButtonActionPerformed(evt);
            }
        });
        CustomAttributePanel.add(addCustomAtributeButton);

        javax.swing.GroupLayout modelPanelLayout = new javax.swing.GroupLayout(modelPanel);
        modelPanel.setLayout(modelPanelLayout);
        modelPanelLayout.setHorizontalGroup(
            modelPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(modelPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(modelPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jLabel4)
                    .addGroup(modelPanelLayout.createSequentialGroup()
                        .addGroup(modelPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(modelPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(subSystemPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(ConfigurationSettings, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(CustomAttributePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(371, 371, 371)
                        .addComponent(streamsComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        modelPanelLayout.setVerticalGroup(
            modelPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, modelPanelLayout.createSequentialGroup()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(subSystemPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(jLabel4)
                .addGap(2, 2, 2)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(modelPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(streamsComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ConfigurationSettings, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(CustomAttributePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(247, 247, 247))
        );

        ReportPreviewPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Preview", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 14), new java.awt.Color(51, 51, 51))); // NOI18N

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

        refreshButton.setText("Refresh");
        refreshButton.setEnabled(false);
        refreshButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshButtonActionPerformed(evt);
            }
        });

        exportReportButton.setText("Export Report");
        exportReportButton.setEnabled(false);
        exportReportButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportReportButtonActionPerformed(evt);
            }
        });

        generateChart.setText("Generate Chart");
        generateChart.setEnabled(false);
        generateChart.setFocusable(false);
        generateChart.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        generateChart.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        javax.swing.GroupLayout ReportPreviewPanelLayout = new javax.swing.GroupLayout(ReportPreviewPanel);
        ReportPreviewPanel.setLayout(ReportPreviewPanelLayout);
        ReportPreviewPanelLayout.setHorizontalGroup(
            ReportPreviewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ReportPreviewPanelLayout.createSequentialGroup()
                .addComponent(refreshButton, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(exportReportButton, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(generateChart, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(ReportPreviewPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(80, Short.MAX_VALUE))
        );
        ReportPreviewPanelLayout.setVerticalGroup(
            ReportPreviewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ReportPreviewPanelLayout.createSequentialGroup()
                .addGroup(ReportPreviewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(ReportPreviewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(refreshButton, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(exportReportButton, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(generateChart, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 367, Short.MAX_VALUE)
                .addContainerGap())
        );

        ReportConfigurationPanel.setBackground(new java.awt.Color(102, 102, 102));
        ReportConfigurationPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Report Configuration", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Comic Sans MS", 0, 14), new java.awt.Color(255, 204, 0))); // NOI18N
        ReportConfigurationPanel.setLayout(new java.awt.GridLayout(1, 3));

        jPanel8.setLayout(new java.awt.GridLayout(2, 1, 5, 0));

        streamAttributesList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jPanel8.add(streamAttributesList);

        ReportConfigurationPanel.add(jPanel8);

        jPanel9.setMaximumSize(new java.awt.Dimension(50, 100));
        jPanel9.setPreferredSize(new java.awt.Dimension(50, 150));
        jPanel9.setLayout(new java.awt.GridLayout(0, 1));

        addAllStreamsToReportingAttributes.setText(">>");
        addAllStreamsToReportingAttributes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addAllStreamsToReportingAttributesActionPerformed(evt);
            }
        });
        jPanel9.add(addAllStreamsToReportingAttributes);

        removeStreamFromReportingAttributes.setText("<");
        removeStreamFromReportingAttributes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeStreamFromReportingAttributesActionPerformed(evt);
            }
        });
        jPanel9.add(removeStreamFromReportingAttributes);

        addStreamToReportingAttributes.setText(">");
        addStreamToReportingAttributes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addStreamToReportingAttributesActionPerformed(evt);
            }
        });
        jPanel9.add(addStreamToReportingAttributes);

        removeAllStreamsFromReportingAttributes.setText("<<");
        removeAllStreamsFromReportingAttributes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeAllStreamsFromReportingAttributesActionPerformed(evt);
            }
        });
        jPanel9.add(removeAllStreamsFromReportingAttributes);

        ReportConfigurationPanel.add(jPanel9);

        jPanel10.setLayout(null);

        reportingAttributesList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jPanel10.add(reportingAttributesList);
        reportingAttributesList.setBounds(0, 0, 98, 130);

        ReportConfigurationPanel.add(jPanel10);

        javax.swing.GroupLayout workbenchPanelLayout = new javax.swing.GroupLayout(workbenchPanel);
        workbenchPanel.setLayout(workbenchPanelLayout);
        workbenchPanelLayout.setHorizontalGroup(
            workbenchPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(workbenchPanelLayout.createSequentialGroup()
                .addGroup(workbenchPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(workbenchPanelLayout.createSequentialGroup()
                        .addGroup(workbenchPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(modelPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 314, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ReportConfigurationPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 314, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(103, 103, 103)
                        .addComponent(ReportPreviewPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(StatusPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        workbenchPanelLayout.setVerticalGroup(
            workbenchPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, workbenchPanelLayout.createSequentialGroup()
                .addComponent(StatusPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(workbenchPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(workbenchPanelLayout.createSequentialGroup()
                        .addComponent(modelPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 287, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ReportConfigurationPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(5, 5, 5))
                    .addComponent(ReportPreviewPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(workbenchPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 37, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(33, 33, 33)
                .addComponent(workbenchPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(28, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void useFileSourceRadioButtonStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_useFileSourceRadioButtonStateChanged
        //Functions.log(" State Changed !!! ");
    }//GEN-LAST:event_useFileSourceRadioButtonStateChanged

    private void useFileSourceRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_useFileSourceRadioButtonActionPerformed
        loadFileButton.setEnabled(true);
        repositoryComboBox.setEnabled(false);
    }//GEN-LAST:event_useFileSourceRadioButtonActionPerformed

    private void useDatabaseSourceRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_useDatabaseSourceRadioButtonActionPerformed
        loadFileButton.setEnabled(false);
        repositoryComboBox.setEnabled(true);
    }//GEN-LAST:event_useDatabaseSourceRadioButtonActionPerformed

    private void addProcessingUnitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addProcessingUnitButtonActionPerformed

       // String value = subsystemInput.getText();
        String value = (String) subsystemComboBox.getEditor().getItem();
        
        if (value.trim().length() > 1 && subsystemModel.getIndexOf(value) < 0)
        {
            subsystemModel.addElement(value);
            removeSubsystemButton.setEnabled(true);
            //subsystemInput.setText("");  // Clear the screen for next input
        }
    }//GEN-LAST:event_addProcessingUnitButtonActionPerformed

    private void subsystemComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_subsystemComboBoxActionPerformed

        listStreamsMappedToSubsystem();

        // Rebuild grid data using all the stream IDs in selectedStreamList
        buildReportViewer();

    }//GEN-LAST:event_subsystemComboBoxActionPerformed

    private void streamsComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_streamsComboBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_streamsComboBoxActionPerformed

    private void addStreamToSubSystemButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addStreamToSubSystemButtonActionPerformed

        String subsystem = (String) subsystemComboBox.getSelectedItem();
        String stream = (String) streamsComboBox.getSelectedItem();

        if (subsystem != null && stream != null)
        {
            if (subsystemStreamMap.get(subsystem) == null)
            {
                subsystemStreamMap.put(subsystem,new ArrayList());
            }

            if (subsystemStreamMap.get(subsystem).indexOf(stream) < 0)
            {
                 subsystemStreamMap.get(subsystem).add(stream);
                 listStreamsMappedToSubsystem();
            }
           
        }
    }//GEN-LAST:event_addStreamToSubSystemButtonActionPerformed

    private void addStreamToReportingAttributesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addStreamToReportingAttributesActionPerformed
        /**
         * Move selected item from source to Target, if source is not empty
         */
        
        if ( ! streamAttributesList.isSelectionEmpty())
        {
            String value = (String) streamAttributesList.getSelectedValue();
            
            streamAttributesModel.removeElement(value);
            reportingAttributesModel.addElement(value);
        }
    }//GEN-LAST:event_addStreamToReportingAttributesActionPerformed

    private void loadFileButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadFileButtonActionPerformed
         
        inputFileLabel.setText(Functions.getUserSelectedFile("pdf | PDF "));
        String suggestedFile = inputFileLabel.getText();
         
        if (! suggestedFile.isEmpty() && suggestedFile.endsWith(".pdf"))
        {
            analyzeAndLoadData();
        }
         
    }//GEN-LAST:event_loadFileButtonActionPerformed

    private void exportReportButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportReportButtonActionPerformed

        String selectedFile = Functions.getUserSelectedFile("XLS | xls ");
        
        if (selectedFile.isEmpty() || ! selectedFile.endsWith("xls"))
        {
            selectedFile ="ModelReport-"+new Date().toGMTString()+ ".xls";
            inputFileLabel.setText("Report will be saved as " + selectedFile);
        }
             
        Functions.saveAsExcelWorkbook(selectedFile, subsystemStreamMap , inMemoryModels , reportingAttributesModel);
    }//GEN-LAST:event_exportReportButtonActionPerformed

    private void refreshButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshButtonActionPerformed
        buildReportViewer();
    }//GEN-LAST:event_refreshButtonActionPerformed

    private void removeSubsystemButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeSubsystemButtonActionPerformed
        
        if (subsystemModel.getSize() > 0 )
          {  
              String itemToRemove = (String) subsystemModel.getSelectedItem();
                           
              Functions.log(" Remove " + itemToRemove);
              
              subsystemModel.removeElement(itemToRemove);
              if (subsystemModel.getSize() == 0) 
              {
                 removeSubsystemButton.setEnabled(false);
              }
           }
    }//GEN-LAST:event_removeSubsystemButtonActionPerformed

    private void addAllStreamsToReportingAttributesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addAllStreamsToReportingAttributesActionPerformed
        
        if (streamModel.getSize() > 0 )
        {
          String sample = streamModel.getElementAt(0);
          Model sampleModel = inMemoryModels.get(sample);
          populateStreamAttributeList(sampleModel);
          
          reportingAttributesModel.removeAllElements();
          for(int i=0;i < streamAttributesModel.getSize();i++)
          {
              reportingAttributesModel.addElement(streamAttributesModel.getElementAt(i));
          }
          streamAttributesModel.removeAllElements();
        }
    }//GEN-LAST:event_addAllStreamsToReportingAttributesActionPerformed

    private void removeAllStreamsFromReportingAttributesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeAllStreamsFromReportingAttributesActionPerformed
         if (streamModel.getSize() > 0 )
         {
          String sample = streamModel.getElementAt(0);
          Model sampleModel = inMemoryModels.get(sample);
          populateStreamAttributeList(sampleModel);
          
          reportingAttributesModel.removeAllElements();
        }
    }//GEN-LAST:event_removeAllStreamsFromReportingAttributesActionPerformed

    private Repo loadRepository() 
    {
         
        String DB= "repo.properties";
        Map<String,String> repoToFileMap = new HashMap<>();
        Map<String,String[]> repoSubsystemToStreamMap = new HashMap<>();
        
        Repo repo = new Repo();
                
        try 
        {
              repositoryModel.removeAllElements();
              
              // Create property file and save the following items
              //1. FileName <optional>|
              //2. Subsystems - [Attributes-Selected]
              //3.
              //   pop user to save with a friendly name
              //   persist
              FileReader reader = new FileReader(DB);
              BufferedReader bufferedReader = new BufferedReader(reader);
              String line;
              
              while((line = bufferedReader.readLine()) != null)
              {
                  String[] props = line.trim().split("=");
                  
                  if (props[0] == null) {continue;} 
                  
                  repositoryModel.addElement(props[0]);
                  String data = null;
                  
                  // Process the file name that is the source
                  if (props[1] != null && props[1].trim().length() > 0)
                  {
                       int cutFileName = props[1].indexOf("|");
                       if ( cutFileName > 0)
                       {
                          String source= props[1].substring(0,cutFileName);
                          repoToFileMap.put(props[0], source);
                          data = props[1].substring(cutFileName+1);
                       }
                  }
                  
                  // Process SubSystem to Stream Mapping
                  if (data != null || data.trim().length() > 0)
                  {
                      String[] dd = data.split(";");
                      log(dd[0]);
                      for(int j=0;j< dd.length ;j ++)
                      {
                         int cut = dd[j].indexOf(">");
                         if (cut >0 )
                         {
                             String subName = dd[j].substring(0,cut);
                             String streamNames = dd[j].substring(cut+1);
                             repoSubsystemToStreamMap.put(subName, streamNames.split(","));
                             //log(repoSubsystemToStreamMap.get(subName).toString());
                         }
                      }
                  
                  }
              }
              
              bufferedReader.close();
              reader.close();
              
              repo.setRepoToFileMap(repoToFileMap);
              repo.setRepoSubsystemToStreamMap(repoSubsystemToStreamMap);
              
        } catch (FileNotFoundException ex) 
        {
              Logger.getLogger(ViewerWithMenu.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) 
        {
              Logger.getLogger(ViewerWithMenu.class.getName()).log(Level.SEVERE, null, ex);
        }    
        
        return repo;
    }

    class Repo
    {
        public final static String DB = "repo.properties";
        
        Map<String,String> repoToFileMap = new HashMap<>();
        Map<String,String[]> repoSubsystemToStreamMap = new HashMap<>();

        public void setRepoSubsystemToStreamMap(Map<String, String[]> repoSubsystemToStreamMap) {
            this.repoSubsystemToStreamMap = repoSubsystemToStreamMap;
        }

        public void setRepoToFileMap(Map<String, String> repoToFileMap) {
            this.repoToFileMap = repoToFileMap;
        }

        public Map<String, String[]> getRepoSubsystemToStreamMap() {
            return repoSubsystemToStreamMap;
        }

        public Map<String, String> getRepoToFileMap() {
            return repoToFileMap;
        }
        
        
    
    }
    
    private void saveConfigurationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveConfigurationActionPerformed
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
              
            String fileSource = inputFileLabel.getText();
            
            StringBuffer subs = new StringBuffer();
            
            Set<String> selectedSubs = subsystemStreamMap.keySet();
            
            subs.append(configName).append("=").append(fileSource);
            for(String sub : selectedSubs)
            {
                 subs.append("|");
                 subs.append(sub).append(" > ");
                 
                 // Get Selected Streams
                 for(String stream : subsystemStreamMap.get(sub) )
                 {
                     subs.append(stream).append(",");
                 }
            }
            
              // Create property file and save the following items
              //1. FileName <optional>|
              //2. Subsystems - [Attributes-Selected]
              //3.
              //   pop user to save with a friendly name
              //   persist
              FileWriter writer = new FileWriter(DB,true);
              BufferedWriter bufferedWriter = new BufferedWriter(writer);
                     
              bufferedWriter.append(subs.toString());
              bufferedWriter.newLine();
              bufferedWriter.flush();
              
              bufferedWriter.close();
              
        } catch (FileNotFoundException ex) 
        {
              Logger.getLogger(ViewerWithMenu.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) 
        {
              Logger.getLogger(ViewerWithMenu.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_saveConfigurationActionPerformed

    private void removeStreamFromReportingAttributesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeStreamFromReportingAttributesActionPerformed
        /**
         * Move selected item from source to Target, if source is not empty
         */
        
        if ( ! reportingAttributesList.isSelectionEmpty())
        {
            String value = (String) reportingAttributesList.getSelectedValue();
            
            streamAttributesModel.addElement(value);
            reportingAttributesModel.removeElement(value);
        }
    }//GEN-LAST:event_removeStreamFromReportingAttributesActionPerformed

    private void removeStreamFromSubsystemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeStreamFromSubsystemActionPerformed
        if (! selectedStreamModel.isEmpty())
        {
           int index =selectedStreamList.getSelectedIndex();
           
           // Delete only when item is selected
           if (index >= 0)
           {
              selectedStreamModel.remove(index);
              subsystemStreamMap.get(subsystemModel.getSelectedItem()).remove(index);
           }
        }
    }//GEN-LAST:event_removeStreamFromSubsystemActionPerformed

    private void repositoryComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_repositoryComboBoxActionPerformed
        
        log("Selection Changed");
       
        
    }//GEN-LAST:event_repositoryComboBoxActionPerformed

    private void repositoryComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_repositoryComboBoxItemStateChanged
       
    }//GEN-LAST:event_repositoryComboBoxItemStateChanged

    private void loadConfigurationFromRepoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadConfigurationFromRepoActionPerformed
               
        String selectedRepo = (String) repositoryModel.getSelectedItem();
        String fileName = getApplicationRepo().getRepoToFileMap().get(selectedRepo);
        // Display Filename
        inputFileLabel.setText(fileName);
        
        // Need Data to proceed
        loadModelInMemory();
        
        // Load config now
        Set<String> subsystems = getApplicationRepo().getRepoSubsystemToStreamMap().keySet();
        
        // Clear any current selection
        subsystemModel.removeAllElements();
        for(String s: subsystems)
        {
           subsystemModel.addElement(s);
           if (subsystemStreamMap.get(s) == null)
           {
              subsystemStreamMap.put(s, new ArrayList());
           }
        }
    }//GEN-LAST:event_loadConfigurationFromRepoActionPerformed

    private void addCustomAtributeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addCustomAtributeButtonActionPerformed
        
        streamAttributesModel.addElement(customAttributeText.getText());
    }//GEN-LAST:event_addCustomAtributeButtonActionPerformed

    public Repo getApplicationRepo() {
        return applicationRepo;
    }


    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel ConfigurationSettings;
    private javax.swing.JPanel CustomAttributePanel;
    private javax.swing.JPanel ReportConfigurationPanel;
    private javax.swing.JPanel ReportPreviewPanel;
    private javax.swing.JPanel StatusPanel;
    private javax.swing.JButton addAllStreamsToReportingAttributes;
    private javax.swing.JButton addCustomAtributeButton;
    private javax.swing.JButton addProcessingUnitButton;
    private javax.swing.JButton addStreamToReportingAttributes;
    private javax.swing.JButton addStreamToSubSystemButton;
    private javax.swing.JTextField customAttributeText;
    private javax.swing.JButton exportReportButton;
    private javax.swing.JButton generateChart;
    private javax.swing.JLabel inputFileLabel;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JButton loadConfigurationFromRepo;
    private javax.swing.JButton loadFileButton;
    private javax.swing.JPanel modelPanel;
    private javax.swing.JButton refreshButton;
    private javax.swing.JButton removeAllStreamsFromReportingAttributes;
    private javax.swing.JButton removeStreamFromReportingAttributes;
    private javax.swing.JButton removeStreamFromSubsystem;
    private javax.swing.JButton removeSubsystemButton;
    private javax.swing.JList reportingAttributesList;
    private javax.swing.JTable reportingTable;
    private javax.swing.JComboBox repositoryComboBox;
    private javax.swing.JButton saveConfiguration;
    private javax.swing.ButtonGroup selectDatasourceGroup;
    private javax.swing.JList selectedStreamList;
    private javax.swing.JList streamAttributesList;
    private javax.swing.JTextField streamInputSelection;
    private javax.swing.JComboBox streamsComboBox;
    private javax.swing.JPanel subSystemPanel;
    private javax.swing.JComboBox subsystemComboBox;
    private javax.swing.JRadioButton useDatabaseSourceRadioButton;
    private javax.swing.JRadioButton useFileSourceRadioButton;
    private javax.swing.JLabel visualIndicatorForDataLoadLabel;
    private javax.swing.JPanel workbenchPanel;
    // End of variables declaration//GEN-END:variables

    

   
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rarediscovery.services.ui;

import com.rarediscovery.services.filters.SearchRequest;
import com.rarediscovery.services.filters.SearchResult;
import com.rarediscovery.services.filters.StringFilter;
import com.rarediscovery.services.logic.Functions;
import static com.rarediscovery.services.logic.Functions.log;
import com.rarediscovery.services.logic.TextReader;
import java.awt.BorderLayout;
import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
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
            reportingAttributesModel;
    
    DefaultComboBoxModel<String>  
            streamModel , subsystemModel;
    
    SearchResult.Models inMemoryModels;
    TableModel reportModel;
     String[] columnNamesPrefix = { "Index" , "Name" };
    
    Map<String, List<String>> subsystemStreamMap;
    
    
    /**
     * Creates new form ViewerWithMenu
     */
    public ViewerWithMenu() {
        initComponents();
        initializeModels();
    }
      
    /**
     * This method initializes the components
     */ 
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
        SearchResult.Model firstModel = null ;
        for (SearchResult.Model m : inMemoryModels.getAll())
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
        if ( ! reportingTable.isVisible())
        {
            reportingTable.setVisible(true);
        }
        
        
        List<SearchResult.Model> models = inMemoryModels.getModels(listStreamsMappedToSubsystem());
        Object[][] dataGrid = convertModelToDataGrid(models);
        
        // No data loaded , exit
        if (dataGrid.length == 0) { return ;}
        
        reportModel = new DefaultTableModel(dataGrid,  createGridHeader(dataGrid[0].length, models));
        
        reportingTable.setModel(reportModel);
        reportingTable.setAutoCreateRowSorter(true);
    }
      
    private Object[][] convertModelToDataGrid(final List<SearchResult.Model> models) 
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

    protected String[] createGridHeader(int columnCount, List<SearchResult.Model> models) 
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

        selectDatasourceGroup = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jToolBar1 = new javax.swing.JToolBar();
        jPanel2 = new javax.swing.JPanel();
        row0 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        row1 = new javax.swing.JPanel();
        useFileSourceRadioButton = new javax.swing.JRadioButton();
        loadFileButton = new javax.swing.JButton();
        row2 = new javax.swing.JPanel();
        useDatabaseSourceRadioButton = new javax.swing.JRadioButton();
        databaseHandleComboBox = new javax.swing.JComboBox();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        jSeparator4 = new javax.swing.JToolBar.Separator();
        jPanel6 = new javax.swing.JPanel();
        analyzeData = new javax.swing.JButton();
        refreshButton = new javax.swing.JButton();
        exportReportButton = new javax.swing.JButton();
        generateChart = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        saveConfiguration = new javax.swing.JButton();
        loadConfiguration = new javax.swing.JButton();
        jSeparator3 = new javax.swing.JToolBar.Separator();
        exitButton = new javax.swing.JButton();
        workbenchPanel = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        pickAttributesButton = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        streamAttributesList = new javax.swing.JList();
        jScrollPane5 = new javax.swing.JScrollPane();
        reportingAttributesList = new javax.swing.JList();
        jLabel7 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        subsystemInput = new javax.swing.JTextField();
        addProcessingUnitButton = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        subsystemComboBox = new javax.swing.JComboBox();
        removeSubsystemButton = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        streamsComboBox = new javax.swing.JComboBox();
        addStreamToSubSystemButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        selectedStreamList = new javax.swing.JList();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        reportingTable = new javax.swing.JTable();
        jPanel7 = new javax.swing.JPanel();
        inputFileLabel = new javax.swing.JLabel();
        visualIndicatorForDataLoadLabel = new javax.swing.JLabel();

        jPanel1.setBackground(new java.awt.Color(102, 102, 102));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Tools", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Comic Sans MS", 0, 14), new java.awt.Color(255, 204, 0))); // NOI18N
        jPanel1.setForeground(new java.awt.Color(255, 204, 0));

        jToolBar1.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jToolBar1.setRollover(true);

        jLabel1.setBackground(new java.awt.Color(153, 153, 153));
        jLabel1.setForeground(new java.awt.Color(255, 204, 51));
        jLabel1.setText("Select Data to Load");
        jLabel1.setOpaque(true);
        row0.add(jLabel1);

        selectDatasourceGroup.add(useFileSourceRadioButton);
        useFileSourceRadioButton.setSelected(true);
        useFileSourceRadioButton.setText("PDF File");
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
        row1.add(useFileSourceRadioButton);

        loadFileButton.setText("Get File");
        loadFileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadFileButtonActionPerformed(evt);
            }
        });
        row1.add(loadFileButton);

        selectDatasourceGroup.add(useDatabaseSourceRadioButton);
        useDatabaseSourceRadioButton.setText("Database");
        useDatabaseSourceRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                useDatabaseSourceRadioButtonActionPerformed(evt);
            }
        });
        row2.add(useDatabaseSourceRadioButton);

        databaseHandleComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        databaseHandleComboBox.setEnabled(false);
        row2.add(databaseHandleComboBox);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(row0, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(row1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(row2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(row0, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(row1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(row2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jToolBar1.add(jPanel2);
        jToolBar1.add(jSeparator1);
        jToolBar1.add(jSeparator4);

        jPanel6.setLayout(new java.awt.GridLayout(0, 1));

        analyzeData.setText("Run");
        analyzeData.setEnabled(false);
        analyzeData.setFocusable(false);
        analyzeData.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        analyzeData.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        analyzeData.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                analyzeDataActionPerformed(evt);
            }
        });
        jPanel6.add(analyzeData);

        refreshButton.setText("Preview");
        refreshButton.setEnabled(false);
        refreshButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshButtonActionPerformed(evt);
            }
        });
        jPanel6.add(refreshButton);

        exportReportButton.setText("Export Report");
        exportReportButton.setEnabled(false);
        exportReportButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportReportButtonActionPerformed(evt);
            }
        });
        jPanel6.add(exportReportButton);

        generateChart.setText("Generate Chart");
        generateChart.setEnabled(false);
        generateChart.setFocusable(false);
        generateChart.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        generateChart.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jPanel6.add(generateChart);
        jPanel6.add(jSeparator2);

        saveConfiguration.setText("Save Configuration");
        saveConfiguration.setFocusable(false);
        saveConfiguration.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        saveConfiguration.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jPanel6.add(saveConfiguration);

        loadConfiguration.setText("Load Configuration");
        loadConfiguration.setFocusable(false);
        loadConfiguration.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        loadConfiguration.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jPanel6.add(loadConfiguration);
        jPanel6.add(jSeparator3);

        exitButton.setText("Exit");
        exitButton.setFocusable(false);
        exitButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        exitButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jPanel6.add(exitButton);

        jToolBar1.add(jPanel6);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        workbenchPanel.setBackground(new java.awt.Color(102, 102, 102));
        workbenchPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Workbench", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Comic Sans MS", 0, 14), new java.awt.Color(255, 204, 0))); // NOI18N

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Report", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, null, new java.awt.Color(0, 0, 0)));

        jLabel5.setText("Stream Attributes");

        pickAttributesButton.setText(">");
        pickAttributesButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pickAttributesButtonActionPerformed(evt);
            }
        });

        streamAttributesList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane4.setViewportView(streamAttributesList);

        reportingAttributesList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane5.setViewportView(reportingAttributesList);

        jLabel7.setText("Reporting Attributes");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(pickAttributesButton, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel7))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(29, 29, 29))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(pickAttributesButton, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane4)
                    .addComponent(jScrollPane5))
                .addContainerGap())
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Model Configuration", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 14), new java.awt.Color(0, 0, 0))); // NOI18N

        jLabel2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel2.setText("Define Sub-System");

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

        jLabel3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel3.setText("Sub-Systems");

        subsystemComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        subsystemComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                subsystemComboBoxActionPerformed(evt);
            }
        });

        removeSubsystemButton.setText(" - ");
        removeSubsystemButton.setEnabled(false);
        removeSubsystemButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeSubsystemButtonActionPerformed(evt);
            }
        });

        jLabel4.setText("Streams");

        streamsComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        streamsComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                streamsComboBoxActionPerformed(evt);
            }
        });

        addStreamToSubSystemButton.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        addStreamToSubSystemButton.setText(" + ");
        addStreamToSubSystemButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addStreamToSubSystemButtonActionPerformed(evt);
            }
        });

        selectedStreamList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane1.setViewportView(selectedStreamList);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jLabel4)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(subsystemComboBox, javax.swing.GroupLayout.Alignment.LEADING, 0, 212, Short.MAX_VALUE)
                            .addComponent(subsystemInput, javax.swing.GroupLayout.Alignment.LEADING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(removeSubsystemButton)
                            .addComponent(addProcessingUnitButton)))
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel4Layout.createSequentialGroup()
                            .addComponent(streamsComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(addStreamToSubSystemButton, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(18, 18, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(subsystemInput, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(addProcessingUnitButton, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(subsystemComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3)))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(removeSubsystemButton)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel4)
                        .addComponent(addStreamToSubSystemButton, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(streamsComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Preview", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 14), new java.awt.Color(51, 51, 51))); // NOI18N

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

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 593, Short.MAX_VALUE)
            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel5Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 560, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(23, Short.MAX_VALUE)))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 413, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap()))
        );

        jPanel7.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        inputFileLabel.setText("/");

        visualIndicatorForDataLoadLabel.setText("Not Loaded");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(inputFileLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 473, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(visualIndicatorForDataLoadLabel))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(inputFileLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(visualIndicatorForDataLoadLabel))
        );

        javax.swing.GroupLayout workbenchPanelLayout = new javax.swing.GroupLayout(workbenchPanel);
        workbenchPanel.setLayout(workbenchPanelLayout);
        workbenchPanelLayout.setHorizontalGroup(
            workbenchPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(workbenchPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(workbenchPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(workbenchPanelLayout.createSequentialGroup()
                .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        workbenchPanelLayout.setVerticalGroup(
            workbenchPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, workbenchPanelLayout.createSequentialGroup()
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(workbenchPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(workbenchPanelLayout.createSequentialGroup()
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, 456, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(workbenchPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(workbenchPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 0, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void useFileSourceRadioButtonStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_useFileSourceRadioButtonStateChanged
        //Functions.log(" State Changed !!! ");
    }//GEN-LAST:event_useFileSourceRadioButtonStateChanged

    private void useFileSourceRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_useFileSourceRadioButtonActionPerformed
        loadFileButton.setEnabled(true);
        databaseHandleComboBox.setEnabled(false);
    }//GEN-LAST:event_useFileSourceRadioButtonActionPerformed

    private void useDatabaseSourceRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_useDatabaseSourceRadioButtonActionPerformed
        loadFileButton.setEnabled(false);
        databaseHandleComboBox.setEnabled(true);
    }//GEN-LAST:event_useDatabaseSourceRadioButtonActionPerformed

    private void subsystemInputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_subsystemInputActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_subsystemInputActionPerformed

    private void subsystemInputKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_subsystemInputKeyReleased
       // subsystemName.setText(subsystemInput.getText());
    }//GEN-LAST:event_subsystemInputKeyReleased

    private void addProcessingUnitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addProcessingUnitButtonActionPerformed

        String value = subsystemInput.getText();
        if (value.trim().length() > 1 && subsystemModel.getIndexOf(value) < 0)
        {
            subsystemModel.addElement(value);
            removeSubsystemButton.setEnabled(true);
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

            subsystemStreamMap.get(subsystem).add(stream);
            listStreamsMappedToSubsystem();
        }
    }//GEN-LAST:event_addStreamToSubSystemButtonActionPerformed

    private void pickAttributesButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pickAttributesButtonActionPerformed
        if ( ! streamAttributesList.isSelectionEmpty())
        {
            String value = (String) streamAttributesList.getSelectedValue();
            reportingAttributesModel.addElement(value);
        }
    }//GEN-LAST:event_pickAttributesButtonActionPerformed

    private void loadFileButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadFileButtonActionPerformed
         inputFileLabel.setText(Functions.getUserSelectedFile("pdf | PDF "));
         String suggestedFile = inputFileLabel.getText();
         
         if (! suggestedFile.isEmpty() && suggestedFile.endsWith(".pdf"))
         {
            analyzeData.setEnabled(true);
         }
         
    }//GEN-LAST:event_loadFileButtonActionPerformed

    private void exportReportButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportReportButtonActionPerformed

        String selectedFile = Functions.getUserSelectedFile("XLS | xls ");
        
        if (selectedFile.isEmpty() || ! selectedFile.endsWith("xls"))
        {
            selectedFile ="model-report.xls";
            inputFileLabel.setText("Report will be saved as " + selectedFile);
        }
             
        Functions.saveAsExcelWorkbook(selectedFile, subsystemStreamMap , inMemoryModels , reportingAttributesModel);
    }//GEN-LAST:event_exportReportButtonActionPerformed

    private void refreshButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshButtonActionPerformed
        buildReportViewer();
    }//GEN-LAST:event_refreshButtonActionPerformed

    private void analyzeDataActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_analyzeDataActionPerformed
       
        loadModelInMemory();
        
        refreshButton.setEnabled(true);
        exportReportButton.setEnabled(true);
        
    }//GEN-LAST:event_analyzeDataActionPerformed

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


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addProcessingUnitButton;
    private javax.swing.JButton addStreamToSubSystemButton;
    private javax.swing.JButton analyzeData;
    private javax.swing.JComboBox databaseHandleComboBox;
    private javax.swing.JButton exitButton;
    private javax.swing.JButton exportReportButton;
    private javax.swing.JButton generateChart;
    private javax.swing.JLabel inputFileLabel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JToolBar.Separator jSeparator3;
    private javax.swing.JToolBar.Separator jSeparator4;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JButton loadConfiguration;
    private javax.swing.JButton loadFileButton;
    private javax.swing.JButton pickAttributesButton;
    private javax.swing.JButton refreshButton;
    private javax.swing.JButton removeSubsystemButton;
    private javax.swing.JList reportingAttributesList;
    private javax.swing.JTable reportingTable;
    private javax.swing.JPanel row0;
    private javax.swing.JPanel row1;
    private javax.swing.JPanel row2;
    private javax.swing.JButton saveConfiguration;
    private javax.swing.ButtonGroup selectDatasourceGroup;
    private javax.swing.JList selectedStreamList;
    private javax.swing.JList streamAttributesList;
    private javax.swing.JComboBox streamsComboBox;
    private javax.swing.JComboBox subsystemComboBox;
    private javax.swing.JTextField subsystemInput;
    private javax.swing.JRadioButton useDatabaseSourceRadioButton;
    private javax.swing.JRadioButton useFileSourceRadioButton;
    private javax.swing.JLabel visualIndicatorForDataLoadLabel;
    private javax.swing.JPanel workbenchPanel;
    // End of variables declaration//GEN-END:variables

   
}

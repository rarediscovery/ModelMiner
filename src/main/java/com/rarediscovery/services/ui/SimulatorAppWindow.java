/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rarediscovery.services.ui;

import com.rarediscovery.services.logic.Delimiters;
import com.rarediscovery.services.logic.TextReader;
import com.rarediscovery.services.filters.OldFilter;
import com.rarediscovery.services.model.DataPage;
import com.rarediscovery.services.ui.Group.ID;

import java.awt.Color;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/**
 *
 * @author usaa_developer
 */
public class SimulatorAppWindow extends GenericWindow {
    
    int count;   
    
    public static void main(String[] args) {
        SimulatorAppWindow app = new SimulatorAppWindow();
        app.setTitle("Plant Data Analytic Engine - 1.0 ");
    }

    public SimulatorAppWindow() {
        super();
        buildUserInterface();
    }
    
   
   protected void buildUserInterface() 
   {
       
        Group group = new Group();        
        Group leftPanel = new Group().vertical();
        
        leftPanel.title(" Control Information ").size(500, 500)
                .add(
                        new Group(useIO())
                            .title(" Input Data ")
                            .size(400, 100)
                       
                        .addButton("Browse", null)
                        .addButton("Clear", null))
                .add(
                        new Group(useIO())
                            .title(" Action Panel ")
                            .size(400, 100)
                  
                        .addLabel(ID.None(), "Selected File :")
                        .addTextInput(
                                ID.of("input.filename",
                                       Group.IO.ReadOnly),
                                       Group.repeat(" ", 40))
                        .addButton("Process File", null)
                        .addButton("Apply Filter", null)
                        .addButton("Save File", null)
                )
                
                .add(
                        new Group(useIO())
                            .title(" Console ")
                            .size(400, 200)
                            .vertical()
                        .addTextDocument(
                                ID.of("console",Group.IO.Output),"...", null));
        
        //
        
        Group rightPanel = new Group(useIO());
        rightPanel.vertical().title("  Result  ").size(700, 500);
        rightPanel
                .add(
                        new Group(useIO())
                            .addButton(" <<", null)
                            .addTextInput(ID.of("page.number", Group.IO.InputOutput), " 0 ")
                            .addButton(" >> ", null)
                )
                .add(
                        new Group(useIO())
                            .vertical()
                            .addTextDocument(ID.of("output.viewer",Group.IO.InputOutput),"", null)
                )
                ;
        
        group.add(leftPanel).add(rightPanel);
        
        getWindow().getContentPane().add(group.getCanvas());
        
        refresh();
    }
   
    private JComponent PDFAnalysisButtonPanel() 
    {
        JPanel AnalysisPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        JButton startButton = new JButton(" - Start Processing -");
        startButton.addActionListener(new ActionListener() 
        {
            @Override
            public void actionPerformed(ActionEvent arg0)
            {
                File inputFile = new File(useIO().getValue("input.file"));
                TextReader textReader = new TextReader(inputFile);
                String t= textReader.convertPDFToString();
                    
                if (t.trim().isEmpty()){ return ;}
                
                /*
                List<DataPage> dataPages = textReader.textToDataPages(t);
                
                // Add to session store
                useIO().addData("list.of.data.page", dataPages);
                useIO().addData("max.page.size", dataPages.size());
                if (dataPages.size() > 0)
                {
                   useIO().addData("page.number", 0);
                }
                    
                OldFilter filter = new OldFilter();
                filter.exclude(1);
                
                useIO().show("content.info", dataPages.get(0).get(filter));
                */
 
            }
        });
        
        AnalysisPanel.add(startButton);
        
        return AnalysisPanel;
    }
    
    public JTextField createFooterPanel() {
        
        JTextField infoLabel = new JTextField();
        infoLabel.setEditable(false);
        infoLabel.setFont(new Font("Consolas", Font.ITALIC+Font.PLAIN, 14));
        infoLabel.setForeground(Color.GREEN);
        useIO().registerOutput("status.info", infoLabel);
        return infoLabel;
    }

     public JPanel createContentPanel() {
        
         JTextArea textBox = new JTextArea("");
         //textBox.setPreferredSize(new Dimension(600,200));
          //textBox.setSize(100, 120);
          //textBox.setAutoscrolls(true);
          //textBox.setEditable(false);
          //textBox.setBackground(Color.GRAY);
           
          textBox.setFont(new Font("Consolas", Font.ITALIC+Font.PLAIN, 12));
          textBox.setForeground(Color.BLACK);
                    
          JScrollPane scrollableTextBox = 
                  new JScrollPane(textBox,
                                    JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                                    JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
          
          JTextField currentPage = new JTextField(3);
          currentPage.setText("1");
          JButton nextButton = new JButton(" >> ");
          JButton previousButton = new JButton(" << ");
          JSlider slider = new JSlider(SwingConstants.HORIZONTAL);
          slider.setMajorTickSpacing(5);
          slider.setMinorTickSpacing(1);
          slider.setMaximum(count);
          
          ActionListener forward = new ActionListener() 
          {
             @Override
             public void actionPerformed(ActionEvent ae) {
                
               int pageNumber =  useIO().getIntegerValue("page.number");
               if ( pageNumber < Integer.MAX_VALUE
                    && useIO().getData("list.of.data.page") != null) 
               {
                   int d = useIO().displayPage(pageNumber+1, new OldFilter());
                    //update screen for page loaded
                   useIO().changeValue("page.number", "" +d);
                   useIO().addData("page.number", d);
               }
             }
          };
          
         ActionListener backwards = new ActionListener() 
          {
             @Override
             public void actionPerformed(ActionEvent ae) {
                
               int pageNumber =  useIO().getIntegerValue("page.number");
               if ( pageNumber < Integer.MAX_VALUE
                    && useIO().getData("list.of.data.page") != null) 
               {
                   int d = useIO().displayPage(pageNumber-1, new OldFilter());
                   //update screen for page loaded
                   useIO().changeValue("page.number", "" +d);
                   useIO().addData("page.number", d);
               }
             }
          };
          
          nextButton.addActionListener(forward);
          previousButton.addActionListener(backwards);
          
          
          Group row1 = new Group();
          row1.add(new JLabel(" Pages : "));
          row1.add(previousButton).add(currentPage).add(nextButton);
                    
          Group row2 = new Group();
          row2.add(scrollableTextBox);
          
          
          Group displayView = new Group().vertical();
          displayView.add(row1).add(row2);
          displayView.title(" Plant Model Analysis ");
                    
          useIO().registerInput("page.number", currentPage);
          useIO().registerOutput("content.info", textBox);
          
        return displayView.getCanvas();
    }
        
    private Integer next() {
        return count++;
    }
        
   public JPanel FileManagementPanel()
   {
      
        Group row1 = new Group();
        JLabel inputFileLabel = new JLabel(" Select File to process : ");
        final JTextField readAsFilename = new JTextField(50);
        readAsFilename.setEditable(false);   /* READ-ONLY */
        JButton selectButton = new JButton(" - Browse -");
        JButton clearButton = new JButton(" - Clear -");
        
        row1.add(inputFileLabel); row1.add(readAsFilename);
        row1.add(selectButton);   row1.add(clearButton);
        /*  ---------                 ----------------*/
        
        Group row2 = new Group();
        JLabel outputFileLabel = new JLabel(" Save Result As : ");
        final JTextField saveAsFilename = new JTextField(50);
        saveAsFilename.setEditable(false);   /* READ ONLY */
        JButton saveButton = new JButton(" - Save -");
        JButton saveAsButton = new JButton(" - Save As -");
        
        row2.add(outputFileLabel); row2.add(saveAsFilename);
        row2.add(saveButton); row2.add(saveAsButton);
        /*  ---------                 ----------------*/
        
        // Behavior to select the file in a drive path
        // SELECT FILE TO READ
        selectButton.addActionListener(new ActionListener() 
        {
            @Override
            public void actionPerformed(ActionEvent arg0)
            {
                String userInputFileName = getUserSelectedFile(".PDF");
                File inputFile = new File(userInputFileName);
                readAsFilename.setText(inputFile.getAbsolutePath());
                
                saveAsFilename.setText(readAsFilename.getText().toLowerCase().replace(".pdf", "-RESULT-"+new Date()+".xls"));
            }
        });
        
        // SAVE AS
        saveAsButton.addActionListener(new ActionListener() 
        {
            @Override
            public void actionPerformed(ActionEvent arg0)
            {
                String userInputFileName = getUserSelectedFile(".xls");
                File aFile = new File(userInputFileName);
                saveAsFilename.setText(aFile.getAbsolutePath());
            }
        });
        
        // SAVE
        saveButton.addActionListener(new ActionListener() 
        {
            @Override
            public void actionPerformed(ActionEvent arg0)
            {
                if (readAsFilename.getText().trim().length() <3 ){ return ;}                
                saveAsFilename.setText(readAsFilename.getText().replace(".PDF", "-RESULT-"+new Date()+".XLS"));
            }
        });
              
        // Behavior to clear content of the filename
        clearButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
               readAsFilename.setText("");
            }
        });
                
       Group group = new Group();
       group.vertical();
       group.add(row1).add(row2);
       group.title(" File Management ");
       
       // CONNECT INPUT and OUTPUT to page elements 
       useIO().registerInput("input.file", readAsFilename);
       useIO().registerInput("output.file", saveAsFilename);
        
       return group.getCanvas();
   }
   
   /**
    * Save Content to a Filename
    * @param content
    * @param inputFileName 
    */
   private void saveToFile(String content , String outputFileName) 
   {
        if (content  != null && outputFileName != null)
        {
           return;
        }
            
	File outputFile = new File(outputFileName);
        
	FileWriter fileWriter = null;
	try 
        {
            fileWriter = new FileWriter(outputFile);
            fileWriter.write(content);
            
	} catch (IOException e)
        {
            // TODO Auto-generated catch block
            
	}finally
	{
            if (fileWriter != null){
	    try {
                 fileWriter.flush();
		 fileWriter.close();
		}catch (IOException e) {}
            }
	}
   }
   
   
   public String getUserSelectedFile(final String fileExtension) 
    {
        FileDialog dialog = new FileDialog(applicationWindow);
        dialog.setFilenameFilter(
                new FilenameFilter() {

                    @Override
                    public boolean accept(File dir, String name) 
                    {
                        return name.endsWith("."+fileExtension);
                    }
                 });

        dialog.setVisible(true);
        return dialog.getDirectory()+ Delimiters.SLASH + dialog.getFile();
    }
}
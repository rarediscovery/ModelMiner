package com.rarediscovery.services.logic;

import com.rarediscovery.services.filters.OldFilter;

import com.rarediscovery.services.model.Stream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComboBox;

import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;

public class TextReader {

	File inputFile , outputFile;
               
	Map<String , Stream> streamsMap;
        String[] streamIDDataModel ;
        JComboBox<String> streamComboBox;
        
    public TextReader(File f) 
    {
            inputFile = f;
            
            streamsMap = new HashMap<String,Stream>();
            streamIDDataModel = new String[]{};
            streamComboBox = new JComboBox(streamIDDataModel);
    }
        
     
    public Stream getStreamByID(String id)
    {
         return streamsMap.get(id);
    }
  

    /*
          Information about the Stream
    
    */
    private void printResult(List<Stream> streamCollection) 
        {
            
            boolean titlePrinted = false;
            for(Stream stream : streamCollection)
            {
                if (!titlePrinted)
                {
                   // stream.printAttributeNames();
                    titlePrinted = true;
                }
                stream.printAttributeData();
            }
	}

        public Map<String, Stream> getStreamsMap() {
            return streamsMap;
        }

	private void saveToFile() 
        {
            if (inputFile == null || ! inputFile.isFile()){
                return;
            }
            
            boolean titlePrinted = false;
	    outputFile = new File(inputFile.getAbsolutePath().replace(".pdf","-"+ Math.random()+".xls"));
	    FileWriter fileWriter = null;
	    try 
            {
		fileWriter = new FileWriter(outputFile);
		for(Stream stream : getStreamsMap().values())
                {
                    if (!titlePrinted)
                    {
                        fileWriter.write(stream.getParameters()+ Delimiters.NewLine);
                        titlePrinted = true;
                        continue;
                    }
				
                    fileWriter.write(stream.getAttributeData() + Delimiters.NewLine);
		}
	    } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
	    }finally
	    {
		if (fileWriter != null){
		   try {
			 fileWriter.flush();
			 fileWriter.close();
			} catch (IOException e) {}
		}
	    }
	}
	private static void print(String msg) {
		System.out.println(msg);
		
	}

        /**
         * Converts the PDF file given as a constructor to this 
         * object to ASCII String <br>
         * @return 
         */
	public String convertPDFToString()
        {
	
            // Validation
           if (inputFile == null || ! inputFile.isFile())
           {
                return "";
            }
           
           String text = "";
            
            try 
            {
                InputStream inputFileStream = new FileInputStream(inputFile);
		PDFParser pdfParser = new PDFParser(inputFileStream );
		
                Timer timer = new Timer().start();
                    pdfParser.parse();	
                timer.stop();
                
		print(" Parsed PDF in " + timer.timeElapsed() + " ms");
			
		COSDocument cosDocument = pdfParser.getDocument();
		PDDocument pdDocument = new PDDocument(cosDocument);
			
                int numberOfPages = pdDocument.getNumberOfPages();               
                print(" Total Number of pages : " + numberOfPages);
                
		PDFTextStripper pdfTextStripper = new PDFTextStripper();
		text = pdfTextStripper.getText(pdDocument);
                
		print (" ********************************************* " );
               
	    }catch (IOException e) 
            {
		e.printStackTrace();
	    }
		
	    return text;
	}
	        
        /**
         * 
         *   given(text).excludeFrom("start").to("end");
         *   given(text).includeFrom("start").to("end");
         * 
         * @param args 
         */
        
      
}

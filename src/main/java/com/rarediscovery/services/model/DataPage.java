/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rarediscovery.services.model;


import com.rarediscovery.services.filters.OldFilter;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 *
 * @author usaa_developer
 */
public class DataPage implements Comparable<DataPage>
{
        int id , startIndex = 1 , lastIndex = 1;

        final String text;
        final String NL = "\n";
        final String SPACE = " ";
        Map<Integer, String> withLineNumbers;
		
        /**
         * Constructor 
         * 
         * @param index
         * @param data 
         */
        public DataPage(int index,String data) 
        {
            if (id < 0 ) throw new IllegalArgumentException(" Id of a page must be positive ");

            id = index;
            text = data;
            withLineNumbers = new HashMap<>();
            
            toLineNumbers();
        }

        public String get(){
            return text;
        }

        public String get(OldFilter f)
        {
            if (f == null ){ return get(); }

            StringBuffer buffer = new StringBuffer();

            for(int i = startIndex; i <= lastIndex ; i++)
            {
               buffer.append(withLineNumbers.get(i) + NL);        
            }
            return buffer.toString();
        }

        public void print(){
               print(text);
        }

        public int Id() {
            return id;
        }

        private void toLineNumbers()
        {
            StringTokenizer tokenizer = new StringTokenizer(text,NL);
            int count = 0;
            while(tokenizer.hasMoreTokens())
            {
                withLineNumbers.put(count++, tokenizer.nextToken());
            }
            lastIndex = count;
            print (" Page Lines # " + lastIndex);
        }

        public int getPageLineNumbers() {
            return lastIndex;
        }




    /**
     * This method transforms the text line items to Stream information <br>
     * @return
     */
    public Stream[] getLineItemsAsStreamArray()
    {
        int detectableStreams = 4;
        String lineOne = "";
        StringTokenizer tokenizer = new StringTokenizer(text,NL);

        // There are 4 attributes , preceded by attribute description
        Stream[] streams = new Stream[detectableStreams];
        int index = 0;

        if (tokenizer.hasMoreElements()){

            lineOne = tokenizer.nextToken();
            String[] attributes = getAttributes(lineOne);

            // You may check that the attributeName is the same as expected here
            // String attributeName =  slice(attributes ,0,attributes.length -detectableStreams -1);

            for(int column=attributes.length -detectableStreams;column < attributes.length ; column++){
                    streams[index++] = new Stream(attributes[column].trim());
            }
        }


        while(tokenizer.hasMoreElements()){

            String record = tokenizer.nextToken();	

            if (record.contains("****")){
                    continue;
            }

            String[] attributes = getAttributes(record);

            //Skip invalid attribute
            if (attributes.length <= detectableStreams ){
                    continue;
            }

            String attributeName =  slice(attributes ,0,attributes.length -detectableStreams -1);

            int streamIndex=0;
            for(int column=attributes.length -detectableStreams;column < attributes.length; column++){
                    streams[streamIndex++].add(attributeName.trim() ,attributes[column].trim());
            }

        }
        return streams;
    }

        private String[] getAttributes(String record) {
                record = deflate(record);
                String[] attributes = record.split(" ");
                return attributes;
        }

        private String slice(String[] attributes, int i, int j) {
                StringBuffer sb = new StringBuffer();

                for(int k=i;k<=j;k++){
                     sb.append(attributes[k] + ' ');
                }
                return sb.toString();
        }



        private static void print(String msg) {
                System.out.println(msg);	
        }

        /**
         * Remove consecutive empty characters from a line item
         * 
         * @param data
         * @return Deflated string
         */
        private String deflate(String data)
        {
                char[] newData = new char[data.length()];
                int i =0;
                char lastSymbol = 0;

                for( char c : data.toCharArray())
                {
                    if ( lastSymbol == c && c == ' ' ){
                        continue;
                    }
                    newData[i++] = c;
                    lastSymbol = c;
                }

                return new String(newData);
        }

        @Override
        public int compareTo(DataPage t) {
           
            if ( this.Id() < t.Id()) return -1;
            if ( this.Id() > t.Id()) return 1;
            
            return 0;
	}
}

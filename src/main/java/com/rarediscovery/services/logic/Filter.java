/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rarediscovery.services.logic;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 *
 * @author usaa_developer
 */
public class Filter 
{
    int next = 0;
    
    String leftKey, rightKey;
    String input, output;    
    Map<Integer, Order> executionOrder;

    private static class Order 
    {
        Qualifier[] qualifiers;
        Operator method;
        public Order() {
        }

        public Order(Operator operator, Qualifier...q) {
            this.method = method;
            this.qualifiers =q;
        }

        public Operator getMethod() {
            return method;
        }
           
    }
       
    enum Operator{
       Excluding,
       Including,
       Slicing,
       ColumnDecomposition,
       ;
    }
    
    enum Qualifier
    {
        AnyStringThatBeginsWith,
        AnyStringThatContains,
        AnyStringThatEndsWith
        ;
        
        private String value;
        
        public Qualifier value(String v){
            value = v;
          return this;
        }
        
        public String getValue(){
          return value;
        }
    }
    
    public Filter(String newValue) 
    {
        input = newValue;
        executionOrder = new HashMap<>();
    }
       
    /**
     * Looks at a set of lines of text and ignores matching string values <br>
     * 
     * @param startingText 
     * @param endingText 
     * @return 
     */
    public Filter using(String startingText , String endingText)
    {
        leftKey = startingText;
        rightKey = endingText;
        
        return this;
    }

     public Filter using(String newValue){
        leftKey = newValue;
        return this;
    }
      
    public Filter by(Operator by , Qualifier...qualifiers)
    {
       executionOrder.put(next++, new Order(leftKey, rightKey, by));
       return this;
    }
    
    public Filter apply()
    {
        if (executionOrder.isEmpty()) 
        {
           return this;
        }
        
        // Get rawInput as the initial output
        String tempOut  = input, 
               tempIn;
        
        for(int i : executionOrder.keySet())
        {
            
            tempIn = tempOut; // Pipe output as new Input
           Order order = executionOrder.get(i);
           switch (order.method)
           {
               case Including:
                   tempOut = extract(tempIn,order);
                   break;
                   
               case Excluding:
                   tempOut = exclude(tempIn,order);
                   break;
                   
               case Slicing:
                   tempOut = split(tempIn,order);
                   break;
                   
               case ColumnDecomposition:
                   tempOut = decompose(tempIn,order);
                   break;
           }
        }
        
        output = tempOut;  // Send the last output to the caller
       return this;
    }
    
    private String extract(String in, Order order) 
    {
        System.out.println(" -- start extracting ......");
        
        StringTokenizer tokenizer = new StringTokenizer(in, Delimiters.NewLine);
                
        String lineItem = null;
        StringBuffer buffer = new StringBuffer();

        boolean collectNow = false;

        while(tokenizer.hasMoreTokens())
        {
           lineItem = tokenizer.nextToken(); 

           if ( lineItem != null 
                && ! collectNow
                && lineItem.contains(order.getStartsWith()))
           {
               collectNow = true;
           }

           if (collectNow)
           {
             buffer.append(lineItem + Delimiters.NewLine);
           }
           
           if (collectNow &&  lineItem.contains(order.getEndsWith()))
           {
               collectNow = false;
               continue;
           }
        }
                             
	return buffer.toString();
    }
    
    private void say(String...stuff)
    {
        for(String s: stuff){
                System.out.println(s);
        }
    }
    
     private String exclude(String in, Order order) 
     {
       say(" [Excluding] ",
           " Starting with " + order.getStartsWith(),
           " Ending with " + order.getEndsWith());
       
       StringTokenizer tokenizer = new StringTokenizer(in, Delimiters.NewLine);
                
        String lineItem = null;
        StringBuffer buffer = new StringBuffer();

        boolean ignoreNow = false;

        while(tokenizer.hasMoreTokens())
        {
           lineItem = tokenizer.nextToken(); 

           if ( lineItem != null 
                && ! ignoreNow
                && lineItem.startsWith(order.getStartsWith()))
           {
               ignoreNow = true;
           }

            if (! ignoreNow)
           {
            buffer.append(lineItem + Delimiters.NewLine);
           }
                        
           if (ignoreNow &&  lineItem.endsWith(order.getEndsWith()))
           {
               ignoreNow = false;
               continue;
           }
        }
                             
	return buffer.toString(); 
    }

    private String split(String in, Order order) 
    {
        say(" [Splitting]  " ,
            " Using " + order.getStartsWith());
        
        StringTokenizer tokenizer = new StringTokenizer(in, Delimiters.NewLine);
                
        String lineItem = null;
        StringBuffer buffer = new StringBuffer();
        StringBuffer bigBuffer = new StringBuffer();

        int matchId = 0;

        while(tokenizer.hasMoreTokens())
        {
           lineItem = tokenizer.nextToken(); 

           if ( lineItem != null 
                   && lineItem.contains(order.getStartsWith()))
            {
               matchId += 1;  // trigger clear buffer

               if (buffer.toString().length() > 0 )
               {
                bigBuffer.append(buffer.toString());
                buffer.setLength(0);  // clear buffer
               }

               continue; // Do not include the trigger
            }

           if ( buffer.toString().length() > 0 || matchId > 0 )
           {
               buffer.append(lineItem + Delimiters.NewLine);
               matchId = 0;  //reset
           }
        }
        return bigBuffer.toString();
    }
      
    private String decompose(String in, Order order) {
        
        say(" [ColumnDecomposition]  " ,
            " Start Marker " + order.getStartsWith(),
            " End Marker " + order.getEndsWith());
        
        StringTokenizer tokenizer = new StringTokenizer(in, Delimiters.NewLine);
                
        String lineItem = null;
        StringBuffer buffer = new StringBuffer();
        BucketList  bucketList = new BucketList();

        int matchId = 0;

        while(tokenizer.hasMoreTokens())
        {
           lineItem = tokenizer.nextToken(); 
           String[] header ;
           
           if ( lineItem != null 
                   && lineItem.contains(order.getStartsWith()))
            {
                // Make the trigger the column marker
                bucketList.add(deflate(lineItem));
            }
        }
        return bucketList.get().toString();
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
        char previousSymbol = ' ';

        for( char currentSymbol : data.toCharArray())
        {
            if ( previousSymbol == currentSymbol && currentSymbol == ' ' ){
                continue;
            }
            newData[i++] = currentSymbol;
            previousSymbol = currentSymbol;
        }

        return new String(newData);
    }
    
    
    public String getResult()
    {
        return deflate(output);
    }
}

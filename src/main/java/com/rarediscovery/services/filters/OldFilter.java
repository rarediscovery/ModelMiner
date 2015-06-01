/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rarediscovery.services.filters;

import com.rarediscovery.services.logic.BucketList;
import com.rarediscovery.services.logic.Delimiters;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 *
 * @author usaa_developer
 */
public class OldFilter 
{
    int next = 0;
 
    String input, output;    
    Map<Integer, Instruction> executionOrder;

    private static class Instruction 
    {
        Qualifier[] qualifiers;
        Operator method;
        
        public Instruction(Operator operator, Qualifier...q) 
        {
            this.method = operator;
            this.qualifiers =q;
        }

        public Operator getOperator() {
            return method;
        }

        public Qualifier[] getQualifiers() {
            return qualifiers;
        } 
        
        public boolean matches(String item){
            
           for (Qualifier qf : qualifiers)
           {
                if (Qualifier.AnyStringThatBeginsWith.equals(qf))
                {
                    return item.startsWith(qf.getValue());
                }
                
                if (Qualifier.AnyStringThatContains.equals(qf))
                {
                    return item.contains(qf.getValue());
                }
                
                if (Qualifier.AnyStringThatEndsWith.equals(qf))
                {
                    return item.endsWith(qf.getValue());
                }
            }
           
           return false;
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
    
    public OldFilter(String newValue) 
    {
        input = newValue;
        executionOrder = new HashMap<>();
    }
       
    public OldFilter by(Operator by , Qualifier...qualifiers)
    {
       executionOrder.put(next++, new Instruction(by,qualifiers));
       return this;
    }
    
    public OldFilter apply()
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
           Instruction instruction = executionOrder.get(i);
           switch (instruction.getOperator())
           {
               case Including:
                   tempOut = extract(tempIn,instruction);
                   break;
                   
               case Excluding:
                   tempOut = exclude(tempIn,instruction);
                   break;
                   
               case Slicing:
                   tempOut = split(tempIn,instruction);
                   break;
                   
               case ColumnDecomposition:
                   tempOut = decompose(tempIn,instruction);
                   break;
           }
        }
        
       output = tempOut;  // Send the last output to the caller
       return this;
    }
    
    private String extract(String in, Instruction order) 
    {
        System.out.println(" -- Start extracting ......");
        
        StringTokenizer tokenizer = new StringTokenizer(in, Delimiters.NewLine);
                
        String lineItem = null;
        StringBuffer buffer = new StringBuffer();

        boolean collectNow = false;

        while(tokenizer.hasMoreTokens())
        {
           lineItem = tokenizer.nextToken(); 

           if ( lineItem != null 
                && ! collectNow
                && order.matches(lineItem))
           {
               collectNow = true;
           }

           if (collectNow)
           {
             buffer.append(lineItem + Delimiters.NewLine);
           }
           
           if (collectNow &&  order.matches(lineItem))
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
    
     private String exclude(String in, Instruction order) 
     {
       say(" [Excluding] ");
       
       StringTokenizer tokenizer = new StringTokenizer(in, Delimiters.NewLine);
                
        String lineItem = null;
        StringBuffer buffer = new StringBuffer();

        boolean ignoreNow = false;

        while(tokenizer.hasMoreTokens())
        {
           lineItem = tokenizer.nextToken(); 

           if ( lineItem != null 
                && ! ignoreNow
                && order.matches(lineItem))
           {
               ignoreNow = true;
           }

            if (! ignoreNow)
           {
            buffer.append(lineItem + Delimiters.NewLine);
           }
                        
           if (ignoreNow &&  order.matches(lineItem))
           {
               ignoreNow = false;
               continue;
           }
        }
                             
	return buffer.toString(); 
    }

    private String split(String in, Instruction order) 
    {
        say(" [Splitting]" );
        
        StringTokenizer tokenizer = new StringTokenizer(in, Delimiters.NewLine);
                
        String lineItem = null;
        StringBuffer buffer = new StringBuffer();
        StringBuffer bigBuffer = new StringBuffer();

        int matchId = 0;

        while(tokenizer.hasMoreTokens())
        {
           lineItem = tokenizer.nextToken(); 

           if ( lineItem != null 
                   && order.matches(lineItem))
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
      
    private String decompose(String in, Instruction order) {
        
        say(" [ColumnDecomposition]  ");
        
        StringTokenizer tokenizer = new StringTokenizer(in, Delimiters.NewLine);
                
        String lineItem = null;
        StringBuffer buffer = new StringBuffer();
        BucketList  bucketList = new BucketList();


        while(tokenizer.hasMoreTokens())
        {
           lineItem = tokenizer.nextToken(); 
           
           if ( lineItem != null 
                   && order.matches(lineItem))
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

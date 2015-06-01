
package com.rarediscovery.services.filters;

import static com.rarediscovery.services.filters.Filter.FilterState.Continue;
import static com.rarediscovery.services.filters.Filter.FilterState.Matched;
import static com.rarediscovery.services.filters.Filter.FilterState.NoMatchFound;
import com.rarediscovery.services.filters.Word.MatchResult;
import static com.rarediscovery.services.filters.Word.SPACE;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 *
 * This class takes a text and the start and end that to split in the text <br>
 * It returns the filtered text <br>
 * 
 * @author usaa_developer
 */
public class StringFilter implements Filter
{
    String originalMessage,
           startTag , endTag;
    
    public StringFilter(String start, String end) 
    {
        buffer = new StringBuffer();
                      
        this.startTag = start;
        this.endTag = end;
        
        currentState = NoMatchFound;
    }
    
    public String select(String msg)
    {
        String[] multiLineText = msg.split("\n"); 
        
        int inputDataLength = multiLineText.length;
        log(" *** Input Data length = " + inputDataLength);
        
        List<Integer> startTagIndex = new ArrayList<>();
        List<Integer> endTagIndex = new ArrayList<>();
        int j=0;
        
        for(String i: multiLineText)
        {
            j++;
                     
            if (i.contains(startTag))
            {
               //echo(i);
               startTagIndex.add(j);
            }
            
            if (i.endsWith(endTag))
            {
               //echo(i);
               endTagIndex.add(j);
            }
        }
        
        log(String.format(" Matched Items : Start Tags = %d ; End Tags = %d ", startTagIndex.size(), endTagIndex.size()));
        
        SortedMap<Integer,Integer> keysToSelect = createRangeMapFromLists(startTagIndex, endTagIndex);
        
        log(" -- Map of line items to select -- ");
        log(keysToSelect.toString());
        log(" ----------------------------------- ");
        
        // Select items fromInputString input data
        StringBuffer finalResult = new  StringBuffer();
       int currentKey = 0; 
       for(Integer jumpKey : keysToSelect.keySet())
       {
           currentKey = jumpKey;
           while(currentKey <= keysToSelect.get(jumpKey))
           {
             finalResult.append(multiLineText[currentKey] + NL);
             currentKey++;
           }
       }
              
        return finalResult.toString();
    }
    
    public String removeLine(String msg)
    {
         String[] multiLineText = msg.split("\n");
          
         // Remove items fromInputString input data
         StringBuffer finalResult = new  StringBuffer();
        
          for(String i: multiLineText)
          {
              if (i.startsWith(startTag))
              {
                 continue;
              }
              finalResult.append(i + NL);
          }
          return finalResult.toString();
    }
    
    
    /**
     * Split the text into multi-line text <br>
     * 
     * @param rawInputText
     * @return  filtered Text
     */
    public String fromInputString(String rawInputText)
    {
        if (rawInputText == null) {return null;}
        
        originalMessage = rawInputText; 
        
        if (startTag == null && endTag == null) { return originalMessage;}
        
        String[] multiLineText = rawInputText.split("\n"); 
        int inputDataLength = multiLineText.length;
        log(" *** Input Data length = " + inputDataLength);
        
        List<Integer> startTagIndex = new ArrayList<>();
        List<Integer> endTagIndex = new ArrayList<>();
        int j=0;
        
        for(String i: multiLineText)
        {
            j++;
                     
            if (i.startsWith(startTag))
            {
               //echo(i);
               startTagIndex.add(j);
            }
            
            if (i.endsWith(endTag))
            {
               //echo(i);
               endTagIndex.add(j);
            }
        }
        
        log(String.format(" Matched Items : Start Tags = %d ; End Tags = %d ", startTagIndex.size(), endTagIndex.size()));
        
        SortedMap<Integer,Integer> keysToRemove = createRangeMapFromLists(startTagIndex, endTagIndex);
        
        log(" -- Map of line items to exclude -- ");
        log(keysToRemove.toString());
        log(" ----------------------------------- ");
        
        // Remove items fromInputString input data
        StringBuffer finalResult = new  StringBuffer();
        
        Iterator<Integer> ks = keysToRemove.keySet().iterator();
        int  jumpKey = ks.next();
        int currentKey = 0;
        
        while(++currentKey < inputDataLength)       // Get the first jump key
        {
            // Whenever there is a matching line number with the range to be excluded
            if (currentKey == jumpKey)
            {
                // Skip to the end of this range to add to buffer 
                currentKey = keysToRemove.get(jumpKey)+1;
                
                if (ks.hasNext())
                {
                  jumpKey = ks.next();
                }
               
               continue;
            }
            
            finalResult.append(multiLineText[currentKey] + NL);
        }
              
        return finalResult.toString();
    }

    protected SortedMap<Integer, Integer> createRangeMapFromLists(List<Integer> startTagIndex, List<Integer> endTagIndex) {
        Map<Integer,Integer> temp = new HashMap<Integer, Integer>();
        int
                leftIndex = 0 ,
                totalStartTagsFound = startTagIndex.size(),
                totalEndTagsFound = endTagIndex.size();
        for(int rightIndex=0;rightIndex<totalEndTagsFound;rightIndex++)
        {
            while( leftIndex < totalStartTagsFound
                    && startTagIndex.get(leftIndex) < endTagIndex.get(rightIndex))
            {
                //System.out.println(String.format("[%d - %d]",startTagIndex.get(leftIndex) , endTagIndex.get(rightIndex)));
                temp.put(endTagIndex.get(rightIndex),startTagIndex.get(leftIndex));
                leftIndex++;
            }            
        }
        SortedMap<Integer,Integer>  keysToRemove = new TreeMap<Integer, Integer>();
        for(Integer i : temp.keySet())
        {
            keysToRemove.put(temp.get(i), i);
        }
        return keysToRemove;
    }
    
    @Override
    public FilterState filterAll(List<String> allText) {
        return null;
    }
       
    
    FilterState currentState;
    StringBuffer buffer ;
    
    private String NL = "\n";
     
    
    
    public FilterState filter(String inputText)
    {               
       // Case 1
       if (inputText == null)  
       {
          return NoMatchFound;
       }
            
       MatchResult beginMatchResult = null;
       beginMatchResult = new Word(inputText).split(startTag);
          
       // Nothing was matched yet
       if ( beginMatchResult.noMatchWasFound() && currentState.is(NoMatchFound))
       {
           buffer.append(inputText + NL);
           currentState = NoMatchFound;
           return currentState;
       }
       
       // Left was matched
       if ( beginMatchResult.isMatchFound() && currentState.is(Continue))
       {
           buffer.append(inputText + NL);
           currentState = Continue;
           return currentState;
       }
       
       // Left has been matched
       MatchResult endMatchResult = new Word(beginMatchResult.getRight()).split(endTag);
       if ( endMatchResult.noMatchWasFound() && currentState.is(NoMatchFound) )
       {
            buffer.append(beginMatchResult.getLeft()+ NL);
            currentState =  Continue;
       }
       
       // Left and Right are matched in a line
       if ( endMatchResult.isMatchFound() && currentState.is(NoMatchFound) )
       {
           buffer.append(beginMatchResult.getLeft());
           buffer.append(endMatchResult.getRight()+ NL);
           currentState =  Matched;
       }
       
       // Left and Right are matched across lines
       if ( endMatchResult.isMatchFound() && currentState.is(Continue) )
       {
           buffer.append(beginMatchResult.getLeft());
           buffer.append(endMatchResult.getRight()+ NL);
           currentState =  Matched;
       }
       return currentState;      

    }

    @Override
    public String filteredText() 
    {
        return buffer.toString();
    }
    

    
    public void showCompleteResult()
    {
        log("*********************************************");
        log("************ ORIGINAL MESSAGE ****************");
        log("*********************************************");
        log("**  FILTER WORDS MATCHING : "+ startTag + " - "+ endTag);
        log("*********************************************");
        log(originalMessage.toString());
        log("*********************************************");
        log("************* FILTERED MESSAGE **************");
        log("*********************************************");
        log(filteredText());
        
    }
    
    private static void log(String s)
    {
        System.out.println(s);
    }

    private void echo(String i) {
        System.out.print(i);
    }
}
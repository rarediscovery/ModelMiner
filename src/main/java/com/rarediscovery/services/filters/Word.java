/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rarediscovery.services.filters;

/**
 *
 * @author usaa_developer
 */
public class Word {
    protected static final String SPACE = " ";
    String value;

    public Word(String value) {
        this.value = value;
    }

    public MatchResult split(String key) {
        String[] items = value.split(SPACE);
        StringBuffer leBuffer = new StringBuffer();
        
        for (String item : items) {
            if (item.equalsIgnoreCase(key)) {
                String lft = leBuffer.toString() + SPACE + key;
                return new MatchResult(leBuffer.toString(), key, value.substring(lft.length())).matchFound();
            }
            leBuffer.append(item + SPACE);
        }
        return new MatchResult(value, "", "");
    }
    
    public class MatchResult 
    {

        String left;
        String key;
        String right;

        boolean matchFound;

        public MatchResult(String left, String key, String right) 
        {
            this.left = left;
            this.key = key;
            this.right = right;
            matchFound = false;
        }

        public String getLeft() {
            return left;
        }

        public String getRight() {
            return right;
        }

        public String getKey() {
            return key;
        }

        public MatchResult matchFound() {
            matchFound = true;
            return this;
        }

        public boolean isMatchFound() {
            return matchFound;
        }

        public boolean noMatchWasFound() {
            return !matchFound;
        }

    }
}

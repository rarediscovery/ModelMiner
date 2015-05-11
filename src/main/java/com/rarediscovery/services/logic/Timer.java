/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rarediscovery.services.logic;

/**
 *
 * @author usaa_developer
 */
class Timer {
    long t, elapsedTime;
    

    public Timer start() {
        t = System.currentTimeMillis();
        return this;
    }

    public long stop() {
        elapsedTime =  System.currentTimeMillis() - t;
        return elapsedTime;
    }

    public long timeElapsed() {
        return elapsedTime;
    }
    
}

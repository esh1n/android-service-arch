/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.evilduck.framework.armedthreadpool;

import java.util.concurrent.Callable;

/**
 *
 * @author sergey
 */

public class ConcreteBadcommand implements Callable<String>{
    private  String id;

    public ConcreteBadcommand(String id){
        this.id=id;
    }
    @Override
    public String call() throws Exception {
        Thread.sleep(1000);
        System.out.println("Callable with id + " + id + " worked");
        throw new NullPointerException("Pizdec");
    }
    
}

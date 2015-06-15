package com.wei.lock;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by weiqisong on 2015/6/15.
 */
public class Test {

    public static void main(String[] args) throws InterruptedException {

        Executor executor = Executors.newFixedThreadPool(10);

        Lock lock = new MCSLock();
        for(int i=0;i<10;i++){
            executor.execute(()->{
                for(int j=0;j<100;j++){
                    lock.lock();
                    System.out.println(Thread.currentThread().getId()+"--");
                    lock.unlock();
                }
            });
        }

        Thread.sleep(10000*10L);
    }


}

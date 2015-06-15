package com.wei.lock;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by weiqisong on 2015/6/15.
 * 该算法主要是通过比较ticket和servingNum是否相等来判断是否可以获取锁。
 *
 */
public final class TicketLock {
    // 起始值是不一样的，需要注意
    private AtomicInteger ticket = new AtomicInteger(2);
    private AtomicInteger servingNum = new AtomicInteger(1);


    public int lock() {

        int num = servingNum.incrementAndGet();

        while (num != ticket.get()) {

        }
        return num;

    }

    public void unlock(int num) {
        if (ticket.get() == num) {
            ticket.incrementAndGet();
            return;
        }
        throw new IllegalArgumentException(String.format("error serving number [%s] ", num));
    }


}

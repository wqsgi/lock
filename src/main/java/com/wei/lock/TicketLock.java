package com.wei.lock;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by weiqisong on 2015/6/15.
 * ���㷨��Ҫ��ͨ���Ƚ�ticket��servingNum�Ƿ�������ж��Ƿ���Ի�ȡ����
 *
 */
public final class TicketLock {
    // ��ʼֵ�ǲ�һ���ģ���Ҫע��
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

package com.wei.lock;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by weiqisong on 2015/6/15.
 */
public class MCSLock implements Lock {

    private AtomicReference<Node> tail = new AtomicReference<>();
    private ThreadLocal<Node> myNode = new ThreadLocal<Node>() {
        @Override
        protected Node initialValue() {
            return new Node();
        }
    };

    @Override
    public void lock() {

        Node node = myNode.get();
        Node preNode = tail.getAndSet(node);
        if(preNode == null){
            return;
        }

        node.isLock = true;
        preNode.next = node;
        while(node.isLock){}
    }

    @Override
    public void unlock() {
        Node node = myNode.get();
        Node next = node.next;
        if(next == null){
            if(tail.compareAndSet(node,null)){
                return;
            }

            // 等待下个节点连上，在下面的操作中设置isLock为false，下个节点可以获取到锁。
            while(node.next==null){}
        }

        next.isLock = false;
        node.next = null;
    }

    private class Node {
        private volatile boolean isLock = true;
        private Node next = null;
    }


}

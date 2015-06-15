package com.wei.lock;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by weiqisong on 2015/6/15.
 * CLH算法将多个线程的自旋分散在不同的节点进行，当一个线程释放锁时，只会将其后继结点的cache失效掉，对于其他线程没有任何影响，其他线程还是在各自的cache中进行局部自旋。这样，就大大减少了bus上的交通阻塞，不会导致锁的释放被延迟。
 另外，CLH算法由于总是将最近一个申请锁的线程放在链表的最后，从而带来了一个附加的功能，那就是对锁竞争的公平性，保证了先申请锁的线程先得到锁。
 而且，CHL算法也是空间有效的，对于N个线程，L个锁，如果每个线程每次最多只能获取一个锁，则需要的存储空间是O（N+L），其中N个线程对应N个myNode，L个锁对应L个tail。
 当然，CLH算法也有一个缺点，在cache-less的NUMA架构体系下，因为每个线程自旋的是其前驱线程的QNode中的locked域，如果内存位置比较远，则性能是要打折扣的。
 */
public class CLHLock implements Lock {

    private AtomicReference<Node> tail = new AtomicReference<>();
    private ThreadLocal<Node> myNodeLocal = new ThreadLocal<Node>(){
        @Override
        protected Node initialValue() {
            return new Node();
        }
    };

    private ThreadLocal<Node> preNodeLocal = new ThreadLocal<>();
    private class Node{
        private volatile boolean isLock = true;
    }
    @Override
    public void lock() {
        Node node = myNodeLocal.get();
        node.isLock = true;
        // 置换尾节点为当前节点，之前的尾节点释放锁后，当前的节点可以退出自旋。
        Node preNode = tail.getAndSet(node);
        if(preNode==null){
            return;
        }
        preNodeLocal.set(preNode);
        while(preNode.isLock){
        }
    }

    @Override
    public void unlock() {
        // 获取当前节点，置为解锁。当前节点的下一个节点将获取锁
        Node node = myNodeLocal.get();
        node.isLock=false;

        myNodeLocal.set(preNodeLocal.get()==null?new Node():preNodeLocal.get());
    }
}

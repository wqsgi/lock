package com.wei.lock;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by weiqisong on 2015/6/15.
 * CLH�㷨������̵߳�������ɢ�ڲ�ͬ�Ľڵ���У���һ���߳��ͷ���ʱ��ֻ�Ὣ���̽���cacheʧЧ�������������߳�û���κ�Ӱ�죬�����̻߳����ڸ��Ե�cache�н��оֲ��������������ʹ�������bus�ϵĽ�ͨ���������ᵼ�������ͷű��ӳ١�
 ���⣬CLH�㷨�������ǽ����һ�����������̷߳����������󣬴Ӷ�������һ�����ӵĹ��ܣ��Ǿ��Ƕ��������Ĺ�ƽ�ԣ���֤�������������߳��ȵõ�����
 ���ң�CHL�㷨Ҳ�ǿռ���Ч�ģ�����N���̣߳�L���������ÿ���߳�ÿ�����ֻ�ܻ�ȡһ����������Ҫ�Ĵ洢�ռ���O��N+L��������N���̶߳�ӦN��myNode��L������ӦL��tail��
 ��Ȼ��CLH�㷨Ҳ��һ��ȱ�㣬��cache-less��NUMA�ܹ���ϵ�£���Ϊÿ���߳�����������ǰ���̵߳�QNode�е�locked������ڴ�λ�ñȽ�Զ����������Ҫ���ۿ۵ġ�
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
        // �û�β�ڵ�Ϊ��ǰ�ڵ㣬֮ǰ��β�ڵ��ͷ����󣬵�ǰ�Ľڵ�����˳�������
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
        // ��ȡ��ǰ�ڵ㣬��Ϊ��������ǰ�ڵ����һ���ڵ㽫��ȡ��
        Node node = myNodeLocal.get();
        node.isLock=false;

        myNodeLocal.set(preNodeLocal.get()==null?new Node():preNodeLocal.get());
    }
}

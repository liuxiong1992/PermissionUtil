package com.lx.permission;

/**
 * 方法说明：请求队列
 * create by liuxiong at 2021/2/3 13:50.
 */
public class RequestQueue {

    /** 第一个节点 */
    RequestBean firstNode;


    public void add(RequestBean requestBean ){
        if(firstNode==null){
            firstNode=requestBean;
        }else{
            RequestBean temp=firstNode;
            while (true){
                if(temp.nextNode==null){
                    temp.nextNode=requestBean;
                    break;
                }

                temp=temp.nextNode;
            }
        }
    }
}

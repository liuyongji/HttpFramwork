/*
 * 文件名：Request.java
 * 创建人：王玉丰
 * 创建时间：2012-12-24
 * 版     权：Copyright Easier Digital Tech. Co. Ltd. All Rights Reserved.
 */
package com.lyj.framwork.logic;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 业务请求<BR>
 * TODO 后续可考虑封装成工厂方式创建
 * 
 * @author 王玉丰
 * @version [Transfer, 2012-12-24] 
 */
public class Request {
    /**
     * 请求ID生成器（自增长）
     */
    private static AtomicLong sIdGenerator = new AtomicLong(0);

    /**
     * 请求ID
     */
    private long requestId;
    
    /**
     * 执行的具体业务ID
     */
    private int actionId;
    
    /**
     * 是否取消该请求
     */
    private volatile boolean cancel;
    
    /**
     * 请求的附属数据（参数）
     */
    private Object data;

    /**
     * 处理结果回调监听器<BR>
     * 每个Request都基本需要一个ResponseListener, 所以增加此属性<BR>
     * 而CancelListener不是所有Request都需要, 所以不纳入属性
     */
    private ResponseListener responseListener;
    
    /**
     * 构造函数
     */
    public Request() {
        requestId = sIdGenerator.incrementAndGet();
    }

    /**
     * 获取请求ID
     * @return requestId 请求ID
     */
    public long getRequestId() {
        return requestId;
    }

    /**
     * 获取执行的具体业务ID
     * @return actionId 执行的具体业务ID
     */
    public int getActionId() {
        return actionId;
    }

    /**
     * 设置执行的具体业务ID
     * @param actionId 执行的具体业务ID
     */
    public void setActionId(int actionId) {
        this.actionId = actionId;
    }

    /**
     * 判断是否取消该请求
     * @return cancel 是否取消该请求
     */
    public boolean isCancel() {
        return cancel;
    }

    /**
     * 设置是否取消该请求
     * @param cancel 是否取消该请求
     */
    public void setCancel(boolean cancel) {
        this.cancel = cancel;
    }

    /**
     * 获取请求的附属数据（参数）
     * @return data 请求的附属数据（参数）
     */
    public Object getData() {
        return data;
    }

    /**
     * 设置请求的附属数据（参数）
     * @param data 请求的附属数据（参数）
     */
    public void setData(Object data) {
        this.data = data;
    }

    /**
     * 获取处理结果回调监听器
     * @return responseListener 处理结果回调监听器
     */
    public ResponseListener getResponseListener() {
        return responseListener;
    }

    /**
     * 设置处理结果回调监听器
     * @param responseListener 处理结果回调监听器
     */
    public void setResponseListener(ResponseListener responseListener) {
        this.responseListener = responseListener;
    }
    
}

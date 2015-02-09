/*
 * 文件名：Processor.java
 * 创建人：王玉丰
 * 创建时间：2012-12-24
 * 版     权：Copyright Easier Digital Tech. Co. Ltd. All Rights Reserved.
 */
package com.lyj.framwork.logic;


/**
 * 请求处理器<BR>
 * 
 * @author 王玉丰
 * @version [Transfer, 2012-12-24] 
 */
public interface Processor {
    /**
     * 处理请求
     * @param request 请求
     */
    void process(Request request);
    
    /**
     * 取消请求
     * @param request 请求
     * @param cancelListener 取消结果回调
     */
    void cancel(Request request, CancelListener cancelListener);
}

/*
 * 文件名：CancelListener.java
 * 创建人：王玉丰
 * 创建时间：2012-12-25
 * 版     权：Copyright Easier Digital Tech. Co. Ltd. All Rights Reserved.
 */
package com.lyj.framwork.logic;

/**
 * 取消请求的回调监听
 * @author 王玉丰
 * @version [Transfer, 2012-12-25] 
 */
public interface CancelListener {
    /**
     * 取消结果
     * @param request 欲取消的请求
     * @param isCancel 是否取消成功
     */
    void onCancelResult(Request request, boolean isCancel);
}

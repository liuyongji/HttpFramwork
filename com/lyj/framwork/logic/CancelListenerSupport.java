/*
 * 文件名：CancelListenerSupport.java
 * 创建人：王玉丰
 * 创建时间：2012-12-26
 * 版     权：Copyright Easier Digital Tech. Co. Ltd. All Rights Reserved.
 */
package com.lyj.framwork.logic;

import java.util.concurrent.ConcurrentHashMap;

/**
 * CancelListener的异步回调管理器
 * @author 王玉丰
 * @version [Transfer, 2012-12-26] 
 */
public class CancelListenerSupport {
    
    /**
     * CancelListenerSupport单例
     */
    private static volatile CancelListenerSupport sInstance;
    
    /**
     * 需要取消的请求集合
     */
    private ConcurrentHashMap<Request, CancelListener> mCancelMap;
    
    /**
     * 取消结果集合
     */
    private ConcurrentHashMap<Request, Boolean> mCancelResultMap;
    
    /**
     * 默认只能私有构造
     */
    private CancelListenerSupport() {
        
    }
    
    /**
     * 获取CancelListenerSupport单例
     * @return CancelListenerSupport单例
     */
    public static CancelListenerSupport getInstance() {
        if (sInstance == null) {
            synchronized (CancelListenerSupport.class) {
                if (sInstance == null) {
                    sInstance = new CancelListenerSupport();
                }
            }
        }
        return sInstance;
    }
    
    /**
     * 添加需要管理的CancelListener
     * @param request 请求
     * @param cancelListener 取消结果回调
     */
    public void addCancelListener(Request request, CancelListener cancelListener) {
        request.setCancel(true);
        if (mCancelMap == null) {
            mCancelMap = new ConcurrentHashMap<Request, CancelListener>();
        }
        if (mCancelResultMap == null) {
            mCancelResultMap = new ConcurrentHashMap<Request, Boolean>();
        }
        mCancelMap.put(request, cancelListener);
        mCancelResultMap.put(request, Boolean.FALSE);
    }
    
    /**
     * 移除相应请求的取消监听器
     * @param request 请求
     */
    public void removeCancelListener(Request request) {
        if (mCancelMap != null && mCancelMap.get(request) != null) {
            mCancelMap.remove(request);
            mCancelResultMap.remove(request);
        }
    }

    /**
     * 获取相应的请求取消监听器
     * @param request 请求
     * @return 监听器
     */
    public CancelListener getCancelListener(Request request) {
        if (mCancelMap != null) {
            return mCancelMap.get(request);
        }
        
        return null;
    }
    
    /**
     * 设置该请求取消成功
     * @param request 请求
     */
    public final void putCancelSuccess(Request request) {
        mCancelResultMap.put(request, Boolean.TRUE);
    }
    
    /**
     * 获取该请求取消状态
     */
    public final Boolean getCancelStatus(Request request) {
        return mCancelResultMap.get(request);
    }

    /**
     * 发出CancelResult回调
     * @param request 请求
     * @return 是否取消成功: true - 取消成功; false - 取消失败
     */
    public Boolean fireCancelResult(Request request) {
        Boolean isCancel = false;
        
        CancelListener cancelList = getCancelListener(request);
        if (cancelList != null) {
            isCancel = getCancelStatus(request);
            cancelList.onCancelResult(request, isCancel);
            removeCancelListener(request);
        }
        
        return isCancel;
    }
}

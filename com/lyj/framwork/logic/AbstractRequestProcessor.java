/*
 * 文件名：AbstractRequestProcessor.java
 * 创建人：王玉丰
 * 创建时间：2012-12-25
 * 版     权：Copyright Easier Digital Tech. Co. Ltd. All Rights Reserved.
 */
package com.lyj.framwork.logic;

import java.util.concurrent.Executor;

/**
 * 请求处理器<BR>
 * 将请求都提交到线程池中去执行
 * 
 * @author 王玉丰
 * @version [Transfer, 2012-12-25] 
 */
public abstract class AbstractRequestProcessor implements Processor {    
    
    /**
     * CancelListener管理器
     */
    protected final CancelListenerSupport mCancelListeners;
    
    /**
     * 线程池
     */
    private final Executor mExecutor;
    
    /**
     * 构造函数
     * @param executor 线程池
     */
    protected AbstractRequestProcessor(Executor executor) {
        if (executor == null) {
            throw new IllegalArgumentException("Executor is null");
        }
        mExecutor = executor;
        mCancelListeners = CancelListenerSupport.getInstance();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void process(Request request) {
        try {
            Runnable runnable = getProcessRunnable(request);
            mExecutor.execute(runnable);            
        } catch(Exception e) {
            
        }
    }
    
    /**
     * 将相应请求的取消结果监听器放置Map中<BR>
     * {@inheritDoc}
     */
    @Override
    public void cancel(Request request, CancelListener cancelListener) {
        mCancelListeners.addCancelListener(request, cancelListener);
    }
    
    /**
     * 获取用于线程执行的Runable<BR>
     * Runnable中自行控制Request的中断操作(检查cancel属性)<BR>
     * 若中断成功调用mCancelListeners.putCancelSuccess(request)方法将取消状态置为成功
     * 
     * @param request 请求
     * @return 返回执行的Runable
     */
    protected abstract Runnable getProcessRunnable(Request request);
    
    /**
     * 发出Result回调
     * @param request 请求
     * @param response 相应的响应结果
     */
    protected void fireResult(Request request, Response response) {
        boolean isCancel = mCancelListeners.fireCancelResult(request);
        
        // 未取消或未取消成功, 则回调ResponseListener
        if (!isCancel) {
            ResponseListener respListener = request.getResponseListener();
            if (respListener != null) {
                respListener.onProcessResult(request, response);
            }
        }
    }
}

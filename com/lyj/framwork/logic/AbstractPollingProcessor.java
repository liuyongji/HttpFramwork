/*
 * 文件名：AbstractPollingProcessor.java
 * 创建人：王玉丰
 * 创建时间：2012-12-24
 * 版     权：Copyright Easier Digital Tech. Co. Ltd. All Rights Reserved.
 */
package com.lyj.framwork.logic;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicReference;

import com.lyj.framwork.log.Logger;

/**
 * 队列请求处理器<BR>
 * 请求执行顺序: 遵循FIFO原则处理请求
 * 
 * @author 王玉丰
 * @version [Transfer, 2012-12-24] 
 */
public abstract class AbstractPollingProcessor implements Processor {
    /**
     * AbstractPollingProcessor TAG
     */
    private static final String TAG = "AbstractPollingProcessor";
    
    /**
     * CancelListener管理器
     */
    protected final CancelListenerSupport mCancelListeners;
    
    /**
     * 线程池
     */
    private final Executor mExecutor;

    /**
     * 请求执行列表
     */
    private final ConcurrentLinkedQueue<Request> mRequestQueue = new ConcurrentLinkedQueue<Request>();
    
    /**
     * 负责处理传入的请求
     */
    private final AtomicReference<Processor> mProcessorRef = new AtomicReference<Processor>();
    
    /**
     * 最后的空闲时间
     */
    private long mLastIdleCheckTime;
    
    /**
     * 允许线程空闲时间范围, 超过这个时间将停止轮循
     */
    private long mIdleCheckTime;
    
    /**
     * 构造函数
     * @param executor 线程池
     */
    protected AbstractPollingProcessor(Executor executor) {
        this(executor, 0L);
    }
    
    /**
     * 构造函数<BR>
     * TODO 需支持设置idleCheckTime
     * @param executor 线程池
     * @param idleCheckTime 线程空闲时间的允许值, 超过这个时间将停止轮循线程的执行
     */
    protected AbstractPollingProcessor(Executor executor, long idleCheckTime) {
        if (executor == null) {
            throw new IllegalArgumentException("Executor is null");
        }
        mExecutor = executor;
        mIdleCheckTime = idleCheckTime;
        mCancelListeners = CancelListenerSupport.getInstance();
    }

    /**
     * 将请求加入到队列<BR>
     * {@inheritDoc}
     */
    @Override
    public void process(Request request) {
        mRequestQueue.add(request);
        startupProcessor();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void cancel(Request request, CancelListener cancelListener) {
        request.setCancel(true);
        if (mRequestQueue.contains(request)) {
            if (mRequestQueue.remove(request)) {
                cancelListener.onCancelResult(request, true);
            }
        } else {
            // 异步方式处理
            mCancelListeners.addCancelListener(request, cancelListener);
        }
    }
    
    /**
     * 在线程中执行真正的处理过程<BR>
     * 将结果填充至Response, 并自行控制Request的中断操作(检查cancel属性)<BR>
     * 若中断成功调用mCancelListeners.putCancelSuccess(request)方法将取消状态置为成功
     * 
     * @param request 请求
     * @param response 填充返回给界面的Response
     */
    protected abstract boolean processInRunnable(Request request, Response response);
    
    /**
     * 启动处理器线程
     */
    private void startupProcessor() {
        Processor processor = mProcessorRef.get();
        if (processor == null) {
            processor = new Processor();

            if (mProcessorRef.compareAndSet(null, processor)) {
                mExecutor.execute(processor);
            }
        }
    }
    
    /**
     * 停止执行请求
     */
    public void stop() {
        Processor processor = mProcessorRef.get();

        if (processor != null) {
            processor.quit();
        }
    }
    
    /**
     * 处理新加入的请求
     * @return 返回处理完毕的数据
     */
    private int handleRequests() {
        int handleCount = 0;
        for (Request request = mRequestQueue.poll(); request != null; request = mRequestQueue.poll()) {
            Response response = new Response();
            try {
                processInRunnable(request, response);
                handleCount++;
            } catch(Exception e) {
                Logger.e(TAG, "processInRunnable exception", e);
            } finally {
                fireResult(request, response);
            }
        }
        
        return handleCount;
    }
    
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
    
    /**
     * 处理请求的Runnable
     * @author 王玉丰
     * @version [Transfer, 2012-12-24]
     */
    private class Processor implements Runnable {
        /**
         * 是否退出
         */
        private volatile boolean mQuit;
        
        /**
         * {@inheritDoc}
         */
        @Override
        public void run() {
            mLastIdleCheckTime = System.currentTimeMillis();
            long currentTime = 0L;
            int nRequest = 0;
            while (!mQuit) {
                try {
                    nRequest = handleRequests();

                    currentTime = System.currentTimeMillis();
                    // 超过一定空闲时间, 退出循环
                    if (nRequest == 0 && mIdleCheckTime > 0) {
                        if (currentTime - mLastIdleCheckTime > mIdleCheckTime) {
                            mQuit = true;
                            break;
                        }
                    } else if(nRequest > 0) {
                        mLastIdleCheckTime = currentTime;
                    }
                } catch (Exception e) {
                    Logger.e(TAG, "Processor run exception", e);
                    mQuit = true;
                    break;
                }
            }
            
            // 线程运行完毕, 重置null
            mProcessorRef.set(null);
        }
        
        /**
         * 停止执行所有请求
         */
        private void quit() {
            mQuit = true;
        }
    }
}

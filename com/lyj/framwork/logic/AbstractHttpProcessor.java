/*
 * 文件名：AbstractHttpProcessor.java
 * 创建人：王玉丰
 * 创建时间：2012-12-26
 * 版     权：Copyright Easier Digital Tech. Co. Ltd. All Rights Reserved.
 */
package com.lyj.framwork.logic;

import java.io.IOException;
import java.net.ConnectException;
import java.util.concurrent.Executor;

import org.apache.http.HttpResponse;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.params.HttpConnectionParams;

import android.net.http.AndroidHttpClient;

import com.lyj.framwork.http.AbstractHttpConnector;
import com.lyj.framwork.log.Logger;
import com.lyj.framwork.logic.HttpAction.ActionResultCode;

/**
 * 支持Http请求的处理器<BR>
 * 封装Http请求并提交到线程池中去执行
 * 
 * @author 王玉丰
 * @version [Transfer, 2012-12-26] 
 */
public abstract class AbstractHttpProcessor extends AbstractHttpConnector implements Processor {    
    /**
     * UserAgent
     */
    private static final String USER_AGENT = "android";
    
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
    protected AbstractHttpProcessor(Executor executor) {
        if (executor == null) {
            throw new IllegalArgumentException("Executor is null");
        }
        mExecutor = executor;
        mCancelListeners = CancelListenerSupport.getInstance();
    }

    /**
     * 获取用于线程执行的Runable<BR>
     * 直接构建网络连接运行的Runnable, 默认认为Request中的actionId就是Http请求ID
     * 
     * @param request 请求
     * @return 返回执行的Runable
     */
    public Runnable getProcessRunnable(final Request request) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                // 默认处理就只有构建网络连接, 不同的具体实现可重写getProcessRunnable方法
                connect(request);
            }
        };
        
        return runnable;
    }
    
    /**
     * 发出连接, 并处理Response逻辑<BR>
     * 前提是Request中的actionId就是httpRequestId, 即Action中只执行单一动作的网络连接
     * @param request 请求
     */
    protected void connect(Request request) {
        Response response = new Response();
        AndroidHttpClient httpClient = null;
        HttpResponse httpResp = null;
        try {
            httpClient = (AndroidHttpClient) getHttpClient();
            //商协汇项目修改，等待数据的超时时间设为3分钟
            HttpConnectionParams.setSoTimeout(httpClient.getParams(), 3 * 60 * 1000);
            if (!request.isCancel()) {
                httpResp = connect(httpClient, request.getActionId(), request);
                // 读取处理返回数据
                processHttpResponse(request, httpResp, response);
            } else {
                // 取消成功
                mCancelListeners.putCancelSuccess(request);
            }
        } catch (HttpHostConnectException e) {
            response.setResultCode(ActionResultCode.HOST_CONNECT_ERROR);
            Logger.e(getTag(), String.valueOf(request.getActionId())+"HttpHostConnectException", e);            
        } catch (ConnectException e) {
            response.setResultCode(ActionResultCode.NETWORK_ERROR);
            Logger.e(getTag(), String.valueOf(request.getActionId())+"ConnectException", e);
        } catch (ConnectTimeoutException e) {
            response.setResultCode(ActionResultCode.NETWORK_TIMEOUT);
            Logger.e(getTag(), String.valueOf(request.getActionId())+"ConnectTimeoutException", e);
        } catch (IOException e) {
            response.setResultCode(ActionResultCode.NETWORK_ERROR);
            Logger.e(getTag(), String.valueOf(request.getActionId())+"IOException", e);
        } catch (Exception e) {
            response.setResultCode(ActionResultCode.CAUGHT_EXCEPTION);
            Logger.e(getTag(), String.valueOf(request.getActionId())+"Connect Exception", e);
        } finally {
            if (httpClient != null) {
                httpClient.close();
            }
            
            fireResult(request, response);
        }
    }

    /**
     * 此方法负责将服务器数据填充至Response中
     * @param request 请求
     * @param httpResp Http请求返回的数据
     * @param response 返回给界面层的Response
     */
    protected void processHttpResponse(Request request, HttpResponse httpResp, Response response) {
        
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void process(Request request) {
        Runnable runnable = getProcessRunnable(request);
        mExecutor.execute(runnable);
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
     * {@inheritDoc}
     * @return AndroidHttpClient对象
     */
    @Override
    protected AndroidHttpClient getHttpClient() {    
        AndroidHttpClient httpClient = AndroidHttpClient.newInstance(USER_AGENT);
        return httpClient;
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
}

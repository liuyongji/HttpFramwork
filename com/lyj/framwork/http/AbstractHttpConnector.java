package com.lyj.framwork.http;



import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpTrace;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import com.lyj.framwork.log.Logger;

/**
 * Http连接�?, 提供连接方法<BR>
 * TODO 暂时封装的是HttpClient的连�?, HttpURLConnection方式后续有时间再补充
 * 
 * @author 王玉�?
 * @version [Transfer, 2012-12-26] 
 */
public abstract class AbstractHttpConnector implements HttpMessage {
    
    /**
     * AbstractHttpConnector TAG
     */
    private static final String TAG = "AbstractHttpConnector";
     
    /**
     * 连接超时时间
     */
    private static final int CONNECT_TIMEOUT = 20 * 1000;
    
    /**
     * 等待超时时间
     */
    private static final int SOCKET_TIMEOUT = 20 * 1000;
    
    /**
     * 发出Http连接请求<BR>
     * 
     * @param httpRequestId Http请求的标识ID
     * @param requestParams 请求相关参数
     */
    protected HttpResponse connect(HttpClient httpClient, int httpRequestId, Object requestParams) throws Exception {
        HttpUriRequest httpUriReq = buildHttpRequest(httpRequestId, requestParams, this);
        HttpResponse httpResp = httpClientExecute(httpClient, httpUriReq);

        return httpResp;
    }
    
    /**
     * 发出HttpClient连接请求<BR>
     * 
     * @param httpClient Http连接�?
     * @param httpRequest Http请求
     * @return 返回HttpResponse对象
     */
    public HttpResponse httpClientExecute(HttpClient httpClient, HttpUriRequest httpUriReq) throws Exception {
        HttpResponse httpResp = httpClient.execute(httpUriReq);
        
        return httpResp;
    }
    
    /**
     * 获取TAG, 用于打印
     * @return TAG
     */
    protected String getTag() {
        return AbstractHttpConnector.TAG;
    }
    
    /**
     * 构�?�Http请求对象
     * 
     * @param httpRequestId Http请求的标识ID
     * @param requestParams 该Http请求附属请求
     * @return HttpRequest对象
     */
    protected HttpUriRequest buildHttpRequest(int httpRequestId, 
            Object requestParams, HttpMessage msg) {
        String url = msg.getUrl(httpRequestId, requestParams);
        String method = msg.getMethod(httpRequestId, requestParams);

        Logger.d(getTag(), String.format("Connect: Method:%s, Url:%s", method, url) +  " httpRequestId:"+ httpRequestId);
        
        HttpUriRequest httpUriReq = null;
        if (Method.GET.equals(method)) {
            httpUriReq = new HttpGet(url);
        } else if (Method.DELETE.equals(method)) {
            httpUriReq = new HttpDelete(url);
        } else if (Method.HEAD.equals(method)) {
            httpUriReq = new HttpHead(url);
        } else if (Method.OPTIONS.equals(method)) {
            httpUriReq = new HttpOptions(url);
        } else if (Method.POST.equals(method)) {
            httpUriReq = new HttpPost(url);
        } else if (Method.PUT.equals(method)) {
            httpUriReq = new HttpPut(url);
        } else if (Method.TRACE.equals(method)) {
            httpUriReq = new HttpTrace(url);
        }
        
        if (Method.POST.equals(method)
                || Method.PUT.equals(method)) {
            HttpEntity entity = msg.getBody(httpRequestId, requestParams);
            if (entity != null) {
                ((HttpEntityEnclosingRequest) httpUriReq).setEntity(entity);
            }
        }

        Header[] headers = msg.getHeaders(httpRequestId, requestParams);
        if (headers != null) {
            httpUriReq.setHeaders(headers);
        }
        
        HttpParams params = msg.getParams(httpRequestId, requestParams);
        if (params != null) {
            httpUriReq.setParams(params);
        }

        return httpUriReq;
    }
    
    /**
     * 获取HttpClient
     * @return 默认返回DefaultHttpClient, 并设置默认的超时时间
     */
    protected HttpClient getHttpClient() {
        HttpClient httpClient = new DefaultHttpClient();
        HttpParams httpParams = httpClient.getParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, CONNECT_TIMEOUT);
        HttpConnectionParams.setSoTimeout(httpParams, SOCKET_TIMEOUT); 
        return httpClient;
    }
    
    /**
     * {@inheritDoc}<BR>
     * 默认返回POST请求, 子类�?般需要重写此方法
     */
    @Override
    public String getMethod(int httpRequestId, Object requestParams) {
        return Method.POST;
    }

    /**
     * {@inheritDoc}<BR>
     * 默认返回NULL, 子类�?般不�?要重写此方法, 有需要定制时再作重写
     */
    @Override
    public Header[] getHeaders(int httpRequestId, Object requestParams) {        
        return null;
    }

    /**
     * {@inheritDoc}<BR>
     * 默认返回NULL, 子类�?般不�?要重写此方法, 有需要定制时再作重写
     */
    @Override
    public HttpParams getParams(int httpRequestId, Object requestParams) {
        return null;
    }
    
    /**
     * Http请求方式
     * @author 王玉�?
     * @version [Transfer, 2012-12-25]
     */
    public interface Method {
        /**
         * GET请求
         */
        String GET = HttpGet.METHOD_NAME;
        
        /**
         * DELETE请求
         */
        String DELETE = HttpDelete.METHOD_NAME;
        
        /**
         * HEAD请求
         */
        String HEAD = HttpHead.METHOD_NAME;
        
        /**
         * OPTIONS请求
         */
        String OPTIONS = HttpOptions.METHOD_NAME;
        
        /**
         * POST请求
         */
        String POST = HttpPost.METHOD_NAME;
        
        /**
         * PUT请求
         */
        String PUT = HttpPut.METHOD_NAME;
        
        /**
         * TRACE请求
         */
        String TRACE = HttpTrace.METHOD_NAME;      
    }
}

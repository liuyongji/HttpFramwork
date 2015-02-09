package com.lyj.framwork.http;


import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.params.HttpParams;


/*
 * 文件名：HttpMessage.java
 * 创建人：王玉�?
 * 创建时间�?2012-12-26
 * �?     权：Copyright Easier Digital Tech. Co. Ltd. All Rights Reserved.
 *com.weedong.mobileassistant.framework.httpmework.http;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.params.HttpParams;

/**
 * Http消息<BR>
 * 
 * @author 王玉�?
 * @version [Transfer, 2012-12-26] 
 */
public interface HttpMessage {
    /**
     * 获取该Http请求的Url<BR>
     * 
     * @param httpRequestId 该Http请求ID
     * @param requestParams 该Http请求附带参数
     * @return 返回该Http请求的Url
     */
    String getUrl(int httpRequestId, Object requestParams);

    /**
     * 获取该Http请求的请求方�?<BR>
     * 
     * @param httpRequestId 该Http请求ID
     * @param requestParams 该Http请求附带参数
     * @return 返回该Http请求的请求方�?
     */
    String getMethod(int httpRequestId, Object requestParams);

    /**
     * 获取该Http请求的请求头<BR>
     * 
     * @param httpRequestId 该Http请求ID
     * @param requestParams 该Http请求附带参数
     * @return 返回该Http请求的请求头
     */
    Header[] getHeaders(int httpRequestId, Object requestParams);
    
    /**
     * 获取该Http请求的请求参�?<BR>
     * 
     * @param httpRequestId 该Http请求ID
     * @param requestParams 该Http请求附带参数
     * @return 返回该Http请求的请求参�?
     */
    HttpParams getParams(int httpRequestId, Object requestParams);

    /**
     * 获取该Http请求的内容实�?<BR>
     * POST或PUT请求才需要用到此对象
     * 
     * @param httpRequestId 该Http请求ID
     * @param requestParams 该Http请求附带参数
     * @return 返回该Http请求的所带内容体
     */
    HttpEntity getBody(int httpRequestId, Object requestParams);
}

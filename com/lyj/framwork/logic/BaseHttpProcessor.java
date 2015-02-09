/*
 * 文件名：BaseHttpProcessor.java
 * 创建人：王玉丰
 * 创建时间：2013-3-16
 * 版     权：Copyright Easier Digital Tech. Co. Ltd. All Rights Reserved.
 */
package com.lyj.framwork.logic;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.concurrent.Executor;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.lyj.framwork.log.Logger;
import com.lyj.framwork.logic.HttpAction.ActionResultCode;

/**
 * 对车友会的Http响应作了业务定制
 * @author 王玉丰
 * @version [CarMates, 2013-3-16] 
 */
public abstract class BaseHttpProcessor extends AbstractHttpProcessor {
    /**
     * BaseHttpProcessor TAG
     */
    private static final String TAG = "BaseHttpProcessor";
    
    /**
     * 车友会服务器实现上的鉴权失败
     */
    private static final int HTTP_BAD_TOKEN = 401;
    
    /**
     * 用于鉴权的Http Header: Authorization
     */
    private static final String AUTHORIZATION = "Authorization";

    /**
     * 构造函数
     * @param executor 线程池
     */
    protected BaseHttpProcessor(Executor executor) {
        super(executor);
    }

    /**
     * 处理Http返回的内容
     * @param request 请求
     * @param content Http返回的内容
     * @param response 返回给界面层的Response
     */
    protected abstract void processRespContent(Request request, String content, Response response);

    /**
     * 此方法负责预处理服务器返回的状态
     * @param request 请求
     * @param httpResp Http请求返回的数据
     * @param response 返回给界面层的Response
     */
    protected void processHttpResponse(Request request, HttpResponse httpResp, Response response) {
        int responseStatusCode = httpResp.getStatusLine().getStatusCode();
        Logger.d(TAG, "ActionId: " + request.getActionId() + " Resp StatusCode: " + responseStatusCode);
        
        switch (responseStatusCode) {
            case HttpURLConnection.HTTP_OK:
                processHttpOk(request, httpResp, response);
                break;
            case HttpURLConnection.HTTP_BAD_REQUEST:
            case HTTP_BAD_TOKEN:
            case HttpURLConnection.HTTP_FORBIDDEN:
            case HttpURLConnection.HTTP_NOT_FOUND:
            case HttpURLConnection.HTTP_INTERNAL_ERROR:
                processHttpNotOk(request, httpResp, response);
                break;
            default:
                break;
        }
    }
    
    /**
     * 此方法负责将请求成功情况下的服务器数据填充至Response中<BR>
     * 默认将内容转换成字符串回调给抽象方法处理, 如果Http返回的不是字符串可自行覆写此方法
     * @param request 请求
     * @param httpResp Http请求返回的数据
     * @param response 返回给界面层的Response
     */
    protected void processHttpOk(Request request, HttpResponse httpResp, Response response) {
        try {
            String respContent = handleResponse(httpResp);
            Logger.d(TAG, "ActionId: " + request.getActionId() + " Resp Content: " + respContent);
            
            // 解析后的数据留给子类去解析
            processRespContent(request, respContent, response);            
        } catch (IOException e) {
            Logger.e(TAG, "processHttpOk IOException", e);
            response.setResultData(ActionResultCode.CAUGHT_EXCEPTION);
        }
    }
    
    /**
     * 按服务器定义的错误情况将数据解析
     * @param request 请求
     * @param httpResp Http请求返回的数据
     * @param response 返回给界面层的Response
     */
    protected void processHttpNotOk(Request request, HttpResponse httpResp, Response response) {
        try {
            // 对错误状态进行处理
            pretreatResult(request, handleResponse(httpResp), response);            
        } catch (IOException e) {
            Logger.e(TAG, "processHttpNotOk handleResponse IOException", e);
            response.setResultCode(ActionResultCode.CAUGHT_EXCEPTION);
        } catch (JSONException je) {
            Logger.e(TAG, "processHttpNotOk pretreatResult JSONException", je);
            
            int responseStatusCode = httpResp.getStatusLine().getStatusCode();
            if (responseStatusCode == HttpURLConnection.HTTP_INTERNAL_ERROR) {
                response.setResultCode(ActionResultCode.UNKNOWN_ERROR);
            } else {
                response.setResultCode(ActionResultCode.PARSE_ERROR);                
            }
        }
    }

    /**
     * 对错误状态的结果进行预处理
     * @param request 请求
     * @param httpResp Http请求返回的数据
     * @param response 返回给界面层的Response
     * @throws JSONException 
     */
    private void pretreatResult(Request request, String content, Response response) throws JSONException {
        Logger.d(TAG, "ActionId: " + request.getActionId() + " Resp pretreatResult: " + content);
        JSONObject rootJsonObj = new JSONObject(content);
        
        // 仅解析Result
        if (rootJsonObj.has("Result")) {
            JSONObject resultObj = rootJsonObj.getJSONObject("Result");
            if (resultObj.has("resultCode")) {
                int resultCode = rootJsonObj.getInt("resultCode");                
                response.setResultCode(resultCode);
            }
            
            if (resultObj.has("resultDesc")) {
                String resultDesc = rootJsonObj.getString("resultDesc");
                response.setResultDesc(resultDesc);
            }       
        } else {
            if (rootJsonObj.has("resultCode")) {
                int resultCode = rootJsonObj.getInt("resultCode");                
                response.setResultCode(resultCode);
            }
            
            if (rootJsonObj.has("resultDesc")) {
                String resultDesc = rootJsonObj.getString("resultDesc");
                response.setResultDesc(resultDesc);
            }            
        }
    }
    
    /**
     * 将HttpResponse的内容转换成字符串
     * @param httpResp Http请求返回的数据
     * @return 转换成字符串的响应数据
     * @throws IOException 抛出IOException异常
     */
    public String handleResponse(HttpResponse httpResp) throws IOException {
        HttpEntity entity = httpResp.getEntity(); 
        if (entity != null) {
            byte[] content = EntityUtils.toByteArray(entity);
            return new String(content, HTTP.UTF_8); 
        } else { 
            return null; 
        } 
    }

    /**
     * {@inheritDoc}<BR>
     */
    @Override
    public Header[] getHeaders(int httpRequestId, Object requestParams) {        
        Header[] header = null;
        switch (httpRequestId) {
//            case UserActionType.USER_REGISTER:
//            case UserActionType.USER_LOGIN:
//            case UserActionType.REFRESH_TOKEN:
//            case UserActionType.GET_VERIFICATION_CODE:
//            case UserActionType.RESET_PASSWORD:
//            case UserActionType.CHECK_VERIFICATION_CODE:
//                header = new BasicHeader[] {new BasicHeader(HTTP.CONTENT_TYPE, "application/json"),new BasicHeader("Accept", "application/json") }; 
//                break;
//            case UserActionType.UPLOAD_PHOTO:
//            case UserActionType.UPLOAD_BJIMAGE:{
////            	String token = AasProcessor.getInstance().getAasResult().getToken();
////            	Logger.i(TAG,"photo===="+token);
////                header = new BasicHeader[] {new BasicHeader(HTTP.CONTENT_TYPE, "multipart/form-data"), 
////                		new BasicHeader(AUTHORIZATION, token)};
//                break;
//            }
//            case UserActionType.USERINFO_SAVE:{
////                String token = AasProcessor.getInstance().getAasResult().getToken();
////                header = new BasicHeader[] {new BasicHeader(HTTP.CONTENT_TYPE, "application/json"),new BasicHeader("ACCEPT", "application/json"), 
////                    new BasicHeader(AUTHORIZATION, token)};
//                break;
//            }
////            case DynamicActionType.UPLOAD_IMG:
////            case DynamicActionType.UPLOAD_BG_IMG:
////            case InfoActionType.UPLOAD_IMG: {
//////                String token = AasProcessor.getInstance().getAasResult().getToken();
//////                header = new BasicHeader[] {
//////                        new BasicHeader(HTTP.USER_AGENT, "Apache-HttpClient/4.2.1 (java 1.5)"),
//////                        new BasicHeader(AUTHORIZATION, token) };
////                break;
//            }
            default: 
//                String token = AasProcessor.getInstance().getAasResult().getToken();
//                
//                if (!StringUtil.isNullOrEmpty(token)) {
//                    header = new BasicHeader[] {new BasicHeader(HTTP.CONTENT_TYPE, "application/json"), 
//                        new BasicHeader(AUTHORIZATION, token)};                    
//                } else {
//                    header = new BasicHeader[] {new BasicHeader(HTTP.CONTENT_TYPE, "application/json"), 
//                        new BasicHeader("Accept", "application/json") }; 
//                }
                break;
        }
        
        return header;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getTag() {
        return BaseHttpProcessor.TAG;
    }
}

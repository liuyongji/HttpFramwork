/*
 * 文件名：Response.java
 * 创建人：王玉丰
 * 创建时间：2012-12-24
 * 版     权：Copyright Easier Digital Tech. Co. Ltd. All Rights Reserved.
 */
package com.lyj.framwork.logic;

/**
 * 请求对应的响应结果<BR>
 * 
 * @author 王玉丰
 * @version [Transfer, 2012-12-24] 
 */
public class Response {
    /**
     * 处理请求返回的结果码
     */
    private int resultCode = -1;
    
    /**
     * 处理请求返回的结果码 
     */
    private  String resultCodeStr;
    /**
     * 结果描述(此字段主要对应于服务器接口的resultDesc字段)
     */
    private String resultDesc;
    
    /**
     * 返回数据
     */
    private Object resultData;

    /**
     * 获取处理请求返回的结果码
     * @return resultCode 处理请求返回的结果码
     */
    public int getResultCode() {
        return resultCode;
    }

    /**
     * 设置处理请求返回的结果码
     * @param resultCode 处理请求返回的结果码
     */
    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }
    
    /**
     * 获取处理请求返回的结果码
     * @return resultCode 处理请求返回的结果码
     */
    public String getResultCodeStr() {
        return resultCodeStr;
    }
    
    /**
     * 设置处理请求返回的结果码
     * @param resultCode 处理请求返回的结果码
     */
    public void setResultCodeStr(String resultCodeStr) {
        this.resultCodeStr = resultCodeStr;
    }

    /**
     * 获取结果描述(此字段主要对应于服务器接口的resultDesc字段)
     * @return resultDesc 结果描述(此字段主要对应于服务器接口的resultDesc字段)
     */
    public String getResultDesc() {
        return resultDesc;
    }

    /**
     * 设置结果描述(此字段主要对应于服务器接口的resultDesc字段)
     * @param resultDesc 结果描述(此字段主要对应于服务器接口的resultDesc字段)
     */
    public void setResultDesc(String resultDesc) {
        this.resultDesc = resultDesc;
    }

    /**
     * 获取返回数据
     * @return resultData 返回数据
     */
    public Object getResultData() {
        return resultData;
    }

    /**
     * 设置返回数据
     * @param resultData 返回数据
     */
    public void setResultData(Object resultData) {
        this.resultData = resultData;
    }
}

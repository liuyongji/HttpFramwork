/*
 * 文件名：ResponseListener.java
 * 创建人：王玉丰
 * 创建时间：2012-12-24
 * 版     权：Copyright Easier Digital Tech. Co. Ltd. All Rights Reserved.
 */
package com.lyj.framwork.logic;

/**
 * 处理结果的回调接口<BR>
 * 为统一界面与执行结果的返回报告, 使用Request与Response方式组织逻辑处理及回调
 * 
 * @author 王玉丰
 * @version [Transfer, 2012-12-24] 
 */
public interface ResponseListener {
    /**
     * 请求处理结果
     * @param request 请求
     * @param response 对应的响应结果
     */
    void onProcessResult(Request request, Response response);
}

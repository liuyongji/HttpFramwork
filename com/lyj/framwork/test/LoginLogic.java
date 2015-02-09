/**
 * 
 */
package com.lyj.framwork.test;


import java.util.concurrent.Executor;

import org.apache.http.HttpEntity;

import android.content.ContentValues;
import android.content.Context;

import com.lyj.framwork.logic.BaseHttpProcessor;
import com.lyj.framwork.logic.Request;
import com.lyj.framwork.logic.Response;
import com.lyj.framwork.logic.HttpAction.LoginActionType;

/**
 * 文件名： LoginLogic.java
 */
public class LoginLogic extends BaseHttpProcessor{
	private static final String TAG =LoginLogic.class.getSimpleName();
	private static LoginLogic sSingleton;
	public static synchronized LoginLogic getInstance() {
//		if (null == sSingleton) {
//			sSingleton = new LoginLogic(ExecutorSupport.getExecutor(),
//					CorpsApp.getInstance());
//		}
		return sSingleton;
	}
	
	protected LoginLogic(Executor executor, Context context) {
		super(executor);
//		associationDBadapter =AssociationDbAdapter.getInstance(context);
	}
	
	protected LoginLogic(Executor executor) {
		super(executor);
		// TODO Auto-generated constructor stub
	}


	private static final String BASE_URL = "";
	/**
	 * 用户注册url [1-1]
	 */
	private static final String REGIST_URL = BASE_URL+"protocol=100101";
	
	private static final String LOGIN_URL = BASE_URL+"protocol=100102";
	
	/**
	 * 获取验证码url [1-1]
	 */
	private static final String GET_VERIFYCODE_URL = BASE_URL+"protocol=100103";
	
	private static final String UPDATE_PROFILE_URL = BASE_URL+"protocol=100104";
	
	private static final String UPLOADE_PHOTO_URL = BASE_URL+"protocol=100105";


	

	/* (non-Javadoc)
	 * @see com.weedong.corps.framwork.http.HttpMessage#getUrl(int, java.lang.Object)
	 */
	@Override
	public String getUrl(int httpRequestId, Object requestParams) {
		StringBuilder urlBuilder = new StringBuilder();		
		switch (httpRequestId) {
		case LoginActionType.DEVICE_REGIST_ACTION:{		
			ContentValues cv = (ContentValues)((Request)requestParams).getData();
			String account = cv.getAsString("account");
			String pwd = cv.getAsString("password");
			String verifycode = cv.getAsString("verifycode");
			urlBuilder.append(REGIST_URL)
			.append("&userid=0")
			.append("&account="+account)
			.append("&password="+pwd)
			.append("&verifycode="+verifycode);
			break;
			}
		case LoginActionType.GET_VERIFICATIONCODE_ACTION:
			String phone = (String)((Request)requestParams).getData();
			urlBuilder.append(GET_VERIFYCODE_URL)
			.append("&userid=0")
			.append("&phone="+phone);
			break;
		case LoginActionType.USER_LOGIN_ACTION:{
			ContentValues cv = (ContentValues)((Request)requestParams).getData();
			String account = cv.getAsString("account");
			String pwd = cv.getAsString("password");
			urlBuilder.append(LOGIN_URL)
			.append("&userid=0")
			.append("&account="+account)
			.append("&password="+pwd);
			break;
			}
		case LoginActionType.UPDATE_PROFILE_ACTION:
			urlBuilder.append(UPDATE_PROFILE_URL);
			break;
		case LoginActionType.UPLOAD_PHOTO:{
//			byte[] photo = (byte[])((Request)requestParams).getData();
			urlBuilder.append(UPLOADE_PHOTO_URL);
			break;
		}
		default:
			break;
		}
//		String  timestamp = String.valueOf(System.currentTimeMillis());
//		String token = MD5Util.string2MD5(SERVER_KEY+timestamp);
//		String deviceId = CorpsApp.getDeviceId();
//		String userid = SharePreferencesUtil.getInstance().getUserId(false);
//		if(urlBuilder.indexOf("userid")<0){
//			urlBuilder.append("&userid="+userid);
//		}
//		urlBuilder.append("&devicecode="+deviceId)
//		.append("&timestamp="+timestamp)
//		.append("&platform=2")
//		.append("&token="+token);
		return urlBuilder.toString();
	}

	/* (non-Javadoc)
	 * @see com.weedong.corps.framwork.http.HttpMessage#getBody(int, java.lang.Object)
	 */

	/* (non-Javadoc)
	 * @see com.weedong.corps.framwork.logic.BaseHttpProcessor#processRespContent(com.weedong.corps.framwork.logic.Request, java.lang.String, com.weedong.corps.framwork.logic.Response)
	 */
	@Override
	protected void processRespContent(Request request, String content,
			Response response) {}

	@Override
	public HttpEntity getBody(int httpRequestId, Object requestParams) {
		// TODO Auto-generated method stub
		return null;
	}
	

}

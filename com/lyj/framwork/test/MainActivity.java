package com.lyj.framwork.test;

import com.lyj.framwork.logic.HttpAction.ActionResultCode;
import com.lyj.framwork.logic.HttpAction.LoginActionType;
import com.lyj.framwork.logic.Request;
import com.lyj.framwork.logic.Response;
import com.lyj.framwork.test.LoginLogic;
import com.lyj.framwork.ui.BaseActivity;




//import android.app.DownloadManager.Request;
import android.os.Bundle;

public class MainActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		setContentView(R.layout.fragment_main);
		processAction(LoginLogic.getInstance(), LoginActionType.USER_LOGIN_ACTION, Request.class);
	}
	@Override
	protected void onProcessUiResult(com.lyj.framwork.logic.Request request,
			Response response) {
		// TODO Auto-generated method stub

		switch (request.getActionId()) {
		case LoginActionType.USER_LOGIN_ACTION:
			// cacelWaitingDialog();
			if (response.getResultCode() == ActionResultCode.ACTION_SUCESS) {
				showToast("do something");
			} else {
				cacelWaitingDialog();
				showToast(response.getResultDesc());
			}
			break;
		default:
			break;
		}
	
	}
}

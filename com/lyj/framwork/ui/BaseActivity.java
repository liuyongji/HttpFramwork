/*
 * 文件名：BaseActivity.java
 * 创建人：王玉丰
 * 创建时间：2012-11-22
 * 版     权：Copyright Easier Digital Tech. Co. Ltd. All Rights Reserved.
 */
package com.lyj.framwork.ui;

import java.util.Stack;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextPaint;
import android.view.KeyEvent;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

//import com.umeng.analytics.MobclickAgent;

import com.lyj.framwork.logic.CancelListener;
import com.lyj.framwork.logic.Processor;
import com.lyj.framwork.logic.Request;
import com.lyj.framwork.logic.Response;
import com.lyj.framwork.logic.ResponseListener;
import com.lyj.framwork.logic.HttpAction.ActionResultCode;

/**
 * 界面基类Activity<BR>
 * 
 * @author 王玉丰
 * @version [Transfer, 2012-11-22]
 */
public abstract class BaseActivity extends Activity implements
		 ResponseListener, CancelListener {
	/**
	 * 功能入口限制提示对话框
	 */
	protected AlertDialog.Builder mLimitDialog = null;

	/**
	 * 游客限制提示对话框
	 */
	protected AlertDialog.Builder mVisitorLimitDialog = null;

	/**
	 * 版本升级s提示对话框
	 */
	protected AlertDialog.Builder mNewVersionDialog = null;

	protected ProgressDialog mWaitingDialog = null;

	/**
	 * activity堆栈
	 */
	private static Stack<Activity> sActivityStack = new Stack<Activity>();

	/**
	 * 提示信息
	 */
	private Toast mToast;


	/**
	 * 初始化动作<BR>
	 * 判断程序是否开启着Service、当前是否已经设置用户名和头像
	 * <P>
	 * 
	 * {@inheritDoc}
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		sActivityStack.add(this);
		if (!isInit()) {
			// TODO 重新启动欢迎界面之类
			return;
		}
	}

	/**
	 * 判断是否已初始化<BR>
	 * 稍后处理，如：判断程序是否开启着Service、当前是否已经设置用户名和头像
	 * 
	 * @return 是否初始化
	 */
	protected boolean isInit() {
		return true;
	}

	/**
	 * 执行处理特定的动作请求<BR>
	 * 不自行创建Request及执行方法, 且回调在本Activity中处理
	 * 
	 * @param processor
	 *            执行请求的处理器
	 * @param actionId
	 *            请求的动作ID
	 * @param data
	 *            请求的参数
	 * @return 返回请求对象
	 */
	protected Request processAction(Processor processor, int actionId,
			Object data) {
		return processAction(processor, actionId, data, this);
	}

	/**
	 * 执行处理特定的动作请求<BR>
	 * 不自行创建Request及执行方法, 且回调在本Activity中处理
	 * 
	 * @param processor
	 *            执行请求的处理器
	 * @param actionId
	 *            请求的动作ID
	 * @param data
	 *            请求的参数
	 * @return 返回请求对象
	 */
	protected Request processActionShowWating(Processor processor,
			int actionId, Object data) {
		return processActionShowWating(processor, actionId, data, null);
	}

	protected Request processActionShowWating(Processor processor,
			int actionId, Object data, String showtext) {
		if (null != showtext && showtext.length() > 0) {
			mWaitingDialog = ProgressDialog.show(
					this.isChild() ? this.getParent() : this, null, showtext,
					true);
		} else {
			mWaitingDialog = ProgressDialog.show(
					this.isChild() ? this.getParent() : this, null,
					"wait...", true);
		}
		mWaitingDialog.setCancelable(true);
		return processAction(processor, actionId, data, this);
	}

	protected void showWaitingDialog() {
		mWaitingDialog = ProgressDialog.show(this.isChild() ? this.getParent()
				: this, null, "wait...", true);
		mWaitingDialog.setCancelable(true);
	}

	protected void cacelWaitingDialog() {
		if (mWaitingDialog != null && mWaitingDialog.isShowing()) {
			mWaitingDialog.cancel();
		}
	}

	/**
	 * 显示提示信息
	 * 
	 * @param msg
	 *            提示信息
	 */
	protected void showProgressDialog(String msg) {
		if (mWaitingDialog == null) {
			mWaitingDialog = new ProgressDialog(this);
			mWaitingDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		}
		mWaitingDialog.setCancelable(false);
		mWaitingDialog.setMessage(msg);
		mWaitingDialog.show();
	}

	/**
	 * 显示提示信息
	 * 
	 * @param msg
	 *            提示信息
	 */
	protected void showProgressDialog() {
		if (mWaitingDialog == null) {
			mWaitingDialog = new ProgressDialog(this);
			mWaitingDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		}
		mWaitingDialog.setCancelable(false);
		mWaitingDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_SEARCH) {
					return true;
				} else {
					return false; // 默认返回 false
				}
			}
		});
//		mWaitingDialog.setMessage(getString(R.string.waiting));
		mWaitingDialog.show();
	}

	/**
	 * 退出应用程序
	 */
	protected void quit() {

		// 清掉密码和取消自动登录
		// UserInfoPreferences.getInstance().savePassword("");
		// UserInfoPreferences.getInstance().saveOutoLoginTag(1);
		//
		Stack<Activity> tmpStack = sActivityStack;
		sActivityStack = new Stack<Activity>();
		clearActivityStack(tmpStack);
	}

	/**
	 * 
	 * 清空栈内Activity<BR>
	 * 
	 * @param stack
	 *            activity堆栈
	 */
	private void clearActivityStack(Stack<Activity> stack) {
		for (int i = 0; i < stack.size(); i++) {
			stack.get(i).finish();
		}
	}

	/**
	 * 结束Activity
	 * 
	 * @see android.app.Activity#finish()
	 */
	@Override
	public void finish() {
		sActivityStack.remove(this);
		super.finish();
	}

	/**
	 * 执行处理特定的动作请求
	 * 
	 * @param processor
	 *            执行请求的处理器
	 * @param actionId
	 *            请求的动作ID
	 * @param data
	 *            请求的参数
	 * @param responseListener
	 *            回调接口
	 * @return 返回请求对象
	 */
	protected Request processAction(Processor processor, int actionId,
			Object data, ResponseListener responseListener) {
		Request request = new Request();
		request.setActionId(actionId);
		request.setData(data);
		request.setResponseListener(responseListener);
		processor.process(request);
		return request;
	}

	/**
	 * 取消执行特定的请求
	 * 
	 * @param processor
	 *            执行该请求的处理器
	 * @param request
	 *            已执行的请求
	 */
	protected void cancelRequest(Processor processor, Request request) {
		processor.cancel(request, this);
	}



	/**
	 * 界面发出的请求(processAction方法)执行结果回调, 此方法一般由处理器的执行线程回调<BR>
	 * 此方法默认回调主线程中处理的方法onProcessUiResult, 若需要回调到onProcessUiResult, 务必调用super<BR>
	 * 可根据具体实现重写此方法
	 * <p>
	 * {@inheritDoc}
	 */
	@Override
	public void onProcessResult(final Request request, final Response response) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				onProcessUiResult(request, response);
			}
		});
	}

	/**
	 * 取消请求的执行结果回调, 此方法一般由处理器的执行线程回调<BR>
	 * 此方法默认回调主线程中处理的方法onCancelUiResult, 若需要回调到onCancelUiResult, 务必调用super<BR>
	 * 可根据具体实现重写此方法
	 * <p>
	 * {@inheritDoc}
	 */
	@Override
	public void onCancelResult(final Request request, final boolean isCancel) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				onCancelUiResult(request, isCancel);
			}
		});
	}

	/**
	 * 处理结果回调到UI中处理
	 * 
	 * @param request
	 *            请求
	 * @param response
	 *            相应的处理结果
	 */
	protected void onProcessUiResult(final Request request,
			final Response response) {
		// 返回结果是失败则回调到handleResponseError统一处理
		if (response.getResultCode() != ActionResultCode.OTHER_ACTION_SUCESS
				&& response.getResultCode() != ActionResultCode.ACTION_SUCESS) {
			handleResponseError(request, response);
		}
	}

	/**
	 * 取消结果回调到UI中处理
	 * 
	 * @param request
	 *            请求
	 * @param isCancel
	 *            是否取消成功
	 */
	protected void onCancelUiResult(final Request request,
			final boolean isCancel) {

	}

	/**
	 * 显示Toast提示
	 * 
	 * @param msg
	 *            显示信息
	 */
	protected void showToast(String msg) {
		if (mToast == null) {
			mToast = Toast.makeText(this, "", Toast.LENGTH_LONG);
		}
		mToast.setText(msg);
		mToast.show();
	}

	/**
	 * 显示Toast提示
	 * 
	 * @param msg
	 *            显示信息
	 */
	protected void showToast(int msgId) {
		if (mToast == null) {
			mToast = Toast.makeText(this, "", Toast.LENGTH_LONG);
		}
		mToast.setText(getResources().getString(msgId));
		mToast.show();
	}



	/**
	 * 处理响应结果为失败的情况
	 * 
	 * @param request
	 *            请求
	 * @param response
	 *            相应的请求的响应
	 */
	protected void handleResponseError(final Request request,
			final Response response) {
		// if (needShowErrorCodeMsg(request)) {
		// // 默认为只显示Toast
		// showToast(ErrorCodeSupport.getResultCodeText(this,
		// response.getResultCode()));
		// }
	}

	/**
	 * 是否需要显示错误提示
	 * 
	 * @param request
	 *            相应请求
	 * @return 是否需要显示错误提示
	 */
	protected boolean needShowErrorCodeMsg(Request request) {
		return false;
	}






	/**
	 * 字体加粗
	 * 
	 * @param tv
	 *            需要加粗的TextView
	 */
	public void setBoldText(TextView tv) {
		TextPaint tp = tv.getPaint();
		tp.setFakeBoldText(true);
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onResume() {
		super.onResume();
//		MobclickAgent.onPageStart(BaseActivity.this.getClass().getSimpleName()); //统计页面
//		MobclickAgent.onResume(this);
		// AssistantApp.setCurrActivity(this);
//		Intent intent = new Intent(BaseActivity.this, FxService.class);
//		stopService(intent);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.FragmentActivity#onPause()
	 */
	@Override
	protected void onPause() {
//		 MobclickAgent.onPageEnd(BaseActivity.this.getClass().getSimpleName()); 
//		MobclickAgent.onPause(this);
		super.onPause();
	}

	// @Override
	// protected void onPause() {
	// super.onPause();
	// Intent intent = new Intent(BaseActivity.this, FxService.class);
	// startService(intent);
	// }

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.FragmentActivity#onStop()
	 */
	@Override
	protected void onStop() {
		super.onStop();
//		Intent intent = new Intent(BaseActivity.this, FxService.class);
//		startService(intent);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	/**
	 * 是否注册头像变化
	 * 
	 * @return 是否注册
	 */
	protected boolean isRegisterPhotoChange() {
		return false;
	}
}

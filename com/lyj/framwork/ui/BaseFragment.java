/*
 * 文件名：BaseFrament.java
 * 创建人：王玉丰
 * 创建时间：2013-6-26
 * 版     权：Copyright Easier Digital Tech. Co. Ltd. All Rights Reserved.
 */
package com.lyj.framwork.ui;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.widget.Toast;

//import com.umeng.analytics.MobclickAgent;

import com.lyj.framwork.logic.CancelListener;
import com.lyj.framwork.logic.Processor;
import com.lyj.framwork.logic.Request;
import com.lyj.framwork.logic.Response;
import com.lyj.framwork.logic.ResponseListener;

/**
 * Fragment的基类<BR>
 * 主要负责与BaseActivity的事件共享<BR>
 * 
 * @author 
 * @version [EasierMicroApp, 2013-6-26] 
 */
public abstract class BaseFragment extends Fragment implements ResponseListener, CancelListener{
    
    /**
     * 与Activity共享事件的回调接口
     */
    private FragmentMessageListener mFragmentMessageListener;
    
    
    /**
	 * 提示信息
	 */
	private Toast mToast;
	
    
    /**
     * 转换成与Activity交互的回调接口<BR>
     * {@inheritDoc}
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        
        try {
            mFragmentMessageListener = (FragmentMessageListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement FragmentMessageListener");
        }
    }
    
    
    /* (non-Javadoc)
     * @see android.support.v4.app.Fragment#onResume()
     */
    @Override
    public void onResume() {
//    	MobclickAgent.onPageStart(BaseFragment.this.getClass().getSimpleName()); //统计页面
    	super.onResume();
    }
    
    /* (non-Javadoc)
     * @see android.support.v4.app.Fragment#onPause()
     */
    @Override
    public void onPause() {
//    	 MobclickAgent.onPageEnd(BaseFragment.this.getClass().getSimpleName()); 
    	super.onPause();
    }
    /**
     * 发送消息至与Fragment绑定的Activity<BR>
     * 
     * @param what 消息类型
     * @param obj 传递数据
     */
    protected void sendMessageToActivity(int what, Object obj) {
        if (mFragmentMessageListener != null) {
            mFragmentMessageListener.handleFragmentMessage(getTag(), what, obj);
        }
    }
    
    /**
     * 执行处理特定的动作请求<BR>
     * 不暴露创建Request的方法及执行方法, 且回调在本Activity中处理
     * 
     * @param processor 执行请求的处理器
     * @param actionId 请求的动作ID
     * @param data 请求的参数
     * @return 返回请求对象
     */
    protected Request processAction(Processor processor, int actionId, Object data) {
        return processAction(processor, actionId, data, this);
    }
    
    /**
     * 执行处理特定的动作请求
     * 
     * @param processor 执行请求的处理器
     * @param actionId 请求的动作ID
     * @param data 请求的参数
     * @param responseListener 回调接口
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
     * @param processor 执行该请求的处理器
     * @param request 已执行的请求
     */
    protected void cancelRequest(Processor processor, Request request) {
        processor.cancel(request, this);
    }

    
    /**
     * 界面发出的请求(processAction方法)执行结果回调, 此方法一般由处理器的执行线程回调<BR>
     * 此方法默认回调主线程中处理的方法onProcessUiResult, 可根据具体实现重写此方法<BR>
     * {@inheritDoc}
     */
    @Override
    public void onProcessResult(final Request request, final Response response) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onProcessUiResult(request, response);
                }
            });            
        }
    }
    
    /**
     * 取消请求的执行结果回调, 此方法一般由处理器的执行线程回调<BR>
     * 此方法默认回调主线程中处理的方法onCancelUiResult, 可根据具体实现重写此方法<BR>
     * {@inheritDoc}
     */
    @Override
    public void onCancelResult(final Request request, final boolean isCancel) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onCancelUiResult(request, isCancel);
                }
            });       
        }
    }
    
    /**
     * 处理结果回调到UI中处理
     * @param request 请求
     * @param response 相应的处理结果
     */
    protected void onProcessUiResult(final Request request, final Response response) {
        
    }
    
    /**
     * 取消结果回调到UI中处理
     * @param request 请求
     * @param isCancel 是否取消成功
     */
    protected void onCancelUiResult(final Request request, final boolean isCancel) {
        
    }
    
    /**
     * 刷新方法
     * @param data 刷新时传递的参数
     */
    public void refreshData(Object data) {
        
    }
    
    protected void showToast(String content) {
    	if (mToast == null) {
			mToast = Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT);
		}
		mToast.setText(content);
		mToast.show();
    }
    
    protected void showToast(int resid) {
    	if (mToast == null) {
			mToast = Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT);
		}
		mToast.setText(getString(resid));
		mToast.show();
    }

    /**
     * 与Activity共享事件的回调接口<BR>
     * 
     * @author 王玉丰
     * @version [EasierMicroApp, 2013-6-26] 
     */
    public interface FragmentMessageListener {
        /**
         * 处理Fragment发送的消息
         * @param tag Fragment的tag
         * @param what 消息类型
         * @param obj 传递数据
         */
        public void handleFragmentMessage(String tag, int what, Object obj);
    }
    
    

}

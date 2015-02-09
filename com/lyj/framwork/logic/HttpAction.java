package com.lyj.framwork.logic;


/**
 * http请求action
 * 
 * @author gegaosong
 * @version [gps_android, 2013-11-13]
 */
public class HttpAction
{
	/**
	 * @version [CarMates, 2013-3-15]
	 */
	public interface LoginActionType {
		/**
		 * 此模块Action值基数
		 */
		int BASE = 1000;
		
		/**
		 * 注册action
		 */
		int DEVICE_REGIST_ACTION = BASE + 1;
		
		/**
		 * 用户登录action
		 */
		int USER_LOGIN_ACTION = BASE + 2;
		
		/**
		 * 获取验证码action
		 */
		int GET_VERIFICATIONCODE_ACTION = BASE + 3;

		/**
		 * 修改资料action
		 */
		int UPDATE_PROFILE_ACTION = BASE + 4;		
		
		int UPLOAD_PHOTO = BASE + 5;
	}
	
	/**
	 * @version [CarMates, 2013-3-15]
	 */
	public interface CorpsActionType {
		/**
		 * 此模块Action值基数
		 */
		int BASE = 2000;
		
		/**
		 * 创建军团action
		 */
		int CREATE_CROPS_ACTION = BASE + 1;
		
		/**
		 * 发布公告action
		 */
		int BROAD_NOTICE_ACTION = BASE + 2;
		
		/**
		 * 军团详情action
		 */
		int GET_CROPS_INFO_ACTION = BASE + 3;

		/**
		 * 添加主玩游戏action
		 */
		int ADD_GAME_ACTION = BASE + 4;		
		
		/**
		 * 游戏列表（检索）action
		 */
		int GAME_INDEX_ACTION = BASE + 5;	
		
		/**
		 * 获取军团成员列表action
		 */
		int GET_CORPS_MEMBER_ACTION = BASE + 6;	
		
		/**
		 * 加入军团action
		 */
		int JOIN_CORPS_ACTION = BASE + 7;
		
		/**
		 * 军团主玩游戏列表action
		 */
		int GET_CORPS_GAMES_ACTION = BASE + 8;
		
		/**
		 * 军会长控制礼包的开关action
		 */
		int SET_GIFT_STATUS_ACTION = BASE + 9;
		
		/**
		 * 游戏详情action
		 */
		int GET_GAME_INFO_ACTION = BASE + 10;
		
		/**
		 * 军团礼包列表action
		 */
		int GET_CORPS_GIFT_ACTION = BASE + 11;
		
		/**
		 * 军团排行列表action
		 */
		int GET_CORPS_RANKING_ACTION = BASE + 12;
		
		/**
		 * 好友详情action
		 */
		int GET_FRIEND_INFO_ACTION = BASE + 13;
		
		/**
		 * 好友详情action
		 */
		int ADD_CORPS_GIFT_ACTION = BASE + 14;
		
		/**
		 * 检查军团名称是否存在action
		 */
		int CHECK_CROPS_NAME_ACTION = BASE + 15;
		
		/**
		 * 查看是否好友关系action
		 */
		int CHECK_FRIEND_RELATIONSHIP_ACTION = BASE + 16;
	}
	
	/**
	 * @version [CarMates, 2013-3-15]
	 */
	public interface HomeActionType {
		/**
		 * 此模块Action值基数
		 */
		int BASE = 3000;
		
		int GET_COMMED_GIFT = BASE + 1;
		
		int GET_GIFT_INFO = BASE + 2;
	}
	
	/**
	 * 界面动作与逻辑交互结果码<BR>
	 * 一般操作失败并不会具体提示失败原因, 这里使用操作成功与失败的ResponseCode来定义<BR>
	 * 如需其他更具体的失败原因可自行再定义ResultCode, 同时在界面一定要处理该失败码
	 * 此ActionResultCode包含了请求服务器接口的ResultCode, 需继承自HttpResultCode
	 * 
	 * @version [CarMates, 2013-3-15]
	 */
	public interface ActionResultCode extends HttpResultCode {
		/**
		 * 请求操作成功(同时也是服务器接口请求成功ResultCode)
		 */
		int ACTION_SUCESS = REQUEST_SUCCESS;
		
        /**
         * 另一个服务器的请求操作成功(同时也是服务器接口请求成功ResultCode)
         */
        int OTHER_ACTION_SUCESS = 0;
        
        /**
         * 资源不足（可认为操作成功）
         */
        int LACK_OF_RESOURCES = 202010019;

		/**
		 * 登录成功
		 */
		int LOGIN_SUCESS = 200002;

		/**
		 * 操作失败
		 */
		int ACTION_FAIL = 4001;

		/**
		 * 网络异常
		 */
		int NETWORK_ERROR = 4002;

		/**
		 * 解析数据出现异常
		 */
		int PARSE_ERROR = 4003;

		/**
		 * 网络连接超时
		 */
		int NETWORK_TIMEOUT = 4004;

		/**
		 * XMPP登录失败
		 */
		int XMPP_LOGIN_ERROR = 4005;

		/**
		 * 捕获程序异常
		 */
		int CAUGHT_EXCEPTION = 4006;

		/**
		 * 连接服务器异常
		 */
		int HOST_CONNECT_ERROR = 4007;
        
        /**
         * 上传图片失败
         */
        int UPLOAD_IMG_FAILED = 4008;
        
        /**
         * 登录失败
         */
        int LOGIN_PASSWORD_ERROR = 101;
        /**
         * 登录失败
         */
        int LOGIN_PASSWORD_ERROR2 = 117;

	}

	/**
	 * 服务器接口返回的ResultCode
	 * 
	 * @version [CarMates, 2013-3-16]
	 */
	public interface HttpResultCode {
		/**
		 * Http请求成功, Http对应StatusCode: 200
		 */
		int REQUEST_SUCCESS = 200;

		/**
		 * 请求数据格式检验失败, Http对应StatusCode: 400
		 */
		int DATA_FORMAT_INVALIDATE = 400001;

		/**
		 * 缺少鉴权信息或者鉴权信息非法, Http对应StatusCode: 401
		 */
		int TOKEN_INVALIDATE = 401001;

		/**
		 * 鉴权信息已过期, Http对应StatusCode: 401
		 */
		int TOKEN_EXPIRED = 401002;

		/**
		 * 某些字段值不允许为空, Http对应StatusCode: 403
		 */
		int FIELD_NOT_ALLOW_EMPTY = 403001;

		/**
		 * 某些字段校验失败, Http对应StatusCode: 403
		 */
		int FIELD_INVALIDATE = 403002;

		/**
		 * 用户被冻结, Http对应StatusCode: 403
		 */
		int USER_FROZEN = 403003;

		/**
		 * 用户不存在, Http对应StatusCode: 403
		 */
		int USER_NOT_EXIST = 403004;

		/**
		 * 手机号码已注册, Http对应StatusCode: 403
		 */
		int PHONE_REGISTERED = 403005;

		/**
		 * 手机验证码无效, Http对应StatusCode: 403
		 */
		int VERIFY_CODE_INVALIDATE = 403006;

		/**
		 * 手机号码不合法,支持11位手机号, Http对应StatusCode: 403
		 */
		int PHONE_INVALIDATE = 403007;

		/**
		 * 好友关系已经存在, Http对应StatusCode: 403
		 */
		int FRIEND_ALREADY_EXIST = 403008;

		/**
		 * 黑名单关系已经存在, Http对应StatusCode: 403
		 */
		int BLACKLIST_ALREADY_EXIST = 403009;

		/**
		 * 密码错误, Http对应StatusCode: 403
		 */
		int PASSWORD_INCORRECT = 403010;

		/**
		 * 没有找到指定资源, Http对应StatusCode: 404
		 */
		int NOT_FOUND = 404001;

		/**
		 * 未知错误（系统内部错误）, Http对应StatusCode: 500
		 */
		int UNKNOWN_ERROR = 500001;

		/**
		 * 服务器资源不足, Http对应StatusCode: 500
		 */
		int SERVER_RESOURCES_INSUFFICIENT = 500002;

		/**
		 * 与XMPP服务器建立通信失败, Http对应StatusCode: 500
		 */
		int XMPP_COMMUNICATION_FAILURE = 500003;

		/**
		 * 与第三方短信通信错误，下发验证码失败, Http对应StatusCode: 500
		 */
		int PUSH_VERIFY_CODE_FAILURE = 500004;
		
		/**
		 * 资源已存在(重复提交)
		 */
		int ADDED_RESOURCE_ALREADY_EXISTS = 202010027;
		/**
		 * 邀请码错误
		 */
		int VISITOR_CODE_ERROR = 1001;
		/**
		 * 邀请码失效
		 */
		int VISITOR_CODE_ERROR2 = 1003;
	}
}

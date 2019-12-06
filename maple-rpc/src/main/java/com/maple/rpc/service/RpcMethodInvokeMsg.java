package com.maple.rpc.service;

import com.maple.rpc.msg.annotation.CrossMsgID;
import com.maple.rpc.msg.annotation.CrossMsgIDs;

import java.util.Map;
import java.util.function.Consumer;

/**
 * @author lfy
 * @date 2019/12/6 17:49
 **/
@CrossMsgID(CrossMsgIDs.RpcMethodInvokeMsg)
public class RpcMethodInvokeMsg {


	private static final ThreadLocal<Map<String,Object>> REPAIR_MSG_LOCAL = new ThreadLocal<>();

	private int methodUid;

	private String className;

	private String methodName;

	private String methodDesc;

	private Object[] args;

	public RpcMethodInvokeMsg(int methodUid, String className, String methodName, String methodDesc, Object[] args) {
		this.methodUid = methodUid;
		this.className = className;
		this.methodName = methodName;
		this.methodDesc = methodDesc;
		this.args = args;
	}

	public void setMethodUid(int methodUid) {
		this.methodUid = methodUid;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public void setMethodDesc(String methodDesc) {
		this.methodDesc = methodDesc;
	}

	public void setArgs(Object[] args) {
		this.args = args;
	}

	/**
	 * 异步执行请求
	 */
	public void executeRequestAsync(final Consumer<RpcResponse> consumer){

	}

}

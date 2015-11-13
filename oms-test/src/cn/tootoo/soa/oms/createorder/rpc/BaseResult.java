package cn.tootoo.soa.oms.createorder.rpc;

import java.util.HashMap;
import java.util.Map;

import cn.tootoo.http.bean.BaseOutputBean;

public class BaseResult {

	private BaseOutputBean baseOutputBean;
	private int status;
	private String message;
	private Map<String,Object> params =new HashMap<String,Object>();
	
	public BaseOutputBean getBaseOutputBean() {
		return baseOutputBean;
	}
	public void setBaseOutputBean(BaseOutputBean baseOutputBean) {
		this.baseOutputBean = baseOutputBean;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Map<String, Object> getParams() {
		return params;
	}
	public void setParams(Map<String, Object> params) {
		this.params = params;
	}
	
	
	

}

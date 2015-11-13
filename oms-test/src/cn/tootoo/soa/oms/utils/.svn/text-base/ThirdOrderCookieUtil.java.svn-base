package cn.tootoo.soa.oms.utils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import cn.tootoo.utils.CookieUtil;
import cn.tootoo.utils.Log;
import cn.tootoo.utils.StringUtil;

/**
 * 第三方订单cookie
 * @author tenbaoxin
 *
 */
public class ThirdOrderCookieUtil {
	private static final String ADS_UNION="ads_union";
	
	private static final String QOID="qoid";
	
	private static final String FANLI_CHANNEL_ID="51fanli_channel_id";
	
	private static final String FANLI_U_ID="51fanli_u_id";
	
	private static final String FANLI_TRACKING_CODE="51fanli_tracking_code";
	
	private static final String CPS360="cps360";
	
	private static final String[] COOKIE_KEYS=new String[]{ADS_UNION,QOID,FANLI_CHANNEL_ID,FANLI_U_ID,FANLI_TRACKING_CODE,CPS360};
	 /**
     * Cookie信息分隔符。
     */
	private static final String COOKIE_SEP_0 = ",";
	private static final String COOKIE_SEP_1 = "=";
	
	public static String getCookieStr(final String uuid, final Logger logger,final HttpServletRequest request){
		try {
			
			 if (request == null) { return null; }
			 Cookie[] cookies = request.getCookies();
			 StringBuffer cookieStr = new StringBuffer();
			 for(String key:COOKIE_KEYS){
				 String value = CookieUtil.getCookieValueByName(cookies, key);
				 if(!StringUtil.isEmpty(value)){
					 if(cookieStr.length()>0){
						 cookieStr.append(COOKIE_SEP_0);
					 }
					 cookieStr.append(key);
					 cookieStr.append(COOKIE_SEP_1);
					 cookieStr.append(value);
				 }
			 }
			 Log.info(logger, uuid, "获取第三方订单cookie", "cookieStr", cookieStr.toString());
			 return cookieStr.toString();
		} catch (Exception e) {
			Log.error(logger, "获取第三方订单cookie异常", e);
			return null; 
		}
	}
}

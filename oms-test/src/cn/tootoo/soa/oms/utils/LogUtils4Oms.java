package cn.tootoo.soa.oms.utils;

import org.apache.log4j.Logger;

/**
 * 日志工具类。
 * 
 * 服务description：订单服务。
 * 服务remark：订单服务。
 */
public final class LogUtils4Oms {
    
    private static final String LOG_PREFIX = "[oms【订单服务。】]";
    
    public static final void debug(Logger logger, String uuid, String message, Object... params) {
        if (logger == null) {
            return;
        }
        StringBuilder sb = new StringBuilder(LOG_PREFIX);
        if (uuid == null || uuid.length() <= 0) {
            uuid = "UNKNOWN";
        }
        sb.append("[UUID:").append(uuid).append("]");
        sb.append("[Debug Message]:").append(message).append(combineParams(params));
        logger.debug(sb.toString());
    }
    
    public static final void info(Logger logger, String uuid, String message, Object... params) {
        if (logger == null) {
            return;
        }
        StringBuilder sb = new StringBuilder(LOG_PREFIX);
        if (uuid == null || uuid.length() <= 0) {
            uuid = "UNKNOWN";
        }
        sb.append("[UUID:").append(uuid).append("]");
        sb.append("[Info Message]:").append(message).append(combineParams(params));
        logger.info(sb.toString());
    }
    
    public static final void warn(Logger logger, String uuid, String message, Object... params) {
        if (logger == null) {
            return;
        }
        StringBuilder sb = new StringBuilder(LOG_PREFIX);
        if (uuid == null || uuid.length() <= 0) {
            uuid = "UNKNOWN";
        }
        sb.append("[UUID:").append(uuid).append("]");
        sb.append("[Warn Message]:").append(message).append(combineParams(params));
        logger.warn(sb.toString());
    }
    
    public static final void error(Logger logger, String uuid, String message, Object... params) {
        if (logger == null) {
            return;
        }
        StringBuilder sb = new StringBuilder(LOG_PREFIX);
        if (uuid == null || uuid.length() <= 0) {
            uuid = "UNKNOWN";
        }
        sb.append("[UUID:").append(uuid).append("]");
        sb.append("[Error Message]:").append(message).append(combineParams(params));
        logger.error(sb.toString());
    }
    
    public static final void error(Logger logger, String uuid, String message, Throwable e, Object... params) {
        if (logger == null) {
            return;
        }
        StringBuilder sb = new StringBuilder(LOG_PREFIX);
        if (uuid == null || uuid.length() <= 0) {
            uuid = "UNKNOWN";
        }
        sb.append("[UUID:").append(uuid).append("]");
        sb.append("[Error Message]:").append(message).append(combineParams(params));
        logger.error(sb.toString(), e);
    }
    
    private static final String combineParams(Object... params) {
        if (params == null || params.length <= 0) {
            return "";
        }
        int length = params.length;
        /*如果参数长度不是偶数，则追加一项为null。*/
        if (length % 2 != 0) {
            length++;
        }
        StringBuilder sb = new StringBuilder("{");
        for (int i = 0; i < length; i++) {
            if (i >= params.length) {
                sb.append("NULL, ");
            } else {
                if (i % 2 == 0) {
                    /*第0、2、4、6、8...项。*/
                    sb.append(params[i] + "=");
                } else {
                    /*第1、3、5、7、9...项。*/
                    sb.append(params[i] + ", ");
                }
            }
        }
        String result = sb.toString();
        if (result.endsWith(", ")) {
            result = result.substring(0, result.length() - ", ".length());
        }
        result += "}";
        return result;
    }
}
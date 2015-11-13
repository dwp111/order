package cn.tootoo.soa.oms.exceptions;

/**
 * 服务委托者异常类。
 * 
 * 服务description：订单服务。
 * 服务remark：订单服务。
 */
public final class OmsDelegateException extends Exception {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 异常反生时的错误码（默认：0—正确）。
     */
    private int resultID = 0;
    
    /**
     * 异常反生时的错误码（默认：0—正确）。
     */
    public int getResultID() {
        return resultID;
    }
    
    /**
     * 异常类构造函数。
     *
     * @param resultID 错误码。
     * @param resultMessage 错误信息。
     * @param e 封装的异常对象。
     */
    public OmsDelegateException(int resultID, String resultMessage, Throwable e) {
        super(resultMessage, e);
        this.resultID = resultID;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("OmsDelegateException{");
        sb.append("resultID=").append(resultID).append(", ");
        sb.append(super.toString());
        sb.append("}");
        return sb.toString();
    }
}
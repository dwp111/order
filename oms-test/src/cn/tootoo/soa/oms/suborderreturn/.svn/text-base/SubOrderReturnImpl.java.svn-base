package cn.tootoo.soa.oms.suborderreturn;

import org.apache.log4j.Logger;

import cn.tootoo.enums.BaseResultEnum;
import cn.tootoo.global.Global;
import cn.tootoo.http.bean.BaseInputBean;
import cn.tootoo.http.bean.BaseOutputBean;
import cn.tootoo.http.bean.BaseOutputHead;
import cn.tootoo.servlet.BaseService;
import cn.tootoo.soa.oms.exceptions.OmsDelegateException;
import cn.tootoo.soa.oms.suborderreturn.delegate.AbstractSubOrderReturnDelegate;
import cn.tootoo.soa.oms.suborderreturn.delegate.SubOrderReturnDelegateFactory;
import cn.tootoo.soa.oms.suborderreturn.output.OmsSubOrderReturnOutputData;
import cn.tootoo.soa.oms.utils.LogUtils4Oms;

/**
 * 服务接口方法的实现类。
 * 
 * 服务description：订单服务。
 * 服务remark：订单服务。
 * 接口description：子订单退货
 * 接口remark：
 */
public final class SubOrderReturnImpl extends BaseService {
    
    static {
        try {
            Class.forName("cn.tootoo.soa.oms.suborderreturn.delegate.SubOrderReturnDelegate");
        } catch (ClassNotFoundException e) {
            Logger.getLogger(SubOrderReturnImpl.class.getName()).error("cn.tootoo.soa.oms.suborderreturn.delegate.SubOrderReturnDelegate未找到！", e);
        }
    }
    
    /**
     * 日志对象。
     */
    private Logger logger = Logger.getLogger(SubOrderReturnImpl.class);
    
    /**
     * 构造函数。
     */
    public SubOrderReturnImpl() {
        /*如果捕获到未知异常，则在此处统一处理。*/
        if (Thread.currentThread().getUncaughtExceptionHandler() != null) {
            Thread.currentThread().setUncaughtExceptionHandler(new MyUncaughtExceptionHandler(logger));
        }
    }
    
    @Override
    public BaseOutputBean doService(BaseInputBean inputBean) throws Exception {
        /*实例化返回结果。*/
        final BaseOutputBean outputBean = new BaseOutputBean();
        final BaseOutputHead outputHead = new BaseOutputHead();
        outputBean.setOutputHead(outputHead);
        /*生成服务执行标识（改为从MainServiceServlet传入）。*/
        // final String uuid = UUID.randomUUID().toString();
        LogUtils4Oms.info(logger, uuid, "请求参数：", "inputBean", inputBean);
        /*验证请求参数。*/
        if (inputBean == null) {
            Global.getOutputHead(BaseResultEnum.REQUEST_PARAM_ERROR.getResultID(), null, outputBean.getOutputHead(), "：请求参数为空！");
            LogUtils4Oms.error(logger, uuid, "请求参数为空！", "inputBean", inputBean);
            return outputBean;
        }
        if (inputBean.getInputHead() == null) {
            Global.getOutputHead(BaseResultEnum.REQUEST_PARAM_ERROR.getResultID(), null, outputBean.getOutputHead(), "：请求参数头为空！");
            LogUtils4Oms.error(logger, uuid, "请求参数头为空！", "inputBean", inputBean);
            return outputBean;
        }
        /*实例化返回结果数据体。*/
        final OmsSubOrderReturnOutputData outputData = new OmsSubOrderReturnOutputData();
        outputBean.setOutputData(outputData);
        /*实例化服务委托者。*/
        final AbstractSubOrderReturnDelegate delegate = SubOrderReturnDelegateFactory.createDelegate();
        /*将生成服务执行标识设置到服务委托者中。*/
        delegate.setUUID(uuid);
        /*将日志对象设置到服务委托者中。*/
        delegate.setLogger(logger);
        /*将Servlet请求和响应对象设置到服务委托者中。*/
        delegate.setHttpRequest(request);
        delegate.setHttpResponse(response);
        /* 将请求参数对象设置到服务委托者中。*/
        delegate.setReqMap(reqMap);
        try {
            /*服务委托者初始化。*/
            LogUtils4Oms.info(logger, uuid, "服务委托者初始化开始——————", "inputBean", inputBean, "outputBean", outputBean);
            delegate.init(inputBean, outputBean);
            LogUtils4Oms.info(logger, uuid, "——————服务委托者初始化结束！", "inputBean", inputBean, "outputBean", outputBean);
            /*服务委托者初始检查，如果检查通过则执行服务操作。*/
            LogUtils4Oms.info(logger, uuid, "服务委托者初始检查开始——————", "inputBean", inputBean, "outputBean", outputBean);
            if (delegate.check(inputBean, outputBean)) {
                LogUtils4Oms.info(logger, uuid, "服务委托者初始检查通过并执行服务操作开始——————", "inputBean", inputBean, "outputBean", outputBean);
                delegate.execute(inputBean, outputBean);
                LogUtils4Oms.info(logger, uuid, "——————服务委托者初始检查通过并执行服务操作结束！", "inputBean", inputBean, "outputBean", outputBean);
            } else {
                Global.getOutputHead(BaseResultEnum.SERVICE_GENERAL_ERROR.getResultID(), null, outputBean.getOutputHead(), "：服务初始检查没有通过！");
                outputBean.setOutputData(null);    // 服务错误时，数据包体设置为空！
                LogUtils4Oms.info(logger, uuid, "——————服务委托者初始检查没有通过！", "inputBean", inputBean, "outputBean", outputBean);
            }
        } catch (OmsDelegateException e) {
            Global.getOutputHead(BaseResultEnum.INSIDE_ERROR.getResultID(), null, outputBean.getOutputHead(), "：服务运行时错误【" + e + "】！");
            outputBean.setOutputData(null);    // 服务错误时，数据包体设置为空！
            LogUtils4Oms.error(logger, uuid, "服务委托者运行时错误！", e);
            return outputBean;
        } catch (Exception e) {
            Global.getOutputHead(BaseResultEnum.INSIDE_ERROR.getResultID(), null, outputBean.getOutputHead(), "：服务运行时未预期的错误【" + e + "】！");
            outputBean.setOutputData(null);    // 服务错误时，数据包体设置为空！
            LogUtils4Oms.error(logger, uuid, "服务委托者运行时未预期的错误！", e);
            return outputBean;
        } finally {
            try {
                LogUtils4Oms.info(logger, uuid, "服务委托者关闭开始——————", "inputBean", inputBean, "outputBean", outputBean);
                delegate.close(inputBean, outputBean);
                LogUtils4Oms.info(logger, uuid, "——————服务委托者关闭结束！", "inputBean", inputBean, "outputBean", outputBean);
            } catch (OmsDelegateException e) {
                Global.getOutputHead(BaseResultEnum.INSIDE_ERROR.getResultID(), null, outputBean.getOutputHead(), "：服务关闭时错误【" + e + "】！");
                outputBean.setOutputData(null);    // 服务错误时，数据包体设置为空！
                LogUtils4Oms.error(logger, uuid, "服务委托者关闭时错误！", e);
                return outputBean;
            } catch (Exception e) {
                Global.getOutputHead(BaseResultEnum.INSIDE_ERROR.getResultID(), null, outputBean.getOutputHead(), "：服务关闭时未预期的错误【" + e + "】！");
                outputBean.setOutputData(null);    // 服务错误时，数据包体设置为空！
                LogUtils4Oms.error(logger, uuid, "服务委托者关闭时未预期的错误！", e);
                return outputBean;
            }
        }
        /* 如果最终的结果不成功。 */
        if (outputBean.getOutputHead().getResultID() != BaseResultEnum.SUCCESS.getResultID()) {
            outputBean.setOutputData(null);    // 服务错误时，数据包体设置为空！
        }
        LogUtils4Oms.info(logger, uuid, "返回结果：", "outputBean", outputBean);
        return outputBean;
    }
    
    @Override
    public BaseService clone() throws CloneNotSupportedException {
        return new SubOrderReturnImpl();
    }
    
    /**
     * 自定义的未知异常处理类。
     */
    static class MyUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
        
        /**
         * 外部传入的日志对象。
         */
        private Logger logger = null;
        
        public MyUncaughtExceptionHandler(Logger logger) {
            this.logger = logger;
        }
        
        /**
         * 异常处理方法。
         */
        @Override
        public void uncaughtException(Thread t, Throwable e) {
            LogUtils4Oms.error(logger, "", "发生完全未知异常！", e, "thread", t);
        }
    }
}
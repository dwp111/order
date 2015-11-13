package cn.tootoo.soa.oms.ordercancel.delegate;

import cn.tootoo.db.egrocery.texvoucher.TExVoucherDao;
import cn.tootoo.enums.BaseResultEnum;
import cn.tootoo.global.Global;
import cn.tootoo.http.bean.BaseInputBean;
import cn.tootoo.http.bean.BaseInputHead;
import cn.tootoo.http.bean.BaseOutputBean;
import cn.tootoo.http.bean.BaseOutputHead;
import cn.tootoo.soa.oms.ordercancel.input.OmsOrderCancelInputData;
import cn.tootoo.soa.oms.ordercancel.output.OmsOrderCancelOutputData;
import cn.tootoo.soa.oms.exceptions.OmsDelegateException;
import cn.tootoo.soa.oms.utils.LogUtils4Oms;
import java.util.ArrayList;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

/**
 * 服务委托者基础抽象类。
 * 
 * 服务description：订单服务。
 * 服务remark：订单服务。
 * 接口description：父订单取消。
 * 接口remark：
 */
public abstract class AbstractOrderCancelDelegate implements Cloneable {
    
    /**
     * 服务执行标识。
     */
    protected String uuid = "";
    
    /**
     * 设置服务执行标识。
     */
    public final void setUUID(String uuid) {
        this.uuid = uuid;
    }
    
    /**
     * 日志对象。
     */
    protected Logger logger = null;
    
    /**
     * 设置日志对象。
     */
    public final void setLogger(Logger logger) {
        this.logger = logger;
    }
    
    /**
     * Servlet请求对象。
     */
    protected HttpServletRequest httpRequest;
    
    public final void setHttpRequest(HttpServletRequest httpRequest) {
        this.httpRequest = httpRequest;
    }
    
    /**
     * Servlet响应对象。
     */
    protected HttpServletResponse httpResponse;
    
    public final void setHttpResponse(HttpServletResponse httpResponse) {
        this.httpResponse = httpResponse;
    }
    
    /**
     * 请求参数。
     */
    protected Map<String, Object> reqMap;
    
    public final void setReqMap(final Map<String, Object> reqMap) {
        this.reqMap = reqMap;
    }
    
    /**
     * DAO对象实例（表：TTS_MAIL）。
     */
    protected cn.tootoo.db.egrocery.ttsmail.TtsMailDao ttsMailDao = null;
    
    /**
     * DAO对象实例（表：T_OS_PARENT_ORDER）。
     */
    protected cn.tootoo.db.egrocery.tosparentorder.TOsParentOrderDao tOsParentOrderDao = null;
    
    /**
     * DAO对象实例（表：OS_ORDER_ITEMS）。
     */
    protected cn.tootoo.db.egrocery.osorderitems.OsOrderItemsDao osOrderItemsDao = null;
    
    /**
     * DAO对象实例（表：OS_ORDER_OPT）。
     */
    protected cn.tootoo.db.egrocery.osorderopt.OsOrderOptDao osOrderOptDao = null;
    
    /**
     * DAO对象实例（表：SP_PAY_METHOD）。
     */
    protected cn.tootoo.db.egrocery.sppaymethod.SpPayMethodDao spPayMethodDao = null;
    
    /**
     * DAO对象实例（表：BUILDING_QUALIFICATION）。
     */
    protected cn.tootoo.db.egrocery.buildingqualification.BuildingQualificationDao buildingQualificationDao = null;
    
    protected cn.tootoo.db.egrocery.texvoucher.TExVoucherDao tExVoucherDao = null;
    
    /**
     * DAO对象实例（表：BS_BUYER_SCORE）。
     */
    protected cn.tootoo.db.egrocery.bsbuyerscore.BsBuyerScoreDao bsBuyerScoreDao = null;
    
    /**
     * DAO对象实例（表：T_OS_CANCEL_ORDER）。
     */
    protected cn.tootoo.db.egrocery.toscancelorder.TOsCancelOrderDao tOsCancelOrderDao = null;
    
    /**
     * DAO对象实例（表：T_SYS_SHIP_SDC）。
     */
    protected cn.tootoo.db.egrocery.tsysshipsdc.TSysShipSdcDao tSysShipSdcDao = null;
    
    /**
     * DAO对象实例（表：T_OS_ORDER_LOT）。
     */
    protected cn.tootoo.db.egrocery.tosorderlot.TOsOrderLotDao tOsOrderLotDao = null;
    
    /**
     * DAO对象实例（表：T_OS_PARENT_ORDER_OPT）。
     */
    protected cn.tootoo.db.egrocery.tosparentorderopt.TOsParentOrderOptDao tOsParentOrderOptDao = null;
    
    /**
     * DAO对象实例（表：OS_ORDER）。
     */
    protected cn.tootoo.db.egrocery.osorder.OsOrderDao osOrderDao = null;
    
    /**
     * DAO对象实例（表：T_BIND_ORDER）。
     */
    protected cn.tootoo.db.egrocery.tbindorder.TBindOrderDao tBindOrderDao = null;
    
    /**
     * DAO对象实例（表：T_SYS_ONLINE_CARD_GOODS）。
     */
    protected cn.tootoo.db.egrocery.tsysonlinecardgoods.TSysOnlineCardGoodsDao tSysOnlineCardGoodsDao = null;
    
    /**
     * DAO对象实例（表：BS_BUYER_BUY_FEE）。
     */
    protected cn.tootoo.db.egrocery.bsbuyerbuyfee.BsBuyerBuyFeeDao bsBuyerBuyFeeDao = null;
    
    /**
     * DAO对象实例（表：TTS_SHIPPING_COMPANY）。
     */
    protected cn.tootoo.db.egrocery.ttsshippingcompany.TtsShippingCompanyDao ttsShippingCompanyDao = null;
    
    /**
     * DAO对象实例（表：BS_BUYER）。
     */
    protected cn.tootoo.db.egrocery.bsbuyer.BsBuyerDao bsBuyerDao = null;
    
    /**
     * DAO对象实例（表：T_EIP_SUB_ORDER）。
     */
    protected cn.tootoo.db.egrocery.teipsuborder.TEipSubOrderDao tEipSubOrderDao = null;
    
    /**
     * DAO对象实例（表：OS_PAY_PLAN）。
     */
    protected cn.tootoo.db.egrocery.ospayplan.OsPayPlanDao osPayPlanDao = null;
    
    /**
     * DAO对象实例（表：OS_SUBORDER_SIGN_REQ）。
     */
    protected cn.tootoo.db.egrocery.ossubordersignreq.OsSuborderSignReqDao osSuborderSignReqDao = null;
    
    /**
     * DAO对象实例（表：BS_BUYER_SCORE_LOG）。
     */
    protected cn.tootoo.db.egrocery.bsbuyerscorelog.BsBuyerScoreLogDao bsBuyerScoreLogDao = null;
    
    /**
     * DAO对象实例（表：OS_RETURNORDER）。
     */
    protected cn.tootoo.db.egrocery.osreturnorder.OsReturnorderDao osReturnorderDao = null;
    
    /**
     * DAO对象实例（表：RETURN_POINTS_RULE）。
     */
    protected cn.tootoo.db.egrocery.returnpointsrule.ReturnPointsRuleDao returnPointsRuleDao = null;
    
    /**
     * DAO对象实例（表：T_OS_PARENT_ORDER_ITEMS）。
     */
    protected cn.tootoo.db.egrocery.tosparentorderitems.TOsParentOrderItemsDao tOsParentOrderItemsDao = null;
    
    /**
     * 克隆方法。
     * 需子类实现。
     */
    public abstract AbstractOrderCancelDelegate clone() throws CloneNotSupportedException;
    
    /**
     * 初始化方法。
     * 该方法中通常实现如下功能：验证登录、加锁、初始化DAO对象等等。
     * 
     * @param inputBean 该接口的请求参数。
     * @param outputBean 该接口的返回结果。
     */
    public final void init(final BaseInputBean inputBean, final BaseOutputBean outputBean) throws OmsDelegateException {
        /*检验请求参数数据体。*/
        if (inputBean.getInputData() != null) {
            if (!(inputBean.getInputData() instanceof OmsOrderCancelInputData)) {
                Global.getOutputHead(BaseResultEnum.REQUEST_PARAM_ERROR.getResultID(), null, outputBean.getOutputHead());
                LogUtils4Oms.error(logger, uuid, "服务委托者初始化失败：请求参数数据体类型错误！", "inputBean.getInputData().class",inputBean.getInputData().getClass().getName());
                return;
            }
        }
        /*初始化DAO对象实例（表：TTS_MAIL）。*/
        ttsMailDao = new cn.tootoo.db.egrocery.ttsmail.TtsMailDao(uuid, logger);
        /*初始化DAO对象实例（表：T_OS_PARENT_ORDER）。*/
        tOsParentOrderDao = new cn.tootoo.db.egrocery.tosparentorder.TOsParentOrderDao(uuid, logger);
        /*初始化DAO对象实例（表：OS_ORDER_ITEMS）。*/
        osOrderItemsDao = new cn.tootoo.db.egrocery.osorderitems.OsOrderItemsDao(uuid, logger);
        /*初始化DAO对象实例（表：OS_ORDER_OPT）。*/
        osOrderOptDao = new cn.tootoo.db.egrocery.osorderopt.OsOrderOptDao(uuid, logger);
        /*初始化DAO对象实例（表：SP_PAY_METHOD）。*/
        spPayMethodDao = new cn.tootoo.db.egrocery.sppaymethod.SpPayMethodDao(uuid, logger);
        /*初始化DAO对象实例（表：BUILDING_QUALIFICATION）。*/
        buildingQualificationDao = new cn.tootoo.db.egrocery.buildingqualification.BuildingQualificationDao(uuid, logger);
        tExVoucherDao = new cn.tootoo.db.egrocery.texvoucher.TExVoucherDao(uuid, logger);
        
        /*初始化DAO对象实例（表：BS_BUYER_SCORE）。*/
        bsBuyerScoreDao = new cn.tootoo.db.egrocery.bsbuyerscore.BsBuyerScoreDao(uuid, logger);
        /*初始化DAO对象实例（表：T_OS_CANCEL_ORDER）。*/
        tOsCancelOrderDao = new cn.tootoo.db.egrocery.toscancelorder.TOsCancelOrderDao(uuid, logger);
        /*初始化DAO对象实例（表：T_SYS_SHIP_SDC）。*/
        tSysShipSdcDao = new cn.tootoo.db.egrocery.tsysshipsdc.TSysShipSdcDao(uuid, logger);
        /*初始化DAO对象实例（表：T_OS_ORDER_LOT）。*/
        tOsOrderLotDao = new cn.tootoo.db.egrocery.tosorderlot.TOsOrderLotDao(uuid, logger);
        /*初始化DAO对象实例（表：T_OS_PARENT_ORDER_OPT）。*/
        tOsParentOrderOptDao = new cn.tootoo.db.egrocery.tosparentorderopt.TOsParentOrderOptDao(uuid, logger);
        /*初始化DAO对象实例（表：OS_ORDER）。*/
        osOrderDao = new cn.tootoo.db.egrocery.osorder.OsOrderDao(uuid, logger);
        /*初始化DAO对象实例（表：T_BIND_ORDER）。*/
        tBindOrderDao = new cn.tootoo.db.egrocery.tbindorder.TBindOrderDao(uuid, logger);
        /*初始化DAO对象实例（表：T_SYS_ONLINE_CARD_GOODS）。*/
        tSysOnlineCardGoodsDao = new cn.tootoo.db.egrocery.tsysonlinecardgoods.TSysOnlineCardGoodsDao(uuid, logger);
        /*初始化DAO对象实例（表：BS_BUYER_BUY_FEE）。*/
        bsBuyerBuyFeeDao = new cn.tootoo.db.egrocery.bsbuyerbuyfee.BsBuyerBuyFeeDao(uuid, logger);
        /*初始化DAO对象实例（表：TTS_SHIPPING_COMPANY）。*/
        ttsShippingCompanyDao = new cn.tootoo.db.egrocery.ttsshippingcompany.TtsShippingCompanyDao(uuid, logger);
        /*初始化DAO对象实例（表：BS_BUYER）。*/
        bsBuyerDao = new cn.tootoo.db.egrocery.bsbuyer.BsBuyerDao(uuid, logger);
        /*初始化DAO对象实例（表：T_EIP_SUB_ORDER）。*/
        tEipSubOrderDao = new cn.tootoo.db.egrocery.teipsuborder.TEipSubOrderDao(uuid, logger);
        /*初始化DAO对象实例（表：OS_PAY_PLAN）。*/
        osPayPlanDao = new cn.tootoo.db.egrocery.ospayplan.OsPayPlanDao(uuid, logger);
        /*初始化DAO对象实例（表：OS_SUBORDER_SIGN_REQ）。*/
        osSuborderSignReqDao = new cn.tootoo.db.egrocery.ossubordersignreq.OsSuborderSignReqDao(uuid, logger);
        /*初始化DAO对象实例（表：BS_BUYER_SCORE_LOG）。*/
        bsBuyerScoreLogDao = new cn.tootoo.db.egrocery.bsbuyerscorelog.BsBuyerScoreLogDao(uuid, logger);
        /*初始化DAO对象实例（表：OS_RETURNORDER）。*/
        osReturnorderDao = new cn.tootoo.db.egrocery.osreturnorder.OsReturnorderDao(uuid, logger);
        /*初始化DAO对象实例（表：RETURN_POINTS_RULE）。*/
        returnPointsRuleDao = new cn.tootoo.db.egrocery.returnpointsrule.ReturnPointsRuleDao(uuid, logger);
        /*初始化DAO对象实例（表：T_OS_PARENT_ORDER_ITEMS）。*/
        tOsParentOrderItemsDao = new cn.tootoo.db.egrocery.tosparentorderitems.TOsParentOrderItemsDao(uuid, logger);
        /*调用子类方法。*/
        doInit(inputBean.getInputHead(), (OmsOrderCancelInputData)inputBean.getInputData(), outputBean.getOutputHead(), (OmsOrderCancelOutputData)outputBean.getOutputData());
    }
    
    /**
     * 初始检查方法。
     * 该方法在实际执行服务功能之前进行检查，默认返回true。
     * 
     * @param inputBean 该接口的请求参数。
     * @param outputBean 该接口的返回结果。
     * 
     * @return boolean 检查是否通过。
     */
    public final boolean check(final BaseInputBean inputBean, final BaseOutputBean outputBean) {
        /*检验请求参数数据体。*/
        if (inputBean.getInputData() != null) {
            if (!(inputBean.getInputData() instanceof OmsOrderCancelInputData)) {
                Global.getOutputHead(BaseResultEnum.REQUEST_PARAM_ERROR.getResultID(), null, outputBean.getOutputHead());
                LogUtils4Oms.error(logger, uuid, "服务委托者初始检查方法失败：请求参数数据体类型错误！", "inputBean.getInputData().class",inputBean.getInputData().getClass().getName());
                return false;
            }
        }
        /*调用子类方法。*/
        return doCheck(inputBean.getInputHead(), (OmsOrderCancelInputData)inputBean.getInputData(), outputBean.getOutputHead(), (OmsOrderCancelOutputData)outputBean.getOutputData());
    }
    
    /**
     * 功能执行方法。
     * 该方法中执行实际的服务功能。
     * 
     * @param inputBean 该接口的请求参数。
     * @param outputBean 该接口的返回结果。
     */
    public final void execute(final BaseInputBean inputBean, final BaseOutputBean outputBean) throws OmsDelegateException {
        /*检验请求参数数据体。*/
        if (inputBean.getInputData() != null) {
            if (!(inputBean.getInputData() instanceof OmsOrderCancelInputData)) {
                Global.getOutputHead(BaseResultEnum.REQUEST_PARAM_ERROR.getResultID(), null, outputBean.getOutputHead());
                LogUtils4Oms.error(logger, uuid, "服务委托者执行失败：请求参数数据体类型错误！", "inputBean.getInputData().class",inputBean.getInputData().getClass().getName());
                return;
            }
        }
        /*调用子类方法。*/
        doExecute(inputBean.getInputHead(), (OmsOrderCancelInputData)inputBean.getInputData(), outputBean.getOutputHead(), (OmsOrderCancelOutputData)outputBean.getOutputData());
    }
    
    /**
     * 关闭方法。
     * 该方法中通常实现如下功能：解锁、提交或回滚事务等等。
     * 
     * @param inputBean 该接口的请求参数。
     * @param outputBean 该接口的返回结果。
     */
    public final void close(final BaseInputBean inputBean, final BaseOutputBean outputBean) throws OmsDelegateException {
        /*检验请求参数数据体。*/
        if (inputBean.getInputData() != null) {
            if (!(inputBean.getInputData() instanceof OmsOrderCancelInputData)) {
                Global.getOutputHead(BaseResultEnum.REQUEST_PARAM_ERROR.getResultID(), null, outputBean.getOutputHead());
                LogUtils4Oms.error(logger, uuid, "服务委托者关闭失败：请求参数数据体类型错误！", "inputBean.getInputData().class",inputBean.getInputData().getClass().getName());
                return;
            }
        }
        /*调用子类方法。*/
        doClose(inputBean.getInputHead(), (OmsOrderCancelInputData)inputBean.getInputData(), outputBean.getOutputHead(), (OmsOrderCancelOutputData)outputBean.getOutputData());
    }
    
    /**
     * 初始化方法。
     * 需子类实现。
     * 该方法中通常实现如下功能：验证登录、加锁、初始化DAO对象等等。
     * 
     * @param inputHead 该接口的请求参数头。
     * @param inputData 该接口的请求参数数据体。
     * @param outputHead 该接口的返回结果头。
     * @param outputData 该接口的返回结果数据体。
     */
    public abstract void doInit(final BaseInputHead inputHead, final OmsOrderCancelInputData inputData, final BaseOutputHead outputHead, final OmsOrderCancelOutputData outputData) throws OmsDelegateException;
    
    /**
     * 初始检查方法。
     * 需子类实现。
     * 该方法在实际执行服务功能之前进行检查，默认返回true。
     * 
     * @param inputHead 该接口的请求参数头。
     * @param inputData 该接口的请求参数数据体。
     * @param outputHead 该接口的返回结果头。
     * @param outputData 该接口的返回结果数据体。
     * 
     * @return boolean 检查是否通过。
     */
    public abstract boolean doCheck(final BaseInputHead inputHead, final OmsOrderCancelInputData inputData, final BaseOutputHead outputHead, final OmsOrderCancelOutputData outputData);
    
    /**
     * 功能执行方法。
     * 需子类实现。
     * 该方法中执行实际的服务功能。
     * 
     * @param inputHead 该接口的请求参数头。
     * @param inputData 该接口的请求参数数据体。
     * @param outputHead 该接口的返回结果头。
     * @param outputData 该接口的返回结果数据体。
     */
    public abstract void doExecute(final BaseInputHead inputHead, final OmsOrderCancelInputData inputData, final BaseOutputHead outputHead, final OmsOrderCancelOutputData outputData) throws OmsDelegateException;
    
    /**
     * 关闭方法。
     * 需子类实现。
     * 该方法中通常实现如下功能：解锁、提交或回滚事务等等。
     * 
     * @param inputHead 该接口的请求参数头。
     * @param inputData 该接口的请求参数数据体。
     * @param outputHead 该接口的返回结果头。
     * @param outputData 该接口的返回结果数据体。
     */
    public abstract void doClose(final BaseInputHead inputHead, final OmsOrderCancelInputData inputData, final BaseOutputHead outputHead, final OmsOrderCancelOutputData outputData) throws OmsDelegateException;
}
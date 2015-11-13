package cn.tootoo.soa.oms.bindorderrefund;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import cn.tootoo.db.egrocery.osorder.OsOrderDao;
import cn.tootoo.db.egrocery.osorder.OsOrderPO;
import cn.tootoo.db.egrocery.osorderitems.OsOrderItemsDao;
import cn.tootoo.db.egrocery.osorderitems.OsOrderItemsPO;
import cn.tootoo.db.egrocery.osreturnorder.OsReturnorderPO;
import cn.tootoo.db.egrocery.osreturnorderdetail.OsReturnorderDetailDao;
import cn.tootoo.db.egrocery.osreturnorderdetail.OsReturnorderDetailPO;
import cn.tootoo.db.egrocery.tosparentorder.TOsParentOrderDao;
import cn.tootoo.db.egrocery.tosparentorder.TOsParentOrderPO;
import cn.tootoo.enums.BaseResultEnum;
import cn.tootoo.enums.BooleanEnum;
import cn.tootoo.http.TootooService;
import cn.tootoo.http.bean.BaseInputBean;
import cn.tootoo.http.bean.BaseOutputBean;
import cn.tootoo.servlet.BaseService;
import cn.tootoo.soa.base.enums.OrderStatusEnum;
import cn.tootoo.soa.base.enums.ParentOrderStatusEnum;
import cn.tootoo.soa.oms.bindorderrefund.input.OmsBindOrderRefundInputData;
import cn.tootoo.soa.oms.bindorderrefund.output.OmsBindOrderRefundOutputData;
import cn.tootoo.soa.oms.bindorderrefund.output.OmsBindOrderRefundResultEnum;
import cn.tootoo.soa.oms.utils.LogUtils4Oms;
import cn.tootoo.soa.payment.cancel.input.PaymentCancelInputData;
import cn.tootoo.soa.payment.cancel.input.PaymentCancelOrderInfoListElementI;
import cn.tootoo.soa.payment.cancel.output.PaymentCancelOutputData;
import cn.tootoo.soa.payment.pcancel.input.PaymentPCancelInputData;
import cn.tootoo.soa.payment.pcancel.output.PaymentPCancelOutputData;
import cn.tootoo.soa.payment.refund.input.PaymentRefundGoodsListElementI;
import cn.tootoo.soa.payment.refund.input.PaymentRefundInputData;
import cn.tootoo.soa.payment.refund.output.PaymentRefundOutputData;
import cn.tootoo.utils.Log;
import cn.tootoo.utils.ResponseUtil;

/**
 * 服务接口方法的实现类。
 * 
 * 服务description：订单服务。
 * 服务remark：订单服务。
 * 接口description：拦截订单退款
 * 接口remark：
 */
public final class BindOrderRefundImpl extends BaseService {
    
    /**
     * 日志对象。
     */
    private Logger logger = Logger.getLogger(BindOrderRefundImpl.class);
    
    
    @Override
    public BaseOutputBean doService(BaseInputBean inputBean) throws Exception {
        BaseOutputBean outputBean = new BaseOutputBean();
        try {
            LogUtils4Oms.info(logger, uuid, "执行服务开始，传入bean", "inputBean", inputBean);
            OmsBindOrderRefundOutputData outputData = new OmsBindOrderRefundOutputData();
            if (inputBean.getInputData() == null) {
                LogUtils4Oms.info(logger, uuid, "参数req_str为空", "inputBean", inputBean);
                outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.REQUEST_PARAM_ERROR.getResultID(), null, "");
                return outputBean;
            }
            OmsBindOrderRefundInputData inputData = null;
            try {
                inputData = (OmsBindOrderRefundInputData)inputBean.getInputData();
            } catch (Exception e) {
                LogUtils4Oms.error(logger, uuid, "接口实现类转换错误", e, "className", OmsBindOrderRefundInputData.class.getName());
                outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.CLASS_TRANSFER_ERROR.getResultID(), null, "");
                return outputBean;
            }
            
            TOsParentOrderDao tOsParentOrderDao = new TOsParentOrderDao(uuid, logger);
            OsOrderDao osOrderDao = new OsOrderDao(uuid, logger);
            OsOrderItemsDao osOrderItemsDao = new OsOrderItemsDao();
            Long orderId = inputData.getOrderId();
            Long refundType = inputData.getRefundType();
            BigDecimal refundFee = inputData.getRefundFee();
            Long userId = inputData.getUserId();
            
            if (refundType == 3L && (refundFee == null || BigDecimal.ZERO.equals(refundFee))) {
                LogUtils4Oms.info(logger, uuid, "投递失败退款金额参数错误", "refundType", refundType, "refundFee", refundFee);
                outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.REQUEST_PARAM_ERROR.getResultID(), null, "");
                LogUtils4Oms.info(logger, uuid, "接口返回", "outputBean", outputBean);
                return outputBean;
            }
            
            
            if (refundType == 1L) {
                // 父订单退款
                TOsParentOrderPO parentOrder = tOsParentOrderDao.findTOsParentOrderPOByID(orderId, false, false, false, true);
                
                if (parentOrder.getOrderStatus() != -4L) {
                    LogUtils4Oms.info(logger, uuid, "父订单状态不是异常状态", "orderStatus", parentOrder.getOrderStatus());
                    outputBean = ResponseUtil.getBaseOutputBean(OmsBindOrderRefundResultEnum.ORDER_NOT_CANCEL.getResultID(), null, "");
                    LogUtils4Oms.info(logger, uuid, "接口返回", "outputBean", outputBean);
                    return outputBean;
                }
                
                PaymentPCancelInputData paymentPCancelInputData = new PaymentPCancelInputData();
                paymentPCancelInputData.setOrderID(parentOrder.getOrderId());
                paymentPCancelInputData.setOrderCode(parentOrder.getOrderCode());
                paymentPCancelInputData.setUserID(userId);
                paymentPCancelInputData.setIsRefundCoupon(BooleanEnum.FALSE.getV());
                
                Map<String, String> paymentServiceParams = new HashMap<String, String>();
                paymentServiceParams.put("method", "pCancel");
                paymentServiceParams.put("req_str", paymentPCancelInputData.toJson());
                
                Log.info(logger, uuid, "调用payment服务pCancel方法开始", "orderCode", parentOrder.getOrderCode(), "paymentServiceParams", paymentServiceParams);            
                BaseOutputBean paymentOutputBean = TootooService.callServer("payment", paymentServiceParams, "post", new PaymentPCancelOutputData());
                Log.info(logger, uuid, "调用payment服务pCancel方法结束", "orderCode", parentOrder.getOrderCode(), "paymentOutputBean", paymentOutputBean);
                
                if (!TootooService.checkService(paymentOutputBean, "payment", "pCancel", parentOrder.getScope())) {
                    Log.info(logger, uuid, "调用payment服务pCancel方法失败", "orderCode", parentOrder.getOrderCode());
                    outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.INSIDE_ERROR.getResultID(), null, "");
                    LogUtils4Oms.info(logger, uuid, "接口返回", "outputBean", outputBean);
                    return outputBean;
                }
                Log.info(logger, uuid, "父订单作废退款成功！", "orderCode", parentOrder.getOrderCode());
                
                outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.SUCCESS.getResultID(), null, "");
                LogUtils4Oms.info(logger, uuid, "接口返回", "outputBean", outputBean);
                return outputBean;
                
                
            } else if (refundType == 2L) {
                // 子订单退款
                OsOrderPO osOrder = osOrderDao.findOsOrderPOByID(orderId, false, false, false, true);
                
                if (!"x".equals(osOrder.getOrderStatus())) {
                    LogUtils4Oms.info(logger, uuid, "子订单状态不是异常状态", "orderStatus", osOrder.getOrderStatus());
                    outputBean = ResponseUtil.getBaseOutputBean(OmsBindOrderRefundResultEnum.ORDER_NOT_CANCEL.getResultID(), null, "");
                    LogUtils4Oms.info(logger, uuid, "接口返回", "outputBean", outputBean);
                    return outputBean;
                }
                
                PaymentCancelInputData paymentCancelInputData = new PaymentCancelInputData();
                List<PaymentCancelOrderInfoListElementI> cancelOrderInfoList = new ArrayList<PaymentCancelOrderInfoListElementI>();
                PaymentCancelOrderInfoListElementI cancelOrderInfo = new PaymentCancelOrderInfoListElementI();
                cancelOrderInfo.setOrderId(osOrder.getOrderId());
                cancelOrderInfo.setOrderCode(osOrder.getOrderCode());
                cancelOrderInfoList.add(cancelOrderInfo);
                paymentCancelInputData.setOrderInfoList(cancelOrderInfoList);
                paymentCancelInputData.setUserID(userId);
                paymentCancelInputData.setIsRefundCoupon(BooleanEnum.FALSE.getV());
                
                Map<String, String> paymentServiceParams = new HashMap<String, String>();
                paymentServiceParams.put("method", "cancel");
                paymentServiceParams.put("req_str", paymentCancelInputData.toJson());
                
                Log.info(logger, uuid, "调用payment服务cancel方法开始", "paymentServiceParams", paymentServiceParams);
                BaseOutputBean paymentOutputBean = TootooService.callServer("payment", paymentServiceParams, "post", new PaymentCancelOutputData());
                Log.info(logger, uuid, "调用payment服务cancel方法结束", "paymentOutputBean", paymentOutputBean);
                
                if (!TootooService.checkService(paymentOutputBean, "payment", "cancel", null)) {
                    Log.info(logger, uuid, "调用payment服务cancel方法失败", "orderCode", osOrder.getOrderCode());
                    outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.INSIDE_ERROR.getResultID(), null, "");
                    LogUtils4Oms.info(logger, uuid, "接口返回", "outputBean", outputBean);
                    return outputBean;
                }
                Log.info(logger, uuid, "子订单作废退款成功！");
                
                
                outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.SUCCESS.getResultID(), null, "");
                LogUtils4Oms.info(logger, uuid, "接口返回", "outputBean", outputBean);
                return outputBean;
                
                
            } else if (refundType == 3L) {
                // 投递失败退款
                OsOrderPO osOrder = osOrderDao.findOsOrderPOByID(orderId, false, false, false, true);
                
                if (!"x".equals(osOrder.getOrderStatus())) {
                    LogUtils4Oms.info(logger, uuid, "子订单状态不是异常状态", "orderStatus", osOrder.getOrderStatus());
                    outputBean = ResponseUtil.getBaseOutputBean(OmsBindOrderRefundResultEnum.ORDER_NOT_CANCEL.getResultID(), null, "");
                    LogUtils4Oms.info(logger, uuid, "接口返回", "outputBean", outputBean);
                    return outputBean;
                }
                
                PaymentRefundInputData paymentRefundInputData = new PaymentRefundInputData();
                paymentRefundInputData.setOrderID(osOrder.getOrderId());
                paymentRefundInputData.setOrderCode(osOrder.getOrderCode());
                paymentRefundInputData.setRefundFee(refundFee); //boss后台已经加好
                paymentRefundInputData.setRefundType(1);
                paymentRefundInputData.setConfirmUser(userId);
                
                //使用了优惠券（>0) 若是礼盒传礼盒皮   payment不做处理，所以不需要传
                
                Map<String, String> paymentServiceParams = new HashMap<String, String>();
                paymentServiceParams.put("method", "refund");
                paymentServiceParams.put("req_str", paymentRefundInputData.toJson());
                
                
                
                Log.info(logger, uuid, "调用payment服务refund方法开始", "paymentServiceParams", paymentServiceParams);
                BaseOutputBean paymentOutputBean = TootooService.callServer("payment", paymentServiceParams, "post", new PaymentRefundOutputData());
                Log.info(logger, uuid, "调用payment服务refund方法结束", "paymentOutputBean", paymentOutputBean);
                
                if (!TootooService.checkService(paymentOutputBean, "payment", "refund", null)) {
                    Log.info(logger, uuid, "调用payment服务refund方法失败", "orderCode", osOrder.getOrderCode());
                    outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.INSIDE_ERROR.getResultID(), null, "");
                    LogUtils4Oms.info(logger, uuid, "接口返回", "outputBean", outputBean);
                    return outputBean;
                }
                Log.info(logger, uuid, "子订单投递失败退款成功！");
                
                
                outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.SUCCESS.getResultID(), null, "");
                LogUtils4Oms.info(logger, uuid, "接口返回", "outputBean", outputBean);
                return outputBean;
                
            } else {
                LogUtils4Oms.info(logger, uuid, "退款类型参数错误", "refundType", refundType);
                outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.REQUEST_PARAM_ERROR.getResultID(), null, "");
                LogUtils4Oms.info(logger, uuid, "接口返回", "outputBean", outputBean);
                return outputBean;
            }
            
        } catch (Exception e) {
            LogUtils4Oms.error(logger, uuid, "拦截订单退款请求出错", e);
            outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.INSIDE_ERROR.getResultID(), null, "");
            LogUtils4Oms.info(logger, uuid, "接口返回", "outputBean", outputBean);
            return outputBean;
        }
    }
    
    @Override
    public BaseService clone() throws CloneNotSupportedException {
        return new BindOrderRefundImpl();
    }
    
}
package cn.tootoo.soa.oms.bindsuborderreturn;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import cn.tootoo.db.egrocery.osorder.OsOrderDao;
import cn.tootoo.db.egrocery.osorder.OsOrderPO;
import cn.tootoo.db.egrocery.osorderitems.OsOrderItemsDao;
import cn.tootoo.db.egrocery.osorderitems.OsOrderItemsPO;
import cn.tootoo.db.egrocery.osreturnorder.OsReturnorderDao;
import cn.tootoo.db.egrocery.osreturnorder.OsReturnorderPO;
import cn.tootoo.db.egrocery.osreturnorderdetail.OsReturnorderDetailDao;
import cn.tootoo.db.egrocery.osreturnorderdetail.OsReturnorderDetailPO;
import cn.tootoo.db.egrocery.tbindorder.TBindOrderDao;
import cn.tootoo.db.egrocery.tbindorder.TBindOrderPO;
import cn.tootoo.enums.BaseResultEnum;
import cn.tootoo.http.TootooService;
import cn.tootoo.http.bean.BaseInputBean;
import cn.tootoo.http.bean.BaseOutputBean;
import cn.tootoo.servlet.BaseService;
import cn.tootoo.soa.base.enums.OrderStatusEnum;
import cn.tootoo.soa.base.enums.PayStatusEnum;
import cn.tootoo.soa.base.enums.RecvCashStatusEnum;
import cn.tootoo.soa.base.enums.ReturnorderCodeStatusEnum;
import cn.tootoo.soa.base.enums.ReturnorderTypeEnum;
import cn.tootoo.soa.oms.bindsuborderreturn.input.OmsBindSubOrderReturnInputData;
import cn.tootoo.soa.oms.bindsuborderreturn.output.OmsBindSubOrderReturnOutputData;
import cn.tootoo.soa.oms.bindsuborderreturn.output.OmsBindSubOrderReturnResultEnum;
import cn.tootoo.soa.oms.utils.LogUtils4Oms;
import cn.tootoo.soa.payment.refund.input.PaymentRefundGoodsListElementI;
import cn.tootoo.soa.payment.refund.input.PaymentRefundInputData;
import cn.tootoo.soa.payment.refund.output.PaymentRefundOutputData;
import cn.tootoo.utils.DateUtil;
import cn.tootoo.utils.Log;
import cn.tootoo.utils.ResponseUtil;

/**
 * 服务接口方法的实现类。
 * 服务description：订单服务。
 * 服务remark：订单服务。
 * 接口description：异常子订单退货
 * 接口remark：
 */
public final class BindSubOrderReturnImpl extends BaseService {
    
    /**
     * 日志对象。
     */
    private Logger logger = Logger.getLogger(BindSubOrderReturnImpl.class);
    
    @Override
    public BaseOutputBean doService(BaseInputBean inputBean) throws Exception {
        BaseOutputBean outputBean = new BaseOutputBean();
        OsOrderItemsDao osOrderItemsDao = new OsOrderItemsDao();
        try {
            LogUtils4Oms.info(logger, uuid, "执行服务开始，传入bean", "inputBean", inputBean);
            OmsBindSubOrderReturnOutputData outputData = new OmsBindSubOrderReturnOutputData();
            if (inputBean.getInputData() == null) {
                LogUtils4Oms.info(logger, uuid, "参数req_str为空", "inputBean", inputBean);
                outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.REQUEST_PARAM_ERROR.getResultID(), null, "");
                return outputBean;
            }
            OmsBindSubOrderReturnInputData inputData = null;
            try {
                inputData = (OmsBindSubOrderReturnInputData)inputBean.getInputData();
            } catch (Exception e) {
                LogUtils4Oms.error(logger, uuid, "接口实现类转换错误", e, "className", OmsBindSubOrderReturnInputData.class.getName());
                outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.CLASS_TRANSFER_ERROR.getResultID(), null, "");
                return outputBean;
            }
            
            LogUtils4Oms.info(logger, uuid, "异常订单退货开始", "inputData", inputData);
            OsReturnorderDao osReturnorderDao = new OsReturnorderDao(uuid, logger);
            OsOrderDao osOrderDao = new OsOrderDao(uuid, logger);
            TBindOrderDao tBindOrderDao = new TBindOrderDao(uuid, logger);
            
            List<Object[]> conditions = new ArrayList<Object[]>();
            Object[] condition = new Object[]{"RETURNORDER_CODE", "=", inputData.getReturnOrderCode()};
            conditions.add(condition);
            List<OsReturnorderPO> returnorderPOList = osReturnorderDao.findOsReturnorderPOListByCondition(conditions, false, false, true);
            
            if (returnorderPOList == null || returnorderPOList.size() != 1) {
                LogUtils4Oms.info(logger, uuid, "不存在退款单", "returnOrderCode", inputData.getReturnOrderCode());
                outputBean = ResponseUtil.getBaseOutputBean(OmsBindSubOrderReturnResultEnum.RETURNORDER_ERROR.getResultID(), null, null);
                return outputBean;
            }
            
            OsReturnorderPO returnOrder = returnorderPOList.get(0);
            LogUtils4Oms.info(logger, uuid, "查询退款单结束", "returnOrder", returnOrder);
            if (!ReturnorderCodeStatusEnum.NEW.getC().equals(returnOrder.getReturnorderCodeStatus())) {
                LogUtils4Oms.info(logger, uuid, "异常订单投递失败退货单状态需为【未审核】", "returnOrderCode", inputData.getReturnOrderCode());
                outputBean = ResponseUtil.getBaseOutputBean(OmsBindSubOrderReturnResultEnum.RETURNORDER_ERROR.getResultID(), null, null);
                return outputBean;
            }
            
            if (!OrderStatusEnum.DELIVER_FAILED.getC().equals(returnOrder.getReturnorderType())) {
                LogUtils4Oms.info(logger, uuid, "异常订单只能操作投递失败", "returnOrderCode", inputData.getReturnOrderCode());
                outputBean = ResponseUtil.getBaseOutputBean(OmsBindSubOrderReturnResultEnum.RETURNORDER_ERROR.getResultID(), null, null);
                return outputBean;
            }
            
            OsOrderPO order = osOrderDao.findOsOrderPOByID(returnOrder.getOrderId(), false, false, false, true);
            LogUtils4Oms.info(logger, uuid, "根据退款单查询订单表结束", "orderCode", order.getOrderCode());
            
    		
            if (!"x".equals(order.getOrderStatus())) {
                LogUtils4Oms.info(logger, uuid, "该订单不是异常拦截子订单", "orderCode", order.getOrderCode(), "orderStatus", order.getOrderStatus());
                outputBean = ResponseUtil.getBaseOutputBean(OmsBindSubOrderReturnResultEnum.RETURN_FAIL.getResultID(), null, null);
                return outputBean;
            }
            
            List<Object[]> bindOrderConditions = new ArrayList<Object[]>();
            bindOrderConditions.add(new Object[]{"STATUS", "=", "0"});
            bindOrderConditions.add(new Object[]{"IS_PARENT", "=", "0"});
            bindOrderConditions.add(new Object[]{"ORDER_CODE", "=", order.getOrderCode()});
            List<TBindOrderPO> bindOrderList = tBindOrderDao.findTBindOrderPOListByCondition(bindOrderConditions, false, false, true);
            if (bindOrderList == null || bindOrderList.size() != 1) {
                LogUtils4Oms.info(logger, uuid, "异常订单查询历史状态错误!", "orderCode", order.getOrderCode());
                outputBean = ResponseUtil.getBaseOutputBean(OmsBindSubOrderReturnResultEnum.RETURN_FAIL.getResultID(), null, null);
                return outputBean;
            }
            LogUtils4Oms.info(logger, uuid, "订单拦截表信息", "tBindOrderPO", bindOrderList.get(0));
            String realOrderStatus = bindOrderList.get(0).getOrderStatus();
            
            // 如果历史状态是“已出库” 至 “派件中”
            if (OrderStatusEnum.OUT_OF_STORAGE.getC().equals(realOrderStatus) || OrderStatusEnum.DELIVERY_RECEIVED.getC().equals(realOrderStatus)
                            || OrderStatusEnum.SHIPPING.getC().equals(realOrderStatus) || OrderStatusEnum.SELF_SEND.getC().equals(realOrderStatus)
                            || OrderStatusEnum.THIRD_SEND.getC().equals(realOrderStatus)
                            || OrderStatusEnum.CENTER_RECEIVED.getC().equals(realOrderStatus)
                            || OrderStatusEnum.CENTER_SEND.getC().equals(realOrderStatus)
                            || OrderStatusEnum.SDC_RECEIVED.getC().equals(realOrderStatus) || OrderStatusEnum.SENDING.getC().equals(realOrderStatus)
                            || OrderStatusEnum.DELAY.getC().equals(realOrderStatus)) {
                realOrderStatus = OrderStatusEnum.SENDING.getC();
            }
            LogUtils4Oms.info(logger, uuid, "异常拦截订单获取订单状态", "oldStatus", bindOrderList.get(0).getOrderStatus(), "newStatus", realOrderStatus);
            
            LogUtils4Oms.info(logger, uuid, "是否退款条件", "isRefund", inputData.getIsRefund(), "returnOrder.getAmount", returnOrder.getAmount());
            PaymentRefundOutputData refundOutputData;
            if (inputData.getIsRefund() != 0L && returnOrder.getAmount().compareTo(BigDecimal.ZERO) > 0) {
                // 调用支付接口退款
                refundOutputData = paymentReturn(osOrderItemsDao,order, returnOrder, inputData.getConfirmUser(), inputData.getConfirmUserName());
            } else {
                refundOutputData = new PaymentRefundOutputData();
                refundOutputData.setOnlineRefundFee(BigDecimal.ZERO);
                refundOutputData.setOfflineRefundFee(BigDecimal.ZERO);
                refundOutputData.setGiftCardRefundFee(BigDecimal.ZERO);
                if (inputData.getIsRefund() != 0L) {
                    LogUtils4Oms.info(logger, uuid, "退款的为零元订单", "returnOrder.getAmount", returnOrder.getAmount());
                } else {
                    LogUtils4Oms.info(logger, uuid, "异常拦截订单选择不退款", "isRefund", inputData.getIsRefund(), "returnOrder.getAmount()：", returnOrder.getAmount());
                }
            }
            
            if (refundOutputData == null) {
                LogUtils4Oms.info(logger, uuid, "调用退款接口失败", "orderCode", order.getOrderCode());
                outputBean = ResponseUtil.getBaseOutputBean(OmsBindSubOrderReturnResultEnum.RETURNFEE_FAIL.getResultID(), null, null);
                return outputBean;
            }
            
            if (order.getReturnFee() == null || "".equals(order.getReturnFee().toString())) {
                order.setReturnFee(returnOrder.getAmount());
            } else {
                order.setVariationReturnFee(returnOrder.getAmount());
            }
            Timestamp time = DateUtil.strToTimestamp(DateUtil.getCurrentDatetime());
            
            // 复核信息
            if (OrderStatusEnum.SENDING.getC().equals(realOrderStatus)) {
                order.setCheckUserId(inputData.getConfirmUser());
                order.setCheckDt(DateUtil.strToTimestamp(DateUtil.getCurrentDatetime()));
            }
            // LogUtils4Oms.info(logger, uuid, "子订状态变动", "realOrderStatus：", realOrderStatus, "ReturnorderType：", returnOrder.getReturnorderType());
            // order.setOrderStatus(returnOrder.getReturnorderType());
            if (ReturnorderTypeEnum.isSigned(returnOrder.getReturnorderType())) {// 被审核的退货单是签收时（部分收货、全部拒收、投递失败）生成的退货单
                order.setSignDt(time);
            }
            if (returnOrder.getReturnorderType().equals(OrderStatusEnum.DELIVER_FAILED.getC())
                            || returnOrder.getReturnorderType().equals(OrderStatusEnum.REJECTED.getC())) {
                order.setRecvCashStatus(RecvCashStatusEnum.OK.getC());
                order.setRealReceivedDate(time);
            } else if (returnOrder.getReturnorderType().equals(OrderStatusEnum.PART_SIGNED.getC())) {
                order.setRealReceivedDate(time);
                // 更新订单收款确认状态：
                if (order.getOfflinePayFee().compareTo(new BigDecimal(0)) == 0) {
                    order.setRecvCashStatus(RecvCashStatusEnum.OK.getC());
                } else if (order.getOfflinePayFee().compareTo(new BigDecimal(0)) > 0) {
                    order.setRecvCashStatus(RecvCashStatusEnum.NO.getC());
                }
                order.setPayStatus(PayStatusEnum.YES.getC());
                order.setPayDt(time);
            }
            
            int resultNum = osOrderDao.updateOsOrderPO(order);
            if (resultNum != 1) {
                osOrderDao.rollback();
                LogUtils4Oms.info(logger, uuid, "更新子订单表信息失败", "orderCode", order.getOrderCode());
                outputBean = ResponseUtil.getBaseOutputBean(OmsBindSubOrderReturnResultEnum.RETURN_FAIL.getResultID(), null, null);
                return outputBean;
            }
            
            /*Long optId = osOrderOptDao.findSeqNextVal("SEQ_OS_ORDER_OPT_PK");
            OsOrderOptPO opt = new OsOrderOptPO();
            opt.setOptId(optId);
            opt.setOptDt(DateUtil.strToTimestamp(DateUtil.getCurrentDatetime()));
            opt.setOrderId(order.getOrderId());
            opt.setOrderDisputeStatus(order.getDisputeStatus());
            opt.setOrderStatus(returnOrder.getReturnorderType());
            opt.setOrderExceptionStatus(order.getExceptionStatus());
            opt.setOrderPayStatus(order.getPayStatus());
            opt.setUserName(inputData.getConfirmUserName());
            opt.setUserId(inputData.getConfirmUser());
            opt.setNotifyStatus("0");
            if (OrderStatusEnum.POS_REJECTED.getC().equals(returnOrder.getReturnorderType())) {
                opt.setRemark("您的订单已被拒收");
                opt.setRemarkEn("Your order has been rejected.");
                opt.setIsShow("1");
            } else if (OrderStatusEnum.POS_DELIVER_FAILED.getC().equals(returnOrder.getReturnorderType())) {
                opt.setRemark("您的订单由于无人签收已投递失败");
                opt.setRemarkEn("Your order is failed to deliver for no one sign for.");
                opt.setIsShow("1");
            } else if (OrderStatusEnum.DELIVER_FAILED.getC().equals(returnOrder.getReturnorderType())) {
                opt.setRemark("您的订单由于无人签收已投递失败");
                opt.setRemarkEn("Your order is failed to deliver for no one sign for.");
                opt.setIsShow("1");
            } else if (OrderStatusEnum.REJECTED.getC().equals(returnOrder.getReturnorderType())) {
                opt.setRemark("您的订单已被拒收");
                opt.setRemarkEn("Your order has been rejected.");
                opt.setIsShow("1");
            } else if (OrderStatusEnum.ALL_SIGNED_RETURN.getC().equals(returnOrder.getReturnorderType())) {
                opt.setRemark("您的订单已退货");
                opt.setIsShow("0");
            } else if (OrderStatusEnum.PART_SIGNED_RETURN.getC().equals(returnOrder.getReturnorderType())) {
                opt.setRemark("您的订单已退货");
                opt.setIsShow("0");
            } else {
                opt.setRemark("This is system added!");
                opt.setIsShow("0");
            }
            
            resultNum = osOrderOptDao.addOsOrderOptPO(opt);
            if (resultNum != 1) {
                osOrderOptDao.rollback();
                LogUtils4Oms.info(logger, uuid, "添加子订单日志失败", "orderCode", order.getOrderCode());
                outputBean = ResponseUtil.getBaseOutputBean(OmsBindSubOrderReturnResultEnum.RETURN_FAIL.getResultID(), null, null);
                return outputBean;
            }*/
            
            osOrderDao.commit();
            LogUtils4Oms.info(logger, uuid, "异常子订单投递失败成功", "order", order);
            outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.SUCCESS.getResultID(), null, null);
            return outputBean;
            
        } catch (Exception e) {
            LogUtils4Oms.error(logger, uuid, "异常子订单退货请求出错", e);
            outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.INSIDE_ERROR.getResultID(), null, "");
            LogUtils4Oms.info(logger, uuid, "接口返回", "outputBean", outputBean);
            return outputBean;
        }
    }
    
    private PaymentRefundOutputData paymentReturn(OsOrderItemsDao osOrderItemsDao,OsOrderPO order, OsReturnorderPO returnOrder, Long confirmUser, String confirmUserName) {
        PaymentRefundOutputData output = null;
        try {
            PaymentRefundInputData input = new PaymentRefundInputData();
            input.setOrderCode(order.getOrderCode());
            input.setOrderID(order.getOrderId());
            BigDecimal couponAmount = returnOrder.getCouponAmount()==null ?BigDecimal.valueOf(0):returnOrder.getCouponAmount();
            input.setRefundFee(returnOrder.getAmount().add(couponAmount));
            
            input.setRefundType(1);
            /*
             * if (refundType == 3) {
             * // 签收后退货,传退货单号，其他情况可以不传
             * input.setReturnOrderCode(returnOrder.getReturnorderCode());
             * }
             */
            input.setConfirmUser(confirmUser);
            //使用了优惠券 
            if(couponAmount.compareTo(BigDecimal.valueOf(0))>0){
            	OsReturnorderDetailDao detailDao = new OsReturnorderDetailDao();
            	List<PaymentRefundGoodsListElementI> paymentGoodsList = new ArrayList<PaymentRefundGoodsListElementI>();
            	
                List<Object[]> conditions = new ArrayList<Object[]>();
        		conditions.add(new Object[]{"RETURNORDER_ID", "=", returnOrder.getReturnorderId()});
                List<OsReturnorderDetailPO> detailPOList = detailDao.findOsReturnorderDetailPOListByCondition(conditions);
                
                Map<Long, BigDecimal> goodsMap = new HashMap<Long, BigDecimal>();
                for (OsReturnorderDetailPO returnorderDetailPO : detailPOList) {
                	OsOrderItemsPO orderItemsPO = osOrderItemsDao.findOsOrderItemsPOByID(returnorderDetailPO.getOrderItemId());
                	if(orderItemsPO.getGiftboxId()!=0){
                		orderItemsPO = osOrderItemsDao.findOsOrderItemsPOByID(Long.valueOf(orderItemsPO.getGiftboxId()));
                	}
                	if(goodsMap.containsKey(orderItemsPO.getGoodsId())){
            			BigDecimal couponFee = goodsMap.get(orderItemsPO.getGoodsId()).add(returnorderDetailPO.getTcouponFee());
            			goodsMap.put(orderItemsPO.getGoodsId(),couponFee);
            			
            		}else{
            			goodsMap.put(orderItemsPO.getGoodsId(),returnorderDetailPO.getTcouponFee());
            		}
                	
				}
                
                //循环map设置新值
                for (Long key : goodsMap.keySet()) {
                	PaymentRefundGoodsListElementI goodsElementI = new PaymentRefundGoodsListElementI();
                    goodsElementI.setGoodsID(key);
                 	goodsElementI.setRefundCouponFee(goodsMap.get(key));
                 	paymentGoodsList.add(goodsElementI);
				}
                input.setGoodsList(paymentGoodsList);
            }
         
            Map<String, String> paymentServiceParams = new HashMap<String, String>();
            paymentServiceParams.put("method", "refund");
            paymentServiceParams.put("req_str", input.toJson());
            
            Log.info(logger, uuid, "调用payment服务refund方法开始", "orderCode", order.getOrderCode(), "paymentServiceParams", paymentServiceParams);
            BaseOutputBean paymentOutputBean = TootooService.callServer("payment", paymentServiceParams, "post", new PaymentRefundOutputData());
            
            Log.info(logger, uuid, "调用payment服务refund方法结束", "orderCode", order.getOrderCode(), "paymentOutputBean", paymentOutputBean);
            
            if (!TootooService.checkService(paymentOutputBean, "payment", "refund", null)) {
                Log.info(logger, uuid, "调用payment服务refund方法失败", "orderCode", order.getOrderCode());
                return output;
            }
            output=(PaymentRefundOutputData)paymentOutputBean.getOutputData();
            Log.info(logger, uuid, "调用退款接口成功！", "orderCode", order.getOrderCode());
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils4Oms.info(logger, uuid, "调用退款接口异常", e);
        }
        return output;
    }
    
    @Override
    public BaseService clone() throws CloneNotSupportedException {
        return new BindSubOrderReturnImpl();
    }
    
}
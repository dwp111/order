package cn.tootoo.soa.oms.suborderreturn.delegate;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.tootoo.db.egrocery.osorder.OsOrderPO;
import cn.tootoo.db.egrocery.osorderitems.OsOrderItemsPO;
import cn.tootoo.db.egrocery.osorderopt.OsOrderOptPO;
import cn.tootoo.db.egrocery.osreturnorder.OsReturnorderPO;
import cn.tootoo.db.egrocery.osreturnorderdetail.OsReturnorderDetailDao;
import cn.tootoo.db.egrocery.osreturnorderdetail.OsReturnorderDetailPO;
import cn.tootoo.enums.BaseResultEnum;
import cn.tootoo.global.Global;
import cn.tootoo.http.AuthorizeClient;
import cn.tootoo.http.TootooService;
import cn.tootoo.http.bean.BaseInputHead;
import cn.tootoo.http.bean.BaseOutputBean;
import cn.tootoo.http.bean.BaseOutputHead;
import cn.tootoo.soa.base.enums.OrderStatusEnum;
import cn.tootoo.soa.base.enums.OrderTypeEnum;
import cn.tootoo.soa.base.enums.PayStatusEnum;
import cn.tootoo.soa.base.enums.ReceiveOrderStatusEnum;
import cn.tootoo.soa.base.enums.RecvCashStatusEnum;
import cn.tootoo.soa.base.enums.ReturnorderCodeStatusEnum;
import cn.tootoo.soa.base.enums.ReturnorderTypeEnum;
import cn.tootoo.soa.base.enums.YesOrNoEnum;
import cn.tootoo.soa.oms.buyerbuyscoreservice.BuyerBuyScoreService;
import cn.tootoo.soa.oms.exceptions.OmsDelegateException;
import cn.tootoo.soa.oms.suborderreturn.input.OmsSubOrderReturnInputData;
import cn.tootoo.soa.oms.suborderreturn.output.OmsSubOrderReturnOutputData;
import cn.tootoo.soa.oms.suborderreturn.output.OmsSubOrderReturnResultEnum;
import cn.tootoo.soa.oms.utils.LogUtils4Oms;
import cn.tootoo.soa.payment.refund.input.PaymentRefundGoodsListElementI;
import cn.tootoo.soa.payment.refund.input.PaymentRefundInputData;
import cn.tootoo.soa.payment.refund.output.PaymentRefundOutputData;
import cn.tootoo.utils.DateUtil;

/**
 * 服务委托者实现类。
 * 
 * 服务description：订单服务。
 * 服务remark：订单服务。
 * 接口description：子订单退货
 * 接口remark：
 */
public final class SubOrderReturnDelegate extends AbstractSubOrderReturnDelegate implements Cloneable {
    
    static {
        SubOrderReturnDelegateFactory.registPrototype(new SubOrderReturnDelegate());
    }
    
    /**
     * 克隆方法。
     */
    @Override
    public AbstractSubOrderReturnDelegate clone() throws CloneNotSupportedException {
        return new SubOrderReturnDelegate();
    }
    
    /**
     * 初始化方法。
     * 该方法中通常实现如下功能：验证登录、加锁、初始化DAO对象等等。
     * 
     * @param inputHead 该接口的请求参数头。
     * @param inputData 该接口的请求参数数据体。
     * @param outputHead 该接口的返回结果头。
     * @param outputData 该接口的返回结果数据体。
     */
    @Override
    public void doInit(final BaseInputHead inputHead, final OmsSubOrderReturnInputData inputData, final BaseOutputHead outputHead, final OmsSubOrderReturnOutputData outputData) throws OmsDelegateException {
    }
    
    /**
     * 初始检查方法。
     * 该方法在实际执行服务功能之前进行检查，默认返回true。
     * 
     * @param inputHead 该接口的请求参数头。
     * @param inputData 该接口的请求参数数据体。
     * @param outputHead 该接口的返回结果头。
     * @param outputData 该接口的返回结果数据体。
     * 
     * @return boolean 检查是否通过。
     */
    @Override
    public boolean doCheck(final BaseInputHead inputHead, final OmsSubOrderReturnInputData inputData, final BaseOutputHead outputHead, final OmsSubOrderReturnOutputData outputData) {
        try {
            return true;
        } catch (Exception e) {
            LogUtils4Oms.error(logger, uuid, "初始检查方法出现异常，返回false！", e);
            return false;
        }
    }
    
    /**
     * 功能执行方法。
     * 该方法中执行实际的服务功能。
     * 
     * @param inputHead 该接口的请求参数头。
     * @param inputData 该接口的请求参数数据体。
     * @param outputHead 该接口的返回结果头。
     * @param outputData 该接口的返回结果数据体。
     */
    @Override
    public void doExecute(final BaseInputHead inputHead, final OmsSubOrderReturnInputData inputData, final BaseOutputHead outputHead, final OmsSubOrderReturnOutputData outputData) throws OmsDelegateException {
        LogUtils4Oms.info(logger, uuid, "子订单退货开始","inputData",inputData);
        
        List<Object[]> conditions=new ArrayList<Object[]>();
        Object[] condition=new Object[]{"RETURNORDER_CODE","=",inputData.getReturnOrderCode()};
        conditions.add(condition);
        List<OsReturnorderPO> returnorderPOList = osReturnorderDao.findOsReturnorderPOListByCondition(conditions);
        if(returnorderPOList!=null&&returnorderPOList.size()>0){
            OsReturnorderPO returnOrder = returnorderPOList.get(0);
            OsOrderPO order = osOrderDao.findOsOrderPOByID(returnOrder.getOrderId(), false, false, false, true);
            
            
            if ("x".equals(order.getOrderStatus())){
                LogUtils4Oms.info(logger, uuid, "异常订单不允许在此进行退货", "orderStatus", order.getOrderStatus());
                Global.getOutputHead(OmsSubOrderReturnResultEnum.RETURN_FAIL.getResultID(), null, outputHead);
                return;
            }
            boolean isReturn = false;
            StringBuffer error=new StringBuffer("退货单状态异常不允许退货：");
            if(String.valueOf(ReturnorderTypeEnum.ALL_SIGNED_RETURN.c()).equals(returnOrder.getReturnorderType())){
                if(YesOrNoEnum.YES.getC().equals(returnOrder.getIsRetrieve())){
                    if(ReceiveOrderStatusEnum.DEFAULT.getC().equals(returnOrder.getReceiveStatus())){
                        isReturn=true;
                    }else{
                        error.append("全签退货单 是需要取件退货单 但【取货单状态】不为【未审核】");
                    }
                }else{
                    if(ReturnorderCodeStatusEnum.NEW.getC().equals(returnOrder.getReturnorderCodeStatus())){
                        isReturn=true;
                    }else{
                        error.append("全签退货单  是不需要取件退货单 但【退受单状态】不为【未审核】");
                    }
                } 
            }else{
                if(ReturnorderCodeStatusEnum.NEW.getC().equals(returnOrder.getReturnorderCodeStatus())){
                    isReturn=true;
                }else{
                    error.append("非全签退货单  【退受单状态】不为【未审核】");
                }
            }
            
            if(isReturn){
                if(checkTiHuoOrderRefund(order, returnOrder)){
                    PaymentRefundOutputData refundOutputData;
                    BigDecimal big0 = new BigDecimal(0.0);
                    if(returnOrder.getAmount().compareTo(big0)>0){
                         refundOutputData = paymentReturn(order,returnOrder,inputData.getConfirmUser(),inputData.getConfirmUserName());
                    }else{
                        refundOutputData=new PaymentRefundOutputData();
                        refundOutputData.setOnlineRefundFee(big0);
                        refundOutputData.setOfflineRefundFee(big0);
                        refundOutputData.setGiftCardRefundFee(big0);
                        LogUtils4Oms.info(logger, uuid, "#########  退款的为零元订单","returnOrder.getAmount()：",returnOrder.getAmount());
                    }
                    
                    if(refundOutputData!=null){
                        try {
                            if (order.getReturnFee() == null || "".equals(order.getReturnFee().toString())) {
                                order.setReturnFee(returnOrder.getAmount());
                            } else {
                                order.setVariationReturnFee(returnOrder.getAmount());
                            }
                            Timestamp time = DateUtil.strToTimestamp(DateUtil.getCurrentDatetime());
                          
                            //复核信息                        
                            if(OrderStatusEnum.SENDING.getC().equals(order.getOrderStatus())){
                                order.setCheckUserId(inputData.getConfirmUser());
                                order.setCheckDt(DateUtil.strToTimestamp(DateUtil.getCurrentDatetime()));
                            }
                            LogUtils4Oms.info(logger, uuid, "子订状态变动","OrderStatus：",order.getOrderStatus(),"ReturnorderType：",returnOrder.getReturnorderType());
                            //换货退货 修改
                            order.setOrderStatus(returnOrder.getReturnorderType().equals("6")?OrderStatusEnum.ALL_SIGNED_RETURN.getC():returnOrder.getReturnorderType());
//                            order.setOrderStatus(returnOrder.getReturnorderType());
                            if(ReturnorderTypeEnum.isSigned(returnOrder.getReturnorderType())){//被审核的退货单是签收时（部分收货、全部拒收、投递失败）生成的退货单
                                order.setSignDt(time);
                            }
                            if (order.getOrderStatus().equals(OrderStatusEnum.DELIVER_FAILED.getC())|| order.getOrderStatus().equals(OrderStatusEnum.REJECTED.getC())) {
                                order.setRecvCashStatus(RecvCashStatusEnum.OK.getC());
                                order.setRealReceivedDate(time);
                            } else if (order.getOrderStatus().equals(OrderStatusEnum.PART_SIGNED.getC())) {
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
                            
                            Long optId =osOrderOptDao.findSeqNextVal("SEQ_OS_ORDER_OPT_PK");
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
                            if(OrderStatusEnum.POS_REJECTED.getC().equals(returnOrder.getReturnorderType())){
                                opt.setRemark("您的订单已被拒收");
                                opt.setRemarkEn("Your order has been rejected.");
                                opt.setIsShow("1");
                            }else if (OrderStatusEnum.POS_DELIVER_FAILED.getC().equals(returnOrder.getReturnorderType())) {
                                opt.setRemark("您的订单由于无人签收已投递失败");
                                opt.setRemarkEn("Your order is failed to deliver for no one sign for.");
                                opt.setIsShow("1");
                            }else if (OrderStatusEnum.DELIVER_FAILED.getC().equals(returnOrder.getReturnorderType())) {
                                opt.setRemark("您的订单由于无人签收已投递失败");
                                opt.setRemarkEn("Your order is failed to deliver for no one sign for.");
                                opt.setIsShow("1");
                            }else if (OrderStatusEnum.REJECTED.getC().equals(returnOrder.getReturnorderType())) {
                                opt.setRemark("您的订单已被拒收");
                                opt.setRemarkEn("Your order has been rejected.");
                                opt.setIsShow("1");
                                //换货退货 修改
                            }else if (OrderStatusEnum.ALL_SIGNED_RETURN.getC().equals(returnOrder.getReturnorderType()) || returnOrder.getReturnorderType().equals("6")) {
//                            }else if (OrderStatusEnum.ALL_SIGNED_RETURN.getC().equals(returnOrder.getReturnorderType())) {
                                opt.setRemark("您的订单已退货");
                                opt.setIsShow("0");
                            }else if (OrderStatusEnum.PART_SIGNED_RETURN.getC().equals(returnOrder.getReturnorderType())) {
                                opt.setRemark("您的订单已退货");
                                opt.setIsShow("0");
                            }else{
                                opt.setRemark("This is system added!");
                                opt.setIsShow("0");
                            }
                           
                            osOrderOptDao.addOsOrderOptPO(opt);
                            osOrderOptDao.commit();
                            osOrderDao.updateOsOrderPO(order);
                            osOrderDao.commit();
                            if(String.valueOf(ReturnorderTypeEnum.ALL_SIGNED_RETURN.c()).equals(returnOrder.getReturnorderType())){
                                BuyerBuyScoreService buyerBuyScoreService=new BuyerBuyScoreService(logger,uuid);
                                buyerBuyScoreService.updateRefundScore(order, refundOutputData.getOfflineRefundFee().add(refundOutputData.getOnlineRefundFee()), refundOutputData.getGiftCardRefundFee());
                            }
                            Global.getOutputHead(BaseResultEnum.SUCCESS.getResultID(), null, outputHead);
                            LogUtils4Oms.info(logger, uuid, "子订单退货成功","order",order);
                        } catch (Exception e) {
                            osOrderDao.rollback();
                            osOrderOptDao.rollback();
                            LogUtils4Oms.info(logger, uuid, "子订单退货异常",e);
                            Global.getOutputHead(OmsSubOrderReturnResultEnum.RETURN_FAIL_A.getResultID(), null, outputHead);
                        }
                    }else{
                        LogUtils4Oms.info(logger, uuid, "调用退款接口失败");
                        Global.getOutputHead(OmsSubOrderReturnResultEnum.RETURN_FAIL_B.getResultID(), null, outputHead);
                    }
                }else{
                    LogUtils4Oms.info(logger, uuid, "该子订单状态是提货卡订单，因兄弟订单状态不为退货!");
                    Global.getOutputHead(OmsSubOrderReturnResultEnum.RETURN_FAIL_C.getResultID(), null, outputHead);
                }
                
            }else{
                LogUtils4Oms.info(logger, uuid, error.toString());
                Global.getOutputHead(OmsSubOrderReturnResultEnum.RETURN_FAIL_D.getResultID(), null, outputHead);
            }
        }else{
            LogUtils4Oms.info(logger, uuid, "不存在退款单");
            Global.getOutputHead(OmsSubOrderReturnResultEnum.RETURN_FAIL_E.getResultID(), null, outputHead);
        }
    }

    private PaymentRefundOutputData paymentReturn(OsOrderPO order, OsReturnorderPO returnOrder, Long confirmUser,String confirmUserName) {
        PaymentRefundOutputData output = null;
        try {
            int refundType = -1;
            if (String.valueOf(ReturnorderTypeEnum.DELIVER_FAILED.c()).equals(returnOrder.getReturnorderType())) {
                refundType = 1;
            } else if (String.valueOf(ReturnorderTypeEnum.REJECTED.c()).equals(returnOrder.getReturnorderType())) {
                refundType = 2;
            } else if (String.valueOf(ReturnorderTypeEnum.ALL_SIGNED_RETURN.c()).equals(returnOrder.getReturnorderType())) {
                refundType = 3;
            } else if (String.valueOf(ReturnorderTypeEnum.PART_SIGNED_RETURN.c()).equals(returnOrder.getReturnorderType())) {
                refundType = 3;
                
            } //换货退货
            else if (String.valueOf(ReturnorderTypeEnum.CHANGE_TO_RETURN.c()).equals(returnOrder.getReturnorderType())) {
                refundType = 3;
            } 
            if (refundType != -1) {
                PaymentRefundInputData input = new PaymentRefundInputData();
                input.setOrderCode(order.getOrderCode());
                input.setOrderID(order.getOrderId());
                BigDecimal couponAmount = returnOrder.getCouponAmount()==null ?BigDecimal.valueOf(0):returnOrder.getCouponAmount();
                
             	
                input.setRefundFee(returnOrder.getAmount().add(couponAmount));
                input.setRefundType(refundType);
                if(refundType == 3){
                    // 签收后退货,传退货单号，其他情况可以不传
                    input.setReturnOrderCode(returnOrder.getReturnorderCode());
                }
                input.setConfirmUser(confirmUser);
                
                //使用了优惠券 
                if(couponAmount.compareTo(BigDecimal.valueOf(0))>0){
                	OsReturnorderDetailDao detailDao = new OsReturnorderDetailDao();
                	List<PaymentRefundGoodsListElementI> paymentGoodsList = new ArrayList<PaymentRefundGoodsListElementI>();
                	
                    List<Object[]> conditions = new ArrayList<Object[]>();
            		conditions.add(new Object[]{"RETURNORDER_ID", "=", returnOrder.getReturnorderId()});
                    List<OsReturnorderDetailPO> detailPOList = detailDao.findOsReturnorderDetailPOListByCondition(conditions);
                    LogUtils4Oms.info(logger, uuid, "退货单列表！", "detailPOList",detailPOList);
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
                    	if(BigDecimal.valueOf(0L).compareTo(goodsMap.get(key))==0){
                        	continue;
                        }
                    	PaymentRefundGoodsListElementI goodsElementI = new PaymentRefundGoodsListElementI();
                        goodsElementI.setGoodsID(key);
                     	goodsElementI.setRefundCouponFee(goodsMap.get(key));
                     	paymentGoodsList.add(goodsElementI);
					}
                    input.setGoodsList(paymentGoodsList);
                }
 
                HashMap<String, String> params = (HashMap<String, String>)AuthorizeClient.getVerifyInfo(this.httpRequest);
                Map<String, String> paramsMap = (Map<String, String>)params.clone();
                paramsMap.put("method", "refund");
                paramsMap.put("req_str", input.toJson());
                PaymentRefundOutputData paymentRefundOutputData = new PaymentRefundOutputData();
                LogUtils4Oms.info(logger, uuid, "调用退款接口入参","paymentInput", input.toJson());
                BaseOutputBean baseBean = TootooService.callServer("payment", paramsMap, "post", paymentRefundOutputData);
                if (baseBean.getOutputHead().getResultID().equals(BaseResultEnum.SUCCESS.getResultID())) {
                    output=(PaymentRefundOutputData)baseBean.getOutputData();
                    LogUtils4Oms.info(logger, uuid, "调用退款接口成功", "input", input.toJson(),"baseBean",baseBean);
                }else{
                    LogUtils4Oms.info(logger, uuid, "调用退款接口失败", "input", input.toJson(),"baseBean",baseBean);
                }
            }else{
                LogUtils4Oms.info(logger, uuid, "调用退款接口失败，不存在此refundType","refundType",refundType);
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils4Oms.info(logger, uuid, "调用退款接口异常",e);
        }
        return output;
    }
    
    boolean checkTiHuoOrderRefund(OsOrderPO orderPo, OsReturnorderPO returnOrder){
        String returnOrderTYpe = returnOrder.getReturnorderType();
        
        if(OrderTypeEnum.isTiHuoOrder(orderPo.getOrderType())){
            List<Object[]> condition=new ArrayList<Object[]>();
            Object[] conditionArr=new Object[]{"PARENT_ID","=",orderPo.getParentId()};
            Object[] conditionArr2=new Object[]{"ORDER_ID","<>",orderPo.getOrderId()};
            condition.add(conditionArr);
            condition.add(conditionArr2);
            List<OsOrderPO> orderPOs = osOrderDao.findOsOrderPOListByCondition(condition);
            if (orderPOs != null && orderPOs.size() > 0){
                if((ReturnorderTypeEnum.REJECTED.c() + "").equals(returnOrderTYpe)){
                    // 拒收：兄弟订单不能有 POS已签收、全部签收确认、已签收确认中、部分签收已确认、投递失败确认中、投递失败已确认。
                    for(OsOrderPO o:orderPOs){
                        if(OrderStatusEnum.getSign1().contains(o.getOrderStatus())){
                            return false;
                        }
                    }
                }else if((ReturnorderTypeEnum.DELIVER_FAILED.c() + "").equals(returnOrderTYpe)){
                    // 投递失败：兄弟订单不能有 POS已签收、全部签收确认、已签收确认中、部分签收已确认、拒收确认中、拒收已确认、POS全部拒收。
                    for(OsOrderPO o:orderPOs){
                        if(OrderStatusEnum.getSign2().contains(o.getOrderStatus())){
                            return false;
                        }
                    }
                }else if((ReturnorderTypeEnum.ALL_SIGNED_RETURN.c() + "").equals(returnOrderTYpe)
                                || (ReturnorderTypeEnum.PART_SIGNED_RETURN.c() + "").equals(returnOrderTYpe)){
                    // 签收退货：兄弟订单不能有 投递失败确认中、投递失败已确认、拒收确认中、拒收已确认、拒收确认失败、POS全部拒收。
                    for(OsOrderPO o:orderPOs){
                        if(OrderStatusEnum.getSign3().contains(o.getOrderStatus())){
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }
    
    /**
     * 关闭方法。
     * 该方法中通常实现如下功能：解锁、提交或回滚事务等等。
     * 
     * @param inputHead 该接口的请求参数头。
     * @param inputData 该接口的请求参数数据体。
     * @param outputHead 该接口的返回结果头。
     * @param outputData 该接口的返回结果数据体。
     */
    @Override
    public void doClose(final BaseInputHead inputHead, final OmsSubOrderReturnInputData inputData, final BaseOutputHead outputHead, final OmsSubOrderReturnOutputData outputData) throws OmsDelegateException {
    }
}
package cn.tootoo.soa.oms.subordersign.delegate;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;

import cn.tootoo.db.egrocery.osorder.OsOrderPO;
import cn.tootoo.db.egrocery.osorderopt.OsOrderOptPO;
import cn.tootoo.db.egrocery.osreturnorder.OsReturnorderPO;
import cn.tootoo.db.egrocery.tbindorder.TBindOrderPO;
import cn.tootoo.enums.BaseResultEnum;
import cn.tootoo.global.Global;
import cn.tootoo.http.TootooService;
import cn.tootoo.http.bean.BaseInputHead;
import cn.tootoo.http.bean.BaseOutputBean;
import cn.tootoo.http.bean.BaseOutputHead;
import cn.tootoo.soa.base.enums.OrderFromEnum;
import cn.tootoo.soa.base.enums.OrderStatusEnum;
import cn.tootoo.soa.base.enums.OrderTypeEnum;
import cn.tootoo.soa.base.enums.PayStatusEnum;
import cn.tootoo.soa.base.enums.RecvCashStatusEnum;
import cn.tootoo.soa.base.enums.ReturnorderCodeStatusEnum;
import cn.tootoo.soa.base.util.LockUtil;
import cn.tootoo.soa.oms.buyerbuyscoreservice.BuyerBuyScoreService;
import cn.tootoo.soa.oms.exceptions.OmsDelegateException;
import cn.tootoo.soa.oms.subordersign.input.OmsSubOrderSignInputData;
import cn.tootoo.soa.oms.subordersign.input.OmsSubOrderSignOfflineDetailListElementI;
import cn.tootoo.soa.oms.subordersign.input.OmsSubOrderSignOrdersElementI;
import cn.tootoo.soa.oms.subordersign.output.OmsSubOrderSignOrdersElementO;
import cn.tootoo.soa.oms.subordersign.output.OmsSubOrderSignOutputData;
import cn.tootoo.soa.oms.subordersign.output.OmsSubOrderSignResultEnum;
import cn.tootoo.soa.oms.utils.LogUtils4Oms;
import cn.tootoo.soa.payment.sign.input.PaymentSignInputData;
import cn.tootoo.soa.payment.sign.input.PaymentSignOfflineDetailsElementI;
import cn.tootoo.soa.payment.sign.output.PaymentSignOutputData;
import cn.tootoo.utils.DateUtil;
import cn.tootoo.utils.StringUtil;

/**
 * 服务委托者实现类。
 * 
 * 服务description：订单服务。
 * 服务remark：订单服务。
 * 接口description：子订单签收
 * 接口remark：
 */
public final class SubOrderSignDelegate extends AbstractSubOrderSignDelegate implements Cloneable {
    /**
     * 日志对象。
     */
    static {
        SubOrderSignDelegateFactory.registPrototype(new SubOrderSignDelegate());
    }
    
    /**
     * 克隆方法。
     */
    @Override
    public AbstractSubOrderSignDelegate clone() throws CloneNotSupportedException {
        return new SubOrderSignDelegate();
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
    public void doInit(final BaseInputHead inputHead, final OmsSubOrderSignInputData inputData, final BaseOutputHead outputHead, final OmsSubOrderSignOutputData outputData) throws OmsDelegateException {
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
    public boolean doCheck(final BaseInputHead inputHead, final OmsSubOrderSignInputData inputData, final BaseOutputHead outputHead, final OmsSubOrderSignOutputData outputData) {
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
    public void doExecute(final BaseInputHead inputHead, final OmsSubOrderSignInputData inputData, final BaseOutputHead outputHead, final OmsSubOrderSignOutputData outputData) throws OmsDelegateException {
        LogUtils4Oms.info(logger, uuid, "订单签收服务开始", "inputData", inputData);
        List<OmsSubOrderSignOrdersElementI> ordersI = inputData.getOrders();
        List<OmsSubOrderSignOrdersElementO> outOrders = new ArrayList<OmsSubOrderSignOrdersElementO>();
        
        for (OmsSubOrderSignOrdersElementI orderI : ordersI){
            LogUtils4Oms.info(logger, uuid, "循环订单签收服务开始", "orderI", orderI);
            
            OmsSubOrderSignOrdersElementO outOrder = new OmsSubOrderSignOrdersElementO();
            outOrder.setOrderCode(orderI.getOrderCode());
            outOrder.setSignResultID(OmsSubOrderSignResultEnum.SIGN_FAIL.getResultID());
            outOrder.setSignResultMessage("该子订单签收失败!");
            outOrders.add(outOrder);
            
            try {
                // 锁表
                LogUtils4Oms.info(logger, uuid, "锁表", "orderCode", orderI.getOrderCode());
                boolean lockResult = LockUtil.lock(logger, uuid, orderI.getOrderCode(), 1, "子订单签收");
                if (!lockResult) {
                    LogUtils4Oms.info(logger, uuid, "锁表失败", "orderCode", orderI.getOrderCode());
                    continue;
                }
            } catch (Exception e) {
                LogUtils4Oms.error(logger, uuid,"锁表异常", e, "orderCode", orderI.getOrderCode());
                continue;
            }
            
            try {
                List<Object[]> orderCondition = new ArrayList<Object[]>();
                orderCondition.add(new Object[]{"ORDER_CODE", "=", orderI.getOrderCode()});
                List<OsOrderPO> orderPOs = osOrderDao.findOsOrderPOListByCondition(orderCondition, false, true, false);
                
                if (orderPOs == null || orderPOs.size() == 0) {
                    String msg = "该子订单不存在!";
                    Global.getOutputHead(OmsSubOrderSignResultEnum.SIGN_FAIL_B.getResultID(), null, outputHead);
                    LogUtils4Oms.info(logger, uuid, msg, "orderCode", orderI.getOrderCode());
                    outOrder.setSignResultID(OmsSubOrderSignResultEnum.SIGN_FAIL_B.getResultID());
                    outOrder.setSignResultMessage(msg);
                    continue;
                }
                
                OsOrderPO orderPO = orderPOs.get(0);
                
                //查询退货表中是否有非删除未完成状态的退货单
                /* *****add by zhaochunna 2015-7-2 start***** */
                List<Object[]> conditions=new ArrayList<Object[]>();
                Object[] condition=new Object[]{"ORDER_ID","=",orderPO.getOrderId()};
                conditions.add(condition);
                List<OsReturnorderPO> returnorderPOList = osReturnorderDao.findOsReturnorderPOListByCondition(conditions);
                if(returnorderPOList!=null&&returnorderPOList.size()>0){
                    OsReturnorderPO returnOrder = returnorderPOList.get(0);
                    if(! ReturnorderCodeStatusEnum.DELETE.getC().equals(returnOrder.getReturnorderCodeStatus())){
                    	String msg = "当前订单正在退货流程中，不允许签收！";
                        Global.getOutputHead(OmsSubOrderSignResultEnum.SIGN_FAIL_F.getResultID(), null, outputHead);
                        LogUtils4Oms.info(logger, uuid, msg, "orderCode", orderI.getOrderCode());
                        outOrder.setSignResultID(OmsSubOrderSignResultEnum.SIGN_FAIL_F.getResultID());
                        outOrder.setSignResultMessage(msg);
                        continue;
                    }
                }
                /* *****add by zhaochunna 2015-7-2 end***** */
                
                
                // 异常拦截订单状态处理
                if ("x".equals(orderPO.getOrderStatus())) {
                    List<Object[]> bindOrderConditions = new ArrayList<Object[]>();
                    bindOrderConditions.add(new Object[]{"STATUS", "=", "0"});
                    bindOrderConditions.add(new Object[]{"IS_PARENT", "=", "0"});
                    bindOrderConditions.add(new Object[]{"ORDER_CODE", "=", orderPO.getOrderCode()});
                    List<TBindOrderPO> bindOrderList = tBindOrderDao.findTBindOrderPOListByCondition(bindOrderConditions, false, false, true);
                    if (bindOrderList == null || bindOrderList.size() != 1){
                        String msg = "异常订单查询历史状态错误!";
                        Global.getOutputHead(OmsSubOrderSignResultEnum.SIGN_FAIL.getResultID(), null, outputHead);
                        LogUtils4Oms.info(logger, uuid, msg, "orderPO", orderPO);
                        outOrder.setSignResultID(OmsSubOrderSignResultEnum.SIGN_FAIL.getResultID());
                        outOrder.setSignResultMessage(msg);
                        continue;
                    }
                    LogUtils4Oms.info(logger, uuid, "订单拦截表信息", "tBindOrderPO", bindOrderList.get(0));
                    String oldStatus = bindOrderList.get(0).getOrderStatus();
                    
                    
                    // 如果历史状态是“已出库” 至 “派件中” 的则可以签收
                    String deliveryType = orderPO.getDeliveryType();
                    if (("2".equals(deliveryType) || "3".equals(deliveryType)) && (OrderStatusEnum.OUT_OF_STORAGE.getC().equals(oldStatus)
                                                    || OrderStatusEnum.DELIVERY_RECEIVED.getC().equals(oldStatus)
                                                    || OrderStatusEnum.SHIPPING.getC().equals(oldStatus))) {
                        orderPO.setOrderStatus(OrderStatusEnum.THIRD_SEND.getC());
                    } else if ("0".equals(deliveryType) && (OrderStatusEnum.OUT_OF_STORAGE.getC().equals(oldStatus)
                                                    || OrderStatusEnum.DELIVERY_RECEIVED.getC().equals(oldStatus)
                                                    || OrderStatusEnum.SHIPPING.getC().equals(oldStatus)
                                                    || OrderStatusEnum.SELF_SEND.getC().equals(oldStatus)
                                                    || OrderStatusEnum.CENTER_RECEIVED.getC().equals(oldStatus)
                                                    || OrderStatusEnum.CENTER_SEND.getC().equals(oldStatus) 
                                                    || OrderStatusEnum.SDC_RECEIVED.getC().equals(oldStatus))) {
                        orderPO.setOrderStatus(OrderStatusEnum.SENDING.getC());
                    } else {
                        orderPO.setOrderStatus(oldStatus);
                    }
                    LogUtils4Oms.info(logger, uuid, "异常拦截订单重新set订单状态", "deliveryType", deliveryType, "oldStatus", oldStatus, "newStatus", orderPO.getOrderStatus());
                }
                
                // 如果此订单已经签收成功，返回成功信息
                if (OrderStatusEnum.ALL_SIGNED.getC().equals(orderPO.getOrderStatus())) {
                    String msg = "该子订单已经签收成功!";
                    Global.getOutputHead(BaseResultEnum.SUCCESS.getResultID(), null, outputHead);
                    LogUtils4Oms.info(logger, uuid, msg, "orderPO", orderPO);
                    outOrder.setSignResultID(BaseResultEnum.SUCCESS.getResultID());
                    outOrder.setSignResultMessage(msg);
                    continue;
                }
                
                // 电子卡订单不判断状态canSign
                if (!OrderStatusEnum.get(orderPO.getOrderStatus()).canSign()
                                && !OrderTypeEnum.ONLINE_CARD_VIRTUAL.getC().equals(orderPO.getOrderType())) {
                    String msg = "该子订单状态不是派件中或第三方发货!";
                    Global.getOutputHead(OmsSubOrderSignResultEnum.SIGN_FAIL_C.getResultID(), null, outputHead);
                    LogUtils4Oms.info(logger, uuid, msg, "orderPO", orderPO);
                    outOrder.setSignResultID(OmsSubOrderSignResultEnum.SIGN_FAIL_C.getResultID());
                    outOrder.setSignResultMessage(msg);
                    continue;
                }

                // 提货卡订单签收需判断其兄弟订单状态
                if (!checkTiHuoOrderSign(orderPO)){
                    String msg = "该子订单状态是提货卡订单，因兄弟订单状态不为全部签收或已发货，不能签收!";
                    Global.getOutputHead(OmsSubOrderSignResultEnum.SIGN_FAIL_D.getResultID(), null, outputHead);
                    LogUtils4Oms.info(logger, uuid, msg,"orderPO",orderPO);
                    outOrder.setSignResultID(OmsSubOrderSignResultEnum.SIGN_FAIL_D.getResultID());
                    outOrder.setSignResultMessage(msg); 
                    continue;
                }
                
                
                /*
                 * 复核
                 * 1.原来的子订单签收功能，单笔订单和货到付款批量签收时，确定收货时，设置签收日期与收款日期相同
                 * （当前签收日期设置为当前操作日期）。
                 * 2.签收复核功能，复核确认时，设置收款日期与签收日期相同（当前复核时未给收款日期赋值）。
                 */
                /*if (OrderStatusEnum.SENDING.getC().equals(orderPO.getOrderStatus())) {
                    if (orderPO.getSignDt() == null) {
                        if (orderI.getOfflinePayDate() != null) {
                            orderPO.setSignDt(DateUtil.strToTimestamp(orderI.getOfflinePayDate()));// 更新签收日期
                            orderPO.setRealReceivedDate(DateUtil.strToTimestamp(orderI.getOfflinePayDate())); // 修改实际收货时间
                        } else if (orderPO.getPayDt() != null) {
                            orderPO.setSignDt(orderPO.getPayDt());// 更新签收日期
                            orderPO.setRealReceivedDate(orderPO.getPayDt()); // 修改实际收货时间
                        } else {
                            orderPO.setSignDt(new Timestamp(System.currentTimeMillis()));// 更新签收日期
                            orderPO.setRealReceivedDate(new Timestamp(System.currentTimeMillis())); // 修改实际收货时间
                        }
                    }
                    if (orderPO.getPayDt() == null) {
                        orderPO.setPayDt(orderPO.getSignDt());
                    }
                } else {
                    if (orderI.getOfflinePayDate() != null) {
                        orderPO.setSignDt(DateUtil.strToTimestamp(orderI.getOfflinePayDate()));// 更新签收日期
                        orderPO.setRealReceivedDate(DateUtil.strToTimestamp(orderI.getOfflinePayDate())); // 修改实际收货时间
                    } else if (orderPO.getPayDt() != null) {
                        orderPO.setSignDt(orderPO.getPayDt());// 更新签收日期
                        orderPO.setRealReceivedDate(orderPO.getPayDt()); // 修改实际收货时间
                    } else {
                        orderPO.setSignDt(new Timestamp(System.currentTimeMillis()));// 更新签收日期
                        orderPO.setRealReceivedDate(new Timestamp(System.currentTimeMillis())); // 修改实际收货时间
                    }
                }*/
                
                /**
                 * 签收时间逻辑修改：2015-7-31： zhaochunna
                 * 1.订单状态POS签收确认中的不处理
                 * 2.后台调用：入参中设置日期则更改为入参值
                 * 3.判断是在线支付还是货到付款
                 */
                
                if(!OrderStatusEnum.WAIT_ALL_SIGNED.getC().equals(orderPO.getOrderStatus())){
                	if(!StringUtil.isEmpty(orderI.getSignDate())){//后台传入签收时间，设置为传入参数
                		orderPO.setSignDt(DateUtil.strToTimestamp(orderI.getSignDate()));//更新签收日期
                		orderPO.setRealReceivedDate(DateUtil.strToTimestamp(orderI.getSignDate()));// 修改实际收货时间
                	}else{//判断几种情况
                		if("1".equals(orderI.getContainOffline())){//货到付款--子订单签收时填入的收款时间
                			if (orderI.getOfflinePayDate() != null) {
                                orderPO.setSignDt(DateUtil.strToTimestamp(orderI.getOfflinePayDate()));// 更新签收日期
                                orderPO.setRealReceivedDate(DateUtil.strToTimestamp(orderI.getOfflinePayDate())); // 修改实际收货时间
                            } else if (orderPO.getPayDt() != null) {
                                orderPO.setSignDt(orderPO.getPayDt());// 更新签收日期
                                orderPO.setRealReceivedDate(orderPO.getPayDt()); // 修改实际收货时间
                            } else {
                                orderPO.setSignDt(new Timestamp(System.currentTimeMillis()));// 更新签收日期
                                orderPO.setRealReceivedDate(new Timestamp(System.currentTimeMillis())); // 修改实际收货时间
                            }
                			if (orderPO.getPayDt() == null) {
                                orderPO.setPayDt(orderPO.getSignDt());
                            }
                		}else{//在线支付--默认为当前系统时间
                			orderPO.setSignDt(new Timestamp(System.currentTimeMillis()));// 更新签收日期
                            orderPO.setRealReceivedDate(new Timestamp(System.currentTimeMillis())); // 修改实际收货时间
                		}
                	}
                }
                
                // 如果boos后台没有更新过复核人和复核时间，此处才更新
                if(orderPO.getCheckUserId() == null){
                    orderPO.setCheckUserId(orderI.getConfirmUser()); // 复核人ID
                    orderPO.setCheckDt(DateUtil.strToTimestamp(DateUtil.getCurrentDatetime())); // 复核时间
                }
                
                orderPO.setOrderStatus(OrderStatusEnum.ALL_SIGNED.getC()); // 修改订单状态
                orderPO.setPayStatus(PayStatusEnum.YES.getC()); // 修改订单支付状态
                
                // 修改订单收款确认状态
                // 如果没有线下支付，即 非货到付款 订单
                if (0 == orderPO.getOfflinePayFee().compareTo(BigDecimal.ZERO)) {
                    orderPO.setRecvCashStatus(RecvCashStatusEnum.OK.getC());
                } else {
                    // 以下为货到付款订单处理
                    // 只有 非 虚拟提货订单，和 非 从满座团购过来的订单，才需要处理 确认状态
                    if (!OrderTypeEnum.VIRTUAL_TIHUO_ORDER.getC().equals(orderPO.getOrderType())
                                    && !OrderFromEnum.MANZUO.getC().equals(orderPO.getOrderFrom())) {
                        orderPO.setRecvCashStatus(RecvCashStatusEnum.NO.getC());
                        orderPO.setPayDt(orderPO.getRealReceivedDate());
                    }
                }
                
                // 如果包含货到付款&&货到付款支付详情不为空
                if ("1".equals(orderI.getContainOffline()) && orderI.getOfflineDetails() != null) {
                    orderPO.setOfflinePayDate(DateUtil.strToTimestamp(orderI.getOfflinePayDate()));
                    orderPO.setOfflineConfirmDate(new Timestamp(System.currentTimeMillis()));
                    orderPO.setOfflineConfirmUser(orderI.getConfirmUser());
                    if (OrderStatusEnum.get(orderPO.getOrderStatus()).isAllSigned()) {
                        orderPO.setRecvCashStatus(RecvCashStatusEnum.OK.getC());
                    } else {
                        orderPO.setRecvCashStatus(RecvCashStatusEnum.NO.getC());
                    }
                    
                    // orderPO.setOfflineRecvCash(orderPO.getOfflinePayFee());
                    // offlineRecvCash改成前台传入的货到付款支付金额之和
                    BigDecimal offlineRecvCash = BigDecimal.ZERO;
                    List<OmsSubOrderSignOfflineDetailListElementI> offlineDetailsListI = orderI.getOfflineDetails().getOfflineDetailList();
                    if (offlineDetailsListI != null && offlineDetailsListI.size() > 0) {
                        for (OmsSubOrderSignOfflineDetailListElementI offlineDetailListI : offlineDetailsListI) {
                            if (offlineDetailListI.getChargeAmount() != null) {
                                offlineRecvCash = offlineRecvCash.add(offlineDetailListI.getChargeAmount());
                            }
                        }
                    }
                    orderPO.setOfflineRecvCash(offlineRecvCash);
                    
                }
                osOrderDao.updateOsOrderPO(orderPO);
                
                // 增加订单日志
                Long optId = osOrderOptDao.findSeqNextVal("SEQ_OS_ORDER_OPT_PK");
                OsOrderOptPO opt = new OsOrderOptPO();
                opt.setOptId(optId);
                opt.setOptDt(DateUtil.strToTimestamp(DateUtil.getCurrentDatetime()));
                opt.setOrderId(orderPO.getOrderId());
                opt.setOrderDisputeStatus(orderPO.getDisputeStatus());
                opt.setOrderStatus(orderPO.getOrderStatus());
                opt.setOrderExceptionStatus(orderPO.getExceptionStatus());
                opt.setOrderPayStatus(orderPO.getPayStatus());
                opt.setRemark("您的订单已签收，感谢您在沱沱工社购物，欢迎再次光临!");
                opt.setUserName(orderI.getConfirmUserName());
                opt.setUserId(orderI.getConfirmUser());
                opt.setNotifyStatus("0");
                opt.setIsShow("1");
                opt.setRemarkEn("Your order has been signed. Thank you and welcome again to TooToo Farm !");
                osOrderOptDao.addOsOrderOptPO(opt);
                
                // 调用支付接口处理签收
                BaseOutputBean signOutputBean = paySign(orderI, orderPO.getOrderId(), orderPO.getOrderType());
                
                if (signOutputBean == null) {
                    String msg = "该子订单调用签收接口异常，不能签收!";
                    LogUtils4Oms.info(logger, uuid, msg, "orderPO", orderPO);
                    outOrder.setSignResultID(OmsSubOrderSignResultEnum.SIGN_FAIL.getResultID());
                    outOrder.setSignResultMessage(msg);
                    
                    osOrderDao.rollback();
                    continue;
                }
                
                PaymentSignOutputData signOutput = (PaymentSignOutputData)signOutputBean.getOutputData();
                if(!signOutputBean.getOutputHead().getResultID().equals(BaseResultEnum.SUCCESS.getResultID()) || signOutput == null){
                    String msg = "该子订单调用签收接口失败，不能签收! ";
                    LogUtils4Oms.info(logger, uuid, msg, "BaseOutputBean", signOutputBean.toJson());
                    outOrder.setSignResultID(OmsSubOrderSignResultEnum.SIGN_FAIL.getResultID());
                    outOrder.setSignResultMessage(msg + "【" + signOutputBean.getOutputHead().getResultMessage() + "】");
                    
                    osOrderDao.rollback();
                    continue;
                }
                
                LogUtils4Oms.info(logger, uuid, "该子订单调用签收接口成功 !", "orderPOs", orderPO);
                osOrderDao.commit(); // 待调用支付签收接口成功后，再commit
                LogUtils4Oms.info(logger, uuid, "处理子订单签收成功提交 ", "处理OmsSubOrderSignOrdersElementI", orderI);
                
                // 积分处理
                LogUtils4Oms.info(logger, uuid, "调用子订单签收积分处理", "orderPO", orderPO, "signOutput", signOutput.toString());
                BuyerBuyScoreService suyerHaveOrderService = new BuyerBuyScoreService(logger, uuid);
                suyerHaveOrderService.updateScore(orderPO, signOutput.getOfflineSignFee().add(signOutput.getOnlineSignFee()), signOutput.getGiftCardSignFee());
                
                outOrder.setSignResultID(BaseResultEnum.SUCCESS.getResultID());
                outOrder.setSignResultMessage("该子订单签收成功!");
            } catch (Exception e) {
                osOrderDao.rollback();
                LogUtils4Oms.error(logger, uuid, "订单签收服务异常错误", e, "OmsSubOrderSignOrdersElementI", orderI);
                Global.getOutputHead(OmsSubOrderSignResultEnum.SIGN_FAIL_E.getResultID(), null, outputHead);
                
            }finally{
                try{
                    LogUtils4Oms.info(logger, uuid, "解锁", "orderCode", orderI.getOrderCode());
                    LockUtil.unlock(logger, uuid, orderI.getOrderCode());
                }catch(Exception e){
                    LogUtils4Oms.error(logger, uuid, "解锁出错", e, "orderCode", orderI.getOrderCode());
                }
            }
        }
        outputData.setOrders(outOrders);
        Global.getOutputHead(BaseResultEnum.SUCCESS.getResultID(), null, outputHead);
       
    }
    
    /**
     * 调用支付接口处理签收
     * Description:<br>
     * 
     * @param suborderSignReq
     * @param orderId
     * @return
     * @throws JSONException
     */
    private BaseOutputBean paySign(OmsSubOrderSignOrdersElementI suborderSignReq, Long orderId, String orderType) throws JSONException {
        try {
            PaymentSignInputData input = new PaymentSignInputData();
            input.setConfirmUser(suborderSignReq.getConfirmUser());
            input.setConfirmUserName(suborderSignReq.getConfirmUserName());
            
            // 如果是提货订单，调用支付接口时设置不包含货到付款详情
            if (OrderTypeEnum.isTiHuoOrder(orderType)) {
                input.setContainOffline(0L);
            } else {
                input.setContainOffline(Long.valueOf(suborderSignReq.getContainOffline()));
            }
            
            input.setOfflinePayDate(suborderSignReq.getOfflinePayDate());
            input.setOrderCode(suborderSignReq.getOrderCode());
            input.setOrderID(orderId);
            if (suborderSignReq.getOfflineDetails() != null && suborderSignReq.getOfflineDetails().getOfflineDetailList() != null
                            && suborderSignReq.getOfflineDetails().getOfflineDetailList().size() > 0) {
                PaymentSignOfflineDetailsElementI offlineDetails = new PaymentSignOfflineDetailsElementI();
                offlineDetails.fromJson(suborderSignReq.getOfflineDetails().toJson());
                input.setOfflineDetails(offlineDetails);
            }
            PaymentSignOutputData paymentSignOutputData = new PaymentSignOutputData();
            Map<String, String> params = new HashMap<String, String>();
            params.put("method", "sign");
            params.put("req_str", input.toJson());
            LogUtils4Oms.info(logger, uuid, "处理子订单支付签收接口入参", "input", input.toJson());
            BaseOutputBean baseBean = TootooService.callServer("payment", params, "post", paymentSignOutputData);
            if (baseBean.getOutputHead().getResultID().equals(BaseResultEnum.SUCCESS.getResultID())) {
                LogUtils4Oms.info(logger, uuid, "处理子订单支付签收成功", "BaseOutputBean", baseBean.toJson());
                return baseBean; 
            }else{
                LogUtils4Oms.info(logger, uuid, "处理子订单支付签收失败", "BaseOutputBean", baseBean.toJson());
                return baseBean; 
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils4Oms.error(logger, uuid,"处理子订单支付签收异常 ", e);
        }
        LogUtils4Oms.error(logger, uuid, "处理子订单支付签收失败", "suborderSignReq", suborderSignReq.toJson());
        return null;
    }
    
    /**
     * 提货卡订单做签收操作时:
     * 他的兄弟订单没有投递失败或拒收的
     * （POS全部拒收、POS投递失败、拒收确认中、拒收确认失败、拒收已确认、投递失败确认中、投递失败确认失败、投递失败已确认）
     * 才能签收。
     * Description:<br>
     * 
     * @param orderPo
     * @return
     */
    private boolean checkTiHuoOrderSign(OsOrderPO orderPo) {
        if (OrderTypeEnum.isTiHuoOrder(orderPo.getOrderType())) {
            List<Object[]> orderCondition = new ArrayList<Object[]>();
            orderCondition.add(new Object[]{"PARENT_ID", "=", orderPo.getParentId()});
            orderCondition.add(new Object[]{"ORDER_ID", "<>", orderPo.getOrderId()});
            orderCondition.add(new Object[]{"ORDER_STATUS", "<>", OrderStatusEnum.ALL_SIGNED.getC()});
            
            List<OsOrderPO> orderPOs = osOrderDao.findOsOrderPOListByCondition(orderCondition);
            if (orderPOs != null && orderPOs.size() > 0) {
                for (OsOrderPO o : orderPOs) {
                    // 签收时：兄弟订单里不能有投递失败或拒收的
                    if (OrderStatusEnum.getReturn().contains(o.getOrderStatus())) { 
                        return false; 
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
    public void doClose(final BaseInputHead inputHead, final OmsSubOrderSignInputData inputData, final BaseOutputHead outputHead, final OmsSubOrderSignOutputData outputData) throws OmsDelegateException {
        
    }
    
}
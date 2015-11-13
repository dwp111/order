package cn.tootoo.soa.oms.getorderdetail.delegate;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.tootoo.db.egrocery.osorder.OsOrderPO;
import cn.tootoo.db.egrocery.osorderitems.OsOrderItemsPO;
import cn.tootoo.db.egrocery.osorderopt.OsOrderOptPO;
import cn.tootoo.db.egrocery.ospayplan.OsPayPlanPO;
import cn.tootoo.db.egrocery.tosparentorder.TOsParentOrderPO;
import cn.tootoo.db.egrocery.tosparentorderitems.TOsParentOrderItemsPO;
import cn.tootoo.db.egrocery.tosparentorderopt.TOsParentOrderOptPO;
import cn.tootoo.db.egrocery.tsysonlinecardgoods.TSysOnlineCardGoodsPO;
import cn.tootoo.enums.BaseResultEnum;
import cn.tootoo.global.Global;
import cn.tootoo.http.AuthorizeClient;
import cn.tootoo.http.bean.BaseInputHead;
import cn.tootoo.http.bean.BaseOutputHead;
import cn.tootoo.soa.base.enums.BaseOrderResultEnum;
import cn.tootoo.soa.base.enums.DeliveryTypeEnum;
import cn.tootoo.soa.base.enums.OrderStatusEnum;
import cn.tootoo.soa.base.enums.OrderStatusGroupEnum;
import cn.tootoo.soa.base.enums.OrderTypeEnum;
import cn.tootoo.soa.base.enums.ParentOrderStatusEnum;
import cn.tootoo.soa.base.enums.PayMethodEnum;
import cn.tootoo.soa.base.enums.PayStatusEnum;
import cn.tootoo.soa.base.enums.ShippingCompanyEnum;
import cn.tootoo.soa.base.enums.SubstationEnEnum;
import cn.tootoo.soa.base.enums.SubstationEnum;
import cn.tootoo.soa.base.global.DictionaryData;
import cn.tootoo.soa.base.util.OrderUtil;
import cn.tootoo.soa.oms.exceptions.OmsDelegateException;
import cn.tootoo.soa.oms.getordercommon.GetOrderCommonService;
import cn.tootoo.soa.oms.getorderdetail.input.OmsGetOrderDetailInputData;
import cn.tootoo.soa.oms.getorderdetail.output.OmsGetOrderDetailGiftBoxItemListElementO;
import cn.tootoo.soa.oms.getorderdetail.output.OmsGetOrderDetailOrderItemListElementO;
import cn.tootoo.soa.oms.getorderdetail.output.OmsGetOrderDetailOutputData;
import cn.tootoo.soa.oms.getorderdetail.output.OmsGetOrderDetailResultEnum;
import cn.tootoo.soa.oms.utils.GetOrderUtil;
import cn.tootoo.soa.oms.utils.LogUtils4Oms;
import cn.tootoo.utils.DateUtil;
import cn.tootoo.utils.Log;
import cn.tootoo.utils.StringUtil;

/**
 * 服务委托者实现类。
 * 
 * 服务description：订单服务。
 * 服务remark：订单服务。
 * 接口description：返回订单详情
 * 接口remark：流水信息建议放到单独的接口中异步加载
 */
public final class GetOrderDetailDelegate extends AbstractGetOrderDetailDelegate implements Cloneable {
    
    private String scope = "";
    private Set<Long> goodsIdSet = new HashSet<Long>();
    static {
        GetOrderDetailDelegateFactory.registPrototype(new GetOrderDetailDelegate());
    }
    
    /**
     * 克隆方法。
     */
    @Override
    public AbstractGetOrderDetailDelegate clone() throws CloneNotSupportedException {
        return new GetOrderDetailDelegate();
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
    public void doInit(final BaseInputHead inputHead, final OmsGetOrderDetailInputData inputData, final BaseOutputHead outputHead, final OmsGetOrderDetailOutputData outputData) throws OmsDelegateException {
        
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
    public boolean doCheck(final BaseInputHead inputHead, final OmsGetOrderDetailInputData inputData, final BaseOutputHead outputHead, final OmsGetOrderDetailOutputData outputData) {
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
    public void doExecute(final BaseInputHead inputHead, final OmsGetOrderDetailInputData inputData, final BaseOutputHead outputHead, final OmsGetOrderDetailOutputData outputData) throws OmsDelegateException {
        // 登录验证信息
        String userID = "";
        scope = inputData.getScope();
        try {
            LogUtils4Oms.info(logger, uuid, "获取订单详情服务,进行验证！");
            this.httpRequest.setAttribute(AuthorizeClient.ATTRIB_NEED_AUTH, AuthorizeClient.NEED_AUTH_YES);
            this.httpRequest.setAttribute(AuthorizeClient.ATTRIB_CHECK_LEVEL, AuthorizeClient.AUTH_GENERAL_LEVEL);
            Map<String, String> tempMap = AuthorizeClient.getVerifyInfo(this.httpRequest);
            tempMap.put(AuthorizeClient.COOKIE_SCOPE, inputData.getScope());
            LogUtils4Oms.info(logger, uuid, "验证前，传入的Map信息", "tempMap", tempMap.toString());
            if (AuthorizeClient.CHECK_OK != AuthorizeClient.verifySession(tempMap)) {
                LogUtils4Oms.info(logger, uuid, "验证失败", "tempMap", tempMap.toString());
                Global.getOutputHead(BaseOrderResultEnum.NOT_LOGIN.getResultID(), scope, outputHead);
                return;
            }
            userID = tempMap.get(AuthorizeClient.COOKIE_BUYER_ID).toString();
            if (null == userID || "".equals(userID)) {
                LogUtils4Oms.info(logger, uuid, "从cookie中获取用户信息失败！");
                Global.getOutputHead(BaseOrderResultEnum.NOT_LOGIN.getResultID(), scope, outputHead);
                return;
            }
            LogUtils4Oms.info(logger, uuid, "验证成功！", "userID", userID);
        } catch (Exception e) {
            LogUtils4Oms.error(logger, uuid, "验证失败！");
            Global.getOutputHead(BaseOrderResultEnum.NOT_LOGIN.getResultID(), scope, outputHead);
            return;
        }
        
        
        LogUtils4Oms.info(logger, uuid, "获取订单详情服务开始", "inputData", inputData);
        String orderGeneration = inputData.getOrderGeneration();
        String orderCode = inputData.getOrderCode();
        
        if ("1".equals(orderGeneration)) {
            
            // 如果是父订单,先查询是否已经拆单,如果已经拆单,则返回错误
            List<Object[]> orderConditions = new ArrayList<Object[]>();
            orderConditions.add(new Object[]{"PARENT_CODE", "=", orderCode});
            List<OsOrderPO> orderList = osOrderDao.findOsOrderPOListByCondition(orderConditions);
            if(orderList != null && orderList.size() > 0){
                LogUtils4Oms.info(logger, uuid, "该父订单已经拆单，请查询其子订单url！", "orderCode", orderCode);
                Global.getOutputHead(OmsGetOrderDetailResultEnum.HAVE_SUB.getResultID(), scope, outputHead);
                return;
            }
            
            
            // 父订单
            List<Object[]> parentOrderConditions = new ArrayList<Object[]>();
            parentOrderConditions.add(new Object[]{"BUYER_ID", "=", userID});
            parentOrderConditions.add(new Object[]{"ORDER_CODE", "=", orderCode});
            List<TOsParentOrderPO> parentOrderList = tOsParentOrderDao.findTOsParentOrderPOListByCondition(parentOrderConditions);
            if (parentOrderList == null || parentOrderList.size() <= 0) {
                // 没有查询到父订单数据
                LogUtils4Oms.info(logger, uuid, "查询parentOrderList为空", "parentOrderConditions", StringUtil.transferObjectList(parentOrderConditions));
                Global.getOutputHead(BaseResultEnum.REQUEST_PARAM_ERROR.getResultID(), scope, outputHead);
                return;
            }
            
            TOsParentOrderPO parentOrderPO = parentOrderList.get(0);
            
            List<Object[]> parentItemsConditions = new ArrayList<Object[]>();
            parentItemsConditions.add(new Object[]{"ORDER_ID", "=", parentOrderPO.getOrderId()});
            List<TOsParentOrderItemsPO> parentOrderItemsList = tOsParentOrderItemsDao.findTOsParentOrderItemsPOListByCondition(parentItemsConditions);
            
            List<OmsGetOrderDetailOrderItemListElementO> outOrderItemList = new ArrayList<OmsGetOrderDetailOrderItemListElementO>();
            
            // 礼盒明细[Key:itemId,Value:礼盒明细列表]
            Map<Long, List<OmsGetOrderDetailGiftBoxItemListElementO>> outGiftBoxItemMap = new HashMap<Long, List<OmsGetOrderDetailGiftBoxItemListElementO>>();
            
            boolean isPurchase = false;
            for (TOsParentOrderItemsPO parentOrderItemsPO : parentOrderItemsList) {
                OmsGetOrderDetailOrderItemListElementO outOrderItem = this.setParentOrderItemElementO(parentOrderItemsPO, parentOrderPO, outGiftBoxItemMap);
                if(outOrderItem != null){
                    outOrderItemList.add(outOrderItem);
                }
                if (parentOrderItemsPO.getPurchaseDate() != null) {
                    isPurchase = true; // 预定商品
                }
            }
            
            // 遍历outOrderItemList,为其set礼盒明细列表
            for (OmsGetOrderDetailOrderItemListElementO outItem : outOrderItemList) {
                List<OmsGetOrderDetailGiftBoxItemListElementO> outGiftBoxItemList = outGiftBoxItemMap.get(outItem.getItemID());
                if (outGiftBoxItemList != null && outGiftBoxItemList.size() > 0){
                    outItem.setGiftBoxItemList(outGiftBoxItemList);
                }
            }
            
            // 如果不是手机端并且不是电子卡订单，就为父订单设置进度条状态
            if(!scope.startsWith("112") && !OrderTypeEnum.ONLINE_CARD_VIRTUAL.getC().equals(parentOrderPO.getOrderType())){
                int progressStatus = 0;
                Long orderStatus = parentOrderPO.getOrderStatus();
                if (!ParentOrderStatusEnum.getFrontCanceled().contains(orderStatus.intValue())) {
                    // Map用来存储订单经过的状态和对应的时间,[Key:ProgressStatus,Value:Date]
                    Map<String, Date> progressDtMap = new HashMap<String, Date>();
                    progressStatus = this.setProgressBar(orderGeneration, parentOrderPO.getOrderId(), 0, progressDtMap);
                    outputData.setProgressStatus(progressStatus);
                    outputData.setProgressCommitDt(DateUtil.dateTimeToStr(progressDtMap.get("commitDt")));
                    outputData.setProgressPayDt(DateUtil.dateTimeToStr(progressDtMap.get("payDt")));
                }else{
                    // 如果订单为已取消则不显示进度条
                    outputData.setProgressStatus(progressStatus);
                }
            }
            
            // 设置父订单输出字段（不包括进度条）
            this.setParentOrderDetail(isPurchase, outputData, parentOrderPO, outOrderItemList);
            
            Global.getOutputHead(BaseResultEnum.SUCCESS.getResultID(), scope, outputHead);
            
        } else if ("2".equals(orderGeneration)) {
            // 子订单
            List<Object[]> orderConditions = new ArrayList<Object[]>();
            orderConditions.add(new Object[]{"BUYER_ID", "=", userID});
            orderConditions.add(new Object[]{"ORDER_CODE", "=", orderCode});
            List<OsOrderPO> orderList = osOrderDao.findOsOrderPOListByCondition(orderConditions);
            if (orderList == null || orderList.size() <= 0) {
                // 没有查询到子订单数据
                LogUtils4Oms.info(logger, uuid, "没有查询到子订单数据", "orderConditions", StringUtil.transferObjectList(orderConditions));
                Global.getOutputHead(BaseResultEnum.REQUEST_PARAM_ERROR.getResultID(), scope, outputHead);
                return;
            }
            
            OsOrderPO orderPo = orderList.get(0);
            
            List<Object[]> orderItemsConditions = new ArrayList<Object[]>();
            orderItemsConditions.add(new Object[]{"ORDER_ID", "=", orderPo.getOrderId()});
            List<OsOrderItemsPO> orderItemsList = osOrderItemsDao.findOsOrderItemsPOListByCondition(orderItemsConditions);
            
            List<OmsGetOrderDetailOrderItemListElementO> outOrderItemList = new ArrayList<OmsGetOrderDetailOrderItemListElementO>();
            
            // 礼盒明细Map,[Key:itemId,Value:礼盒明细列表]
            Map<Long, List<OmsGetOrderDetailGiftBoxItemListElementO>> outGiftBoxItemMap = new HashMap<Long, List<OmsGetOrderDetailGiftBoxItemListElementO>>();
            
            List<Object[]> pOrderConditions = new ArrayList<Object[]>();
            pOrderConditions.add(new Object[]{"BUYER_ID", "=", userID});
            pOrderConditions.add(new Object[]{"ORDER_CODE", "=", orderPo.getParentCode()});
            List<TOsParentOrderPO> prderList = tOsParentOrderDao.findTOsParentOrderPOListByCondition(pOrderConditions);
            TOsParentOrderPO pOrderPo = prderList.get(0);
            
            
            // 是否显示[优惠]栏
            Boolean isShowCouponDiscount = false;
            BigDecimal couponFee = orderPo.getCouponFee()==null?BigDecimal.ZERO:orderPo.getCouponFee();
            if (pOrderPo.getOrderStatus() >= 125 && pOrderPo.getOrderStatus() != 126){
                if(couponFee.compareTo(BigDecimal.ZERO) > 0){
                    BigDecimal itemCouponFee = BigDecimal.ZERO;
                    for (OsOrderItemsPO osOrderItemsPO : orderItemsList) {
                        itemCouponFee = itemCouponFee.add(osOrderItemsPO.getCouponFee() == null?BigDecimal.ZERO:osOrderItemsPO.getCouponFee());
                    }
                    if (itemCouponFee.compareTo(BigDecimal.ZERO) > 0){
                        isShowCouponDiscount = true;
                    }
                }else{
                    isShowCouponDiscount = true;
                }
            }
            
            
            for (OsOrderItemsPO osOrderItemsPO : orderItemsList) {
                OmsGetOrderDetailOrderItemListElementO outOrderItem = this.setChildOrderItemElementO(osOrderItemsPO, orderPo, outGiftBoxItemMap, isShowCouponDiscount);
                if(outOrderItem != null){
                    outOrderItemList.add(outOrderItem);
                }
            }
            
            // 遍历outOrderItemList,为其set礼盒明细列表
            for (OmsGetOrderDetailOrderItemListElementO outItem : outOrderItemList) {
                List<OmsGetOrderDetailGiftBoxItemListElementO> outGiftBoxItemList = outGiftBoxItemMap.get(outItem.getItemID());
                if (outGiftBoxItemList != null && outGiftBoxItemList.size() > 0){
                    outItem.setGiftBoxItemList(outGiftBoxItemList);
                }
            }
            
            
            // 如果不是手机端并且不是电子卡订单，就为子订单订单设置进度条状态
            if(!scope.startsWith("112") && !OrderTypeEnum.ONLINE_CARD_VIRTUAL.getC().equals(orderPo.getOrderType())){
                int progressStatus = 0;
                String orderStatus = orderPo.getOrderStatus();
                if (!OrderStatusEnum.getFrontCanceled().equals(orderStatus)){
                    // Map用来存储订单经过的状态和对应的时间,[Key:ProgressStatus,Value:Date]
                    Map<String, Date> progressDtMap = new HashMap<String, Date>();
                    progressStatus = this.setProgressBar(orderGeneration, orderPo.getParentId(), orderPo.getOrderId(), progressDtMap);
                    outputData.setProgressStatus(progressStatus);
                    outputData.setProgressCommitDt(DateUtil.dateTimeToStr(progressDtMap.get("commitDt")));
                    outputData.setProgressPayDt(DateUtil.dateTimeToStr(progressDtMap.get("payDt")));
                    outputData.setProgressOutDt(DateUtil.dateTimeToStr(progressDtMap.get("outDt")));
                    outputData.setProgressSendDt(DateUtil.dateTimeToStr(progressDtMap.get("sendDt")));
                    outputData.setProgressFinishDt(DateUtil.dateTimeToStr(progressDtMap.get("finishDt")));
                    
                    outputData.setSelfSendDt(DateUtil.dateTimeToStr(progressDtMap.get("selfSendDt")));
                }else{
                    // 如果订单为已取消则不显示进度条
                    outputData.setProgressStatus(progressStatus);
                }
            }
            
            // 设置子订单输出字段（不包括进度条）
            this.setChildOrderDetail(outputData, orderPo, outOrderItemList);
            Global.getOutputHead(BaseResultEnum.SUCCESS.getResultID(), scope, outputHead);
            
            
        } else {
            LogUtils4Oms.info(logger, uuid, "参数orderGeneration非法", "orderGeneration", orderGeneration);
            Global.getOutputHead(BaseResultEnum.REQUEST_PARAM_ERROR.getResultID(), scope, outputHead);
            return;
        }
        
    
    }
    
    /**
     * 设置父订单输出字段（不包括进度条）
     * Description:<br>
     * 
     * @param outputData
     * @param parentOrderPO
     * @param outOrderItemList
     */
    private void setParentOrderDetail(boolean isPurchase, OmsGetOrderDetailOutputData outputData, TOsParentOrderPO parentOrderPO, List<OmsGetOrderDetailOrderItemListElementO> outOrderItemList){
        outputData.setOrderID(parentOrderPO.getOrderId());
        outputData.setOrderCode(parentOrderPO.getOrderCode());

        outputData.setActiveType(parentOrderPO.getActiveType());
        outputData.setActiveID(parentOrderPO.getActiveId());
        
        Long substationId = parentOrderPO.getSubstationId(); 
        outputData.setSubstationID(substationId);
        
        if("21101".equals(scope)){ // 英文站
            outputData.setSubstationName(SubstationEnEnum.get(substationId.intValue()));
        }else{
            outputData.setSubstationName(SubstationEnum.get(substationId.intValue()));
        }
        outputData.setBuyerID(parentOrderPO.getBuyerId());
        outputData.setCreateDt(DateUtil.dateTimeToStr(parentOrderPO.getCreateDt()));
        outputData.setPayDt(DateUtil.dateTimeToStr(parentOrderPO.getPayDt()));
        
        String orderType = parentOrderPO.getOrderType();
        Map<String, String> receiveMap = OrderUtil.getReceiveDt(true, parentOrderPO.getOrderStatus().intValue(), "", orderType, parentOrderPO.getDeliveryType(), parentOrderPO.getSubstationId(), parentOrderPO.getTeamId(), parentOrderPO.getSpecifiedShippingdate(), parentOrderPO.getReceiveDt(), parentOrderPO.getReceiveDtMsg(), logger, uuid, scope);
        outputData.setIsShowReceiveDt(receiveMap.get("isShowReceiveDt"));
        outputData.setReceiveMsg(receiveMap.get("receiveMsg"));
        outputData.setReceiveDt(receiveMap.get("receiveDt"));
        
        outputData.setShipAddr(parentOrderPO.getShipAddr());
        outputData.setTel(parentOrderPO.getTel());
        outputData.setMobile(parentOrderPO.getMobile());
        outputData.setRemark(parentOrderPO.getRemark());
        outputData.setReceiptShowAmount(parentOrderPO.getReceiptShowAmount());
        outputData.setOrderType(orderType);
        outputData.setOrderFrom(parentOrderPO.getOrderFrom());
        outputData.setOrderStatus(parentOrderPO.getOrderStatus().toString());
        // 父订单状态对应前台中文显示
        outputData.setOrderStatusMsg(ParentOrderStatusEnum.getFrontParentOrderStatus(scope, parentOrderPO.getOrderStatus().intValue(), parentOrderPO.getPayType()));
        outputData.setPayType(parentOrderPO.getPayType());
        outputData.setPayStatus(parentOrderPO.getPayStatus());
        outputData.setReceiver(parentOrderPO.getReceiver());
        
        String deliveryType = parentOrderPO.getDeliveryType();
        outputData.setDeliveryType(deliveryType);
        
        //是否第三方公司配送
        if(DeliveryTypeEnum.THIRD.getC().equals(deliveryType)){
            outputData.setDcName(null);
            outputData.setIsThirdDc("1");
        } else {
            if ("21101".equals(scope)) { // 英文站
                outputData.setDcName(ShippingCompanyEnum.get(1).getG());
            } else { // 中文站
                outputData.setDcName(ShippingCompanyEnum.get(1).getS());
            }
            outputData.setIsThirdDc("0");
        }
        
        // 父订单无：第三方公司ID、Name、运单号
        outputData.setDcID(null);
        outputData.setDcCode(null);
        
        // 提货卡订单不显示金额
        String isShowOrderCallFee = "1";
        if(OrderTypeEnum.isTiHuoOrder(orderType)){
            isShowOrderCallFee = "0";
        }
        outputData.setIsShowOrderCallFee(isShowOrderCallFee);
        
        String payStatus = parentOrderPO.getPayStatus();
        if("1".equals(isShowOrderCallFee)){
            BigDecimal goodsCallFee = parentOrderPO.getGoodsCallFee(); // 商品总额
            BigDecimal shipCallFee = parentOrderPO.getShipCallFee(); // 运费
            BigDecimal discountFee = parentOrderPO.getDiscountFee(); // 活动减免
            
            Map<String, BigDecimal> payFeeMap = this.getFeeByOsPayPlan(parentOrderPO);
            BigDecimal couponFee = payFeeMap.get("couponFee");
            BigDecimal hasPayFee = payFeeMap.get("hasPayFee");
            
            BigDecimal notPayFee = BigDecimal.ZERO; // 还需支付＝商品总额+运费－活动减免－优惠券抵扣－已支付金额
            if (!PayStatusEnum.YES.getC().equals(payStatus)) {
                notPayFee = goodsCallFee.add(shipCallFee).subtract(discountFee).subtract(couponFee).subtract(hasPayFee);
            }
            
            outputData.setGoodsCallFee(goodsCallFee);
            outputData.setShipCallFee(shipCallFee);
            outputData.setDiscountFee(discountFee);
            outputData.setCouponFee(couponFee);
            outputData.setHasPayFee(hasPayFee);
            outputData.setNotPayFee(notPayFee);
        }
        
        // 可否再次购买,满足条件是： 订单中的商品（不包括赠品is_gift）必须至少有一个是可见的visible_flag
        // 改为调用商品接口，为outOrderItemList设置是否可见，并返回是否可再次购买
        String canBuyAg = this.setGoodsCanSale(orderType, outOrderItemList);
        outputData.setCanBuyAG(canBuyAg);
        
        // 是否可修改支付方式
        outputData.setCanChgPay(parentOrderPO.getChgPay());
        
        // 是否显示去支付按钮,只针对父订单
        String isShowToPay = "0";
        if(GetOrderUtil.getParentIsShowToPay(parentOrderPO.getOrderStatus(), payStatus, parentOrderPO.getPayType(), parentOrderPO.getOrderId(), osPayPlanDao, logger, uuid)){
            isShowToPay = "1";
        }
        outputData.setIsShowToPay(isShowToPay);
        
        
        if("1".equals(isShowToPay)){
            if(isPurchase){
                outputData.setIsPurchase(1L);
            }else{
                outputData.setIsPurchase(0L);
            }
            
            Log.info(logger, uuid, "查询原始订单计划开始", "orderCode", parentOrderPO.getOrderCode());
            OsPayPlanPO payPlan = osPayPlanDao.findOsPayPlanPOByID(parentOrderPO.getOrderId());
            Log.info(logger, uuid, "查询原是支付计划完毕", "orderCode", parentOrderPO.getOrderCode(), "payPlan", payPlan);
            if (payPlan == null || StringUtil.isEmpty(payPlan.getPayInfo())) {
                Log.info(logger, uuid, "此订单支付计划有误,数据错误", "orderCode", parentOrderPO.getOrderCode(), "payPlan", payPlan);
                outputData.setOrderPayFee(BigDecimal.ZERO);
                outputData.setOnlinePayFee(BigDecimal.ZERO);
                outputData.setOfflinePayFee(BigDecimal.ZERO);
            }else{
                Map<Long, Long> payMethodMap = DictionaryData.getPayMethodPidMap(logger, uuid);
                Log.info(logger, uuid, "支付方式", "payMethodMap", payMethodMap);
                BigDecimal onlinePayFee = BigDecimal.ZERO;
                BigDecimal offlinePayFee = BigDecimal.ZERO;
                BigDecimal couponFee = BigDecimal.ZERO;
                Long defaultPayMethodId = 0L;
                String[] payInfoArray = payPlan.getPayInfo().split("\\^");
                for (String s : payInfoArray) {
                    String[] array = s.split("_");
                    BigDecimal bd = new BigDecimal(array[1]);
                    
                    Long key = payMethodMap.get(Long.valueOf(array[0]));
                    if(key == 0L){
                        key = Long.valueOf(array[0]);
                    }
                    
                    if(PayMethodEnum.ONLINE.getC() == key.intValue()){
                        defaultPayMethodId = Long.valueOf(array[0]);
                        onlinePayFee = onlinePayFee.add(bd);
                    }else if(PayMethodEnum.OFFLINE.getC() == key.intValue()){
                        offlinePayFee = offlinePayFee.add(bd);
                    }else if(PayMethodEnum.COUPON.getC() == key.intValue()){
                        couponFee = couponFee.add(bd);
                    }
                }
                
                outputData.setOrderPayFee(payPlan.getPlanAmt().subtract(couponFee));
                outputData.setOnlinePayFee(onlinePayFee);
                outputData.setOfflinePayFee(offlinePayFee);
                
                if (onlinePayFee.compareTo(BigDecimal.ZERO) != 0) {
                    outputData.setPayMethodId(defaultPayMethodId);
                    if("21101".equals(scope)){
                        Map<Long, String> payMethodTitleEnMap = DictionaryData.getPayMethodTitleEnMap(logger, uuid);
                        outputData.setPayMethodTile(payMethodTitleEnMap.get(defaultPayMethodId));
                    }else{
                        Map<Long, String> payMethodTitleMap = DictionaryData.getPayMethodTitleMap(logger, uuid);
                        outputData.setPayMethodTile(payMethodTitleMap.get(defaultPayMethodId));
                    }
                }
            }
        }
        
        
        // 是否当日达
        String isDrd = "0";
        if("1".equals(parentOrderPO.getDeliveryTimeType())){
            isDrd = "1";
        }
        outputData.setIsDrd(isDrd);
        
        // 是否显示用户自助取消订单按钮
        Long orderStatus = parentOrderPO.getOrderStatus();
        if (OrderTypeEnum.ONLINE_CARD_VIRTUAL.getC().equals(parentOrderPO.getOrderType())) {
            if((orderStatus >= 1 && orderStatus <= 30) || orderStatus == 60){
                outputData.setIsShowCancel("1");                
            }else{
                outputData.setIsShowCancel("0");
            }
        } else if (OrderTypeEnum.BAOYOU_TUANGOU_ORDER.getC().equals(parentOrderPO.getOrderType())
                        && new Timestamp(System.currentTimeMillis()).after(DateUtil.getDateBeforeOrAfterDays(parentOrderPO.getReceiveDt(), -1))) {
            Log.info(logger, uuid, "包邮团购订单从发货日前1天开始，前台不允许取消订单", "orderCode", parentOrderPO.getOrderCode(), "receiveDt", parentOrderPO.getReceiveDt());
            outputData.setIsShowCancel("0");
        } else if (orderStatus >= 1 && orderStatus <= 120 && orderStatus != 110) {
            outputData.setIsShowCancel("1");
        } else if (orderStatus >= 125 || orderStatus == 110) {
            outputData.setIsShowCancel("1");
            List<Object[]> orderConditions = new ArrayList<Object[]>();
            orderConditions.add(new Object[]{"PARENT_ID", "=", parentOrderPO.getOrderId()});
            orderConditions.add(new Object[]{"ORDER_STATUS", "<>", OrderStatusEnum.REPALCE_GOODS_CANCEL.getC()});
            List<OsOrderPO> orderList = osOrderDao.findOsOrderPOListByCondition(orderConditions);
            for (OsOrderPO osOrder : orderList) {
                LogUtils4Oms.info(logger, uuid, "自助取消按钮判断子订单状态", "subOrderCode", osOrder.getOrderCode(), "orderStatus", osOrder.getOrderStatus());
                OrderStatusEnum orderStatusEnum = OrderStatusEnum.get(osOrder.getOrderStatus());
                if (orderStatusEnum == null || !OrderStatusEnum.canBeCanceled(orderStatusEnum)) {
                    outputData.setIsShowCancel("0");
                    break;
                }
            }
        } else {
            outputData.setIsShowCancel("0");
        }
        
        outputData.setOrderItemList(outOrderItemList);
    }
    
    /**
     * 设置子订单输出字段（不包括进度条）
     * Description:<br>
     * 
     * @param outputData
     * @param orderPo
     * @param outOrderItemList
     */
    private void setChildOrderDetail(OmsGetOrderDetailOutputData outputData, OsOrderPO orderPo, List<OmsGetOrderDetailOrderItemListElementO> outOrderItemList){
        outputData.setOrderID(orderPo.getOrderId());
        outputData.setOrderCode(orderPo.getOrderCode());
        outputData.setParentID(orderPo.getParentId());
        outputData.setParentOrderCode(orderPo.getParentCode());

        outputData.setActiveType(orderPo.getActiveType());
        outputData.setActiveID(orderPo.getActiveId());
        
        Long substationId = orderPo.getSubstationId();
        outputData.setSubstationID(substationId);
        
        if("21101".equals(scope)){ // 英文站
            outputData.setSubstationName(SubstationEnEnum.get(substationId.intValue()));
        }else{
            outputData.setSubstationName(SubstationEnum.get(substationId.intValue()));
        }
        outputData.setBuyerID(orderPo.getBuyerId());
        outputData.setCreateDt(DateUtil.dateTimeToStr(orderPo.getCreateDt()));
        
        String orderType = orderPo.getOrderType();
        Map<String, String> receiveMap = OrderUtil.getReceiveDt(false, 0, orderPo.getOrderStatus(), orderType, orderPo.getDeliveryType(), orderPo.getSubstationId(), orderPo.getTeamId(), orderPo.getSpecifiedShippingdate(), orderPo.getReceiveDt(), orderPo.getReceiveDtMsg(), logger, uuid, scope);
        outputData.setIsShowReceiveDt(receiveMap.get("isShowReceiveDt"));
        outputData.setReceiveMsg(receiveMap.get("receiveMsg"));
        outputData.setReceiveDt(receiveMap.get("receiveDt"));
        
        outputData.setPayDt(DateUtil.dateTimeToStr(orderPo.getPayDt()));
        outputData.setShipAddr(orderPo.getShipAddr());
        outputData.setTel(orderPo.getTel());
        outputData.setMobile(orderPo.getMobile());
        outputData.setRemark(orderPo.getRemark());
        outputData.setReceiptShowAmount(orderPo.getReceiptShowAmount());
        outputData.setOrderType(orderType);
        outputData.setOrderFrom(orderPo.getOrderFrom());
        outputData.setOrderStatus(orderPo.getOrderStatus().toString());
        // 子订单状态对应前台中文显示
        outputData.setOrderStatusMsg(OrderStatusEnum.getFrontOrderStatus(scope, orderPo.getOrderStatus()));
        outputData.setPayType(orderPo.getPayType());
        outputData.setPayStatus(orderPo.getPayStatus());
        outputData.setReceiver(orderPo.getReceiver());
        
        String deliveryType = orderPo.getDeliveryType();
        outputData.setDeliveryType(deliveryType);
        
        
        if (DeliveryTypeEnum.THIRD.getC().equals(deliveryType)) {
            Long dcId = orderPo.getDcId();
            String dcName = "";
            ShippingCompanyEnum shippingCompanyEnum = ShippingCompanyEnum.get(dcId.intValue());
            if ("21101".equals(scope)) { // 英文站
                dcName = shippingCompanyEnum == null?"":shippingCompanyEnum.getG();
            } else { // 中文站
                dcName = shippingCompanyEnum == null?"":shippingCompanyEnum.getS();
            }
            // 配送公司名称
            outputData.setDcName(dcName);
            // 是否第三方公司配送
            outputData.setIsThirdDc("1");
            // 第三方公司ID
            outputData.setDcID(dcId);
            // 第三方运单号
            outputData.setDcCode(orderPo.getDcCode());
        } else {
            if ("21101".equals(scope)) { // 英文站
                outputData.setDcName(ShippingCompanyEnum.get(1).getG());
            } else { // 中文站
                outputData.setDcName(ShippingCompanyEnum.get(1).getS());
            }
            outputData.setIsThirdDc("0");
            outputData.setDcID(null);
            outputData.setDcCode(null);
        }
        
        
        // 提货卡订单不显示金额
        String isShowOrderCallFee = "1";
        if(OrderTypeEnum.isTiHuoOrder(orderType)){
            isShowOrderCallFee = "0";
        }
        outputData.setIsShowOrderCallFee(isShowOrderCallFee);
        
        if("1".equals(isShowOrderCallFee)){
            String payStatus = orderPo.getPayStatus();
            BigDecimal goodsCallFee = BigDecimal.ZERO;
            if (OrderStatusEnum.isBeforeOutOfStorage(orderPo.getOrderStatus()) || OrderStatusEnum.isCancel(orderPo.getOrderStatus())) {
                goodsCallFee = orderPo.getGoodsCallFee(); // 商品总额
            } else {
                goodsCallFee = orderPo.getGoodsPackfreshFee() == null?orderPo.getGoodsCallFee():orderPo.getGoodsPackfreshFee(); // 商品称重金额
            }
            
            BigDecimal shipCallFee = orderPo.getShipCallFee(); // 运费
            BigDecimal discountFee = orderPo.getDiscountFee(); // 活动减免
            BigDecimal couponFee = orderPo.getCouponFee(); // 优惠券抵扣
            
            BigDecimal hasPayFee = orderPo.getOnlinePayFee() == null?BigDecimal.ZERO:orderPo.getOnlinePayFee(); // 已支付金额＝用户已支付金额
            if (PayStatusEnum.YES.getC().equals(payStatus)) {
                hasPayFee = orderPo.getOnlinePayFee().add(orderPo.getOfflinePayFee());
            }
            
            BigDecimal notPayFee = BigDecimal.ZERO; // 还需支付＝商品总额+运费－活动减免－优惠券抵扣－已支付金额
            if (!PayStatusEnum.YES.getC().equals(payStatus)) {
                notPayFee = goodsCallFee.add(shipCallFee).subtract(discountFee).subtract(couponFee).subtract(hasPayFee);
            }
            
            outputData.setGoodsCallFee(goodsCallFee);
            outputData.setShipCallFee(shipCallFee);
            outputData.setDiscountFee(discountFee);
            outputData.setCouponFee(couponFee);
            outputData.setHasPayFee(hasPayFee);
            outputData.setNotPayFee(notPayFee);
        }
        
        // 可否再次购买,满足条件是： 订单中的商品（不包括赠品is_gift）必须至少有一个是可见的visible_flag
        // 改为调用商品接口，为outOrderItemList设置是否可见，并返回是否可再次购买
        String canBuyAg = this.setGoodsCanSale(orderType, outOrderItemList);
        outputData.setCanBuyAG(canBuyAg);
        
        // 是否可修改支付方式
        outputData.setCanChgPay("0");
        
        // 是否显示去支付按钮,只针对父订单
        outputData.setIsShowToPay("0");
        
        // 是否当日达
        String isDrd = "0";
        if("1".equals(orderPo.getDeliveryTimeType())){
            isDrd = "1";
        }
        outputData.setIsDrd(isDrd);
        
        //是否显示用户自助取消按钮，只针对父订单
        outputData.setIsShowCancel("0"); 
        
        // 为礼盒的壳子设置实重为其明细实重的总和，只针对子订单
        this.setGiftBoxWeight(outOrderItemList);
        
        outputData.setOrderItemList(outOrderItemList);
    }
    
    
    
    /**
     * 拼父订单明细输出list
     * Description:<br>
     * 
     * @param parentOrderItemsPO
     * @param parentOrderPO
     * @param outGiftBoxItemMap
     * @return
     */
    private OmsGetOrderDetailOrderItemListElementO setParentOrderItemElementO(TOsParentOrderItemsPO parentOrderItemsPO, TOsParentOrderPO parentOrderPO, Map<Long, List<OmsGetOrderDetailGiftBoxItemListElementO>> outGiftBoxItemMap){
        OmsGetOrderDetailOrderItemListElementO outOrderItem = new OmsGetOrderDetailOrderItemListElementO();
        
        outOrderItem.setItemID(parentOrderItemsPO.getItemId());
        Long goodsId = parentOrderItemsPO.getGoodsId();
        outOrderItem.setGoodsID(goodsId);
        
        // 如果订单类型是电子卡订单，则需返回卡类型
        if(OrderTypeEnum.ONLINE_CARD_VIRTUAL.getC().equals(parentOrderPO.getOrderType())){
            List<Object[]> onlineCardConditions = new ArrayList<Object[]>();
            onlineCardConditions.add(new Object[]{"GOODS_ID", "=", goodsId});
            List<TSysOnlineCardGoodsPO> onlineCardList = tSysOnlineCardGoodsDao.findTSysOnlineCardGoodsPOListByCondition(onlineCardConditions);
            if (onlineCardList != null && onlineCardList.size() >= 1){
                TSysOnlineCardGoodsPO onlineCardGoods = onlineCardList.get(0);
                outOrderItem.setCardType(onlineCardGoods.getCardType());
            }else{
                LogUtils4Oms.error(logger, uuid, "查询电子卡订单卡类型为空", "goodsId", goodsId, "onlineCardList", onlineCardList);
            }
        }
        
        outOrderItem.setSkuID(parentOrderItemsPO.getSku());
        outOrderItem.setGoodsNumber(parentOrderItemsPO.getGoodsNumber().longValue());
        
        // 是否可售暂不处理，待循环结束后统一处理
        // outOrderItem.setCanSale(null);
        goodsIdSet.add(goodsId);
        
        if ("21101".equals(scope)) {
            // 英文站
            outOrderItem.setGoodsName(parentOrderItemsPO.getGoodsNameEn());
            outOrderItem.setGoodsUnit(parentOrderItemsPO.getGoodsUnitEn());
            outOrderItem.setUnitName(parentOrderItemsPO.getUnitNameEn());
        } else {
            // 非英文站
            outOrderItem.setGoodsName(parentOrderItemsPO.getGoodsName());
            outOrderItem.setGoodsUnit(parentOrderItemsPO.getGoodsUnit());
            outOrderItem.setUnitName(parentOrderItemsPO.getUnitName());
        }
        
        // 父订单为出库前，实发数量、实重为空
        outOrderItem.setRealOutNum(null);
        outOrderItem.setGoodsWeight(null);
        
        BigDecimal goodsPrice = parentOrderItemsPO.getGoodsPrice();
        outOrderItem.setShopPrice(parentOrderItemsPO.getShopPrice());
        outOrderItem.setGoodsPrice(goodsPrice);
        outOrderItem.setGoodsAmount(parentOrderItemsPO.getGoodsAmount());
        outOrderItem.setCouponDiscount(null);
        outOrderItem.setSubtotal(null); 
        
        String picPath = parentOrderItemsPO.getPicPath();
        if(picPath != null){
            picPath = picPath.split(",")[0];
        }
        outOrderItem.setGoodsPic(picPath);
        
        // 商品链接
        String webUrl = GetOrderUtil.getWebUrl(scope, goodsId, parentOrderItemsPO.getPromoteId());
        outOrderItem.setWebUrl(webUrl);
        
        // 是否显示价格
        String orderType = parentOrderPO.getOrderType();
        String isShowPrice = "0";
        if(GetOrderUtil.getIsShowPrice(goodsPrice, orderType)){
            isShowPrice = "1";
        }
        outOrderItem.setIsShowPrice(isShowPrice);
        
        // 是否提供链接
        String hasLink = "0";
        if(GetOrderUtil.getHasLink(goodsId, orderType)){
            hasLink = "1";
        }
        outOrderItem.setHasLink(hasLink);
        
        // 父订单是否显示分享按钮
        Byte isGift = parentOrderItemsPO.getIsGift();
        String isShowShare = "0";
        if(GetOrderUtil.getParentIsShowShare(scope, parentOrderPO.getOrderStatus(), orderType, isGift)){
            isShowShare = "1";
        }
        outOrderItem.setIsShowShare(isShowShare);
        
        // 是否显示评论按钮(父订单不显示)
        outOrderItem.setIsShowReview("0");
        
        // 是否赠品
        if (isGift == null || isGift != 1) {
            outOrderItem.setIsGift("0");
        } else {
            outOrderItem.setIsGift("1");
        }
        
        // 是否礼盒
        outOrderItem.setIsGiftBox(parentOrderItemsPO.getIsGiftbox());
        
        //是否是兑换商品  
        /* *****add by zhaochunn 2015-6-30 start***** */
        if(StringUtil.isEmpty(parentOrderItemsPO.getExCouponSn())){
        	outOrderItem.setIsExGood("0");
        }else{
        	outOrderItem.setIsExGood("1");
        }
        /* *****add by zhaochunn 2015-6-30 end***** */
        
        // 如果是礼盒的明细（所属礼盒ID不是0），则存入明细map里
        if(parentOrderItemsPO.getGiftboxId() != 0){
            OmsGetOrderDetailGiftBoxItemListElementO giftBoxItem = new OmsGetOrderDetailGiftBoxItemListElementO();
            giftBoxItem.setItemID(outOrderItem.getItemID());
            giftBoxItem.setGoodsID(outOrderItem.getGoodsID());
            giftBoxItem.setCardType(outOrderItem.getCardType());
            giftBoxItem.setSkuID(outOrderItem.getSkuID());
            giftBoxItem.setGoodsName(outOrderItem.getGoodsName());
            giftBoxItem.setGoodsNumber(outOrderItem.getGoodsNumber());
            giftBoxItem.setGoodsUnit(outOrderItem.getGoodsUnit());
            giftBoxItem.setUnitName(outOrderItem.getUnitName());
            giftBoxItem.setRealOutNum(outOrderItem.getRealOutNum());
            giftBoxItem.setGoodsWeight(outOrderItem.getGoodsWeight());
            giftBoxItem.setShopPrice(outOrderItem.getShopPrice());
            giftBoxItem.setGoodsPrice(outOrderItem.getGoodsPrice());
            giftBoxItem.setGoodsAmount(outOrderItem.getGoodsAmount());
            giftBoxItem.setCouponDiscount(outOrderItem.getCouponDiscount());
            giftBoxItem.setSubtotal(outOrderItem.getSubtotal());
            giftBoxItem.setGoodsPic(outOrderItem.getGoodsPic());
            // giftBoxItem.setWebUrl(outOrderItem.getWebUrl());
            // giftBoxItem.setCanSale(outOrderItem.getCanSale());
            giftBoxItem.setIsShowPrice(outOrderItem.getIsShowPrice());
            giftBoxItem.setHasLink(outOrderItem.getHasLink());
            giftBoxItem.setIsShowShare(outOrderItem.getIsShowShare());
            giftBoxItem.setIsShowReview(outOrderItem.getIsShowReview());
            giftBoxItem.setIsGift(outOrderItem.getIsGift());
            
            List<OmsGetOrderDetailGiftBoxItemListElementO> temp = outGiftBoxItemMap.get(parentOrderItemsPO.getGiftboxId().longValue());
            if (temp == null || temp.size() <= 0) {
                temp = new ArrayList<OmsGetOrderDetailGiftBoxItemListElementO>();
            }
            temp.add(giftBoxItem);
            outGiftBoxItemMap.put(parentOrderItemsPO.getGiftboxId().longValue(), temp);
            
            return null;
        }
        
        return outOrderItem;
    }
    
    /**
     * 拼子订单明细输出list
     * Description:<br>
     * 
     * @param osOrderItemsPO
     * @param orderPo
     * @param outGiftBoxItemMap
     * @return
     */
    private OmsGetOrderDetailOrderItemListElementO setChildOrderItemElementO(OsOrderItemsPO osOrderItemsPO, OsOrderPO orderPo, Map<Long, List<OmsGetOrderDetailGiftBoxItemListElementO>> outGiftBoxItemMap, boolean isShowCouponDiscount){
        OmsGetOrderDetailOrderItemListElementO outOrderItem = new OmsGetOrderDetailOrderItemListElementO();
        
        outOrderItem.setItemID(osOrderItemsPO.getItemId());
        Long goodsId = osOrderItemsPO.getGoodsId();
        outOrderItem.setGoodsID(goodsId);
        
        // 如果订单类型是电子卡订单，则需返回卡类型
        if(OrderTypeEnum.ONLINE_CARD_VIRTUAL.getC().equals(orderPo.getOrderType())){
            List<Object[]> onlineCardConditions = new ArrayList<Object[]>();
            onlineCardConditions.add(new Object[]{"GOODS_ID", "=", goodsId});
            List<TSysOnlineCardGoodsPO> onlineCardList = tSysOnlineCardGoodsDao.findTSysOnlineCardGoodsPOListByCondition(onlineCardConditions);
            if (onlineCardList != null && onlineCardList.size() >= 1){
                TSysOnlineCardGoodsPO onlineCardGoods = onlineCardList.get(0);
                outOrderItem.setCardType(onlineCardGoods.getCardType());
            }else{
                LogUtils4Oms.error(logger, uuid, "查询电子卡订单卡类型为空", "goodsId", goodsId, "onlineCardList", onlineCardList);
            }
        }
        
        outOrderItem.setSkuID(osOrderItemsPO.getSku());
        outOrderItem.setGoodsNumber(osOrderItemsPO.getGoodsNumber().longValue());
        
        // 是否可售暂不处理，待循环结束后统一处理
        // outOrderItem.setCanSale(null);
        goodsIdSet.add(goodsId);
        
        if ("21101".equals(scope)) {
            // 英文站
            outOrderItem.setGoodsName(osOrderItemsPO.getGoodsNameEn());
            outOrderItem.setGoodsUnit(osOrderItemsPO.getGoodsUnitEn());
            outOrderItem.setUnitName(osOrderItemsPO.getUnitNameEn());
        } else {
            // 非英文站
            outOrderItem.setGoodsName(osOrderItemsPO.getGoodsName());
            outOrderItem.setGoodsUnit(osOrderItemsPO.getGoodsUnit());
            outOrderItem.setUnitName(osOrderItemsPO.getUnitName());
        }
        
        // 出库后设置实发数量
        if (OrderStatusEnum.isBeforeOutOfStorage(orderPo.getOrderStatus()) || OrderStatusEnum.isCancel(orderPo.getOrderStatus())) {
            outOrderItem.setRealOutNum(null);
            outOrderItem.setGoodsWeight(null);
        }else{
            outOrderItem.setRealOutNum(osOrderItemsPO.getRealOutNum());
            if (osOrderItemsPO.getRealOutNum() != null && osOrderItemsPO.getUnitWeight() != null) {
                outOrderItem.setGoodsWeight(osOrderItemsPO.getRealOutNum().multiply(osOrderItemsPO.getUnitWeight()).divide(new BigDecimal(1000)));
            }else{
                outOrderItem.setGoodsWeight(null);
            }
        }
        
        
        BigDecimal goodsPrice = osOrderItemsPO.getGoodsPrice();
        outOrderItem.setShopPrice(osOrderItemsPO.getShopPrice());
        outOrderItem.setGoodsPrice(goodsPrice);
        outOrderItem.setGoodsAmount(osOrderItemsPO.getGoodsAmount());
        
        if (isShowCouponDiscount) {
            BigDecimal couponDiscount = (osOrderItemsPO.getCouponFee() == null?BigDecimal.ZERO:osOrderItemsPO.getCouponFee()).add(osOrderItemsPO.getDiscountFee() == null?BigDecimal.ZERO:osOrderItemsPO.getDiscountFee()).add(osOrderItemsPO.getOtherDiscountFee() == null?BigDecimal.ZERO:osOrderItemsPO.getOtherDiscountFee());
            outOrderItem.setCouponDiscount(couponDiscount);
            // 子订单小计 = 实际出库金额
            if (OrderStatusEnum.isBeforeOutOfStorage(orderPo.getOrderStatus()) || OrderStatusEnum.isCancel(orderPo.getOrderStatus())) {
                outOrderItem.setSubtotal(osOrderItemsPO.getGoodsAmount().subtract(couponDiscount));
            } else {
                outOrderItem.setSubtotal((osOrderItemsPO.getGoodsPackfreshPrice() == null?osOrderItemsPO.getGoodsAmount():osOrderItemsPO.getGoodsPackfreshPrice()).subtract(couponDiscount));
            }
        }else{
            outOrderItem.setCouponDiscount(null);
            outOrderItem.setSubtotal(null);
        }
        
        String picPath = osOrderItemsPO.getPicPath();
        if(picPath != null){
            picPath = picPath.split(",")[0];
        }
        outOrderItem.setGoodsPic(picPath);
        
        // 商品链接
        String webUrl = GetOrderUtil.getWebUrl(scope, goodsId, osOrderItemsPO.getPromoteId());
        outOrderItem.setWebUrl(webUrl);
        
        // 是否显示价格
        String orderType = orderPo.getOrderType();
        String isShowPrice = "0";
        if(GetOrderUtil.getIsShowPrice(goodsPrice, orderType)){
            isShowPrice = "1";
        }
        outOrderItem.setIsShowPrice(isShowPrice);
        
        // 是否提供链接
        String hasLink = "0";
        if(GetOrderUtil.getHasLink(goodsId, orderType)){
            hasLink = "1";
        }
        outOrderItem.setHasLink(hasLink);
        
        // 子订单是否显示分享按钮
        Byte isGift = osOrderItemsPO.getIsGift();
        String isShowShare = "0";
        if(GetOrderUtil.getSubIsShowShare(scope, orderPo.getOrderStatus(), orderType, isGift)){
            isShowShare = "1";
        }
        outOrderItem.setIsShowShare(isShowShare);
        
        // 是否显示评论按钮(父订单不显示)
        String isShowReview = "0";
        if (GetOrderUtil.getIsShowReview(orderPo.getOrderStatus(), orderPo.getReceiveDt(), isGift, osOrderItemsPO.getReviewId(), osOrderItemsPO.getIsGiftbox())) {
            isShowReview = "1";
        }
        outOrderItem.setIsShowReview(isShowReview);
        
        // 是否赠品
        if (isGift == null || isGift != 1) {
            outOrderItem.setIsGift("0");
        } else {
            outOrderItem.setIsGift("1");
        }
        
        // 是否礼盒
        outOrderItem.setIsGiftBox(osOrderItemsPO.getIsGiftbox());
        
        //是否是兑换商品  
        /* *****add by zhaochunn 2015-6-30 start***** */
        if(StringUtil.isEmpty(osOrderItemsPO.getExCouponSn())){
        	outOrderItem.setIsExGood("0");
        }else{
        	outOrderItem.setIsExGood("1");
        }
        /* *****add by zhaochunn 2015-6-30 end***** */
        
        // 如果是礼盒的明细（所属礼盒ID不是0），则存入明细map里
        if(osOrderItemsPO.getGiftboxId() != 0){
            OmsGetOrderDetailGiftBoxItemListElementO giftBoxItem = new OmsGetOrderDetailGiftBoxItemListElementO();
            giftBoxItem.setItemID(outOrderItem.getItemID());
            giftBoxItem.setGoodsID(outOrderItem.getGoodsID());
            giftBoxItem.setCardType(outOrderItem.getCardType());
            giftBoxItem.setSkuID(outOrderItem.getSkuID());
            giftBoxItem.setGoodsName(outOrderItem.getGoodsName());
            giftBoxItem.setGoodsNumber(outOrderItem.getGoodsNumber());
            giftBoxItem.setGoodsUnit(outOrderItem.getGoodsUnit());
            giftBoxItem.setUnitName(outOrderItem.getUnitName());
            giftBoxItem.setRealOutNum(outOrderItem.getRealOutNum());
            giftBoxItem.setGoodsWeight(outOrderItem.getGoodsWeight());
            giftBoxItem.setShopPrice(outOrderItem.getShopPrice());
            giftBoxItem.setGoodsPrice(outOrderItem.getGoodsPrice());
            giftBoxItem.setGoodsAmount(outOrderItem.getGoodsAmount());
            giftBoxItem.setCouponDiscount(outOrderItem.getCouponDiscount());
            giftBoxItem.setSubtotal(outOrderItem.getSubtotal());
            giftBoxItem.setGoodsPic(outOrderItem.getGoodsPic());
            // giftBoxItem.setWebUrl(outOrderItem.getWebUrl());
            // giftBoxItem.setCanSale(outOrderItem.getCanSale());
            giftBoxItem.setIsShowPrice(outOrderItem.getIsShowPrice());
            giftBoxItem.setHasLink(outOrderItem.getHasLink());
            giftBoxItem.setIsShowShare(outOrderItem.getIsShowShare());
            giftBoxItem.setIsShowReview(outOrderItem.getIsShowReview());
            giftBoxItem.setIsGift(outOrderItem.getIsGift());
            
            List<OmsGetOrderDetailGiftBoxItemListElementO> temp = outGiftBoxItemMap.get(osOrderItemsPO.getGiftboxId().longValue());
            if (temp == null || temp.size() <= 0) {
                temp = new ArrayList<OmsGetOrderDetailGiftBoxItemListElementO>();
            }
            temp.add(giftBoxItem);
            outGiftBoxItemMap.put(osOrderItemsPO.getGiftboxId().longValue(), temp);
            
            return null;
        }
        
        return outOrderItem;
    }
    
    
    /**
     * 调用商品接口查询对应商品是否上架并且可见的
     * 然后设置商品是否可见，订单是否可再次购买
     * Description:<br>
     * 
     * @param goodsIds
     * @return
     */
    @SuppressWarnings("unchecked")
    private String setGoodsCanSale(String orderType, List<OmsGetOrderDetailOrderItemListElementO> outOrderItemList) {
        String canBuyAg = "0";

        // 沱沱惠订单、提货订单和卡订单不提供再次购买功能
        if (OrderTypeEnum.BAOYOU_TUANGOU_ORDER.getC().equals(orderType) || OrderTypeEnum.isTiHuoOrder(orderType)
                        || OrderTypeEnum.GIFT_CARD_ORDER.getC().equals(orderType) || OrderTypeEnum.ONLINE_CARD_VIRTUAL.getC().equals(orderType)){
            return canBuyAg;
        }
        
        GetOrderCommonService getOrderCommonService = new GetOrderCommonService(logger, uuid);
        Map<String, Map> goodsInfo = getOrderCommonService.getGoodsInfo(goodsIdSet, scope, httpRequest);
        if (goodsInfo == null || goodsInfo.isEmpty()) {
            LogUtils4Oms.error(logger, uuid, "调用获取商品详情接口获取Map:goodsInfo异常", null, "goodsInfo", goodsInfo);
            return canBuyAg;
        }
        
        try {
            Map<Long, String> goodsCanSaleMap = goodsInfo.get("goodsCanSaleMap");
            Map<Long, Long> goodsMaxBuyMap = goodsInfo.get("goodsMaxBuyMap");
            Map<Long, Long> goodsMinBuyMap = goodsInfo.get("goodsMinBuyMap");
            
            // 可否再次购买:订单中的商品（不包括赠品is_gift）必须至少有一个是可见的visible_flag
            for (OmsGetOrderDetailOrderItemListElementO orderItemElementO : outOrderItemList) {
                if("1".equals(orderItemElementO.getIsGiftBox())){ // 如果是礼盒，需礼盒下面的所有商品都可售，此礼盒才可售
                    String giftBoxCanSale = "1";
                    // 循环为礼品明细设置是否可见
                    List<OmsGetOrderDetailGiftBoxItemListElementO> giftBoxItemListElementO = orderItemElementO.getGiftBoxItemList();
                    for (OmsGetOrderDetailGiftBoxItemListElementO giftBoxItemElementO : giftBoxItemListElementO) {
                        String giftCanSale = goodsCanSaleMap.get(giftBoxItemElementO.getGoodsID());
                        if ("1".equals(giftCanSale)) {
                            giftBoxItemElementO.setCanSale("1");
                            giftBoxItemElementO.setMaxBuyNum(goodsMaxBuyMap.get(giftBoxItemElementO.getGoodsID()));
                            giftBoxItemElementO.setMinBuyNum(goodsMinBuyMap.get(giftBoxItemElementO.getGoodsID()));
                        } else {
                            giftBoxItemElementO.setCanSale("0");
                            giftBoxItemElementO.setMaxBuyNum(0L);
                            giftBoxItemElementO.setMinBuyNum(0L);
                            giftBoxCanSale = "0";
                        }
                    }
                    orderItemElementO.setCanSale(giftBoxCanSale);
                    orderItemElementO.setMaxBuyNum(goodsMaxBuyMap.get(orderItemElementO.getGoodsID()));
                    orderItemElementO.setMinBuyNum(goodsMinBuyMap.get(orderItemElementO.getGoodsID()));
                    if("1".equals(giftBoxCanSale)){
                        canBuyAg = "1";
                    }
                    
                }else{ // 普通商品，非礼盒
                    String canSale = goodsCanSaleMap.get(orderItemElementO.getGoodsID());
                    if ("1".equals(canSale)) {
                        orderItemElementO.setCanSale("1");
                        orderItemElementO.setMaxBuyNum(goodsMaxBuyMap.get(orderItemElementO.getGoodsID()));
                        orderItemElementO.setMinBuyNum(goodsMinBuyMap.get(orderItemElementO.getGoodsID()));
                        if ("0".equals(orderItemElementO.getIsGift())) {
                            canBuyAg = "1";
                        }
                    } else {
                        orderItemElementO.setCanSale("0");
                        orderItemElementO.setMaxBuyNum(0L);
                        orderItemElementO.setMinBuyNum(0L);
                    }
                }
            }
            return canBuyAg;
            
        } catch (Exception e) {
            LogUtils4Oms.error(logger, uuid, "设置商品可售和再次购买错误", e, "goodsInfo", goodsInfo);
            return canBuyAg;
        }
        
    }
    
    
    /**
     * 获取进度条相关状态和时间
     * Description:<br>
     * 
     * @param outputData
     * @throws OmsDelegateException 
     */
    private int setProgressBar(String orderGeneration, long parentOrderId, long subOrderId, Map<String, Date> progressDtMap){
        int progressStatus = 0;
        
        // 父订单和子订单都要先查询父订单的流水
        List<Object[]> parentOptConditions = new ArrayList<Object[]>();
        parentOptConditions.add(new Object[]{"ORDER_ID", "=", parentOrderId});
        List<TOsParentOrderOptPO> parentOptList = tOsParentOrderOptDao.findTOsParentOrderOptPOListByCondition(parentOptConditions);
        for (TOsParentOrderOptPO parentOrderOpt : parentOptList) {
            Long orderStatus = parentOrderOpt.getOrderStatus();
            Date newDate = parentOrderOpt.getOptDt();
            if (orderStatus != null && ParentOrderStatusEnum.ORIGINAL.getStatus() == orderStatus) {
                // 提交订单
                Date oldDt = progressDtMap.get("commitDt");
                if ((oldDt != null && oldDt.before(newDate)) || (oldDt == null)) {
                    progressDtMap.put("commitDt", newDate);
                    progressStatus = progressStatus < 10?10:progressStatus;
                }
            } else if (orderStatus != null && ParentOrderStatusEnum.PAY_SUCCESS.getStatus() == orderStatus) {
                // 付款成功
                Date oldDt = progressDtMap.get("payDt");
                if ((oldDt != null && oldDt.before(newDate)) || (oldDt == null)) {
                    progressDtMap.put("payDt", newDate);
                    progressStatus = progressStatus < 20?20:progressStatus;
                }
            }
        }
        
        // 子订单,单独再查询子订单流水
        if ("2".equals(orderGeneration)) {
            List<Object[]> subOptConditions = new ArrayList<Object[]>();
            subOptConditions.add(new Object[]{"ORDER_ID", "=", subOrderId});
            List<OsOrderOptPO> subOptList = osOrderOptDao.findOsOrderOptPOListByCondition(subOptConditions);
            
            for (OsOrderOptPO subOrderOpt : subOptList) {
                String orderStatus = subOrderOpt.getOrderStatus();
                Date newDate = subOrderOpt.getOptDt();
                
                if (OrderStatusEnum.SELF_SEND.getC().equals(orderStatus)) {
                    // 自营发货
                    progressDtMap.put("selfSendDt", newDate);
                }
                
                
                if (OrderStatusEnum.OUT_OF_STORAGE.getC().equals(orderStatus)) {
                    // 商品出库
                    Date oldDt = progressDtMap.get("outDt");
                    if ((oldDt != null && oldDt.before(newDate)) || (oldDt == null)) {
                        progressDtMap.put("outDt", newDate);
                        progressStatus = progressStatus < 30?30:progressStatus;
                    }
                } else if (OrderStatusEnum.SHIPPING.getC().equals(orderStatus) || OrderStatusEnum.THIRD_SEND.getC().equals(orderStatus)
                                || OrderStatusEnum.SENDING.getC().equals(orderStatus)){
                    // 派件中
                    Date oldDt = progressDtMap.get("sendDt");
                    if ((oldDt != null && oldDt.before(newDate)) || (oldDt == null)) {
                        progressDtMap.put("sendDt", newDate);
                        progressStatus = progressStatus < 40?40:progressStatus;
                    }
                } else if (OrderStatusEnum.getOrderStatusByGroup(OrderStatusGroupEnum.SIGNED.getC()).contains(orderStatus)
                                || OrderStatusEnum.getOrderStatusByGroup(OrderStatusGroupEnum.ALL_SIGNED.getC()).contains(orderStatus)
                                || OrderStatusEnum.getOrderStatusByGroup(OrderStatusGroupEnum.REJECTE.getC()).contains(orderStatus)
                                || OrderStatusEnum.getOrderStatusByGroup(OrderStatusGroupEnum.REJECTED.getC()).contains(orderStatus)
                                || OrderStatusEnum.getOrderStatusByGroup(OrderStatusGroupEnum.DELIVER_FAIL.getC()).contains(orderStatus)
                                || OrderStatusEnum.getOrderStatusByGroup(OrderStatusGroupEnum.DELIVER_FAILED.getC()).contains(orderStatus)){
                    // 已完成
                    Date oldDt = progressDtMap.get("finishDt");
                    if ((oldDt != null && oldDt.before(newDate)) || (oldDt == null)) {
                        progressDtMap.put("finishDt", newDate);
                        progressStatus = progressStatus < 50?50:progressStatus;
                    }
                }
                
            }
            
        }
        
        return progressStatus;
        
    }
    
    
    /**
     * Description:<br>
     * 从原始支付计划表里查询已支付金额、优惠券金额
     * 
     * @param parentOrderPO
     * @return
     */
    private Map<String, BigDecimal> getFeeByOsPayPlan(TOsParentOrderPO parentOrderPO){
        Map<String, BigDecimal> payFeeMap = new HashMap<String, BigDecimal>();
        BigDecimal hasPayFee = BigDecimal.ZERO;
        BigDecimal couponFee = BigDecimal.ZERO;
        
        List<Object[]> paySelectCondition = new ArrayList<Object[]>();
        paySelectCondition.add(new Object[]{"ORDER_ID", "=", parentOrderPO.getOrderId()});
        List<OsPayPlanPO> payPlan = osPayPlanDao.findOsPayPlanPOListByCondition(paySelectCondition);
        if (payPlan == null || payPlan.isEmpty() || payPlan.size() != 1) {
            Log.error(logger, uuid, "此订单支付计划有误,数据错误", null, "orderCode", parentOrderPO.getOrderCode(), "orderId", parentOrderPO.getOrderId());
            payFeeMap.put("hasPayFee", hasPayFee);
            payFeeMap.put("couponFee", couponFee);
            return payFeeMap;
        }
        OsPayPlanPO payPlanPo = payPlan.get(0);
        if (StringUtil.isEmpty(payPlanPo.getPayInfo())){
            Log.error(logger, uuid, "此订单支付方式有误,数据错误", null, "orderCode", parentOrderPO.getOrderCode(), "orderId", parentOrderPO.getOrderId());
            payFeeMap.put("hasPayFee", hasPayFee);
            payFeeMap.put("couponFee", couponFee);
            return payFeeMap;
        }
        Log.info(logger, uuid, "查询原始订单计划结束", "orderCode", parentOrderPO.getOrderCode());
        
        Map<Long, Long> payMethodMap = DictionaryData.getPayMethodPidMap(logger, uuid);
        Log.info(logger, uuid, "支付方式", "payMethodMap", payMethodMap);
        Map<Long, BigDecimal> payInfoMap = new HashMap<Long, BigDecimal>();
        String[] payInfoArray = payPlanPo.getPayInfo().split("\\^");
        for (String s : payInfoArray) {
            String[] array = s.split("_");
            BigDecimal bd = new BigDecimal(array[1]);
            bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
            
            Long key = payMethodMap.get(Long.valueOf(array[0]));
            if(key == 0L){
                key = Long.valueOf(array[0]);
            }
            payInfoMap.put(key, bd);
        }
        
        for (Map.Entry<Long, BigDecimal> entry : payInfoMap.entrySet()) {
            if (entry.getKey().intValue() == 4) {
                // 优惠券
                couponFee = couponFee.add(entry.getValue());
            } else if(entry.getKey().intValue() == 1 || entry.getKey().intValue() == 2){
                // 账户余额  + 礼品卡
                hasPayFee = hasPayFee.add(entry.getValue());
            }
        }
        
        payFeeMap.put("couponFee", couponFee);
        if (parentOrderPO.getOrderStatus() >= 0L && parentOrderPO.getOrderStatus() < 30L) {
            payFeeMap.put("hasPayFee", hasPayFee);
        } else {
            BigDecimal hasPayFee2 = parentOrderPO.getOnlinePayFee() == null?BigDecimal.ZERO:parentOrderPO.getOnlinePayFee(); // 已支付金额＝用户已支付金额
            if (PayStatusEnum.YES.getC().equals(parentOrderPO.getPayStatus())) {
                hasPayFee2 = hasPayFee2.add(parentOrderPO.getOfflinePayFee());
            }
            payFeeMap.put("hasPayFee", hasPayFee2);
        }
        
        return payFeeMap;
    }
    
    
    /**
     * Description:<br>
     * 为礼盒的壳子设置实重为其明细实重的总和
     * 只针对出库后非作废的子订单
     * 
     * @param outOrderItemList
     * @return
     */
    private void setGiftBoxWeight(List<OmsGetOrderDetailOrderItemListElementO> outOrderItemList) {
        for (OmsGetOrderDetailOrderItemListElementO orderItemElementO : outOrderItemList) {
            if ("1".equals(orderItemElementO.getIsGiftBox()) && orderItemElementO.getRealOutNum() != null){
                BigDecimal giftBoxWeight = BigDecimal.ZERO;
                for (OmsGetOrderDetailGiftBoxItemListElementO giftBoxItemElementO : orderItemElementO.getGiftBoxItemList()) {
                    BigDecimal giftBoxItemWeight = giftBoxItemElementO.getGoodsWeight();
                    if (giftBoxItemWeight != null) {
                        giftBoxWeight = giftBoxWeight.add(giftBoxItemWeight);
                    }
                }
                orderItemElementO.setGoodsWeight(giftBoxWeight);
            }
        }
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
    public void doClose(final BaseInputHead inputHead, final OmsGetOrderDetailInputData inputData, final BaseOutputHead outputHead, final OmsGetOrderDetailOutputData outputData) throws OmsDelegateException {
        
    }
}
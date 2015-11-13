package cn.tootoo.soa.oms.getorderpay.delegate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.tootoo.db.egrocery.osorder.OsOrderPO;
import cn.tootoo.db.egrocery.ospayplan.OsPayPlanPO;
import cn.tootoo.db.egrocery.tosparentorder.TOsParentOrderPO;
import cn.tootoo.enums.BaseResultEnum;
import cn.tootoo.global.Global;
import cn.tootoo.http.AuthorizeClient;
import cn.tootoo.http.TootooService;
import cn.tootoo.http.bean.BaseInputHead;
import cn.tootoo.http.bean.BaseOutputBean;
import cn.tootoo.http.bean.BaseOutputHead;
import cn.tootoo.soa.base.enums.BaseOrderResultEnum;
import cn.tootoo.soa.base.enums.OrderStatusEnum;
import cn.tootoo.soa.base.enums.PayStatusEnum;
import cn.tootoo.soa.base.global.DictionaryData;
import cn.tootoo.soa.oms.exceptions.OmsDelegateException;
import cn.tootoo.soa.oms.getorderpay.input.OmsGetOrderPayInputData;
import cn.tootoo.soa.oms.getorderpay.output.OmsGetOrderPayOutputData;
import cn.tootoo.soa.oms.getorderpay.output.OmsGetOrderPayPayItemsListElementO;
import cn.tootoo.soa.oms.utils.LogUtils4Oms;
import cn.tootoo.soa.payment.getpaymentinfo.input.PaymentGetPaymentInfoInputData;
import cn.tootoo.soa.payment.getpaymentinfo.input.PaymentGetPaymentInfoOrderCodeListElementI;
import cn.tootoo.soa.payment.getpaymentinfo.output.PaymentGetPaymentInfoOutputData;
import cn.tootoo.soa.payment.getpaymentinfo.output.PaymentGetPaymentInfoPayItemsListElementO;
import cn.tootoo.soa.payment.getpaymentinfo.output.PaymentGetPaymentInfoPaymentInfoListElementO;
import cn.tootoo.utils.Log;
import cn.tootoo.utils.StringUtil;

/**
 * 服务委托者实现类。
 * 服务description：订单服务。
 * 服务remark：订单服务。
 * 接口description：获取订单付款信息
 * 接口remark：
 */
public final class GetOrderPayDelegate extends AbstractGetOrderPayDelegate implements Cloneable {
    
    private String scope = "";
    static {
        GetOrderPayDelegateFactory.registPrototype(new GetOrderPayDelegate());
    }
    
    /**
     * 克隆方法。
     */
    @Override
    public AbstractGetOrderPayDelegate clone() throws CloneNotSupportedException {
        return new GetOrderPayDelegate();
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
    public void doInit(final BaseInputHead inputHead, final OmsGetOrderPayInputData inputData, final BaseOutputHead outputHead, final OmsGetOrderPayOutputData outputData) throws OmsDelegateException {
        
    }
    
    /**
     * 初始检查方法。
     * 该方法在实际执行服务功能之前进行检查，默认返回true。
     * 
     * @param inputHead 该接口的请求参数头。
     * @param inputData 该接口的请求参数数据体。
     * @param outputHead 该接口的返回结果头。
     * @param outputData 该接口的返回结果数据体。
     * @return boolean 检查是否通过。
     */
    @Override
    public boolean doCheck(final BaseInputHead inputHead, final OmsGetOrderPayInputData inputData, final BaseOutputHead outputHead, final OmsGetOrderPayOutputData outputData) {
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
    public void doExecute(final BaseInputHead inputHead, final OmsGetOrderPayInputData inputData, final BaseOutputHead outputHead, final OmsGetOrderPayOutputData outputData) throws OmsDelegateException {
        // 登录验证信息
        String userID = "";
        scope = inputData.getScope();
        try {
            LogUtils4Oms.info(logger, uuid, "获取订单付款信息服务,进行验证！");
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
        
        LogUtils4Oms.info(logger, uuid, "获取订单付款信息服务开始", "inputData", inputData);
        String orderGeneration = inputData.getOrderGeneration();
        String orderCode = inputData.getOrderCode();
        
        if ("1".equals(orderGeneration)) {
            // 父订单
            
            List<Object[]> parentOrderConditions = new ArrayList<Object[]>();
            parentOrderConditions.add(new Object[]{"BUYER_ID", "=", userID});
            parentOrderConditions.add(new Object[]{"ORDER_CODE", "=", orderCode});
            List<TOsParentOrderPO> parentOrderList = tOsParentOrderDao.findTOsParentOrderPOListByCondition(parentOrderConditions);
            if (parentOrderList == null || parentOrderList.size() <= 0) {
                // 没有查询到父订单数据
                LogUtils4Oms.info(logger, uuid, "查询parentOrderList为空", "parentOrderConditions", parentOrderConditions);
                Global.getOutputHead(BaseResultEnum.REQUEST_PARAM_ERROR.getResultID(), scope, outputHead);
                return;
            }
            
            TOsParentOrderPO parentOrderPO = parentOrderList.get(0);
            // 父订单商品总额
            BigDecimal parentGoodsCallFee = parentOrderPO.getGoodsCallFee() == null?BigDecimal.ZERO:parentOrderPO.getGoodsCallFee();
            // 父订单运费
            BigDecimal parentShipCallFee = parentOrderPO.getShipCallFee() == null?BigDecimal.ZERO:parentOrderPO.getShipCallFee();
            // 父订单优惠金额
            BigDecimal parentDiscountFee = parentOrderPO.getDiscountFee() == null?BigDecimal.ZERO:parentOrderPO.getDiscountFee();
            // 父订单优惠券金额
            BigDecimal parentCouponFee = getFeeByOsPayPlan(parentOrderPO);
            
            // 父订单应付总金额=父订单商品总额+父订单运费总额-父订单优惠总额-父订单优惠券总额
            BigDecimal parentShouldPayFee = parentGoodsCallFee.add(parentShipCallFee).subtract(parentDiscountFee).subtract(parentCouponFee);
            
            outputData.setParentGoodsCallFee(parentGoodsCallFee);
            outputData.setParentShipCallFee(parentShipCallFee);
            outputData.setParentDiscountFee(parentDiscountFee);
            outputData.setParentCouponFee(parentCouponFee);
            outputData.setParentShouldPayFee(parentShouldPayFee);
            
            // 调用支付接口set各支付方式、支付明细表
            this.setOrderPayDetail(outputData, orderCode, orderGeneration);
            
            Global.getOutputHead(BaseResultEnum.SUCCESS.getResultID(), scope, outputHead);
            
        } else if ("2".equals(orderGeneration)) {
            // 子订单
            
            List<Object[]> orderConditions = new ArrayList<Object[]>();
            orderConditions.add(new Object[]{"BUYER_ID", "=", userID});
            orderConditions.add(new Object[]{"ORDER_CODE", "=", orderCode});
            List<OsOrderPO> orderList = osOrderDao.findOsOrderPOListByCondition(orderConditions);
            if (orderList == null || orderList.size() <= 0) {
                // 没有查询到子订单数据
                LogUtils4Oms.info(logger, uuid, "查询orderList为空", "orderConditions", orderConditions);
                Global.getOutputHead(BaseResultEnum.REQUEST_PARAM_ERROR.getResultID(), scope, outputHead);
                return;
            }
            
            OsOrderPO orderPO = orderList.get(0);
            Long parentOrderId = orderPO.getParentId();
            // 父订单
            TOsParentOrderPO parentOrderPO = tOsParentOrderDao.findTOsParentOrderPOByID(parentOrderId);
            
            // 父订单商品总额
            BigDecimal parentGoodsCallFee = parentOrderPO.getGoodsCallFee() == null?BigDecimal.ZERO:parentOrderPO.getGoodsCallFee();
            // 父订单运费
            BigDecimal parentShipCallFee = parentOrderPO.getShipCallFee() == null?BigDecimal.ZERO:parentOrderPO.getShipCallFee();
            // 父订单优惠金额
            BigDecimal parentDiscountFee = parentOrderPO.getDiscountFee() == null?BigDecimal.ZERO:parentOrderPO.getDiscountFee();
            // 父订单优惠券金额
            BigDecimal parentCouponFee = parentOrderPO.getCouponFee() == null?BigDecimal.ZERO:parentOrderPO.getCouponFee();
            // 父订单应付总金额=父订单商品总额+父订单运费总额-父订单优惠总额-父订单优惠券总额
            BigDecimal parentShouldPayFee = parentGoodsCallFee.add(parentShipCallFee).subtract(parentDiscountFee).subtract(parentCouponFee);
            
            outputData.setParentGoodsCallFee(parentGoodsCallFee);
            outputData.setParentShipCallFee(parentShipCallFee);
            outputData.setParentDiscountFee(parentDiscountFee);
            outputData.setParentCouponFee(parentCouponFee);
            outputData.setParentShouldPayFee(parentShouldPayFee);
            
            // 子订单商品总额
            BigDecimal subGoodsCallFee = BigDecimal.ZERO;
            if (OrderStatusEnum.isBeforeOutOfStorage(orderPO.getOrderStatus()) || OrderStatusEnum.isCancel(orderPO.getOrderStatus())) {
                subGoodsCallFee = orderPO.getGoodsCallFee();
            } else {
                subGoodsCallFee = orderPO.getGoodsPackfreshFee() == null?orderPO.getGoodsCallFee():orderPO.getGoodsPackfreshFee();
            }
            // 子订单运费
            BigDecimal subShipCallFee = orderPO.getShipCallFee() == null?BigDecimal.ZERO:orderPO.getShipCallFee();
            // 子订单优惠金额
            BigDecimal subDiscountFee = orderPO.getDiscountFee() == null?BigDecimal.ZERO:orderPO.getDiscountFee();
            // 子订单优惠券金额
            BigDecimal subCouponFee = orderPO.getCouponFee() == null?BigDecimal.ZERO:orderPO.getCouponFee();
            // 应付金额=商品总额+运费-优惠金额-优惠券金额
            BigDecimal subShouldPayFee = subGoodsCallFee.add(subShipCallFee).subtract(subDiscountFee).subtract(subCouponFee);
            
            outputData.setSubGoodsCallFee(subGoodsCallFee);
            outputData.setSubShipCallFee(subShipCallFee);
            outputData.setSubDiscountFee(subDiscountFee);
            outputData.setSubCouponFee(subCouponFee);
            outputData.setSubShouldPayFee(subShouldPayFee);
            
            // 调用支付接口set各支付方式、支付明细表
            this.setOrderPayDetail(outputData, orderCode, orderGeneration);
            
            Global.getOutputHead(BaseResultEnum.SUCCESS.getResultID(), scope, outputHead);
            
        } else {
            LogUtils4Oms.info(logger, uuid, "参数orderGeneration非法", "orderGeneration", orderGeneration);
            Global.getOutputHead(BaseResultEnum.REQUEST_PARAM_ERROR.getResultID(), scope, outputHead);
            return;
        }
        
    }
    
    /**
     * 调用支付接口，查询订单支付信息
     * Description:<br>
     * 
     * @param orderCode
     * @return
     */
    private void setOrderPayDetail(OmsGetOrderPayOutputData outputData, String orderCode, String orderGeneration) {
        try {
            PaymentGetPaymentInfoInputData paymentI = new PaymentGetPaymentInfoInputData();
            List<PaymentGetPaymentInfoOrderCodeListElementI> orderCodeListI = new ArrayList<PaymentGetPaymentInfoOrderCodeListElementI>();
            PaymentGetPaymentInfoOrderCodeListElementI orderCodeI = new PaymentGetPaymentInfoOrderCodeListElementI();
            orderCodeI.setOrderCode(orderCode);
            orderCodeI.setOrderGeneration(orderGeneration);
            orderCodeListI.add(orderCodeI);
            paymentI.setScope(scope);
            paymentI.setOrderCodeList(orderCodeListI);
            LogUtils4Oms.info(logger, uuid, "调用订单支付信息接口入参", "json", paymentI.toJson());
            
            HashMap<String, String> params = (HashMap<String, String>)AuthorizeClient.getVerifyInfo(httpRequest);
            Map<String, String> paramsMap = (Map<String, String>)params.clone();
            paramsMap.put("method", "getPaymentInfo");
            paramsMap.put("req_str", paymentI.toJson());
            PaymentGetPaymentInfoOutputData getPaymentInfoOutputData = new PaymentGetPaymentInfoOutputData();
            BaseOutputBean baseBean = TootooService.callServer("payment", paramsMap, "post", getPaymentInfoOutputData);
            LogUtils4Oms.info(logger, uuid, "调用订单支付信息接口出参", "baseBean", baseBean);
            
            if (!baseBean.getOutputHead().getResultID().equals(BaseResultEnum.SUCCESS.getResultID())) {
                LogUtils4Oms.error(logger, uuid, "调用订单支付信息接口返回异常", "resultID", baseBean.getOutputHead().getResultID());
                return;
            }
            
            PaymentGetPaymentInfoOutputData output = (PaymentGetPaymentInfoOutputData)baseBean.getOutputData();
            LogUtils4Oms.info(logger, uuid, "调用获取订单支付信息接口成功");
            
            List<PaymentGetPaymentInfoPaymentInfoListElementO> paymentInfoListO = output.getPaymentInfoList();
            if (paymentInfoListO == null || paymentInfoListO.size() != 1) {
                LogUtils4Oms.error(logger, uuid, "调用订单支付信息接口返回信息为空", "paymentInfoListO", paymentInfoListO);
                return;
            }
            PaymentGetPaymentInfoPaymentInfoListElementO paymentInfoO = paymentInfoListO.get(0);
            List<PaymentGetPaymentInfoPayItemsListElementO> payItemsListO = paymentInfoO.getPayItemsList();
            
            List<OmsGetOrderPayPayItemsListElementO> omsPayItemsList = new ArrayList<OmsGetOrderPayPayItemsListElementO>();
            for (PaymentGetPaymentInfoPayItemsListElementO payItemsO : payItemsListO) {
                /*
                 * 支付方式：此处显示的支付方式包括预存账户余额、礼品卡、各具体的支付平台、网银、
                 * 货到付款（通过物流POS读取应该比较复杂，可以不要）。其中支付平台目前有支付宝和智慧支付，
                 * 除平台支付以外的在线支付的支付方式统称为“网银”，
                 * 优惠券及货到付款不在支付明细表里显示。
                 */
                String payCode = payItemsO.getPayCode();
                if(StringUtil.isEmpty(payCode)){
                    payCode = "--";
                }
                String method = payItemsO.getMethod();
                String payMethodTitle = "";
                if ("account".equalsIgnoreCase(method) || "giftcard".equalsIgnoreCase(method) || method.toLowerCase().contains("alipay")
                                || "Zhihuika".equalsIgnoreCase(method)) {
                    if("21101".equals(scope)){ // 英文站
                        payMethodTitle = payItemsO.getPayMethodTitleEn();
                    }else{
                        payMethodTitle = payItemsO.getPayMethodTitle();
                    }
                } else if (method.startsWith("coupon") || method.startsWith("offline") || "arrear".equalsIgnoreCase(method)) {
                    continue;
                } else {
                    if("21101".equals(scope)){ // 英文站
                        payMethodTitle = "Online Banking";
                    }else{
                        payMethodTitle = "网银";
                    }
                }
                
                OmsGetOrderPayPayItemsListElementO omsPayItems = new OmsGetOrderPayPayItemsListElementO();
                omsPayItems.setPayDt(payItemsO.getPayDt());
                omsPayItems.setPayCode(payCode);
                omsPayItems.setMethod(method);
                omsPayItems.setPayMethod(payMethodTitle);
                omsPayItems.setPayFee(payItemsO.getPayFee());
                omsPayItems.setPayFeeParent(payItemsO.getPayFeeParent());
                omsPayItemsList.add(omsPayItems);
            }
            
            // 缺货出库金额
            outputData.setRefundOfLackFee(paymentInfoO.getRefundOfLackFee());
            // 各支付方式、支付明细表
            outputData.setPayItemsList(omsPayItemsList);
            
        } catch (Exception e) {
            LogUtils4Oms.error(logger, uuid, "调用订单支付信息接口返回异常", e);
        }
    }
    
    /**
     * Description:<br>
     * 从原始支付计划表里查询优惠券金额
     * 
     * @param parentOrderPO
     * @return
     */
    private BigDecimal getFeeByOsPayPlan(TOsParentOrderPO parentOrderPO){
        BigDecimal couponFee = BigDecimal.ZERO;
        
        List<Object[]> paySelectCondition = new ArrayList<Object[]>();
        paySelectCondition.add(new Object[]{"ORDER_ID", "=", parentOrderPO.getOrderId()});
        List<OsPayPlanPO> payPlan = osPayPlanDao.findOsPayPlanPOListByCondition(paySelectCondition);
        if (payPlan == null || payPlan.isEmpty() || payPlan.size() != 1) {
            Log.error(logger, uuid, "此订单支付计划有误,数据错误", null, "orderCode", parentOrderPO.getOrderCode(), "orderId", parentOrderPO.getOrderId());
            return couponFee;
        }
        OsPayPlanPO payPlanPo = payPlan.get(0);
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
            }
        }
        
        return couponFee;
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
    public void doClose(final BaseInputHead inputHead, final OmsGetOrderPayInputData inputData, final BaseOutputHead outputHead, final OmsGetOrderPayOutputData outputData) throws OmsDelegateException {
        
    }
}
package cn.tootoo.soa.oms.createorder;

import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import cn.tootoo.db.egrocery.buildingqualification.BuildingQualificationDao;
import cn.tootoo.db.egrocery.buyerfirstorder.BuyerFirstOrderDao;
import cn.tootoo.db.egrocery.ospayplan.OsPayPlanDao;
import cn.tootoo.db.egrocery.ospayplan.OsPayPlanPO;
import cn.tootoo.db.egrocery.tastedetail.TasteDetailDao;
import cn.tootoo.db.egrocery.tastedetail.TasteDetailPO;
import cn.tootoo.db.egrocery.texvoucher.TExVoucherDao;
import cn.tootoo.db.egrocery.tosparentorder.TOsParentOrderDao;
import cn.tootoo.db.egrocery.tosparentorder.TOsParentOrderPO;
import cn.tootoo.db.egrocery.tosparentorderitems.TOsParentOrderItemsDao;
import cn.tootoo.db.egrocery.tosparentorderitems.TOsParentOrderItemsPO;
import cn.tootoo.db.egrocery.tosparentorderopt.TOsParentOrderOptDao;
import cn.tootoo.db.egrocery.tosparentorderopt.TOsParentOrderOptPO;
import cn.tootoo.db.egrocery.ttsprivateteamjoin.TtsPrivateTeamJoinDao;
import cn.tootoo.db.egrocery.ttsprivateteamjoin.TtsPrivateTeamJoinPO;
import cn.tootoo.enums.BaseResultEnum;
import cn.tootoo.enums.BooleanEnum;
import cn.tootoo.http.AuthorizeClient;
import cn.tootoo.http.TootooService;
import cn.tootoo.http.bean.BaseInputBean;
import cn.tootoo.http.bean.BaseOutputBean;
import cn.tootoo.soa.address.getaddressbyid.output.AddressGetAddressByIDOutputData;
import cn.tootoo.soa.address.updatedefaultaddress.input.AddressUpdateDefaultAddressInputData;
import cn.tootoo.soa.address.updatedefaultaddress.output.AddressUpdateDefaultAddressOutputData;
import cn.tootoo.soa.base.bean.GiftItemsBean;
import cn.tootoo.soa.base.bean.GoodsNumberBean;
import cn.tootoo.soa.base.bean.GoodsWarehouseAndDcBean;
import cn.tootoo.soa.base.enums.BaseOrderResultEnum;
import cn.tootoo.soa.base.enums.DeliveryTypeEnum;
import cn.tootoo.soa.base.enums.OrderTypeEnum;
import cn.tootoo.soa.base.enums.ParentOrderStatusEnum;
import cn.tootoo.soa.base.enums.PayMethodEnum;
import cn.tootoo.soa.base.enums.PayStatusEnum;
import cn.tootoo.soa.base.enums.PayTypeEnum;
import cn.tootoo.soa.base.enums.PromotionStatusEnum;
import cn.tootoo.soa.base.enums.PromotionTypeEnum;
import cn.tootoo.soa.base.enums.TimeShipTypeEnum;
import cn.tootoo.soa.base.global.DictionaryData;
import cn.tootoo.soa.base.global.SpecialInfos;
import cn.tootoo.soa.base.util.BuyerFirstOrderUtil;
import cn.tootoo.soa.base.util.OrderUtil;
import cn.tootoo.soa.base.util.PromotionScopeUtil;
import cn.tootoo.soa.goods.getgoodsinfo.output.GoodsGetGoodsInfoGoodsInfoElementO;
import cn.tootoo.soa.goods.getgoodsinfo.output.GoodsGetGoodsInfoSaleCatsElementO;
import cn.tootoo.soa.goods.getgoodsinfo.output.GoodsGetGoodsInfoSendDaysElementO;
import cn.tootoo.soa.goods.getgoodsinfo.output.GoodsGetGoodsInfoStairPriceElementO;
import cn.tootoo.soa.goods.getgoodsinfo.output.GoodsGetGoodsInfoVipPriceElementO;
import cn.tootoo.soa.goods.getgoodssalecats.input.GoodsGetGoodsSaleCatsGoodsIDsElementI;
import cn.tootoo.soa.goods.getgoodssalecats.input.GoodsGetGoodsSaleCatsInputData;
import cn.tootoo.soa.goods.getgoodssalecats.output.GoodsGetGoodsSaleCatsGoodsSaleCatInfosElementO;
import cn.tootoo.soa.goods.getgoodssalecats.output.GoodsGetGoodsSaleCatsOutputData;
import cn.tootoo.soa.oms.createorder.input.OmsCreateOrderExGoodsIDElementI;
import cn.tootoo.soa.oms.createorder.input.OmsCreateOrderExListElementI;
import cn.tootoo.soa.oms.createorder.input.OmsCreateOrderGiftCardGoodsListElementI;
import cn.tootoo.soa.oms.createorder.input.OmsCreateOrderGiftGoodsIDElementI;
import cn.tootoo.soa.oms.createorder.input.OmsCreateOrderGiftListElementI;
import cn.tootoo.soa.oms.createorder.input.OmsCreateOrderGoodsListElementI;
import cn.tootoo.soa.oms.createorder.input.OmsCreateOrderGroupBuyingGoodsListElementI;
import cn.tootoo.soa.oms.createorder.input.OmsCreateOrderInputData;
import cn.tootoo.soa.oms.createorder.input.OmsCreateOrderNyGoodsListElementI;
import cn.tootoo.soa.oms.createorder.input.OmsCreateOrderNyListElementI;
import cn.tootoo.soa.oms.createorder.input.OmsCreateOrderPayListElementI;
import cn.tootoo.soa.oms.createorder.input.OmsCreateOrderTihuoGoodsListElementI;
import cn.tootoo.soa.oms.createorder.output.OmsCreateOrderOrderItemListElementO;
import cn.tootoo.soa.oms.createorder.output.OmsCreateOrderOutputData;
import cn.tootoo.soa.oms.utils.SqlUtil;
import cn.tootoo.soa.oms.utils.ThirdOrderCookieUtil;
import cn.tootoo.soa.promotion.getpromotionbyid.output.PromotionGetPromotionByIdDetailElementO;
import cn.tootoo.soa.promotion.getpromotionbyid.output.PromotionGetPromotionByIdOutputData;
import cn.tootoo.soa.promotion.getpromotionbyid.output.PromotionGetPromotionByIdPromotionListElementO;
import cn.tootoo.soa.promotion.getpromotioninfo.input.PromotionGetPromotionInfoGoodsListCateElementI;
import cn.tootoo.soa.promotion.getpromotioninfo.input.PromotionGetPromotionInfoGoodsListElementI;
import cn.tootoo.soa.promotion.getpromotioninfo.input.PromotionGetPromotionInfoInputData;
import cn.tootoo.soa.promotion.getpromotioninfo.input.PromotionGetPromotionInfoNotCareElementI;
import cn.tootoo.soa.promotion.getpromotioninfo.input.PromotionGetPromotionInfoNyListElementI;
import cn.tootoo.soa.promotion.getpromotioninfo.input.PromotionGetPromotionInfoNyListSkuIdListElementI;
import cn.tootoo.soa.promotion.getpromotioninfo.output.PromotionGetPromotionInfoDiscountListElementO;
import cn.tootoo.soa.promotion.getpromotioninfo.output.PromotionGetPromotionInfoGiftGoodsListElementO;
import cn.tootoo.soa.promotion.getpromotioninfo.output.PromotionGetPromotionInfoGiftListElementO;
import cn.tootoo.soa.promotion.getpromotioninfo.output.PromotionGetPromotionInfoGoodsListElementO;
import cn.tootoo.soa.promotion.getpromotioninfo.output.PromotionGetPromotionInfoItemsElementO;
import cn.tootoo.soa.promotion.getpromotioninfo.output.PromotionGetPromotionInfoNyItemsElementO;
import cn.tootoo.soa.promotion.getpromotioninfo.output.PromotionGetPromotionInfoNyListElementO;
import cn.tootoo.soa.promotion.getpromotioninfo.output.PromotionGetPromotionInfoNyListSkuIdListElementO;
import cn.tootoo.soa.promotion.getpromotioninfo.output.PromotionGetPromotionInfoOutputData;
import cn.tootoo.soa.shipping.getgoodsshippingfee.input.ShippingGetGoodsShippingFeeGiftItemsElementI;
import cn.tootoo.soa.shipping.getgoodsshippingfee.input.ShippingGetGoodsShippingFeeGoodsListElementI;
import cn.tootoo.soa.shipping.getgoodsshippingfee.input.ShippingGetGoodsShippingFeeInputData;
import cn.tootoo.soa.shipping.getgoodsshippinginfos.input.ShippingGetGoodsShippingInfosGeoListElementI;
import cn.tootoo.soa.shipping.getgoodsshippinginfos.input.ShippingGetGoodsShippingInfosGiftItemsElementI;
import cn.tootoo.soa.shipping.getgoodsshippinginfos.input.ShippingGetGoodsShippingInfosGoodsListElementI;
import cn.tootoo.soa.shipping.getgoodsshippinginfos.input.ShippingGetGoodsShippingInfosInputData;
import cn.tootoo.soa.shipping.getgoodsshippinginfos.input.ShippingGetGoodsShippingInfosShippingDatesElementI;
import cn.tootoo.utils.DateUtil;
import cn.tootoo.utils.Log;
import cn.tootoo.utils.Memcached;
import cn.tootoo.utils.ResponseUtil;
import cn.tootoo.utils.StringUtil;

public class CreateOrderBaseService {
    
    protected String uuid;
    
    protected Logger logger;
    
    public CreateOrderBaseService(String uuid, Logger logger)
    {
        super();
        this.uuid = uuid;
        this.logger = logger;
    }
    
    public BaseOutputBean createOrder(HttpServletRequest request, BaseInputBean inputBean, OmsCreateOrderInputData inputData, BaseOutputBean outputBean, OmsCreateOrderOutputData outputData, HashMap<String, String> params, HashMap<Long, GoodsNumberBean> goodsCount, Map<Long, PromotionGetPromotionByIdDetailElementO> groupMap, Map<Long, GoodsGetGoodsInfoGoodsInfoElementO> goodsInfo, Map<String, String> buyFromMap, Map<Long, GoodsWarehouseAndDcBean> goodsWarehouseAndDcBean, Map<Long, String> goodsShipCapacityMap, Map<String, BigDecimal> discountMap, Map<String, BigDecimal> otherDiscountMap,
    
    AddressGetAddressByIDOutputData addressOutputData, PromotionGetPromotionInfoOutputData promotionOutputData,
    
    TOsParentOrderDao parentOrderDao,
    
    StringBuffer bsMobile, StringBuffer goodsIDMax,
    
    String isIncludeSpecial, String isCheckCOD, String couponSN, String receiveDate,
    
    BigDecimal sendAmount, BigDecimal shipFee, BigDecimal shipTotalFee, BigDecimal deliveryTimeFee, BigDecimal privateTeamStepPrice, Long payMethodId, boolean canReserve, boolean haveOnlinePay, boolean groupWy, TtsPrivateTeamJoinDao ttsPrivateTeamJoinDao, TtsPrivateTeamJoinPO ttsPrivateTeamJoinPO) {
        
        /******************************************************************************* 数据库操作开始 ********************************************************************************************/
        
        // 操作数据库插入订单表和订单详情表
        TOsParentOrderItemsDao parentOrderItemDao = new TOsParentOrderItemsDao(uuid, logger);
        TOsParentOrderOptDao parentOrderOptDao = new TOsParentOrderOptDao(uuid, logger);
        OsPayPlanDao payPlanDao = new OsPayPlanDao(uuid, logger);
        BuildingQualificationDao buildingQualificationDao = new BuildingQualificationDao(uuid, logger);
        TExVoucherDao tExVoucherDao = new TExVoucherDao(uuid, logger);
        try {
            Long orderId = parentOrderDao.findSeqNextVal("SEQ_OS_PARENT_ORDER_PK");
            if (orderId.intValue() == -1) {
                Log.info(logger, uuid, "获得父订单主键失败", "orderId", orderId);
                outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.INSIDE_ERROR.getResultID(), null, inputData.getScope());
                Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
                return outputBean;
            }
            Log.info(logger, uuid, "获得父订单主键成功", "orderId", orderId);
            
            String orderCode = SqlUtil.getParentOrderCodeLoop(logger, uuid, parentOrderDao.getWriteConnectionName());
            if (StringUtil.isEmpty(orderCode)) {
                Log.info(logger, uuid, "获得父订单的订单号失败", "orderCode", orderCode);
                outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.INSIDE_ERROR.getResultID(), null, inputData.getScope());
                Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
                return outputBean;
            }
            Log.info(logger, uuid, "获得父订单的订单号", "orderCode", orderCode);
            
            Log.info(logger, uuid, "测试信息9", "goodsCount", goodsCount);
            List<TOsParentOrderItemsPO> parentOrderItemsPoList = getParentItems(inputData, parentOrderItemDao, isIncludeSpecial, goodsShipCapacityMap, discountMap, otherDiscountMap, buyFromMap, orderId, inputData.getOrderType(), inputData.getScope(), receiveDate, goodsIDMax.toString(), inputData.getGroupPromotionID(), goodsInfo, promotionOutputData, goodsCount, goodsWarehouseAndDcBean, params, groupMap, privateTeamStepPrice);
            Log.info(logger, uuid, "获得父订单详情数据", "parentOrderItemsPoList", parentOrderItemsPoList);
            if (parentOrderItemsPoList == null) {// 订单类型错误
                Log.info(logger, uuid, "获得父订单详情时,订单类型错误");
                outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.INSIDE_ERROR.getResultID(), null, inputData.getScope());
                Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
                return outputBean;
            }
            StringBuffer goodsStringSb = new StringBuffer("");
            String goodsString = "";
            JSONArray arrayGoods = new JSONArray();
            for (TOsParentOrderItemsPO itemPo : parentOrderItemsPoList) {
                if (itemPo.getGiftboxId() == null
                                || itemPo.getGiftboxId().intValue() == 0) {
                    goodsStringSb.append(itemPo.getGoodsName());
                    goodsStringSb.append(itemPo.getGoodsNumber());
                    goodsStringSb.append(itemPo.getUnitName());
                    goodsStringSb.append(",");
                    
                    JSONObject jsonGoods = new JSONObject();
                    jsonGoods.put("goodsTitle", itemPo.getGoodsName());
                    jsonGoods.put("count", itemPo.getGoodsNumber()
                                    + (StringUtil.isEmpty(itemPo.getUnitName())?"":itemPo.getUnitName()));
                    arrayGoods.put(jsonGoods);
                }
            }
            if (!StringUtil.isEmpty(goodsStringSb.toString().trim())
                            && goodsStringSb.toString().trim().endsWith(",")) {
                goodsString = StringUtil.removeLast(goodsStringSb.toString().trim());
            }
            
            Log.info(logger, uuid, "获得父订单数据");
            String type = "1";
            if (SpecialInfos.specialShippingFeeMap.containsKey(params.get(AuthorizeClient.PARAM_BUYER_NAME))) {// 是特殊用户
                type = "2";
            }
            
            
            boolean saleNumAdjust = false; // 是否含有设置了增量阀值的商品
            String sellerType = null;
            Long sellerId = null; 
            for (Map.Entry<Long, GoodsGetGoodsInfoGoodsInfoElementO> entry : goodsInfo.entrySet()) {
                sellerType = entry.getValue().getSellerType();
                sellerId = entry.getValue().getSellerId();
                if (entry.getValue().getSkuInfo() != null && entry.getValue().getSkuInfo().getSaleNumAdjustValue() != null
                                && entry.getValue().getSkuInfo().getSaleNumAdjustValue().compareTo(BigDecimal.ZERO) > 0){
                    saleNumAdjust = true;
                    break;
                }
            }
            
            Log.info(logger, uuid, "判断用户首单标识");
            BuyerFirstOrderDao buyerFirstOrderDao = new BuyerFirstOrderDao(uuid, logger);
            String isFirst = BuyerFirstOrderUtil.isFirstOrder(Long.valueOf(params.get(AuthorizeClient.COOKIE_BUYER_ID)), logger, uuid, buyerFirstOrderDao);
            if("1".equals(isFirst)){
                int firstNum = BuyerFirstOrderUtil.insertFirstOrder(Long.valueOf(params.get(AuthorizeClient.COOKIE_BUYER_ID)), orderCode, inputData.getScope(), logger, uuid, buyerFirstOrderDao);
                if(firstNum != 1){
                    Log.info(logger, uuid, "插入用户首单关系表失败，不影响下单", "firstNum", firstNum);
                }
            }
            
            
            TOsParentOrderPO parentOrderPo = getParentOrder(inputData.getProvinceName(), inputData.getCityName(), inputData.getDistrictName(), inputData.getAddrDetail(), inputData.getZipcode(), inputData.getReceiver(), inputData.getTel(), inputData.getMobile(), DictionaryData.shippingCompanyTypeMap, DictionaryData.payMethodTitleEnMap, DictionaryData.payMethodTitleMap, orderId, orderCode, receiveDate, addressOutputData, inputBean, inputData, parentOrderItemsPoList, shipFee, shipTotalFee, deliveryTimeFee, promotionOutputData, params, canReserve, type, ttsPrivateTeamJoinPO, outputData.getCanChgPay(), outputData.getReceiveDt(), saleNumAdjust, isFirst, sellerType, sellerId);
            // 设置第三方的订单cookie
            parentOrderPo.setCookie(ThirdOrderCookieUtil.getCookieStr(uuid, logger, request));
            
            Log.info(logger, uuid, "获得父订单数据完成", "parentOrderPo", parentOrderPo);
            
            Log.info(logger, uuid, "插入父订单数据开始");
            int num = parentOrderDao.addTOsParentOrderPO(parentOrderPo);
            if (num != 1) {
                parentOrderDao.rollback();
                Log.info(logger, uuid, "插入父订单失败", "num", num);
                outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.INSIDE_ERROR.getResultID(), null, inputData.getScope());
                Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
                return outputBean;
            }
            Log.info(logger, uuid, "插入父订单数据结束");

            /***********订单出参增加订单金额字段 by zhaochunna 2015-8-12*************/
            outputData.setOrderCallFee(parentOrderPo.getOrderCallFee());
            
            Log.info(logger, uuid, "插入父订单详情数据开始");
            num = parentOrderItemDao.addTOsParentOrderItemsPOList(parentOrderItemsPoList);
            if (num != parentOrderItemsPoList.size()) {
                parentOrderItemDao.rollback();
                Log.info(logger, uuid, "插入父订单详情数据失败", "num", num);
                outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.INSIDE_ERROR.getResultID(), null, inputData.getScope());
                Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
                return outputBean;
            }
            Log.info(logger, uuid, "插入父订单详情数据结束");
            
            Log.info(logger, uuid, "组装父订单日志表数据开始");
            TOsParentOrderOptPO parentOrderOpt = new TOsParentOrderOptPO();
            parentOrderOpt.setOrderId(parentOrderPo.getOrderId());
            parentOrderOpt.setOptDt(new Timestamp(System.currentTimeMillis()));
            parentOrderOpt.setUserId(Long.valueOf(params.get(AuthorizeClient.COOKIE_BUYER_ID)));
            parentOrderOpt.setOrderStatus(Long.valueOf(ParentOrderStatusEnum.ORIGINAL.getStatus()));
            parentOrderOpt.setRemark("您已提交了订单，请等待系统审核");
            parentOrderOpt.setUserName("客户");
            parentOrderOpt.setIsShow("1");
            parentOrderOpt.setRemarkEn("Your order has been lodged. It is under review.");
            Log.info(logger, uuid, "组装父订单日志表数据结束");
            
            Log.info(logger, uuid, "插入父订单日志表开始");
            num = parentOrderOptDao.addTOsParentOrderOptPO(parentOrderOpt);
            if (num != 1) {
                parentOrderOptDao.rollback();
                Log.info(logger, uuid, "插入父订单日志表失败", "num", num);
                outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.INSIDE_ERROR.getResultID(), null, inputData.getScope());
                Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
                return outputBean;
            }
            Log.info(logger, uuid, "插入父订单日志表结束");
            
            Log.info(logger, uuid, "组装支付原始请求表数据开始");
            OsPayPlanPO payPlan = new OsPayPlanPO();
            payPlan.setBuyerId(Long.valueOf(params.get(AuthorizeClient.COOKIE_BUYER_ID)));
            payPlan.setCreateDate(new Timestamp(System.currentTimeMillis()));
            payPlan.setMarketId(-1L);
            payPlan.setOrderId(parentOrderPo.getOrderId());
            payPlan.setOrderType(parentOrderPo.getOrderType());
            
            StringBuffer sb = new StringBuffer("");
            StringBuffer lockPayMethodBuffer = new StringBuffer("");
            BigDecimal planAmt = BigDecimal.ZERO;
            for (OmsCreateOrderPayListElementI payElement : inputData.getPayList()) {
                planAmt = planAmt.add(payElement.getPayAmount());
                if (payElement.getPayMethodID().intValue() == PayMethodEnum.COUPON.getC()) {// 优惠劵
                    sb.append(payElement.getPayMethodID() + "_"
                                    + payElement.getPayAmount() + "_"
                                    + couponSN + "^");
                } else {
                    sb.append(payElement.getPayMethodID() + "_"
                                    + payElement.getPayAmount() + "_^");
                    lockPayMethodBuffer.append(payElement.getPayMethodID()).append(",");
                    
                }
            }
            String payInfo = "";
            if (sb.toString().endsWith("^")) {
                payInfo = StringUtil.removeLast(sb.toString());
            } else {
                payInfo = sb.toString();
            }
            payPlan.setPayInfo(payInfo);
            String lockPayMethod = "";
            if(lockPayMethodBuffer.toString().endsWith(",")){
            	lockPayMethod = StringUtil.removeLast(lockPayMethodBuffer.toString());
            } else{
            	lockPayMethod = lockPayMethodBuffer.toString();
            }
            
            if(BooleanEnum.FALSE.getV().equals(parentOrderPo.getChgPay())){//不能修改支付方式
            	payPlan.setLockedPayMenthodId(lockPayMethod);
            }
            
            payPlan.setPlanAmt(planAmt);
            Log.info(logger, uuid, "组装支付原始请求表数据结束");
            if (planAmt.compareTo(parentOrderPo.getOrderCallFee().subtract(parentOrderPo.getDiscountFee())) != 0) {// 订单金额和需要支付的金额不相等
                parentOrderOptDao.rollback();
                Log.info(logger, uuid, "订单金额和需要支付的金额不相等", "planAmt", planAmt, "orderCallFee", parentOrderPo.getOrderCallFee(), "discountFee", parentOrderPo.getDiscountFee());
                
                // 私享团
                if (OrderTypeEnum.BAOYOU_TUANGOU_ORDER.getC().equals(inputData.getOrderType())
                                && inputData.getGroupTeamID() != null
                                && !Long.valueOf(0).equals(inputData.getGroupTeamID())) {
                    outputBean = ResponseUtil.getBaseOutputBean(BaseOrderResultEnum.PRIVATE_TEAM_PRICE_NOT_SAME.getResultID(), null, inputData.getScope());
                } else {
                    outputBean = ResponseUtil.getBaseOutputBean(BaseOrderResultEnum.PRICE_NOT_SAME.getResultID(), null, inputData.getScope());
                }
                Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
                return outputBean;
            }
            
            if (OrderTypeEnum.JIELONG_GROUPBUY.getC().equals(inputData.getOrderType())) {// 接龙团购
                if (sendAmount != null
                                && sendAmount.compareTo(parentOrderPo.getOrderCallFee()) > 0) {
                    Log.info(logger, uuid, "接龙团购订单,没到起送金额", "orderFee", parentOrderPo.getOrderCallFee(), "sendAmount", sendAmount);
                    outputBean = ResponseUtil.getBaseOutputBean(BaseOrderResultEnum.GROUPBUY_IS_NULL.getResultID(), null, inputData.getScope());
                    Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
                    return outputBean;
                }
            }
            
            
            Log.info(logger, uuid, "判断是否楼宇活动");
            if("1".equals(inputData.getActivityType()) && inputData.getActivityId() != null){
                Log.info(logger, uuid, "楼宇活动下单成功作废资格");
                List<Object[]> buildingQualiChangeList = new ArrayList<Object[]>();
                buildingQualiChangeList.add(new Object[]{"IS_USED", BooleanEnum.TRUE.getV()});
                List<Object[]> buildingQualiWhereList = new ArrayList<Object[]>();
                buildingQualiWhereList.add(new Object[]{"ACTIVITY_ID", "=", inputData.getActivityId()});
                buildingQualiWhereList.add(new Object[]{"BUYER_ID", "=", params.get(AuthorizeClient.COOKIE_BUYER_ID)});
                buildingQualiWhereList.add(new Object[]{"IS_USED", "=", BooleanEnum.FALSE.getV()});
                num = buildingQualificationDao.updateBuildingQualificationPOByCondition(buildingQualiChangeList, buildingQualiWhereList);
                if (num != 1) {
                    buildingQualificationDao.rollback();
                    Log.info(logger, uuid, "楼宇活动下单作废资格失败", "num", num);
                    outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.INSIDE_ERROR.getResultID(), null, inputData.getScope());
                    Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
                    return outputBean;
                }
            }
            
            Log.info(logger, uuid, "判断是否兑换券下单");
            if(inputData.getExList() != null && inputData.getExList().size() >= 1){
                for (OmsCreateOrderExListElementI exElementI : inputData.getExList()) {
                    Log.info(logger, uuid, "把兑换券置为使用状态，并记录订单号", "exSn", exElementI.getExSn());
                    List<Object[]> exChangeList = new ArrayList<Object[]>();
                    exChangeList.add(new Object[]{"STATUS", "3"});
                    exChangeList.add(new Object[]{"USED_BY_ID", params.get(AuthorizeClient.COOKIE_BUYER_ID)});
                    exChangeList.add(new Object[]{"USED_TIME", new Timestamp(new Date().getTime())});
                    exChangeList.add(new Object[]{"ORDER_CODE", orderCode});
                    List<Object[]> exWhereList = new ArrayList<Object[]>();
                    exWhereList.add(new Object[]{"BIND_BY_ID", "=", params.get(AuthorizeClient.COOKIE_BUYER_ID)});
                    exWhereList.add(new Object[]{"STATUS", "=", "2"});
                    exWhereList.add(new Object[]{"VOUCHER_SN", "=", exElementI.getExSn()});
                    num = tExVoucherDao.updateTExVoucherPOByCondition(exChangeList, exWhereList);
                    if (num != 1) {
                        tExVoucherDao.rollback();
                        Log.info(logger, uuid, "把兑换券置为使用状态失败", "num", num);
                        outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.INSIDE_ERROR.getResultID(), null, inputData.getScope());
                        Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
                        return outputBean;
                    }
                }
            }
            
            Log.info(logger, uuid, "插入支付原始请求表开始");
            num = payPlanDao.addOsPayPlanPO(payPlan);
            if (num != 1) {
                payPlanDao.rollback();
                Log.info(logger, uuid, "插入原始支付计划表失败", "num", num);
                outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.INSIDE_ERROR.getResultID(), null, inputData.getScope());
                Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
                return outputBean;
            }
            Log.info(logger, uuid, "插入支付原始请求表结束");
            
            
            try {
                if (groupWy) {// 特殊的2种团购(网易)
                    if (!SqlUtil.insertSpecialGroupBuy(logger, uuid, parentOrderDao.getWriteConnectionName(), params.get(AuthorizeClient.COOKIE_BUYER_ID), inputData.getGroupPromotionID())) {
                        parentOrderDao.rollback();
                        Log.info(logger, uuid, "网易特殊团购活动插入失败");
                        outputBean = ResponseUtil.getBaseOutputBean(170036, null, inputData.getScope());
                        Log.info(logger, uuid, "此用户已经购买了一个团购,接口返回", "outputBean", outputBean);
                        return outputBean;
                    }
                    
                    String sMobile = bsMobile.toString().trim();
                    if (!StringUtil.isEmpty(sMobile) && sMobile.length() >= 7) {
                        sMobile = sMobile.substring(0, 3) + "****"
                                        + sMobile.substring(7);
                    } else {
                        sMobile = sMobile + "****";
                    }
                    outputData.setPayInfo(params.get(AuthorizeClient.PARAM_BUYER_NAME).trim().substring(0, 2)
                                    + "***  "
                                    + sMobile
                                    + "  "
                                    + parentOrderPo.getGoodsCallFee()
                                    + "元抢到"
                                    + goodsString);
                    
                }
            } catch (Exception e) {
                parentOrderDao.rollback();
                Log.error(logger, uuid, "网易特殊团购活动插入失败", e);
                outputBean = ResponseUtil.getBaseOutputBean(170036, null, inputData.getScope());
                Log.info(logger, uuid, "此用户已经购买了一个团购,接口返回", "outputBean", outputBean);
                return outputBean;
            }
            
            if (OrderTypeEnum.JIELONG_GROUPBUY.getC().equals(inputData.getOrderType())) {// 接龙团购订单
                JSONObject jsonPayInfo = new JSONObject();
                jsonPayInfo.put("nickname", params.get(AuthorizeClient.PARAM_BUYER_NAME).trim());
                jsonPayInfo.put("goodsList", arrayGoods);
                outputData.setPayInfo(URLEncoder.encode(jsonPayInfo.toString(), "utf-8"));
            }
            
            // 私享团
            if (OrderTypeEnum.BAOYOU_TUANGOU_ORDER.getC().equals(inputData.getOrderType())
                            && inputData.getGroupTeamID() != null
                            && !Long.valueOf(0).equals(inputData.getGroupTeamID())) {
                if (ttsPrivateTeamJoinDao.updateTtsPrivateTeamJoinPO(ttsPrivateTeamJoinPO) == 0) {
                    ttsPrivateTeamJoinDao.rollback();
                    Log.info(logger, uuid, "修改ttsPrivateTeamJoinPO失败", "ttsPrivateTeamJoinPO", ttsPrivateTeamJoinPO);
                    outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.INSIDE_ERROR.getResultID(), null, inputData.getScope());
                    Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
                    return outputBean;
                }
                if (!SqlUtil.updatePrivateTeamOrderNum(logger, uuid, ttsPrivateTeamJoinDao.getWriteConnectionName(), ttsPrivateTeamJoinPO.getTeamId())) {
                    ttsPrivateTeamJoinDao.rollback();
                    Log.info(logger, uuid, "修改活动团TTS_PRIVATE_TEAM  下单量失败", "TeamId", ttsPrivateTeamJoinPO.getTeamId());
                    outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.INSIDE_ERROR.getResultID(), null, inputData.getScope());
                    Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
                    return outputBean;
                }
            }
            
            parentOrderDao.commit();
            outputData.setOrderID(parentOrderPo.getOrderId());
            outputData.setOrderCode(parentOrderPo.getOrderCode());
            
            
            
            Map<String, String> receiveMap = OrderUtil.getReceiveDt(true, parentOrderPo.getOrderStatus().intValue(), "", parentOrderPo.getOrderType(), parentOrderPo.getDeliveryType(), parentOrderPo.getSubstationId(), parentOrderPo.getTeamId(), parentOrderPo.getSpecifiedShippingdate(), parentOrderPo.getReceiveDt(), outputData.getReceiveDt(), logger, uuid, inputData.getScope());
            outputData.setIsShowReceiveDt(receiveMap.get("isShowReceiveDt"));
            outputData.setReceiveMsg(receiveMap.get("receiveMsg"));
            outputData.setReceiveDt(receiveMap.get("receiveDt"));
            outputData.setOrderType(parentOrderPo.getOrderType());
            
            if (canReserve) {// 有预定商品
                outputData.setIsPurchase(1L);
            } else {
                outputData.setIsPurchase(0L);
            }
            Map<Long, Long> payMethodPidMap = DictionaryData.getPayMethodPidMap(logger, uuid);
            BigDecimal onlinePayFee = BigDecimal.ZERO;
            BigDecimal offlinePayFee = BigDecimal.ZERO;
            BigDecimal couponFee = BigDecimal.ZERO;
            Long defaultPayMethodId = 0L;
            for (OmsCreateOrderPayListElementI payElement : inputData.getPayList()) {
                Long key = payMethodPidMap.get(payElement.getPayMethodID());
                if(key == 0L){
                    key = payElement.getPayMethodID();
                }
                if(PayMethodEnum.ONLINE.getC() == key.intValue()){
                    defaultPayMethodId = payElement.getPayMethodID();
                    onlinePayFee = onlinePayFee.add(payElement.getPayAmount());
                }else if(PayMethodEnum.OFFLINE.getC() == key.intValue()){
                    offlinePayFee = offlinePayFee.add(payElement.getPayAmount());
                }else if(PayMethodEnum.COUPON.getC() == key.intValue()){
                    couponFee = couponFee.add(payElement.getPayAmount());
                }
            }
            outputData.setOrderPayFee(planAmt.subtract(couponFee));
            outputData.setOnlinePayFee(onlinePayFee);
            outputData.setOfflinePayFee(offlinePayFee);
            
            if (onlinePayFee.compareTo(BigDecimal.ZERO) != 0) {
                outputData.setPayMethodId(defaultPayMethodId);
                if("21101".equals(inputData.getScope())){
                    Map<Long, String> payMethodTitleEnMap = DictionaryData.getPayMethodTitleEnMap(logger, uuid);
                    outputData.setPayMethodTile(payMethodTitleEnMap.get(defaultPayMethodId));
                }else{
                    Map<Long, String> payMethodTitleMap = DictionaryData.getPayMethodTitleMap(logger, uuid);
                    outputData.setPayMethodTile(payMethodTitleMap.get(defaultPayMethodId));
                }
            }
            
            if(offlinePayFee.compareTo(BigDecimal.ZERO) != 0){
                outputData.setShipAddr(parentOrderPo.getShipAddr());
            }

            outputData.setShipCallFee(parentOrderPo.getShipCallFee());
            outputData.setShipProvince(parentOrderPo.getShipProvince());
            outputData.setShipCity(parentOrderPo.getShipCity());
            outputData.setShipDistrict(parentOrderPo.getShipDistrict());
            
            
            Log.info(logger, uuid, "组装第三方推送订单明细信息开始");
            Map<Long, Long> salCatMap = new HashMap<Long, Long>();
            try {
                GoodsGetGoodsSaleCatsInputData getGoodsSaleCatsI = new GoodsGetGoodsSaleCatsInputData();
                getGoodsSaleCatsI.setScope(inputData.getScope());
                List<GoodsGetGoodsSaleCatsGoodsIDsElementI> goodsIdListI = new ArrayList<GoodsGetGoodsSaleCatsGoodsIDsElementI>();
                for (TOsParentOrderItemsPO tOsParentOrderItemsPO : parentOrderItemsPoList) {
                    GoodsGetGoodsSaleCatsGoodsIDsElementI goodsIdI = new GoodsGetGoodsSaleCatsGoodsIDsElementI();
                    goodsIdI.setGoodsID(tOsParentOrderItemsPO.getGoodsId());
                    goodsIdListI.add(goodsIdI);
                }
                getGoodsSaleCatsI.setGoodsIDs(goodsIdListI);
                Map<String, String> getGoodsSaleCatsParams = new HashMap<String, String>();
                getGoodsSaleCatsParams.put("method", "getGoodsSaleCats");
                getGoodsSaleCatsParams.put("req_str", getGoodsSaleCatsI.toJson());
                Log.info(logger, uuid, "组装goods服务getGoodsSaleCats方法所需参数结束");
                
                Log.info(logger, uuid, "调用goods服务getGoodsSaleCats方法开始", "getGoodsSaleCatsParams", getGoodsSaleCatsParams);
                outputBean = TootooService.callServer("goods", getGoodsSaleCatsParams, "post", new GoodsGetGoodsSaleCatsOutputData());
                Log.info(logger, uuid, "调用goods服务getGoodsSaleCats方法结束", "outputBean", outputBean);
                
                if (!TootooService.checkService(outputBean, "goods", "getGoodsSaleCats", inputData.getScope())) {
                    Log.info(logger, uuid, "调用goods服务getGoodsSaleCats方法失败,接口返回", "outputBean", outputBean);
                } else {
                    GoodsGetGoodsSaleCatsOutputData getGoodsSaleCatsOutputData = (GoodsGetGoodsSaleCatsOutputData)outputBean.getOutputData();
                    List<GoodsGetGoodsSaleCatsGoodsSaleCatInfosElementO> getGoodsSaleCatsListO = getGoodsSaleCatsOutputData.getGoodsSaleCatInfos();
                    if (getGoodsSaleCatsListO != null) {
                        for (GoodsGetGoodsSaleCatsGoodsSaleCatInfosElementO getGoodsSaleCatsO : getGoodsSaleCatsListO) {
                            if (getGoodsSaleCatsO.getSaleCatInfos() != null && getGoodsSaleCatsO.getSaleCatInfos().size() >= 1) {
                                salCatMap.put(getGoodsSaleCatsO.getGoodsID(), getGoodsSaleCatsO.getSaleCatInfos().get(0).getRootID());
                            }
                        }
                    }
                }
                Log.info(logger, uuid, "调用goods服务获取rootId结束", "salCatMap", salCatMap);
            } catch (Exception e) {
                Log.info(logger, uuid, "调用goods服务获取rootId异常", e);
            }
            List<OmsCreateOrderOrderItemListElementO> orderItemListO = new ArrayList<OmsCreateOrderOrderItemListElementO>();
            for (TOsParentOrderItemsPO tOsParentOrderItemsPO : parentOrderItemsPoList) {
                if(tOsParentOrderItemsPO.getGiftboxId() != 0){
                    continue;
                }
                OmsCreateOrderOrderItemListElementO orderItemO = new OmsCreateOrderOrderItemListElementO();
                orderItemO.setItemID(tOsParentOrderItemsPO.getItemId());
                orderItemO.setIsGift(tOsParentOrderItemsPO.getIsGift() + "");
                orderItemO.setIsGiftBox(tOsParentOrderItemsPO.getIsGiftbox());
                orderItemO.setGoodsID(tOsParentOrderItemsPO.getGoodsId());
                orderItemO.setGoodsAmount(tOsParentOrderItemsPO.getGoodsAmount());
                orderItemO.setGoodsNumber(tOsParentOrderItemsPO.getGoodsNumber().longValue());
                orderItemO.setGoodsPrice(tOsParentOrderItemsPO.getGoodsPrice());
                orderItemO.setSkuID(tOsParentOrderItemsPO.getSku());
                if (tOsParentOrderItemsPO.getIsGift() != 1) {
                    orderItemO.setMainCat(salCatMap.get(tOsParentOrderItemsPO.getGoodsId()));
                }
                if("21101".equals(inputData.getScope())){
                    orderItemO.setGoodsName(tOsParentOrderItemsPO.getGoodsNameEn());
                }else{
                    orderItemO.setGoodsName(tOsParentOrderItemsPO.getGoodsName());
                }
                orderItemO.setCatId(tOsParentOrderItemsPO.getCatId());
                orderItemListO.add(orderItemO);
            }
            outputData.setOrderItemList(orderItemListO);
            Log.info(logger, uuid, "组装第三方推送订单明细信息结束");
            
            
            if (OrderTypeEnum.BAOYOU_TUANGOU_ORDER.getC().equals(inputData.getOrderType())){
                
                TasteDetailDao tOsTasteDetailDao = new TasteDetailDao(uuid, logger);
                List<Object[]> tasteConditions = new ArrayList<Object[]>();
                tasteConditions.add(new Object[]{"PROMOTION_ID", "=", inputData.getGroupPromotionID()});
                Log.info(logger, uuid, "促销id", "PROMOTION_ID", inputData.getGroupPromotionID());
                List<TasteDetailPO> tasteDetailList = tOsTasteDetailDao.findTasteDetailPOListByCondition(tasteConditions, false, false, true);
                Log.info(logger, uuid, "试吃活动明细", "tasteDetailList", tasteDetailList);
                
                if (tasteDetailList != null && tasteDetailList.size() >= 1) {
                    Long activityId = tasteDetailList.get(0).getActivityId();
                    Log.info(logger, uuid, "试吃活动把购买信息放入缓存开始");
                    String key = "tasteOrderShopping-" + activityId;
                    Object tasteOrderCache = Memcached.get(key);
                    Log.info(logger, uuid, "从缓存中取出数据", "tasteOrderCache", tasteOrderCache);
                    List<Map<String, String>> tasteOrderList = new ArrayList<Map<String, String>>();
                    if (tasteOrderCache != null) {
                        tasteOrderList = (List<Map<String, String>>)tasteOrderCache;
                    }
                    for (TOsParentOrderItemsPO tOsParentOrderItemsPO : parentOrderItemsPoList) {
                        if (tOsParentOrderItemsPO.getGiftboxId() == 0){
                            Map<String, String> tasteOrderMap = new HashMap<String, String>();
                            tasteOrderMap.put("buyerId", params.get(AuthorizeClient.COOKIE_BUYER_ID));
                            tasteOrderMap.put("buyerEmail", params.get(AuthorizeClient.PARAM_BUYER_NAME));
                            tasteOrderMap.put("nickName", params.get(AuthorizeClient.COOKIE_NICK_NAME));
                            tasteOrderMap.put("createDt", DateUtil.dateTimeToStr(parentOrderPo.getCreateDt()));
                            tasteOrderMap.put("goodsId", tOsParentOrderItemsPO.getGoodsId() + "");
                            tasteOrderMap.put("goodsNumber", tOsParentOrderItemsPO.getGoodsNumber() + "");
                            tasteOrderMap.put("unitName", tOsParentOrderItemsPO.getUnitName());
                            tasteOrderMap.put("goodsName", tOsParentOrderItemsPO.getGoodsName());
                            tasteOrderList.add(0, tasteOrderMap);
                        }
                    }
                    boolean flag = Memcached.set(key, tasteOrderList);
                    Log.info(logger, uuid, "试吃活动缓存购买信息", "flag", flag, "tasteOrderList", tasteOrderList);
                }
                
                
                // 沱沱惠团购商品限购  by 20150824
                if ("67025".equals(inputData.getGroupPromotionID() + "") ) {
                    Log.info(logger, uuid, "指定沱沱惠促销ID的每个用户只能下一次，下单成功后放入缓存", "promotionId", inputData.getGroupPromotionID());
                    String key = "oncePromotion-" + inputData.getGroupPromotionID() + "-" + params.get(AuthorizeClient.COOKIE_BUYER_ID);
                    Object buyTimeCache = Memcached.get(key);
                    Log.info(logger, uuid, "从缓存中取出数据", "buyTimeCache", buyTimeCache);
                    int buyTime = 1;
                    if (buyTimeCache != null) {
                        buyTime = (Integer)buyTimeCache + 1;
                    }
                    boolean flag = Memcached.set(key, buyTime);
                    Log.info(logger, uuid, "指定沱沱惠促销ID缓存信息", "flag", flag, "buyTime", buyTime);
                }
                
            }
            
            
        } catch (Exception e) {
            parentOrderDao.rollback();
            Log.error(logger, "下单失败", e);
            outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.INSIDE_ERROR.getResultID(), null, inputData.getScope());
            Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
            return outputBean;
        }
        /******************************************************************************* 数据库操作结束 ********************************************************************************************/
        
        /*********************************************************************** 调用地址服务开始(修改为默认地址) **********************************************************************************/
        try {
            // 前台选择了设置默认地址
            if (BooleanEnum.TRUE.getV().equals(inputData.getIsSetDefault())) {
                
                Log.info(logger, uuid, "组装address服务updateDefaultAddress方法所需参数开始");
                AddressUpdateDefaultAddressInputData addressUpdateDefaultInputData = new AddressUpdateDefaultAddressInputData();
                addressUpdateDefaultInputData.setScope(inputData.getScope());
                addressUpdateDefaultInputData.setShipAddrID(inputData.getReceiveAddrID());
                if (haveOnlinePay || BooleanEnum.TRUE.getV().equals(isCheckCOD)) {// 选择了线上或者线下
                    addressUpdateDefaultInputData.setPayID(payMethodId);
                }
                addressUpdateDefaultInputData.setNeedReturn(BooleanEnum.FALSE.getV());
                HashMap<String, String> addressUpdateDefaultServiceParams = (HashMap<String, String>)params.clone();
                addressUpdateDefaultServiceParams.put("method", "updateDefaultAddress");
                addressUpdateDefaultServiceParams.put("req_str", addressUpdateDefaultInputData.toJson());
                Log.info(logger, uuid, "组装address服务updateDefaultAddress方法所需参数结束");
                
                Log.info(logger, uuid, "调用address服务updateDefaultAddress方法开始", "addressUpdateDefaultServiceParams", addressUpdateDefaultServiceParams);
                outputBean = TootooService.callServer("address", addressUpdateDefaultServiceParams, "post", new AddressUpdateDefaultAddressOutputData());
                Log.info(logger, uuid, "调用address服务updateDefaultAddress方法结束", "outputBean", outputBean);
                
                if (!TootooService.checkService(outputBean, "address", "updateDefaultAddress", inputData.getScope())) {
                    Log.info(logger, uuid, "调用address服务updateDefaultAddress方法失败,接口返回", "outputBean", outputBean);
                }
                // 如果调用失败,不影响下单
            }
        } catch (Exception e) {
            Log.error(logger, "修改为默认地址失败", e);
        }
        /*********************************************************************** 调用地址服务结束(修改为默认地址) **********************************************************************************/
                
        /***********订单出参增加促销来源字段 by zhaochunna 2015-7-17*************/
        outputData.setPromotionFrom(inputData.getPromotionFrom());
        
        outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.SUCCESS.getResultID(), outputData, inputData.getScope());
        Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
        return outputBean;
    }
    
    /**
     * 根据传入的商品列表、N元Y件列表、赠品列表，获得商品的map,goodCount<Long,buyNumber-giftNumber>
     * goodCount中NY的主键为所有goodsID的最大值加上promotionID
     * Description:<br>
     * 
     * @param inputData
     * @param redemptionList(换购的列表)
     * @param goodsMax(所有goodsID的最大值)
     * @param allGoods(所有商品的ID,包括NY内部的goodsID,但不包括礼盒商品内部的goodsID)
     * @return
     */
    public Map<Long, GoodsNumberBean> getGoodsCount(Map<Long, Long> buyMap, OmsCreateOrderInputData inputData, List<Long> redemptionList, StringBuffer goodsIDMax, Set<Long> allGoods, Map<Long, Map<Long, Long>> giftMap, Map<String, String> buyFromMap) throws Exception {
        
        Map<Long, GoodsNumberBean> goodsCount = new HashMap<Long, GoodsNumberBean>();// 保存所有商品的ID和购买数量(包括换购)
        // 判断订单类型
        if (OrderTypeEnum.GIFT_CARD_ORDER.getC().equals(inputData.getOrderType())
                        || OrderTypeEnum.ONLINE_CARD_VIRTUAL.getC().equals(inputData.getOrderType())) {// 卡订单
            for (OmsCreateOrderGiftCardGoodsListElementI giftCardElement : inputData.getGiftCardGoodsList()) {
                if (goodsCount.containsKey(giftCardElement.getGoodsID())) {
                    goodsCount.get(giftCardElement.getGoodsID()).setBuyNumber(goodsCount.get(giftCardElement.getGoodsID()).getBuyNumber()
                                    + giftCardElement.getGoodsCount());
                } else {
                    goodsCount.put(giftCardElement.getGoodsID(), new GoodsNumberBean(giftCardElement.getGoodsCount(), 0L, 0L, "", null, null, "", new ArrayList<GiftItemsBean>()));
                }
                allGoods.add(giftCardElement.getGoodsID());
            }
        } else if (OrderTypeEnum.TIHUO_ORDER.getC().equals(inputData.getOrderType())
                        || OrderTypeEnum.VIRTUAL_TIHUO_ORDER.getC().equals(inputData.getOrderType())) {// 提货订单
            for (OmsCreateOrderTihuoGoodsListElementI tihuoElement : inputData.getTihuoGoodsList()) {
                if (goodsCount.containsKey(tihuoElement.getGoodsID())) {
                    goodsCount.get(tihuoElement.getGoodsID()).setBuyNumber(goodsCount.get(tihuoElement.getGoodsID()).getBuyNumber()
                                    + tihuoElement.getGoodsCount());
                } else {
                    goodsCount.put(tihuoElement.getGoodsID(), new GoodsNumberBean(tihuoElement.getGoodsCount(), 0L, 0L, "", null, null, "", new ArrayList<GiftItemsBean>()));
                }
                allGoods.add(tihuoElement.getGoodsID());
            }
        } else if (OrderTypeEnum.BAOYOU_TUANGOU_ORDER.getC().equals(inputData.getOrderType())
                        || OrderTypeEnum.JIELONG_GROUPBUY.getC().equals(inputData.getOrderType())) {// 团购订单
            for (OmsCreateOrderGroupBuyingGoodsListElementI groupBuyElement : inputData.getGroupBuyingGoodsList()) {
                if (goodsCount.containsKey(groupBuyElement.getGoodsID())) {
                    goodsCount.get(groupBuyElement.getGoodsID()).setBuyNumber(goodsCount.get(groupBuyElement.getGoodsID()).getBuyNumber()
                                    + groupBuyElement.getGoodsCount());
                } else {
                    goodsCount.put(groupBuyElement.getGoodsID(), new GoodsNumberBean(groupBuyElement.getGoodsCount(), 0L, 0L, "", null, null, "", new ArrayList<GiftItemsBean>()));
                }
                allGoods.add(groupBuyElement.getGoodsID());
            }
        } else if (OrderTypeEnum.NORMAL.getC().equals(inputData.getOrderType())) {// 普通订单
            for (OmsCreateOrderGoodsListElementI good : inputData.getGoodsList()) {
                if (goodsCount.containsKey(good.getGoodsID())) {
                    goodsCount.get(good.getGoodsID()).setBuyNumber(goodsCount.get(good.getGoodsID()).getBuyNumber()
                                    + good.getGoodsCount());
                } else {
                    goodsCount.put(good.getGoodsID(), new GoodsNumberBean(good.getGoodsCount(), 0L, 0L, "", null, null, "", new ArrayList<GiftItemsBean>()));
                }
                if ("2".equals(good.getCartMethod())) {// 换购
                    redemptionList.add(good.getGoodsID());
                } else {
                    if (buyMap.containsKey(good.getGoodsID())) {
                        buyMap.put(good.getGoodsID(), buyMap.get(good.getGoodsID())
                                        + good.getGoodsCount());
                    } else {
                        buyMap.put(good.getGoodsID(), good.getGoodsCount());
                    }
                }
                allGoods.add(good.getGoodsID());
                buyFromMap.put(good.getGoodsID() + "-" + good.getCartMethod(), good.getBuyFrom());
            }
            if (inputData.getGiftList() != null
                            && inputData.getGiftList().size() > 0) {
                for (OmsCreateOrderGiftListElementI giftGood : inputData.getGiftList()) {
                    for (OmsCreateOrderGiftGoodsIDElementI giftGoodElement : giftGood.getGiftGoodsID()) {
                        if (goodsCount.containsKey(giftGoodElement.getGoodsID())) {
                            goodsCount.get(giftGoodElement.getGoodsID()).setGiftNumber(goodsCount.get(giftGoodElement.getGoodsID()).getGiftNumber()
                                            + giftGoodElement.getCount());
                        } else {
                            goodsCount.put(giftGoodElement.getGoodsID(), new GoodsNumberBean(0L, giftGoodElement.getCount(), 0L, "", null, null, "", new ArrayList<GiftItemsBean>()));
                        }
                        // 保存赠品Map<goodsID,Map<promotionID, giftNum>>
                        if (giftMap.containsKey(giftGoodElement.getGoodsID())) {
                            if (giftMap.get(giftGoodElement.getGoodsID()).containsKey(giftGood.getPromotionID())) {
                                giftMap.get(giftGoodElement.getGoodsID()).put(giftGood.getPromotionID(), giftMap.get(giftGoodElement.getGoodsID()).get(giftGood.getPromotionID())
                                                + giftGoodElement.getCount());
                            } else {
                                giftMap.get(giftGoodElement.getGoodsID()).put(giftGood.getPromotionID(), giftGoodElement.getCount());
                            }
                        } else {
                            Map<Long, Long> promotionMap = new HashMap<Long, Long>();
                            promotionMap.put(giftGood.getPromotionID(), giftGoodElement.getCount());
                            giftMap.put(giftGoodElement.getGoodsID(), promotionMap);
                        }
                        allGoods.add(giftGoodElement.getGoodsID());
                    }
                }
            }
            // 所有goodsID向左移一位再求和 再拼上当前促销ID，做为本NY的虚拟礼盒的主键
            if (inputData.getNyList() != null
                            && inputData.getNyList().size() > 0) {
                for (OmsCreateOrderNyListElementI nyGood : inputData.getNyList()) {
                    Long goodsMax = 0L;
                    for (OmsCreateOrderNyGoodsListElementI nyGoodsElement : nyGood.getNyGoodsList()) {
                        goodsMax += (nyGoodsElement.getGoodsID() << 1L);
                    }
                    
                    if (StringUtil.isEmpty(goodsIDMax.toString())) {
                        goodsIDMax.append(nyGood.getPromotionID() + ":" + goodsMax);
                    } else {
                        goodsIDMax.append("," + nyGood.getPromotionID() + ":" + goodsMax);
                    }
                    
                    if (goodsCount.containsKey(goodsMax
                                    + nyGood.getPromotionID())) {
                        goodsCount.get(goodsMax + nyGood.getPromotionID()).setBuyNumber(goodsCount.get(goodsMax
                                        + nyGood.getPromotionID()).getBuyNumber()
                                        + nyGood.getAmount());
                    } else {
                        List<GiftItemsBean> nyList = new ArrayList<GiftItemsBean>();
                        for (OmsCreateOrderNyGoodsListElementI nyGoodsElement : nyGood.getNyGoodsList()) {
                            nyList.add(new GiftItemsBean(nyGoodsElement.getGoodsID(), 1L, BigDecimal.ZERO));
                        }
                        goodsCount.put(goodsMax + nyGood.getPromotionID(), new GoodsNumberBean(nyGood.getAmount(), 0L, 0L, "", null, null, "0", nyList));
                    }
                    for (OmsCreateOrderNyGoodsListElementI nyGoodsElement : nyGood.getNyGoodsList()) {
                        allGoods.add(nyGoodsElement.getGoodsID());
                    }
                }
            }
            if (inputData.getExList() != null && inputData.getExList().size() > 0) {
                for (OmsCreateOrderExListElementI exGood : inputData.getExList()) {
                    for (OmsCreateOrderExGoodsIDElementI exGoodElement : exGood.getExGoodsID()) {
                        if (goodsCount.containsKey(exGoodElement.getGoodsID())) {
                            goodsCount.get(exGoodElement.getGoodsID()).setExNumber(goodsCount.get(exGoodElement.getGoodsID()).getExNumber()
                                            + exGoodElement.getCount());
                            goodsCount.get(exGoodElement.getGoodsID()).setExSn(exGood.getExSn());
                        } else {
                            goodsCount.put(exGoodElement.getGoodsID(), new GoodsNumberBean(0L, 0L, exGoodElement.getCount(), exGood.getExSn(), null, null, "", new ArrayList<GiftItemsBean>()));
                        }
                        allGoods.add(exGoodElement.getGoodsID());
                    }
                }
            }
        }
        return goodsCount;
    }
    
    /**
     * 验证团购
     * Description:<br>
     * 
     * @param inputData
     * @param promotionByIdOutputData
     * @param outputBean
     * @param groupMap
     * @return
     */
    public boolean checkGroupBuy(OmsCreateOrderInputData inputData, PromotionGetPromotionByIdOutputData promotionByIdOutputData, BaseOutputBean outputBean, Map<Long, PromotionGetPromotionByIdDetailElementO> groupMap, Map<Long, Long> groupHisBuyMap) throws Exception {
        
        BaseOutputBean outputBeanInside = null;
        
        if (promotionByIdOutputData.getPromotionList() == null
                        || promotionByIdOutputData.getPromotionList().isEmpty()) {
            Log.info(logger, uuid, "promotion服务返回列表为空", "promotionByIdOutputData", promotionByIdOutputData);
            outputBeanInside = ResponseUtil.getBaseOutputBean(BaseOrderResultEnum.GETPROMOTIONID_IS_NULL.getResultID(), null, inputData.getScope());
            outputBean.setOutputHead(outputBeanInside.getOutputHead());
            outputBean.setOutputData(outputBeanInside.getOutputData());
            return false;
        }
        
        for (PromotionGetPromotionByIdPromotionListElementO groupElement : promotionByIdOutputData.getPromotionList()) {
            if (inputData.getGroupPromotionID().intValue() == groupElement.getPromotionId().intValue()) {
                for (PromotionGetPromotionByIdDetailElementO detailElement : groupElement.getDetail()) {
                    groupMap.put(detailElement.getGoodsId(), detailElement);
                }
            }
        }
        
        for (OmsCreateOrderGroupBuyingGoodsListElementI groupGoodElement : inputData.getGroupBuyingGoodsList()) {
            if (groupMap.get(groupGoodElement.getGoodsID()) == null
                            || groupMap.get(groupGoodElement.getGoodsID()).getLeftNum() <= 0
                            || groupMap.get(groupGoodElement.getGoodsID()).getMaxNumPerOrder() < groupGoodElement.getGoodsCount()
                            || groupMap.get(groupGoodElement.getGoodsID()).getGoodsNum() > groupGoodElement.getGoodsCount()) {
                Log.info(logger, uuid, "无此团购或者购买数量非法");
                outputBeanInside = ResponseUtil.getBaseOutputBean(BaseOrderResultEnum.GROUPBUY_IS_INVALID.getResultID(), null, inputData.getScope());
                outputBean.setOutputHead(outputBeanInside.getOutputHead());
                outputBean.setOutputData(outputBeanInside.getOutputData());
                return false;
            }
            if (groupMap.get(groupGoodElement.getGoodsID()).getMax_num_per_buyer() != null
                            && groupMap.get(groupGoodElement.getGoodsID()).getMax_num_per_buyer() > 0
                            && (groupHisBuyMap.get(groupGoodElement.getGoodsID()) + groupGoodElement.getGoodsCount()) > groupMap.get(groupGoodElement.getGoodsID()).getMax_num_per_buyer()){
                // (历史购买数 + 本次购买数) > 活动设置此商品最大购买量
                Log.info(logger, uuid, "超过活动设置此商品每人最大购买量", "goodsId", groupGoodElement.getGoodsID(), "历史购买数", groupHisBuyMap.get(groupGoodElement.getGoodsID()), "本次购买数", groupGoodElement.getGoodsCount(), "活动设置数", groupMap.get(groupGoodElement.getGoodsID()).getMax_num_per_buyer());
                outputBeanInside = ResponseUtil.getBaseOutputBean(BaseOrderResultEnum.GROUPBUY_IS_INVALID.getResultID(), null, inputData.getScope());
                outputBean.setOutputHead(outputBeanInside.getOutputHead());
                outputBean.setOutputData(outputBeanInside.getOutputData());
                return false;
            }
        }
        
        return true;
        
    }
    
    /**
     * 获得商品现价
     * Description:<br>
     * 
     * @param goodsInfo
     * @param goodsId
     * @param goodCount
     * @param params
     * @return
     */
    public BigDecimal getCurrentPrice(Map<Long, GoodsGetGoodsInfoGoodsInfoElementO> goodsInfo, Long goodsId, Long goodCount, Map<String, String> params) throws Exception {
        
        BigDecimal currentPrice = null;
        // 阶梯价>会员价>原价
        if (BooleanEnum.TRUE.getV().equals(goodsInfo.get(goodsId).getPriceInfo().getLadderPriceFlag())) {// 有阶梯价
            for (GoodsGetGoodsInfoStairPriceElementO stairPrice : goodsInfo.get(goodsId).getPriceInfo().getStairPrice()) {
                if (goodCount >= stairPrice.getStartNum().intValue()
                                && goodCount <= stairPrice.getEndNum().intValue()) {
                    currentPrice = stairPrice.getGoodsPrice();
                    break;
                }
            }
        }
        if (currentPrice == null
                        && "0".equals(goodsInfo.get(goodsId).getDisFlag())) {// 参与会员折扣
            for (GoodsGetGoodsInfoVipPriceElementO vipPrice : goodsInfo.get(goodsId).getPriceInfo().getVipPrice()) {
                if (!StringUtil.isEmpty(params.get(AuthorizeClient.COOKIE_BUYER_TYPE))) {
                    if (vipPrice.getBuyerLevelId().intValue() == Integer.parseInt(params.get(AuthorizeClient.COOKIE_BUYER_TYPE))) {// 根据会员等级取会员价格
                        currentPrice = vipPrice.getGoodsPrice();
                    }
                }
            }
        }
        if (currentPrice == null) {
            currentPrice = goodsInfo.get(goodsId).getSkuInfo().getTheOriginalPrice();
        }
        return currentPrice;
        
    }
    
    /**
     * 获得促销服务的inputData
     * Description:<br>
     * 
     * @param inputData
     * @param goodsInfo
     * @return
     */
    public PromotionGetPromotionInfoInputData getPromotionInputData(String scenario, OmsCreateOrderInputData inputData, Map<Long, GoodsGetGoodsInfoGoodsInfoElementO> goodsInfo, Map<String, String> params) throws Exception {
        
        PromotionGetPromotionInfoInputData promotionInputData = new PromotionGetPromotionInfoInputData();
        Log.info(logger, uuid, "组装促销商品列表开始");
        boolean promotionIsOK = false;
        
        // 防止相同商品分多次传入(多次传入调用促销有问题)
        Map<String, OmsCreateOrderGoodsListElementI> goodsMap = new HashMap<String, OmsCreateOrderGoodsListElementI>();
        for (OmsCreateOrderGoodsListElementI good : inputData.getGoodsList()) {
            String key = "0";
            if ("2".equals(good.getCartMethod())) {// 换购
                key = "2";
            }
            if (goodsMap.containsKey(good.getGoodsID() + "-" + key)) {
                goodsMap.get(good.getGoodsID() + "-" + key).setGoodsCount(goodsMap.get(good.getGoodsID()
                                + "-" + key).getGoodsCount()
                                + good.getGoodsCount());
            } else {
                goodsMap.put(good.getGoodsID() + "-" + key, good);
            }
        }
        
        for (Map.Entry<String, OmsCreateOrderGoodsListElementI> entry : goodsMap.entrySet()) {
            
            String[] array = entry.getKey().split("-");
            Log.info(logger, uuid, "购买商品ID和购买方式", "array", Arrays.toString(array));
            
            PromotionGetPromotionInfoGoodsListElementI promotionGoodElement = new PromotionGetPromotionInfoGoodsListElementI();
            promotionGoodElement.setGoodsId(Long.valueOf(array[0]));
            promotionGoodElement.setSkuId(goodsInfo.get(Long.valueOf(array[0])).getSkuID());
            promotionGoodElement.setChecked("1");
            promotionGoodElement.setSubstationId(goodsInfo.get(Long.valueOf(array[0])).getSubstationID());
            promotionGoodElement.setAmount(entry.getValue().getGoodsCount());
            
            if ((PromotionTypeEnum.SALE.getC().equals(goodsInfo.get(Long.valueOf(array[0])).getSkuInfo().getPromotionType()) || PromotionTypeEnum.HUAN_GOU.getC().equals(array[1]))
                            && PromotionScopeUtil.getPromotionScope(goodsInfo.get(Long.valueOf(array[0])).getSkuInfo().getPromotionScope().intValue()).contains(inputData.getScope())
                            && PromotionStatusEnum.GOING.getC().equals(goodsInfo.get(Long.valueOf(array[0])).getSkuInfo().getPromotionStatus())) {
                promotionGoodElement.setPrice(goodsInfo.get(Long.valueOf(array[0])).getSkuInfo().getPromotionPrice());
            } else {
                promotionGoodElement.setPrice(this.getCurrentPrice(goodsInfo, Long.valueOf(array[0]), entry.getValue().getGoodsCount(), params));
            }
            if (promotionGoodElement.getPrice() == null) {// 没有查询到商品价格
                promotionGoodElement.setPrice(goodsInfo.get(Long.valueOf(array[0])).getSkuInfo().getTheOriginalPrice());
            }
            promotionGoodElement.setFee(promotionGoodElement.getPrice().multiply(new BigDecimal(promotionGoodElement.getAmount())));
            promotionGoodElement.setCartMethod(array[1]);
            promotionIsOK = OrderUtil.checkPromotion(inputData.getScope(), goodsInfo.get(Long.valueOf(array[0])));
            promotionGoodElement.setPromotionType(promotionIsOK?goodsInfo.get(Long.valueOf(array[0])).getSkuInfo().getPromotionType():"0");
            promotionGoodElement.setPromotionId(promotionIsOK?goodsInfo.get(Long.valueOf(array[0])).getSkuInfo().getPromotionID():0L);
            for (GoodsGetGoodsInfoSaleCatsElementO saleCatInfo : goodsInfo.get(Long.valueOf(array[0])).getSaleCatInfo().getSaleCats()) {
                PromotionGetPromotionInfoGoodsListCateElementI goodCateElement = new PromotionGetPromotionInfoGoodsListCateElementI();
                goodCateElement.setCateId(saleCatInfo.getSaleCatID());
                promotionGoodElement.getGoodsListCate().add(goodCateElement);
            }
            promotionGoodElement.setBrandId(goodsInfo.get(Long.valueOf(array[0])).getBrandID());
            promotionInputData.getGoodsList().add(promotionGoodElement);
        }
        
        // for (OmsCreateOrderGoodsListElementI good : inputData.getGoodsList())
        // {
        // PromotionGetPromotionInfoGoodsListElementI promotionGoodElement = new
        // PromotionGetPromotionInfoGoodsListElementI();
        // promotionGoodElement.setGoodsId(good.getGoodsID());
        // promotionGoodElement.setSkuId(goodsInfo.get(good.getGoodsID()).getSkuID());
        // promotionGoodElement.setChecked("1");
        // promotionGoodElement.setSubstationId(goodsInfo.get(good.getGoodsID()).getSubstationID());
        // promotionGoodElement.setAmount(good.getGoodsCount());
        //
        // if(
        // (PromotionTypeEnum.SALE.getC().equals(goodsInfo.get(good.getGoodsID()).getSkuInfo().getPromotionType())
        // || PromotionTypeEnum.HUAN_GOU.getC().equals(good.getCartMethod())) &&
        // PromotionScopeUtil.getPromotionScope(goodsInfo.get(good.getGoodsID()).getSkuInfo().getPromotionScope().intValue()).contains(inputData.getScope())
        // &&
        // PromotionStatusEnum.GOING.getC().equals(goodsInfo.get(good.getGoodsID()).getSkuInfo().getPromotionStatus())){
        // promotionGoodElement.setPrice(goodsInfo.get(good.getGoodsID()).getSkuInfo().getPromotionPrice());
        // } else {
        // promotionGoodElement.setPrice(this.getCurrentPrice(goodsInfo,
        // good.getGoodsID(), good.getGoodsCount(), params));
        // }
        // if (promotionGoodElement.getPrice() == null) {// 没有查询到商品价格
        // promotionGoodElement.setPrice(goodsInfo.get(good.getGoodsID()).getSkuInfo().getTheOriginalPrice());
        // }
        // promotionGoodElement.setFee(promotionGoodElement.getPrice().multiply(new
        // BigDecimal(promotionGoodElement.getAmount())));
        // promotionGoodElement.setCartMethod(good.getCartMethod());
        // promotionIsOK = OrderUtil.checkPromotion(inputData.getScope(),
        // goodsInfo.get(good.getGoodsID()));
        // promotionGoodElement.setPromotionType(promotionIsOK?goodsInfo.get(good.getGoodsID()).getSkuInfo().getPromotionType():"0");
        // promotionGoodElement.setPromotionId(promotionIsOK?goodsInfo.get(good.getGoodsID()).getSkuInfo().getPromotionID():0L);
        // for (GoodsGetGoodsInfoSaleCatsElementO saleCatInfo :
        // goodsInfo.get(good.getGoodsID()).getSaleCatInfo().getSaleCats()) {
        // PromotionGetPromotionInfoGoodsListCateElementI goodCateElement = new
        // PromotionGetPromotionInfoGoodsListCateElementI();
        // goodCateElement.setCateId(saleCatInfo.getSaleCatID());
        // promotionGoodElement.getGoodsListCate().add(goodCateElement);
        // }
        // promotionGoodElement.setBrandId(goodsInfo.get(good.getGoodsID()).getBrandID());
        // promotionInputData.getGoodsList().add(promotionGoodElement);
        // }
        Log.info(logger, uuid, "组装促销商品列表结束");
        if (inputData.getNyList() != null && inputData.getNyList().size() > 0) {
            Log.info(logger, uuid, "组装NY列表开始");
            for (OmsCreateOrderNyListElementI nyGood : inputData.getNyList()) {
                PromotionGetPromotionInfoNyListElementI nyListElement = new PromotionGetPromotionInfoNyListElementI();
                nyListElement.setPromotionId(nyGood.getPromotionID());
                for (OmsCreateOrderNyGoodsListElementI nyGoodElement : nyGood.getNyGoodsList()) {
                    PromotionGetPromotionInfoNyListSkuIdListElementI nySKU = new PromotionGetPromotionInfoNyListSkuIdListElementI();
                    nySKU.setSkuId(goodsInfo.get(nyGoodElement.getGoodsID()).getSkuID());
                    nyListElement.getNyListSkuIdList().add(nySKU);
                }
                nyListElement.setChecked("1");
                nyListElement.setAmount(nyGood.getAmount());
                promotionInputData.getNyList().add(nyListElement);
            }
            Log.info(logger, uuid, "组装NY列表结束");
        } else {
            promotionInputData.getNyList();// 促销服务入参为List时不能为null，必须为空数组
        }
        promotionInputData.setScope(inputData.getScope());
        promotionInputData.setScenario(scenario);// 订单确认页
        if (BooleanEnum.FALSE.getV().equals(inputData.getIsNeedMJ())) {// 不需要满减
            PromotionGetPromotionInfoNotCareElementI notCareElementOrder = new PromotionGetPromotionInfoNotCareElementI();
            notCareElementOrder.setPromotionType(PromotionTypeEnum.ORDER_MAN_JIAN.getC());
            promotionInputData.getNotCare().add(notCareElementOrder);
            PromotionGetPromotionInfoNotCareElementI notCareElementBrand = new PromotionGetPromotionInfoNotCareElementI();
            notCareElementBrand.setPromotionType(PromotionTypeEnum.BRAND_MAN_JIAN.getC());
            promotionInputData.getNotCare().add(notCareElementBrand);
            PromotionGetPromotionInfoNotCareElementI notCareElementCat = new PromotionGetPromotionInfoNotCareElementI();
            notCareElementCat.setPromotionType(PromotionTypeEnum.CAT_MAN_JIAN.getC());
            promotionInputData.getNotCare().add(notCareElementCat);
            PromotionGetPromotionInfoNotCareElementI notCareElementGoods = new PromotionGetPromotionInfoNotCareElementI();
            notCareElementGoods.setPromotionType(PromotionTypeEnum.GOODS_MAN_JIAN.getC());
            promotionInputData.getNotCare().add(notCareElementGoods);
        }
        return promotionInputData;
    }
    
    /**
     * 获得订单的支付输入(普通订单和卡订单,团购订单)
     * Description:<br>
     * 
     * @param inputData
     * @param promotionOutputData
     * @param goodsCount
     * @param goodsInfo
     * @param groupbuyOrderFee
     * @return
     * @throws Exception
     */
    // public List<PayBean> getPayInfo(OmsCreateOrderInputData inputData,
    // PromotionGetPromotionInfoOutputData promotionOutputData, Map<Long,
    // GoodsNumberBean> goodsCount, Map<Long,
    // GoodsGetGoodsInfoGoodsInfoElementO> goodsInfo, Map<Long,
    // PromotionGetPromotionByIdDetailElementO> groupMap) throws Exception {
    //
    // List<PayBean> payList = new ArrayList<PayBean>();
    //
    // if (OrderTypeEnum.NORMAL.getC().equals(inputData.getOrderType())) {//
    // 普通订单
    // for (PromotionGetPromotionInfoGoodsListElementO goodsListElement :
    // promotionOutputData.getGoodsList()) {
    //
    // PayBean payBean = new PayBean();
    // payBean.setGoodsId(goodsListElement.getGoodsId());
    // payBean.setSkuId(goodsListElement.getSkuId());
    // payBean.setGoodsCallFee(goodsListElement.getFee());
    // payBean.setCatId(goodsInfo.get(goodsListElement.getGoodsId()).getCatID());
    // String saleCatIds = "";
    // if (goodsInfo.get(goodsListElement.getGoodsId()).getSaleCatInfo() != null
    // &&
    // goodsInfo.get(goodsListElement.getGoodsId()).getSaleCatInfo().getSaleCats()
    // != null
    // &&
    // !goodsInfo.get(goodsListElement.getGoodsId()).getSaleCatInfo().getSaleCats().isEmpty())
    // {
    // for (GoodsGetGoodsInfoSaleCatsElementO saleCatElement :
    // goodsInfo.get(goodsListElement.getGoodsId()).getSaleCatInfo().getSaleCats())
    // {
    // saleCatIds += saleCatElement.getSaleCatID() + ",";
    // }
    // }
    // payBean.setSaleCateIds(saleCatIds.endsWith(",")?StringUtil.removeLast(saleCatIds):saleCatIds);
    // payBean.setBrandId(goodsListElement.getBrandId());
    // payBean.setBenefitScope(goodsInfo.get(goodsListElement.getGoodsId()).getBenefitScope());
    // payList.add(payBean);
    //
    // }
    // if (promotionOutputData.getNyList() != null
    // && !promotionOutputData.getNyList().isEmpty()) {
    // for (PromotionGetPromotionInfoNyListElementO nyElement :
    // promotionOutputData.getNyList()) {
    //
    // PayBean payBean = new PayBean();
    // payBean.setGoodsId(virtualGift);
    // payBean.setSkuId(virtualGift);
    // payBean.setGoodsCallFee(nyElement.getN());
    // payBean.setCatId(goodsInfo.get(virtualGift).getCatID());
    // String saleCatIds = "";
    // if (goodsInfo.get(virtualGift).getSaleCatInfo() != null
    // && goodsInfo.get(virtualGift).getSaleCatInfo().getSaleCats() != null
    // && !goodsInfo.get(virtualGift).getSaleCatInfo().getSaleCats().isEmpty())
    // {
    // for (GoodsGetGoodsInfoSaleCatsElementO saleCatElement :
    // goodsInfo.get(virtualGift).getSaleCatInfo().getSaleCats()) {
    // saleCatIds += saleCatElement.getSaleCatID() + ",";
    // }
    // }
    // payBean.setSaleCateIds(saleCatIds.endsWith(",")?StringUtil.removeLast(saleCatIds):saleCatIds);
    // payBean.setBrandId(goodsInfo.get(virtualGift).getBrandID());
    // payBean.setBenefitScope(goodsInfo.get(virtualGift).getBenefitScope());
    // payList.add(payBean);
    //
    // }
    // }
    // } else if
    // (OrderTypeEnum.GIFT_CARD_ORDER.getC().equals(inputData.getOrderType()))
    // {// 卡订单
    //
    // for (Map.Entry<Long, GoodsNumberBean> entry : goodsCount.entrySet()) {
    // if (entry.getValue().getBuyNumber() > 0) {
    //
    // PayBean payBean = new PayBean();
    // payBean.setGoodsId(entry.getKey());
    // payBean.setSkuId(goodsInfo.get(entry.getKey()).getSkuID());
    // payBean.setGoodsCallFee(goodsInfo.get(entry.getKey()).getSkuInfo().getTheOriginalPrice().multiply(BigDecimal.valueOf(entry.getValue().getBuyNumber())));
    // payBean.setCatId(goodsInfo.get(entry.getKey()).getCatID());
    // String saleCatIds = "";
    // if (goodsInfo.get(entry.getKey()).getSaleCatInfo() != null
    // && goodsInfo.get(entry.getKey()).getSaleCatInfo().getSaleCats() != null
    // &&
    // !goodsInfo.get(entry.getKey()).getSaleCatInfo().getSaleCats().isEmpty())
    // {
    // for (GoodsGetGoodsInfoSaleCatsElementO saleCatElement :
    // goodsInfo.get(entry.getKey()).getSaleCatInfo().getSaleCats()) {
    // saleCatIds += saleCatElement.getSaleCatID() + ",";
    // }
    // }
    // payBean.setSaleCateIds(saleCatIds.endsWith(",")?StringUtil.removeLast(saleCatIds):saleCatIds);
    // payBean.setBrandId(goodsInfo.get(entry.getKey()).getBrandID());
    // payBean.setBenefitScope(goodsInfo.get(entry.getKey()).getBenefitScope());
    // payList.add(payBean);
    //
    // }
    // }
    //
    // } else if
    // (OrderTypeEnum.BAOYOU_TUANGOU_ORDER.getC().equals(inputData.getOrderType()))
    // {// 团购订单
    //
    // for (OmsCreateOrderGroupBuyingGoodsListElementI groupGoodElement :
    // inputData.getGroupBuyingGoodsList()) {
    // PayBean payBean = new PayBean();
    // payBean.setGoodsId(groupGoodElement.getGoodsID());
    // payBean.setSkuId(goodsInfo.get(groupGoodElement.getGoodsID()).getSkuID());
    // payBean.setGoodsCallFee(groupMap.get(groupGoodElement.getGoodsID()).getPromotionPrice());
    // payBean.setCatId(goodsInfo.get(groupGoodElement.getGoodsID()).getCatID());
    // String saleCatIds = "";
    // if (goodsInfo.get(groupGoodElement.getGoodsID()).getSaleCatInfo() != null
    // &&
    // goodsInfo.get(groupGoodElement.getGoodsID()).getSaleCatInfo().getSaleCats()
    // != null
    // &&
    // !goodsInfo.get(groupGoodElement.getGoodsID()).getSaleCatInfo().getSaleCats().isEmpty())
    // {
    // for (GoodsGetGoodsInfoSaleCatsElementO saleCatElement :
    // goodsInfo.get(groupGoodElement.getGoodsID()).getSaleCatInfo().getSaleCats())
    // {
    // saleCatIds += saleCatElement.getSaleCatID() + ",";
    // }
    // }
    // payBean.setSaleCateIds(saleCatIds.endsWith(",")?StringUtil.removeLast(saleCatIds):saleCatIds);
    // payBean.setBrandId(goodsInfo.get(groupGoodElement.getGoodsID()).getBrandID());
    // payBean.setBenefitScope(goodsInfo.get(groupGoodElement.getGoodsID()).getBenefitScope());
    // payList.add(payBean);
    // }
    //
    // }
    // return payList;
    //
    // }
    
    /**
     * 获得订单的订单金额(普通订单和卡订单)
     * Description:<br>
     * 
     * @param inputData
     * @param promotionOutputData
     * @param goodsCount
     * @param goodsInfo
     * @return
     * @throws Exception
     */
    public BigDecimal getOrderFee(OmsCreateOrderInputData inputData, PromotionGetPromotionInfoOutputData promotionOutputData, Map<Long, GoodsNumberBean> goodsCount, Map<Long, GoodsGetGoodsInfoGoodsInfoElementO> goodsInfo, Map<Long, PromotionGetPromotionByIdDetailElementO> groupMap) throws Exception {
        
        BigDecimal orderFee = BigDecimal.ZERO;
        
        if (OrderTypeEnum.NORMAL.getC().equals(inputData.getOrderType())) {// 普通订单
            for (PromotionGetPromotionInfoGoodsListElementO goodsListElement : promotionOutputData.getGoodsList()) {
                orderFee = orderFee.add(goodsListElement.getFee());
            }
            if (promotionOutputData.getNyList() != null
                            && !promotionOutputData.getNyList().isEmpty()) {
                for (PromotionGetPromotionInfoNyListElementO nyElement : promotionOutputData.getNyList()) {
                    orderFee = orderFee.add(nyElement.getN().multiply(BigDecimal.valueOf(nyElement.getAmount())));
                }
            }
        } else if (OrderTypeEnum.GIFT_CARD_ORDER.getC().equals(inputData.getOrderType())
                        || OrderTypeEnum.ONLINE_CARD_VIRTUAL.getC().equals(inputData.getOrderType())) {// 卡订单
            for (Map.Entry<Long, GoodsNumberBean> entry : goodsCount.entrySet()) {
                if (entry.getValue().getBuyNumber() > 0) {
                    orderFee = orderFee.add(goodsInfo.get(entry.getKey()).getSkuInfo().getTheOriginalPrice().multiply(BigDecimal.valueOf(entry.getValue().getBuyNumber())));
                }
            }
        } else if (OrderTypeEnum.BAOYOU_TUANGOU_ORDER.getC().equals(inputData.getOrderType())
                        || OrderTypeEnum.JIELONG_GROUPBUY.getC().equals(inputData.getOrderType())) {// 团购订单
            for (OmsCreateOrderGroupBuyingGoodsListElementI groupGoodElement : inputData.getGroupBuyingGoodsList()) {
                orderFee = orderFee.add(groupMap.get(groupGoodElement.getGoodsID()).getPromotionPrice().multiply(BigDecimal.valueOf(groupGoodElement.getGoodsCount())));
            }
        }
        return orderFee;
        
    }
    
    /**
     * 获得支付调用的inputData
     * Description:<br>
     * 
     * @param promotionOutputData
     * @param goodsInfo
     * @param goodsInfoSKU
     * @param inputData
     * @param params
     * @param province
     * @param phoneNumber
     * @return
     */
    // public Map<String, String> getPayInput(OmsCreateOrderInputData inputData,
    // HashMap<String, String> params, Long province, String mobileNumber,
    // List<PayBean> payBeanList) throws Exception {
    //
    // Map<String, String> payParams = (Map<String, String>)params.clone();
    // payParams.put("r", "TService/getAllPayInfo");
    // payParams.put("buyerId", params.get(AuthorizeClient.COOKIE_BUYER_ID));
    // payParams.put("scope", inputData.getScope());
    // payParams.put("subStationId", inputData.getSubstationID() + "");
    // payParams.put("orderType", inputData.getOrderType());
    // payParams.put("haveOrder",
    // params.get(AuthorizeClient.COOKIE_HAVE_ORDERS));
    //
    // boolean account = false;
    // boolean giftcard = false;
    // boolean coupon = false;
    // boolean online = false;
    // boolean offline = false;
    // String couponSN = "";
    // for (OmsCreateOrderPayListElementI payListElement :
    // inputData.getPayList()) {
    // if (payListElement.getPayMethodID().intValue() == 1) {// 账户余额
    // account = true;
    // }
    // if (payListElement.getPayMethodID().intValue() == 2) {// 礼品卡
    // giftcard = true;
    // }
    // if (payListElement.getPayMethodID().intValue() == 4) {// 优惠券
    // if (payListElement.getPayCode() != null
    // && payListElement.getPayCode().contains("__")) {
    // couponSN = payListElement.getPayCode().split("__")[0];
    // } else {
    // couponSN = payListElement.getPayCode();
    // }
    // coupon = true;
    // }
    // if (payListElement.getPayMethodID().intValue() == 5) {// 线上支付
    // online = true;
    // }
    // if (payListElement.getPayMethodID().intValue() == 6) {// 线下支付
    // offline = true;
    // }
    // }
    //
    // if (account) {
    // payParams.put("getAccount", BooleanEnum.TRUE.getV());
    // }
    //
    // if (giftcard) {
    // payParams.put("getGiftcard", BooleanEnum.TRUE.getV());
    // }
    // if (coupon) {
    // payParams.put("checkCard", BooleanEnum.TRUE.getV());
    // payParams.put("checkCardExt[card][0][type]", "3");
    // payParams.put("checkCardExt[card][0][sn]", couponSN);
    // payParams.put("checkCardExt[card][0][pwd]", "");
    // payParams.put("checkCardExt[province]", province + "");
    // payParams.put("checkCardExt[mobile]", mobileNumber);
    // payParams.put("checkCardExt[stationId]", inputData.getSubstationID()
    // + "");
    //
    // int i = 0;
    // for (PayBean payBean : payBeanList) {
    // payParams.put("checkCardExt[goodsInfo][" + i + "][goodsId]",
    // payBean.getGoodsId()
    // + "");
    // payParams.put("checkCardExt[goodsInfo][" + i + "][skuId]",
    // payBean.getSkuId()
    // + "");
    // payParams.put("checkCardExt[goodsInfo][" + i
    // + "][goodsCallFee]", payBean.getGoodsCallFee()
    // + "");
    // payParams.put("checkCardExt[goodsInfo][" + i + "][catId]",
    // payBean.getCatId()
    // + "");
    // payParams.put("checkCardExt[goodsInfo][" + i + "][saleCateIds]",
    // payBean.getSaleCateIds());
    // payParams.put("checkCardExt[goodsInfo][" + i + "][bandId]",
    // payBean.getBrandId()
    // + "");
    // payParams.put("checkCardExt[goodsInfo][" + i
    // + "][benefitScope]", payBean.getBenefitScope());
    // i++;
    // }
    // }
    // if (online) {
    // payParams.put("getOnline", BooleanEnum.TRUE.getV());
    // }
    // if (offline) {
    // payParams.put("getOffline", BooleanEnum.TRUE.getV());
    // }
    // return payParams;
    // }
    
    /**
     * 验证支付
     * Description:<br>
     * 
     * @param inputData
     * @param payJson
     * @param outputBean
     * @return
     */
    // public boolean checkPay(OmsCreateOrderInputData inputData, JSONObject
    // payJson, BaseOutputBean outputBean, StringBuffer couponPayInfo) throws
    // Exception {
    //
    // boolean account = false;
    // boolean giftcard = false;
    // boolean coupon = false;
    // boolean online = false;
    // boolean offline = false;
    // Map<Long, OmsCreateOrderPayListElementI> payMethodMap = new HashMap<Long,
    // OmsCreateOrderPayListElementI>();
    // for (OmsCreateOrderPayListElementI payListElement :
    // inputData.getPayList()) {
    // payMethodMap.put(payListElement.getPayMethodID(), payListElement);
    //
    // if (payListElement.getPayMethodID().intValue() == 1) {// 账户余额
    // account = true;
    // }
    // if (payListElement.getPayMethodID().intValue() == 2) {// 礼品卡
    // giftcard = true;
    // }
    // if (payListElement.getPayMethodID().intValue() == 4) {// 优惠券
    // coupon = true;
    // }
    // if (payListElement.getPayMethodID().intValue() == 5) {// 线上支付
    // online = true;
    // }
    // if (payListElement.getPayMethodID().intValue() == 6) {// 线下支付
    // offline = true;
    // }
    // }
    //
    // BaseOutputBean outputBeanInside = null;
    //
    // if (account) {// 有账户余额支付
    // if
    // (payJson.getJSONObject("result").getJSONObject("accountInfo").getInt("err_code")
    // != 0) {
    // Log.info(logger, uuid, "支付服务中账户余额信息有误", "err_code",
    // payJson.getJSONObject("result").getJSONObject("accountInfo").getInt("err_code"));
    // outputBeanInside =
    // ResponseUtil.getBaseOutputBean(BaseOrderResultEnum.ACCOUNT_ERROR.getResultID(),
    // null, inputData.getScope());
    // outputBean.setOutputHead(outputBeanInside.getOutputHead());
    // outputBean.setOutputData(outputBeanInside.getOutputData());
    // return false;
    // }
    // if
    // (BigDecimal.valueOf(payJson.getJSONObject("result").getJSONObject("accountInfo").getJSONObject("result").getDouble("amount")).compareTo(payMethodMap.get(Long.valueOf(PayMethodEnum.ACCOUNT.getC())).getPayAmount())
    // < 0) {
    // Log.info(logger, uuid, "账户余额不足", "amount",
    // payJson.getJSONObject("result").getJSONObject("accountInfo").getJSONObject("result").getDouble("amount"),
    // "payAmount",
    // payMethodMap.get(Long.valueOf(PayMethodEnum.ACCOUNT.getC())).getPayAmount());
    // outputBeanInside =
    // ResponseUtil.getBaseOutputBean(BaseOrderResultEnum.ACCOUNT_NOT_ENOUGH.getResultID(),
    // null, inputData.getScope());
    // outputBean.setOutputHead(outputBeanInside.getOutputHead());
    // outputBean.setOutputData(outputBeanInside.getOutputData());
    // return false;
    // }
    // }
    //
    // if (giftcard) {// 有礼品卡支付
    // if
    // (payJson.getJSONObject("result").getJSONObject("giftcardInfo").getInt("err_code")
    // != 0) {
    // Log.info(logger, uuid, "支付服务中礼品卡账户信息有误", "err_code",
    // payJson.getJSONObject("result").getJSONObject("giftcardInfo").getInt("err_code"));
    // outputBeanInside =
    // ResponseUtil.getBaseOutputBean(BaseOrderResultEnum.GIFTCARD_ERROR.getResultID(),
    // null, inputData.getScope());
    // outputBean.setOutputHead(outputBeanInside.getOutputHead());
    // outputBean.setOutputData(outputBeanInside.getOutputData());
    // return false;
    // }
    // if
    // (BigDecimal.valueOf(payJson.getJSONObject("result").getJSONObject("giftcardInfo").getJSONObject("result").getDouble("amount")).compareTo(payMethodMap.get(Long.valueOf(PayMethodEnum.GIFTCARD.getC())).getPayAmount())
    // < 0) {
    // Log.info(logger, uuid, "礼品卡账户余额不足", "amount",
    // payJson.getJSONObject("result").getJSONObject("giftcardInfo").getJSONObject("result").getDouble("amount"),
    // "payAmount",
    // payMethodMap.get(Long.valueOf(PayMethodEnum.GIFTCARD.getC())).getPayAmount());
    // outputBeanInside =
    // ResponseUtil.getBaseOutputBean(BaseOrderResultEnum.GIFTCARD_NOT_ENOUGH.getResultID(),
    // null, inputData.getScope());
    // outputBean.setOutputHead(outputBeanInside.getOutputHead());
    // outputBean.setOutputData(outputBeanInside.getOutputData());
    // return false;
    // }
    // }
    // if (coupon) {// 优惠券支付
    //
    // if
    // (payJson.getJSONObject("result").getJSONObject("checkCardInfo").getInt("err_code")
    // != 0) {
    // Log.info(logger, uuid, "优惠券信息有误", "err_code",
    // payJson.getJSONObject("result").getJSONObject("giftcardInfo").getInt("err_code"));
    // outputBeanInside =
    // ResponseUtil.getBaseOutputBean(BaseOrderResultEnum.COUPON_ERROR.getResultID(),
    // null, inputData.getScope());
    // outputBean.setOutputHead(outputBeanInside.getOutputHead());
    // outputBean.setOutputData(outputBeanInside.getOutputData());
    // return false;
    // } else {
    // JSONArray array =
    // payJson.getJSONObject("result").getJSONObject("checkCardInfo").getJSONArray("result");
    // if (array.length() != 1) {
    // Log.info(logger, uuid, "优惠券信息有误,优惠劵列表返回不为1");
    // outputBeanInside =
    // ResponseUtil.getBaseOutputBean(BaseOrderResultEnum.COUPON_ERROR.getResultID(),
    // null, inputData.getScope());
    // outputBean.setOutputHead(outputBeanInside.getOutputHead());
    // outputBean.setOutputData(outputBeanInside.getOutputData());
    // return false;
    // }
    // JSONObject json = (JSONObject)array.get(0);
    // if (json.getInt("err_code") != 0) {
    // Log.info(logger, uuid, "优惠券信息有误");
    // outputBeanInside =
    // ResponseUtil.getBaseOutputBean(BaseOrderResultEnum.COUPON_ERROR.getResultID(),
    // null, inputData.getScope());
    // outputBean.setOutputHead(outputBeanInside.getOutputHead());
    // outputBean.setOutputData(outputBeanInside.getOutputData());
    // return false;
    // }
    // array = json.getJSONObject("result").getJSONArray("usedGoodsIds");
    // for (int i = 0; i < array.length(); i++) {
    // JSONObject jo = (JSONObject)array.get(i);
    // couponPayInfo.append(jo.getInt("goodsId") + "_"
    // + jo.getString("amount") + "|");
    // }
    // }
    // }
    // if (online) {// 线上支付
    // if
    // (payJson.getJSONObject("result").getJSONObject("onlineInfo").getInt("err_code")
    // != 0) {
    // Log.info(logger, uuid, "线上支付信息有误", "err_code",
    // payJson.getJSONObject("result").getJSONObject("onlineInfo").getInt("err_code"));
    // outputBeanInside =
    // ResponseUtil.getBaseOutputBean(BaseOrderResultEnum.ONLINE_ERROR.getResultID(),
    // null, inputData.getScope());
    // outputBean.setOutputHead(outputBeanInside.getOutputHead());
    // outputBean.setOutputData(outputBeanInside.getOutputData());
    // return false;
    // }
    // JSONArray array =
    // payJson.getJSONObject("result").getJSONObject("onlineInfo").getJSONObject("result").getJSONArray("child");
    // Set<String> onlineSet = new HashSet<String>();
    // for (int i = 0; i < array.length(); i++) {
    // JSONObject jsonObject = array.getJSONObject(i);
    // onlineSet.add(jsonObject.getString("PAY_METHOD_ID"));
    // }
    // if
    // (!onlineSet.contains(payMethodMap.get(Long.valueOf(PayMethodEnum.ONLINE.getC())).getPayMethodID()
    // + "")) {
    // Log.info(logger, uuid, "不能使用此线上支付方式", "onlineSet", onlineSet,
    // "payMethodId",
    // payMethodMap.get(Long.valueOf(PayMethodEnum.ONLINE.getC())).getPayMethodID());
    // outputBeanInside =
    // ResponseUtil.getBaseOutputBean(BaseOrderResultEnum.ONLINE_NOT_CONTAINS.getResultID(),
    // null, inputData.getScope());
    // outputBean.setOutputHead(outputBeanInside.getOutputHead());
    // outputBean.setOutputData(outputBeanInside.getOutputData());
    // return false;
    //
    // }
    // }
    // if (offline) {// 线下支付
    // if
    // (payJson.getJSONObject("result").getJSONObject("offlineInfo").getInt("err_code")
    // != 0) {
    // Log.info(logger, uuid, "线下支付信息有误", "err_code",
    // payJson.getJSONObject("result").getJSONObject("offlineInfo").getInt("err_code"));
    // outputBeanInside =
    // ResponseUtil.getBaseOutputBean(BaseOrderResultEnum.OFFLINE_ERROR.getResultID(),
    // null, inputData.getScope());
    // outputBean.setOutputHead(outputBeanInside.getOutputHead());
    // outputBean.setOutputData(outputBeanInside.getOutputData());
    // return false;
    // }
    // JSONArray array =
    // payJson.getJSONObject("result").getJSONObject("offlineInfo").getJSONObject("result").getJSONArray("child");
    // Set<String> offlineSet = new HashSet<String>();
    // for (int i = 0; i < array.length(); i++) {
    // JSONObject jsonObject = array.getJSONObject(i);
    // offlineSet.add(jsonObject.getString("PAY_METHOD_ID"));
    // }
    // if
    // (!offlineSet.contains(payMethodMap.get(Long.valueOf(PayMethodEnum.OFFLINE.getC())).getPayMethodID()
    // + "")) {
    // Log.info(logger, uuid, "不能使用此线上支付方式", "offlineSet", offlineSet,
    // "payMethodId",
    // payMethodMap.get(Long.valueOf(PayMethodEnum.OFFLINE.getC())).getPayMethodID());
    // outputBeanInside =
    // ResponseUtil.getBaseOutputBean(BaseOrderResultEnum.OFFLINE_NOT_CONTAINS.getResultID(),
    // null, inputData.getScope());
    // outputBean.setOutputHead(outputBeanInside.getOutputHead());
    // outputBean.setOutputData(outputBeanInside.getOutputData());
    // return false;
    //
    // }
    // }
    //
    // return true;
    // }
    
    /**
     * 获得父订单
     * Description:<br>
     * 
     * @param addressOutputData
     * @param inputBean
     * @param inputData
     * @param goodsOutputData
     * @param goodsList
     * @param jsonPromotion
     * @return
     * @throws Exception
     */
    public TOsParentOrderPO getParentOrder(String shipProvince, String shipCity, String shipDistrict, String shipAddr, String zipCode, String receiver, String tel, String mobile, Map<Long, String> companyMap, Map<Long, String> payMethodEnMap, Map<Long, String> payMethodMap, Long orderId, String orderCode, String receiveDate, AddressGetAddressByIDOutputData addressOutputData, BaseInputBean inputBean, OmsCreateOrderInputData inputData, List<TOsParentOrderItemsPO> parentOrderItemsPoList, BigDecimal shipFee, BigDecimal shipTotalFee, BigDecimal deliveryTimeFee, PromotionGetPromotionInfoOutputData promotionOutputData, HashMap<String, String> params, boolean canReserve, String type, TtsPrivateTeamJoinPO ttsPrivateTeamJoinPO, Long canChgPay, String receiveDtMsg, boolean saleNumAdjust, String isFirst, String sellerType, Long sellerId) throws Exception {
        
        TOsParentOrderPO parentOrderPo = new TOsParentOrderPO();
        if ("21101".equals(inputData.getScope())) {// 英文站订单
            parentOrderPo.setIsEnOrder("1");
        } else {
            parentOrderPo.setIsEnOrder("0");
        }
        parentOrderPo.setOrderId(orderId);
        parentOrderPo.setOrderCode(orderCode);
        parentOrderPo.setBuyerId(Long.valueOf(params.get(AuthorizeClient.COOKIE_BUYER_ID)));
        parentOrderPo.setCreateDt(new Timestamp(System.currentTimeMillis()));
        parentOrderPo.setPayDt(null);
        parentOrderPo.setCreateIp(inputBean.getInputHead().getIp());
        parentOrderPo.setOrderStatus(Long.valueOf(ParentOrderStatusEnum.ORIGINAL.getStatus()));// 默认初始订单
        parentOrderPo.setSdcId(-1L);
        
        parentOrderPo.setShipDesc(null);
        if(!OrderTypeEnum.ONLINE_CARD_VIRTUAL.getC().equals(inputData.getOrderType())){

            if (addressOutputData == null) {
                parentOrderPo.setShipProvince(shipProvince);
                parentOrderPo.setShipCity(shipCity);
                parentOrderPo.setShipDistrict(shipDistrict);
                parentOrderPo.setShipAddr(shipAddr);
                parentOrderPo.setZipcode(zipCode);
                parentOrderPo.setReceiver(receiver);
                parentOrderPo.setTel(tel);
                parentOrderPo.setMobile(mobile);
            } else {
                parentOrderPo.setShipProvince(addressOutputData.getProvinceName());
                parentOrderPo.setShipCity(addressOutputData.getCityName());
                parentOrderPo.setShipDistrict(addressOutputData.getDistrictName());
                parentOrderPo.setShipAddr(addressOutputData.getAddrDetail());
                parentOrderPo.setZipcode(addressOutputData.getPostalCode());
                parentOrderPo.setReceiver(addressOutputData.getReceiver());
                parentOrderPo.setTel(addressOutputData.getPhoneNumber());
                parentOrderPo.setMobile(addressOutputData.getMobileNumber());
            }
        }
        parentOrderPo.setEmail(params.get(AuthorizeClient.PARAM_BUYER_NAME));
        parentOrderPo.setReceiveDt(DateUtil.strToTimestamp(receiveDate));
        parentOrderPo.setReceiveTimeSeg("00:00-24:00");
        parentOrderPo.setReceiveDtMsg(receiveDtMsg);
        BigDecimal originalPrice = BigDecimal.ZERO;
        BigDecimal currentPrice = BigDecimal.ZERO;
        Set<String> deliveryTypeSet = new HashSet<String>();
        for (TOsParentOrderItemsPO items : parentOrderItemsPoList) {
            if (items.getGiftboxId().intValue() == 0) {// 不是礼盒内部商品
                originalPrice = originalPrice.add(items.getShopPrice().multiply(items.getGoodsNumber()));
                currentPrice = currentPrice.add(items.getGoodsAmount());
                deliveryTypeSet.add(companyMap.get(items.getDcId()));
            }
        }
        parentOrderPo.setGoodsFee(originalPrice);
        parentOrderPo.setGoodsCallFee(currentPrice);
        parentOrderPo.setOnlinePayFee(BigDecimal.ZERO);
        parentOrderPo.setPromotionId(inputData.getGroupPromotionID() == null?Long.valueOf(0):inputData.getGroupPromotionID());
        parentOrderPo.setPayStatus(PayStatusEnum.NO.getC());
        parentOrderPo.setRemark(inputData.getRemark());
        // parentOrderPo.setVipflag((StringUtil.isEmpty(inputData.getVipFlag())
        // || inputData.getVipFlag().length() > 1)?"0":inputData.getVipFlag());
        parentOrderPo.setVipflag("2".equals(params.get(AuthorizeClient.COOKIE_BUYER_TYPE))?"1":"0");
        parentOrderPo.setPayingMethod("");
        parentOrderPo.setSystemRemark(null);
        parentOrderPo.setActName(inputData.getActName());
        parentOrderPo.setOfflinePayFee(BigDecimal.ZERO);
        Set<Long> parentPayMethodSet = new HashSet<Long>();
        for (OmsCreateOrderPayListElementI payElement : inputData.getPayList()) {
            if (payElement.getParentPayMethodID().intValue() == PayMethodEnum.OFFLINE.getC()) {// 货到付款
                if (parentOrderPo.getIsEnOrder().equals("1")) {// 英文订单
                    parentOrderPo.setPayingMethod("Pay on delivery-"
                                    + payMethodEnMap.get(payElement.getPayMethodID())
                                    + "(￥" + payElement.getPayAmount() + ")");
                } else {
                    parentOrderPo.setPayingMethod("货到付款-"
                                    + payMethodMap.get(payElement.getPayMethodID())
                                    + "(￥" + payElement.getPayAmount() + ")");
                }
                parentOrderPo.setOfflinePayFee(payElement.getPayAmount());
            }
            if (payElement.getParentPayMethodID() == 0) {// 本来就是顶级
                parentPayMethodSet.add(payElement.getPayMethodID());
            } else {
                parentPayMethodSet.add(payElement.getParentPayMethodID());
            }
        }
        String payType = "";
        if (parentPayMethodSet.contains(Long.valueOf(PayMethodEnum.OFFLINE.getC()))) {
            if (parentPayMethodSet.size() > 1) {
                payType = PayTypeEnum.PART.getC();
            } else {
                payType = PayTypeEnum.OFFLINE.getC();
            }
        } else {
            payType = PayTypeEnum.ONLINE.getC();
        }
        parentOrderPo.setPayType(payType);
        parentOrderPo.setOrderType(inputData.getOrderType());
        
        if (OrderTypeEnum.BAOYOU_TUANGOU_ORDER.getC().equals(inputData.getOrderType())
                        && inputData.getGroupTeamID() != null
                        && !Long.valueOf(0).equals(inputData.getGroupTeamID())) {
            parentOrderPo.setTeamId(ttsPrivateTeamJoinPO.getTeamId());
            parentOrderPo.setActivityId(ttsPrivateTeamJoinPO.getActivityId());
        }
        parentOrderPo.setCouponFee(BigDecimal.ZERO);
        parentOrderPo.setOrderFrom(inputData.getOrderFrom());
        parentOrderPo.setCreateByName(params.get(AuthorizeClient.PARAM_BUYER_NAME));
        parentOrderPo.setPromotionFrom(inputData.getPromotionFrom());
        BigDecimal discountFee = BigDecimal.ZERO;
        if (promotionOutputData != null
                        && !promotionOutputData.getDiscountList().isEmpty()) {
            for (PromotionGetPromotionInfoDiscountListElementO discountElement : promotionOutputData.getDiscountList()) {
                discountFee = discountFee.add(discountElement.getDiscount());
            }
        }
        parentOrderPo.setDiscountFee(discountFee);
        parentOrderPo.setAddrId(inputData.getReceiveAddrID());
        parentOrderPo.setSdcName("未指定");
        if (deliveryTypeSet.contains(DeliveryTypeEnum.TTCN.getC())) {
            parentOrderPo.setDeliveryType(DeliveryTypeEnum.TTCN.getC());
        } else {
            parentOrderPo.setDeliveryType(DeliveryTypeEnum.THIRD.getC());
        }

        //如果是电子卡默认坨坨自营
        if(OrderTypeEnum.ONLINE_CARD_VIRTUAL.getC().equals(inputData.getOrderType())){
            parentOrderPo.setDeliveryType(DeliveryTypeEnum.TTCN.getC());
        }

        // parentOrderPo.setIsFirst((StringUtil.isEmpty(params.get(AuthorizeClient.COOKIE_HAVE_ORDERS)) || params.get(AuthorizeClient.COOKIE_HAVE_ORDERS).equals("0"))?"1":"0");
        parentOrderPo.setIsFirst(StringUtil.isEmpty(isFirst)?"0":isFirst);
        if (canReserve) {// 有预定商品
            parentOrderPo.setOrderMarks(1L);
        } else {
            parentOrderPo.setOrderMarks(0L);
        }
        
        if(saleNumAdjust){
            parentOrderPo.setSalenumAdjust("1");
        }else{
            parentOrderPo.setSalenumAdjust("0");
        }
        
        
        parentOrderPo.setInsourceFrom(inputData.getInsourceFrom());
        parentOrderPo.setReceiptShowAmount(inputData.getReceiptShowAmount());
        parentOrderPo.setPayReceiveStatus("0");
        parentOrderPo.setPaidMethodIds(Long.valueOf(0));
        parentOrderPo.setLotCode(null);
        parentOrderPo.setSplitStatus(Long.valueOf(0));
        parentOrderPo.setOrderCount(Long.valueOf(0));
        parentOrderPo.setDeliveryTimeType(inputData.getDeliveryTimeType());
        
        if (TimeShipTypeEnum.TODAY.getC().equals(inputData.getDeliveryTimeType())) {// 当日达
            if (deliveryTimeFee == null) {
                parentOrderPo.setDeliveryTimeFee(BigDecimal.ZERO);
            } else {
                parentOrderPo.setDeliveryTimeFee(deliveryTimeFee);
            }
        } else {
            parentOrderPo.setDeliveryTimeFee(BigDecimal.ZERO);
        }
        parentOrderPo.setShipFee(shipTotalFee);
        if (type.equals("1")) {
            parentOrderPo.setShipCallFee(shipFee.add(deliveryTimeFee));
        } else {// 现代牧场特殊
            if (parentOrderPo.getGoodsCallFee().compareTo(SpecialInfos.specialShippingFeeMap.get(params.get(AuthorizeClient.PARAM_BUYER_NAME))) >= 0) {
                Log.info(logger, uuid, "是特殊用户,按照运费规则设置运费", "buyerId", parentOrderPo.getBuyerId(), "threshold", SpecialInfos.specialShippingFeeMap.get(params.get(AuthorizeClient.PARAM_BUYER_NAME)));
                parentOrderPo.setShipCallFee(BigDecimal.ZERO);
                parentOrderPo.setDeliveryTimeFee(BigDecimal.ZERO);
            }
        }
        parentOrderPo.setOrderFee(parentOrderPo.getGoodsFee().add(parentOrderPo.getShipFee()));
        parentOrderPo.setOrderCallFee(parentOrderPo.getGoodsCallFee().add(parentOrderPo.getShipCallFee()));
        parentOrderPo.setNeedPayFee(parentOrderPo.getOrderCallFee().subtract(parentOrderPo.getDiscountFee()));
        parentOrderPo.setSubstationId(inputData.getSubstationID());
        parentOrderPo.setScope(inputData.getScope());
        parentOrderPo.setSpecifiedShippingdate(StringUtil.isEmpty(inputData.getReceiveDate())?BooleanEnum.FALSE.getV():BooleanEnum.TRUE.getV());
        
        parentOrderPo.setActiveType(inputData.getActivityType());
        parentOrderPo.setActiveId(inputData.getActivityId());
        parentOrderPo.setChgPay(canChgPay == null?"1":String.valueOf(canChgPay));
        
        parentOrderPo.setOrderPtype(sellerType);
        parentOrderPo.setSellerId(sellerId);
        
        return parentOrderPo;
        
    }
    
    public void setCommonParentOrderItemsPo(String buyFrom, TOsParentOrderItemsPO parentOrderItemPo, Map<Long, GoodsGetGoodsInfoGoodsInfoElementO> goodsInfo, Long goodsId, String scope, Map<Long, GoodsNumberBean> goodsCount, Logger logger, String uuid) throws Exception {
        
        parentOrderItemPo.setSku(goodsInfo.get(goodsId).getSkuID());
        parentOrderItemPo.setGoodsId(goodsId);
        parentOrderItemPo.setGoodsEan(goodsInfo.get(goodsId).getGoodsEan());
        parentOrderItemPo.setGoodsSn(goodsInfo.get(goodsId).getGoodsSN());
        parentOrderItemPo.setGoodsAmount(parentOrderItemPo.getGoodsPrice().multiply(parentOrderItemPo.getGoodsNumber()));
        parentOrderItemPo.setGoodsType(goodsInfo.get(goodsId).getGoodsType());
        parentOrderItemPo.setBrandId(goodsInfo.get(goodsId).getBrandID());
        parentOrderItemPo.setBrandName(goodsInfo.get(goodsId).getBrandName());
        parentOrderItemPo.setCatId(goodsInfo.get(goodsId).getCatID());
        parentOrderItemPo.setCatName(goodsInfo.get(goodsId).getCatName());
        parentOrderItemPo.setGoodsUnit(goodsInfo.get(goodsId).getSkuInfo().getGoodsUnit());
        parentOrderItemPo.setGoodsDesc(goodsInfo.get(goodsId).getGoodsDesc().getGoodsDesc() == null?"":URLDecoder.decode(goodsInfo.get(goodsId).getGoodsDesc().getGoodsDesc(), "utf-8"));
        parentOrderItemPo.setGoodsBrief(goodsInfo.get(goodsId).getGoodsBrief());
        if (goodsInfo.get(goodsId) != null
                        && goodsInfo.get(goodsId).getPicInfo() != null
                        && goodsInfo.get(goodsId).getPicInfo().getPicPaths() != null
                        && !goodsInfo.get(goodsId).getPicInfo().getPicPaths().isEmpty()) {
            parentOrderItemPo.setPicPath(goodsInfo.get(goodsId).getPicInfo().getPicPaths().get(0).getPicPath());
        }
        
        Log.info(logger, uuid, "goodsCount", "goodsCount", goodsCount, "goodsId", goodsId);
        if (goodsCount.get(goodsId) != null
                        && "1".equals(goodsCount.get(goodsId).getGiftType())) {// 实物礼盒
            Log.info(logger, uuid, "实物礼盒", "goodsCount.get(goodsId).getGiftType()", goodsCount.get(goodsId).getGiftType());
            BigDecimal giftBoxUnitWeigt = BigDecimal.ZERO;
            for (GiftItemsBean giftBean : goodsCount.get(goodsId).getGiftItems()) {
                giftBoxUnitWeigt = giftBoxUnitWeigt.add((goodsInfo.get(giftBean.getGoodsId()).getSkuInfo().getUnitWeight() == null?BigDecimal.ZERO:goodsInfo.get(giftBean.getGoodsId()).getSkuInfo().getUnitWeight()).multiply(BigDecimal.valueOf(giftBean.getCount())));
            }
            parentOrderItemPo.setUnitWeight(giftBoxUnitWeigt);
        } else {
            parentOrderItemPo.setUnitWeight(goodsInfo.get(goodsId).getSkuInfo().getUnitWeight() == null?BigDecimal.ZERO:goodsInfo.get(goodsId).getSkuInfo().getUnitWeight());
        }
        
        parentOrderItemPo.setGoodsWeight((parentOrderItemPo.getUnitWeight() == null?BigDecimal.ZERO:parentOrderItemPo.getUnitWeight()).multiply(parentOrderItemPo.getGoodsNumber()));
        
        parentOrderItemPo.setUnitName(goodsInfo.get(goodsId).getSkuInfo().getMarketingUnit());
        if ("21101".equals(scope)) {// 英文站订单
            parentOrderItemPo.setGoodsNameEn(goodsInfo.get(goodsId).getGoodsTitle());
            parentOrderItemPo.setBrandNameEn(goodsInfo.get(goodsId).getBrandName());
            parentOrderItemPo.setGoodsUnitEn(goodsInfo.get(goodsId).getSkuInfo().getGoodsUnit());
            parentOrderItemPo.setUnitNameEn(goodsInfo.get(goodsId).getSkuInfo().getMarketingUnit());
            parentOrderItemPo.setGoodsName(goodsInfo.get(goodsId).getGoodsTitleCn());
            parentOrderItemPo.setBrandName(goodsInfo.get(goodsId).getBrandNameCn());
            parentOrderItemPo.setGoodsUnit(goodsInfo.get(goodsId).getSkuInfo().getGoodsUnitCn());
            parentOrderItemPo.setUnitName(goodsInfo.get(goodsId).getSkuInfo().getMarketingUnitCn());
        }
        parentOrderItemPo.setBuyFrom(buyFrom);
        parentOrderItemPo.setProduceDt(goodsInfo.get(goodsId).getSkuInfo().getProductDt() == null?null:DateUtil.strToTimestamp(goodsInfo.get(goodsId).getSkuInfo().getProductDt()));
        parentOrderItemPo.setReviewId(0L);
        parentOrderItemPo.setStatus("1");
        parentOrderItemPo.setBasicId(goodsInfo.get(goodsId).getBasicID());
    }
    
    /**
     * 获得父订单详情
     * Description:<br>
     * 
     * @param goodsInfos
     * @param inputData
     * @return
     * @throws Exception
     */
    public List<TOsParentOrderItemsPO> getParentItems(OmsCreateOrderInputData inputData, TOsParentOrderItemsDao parentOrderItemDao, String isIncludeSpecial, Map<Long, String> goodsShipCapacityMap, Map<String, BigDecimal> discountMap, Map<String, BigDecimal> otherDiscountMap, Map<String, String> buyFromMap, Long orderId, String orderType, String scope, String receiveDate, String goodsIDMax, Long groupPromotionID, Map<Long, GoodsGetGoodsInfoGoodsInfoElementO> goodsInfo, PromotionGetPromotionInfoOutputData promotionOutputData, HashMap<Long, GoodsNumberBean> goodsCount, Map<Long, GoodsWarehouseAndDcBean> goodsWarehouseAndDcBean, Map<String, String> params, Map<Long, PromotionGetPromotionByIdDetailElementO> groupMap, BigDecimal privateTeamStepPrice) throws Exception {
        
        List<TOsParentOrderItemsPO> items = new ArrayList<TOsParentOrderItemsPO>();
        
        Calendar calendarReceive = Calendar.getInstance();
        calendarReceive.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(receiveDate));
        
        StringBuffer specialGoods = new StringBuffer("");
        for (Map.Entry<String, String> entry : SpecialInfos.specialGoods.entrySet()) {
            specialGoods.append(entry.getValue());
            specialGoods.append(",");
        }
        
        if (OrderTypeEnum.GIFT_CARD_ORDER.getC().equals(orderType)
                        || OrderTypeEnum.ONLINE_CARD_VIRTUAL.getC().equals(orderType)
                        || OrderTypeEnum.BAOYOU_TUANGOU_ORDER.getC().equals(orderType)
                        || OrderTypeEnum.JIELONG_GROUPBUY.getC().equals(orderType)) {// 卡订单或者团购
            for (Map.Entry<Long, GoodsNumberBean> entry : goodsCount.entrySet()) {
                Long itemId = parentOrderItemDao.findSeqNextVal("SEQ_OS_PARENT_ORDER_ITEMS_PK");
                if (itemId.intValue() == -1) {
                    Log.info(logger, uuid, "获得父订单详情表主键失败", "itemId", itemId);
                    return null;
                }
                TOsParentOrderItemsPO parentOrderItemPo = new TOsParentOrderItemsPO();
                parentOrderItemPo.setItemId(itemId);
                parentOrderItemPo.setOrderId(orderId);
                parentOrderItemPo.setGoodsNumber(BigDecimal.valueOf(entry.getValue().getBuyNumber()));
                
                // 私享团
                if (OrderTypeEnum.BAOYOU_TUANGOU_ORDER.getC().equals(inputData.getOrderType())
                                && inputData.getGroupTeamID() != null
                                && !Long.valueOf(0).equals(inputData.getGroupTeamID())) {
                    parentOrderItemPo.setShopPrice(goodsInfo.get(entry.getKey()).getSkuInfo().getTheOriginalPrice());
                    parentOrderItemPo.setGoodsPrice(privateTeamStepPrice);
                    parentOrderItemPo.setPromoteId("0");
                } else if ("4".equals(inputData.getActivityType()) || OrderTypeEnum.JIELONG_GROUPBUY.getC().equals(orderType)){//JET
                    parentOrderItemPo.setShopPrice(goodsInfo.get(entry.getKey()).getSkuInfo().getTheOriginalPrice());
                    parentOrderItemPo.setGoodsPrice(groupMap.get(entry.getKey()).getPromotionPrice());
                    parentOrderItemPo.setPromoteId(groupPromotionID + "");
                } else if (OrderTypeEnum.BAOYOU_TUANGOU_ORDER.getC().equals(orderType)
                                || OrderTypeEnum.JIELONG_GROUPBUY.getC().equals(orderType)) {// 团购订单
                    parentOrderItemPo.setShopPrice(groupMap.get(entry.getKey()).getMarketPrice());
                    parentOrderItemPo.setGoodsPrice(groupMap.get(entry.getKey()).getPromotionPrice());
                    parentOrderItemPo.setPromoteId(groupPromotionID + "");
                } else {// 卡订单
                    parentOrderItemPo.setShopPrice(goodsInfo.get(entry.getKey()).getSkuInfo().getTheOriginalPrice());
                    if (PromotionTypeEnum.SALE.getC().equals(goodsInfo.get(entry.getKey()).getSkuInfo().getPromotionType())
                                    && PromotionScopeUtil.getPromotionScope(goodsInfo.get(entry.getKey()).getSkuInfo().getPromotionScope().intValue()).contains(scope)
                                    && PromotionStatusEnum.GOING.getC().equals(goodsInfo.get(entry.getKey()).getSkuInfo().getPromotionStatus())) {
                        parentOrderItemPo.setGoodsPrice(goodsInfo.get(entry.getKey()).getSkuInfo().getPromotionPrice());
                    } else {
                        parentOrderItemPo.setGoodsPrice(this.getCurrentPrice(goodsInfo, entry.getKey(), entry.getValue().getBuyNumber(), params));
                    }
                    parentOrderItemPo.setPromoteId("0");
                }
                parentOrderItemPo.setGoodsName(goodsInfo.get(entry.getKey()).getGoodsTitle());
                parentOrderItemPo.setIsGiftbox(BooleanEnum.FALSE.getV());
                parentOrderItemPo.setGiftboxId(Integer.valueOf(0));
                parentOrderItemPo.setIsGift((byte) 0);
                parentOrderItemPo.setGiftFrom(0L);
                
                if (!"0".equals(goodsInfo.get(entry.getKey()).getCanReserve())) {// 预定商品(收货日期+备货期)
                    Calendar calendarClone = (Calendar)calendarReceive.clone();
                    if (specialGoods.toString().indexOf(String.valueOf(entry.getKey())) >= 0) {
                        calendarClone.add(Calendar.DAY_OF_MONTH, -(Integer.parseInt(goodsInfo.get(entry.getKey()).getDaysPrepare()) + 1));
                        parentOrderItemPo.setPurchaseDate(new Timestamp(calendarClone.getTime().getTime()));
                    } else {
                        calendarClone.add(Calendar.DAY_OF_MONTH, -(Integer.parseInt(goodsInfo.get(entry.getKey()).getDaysPrepare())));
                        parentOrderItemPo.setPurchaseDate(new Timestamp(calendarClone.getTime().getTime()));
                    }
                } else {
                    parentOrderItemPo.setPurchaseDate(null);
                }
                parentOrderItemPo.setDiscountFee(BigDecimal.ZERO);
                parentOrderItemPo.setOtherDiscountFee(BigDecimal.ZERO);
                /****增加原始订单折扣数据 by zhaochunna  2015-7-17 ****/
                parentOrderItemPo.setOrigDiscountFee(BigDecimal.ZERO);
                parentOrderItemPo.setOrigOtherDiscountFee(BigDecimal.ZERO);

                //电子卡库房 = substationid
                if(OrderTypeEnum.ONLINE_CARD_VIRTUAL.getC().equals(inputData.getOrderType()) ){
                    parentOrderItemPo.setWarehouseId(inputData.getSubstationID());
                }else{
                    parentOrderItemPo.setWarehouseId(goodsWarehouseAndDcBean.get(entry.getKey()).getWarehouseID());
                }

                Log.info(logger, uuid, "商品库房", "WarehouseId",parentOrderItemPo.getWarehouseId());

                if (entry.getValue().getGiftItems() == null
                                || entry.getValue().getGiftItems().isEmpty()) {// 不是礼盒
                    if (goodsInfo.get(entry.getKey()).getWarmZoneID() == null) {// 温区为空
                        Log.info(logger, uuid, "商品温区为空", "goodsId", entry.getValue());
                        return null;
                    }
                }
                
                parentOrderItemPo.setWarmzoneId(goodsInfo.get(entry.getKey()).getWarmZoneID());

                Log.info(logger, uuid, "商品温区", "WarmzoneId", parentOrderItemPo.getWarmzoneId());

                //电子卡配送公司1坨坨自营
                if(OrderTypeEnum.ONLINE_CARD_VIRTUAL.getC().equals(inputData.getOrderType()) ){
                    parentOrderItemPo.setDcId(1L);

                }else{
                    parentOrderItemPo.setDcId(goodsWarehouseAndDcBean.get(entry.getKey()).getDC());
                }

                Log.info(logger, uuid, "商品配送公司", "DcId",parentOrderItemPo.getDcId());

                parentOrderItemPo.setShipCapacity(goodsShipCapacityMap.get(entry.getKey()));
                this.setCommonParentOrderItemsPo("", parentOrderItemPo, goodsInfo, entry.getKey(), scope, goodsCount, logger, uuid);
                items.add(parentOrderItemPo);
                
                if (entry.getValue().getGiftItems() != null
                                && !entry.getValue().getGiftItems().isEmpty()) {// 礼盒
                    parentOrderItemPo.setIsGiftbox(BooleanEnum.TRUE.getV());
                    parentOrderItemPo.setWarmzoneId(null);
                    int i = 1;
                    BigDecimal sum = BigDecimal.ZERO;
                    for (GiftItemsBean giftElement : entry.getValue().getGiftItems()) {
                        TOsParentOrderItemsPO parentOrderItemPoGift = new TOsParentOrderItemsPO();
                        parentOrderItemPoGift.setOrderId(orderId);
                        parentOrderItemPoGift.setGoodsNumber(BigDecimal.valueOf(giftElement.getCount()).multiply(parentOrderItemPo.getGoodsNumber()));
                        
//                        if (OrderTypeEnum.BAOYOU_TUANGOU_ORDER.getC().equals(orderType)
//                                        || OrderTypeEnum.JIELONG_GROUPBUY.getC().equals(orderType)) {// 团购订单
//                            parentOrderItemPoGift.setShopPrice(giftElement.getPrice());
//                        } else {
//                            parentOrderItemPoGift.setShopPrice(giftElement.getPrice());
//                        }
                        parentOrderItemPoGift.setShopPrice(giftElement.getPrice());
                        
                        // 现价
                        if (i == entry.getValue().getGiftItems().size()) {// 最后一个
                            parentOrderItemPoGift.setGoodsPrice((parentOrderItemPo.getGoodsAmount().subtract(sum)).divide(parentOrderItemPoGift.getGoodsNumber(), 2, BigDecimal.ROUND_HALF_UP));
                        } else {
                            parentOrderItemPoGift.setGoodsPrice(parentOrderItemPoGift.getShopPrice().multiply(parentOrderItemPo.getGoodsPrice()).divide(parentOrderItemPo.getShopPrice(), 2, BigDecimal.ROUND_HALF_UP));
                        }
                        parentOrderItemPoGift.setPromoteId("0");
                        parentOrderItemPoGift.setGoodsName(goodsInfo.get(giftElement.getGoodsId()).getGoodsTitle());
                        parentOrderItemPoGift.setIsGiftbox(BooleanEnum.FALSE.getV());// 不是礼盒
                        parentOrderItemPoGift.setGiftboxId(itemId.intValue());
                        parentOrderItemPoGift.setIsGift((byte)0);
                        parentOrderItemPoGift.setGiftFrom(0L);
                        
                        if (!"0".equals(goodsInfo.get(giftElement.getGoodsId()).getCanReserve())) {// 预定商品
                            Calendar calendarClone = (Calendar)calendarReceive.clone();
                            if (specialGoods.toString().indexOf(String.valueOf(giftElement.getGoodsId())) >= 0) {
                                calendarClone.add(Calendar.DAY_OF_MONTH, -(Integer.parseInt(goodsInfo.get(giftElement.getGoodsId()).getDaysPrepare()) + 1));
                                parentOrderItemPoGift.setPurchaseDate(new Timestamp(calendarClone.getTime().getTime()));
                            } else {
                                calendarClone.add(Calendar.DAY_OF_MONTH, -(Integer.parseInt(goodsInfo.get(giftElement.getGoodsId()).getDaysPrepare())));
                                parentOrderItemPoGift.setPurchaseDate(new Timestamp(calendarClone.getTime().getTime()));
                            }
                        } else {
                            parentOrderItemPoGift.setPurchaseDate(null);
                        }
                        
                        // parentOrderItemPoGift.setPurchaseDate(parentOrderItemPo.getPurchaseDate());
                        parentOrderItemPoGift.setDiscountFee(BigDecimal.ZERO);
                        parentOrderItemPoGift.setOtherDiscountFee(BigDecimal.ZERO);
                        /****增加原始订单折扣数据 by zhaochunna  2015-7-17 ****/
                        parentOrderItemPoGift.setOrigDiscountFee(BigDecimal.ZERO);
                        parentOrderItemPoGift.setOrigOtherDiscountFee(BigDecimal.ZERO);
                        parentOrderItemPoGift.setWarehouseId(parentOrderItemPo.getWarehouseId());
                        
                        if (goodsInfo.get(giftElement.getGoodsId()).getWarmZoneID() == null) {// 温区为空
                            Log.info(logger, uuid, "商品温区为空", "goodsId", giftElement.getGoodsId());
                            return null;
                        }
                        
                        parentOrderItemPoGift.setWarmzoneId(goodsInfo.get(giftElement.getGoodsId()).getWarmZoneID());
                        parentOrderItemPoGift.setDcId(parentOrderItemPo.getDcId());
                        parentOrderItemPoGift.setShipCapacity(parentOrderItemPo.getShipCapacity());
                        this.setCommonParentOrderItemsPo("", parentOrderItemPoGift, goodsInfo, giftElement.getGoodsId(), scope, goodsCount, logger, uuid);
                        items.add(parentOrderItemPoGift);
                        sum = sum.add(parentOrderItemPoGift.getGoodsAmount());
                        i++;
                    }
                }
            }
            return items;
        }
        
        
        if ("1".equals(inputData.getActivityType()) && inputData.getActivityId() != null && OrderTypeEnum.NORMAL.getC().equals(orderType)) {
            for (Map.Entry<Long, GoodsNumberBean> entry : goodsCount.entrySet()) {

                Long itemId = parentOrderItemDao.findSeqNextVal("SEQ_OS_PARENT_ORDER_ITEMS_PK");
                if (itemId.intValue() == -1) {
                    Log.info(logger, uuid, "获得父订单详情表主键失败", "orderId", orderId);
                    return null;
                }
                TOsParentOrderItemsPO parentOrderItemPo = new TOsParentOrderItemsPO();
                parentOrderItemPo.setItemId(itemId);
                parentOrderItemPo.setOrderId(orderId);
                parentOrderItemPo.setGoodsNumber(BigDecimal.valueOf(entry.getValue().getBuyNumber()));
                parentOrderItemPo.setShopPrice(goodsInfo.get(entry.getKey()).getSkuInfo().getTheOriginalPrice());
                
                
                BigDecimal goodsPrice;
                if ((PromotionTypeEnum.SALE.getC().equals(goodsInfo.get(entry.getKey()).getSkuInfo().getPromotionType()) || PromotionTypeEnum.HUAN_GOU.getC().equals(entry.getKey()))
                                && PromotionScopeUtil.getPromotionScope(goodsInfo.get(entry.getKey()).getSkuInfo().getPromotionScope().intValue()).contains(inputData.getScope())
                                && PromotionStatusEnum.GOING.getC().equals(goodsInfo.get(entry.getKey()).getSkuInfo().getPromotionStatus())) {
                    goodsPrice = (goodsInfo.get(entry.getKey()).getSkuInfo().getPromotionPrice());
                } else {
                    goodsPrice = (this.getCurrentPrice(goodsInfo, entry.getKey(), entry.getValue().getBuyNumber(), params));
                }
                if (goodsPrice == null) {// 没有查询到商品价格
                    goodsPrice = (goodsInfo.get(Long.valueOf(entry.getKey())).getSkuInfo().getTheOriginalPrice());
                }
                
                parentOrderItemPo.setGoodsPrice(goodsPrice);
                parentOrderItemPo.setPromoteId("0");
                
                // String goodsTitle = goodsInfo.get(goodsListElement.getGoodsId()).getGoodsTitle();
                String goodsTitle = goodsInfo.get(entry.getKey()).getGoodsTitle();
                parentOrderItemPo.setGoodsName(goodsTitle);
                parentOrderItemPo.setIsGiftbox(BooleanEnum.FALSE.getV());
                parentOrderItemPo.setGiftboxId(Integer.valueOf(0));
                parentOrderItemPo.setIsGift((byte)0);
                parentOrderItemPo.setGiftFrom(0L);
                // 如果是礼盒备货期是null
                if (goodsCount.get(entry.getKey()).getGiftItems() != null
                                && !goodsCount.get(entry.getKey()).getGiftItems().isEmpty()) {// 礼盒
                    parentOrderItemPo.setPurchaseDate(null);
                } else {
                    if (!"0".equals(goodsInfo.get(entry.getKey()).getCanReserve())) {// 预定商品
                        Calendar calendarClone = (Calendar)calendarReceive.clone();
                        if (specialGoods.toString().indexOf(String.valueOf(entry.getKey())) >= 0) {
                            calendarClone.add(Calendar.DAY_OF_MONTH, -(Integer.parseInt(goodsInfo.get(entry.getKey()).getDaysPrepare()) + 1));
                            parentOrderItemPo.setPurchaseDate(new Timestamp(calendarClone.getTime().getTime()));
                        } else {
                            calendarClone.add(Calendar.DAY_OF_MONTH, -(Integer.parseInt(goodsInfo.get(entry.getKey()).getDaysPrepare())));
                            parentOrderItemPo.setPurchaseDate(new Timestamp(calendarClone.getTime().getTime()));
                        }
                    } else {
                        parentOrderItemPo.setPurchaseDate(null);
                    }
                }
                
                
                parentOrderItemPo.setDiscountFee(discountMap.get(entry.getKey() + "") == null?BigDecimal.ZERO:discountMap.get(entry.getKey() + ""));
                parentOrderItemPo.setOtherDiscountFee(BigDecimal.ZERO);
                /****增加原始订单折扣数据 by zhaochunna  2015-7-17 ****/
                parentOrderItemPo.setOrigDiscountFee(discountMap.get(entry.getKey() + "") == null?BigDecimal.ZERO:discountMap.get(entry.getKey() + ""));
                parentOrderItemPo.setOrigOtherDiscountFee(BigDecimal.ZERO);
                
                parentOrderItemPo.setWarehouseId(goodsWarehouseAndDcBean.get(entry.getKey()).getWarehouseID());
                
                if (goodsCount.get(entry.getKey()).getGiftItems() == null
                                || goodsCount.get(entry.getKey()).getGiftItems().isEmpty()) {// 不是礼盒
                    if (goodsInfo.get(entry.getKey()).getWarmZoneID() == null) {
                        Log.info(logger, uuid, "商品温区为空", "goodsId", entry.getKey());
                        return null;
                    }
                }
                
                parentOrderItemPo.setWarmzoneId(goodsInfo.get(entry.getKey()).getWarmZoneID());
                parentOrderItemPo.setDcId(goodsWarehouseAndDcBean.get(entry.getKey()).getDC());
                parentOrderItemPo.setShipCapacity(goodsShipCapacityMap.get(entry.getKey()));
                String buyFrom = "";
                this.setCommonParentOrderItemsPo(buyFrom, parentOrderItemPo, goodsInfo, entry.getKey(), scope, goodsCount, logger, uuid);
                items.add(parentOrderItemPo);
                if (goodsCount.get(entry.getKey()).getGiftItems() != null
                                && !goodsCount.get(entry.getKey()).getGiftItems().isEmpty()) {// 礼盒
                    parentOrderItemPo.setIsGiftbox(BooleanEnum.TRUE.getV());
                    parentOrderItemPo.setWarmzoneId(null);
                    int i = 1;
                    BigDecimal sum = BigDecimal.ZERO;
                    BigDecimal sumDiscount = BigDecimal.ZERO;
                    BigDecimal sumOtherDiscount = BigDecimal.ZERO;
                    for (GiftItemsBean giftElement : goodsCount.get(entry.getKey()).getGiftItems()) {
                        
                        TOsParentOrderItemsPO parentOrderItemPoGift = new TOsParentOrderItemsPO();
                        parentOrderItemPoGift.setOrderId(orderId);
                        parentOrderItemPoGift.setGoodsNumber(BigDecimal.valueOf(giftElement.getCount()).multiply(BigDecimal.valueOf(entry.getValue().getBuyNumber())));
                        parentOrderItemPoGift.setShopPrice(giftElement.getPrice());
                        // 现价
                        if (i == goodsCount.get(entry.getKey()).getGiftItems().size()) {// 最后一个
                            parentOrderItemPoGift.setGoodsPrice((parentOrderItemPo.getGoodsAmount().subtract(sum)).divide(parentOrderItemPoGift.getGoodsNumber(), 2, BigDecimal.ROUND_HALF_UP));
                        } else {
                            parentOrderItemPoGift.setGoodsPrice(parentOrderItemPoGift.getShopPrice().multiply(parentOrderItemPo.getGoodsPrice()).divide(parentOrderItemPo.getShopPrice(), 2, BigDecimal.ROUND_HALF_UP));
                        }
                        parentOrderItemPoGift.setPromoteId("0");
                        parentOrderItemPoGift.setGoodsName(goodsInfo.get(giftElement.getGoodsId()).getGoodsTitle());
                        parentOrderItemPoGift.setIsGiftbox(BooleanEnum.FALSE.getV());// 不是礼盒
                        parentOrderItemPoGift.setGiftboxId(itemId.intValue());
                        parentOrderItemPoGift.setIsGift((byte)0);
                        parentOrderItemPoGift.setGiftFrom(0L);
                        
                        if (!"0".equals(goodsInfo.get(giftElement.getGoodsId()).getCanReserve())) {// 预定商品
                            Calendar calendarClone = (Calendar)calendarReceive.clone();
                            if (specialGoods.toString().indexOf(String.valueOf(giftElement.getGoodsId())) >= 0) {
                                calendarClone.add(Calendar.DAY_OF_MONTH, -(Integer.parseInt(goodsInfo.get(giftElement.getGoodsId()).getDaysPrepare()) + 1));
                                parentOrderItemPoGift.setPurchaseDate(new Timestamp(calendarClone.getTime().getTime()));
                            } else {
                                calendarClone.add(Calendar.DAY_OF_MONTH, -(Integer.parseInt(goodsInfo.get(giftElement.getGoodsId()).getDaysPrepare())));
                                parentOrderItemPoGift.setPurchaseDate(new Timestamp(calendarClone.getTime().getTime()));
                            }
                        } else {
                            parentOrderItemPoGift.setPurchaseDate(null);
                        }
                        
                        // parentOrderItemPoGift.setPurchaseDate(parentOrderItemPo.getPurchaseDate());
                        parentOrderItemPoGift.setWarehouseId(parentOrderItemPo.getWarehouseId());
                        
                        if (goodsInfo.get(giftElement.getGoodsId()).getWarmZoneID() == null) {
                            Log.info(logger, uuid, "商品温区为空", "goodsId", giftElement.getGoodsId());
                            return null;
                        }
                        
                        parentOrderItemPoGift.setWarmzoneId(goodsInfo.get(giftElement.getGoodsId()).getWarmZoneID());
                        parentOrderItemPoGift.setDcId(parentOrderItemPo.getDcId());
                        parentOrderItemPoGift.setShipCapacity(parentOrderItemPo.getShipCapacity());
                        this.setCommonParentOrderItemsPo("", parentOrderItemPoGift, goodsInfo, giftElement.getGoodsId(), scope, goodsCount, logger, uuid);
                        
                        if (i == goodsCount.get(entry.getKey()).getGiftItems().size()) {// 最后一个
                            parentOrderItemPoGift.setDiscountFee(parentOrderItemPo.getDiscountFee().subtract(sumDiscount));
                            // parentOrderItemPoGift.setOtherDiscountFee(parentOrderItemPo.getOtherDiscountFee().subtract(sumOtherDiscount));
                            /****增加原始订单折扣数据 by zhaochunna  2015-7-17 ****/
                            parentOrderItemPoGift.setOrigDiscountFee(parentOrderItemPo.getDiscountFee().subtract(sumDiscount));
                        } else {
                            parentOrderItemPoGift.setDiscountFee(parentOrderItemPoGift.getGoodsAmount().multiply(parentOrderItemPo.getDiscountFee()).divide(parentOrderItemPo.getGoodsAmount(), 2, BigDecimal.ROUND_HALF_UP));
                            // parentOrderItemPoGift.setOtherDiscountFee(parentOrderItemPoGift.getGoodsAmount().multiply(parentOrderItemPo.getOtherDiscountFee()).divide(parentOrderItemPo.getGoodsAmount(), 2, BigDecimal.ROUND_HALF_UP));
                            /****增加原始订单折扣数据 by zhaochunna  2015-7-17 ****/
                            parentOrderItemPoGift.setOrigDiscountFee(parentOrderItemPoGift.getGoodsAmount().multiply(parentOrderItemPo.getDiscountFee()).divide(parentOrderItemPo.getGoodsAmount(), 2, BigDecimal.ROUND_HALF_UP));
                        }
                        
                        parentOrderItemPoGift.setOtherDiscountFee(BigDecimal.ZERO);
                        /****增加原始订单折扣数据 by zhaochunna  2015-7-17 ****/
                        parentOrderItemPoGift.setOrigOtherDiscountFee(BigDecimal.ZERO);
                        items.add(parentOrderItemPoGift);
                        sum = sum.add(parentOrderItemPoGift.getGoodsAmount());
                        sumDiscount = sumDiscount.add(parentOrderItemPoGift.getDiscountFee());
                        // sumOtherDiscount = sumOtherDiscount.add(parentOrderItemPoGift.getOtherDiscountFee());
                        i++;
                    }
                }
                
            }
            
            return items;
            
            
        } else if (OrderTypeEnum.NORMAL.getC().equals(orderType)) {// 普通订单订单
        
            for (PromotionGetPromotionInfoGoodsListElementO goodsListElement : promotionOutputData.getGoodsList()) {
                Long itemId = parentOrderItemDao.findSeqNextVal("SEQ_OS_PARENT_ORDER_ITEMS_PK");
                if (itemId.intValue() == -1) {
                    Log.info(logger, uuid, "获得父订单详情表主键失败", "orderId", orderId);
                    return null;
                }
                TOsParentOrderItemsPO parentOrderItemPo = new TOsParentOrderItemsPO();
                parentOrderItemPo.setItemId(itemId);
                parentOrderItemPo.setOrderId(orderId);
                parentOrderItemPo.setGoodsNumber(BigDecimal.valueOf(goodsListElement.getAmount()));
                parentOrderItemPo.setShopPrice(goodsInfo.get(goodsListElement.getGoodsId()).getSkuInfo().getTheOriginalPrice());
                parentOrderItemPo.setGoodsPrice(goodsListElement.getPrice());
                parentOrderItemPo.setPromoteId(goodsListElement.getPromotionId() == null?"0":goodsListElement.getPromotionId()
                                + "");
                
                String goodsTitle = goodsInfo.get(goodsListElement.getGoodsId()).getGoodsTitle();
                if (PromotionTypeEnum.MN.getC().equals(goodsListElement.getPromotionType())) {// 是M件N折
                    goodsTitle = goodsListElement.getPrefix()
                                    + goodsInfo.get(goodsListElement.getGoodsId()).getGoodsTitle();
                }
                if ("2".equals(goodsListElement.getCartMethod())) {// 换购
                    goodsTitle = "【换购】"
                                    + goodsInfo.get(goodsListElement.getGoodsId()).getGoodsTitle();
                }
                parentOrderItemPo.setGoodsName(goodsTitle);
                parentOrderItemPo.setIsGiftbox(BooleanEnum.FALSE.getV());
                parentOrderItemPo.setGiftboxId(Integer.valueOf(0));
                parentOrderItemPo.setIsGift((byte)0);
                parentOrderItemPo.setGiftFrom(0L);
                // 如果是礼盒备货期是null
                if (goodsCount.get(goodsListElement.getGoodsId()).getGiftItems() != null
                                && !goodsCount.get(goodsListElement.getGoodsId()).getGiftItems().isEmpty()) {// 礼盒
                    parentOrderItemPo.setPurchaseDate(null);
                } else {
                    if (!"0".equals(goodsInfo.get(goodsListElement.getGoodsId()).getCanReserve())) {// 预定商品
                        Calendar calendarClone = (Calendar)calendarReceive.clone();
                        if (specialGoods.toString().indexOf(String.valueOf(goodsListElement.getGoodsId())) >= 0) {
                            calendarClone.add(Calendar.DAY_OF_MONTH, -(Integer.parseInt(goodsInfo.get(goodsListElement.getGoodsId()).getDaysPrepare()) + 1));
                            parentOrderItemPo.setPurchaseDate(new Timestamp(calendarClone.getTime().getTime()));
                        } else {
                            calendarClone.add(Calendar.DAY_OF_MONTH, -(Integer.parseInt(goodsInfo.get(goodsListElement.getGoodsId()).getDaysPrepare())));
                            parentOrderItemPo.setPurchaseDate(new Timestamp(calendarClone.getTime().getTime()));
                        }
                    } else {
                        parentOrderItemPo.setPurchaseDate(null);
                    }
                }
                
                if ("2".equals(goodsListElement.getCartMethod())) {// 换购
                    parentOrderItemPo.setDiscountFee(BigDecimal.ZERO);
                    parentOrderItemPo.setOtherDiscountFee(BigDecimal.ZERO);
                    /****增加原始订单折扣数据 by zhaochunna  2015-7-17 ****/
                    parentOrderItemPo.setOrigDiscountFee(BigDecimal.ZERO);
                    parentOrderItemPo.setOrigOtherDiscountFee(BigDecimal.ZERO);
                } else {
                    parentOrderItemPo.setDiscountFee(discountMap.get(goodsListElement.getGoodsId()
                                    + "-" + goodsListElement.getMnMaster()) == null?BigDecimal.ZERO:discountMap.get(goodsListElement.getGoodsId()
                                    + "-" + goodsListElement.getMnMaster()));
                    parentOrderItemPo.setOtherDiscountFee(otherDiscountMap.get(goodsListElement.getGoodsId()
                                    + "-" + goodsListElement.getMnMaster()) == null?BigDecimal.ZERO:otherDiscountMap.get(goodsListElement.getGoodsId()
                                    + "-" + goodsListElement.getMnMaster()));
                    /****增加原始订单折扣数据 by zhaochunna  2015-7-17 ****/
                    parentOrderItemPo.setOrigDiscountFee(discountMap.get(goodsListElement.getGoodsId()
                            + "-" + goodsListElement.getMnMaster()) == null?BigDecimal.ZERO:discountMap.get(goodsListElement.getGoodsId()
                            + "-" + goodsListElement.getMnMaster()));
                    parentOrderItemPo.setOrigOtherDiscountFee(otherDiscountMap.get(goodsListElement.getGoodsId()
                            + "-" + goodsListElement.getMnMaster()) == null?BigDecimal.ZERO:otherDiscountMap.get(goodsListElement.getGoodsId()
                            + "-" + goodsListElement.getMnMaster()));
                }
                parentOrderItemPo.setWarehouseId(goodsWarehouseAndDcBean.get(goodsListElement.getGoodsId()).getWarehouseID());
                
                if (goodsCount.get(goodsListElement.getGoodsId()).getGiftItems() == null
                                || goodsCount.get(goodsListElement.getGoodsId()).getGiftItems().isEmpty()) {// 不是礼盒
                    if (goodsInfo.get(goodsListElement.getGoodsId()).getWarmZoneID() == null) {
                        Log.info(logger, uuid, "商品温区为空", "goodsId", goodsListElement.getGoodsId());
                        return null;
                    }
                }
                
                parentOrderItemPo.setWarmzoneId(goodsInfo.get(goodsListElement.getGoodsId()).getWarmZoneID());
                parentOrderItemPo.setDcId(goodsWarehouseAndDcBean.get(goodsListElement.getGoodsId()).getDC());
                parentOrderItemPo.setShipCapacity(goodsShipCapacityMap.get(goodsListElement.getGoodsId()));
                String buyFrom = buyFromMap.get(goodsListElement.getGoodsId()
                                + "-" + goodsListElement.getCartMethod()) == null?"":buyFromMap.get(goodsListElement.getGoodsId()
                                + "-" + goodsListElement.getCartMethod());
                this.setCommonParentOrderItemsPo(buyFrom, parentOrderItemPo, goodsInfo, goodsListElement.getGoodsId(), scope, goodsCount, logger, uuid);
                items.add(parentOrderItemPo);
                if (goodsCount.get(goodsListElement.getGoodsId()).getGiftItems() != null
                                && !goodsCount.get(goodsListElement.getGoodsId()).getGiftItems().isEmpty()) {// 礼盒
                    parentOrderItemPo.setIsGiftbox(BooleanEnum.TRUE.getV());
                    parentOrderItemPo.setWarmzoneId(null);
                    int i = 1;
                    BigDecimal sum = BigDecimal.ZERO;
                    BigDecimal sumDiscount = BigDecimal.ZERO;
                    BigDecimal sumOtherDiscount = BigDecimal.ZERO;
                    for (GiftItemsBean giftElement : goodsCount.get(goodsListElement.getGoodsId()).getGiftItems()) {
                        TOsParentOrderItemsPO parentOrderItemPoGift = new TOsParentOrderItemsPO();
                        parentOrderItemPoGift.setOrderId(orderId);
                        parentOrderItemPoGift.setGoodsNumber(BigDecimal.valueOf(giftElement.getCount()).multiply(BigDecimal.valueOf(goodsListElement.getAmount())));
                        parentOrderItemPoGift.setShopPrice(giftElement.getPrice());
                        // 现价
                        if (i == goodsCount.get(goodsListElement.getGoodsId()).getGiftItems().size()) {// 最后一个
                            parentOrderItemPoGift.setGoodsPrice((parentOrderItemPo.getGoodsAmount().subtract(sum)).divide(parentOrderItemPoGift.getGoodsNumber(), 2, BigDecimal.ROUND_HALF_UP));
                        } else {
                            parentOrderItemPoGift.setGoodsPrice(parentOrderItemPoGift.getShopPrice().multiply(parentOrderItemPo.getGoodsPrice()).divide(parentOrderItemPo.getShopPrice(), 2, BigDecimal.ROUND_HALF_UP));
                        }
                        parentOrderItemPoGift.setPromoteId("0");
                        parentOrderItemPoGift.setGoodsName(goodsInfo.get(giftElement.getGoodsId()).getGoodsTitle());
                        parentOrderItemPoGift.setIsGiftbox(BooleanEnum.FALSE.getV());// 不是礼盒
                        parentOrderItemPoGift.setGiftboxId(itemId.intValue());
                        parentOrderItemPoGift.setIsGift((byte)0);
                        parentOrderItemPoGift.setGiftFrom(0L);
                        
                        if (!"0".equals(goodsInfo.get(giftElement.getGoodsId()).getCanReserve())) {// 预定商品
                            Calendar calendarClone = (Calendar)calendarReceive.clone();
                            if (specialGoods.toString().indexOf(String.valueOf(giftElement.getGoodsId())) >= 0) {
                                calendarClone.add(Calendar.DAY_OF_MONTH, -(Integer.parseInt(goodsInfo.get(giftElement.getGoodsId()).getDaysPrepare()) + 1));
                                parentOrderItemPoGift.setPurchaseDate(new Timestamp(calendarClone.getTime().getTime()));
                            } else {
                                calendarClone.add(Calendar.DAY_OF_MONTH, -(Integer.parseInt(goodsInfo.get(giftElement.getGoodsId()).getDaysPrepare())));
                                parentOrderItemPoGift.setPurchaseDate(new Timestamp(calendarClone.getTime().getTime()));
                            }
                        } else {
                            parentOrderItemPoGift.setPurchaseDate(null);
                        }
                        
                        // parentOrderItemPoGift.setPurchaseDate(parentOrderItemPo.getPurchaseDate());
                        parentOrderItemPoGift.setWarehouseId(parentOrderItemPo.getWarehouseId());
                        
                        if (goodsInfo.get(giftElement.getGoodsId()).getWarmZoneID() == null) {
                            Log.info(logger, uuid, "商品温区为空", "goodsId", giftElement.getGoodsId());
                            return null;
                        }
                        
                        parentOrderItemPoGift.setWarmzoneId(goodsInfo.get(giftElement.getGoodsId()).getWarmZoneID());
                        parentOrderItemPoGift.setDcId(parentOrderItemPo.getDcId());
                        parentOrderItemPoGift.setShipCapacity(parentOrderItemPo.getShipCapacity());
                        this.setCommonParentOrderItemsPo("", parentOrderItemPoGift, goodsInfo, giftElement.getGoodsId(), scope, goodsCount, logger, uuid);
                        if (i == goodsCount.get(goodsListElement.getGoodsId()).getGiftItems().size()) {// 最后一个
                            parentOrderItemPoGift.setDiscountFee(parentOrderItemPo.getDiscountFee().subtract(sumDiscount));
                            parentOrderItemPoGift.setOtherDiscountFee(parentOrderItemPo.getOtherDiscountFee().subtract(sumOtherDiscount));
                            /****增加原始订单折扣数据 by zhaochunna  2015-7-17 ****/
                            parentOrderItemPoGift.setOrigDiscountFee(parentOrderItemPo.getDiscountFee().subtract(sumDiscount));
                            parentOrderItemPoGift.setOrigOtherDiscountFee(parentOrderItemPo.getOtherDiscountFee().subtract(sumOtherDiscount));
                        } else {
                            parentOrderItemPoGift.setDiscountFee(parentOrderItemPoGift.getGoodsAmount().multiply(parentOrderItemPo.getDiscountFee()).divide(parentOrderItemPo.getGoodsAmount(), 2, BigDecimal.ROUND_HALF_UP));
                            parentOrderItemPoGift.setOtherDiscountFee(parentOrderItemPoGift.getGoodsAmount().multiply(parentOrderItemPo.getOtherDiscountFee()).divide(parentOrderItemPo.getGoodsAmount(), 2, BigDecimal.ROUND_HALF_UP));
                            /****增加原始订单折扣数据 by zhaochunna  2015-7-17 ****/
                            parentOrderItemPoGift.setOrigDiscountFee(parentOrderItemPoGift.getGoodsAmount().multiply(parentOrderItemPo.getDiscountFee()).divide(parentOrderItemPo.getGoodsAmount(), 2, BigDecimal.ROUND_HALF_UP));
                            parentOrderItemPoGift.setOrigOtherDiscountFee(parentOrderItemPoGift.getGoodsAmount().multiply(parentOrderItemPo.getOtherDiscountFee()).divide(parentOrderItemPo.getGoodsAmount(), 2, BigDecimal.ROUND_HALF_UP));
                        }
                        items.add(parentOrderItemPoGift);
                        sum = sum.add(parentOrderItemPoGift.getGoodsAmount());
                        sumDiscount = sumDiscount.add(parentOrderItemPoGift.getDiscountFee());
                        sumOtherDiscount = sumOtherDiscount.add(parentOrderItemPoGift.getOtherDiscountFee());
                        i++;
                    }
                }
            }
            
            if (promotionOutputData.getNyList() != null
                            && !promotionOutputData.getNyList().isEmpty()) {
                for (PromotionGetPromotionInfoNyListElementO nyElement : promotionOutputData.getNyList()) {
                    BigDecimal sumShopPrice = BigDecimal.ZERO;// 原价的和
                    for (PromotionGetPromotionInfoNyListSkuIdListElementO nySKUElement : nyElement.getNyListSkuIdList()) {
                        sumShopPrice = sumShopPrice.add(goodsInfo.get(nySKUElement.getGoodsId()).getSkuInfo().getTheOriginalPrice());
                    }
                    Long itemId = parentOrderItemDao.findSeqNextVal("SEQ_OS_PARENT_ORDER_ITEMS_PK");
                    if (itemId.intValue() == -1) {
                        Log.info(logger, uuid, "获得父订单详情表主键失败", "itemId", itemId);
                        return null;
                    }
                    TOsParentOrderItemsPO parentOrderItemPo = new TOsParentOrderItemsPO();
                    parentOrderItemPo.setItemId(itemId);
                    parentOrderItemPo.setOrderId(orderId);
                    parentOrderItemPo.setGoodsNumber(new BigDecimal(nyElement.getAmount()));
                    parentOrderItemPo.setShopPrice(sumShopPrice);// 原价
                    parentOrderItemPo.setGoodsPrice(nyElement.getN());// 现价
                    parentOrderItemPo.setPromoteId(nyElement.getPromotionId()
                                    + "");
                    parentOrderItemPo.setGoodsName(nyElement.getPromotionName());
                    parentOrderItemPo.setIsGiftbox(BooleanEnum.TRUE.getV());
                    parentOrderItemPo.setGiftboxId(Integer.valueOf(0));
                    parentOrderItemPo.setIsGift((byte)0);
                    parentOrderItemPo.setGiftFrom(0L);
                    parentOrderItemPo.setPurchaseDate(null);
                    parentOrderItemPo.setDiscountFee(discountMap.get(nyElement.getPromotionId()
                                    + "") == null?BigDecimal.ZERO:discountMap.get(nyElement.getPromotionId()
                                    + ""));
                    parentOrderItemPo.setOtherDiscountFee(otherDiscountMap.get(nyElement.getPromotionId()
                                    + "") == null?BigDecimal.ZERO:otherDiscountMap.get(nyElement.getPromotionId()
                                    + ""));
                    /****增加原始订单折扣数据 by zhaochunna  2015-7-17 ****/
                    parentOrderItemPo.setOrigDiscountFee(discountMap.get(nyElement.getPromotionId()
                            + "") == null?BigDecimal.ZERO:discountMap.get(nyElement.getPromotionId()
                            + ""));
                    parentOrderItemPo.setOrigOtherDiscountFee(otherDiscountMap.get(nyElement.getPromotionId()
                            + "") == null?BigDecimal.ZERO:otherDiscountMap.get(nyElement.getPromotionId()
                            + ""));
                            
                    Long goodsMax = 0L;
                    for (PromotionGetPromotionInfoNyListSkuIdListElementO nySKUElement : nyElement.getNyListSkuIdList()) {
                        goodsMax += (nySKUElement.getGoodsId() << 1L);
                    }
                    
                    parentOrderItemPo.setWarehouseId(goodsWarehouseAndDcBean.get(nyElement.getPromotionId()
                                    + goodsMax).getWarehouseID());
                    parentOrderItemPo.setWarmzoneId(null);
                    parentOrderItemPo.setDcId(goodsWarehouseAndDcBean.get(nyElement.getPromotionId()
                                    + goodsMax).getDC());
                    parentOrderItemPo.setShipCapacity(goodsShipCapacityMap.get(nyElement.getPromotionId()
                                    + goodsMax));
                    /*parentOrderItemPo.setWarehouseId(goodsWarehouseAndDcBean.get(nyElement.getPromotionId()
                                    + Long.parseLong(goodsIDMax)).getWarehouseID());
                    parentOrderItemPo.setWarmzoneId(null);
                    parentOrderItemPo.setDcId(goodsWarehouseAndDcBean.get(nyElement.getPromotionId()
                                    + Long.parseLong(goodsIDMax)).getDC());
                    parentOrderItemPo.setShipCapacity(goodsShipCapacityMap.get(nyElement.getPromotionId()
                                    + Long.parseLong(goodsIDMax)));*/
                    this.setCommonParentOrderItemsPo("", parentOrderItemPo, goodsInfo, CreateOrderImpl.VIRTUAL_GIFT, scope, goodsCount, logger, uuid);
                    items.add(parentOrderItemPo);
                    int i = 1;
                    BigDecimal sum = BigDecimal.ZERO;
                    BigDecimal sumDiscount = BigDecimal.ZERO;
                    BigDecimal sumOtherDiscount = BigDecimal.ZERO;
                    for (PromotionGetPromotionInfoNyListSkuIdListElementO nySKUElement : nyElement.getNyListSkuIdList()) {
                        TOsParentOrderItemsPO parentOrderItemPoGift = new TOsParentOrderItemsPO();
                        parentOrderItemPoGift.setOrderId(orderId);
                        parentOrderItemPoGift.setGoodsNumber(BigDecimal.valueOf(nyElement.getAmount()));
                        parentOrderItemPoGift.setShopPrice(goodsInfo.get(nySKUElement.getGoodsId()).getSkuInfo().getTheOriginalPrice());
                        // 现价
                        if (i == nyElement.getNyListSkuIdList().size()) {// 最后一个
                            parentOrderItemPoGift.setGoodsPrice((parentOrderItemPo.getGoodsAmount().subtract(sum)).divide(parentOrderItemPoGift.getGoodsNumber(), 2, BigDecimal.ROUND_HALF_UP));
                        } else {
                            parentOrderItemPoGift.setGoodsPrice(parentOrderItemPoGift.getShopPrice().multiply(parentOrderItemPo.getGoodsPrice()).divide(parentOrderItemPo.getShopPrice(), 2, BigDecimal.ROUND_HALF_UP));
                        }
                        parentOrderItemPoGift.setPromoteId("0");
                        parentOrderItemPoGift.setGoodsName(goodsInfo.get(nySKUElement.getGoodsId()).getGoodsTitle());
                        parentOrderItemPoGift.setIsGiftbox(BooleanEnum.FALSE.getV());// 不是礼盒
                        parentOrderItemPoGift.setGiftboxId(itemId.intValue());
                        parentOrderItemPoGift.setIsGift((byte)0);
                        parentOrderItemPoGift.setGiftFrom(0L);
                        
                        if (!"0".equals(goodsInfo.get(nySKUElement.getGoodsId()).getCanReserve())) {// 预定商品
                            Calendar calendarClone = (Calendar)calendarReceive.clone();
                            if (specialGoods.toString().indexOf(String.valueOf(nySKUElement.getGoodsId())) >= 0) {
                                calendarClone.add(Calendar.DAY_OF_MONTH, -(Integer.parseInt(goodsInfo.get(nySKUElement.getGoodsId()).getDaysPrepare()) + 1));
                                parentOrderItemPoGift.setPurchaseDate(new Timestamp(calendarClone.getTime().getTime()));
                            } else {
                                calendarClone.add(Calendar.DAY_OF_MONTH, -(Integer.parseInt(goodsInfo.get(nySKUElement.getGoodsId()).getDaysPrepare())));
                                parentOrderItemPoGift.setPurchaseDate(new Timestamp(calendarClone.getTime().getTime()));
                            }
                        } else {
                            parentOrderItemPoGift.setPurchaseDate(null);
                        }
                        
                        // parentOrderItemPoGift.setPurchaseDate(null);
                        parentOrderItemPoGift.setWarehouseId(parentOrderItemPo.getWarehouseId());
                        
                        if (goodsInfo.get(nySKUElement.getGoodsId()).getWarmZoneID() == null) {
                            Log.info(logger, uuid, "商品温区为空", "goodsId", nySKUElement.getGoodsId());
                            return null;
                        }
                        
                        parentOrderItemPoGift.setWarmzoneId(goodsInfo.get(nySKUElement.getGoodsId()).getWarmZoneID());
                        parentOrderItemPoGift.setDcId(parentOrderItemPo.getDcId());
                        parentOrderItemPoGift.setShipCapacity(parentOrderItemPo.getShipCapacity());
                        this.setCommonParentOrderItemsPo("", parentOrderItemPoGift, goodsInfo, nySKUElement.getGoodsId(), scope, goodsCount, logger, uuid);
                        if (i == nyElement.getNyListSkuIdList().size()) {// 最后一个
                            parentOrderItemPoGift.setDiscountFee(parentOrderItemPo.getDiscountFee().subtract(sumDiscount));
                            parentOrderItemPoGift.setOtherDiscountFee(parentOrderItemPo.getOtherDiscountFee().subtract(sumOtherDiscount));
                            /****增加原始订单折扣数据 by zhaochunna  2015-7-17 ****/
                            parentOrderItemPoGift.setOrigDiscountFee(parentOrderItemPo.getDiscountFee().subtract(sumDiscount));
                            parentOrderItemPoGift.setOrigOtherDiscountFee(parentOrderItemPo.getOtherDiscountFee().subtract(sumOtherDiscount));
                        } else {
                            parentOrderItemPoGift.setDiscountFee(parentOrderItemPoGift.getGoodsAmount().multiply(parentOrderItemPo.getDiscountFee()).divide(parentOrderItemPo.getGoodsAmount(), 2, BigDecimal.ROUND_HALF_UP));
                            parentOrderItemPoGift.setOtherDiscountFee(parentOrderItemPoGift.getGoodsAmount().multiply(parentOrderItemPo.getOtherDiscountFee()).divide(parentOrderItemPo.getGoodsAmount(), 2, BigDecimal.ROUND_HALF_UP));
                            /****增加原始订单折扣数据 by zhaochunna  2015-7-17 ****/
                            parentOrderItemPoGift.setOrigDiscountFee(parentOrderItemPoGift.getGoodsAmount().multiply(parentOrderItemPo.getDiscountFee()).divide(parentOrderItemPo.getGoodsAmount(), 2, BigDecimal.ROUND_HALF_UP));
                            parentOrderItemPoGift.setOrigOtherDiscountFee(parentOrderItemPoGift.getGoodsAmount().multiply(parentOrderItemPo.getOtherDiscountFee()).divide(parentOrderItemPo.getGoodsAmount(), 2, BigDecimal.ROUND_HALF_UP));
                        }
                        items.add(parentOrderItemPoGift);
                        sum = sum.add(parentOrderItemPoGift.getGoodsAmount());
                        sumDiscount = sumDiscount.add(parentOrderItemPoGift.getDiscountFee());
                        sumOtherDiscount = sumOtherDiscount.add(parentOrderItemPoGift.getOtherDiscountFee());
                        i++;
                    }
                }
            }
            
            // 赠品
            for (Map.Entry<Long, GoodsNumberBean> entry : goodsCount.entrySet()) {
                if (entry.getValue().getGiftNumber() > 0) {
                    Long itemId = parentOrderItemDao.findSeqNextVal("SEQ_OS_PARENT_ORDER_ITEMS_PK");
                    if (itemId.intValue() == -1) {
                        Log.info(logger, uuid, "获得父订单详情表主键失败", "itemId", itemId);
                        return null;
                    }
                    TOsParentOrderItemsPO parentOrderItemPo = new TOsParentOrderItemsPO();
                    parentOrderItemPo.setItemId(itemId);
                    parentOrderItemPo.setOrderId(orderId);
                    parentOrderItemPo.setGoodsNumber(BigDecimal.valueOf(entry.getValue().getGiftNumber()));
                    parentOrderItemPo.setShopPrice(goodsInfo.get(entry.getKey()).getSkuInfo().getTheOriginalPrice());
                    parentOrderItemPo.setGoodsPrice(BigDecimal.ZERO);
                    parentOrderItemPo.setPromoteId(goodsInfo.get(entry.getKey()).getSkuInfo().getPromotionID()
                                    + "");
                    parentOrderItemPo.setGoodsName("【赠品】"
                                    + goodsInfo.get(entry.getKey()).getGoodsTitle());
                    parentOrderItemPo.setIsGiftbox(BooleanEnum.FALSE.getV());
                    parentOrderItemPo.setGiftboxId(Integer.valueOf(0));
                    parentOrderItemPo.setIsGift((byte)1);
                    parentOrderItemPo.setGiftFrom(0L);
                    
                    if (!"0".equals(goodsInfo.get(entry.getKey()).getCanReserve())) {// 预定商品(收货日期+备货期)
                        Calendar calendarClone = (Calendar)calendarReceive.clone();
                        if (specialGoods.toString().indexOf(String.valueOf(entry.getKey())) >= 0) {
                            calendarClone.add(Calendar.DAY_OF_MONTH, -(Integer.parseInt(goodsInfo.get(entry.getKey()).getDaysPrepare()) + 1));
                            parentOrderItemPo.setPurchaseDate(new Timestamp(calendarClone.getTime().getTime()));
                        } else {
                            calendarClone.add(Calendar.DAY_OF_MONTH, -(Integer.parseInt(goodsInfo.get(entry.getKey()).getDaysPrepare())));
                            parentOrderItemPo.setPurchaseDate(new Timestamp(calendarClone.getTime().getTime()));
                        }
                    } else {
                        parentOrderItemPo.setPurchaseDate(null);
                    }
                    parentOrderItemPo.setDiscountFee(BigDecimal.ZERO);
                    parentOrderItemPo.setOtherDiscountFee(BigDecimal.ZERO);
                    /****增加原始订单折扣数据 by zhaochunna  2015-7-17 ****/
                    parentOrderItemPo.setOrigDiscountFee(BigDecimal.ZERO);
                    parentOrderItemPo.setOrigOtherDiscountFee(BigDecimal.ZERO);
                    parentOrderItemPo.setWarehouseId(goodsWarehouseAndDcBean.get(entry.getKey()).getWarehouseID());
                    
                    if (entry.getValue().getGiftItems() == null
                                    || entry.getValue().getGiftItems().isEmpty()) {// 礼盒
                        if (goodsInfo.get(entry.getKey()).getWarmZoneID() == null) {
                            Log.info(logger, uuid, "商品温区为空", "goodsId", entry.getKey());
                            return null;
                        }
                    }
                    
                    parentOrderItemPo.setWarmzoneId(goodsInfo.get(entry.getKey()).getWarmZoneID());
                    parentOrderItemPo.setDcId(goodsWarehouseAndDcBean.get(entry.getKey()).getDC());
                    parentOrderItemPo.setShipCapacity(goodsShipCapacityMap.get(entry.getKey()));
                    this.setCommonParentOrderItemsPo("", parentOrderItemPo, goodsInfo, entry.getKey(), scope, goodsCount, logger, uuid);
                    items.add(parentOrderItemPo);
                    
                    if (entry.getValue().getGiftItems() != null
                                    && !entry.getValue().getGiftItems().isEmpty()) {// 礼盒
                        parentOrderItemPo.setIsGiftbox(BooleanEnum.TRUE.getV());
                        parentOrderItemPo.setWarmzoneId(null);
                        for (GiftItemsBean giftElement : entry.getValue().getGiftItems()) {
                            TOsParentOrderItemsPO parentOrderItemPoGift = new TOsParentOrderItemsPO();
                            parentOrderItemPoGift.setOrderId(orderId);
                            parentOrderItemPoGift.setGoodsNumber(BigDecimal.valueOf(giftElement.getCount()).multiply(parentOrderItemPo.getGoodsNumber()));
                            parentOrderItemPoGift.setShopPrice(giftElement.getPrice());
                            parentOrderItemPoGift.setGoodsPrice(BigDecimal.ZERO);
                            parentOrderItemPoGift.setPromoteId("0");
                            parentOrderItemPoGift.setGoodsName(goodsInfo.get(giftElement.getGoodsId()).getGoodsTitle());
                            parentOrderItemPoGift.setIsGiftbox(BooleanEnum.FALSE.getV());// 不是礼盒
                            parentOrderItemPoGift.setGiftboxId(itemId.intValue());
                            parentOrderItemPoGift.setIsGift((byte)0);
                            parentOrderItemPoGift.setGiftFrom(0L);
                            
                            if (!"0".equals(goodsInfo.get(giftElement.getGoodsId()).getCanReserve())) {// 预定商品
                                Calendar calendarClone = (Calendar)calendarReceive.clone();
                                if (specialGoods.toString().indexOf(String.valueOf(giftElement.getGoodsId())) >= 0) {
                                    calendarClone.add(Calendar.DAY_OF_MONTH, -(Integer.parseInt(goodsInfo.get(giftElement.getGoodsId()).getDaysPrepare()) + 1));
                                    parentOrderItemPoGift.setPurchaseDate(new Timestamp(calendarClone.getTime().getTime()));
                                } else {
                                    calendarClone.add(Calendar.DAY_OF_MONTH, -(Integer.parseInt(goodsInfo.get(giftElement.getGoodsId()).getDaysPrepare())));
                                    parentOrderItemPoGift.setPurchaseDate(new Timestamp(calendarClone.getTime().getTime()));
                                }
                            } else {
                                parentOrderItemPoGift.setPurchaseDate(null);
                            }
                            
                            // parentOrderItemPoGift.setPurchaseDate(parentOrderItemPo.getPurchaseDate());
                            parentOrderItemPoGift.setWarehouseId(parentOrderItemPo.getWarehouseId());
                            
                            if (goodsInfo.get(giftElement.getGoodsId()).getWarmZoneID() == null) {
                                Log.info(logger, uuid, "商品温区为空", "goodsId", entry.getKey());
                                return null;
                            }
                            
                            parentOrderItemPoGift.setWarmzoneId(goodsInfo.get(giftElement.getGoodsId()).getWarmZoneID());
                            parentOrderItemPoGift.setDcId(parentOrderItemPo.getDcId());
                            parentOrderItemPoGift.setShipCapacity(parentOrderItemPo.getShipCapacity());
                            this.setCommonParentOrderItemsPo("", parentOrderItemPoGift, goodsInfo, giftElement.getGoodsId(), scope, goodsCount, logger, uuid);
                            parentOrderItemPoGift.setDiscountFee(BigDecimal.ZERO);
                            parentOrderItemPoGift.setOtherDiscountFee(BigDecimal.ZERO);
                            /****增加原始订单折扣数据 by zhaochunna  2015-7-17 ****/
                            parentOrderItemPoGift.setOrigDiscountFee(BigDecimal.ZERO);
                            parentOrderItemPoGift.setOrigOtherDiscountFee(BigDecimal.ZERO);
                            items.add(parentOrderItemPoGift);
                        }
                    }
                }
            }
            
            // 兑换券商品
            for (Map.Entry<Long, GoodsNumberBean> entry : goodsCount.entrySet()) {
                if (entry.getValue().getExNumber() > 0) {
                    Long itemId = parentOrderItemDao.findSeqNextVal("SEQ_OS_PARENT_ORDER_ITEMS_PK");
                    if (itemId.intValue() == -1) {
                        Log.info(logger, uuid, "获得父订单详情表主键失败", "itemId", itemId);
                        return null;
                    }
                    TOsParentOrderItemsPO parentOrderItemPo = new TOsParentOrderItemsPO();
                    parentOrderItemPo.setItemId(itemId);
                    parentOrderItemPo.setOrderId(orderId);
                    parentOrderItemPo.setGoodsNumber(BigDecimal.valueOf(entry.getValue().getExNumber()));
                    parentOrderItemPo.setShopPrice(goodsInfo.get(entry.getKey()).getSkuInfo().getTheOriginalPrice());
                    parentOrderItemPo.setGoodsPrice(BigDecimal.ZERO);
                    parentOrderItemPo.setPromoteId(null);
                    parentOrderItemPo.setGoodsName("【兑换】" + goodsInfo.get(entry.getKey()).getGoodsTitle());
                    parentOrderItemPo.setIsGiftbox(BooleanEnum.FALSE.getV());
                    parentOrderItemPo.setGiftboxId(Integer.valueOf(0));
                    parentOrderItemPo.setIsGift((byte)0);
                    parentOrderItemPo.setGiftFrom(0L);
                    
                    if (!"0".equals(goodsInfo.get(entry.getKey()).getCanReserve())) {// 预定商品(收货日期+备货期)
                        Calendar calendarClone = (Calendar)calendarReceive.clone();
                        if (specialGoods.toString().indexOf(String.valueOf(entry.getKey())) >= 0) {
                            calendarClone.add(Calendar.DAY_OF_MONTH, -(Integer.parseInt(goodsInfo.get(entry.getKey()).getDaysPrepare()) + 1));
                            parentOrderItemPo.setPurchaseDate(new Timestamp(calendarClone.getTime().getTime()));
                        } else {
                            calendarClone.add(Calendar.DAY_OF_MONTH, -(Integer.parseInt(goodsInfo.get(entry.getKey()).getDaysPrepare())));
                            parentOrderItemPo.setPurchaseDate(new Timestamp(calendarClone.getTime().getTime()));
                        }
                    } else {
                        parentOrderItemPo.setPurchaseDate(null);
                    }
                    parentOrderItemPo.setDiscountFee(BigDecimal.ZERO);
                    parentOrderItemPo.setOtherDiscountFee(BigDecimal.ZERO);
                    /****增加原始订单折扣数据 by zhaochunna  2015-7-17 ****/
                    parentOrderItemPo.setOrigDiscountFee(BigDecimal.ZERO);
                    parentOrderItemPo.setOrigOtherDiscountFee(BigDecimal.ZERO);
                    parentOrderItemPo.setWarehouseId(goodsWarehouseAndDcBean.get(entry.getKey()).getWarehouseID());
                    
                    if (entry.getValue().getGiftItems() == null
                                    || entry.getValue().getGiftItems().isEmpty()) {// 礼盒
                        if (goodsInfo.get(entry.getKey()).getWarmZoneID() == null) {
                            Log.info(logger, uuid, "商品温区为空", "goodsId", entry.getKey());
                            return null;
                        }
                    }
                    
                    parentOrderItemPo.setWarmzoneId(goodsInfo.get(entry.getKey()).getWarmZoneID());
                    parentOrderItemPo.setDcId(goodsWarehouseAndDcBean.get(entry.getKey()).getDC());
                    parentOrderItemPo.setShipCapacity(goodsShipCapacityMap.get(entry.getKey()));
                    parentOrderItemPo.setExCouponSn(entry.getValue().getExSn());
                    this.setCommonParentOrderItemsPo("", parentOrderItemPo, goodsInfo, entry.getKey(), scope, goodsCount, logger, uuid);
                    items.add(parentOrderItemPo);
                    
                    if (entry.getValue().getGiftItems() != null
                                    && !entry.getValue().getGiftItems().isEmpty()) {// 礼盒
                        parentOrderItemPo.setIsGiftbox(BooleanEnum.TRUE.getV());
                        parentOrderItemPo.setWarmzoneId(null);
                        for (GiftItemsBean giftElement : entry.getValue().getGiftItems()) {
                            TOsParentOrderItemsPO parentOrderItemPoGift = new TOsParentOrderItemsPO();
                            parentOrderItemPoGift.setOrderId(orderId);
                            parentOrderItemPoGift.setGoodsNumber(BigDecimal.valueOf(giftElement.getCount()).multiply(parentOrderItemPo.getGoodsNumber()));
                            parentOrderItemPoGift.setShopPrice(giftElement.getPrice());
                            parentOrderItemPoGift.setGoodsPrice(BigDecimal.ZERO);
                            parentOrderItemPoGift.setPromoteId("0");
                            parentOrderItemPoGift.setGoodsName(goodsInfo.get(giftElement.getGoodsId()).getGoodsTitle());
                            parentOrderItemPoGift.setIsGiftbox(BooleanEnum.FALSE.getV());// 不是礼盒
                            parentOrderItemPoGift.setGiftboxId(itemId.intValue());
                            parentOrderItemPoGift.setIsGift((byte)0);
                            parentOrderItemPoGift.setGiftFrom(0L);
                            
                            if (!"0".equals(goodsInfo.get(giftElement.getGoodsId()).getCanReserve())) {// 预定商品
                                Calendar calendarClone = (Calendar)calendarReceive.clone();
                                if (specialGoods.toString().indexOf(String.valueOf(giftElement.getGoodsId())) >= 0) {
                                    calendarClone.add(Calendar.DAY_OF_MONTH, -(Integer.parseInt(goodsInfo.get(giftElement.getGoodsId()).getDaysPrepare()) + 1));
                                    parentOrderItemPoGift.setPurchaseDate(new Timestamp(calendarClone.getTime().getTime()));
                                } else {
                                    calendarClone.add(Calendar.DAY_OF_MONTH, -(Integer.parseInt(goodsInfo.get(giftElement.getGoodsId()).getDaysPrepare())));
                                    parentOrderItemPoGift.setPurchaseDate(new Timestamp(calendarClone.getTime().getTime()));
                                }
                            } else {
                                parentOrderItemPoGift.setPurchaseDate(null);
                            }
                            
                            // parentOrderItemPoGift.setPurchaseDate(parentOrderItemPo.getPurchaseDate());
                            parentOrderItemPoGift.setWarehouseId(parentOrderItemPo.getWarehouseId());
                            
                            if (goodsInfo.get(giftElement.getGoodsId()).getWarmZoneID() == null) {
                                Log.info(logger, uuid, "商品温区为空", "goodsId", entry.getKey());
                                return null;
                            }
                            
                            parentOrderItemPoGift.setWarmzoneId(goodsInfo.get(giftElement.getGoodsId()).getWarmZoneID());
                            parentOrderItemPoGift.setDcId(parentOrderItemPo.getDcId());
                            parentOrderItemPoGift.setShipCapacity(parentOrderItemPo.getShipCapacity());
                            parentOrderItemPoGift.setExCouponSn(parentOrderItemPo.getExCouponSn());
                            this.setCommonParentOrderItemsPo("", parentOrderItemPoGift, goodsInfo, giftElement.getGoodsId(), scope, goodsCount, logger, uuid);
                            parentOrderItemPoGift.setDiscountFee(BigDecimal.ZERO);
                            parentOrderItemPoGift.setOtherDiscountFee(BigDecimal.ZERO);
                            /****增加原始订单折扣数据 by zhaochunna  2015-7-17 ****/
                            parentOrderItemPoGift.setOrigDiscountFee(BigDecimal.ZERO);
                            parentOrderItemPoGift.setOrigOtherDiscountFee(BigDecimal.ZERO);
                            items.add(parentOrderItemPoGift);
                        }
                    }
                }
            }
            return items;
        }
        return null;
    }
    
    public Map<String, BigDecimal>[] getDiscountFee(PromotionGetPromotionInfoOutputData promotionOutputData) {
        
        Map<String, BigDecimal> goodsFeeMap = new HashMap<String, BigDecimal>();
        for (PromotionGetPromotionInfoGoodsListElementO goodsElement : promotionOutputData.getGoodsList()) {
            if (!"2".equals(goodsElement.getCartMethod())) {// 不是换购
                goodsFeeMap.put(goodsElement.getGoodsId() + "-"
                                + goodsElement.getMnMaster(), goodsElement.getFee());
            }
        }
        if (promotionOutputData.getNyList() != null
                        && !promotionOutputData.getNyList().isEmpty()) {
            for (PromotionGetPromotionInfoNyListElementO nyElement : promotionOutputData.getNyList()) {
                goodsFeeMap.put(nyElement.getPromotionId() + "", nyElement.getN().multiply(BigDecimal.valueOf(nyElement.getAmount())));
            }
        }
        Map<String, BigDecimal> discountMap = new HashMap<String, BigDecimal>();
        Map<String, BigDecimal> otherDiscountMap = new HashMap<String, BigDecimal>();
        for (PromotionGetPromotionInfoDiscountListElementO discountElement : promotionOutputData.getDiscountList()) {
            List<String> list = new ArrayList<String>();
            for (PromotionGetPromotionInfoItemsElementO itemElement : discountElement.getItems()) {
                list.add(itemElement.getGoodsId() + "-"
                                + itemElement.getMnMaster());
            }
            for (PromotionGetPromotionInfoNyItemsElementO nyElement : discountElement.getNyItems()) {
                list.add(nyElement.getPromotionId() + "");
            }
            BigDecimal allFee = BigDecimal.ZERO;
            for (String s : list) {
                allFee = allFee.add(goodsFeeMap.get(s));
            }
            
            int i = 1;
            BigDecimal sum = BigDecimal.ZERO;
            Collections.sort(list);
            for (String s : list) {
                BigDecimal fee = BigDecimal.ZERO;
                if (i == list.size()) {// 最后一个
                    fee = fee.add(discountElement.getDiscount().subtract(sum));
                } else {
                    fee = fee.add(goodsFeeMap.get(s).multiply(discountElement.getDiscount()).divide(allFee, 2, BigDecimal.ROUND_HALF_UP));
                }
                if (PromotionTypeEnum.ORDER_MAN_JIAN.getC().equals(discountElement.getPromotionType())) {// 订单满减
                    discountMap.put(s, fee);
                } else {
                    if (otherDiscountMap.containsKey(s)) {
                        otherDiscountMap.put(s, otherDiscountMap.get(s).add(fee));
                    } else {
                        otherDiscountMap.put(s, fee);
                    }
                }
                sum = sum.add(fee);
                i++;
            }
        }
        Map<String, BigDecimal>[] map = new HashMap[2];
        map[0] = discountMap;
        map[1] = otherDiscountMap;
        return map;
        
    }
    
    /**
     * 获得配送输入参数
     * Description:<br>
     * 
     * @param goodsInfos
     * @param inputData
     * @return
     * @throws Exception
     */
    public ShippingGetGoodsShippingInfosInputData getGoodsShippingInfosInputData(String couponSN,boolean isPower, String scope, String orderType, String orderFrom, Long substationID, Long templeteId, Long lastGeoID, Map<Long, GoodsNumberBean> goodsCount, Map<Long, GoodsGetGoodsInfoGoodsInfoElementO> goodsInfo, String timeShipType) throws Exception {
        ShippingGetGoodsShippingInfosInputData goodsShippingInfosInputData = new ShippingGetGoodsShippingInfosInputData();
        goodsShippingInfosInputData.setScope(scope);
        goodsShippingInfosInputData.setOrderType(orderType);
        goodsShippingInfosInputData.setCouponSN(couponSN);
        if (isPower) {
            goodsShippingInfosInputData.setOrderFrom(orderFrom);
        } else {
            goodsShippingInfosInputData.setOrderFrom("abc");
        }
        goodsShippingInfosInputData.setTemplateID(templeteId);
        ShippingGetGoodsShippingInfosGeoListElementI geoElement = new ShippingGetGoodsShippingInfosGeoListElementI();
        geoElement.setGeoID(lastGeoID);
        goodsShippingInfosInputData.getGeoList().add(geoElement);
        List<String> sendDateList = new ArrayList<String>();
        
        int i = 0;
        for (Map.Entry<Long, GoodsNumberBean> entry : goodsCount.entrySet()) {
            if (entry.getValue().getBuyNumber() > 0 || entry.getValue().getExNumber() > 0) {// 不是纯赠品
                ShippingGetGoodsShippingInfosGoodsListElementI goodsListElement = new ShippingGetGoodsShippingInfosGoodsListElementI();
                goodsListElement.setSubstationID(substationID);
                goodsListElement.setGoodsID(entry.getKey());
                goodsListElement.setIsGift(BooleanEnum.FALSE.getV());
                goodsListElement.setShippingModeID("0".equals(entry.getValue().getGiftType())?null:goodsInfo.get(entry.getKey()).getShippingModeID());
                goodsListElement.setGiftType(entry.getValue().getGiftType());
                List<String> sendDateElement = new ArrayList<String>();
                if (entry.getValue().getGiftItems() != null
                                && !entry.getValue().getGiftItems().isEmpty()) {// 礼盒或者NY(虚拟礼盒)
                    goodsListElement.setIsGift(BooleanEnum.TRUE.getV());
                    int j = 0;
                    for (GiftItemsBean bean : entry.getValue().getGiftItems()) {
                        ShippingGetGoodsShippingInfosGiftItemsElementI giftElement = new ShippingGetGoodsShippingInfosGiftItemsElementI();
                        giftElement.setGoodsID(bean.getGoodsId());
                        giftElement.setShippingModeID(goodsInfo.get(bean.getGoodsId()).getShippingModeID());
                        giftElement.setAmount(BigDecimal.valueOf(bean.getCount()));
                        goodsListElement.getGiftItems().add(giftElement);
                        
                        // 每个商品的配送日期取交集
                        List<String> giftItemsDateList = new ArrayList<String>();
                        for (GoodsGetGoodsInfoSendDaysElementO sendDayElement : goodsInfo.get(bean.getGoodsId()).getExtendsInfo().getSendDays()) {
                            if (!giftItemsDateList.contains(sendDayElement.getSendDay())) {
                                giftItemsDateList.add(sendDayElement.getSendDay());
                            }
                        }
                        if (j == 0) {// 第一次
                            sendDateElement.addAll(giftItemsDateList);
                        } else {
                            sendDateElement.retainAll(giftItemsDateList);
                        }
                        j++;
                    }
                } else {
                    for (GoodsGetGoodsInfoSendDaysElementO sendDayElement : goodsInfo.get(entry.getKey()).getExtendsInfo().getSendDays()) {
                        sendDateElement.add(sendDayElement.getSendDay());
                    }
                }
                goodsShippingInfosInputData.getGoodsList().add(goodsListElement);
                
                if (i == 0) {// 第一次
                    sendDateList.addAll(sendDateElement);
                } else {
                    sendDateList.retainAll(sendDateElement);
                }
                i++;
            }
        }
        
        Log.info(logger, uuid, "日期测试", "sendDateList", sendDateList);
        
        if (OrderTypeEnum.JIELONG_GROUPBUY.getC().equals(orderType)
                        && (StringUtil.isEmpty(templeteId.toString()) || !templeteId.toString().equals("0"))) {// 接龙团购且走模板
        
        } else {
            
            if (sendDateList.isEmpty()) {// 不可同天送
                return null;
            }
            
            // Set<Long> giftNot = new HashSet<Long>();
            for (Map.Entry<Long, GoodsNumberBean> entry : goodsCount.entrySet()) {
                if (entry.getValue().getBuyNumber() <= 0) {// 是纯赠品
                    ShippingGetGoodsShippingInfosGoodsListElementI goodsListElement = new ShippingGetGoodsShippingInfosGoodsListElementI();
                    goodsListElement.setSubstationID(substationID);
                    goodsListElement.setGoodsID(entry.getKey());
                    goodsListElement.setIsGift(BooleanEnum.FALSE.getV());
                    goodsListElement.setShippingModeID("0".equals(entry.getValue().getGiftType())?null:goodsInfo.get(entry.getKey()).getShippingModeID());
                    goodsListElement.setGiftType(entry.getValue().getGiftType());
                    List<String> sendDateElement = new ArrayList<String>();
                    if (entry.getValue().getGiftItems() != null
                                    && !entry.getValue().getGiftItems().isEmpty()) {// 礼盒或者NY(虚拟礼盒)
                        goodsListElement.setIsGift(BooleanEnum.TRUE.getV());
                        int j = 0;
                        for (GiftItemsBean bean : entry.getValue().getGiftItems()) {
                            ShippingGetGoodsShippingInfosGiftItemsElementI giftElement = new ShippingGetGoodsShippingInfosGiftItemsElementI();
                            giftElement.setGoodsID(bean.getGoodsId());
                            giftElement.setShippingModeID(goodsInfo.get(bean.getGoodsId()).getShippingModeID());
                            giftElement.setAmount(BigDecimal.valueOf(bean.getCount()));
                            goodsListElement.getGiftItems().add(giftElement);
                            
                            // 每个商品的配送日期取交集
                            List<String> giftItemsDateList = new ArrayList<String>();
                            for (GoodsGetGoodsInfoSendDaysElementO sendDayElement : goodsInfo.get(bean.getGoodsId()).getExtendsInfo().getSendDays()) {
                                if (!giftItemsDateList.contains(sendDayElement.getSendDay())) {
                                    giftItemsDateList.add(sendDayElement.getSendDay());
                                }
                            }
                            if (j == 0) {// 第一次
                                sendDateElement.addAll(giftItemsDateList);
                            } else {
                                sendDateElement.retainAll(giftItemsDateList);
                            }
                            j++;
                        }
                    } else {
                        for (GoodsGetGoodsInfoSendDaysElementO sendDayElement : goodsInfo.get(entry.getKey()).getExtendsInfo().getSendDays()) {
                            sendDateElement.add(sendDayElement.getSendDay());
                        }
                    }
                    
                    Log.info(logger, uuid, "日期测试1", "sendDateList", sendDateList);
                    Log.info(logger, uuid, "日期测试11", "sendDateElement", sendDateElement);
                    
                    List<String> temp = new ArrayList<String>(sendDateList);
                    temp.retainAll(sendDateElement);
                    Log.info(logger, uuid, "日期测试", "temp", temp);
                    if (temp.isEmpty()) {
                        // giftNot.add(entry.getKey());
                    } else {
                        goodsShippingInfosInputData.getGoodsList().add(goodsListElement);
                    }
                }
            }
            // Log.info(logger, uuid, "日期测试2", "giftNot", giftNot);
            // if(!giftNot.isEmpty()){
            // for(Long l : giftNot){
            // goodsCount.remove(l);
            // }
            // }
            Log.info(logger, uuid, "日期测试3", "goodsCount", goodsCount);
            
            goodsShippingInfosInputData.setTimeShipType(timeShipType);
            Set<String> dateSet = new HashSet<String>(sendDateList);
            
            if (timeShipType.equals(TimeShipTypeEnum.TODAY.getC())) {// 当日达
                if (!dateSet.contains(new SimpleDateFormat("yyyy-MM-dd").format(new Date()))) {// 不包含当天
                    Log.info(logger, uuid, "不支持当日达", "dateSet", dateSet);
                    return null;
                }
            } else {
                dateSet.remove(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
            }
            if (dateSet.isEmpty()) {// 非当日达的去掉当天的日期，如果为空，不可同天送
                return null;
            }
            
            for (String date : dateSet) {
                ShippingGetGoodsShippingInfosShippingDatesElementI dateElement = new ShippingGetGoodsShippingInfosShippingDatesElementI();
                dateElement.setShippingDate(date);
                goodsShippingInfosInputData.getShippingDates().add(dateElement);
            }
            
        }
        
        return goodsShippingInfosInputData;
    }
    
    public ShippingGetGoodsShippingFeeInputData getGoodsItemsShipfee(String couponType,String couponSN,BigDecimal orderdiscountFee,boolean isGiftOK, Long giftGoodsId, String scope, String orderType, String timeShipType, Long substationID, Long geoID, BigDecimal orderFee, HashMap<Long, GoodsNumberBean> goodsCount, Map<Long, GoodsGetGoodsInfoGoodsInfoElementO> goodsInfo) throws Exception {
        
        ShippingGetGoodsShippingFeeInputData shippingFeeInputData = new ShippingGetGoodsShippingFeeInputData();
        
        shippingFeeInputData.setScope(scope);
        shippingFeeInputData.setCouponType(couponType);
        shippingFeeInputData.setCouponSN(couponSN);
        shippingFeeInputData.setDisCountFee(orderdiscountFee);
        shippingFeeInputData.setOrderType(orderType);
        shippingFeeInputData.setTimeShipType(timeShipType);// 查询当日达，前台通过日期判断是否显示服务费
        shippingFeeInputData.setSubstationID(substationID);
        shippingFeeInputData.setGeoID(geoID);
        
        boolean b = false;
        GoodsNumberBean beanGift = null;
        Log.info(logger, uuid, "测试信息11", "goodsCount", goodsCount);
        if (isGiftOK) {
            if (goodsCount.containsKey(giftGoodsId)) {
                b = true;
                goodsCount.get(giftGoodsId).setGiftNumber(goodsCount.get(giftGoodsId).getGiftNumber() - 1);
                if (goodsCount.get(giftGoodsId).getBuyNumber() <= 0
                                && goodsCount.get(giftGoodsId).getGiftNumber() <= 0) {
                    beanGift = goodsCount.get(giftGoodsId);
                    goodsCount.remove(giftGoodsId);
                }
            }
        }
        Log.info(logger, uuid, "测试信息12", "goodsCount", goodsCount);
        for (Map.Entry<Long, GoodsNumberBean> entry : goodsCount.entrySet()) {
            
            ShippingGetGoodsShippingFeeGoodsListElementI goodsListElement = new ShippingGetGoodsShippingFeeGoodsListElementI();
            goodsListElement.setGoodsID(entry.getKey());
            goodsListElement.setGoodsBuyNum(BigDecimal.valueOf(entry.getValue().getBuyNumber()
                            + entry.getValue().getGiftNumber()
                            + entry.getValue().getExNumber()));
            goodsListElement.setShippingModeID("0".equals(entry.getValue().getGiftType())?null:goodsInfo.get(entry.getKey()).getShippingModeID());
            goodsListElement.setGiftType(entry.getValue().getGiftType());
            if (entry.getValue().getGiftItems() != null
                            && !entry.getValue().getGiftItems().isEmpty()) {// 礼盒
                goodsListElement.setIsGift(BooleanEnum.TRUE.getV());
                for (GiftItemsBean bean : entry.getValue().getGiftItems()) {
                    ShippingGetGoodsShippingFeeGiftItemsElementI giftItemElement = new ShippingGetGoodsShippingFeeGiftItemsElementI();
                    giftItemElement.setGoodsID(bean.getGoodsId());
                    giftItemElement.setGoodsWeight(goodsInfo.get(bean.getGoodsId()).getSkuInfo().getUnitWeight());
                    giftItemElement.setShippingModeID(goodsInfo.get(bean.getGoodsId()).getShippingModeID());
                    giftItemElement.setAmount(BigDecimal.valueOf(bean.getCount()));
                    goodsListElement.getGiftItems().add(giftItemElement);
                }
            } else {
                goodsListElement.setIsGift(BooleanEnum.FALSE.getV());
                goodsListElement.setGoodsWeight(goodsInfo.get(entry.getKey()).getSkuInfo().getUnitWeight());
            }
            shippingFeeInputData.getGoodsList().add(goodsListElement);
        }
        shippingFeeInputData.setOrderFee(orderFee);
        
        if (b) {
            Log.info(logger, uuid, "测试信息13", "goodsCount", goodsCount);
            if (goodsCount.containsKey(giftGoodsId)) {
                Log.info(logger, uuid, "测试信息14", "goodsCount", goodsCount);
                goodsCount.get(giftGoodsId).setGiftNumber(goodsCount.get(giftGoodsId).getGiftNumber() + 1);
            } else {
                Log.info(logger, uuid, "测试信息15", "goodsCount", goodsCount);
                goodsCount.put(giftGoodsId, beanGift);
                goodsCount.get(giftGoodsId).setGiftNumber(goodsCount.get(giftGoodsId).getGiftNumber() + 1);
                Log.info(logger, uuid, "测试信息16", "goodsCount", goodsCount);
            }
        }
        
        return shippingFeeInputData;
    }
    
    /**
     * 获得真实的赠品(验证促销赠品后的赠品)
     * Description:<br>
     * 
     * @param selectedGiftList
     * @param promotionGiftList
     * @param goodsInfo
     * @return
     */
    public boolean setActualGift(PromotionGetPromotionInfoOutputData promotionOutputData, Map<Long, GoodsNumberBean> goodsCount, boolean isGiftOK, Long giftGoodsId) throws Exception {
        
        Map<Long, Long> promotionGiftMap = new HashMap<Long, Long>();
        Map<Long, Long> promotionGiftNumMap = new HashMap<Long, Long>(); // 促销ID对应的赠品种类数
        Map<Long, Set<Long>> promotionGiftGoodsMap = new HashMap<Long, Set<Long>>(); // 促销ID对应的赠品list
        for (PromotionGetPromotionInfoGiftListElementO giftElement : promotionOutputData.getGiftList()) {
            Set<Long> giftGoods = new HashSet<Long>();
            for (PromotionGetPromotionInfoGiftGoodsListElementO goodElement : giftElement.getGiftGoodsList()) {
                if (goodElement.getAmount() > 0) {
                    if (promotionGiftMap.containsKey(goodElement.getGoodsId())) {
                        promotionGiftMap.put(goodElement.getGoodsId(), promotionGiftMap.get(goodElement.getGoodsId())
                                        + goodElement.getAmount());
                    } else {
                        promotionGiftMap.put(goodElement.getGoodsId(), goodElement.getAmount());
                    }
                    giftGoods.add(goodElement.getGoodsId());
                }
            }
            promotionGiftNumMap.put(giftElement.getPromotionId(), giftElement.getMzGiftNum());
            promotionGiftGoodsMap.put(giftElement.getPromotionId(), giftGoods);
        }
        
        for (Map.Entry<Long, GoodsNumberBean> entry : goodsCount.entrySet()) {
            if (entry.getValue().getGiftNumber() > 0) {// 是赠品
                if (promotionGiftMap.get(entry.getKey()) != null) {
                    if (isGiftOK
                                    && entry.getKey().intValue() == giftGoodsId.intValue()) {// 没有真正已经赠了
                        if (entry.getValue().getGiftNumber() - 1 > promotionGiftMap.get(entry.getKey())) {// 小于促销返回的赠品数
                            entry.getValue().setGiftNumber(promotionGiftMap.get(entry.getKey()) + 1);
                        }
                    } else {
                        if (entry.getValue().getGiftNumber() > promotionGiftMap.get(entry.getKey())) {// 小于促销返回的赠品数
                            entry.getValue().setGiftNumber(promotionGiftMap.get(entry.getKey()));
                        }
                    }
                } else {// 促销返回中没有
                    if (!(isGiftOK && entry.getKey().intValue() == giftGoodsId.intValue())) {// 没有真正已经赠了
                        return false;
                    }
                }
            }
        }
        Set<Long> removeGift = new HashSet<Long>(); //如果购买的赠品种类数>促销返回的种类数，则抹掉多余的
        for (Long promotionId : promotionGiftGoodsMap.keySet()) {
            Long mzGiftNum = promotionGiftNumMap.get(promotionId);
            if(mzGiftNum == null){
                continue;
            }
            Set<Long> giftGoodsSet = promotionGiftGoodsMap.get(promotionId);
            int num = 0;
            for (Map.Entry<Long, GoodsNumberBean> entry : goodsCount.entrySet()) {
                if (entry.getValue().getGiftNumber() > 0) {// 是赠品
                    if (giftGoodsSet.contains(entry.getKey())) {
                        num++;
                        if(num > mzGiftNum){
                            removeGift.add(entry.getKey());
                        }
                    }
                }
            }
        }
        for (Long goodsId : removeGift) {
            goodsCount.remove(goodsId);
        }
        return true;
        
    }
    
    /**
     * 白名单（用户下单过快是否拦截）
     * 
     * @param buyerId
     * @return
     */
    
    public boolean ifCheckUserOrder(String buyerId) {
        // 不校验下单过快的用户ID
        List<String> buyerIds = new ArrayList<String>();
        buyerIds.add("176938");
        buyerIds.add("1511215");
        buyerIds.add("498655");
        
        if (buyerIds.contains(buyerId)) {
            return false;
        } else {
            return true;
        }
    }
}

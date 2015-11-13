package cn.tootoo.soa.oms.createorder;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import cn.tootoo.db.egrocery.texvoucher.TExVoucherDao;
import cn.tootoo.db.egrocery.texvoucher.TExVoucherPO;
import cn.tootoo.db.egrocery.texvoucherlot.TExVoucherLotDao;
import cn.tootoo.db.egrocery.texvoucherlot.TExVoucherLotPO;
import cn.tootoo.db.egrocery.tosparentorder.TOsParentOrderDao;
import cn.tootoo.enums.BaseResultEnum;
import cn.tootoo.enums.BooleanEnum;
import cn.tootoo.http.AuthorizeClient;
import cn.tootoo.http.TootooService;
import cn.tootoo.http.bean.BaseInputBean;
import cn.tootoo.http.bean.BaseOutputBean;
import cn.tootoo.soa.address.getaddressbyid.input.AddressGetAddressByIDInputData;
import cn.tootoo.soa.address.getaddressbyid.output.AddressGetAddressByIDOutputData;
import cn.tootoo.soa.base.bean.GoodsNumberBean;
import cn.tootoo.soa.base.bean.GoodsWarehouseAndDcBean;
import cn.tootoo.soa.base.bean.ShippingDateBean;
import cn.tootoo.soa.base.enums.BaseOrderResultEnum;
import cn.tootoo.soa.base.enums.OrderTypeEnum;
import cn.tootoo.soa.base.enums.ReturnOrderStatusEnum;
import cn.tootoo.soa.base.enums.SubstationEnum;
import cn.tootoo.soa.base.enums.TimeShipTypeEnum;
import cn.tootoo.soa.base.global.SpecialInfos;
import cn.tootoo.soa.base.util.OrderUtil;
import cn.tootoo.soa.goods.getgoodsinfo.output.GoodsGetGoodsInfoGoodsInfoElementO;
import cn.tootoo.soa.oms.createorder.input.OmsCreateOrderExListElementI;
import cn.tootoo.soa.oms.createorder.input.OmsCreateOrderInputData;
import cn.tootoo.soa.oms.createorder.output.OmsCreateOrderOutputData;
import cn.tootoo.soa.oms.utils.SqlUtil;
import cn.tootoo.soa.promotion.getpromotionbyid.output.PromotionGetPromotionByIdDetailElementO;
import cn.tootoo.soa.promotion.getpromotioninfo.input.PromotionGetPromotionInfoInputData;
import cn.tootoo.soa.promotion.getpromotioninfo.output.PromotionGetPromotionInfoDiscountListElementO;
import cn.tootoo.soa.promotion.getpromotioninfo.output.PromotionGetPromotionInfoOutputData;
import cn.tootoo.soa.shipping.getgoodsshippingfee.input.ShippingGetGoodsShippingFeeInputData;
import cn.tootoo.soa.shipping.getgoodsshippingfee.output.ShippingGetGoodsShippingFeeOutputData;
import cn.tootoo.soa.shipping.getgoodsshippinginfos.input.ShippingGetGoodsShippingInfosInputData;
import cn.tootoo.soa.shipping.getgoodsshippinginfos.output.ShippingGetGoodsShippingInfosOutputData;
import cn.tootoo.soa.shipping.getgoodsshippinginfos.output.ShippingGetGoodsShippingInfosResultEnum;
import cn.tootoo.soa.shipping.getgoodsshippinginfos.output.ShippingGetGoodsShippingInfosShippingRulesElementO;
import cn.tootoo.soa.stock.getsalestock.input.StockGetSaleStockInputData;
import cn.tootoo.soa.stock.getsalestock.output.StockGetSaleStockOutputData;
import cn.tootoo.utils.Log;
import cn.tootoo.utils.Memcached;
import cn.tootoo.utils.ResponseUtil;
import cn.tootoo.utils.StringUtil;

/**
 * 普通订单Service
 * @author tenbaoxin
 *
 */          
public class NormalCreateOrderService  extends CreateOrderBaseService{

	public NormalCreateOrderService(String uuid, Logger logger) {
		super(uuid, logger);
	}

	public  BaseOutputBean createNormalOrder(
			HttpServletRequest request,
			BaseInputBean inputBean,OmsCreateOrderInputData inputData ,
			BaseOutputBean outputBean,OmsCreateOrderOutputData outputData, 
			TOsParentOrderDao parentOrderDao, 
			Map<String, BigDecimal> discountMap,Map<String, BigDecimal> otherDiscountMap,
			
			Map<Long, PromotionGetPromotionByIdDetailElementO> groupMap, 
			HashMap<String, String>  params, HashMap<Long, GoodsNumberBean> goodsCount, 
			boolean canReserve, Map<Long, GoodsGetGoodsInfoGoodsInfoElementO> goodsInfo, 
			String isIncludeSpecial, String isCheckCOD, boolean isNeedGift, Long giftGoodsId,
			String couponSN, int giftGoodsNum, Long payMethodId, boolean haveOnlinePay, 
			Map<String, String> buyFromMap, 
			StringBuffer goodsIDMax, List<Long> redemptionList
			) throws Exception {

    	
   	 	AddressGetAddressByIDOutputData addressOutputData = null;
        if (!"1".equals(inputData.getReqFrom())) {// 手机端调用，按照以前的规则
            /****************************************************************************** 调用地址服务开始 ********************************************************************************************/
            Log.info(logger, uuid, "组装address服务getAddressByID方法所需参数开始");
            AddressGetAddressByIDInputData addressInputData = new AddressGetAddressByIDInputData();
            addressInputData.setScope(inputData.getScope());
            addressInputData.setShipAddrID(inputData.getReceiveAddrID());
            HashMap<String, String> addressServiceParams = (HashMap<String, String>)params.clone();
            addressServiceParams.put("method", "getAddressByID");
            addressServiceParams.put("req_str", addressInputData.toJson());
            Log.info(logger, uuid, "组装address服务getAddressByID方法所需参数结束");
            
            Log.info(logger, uuid, "调用address服务getAddressByID方法开始", "addressServiceParams", addressServiceParams);
            outputBean = TootooService.callServer("address", addressServiceParams, "post", new AddressGetAddressByIDOutputData());
            Log.info(logger, uuid, "调用address服务getAddressByID方法结束", "outputBean", outputBean);
            
            if (!TootooService.checkService(outputBean, "address", "getAddressByID", inputData.getScope())) {
                Log.info(logger, uuid, "调用address服务getAddressByID方法失败,接口返回", "outputBean", outputBean);
                return outputBean;
            }
            
            addressOutputData = (AddressGetAddressByIDOutputData)outputBean.getOutputData();
            /****************************************************************************** 调用地址服务结束 ********************************************************************************************/
        } else {
            if (inputData.getLastGeoId() == null) {
                Log.info(logger, uuid, "地址末节ID为空", "inputData", inputData);
                outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.CALLSERVER_ERROR.getResultID(), null, inputData.getScope());
                Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
                return outputBean;
            }
        }
        
        /****************************************************************************** 调用库存服务开始 ********************************************************************************************/
        Log.info(logger, uuid, "组装stock服务getSaleStock方法所需参数开始");
        StockGetSaleStockInputData stockInputData = OrderUtil.getSaleStockInputData(inputData.getOrderType(), inputData.getSubstationID(), goodsCount, goodsInfo);
        Map<String, String> stockServiceParams = new HashMap<String, String>();
        stockServiceParams.put("method", "getSaleStock");
        stockServiceParams.put("req_str", stockInputData.toJson());
        Log.info(logger, uuid, "组装stock服务getSaleStock方法所需参数结束");
        
        Log.info(logger, uuid, "调用stock服务getSaleStock方法开始", "stockServiceParams", stockServiceParams);
        outputBean = TootooService.callServer("stock", stockServiceParams, "post", new StockGetSaleStockOutputData());
        Log.info(logger, uuid, "调用stock服务getSaleStock方法结束", "outputBean", outputBean);
        
        if (!TootooService.checkService(outputBean, "stock", "getSaleStock", inputData.getScope())) {
            Log.info(logger, uuid, "调用stock服务getSaleStock方法失败,接口返回", "outputBean", outputBean);
            return outputBean;
        }
        
        StockGetSaleStockOutputData stockOutputData = (StockGetSaleStockOutputData)outputBean.getOutputData();
        
        Log.info(logger, uuid, "判断库存开始");
        // 保存验证错误时的错误商品
        Set<String> returnSetStock = new HashSet<String>();
        // 保存商品的可销售库存量
        Map<Long, Map<Long, BigDecimal>> goodsCanStock = new HashMap<Long, Map<Long, BigDecimal>>();
        Map<Long, Set<Long>> goodsStock = OrderUtil.checkStock(inputData.getOrderType(), stockOutputData, goodsCount, goodsCanStock, goodsInfo, returnSetStock);
        if (goodsStock == null) {
            Log.info(logger, uuid, "有商品库存不足", "goodsCount", goodsCount, "goodsList", returnSetStock);
            outputBean = ResponseUtil.getBaseOutputBean(BaseOrderResultEnum.STOCK_NOT_ENOUGH.getResultID(), null, inputData.getScope());
            Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
            return outputBean;
        }
        Log.info(logger, uuid, "判断库存结束", "goodsStock", goodsStock);
        /****************************************************************************** 调用库存服务结束 ********************************************************************************************/
        
        
        String timeShipType = TimeShipTypeEnum.NORMAL.getC();
        if (BooleanEnum.TRUE.getV().equals(inputData.getDeliveryTimeType())) {// 选择了当日达
            if ((OrderTypeEnum.NORMAL.getC().equals(inputData.getOrderType())
                            && OrderUtil.checkTime() && !canReserve)
                            && inputData.getReceiveDate() != null
                            && inputData.getSubstationID().intValue() == SubstationEnum.BJ.getC()) {// 网站普通订单且11点之前且没有预定商品
                timeShipType = TimeShipTypeEnum.TODAY.getC();
            } else {
                Log.info(logger, uuid, "不支持当日达");
                outputBean = ResponseUtil.getBaseOutputBean(BaseOrderResultEnum.TODAY_IS_INVALID.getResultID(), null, inputData.getScope());
                Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
                return outputBean;
            }
        }
        
        
        
        /****************************************************************************** 调用配送服务开始 ********************************************************************************************/
        Map<Long, Map<Long, List<ShippingGetGoodsShippingInfosShippingRulesElementO>>> goodsWarehouseRule = new HashMap<Long, Map<Long, List<ShippingGetGoodsShippingInfosShippingRulesElementO>>>();
        Object o = this.callShippingService(goodsWarehouseRule, false, inputData, addressOutputData, goodsCount, goodsInfo, timeShipType, goodsStock);
        if (o instanceof BaseOrderResultEnum) {
            BaseOrderResultEnum transferObject = (BaseOrderResultEnum)o;
            outputBean = ResponseUtil.getBaseOutputBean(transferObject.getResultID(), null, inputData.getScope());
            Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
            return outputBean;
        }
        if (o instanceof BaseOutputBean) {
            BaseOutputBean transferObject = (BaseOutputBean)o;
            return transferObject;
        }
        if (o instanceof HashSet) {
            HashSet<ShippingDateBean> transferObject = (HashSet<ShippingDateBean>)o;
            if (transferObject == null || transferObject.isEmpty()) {
                Log.info(logger, uuid, "有商品不可同日达");
                outputBean = ResponseUtil.getBaseOutputBean(BaseOrderResultEnum.NOT_SAMETIME_SHIP.getResultID(), null, inputData.getScope());
                Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
                return outputBean;
            }
        }
        /****************************************************************************** 调用配送服务结束 ********************************************************************************************/
        Set<ShippingDateBean> allDates = new HashSet<ShippingDateBean>();
        /****************************************************************************** 调用配送服务开始(运力) ********************************************************************************************/
        o = this.callShippingService(goodsWarehouseRule, true, inputData, addressOutputData, goodsCount, goodsInfo, timeShipType, goodsStock);
        if (o instanceof BaseOrderResultEnum) {
            BaseOrderResultEnum transferObject = (BaseOrderResultEnum)o;
            outputBean = ResponseUtil.getBaseOutputBean(transferObject.getResultID(), null, inputData.getScope());
            Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
            return outputBean;
        }
        if (o instanceof BaseOutputBean) {
            BaseOutputBean transferObject = (BaseOutputBean)o;
            return transferObject;
        }
        if (o instanceof HashSet) {
            HashSet<ShippingDateBean> transferObject = (HashSet<ShippingDateBean>)o;
            if (transferObject == null || transferObject.isEmpty()) {
                Log.info(logger, uuid, "无运力");
                outputBean = ResponseUtil.getBaseOutputBean(BaseOrderResultEnum.NOT_SHIP_POWER.getResultID(), null, inputData.getScope());
                Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
                return outputBean;
            }
            allDates = transferObject;
        }
        /****************************************************************************** 调用配送服务结束(运力) ********************************************************************************************/
        
        
        
 	   
        String receiveDate = "";
        boolean isTuangou = false;
        Log.info(logger, uuid, "确定收货日期开始");
        receiveDate = OrderUtil.getReceiveDate(inputData.getSubstationID(), isIncludeSpecial, inputData.getReceiveDate(), isCheckCOD, allDates, logger, uuid, outputBean, inputData.getScope());
        if (receiveDate == null) {
            Log.info(logger, uuid, "不支持COD方式或选定的日期非法,接口返回", "outputBean", outputBean);
            return outputBean;
        }
        Log.info(logger, uuid, "确定收货日期结束", "receiveDate", receiveDate);
        if (BooleanEnum.TRUE.getV().equals(inputData.getDeliveryTimeType())) {// 选择了当日达
            for (ShippingDateBean dateBean : allDates) {
                if (dateBean.getShipDate().equals(receiveDate)) {
                    if (!BooleanEnum.TRUE.getV().equals(dateBean.getIsSpecifiedDate())) {// 本日期不可指定
                        Log.info(logger, uuid, "不支持当日达,当日达订单必须是可指定日期的");
                        outputBean = ResponseUtil.getBaseOutputBean(BaseOrderResultEnum.TODAY_IS_INVALID.getResultID(), null, inputData.getScope());
                        Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
                        return outputBean;
                    }
                }
            }
        }
        outputData.setSendDate(receiveDate);

        Map<Long, Set<GoodsWarehouseAndDcBean>> goodsWarehouseDc = OrderUtil.getWarehouseAndDcList(isTuangou, goodsWarehouseRule, isCheckCOD, receiveDate);
        Log.info(logger, uuid, "根据确定的日期和是否选择了COD,找出所有的库房和配送公司组合", "goodsWarehouseDc", goodsWarehouseDc);
        
        Map<Long, GoodsWarehouseAndDcBean> goodsWarehouseAndDcBean = OrderUtil.getWarehouseAndDc(goodsWarehouseDc);
        Log.info(logger, uuid, "按照最少拆单原则,指定商品的库房和配送公司开始", "goodsWarehouseAndDcBean", goodsWarehouseAndDcBean);
        
        // 商品在本库房本配送公司下，是否考虑运力
        Map<Long, String> goodsShipCapacityMap = new HashMap<Long, String>();
        for (Map.Entry<Long, GoodsWarehouseAndDcBean> entry : goodsWarehouseAndDcBean.entrySet()) {
            for (ShippingGetGoodsShippingInfosShippingRulesElementO ruleElement : goodsWarehouseRule.get(entry.getKey()).get(entry.getValue().getWarehouseID())) {
                if (entry.getValue().getDC().intValue() == ruleElement.getShippingCompanyID().intValue()) {
                    goodsShipCapacityMap.put(entry.getKey(), ruleElement.getConsiderShippingCapacity());
                    break;
                }
            }
        }

        /****************************************************************************** 调用促销服务开始 ********************************************************************************************/
        int res = -3;
        
        PromotionGetPromotionInfoOutputData promotionOutputData = null;
        if (OrderTypeEnum.NORMAL.getC().equals(inputData.getOrderType())) {
	        Log.info(logger, uuid, "验证换购合法性(换购，包括促销时间、状态、适用范围)开始");
	        Set<String> invalidGood = new HashSet<String>();
	        if (!OrderUtil.checkRedemption(inputData.getScope(), goodsInfo, redemptionList, invalidGood)) {
	            Log.info(logger, uuid, "换购非法", "invalidGood", invalidGood);
	            outputBean = ResponseUtil.getBaseOutputBean(BaseOrderResultEnum.REDEMPTION_INVALID.getResultID(), null, inputData.getScope());
	            Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
	            return outputBean;
	        }
	        Log.info(logger, uuid, "验证换购合法性(换购，包括促销时间、状态、适用范围)结束");
	        
	        Log.info(logger, uuid, "组装promotion服务getPromotionInfo方法所需参数开始");
	        PromotionGetPromotionInfoInputData promotionInputData = getPromotionInputData("addorder", inputData, goodsInfo, params);
	        Map<String, String> promotionServiceParams = new HashMap<String, String>();
	        promotionServiceParams.put("r", "tCalculate");
	        promotionServiceParams.put("req_str", promotionInputData.toJson());
	        Log.info(logger, uuid, "组装promotion服务getPromotionInfo方法所需参数结束");
	        
	        Log.info(logger, uuid, "调用promotion服务getPromotionInfo方法开始", "promotionServiceParams", promotionServiceParams);
	        outputBean = TootooService.callServer("promotion", promotionServiceParams, "post", new PromotionGetPromotionInfoOutputData());
	        Log.info(logger, uuid, "调用promotion服务getPromotionInfo方法结束", "outputBean", outputBean);
	        
	        if (!TootooService.checkService(outputBean, "promotion", "getPromotionInfo", inputData.getScope())) {
	            Log.info(logger, uuid, "调用promotion服务getPromotionInfo方法失败,接口返回", "outputBean", outputBean);
	            return outputBean;
	        }
	        
	        promotionOutputData = (PromotionGetPromotionInfoOutputData)outputBean.getOutputData();
	        
	        Log.info(logger, uuid, "判断促销开始");
	        //新增秒抢商品只能购买一件校验  
	        res = SqlUtil.checkPromotionInfoGoodsList(params.get(AuthorizeClient.COOKIE_BUYER_ID), logger, uuid, parentOrderDao.getReadConnectionName(), promotionOutputData.getGoodsList());
	        if (res == -1) {
	            outputBean = ResponseUtil.getBaseOutputBean(170064, null, inputData.getScope());
	            Log.info(logger, uuid, "订单中含有秒抢商品，十分抱歉，您已经参与过秒抢活动，不能重复参与!", "outputBean", outputBean);
	            return outputBean;
	        }
	        if (res == -2) {
	            outputBean = ResponseUtil.getBaseOutputBean(170063, null, inputData.getScope());
	            Log.info(logger, uuid, "十分抱歉，秒抢商品每位用户限购1件!", "outputBean", outputBean);
	            return outputBean;
	        }
	        
	        StringBuffer returnMsg = new StringBuffer("");
	        if (!OrderUtil.checkPromotion(promotionOutputData, goodsInfo, returnMsg, logger, uuid)) {
	            outputBean = ResponseUtil.getBaseOutputBean(BaseOrderResultEnum.PROMOTION_INVALID.getResultID(), null, inputData.getScope());
	            Log.info(logger, uuid, "促销不合法,接口返回", "outputBean", outputBean);
	            return outputBean;
	        }
	        Log.info(logger, uuid, "判断促销结束");
	        
	        Map<String, BigDecimal>[] map = getDiscountFee(promotionOutputData);
	        discountMap = map[0];
	        otherDiscountMap = map[1];
	        Log.info(logger, uuid, "获得满减的信息", "discountMap", discountMap, "otherDiscountMap", otherDiscountMap);
        }
        /****************************************************************************** 调用促销服务结束 ********************************************************************************************/
       
        BigDecimal orderFee = getOrderFee(inputData, promotionOutputData, goodsCount, goodsInfo, groupMap);
        Log.info(logger, uuid, "获得订单的订单金额", "orderFee", orderFee);
        
        boolean isGiftOK = false;
        if (isNeedGift) {// 市场部活动需要赠送商品
            if (orderFee.compareTo(SpecialInfos.specialCouponMap.get(couponSN).getAmount()) < 0) {// 商品总价(不包括运费)没有超过活动限额,把赠品中的此赠品删除
                Log.info(logger, uuid, "商品总价不够活动限额", "goodsCount", goodsCount);
                if (goodsCount.containsKey(giftGoodsId)) {
                    if (goodsCount.get(giftGoodsId).getGiftNumber() > 0
                                    && goodsCount.get(giftGoodsId).getGiftNumber().intValue() == giftGoodsNum + 1) {
                        
                        goodsCount.get(giftGoodsId).setGiftNumber(goodsCount.get(giftGoodsId).getGiftNumber() - 1);
                        if (goodsCount.get(giftGoodsId).getGiftNumber() < 0) {
                            goodsCount.get(giftGoodsId).setGiftNumber(0L);
                        }
                        if (goodsCount.get(giftGoodsId).getBuyNumber() <= 0
                                        && goodsCount.get(giftGoodsId).getGiftNumber() <= 0) {
                            Log.info(logger, uuid, "测试信息4", "goodsCount", goodsCount);
                            goodsCount.remove(giftGoodsId);
                        }
                    }
                }
            } else {
                Log.info(logger, uuid, "测试信息5", "goodsCount", goodsCount);
                if (goodsCount.containsKey(giftGoodsId)
                                && goodsCount.get(giftGoodsId).getGiftNumber() > 0) {
                    Log.info(logger, uuid, "测试信息6", "goodsCount", goodsCount);
                    if (goodsCount.get(giftGoodsId).getGiftNumber().intValue() == giftGoodsNum + 1) {
                        Log.info(logger, uuid, "测试信息7", "goodsCount", goodsCount);
                        isGiftOK = true;
                    }
                }
            }
        }

        if (OrderTypeEnum.NORMAL.getC().equals(inputData.getOrderType())) {// 普通订单
	        Log.info(logger, uuid, "获得实际合法的赠品开始", "promotionOutputData", promotionOutputData, "goodsCount", goodsCount);
	        if (!setActualGift(promotionOutputData, goodsCount, isGiftOK, giftGoodsId)) {
	            outputBean = ResponseUtil.getBaseOutputBean(BaseOrderResultEnum.PROMOTION_INVALID.getResultID(), null, inputData.getScope());
	            Log.info(logger, uuid, "赠品不合法,接口返回", "outputBean", outputBean);
	            return outputBean;
	        }
	        Log.info(logger, uuid, "获得实际合法的赠品结束", "goodsCount", goodsCount);
        }
        BigDecimal shipFee = BigDecimal.ZERO;
        BigDecimal shipTotalFee = BigDecimal.ZERO;
        BigDecimal deliveryTimeFee = BigDecimal.ZERO;

        /****************************************************************************** 调用运费服务开始 ********************************************************************************************/
        //==============添加优惠券、兑换券包邮判断===开始==20151013=============
        String couponType = "1";
        //计算discountfee
        BigDecimal orderdiscountFee = BigDecimal.ZERO;
        if (promotionOutputData != null
                        && !promotionOutputData.getDiscountList().isEmpty()) {
            for (PromotionGetPromotionInfoDiscountListElementO discountElement : promotionOutputData.getDiscountList()) {
            	orderdiscountFee = orderdiscountFee.add(discountElement.getDiscount());
            }
        }
        //因优惠券与兑换券不能共存   
        if (inputData.getExList() != null && inputData.getExList().size() >= 1) {
        	couponType="2";
        	OmsCreateOrderExListElementI exElementI = inputData.getExList().get(0); 	
        	couponSN = exElementI.getExSn();
        }
        if(StringUtil.isEmpty(couponSN)){
        	couponType = "";
        }
        
      //==============添加优惠券、兑换券包邮判断===结束==20151013=============
        Log.info(logger, uuid, "组装shipping服务getGoodsShippingFee方法所需参数开始");
        ShippingGetGoodsShippingFeeInputData shipFeeInputData = getGoodsItemsShipfee(couponType,couponSN,orderdiscountFee,isGiftOK, isNeedGift?SpecialInfos.specialCouponMap.get(couponSN).getGoodsId():null, inputData.getScope(), inputData.getOrderType(), timeShipType, inputData.getSubstationID(), ("1".equals(inputData.getReqFrom())?inputData.getLastGeoId():addressOutputData.getLastGeoID()), orderFee, goodsCount, goodsInfo);
        Map<String, String> shipFeeServiceParams = new HashMap<String, String>();
        shipFeeServiceParams.put("method", "getGoodsShippingFee");
        shipFeeServiceParams.put("req_str", shipFeeInputData.toJson());
        Log.info(logger, uuid, "组装shipping服务getGoodsShippingFee方法所需参数结束");
        
        Log.info(logger, uuid, "调用shipping服务getGoodsShippingFee方法开始", "shipFeeServiceParams", shipFeeServiceParams);
        outputBean = TootooService.callServer("shipping", shipFeeServiceParams, "post", new ShippingGetGoodsShippingFeeOutputData());
        Log.info(logger, uuid, "调用shipping服务getGoodsShippingFee方法结束", "outputBean", outputBean);
        
        if (!TootooService.checkService(outputBean, "shipping", "getGoodsShippingFee", inputData.getScope())) {
            Log.info(logger, uuid, "调用shipping服务getGoodsShippingFee方法失败,接口返回", "outputBean", outputBean);
            return outputBean;
        }
        ShippingGetGoodsShippingFeeOutputData shipFeeOutputData = (ShippingGetGoodsShippingFeeOutputData)outputBean.getOutputData();
        shipFee = shipFee.add(shipFeeOutputData.getDiscountShippingFee()==null?BigDecimal.ZERO:shipFeeOutputData.getDiscountShippingFee());
        shipTotalFee = shipTotalFee.add(shipFeeOutputData.getTotalShippingFee()==null?BigDecimal.ZERO:shipFeeOutputData.getTotalShippingFee());
        deliveryTimeFee = deliveryTimeFee.add(shipFeeOutputData.getTimeShipServiceFee()==null?BigDecimal.ZERO:shipFeeOutputData.getTimeShipServiceFee());
        
        
//        // 查询兑换券包邮属性
//        boolean isExBaoYou = false;
//        if (inputData.getExList() != null && inputData.getExList().size() >= 1) {
//            TExVoucherDao tExVoucherDao = new TExVoucherDao(uuid, logger);
//            for (OmsCreateOrderExListElementI exElementI : inputData.getExList()) {
//                List<Object[]> exConditions = new ArrayList<Object[]>();
//                exConditions.add(new Object[]{"VOUCHER_SN", "=", exElementI.getExSn()});
//                List<TExVoucherPO> tExVoucherList = tExVoucherDao.findTExVoucherPOListByCondition(exConditions, false, false, true);
//                Log.info(logger, uuid, "查询兑换券信息", "tExVoucherList", tExVoucherList);
//                if (tExVoucherList != null && tExVoucherList.size() == 1) {
//                    TExVoucherLotDao tExVoucherLotDao = new TExVoucherLotDao(uuid, logger);
//                    List<Object[]> exLotConditions = new ArrayList<Object[]>();
//                    exLotConditions.add(new Object[]{"LOT_SN", "=", tExVoucherList.get(0).getLotSn()});
//                    List<TExVoucherLotPO> tExtVoucherLotList = tExVoucherLotDao.findTExVoucherLotPOListByCondition(exLotConditions, false, false, true);
//                    Log.info(logger, uuid, "查询兑换券包邮信息", "tExtVoucherLotList", tExtVoucherLotList);
//                    if (tExtVoucherLotList != null && tExtVoucherLotList.size() == 1 && "1".equals(tExtVoucherLotList.get(0).getIsBaoyou())) {
//                        isExBaoYou = true;
//                    }
//                }
//            }
//        }
//        if (isExBaoYou){
//            shipFee = BigDecimal.ZERO;
//            shipTotalFee = BigDecimal.ZERO;
//        }
        
        
        /****************************************************************************** 调用运费服务结束 ********************************************************************************************/
        BaseOutputBean outputBeanNomal = createOrder(request,inputBean, inputData, outputBean, outputData, params, goodsCount, groupMap, goodsInfo, buyFromMap, goodsWarehouseAndDcBean, goodsShipCapacityMap, discountMap, otherDiscountMap, addressOutputData, promotionOutputData, parentOrderDao, new StringBuffer(""), goodsIDMax, isIncludeSpecial, isCheckCOD, couponSN, receiveDate, BigDecimal.ZERO, shipFee, shipTotalFee, deliveryTimeFee, BigDecimal.ZERO,payMethodId, canReserve, haveOnlinePay, false,null,null);
        
        if(res==1){
        	Memcached.addFlushBuy("flush"+params.get(AuthorizeClient.COOKIE_BUYER_ID));
        }
        return outputBeanNomal;
        
        
        
        
	}
	
	
	private Object callShippingService(Map<Long, Map<Long, List<ShippingGetGoodsShippingInfosShippingRulesElementO>>> goodsWarehouseRule, boolean isPower, OmsCreateOrderInputData inputData, AddressGetAddressByIDOutputData addressOutputData, HashMap<Long, GoodsNumberBean> goodsCount, Map<Long, GoodsGetGoodsInfoGoodsInfoElementO> goodsInfo, String timeShipType, Map<Long, Set<Long>> goodsStock) throws Exception{
		//****************判断选择的地址是否在兑换券区域中******开始****
    	String couponSN="";
        if (inputData.getExList() != null && inputData.getExList().size() >= 1) {
        	couponSN= inputData.getExList().get(0).getExSn(); 	
        }
      //****************判断选择的地址是否在兑换券区域中******结束****
        
	    
	    Log.info(logger, uuid, "组装shipping服务getGoodsShippingInfos方法所需参数开始");
        ShippingGetGoodsShippingInfosInputData getGoodsShippingInfosInputData = getGoodsShippingInfosInputData(couponSN,isPower, inputData.getScope(), inputData.getOrderType(), inputData.getOrderFrom(), inputData.getSubstationID(), 0L, ("1".equals(inputData.getReqFrom())?inputData.getLastGeoId():addressOutputData.getLastGeoID()), goodsCount, goodsInfo, timeShipType);  
        
        if (getGoodsShippingInfosInputData == null) {// 不可同天送
            Log.info(logger, uuid, "有商品不可同日达或者不支持当日达");
            return BaseOrderResultEnum.NOT_SAMETIME_SHIP;
        }
        
        Map<String, String> getGoodsShippingInfosServiceParams = new HashMap<String, String>();
        getGoodsShippingInfosServiceParams.put("method", "getGoodsShippingInfos");
        getGoodsShippingInfosServiceParams.put("req_str", getGoodsShippingInfosInputData.toJson());
        Log.info(logger, uuid, "组装shipping服务getGoodsShippingInfos方法所需参数结束");
        
        Log.info(logger, uuid, "调用shipping服务getGoodsShippingInfos方法开始", "getGoodsShippingInfosServiceParams", getGoodsShippingInfosServiceParams);
        BaseOutputBean outputBean = TootooService.callServer("shipping", getGoodsShippingInfosServiceParams, "post", new ShippingGetGoodsShippingInfosOutputData());
        Log.info(logger, uuid, "调用shipping服务getGoodsShippingInfos方法结束", "outputBean", outputBean);
        
        if (!TootooService.checkService(outputBean, "shipping", "getGoodsShippingInfos", inputData.getScope())) {
        	if(outputBean.getOutputHead().getResultID()== ShippingGetGoodsShippingInfosResultEnum.NOT_IN_COUPON.getResultID()){
                outputBean = ResponseUtil.getBaseOutputBean(ShippingGetGoodsShippingInfosResultEnum.NOT_IN_COUPON.getResultID(), null, inputData.getScope());
        		Log.info(logger, uuid, "您当前的收货区域未在兑换券福利覆盖范围之内，您可以修改收货地址或返回购物车页取消兑换！","inputData",inputData);
                return outputBean;
        	}
            Log.info(logger, uuid, "调用shipping服务getGoodsShippingInfos方法失败,接口返回", "outputBean", outputBean);
            return outputBean;
        }
        ShippingGetGoodsShippingInfosOutputData shippingOutputData = (ShippingGetGoodsShippingInfosOutputData)outputBean.getOutputData();
        
        Log.info(logger, uuid, "判断配送开始");
        // 保存验证错误时的错误商品
        Set<String> returnSetShipping = new HashSet<String>();
        // 商品库房配送规则
        
        // 商品库房配送日期
        Map<Long, Map<Long, Set<ShippingDateBean>>> goodsWarehouseDate = OrderUtil.checkShipping(false, ("1".equals(inputData.getReqFrom())?inputData.getLastGeoId():addressOutputData.getLastGeoID()), goodsStock, shippingOutputData, goodsCount, returnSetShipping, goodsInfo, goodsWarehouseRule);
        if (goodsWarehouseDate == null) {
            Log.info(logger, uuid, "有商品不可送达", "goodsList", returnSetShipping);
            return BaseOrderResultEnum.NOT_SHIP;
        }
        Log.info(logger, uuid, "判断配送结束", "goodsWarehouseDate", goodsWarehouseDate);
        
        Log.info(logger, uuid, "获得所有商品的可配送日期开始");
        Set<ShippingDateBean> allDates = OrderUtil.getAllGoodsDates(goodsWarehouseDate, logger, uuid);
        Log.info(logger, uuid, "获得所有商品的可配送日期结束", "allDates", allDates);
        
        if (BooleanEnum.TRUE.getV().equals(inputData.getDeliveryTimeType())) {// 选择了当日达
            if (!allDates.contains(new ShippingDateBean(new SimpleDateFormat("yyyy-MM-dd").format(new Date()), "", "", ""))) {
                Log.info(logger, uuid, "不支持当日达");
                return BaseOrderResultEnum.TODAY_IS_INVALID;
            }
        } else {
            allDates.remove(new ShippingDateBean(new SimpleDateFormat("yyyy-MM-dd").format(new Date()), "", "", ""));
        }

        return allDates;
	}
	
	public boolean checkExvocherEnable(AddressGetAddressByIDOutputData addressOutputData,String voucherSn){
		if(addressOutputData == null){
			return false;
		}
		String privince = String.valueOf(addressOutputData.getProvince());
    	TExVoucherDao tExVoucherDao = new TExVoucherDao();
		List<Object[]> exConditions = new ArrayList<Object[]>();
        exConditions.add(new Object[]{"VOUCHER_SN", "=", voucherSn});
        List<TExVoucherPO> tExVoucherList = tExVoucherDao.findTExVoucherPOListByCondition(exConditions, false, false, true);
        Log.info(logger, uuid, "查询兑换券信息", "tExVoucherList", tExVoucherList);
        if (tExVoucherList != null && tExVoucherList.size() == 1) {
            TExVoucherLotDao tExVoucherLotDao = new TExVoucherLotDao();
            List<Object[]> exLotConditions = new ArrayList<Object[]>();
            exLotConditions.add(new Object[]{"LOT_SN", "=", tExVoucherList.get(0).getLotSn()});
            List<TExVoucherLotPO> tExtVoucherLotList = tExVoucherLotDao.findTExVoucherLotPOListByCondition(exLotConditions, false, false, true);
            Log.info(logger, uuid, "查询兑换券包邮信息", "tExtVoucherLotList", tExtVoucherLotList);
            if (tExtVoucherLotList != null && tExtVoucherLotList.size() == 1) {
            	String receiverAddr = tExtVoucherLotList.get(0).getReceiverAddr();
            	if(!StringUtil.isEmpty(receiverAddr) && equalStr(receiverAddr,privince)){
            		return true;
            	}
            }
        }
        return false;
	}
	
	 public static boolean equalStr(String OrStr,String findStr){
	    	String[] arry =  OrStr.split(",");
	    	for (String s : arry) {
				if(s.equals(String.valueOf(findStr))){
					return true;
				}
			}
	    	return false;
	    }
}

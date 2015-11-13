package cn.tootoo.soa.oms.createorder;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import cn.tootoo.db.egrocery.tosparentorder.TOsParentOrderDao;
import cn.tootoo.db.egrocery.ttsmultistepprice.TtsMultiStepPriceDao;
import cn.tootoo.db.egrocery.ttsmultistepprice.TtsMultiStepPricePO;
import cn.tootoo.db.egrocery.ttsprivateactivity.TtsPrivateActivityDao;
import cn.tootoo.db.egrocery.ttsprivateactivity.TtsPrivateActivityPO;
import cn.tootoo.db.egrocery.ttsprivateteam.TtsPrivateTeamDao;
import cn.tootoo.db.egrocery.ttsprivateteam.TtsPrivateTeamPO;
import cn.tootoo.db.egrocery.ttsprivateteamjoin.TtsPrivateTeamJoinDao;
import cn.tootoo.db.egrocery.ttsprivateteamjoin.TtsPrivateTeamJoinPO;
import cn.tootoo.db.egrocery.ttsteamgoodsrelations.TtsTeamGoodsRelationsDao;
import cn.tootoo.db.egrocery.ttsteamgoodsrelations.TtsTeamGoodsRelationsPO;
import cn.tootoo.enums.BaseResultEnum;
import cn.tootoo.enums.BooleanEnum;
import cn.tootoo.http.AuthorizeClient;
import cn.tootoo.http.TootooService;
import cn.tootoo.http.bean.BaseInputBean;
import cn.tootoo.http.bean.BaseOutputBean;
import cn.tootoo.soa.address.getaddressbyid.input.AddressGetAddressByIDInputData;
import cn.tootoo.soa.address.getaddressbyid.output.AddressGetAddressByIDOutputData;
import cn.tootoo.soa.base.bean.GiftItemsBean;
import cn.tootoo.soa.base.bean.GoodsNumberBean;
import cn.tootoo.soa.base.bean.GoodsWarehouseAndDcBean;
import cn.tootoo.soa.base.bean.ShippingDateBean;
import cn.tootoo.soa.base.enums.BaseOrderResultEnum;
import cn.tootoo.soa.base.enums.PromotionStatusEnum;
import cn.tootoo.soa.base.enums.SubstationEnum;
import cn.tootoo.soa.base.enums.TimeShipTypeEnum;
import cn.tootoo.soa.base.global.SpecialInfos;
import cn.tootoo.soa.base.util.OrderUtil;
import cn.tootoo.soa.base.util.PromotionScopeUtil;
import cn.tootoo.soa.goods.getgoodsinfo.input.GoodsGetGoodsInfoGoodsListElementI;
import cn.tootoo.soa.goods.getgoodsinfo.input.GoodsGetGoodsInfoInputData;
import cn.tootoo.soa.goods.getgoodsinfo.output.GoodsGetGoodsInfoGoodsInfoElementO;
import cn.tootoo.soa.goods.getgoodsinfo.output.GoodsGetGoodsInfoOutputData;
import cn.tootoo.soa.goods.getgoodsprops.input.GoodsGetGoodsPropsGoodsListElementI;
import cn.tootoo.soa.goods.getgoodsprops.input.GoodsGetGoodsPropsInputData;
import cn.tootoo.soa.goods.getgoodsprops.output.GoodsGetGoodsPropsGiftItemsElementO;
import cn.tootoo.soa.goods.getgoodsprops.output.GoodsGetGoodsPropsGoodsListElementO;
import cn.tootoo.soa.goods.getgoodsprops.output.GoodsGetGoodsPropsOutputData;
import cn.tootoo.soa.oms.createorder.input.OmsCreateOrderGroupBuyingGoodsListElementI;
import cn.tootoo.soa.oms.createorder.input.OmsCreateOrderInputData;
import cn.tootoo.soa.oms.createorder.output.OmsCreateOrderOutputData;
import cn.tootoo.soa.promotion.getpromotionbyid.input.PromotionGetPromotionByIdInputData;
import cn.tootoo.soa.promotion.getpromotionbyid.input.PromotionGetPromotionByIdPromotionIdListElementI;
import cn.tootoo.soa.promotion.getpromotionbyid.output.PromotionGetPromotionByIdDetailElementO;
import cn.tootoo.soa.promotion.getpromotionbyid.output.PromotionGetPromotionByIdOutputData;
import cn.tootoo.soa.promotion.getpromotionbyid.output.PromotionGetPromotionByIdPromotionListElementO;
import cn.tootoo.soa.shipping.getgoodsshippinginfos.input.ShippingGetGoodsShippingInfosInputData;
import cn.tootoo.soa.shipping.getgoodsshippinginfos.output.ShippingGetGoodsShippingInfosOutputData;
import cn.tootoo.soa.shipping.getgoodsshippinginfos.output.ShippingGetGoodsShippingInfosShippingRulesElementO;
import cn.tootoo.soa.stock.getsalestock.input.StockGetSaleStockInputData;
import cn.tootoo.soa.stock.getsalestock.output.StockGetSaleStockOutputData;
import cn.tootoo.utils.DateUtil;
import cn.tootoo.utils.Log;
import cn.tootoo.utils.ResponseUtil;
import cn.tootoo.utils.StringUtil;

/**
 * 私享团订单Service
 * @author tenbaoxin
 *
 */ 
public class PrivateTeamCreateOrderService  extends CreateOrderBaseService {


	public PrivateTeamCreateOrderService(String uuid, Logger logger) {
		super(uuid, logger);
	}
	
	public BaseOutputBean createPrivateTeamOrder(
	                Set<Long> payId,
			HttpServletRequest request,
			BaseInputBean inputBean,OmsCreateOrderInputData inputData ,
			BaseOutputBean outputBean,OmsCreateOrderOutputData outputData, 
			TOsParentOrderDao parentOrderDao, 
			HashMap<String, String>  params, HashMap<Long, GoodsNumberBean> goodsCount,
		    String isCheckCOD, boolean isNeedGift, Long giftGoodsId,
			String couponSN, int giftGoodsNum, Long payMethodId, boolean haveOnlinePay, 
			Map<String, String> buyFromMap, 
			StringBuffer goodsIDMax,
			Map<Long, Long> buyMap,
			 Set<Long> allGoodsID,
			 List<GoodsGetGoodsPropsGoodsListElementO> goodsPropsGoodsList
			) throws Exception {
		
		String getSubstationGoodsResult = getSubstationGoods(inputData.getSubstationID(), goodsPropsGoodsList, inputData);
		if("-1".equals(getSubstationGoodsResult)){
			Log.info(logger, uuid, "商品分站转换错误", "orderType", inputData.getOrderType(), "goodsCount", goodsCount);
	        outputBean = ResponseUtil.getBaseOutputBean(BaseOrderResultEnum.PRIVATE_TEAM_GOODSLIST_INVALID.getResultID(), null, inputData.getScope());
	        Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
	        return outputBean;
		}
        if("1".equals(getSubstationGoodsResult)){
	        /****************************************************************************** 调用商品服务开始 ********************************************************************************************/
	        Log.info(logger, uuid, "根据传入的商品列表、N元Y件列表、赠品列表，获得商品的map,goodCount<Long,buyNumber-giftNumber>");
	        List<Long> redemptionList = new ArrayList<Long>();// 保存所有换购的商品
	        goodsIDMax = new StringBuffer("");// 除NY的所有goodsID的最大值
	        Set<Long> allGoodsIDNotGift = new HashSet<Long>();// 所有的goodsID，包括NY内部的goodsID(不包括NY的ID),但不包括礼盒内部商品(包括礼盒本身的ID)
	        Map<Long, Map<Long, Long>> giftMap = new HashMap<Long, Map<Long, Long>>(); // 保存赠品Map<goodsID,Map<promotionID,
	        buyFromMap = new HashMap<String, String>();// 保存商品的buyfrom
	        buyMap = new HashMap<Long, Long>();
	        goodsCount = (HashMap)getGoodsCount(buyMap,inputData, redemptionList, goodsIDMax, allGoodsIDNotGift, giftMap, buyFromMap);
	        
	        if (goodsCount == null || goodsCount.isEmpty()) {
	            Log.info(logger, uuid, "商品列表为空或者订单类型错误", "orderType", inputData.getOrderType(), "goodsCount", goodsCount);
	            outputBean = ResponseUtil.getBaseOutputBean(BaseOrderResultEnum.GOODSlIST_INVALID.getResultID(), null, inputData.getScope());
	            Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
	            return outputBean;
	        }
	        giftGoodsNum = 0;
	        if (isNeedGift) {// 市场部活动需要赠送商品
	            if (goodsCount.containsKey(giftGoodsId)) {
	                giftGoodsNum = goodsCount.get(giftGoodsId).getGiftNumber().intValue();
	                goodsCount.get(giftGoodsId).setGiftNumber(goodsCount.get(giftGoodsId).getGiftNumber() + 1);
	            } else {
	                goodsCount.put(giftGoodsId, new GoodsNumberBean(0L, 1L, 0L, "", null, null, "", new ArrayList<GiftItemsBean>()));
	            }
	            allGoodsIDNotGift.add(giftGoodsId);
	        }
	        
	        Log.info(logger, uuid, "获得商品的map,goodCount<Long,buyNumber-giftNumber>结束", "goodsCount", goodsCount);
	        Log.info(logger, uuid, "获得除NY的所有goodsID的最大值", "goodsIDMax", goodsIDMax);
	        Log.info(logger, uuid, "获得所有的goodsID(包括NY内部的goodsID,不包括礼盒内部商品)", "allGoodsIDNotGift", allGoodsIDNotGift);
	        Log.info(logger, uuid, "保存赠品Map<goodsID,Map<promotionID, giftNum>>", "giftMap", giftMap);
	        
	        Log.info(logger, uuid, "组装goods服务getGoodsProps方法所需参数开始");
	        GoodsGetGoodsPropsInputData getGoodsPropsInputData = new GoodsGetGoodsPropsInputData();
	        for (Long goodsID : allGoodsIDNotGift) {
	            GoodsGetGoodsPropsGoodsListElementI goodsServiceGoodsListElement = new GoodsGetGoodsPropsGoodsListElementI();
	            goodsServiceGoodsListElement.setCallType(0);
	            goodsServiceGoodsListElement.setGoodsID(goodsID);
	            getGoodsPropsInputData.getGoodsList().add(goodsServiceGoodsListElement);
	        }
	        Map<String, String> getGoodsPropsServiceParams = new HashMap<String, String>();
	        getGoodsPropsServiceParams.put("method", "getGoodsProps");
	        getGoodsPropsServiceParams.put("req_str", getGoodsPropsInputData.toJson());
	        Log.info(logger, uuid, "组装goods服务getGoodsProps方法所需参数结束");
	        
	        Log.info(logger, uuid, "调用goods服务getGoodsProps方法开始", "getGoodsPropsServiceParams", getGoodsPropsServiceParams);
	        outputBean = TootooService.callServer("goods", getGoodsPropsServiceParams, "post", new GoodsGetGoodsPropsOutputData());
	        Log.info(logger, uuid, "调用goods服务getGoodsProps方法结束", "outputBean", outputBean);
	        
	        if (!TootooService.checkService(outputBean, "goods", "getGoodsProps", inputData.getScope())) {
	            Log.info(logger, uuid, "调用goods服务getGoodsProps方法失败,接口返回", "outputBean", outputBean);
	            return outputBean;
	        }
	        GoodsGetGoodsPropsOutputData getGoodsPropsOutputData = (GoodsGetGoodsPropsOutputData)outputBean.getOutputData();
	        // 判断是否所有商品调用商品服务都有信息
	        if (allGoodsIDNotGift.size() != getGoodsPropsOutputData.getGoodsList().size()) {
	            Log.info(logger, uuid, "调用goods服务getGoodsProps方法失败,有商品没有查询到详细信息", "getGoodsPropsOutputData", getGoodsPropsOutputData);
	            outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.CALLSERVER_ERROR.getResultID(), null, inputData.getScope());
	            Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
	            return outputBean;
	        }
	        // 将礼盒内部的goodsID加入到所有商品ID的set中，再加入goodsCount中
	        allGoodsID = allGoodsIDNotGift;// 所有商品的ID,包括NY内部(不包括NY的ID)和礼盒内部的商品ID(包括礼盒本身的ID)
	        for (GoodsGetGoodsPropsGoodsListElementO element : getGoodsPropsOutputData.getGoodsList()) {
	            if (BooleanEnum.TRUE.getV().equals(element.getIsGift())) {// 是礼盒
	                for (GoodsGetGoodsPropsGiftItemsElementO giftItemsElement : element.getGiftItems()) {
	                    allGoodsID.add(giftItemsElement.getGoodsID());
	                    goodsCount.get(element.getGoodsID()).setGiftType("1");
	                    goodsCount.get(element.getGoodsID()).getGiftItems().add(new GiftItemsBean(giftItemsElement.getGoodsID(), giftItemsElement.getAmount().longValue(), giftItemsElement.getGoodsPrice()));
	                }
	            }
	        }
	        Log.info(logger, uuid, "获得所有的goodsID(包括NY内部的goodsID和包括礼盒内部商品)", "allGoodsID", allGoodsID);
	        
	        if (inputData.getNyList() != null&& inputData.getNyList().size() > 0) {
	            // 增加虚拟礼盒的ID(固定的为1034964L)
	            allGoodsID.add(CreateOrderImpl.VIRTUAL_GIFT);
	        }
        }
        
        Log.info(logger, uuid, "组装goods服务getGoodsInfo方法所需参数开始");
        GoodsGetGoodsInfoInputData getGoodsInfoInputData = new GoodsGetGoodsInfoInputData();
        getGoodsInfoInputData.setScope(inputData.getScope());
        getGoodsInfoInputData.setWithGoodsDesc(BooleanEnum.FALSE.getV());
        getGoodsInfoInputData.setWithExtendsInfo(BooleanEnum.TRUE.getV());
        getGoodsInfoInputData.setWithSaleCatInfo(BooleanEnum.TRUE.getV());
        getGoodsInfoInputData.setWithSavInfo(BooleanEnum.FALSE.getV());
        getGoodsInfoInputData.setWithPicInfo(BooleanEnum.TRUE.getV());
        getGoodsInfoInputData.setWithSpecialInfo(BooleanEnum.FALSE.getV());
        getGoodsInfoInputData.setWithPriceInfo(BooleanEnum.TRUE.getV());
        for (Long goodsID : allGoodsID) {
            GoodsGetGoodsInfoGoodsListElementI goodsListElement = new GoodsGetGoodsInfoGoodsListElementI();
            goodsListElement.setGoodsID(goodsID);
            getGoodsInfoInputData.getGoodsList().add(goodsListElement);
        }
        Map<String, String> getGoodsInfoServiceParams = new HashMap<String, String>();
        getGoodsInfoServiceParams.put("method", "getGoodsInfo");
        getGoodsInfoServiceParams.put("req_str", getGoodsInfoInputData.toJson());
        Log.info(logger, uuid, "组装goods服务getGoodsInfo方法所需参数结束");
        
        Log.info(logger, uuid, "调用goods服务getGoodsInfo方法开始", "getGoodsInfoServiceParams", getGoodsInfoServiceParams);
        outputBean = TootooService.callServer("goods", getGoodsInfoServiceParams, "post", new GoodsGetGoodsInfoOutputData());
        Log.info(logger, uuid, "调用goods服务getGoodsInfo方法结束", "outputBean", outputBean);
        
        if (!TootooService.checkService(outputBean, "goods", "getGoodsInfo", inputData.getScope())) {
            Log.info(logger, uuid, "调用goods服务getGoodsInfo方法失败,接口返回", "outputBean", outputBean);
            return outputBean;
        }
        GoodsGetGoodsInfoOutputData getGoodsInfoOutputData = (GoodsGetGoodsInfoOutputData)outputBean.getOutputData();
        
        if (!OrderUtil.checkGoods(buyMap,inputData.getOrderType(), goodsCount, allGoodsID, getGoodsInfoOutputData, outputBean, logger, uuid, inputData.getSubstationID(), inputData.getScope())) {
            Log.info(logger, uuid, "有商品没有详细信息或者这些商品不是本分站的商品,接口返回", "outputBean", outputBean);
            return outputBean;
        }
        
        // 是否包含特殊商品
        String isIncludeSpecial = getGoodsInfoOutputData.getIsIncludeSpecial();
        Log.info(logger, uuid, "包含特殊商品");
        // 将商品信息存在map中, key:goodsID
        Map<Long, GoodsGetGoodsInfoGoodsInfoElementO> goodsInfo = OrderUtil.getGoodsInfo(getGoodsInfoOutputData.getGoodsInfo());
        // 是否包含预定商品
        boolean canReserve = false;
        for (Long goodsID : allGoodsID) {
            if (!"0".equals(goodsInfo.get(goodsID).getCanReserve())) {// 预定商品(包括礼盒)
                canReserve = true;
            }
        }
        /****************************************************************************** 调用商品服务结束 ********************************************************************************************/
        
        
        
        Set<Long> bankPromotionIds = new HashSet<Long>();
        for(Map.Entry<Long, Long> entry : buyMap.entrySet()){
            if(goodsInfo.get(entry.getKey()) != null && goodsInfo.get(entry.getKey()).getSkuInfo() != null && goodsInfo.get(entry.getKey()).getSkuInfo().getPromotionID() != null){
                if (PromotionStatusEnum.GOING.getC().equals(goodsInfo.get(entry.getKey()).getSkuInfo().getPromotionStatus())) {
                    bankPromotionIds.add(goodsInfo.get(entry.getKey()).getSkuInfo().getPromotionID());
                }
            }
        }
        Log.info(logger, uuid, "判断银行促销开始", "bankPromotionIds", bankPromotionIds);
        Set<Long> dicPromotionIds = new HashSet<Long>();
        for(Map.Entry<Long, String> entry : SpecialInfos.specialPromotion.entrySet()){
            Log.info(logger, uuid, "组装promotion服务getPromotionById方法所需参数开始");
            PromotionGetPromotionByIdInputData promotionGetPromotionByIdInputData = new PromotionGetPromotionByIdInputData();
            promotionGetPromotionByIdInputData.setScope(inputData.getScope());
            promotionGetPromotionByIdInputData.setPromotionType("1");
            PromotionGetPromotionByIdPromotionIdListElementI promotionElement = new PromotionGetPromotionByIdPromotionIdListElementI();
            promotionElement.setPromotionId(entry.getKey());
            promotionGetPromotionByIdInputData.getPromotionIdList().add(promotionElement);
            Map<String, String> promotionGetPromotionByIdServiceParams = new HashMap<String, String>();
            promotionGetPromotionByIdServiceParams.put("r", "TGetPromotionById");
            promotionGetPromotionByIdServiceParams.put("req_str", promotionGetPromotionByIdInputData.toJson());
            Log.info(logger, uuid, "组装promotion服务getPromotionById方法所需参数结束");
            
            Log.info(logger, uuid, "调用promotion服务getPromotionById方法开始", "promotionGetPromotionByIdServiceParams", promotionGetPromotionByIdServiceParams);
            outputBean = TootooService.callServer("promotion", promotionGetPromotionByIdServiceParams, "post", new PromotionGetPromotionByIdOutputData());
            Log.info(logger, uuid, "调用promotion服务getPromotionById方法结束", "outputBean", outputBean);
            
            if (!TootooService.checkService(outputBean, "promotion", "getPromotionById", inputData.getScope())) {
                Log.info(logger, uuid, "调用promotion服务getPromotionById方法失败,接口返回", "outputBean", outputBean);
                return outputBean;
            }
            PromotionGetPromotionByIdOutputData promotionByIdOutputData = (PromotionGetPromotionByIdOutputData)outputBean.getOutputData();
            
            if(promotionByIdOutputData.getPromotionList()!=null && !promotionByIdOutputData.getPromotionList().isEmpty()){
                PromotionGetPromotionByIdPromotionListElementO promotionElementO = promotionByIdOutputData.getPromotionList().get(0);
                List<String> scopes = PromotionScopeUtil.getPromotionScope(promotionElementO.getPromotionScope().intValue());
                if(scopes.contains(inputData.getScope())){
                    dicPromotionIds.add(entry.getKey());
                }
            }
        }
        bankPromotionIds.retainAll(dicPromotionIds);
        
        
        if (bankPromotionIds.size() > 0) {
            Log.info(logger, uuid, "判断银行促销配置表开始", "bankPromotionIds", bankPromotionIds, "dicPromotionIds", dicPromotionIds, "payId", payId);
            
            List<String> payMethodIdList = new ArrayList<String>();
            int i = 0;
            // 循环促销商品ID，取出配置表里的所有支付方式的交集（减去优惠券）
            for (Long promotionId : bankPromotionIds) {
                String[] payMethodIds = SpecialInfos.specialPromotion.get(promotionId).split(",");
                List<String> payMethodIdListTemp = new ArrayList<String>();
                payMethodIdListTemp.addAll(Arrays.asList(payMethodIds));
                payMethodIdListTemp.remove("4");
                if(i == 0){
                    payMethodIdList = payMethodIdListTemp;
                }else{
                    payMethodIdList.retainAll(payMethodIdListTemp);
                }
            }
            if(payMethodIdList.size() <= 0){
                Log.info(logger, uuid, "促销商品对应配置表中所有支付方式交集为空", "bankPromotionIds", bankPromotionIds, "dicPromotionIds", dicPromotionIds, "payId", payId);
                outputBean = ResponseUtil.getBaseOutputBean(BaseOrderResultEnum.PAY_INVALID.getResultID(), null, inputData.getScope());
                Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
                return outputBean;
            }
            
            Set<Long> promotionIdSet = new HashSet<Long>();
            for (String pId : payMethodIdList) {
                promotionIdSet.add(Long.valueOf(pId));
            }
            promotionIdSet.retainAll(payId);
            
            if(promotionIdSet.size() <= 0){
                Log.info(logger, uuid, "实际支付方式和对应配置的支付方式取交集为空", "bankPromotionIds", bankPromotionIds, "dicPromotionIds", dicPromotionIds, "payId", payId);
                outputBean = ResponseUtil.getBaseOutputBean(BaseOrderResultEnum.PAY_INVALID.getResultID(), null, inputData.getScope());
                Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
                return outputBean;
            }
        }
        
        /*if(bankPromotionIds.size()>1){
            Log.info(logger, uuid, "有多个支付银行促销", "bankPromotionIds", bankPromotionIds);
            outputBean = ResponseUtil.getBaseOutputBean(BaseOrderResultEnum.MULTI_PROMOTION.getResultID(), null, inputData.getScope());
            Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
            return outputBean;
        }
        if(bankPromotionIds.size()==1){
            Long promotionId = 0L;
            for(Long l : bankPromotionIds){
                promotionId = l;
            }
            for(String payMethodIdS : SpecialInfos.specialPromotion.get(promotionId).split(",")){
                payId.remove(Long.valueOf(payMethodIdS));
            }
            if(payId != null && !payId.isEmpty()){
                Log.info(logger, uuid, "中行促销，只能选择中行支付或者优惠券", "bankPromotionIds", bankPromotionIds, "payId", payId);
                outputBean = ResponseUtil.getBaseOutputBean(BaseOrderResultEnum.PAY_INVALID.getResultID(), null, inputData.getScope());
                Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
                return outputBean;
            }
        }*/
        
        
        
        // 保存团购订单的促销信息
        Map<Long, PromotionGetPromotionByIdDetailElementO> groupMap = new HashMap<Long, PromotionGetPromotionByIdDetailElementO>();
        Map<String, BigDecimal> discountMap = new HashMap<String, BigDecimal>();// 订单满减
        Map<String, BigDecimal> otherDiscountMap = new HashMap<String, BigDecimal>();// 其他满减
        
		boolean groupWy = false;
		StringBuffer bsMobile = new StringBuffer("");
		BigDecimal sendAmount = BigDecimal.ZERO;
				
        Log.info(logger, uuid, "验证团购订单入参开始");
        TtsPrivateTeamJoinPO ttsPrivateTeamJoinPO = null;	
        TtsPrivateTeamDao ttsPrivateTeamDao = new TtsPrivateTeamDao(uuid,logger);
		TtsPrivateActivityDao ttsPrivateActivityDao=new TtsPrivateActivityDao(uuid, logger);
		TtsPrivateTeamJoinDao ttsPrivateTeamJoinDao=new TtsPrivateTeamJoinDao(uuid, logger);
		
		TtsPrivateTeamPO ttsPrivateTeamPO= ttsPrivateTeamDao.findTtsPrivateTeamPOByID(inputData.getGroupTeamID());
		
		// 判断活动团是否在购买时间内		
		if(ttsPrivateTeamPO==null
				||ttsPrivateTeamPO.getBuyEndTime()==null
				||ttsPrivateTeamPO.getBuyStartTime()==null
				||DateUtil.getNow()>ttsPrivateTeamPO.getBuyEndTime().getTime()
				||DateUtil.getNow()<ttsPrivateTeamPO.getBuyStartTime().getTime()){
			// 不在购买期内		
			outputBean = ResponseUtil.getBaseOutputBean(BaseOrderResultEnum.PRIVATE_TEAM_NOT_BUYTIME.getResultID(), null, inputData.getScope());
			Log.info(logger, uuid, "不在购买期内", "ttsPrivateTeamPO", ttsPrivateTeamPO);
	        return outputBean;
		}else{
			/**
			0：发起
			1：已审核
			2：已作废
			3：已成团
			4：已关闭 
			 */
			if(!"3".equals(ttsPrivateTeamPO.getTeamStatus())){
				// 没有成团		
				outputBean = ResponseUtil.getBaseOutputBean(BaseOrderResultEnum.PRIVATE_TEAM_STATUS_NO_BEGIN.getResultID(), null, inputData.getScope());
				Log.info(logger, uuid, "没有成团	", "ttsPrivateTeamPO", ttsPrivateTeamPO);
		        return outputBean;
			}else{
				List<Object[]> condition=new ArrayList<Object[]>();
				condition.add(new Object[]{"JOINER","=",Long.valueOf(params.get(AuthorizeClient.COOKIE_BUYER_ID))});
				condition.add(new Object[]{"TEAM_ID","=",inputData.getGroupTeamID()});
				List<TtsPrivateTeamJoinPO> ttsPrivateTeamJoinPOList =ttsPrivateTeamJoinDao.findTtsPrivateTeamJoinPOListByCondition(condition);
				/**
					1：参与	
					2：获得资格
					3：已下单
					4：已支付
					5：取消资格
				 */
				if(ttsPrivateTeamJoinPOList==null||ttsPrivateTeamJoinPOList.size()==0||!"2".equals(ttsPrivateTeamJoinPOList.get(0).getJoinStatus())){
					outputBean = ResponseUtil.getBaseOutputBean(BaseOrderResultEnum.PRIVATE_TEAM_NO_JOIN.getResultID(), null, inputData.getScope());
					Log.info(logger, uuid, "没有资格	", "ttsPrivateTeamJoinPOList", ttsPrivateTeamJoinPOList);
			        return outputBean;
				}else{
					ttsPrivateTeamJoinPO = ttsPrivateTeamJoinPOList.get(0);
				}
			}
		
		}	
        
        
        
        Log.info(logger, uuid, "验证团购订单入参结束");
        
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
            Log.info(logger, uuid, "不支持当日达");
            outputBean = ResponseUtil.getBaseOutputBean(BaseOrderResultEnum.TODAY_IS_INVALID.getResultID(), null, inputData.getScope());
            Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
            return outputBean;
        }
        Long templeteId = 0L;// 模板ID
        
        TtsPrivateActivityPO ttsPrivateActivityPO = ttsPrivateActivityDao.findTtsPrivateActivityPOByID(ttsPrivateTeamPO.getActivityId());
        if(ttsPrivateActivityPO!=null){
            templeteId=ttsPrivateActivityPO.getShipTemplateId();
        }
        
        
        /****************************************************************************** 调用配送服务开始 ********************************************************************************************/
        Map<Long, Map<Long, List<ShippingGetGoodsShippingInfosShippingRulesElementO>>> goodsWarehouseRule = new HashMap<Long, Map<Long, List<ShippingGetGoodsShippingInfosShippingRulesElementO>>>();
        Object o = this.callShippingService(goodsWarehouseRule, false, inputData, templeteId, addressOutputData, goodsCount, goodsInfo, timeShipType, goodsStock);
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
        o = this.callShippingService(goodsWarehouseRule, true, inputData, templeteId, addressOutputData, goodsCount, goodsInfo, timeShipType, goodsStock);
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
        boolean isTuangou = true;
        Log.info(logger, uuid, "确定收货日期开始");
        receiveDate = getprivateTeamReceiveDate(inputData.getSubstationID(), isIncludeSpecial, inputData.getReceiveDate(), isCheckCOD, allDates, logger, uuid, outputBean, inputData.getScope());
        if (receiveDate == null) {
            Log.info(logger, uuid, "不支持COD方式或选定的日期非法,接口返回", "outputBean", outputBean);
            return outputBean;
        }
        Log.info(logger, uuid, "确定收货日期结束", "receiveDate", receiveDate);
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
        
        long count = 0;
        if(ttsPrivateTeamPO.getJoinNum()!=null){
        	count=ttsPrivateTeamPO.getJoinNum();
        }
        TtsMultiStepPriceDao ttsMultiStepPriceDao=new TtsMultiStepPriceDao();
        List<Object[]> condition=new ArrayList<Object[]>();
		condition.add(new Object[]{"ACTIVITY_ID","=",ttsPrivateTeamPO.getActivityId()});
		condition.add(new Object[]{"STEP_NUM","<=",count});
		List<TtsMultiStepPricePO> ttsMultiStepPricePOList = ttsMultiStepPriceDao.findTtsMultiStepPricePOListByCondition(condition);
		TtsMultiStepPricePO goodsTtsMultiStepPricePO=null;
		if(ttsMultiStepPricePOList!=null&&ttsMultiStepPricePOList.size()>0){
			for(TtsMultiStepPricePO ttsMultiStepPricePO: ttsMultiStepPricePOList){
				if(goodsTtsMultiStepPricePO==null){
					goodsTtsMultiStepPricePO=ttsMultiStepPricePO;
				}else{
					if(ttsMultiStepPricePO.getStepNum()>goodsTtsMultiStepPricePO.getStepNum()
							&&count>=ttsMultiStepPricePO.getStepNum()){
						goodsTtsMultiStepPricePO=ttsMultiStepPricePO;
					}
				}
			}
		}else{
			// 价格异常
			outputBean = ResponseUtil.getBaseOutputBean(BaseOrderResultEnum.PRIVATE_TEAM_NO_STEPPRICE.getResultID(), null, inputData.getScope());
			Log.info(logger, uuid, "阶梯价格异常", "ttsMultiStepPricePOList", ttsMultiStepPricePOList,"condition",condition);
	        return outputBean;
		}
		BigDecimal orderFee = BigDecimal.ZERO;
	    for (OmsCreateOrderGroupBuyingGoodsListElementI groupGoodElement : inputData.getGroupBuyingGoodsList()) {
	    	orderFee = orderFee.add(goodsTtsMultiStepPricePO.getStepPrice().multiply(BigDecimal.valueOf(groupGoodElement.getGoodsCount())));
        }
		Log.info(logger, uuid, "获得订单的订单金额", "orderFee", orderFee);
        
//        boolean isGiftOK = false;
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
//                        isGiftOK = true;
                    }
                }
            }
        }
        
        
        BigDecimal shipFee = BigDecimal.ZERO;
        BigDecimal shipTotalFee = BigDecimal.ZERO;
        BigDecimal deliveryTimeFee = BigDecimal.ZERO;

        /****************************************************************************** 调用运费服务开始 ********************************************************************************************/
//        Log.info(logger, uuid, "组装shipping服务getGoodsShippingFee方法所需参数开始");
//        ShippingGetGoodsShippingFeeInputData shipFeeInputData = getGoodsItemsShipfee(isGiftOK, isNeedGift?SpecialInfos.specialCouponMap.get(couponSN).getGoodsId():null, inputData.getScope(), inputData.getOrderType(), timeShipType, inputData.getSubstationID(), ("1".equals(inputData.getReqFrom())?inputData.getLastGeoId():addressOutputData.getLastGeoID()), orderFee, goodsCount, goodsInfo);
//        Map<String, String> shipFeeServiceParams = new HashMap<String, String>();
//        shipFeeServiceParams.put("method", "getGoodsShippingFee");
//        shipFeeServiceParams.put("req_str", shipFeeInputData.toJson());
//        Log.info(logger, uuid, "组装shipping服务getGoodsShippingFee方法所需参数结束");
//        
//        Log.info(logger, uuid, "调用shipping服务getGoodsShippingFee方法开始", "shipFeeServiceParams", shipFeeServiceParams);
//        outputBean = TootooService.callServer("shipping", shipFeeServiceParams, "post", new ShippingGetGoodsShippingFeeOutputData());
//        Log.info(logger, uuid, "调用shipping服务getGoodsShippingFee方法结束", "outputBean", outputBean);
//        
//        if (!TootooService.checkService(outputBean, "shipping", "getGoodsShippingFee", inputData.getScope())) {
//            Log.info(logger, uuid, "调用shipping服务getGoodsShippingFee方法失败,接口返回", "outputBean", outputBean);
//            return outputBean;
//        }
//        ShippingGetGoodsShippingFeeOutputData shipFeeOutputData = (ShippingGetGoodsShippingFeeOutputData)outputBean.getOutputData();
//        shipFee = shipFee.add(shipFeeOutputData.getDiscountShippingFee()==null?BigDecimal.ZERO:shipFeeOutputData.getDiscountShippingFee());
//        shipTotalFee = shipTotalFee.add(shipFeeOutputData.getTotalShippingFee()==null?BigDecimal.ZERO:shipFeeOutputData.getTotalShippingFee());
//        deliveryTimeFee = deliveryTimeFee.add(shipFeeOutputData.getTimeShipServiceFee()==null?BigDecimal.ZERO:shipFeeOutputData.getTimeShipServiceFee());
        /****************************************************************************** 调用运费服务结束 ********************************************************************************************/
      
        ttsPrivateTeamJoinPO.setJoinStatus("3");

        return   createOrder(request,inputBean, inputData, outputBean, outputData, params, 
        		goodsCount, groupMap, goodsInfo, buyFromMap,
        		goodsWarehouseAndDcBean, goodsShipCapacityMap, 
        		discountMap, otherDiscountMap, addressOutputData, 
        		null, parentOrderDao, bsMobile, goodsIDMax, 
        		isIncludeSpecial, isCheckCOD, couponSN, receiveDate,
        		sendAmount, shipFee, shipTotalFee, deliveryTimeFee,
        		goodsTtsMultiStepPricePO.getStepPrice(),
        		payMethodId, canReserve, haveOnlinePay, groupWy,
        		ttsPrivateTeamJoinDao,ttsPrivateTeamJoinPO);
	}
	
	
	private String getSubstationGoods(Long substationID,List<GoodsGetGoodsPropsGoodsListElementO> goodsPropsGoodsList,OmsCreateOrderInputData inputData) {
		TtsTeamGoodsRelationsDao ttsTeamGoodsRelationsDao=new TtsTeamGoodsRelationsDao();
		String result="0";
		for (GoodsGetGoodsPropsGoodsListElementO goodsListElement:goodsPropsGoodsList) {
			List<Object[]> condition=new ArrayList<Object[]>();
			condition.add(new Object[]{"SUBSTATION_ID","=",substationID});
			if (BooleanEnum.TRUE.getV().equals(goodsListElement.getIsGift())) {// 是礼盒
				condition.add(new Object[]{"GOODS_TYPE","=","1"});
				Long goodsBasicID=0L;
				for (GoodsGetGoodsPropsGiftItemsElementO giftItemsElement : goodsListElement.getGiftItems()) {
					goodsBasicID+=giftItemsElement.getGoodsBasicID();
				}
				condition.add(new Object[]{"BASIC_ID","=",goodsBasicID});
			}else{
				condition.add(new Object[]{"GOODS_TYPE","=","0"});
				condition.add(new Object[]{"BASIC_ID","=",goodsListElement.getGoodsBasicID()});
			}
			
			List<TtsTeamGoodsRelationsPO> ttsTeamGoodsRelationsPOList = ttsTeamGoodsRelationsDao.findTtsTeamGoodsRelationsPOListByCondition(condition);
			if(ttsTeamGoodsRelationsPOList==null||ttsTeamGoodsRelationsPOList.size()==0){
				return "-1";
			}else{
				TtsTeamGoodsRelationsPO ttsTeamGoodsRelationsPO = ttsTeamGoodsRelationsPOList.get(0);
				if(!goodsListElement.getGoodsID().equals(ttsTeamGoodsRelationsPO.getGoodsId())){
					for (OmsCreateOrderGroupBuyingGoodsListElementI groupBuyElement : inputData.getGroupBuyingGoodsList()) {
						if(goodsListElement.getGoodsID().equals(groupBuyElement.getGoodsID())){
							Log.info(logger, uuid, "更换私享团商品goodsId","groupBuyElement",groupBuyElement,"ttsTeamGoodsRelationsPO",ttsTeamGoodsRelationsPO);							
							groupBuyElement.setGoodsID(ttsTeamGoodsRelationsPO.getGoodsId());
							result="1";
						}
					}
				}
			}
			
		}
		return result;
	}
	
	
	 /**
     * 确定收货日期
     * Description:<br>
     * 
     * @param inputReceiveDate
     * @param isCheckCOD
     * @param allDates
     * @param logger
     * @param outputBean
     * @return
     * @throws Exception
     */
    public String getprivateTeamReceiveDate(Long substationId, String isIncludeSpecial, String inputReceiveDate, String isCheckCOD, Set<ShippingDateBean> allDates, Logger logger, String uuid, BaseOutputBean outputBean, String scope) throws Exception {
        
        BaseOutputBean outputBeanInside = null;
        String receiveDate = inputReceiveDate;
        
        Date date = new Date();// 取时间
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, 1);// 把日期往后增加一天.整数往后推,负数往前移动
        date = calendar.getTime(); // 这个时间就是日期往后推一天的结果
        
        if (StringUtil.isEmpty(inputReceiveDate)) {// 没有指定日期
            Set<String> dates = new HashSet<String>();
            if (BooleanEnum.TRUE.getV().equals(isCheckCOD)) {// 选择了COD
                for (ShippingDateBean dateBean : allDates) {
                    if (dateBean.getIsSupportCOD().equals(isCheckCOD)) {
                        dates.add(dateBean.getShipDate());
                    }
                }
                if (dates.isEmpty()) {
                    Log.info(logger, uuid, "不支持COD方式");
                    outputBeanInside = ResponseUtil.getBaseOutputBean(BaseOrderResultEnum.NOT_SURPPORT_COD.getResultID(), null, scope);
                    outputBean.setOutputHead(outputBeanInside.getOutputHead());
                    outputBean.setOutputData(outputBeanInside.getOutputData());
                    return null;
                }
            } else {// 没有选择COD，在所有日期中选最早的
                for (ShippingDateBean dateBean : allDates) {
                    dates.add(dateBean.getShipDate());
                }
            }
            receiveDate = Collections.min(dates);
            
            if (BooleanEnum.FALSE.getV().equals(isIncludeSpecial)
                            && substationId.intValue() == SubstationEnum.SH.getC()) {// 不包含特殊商品且为上海站,结单时间为21点
                if (java.util.Calendar.getInstance().get(Calendar.HOUR_OF_DAY) >= 21) {// 大于21点
                    if (receiveDate.equals(new SimpleDateFormat("yyyy-MM-dd").format(date))) {
                        dates.remove(new SimpleDateFormat("yyyy-MM-dd").format(date));
                        if (dates.isEmpty()) {
                            Log.info(logger, uuid, "上海站大于21点,去掉明天后，日期为空");
                            return null;
                        }
                        receiveDate = Collections.min(dates);
                    }
                }
            }
            
            String now = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            if (receiveDate.equals(now)){
            	dates.remove(now);
            	  if (dates.isEmpty()) {
                      Log.info(logger, uuid, "私享团去除今天，日期为空");
                      return null;
                  }
            }
            
        } else {
            if (!allDates.contains(new ShippingDateBean(inputReceiveDate, "", "", ""))) {// 不包含选定的日期
                Log.info(logger, uuid, "选择的配送日期非法");
                outputBeanInside = ResponseUtil.getBaseOutputBean(BaseOrderResultEnum.SHIPPINGDATE_INVALID.getResultID(), null, scope);
                outputBean.setOutputHead(outputBeanInside.getOutputHead());
                outputBean.setOutputData(outputBeanInside.getOutputData());
                return null;
            }
            for (ShippingDateBean dateBean : allDates) {
                if (dateBean.getShipDate().equals(inputReceiveDate)) {
                    if (BooleanEnum.TRUE.getV().equals(isCheckCOD)) {// 选择了COD
                        if (!BooleanEnum.TRUE.getV().equals(dateBean.getIsSupportCOD())) {
                            Log.info(logger, uuid, "不支持COD方式");
                            outputBeanInside = ResponseUtil.getBaseOutputBean(BaseOrderResultEnum.NOT_SURPPORT_COD.getResultID(), null, scope);
                            outputBean.setOutputHead(outputBeanInside.getOutputHead());
                            outputBean.setOutputData(outputBeanInside.getOutputData());
                            return null;
                        }
                    }
                }
            }
            if (BooleanEnum.FALSE.getV().equals(isIncludeSpecial)
                            && substationId.intValue() == SubstationEnum.SH.getC()) {// 不包含特殊商品且为上海站,结单时间为21点
                if (java.util.Calendar.getInstance().get(Calendar.HOUR_OF_DAY) >= 21) {// 大于21点
                    if (inputReceiveDate.equals(new SimpleDateFormat("yyyy-MM-dd").format(date))) {
                        Log.info(logger, uuid, "已经超过21点，上海站不能选择明天");
                        return null;
                    }
                }
            }
            
        }
        return receiveDate;
    }
    
    
    private Object callShippingService(Map<Long, Map<Long, List<ShippingGetGoodsShippingInfosShippingRulesElementO>>> goodsWarehouseRule, boolean isPower, OmsCreateOrderInputData inputData, Long templeteId, AddressGetAddressByIDOutputData addressOutputData, HashMap<Long, GoodsNumberBean> goodsCount, Map<Long, GoodsGetGoodsInfoGoodsInfoElementO> goodsInfo, String timeShipType, Map<Long, Set<Long>> goodsStock) throws Exception{
        
        
        Log.info(logger, uuid, "组装shipping服务getGoodsShippingInfos方法所需参数开始");
        
        ShippingGetGoodsShippingInfosInputData getGoodsShippingInfosInputData = getGoodsShippingInfosInputData(null,isPower, inputData.getScope(), inputData.getOrderType(), inputData.getOrderFrom(), inputData.getSubstationID(), templeteId, ("1".equals(inputData.getReqFrom())?inputData.getLastGeoId():addressOutputData.getLastGeoID()), goodsCount, goodsInfo, timeShipType);
        
            
        if (getGoodsShippingInfosInputData == null) {// 不可同天送
            Log.info(logger, uuid, "有商品不可同日达或者不支持当日达");
            return BaseOrderResultEnum.NOT_SAMETIME_SHIP;
//            outputBean = ResponseUtil.getBaseOutputBean(BaseOrderResultEnum.NOT_SAMETIME_SHIP.getResultID(), null, inputData.getScope());
//            Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
//            return outputBean;
        }
        
        Map<String, String> getGoodsShippingInfosServiceParams = new HashMap<String, String>();
        getGoodsShippingInfosServiceParams.put("method", "getGoodsShippingInfos");
        getGoodsShippingInfosServiceParams.put("req_str", getGoodsShippingInfosInputData.toJson());
        Log.info(logger, uuid, "组装shipping服务getGoodsShippingInfos方法所需参数结束");
        
        Log.info(logger, uuid, "调用shipping服务getGoodsShippingInfos方法开始", "getGoodsShippingInfosServiceParams", getGoodsShippingInfosServiceParams);
        BaseOutputBean outputBean = TootooService.callServer("shipping", getGoodsShippingInfosServiceParams, "post", new ShippingGetGoodsShippingInfosOutputData());
        Log.info(logger, uuid, "调用shipping服务getGoodsShippingInfos方法结束", "outputBean", outputBean);
        
        if (!TootooService.checkService(outputBean, "shipping", "getGoodsShippingInfos", inputData.getScope())) {
            Log.info(logger, uuid, "调用shipping服务getGoodsShippingInfos方法失败,接口返回", "outputBean", outputBean);
            return outputBean;
        }
        ShippingGetGoodsShippingInfosOutputData shippingOutputData = (ShippingGetGoodsShippingInfosOutputData)outputBean.getOutputData();
        
        Log.info(logger, uuid, "判断配送开始");
        // 保存验证错误时的错误商品
        Set<String> returnSetShipping = new HashSet<String>();
        // 商品库房配送规则
//        Map<Long, Map<Long, List<ShippingGetGoodsShippingInfosShippingRulesElementO>>> goodsWarehouseRule = new HashMap<Long, Map<Long, List<ShippingGetGoodsShippingInfosShippingRulesElementO>>>();
        // 商品库房配送日期
        Map<Long, Map<Long, Set<ShippingDateBean>>> goodsWarehouseDate = OrderUtil.checkShipping(false, ("1".equals(inputData.getReqFrom())?inputData.getLastGeoId():addressOutputData.getLastGeoID()), goodsStock, shippingOutputData, goodsCount, returnSetShipping, goodsInfo, goodsWarehouseRule);
        if (goodsWarehouseDate == null) {
            Log.info(logger, uuid, "有商品不可送达", "goodsList", returnSetShipping);
            return BaseOrderResultEnum.NOT_SHIP;
//            outputBean = ResponseUtil.getBaseOutputBean(BaseOrderResultEnum.NOT_SHIP.getResultID(), null, inputData.getScope());
//            Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
//            return outputBean;
        }
        Log.info(logger, uuid, "判断配送结束", "goodsWarehouseDate", goodsWarehouseDate);
        
        
        Log.info(logger, uuid, "获得所有商品的可配送日期开始");
        Set<ShippingDateBean> allDates = OrderUtil.getAllGoodsDates(goodsWarehouseDate, logger, uuid);
        Log.info(logger, uuid, "获得所有商品的可配送日期结束", "allDates", allDates);
        
        return allDates;
    }
    
	
}

package cn.tootoo.soa.oms.createorder;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import cn.tootoo.db.egrocery.buildingactivity.BuildingActivityDao;
import cn.tootoo.db.egrocery.buildingactivity.BuildingActivityPO;
import cn.tootoo.db.egrocery.buildingjoin.BuildingJoinDao;
import cn.tootoo.db.egrocery.buildingjoin.BuildingJoinPO;
import cn.tootoo.db.egrocery.sysgeo.SysGeoPO;
import cn.tootoo.db.egrocery.tosparentorder.TOsParentOrderDao;
import cn.tootoo.enums.BaseResultEnum;
import cn.tootoo.enums.BooleanEnum;
import cn.tootoo.http.AuthorizeClient;
import cn.tootoo.http.TootooService;
import cn.tootoo.http.bean.BaseInputBean;
import cn.tootoo.http.bean.BaseOutputBean;
import cn.tootoo.soa.base.bean.GiftItemsBean;
import cn.tootoo.soa.base.bean.GoodsNumberBean;
import cn.tootoo.soa.base.bean.GoodsWarehouseAndDcBean;
import cn.tootoo.soa.base.bean.ShippingDateBean;
import cn.tootoo.soa.base.enums.BaseOrderResultEnum;
import cn.tootoo.soa.base.enums.OrderTypeEnum;
import cn.tootoo.soa.base.enums.PromotionStatusEnum;
import cn.tootoo.soa.base.enums.PromotionTypeEnum;
import cn.tootoo.soa.base.enums.TimeShipTypeEnum;
import cn.tootoo.soa.base.global.SpecialInfos;
import cn.tootoo.soa.base.util.OrderUtil;
import cn.tootoo.soa.base.util.PromotionScopeUtil;
import cn.tootoo.soa.goods.getgoodsinfo.input.GoodsGetGoodsInfoGoodsListElementI;
import cn.tootoo.soa.goods.getgoodsinfo.input.GoodsGetGoodsInfoInputData;
import cn.tootoo.soa.goods.getgoodsinfo.output.GoodsGetGoodsInfoGoodsInfoElementO;
import cn.tootoo.soa.goods.getgoodsinfo.output.GoodsGetGoodsInfoOutputData;
import cn.tootoo.soa.goods.getgoodsinfo.output.GoodsGetGoodsInfoSendDaysElementO;
import cn.tootoo.soa.goods.getgoodsprops.input.GoodsGetGoodsPropsGoodsListElementI;
import cn.tootoo.soa.goods.getgoodsprops.input.GoodsGetGoodsPropsInputData;
import cn.tootoo.soa.goods.getgoodsprops.output.GoodsGetGoodsPropsGiftItemsElementO;
import cn.tootoo.soa.goods.getgoodsprops.output.GoodsGetGoodsPropsGoodsListElementO;
import cn.tootoo.soa.goods.getgoodsprops.output.GoodsGetGoodsPropsOutputData;
import cn.tootoo.soa.oms.createorder.input.OmsCreateOrderGoodsListElementI;
import cn.tootoo.soa.oms.createorder.input.OmsCreateOrderInputData;
import cn.tootoo.soa.oms.createorder.output.OmsCreateOrderOutputData;
import cn.tootoo.soa.promotion.getpromotionbyid.output.PromotionGetPromotionByIdDetailElementO;
import cn.tootoo.soa.promotion.getpromotioninfo.output.PromotionGetPromotionInfoDiscountListElementO;
import cn.tootoo.soa.promotion.getpromotioninfo.output.PromotionGetPromotionInfoOutputData;
import cn.tootoo.soa.shipping.getgoodsshippinginfos.input.ShippingGetGoodsShippingInfosGeoListElementI;
import cn.tootoo.soa.shipping.getgoodsshippinginfos.input.ShippingGetGoodsShippingInfosGiftItemsElementI;
import cn.tootoo.soa.shipping.getgoodsshippinginfos.input.ShippingGetGoodsShippingInfosGoodsListElementI;
import cn.tootoo.soa.shipping.getgoodsshippinginfos.input.ShippingGetGoodsShippingInfosInputData;
import cn.tootoo.soa.shipping.getgoodsshippinginfos.input.ShippingGetGoodsShippingInfosShippingDatesElementI;
import cn.tootoo.soa.shipping.getgoodsshippinginfos.output.ShippingGetGoodsShippingInfosOutputData;
import cn.tootoo.soa.shipping.getgoodsshippinginfos.output.ShippingGetGoodsShippingInfosShippingRulesElementO;
import cn.tootoo.soa.shopping.buildingcheckcreatorder.input.ShoppingBuildingCheckCreatOrderInputData;
import cn.tootoo.soa.shopping.buildingcheckcreatorder.output.ShoppingBuildingCheckCreatOrderOutputData;
import cn.tootoo.soa.shopping.buildingcheckgoodsinactivitypool.input.ShoppingBuildingCheckGoodsInActivityPoolGoodsListElementI;
import cn.tootoo.soa.shopping.buildingcheckgoodsinactivitypool.input.ShoppingBuildingCheckGoodsInActivityPoolInputData;
import cn.tootoo.soa.shopping.buildingcheckgoodsinactivitypool.output.ShoppingBuildingCheckGoodsInActivityPoolOutputData;
import cn.tootoo.soa.stock.getsalestock.input.StockGetSaleStockInputData;
import cn.tootoo.soa.stock.getsalestock.output.StockGetSaleStockOutputData;
import cn.tootoo.utils.DateUtil;
import cn.tootoo.utils.Log;
import cn.tootoo.utils.ResponseUtil;
import cn.tootoo.utils.StringUtil;

/**
 * Description:<br>
 * 楼宇活动下单
 * 
 * @author chuan
 * @version 1.0
 */
public class BuildingCreateOrderService  extends CreateOrderBaseService {
    
    public static final Long virtualGift = 1034964L;


	public BuildingCreateOrderService(String uuid, Logger logger) {
		super(uuid, logger);
	}
	
	public BaseOutputBean createBuildingOrder(
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
	    
	    
	    
	    Log.info(logger, uuid, "校验支付方式只能为线上支付");
	    for (Long payMethod : payId) {
	        if(payMethod == 4){
	            Log.info(logger, uuid, "有优惠券支付");
	            outputBean = ResponseUtil.getBaseOutputBean(BaseOrderResultEnum.CHECK_PAY_ERROR.getResultID(), null, inputData.getScope());
	            Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
	            return outputBean;
	        }
        }
	    
        if (BooleanEnum.TRUE.getV().equals(isCheckCOD)){
            Log.info(logger, uuid, "有线下支付");
            outputBean = ResponseUtil.getBaseOutputBean(BaseOrderResultEnum.CHECK_PAY_ERROR.getResultID(), null, inputData.getScope());
            Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
            return outputBean;
	    }
	    
	    
	    /****************************************************************************** 验证是否可下楼宇活动订单开始 ********************************************************************************************/
	    Log.info(logger, uuid, "验证是否可下楼宇活动订单开始");
	    ShoppingBuildingCheckCreatOrderInputData buildingCheckCreatOrderInputData = new ShoppingBuildingCheckCreatOrderInputData();
        buildingCheckCreatOrderInputData.setScope(inputData.getScope());
        buildingCheckCreatOrderInputData.setActivityType(inputData.getActivityType());
        buildingCheckCreatOrderInputData.setActivityId(inputData.getActivityId());
        HashMap<String, String> shopingServiceParams = (HashMap<String, String>)params.clone();
        shopingServiceParams.put("method", "buildingCheckCreatOrder");
        shopingServiceParams.put("req_str", buildingCheckCreatOrderInputData.toJson());
        
        Log.info(logger, uuid, "调用shopping服务buildingCheckCreatOrder方法开始", "shopingServiceParams", shopingServiceParams);
        outputBean = TootooService.callServer("shopping", shopingServiceParams, "post", new ShoppingBuildingCheckCreatOrderOutputData());
        Log.info(logger, uuid, "调用shopping服务buildingCheckCreatOrder方法结束", "outputBean", outputBean);
        
        if (!TootooService.checkService(outputBean, "shopping", "buildingCheckCreatOrder", inputData.getScope())) {
            Log.info(logger, uuid, "调用shopping服务buildingCheckCreatOrder方法失败,接口返回", "outputBean", outputBean);
            return outputBean;
        }
        /****************************************************************************** 验证是否可下楼宇活动订单结束 ********************************************************************************************/
        
	    
	    
	    
	    /****************************************************************************** 调用商品服务开始 ********************************************************************************************/
        Log.info(logger, uuid, "根据传入的商品列表、N元Y件列表、赠品列表，获得商品的map,goodCount<Long,buyNumber-giftNumber>");
        List<Long> redemptionList = new ArrayList<Long>();// 保存所有换购的商品
        goodsIDMax = new StringBuffer("");// 除NY的所有goodsID的最大值
        Set<Long> allGoodsIDNotGift = new HashSet<Long>();// 所有的goodsID，包括NY内部的goodsID(不包括NY的ID),但不包括礼盒内部商品(包括礼盒本身的ID)
        Map<Long, Map<Long, Long>> giftMap = new HashMap<Long, Map<Long, Long>>(); // 保存赠品Map<goodsID,Map<promotionID,
                                                                                   // giftNum>>
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
        
        
        /****************************************************************************** 校验商品是否都在商品池开始********************************************************************************************/
        Log.info(logger, uuid, "组装shopping服务buildingCheckGoodsInActivityPool方法所需参数开始");
        ShoppingBuildingCheckGoodsInActivityPoolInputData checkGoodsInPoolInputData = new ShoppingBuildingCheckGoodsInActivityPoolInputData();
        checkGoodsInPoolInputData.setScope(inputData.getScope());
        checkGoodsInPoolInputData.setActivityType(inputData.getActivityType());
        checkGoodsInPoolInputData.setActivityId(inputData.getActivityId());
        List<ShoppingBuildingCheckGoodsInActivityPoolGoodsListElementI> checkGoodsListElementIList = new ArrayList<ShoppingBuildingCheckGoodsInActivityPoolGoodsListElementI>();
        for (Entry<Long, GoodsNumberBean> entry : goodsCount.entrySet()) {
            ShoppingBuildingCheckGoodsInActivityPoolGoodsListElementI checkGoodsListElementI = new ShoppingBuildingCheckGoodsInActivityPoolGoodsListElementI();
            checkGoodsListElementI.setGoodsID(entry.getKey());
            checkGoodsListElementIList.add(checkGoodsListElementI);
        }
        checkGoodsInPoolInputData.setGoodsList(checkGoodsListElementIList);
        
        HashMap<String, String> checkGoodsParams = (HashMap<String, String>)params.clone();
        checkGoodsParams.put("method", "buildingCheckGoodsInActivityPool");
        checkGoodsParams.put("req_str", checkGoodsInPoolInputData.toJson());
        Log.info(logger, uuid, "组装shopping服务buildingCheckGoodsInActivityPool方法所需参数结束");
        
        
        Log.info(logger, uuid, "调用shopping服务buildingCheckGoodsInActivityPool方法开始", "checkGoodsParams", checkGoodsParams);
        outputBean = TootooService.callServer("shopping", checkGoodsParams, "post", new ShoppingBuildingCheckGoodsInActivityPoolOutputData());
        Log.info(logger, uuid, "调用shopping服务buildingCheckGoodsInActivityPool方法结束", "outputBean", outputBean);
        
        if (!TootooService.checkService(outputBean, "shopping", "buildingCheckGoodsInActivityPool", inputData.getScope())) {
            Log.info(logger, uuid, "调用shopping服务buildingCheckGoodsInActivityPool方法验证失败,接口返回", "outputBean", outputBean);
            return outputBean;
        }
        ShoppingBuildingCheckGoodsInActivityPoolOutputData checkGoodsOutputData = (ShoppingBuildingCheckGoodsInActivityPoolOutputData)outputBean.getOutputData();
        String isAllInPool = checkGoodsOutputData.getIsAllInPool();
        if (!BooleanEnum.TRUE.getV().equals(isAllInPool)){
            Log.info(logger, uuid, "调用shopping服务buildingCheckGoodsInActivityPool方法验证不通过,接口返回", "outputBean", outputBean);
            outputBean = ResponseUtil.getBaseOutputBean(BaseOrderResultEnum.BUIILDING_NO_GOODS.getResultID(), null, inputData.getScope());
            Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
            return outputBean;
        }
        
        
        /****************************************************************************** 校验商品是否都在商品池结束********************************************************************************************/
        
        
        
        /******************************************************************************自己书写地址开始 ********************************************************************************************/
        Log.info(logger, uuid, "重新设置入参中的地址相关信息");
        Long receiveAddrID = null;
        String addrDetail = "";
        String receiver = "";
        String phoneNumber = "";
        String receiverMobile = "";
        Long province = null;
        String provinceName = "";
        Long city = null;
        String cityName = "";
        Long district = null;
        String districtName = "";
        Long area = 0L;
        String areaName = "";
        Long lastGeoID = null;
        Long defaultPayId = null;
        String addrLine1 = "";
        
        
        BuildingJoinDao buildingJoinDao = new BuildingJoinDao(uuid, logger);
        List<Object[]> joinConditions = new ArrayList<Object[]>();
        joinConditions.add(new Object[]{"ACTIVITY_ID", "=", inputData.getActivityId()});
        joinConditions.add(new Object[]{"BUYER_ID", "=", params.get(AuthorizeClient.COOKIE_BUYER_ID)});
        Log.info(logger, uuid, "查询条件", "joinConditions", StringUtil.transferObjectList(joinConditions));
        List<BuildingJoinPO> joinList = buildingJoinDao.findBuildingJoinPOListByCondition(joinConditions);
        Log.info(logger, uuid, "查询结果", "joinList", joinList);
        if (joinList == null || joinList.size() <= 0) {
            Log.info(logger, uuid, "查询用户报名表失败", "joinList", joinList);
            outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.INSIDE_ERROR.getResultID(), null, inputData.getScope());
            Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
            return outputBean;
        }
        BuildingJoinPO buildingJoin = joinList.get(0);
        
        Map<Long, SysGeoPO> geoMap = SpecialInfos.SysGeoMap;
        Log.info(logger, uuid, "四级地址map", "geoMap.size", geoMap == null?null:geoMap.size());
        if(geoMap == null){
            outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.INSIDE_ERROR.getResultID(), null, inputData.getScope());
            return outputBean;
        }
        
        receiveAddrID = buildingJoin.getId();
        receiver = buildingJoin.getJoinName();
        phoneNumber = "";
        receiverMobile = buildingJoin.getJoinMobile();
        province = buildingJoin.getProvinceId();
        provinceName = geoMap.get(province).getGeoName();
        city = buildingJoin.getCityId();
        cityName = geoMap.get(city).getGeoName();
        district = buildingJoin.getDistrictId();
        districtName = geoMap.get(district).getGeoName();
        area = buildingJoin.getAreaId();
        areaName = area == null?"":geoMap.get(area).getGeoName();
        addrLine1 = buildingJoin.getAddress() + buildingJoin.getBuildingName()
                        + (buildingJoin.getBuildingNo() == null?"":buildingJoin.getBuildingNo()) + buildingJoin.getBuildingFloor();
        addrDetail = cityName + districtName + areaName + addrLine1;
        defaultPayId = null;
        lastGeoID = area == null?district:area;
        
        
        inputData.setReceiveAddrID(receiveAddrID);
        inputData.setLastGeoId(lastGeoID);
        inputData.setProvinceName(provinceName);
        inputData.setCityName(cityName);
        inputData.setDistrictName(districtName);
        inputData.setAddrDetail(addrDetail);
        inputData.setReceiver(receiver);
        inputData.setMobile(receiverMobile);
        
        
        
        /******************************************************************************自己书写地址结束 ********************************************************************************************/
        
        
        
        
        /****************************************************************************** 调用库存服务开始 ********************************************************************************************/
        if (inputData.getLastGeoId() == null) {
            Log.info(logger, uuid, "地址末节ID为空", "inputData", inputData);
            outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.CALLSERVER_ERROR.getResultID(), null, inputData.getScope());
            Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
            return outputBean;
        }
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
            outputBean = ResponseUtil.getBaseOutputBean(BaseOrderResultEnum.BUIILDING_NO_STOCK.getResultID(), null, inputData.getScope());
            Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
            return outputBean;
        }
        Log.info(logger, uuid, "判断库存结束", "goodsStock", goodsStock);
        /****************************************************************************** 调用库存服务结束 ********************************************************************************************/
        
        
        
        /****************************************************************************** 调用配送服务开始 ********************************************************************************************/
        String timeShipType = TimeShipTypeEnum.NORMAL.getC();
        Map<Long, Map<Long, List<ShippingGetGoodsShippingInfosShippingRulesElementO>>> goodsWarehouseRule = new HashMap<Long, Map<Long, List<ShippingGetGoodsShippingInfosShippingRulesElementO>>>();
        Object o = this.callShippingService(goodsWarehouseRule, false, inputData, goodsCount, goodsInfo, timeShipType, goodsStock);
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
        BigDecimal orderFee = BigDecimal.ZERO;
        BigDecimal shipFee = BigDecimal.ZERO;
        BigDecimal shipTotalFee = BigDecimal.ZERO;
        BigDecimal deliveryTimeFee = BigDecimal.ZERO;

        Map<Long, BigDecimal> goodsFeeMap = new HashMap<Long, BigDecimal>();
        
        Map<String, OmsCreateOrderGoodsListElementI> goodsMap = new HashMap<String, OmsCreateOrderGoodsListElementI>();
        for (OmsCreateOrderGoodsListElementI good : inputData.getGoodsList()) {
            String key = "0";
            if ("2".equals(good.getCartMethod())) {// 换购
                key = "2";
            }
            if (goodsMap.containsKey(good.getGoodsID() + "-" + key)) {
                goodsMap.get(good.getGoodsID() + "-" + key).setGoodsCount(goodsMap.get(good.getGoodsID() + "-" + key).getGoodsCount()
                                + good.getGoodsCount());
            } else {
                goodsMap.put(good.getGoodsID() + "-" + key, good);
            }
        }
        for (Map.Entry<String, OmsCreateOrderGoodsListElementI> entry : goodsMap.entrySet()) {
            String[] array = entry.getKey().split("-");
            Log.info(logger, uuid, "购买商品ID和购买方式", "array", Arrays.toString(array));
            BigDecimal goodsPrice = null;
            if ((PromotionTypeEnum.SALE.getC().equals(goodsInfo.get(Long.valueOf(array[0])).getSkuInfo().getPromotionType()) || PromotionTypeEnum.HUAN_GOU.getC().equals(array[1]))
                            && PromotionScopeUtil.getPromotionScope(goodsInfo.get(Long.valueOf(array[0])).getSkuInfo().getPromotionScope().intValue()).contains(inputData.getScope())
                            && PromotionStatusEnum.GOING.getC().equals(goodsInfo.get(Long.valueOf(array[0])).getSkuInfo().getPromotionStatus())) {
                goodsPrice = (goodsInfo.get(Long.valueOf(array[0])).getSkuInfo().getPromotionPrice());
            } else {
                goodsPrice = (this.getCurrentPrice(goodsInfo, Long.valueOf(array[0]), entry.getValue().getGoodsCount(), params));
            }
            if (goodsPrice == null) {// 没有查询到商品价格
                goodsPrice = (goodsInfo.get(Long.valueOf(array[0])).getSkuInfo().getTheOriginalPrice());
            }
            orderFee = orderFee.add(goodsPrice.multiply(BigDecimal.valueOf(entry.getValue().getGoodsCount())));
            goodsFeeMap.put(Long.valueOf(array[0]), goodsPrice.multiply(BigDecimal.valueOf(entry.getValue().getGoodsCount())));
        }
        Log.info(logger, uuid, "获得订单总金额", "orderFee", orderFee);
        Log.info(logger, uuid, "获得商品对应金额", "goodsFeeMap", goodsFeeMap);
        
        
        
        BuildingActivityDao buildingActivityDao = new BuildingActivityDao(uuid, logger);
        BuildingActivityPO buildingActivityPO = buildingActivityDao.findBuildingActivityPOByID(inputData.getActivityId());
        Log.info(logger, uuid, "查询活动表结束", "buildingActivityPO", buildingActivityPO);
        BigDecimal payFee = buildingActivityPO.getPayFee(); //40
        BigDecimal reduceFee = buildingActivityPO.getReduceFee(); //100
        
        BigDecimal discountFee = BigDecimal.ZERO;      
        if (orderFee.compareTo(payFee) <= 0) {
            discountFee = BigDecimal.ZERO;
            shipFee = payFee.subtract(orderFee);
            shipTotalFee = payFee.subtract(orderFee);
        } else if (orderFee.compareTo(payFee) > 0 && orderFee.compareTo(reduceFee) <= 0) {
            discountFee = orderFee.subtract(payFee);
        } else {
            discountFee = reduceFee.subtract(payFee);
        }
        Log.info(logger, uuid, "获得订单满减金额", "discountFee", discountFee);
        
        
        // 保存团购订单的促销信息
        Map<Long, PromotionGetPromotionByIdDetailElementO> groupMap = new HashMap<Long, PromotionGetPromotionByIdDetailElementO>();
        Map<String, BigDecimal> discountMap = new HashMap<String, BigDecimal>();// 订单满减
        Map<String, BigDecimal> otherDiscountMap = new HashMap<String, BigDecimal>();// 其他满减
        
        
        int i = 1;
        BigDecimal sum = BigDecimal.ZERO;
        for (Map.Entry<Long, GoodsNumberBean> entry : goodsCount.entrySet()) {
            BigDecimal fee = BigDecimal.ZERO;
            if (i == goodsCount.size()) { // 最后一个
                fee = fee.add(discountFee.subtract(sum));
            } else {
                fee = fee.add(goodsFeeMap.get(entry.getKey()).multiply(discountFee).divide(orderFee, 2, BigDecimal.ROUND_HALF_UP));
            }
            discountMap.put(entry.getKey() + "", fee);
            sum = sum.add(fee);
            i++;
        }
        Log.info(logger, uuid, "拆分后商品对应满减金额", "discountMap", discountMap);
        
        
        boolean isTuangou = false;
        String receiveDate = new SimpleDateFormat("yyyy-MM-dd").format(buildingActivityPO.getReceiveDate());
        inputData.setReceiveDate(receiveDate);
        outputData.setSendDate(receiveDate);

        Map<Long, Set<GoodsWarehouseAndDcBean>> goodsWarehouseDc = this.getWarehouseAndDcList(isTuangou, goodsWarehouseRule, isCheckCOD, receiveDate);
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

        
        PromotionGetPromotionInfoOutputData promotionOutputData = new PromotionGetPromotionInfoOutputData();
        List<PromotionGetPromotionInfoDiscountListElementO> discountElementList = new ArrayList<PromotionGetPromotionInfoDiscountListElementO>();
        PromotionGetPromotionInfoDiscountListElementO discountElement = new PromotionGetPromotionInfoDiscountListElementO();
        discountElement.setDiscount(discountFee);
        discountElementList.add(discountElement);
        promotionOutputData.setDiscountList(discountElementList);
        
        
        inputData.setIsSetDefault(BooleanEnum.FALSE.getV());

        return createOrder(request, inputBean, inputData, outputBean, outputData, params, 
                        goodsCount, groupMap, goodsInfo, buyFromMap, 
                        goodsWarehouseAndDcBean, goodsShipCapacityMap, 
                        discountMap, otherDiscountMap, null, 
                        promotionOutputData, parentOrderDao, new StringBuffer(""), goodsIDMax, 
                        isIncludeSpecial, isCheckCOD, couponSN, receiveDate, 
                        BigDecimal.ZERO, shipFee, shipTotalFee, deliveryTimeFee, 
                        BigDecimal.ZERO, 
                        payMethodId, canReserve, haveOnlinePay, false, null, null);    
	}
	
	
    
    
    
    
    private Object callShippingService(Map<Long, Map<Long, List<ShippingGetGoodsShippingInfosShippingRulesElementO>>> goodsWarehouseRule, boolean isPower, OmsCreateOrderInputData inputData, HashMap<Long, GoodsNumberBean> goodsCount, Map<Long, GoodsGetGoodsInfoGoodsInfoElementO> goodsInfo, String timeShipType, Map<Long, Set<Long>> goodsStock) throws Exception{
        
        
        Log.info(logger, uuid, "组装shipping服务getGoodsShippingInfos方法所需参数开始");
        ShippingGetGoodsShippingInfosInputData getGoodsShippingInfosInputData = this.getGoodsShippingInfosInputData(isPower, inputData.getScope(), inputData.getOrderType(), inputData.getOrderFrom(), inputData.getSubstationID(), 0L, inputData.getLastGeoId(), goodsCount, goodsInfo, timeShipType);  
        
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
            Log.info(logger, uuid, "调用shipping服务getGoodsShippingInfos方法失败,接口返回", "outputBean", outputBean);
            return outputBean;
        }
        ShippingGetGoodsShippingInfosOutputData shippingOutputData = (ShippingGetGoodsShippingInfosOutputData)outputBean.getOutputData();
        
        Log.info(logger, uuid, "判断配送开始");
        // 保存验证错误时的错误商品
        Set<String> returnSetShipping = new HashSet<String>();
        // 商品库房配送规则
        
        // 商品库房配送日期
        Map<Long, Map<Long, Set<ShippingDateBean>>> goodsWarehouseDate = OrderUtil.checkShipping(false, inputData.getLastGeoId(), goodsStock, shippingOutputData, goodsCount, returnSetShipping, goodsInfo, goodsWarehouseRule);
        if (goodsWarehouseDate == null) {
            Log.info(logger, uuid, "有商品不可送达", "goodsList", returnSetShipping);
            return BaseOrderResultEnum.NOT_SHIP;
        }
        Log.info(logger, uuid, "判断配送结束", "goodsWarehouseDate", goodsWarehouseDate);
        
        Log.info(logger, uuid, "获得所有商品的可配送日期开始");
        Set<ShippingDateBean> allDates = OrderUtil.getAllGoodsDates(goodsWarehouseDate, logger, uuid);
        Log.info(logger, uuid, "获得所有商品的可配送日期结束", "allDates", allDates);
        
        /*if (BooleanEnum.TRUE.getV().equals(inputData.getDeliveryTimeType())) {// 选择了当日达
            if (!allDates.contains(new ShippingDateBean(new SimpleDateFormat("yyyy-MM-dd").format(new Date()), "", "", ""))) {
                Log.info(logger, uuid, "不支持当日达");
                return BaseOrderResultEnum.TODAY_IS_INVALID;
            }
        } else {
            allDates.remove(new ShippingDateBean(new SimpleDateFormat("yyyy-MM-dd").format(new Date()), "", "", ""));
        }*/

        return allDates;
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
    public ShippingGetGoodsShippingInfosInputData getGoodsShippingInfosInputData(boolean isPower, String scope, String orderType, String orderFrom, Long substationID, Long templeteId, Long lastGeoID, Map<Long, GoodsNumberBean> goodsCount, Map<Long, GoodsGetGoodsInfoGoodsInfoElementO> goodsInfo, String timeShipType) throws Exception {
        ShippingGetGoodsShippingInfosInputData goodsShippingInfosInputData = new ShippingGetGoodsShippingInfosInputData();
        goodsShippingInfosInputData.setScope(scope);
        goodsShippingInfosInputData.setOrderType(orderType);
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
            if (entry.getValue().getBuyNumber() > 0) {// 不是纯赠品
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
            
            /*if (sendDateList.isEmpty()) {// 不可同天送
                return null;
            }*/
            
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
            if (dateSet.isEmpty()) {// 楼宇如果日期是空，随便指定个日期
                ShippingGetGoodsShippingInfosShippingDatesElementI dateElement = new ShippingGetGoodsShippingInfosShippingDatesElementI();
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DATE, 1);
                
                dateElement.setShippingDate(new SimpleDateFormat("yyyy-MM-dd ").format(cal.getTime()));
                goodsShippingInfosInputData.getShippingDates().add(dateElement);
            }
            
            for (String date : dateSet) {
                ShippingGetGoodsShippingInfosShippingDatesElementI dateElement = new ShippingGetGoodsShippingInfosShippingDatesElementI();
                dateElement.setShippingDate(date);
                goodsShippingInfosInputData.getShippingDates().add(dateElement);
            }
            
        }
        
        return goodsShippingInfosInputData;
    }
    
    
    
    /**
     * 根据确定的日期和是否选择了COD,找出所有的库房和配送公司组合
     * Description:<br>
     * 
     * @param goodsWarehouseRule
     * @param isCheckCOD
     * @param receiveDate
     * @return
     * @throws Exception
     */
    public Map<Long, Set<GoodsWarehouseAndDcBean>> getWarehouseAndDcList(boolean isTuangou, Map<Long, Map<Long, List<ShippingGetGoodsShippingInfosShippingRulesElementO>>> goodsWarehouseRule, String isCheckCOD, String receiveDate) throws Exception {
        Map<Long, Set<GoodsWarehouseAndDcBean>> goodsWarehouseDc = new HashMap<Long, Set<GoodsWarehouseAndDcBean>>();
        for (Map.Entry<Long, Map<Long, List<ShippingGetGoodsShippingInfosShippingRulesElementO>>> entry : goodsWarehouseRule.entrySet()) {
            Set<GoodsWarehouseAndDcBean> beanList = new HashSet<GoodsWarehouseAndDcBean>();
            for (Map.Entry<Long, List<ShippingGetGoodsShippingInfosShippingRulesElementO>> entryInside : entry.getValue().entrySet()) {
                for (ShippingGetGoodsShippingInfosShippingRulesElementO ruleElement : entryInside.getValue()) {
                    beanList.add(new GoodsWarehouseAndDcBean(entryInside.getKey(), ruleElement.getShippingCompanyID()));
                    break;
                    
                }
            }
            goodsWarehouseDc.put(entry.getKey(), beanList);
        }
        return goodsWarehouseDc;
    }
    
    
}

package cn.tootoo.soa.oms.createorder.bussiness;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import cn.tootoo.db.egrocery.tosparentorder.TOsParentOrderDao;
import cn.tootoo.db.egrocery.webchatactivity.WebchatActivityDao;
import cn.tootoo.db.egrocery.webchatactivity.WebchatActivityPO;
import cn.tootoo.enums.BaseResultEnum;
import cn.tootoo.enums.BooleanEnum;
import cn.tootoo.global.Global;
import cn.tootoo.http.AuthorizeClient;
import cn.tootoo.http.TootooService;
import cn.tootoo.http.bean.BaseOutputBean;
import cn.tootoo.soa.authorize.getjetstatus.input.AuthorizeGetJETStatusInputData;
import cn.tootoo.soa.authorize.getjetstatus.output.AuthorizeGetJETStatusOutputData;
import cn.tootoo.soa.base.bean.GoodsNumberBean;
import cn.tootoo.soa.base.bean.GoodsWarehouseAndDcBean;
import cn.tootoo.soa.base.bean.ShippingDateBean;
import cn.tootoo.soa.base.enums.BaseOrderResultEnum;
import cn.tootoo.soa.base.enums.OrderTypeEnum;
import cn.tootoo.soa.base.enums.SubstationEnum;
import cn.tootoo.soa.base.enums.TimeShipTypeEnum;
import cn.tootoo.soa.base.global.SpecialInfos;
import cn.tootoo.soa.base.util.OrderUtil;
import cn.tootoo.soa.goods.getgoodsinfo.output.GoodsGetGoodsInfoGoodsInfoElementO;
import cn.tootoo.soa.oms.createorder.input.OmsCreateOrderGroupBuyingGoodsListElementI;
import cn.tootoo.soa.oms.createorder.input.OmsCreateOrderInputData;
import cn.tootoo.soa.oms.createorder.output.OmsCreateOrderOutputData;
import cn.tootoo.soa.oms.createorder.rpc.BaseResult;
import cn.tootoo.soa.oms.createorder.rpc.TuangouJielongCreateOrderRPC;
import cn.tootoo.soa.oms.getbuyerpromotionnum.output.OmsGetBuyerPromotionNumGoodsListElementO;
import cn.tootoo.soa.oms.getbuyerpromotionnum.output.OmsGetBuyerPromotionNumOutputData;
import cn.tootoo.soa.oms.setbuyerpromotionnum.input.OmsSetBuyerPromotionNumGoodsListElementI;
import cn.tootoo.soa.oms.setbuyerpromotionnum.input.OmsSetBuyerPromotionNumInputData;
import cn.tootoo.soa.oms.setbuyerpromotionnum.output.OmsSetBuyerPromotionNumOutputData;
import cn.tootoo.soa.oms.utils.SqlUtil;
import cn.tootoo.soa.promotion.getpromotionbyid.output.PromotionGetPromotionByIdDetailElementO;
import cn.tootoo.soa.promotion.getpromotionbyid.output.PromotionGetPromotionByIdOutputData;
import cn.tootoo.soa.promotion.getpromotionbyid.output.PromotionGetPromotionByIdPromotionListElementO;
import cn.tootoo.soa.promotion.getpromotioninfo.output.PromotionGetPromotionInfoGoodsListElementO;
import cn.tootoo.soa.promotion.getpromotioninfo.output.PromotionGetPromotionInfoNyListElementO;
import cn.tootoo.soa.promotion.getpromotioninfo.output.PromotionGetPromotionInfoOutputData;
import cn.tootoo.soa.shipping.getgoodsshippinginfos.output.ShippingGetGoodsShippingInfosShippingDatesElementO;
import cn.tootoo.soa.shipping.getgoodsshippinginfos.output.ShippingGetGoodsShippingInfosShippingRulesElementO;
import cn.tootoo.soa.shopping.checkisdelicacy.input.ShoppingCheckIsDelicacyInputData;
import cn.tootoo.soa.shopping.checkisdelicacy.output.ShoppingCheckIsDelicacyOutputData;
import cn.tootoo.utils.DateUtil;
import cn.tootoo.utils.Log;
import cn.tootoo.utils.Memcached;
import cn.tootoo.utils.ResponseUtil;
import cn.tootoo.utils.StringUtil;

/**
 * 与数据库交互，业务逻辑写到这个类里
 * @author shidake
 *
 */
public class TuangouJielongOrderBussiness {
	
    protected String uuid;
    protected Logger logger;

	private String isDelicacy = "0";//是否美食九段专享(微信接龙)0:否，1:是		
	private String isDelicacyUser = "0";//是否美食九段会员（0:否，1:是）
	private String isTootoo = "0";//是否沱沱粉专享(微信接龙)0:否，1:是		
	private String isTootooUser = "0";//是否沱沱粉会员（0:否，1:是）
	private boolean isJielong = false;
	private boolean isTuangou = false;
	private String receiveDate = "";
	private String receiveDateGroup = "";
	private Long templeteId = 0L;// 获得团购模板ID 
	private boolean groupWy = false;//
	private StringBuffer bsMobile = new StringBuffer("");//
	private BigDecimal shipFeeGroupbuy = BigDecimal.ZERO;//获得团购运费
	private BigDecimal sendAmount = BigDecimal.ZERO;//起送金额
	private BigDecimal noShipFeeAmount = BigDecimal.ZERO; //最低免邮金额
	private String shipDate = "";
	private String timeShipType = TimeShipTypeEnum.NORMAL.getC();
	private boolean isGiftOK = false;
	private BigDecimal orderFee  = BigDecimal.ZERO;
	private Map<Long, GoodsWarehouseAndDcBean> goodsWarehouseAndDcBean = new HashMap<Long, GoodsWarehouseAndDcBean>();
	private Map<Long, String> goodsShipCapacityMap = new HashMap<Long, String>();
	private Map<Long, Long> groupMaxBuyMap; // 每个商品设置的最大购买量
	private Map<Long, Long> groupHisBuyMap; // 每个商品的历史购买量
	
	private TuangouJielongCreateOrderRPC tuangouJielongCreateOrderRPC;
	
	public TuangouJielongOrderBussiness(String uuid, Logger logger , PromotionGetPromotionByIdOutputData promotionByIdOutputData){
    	super();
    	this.uuid=uuid;
    	this.logger=logger;
    	initFillData(promotionByIdOutputData);
    }
    
    /**
     * 初始化必要参数
     */
    private void initFillData(PromotionGetPromotionByIdOutputData promotionByIdOutputData){
    	
        templeteId = promotionByIdOutputData.getPromotionList().get(0).getRegionTempletId();
        Log.info(logger, uuid, "获得团购模板ID", "templeteId", templeteId);
        
        receiveDateGroup = promotionByIdOutputData.getPromotionList().get(0).getEndDt();
        Log.info(logger, uuid, "获得团购结束时间", "receiveDateGroup", receiveDateGroup);
        
        shipFeeGroupbuy = promotionByIdOutputData.getPromotionList().get(0).getShipFee();
        Log.info(logger, uuid, "获得团购运费", "shipFeeGroupbuy", shipFeeGroupbuy);
        
        sendAmount = promotionByIdOutputData.getPromotionList().get(0).getOrderValue();
        Log.info(logger, uuid, "起送金额", "sendAmount", sendAmount);
        
        noShipFeeAmount = promotionByIdOutputData.getPromotionList().get(0).getNoShipFeeAmount();
        Log.info(logger, uuid, "最低免邮金额", "noShipFeeAmount", noShipFeeAmount);
        
        shipDate = promotionByIdOutputData.getPromotionList().get(0).getShipDate();
        Log.info(logger, uuid, "获得配送时间", "shipDate", shipDate);
        
        /** add by zhaochunna for 2015-8-11 stat **/
        isDelicacy = promotionByIdOutputData.getPromotionList().get(0).getIsDelicacy();
        Log.info(logger, uuid, "获得九段专享标识", "isDelicacy", isDelicacy);
        /** add by zhaochunna for 2015-8-11 end **/
        
        /** add by zhaochunna for 2015-10-10 stat **/
        isTootoo = promotionByIdOutputData.getPromotionList().get(0).getIsTootoo();
        Log.info(logger, uuid, "获得沱沱粉专享活动标识", "isTootoo", isTootoo);	
        
        tuangouJielongCreateOrderRPC = new TuangouJielongCreateOrderRPC(uuid, logger);
    }
    
    
    /**
     * Description:<br>
     * 获得限制商品的用户历史购买量
     * 
     * @param inputData
     * @param params
     * @param promotionByIdOutputData
     * @return
     * @throws Exception 
     */
    public BaseOutputBean getBuyerPromotionNum(BaseOutputBean outputBean, OmsCreateOrderInputData inputData, HashMap<String, String> params, PromotionGetPromotionByIdOutputData promotionByIdOutputData) throws Exception{
        try{
            Set<Long> buyGoodsSet = new HashSet<Long>();// 本次购买商品
            for (OmsCreateOrderGroupBuyingGoodsListElementI groupGoodElement : inputData.getGroupBuyingGoodsList()) {
                buyGoodsSet.add(groupGoodElement.getGoodsID());
            }
            
            // 查询出本次购买商品中有限制最大购买量的商品
            groupMaxBuyMap = new HashMap<Long, Long>(); // 每个商品设置的最大购买量
            for (PromotionGetPromotionByIdPromotionListElementO groupElement : promotionByIdOutputData.getPromotionList()) {
                if (inputData.getGroupPromotionID().intValue() != groupElement.getPromotionId().intValue()) {
                    continue;
                }
                for (PromotionGetPromotionByIdDetailElementO detailElement : groupElement.getDetail()) {
                    if (detailElement.getMax_num_per_buyer() != null && detailElement.getMax_num_per_buyer() > 0
                                    && buyGoodsSet.contains(detailElement.getGoodsId())) {
                        groupMaxBuyMap.put(detailElement.getGoodsId(), detailElement.getMax_num_per_buyer());
                    }
                }
            }
            
            groupHisBuyMap = new HashMap<Long, Long>(); // 每个商品的历史购买量
            if (groupMaxBuyMap.size() > 0) {
                Log.info(logger, uuid, "商品设置的购买数量", "groupMaxBuyMap", groupMaxBuyMap);
                BaseResult getBuyerPromotionNumResult = tuangouJielongCreateOrderRPC.getBuyerPromotionNumRPC(inputData, params, promotionByIdOutputData, groupMaxBuyMap);
                if(!checkRPC(getBuyerPromotionNumResult , inputData.getScope())){
                    return getBuyerPromotionNumResult.getBaseOutputBean();
                }
                outputBean = getBuyerPromotionNumResult.getBaseOutputBean();
                OmsGetBuyerPromotionNumOutputData getBuyerNumO = (OmsGetBuyerPromotionNumOutputData)outputBean.getOutputData();
                if (getBuyerNumO.getPromotionList().size() >= 1 && getBuyerNumO.getPromotionList().get(0).getGoodsList() != null){
                    for (OmsGetBuyerPromotionNumGoodsListElementO goodsNum : getBuyerNumO.getPromotionList().get(0).getGoodsList()) {
                        groupHisBuyMap.put(goodsNum.getGoodsId(), goodsNum.getNum());
                    }
                }
                Log.info(logger, uuid, "用户之前的购买数量", "groupHisBuyMap", groupHisBuyMap);
            }
            
        } catch (RuntimeException r) {
            outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.INSIDE_ERROR.getResultID(), null, inputData.getScope());
            Log.error(logger, uuid, "运行时异常：+'方法名getBuyerPromotionNum:'", r);
        } catch (Exception e) {
            outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.INSIDE_ERROR.getResultID(), null, inputData.getScope());
            Log.error(logger, uuid, "服务异常：+'方法名getBuyerPromotionNum:'", e);
        }
        return outputBean;
    }
    
    
    
	/**
	 *
	 * 校验两种特殊团购(网易)
	 */
	public BaseOutputBean specialActivityInitAndFillData(BaseOutputBean outputBean,OmsCreateOrderInputData inputData,HashMap<String, String>  params,TOsParentOrderDao parentOrderDao){
    	if (inputData.getGroupPromotionID().intValue() == 48181 
    			|| inputData.getGroupPromotionID().intValue() == 48182
    			|| inputData.getGroupPromotionID().intValue() == 48224) {
    		groupWy =true;
        	
        	
            // 验证该用户是否绑定了手机号
            Log.info(logger, uuid, "验证该用户是否绑定了手机号", "buyerId", params.get(AuthorizeClient.COOKIE_BUYER_ID));
            if (!SqlUtil.checkUserMobile(logger, uuid, parentOrderDao.getWriteConnectionName(), params.get(AuthorizeClient.COOKIE_BUYER_ID), bsMobile)) {
                outputBean = ResponseUtil.getBaseOutputBean(170037, null, inputData.getScope());
                Log.info(logger, uuid, "此用户没有绑定手机号,接口返回", "outputBean", outputBean);
                return outputBean;
            }
            
            // TODO 验证一个用户只能有一个
            if (!SqlUtil.checkUserGroup(logger, uuid, parentOrderDao.getWriteConnectionName(), params.get(AuthorizeClient.COOKIE_BUYER_ID), inputData.getGroupPromotionID())) {
                outputBean = ResponseUtil.getBaseOutputBean(170036, null, inputData.getScope());
                Log.info(logger, uuid, "此用户已经购买了一个团购,接口返回", "outputBean", outputBean);
                return outputBean;
            }
    	}

		return outputBean;
	}
	
	/**
	 * 微信活动校验并且填充数据
	 */
	public BaseOutputBean webchatActivityInitAndFillData(BaseOutputBean outputBean,OmsCreateOrderInputData inputData,HashMap<String, String>  params){
		if("3".equals(inputData.getActivityType())){
	    	WebchatActivityDao webchatActivityDao = new WebchatActivityDao(uuid, logger);
	    	WebchatActivityPO po = webchatActivityDao.findWebchatActivityPOByID(inputData.getActivityId());
			if(po==null || po.getActivityId() == null || po.getActivityClickNum() == null || po.getActivityLinkPromotionid() == null){
				Log.info(logger, uuid, "微信团活动-没有活动信息", "activityId", inputData.getActivityId());
				outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.INSIDE_ERROR.getResultID(), null, inputData.getScope());
				Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
	            return outputBean;
	        }
			if(inputData.getGroupPromotionID().intValue() != po.getActivityLinkPromotionid().intValue()){//微信团活动对应的团购ID与下单入参的团购ID不同
				Log.info(logger, uuid, "微信团活动对应的团购ID与下单入参的团购ID不同", "promotionId", inputData.getGroupPromotionID().intValue(), "poPromotonId", po.getActivityLinkPromotionid());
				outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.INSIDE_ERROR.getResultID(), null, inputData.getScope());
				Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
	            return outputBean;
			}
			if(Memcached.getOK("webChatGroupbuy" + inputData.getActivityId() +params.get(AuthorizeClient.COOKIE_BUYER_ID))!=null && Memcached.getOK("webChatGroupbuy" + inputData.getActivityId() + params.get(AuthorizeClient.COOKIE_BUYER_ID)).equals("flushBuy")){
	    		Log.info(logger, uuid, "荔枝团购订单每个用户只能下一单", "buyerId", params.get(AuthorizeClient.COOKIE_BUYER_ID), "activityId", inputData.getActivityId());
	            outputBean = ResponseUtil.getBaseOutputBean(162389, null, inputData.getScope());
	            Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
	            return outputBean;
	    	}
			
		}
		return outputBean;
	}
	
	/**
	 * 沱沱惠团购商品限购并且填充数据
	 */
	public BaseOutputBean tootoohuiActivityInitAndFillData(BaseOutputBean outputBean,OmsCreateOrderInputData inputData,HashMap<String, String>  params){
		if (OrderTypeEnum.BAOYOU_TUANGOU_ORDER.getC().equals(inputData.getOrderType()) && ("67025".equals(inputData.getGroupPromotionID() + "")) ) {
	        Log.info(logger, uuid, "指定沱沱惠促销ID的每个用户只能下一次，查询缓存判断是否下过", "promotionId", inputData.getGroupPromotionID());
	        String key = "oncePromotion-" + inputData.getGroupPromotionID() + "-" + params.get(AuthorizeClient.COOKIE_BUYER_ID);
	        Object buyTimeCache = Memcached.get(key);
	        Log.info(logger, uuid, "从缓存中取出数据", "buyTimeCache", buyTimeCache);
	        if (buyTimeCache != null) {
	            Log.info(logger, uuid, "指定沱沱惠促销ID的每个用户只能下一单", "buyerId", params.get(AuthorizeClient.COOKIE_BUYER_ID), "activityId", inputData.getActivityId());
	            outputBean = ResponseUtil.getBaseOutputBean(BaseOrderResultEnum.ONCE_PROMOTION.getResultID(), null, inputData.getScope());
	            Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
	            return outputBean;
	        }
		}
		return outputBean;
	}
	
	/**
	 * 九段会员专享
	 * @throws Exception 
	 */
	public BaseOutputBean nineExclusiveActivityInitAndFillData(BaseOutputBean outputBean,OmsCreateOrderInputData inputData,HashMap<String, String>  params) throws Exception{
			
	        if(OrderTypeEnum.JIELONG_GROUPBUY.getC().equals(inputData.getOrderType()) && (StringUtil.isEmpty(templeteId.toString()) ||  !templeteId.toString().equals("0"))){//接龙团购
	            isJielong = true;
	        }
	        Log.info(logger, uuid, "是否接龙团购", "isJielong", isJielong);
	        /** add by zhaochunna for 2015-8-11 stat **/ 
	        //判断团购是否为九段专享，如果是 验证会员是否为九段会员
	        ShoppingCheckIsDelicacyOutputData checkIsDelicacyOutputData = null;
	        if(isJielong && "1".equals(isDelicacy)){
	        	Log.info(logger, uuid, "组装shopping服务checkIsDelicacy方法所需参数开始");
	        	ShoppingCheckIsDelicacyInputData checkIsDelicacyInputData =  new ShoppingCheckIsDelicacyInputData();
	        	checkIsDelicacyInputData.setScope(inputData.getScope());
	        	HashMap<String, String> checkIsDelicacyParams = (HashMap<String, String>)params.clone();
	        	checkIsDelicacyParams.put("method", "checkIsDelicacy");
	        	checkIsDelicacyParams.put("req_str", checkIsDelicacyInputData.toJson());
	            Log.info(logger, uuid, "组装shopping服务checkIsDelicacy方法所需参数结束");
	            
	            Log.info(logger, uuid, "调用shopping服务checkIsDelicacy方法开始", "checkIsDelicacyParams", checkIsDelicacyParams);
	            outputBean = TootooService.callServer("shopping", checkIsDelicacyParams, "post", new ShoppingCheckIsDelicacyOutputData());
	            Log.info(logger, uuid, "调用shopping服务checkIsDelicacy方法结束", "outputBean", outputBean);
	            
	            if (!TootooService.checkService(outputBean, "shopping", "checkIsDelicacy", inputData.getScope())) {
	                Log.info(logger, uuid, "调用shopping服务checkIsDelicacy方法失败,接口返回", "outputBean", outputBean);
	                return outputBean;
	            }
	            
	            checkIsDelicacyOutputData = (ShoppingCheckIsDelicacyOutputData)outputBean.getOutputData();
	            isDelicacyUser = checkIsDelicacyOutputData.getIsDelicacy();
	            Log.info(logger, uuid, "获得当前登录用户是否是九段会员", "isDelicacyUser", isDelicacyUser);
	            
	            if(!"1".endsWith(isDelicacyUser)){
	            	Log.info(logger, uuid, "九段专享必须九段会员才能购买，接口返回", "outputBean", outputBean);
	            	outputBean = ResponseUtil.getBaseOutputBean(BaseOrderResultEnum.USER_NOT_DELICACY.getResultID(), null, inputData.getScope());
	                return outputBean;
	            }
	        }
			return outputBean;
	}
	
	/**
	 * jet会员,沱粉专享会员
	 * @throws Exception 
	 */
	public BaseOutputBean jetActivityInitAndFillData(BaseOutputBean outputBean,OmsCreateOrderInputData inputData,HashMap<String, String>  params,String couponSN) throws Exception{
		if(OrderTypeEnum.JIELONG_GROUPBUY.getC().equals(inputData.getOrderType()) && (StringUtil.isEmpty(templeteId.toString()) ||  !templeteId.toString().equals("0"))){//接龙团购
            isJielong = true;
        } 
		AuthorizeGetJETStatusOutputData getGETStatusoutputData = null;
	        if(isJielong && "1".equals(isTootoo) && "4".equals(inputData.getActivityType())){
	        	Log.info(logger, uuid, "组装authorize服务getJETStatus方法所需参数开始");
	        	AuthorizeGetJETStatusInputData getGETStatusInputData  =  new AuthorizeGetJETStatusInputData();
	        	getGETStatusInputData.setScope(inputData.getScope());
	        	HashMap<String, String> getGETStatusParams = (HashMap<String, String>)params.clone();
	        	getGETStatusParams.put("method", "getJETStatus");
	        	getGETStatusParams.put("req_str", getGETStatusInputData.toJson());
	            Log.info(logger, uuid, "组装authorize服务getJETStatus方法所需参数结束");
	            
	            Log.info(logger, uuid, "调用authorize服务getJETStatus方法开始", "getGETStatusParams", getGETStatusParams);
	            outputBean = TootooService.callServer("authorize", getGETStatusParams, "post", new AuthorizeGetJETStatusOutputData());
	            Log.info(logger, uuid, "调用authorize服务getJETStatus方法结束", "outputBean", outputBean);
	            
	            if (!TootooService.checkService(outputBean, "authorize", "getJETStatus", inputData.getScope())) {
	                Log.info(logger, uuid, "调用authorize服务getJETStatus方法失败,接口返回", "outputBean", outputBean);
	                return outputBean;
	            }
	            
	            getGETStatusoutputData = (AuthorizeGetJETStatusOutputData)outputBean.getOutputData();
	            isTootooUser = getGETStatusoutputData.getStatus();
	            Log.info(logger, uuid, "获得当前登录用户是否是沱粉会员", "isTootooUser", isTootooUser);
	            
	            if(!"1".endsWith(isTootooUser)){
	            	Log.info(logger, uuid, "沱粉专享必须沱粉会员才能购买，接口返回", "outputBean", outputBean);
	            	outputBean = ResponseUtil.getBaseOutputBean(BaseOrderResultEnum.USER_NOT_TOOTOO.getResultID(), null, inputData.getScope());
	                return outputBean;
	            }
	            
	            //验证优惠券号是否weinull
	            if(!StringUtil.isEmpty(couponSN)){
	            	Log.info(logger, uuid, "沱粉专享不能使用优惠券，接口返回", "couponSN", couponSN);
	            	outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.INSIDE_ERROR.getResultID(), null, inputData.getScope());
	                return outputBean;
	            }
	        }
			return outputBean;
	}
	
	
	
	/**
	 * 判断是否选择了当日达
	 * @throws Exception 
	 */
	public BaseOutputBean isDeliveryTimeAndFillData(BaseOutputBean outputBean,OmsCreateOrderInputData inputData,boolean canReserve) throws Exception{
		
		
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
    return outputBean;
	}
	
	/**
	 * 设置收货时间
	 */
	
	public BaseOutputBean setReceiveDateAndFillData(BaseOutputBean outputBean,OmsCreateOrderOutputData outputData,OmsCreateOrderInputData inputData,HashMap<Long, GoodsNumberBean> goodsCount,PromotionGetPromotionByIdOutputData promotionByIdOutputData,String isIncludeSpecial, String isCheckCOD,Set<ShippingDateBean> allDates) throws Exception{
		
		
        String receiveDateMsg = "";
        if ((OrderTypeEnum.JIELONG_GROUPBUY.getC().equals(inputData.getOrderType()) && isJielong)) {// 接龙团购
            isTuangou = true;
            if(StringUtil.isEmpty(shipDate)){
                Log.info(logger, uuid, "从促销获取的shipDate为空，按老的方式设置日期", "shipDate", shipDate);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(DateUtil.strToDateTime(receiveDateGroup));
                calendar.add(Calendar.DATE, 4);                    
                receiveDate = DateUtil.dateToStr(calendar.getTime());
            }else{
                receiveDate = shipDate;
            }
            outputData.setSendDate(receiveDate);
        }else if(OrderTypeEnum.BAOYOU_TUANGOU_ORDER.getC().equals(inputData.getOrderType())){// 包邮团购
            List<PromotionGetPromotionByIdDetailElementO> promotionDetailList = promotionByIdOutputData.getPromotionList().get(0).getDetail();
            for (Long goodsId :goodsCount.keySet()) {
                for (PromotionGetPromotionByIdDetailElementO promotionGetPromotionByIdDetailElementO : promotionDetailList) {
                    if(goodsId.equals(promotionGetPromotionByIdDetailElementO.getGoodsId())){
                        receiveDate = promotionGetPromotionByIdDetailElementO.getShipDate();
                        receiveDateMsg = promotionGetPromotionByIdDetailElementO.getShipDateShow();
                        break;
                    }
                }
                if(!StringUtil.isEmpty(receiveDate)){
                    break;
                }
            }
            
            if(StringUtil.isEmpty(receiveDate)){
                Log.info(logger, uuid, "从促销获取的shipDate为空，按老的方式设置日期", "receiveDate", receiveDate);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(DateUtil.strToDateTime(receiveDateGroup));
                calendar.add(Calendar.DATE, 1);
                receiveDate = DateUtil.dateToStr(calendar.getTime());
            }
            outputData.setSendDate(receiveDate);
            outputData.setReceiveDt(receiveDateMsg);
        }else{
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
        }
        Log.info(logger, uuid, "收货日期", "receiveDate", receiveDate);
        return outputBean;
	}
	
	
	/**
	 * 指定商品的库房和配送公司
	 */
	public BaseOutputBean setWarehouseAndDCAndFillData(BaseOutputBean outputBean,Map<Long, Map<Long, List<ShippingGetGoodsShippingInfosShippingRulesElementO>>> goodsWarehouseRule , String isCheckCOD) throws Exception{
		
		
		Map<Long, Set<GoodsWarehouseAndDcBean>> goodsWarehouseDc = this.getWarehouseAndDcList(isTuangou, goodsWarehouseRule, isCheckCOD, receiveDate);
        Log.info(logger, uuid, "根据确定的日期和是否选择了COD,找出所有的库房和配送公司组合", "goodsWarehouseDc", goodsWarehouseDc);
        
        goodsWarehouseAndDcBean = OrderUtil.getWarehouseAndDc(goodsWarehouseDc);
        Log.info(logger, uuid, "按照最少拆单原则,指定商品的库房和配送公司开始", "goodsWarehouseAndDcBean", goodsWarehouseAndDcBean);
        
        // 商品在本库房本配送公司下，是否考虑运力
        
        for (Map.Entry<Long, GoodsWarehouseAndDcBean> entry : goodsWarehouseAndDcBean.entrySet()) {
            for (ShippingGetGoodsShippingInfosShippingRulesElementO ruleElement : goodsWarehouseRule.get(entry.getKey()).get(entry.getValue().getWarehouseID())) {
                if (entry.getValue().getDC().intValue() == ruleElement.getShippingCompanyID().intValue()) {
                    goodsShipCapacityMap.put(entry.getKey(), ruleElement.getConsiderShippingCapacity());
                    break;
                }
            }
        }
        return outputBean;
	}
	
	
	/**
	 * 获取运费，为了商品总价(不包括运费)没有超过活动限额,把赠品中的此赠品删除 做的
	 */
	
	public BaseOutputBean setOrderFeeAndFillData(BaseOutputBean outputBean, OmsCreateOrderInputData inputData ,HashMap<Long, GoodsNumberBean> goodsCount, Map<Long, GoodsGetGoodsInfoGoodsInfoElementO> goodsInfo,Map<Long, PromotionGetPromotionByIdDetailElementO> groupMap, boolean isNeedGift, Long giftGoodsId,
			int giftGoodsNum,String couponSN) throws Exception{
		orderFee = getOrderFee(inputData, null, goodsCount, goodsInfo, groupMap);
	    Log.info(logger, uuid, "获得订单的订单金额", "orderFee", orderFee);
	    
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
	    return outputBean;
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
                    
                    if(isTuangou){
                        if (BooleanEnum.TRUE.getV().equals(isCheckCOD)) {// 选择了COD
                            if(BooleanEnum.TRUE.getV().equals(ruleElement.getSupportCOD())){
                                beanList.add(new GoodsWarehouseAndDcBean(entryInside.getKey(), ruleElement.getShippingCompanyID()));
                            }
                        }else{
                            beanList.add(new GoodsWarehouseAndDcBean(entryInside.getKey(), ruleElement.getShippingCompanyID()));
                        }
                    }else{
                        if (BooleanEnum.TRUE.getV().equals(isCheckCOD)) {// 选择了COD
                            for (ShippingGetGoodsShippingInfosShippingDatesElementO dateElement : ruleElement.getShippingDates()) {
                                // if (dateElement.getShippingDate().equals(receiveDate) && BooleanEnum.TRUE.getV().equals(ruleElement.getSupportCOD())) {
                                if (BooleanEnum.TRUE.getV().equals(ruleElement.getSupportCOD())) {
                                    beanList.add(new GoodsWarehouseAndDcBean(entryInside.getKey(), ruleElement.getShippingCompanyID()));
                                    break;
                                }
                            }
                        } else {
                            for (ShippingGetGoodsShippingInfosShippingDatesElementO dateElement : ruleElement.getShippingDates()) {
                                //if (dateElement.getShippingDate().equals(receiveDate)) {
                                beanList.add(new GoodsWarehouseAndDcBean(entryInside.getKey(), ruleElement.getShippingCompanyID()));
                                break;
                                //}
                            }
                        }
                    }
                }
            }
            goodsWarehouseDc.put(entry.getKey(), beanList);
        }
        return goodsWarehouseDc;
    }
	
    
    
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
     * 下单服务 插入数据
     * @return
     */
    

    public String getTimeShipType() {
		return timeShipType;
	}

	public boolean isTuangou() {
		return isTuangou;
	}

	public void setTuangou(boolean isTuangou) {
		this.isTuangou = isTuangou;
	}

	public String getReceiveDate() {
		return receiveDate;
	}

	public void setReceiveDate(String receiveDate) {
		this.receiveDate = receiveDate;
	}

	public boolean isGiftOK() {
		return isGiftOK;
	}

	public void setGiftOK(boolean isGiftOK) {
		this.isGiftOK = isGiftOK;
	}

	public BigDecimal getOrderFee() {
		return orderFee;
	}

	public void setOrderFee(BigDecimal orderFee) {
		this.orderFee = orderFee;
	}

	public Map<Long, String> getGoodsShipCapacityMap() {
		return goodsShipCapacityMap;
	}

	public void setGoodsShipCapacityMap(Map<Long, String> goodsShipCapacityMap) {
		this.goodsShipCapacityMap = goodsShipCapacityMap;
	}

	public void setTimeShipType(String timeShipType) {
		this.timeShipType = timeShipType;
	}

	public String getIsDelicacy() {
		return isDelicacy;
	}

	public void setIsDelicacy(String isDelicacy) {
		this.isDelicacy = isDelicacy;
	}

	public String getIsDelicacyUser() {
		return isDelicacyUser;
	}

	public void setIsDelicacyUser(String isDelicacyUser) {
		this.isDelicacyUser = isDelicacyUser;
	}

	public String getIsTootoo() {
		return isTootoo;
	}

	public void setIsTootoo(String isTootoo) {
		this.isTootoo = isTootoo;
	}

	public String getIsTootooUser() {
		return isTootooUser;
	}

	public void setIsTootooUser(String isTootooUser) {
		this.isTootooUser = isTootooUser;
	}

	public boolean isJielong() {
		return isJielong;
	}

	public void setJielong(boolean isJielong) {
		this.isJielong = isJielong;
	}

	public String getReceiveDateGroup() {
		return receiveDateGroup;
	}

	public void setReceiveDateGroup(String receiveDateGroup) {
		this.receiveDateGroup = receiveDateGroup;
	}

	public Long getTempleteId() {
		return templeteId;
	}

	public void setTempleteId(Long templeteId) {
		this.templeteId = templeteId;
	}

	public boolean isGroupWy() {
		return groupWy;
	}

	public void setGroupWy(boolean groupWy) {
		this.groupWy = groupWy;
	}

	public StringBuffer getBsMobile() {
		return bsMobile;
	}

	public void setBsMobile(StringBuffer bsMobile) {
		this.bsMobile = bsMobile;
	}

	public BigDecimal getShipFeeGroupbuy() {
		return shipFeeGroupbuy;
	}

	public void setShipFeeGroupbuy(BigDecimal shipFeeGroupbuy) {
		this.shipFeeGroupbuy = shipFeeGroupbuy;
	}

	public BigDecimal getSendAmount() {
		return sendAmount;
	}

	public void setSendAmount(BigDecimal sendAmount) {
		this.sendAmount = sendAmount;
	}

	public String getShipDate() {
		return shipDate;
	}

	public void setShipDate(String shipDate) {
		this.shipDate = shipDate;
	}

	public Map<Long, GoodsWarehouseAndDcBean> getGoodsWarehouseAndDcBean() {
		return goodsWarehouseAndDcBean;
	}

	public void setGoodsWarehouseAndDcBean(
			Map<Long, GoodsWarehouseAndDcBean> goodsWarehouseAndDcBean) {
		this.goodsWarehouseAndDcBean = goodsWarehouseAndDcBean;
	}

    
    public BigDecimal getNoShipFeeAmount() {
        return noShipFeeAmount;
    }

    
    public void setNoShipFeeAmount(BigDecimal noShipFeeAmount) {
        this.noShipFeeAmount = noShipFeeAmount;
    }
    
    
    public Map<Long, Long> getGroupMaxBuyMap() {
        return groupMaxBuyMap;
    }

    
    public void setGroupMaxBuyMap(Map<Long, Long> groupMaxBuyMap) {
        this.groupMaxBuyMap = groupMaxBuyMap;
    }

    
    public Map<Long, Long> getGroupHisBuyMap() {
        return groupHisBuyMap;
    }

    
    public void setGroupHisBuyMap(Map<Long, Long> groupHisBuyMap) {
        this.groupHisBuyMap = groupHisBuyMap;
    }

    /**
     * 校验RPC返回接口数据是否成功。
     */
    private boolean checkRPC(BaseResult result ,String scope) throws Exception {
        
        if (result == null || result.getBaseOutputBean() == null || result.getBaseOutputBean().getOutputHead() == null
                || result.getBaseOutputBean().getOutputHead().getResultID() == null) {
            result.getBaseOutputBean().setOutputHead(Global.getOutputHead(BaseResultEnum.CALLSERVER_ERROR.getResultID(), scope, null));
            result.getBaseOutputBean().setOutputData(null);
            return false;
        }
        if (result.getStatus() != BaseResultEnum.SUCCESS.getResultID() || result.getBaseOutputBean().getOutputHead().getResultID() != BaseResultEnum.SUCCESS.getResultID() ) {
            return false;
        }
        return true; 
    }

}

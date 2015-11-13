package cn.tootoo.soa.oms.createorder.rpc;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import cn.tootoo.enums.BaseResultEnum;
import cn.tootoo.enums.BooleanEnum;
import cn.tootoo.http.AuthorizeClient;
import cn.tootoo.http.TootooService;
import cn.tootoo.http.bean.BaseOutputBean;
import cn.tootoo.soa.address.getaddressbyid.input.AddressGetAddressByIDInputData;
import cn.tootoo.soa.address.getaddressbyid.output.AddressGetAddressByIDOutputData;
import cn.tootoo.soa.base.bean.GoodsNumberBean;
import cn.tootoo.soa.base.bean.ShippingDateBean;
import cn.tootoo.soa.base.enums.ActiveTypeEnum;
import cn.tootoo.soa.base.enums.BaseOrderResultEnum;
import cn.tootoo.soa.base.enums.OrderTypeEnum;
import cn.tootoo.soa.base.global.SpecialInfos;
import cn.tootoo.soa.base.util.OrderUtil;
import cn.tootoo.soa.goods.getgoodsinfo.output.GoodsGetGoodsInfoGoodsInfoElementO;
import cn.tootoo.soa.oms.createorder.input.OmsCreateOrderGroupBuyingGoodsListElementI;
import cn.tootoo.soa.oms.createorder.input.OmsCreateOrderInputData;
import cn.tootoo.soa.oms.getbuyerpromotionnum.input.OmsGetBuyerPromotionNumGoodsListElementI;
import cn.tootoo.soa.oms.getbuyerpromotionnum.input.OmsGetBuyerPromotionNumInputData;
import cn.tootoo.soa.oms.getbuyerpromotionnum.input.OmsGetBuyerPromotionNumPromotionListElementI;
import cn.tootoo.soa.oms.getbuyerpromotionnum.output.OmsGetBuyerPromotionNumGoodsListElementO;
import cn.tootoo.soa.oms.getbuyerpromotionnum.output.OmsGetBuyerPromotionNumOutputData;
import cn.tootoo.soa.oms.setbuyerpromotionnum.input.OmsSetBuyerPromotionNumGoodsListElementI;
import cn.tootoo.soa.oms.setbuyerpromotionnum.input.OmsSetBuyerPromotionNumInputData;
import cn.tootoo.soa.oms.setbuyerpromotionnum.output.OmsSetBuyerPromotionNumOutputData;
import cn.tootoo.soa.promotion.getpromotionbyid.input.PromotionGetPromotionByIdInputData;
import cn.tootoo.soa.promotion.getpromotionbyid.input.PromotionGetPromotionByIdPromotionIdListElementI;
import cn.tootoo.soa.promotion.getpromotionbyid.output.PromotionGetPromotionByIdDetailElementO;
import cn.tootoo.soa.promotion.getpromotionbyid.output.PromotionGetPromotionByIdOutputData;
import cn.tootoo.soa.promotion.getpromotionbyid.output.PromotionGetPromotionByIdPromotionListElementO;
import cn.tootoo.soa.promotion.increamenttuangoucount.input.PromotionIncreamentTuangouCountGoodsInfoElementI;
import cn.tootoo.soa.promotion.increamenttuangoucount.input.PromotionIncreamentTuangouCountInputData;
import cn.tootoo.soa.promotion.increamenttuangoucount.output.PromotionIncreamentTuangouCountOutputData;
import cn.tootoo.soa.promotion.updateweixinjielongbuyhistory.input.PromotionUpdateWeixinjielongBuyHistoryBuyHistoryElementI;
import cn.tootoo.soa.promotion.updateweixinjielongbuyhistory.input.PromotionUpdateWeixinjielongBuyHistoryInputData;
import cn.tootoo.soa.promotion.updateweixinjielongbuyhistory.output.PromotionUpdateWeixinjielongBuyHistoryOutputData;
import cn.tootoo.soa.shipping.getgoodsshippingfee.input.ShippingGetGoodsShippingFeeInputData;
import cn.tootoo.soa.shipping.getgoodsshippingfee.output.ShippingGetGoodsShippingFeeOutputData;
import cn.tootoo.soa.shipping.getgoodsshippinginfos.input.ShippingGetGoodsShippingInfosInputData;
import cn.tootoo.soa.shipping.getgoodsshippinginfos.output.ShippingGetGoodsShippingInfosOutputData;
import cn.tootoo.soa.shipping.getgoodsshippinginfos.output.ShippingGetGoodsShippingInfosShippingRulesElementO;
import cn.tootoo.soa.stock.getsalestock.input.StockGetSaleStockInputData;
import cn.tootoo.soa.stock.getsalestock.output.StockGetSaleStockOutputData;
import cn.tootoo.utils.Log;
import cn.tootoo.utils.Memcached;
import cn.tootoo.utils.ResponseUtil;
import cn.tootoo.utils.StringUtil;

/**
 * 团购RPC接口服务
 * @author shidake
 *
 */

public class TuangouJielongCreateOrderRPC extends BaseRPC{
    
    public TuangouJielongCreateOrderRPC(String uuid, Logger logger){
        super(uuid, logger);
    }
	
	/**
	 * 调用促销服务
	 * @return 
	 * @throws Exception 
	 */
	public BaseOutputBean getPromotionPRC(OmsCreateOrderInputData inputData) throws Exception{
		BaseOutputBean outputBean = new BaseOutputBean();
		try {
			Log.info(logger, uuid, "组装promotion服务getPromotionById方法所需参数开始");
			PromotionGetPromotionByIdInputData promotionGetPromotionByIdInputData = new PromotionGetPromotionByIdInputData();
			promotionGetPromotionByIdInputData.setScope(inputData.getScope());
			if(OrderTypeEnum.BAOYOU_TUANGOU_ORDER.getC().equals(inputData.getOrderType()) && ActiveTypeEnum.ITEMGROUP.getC().equals(inputData.getActivityType())){
                //单品团
                promotionGetPromotionByIdInputData.setPromotionType("k");
            }else if(OrderTypeEnum.BAOYOU_TUANGOU_ORDER.getC().equals(inputData.getOrderType()) && ActiveTypeEnum.ORIGINSEND.getC().equals(inputData.getActivityType())){
                //产地直发
                promotionGetPromotionByIdInputData.setPromotionType("m");
            }else if(OrderTypeEnum.JIELONG_GROUPBUY.getC().equals(inputData.getOrderType()) && ActiveTypeEnum.BRANDGROUP.getC().equals(inputData.getActivityType())){
                //品牌团
                promotionGetPromotionByIdInputData.setPromotionType("l");
            }else if(OrderTypeEnum.JIELONG_GROUPBUY.getC().equals(inputData.getOrderType())){
                //接龙团购
                promotionGetPromotionByIdInputData.setPromotionType("j");
            }else{
                //包邮团购
                promotionGetPromotionByIdInputData.setPromotionType("e");
            }
			PromotionGetPromotionByIdPromotionIdListElementI promotionElement = new PromotionGetPromotionByIdPromotionIdListElementI();
			promotionElement.setPromotionId(inputData.getGroupPromotionID());
			promotionGetPromotionByIdInputData.getPromotionIdList().add(promotionElement);
			Map<String, String> promotionGetPromotionByIdServiceParams = new HashMap<String, String>();
			promotionGetPromotionByIdServiceParams.put("r", "TGetPromotionById");
			promotionGetPromotionByIdServiceParams.put("req_str", promotionGetPromotionByIdInputData.toJson());
			Log.info(logger, uuid, "组装promotion服务getPromotionById方法所需参数结束");

			Log.info(logger, uuid, "调用promotion服务getPromotionById方法开始", "promotionGetPromotionByIdServiceParams", promotionGetPromotionByIdServiceParams);
			outputBean = TootooService.callServer("promotion", promotionGetPromotionByIdServiceParams, "post", new PromotionGetPromotionByIdOutputData());
			Log.info(logger, uuid, "调用promotion服务getPromotionById方法结束", "outputBean", outputBean);
		} catch (RuntimeException r) {
			Log.error(logger, uuid, "运行时异常：+'方法名getPromotionPRC:'", r);
		} catch (Exception e) {
			Log.error(logger, uuid, "服务异常：+'方法名getPromotionPRC:'", e);
		}
//	        if (!TootooService.checkService(outputBean, "promotion", "getPromotionById", inputData.getScope())) {
//	            Log.info(logger, uuid, "调用promotion服务getPromotionById方法失败,接口返回", "outputBean", outputBean);
//	            return outputBean;
//	        }
			return outputBean;     
	   }
	
	
	/**
	 * Description:<br>
	 * 获得限制商品的用户历史购买量
	 * 
	 * @param inputData
	 * @param promotionByIdOutputData
	 * @return
	 */
	public BaseResult getBuyerPromotionNumRPC(OmsCreateOrderInputData inputData, HashMap<String, String> params, PromotionGetPromotionByIdOutputData promotionByIdOutputData, Map<Long, Long> groupMaxBuyMap){
	    BaseResult result = new BaseResult();
	    BaseOutputBean outputBean = new BaseOutputBean();
	    try{
	        
	        OmsGetBuyerPromotionNumInputData getBuyerNumI = new OmsGetBuyerPromotionNumInputData();
            getBuyerNumI.setScope(inputData.getScope());
            List<OmsGetBuyerPromotionNumPromotionListElementI> promotionListI = new ArrayList<OmsGetBuyerPromotionNumPromotionListElementI>();
            OmsGetBuyerPromotionNumPromotionListElementI promotionI = new OmsGetBuyerPromotionNumPromotionListElementI();
            promotionI.setPromotionId(inputData.getGroupPromotionID());
            List<OmsGetBuyerPromotionNumGoodsListElementI> goodsList = new ArrayList<OmsGetBuyerPromotionNumGoodsListElementI>();
            for (Long maxBuyGoodsId : groupMaxBuyMap.keySet()) {
                OmsGetBuyerPromotionNumGoodsListElementI goodsId = new OmsGetBuyerPromotionNumGoodsListElementI();
                goodsId.setGoodsId(maxBuyGoodsId);
                goodsList.add(goodsId);
            }
            promotionI.setGoodsList(goodsList);
            promotionListI.add(promotionI);
            getBuyerNumI.setPromotionList(promotionListI);
            
            HashMap<String, String> getBuyerPromotionNumServiceParams = (HashMap<String, String>)params.clone();
            getBuyerPromotionNumServiceParams.put("method", "getBuyerPromotionNum");
            getBuyerPromotionNumServiceParams.put("req_str", getBuyerNumI.toJson());
            
            Log.info(logger, uuid, "调用oms服务getBuyerPromotionNum方法开始", "getBuyerPromotionNumServiceParams", getBuyerPromotionNumServiceParams);
            outputBean = TootooService.callServer("oms", getBuyerPromotionNumServiceParams, "post", new OmsGetBuyerPromotionNumOutputData());
            Log.info(logger, uuid, "调用oms服务getBuyerPromotionNum方法结束", "outputBean", outputBean);
            
            result.setBaseOutputBean(outputBean);
            result.setStatus(BaseResultEnum.SUCCESS.getResultID());
            result.setMessage("调用oms服务完毕");
            return result;
	        
	    } catch (RuntimeException r) {
            Log.error(logger, uuid, "运行时异常：+'方法名getBuyerPromotionNum:'", r);
        } catch (Exception e) {
            Log.error(logger, uuid, "服务异常：+'方法名getBuyerPromotionNum:'", e);
        }
        result.setBaseOutputBean(outputBean);
        return result;
	}
	
	
	
	
	/**
	 * 调用地址服务
	 *      
	 */
	public BaseResult getAddressRPC(OmsCreateOrderInputData inputData, HashMap<String, String>  params) throws Exception{
		
		BaseResult result = new BaseResult();
		BaseOutputBean outputBean = new BaseOutputBean();
		AddressGetAddressByIDOutputData addressOutputData = null;
		try{ 
			if (!"1".equals(inputData.getReqFrom())) {// 手机端调用，按照以前的规则
	
				Log.info(logger, uuid, "组装address服务getAddressByID方法所需参数开始");
				AddressGetAddressByIDInputData addressInputData = new AddressGetAddressByIDInputData();
				addressInputData.setScope(inputData.getScope());
				addressInputData.setShipAddrID(inputData.getReceiveAddrID());
				HashMap<String, String> addressServiceParams = (HashMap<String, String>) params.clone();
				addressServiceParams.put("method", "getAddressByID");
				addressServiceParams.put("req_str", addressInputData.toJson());
				Log.info(logger, uuid, "组装address服务getAddressByID方法所需参数结束");
	
				Log.info(logger, uuid, "调用address服务getAddressByID方法开始", "addressServiceParams", addressServiceParams);
				outputBean = TootooService.callServer("address", addressServiceParams, "post", new AddressGetAddressByIDOutputData());
				Log.info(logger, uuid, "调用address服务getAddressByID方法结束", "outputBean", outputBean);
	
				addressOutputData = (AddressGetAddressByIDOutputData) outputBean.getOutputData();
			} else {
	
				if (inputData.getLastGeoId() == null) {
					Log.info(logger, uuid, "地址末节ID为空", "inputData", inputData);
					outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.CALLSERVER_ERROR.getResultID(), null, inputData.getScope());
					result.setBaseOutputBean(outputBean);
					result.setStatus(BaseResultEnum.CALLSERVER_ERROR.getResultID());// 地址末节ID为空
					result.setMessage("地址末节ID为空,接口返回");// 
					return result;
				}
			}
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("addressOutputData", addressOutputData);
			result.setBaseOutputBean(outputBean);
			result.setParams(paramMap);
			result.setStatus(BaseResultEnum.SUCCESS.getResultID());
			result.setMessage("调用地址服务成功");
			return result;
		} catch (RuntimeException r) {
			Log.error(logger, uuid, "运行时异常：+'方法名getAddressAPC:'", r);
		} catch (Exception e) {
			Log.error(logger, uuid, "服务异常：+'方法名getAddressAPC:'", e);
		}
		result.setBaseOutputBean(outputBean);
		return result;
	}
	
	
	 /**
	  * 调用库存服务
	  */
	public BaseResult getSaleStockRPC (OmsCreateOrderInputData inputData ,HashMap<String, String>  params, HashMap<Long, GoodsNumberBean> goodsCount,Map<Long, GoodsGetGoodsInfoGoodsInfoElementO> goodsInfo ) throws Exception{
		
		BaseResult result = new BaseResult();
		BaseOutputBean outputBean = new BaseOutputBean();
		try{
			Log.info(logger, uuid, "组装stock服务getSaleStock方法所需参数开始");
			StockGetSaleStockInputData stockInputData = OrderUtil.getSaleStockInputData(inputData.getOrderType(), inputData.getSubstationID(), goodsCount, goodsInfo);
			Map<String, String> stockServiceParams = new HashMap<String, String>();
			stockServiceParams.put("method", "getSaleStock");
			stockServiceParams.put("req_str", stockInputData.toJson());
			Log.info(logger, uuid, "组装stock服务getSaleStock方法所需参数结束");
	
			Log.info(logger, uuid, "调用stock服务getSaleStock方法开始", "stockServiceParams", stockServiceParams);
			outputBean = TootooService.callServer("stock", stockServiceParams, "post", new StockGetSaleStockOutputData());
			Log.info(logger, uuid, "调用stock服务getSaleStock方法结束", "outputBean", outputBean);
	
			StockGetSaleStockOutputData stockOutputData = (StockGetSaleStockOutputData) outputBean.getOutputData();
	
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
				result.setBaseOutputBean(outputBean);
				result.setStatus(BaseOrderResultEnum.STOCK_NOT_ENOUGH.getResultID());// 待定义
				result.setMessage("有商品库存不足");// 待定义
				return result;
			}
			Log.info(logger, uuid, "判断库存结束", "goodsStock", goodsStock);
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("goodsStock", goodsStock);
			result.setBaseOutputBean(outputBean);
			result.setParams(paramMap);
			result.setStatus(BaseResultEnum.SUCCESS.getResultID());
			result.setMessage("判断库存成功");
			return result;
		} catch (RuntimeException r) {
			Log.error(logger, uuid, "运行时异常：+'方法名getSaleStockRPC:'", r);
		} catch (Exception e) {
			Log.error(logger, uuid, "服务异常：+'方法名getSaleStockRPC:'", e);
		}
		result.setBaseOutputBean(outputBean);
		return result;
	}
	
	 /**
	  * 调用配送服务
	  */
	public BaseResult getShippingRPC(OmsCreateOrderInputData inputData, HashMap<String, String> params, AddressGetAddressByIDOutputData addressOutputData, boolean isJielong, boolean isPower, 
			Long templeteId, HashMap<Long, GoodsNumberBean> goodsCount, Map<Long, GoodsGetGoodsInfoGoodsInfoElementO> goodsInfo, String timeShipType, Map<Long, Set<Long>> goodsStock) throws Exception {
		BaseResult result = new BaseResult(); 
		BaseOutputBean outputBean = new BaseOutputBean();
        Map<Long, Map<Long, List<ShippingGetGoodsShippingInfosShippingRulesElementO>>> goodsWarehouseRule = new HashMap<Long, Map<Long, List<ShippingGetGoodsShippingInfosShippingRulesElementO>>>();
        try{
			Object o = this.callShippingService(isJielong, goodsWarehouseRule, false, inputData, templeteId, addressOutputData, goodsCount, goodsInfo, timeShipType, goodsStock);
			if (o instanceof BaseOrderResultEnum) {
				BaseOrderResultEnum transferObject = (BaseOrderResultEnum) o;
				outputBean = ResponseUtil.getBaseOutputBean(transferObject.getResultID(), null, inputData.getScope());
				Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
				result.setBaseOutputBean(outputBean);
				result.setStatus(transferObject.getResultID());
				result.setMessage("同日达或当日达或配送出错");
				return result;
			}
			if (o instanceof BaseOutputBean) {
				BaseOutputBean transferObject = (BaseOutputBean) o;
				result.setBaseOutputBean(transferObject);
				result.setStatus(transferObject.getOutputHead().getResultID());
				result.setMessage("配送服务出错");
				return result;
			}
			if (o instanceof HashSet) {
				HashSet<ShippingDateBean> transferObject = (HashSet<ShippingDateBean>) o;
				if (!isJielong) {
					if (transferObject == null || transferObject.isEmpty()) {
						Log.info(logger, uuid, "有商品不可同日达");
						outputBean = ResponseUtil.getBaseOutputBean(BaseOrderResultEnum.NOT_SAMETIME_SHIP.getResultID(), null, inputData.getScope());
						Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
						result.setBaseOutputBean(outputBean);
						result.setStatus(BaseOrderResultEnum.NOT_SAMETIME_SHIP.getResultID());
						result.setMessage("有商品不可同日达");
						return result;
					}
				}
			}
			outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.SUCCESS.getResultID(), null, inputData.getScope());
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("goodsWarehouseRule", goodsWarehouseRule);
			result.setBaseOutputBean(outputBean);
			result.setParams(paramMap);
			result.setStatus(BaseResultEnum.SUCCESS.getResultID());
			result.setMessage("调用配送服务成功");
			return result;
        } catch (RuntimeException r) {
			Log.error(logger, uuid, "运行时异常：+'方法名getShippingRPC:'", r);
		} catch (Exception e) {
			Log.error(logger, uuid, "服务异常：+'方法名getShippingRPC:'", e);
		}
		result.setBaseOutputBean(outputBean);
		return result;
	}
	
	/**
	 * 调用运力服务
	 */
	public BaseResult getTransportRPC(OmsCreateOrderInputData inputData, HashMap<String, String> params, AddressGetAddressByIDOutputData addressOutputData,	boolean isJielong, boolean isPower, Long templeteId,
			HashMap<Long, GoodsNumberBean> goodsCount, Map<Long, GoodsGetGoodsInfoGoodsInfoElementO> goodsInfo, String timeShipType, Map<Long, Set<Long>> goodsStock) throws Exception {
		
		BaseResult result = new BaseResult();
		BaseOutputBean outputBean = new BaseOutputBean();
        Map<Long, Map<Long, List<ShippingGetGoodsShippingInfosShippingRulesElementO>>> goodsWarehouseRule = new HashMap<Long, Map<Long, List<ShippingGetGoodsShippingInfosShippingRulesElementO>>>();
        try{
	        Object o = this.callShippingService(isJielong, goodsWarehouseRule, true, inputData, templeteId, addressOutputData, goodsCount, goodsInfo, timeShipType, goodsStock);
	        if (o instanceof BaseOrderResultEnum) {
	            BaseOrderResultEnum transferObject = (BaseOrderResultEnum)o;
	            outputBean = ResponseUtil.getBaseOutputBean(transferObject.getResultID(), null, inputData.getScope());
	            Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
	            result.setBaseOutputBean(outputBean);
	            result.setStatus(transferObject.getResultID()); //待定义
	            result.setMessage("无运力");//待定义
	            return result;
	        }
	        if (o instanceof BaseOutputBean) {
	            BaseOutputBean transferObject = (BaseOutputBean)o;
	            result.setBaseOutputBean(transferObject);
	            result.setStatus(transferObject.getOutputHead().getResultID());//待定义
	            result.setMessage("无运力");//待定义
	            return result;
	        }
	        if (o instanceof HashSet) {
	            HashSet<ShippingDateBean> transferObject = (HashSet<ShippingDateBean>)o;
	            if(!isJielong){
	                if (transferObject == null || transferObject.isEmpty()) {
	                    Log.info(logger, uuid, "无运力");
	                    outputBean = ResponseUtil.getBaseOutputBean(BaseOrderResultEnum.NOT_SHIP_POWER.getResultID(), null, inputData.getScope());
	                    Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
	                    result.setBaseOutputBean(outputBean);
	    	            result.setStatus(BaseOrderResultEnum.NOT_SHIP_POWER.getResultID());//待定义
	    	            result.setMessage("无运力");//待定义
	    	            return result;
	                }
	            }
	            outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.SUCCESS.getResultID(), null, inputData.getScope());
	            Map<String,Object> paramMap = new HashMap<String,Object>();
	            paramMap.put("allDates", transferObject);
	            result.setBaseOutputBean(outputBean);
	            result.setParams(paramMap);
	            result.setStatus(BaseResultEnum.SUCCESS.getResultID());
				result.setMessage("调用运力服务成功");
				return result;
	        }
        } catch (RuntimeException r) {
			Log.error(logger, uuid, "运行时异常：+'方法名getTransportRPC:'", r);
		} catch (Exception e) {
			Log.error(logger, uuid, "服务异常：+'方法名getSaleStockRPC:'", e);
		}
		result.setBaseOutputBean(outputBean);
		return result;
	}
	
	
	
	/**
	 * 调用运费服务
	 */
	public BaseResult getShippingFeeRPC(OmsCreateOrderInputData inputData,BigDecimal shipFeeGroupbuy, BigDecimal noShipFeeAmount, boolean isNeedGift, BigDecimal orderFee, String couponSN, boolean isGiftOK , 
			AddressGetAddressByIDOutputData addressOutputData, Long templeteId, HashMap<Long, GoodsNumberBean> goodsCount, Map<Long, GoodsGetGoodsInfoGoodsInfoElementO> goodsInfo,	String timeShipType) throws Exception {
		
		BaseResult result = new BaseResult();
		BaseOutputBean outputBean = new BaseOutputBean();
		BigDecimal shipFee = BigDecimal.ZERO;
	    BigDecimal shipTotalFee = BigDecimal.ZERO;
	    BigDecimal deliveryTimeFee = BigDecimal.ZERO;
	
	    try{
	        if(ActiveTypeEnum.isFindChannel(inputData.getActivityType())
                            && (OrderTypeEnum.BAOYOU_TUANGOU_ORDER.getC().equals(inputData.getOrderType()) || OrderTypeEnum.JIELONG_GROUPBUY.getC().equals(inputData.getOrderType()))){//发现频道
	            Log.info(logger, uuid, "发现频道运费定义", "orderFee", orderFee, "noShipFeeAmount", noShipFeeAmount, "shipFeeGroupbuy", shipFeeGroupbuy);
	            if (orderFee.compareTo(noShipFeeAmount) < 0) {
	                shipFee = shipFeeGroupbuy;
	                shipTotalFee = shipFeeGroupbuy;
	            }
	            outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.SUCCESS.getResultID(), null, inputData.getScope());
	            
	        }else if(OrderTypeEnum.JIELONG_GROUPBUY.getC().equals(inputData.getOrderType()) && (StringUtil.isEmpty(templeteId.toString()) ||  !templeteId.toString().equals("0"))){//接龙团购
		        
		        Log.info(logger, uuid, "此订单是接龙团购订单,且配送规自定义", "templeteId", templeteId);
		        shipFee = shipFee.add(shipFeeGroupbuy);
		        shipTotalFee = shipTotalFee.add(shipFeeGroupbuy);
		        outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.SUCCESS.getResultID(), null, inputData.getScope());
		        
		    }else{
		        
		        /****************************************************************************** 调用运费服务开始 ********************************************************************************************/
		        Log.info(logger, uuid, "组装shipping服务getGoodsShippingFee方法所需参数开始");
		        ShippingGetGoodsShippingFeeInputData shipFeeInputData = getGoodsItemsShipfee(isGiftOK, isNeedGift?SpecialInfos.specialCouponMap.get(couponSN).getGoodsId():null, inputData.getScope(), inputData.getOrderType(), timeShipType, inputData.getSubstationID(), ("1".equals(inputData.getReqFrom())?inputData.getLastGeoId():addressOutputData.getLastGeoID()), orderFee, goodsCount, goodsInfo);
		        Map<String, String> shipFeeServiceParams = new HashMap<String, String>();
		        shipFeeServiceParams.put("method", "getGoodsShippingFee");
		        shipFeeServiceParams.put("req_str", shipFeeInputData.toJson());
		        Log.info(logger, uuid, "组装shipping服务getGoodsShippingFee方法所需参数结束");
		        
		        Log.info(logger, uuid, "调用shipping服务getGoodsShippingFee方法开始", "shipFeeServiceParams", shipFeeServiceParams);
		        outputBean = TootooService.callServer("shipping", shipFeeServiceParams, "post", new ShippingGetGoodsShippingFeeOutputData());
		        Log.info(logger, uuid, "调用shipping服务getGoodsShippingFee方法结束", "outputBean", outputBean);
		        
		        ShippingGetGoodsShippingFeeOutputData shipFeeOutputData = (ShippingGetGoodsShippingFeeOutputData)outputBean.getOutputData();
		        shipFee = shipFee.add(shipFeeOutputData.getDiscountShippingFee()==null?BigDecimal.ZERO:shipFeeOutputData.getDiscountShippingFee());
		        shipTotalFee = shipTotalFee.add(shipFeeOutputData.getTotalShippingFee()==null?BigDecimal.ZERO:shipFeeOutputData.getTotalShippingFee());
		        deliveryTimeFee = deliveryTimeFee.add(shipFeeOutputData.getTimeShipServiceFee()==null?BigDecimal.ZERO:shipFeeOutputData.getTimeShipServiceFee());
		        /****************************************************************************** 调用运费服务结束 ********************************************************************************************/
		    }
		    Map<String,Object> paramMap = new HashMap<String,Object>();
	        paramMap.put("shipFee", shipFee);
	        paramMap.put("shipTotalFee", shipTotalFee);
	        paramMap.put("deliveryTimeFee", deliveryTimeFee); 
	        result.setParams(paramMap);
		    result.setBaseOutputBean(outputBean);
		    result.setStatus(BaseResultEnum.SUCCESS.getResultID());
			result.setMessage("调用运费服务成功");
	        return result;

	    } catch (RuntimeException r) {
			Log.error(logger, uuid, "运行时异常：+'方法名getTransportRPC:'", r);
		} catch (Exception e) {
			Log.error(logger, uuid, "服务异常：+'方法名getSaleStockRPC:'", e);
		}
		result.setBaseOutputBean(outputBean);
		return result;
	}
	
	
	protected Object callShippingService(boolean isJielong, Map<Long, Map<Long, List<ShippingGetGoodsShippingInfosShippingRulesElementO>>> goodsWarehouseRule, boolean isPower, OmsCreateOrderInputData inputData, Long templeteId, AddressGetAddressByIDOutputData addressOutputData, HashMap<Long, GoodsNumberBean> goodsCount, Map<Long, GoodsGetGoodsInfoGoodsInfoElementO> goodsInfo, String timeShipType, Map<Long, Set<Long>> goodsStock) throws Exception{
        
        Log.info(logger, uuid, "组装shipping服务getGoodsShippingInfos方法所需参数开始");
        
        ShippingGetGoodsShippingInfosInputData getGoodsShippingInfosInputData = getGoodsShippingInfosInputData(isPower, inputData.getScope(), inputData.getOrderType(), inputData.getOrderFrom(), inputData.getSubstationID(), templeteId, ("1".equals(inputData.getReqFrom())?inputData.getLastGeoId():addressOutputData.getLastGeoID()), goodsCount, goodsInfo, timeShipType);
        
        if(OrderTypeEnum.JIELONG_GROUPBUY.getC().equals(inputData.getOrderType()) && isJielong){//接龙团购且走模板
            
        }else{
            
            if (getGoodsShippingInfosInputData == null) {// 不可同天送
                Log.info(logger, uuid, "有商品不可同日达或者不支持当日达");
                return BaseOrderResultEnum.NOT_SAMETIME_SHIP;
//                outputBean = ResponseUtil.getBaseOutputBean(BaseOrderResultEnum.NOT_SAMETIME_SHIP.getResultID(), null, inputData.getScope());
//                Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
//                return outputBean;
            }
            
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
        Map<Long, Map<Long, Set<ShippingDateBean>>> goodsWarehouseDate = OrderUtil.checkShipping(isJielong, ("1".equals(inputData.getReqFrom())?inputData.getLastGeoId():addressOutputData.getLastGeoID()), goodsStock, shippingOutputData, goodsCount, returnSetShipping, goodsInfo, goodsWarehouseRule);
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
        
        if (BooleanEnum.TRUE.getV().equals(inputData.getDeliveryTimeType())) {// 选择了当日达
            if (!allDates.contains(new ShippingDateBean(new SimpleDateFormat("yyyy-MM-dd").format(new Date()), "", "", ""))) {
                Log.info(logger, uuid, "不支持当日达");
                return BaseOrderResultEnum.TODAY_IS_INVALID;
//                outputBean = ResponseUtil.getBaseOutputBean(BaseOrderResultEnum.TODAY_IS_INVALID.getResultID(), null, inputData.getScope());
//                Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
//                return outputBean;
            }
        } else {
            allDates.remove(new ShippingDateBean(new SimpleDateFormat("yyyy-MM-dd").format(new Date()), "", "", ""));
        }
        
        if(!isJielong){//接龙团购且走模板
            if (allDates == null || allDates.isEmpty()) {
                Log.info(logger, uuid, "有商品不可同日达");
                outputBean = ResponseUtil.getBaseOutputBean(BaseOrderResultEnum.NOT_SAMETIME_SHIP.getResultID(), null, inputData.getScope());
                Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
                return outputBean;
            }
        }
        
        return allDates;
        
    }
	
	/**
	 * 团购订单调用服务更新缓存
	 */

	public void updateCache(OmsCreateOrderInputData inputData ,HashMap<String, String>  params, Map<Long, GoodsGetGoodsInfoGoodsInfoElementO> goodsInfo)throws Exception{
		BaseOutputBean outputBean =new BaseOutputBean();
		try {
			if("3".equals(inputData.getActivityType())){//微信团活动:下单成功后记到缓存中防止再次下单
	            Memcached.addFlushBuy("webChatGroupbuy" + inputData.getActivityId() + params.get(AuthorizeClient.COOKIE_BUYER_ID));
	            Log.info(logger, uuid, "登录查询", "huancun", Memcached.getOK("webChatGroupbuy" + inputData.getActivityId() + params.get(AuthorizeClient.COOKIE_BUYER_ID)));
	        }
			
            if (OrderTypeEnum.BAOYOU_TUANGOU_ORDER.getC().equals(inputData.getOrderType())) {// 团购订单
                Log.info(logger, uuid, "组装promotion服务increamentTuangouCount方法所需参数开始");
                PromotionIncreamentTuangouCountInputData increamentInputData = new PromotionIncreamentTuangouCountInputData();
                increamentInputData.setScope(inputData.getScope());
                increamentInputData.setPromotionId(inputData.getGroupPromotionID());
                for (OmsCreateOrderGroupBuyingGoodsListElementI groupBuyElement : inputData.getGroupBuyingGoodsList()) {
                    PromotionIncreamentTuangouCountGoodsInfoElementI increamentElement = new PromotionIncreamentTuangouCountGoodsInfoElementI();
                    increamentElement.setSkuId(goodsInfo.get(groupBuyElement.getGoodsID()).getSkuID());
                    increamentElement.setGoodsNum(groupBuyElement.getGoodsCount());
                    increamentInputData.getGoodsInfo().add(increamentElement);
                }
                Map<String, String> increamentParams = new HashMap<String, String>();
                increamentParams.put("r", "tIncreamentTuangouCount");
                increamentParams.put("req_str", increamentInputData.toJson());
                Log.info(logger, uuid, "组装promotion服务increamentTuangouCount方法所需参数结束");
                
                Log.info(logger, uuid, "调用promotion服务increamentTuangouCount方法开始", "increamentParams", increamentParams);
                outputBean = TootooService.callServer(logger, uuid, "promotion", increamentParams, "post", new PromotionIncreamentTuangouCountOutputData());
                Log.info(logger, uuid, "调用promotion服务increamentTuangouCount方法结束", "outputBean", outputBean);
                
                if (!TootooService.checkService(outputBean, "promotion", "increamentTuangouCount", inputData.getScope())) {
                    Log.error(logger, uuid, "调用promotion服务increamentTuangouCount方法失败,接口返回", null, "outputBean", outputBean);
                }
                // 如果调用失败,不影响下单
            }
            
            if (OrderTypeEnum.JIELONG_GROUPBUY.getC().equals(inputData.getOrderType())) {// 接龙团购
            	Log.info(logger, uuid, "组装promotion服务updateWeixinjielongBuyHistory方法所需参数开始");
            	
            	PromotionUpdateWeixinjielongBuyHistoryInputData jielongInputData = new PromotionUpdateWeixinjielongBuyHistoryInputData();
            	
            	jielongInputData.setScope(inputData.getScope());
            	jielongInputData.setPromotionId(inputData.getGroupPromotionID());
            	for(OmsCreateOrderGroupBuyingGoodsListElementI groupBuyElement : inputData.getGroupBuyingGoodsList()){
            		PromotionUpdateWeixinjielongBuyHistoryBuyHistoryElementI jielongElement = new PromotionUpdateWeixinjielongBuyHistoryBuyHistoryElementI();
            		jielongElement.setCreateTime(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
            		jielongElement.setGoodsId(groupBuyElement.getGoodsID()+"");
            		jielongElement.setNickname(params.get(AuthorizeClient.PARAM_BUYER_NAME));
            		jielongElement.setNum(groupBuyElement.getGoodsCount()+"");
            		jielongInputData.getBuyHistory().add(jielongElement);
            	}
                Map<String, String> jielongParams = new HashMap<String, String>();
                jielongParams.put("r", "TUpdateWeixinjielongBuyHistory");
                jielongParams.put("req_str", jielongInputData.toJson());
                Log.info(logger, uuid, "组装promotion服务updateWeixinjielongBuyHistory方法所需参数结束");
                
                Log.info(logger, uuid, "调用promotion服务updateWeixinjielongBuyHistory方法开始", "jielongParams", jielongParams);
                outputBean = TootooService.callServer(logger, uuid, "promotion", jielongParams, "post", new PromotionUpdateWeixinjielongBuyHistoryOutputData());
                Log.info(logger, uuid, "调用promotion服务updateWeixinjielongBuyHistory方法结束", "outputBean", outputBean);
                
                if (!TootooService.checkService(outputBean, "promotion", "updateWeixinjielongBuyHistory", inputData.getScope())) {
                    Log.error(logger, uuid, "调用promotion服务updateWeixinjielongBuyHistory方法失败,接口返回", null, "outputBean", outputBean);
                }
            	
            }
		} catch (RuntimeException r) {
			Log.error(logger, uuid, "运行时异常：+'方法名updateCache:'", r);
        } catch (Exception e) {
            Log.error(logger, uuid, "团购订单更新缓存失败:+'方法名updateCache:'", e);
        }
	}
	
	
	/**
	 * Description:<br>
	 * 发现频道下单成后，设置促销商品会员购买数
	 * 
	 * @param inputData
	 * @param params
	 * @param groupMaxBuyMap 活动设置的限制用户购买的商品和数量
	 */
	public void setBuyerPromotionNumRPC(OmsCreateOrderInputData inputData, HashMap<String, String> params, Map<Long, Long> groupMaxBuyMap){
	    try{
	        if(groupMaxBuyMap == null || groupMaxBuyMap.size() <= 0){
	            return;
	        }
	        
	        OmsSetBuyerPromotionNumInputData setBuyerNumInputData = new OmsSetBuyerPromotionNumInputData();
	        setBuyerNumInputData.setScope(inputData.getScope());
	        setBuyerNumInputData.setPromotionId(inputData.getGroupPromotionID());
	        List<OmsSetBuyerPromotionNumGoodsListElementI> goodsList = new ArrayList<OmsSetBuyerPromotionNumGoodsListElementI>();
	        for (OmsCreateOrderGroupBuyingGoodsListElementI groupGoodElement : inputData.getGroupBuyingGoodsList()) {
                if (!groupMaxBuyMap.containsKey(groupGoodElement.getGoodsID())) {
                    continue;
                }
                OmsSetBuyerPromotionNumGoodsListElementI goods = new OmsSetBuyerPromotionNumGoodsListElementI();
                goods.setGoodsId(groupGoodElement.getGoodsID());
                goods.setBuyerNum(groupGoodElement.getGoodsCount());
                goodsList.add(goods);
	        }
	        setBuyerNumInputData.setGoodsList(goodsList);
	        
	        HashMap<String, String> setBuyerNumServiceParams = (HashMap<String, String>)params.clone();
	        setBuyerNumServiceParams.put("method", "setBuyerPromotionNum");
	        setBuyerNumServiceParams.put("req_str", setBuyerNumInputData.toJson());
	        
	        Log.info(logger, uuid, "调用oms服务setBuyerPromotionNum方法开始", "setBuyerNumServiceParams", setBuyerNumServiceParams);
	        BaseOutputBean outputBean = TootooService.callServer("oms", setBuyerNumServiceParams, "post", new OmsSetBuyerPromotionNumOutputData());
	        Log.info(logger, uuid, "调用oms服务setBuyerPromotionNum方法结束", "outputBean", outputBean);
	        
	        if (!TootooService.checkService(outputBean, "oms", "setBuyerPromotionNum", inputData.getScope())) {
	            Log.error(logger, uuid, "调用oms服务setBuyerPromotionNum方法失败,接口返回", null, "outputBean", outputBean);
	        }
	        
	    } catch (RuntimeException r) {
            Log.error(logger, uuid, "运行时异常：+'方法名setBuyerPromotionNumRPC:'" , r);
        } catch (Exception e) {
            Log.error(logger, uuid, "发现频道设置促销商品会员购买数失败：+'方法名setBuyerPromotionNumRPC:'" , e);
        }
	}
	
}
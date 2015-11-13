package cn.tootoo.soa.oms.createorder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;

import cn.tootoo.db.egrocery.bsbuyer.BsBuyerDao;
import cn.tootoo.db.egrocery.bsbuyer.BsBuyerPO;
import cn.tootoo.db.egrocery.tosparentorder.TOsParentOrderDao;
import cn.tootoo.enums.BaseResultEnum;
import cn.tootoo.enums.BooleanEnum;
import cn.tootoo.exceptions.AuthorizeException;
import cn.tootoo.global.Global;
import cn.tootoo.http.AuthorizeClient;
import cn.tootoo.http.TootooService;
import cn.tootoo.http.bean.BaseInputBean;
import cn.tootoo.http.bean.BaseOutputBean;
import cn.tootoo.servlet.BaseService;
import cn.tootoo.soa.authorize.checktransactionpassword.input.AuthorizeCheckTransactionPasswordInputData;
import cn.tootoo.soa.authorize.checktransactionpassword.output.AuthorizeCheckTransactionPasswordOutputData;
import cn.tootoo.soa.authorize.getpaymethodids.input.AuthorizeGetPayMethodIdsInputData;
import cn.tootoo.soa.authorize.getpaymethodids.output.AuthorizeGetPayMethodIdsOutputData;
import cn.tootoo.soa.authorize.getpaymethodids.output.AuthorizeGetPayMethodIdsResultEnum;
import cn.tootoo.soa.base.bean.GiftItemsBean;
import cn.tootoo.soa.base.bean.GoodsNumberBean;
import cn.tootoo.soa.base.enums.BaseOrderResultEnum;
import cn.tootoo.soa.base.enums.OrderFromEnum;
import cn.tootoo.soa.base.enums.OrderTypeEnum;
import cn.tootoo.soa.base.enums.PayMethodEnum;
import cn.tootoo.soa.base.enums.PromotionStatusEnum;
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
import cn.tootoo.soa.oms.createorder.input.OmsCreateOrderExGoodsIDElementI;
import cn.tootoo.soa.oms.createorder.input.OmsCreateOrderExListElementI;
import cn.tootoo.soa.oms.createorder.input.OmsCreateOrderInputData;
import cn.tootoo.soa.oms.createorder.input.OmsCreateOrderPayListElementI;
import cn.tootoo.soa.oms.createorder.output.OmsCreateOrderOutputData;
import cn.tootoo.soa.oms.utils.SqlUtil;
import cn.tootoo.soa.promotion.getpromotionbyid.input.PromotionGetPromotionByIdInputData;
import cn.tootoo.soa.promotion.getpromotionbyid.input.PromotionGetPromotionByIdPromotionIdListElementI;
import cn.tootoo.soa.promotion.getpromotionbyid.output.PromotionGetPromotionByIdDetailElementO;
import cn.tootoo.soa.promotion.getpromotionbyid.output.PromotionGetPromotionByIdOutputData;
import cn.tootoo.soa.promotion.getpromotionbyid.output.PromotionGetPromotionByIdPromotionListElementO;
import cn.tootoo.soa.shopping.checkvolume.input.ShoppingCheckVolumeBuyerInfoElementI;
import cn.tootoo.soa.shopping.checkvolume.input.ShoppingCheckVolumeGoodsListElementI;
import cn.tootoo.soa.shopping.checkvolume.input.ShoppingCheckVolumeInputData;
import cn.tootoo.soa.shopping.checkvolume.output.ShoppingCheckVolumeOutputData;
import cn.tootoo.utils.ConfigUtil;
import cn.tootoo.utils.Log;
import cn.tootoo.utils.MD5Util;
import cn.tootoo.utils.Memcached;
import cn.tootoo.utils.ResponseUtil;
import cn.tootoo.utils.StringUtil;

public class CreateOrderImpl extends BaseService {
    
    private Logger logger = Logger.getLogger(this.getClass());
    
    public static final Long VIRTUAL_GIFT = 1034964L;// 虚拟礼盒ID固定
    
    @Override
    public BaseOutputBean doService(BaseInputBean inputBean) throws Exception {
        
        BaseOutputBean outputBean = new BaseOutputBean();
        NormalCreateOrderService normalCreateOrderService=new NormalCreateOrderService(uuid, logger);
        try {
            
            Log.info(logger, uuid, "执行服务开始，传入bean", "inputBean", inputBean);
            request.setAttribute(AuthorizeClient.ATTRIB_CHECK_LEVEL, AuthorizeClient.AUTH_SENIOR_LEVEL);
            HashMap<String, String> params = (HashMap<String, String>)AuthorizeClient.getVerifyInfo(request);
            OmsCreateOrderOutputData outputData = new OmsCreateOrderOutputData();
            if (inputBean.getInputData() == null) {
                Log.info(logger, uuid, "参数req_str为空", "inputBean", inputBean);
                outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.REQUEST_PARAM_ERROR.getResultID(), null, "");
                Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
                return outputBean;
            }
            
            OmsCreateOrderInputData inputData = null;
            try {
                inputData = (OmsCreateOrderInputData)inputBean.getInputData();
            } catch (Exception e) {
                Log.error(logger, "接口实现类转换错误", e, "className", OmsCreateOrderInputData.class.getName());
                outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.CLASS_TRANSFER_ERROR.getResultID(), null, "");
                Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
                return outputBean;
            }
            
            /****************************************************************************** 调用鉴权服务开始 ********************************************************************************************/
            Log.info(logger, uuid, "调用用户鉴权服务开始", "params", params);
            int loginAuth;
            try {
                params.put(AuthorizeClient.COOKIE_SCOPE, inputData.getScope());
                loginAuth = AuthorizeClient.verifySession(params);
            } catch (AuthorizeException authException) {
                Log.error(logger, "用户未登录", authException);
                outputBean = ResponseUtil.getBaseOutputBean(BaseOrderResultEnum.NOT_LOGIN.getResultID(), null, inputData.getScope());
                Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
                return outputBean;
            }
            Log.info(logger, uuid, "调用用户鉴权服务结束", "loginAuth", loginAuth);
            if (loginAuth == AuthorizeClient.CHECK_NOT_OK) {
                Log.info(logger, uuid, "用户未登录", "loginAuth", loginAuth);
                outputBean = ResponseUtil.getBaseOutputBean(BaseOrderResultEnum.NOT_LOGIN.getResultID(), null, inputData.getScope());
                Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
                return outputBean;
            }
            /****************************************************************************** 调用鉴权服务结束 ********************************************************************************************/
            
            /****************************************************************************** 验证支付密码开始 ********************************************************************************************/
            
            if( /*!StringUtil.isEmpty(inputData.getTransactionPassword()) &&*/  ( !"11102".equals(inputData.getScope())  ) || ("11102".equals(inputData.getScope()) && !StringUtil.isEmpty(inputData.getTransactionPassword())) /*&& ( (!inputData.getScope().startsWith("112")  ) || (inputData.getScope().startsWith("112") && !StringUtil.isEmpty(inputData.getTransactionPassword())))*/ ){//非移动端和不是WAp站或者移动端并且支付密码不为null
	            
            	if(!"21101".equals(inputData.getScope()) && ( OrderTypeEnum.BAOYOU_TUANGOU_ORDER.getC().equals(inputData.getOrderType()) || OrderTypeEnum.NORMAL.getC().equals(inputData.getOrderType()) ) ){//不是英文站并且订单类型为普通订单或者沱沱惠订单
            		Log.info(logger, uuid, "验证支付密码开始", "TransactionPassword", inputData.getTransactionPassword());
            		
	            	//1.先根据用户传入的支付方式列表中是否有礼品卡，账户余额两种支付方式
	            	List<OmsCreateOrderPayListElementI> omsPayListI = inputData.getPayList();
		            if(omsPayListI!=null && omsPayListI.size()>0){
		            	
			            Set<Long> PayMethodsISet = new HashSet<Long>();
			            for(OmsCreateOrderPayListElementI e:omsPayListI){
			            	PayMethodsISet.add(e.getPayMethodID());
			            }
			            Set<Long> checkPayMethodsSet1 = new HashSet<Long>();
			            checkPayMethodsSet1.add((long) PayMethodEnum.ACCOUNT.getC());
			            //checkPayMethodsSet1.add((long) PayMethodEnum.COUPON.getC());
			            checkPayMethodsSet1.add((long) PayMethodEnum.GIFTCARD.getC());
			            
			            PayMethodsISet.retainAll(checkPayMethodsSet1);
			            Log.info(logger, uuid, "判断用户传入的支付方式列表中是否有礼品卡，账户余额两种支付方式", "PayMethodsISet", PayMethodsISet);
			            if(PayMethodsISet.size()>0){//有交集,校验是否设置支付方式
			            	//2.获取用户设置的需要支付密码的支付方式
			            	Log.info(logger, uuid, "获取用户设置的需要支付密码的支付方式开始");
				            Log.info(logger, uuid, "组装authorize服务getPayMethodIds方法所需参数开始");
				            AuthorizeGetPayMethodIdsInputData authGetPayIdsInputData = new AuthorizeGetPayMethodIdsInputData();
				            authGetPayIdsInputData.setScope(inputData.getScope());
				            Map<String, String> authGetPayIdsParams = (Map<String, String>)params.clone();
				            authGetPayIdsParams.put("method", "getPayMethodIds");
				            authGetPayIdsParams.put("req_str", authGetPayIdsInputData.toJson());
				            Log.info(logger, uuid, "组装authorize服务getPayMethodIds方法所需参数结束");
				            
				            Log.info(logger, uuid, "调用authorize服务getPayMethodIds方法开始", "shipFeeServiceParams", authGetPayIdsParams);
				            outputBean = TootooService.callServer("authorize", authGetPayIdsParams, "post", new AuthorizeGetPayMethodIdsOutputData());
				            Log.info(logger, uuid, "调用authorize服务getPayMethodIds方法结束", "outputBean", outputBean);
				            
				            
				            if (outputBean == null || outputBean.getOutputHead() == null) {
				            	 Log.info(logger, uuid, "调用authorize服务getPayMethodIds方法失败,接口返回", "outputBean", outputBean);
					            outputBean.setOutputHead(Global.getOutputHead(BaseResultEnum.CALLSERVER_ERROR.getResultID(), inputData.getScope(), null));
					            outputBean.setOutputData(null);
					            return outputBean;
					        }
					        if (outputBean.getOutputHead().getResultID() == BaseResultEnum.SUCCESS.getResultID()) {
					            	
					            AuthorizeGetPayMethodIdsOutputData authPayMethodIds= (AuthorizeGetPayMethodIdsOutputData) outputBean.getOutputData();
					            Log.info(logger, uuid, "获取用户设置的需要支付密码的支付方式结束", "authPayMethodIds", authPayMethodIds);
					            String payMethodIds = authPayMethodIds.getPayMethodIds();
					            String[] array = payMethodIds.split(",");
					            //需要校验的支付方式set
					            Set<Long> checkPayMethodsSet2 = new HashSet<Long>();
					            for(int i=0;i<array.length;i++){
					            	checkPayMethodsSet2.add(Long.parseLong(array[i]));
					            }
					            Set<Long> authPayMethodsSet = new HashSet<Long>();
					            for(OmsCreateOrderPayListElementI e:omsPayListI){
					            	authPayMethodsSet.add(e.getPayMethodID());
					            }
					            //3.校验用户传入的支付方式中是否有需要支付密码的支付方式
					            authPayMethodsSet.retainAll(checkPayMethodsSet2);
					            Log.info(logger, uuid, "用户传入的支付方式中是否有需要支付密码的支付方式", "authPayMethodsSet", authPayMethodsSet);
					            if(authPayMethodsSet.size()>0){
					            	//校验支付密码
					            	Log.info(logger, uuid, "校验支付密码开始");
					            	String transactionPassword = inputData.getTransactionPassword();
					            	if(StringUtil.isEmpty(transactionPassword)){
					            		Log.info(logger, uuid, "支付密码为空！");
					                    outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.REQUEST_PARAM_ERROR.getResultID(), null, inputData.getScope());
					                    Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
					                    return outputBean;
					            	}
					            	
					            	Log.info(logger, uuid, "组装authorize服务checkTransactionPassword方法所需参数开始");
					            	AuthorizeCheckTransactionPasswordInputData checkTransactionPwdInputData = new AuthorizeCheckTransactionPasswordInputData();
					            	checkTransactionPwdInputData.setScope(inputData.getScope());
					            	checkTransactionPwdInputData.setTransactionPassword(transactionPassword);
					                Map<String, String> checkTransactionPwdParams = (Map<String, String>)params.clone();
					                checkTransactionPwdParams.put("method", "checkTransactionPassword");
					                checkTransactionPwdParams.put("req_str", checkTransactionPwdInputData.toJson());
					                Log.info(logger, uuid, "组装authorize服务checkTransactionPassword方法所需参数结束");
					                
					                Log.info(logger, uuid, "调用authorize服务checkTransactionPassword方法开始", "checkTransactionPwdParams", checkTransactionPwdParams);
					                outputBean = TootooService.callServer("authorize", checkTransactionPwdParams, "post", new AuthorizeCheckTransactionPasswordOutputData());
					                Log.info(logger, uuid, "调用authorize服务checkTransactionPassword方法结束", "outputBean", outputBean);
					                
					                if (!TootooService.checkService(outputBean, "authorize", "checkTransactionPassword", inputData.getScope())) {
					                    Log.info(logger, uuid, "调用authorize服务checkTransactionPassword方法失败,接口返回", "outputBean", outputBean);
					                    return outputBean;
					                }
					                
					                AuthorizeCheckTransactionPasswordOutputData actpOutputData = (AuthorizeCheckTransactionPasswordOutputData) outputBean.getOutputData(); 
					                Log.info(logger, uuid, "校验支付密码结束","actpOutputData",actpOutputData);
					                String isCorrect = actpOutputData.getIsCorrect();
					                if("0".equals(isCorrect)){
					                	Integer checkTime = actpOutputData.getCheckTime();
					                	if(checkTime<6){
					                		outputData.setErrorMessage("支付密码错误，您最多还可以输入"+(6-checkTime) +"次");
					                		outputData.setFreezingStatus(1);
					                	}
					                	if(checkTime == 6){
					                		outputData.setErrorMessage("支付密码连错6次，将被锁定2小时，请稍后再试！");
					                		outputData.setFreezingStatus(0);
					                	}
					                	outputBean = ResponseUtil.getBaseOutputBean(BaseOrderResultEnum.TRANSACTIONPWD_ERROR.getResultID(), null, "");
					        	        outputBean.setOutputData(outputData);
					        	        Log.info(logger, uuid, "支付密码错误，返回错误次数！", "outputBean", outputBean);
					                    return outputBean;
					                }
					                
					            }
				            
				            } else {
				            	if(outputBean.getOutputHead().getResultID() != AuthorizeGetPayMethodIdsResultEnum.TRANSACTIONPWD_NOT_SET.getResultID()){
				            		Log.info(logger, uuid, "调用authorize服务getPayMethodIds方法失败,接口返回", "outputBean", outputBean);
				            		outputBean.setOutputHead(Global.getOutputHead(BaseResultEnum.CALLSERVER_ERROR.getResultID(), inputData.getScope(), null));
						            outputBean.setOutputData(null);
						            return outputBean;
				            	}
				            }
			            }
	            	}
            	}
            }
            /****************************************************************************** 验证支付密码结束 ********************************************************************************************/
            TOsParentOrderDao parentOrderDao = new TOsParentOrderDao(uuid, logger);
            BsBuyerDao bsBuyerDao = new BsBuyerDao(uuid, logger);
            if(normalCreateOrderService.ifCheckUserOrder(params.get(AuthorizeClient.COOKIE_BUYER_ID))){
	            String md5 = MD5Util.md5Encode(inputData.toJson()+params.get(AuthorizeClient.COOKIE_BUYER_ID));
	            Log.info(logger, uuid, "下单json串MD5", "md5", md5);
	            Log.info(logger, uuid, "下单json串MD5", "OK", Memcached.get(md5));
	            
	            if(!StringUtil.isEmpty((String)Memcached.get(md5))){
	                Log.info(logger, uuid, "用户提交订单太快", "value", (String)Memcached.getOK(md5));
	                outputBean = ResponseUtil.getBaseOutputBean(170035, null, inputData.getScope());
	                Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
	                return outputBean;
	            }
	            Memcached.add(md5);
	            
	            if (!OrderFromEnum.isOuterOrder(inputData.getOrderFrom())
	                            && !SqlUtil.checkUserOrder(logger, uuid, parentOrderDao.getReadConnectionName(), params.get(AuthorizeClient.COOKIE_BUYER_ID))) {
	                Log.info(logger, uuid, "该用户已经在1小时内下了20单或者15秒之内下了一单");
	                outputBean = ResponseUtil.getBaseOutputBean(170035, null, inputData.getScope());
	                Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
	                return outputBean;
	            }
            }
            
            
            // 如果纯兑换券商品特殊处理
            if ((inputData.getExList() != null && inputData.getExList().size() >= 1)
                            && (inputData.getGroupBuyingGoodsList() == null || inputData.getGroupBuyingGoodsList().size() <= 0)
                            && (inputData.getGiftCardGoodsList() == null || inputData.getGiftCardGoodsList().size() <= 0)
                            && (inputData.getTihuoGoodsList() == null || inputData.getTihuoGoodsList().size() <= 0)
                            && (inputData.getGoodsList() == null || inputData.getGoodsList().size() <= 0)
                            && (inputData.getNyList() == null || inputData.getNyList().size() <= 0)
                            && (inputData.getGiftList() == null || inputData.getGiftList().size() <= 0)) {
                if (inputData.getPayList() == null || inputData.getPayList().size() <= 0) {
                    Log.info(logger, uuid, "纯兑换券商品且支付方式为空（兑换券包邮），自己拼接一个账户余额为0的支付方式");
                    List<OmsCreateOrderPayListElementI>  payListI = new ArrayList<OmsCreateOrderPayListElementI>();
                    OmsCreateOrderPayListElementI payI = new OmsCreateOrderPayListElementI();
                    payI.setPayMethodID(Long.valueOf(PayMethodEnum.ACCOUNT.getC()));
                    payI.setPayMethodTitle(null);
                    payI.setParentPayMethodID(0L);
                    payI.setPayAmount(BigDecimal.ZERO);
                    payI.setPayCode(null);
                    payListI.add(payI);
                    inputData.setPayList(payListI);
                    Log.info(logger, uuid, "拼接支付信息", "payList", inputData.getPayList());
                }
            } else if (inputData.getPayList() == null || inputData.getPayList().size() <= 0){
                Log.info(logger, uuid, "支付方式为空！");
                outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.REQUEST_PARAM_ERROR.getResultID(), null, inputData.getScope());
                Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
                return outputBean;
            }
            
            
            
            Long payMethodId = 0L;
            boolean haveOnlinePay = false;
            boolean isHaveCoupon = false;
            String isCheckCOD = BooleanEnum.FALSE.getV();// 前台是否选中货到付款支付方式
            String couponSN = "";
            List<OmsCreateOrderPayListElementI> payList = inputData.getPayList();
            
            Set<Long> payId = new HashSet<Long>();
            
            for (OmsCreateOrderPayListElementI pay : payList) {
                if (PayMethodEnum.OFFLINE.getC() == pay.getParentPayMethodID().intValue()) {
                    payMethodId = pay.getPayMethodID();
                    isCheckCOD = BooleanEnum.TRUE.getV();
                    Log.info(logger, uuid, "前台选中了COD支付方式");
                }
                if (PayMethodEnum.ONLINE.getC() == pay.getParentPayMethodID().intValue()) {
                    payMethodId = pay.getPayMethodID();
                    haveOnlinePay = true;
                    Log.info(logger, uuid, "前台选中了线上支付方式");
                }
                if (pay.getPayMethodID().intValue() == 4) {// 优惠券
                	
                	isHaveCoupon = true;
                	
                    if (pay.getPayCode() != null
                                    && pay.getPayCode().contains("__")) {
                        couponSN = pay.getPayCode().split("__")[0].trim();
                    } else {
                        if(pay.getPayCode()==null){
                            couponSN = "";
                        }else{
                            couponSN = pay.getPayCode().trim();
                        }
                    }
                }
                payId.add(pay.getPayMethodID());
            }
            
            // 判断是否优惠券和兑换券同时存在
            if (isHaveCoupon && inputData.getExList() != null && inputData.getExList().size() >= 1){
                Log.info(logger, uuid, "优惠券和兑换券互斥", "couponSN", couponSN, "inputData.getExList()", inputData.getExList());
                outputBean = ResponseUtil.getBaseOutputBean(BaseOrderResultEnum.COUPON_EX_DIFF.getResultID(), null, inputData.getScope());
                Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
                return outputBean;
            }
            if(inputData.getExList() != null && inputData.getExList().size() >= 2){
                Log.info(logger, uuid, "不能同时使用多张兑换券！", "inputData.getExList()", inputData.getExList());
                outputBean = ResponseUtil.getBaseOutputBean(BaseOrderResultEnum.EX_COUPON_MORE.getResultID(), null, inputData.getScope());
                Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
                return outputBean;
            }
            
            inputData.setIsNeedMJ(BooleanEnum.TRUE.getV());
            /*if(isHaveCoupon && BooleanEnum.TRUE.getV().equals(inputData.getIsNeedMJ())){
            	Log.info(logger, uuid, "优惠劵满减互斥", "isHaveCoupon", isHaveCoupon,"inputData.getIsNeedMJ()",inputData.getIsNeedMJ());
                outputBean = ResponseUtil.getBaseOutputBean(170050, null, inputData.getScope());
                Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
                return outputBean;
            }*/
            
            
            
            Log.info(logger, uuid, "优惠券号", "couponSN", couponSN);
            boolean isNeedGift = false;
            Long giftGoodsId = null;// 市场部活动赠品
            if (!StringUtil.isEmpty(couponSN)) {// 使用 优惠券
                if (SpecialInfos.specialCouponMap.containsKey(couponSN)) {
                    isNeedGift = true;
                    giftGoodsId = SpecialInfos.specialCouponMap.get(couponSN).getGoodsId();
                }
            }
            
            if (OrderTypeEnum.TIHUO_ORDER.getC().equals(inputData.getOrderType())
            		|| OrderTypeEnum.VIRTUAL_TIHUO_ORDER.getC().equals(inputData.getOrderType())) {
            	return new ThPhpCreateOrderService(uuid, logger).createTiHuoCreateOrder(inputBean, inputData, outputBean, outputData, params, haveOnlinePay, isCheckCOD, payMethodId);
            }
            
            
            
            
            /****************************************************************************** 调用商品服务开始 ********************************************************************************************/
            Log.info(logger, uuid, "根据传入的商品列表、N元Y件列表、赠品列表，获得商品的map,goodCount<Long,buyNumber-giftNumber>");
            List<Long> redemptionList = new ArrayList<Long>();// 保存所有换购的商品
            StringBuffer goodsIDMax = new StringBuffer("");// 除NY的所有goodsID的最大值
            Set<Long> allGoodsIDNotGift = new HashSet<Long>();// 所有的goodsID，包括NY内部的goodsID(不包括NY的ID),但不包括礼盒内部商品(包括礼盒本身的ID)
            Map<Long, Map<Long, Long>> giftMap = new HashMap<Long, Map<Long, Long>>(); // 保存赠品Map<goodsID,Map<promotionID,
                                                                                       // giftNum>>
            Map<String, String> buyFromMap = new HashMap<String, String>();// 保存商品的buyfrom
            Map<Long, Long> buyMap = new HashMap<Long, Long>();
            HashMap<Long, GoodsNumberBean> goodsCount = (HashMap)normalCreateOrderService.getGoodsCount(buyMap,inputData, redemptionList, goodsIDMax, allGoodsIDNotGift, giftMap, buyFromMap);
            
            if (goodsCount == null || goodsCount.isEmpty()) {
                Log.info(logger, uuid, "商品列表为空或者订单类型错误", "orderType", inputData.getOrderType(), "goodsCount", goodsCount);
                outputBean = ResponseUtil.getBaseOutputBean(BaseOrderResultEnum.GOODSlIST_INVALID.getResultID(), null, inputData.getScope());
                Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
                return outputBean;
            }

            int giftGoodsNum = 0;
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
            Set<Long> allGoodsID = allGoodsIDNotGift;// 所有商品的ID,包括NY内部(不包括NY的ID)和礼盒内部的商品ID(包括礼盒本身的ID)
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
                allGoodsID.add(VIRTUAL_GIFT);
            }
            //判断活动是否开启    0：未开启  1:开启
            Properties properties = ConfigUtil.getProperties("activity.properties");
            String privateTeamFlag="";
            String buildingFlag=""; //
            if(properties!=null){
            	privateTeamFlag =properties.getProperty("PrivateTeam");
            	buildingFlag =properties.getProperty("Building");
            }
            
            if(!StringUtil.isEmpty(privateTeamFlag) && "1".equals(privateTeamFlag)){
            	 // 私享团订单
                if (OrderTypeEnum.BAOYOU_TUANGOU_ORDER.getC().equals(inputData.getOrderType()) && inputData.getGroupTeamID()!=null&&!Long.valueOf(0).equals(inputData.getGroupTeamID())) {
                	return new PrivateTeamCreateOrderService(uuid, logger).createPrivateTeamOrder(payId, request,inputBean, inputData, outputBean, outputData, parentOrderDao, params, goodsCount, isCheckCOD, isNeedGift, giftGoodsId, couponSN, giftGoodsNum, payMethodId, haveOnlinePay, buyFromMap, goodsIDMax, buyMap, allGoodsID, getGoodsPropsOutputData.getGoodsList());
                }
            }
            
            if(!StringUtil.isEmpty(buildingFlag) && "1".equals(buildingFlag)){

                // 楼宇活动
                if ("1".equals(inputData.getActivityType()) && inputData.getActivityId() != null) {
                    return new BuildingCreateOrderService(uuid, logger).createBuildingOrder(payId, request,inputBean, inputData, outputBean, outputData, parentOrderDao, params, goodsCount, isCheckCOD, isNeedGift, giftGoodsId, couponSN, giftGoodsNum, payMethodId, haveOnlinePay, buyFromMap, goodsIDMax, buyMap, allGoodsID, getGoodsPropsOutputData.getGoodsList());
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
            
            /******************************************************************************兑换券商品校验开始********************************************************************************************/
            Log.info(logger, uuid, "如果有兑换券，校验商品");
            if(inputData.getExList() != null && inputData.getExList().size() >= 1){
                BsBuyerPO bsBuyerPO = bsBuyerDao.findBsBuyerPOByID(Long.parseLong(params.get(AuthorizeClient.COOKIE_BUYER_ID)), false, false, false, true);
                if(bsBuyerPO == null){
                    Log.info(logger, uuid, "查询用户信息失败", "bsBuyerPO", bsBuyerPO);
                    outputBean = ResponseUtil.getBaseOutputBean(BaseOrderResultEnum.USER_NULL.getResultID(), null, inputData.getScope());
                    Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
                    return outputBean;
                }
                
                OmsCreateOrderExListElementI exListElementI = inputData.getExList().get(0);
                ShoppingCheckVolumeInputData checkVolumeI = new ShoppingCheckVolumeInputData();
                checkVolumeI.setScope(inputData.getScope());
                checkVolumeI.setVolumerSn(exListElementI.getExSn());
                
                ShoppingCheckVolumeBuyerInfoElementI shoppingBuyerInfoI = new ShoppingCheckVolumeBuyerInfoElementI();
                shoppingBuyerInfoI.setBuyerId(bsBuyerPO.getBuyerId());
                shoppingBuyerInfoI.setHaveOrder(bsBuyerPO.getHaveOrder());
                shoppingBuyerInfoI.setMobile(bsBuyerPO.getMobile());
                checkVolumeI.setBuyerInfo(shoppingBuyerInfoI);
                
                List<ShoppingCheckVolumeGoodsListElementI> shoppingGoodListI = new ArrayList<ShoppingCheckVolumeGoodsListElementI>();
                for (OmsCreateOrderExGoodsIDElementI omsGoodI : exListElementI.getExGoodsID()) {
                    ShoppingCheckVolumeGoodsListElementI shoppingGoodI = new ShoppingCheckVolumeGoodsListElementI();
                    shoppingGoodI.setGoodsID(omsGoodI.getGoodsID());
                    shoppingGoodI.setGoodsCount(omsGoodI.getCount().intValue());
                    shoppingGoodListI.add(shoppingGoodI);
                }
                checkVolumeI.setGoodsList(shoppingGoodListI);
                
                Map<String, String> checkVolumeParams = (Map<String, String>)params.clone();
                checkVolumeParams.put("method", "checkVolume");
                checkVolumeParams.put("req_str", checkVolumeI.toJson());
                
                Log.info(logger, uuid, "调用shopping服务checkVolumeI方法开始", "checkVolumeParams", checkVolumeParams);
                outputBean = TootooService.callServer("shopping", checkVolumeParams, "post", new ShoppingCheckVolumeOutputData());
                Log.info(logger, uuid, "调用shopping服务checkVolumeI方法结束", "outputBean", outputBean);
                if (!TootooService.checkService(outputBean, "shopping", "checkVolumeI", inputData.getScope())) {
                    Log.info(logger, uuid, "调用shopping服务checkVolumeI方法失败,接口返回", "outputBean", outputBean);
                    return outputBean;
                }
                ShoppingCheckVolumeOutputData checkVolumeO = (ShoppingCheckVolumeOutputData)outputBean.getOutputData();
                BigDecimal planAmt = BigDecimal.ZERO;
                for (OmsCreateOrderPayListElementI payElement : inputData.getPayList()) {
                    planAmt = planAmt.add(payElement.getPayAmount());
                }
                
                if (planAmt.compareTo(new BigDecimal(checkVolumeO.getOrderMoney() == null?0:checkVolumeO.getOrderMoney())) < 0){
                    Log.info(logger, uuid, "订单金额未达到兑换券消费限额", "订单金额", planAmt, "兑换券消费金额", checkVolumeO.getOrderMoney());
                    outputBean = ResponseUtil.getBaseOutputBean(BaseOrderResultEnum.AMT_NOT_ENOUGH.getResultID(), null, inputData.getScope());
                    Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
                    return outputBean;
                }
            }
            /******************************************************************************兑换券商品校验结束********************************************************************************************/
            
            /******************************************************************************判断银行专享开始********************************************************************************************/
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
                
                outputData.setCanChgPay(0L);
            }
            /******************************************************************************判断银行专享结束********************************************************************************************/
            
            
            
            // 保存团购订单的促销信息
            Map<Long, PromotionGetPromotionByIdDetailElementO> groupMap = new HashMap<Long, PromotionGetPromotionByIdDetailElementO>();
            Map<String, BigDecimal> discountMap = new HashMap<String, BigDecimal>();// 订单满减
            Map<String, BigDecimal> otherDiscountMap = new HashMap<String, BigDecimal>();// 其他满减

            //电子卡订单
            if(OrderTypeEnum.ONLINE_CARD_VIRTUAL.getC().equals(inputData.getOrderType())){
                return new DianZiKaCreateOrderService(uuid, logger).createNormalOrder(request,inputBean, inputData, outputBean, outputData, parentOrderDao, discountMap, otherDiscountMap, groupMap, params, goodsCount, canReserve, goodsInfo, isIncludeSpecial, isCheckCOD, isNeedGift, giftGoodsId, couponSN, giftGoodsNum, payMethodId, haveOnlinePay, buyFromMap, goodsIDMax, redemptionList);
            }
            
            // 团购订单
            if (OrderTypeEnum.BAOYOU_TUANGOU_ORDER.getC().equals(inputData.getOrderType()) || OrderTypeEnum.JIELONG_GROUPBUY.getC().equals(inputData.getOrderType())) {
            
            	return new TuangouJielongOrderService(uuid, logger).createTuangouJielongOrder(request,inputBean, inputData, outputBean, outputData, parentOrderDao, discountMap, otherDiscountMap, groupMap, params, goodsCount, canReserve, goodsInfo, isIncludeSpecial, isCheckCOD, isNeedGift, giftGoodsId, couponSN, giftGoodsNum, payMethodId, haveOnlinePay, buyFromMap, goodsIDMax);
            }else{
            	
            	return normalCreateOrderService.createNormalOrder(request,inputBean, inputData, outputBean, outputData, parentOrderDao, discountMap, otherDiscountMap, groupMap, params, goodsCount, canReserve, goodsInfo, isIncludeSpecial, isCheckCOD, isNeedGift, giftGoodsId, couponSN, giftGoodsNum, payMethodId, haveOnlinePay, buyFromMap, goodsIDMax, redemptionList);
            }
            
        } catch (Exception e) {
            Log.error(logger, uuid, "下单出错", e);
            outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.INSIDE_ERROR.getResultID(), null, "");
            Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
            return outputBean;
        }
    }
    
    @Override
    public BaseService clone() throws CloneNotSupportedException {
        return new CreateOrderImpl();
    }
 
}

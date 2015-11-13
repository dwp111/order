package cn.tootoo.soa.oms.suborderoutstock;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import cn.tootoo.db.egrocery.osorder.OsOrderDao;
import cn.tootoo.db.egrocery.osorder.OsOrderPO;
import cn.tootoo.db.egrocery.osorderitems.OsOrderItemsDao;
import cn.tootoo.db.egrocery.osorderitems.OsOrderItemsPO;
import cn.tootoo.db.egrocery.osorderopt.OsOrderOptDao;
import cn.tootoo.db.egrocery.osorderopt.OsOrderOptPO;
import cn.tootoo.db.egrocery.osorderoutstockmessage.OsOrderOutstockMessageDao;
import cn.tootoo.db.egrocery.osorderoutstockmessage.OsOrderOutstockMessagePO;
import cn.tootoo.db.egrocery.texvoucher.TExVoucherDao;
import cn.tootoo.enums.BaseResultEnum;
import cn.tootoo.http.TootooService;
import cn.tootoo.http.bean.BaseInputBean;
import cn.tootoo.http.bean.BaseOutputBean;
import cn.tootoo.servlet.BaseService;
import cn.tootoo.soa.base.enums.OrderStatusEnum;
import cn.tootoo.soa.base.enums.OrderTypeEnum;
import cn.tootoo.soa.oms.exceptions.OmsDelegateException;
import cn.tootoo.soa.oms.ordercancel.output.OmsOrderCancelResultEnum;
import cn.tootoo.soa.oms.suborderoutstock.input.OmsSubOrderOutStockInputData;
import cn.tootoo.soa.oms.suborderoutstock.input.OmsSubOrderOutStockStockGoodsListElementI;
import cn.tootoo.soa.oms.suborderoutstock.output.OmsSubOrderOutStockOutputData;
import cn.tootoo.soa.oms.suborderoutstock.output.OmsSubOrderOutStockResultEnum;
import cn.tootoo.soa.oms.utils.LogUtils4Oms;
import cn.tootoo.soa.oms.utils.OrderOptIsShowEnum;
import cn.tootoo.soa.oms.utils.OrderScanningStatusEnum;
import cn.tootoo.soa.payment.refundoflack.input.PaymentRefundOfLackGoodsListElementI;
import cn.tootoo.soa.payment.refundoflack.input.PaymentRefundOfLackInputData;
import cn.tootoo.soa.payment.refundoflack.output.PaymentRefundOfLackOutputData;
import cn.tootoo.soa.stock.modifylogicstock.input.StockModifyLogicStockInputData;
import cn.tootoo.soa.stock.modifylogicstock.input.StockModifyLogicStockModifyInfoElementI;
import cn.tootoo.soa.stock.modifylogicstock.input.StockModifyLogicStockModifyListElementI;
import cn.tootoo.soa.stock.modifylogicstock.output.StockModifyLogicStockOutputData;
import cn.tootoo.utils.ResponseUtil;
import cn.tootoo.utils.StringUtil;

/**
 * 服务接口方法的实现类。
 * 
 * 服务description：订单服务。
 * 服务remark：订单服务。
 * 接口description：出库接口
 * 接口remark：
 */
public final class SubOrderOutStockImpl extends BaseService {

	private Logger logger = Logger.getLogger(this.getClass());
	
	@Override
	public BaseService clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return new SubOrderOutStockImpl();
	}
    
	@Override
	public BaseOutputBean doService(BaseInputBean inputBean) throws Exception {
		LogUtils4Oms.info(logger, uuid, "调用出库接口开始！");
        BaseOutputBean outputBean = new BaseOutputBean();
        
        OsOrderDao orderDao = new OsOrderDao();
        OsOrderItemsDao orderItemsDao = new OsOrderItemsDao();
        TExVoucherDao tExVoucherDao = new TExVoucherDao();
        OsOrderOutstockMessageDao messageDao = new OsOrderOutstockMessageDao();
        OmsSubOrderOutStockInputData inputData = null;
        OmsSubOrderOutStockOutputData outputData = new OmsSubOrderOutStockOutputData();
        
        try {
            inputData = (OmsSubOrderOutStockInputData)inputBean.getInputData();
        } catch (Exception e) {
        	LogUtils4Oms.error(logger,"uuid","接口实现类转换错误", e, "className", OmsSubOrderOutStockInputData.class.getName());
            outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.CLASS_TRANSFER_ERROR.getResultID(), null, "");
            LogUtils4Oms.info(logger, uuid, "接口返回", "outputBean", outputBean);
            return outputBean;
        }
        LogUtils4Oms.info(logger, uuid, "输入参数", "inputData",inputData.toJson());
		
        //初始化子订单表   输入商品集合
		OsOrderPO osOrderPO = orderDao.findOsOrderPOByID(inputData.getOrderId());
		LogUtils4Oms.info(logger, uuid, "子订单原始数据！", "osOrderPO",osOrderPO);
		
		Map<Long,OmsSubOrderOutStockStockGoodsListElementI> inputGoodsMap = getInputGoodsMap(inputData);	//输入商品集合
		Map<Long,OsOrderItemsPO> initItemMap = initItemMapByOrderId(inputData.getOrderId(),orderItemsDao);	//所有的订单明细集合
		if(null==initItemMap || initItemMap.size()<=0){
			//TODO  查询明细错误！
			LogUtils4Oms.info(logger, uuid, "根据orderID查找订单明细错误！","orderID",inputData.getOrderId());
			outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.INSIDE_ERROR.getResultID(), null, "");
            return outputBean;
		}
		if(initItemMap.size()!=inputGoodsMap.size()){
			LogUtils4Oms.info(logger, uuid, "输入商品列表与订单明细列表数量不一致！");
			outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.INSIDE_ERROR.getResultID(), null, "");
            return outputBean;
		}
		
		Map<Long,OsOrderItemsPO> lackItemMap = getLackItemList(initItemMap, inputGoodsMap);//缺货的订单明细
		
		StockModifyLogicStockInputData stockInputData = new StockModifyLogicStockInputData();
		if(lackItemMap != null && lackItemMap.size()>0){
			//校验是否正常缺货出库。礼盒情况下，只能整个礼盒缺货，不能单独明细缺货。
			
			
        	LogUtils4Oms.info(logger, uuid, "=========缺货出库情况开始==============");
			//商品正常出库开始
        	if(!toNormalOutStock(stockInputData, osOrderPO, initItemMap, outputBean,inputGoodsMap)){
        		LogUtils4Oms.info(logger, uuid, "调用出库接口错误！");
        		outputBean = ResponseUtil.getBaseOutputBean(OmsSubOrderOutStockResultEnum.CALL_STOCK_FAIL.getResultID(), null, "");
                return outputBean;
        	}
    		//缺货出库调用库存接口，退库存
            if(!toLackOutStock(stockInputData, osOrderPO, lackItemMap, outputBean,inputGoodsMap)){
        		LogUtils4Oms.info(logger, uuid, "调用出库接口错误--释放冻结的库存！！");
        		outputBean = ResponseUtil.getBaseOutputBean(OmsSubOrderOutStockResultEnum.CALL_STOCK_FAIL.getResultID(), null, "");
                return outputBean;
        	}
        	 
            //订单操作日志表。
            if(!addLog(osOrderPO, inputData)){
            	LogUtils4Oms.info(logger, uuid, "增加订单操作日志表失败！");
            	orderDao.rollback();
            	outputBean = ResponseUtil.getBaseOutputBean(OmsSubOrderOutStockResultEnum.ADD_OPT_FAIL.getResultID(), null, "");
                return outputBean;
            	
            }
            
            //更新订单明细信息
        	if(!updateOsOrderItem(initItemMap,lackItemMap,orderItemsDao,inputGoodsMap)){
        		LogUtils4Oms.info(logger, uuid, "更新订单明细信息失败！");
        		orderDao.rollback();
            	outputBean = ResponseUtil.getBaseOutputBean(OmsSubOrderOutStockResultEnum.UPDATE_OSORDERITEMS_FAIL.getResultID(), null, "");
                return outputBean;
        	}
        	
        	//更新订单表信息
        	if(!updateOsOrder(inputData,osOrderPO,orderDao,initItemMap,inputGoodsMap)){
        		LogUtils4Oms.info(logger, uuid, "更新订单表信息失败！");
        		orderDao.rollback();
            	outputBean = ResponseUtil.getBaseOutputBean(OmsSubOrderOutStockResultEnum.UPDATE_OSORDER_FAIL.getResultID(), null, "");
                return outputBean;
        	}
        	
//        	插入缺货短信表
        	if(!insertOutStockMessage(osOrderPO,messageDao)){
        		LogUtils4Oms.info(logger, uuid, "插入缺货出库发送短信表失败!");
        		orderDao.rollback();
        		outputBean = ResponseUtil.getBaseOutputBean(OmsSubOrderOutStockResultEnum.INSERT_MESSAGE_FAIL.getResultID(), null, "");
        		return outputBean;
        	}
        	//若缺货出库的商品使用了兑换券，那么恢复兑换券使用资格
        	Set<String> vocherSet = haveUsedSubOrderVocher(lackItemMap);
        	 if(vocherSet!=null && vocherSet.size()>0){
             	if(!updateVoucher(osOrderPO.getBuyerId(),vocherSet,tExVoucherDao)){
             		LogUtils4Oms.info(logger,uuid, "恢复用户兑换券失败！","buyerId",osOrderPO.getBuyerId(),"buyerEmail",osOrderPO.getEmail());
                 }
             	LogUtils4Oms.info(logger,uuid, "恢复用户兑换券成功！");
             }else {
             	LogUtils4Oms.info(logger, uuid, "缺货的商品未使用兑换券，不需要恢复兑换券资格！");
     		}
        	
        	
        	
        	
        	
        	Map<Long, BigDecimal> lackCouponFeeMap = new HashMap<Long, BigDecimal>();
        	
        	//调支付接口,回写子订单金额信息
        	
        	//缺货出库  更新订单明细金额相关信息 
        	BigDecimal refundFee = updateOrderItemOfLack(osOrderPO,initItemMap,lackCouponFeeMap,lackItemMap,inputGoodsMap,orderItemsDao,orderDao); 
        	//调支付缺货退款接口需传的值  若没有使用优惠券，则不传，若是礼盒，传皮
        	if(refundFee.compareTo(BigDecimal.valueOf(0))<0){
        		LogUtils4Oms.info(logger, uuid, "缺货出库时，更新订单明细错误！");
        		orderDao.rollback();
        		outputBean = ResponseUtil.getBaseOutputBean(OmsSubOrderOutStockResultEnum.UPDATE_OSORDERITEMS_FAIL.getResultID(), null, "");
        		return outputBean;
        	}
        	
            //如果退款金额=0，不掉payment接口
        	if(refundFee.compareTo(BigDecimal.valueOf(0))==0){
        		LogUtils4Oms.info(logger, uuid, "退款金额=0,不掉payment接口！");
	            orderDao.commit();
	            LogUtils4Oms.info(logger, uuid, "=========缺货出库情况完成==============");
	            outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.SUCCESS.getResultID(), null, "");
	            return outputBean;
        	}else{
	        	List<PaymentRefundOfLackGoodsListElementI> paymentGoodsList = getPaymentGoodsList(lackCouponFeeMap, inputGoodsMap);
	        	outputBean = callRefundLack(outputBean, inputData, osOrderPO, refundFee, paymentGoodsList);
	        	if(outputBean.getOutputHead().getResultID()!=0){
	        		LogUtils4Oms.info(logger, uuid, "缺货出库时，调用支付接口refundOfLack错误！","outputBean",outputBean.toJson());
	        		outputBean = ResponseUtil.getBaseOutputBean(OmsSubOrderOutStockResultEnum.CALL_PAYMENT_FAIL.getResultID(), null, "");
	        		return outputBean;
	        	}
	        	
	        	LogUtils4Oms.info(logger, uuid, "缺货出库时，更新子订单表完成！");
	        	PaymentRefundOfLackOutputData refundOutputData  = (PaymentRefundOfLackOutputData) outputBean.getOutputData();
	        	LogUtils4Oms.info(logger, uuid, "====test===更新前打印信息========", "needPayFee",osOrderPO.getNeedPayFee(),"couponFee",osOrderPO.getCouponFee(),"dicFee",osOrderPO.getDiscountFee());
	        	//returnFee:不包含优惠券，退用户的钱   needPayFee= order_callFee-discontFee
	        	
	            List<Object[]> updateObj = new ArrayList<Object[]>();
	 	        updateObj.add(new Object[]{"offline_pay_fee",osOrderPO.getOfflinePayFee().subtract(refundOutputData.getOfflineRefundFee())});
	 	        updateObj.add(new Object[]{"online_pay_fee", osOrderPO.getOnlinePayFee().subtract(refundOutputData.getOnlineRefundFee())});
	 	        updateObj.add(new Object[]{"need_pay_fee", osOrderPO.getNeedPayFee().subtract(refundFee)});
	 	        updateObj.add(new Object[]{"outstock_fee", refundFee});
	         	List<Object[]> condition = new ArrayList<Object[]>();
	         	condition.add(new Object[]{"order_id","=",osOrderPO.getOrderId()});
	         	int result = orderDao.updateOsOrderPOByCondition(updateObj, condition);
	            if(1!=result){
	            	LogUtils4Oms.info(logger, uuid, "调用支付接口后，回写子订单信息错误！");
	            	orderDao.rollback();
		        	outputBean = ResponseUtil.getBaseOutputBean(OmsSubOrderOutStockResultEnum.UPDATE_OSORDER_FAIL.getResultID(), null, "");
		        	return outputBean;	
	            }
	            LogUtils4Oms.info(logger, uuid, "缺货出库时，更新子订单表完成！");
	            orderDao.commit();
	            LogUtils4Oms.info(logger, uuid, "=========缺货出库情况完成==============");
	            outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.SUCCESS.getResultID(), null, "");
	            return outputBean;
	            
        	}
        }else{
        	LogUtils4Oms.info(logger, uuid, "=========全部正常出库情况开始==============");
        	//商品正常出库开始
        	if(!toNormalOutStock(stockInputData, osOrderPO, initItemMap, outputBean,inputGoodsMap)){
        		LogUtils4Oms.info(logger, uuid, "调用出库接口错误！");
        		outputBean = ResponseUtil.getBaseOutputBean(OmsSubOrderOutStockResultEnum.CALL_STOCK_FAIL.getResultID(), null, "");
                return outputBean;
        	}
            
            //订单操作日志表。
            if(!addLog(osOrderPO, inputData)){
            	LogUtils4Oms.info(logger, uuid, "增加订单操作日志表失败！");
            	orderDao.rollback();
            	outputBean = ResponseUtil.getBaseOutputBean(OmsSubOrderOutStockResultEnum.ADD_OPT_FAIL.getResultID(), null, "");
                return outputBean;
            	
            }
        	
          //更新订单明细信息
        	if(!updateOsOrderItem(initItemMap,lackItemMap,orderItemsDao, inputGoodsMap)){
        		LogUtils4Oms.info(logger, uuid, "更新订单明细信息失败！");
        		orderDao.rollback();
            	outputBean = ResponseUtil.getBaseOutputBean(OmsSubOrderOutStockResultEnum.UPDATE_OSORDERITEMS_FAIL.getResultID(), null, "");
                return outputBean;
        	}
        	
        	//更新订单表信息
        	if(!updateOsOrder(inputData,osOrderPO,orderDao,initItemMap,inputGoodsMap)){
        		LogUtils4Oms.info(logger, uuid, "更新订单表信息失败！");
        		orderDao.rollback();
            	outputBean = ResponseUtil.getBaseOutputBean(OmsSubOrderOutStockResultEnum.UPDATE_OSORDER_FAIL.getResultID(), null, "");
                return outputBean;
        	}
        	LogUtils4Oms.info(logger, uuid, "更新订单表信息完成！");
        	LogUtils4Oms.info(logger, uuid, "=========全部正常出库情况完成==============");
        	orderDao.commit();
        	outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.SUCCESS.getResultID(), null, "");
            return outputBean;
        }
	}
	
	public List<PaymentRefundOfLackGoodsListElementI>  getPaymentGoodsList(Map<Long,BigDecimal> lackCouponFeeMap, Map<Long,OmsSubOrderOutStockStockGoodsListElementI> inputGoodsMap){
		List<PaymentRefundOfLackGoodsListElementI> paymentGoodsList = new ArrayList<PaymentRefundOfLackGoodsListElementI>();
		for (Long goodsID : lackCouponFeeMap.keySet()) {
    			PaymentRefundOfLackGoodsListElementI goodsElementI = new PaymentRefundOfLackGoodsListElementI();
    			LogUtils4Oms.info(logger, uuid, "====test===coupeFee组装paymentGoodList明细原始","goodsId",goodsID,"couponFee",lackCouponFeeMap.get(goodsID));
             	goodsElementI.setGoodsID(goodsID);
             	goodsElementI.setRefundCouponFee(lackCouponFeeMap.get(goodsID));
             	paymentGoodsList.add(goodsElementI);
		 }
		return paymentGoodsList;
	}
	
	public BigDecimal updateOrderItemOfLack(OsOrderPO osOrderPO,Map<Long,OsOrderItemsPO> initItemMap,Map<Long, BigDecimal> lackCouponFeeMap,Map<Long,OsOrderItemsPO> lackItemMap,Map<Long,OmsSubOrderOutStockStockGoodsListElementI> inputGoodsMap,OsOrderItemsDao orderItemsDao,OsOrderDao orderDao){
		try {
			LogUtils4Oms.info(logger, uuid, "缺货出库，更新订单明细（满减金额、优惠券金额）信息开始！");
			BigDecimal refundFee = BigDecimal.valueOf(0);
			BigDecimal sum_coupFee = BigDecimal.valueOf(0);
			BigDecimal sum_discountFee = BigDecimal.valueOf(0);
			BigDecimal sum_otherDiscountFee = BigDecimal.valueOf(0);
			for (Long itemId : lackItemMap.keySet()) {
				OsOrderItemsPO itemPO = lackItemMap.get(itemId);
		    	OmsSubOrderOutStockStockGoodsListElementI itemEle = inputGoodsMap.get(itemPO.getItemId());
				BigDecimal lackNum = itemPO.getGoodsNumber().subtract(BigDecimal.valueOf(itemEle.getOutNum()));
				//缺货后，商品最终的满减优惠金额
				BigDecimal discounFee = BigDecimal.valueOf(0);
				BigDecimal _discountFee = BigDecimal.valueOf(0);
				BigDecimal db_discountFee = itemPO.getDiscountFee()==null?BigDecimal.valueOf(0):itemPO.getDiscountFee();
				if(itemEle.getOutNum()==0){
					discounFee = db_discountFee;
					_discountFee = db_discountFee.subtract(discounFee);
				}else{
					discounFee = db_discountFee.divide(itemPO.getGoodsNumber(),2,RoundingMode.HALF_UP).multiply(lackNum).setScale(2,RoundingMode.HALF_UP);
					_discountFee = db_discountFee.subtract(discounFee);
				}
	        	//分摊的其他折扣
				BigDecimal otherDisCountFee = BigDecimal.valueOf(0);
				BigDecimal _otherDisCountFee = BigDecimal.valueOf(0);
				BigDecimal db_otherdiscountFee = itemPO.getOtherDiscountFee()==null?BigDecimal.valueOf(0):itemPO.getOtherDiscountFee();
				if(itemEle.getOutNum()==0){
		        	otherDisCountFee = db_otherdiscountFee;
		        	 _otherDisCountFee = db_otherdiscountFee.subtract(otherDisCountFee);
				}else{
					otherDisCountFee = db_otherdiscountFee.divide(itemPO.getGoodsNumber(),2,RoundingMode.HALF_UP).multiply(lackNum).setScale(2,RoundingMode.HALF_UP);
					_otherDisCountFee = db_otherdiscountFee.subtract(otherDisCountFee);	
				}

	        	//分摊的优惠券金额
				BigDecimal couFee = itemPO.getCouponFee()==null?BigDecimal.valueOf(0):itemPO.getCouponFee();
				BigDecimal couponFee = BigDecimal.valueOf(0);
				BigDecimal _couponFee = BigDecimal.valueOf(0);
				if(itemEle.getOutNum()==0){
		        	 couponFee = couFee;
		        	 _couponFee = couFee.subtract(couponFee);
				}else{
		        	couponFee = couFee.divide(itemPO.getGoodsNumber(),2,RoundingMode.HALF_UP).multiply(lackNum).setScale(2,RoundingMode.HALF_UP);
		        	_couponFee = couFee.subtract(couponFee);
				}
	        	LogUtils4Oms.info(logger, uuid, "缺货商品享受的优惠信息！","goodsID",itemPO.getGoodsId(), "discounFee",discounFee,"otherDisCountFee",otherDisCountFee,"couponFee",couponFee);
	        	//便于少川订单详情页金额正确展示，这样回写。
	        	if(!itemPO.getIsGiftbox().equals("1")){//（商品价格*缺货的数量）-缺货商品分摊的满减-缺货商品分摊的其他满减
	        		BigDecimal checkFee =itemPO.getGoodsPrice().multiply(lackNum).subtract(discounFee).subtract(otherDisCountFee).subtract(couponFee);
	        		refundFee =refundFee.add(itemPO.getGoodsPrice().multiply(lackNum)).subtract(discounFee).subtract(otherDisCountFee);
	        		if(checkFee.compareTo(BigDecimal.valueOf(0))<0){
	        			refundFee=refundFee.add(BigDecimal.valueOf(0));
	        			couponFee = couFee.divide(itemPO.getGoodsNumber(),2,RoundingMode.HALF_DOWN).multiply(lackNum).setScale(2,RoundingMode.HALF_UP);
	        			_couponFee = couFee.subtract(couponFee);
	        		}
	        		sum_coupFee = sum_coupFee.add(couponFee);
	        		sum_discountFee = sum_discountFee.add(discounFee);
	        		sum_otherDiscountFee = sum_otherDiscountFee.add(otherDisCountFee);
	        	}
	        	LogUtils4Oms.info(logger, uuid, "缺货商品享受的优惠信息！","goodsID",itemPO.getGoodsId(), "discounFee",discounFee,"otherDisCountFee",otherDisCountFee,"couponFee",couponFee);
	        	if(couponFee.compareTo(BigDecimal.valueOf(0))>0){
		        	if(!itemPO.getIsGiftbox().equals("1")){
		        		if(itemPO.getGiftboxId()>0){ //是礼盒里的商品  找出礼盒
		        			OsOrderItemsPO tempPO = initItemMap.get(Long.valueOf(itemPO.getGiftboxId()));
			        		if(null!=lackCouponFeeMap.get(tempPO.getGoodsId())){
			        			lackCouponFeeMap.put(tempPO.getGoodsId(), couponFee.add(lackCouponFeeMap.get(tempPO.getGoodsId())));
			        		}else{
			        			lackCouponFeeMap.put(tempPO.getGoodsId(), couponFee);
			        		}
		        		}else{
		        			lackCouponFeeMap.put(itemPO.getGoodsId(), couponFee);
		        		}
		        	}
	        	}
             	List<Object[]> updateObj = new ArrayList<Object[]>();
     	        updateObj.add(new Object[]{"discount_fee",_discountFee});
     	        updateObj.add(new Object[]{"other_discount_fee", _otherDisCountFee});
     	        updateObj.add(new Object[]{"coupon_fee", _couponFee});
             	List<Object[]> condition = new ArrayList<Object[]>();
             	condition.add(new Object[]{"item_id","=",itemPO.getItemId()});
             	int result = orderItemsDao.updateOsOrderItemsPOByCondition(updateObj, condition);
             	if(result!=1){
             		LogUtils4Oms.info(logger, uuid, "缺货出库---更新【子订单明细】（满减金额、优惠券金额）信息错误！","item_id",itemPO.getItemId(),"goods_id",itemPO.getGoodsId());
             		return BigDecimal.valueOf(-1);
             	}
	        	
			}
		    
		    List<Object[]> updateObj = new ArrayList<Object[]>();
 	        updateObj.add(new Object[]{"discount_fee",osOrderPO.getDiscountFee().subtract(sum_discountFee).subtract(sum_otherDiscountFee)});
 	        updateObj.add(new Object[]{"coupon_fee", osOrderPO.getCouponFee().subtract(sum_coupFee)});
         	List<Object[]> condition = new ArrayList<Object[]>();
         	condition.add(new Object[]{"order_id","=",osOrderPO.getOrderId()});
         	int result = orderDao.updateOsOrderPOByCondition(updateObj, condition);
         	if(result!=1){
         		LogUtils4Oms.info(logger, uuid, "缺货出库---更新【子订单】（满减金额、优惠券金额）信息错误！","OrderId",osOrderPO.getOrderId());
         		return BigDecimal.valueOf(-1);
         	}
		    
		    LogUtils4Oms.info(logger, uuid, "缺货出库，更新订单明细（满减金额、优惠券金额）信息完成！","refundFee",refundFee);
		    return refundFee;
		} catch (Exception e) {
			e.printStackTrace();
			LogUtils4Oms.info(logger, uuid, "缺货出库，更新订单明细（满减金额、优惠券金额）信息错误！");
			return BigDecimal.valueOf(-1);
		}
	}
	
	public BaseOutputBean callRefundLack(BaseOutputBean outputBean,OmsSubOrderOutStockInputData inputData,OsOrderPO osOrderPO,BigDecimal refundFee,List<PaymentRefundOfLackGoodsListElementI> paymentGoodsList){
	 	try {
	 		PaymentRefundOfLackInputData paymentInputData  = new PaymentRefundOfLackInputData();
		 	paymentInputData.setOrderID(osOrderPO.getOrderId());
		 	paymentInputData.setOrderCode(osOrderPO.getOrderCode());
		 	paymentInputData.setOutTime(StringUtil.dateTimeToStr(new Date()));
		 	paymentInputData.setRefundFee(refundFee);
		 	paymentInputData.setConfirmUser(inputData.getUserId());
		 	paymentInputData.setGoodsList(paymentGoodsList);
		 	
		 	Map tempMap = new HashMap<String,String>();
			tempMap.put("method", "refundOfLack");
			tempMap.put("req_str", paymentInputData.toJson());
			LogUtils4Oms.info(logger, uuid, "调用payment的refundOfLack开始！","paymentInputData",paymentInputData.toJson());
			outputBean = TootooService.callServer("payment", tempMap, "post", new PaymentRefundOfLackOutputData());
		    //判断调用的接口是否正确
		    if (!TootooService.checkService("payment", "refundOfLack", outputBean)) {
		    	LogUtils4Oms.info(logger, uuid, "调用payment的refundOfLack失败！","outputBean",outputBean.toJson());
		    	outputBean = ResponseUtil.getBaseOutputBean(OmsSubOrderOutStockResultEnum.CALL_PAYMENT_FAIL.getResultID(), null, "");
		        return outputBean;
		    }
	 		
		} catch (Exception e) {
			e.printStackTrace();
		}
		LogUtils4Oms.info(logger, uuid, "调用payment的refundOfLack完成！","outputBean",outputBean);
		return outputBean;
	}
	
	public boolean insertOutStockMessage(OsOrderPO order,OsOrderOutstockMessageDao messageDao) {
		LogUtils4Oms.info(logger, uuid, "插入缺货出库发送短信表开始！");
    	OsOrderOutstockMessagePO messagePO = new OsOrderOutstockMessagePO();
    	messagePO.setOrderId(order.getOrderId());
    	messagePO.setOrderCode(order.getOrderCode());
    	messagePO.setOutDt(new Timestamp(System.currentTimeMillis()));
    	messagePO.setReceiveDt(order.getReceiveDt());
    	messagePO.setIsSend("0");
    	int result = messageDao.addOsOrderOutstockMessagePO(messagePO);
    	if(result!=1){
    		LogUtils4Oms.info(logger, uuid, "插入缺货出库短信表失败！", "num",result,"messagePO",messagePO);
    		return false;
    	}
    	LogUtils4Oms.info(logger, uuid, "插入缺货出库发送短信表成功！","messagePO",messagePO);
    	return true;
	}
	
	/**
	 * 更新子订单明细
	 * @param orderItemList
	 * @param orderItemsDao
	 * @param inputGoodsMap
	 * @return
	 */
	public boolean updateOsOrderItem(Map<Long,OsOrderItemsPO> initItemMap,Map<Long,OsOrderItemsPO> lackItemMap,OsOrderItemsDao orderItemsDao,Map<Long,OmsSubOrderOutStockStockGoodsListElementI> inputGoodsMap){
		try {
			LogUtils4Oms.info(logger, uuid, "更新订单明细信息开始！");
			for (Long itemId : initItemMap.keySet()) {
				OsOrderItemsPO orderItem = initItemMap.get(itemId);
		    	OmsSubOrderOutStockStockGoodsListElementI ele = inputGoodsMap.get(itemId);
				orderItem.setRealOutNum(BigDecimal.valueOf(ele.getOutNum()));
		    	orderItem.setGoodsPackfreshNumber(BigDecimal.valueOf(ele.getOutNum()));
	    		orderItem.setGoodsPackfreshPrice(orderItem.getGoodsPrice().multiply(BigDecimal.valueOf(ele.getOutNum())));
		    	List<Object[]> updatecondition = new ArrayList<Object[]>();
		    	updatecondition.add(new Object[]{"real_out_num", BigDecimal.valueOf(ele.getOutNum())});
		    	updatecondition.add(new Object[]{"goods_packfresh_number", BigDecimal.valueOf(ele.getOutNum())});
		    	updatecondition.add(new Object[]{"goods_packfresh_price", (lackItemMap==null||lackItemMap.size()<=0)?orderItem.getGoodsAmount():orderItem.getGoodsPrice().multiply(BigDecimal.valueOf(ele.getOutNum()))});
	        	List<Object[]> whereCondition = new ArrayList<Object[]>();
	        	whereCondition.add(new Object[]{"item_id","=",orderItem.getItemId()});
	        	int result = orderItemsDao.updateOsOrderItemsPOByCondition(updatecondition,whereCondition);
	        	if(result!=1){
	        		LogUtils4Oms.info(logger, uuid, "更新本子订单明细失败！", "orderItem",orderItem.getItemId(),"goodsId",orderItem.getGoodsId());
	        		return false;
	        	}
			}
		    LogUtils4Oms.info(logger, uuid, "更新订单明细信息完成！");
		    return true;
		    
		} catch (Exception e) {
			// TODO: handle exception
			 LogUtils4Oms.info(logger, uuid, "更新订单明细信息错误！");
			 return false;
		}
	}
	
   /**
    * 更新订单表
    * @param osOrderPO
    * @param orderDao
    * @param returnFee
    * @param allItemList
    * @param inputGoodsMap
    * @return
    */
	public boolean  updateOsOrder (OmsSubOrderOutStockInputData inputData,OsOrderPO osOrderPO,OsOrderDao orderDao,Map<Long,OsOrderItemsPO> initItemMap,Map<Long,OmsSubOrderOutStockStockGoodsListElementI> inputGoodsMap){
		try {
			LogUtils4Oms.info(logger, uuid, "更新订单表信息开始！");
			BigDecimal goodsPackfreshFee = getGoodsPackFreshFee(initItemMap,inputGoodsMap);//单价*数量
	        List<Object[]> updateObj = new ArrayList<Object[]>();
	        updateObj.add(new Object[]{"package_num",inputData.getPackageNum()});
	        updateObj.add(new Object[]{"goods_packfresh_fee", goodsPackfreshFee});
	        updateObj.add(new Object[]{"order_packfresh_fee", goodsPackfreshFee.add(osOrderPO.getShipCallFee())});
	        updateObj.add(new Object[]{"order_final_fee", goodsPackfreshFee.add(osOrderPO.getShipCallFee())});
	        updateObj.add(new Object[]{"scanning_status", OrderScanningStatusEnum.FINISH.getC()});
	        updateObj.add(new Object[]{"order_status", OrderStatusEnum.OUT_OF_STORAGE.getC()});
	        updateObj.add(new Object[]{"out_dt", new Timestamp(System.currentTimeMillis())});
        	List<Object[]> condition = new ArrayList<Object[]>();
        	condition.add(new Object[]{"order_id","=",osOrderPO.getOrderId()});
//        	condition.add(new Object[]{"order_status","=",OrderStatusEnum.OUTING.getC()});
        	int result = orderDao.updateOsOrderPOByCondition(updateObj, condition);
        	if(result!=1){
        		LogUtils4Oms.info(logger, uuid, "更新本子订单失败！", "orderID",osOrderPO.getOrderId());
        		return false;
        	}
	        LogUtils4Oms.info(logger, uuid, "更新订单表信息完成！");
	        return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
       
	}
	/**
	 * 出库商品的累计金额
	 * @param allItemList
	 * @param inputGoodsMap
	 * @return
	 */
	public BigDecimal getGoodsPackFreshFee(Map<Long,OsOrderItemsPO> initItemMap,Map<Long,OmsSubOrderOutStockStockGoodsListElementI> inputGoodsMap){
		//更新订单表信息
		BigDecimal goodsPackfreshFee = new BigDecimal(0);
		for (Long itemId : initItemMap.keySet()) {
			OsOrderItemsPO itemsPO = initItemMap.get(itemId);
			if(itemsPO.getGiftboxId()>0){
				continue;//是礼盒 内部商品，过滤，按照礼盒算就可以。
			}
			OmsSubOrderOutStockStockGoodsListElementI ele = inputGoodsMap.get(itemId);
			if(ele.getOutNum()==null || ele.getOutNum()==0){
				continue;
			}
			if(itemsPO.getGiftboxId()==0 && !itemsPO.getIsGiftbox().equals("1")){
				goodsPackfreshFee = goodsPackfreshFee.add(itemsPO.getGoodsPrice().multiply(BigDecimal.valueOf(ele.getOutNum())));
			}
			
			if(itemsPO.getIsGiftbox().equals("1")){
				goodsPackfreshFee = goodsPackfreshFee.add(itemsPO.getGoodsPrice().multiply(BigDecimal.valueOf(ele.getOutNum())));
			}
		}
		LogUtils4Oms.info(logger, uuid, "===========goodsPackfreshFee ======", "goodsPackfreshFee",goodsPackfreshFee);
		return goodsPackfreshFee;
	}
	
	/**
	 * 增加订单操作日志
	 * @param osOrderPO
	 * @param inputData
	 * @return
	 * @throws Exception
	 */
	private boolean addLog(OsOrderPO osOrderPO,OmsSubOrderOutStockInputData inputData) throws Exception {
		LogUtils4Oms.info(logger, uuid, "增加订单操作日志表开始！");
		OsOrderOptPO opt = new OsOrderOptPO();
		OsOrderOptDao optDao = new OsOrderOptDao();
		opt.setOptDt(new Timestamp(System.currentTimeMillis()));
		opt.setOrderId(osOrderPO.getOrderId());
		opt.setOrderDisputeStatus(osOrderPO.getDisputeStatus());
		opt.setOrderStatus(OrderStatusEnum.OUT_OF_STORAGE.getC());
		opt.setOrderExceptionStatus(osOrderPO.getExceptionStatus());
		opt.setOrderPayStatus(osOrderPO.getPayStatus());
		opt.setRemark("您的订单已经出库，很快为您发货！");
		opt.setUserId(inputData.getUserId());
		opt.setUserName(inputData.getUserName());
		opt.setIsShow(OrderOptIsShowEnum.SHOW.getC());
		opt.setRemarkEn("Your order is ready for dispatch to the distribution center");
		
		int result = optDao.addOsOrderOptPO(opt);
		if(1!=result){
			return false;
		}
		LogUtils4Oms.info(logger, uuid, "增加订单操作日志成功！","OsOrderOptPO",opt);
		return true;
	}
	
	
	/**
	 * 根据出库商品或者缺货商品 返回 调用库存的商品list
	 * @param callType
	 * @param itemList
	 * @param osOrderPO
	 * @param inputGoodsMap
	 * @return
	 */
	public List<StockModifyLogicStockModifyListElementI> getModifyList(int callType,Map<Long,OsOrderItemsPO> itemMap,OsOrderPO osOrderPO,Map<Long,OmsSubOrderOutStockStockGoodsListElementI> inputGoodsMap){
		List<StockModifyLogicStockModifyListElementI> modifyListElementIs = new ArrayList<StockModifyLogicStockModifyListElementI>();
    	for (Long itemId : itemMap.keySet()) {
    		OsOrderItemsPO itemsPO = itemMap.get(itemId);
			//礼盒皮儿不调库存
    		if(itemsPO.getIsGiftbox().equals("1")){
    			continue;
    		}
    		
    		StockModifyLogicStockModifyListElementI elementI =  new StockModifyLogicStockModifyListElementI();
    		elementI.setGoodsBasicID(itemsPO.getBasicId());
    		elementI.setSubstationID(osOrderPO.getSubstationId());
    		elementI.setWarehouseID(osOrderPO.getWarehouseId());
    		 // 如果是下架促销，退残品库存
            if(OrderTypeEnum.PULLOFF_SHELVES.getC().equals(osOrderPO.getOrderType())){
            	elementI.setIsDefective("1");
            }else{
            	elementI.setIsDefective("0");
            }
            BigDecimal outNum = BigDecimal.valueOf(inputGoodsMap.get(itemsPO.getItemId()).getOutNum());
           
            if(callType==1){//callTyp=1,缺货出库类型  amount传 缺货数量
            	BigDecimal lackNum = itemsPO.getGoodsNumber().subtract(outNum);
            	elementI.setAmount(lackNum);
            }else{
            	 if(outNum.compareTo(BigDecimal.valueOf(0))<=0){
                 	continue;
                 }
            	elementI.setAmount(outNum);
            }
            
    		modifyListElementIs.add(elementI);
		}
    	return modifyListElementIs;
	}
	
	/**
	 * 正常出库
	 * @param stockInputData
	 * @param osOrderPO
	 * @param itemList
	 * @param outputBean
	 * @param inputGoodsMap
	 * @return
	 */
	public boolean toNormalOutStock(StockModifyLogicStockInputData stockInputData,OsOrderPO  osOrderPO,Map<Long,OsOrderItemsPO> initItemMap,BaseOutputBean outputBean,Map<Long,OmsSubOrderOutStockStockGoodsListElementI> inputGoodsMap){
		LogUtils4Oms.info(logger, uuid, "商品正常出库开始！");
		try {
	    	stockInputData.setRequestType("2");
	    	stockInputData.setRequestorCode(osOrderPO.getParentCode());
	    	stockInputData.setRelatedRequestorCode(osOrderPO.getOrderCode());
	    	
	    	if (OrderTypeEnum.isTiHuoOrder(osOrderPO.getOrderType())) {
	    		stockInputData.setRequestorType("4");//提货订单（提货订单下单不占库存，出库不释放冻结数）
    		}else {
    			stockInputData.setRequestorType("0");//普通订单
    		}
	    	
	    	StockModifyLogicStockModifyInfoElementI modifyInfo = new StockModifyLogicStockModifyInfoElementI();
	    	modifyInfo.setModifyList(getModifyList(0,initItemMap,osOrderPO, inputGoodsMap));
	    	stockInputData.setModifyInfo(modifyInfo);
	    	Map tempMap = new HashMap<String,String>();
	        tempMap.put("method", "modifyLogicStock");
	        tempMap.put("req_str", stockInputData.toJson());
	        LogUtils4Oms.info(logger, uuid, "调用库存服务modifyLogicStock开始！","stockInputData",stockInputData.toJson());
	        outputBean = TootooService.callServer("stock", tempMap, "post", new StockModifyLogicStockOutputData());
	        //判断调用的接口是否正确
	        if (!TootooService.checkService("stock", "modifyLogicStock", outputBean)) {
	        	LogUtils4Oms.info(logger, uuid, "调用库存服务modifyLogicStock失败！","outputBean",outputBean.toJson());
	            return false;
	        }
     	} catch (Exception e) {
     		LogUtils4Oms.info(logger, uuid, "调用库存服务modifyLogicStock失败！");
     		e.printStackTrace();
            return false;
		}
        LogUtils4Oms.info(logger, uuid, "调用库存服务modifyLogicStock成功！出库完成！");
        LogUtils4Oms.info(logger, uuid, "商品正常出库完成！");
		return true;
	 }
	 
	/**
	 * 缺货出库
	 * @param stockInputData
	 * @param osOrderPO
	 * @param itemList
	 * @param outputBean
	 * @param inputGoodsMap
	 * @return
	 */
	 public boolean toLackOutStock(StockModifyLogicStockInputData stockInputData,OsOrderPO  osOrderPO,Map<Long,OsOrderItemsPO> lackItemMap,BaseOutputBean outputBean,Map<Long,OmsSubOrderOutStockStockGoodsListElementI> inputGoodsMap){
		//缺货商品出库
		 try {
			 LogUtils4Oms.info(logger, uuid, "缺货商品出库开始！");
	 		 stockInputData.setRequestType("1");  //调用库存类型，释放冻结数      
	     	 stockInputData.setRequestorType("0");
	     	 stockInputData.setRequestorCode(osOrderPO.getParentCode()); //父订单号
	     	 stockInputData.setRelatedRequestorCode(osOrderPO.getOrderCode()); //子订单号
	     	 StockModifyLogicStockModifyInfoElementI modifyInfo = new StockModifyLogicStockModifyInfoElementI();
	     	 modifyInfo.setModifyList(getModifyList(1,lackItemMap, osOrderPO, inputGoodsMap));
	     	 stockInputData.setModifyInfo(modifyInfo);
	         Map tempMap = new HashMap<String,String>();
	         tempMap.put("method", "modifyLogicStock");
	         tempMap.put("req_str", stockInputData.toJson());
	         LogUtils4Oms.info(logger, uuid, "调用库存服务modifyLogicStock开始！","stockInputData",stockInputData.toJson());
	         outputBean = TootooService.callServer("stock", tempMap, "post", new StockModifyLogicStockOutputData());
	         //判断调用的接口是否正确
	         if (!TootooService.checkService("stock", "modifyLogicStock", outputBean)) {
	        	 LogUtils4Oms.info(logger, uuid, "调用库存服务modifyLogicStock失败！","outputBean",outputBean.toJson());
	        	 return false;
	         }
		 } catch (Exception e) {
			 LogUtils4Oms.info(logger, uuid, "调用库存服务modifyLogicStock失败！");
			 e.printStackTrace();
			 return false;
		 }
		 
		 LogUtils4Oms.info(logger, uuid, "调用库存服务modifyLogicStock成功！");
		 LogUtils4Oms.info(logger, uuid, "缺货商品出库完成！");
		 return true;
	}
	 
	 /**
	  * 缓存入参的商品列表
	  * @param inputData
	  * @return
	  */
	 public Map<Long,OmsSubOrderOutStockStockGoodsListElementI> getInputGoodsMap(OmsSubOrderOutStockInputData inputData){
		Map<Long,OmsSubOrderOutStockStockGoodsListElementI> inputGoodsMap = new HashMap<Long, OmsSubOrderOutStockStockGoodsListElementI>();
		for (OmsSubOrderOutStockStockGoodsListElementI ele : inputData.getStockGoodsList()) {
			inputGoodsMap.put(ele.getItemID(), ele);
		}
		return inputGoodsMap;
	}
	
	/**
	 * 根据orderID查找订单明细List
	 * @param orderId
	 * @param orderItemsDao
	 * @return
	 */
	public List<OsOrderItemsPO> getItemsByOrderId(Long orderId,Map<Long,OsOrderItemsPO> itemMap,OsOrderItemsDao orderItemsDao){
		List<Object[]> conditions = new ArrayList<Object[]>();
		conditions.add(new Object[]{"ORDER_ID", "=", orderId});
		List<OsOrderItemsPO> itemList =  orderItemsDao.findOsOrderItemsPOListByCondition(conditions);
		if(null == itemList){
			return null;
		}
		for (OsOrderItemsPO osOrderItemsPO : itemList) {
			itemMap.put(osOrderItemsPO.getItemId(), osOrderItemsPO);
		}
		return itemList;
	}
	
	
	public Map<Long,OsOrderItemsPO> initItemMapByOrderId(Long orderId,OsOrderItemsDao orderItemsDao){
		Map<Long,OsOrderItemsPO> itemMap = new HashMap<Long,OsOrderItemsPO>();
		List<Object[]> conditions = new ArrayList<Object[]>();
		conditions.add(new Object[]{"ORDER_ID", "=", orderId});
		List<OsOrderItemsPO> itemList =  orderItemsDao.findOsOrderItemsPOListByCondition(conditions);
		if(null == itemList || itemList.size()<=0){
			return null;
		}
		for (OsOrderItemsPO osOrderItemsPO : itemList) {
			itemMap.put(osOrderItemsPO.getItemId(), osOrderItemsPO);
		}
		return itemMap;
	}
	 
	 
	/**
	 * 获得缺货出库的订单明细
	 * @param itemList
	 * @param inputGoodsMap
	 * @return
	 */
	public Map<Long,OsOrderItemsPO> getLackItemList(Map<Long,OsOrderItemsPO> initItemMap,Map<Long,OmsSubOrderOutStockStockGoodsListElementI> inputGoodsMap){
		Map<Long,OsOrderItemsPO> outstockItemMap = new HashMap<Long,OsOrderItemsPO>();
		for (Long itemId : initItemMap.keySet()) {
			OmsSubOrderOutStockStockGoodsListElementI ele = inputGoodsMap.get(itemId);
			if(initItemMap.get(itemId).getGoodsNumber().compareTo(BigDecimal.valueOf(ele.getOutNum()))!=0){
				LogUtils4Oms.info(logger, uuid, "缺货的商品==","itemID",ele.getItemID(), "goodsID",initItemMap.get(itemId).getGoodsId(),"goodsNumber",initItemMap.get(itemId).getGoodsNumber(),"outNum",ele.getOutNum());
				outstockItemMap.put(itemId, initItemMap.get(itemId));
			}
		}
		return outstockItemMap;

	}

	 public Set<String> haveUsedSubOrderVocher(Map<Long,OsOrderItemsPO> lackItemMap){
    	Set<String> set = new HashSet<String>();
    	for (Long itemID : lackItemMap.keySet()) {
    		OsOrderItemsPO item = lackItemMap.get(itemID);
			if(item.getExCouponSn()!=null && item.getExCouponSn().length()>0){
				set.add(item.getExCouponSn());
			}
		}
    	return set;
    }
	 public boolean updateVoucher(Long buyerID,Set<String> vocherSet,TExVoucherDao tExVoucherDao){
	    	for (String vocher : vocherSet) {
	    		List<Object[]> qualifyCondition = new ArrayList<Object[]>();
	        	qualifyCondition.add(new Object[]{"STATUS", 2});
	        	qualifyCondition.add(new Object[]{"ORDER_CODE", ""});
	        	
	        	List<Object[]> whereCondition = new ArrayList<Object[]>();
	        	whereCondition.add(new Object[]{"BIND_BY_ID","=", buyerID});
	        	whereCondition.add(new Object[]{"STATUS","=",3});
	        	whereCondition.add(new Object[]{"VOUCHER_SN","=",vocher});
	        	int result = tExVoucherDao.updateTExVoucherPOByCondition(qualifyCondition, whereCondition);
	        	if(1!=result){
	        		LogUtils4Oms.info(logger, uuid, "恢复此兑换券资格失败！", "buyerID",buyerID,"vocher",vocher);
	        		return false;
	        	}
			}
	    	LogUtils4Oms.info(logger, uuid, "恢复用户公券资格成功！");
	    	return true; 
	    }
}
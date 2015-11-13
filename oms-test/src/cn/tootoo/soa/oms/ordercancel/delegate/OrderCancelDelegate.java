package cn.tootoo.soa.oms.ordercancel.delegate;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.tootoo.db.egrocery.buildingqualification.BuildingQualificationPO;
import cn.tootoo.db.egrocery.buyerfirstorder.BuyerFirstOrderDao;
import cn.tootoo.db.egrocery.osorder.OsOrderPO;
import cn.tootoo.db.egrocery.tbindorder.TBindOrderPO;
import cn.tootoo.db.egrocery.tosparentorder.TOsParentOrderPO;
import cn.tootoo.db.egrocery.tosparentorderitems.TOsParentOrderItemsPO;
import cn.tootoo.db.egrocery.tosparentorderopt.TOsParentOrderOptPO;
import cn.tootoo.enums.BaseResultEnum;
import cn.tootoo.enums.BooleanEnum;
import cn.tootoo.global.Global;
import cn.tootoo.http.AuthorizeClient;
import cn.tootoo.http.TootooService;
import cn.tootoo.http.bean.BaseInputHead;
import cn.tootoo.http.bean.BaseOutputBean;
import cn.tootoo.http.bean.BaseOutputHead;
import cn.tootoo.soa.base.enums.ActiveTypeEnum;
import cn.tootoo.soa.base.enums.OrderTypeEnum;
import cn.tootoo.soa.base.enums.ParentOrderStatusEnum;
import cn.tootoo.soa.base.util.BuyerFirstOrderUtil;
import cn.tootoo.soa.base.util.LockUtil;
import cn.tootoo.soa.oms.exceptions.OmsDelegateException;
import cn.tootoo.soa.oms.getbuyerpromotionnum.input.OmsGetBuyerPromotionNumGoodsListElementI;
import cn.tootoo.soa.oms.getbuyerpromotionnum.input.OmsGetBuyerPromotionNumInputData;
import cn.tootoo.soa.oms.getbuyerpromotionnum.input.OmsGetBuyerPromotionNumPromotionListElementI;
import cn.tootoo.soa.oms.getbuyerpromotionnum.output.OmsGetBuyerPromotionNumGoodsListElementO;
import cn.tootoo.soa.oms.getbuyerpromotionnum.output.OmsGetBuyerPromotionNumOutputData;
import cn.tootoo.soa.oms.ordercancel.input.OmsOrderCancelInputData;
import cn.tootoo.soa.oms.ordercancel.output.OmsOrderCancelOutputData;
import cn.tootoo.soa.oms.ordercancel.output.OmsOrderCancelResultEnum;
import cn.tootoo.soa.oms.setbuyerpromotionnum.input.OmsSetBuyerPromotionNumGoodsListElementI;
import cn.tootoo.soa.oms.setbuyerpromotionnum.input.OmsSetBuyerPromotionNumInputData;
import cn.tootoo.soa.oms.setbuyerpromotionnum.output.OmsSetBuyerPromotionNumOutputData;
import cn.tootoo.soa.oms.utils.LogUtils4Oms;
import cn.tootoo.soa.payment.cancel.output.PaymentCancelOutputData;
import cn.tootoo.soa.payment.pcancel.input.PaymentPCancelInputData;
import cn.tootoo.soa.promotion.increamenttuangoucount.input.PromotionIncreamentTuangouCountGoodsInfoElementI;
import cn.tootoo.soa.promotion.increamenttuangoucount.input.PromotionIncreamentTuangouCountInputData;
import cn.tootoo.soa.promotion.increamenttuangoucount.output.PromotionIncreamentTuangouCountOutputData;
import cn.tootoo.soa.promotion.updateweixinjielongbuyhistory.input.PromotionUpdateWeixinjielongBuyHistoryBuyHistoryElementI;
import cn.tootoo.soa.promotion.updateweixinjielongbuyhistory.input.PromotionUpdateWeixinjielongBuyHistoryInputData;
import cn.tootoo.soa.promotion.updateweixinjielongbuyhistory.output.PromotionUpdateWeixinjielongBuyHistoryOutputData;
import cn.tootoo.soa.shipping.occupyshippingpower.input.ShippingOccupyShippingPowerInputData;
import cn.tootoo.soa.shipping.occupyshippingpower.input.ShippingOccupyShippingPowerOccupyInfoElementI;
import cn.tootoo.soa.shipping.occupyshippingpower.input.ShippingOccupyShippingPowerOccupyListElementI;
import cn.tootoo.soa.shipping.occupyshippingpower.output.ShippingOccupyShippingPowerOutputData;
import cn.tootoo.soa.stock.modifylogicstock.input.StockModifyLogicStockInputData;
import cn.tootoo.soa.stock.modifylogicstock.input.StockModifyLogicStockModifyInfoElementI;
import cn.tootoo.soa.stock.modifylogicstock.input.StockModifyLogicStockModifyListElementI;
import cn.tootoo.soa.stock.modifylogicstock.output.StockModifyLogicStockOutputData;
import cn.tootoo.utils.Log;
import cn.tootoo.utils.StringUtil;

/**
 * 服务委托者实现类。
 * 服务description：订单服务。
 * 服务remark：订单服务。
 * 接口description：父订单取消。
 * 接口remark：
 */
public final class OrderCancelDelegate extends AbstractOrderCancelDelegate implements Cloneable {
    
    static {
        OrderCancelDelegateFactory.registPrototype(new OrderCancelDelegate());
    }
    
    /**
     * 克隆方法。
     */
    @Override
    public AbstractOrderCancelDelegate clone() throws CloneNotSupportedException {
        return new OrderCancelDelegate();
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
    public void doInit(final BaseInputHead inputHead, final OmsOrderCancelInputData inputData, final BaseOutputHead outputHead, final OmsOrderCancelOutputData outputData) throws OmsDelegateException {}
    
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
    public boolean doCheck(final BaseInputHead inputHead, final OmsOrderCancelInputData inputData, final BaseOutputHead outputHead, final OmsOrderCancelOutputData outputData) {
        try {
            return true;
        } catch (Exception e) {
            LogUtils4Oms.info(logger, uuid, "初始检查方法出现异常，返回false！", e);
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
    public void doExecute(final BaseInputHead inputHead, final OmsOrderCancelInputData inputData, final BaseOutputHead outputHead, final OmsOrderCancelOutputData outputData) throws OmsDelegateException {
        // 接受参数
        Long orderId = inputData.getOrderId();
        Long userId = inputData.getUserId();
        Long caller = inputData.getCaller();
        Long isRefund = inputData.getIsRefund();
        Long cancelReason = inputData.getCancelReason();
        String cancelReasonOther = inputData.getCancelReasonOther();
        if (!StringUtil.isEmpty(cancelReasonOther) && cancelReasonOther.length() > 200){
            LogUtils4Oms.info(logger, uuid, "传过来作废原因", "cancelReasonOther", cancelReasonOther);
            cancelReasonOther = cancelReasonOther.substring(0, 200);
            LogUtils4Oms.info(logger, uuid, "截取200长度作废原因", "cancelReasonOther", cancelReasonOther);
        }
        
        
        if (orderId == null) {
            LogUtils4Oms.info(logger, uuid, "提交作废orderId为空", "orderId", orderId);
            throw new OmsDelegateException(OmsOrderCancelResultEnum.CANCEL_FAIL.getResultID(), "提交作废orderId为空", null);
        }
        if (caller > -1 || caller < -3) {
            LogUtils4Oms.info(logger, uuid, "参数caller不正确!", "caller", caller);
            throw new OmsDelegateException(OmsOrderCancelResultEnum.CANCEL_FAIL.getResultID(), "参数caller不正确!", null);
        }
        
        TOsParentOrderPO parentOrder = tOsParentOrderDao.findTOsParentOrderPOByID(orderId, false, false, false, true);
        if (parentOrder == null) {
            LogUtils4Oms.info(logger, uuid, "作废订单不存在!", "orderId", orderId);
            throw new OmsDelegateException(OmsOrderCancelResultEnum.CANCEL_FAIL.getResultID(), "订单不存在！", null);
        }
        try {
            boolean lock = LockUtil.lock(logger, uuid, parentOrder.getOrderCode(), 1, "父订单作废");
            if (!lock) {
                LogUtils4Oms.info(logger, uuid, "父订单作废加锁失败", "orderCode", parentOrder.getOrderCode());
                throw new OmsDelegateException(OmsOrderCancelResultEnum.CANCEL_FAIL.getResultID(), "父订单作废加锁失败，请稍后再试！", null);
            }
        } catch (Exception e1) {
            LogUtils4Oms.error(logger, uuid, "父订单作废加锁失败，请稍后再试！", e1, "orderCode", parentOrder.getOrderCode());
            throw new OmsDelegateException(OmsOrderCancelResultEnum.CANCEL_FAIL.getResultID(), "父订单作废加锁失败，请稍后再试！", e1);
        }
        
        try {
            // 父订单作废
            cancelParentOrder(parentOrder, userId, caller, isRefund, cancelReason, cancelReasonOther, outputHead);
            LogUtils4Oms.info(logger, uuid, "父订单取消成功！", "orderId", orderId);
            Global.getOutputHead(BaseResultEnum.SUCCESS.getResultID(), null, outputHead);
            // 提交
            tOsParentOrderDao.commit();
        } catch (OmsDelegateException e) {
            LogUtils4Oms.info(logger, uuid, "父订单作废失败", e);
            Global.getOutputHead(e.getResultID(), null, outputHead);
            // 回滚
            tOsParentOrderDao.rollback();
        } catch (Exception e) {
            LogUtils4Oms.info(logger, uuid, "提交作废失败!", e);
            Global.getOutputHead(OmsOrderCancelResultEnum.CANCEL_FAIL.getResultID(), null, outputHead);
            // 回滚
            tOsParentOrderDao.rollback();
            throw new OmsDelegateException(OmsOrderCancelResultEnum.CANCEL_FAIL.getResultID(), "提交作废失败!", e);
        } finally {
            try {
                LogUtils4Oms.info(logger, uuid,  "解锁", "orderCode", parentOrder.getOrderCode());
                LockUtil.unlock(logger, uuid, parentOrder.getOrderCode());
            } catch (Exception e) {
                LogUtils4Oms.error(logger, uuid, "解锁出错", e, "orderCode", parentOrder.getOrderCode());
            }
        }
    }
    
    // 父订单作废
    private void cancelParentOrder(TOsParentOrderPO parentOrder, Long userId, Long caller, Long isRefund, Long cancelReason, String cancelReasonOther, final BaseOutputHead outputHead) throws OmsDelegateException, Exception {
        // 如果订单已经作废了，则直接返回成功
        if (parentOrder.getOrderStatus() <= -1 && parentOrder.getOrderStatus() >= -3) {
            LogUtils4Oms.info(logger, uuid, "父订单已经作废!", "orderCode", parentOrder.getOrderCode());
            Global.getOutputHead(BaseResultEnum.SUCCESS.getResultID(), null, outputHead);
            return;
        }
        
        if (parentOrder.getOrderStatus() == -4 && isRefund != 0L && isRefund != 1L) {
            LogUtils4Oms.info(logger, uuid, "异常订单作废，【是否退款】参数错误！", "orderCode", parentOrder.getOrderCode(), "isRefund", isRefund);
            throw new OmsDelegateException(BaseResultEnum.REQUEST_PARAM_ERROR.getResultID(), "异常订单作废，【是否退款】参数错误！", null);
        }else if(parentOrder.getOrderStatus() != -4 && isRefund != null){
            LogUtils4Oms.info(logger, uuid, "普通订单作废，【是否退款】参数必须为空！", "orderCode", parentOrder.getOrderCode(), "isRefund", isRefund);
            throw new OmsDelegateException(BaseResultEnum.REQUEST_PARAM_ERROR.getResultID(), "普通订单作废，【是否退款】参数必须为空！", null);
        }
        

        // 异常拦截订单
        long realOrderStatus = parentOrder.getOrderStatus();
        boolean returnFee = true;
        if (realOrderStatus == -4) {
            if(isRefund == 0L){
                returnFee = false;
            }
            List<Object[]> orderConditions = new ArrayList<Object[]>();
            orderConditions.add(new Object[]{"PARENT_CODE", "=", parentOrder.getOrderCode()});
            List<OsOrderPO> orderList = osOrderDao.findOsOrderPOListByCondition(orderConditions);
            if(orderList != null && orderList.size() > 0){
                LogUtils4Oms.info(logger, uuid, "异常拦截订单已经拆单，不能进行作废!", "orderCode", parentOrder.getOrderCode());
                throw new OmsDelegateException(OmsOrderCancelResultEnum.CANCEL_FAIL.getResultID(), "异常拦截订单已经拆单，不能进行作废!", null);
            }
            
            List<Object[]> bindOrderConditions = new ArrayList<Object[]>();
            bindOrderConditions.add(new Object[]{"STATUS", "=", "0"});
            bindOrderConditions.add(new Object[]{"IS_PARENT", "=", "1"});
            bindOrderConditions.add(new Object[]{"ORDER_CODE", "=", parentOrder.getOrderCode()});
            List<TBindOrderPO> bindOrderList = tBindOrderDao.findTBindOrderPOListByCondition(bindOrderConditions, false, false, true);
            if (bindOrderList == null || bindOrderList.size() != 1){
                LogUtils4Oms.info(logger, uuid, "异常订单查询历史状态错误!", "orderCode", parentOrder.getOrderCode(), "bindOrderList", bindOrderList);
                throw new OmsDelegateException(OmsOrderCancelResultEnum.CANCEL_FAIL.getResultID(), "异常订单查询历史状态错误!", null);
            }
            LogUtils4Oms.info(logger, uuid, "订单拦截表信息", "tBindOrderPO", bindOrderList.get(0));
            String oldStatus = bindOrderList.get(0).getOrderStatus();
            if("2".equals(oldStatus)){ 
                oldStatus = "1"; // 原始订单状态特殊处理(原始订单置为异常订单时会把1变为2)
            }
            realOrderStatus = Long.parseLong(oldStatus);
        }
        if (!canCancelParentOrder(realOrderStatus)) {
            LogUtils4Oms.info(logger, uuid, "父订单已经拆单，不能进行作废!", "orderCode", parentOrder.getOrderCode(), "orderStatus", parentOrder.getOrderStatus());
            throw new OmsDelegateException(OmsOrderCancelResultEnum.CANCEL_FAIL.getResultID(), "订单已经拆单，不能进行作废！", null);
        }
        if (caller == -1 && "0".equals(parentOrder.getPayReceiveStatus())) {
            LogUtils4Oms.info(logger, uuid, "订单状态为初始化状态，不能进行作废!", "parentOrder", parentOrder);
            throw new OmsDelegateException(OmsOrderCancelResultEnum.CANCEL_FAIL.getResultID(), "订单状态为初始化状态，不能进行作废！", null);
        }
        if (realOrderStatus == 1) {// 初始化状态作废
            cancelOrderCommonmethod(false, false, false, parentOrder, userId, caller, cancelReason, cancelReasonOther);
            Global.getOutputHead(BaseResultEnum.SUCCESS.getResultID(), null, outputHead);
            return;
        }
        
        if(null != parentOrder.getActiveId() && "1".equals(parentOrder.getActiveType())){
        	if (realOrderStatus == 10) {
                cancelOrderCommonmethod(false, false, false, parentOrder, userId, caller, cancelReason, cancelReasonOther);
            } else if (realOrderStatus == 20) {
                cancelOrderCommonmethod(true, false, false, parentOrder, userId, caller, cancelReason, cancelReasonOther);
            } else {
                cancelOrderCommonmethod(true, false, returnFee, parentOrder, userId, caller, cancelReason, cancelReasonOther);
            }
        }else{
        	String orderType = parentOrder.getOrderType();
            switch (OrderTypeEnum.get(orderType)) {
                // 包邮团购、接龙团购的不占运力，提货卡不占库存，售卡订单与普通订单相同
                case JIELONG_GROUPBUY:
                case BAOYOU_TUANGOU_ORDER:
                    if (realOrderStatus == 10) {
                        cancelOrderCommonmethod(false, false, false, parentOrder, userId, caller, cancelReason, cancelReasonOther);
                    } else if (realOrderStatus == 20) {
                        cancelOrderCommonmethod(true, false, false, parentOrder, userId, caller, cancelReason, cancelReasonOther);
                    } else {
                        cancelOrderCommonmethod(true, false, returnFee, parentOrder, userId, caller, cancelReason, cancelReasonOther);
                    }
                    break;
                case TIHUO_ORDER:
                case VIRTUAL_TIHUO_ORDER:
                    if (realOrderStatus == 10 || realOrderStatus == 20) {
                        cancelOrderCommonmethod(false, true, false, parentOrder, userId, caller, cancelReason, cancelReasonOther);
                    } else {
                        cancelOrderCommonmethod(false, true, true, parentOrder, userId, caller, cancelReason, cancelReasonOther);
                    }
                    break;
                case GIFT_CARD_ORDER:
                    if (realOrderStatus == 10 || realOrderStatus == 20) {
                        cancelOrderCommonmethod(false, true, false, parentOrder, userId, caller, cancelReason, cancelReasonOther);
                    } else {
                        cancelOrderCommonmethod(false, true, returnFee, parentOrder, userId, caller, cancelReason, cancelReasonOther);
                    }
                    break;
                case ONLINE_CARD_VIRTUAL:
                    // 电子卡订单不占库存，不占运力，支付成功不允许作废
                    if (caller == -1){
                        // 如果是是系统取消
                        cancelOrderCommonmethod(false, false, true, parentOrder, userId, caller, cancelReason, cancelReasonOther);
                    } else if (realOrderStatus == ParentOrderStatusEnum.OCCUPY_SHIPPINGCAPACITY.getStatus()
                                    || realOrderStatus == ParentOrderStatusEnum.OCCUPY_STOCK.getStatus()
                                    || realOrderStatus == ParentOrderStatusEnum.PAY_FAILED.getStatus()) {
                        cancelOrderCommonmethod(false, false, false, parentOrder, userId, caller, cancelReason, cancelReasonOther);
                    } else if(realOrderStatus == ParentOrderStatusEnum.TO_PAY.getStatus()){
                        cancelOrderCommonmethod(false, false, true, parentOrder, userId, caller, cancelReason, cancelReasonOther);
                    } else {
                        LogUtils4Oms.info(logger, uuid, "该订单属于电子卡订单且已支付，不能进行作废！", "parentOrder", parentOrder);
                        throw new OmsDelegateException(OmsOrderCancelResultEnum.CANCEL_FAIL.getResultID(), "该订单属于电子卡订单且已支付，不能进行作废！", null);
                    }
                    break;
                default:
                    if (realOrderStatus == 10) {
                        cancelOrderCommonmethod(false, true, false, parentOrder, userId, caller, cancelReason, cancelReasonOther);
                    } else if (realOrderStatus == 20) {
                        cancelOrderCommonmethod(true, true, false, parentOrder, userId, caller, cancelReason, cancelReasonOther);
                    } else {
                        cancelOrderCommonmethod(true, true, returnFee, parentOrder, userId, caller, cancelReason, cancelReasonOther);
                    }
            }	
        	
        }
        
        
        
    }
    
    // 是否能进行作废
    private boolean canCancelParentOrder(Long orderStatus) {
        boolean result = false;
        // 如果订单已经拆单了，则订单作废失败
        if (orderStatus >= 0 && orderStatus <= 120 && orderStatus != 110) {// 拆单之前状态，或者拆单失败
            result = true;
        }
        return result;
    }
    
    // 更新订单状态为作废
    private int cancelOrder(TOsParentOrderPO parentOrder, Long caller) {
        List<Object[]> parentChangeList = new ArrayList<Object[]>();
        parentChangeList.add(new Object[]{"ORDER_STATUS", caller});
        parentChangeList.add(new Object[]{"SYSTEM_REMARK", (caller == -1?"系统":caller == -2?"管理员":"用户") + "作废订单！"});
        List<Object[]> parentWhereList = new ArrayList<Object[]>();
        parentWhereList.add(new Object[]{"ORDER_CODE", "=", parentOrder.getOrderCode()});
        parentWhereList.add(new Object[]{"ORDER_STATUS", "=", parentOrder.getOrderStatus()});
        Log.info(logger, uuid, "更新父订单状态为作废", "parentChangeList", StringUtil.transferObjectList(parentChangeList), "parentWhereList", StringUtil.transferObjectList(parentWhereList));
        return tOsParentOrderDao.updateTOsParentOrderPOByCondition(parentChangeList, parentWhereList);
    }
    
    // 增加订单操作日志
    private int addOrderOpt(TOsParentOrderPO order, Long userId, Long caller, Long cancelReason, String cancelReasonOther) {
        TOsParentOrderOptPO optVo = new TOsParentOrderOptPO();
        optVo.setOptDt(new Timestamp(System.currentTimeMillis()));
        optVo.setOrderId(order.getOrderId());
        optVo.setUserId(userId);
        optVo.setOrderStatus(caller);// 作废状态值
        if (caller == -1) {
            optVo.setRemark("您的订单因超时未支付，已被系统取消，详情请咨询400-898-9797");
            if (ParentOrderStatusEnum.PAY_FAILED.getStatus() == order.getOrderStatus()) {
                optVo.setRemark("您的订单因支付失败，已被系统取消，详情请咨询400-898-9797");
            }
        } else {
            optVo.setRemark("您的订单已被取消，详情请咨询400-898-9797");
        }
        optVo.setIsShow("1");
        optVo.setCancelType(caller);
        optVo.setCancelReason(cancelReason);
        if(!StringUtil.isEmpty(cancelReasonOther)){
            optVo.setCancelReasonOther(cancelReasonOther);
        }
        LogUtils4Oms.info(logger, uuid, "增加订单操作日志", "optVo", optVo);
        return tOsParentOrderOptDao.addTOsParentOrderOptPO(optVo);
    }
    
    /**
     * 恢复用户楼宇活动资格
     * @param activieID
     * @param buyerID
     */
    public int updateBuildingQualification(Long activieID,Long buyerID){
    	List<Object[]> buildingCondition = new ArrayList<Object[]>();
    	buildingCondition.add(new Object[]{"IS_USED", "0"});
    	
    	List<Object[]> whereCondition = new ArrayList<Object[]>();
    	whereCondition.add(new Object[]{"ACTIVITY_ID","=", activieID});
    	whereCondition.add(new Object[]{"BUYER_ID","=",buyerID});
    	whereCondition.add(new Object[]{"IS_USED","=",1});
    	LogUtils4Oms.info(logger, uuid, "恢复用户楼宇活动资格！");
    	return  buildingQualificationDao.updateBuildingQualificationPOByCondition(buildingCondition, whereCondition);
    }
    
    public Set<String> haveUsedVocher(Long parentOrderId){
    	Set<String> set = new HashSet<String>();
    	List<Object[]> parentItemCondition = new ArrayList<Object[]>();
    	parentItemCondition.add(new Object[]{"ORDER_ID","=", parentOrderId});
    	List<TOsParentOrderItemsPO> parentOrderList = tOsParentOrderItemsDao.findTOsParentOrderItemsPOListByCondition(parentItemCondition);
    	for (TOsParentOrderItemsPO item : parentOrderList) {
			if(item.getExCouponSn()!=null && item.getExCouponSn().length()>0){
				set.add(item.getExCouponSn());
			}
		}
    	return set;
    }
    
    public boolean updateVoucher(Long buyerID,Set<String> vocherSet){
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
    
    public boolean userHavedQualification(TOsParentOrderPO order){
    	List<Object[]> qualifyCondition = new ArrayList<Object[]>();
    	qualifyCondition.add(new Object[]{"ACTIVITY_ID","=", order.getActiveId()});
    	qualifyCondition.add(new Object[]{"BUYER_ID","=",order.getBuyerId()});
    	List<BuildingQualificationPO> qualificationPOList =  buildingQualificationDao.findBuildingQualificationPOListByCondition(qualifyCondition);
    	if(null==qualificationPOList || qualificationPOList.size()<=0){
    		return false;
    	}
    	return true;
    }
    
    /**
     * 作废订单时公用方法
     * Description:<br>
     * 
     * @param returnCanSaleNum：是否退库存
     * @param returnShip：是否退运力
     * @param refundFee：是否退款
     * @param parentOrder
     * @param userId
     * @param caller
     * @throws OmsDelegateException
     * @throws Exception
     */
    private void cancelOrderCommonmethod(boolean returnCanSaleNum, boolean returnShip, boolean refundFee, TOsParentOrderPO parentOrder, Long userId, Long caller, Long cancelReason, String cancelReasonOther) throws OmsDelegateException, Exception {
        
        if(parentOrder.getOrderStatus() != -4L){
            // 作废订单
            int resultNum = cancelOrder(parentOrder, caller);
            if (resultNum != 1) {
                LogUtils4Oms.info(logger, uuid, "更新订单状态为作废失败", "resultNum", resultNum, "parentOrder", parentOrder);
                throw new OmsDelegateException(OmsOrderCancelResultEnum.CANCEL_FAIL.getResultID(), "更新订单状态为作废失败！", null);
            }
            // 添加日志
            resultNum = addOrderOpt(parentOrder, userId, caller, cancelReason, cancelReasonOther);
            if (resultNum != 1) {
                LogUtils4Oms.info(logger, uuid, "增加订单操作日志失败", "resultNum", resultNum, "parentOrder", parentOrder);
                throw new OmsDelegateException(OmsOrderCancelResultEnum.CANCEL_FAIL.getResultID(), "增加订单操作日志失败！", null);
            }
        }
        
        List<TOsParentOrderItemsPO> parentOrderItemsList = tOsParentOrderItemsDao.findTOsParentOrderItemsPOListBySql("ORDER_ID="
                        + parentOrder.getOrderId());
        if (parentOrderItemsList == null || parentOrderItemsList.size() <= 0) {
            LogUtils4Oms.info(logger, uuid, "父订单item没有明细信息！", "ORDER_ID", parentOrder.getOrderId());
            throw new OmsDelegateException(OmsOrderCancelResultEnum.CANCEL_FAIL.getResultID(), "父订单没有商品明细信息！", null);
        }
        
        // 恢复用户参加楼宇资格
        if(null != parentOrder.getActiveId() && "1".equals(parentOrder.getActiveType())){
        	LogUtils4Oms.info(logger, uuid, "归还用户楼宇活动参与资格开始！");
        	//判断是否有资格，没有资格直接略过,恢复资格步骤，进行退库存、退运力
        	if(userHavedQualification(parentOrder)){
        		LogUtils4Oms.info(logger, uuid, "用户曾获得过资格！");
        		int resultNum  = updateBuildingQualification(parentOrder.getActiveId(),parentOrder.getBuyerId());
            	if(resultNum != 1){
            		LogUtils4Oms.info(logger, uuid, "恢复楼宇活动用户的参与资格失败", "resultNum", resultNum, "orderID", parentOrder.getOrderId());
            		throw new OmsDelegateException(OmsOrderCancelResultEnum.CANCEL_FAIL.getResultID(), "恢复楼宇活动用户的参与资格失败！", null);
            	}
            	LogUtils4Oms.info(logger, uuid, "归还用户楼宇活动参与资格成功！");
        	}else{
        		LogUtils4Oms.info(logger, uuid, "未查找到用户曾获得过资格！");
        	}
        	
        }
        
        //恢复兑换券资格
        LogUtils4Oms.info(logger, uuid,"恢复用户兑换券资格开始！");
        Set<String> vocherSet = haveUsedVocher(parentOrder.getOrderId());
        if(vocherSet!=null && vocherSet.size()>0){
        	if(!updateVoucher(parentOrder.getBuyerId(),vocherSet)){
        		LogUtils4Oms.info(logger,uuid, "恢复用户兑换券失败！");
        		throw new OmsDelegateException(OmsOrderCancelResultEnum.CANCEL_FAIL.getResultID(), "恢复用户兑换券失败！", null);
            }
        	LogUtils4Oms.info(logger,uuid, "恢复用户兑换券成功！");
        }else{
        	Log.info(logger, uuid,"用户未使用兑换券，不需要恢复兑换券资格！");
        }
        
        this.updateFindChannelNum(parentOrder, parentOrderItemsList);
        
        /********************用户首单标识*********************/
        if("1".equals(parentOrder.getIsFirst())){
            int firstNum = BuyerFirstOrderUtil.deleteFirstOrder(parentOrder.getBuyerId(), parentOrder.getOrderCode(), logger, uuid, new BuyerFirstOrderDao(uuid, logger));
            if(firstNum != 1){
                Log.info(logger, uuid, "逻辑删除用户首单关系数据失败", "firstNum", firstNum);
            }
        }
        /********************用户首单标识*********************/
        
        if (refundFee) {
            // 退款
            PaymentPCancelInputData paymentPCancelInputData = new PaymentPCancelInputData();
            paymentPCancelInputData.setOrderID(parentOrder.getOrderId());
            paymentPCancelInputData.setOrderCode(parentOrder.getOrderCode());
            paymentPCancelInputData.setUserID(userId);
            if(parentOrder.getOrderStatus() == -4){ //异常拦截订单不退优惠券
                paymentPCancelInputData.setIsRefundCoupon(BooleanEnum.FALSE.getV());
            }
            HashMap<String, String> params = (HashMap<String, String>)AuthorizeClient.getVerifyInfo(this.httpRequest);
            Map<String, String> omsCat = (Map<String, String>)params.clone();
            omsCat.put("method", "pCancel");
            String json = null;
            BaseOutputBean baseBean = new BaseOutputBean();
            try {
                json = paymentPCancelInputData.toJson();
                omsCat.put("req_str", json);
                baseBean = TootooService.callServer("payment", omsCat, "post", new PaymentCancelOutputData());
                if (BaseResultEnum.SUCCESS.getResultID() == baseBean.getOutputHead().getResultID().intValue()) {
                    LogUtils4Oms.info(logger, uuid, json, "父订单作废退款成功！", "order", parentOrder);
                } else {
                    LogUtils4Oms.info(logger, uuid, json, baseBean.getOutputHead().getResultMessage(), "退款接口resultId", baseBean.getOutputHead().getResultID());
                    throw new OmsDelegateException(OmsOrderCancelResultEnum.CANCEL_FAIL.getResultID(), "订单作废调用退款接口返回失败结果，"
                                    + baseBean.getOutputHead().getResultMessage(), null);
                }
            } catch (OmsDelegateException e1) {
                LogUtils4Oms.info(logger, uuid, json, "订单作废调用退款接口返回失败结果", "json", json, e1);
                throw e1;
            } catch (Exception e) {
                LogUtils4Oms.info(logger, uuid, json, "父订单作废退款接口调用失败！", "json", json);
                throw e;
            }
        }
        if (returnCanSaleNum) {
            // 库存
            StockModifyLogicStockInputData stockInputData = new StockModifyLogicStockInputData();
            stockInputData.setRequestType("1");
            stockInputData.setRequestorType("0");
            stockInputData.setRequestorCode(parentOrder.getOrderCode());
            StockModifyLogicStockModifyInfoElementI modifyInfo = new StockModifyLogicStockModifyInfoElementI();
            List<StockModifyLogicStockModifyListElementI> modifyList = new ArrayList<StockModifyLogicStockModifyListElementI>();
            
            for (TOsParentOrderItemsPO orderItem : parentOrderItemsList) {
                if ("1".equals(orderItem.getIsGiftbox())) continue;// 礼盒商品直接跳过
                StockModifyLogicStockModifyListElementI stockElement = new StockModifyLogicStockModifyListElementI();
                stockElement.setSubstationID(parentOrder.getSubstationId());
                stockElement.setWarehouseID(orderItem.getWarehouseId());
                stockElement.setGoodsBasicID(orderItem.getBasicId());
                
                // 如果是下架促销，退残品库存
                if(OrderTypeEnum.PULLOFF_SHELVES.getC().equals(parentOrder.getOrderType())){
                    stockElement.setIsDefective("1");
                }else{
                    stockElement.setIsDefective("0");
                }
                
                stockElement.setAmount(orderItem.getGoodsNumber());
                modifyList.add(stockElement);
            }
            modifyInfo.setModifyList(modifyList);
            stockInputData.setModifyInfo(modifyInfo);
            HashMap<String, String> params = (HashMap<String, String>)AuthorizeClient.getVerifyInfo(this.httpRequest);
            Map<String, String> omsCat = (Map<String, String>)params.clone();
            omsCat.put("method", "modifyLogicStock");
            String json = null;
            BaseOutputBean baseBean = new BaseOutputBean();
            try {
                json = stockInputData.toJson();
                omsCat.put("req_str", json);
                baseBean = TootooService.callServer("stock", omsCat, "post", new StockModifyLogicStockOutputData());
                if (BaseResultEnum.SUCCESS.getResultID() == baseBean.getOutputHead().getResultID().intValue()) {
                    LogUtils4Oms.info(logger, uuid, json, "父订单作废退库存成功！", "order", parentOrder);
                } else {
                    LogUtils4Oms.info(logger, uuid, json, baseBean.getOutputHead().getResultMessage(), "库存接口resultId", baseBean.getOutputHead().getResultID());
                    throw new OmsDelegateException(OmsOrderCancelResultEnum.CANCEL_FAIL.getResultID(), "订单作废调用退库存接口返回失败结果！"
                                    + "，"
                                    + baseBean.getOutputHead().getResultMessage(), null);
                }
            } catch (OmsDelegateException e1) {
                LogUtils4Oms.info(logger, uuid, json, "订单作废调用退库存接口返回失败结果！", "json", json, e1);
                throw e1;
            } catch (Exception e) {
                LogUtils4Oms.info(logger, uuid, json, "父订单作废库存接口调用失败！", "json", json);
                throw e;
            }
        }
        if (returnShip) {
            // 运力
            ShippingOccupyShippingPowerInputData shippingOccupyInputData = new ShippingOccupyShippingPowerInputData();
            shippingOccupyInputData.setIsRelease("1");
            shippingOccupyInputData.setIsAllCancel("1");
            shippingOccupyInputData.setOrderCode(parentOrder.getOrderCode());
            // 修改，运力参数
            ShippingOccupyShippingPowerOccupyInfoElementI occupyInfo = new ShippingOccupyShippingPowerOccupyInfoElementI();
            List<ShippingOccupyShippingPowerOccupyListElementI> occupyList = new ArrayList<ShippingOccupyShippingPowerOccupyListElementI>();
            
            for (TOsParentOrderItemsPO orderItem : parentOrderItemsList) {
                ShippingOccupyShippingPowerOccupyListElementI occupy = new ShippingOccupyShippingPowerOccupyListElementI();
                occupy.setWarehouseID(orderItem.getWarehouseId());
                occupy.setShippingCompanyID(orderItem.getDcId());
                boolean hasOccupy = false;
                for (ShippingOccupyShippingPowerOccupyListElementI i : occupyList) {
                    if (i.getShippingCompanyID().compareTo(occupy.getShippingCompanyID()) == 0
                                    && i.getWarehouseID().compareTo(occupy.getWarehouseID()) == 0) {
                        hasOccupy = true;
                    }
                }
                if (!hasOccupy) {
                    occupyList.add(occupy);
                }
            }
            occupyInfo.setOccupyList(occupyList);
            shippingOccupyInputData.setOccupyInfo(occupyInfo);
            
            HashMap<String, String> params = (HashMap<String, String>)AuthorizeClient.getVerifyInfo(this.httpRequest);
            Map<String, String> omsCat = (Map<String, String>)params.clone();
            omsCat.put("method", "occupyShippingPower");
            String json = null;
            BaseOutputBean baseBean = new BaseOutputBean();
            try {
                json = shippingOccupyInputData.toJson();
                omsCat.put("req_str", json);
                baseBean = TootooService.callServer("shipping", omsCat, "post", new ShippingOccupyShippingPowerOutputData());
                if (BaseResultEnum.SUCCESS.getResultID() == baseBean.getOutputHead().getResultID().intValue()) {
                    LogUtils4Oms.info(logger, uuid, json, "父订单作废退运力成功！", "order", parentOrder);
                } else {
                    LogUtils4Oms.info(logger, uuid, json, baseBean.getOutputHead().getResultMessage(), "运力接口resultId", baseBean.getOutputHead().getResultID());
                    throw new OmsDelegateException(OmsOrderCancelResultEnum.CANCEL_FAIL.getResultID(), "订单作废调用退运力接口返回失败结果"
                                    + "，"
                                    + baseBean.getOutputHead().getResultMessage(), null);
                }
            } catch (OmsDelegateException e1) {
                LogUtils4Oms.info(logger, uuid, json, "订单作废调用退运力接口返回失败结果", "json", json, e1);
                throw e1;
            } catch (Exception e) {
                LogUtils4Oms.info(logger, uuid, json, "父订单作废退运力接口调用失败！", "json", json);
                throw e;
            }
        }
    }
    
    /**
     * Description:<br>
     * 发现频道作废减冻结数，减历史购买数
     * 
     * @param parentOrder
     * @param parentOrderItemsList
     */
    public void updateFindChannelNum(TOsParentOrderPO parentOrder, List<TOsParentOrderItemsPO> parentOrderItemsList) {
        try{
            if (ActiveTypeEnum.isFindChannel(parentOrder.getActiveType())) {
                long promotionId = 0L;
                for (TOsParentOrderItemsPO item : parentOrderItemsList) {
                    if (item.getGiftboxId() != 0 || StringUtil.isEmpty(item.getPromoteId()) || "0".equals(item.getPromoteId())) {
                        continue;
                    }
                    promotionId = Long.valueOf(item.getPromoteId());
                }
                
                Log.info(logger, uuid, "发现频道作废减冻结数");
                if (promotionId != 0 && OrderTypeEnum.BAOYOU_TUANGOU_ORDER.getC().equals(parentOrder.getOrderType())) {// 团购订单
                    Log.info(logger, uuid, "组装promotion服务increamentTuangouCount方法所需参数开始");
                    PromotionIncreamentTuangouCountInputData increamentInputData = new PromotionIncreamentTuangouCountInputData();
                    increamentInputData.setScope(parentOrder.getScope());
                    increamentInputData.setPromotionId(promotionId);
                    increamentInputData.setFlag(1L); // 删除
                    for (TOsParentOrderItemsPO item : parentOrderItemsList) {
                        if (item.getGiftboxId() != 0 || StringUtil.isEmpty(item.getPromoteId()) || "0".equals(item.getPromoteId())) {
                            continue;
                        }
                        PromotionIncreamentTuangouCountGoodsInfoElementI increamentElement = new PromotionIncreamentTuangouCountGoodsInfoElementI();
                        increamentElement.setSkuId(item.getSku());
                        increamentElement.setGoodsNum(item.getGoodsNumber().longValue());
                        increamentInputData.getGoodsInfo().add(increamentElement);
                    }
                    Map<String, String> increamentParams = new HashMap<String, String>();
                    increamentParams.put("r", "tIncreamentTuangouCount");
                    increamentParams.put("req_str", increamentInputData.toJson());
                    Log.info(logger, uuid, "调用promotion服务increamentTuangouCount方法开始", "increamentParams", increamentParams);
                    BaseOutputBean promotionOutputBean = TootooService.callServer(logger, uuid, "promotion", increamentParams, "post", new PromotionIncreamentTuangouCountOutputData());
                    Log.info(logger, uuid, "调用promotion服务increamentTuangouCount方法结束", "promotionOutputBean", promotionOutputBean);
                    if (!TootooService.checkService(promotionOutputBean, "promotion", "increamentTuangouCount", parentOrder.getScope())) {
                        Log.error(logger, uuid, "调用promotion服务increamentTuangouCount方法失败", null);
                    }
                }
                
                if (promotionId != 0 && OrderTypeEnum.JIELONG_GROUPBUY.getC().equals(parentOrder.getOrderType())) {// 接龙团购
                    Log.info(logger, uuid, "组装promotion服务updateWeixinjielongBuyHistory方法所需参数开始");
                    PromotionUpdateWeixinjielongBuyHistoryInputData jielongInputData = new PromotionUpdateWeixinjielongBuyHistoryInputData();
                    jielongInputData.setScope(parentOrder.getScope());
                    jielongInputData.setPromotionId(promotionId);
                    jielongInputData.setFlag(1L); // 删除
                    for (TOsParentOrderItemsPO item : parentOrderItemsList) {
                        if (item.getGiftboxId() != 0 || StringUtil.isEmpty(item.getPromoteId()) || "0".equals(item.getPromoteId())) {
                            continue;
                        }
                        PromotionUpdateWeixinjielongBuyHistoryBuyHistoryElementI jielongElement = new PromotionUpdateWeixinjielongBuyHistoryBuyHistoryElementI();
                        jielongElement.setCreateTime(new SimpleDateFormat("yyyy-MM-dd").format(parentOrder.getCreateDt()));
                        jielongElement.setGoodsId(item.getGoodsId() + "");
                        jielongElement.setNickname(null); 
                        jielongElement.setNum(item.getGoodsNumber().toString());
                        jielongInputData.getBuyHistory().add(jielongElement);
                    }
                    Map<String, String> jielongParams = new HashMap<String, String>();
                    jielongParams.put("r", "TUpdateWeixinjielongBuyHistory");
                    jielongParams.put("req_str", jielongInputData.toJson());
                    Log.info(logger, uuid, "调用promotion服务updateWeixinjielongBuyHistory方法开始", "jielongParams", jielongParams);
                    BaseOutputBean promotionOutputBean = TootooService.callServer(logger, uuid, "promotion", jielongParams, "post", new PromotionUpdateWeixinjielongBuyHistoryOutputData());
                    Log.info(logger, uuid, "调用promotion服务updateWeixinjielongBuyHistory方法结束", "promotionOutputBean", promotionOutputBean);
                    if (!TootooService.checkService(promotionOutputBean, "promotion", "updateWeixinjielongBuyHistory", parentOrder.getScope())) {
                        Log.error(logger, uuid, "调用promotion服务updateWeixinjielongBuyHistory方法失败", null);
                    }
                    
                }
            
                
                Log.info(logger, uuid, "发现频道作废减历史购买数（如果有）");
                // 先查询作废的商品是否有存购买数量（设置了历史购买数）
                OmsGetBuyerPromotionNumInputData getBuyerNumI = new OmsGetBuyerPromotionNumInputData();
                getBuyerNumI.setScope(parentOrder.getScope());
                getBuyerNumI.setBuyerId(parentOrder.getBuyerId());
                List<OmsGetBuyerPromotionNumPromotionListElementI> promotionListI = new ArrayList<OmsGetBuyerPromotionNumPromotionListElementI>();
                OmsGetBuyerPromotionNumPromotionListElementI promotionI = new OmsGetBuyerPromotionNumPromotionListElementI();
                promotionI.setPromotionId(promotionId);
                List<OmsGetBuyerPromotionNumGoodsListElementI> getGoodsList = new ArrayList<OmsGetBuyerPromotionNumGoodsListElementI>();
                for (TOsParentOrderItemsPO item : parentOrderItemsList) {
                    if (item.getGiftboxId() != 0 || StringUtil.isEmpty(item.getPromoteId()) || "0".equals(item.getPromoteId())) {
                        continue;
                    }
                    OmsGetBuyerPromotionNumGoodsListElementI goodsId = new OmsGetBuyerPromotionNumGoodsListElementI();
                    goodsId.setGoodsId(item.getGoodsId());
                    getGoodsList.add(goodsId);
                }
                promotionI.setGoodsList(getGoodsList);
                promotionListI.add(promotionI);
                getBuyerNumI.setPromotionList(promotionListI);
                HashMap<String, String> params = (HashMap<String, String>)AuthorizeClient.getVerifyInfo(this.httpRequest);
                HashMap<String, String> getBuyerPromotionNumServiceParams = (HashMap<String, String>)params.clone();
                getBuyerPromotionNumServiceParams.put("method", "getBuyerPromotionNum");
                getBuyerPromotionNumServiceParams.put("req_str", getBuyerNumI.toJson());
                Log.info(logger, uuid, "调用oms服务getBuyerPromotionNum方法开始", "getBuyerPromotionNumServiceParams", getBuyerPromotionNumServiceParams);
                BaseOutputBean getBuyerNumOutputBean = TootooService.callServer("oms", getBuyerPromotionNumServiceParams, "post", new OmsGetBuyerPromotionNumOutputData());
                Log.info(logger, uuid, "调用oms服务getBuyerPromotionNum方法结束", "getBuyerNumOutputBean", getBuyerNumOutputBean);
                if (!TootooService.checkService(getBuyerNumOutputBean, "oms", "getBuyerPromotionNum", parentOrder.getScope())) {
                    Log.error(logger, uuid, "调用oms服务getBuyerPromotionNumServiceParams方法失败", null);
                    return;
                }
                
                
                // 作废减历史购买数（如果有）
                Set<Long> goodIdSet = new HashSet<Long>();
                OmsGetBuyerPromotionNumOutputData getBuyerNumOutputData = (OmsGetBuyerPromotionNumOutputData)getBuyerNumOutputBean.getOutputData();
                if (getBuyerNumOutputData.getPromotionList().size() >= 1 && getBuyerNumOutputData.getPromotionList().get(0).getGoodsList() != null){
                    for (OmsGetBuyerPromotionNumGoodsListElementO goodsIdElementO : getBuyerNumOutputData.getPromotionList().get(0).getGoodsList()) {
                        goodIdSet.add(goodsIdElementO.getGoodsId());
                    }
                }
                if(goodIdSet.size()<=0){
                    Log.info(logger, uuid, "没有设置了用户最大购买数的商品");
                    return;
                }
                OmsSetBuyerPromotionNumInputData setBuyerNumInputData = new OmsSetBuyerPromotionNumInputData();
                setBuyerNumInputData.setScope(parentOrder.getScope());
                setBuyerNumInputData.setBuyerId(parentOrder.getBuyerId());
                setBuyerNumInputData.setPromotionId(promotionId);
                List<OmsSetBuyerPromotionNumGoodsListElementI> setGoodsList = new ArrayList<OmsSetBuyerPromotionNumGoodsListElementI>();
                for (TOsParentOrderItemsPO item : parentOrderItemsList) {
                    if (!goodIdSet.contains(item.getGoodsId())) {
                        continue;
                    }
                    OmsSetBuyerPromotionNumGoodsListElementI goodsId = new OmsSetBuyerPromotionNumGoodsListElementI();
                    goodsId.setGoodsId(item.getGoodsId());
                    goodsId.setBuyerNum(item.getGoodsNumber().negate().longValue());
                    setGoodsList.add(goodsId);
                }
                setBuyerNumInputData.setGoodsList(setGoodsList);
                HashMap<String, String> setBuyerNumServiceParams = (HashMap<String, String>)params.clone();
                setBuyerNumServiceParams.put("method", "setBuyerPromotionNum");
                setBuyerNumServiceParams.put("req_str", setBuyerNumInputData.toJson());
                Log.info(logger, uuid, "调用oms服务setBuyerPromotionNum方法开始", "setBuyerNumServiceParams", setBuyerNumServiceParams);
                BaseOutputBean setBuyerNumOutputBean = TootooService.callServer("oms", setBuyerNumServiceParams, "post", new OmsSetBuyerPromotionNumOutputData());
                Log.info(logger, uuid, "调用oms服务setBuyerPromotionNum方法结束", "setBuyerNumOutputBean", setBuyerNumOutputBean);
                
                if (!TootooService.checkService(setBuyerNumOutputBean, "oms", "setBuyerPromotionNum", parentOrder.getScope())) {
                    Log.error(logger, uuid, "调用oms服务setBuyerPromotionNum方法失败", null);
                }
            }
        
        } catch (RuntimeException r) {
            Log.error(logger, uuid, "运行时异常：+'方法名updateCache:'", r);
        } catch (Exception e) {
            Log.error(logger, uuid, "团购订单更新缓存失败:+'方法名updateCache:'", e);
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
    public void doClose(final BaseInputHead inputHead, final OmsOrderCancelInputData inputData, final BaseOutputHead outputHead, final OmsOrderCancelOutputData outputData) throws OmsDelegateException {
        
    }
}
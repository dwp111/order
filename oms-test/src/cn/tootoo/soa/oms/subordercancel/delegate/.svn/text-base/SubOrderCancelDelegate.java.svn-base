package cn.tootoo.soa.oms.subordercancel.delegate;

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
import cn.tootoo.db.egrocery.osorderitems.OsOrderItemsPO;
import cn.tootoo.db.egrocery.osorderopt.OsOrderOptPO;
import cn.tootoo.db.egrocery.tbindorder.TBindOrderPO;
import cn.tootoo.enums.BaseResultEnum;
import cn.tootoo.enums.BooleanEnum;
import cn.tootoo.global.Global;
import cn.tootoo.http.AuthorizeClient;
import cn.tootoo.http.TootooService;
import cn.tootoo.http.bean.BaseInputHead;
import cn.tootoo.http.bean.BaseOutputBean;
import cn.tootoo.http.bean.BaseOutputHead;
import cn.tootoo.soa.base.enums.ActiveTypeEnum;
import cn.tootoo.soa.base.enums.OrderStatusEnum;
import cn.tootoo.soa.base.enums.OrderTypeEnum;
import cn.tootoo.soa.base.util.BuyerFirstOrderUtil;
import cn.tootoo.soa.base.util.LockUtil;
import cn.tootoo.soa.oms.exceptions.OmsDelegateException;
import cn.tootoo.soa.oms.getbuyerpromotionnum.input.OmsGetBuyerPromotionNumGoodsListElementI;
import cn.tootoo.soa.oms.getbuyerpromotionnum.input.OmsGetBuyerPromotionNumInputData;
import cn.tootoo.soa.oms.getbuyerpromotionnum.input.OmsGetBuyerPromotionNumPromotionListElementI;
import cn.tootoo.soa.oms.getbuyerpromotionnum.output.OmsGetBuyerPromotionNumGoodsListElementO;
import cn.tootoo.soa.oms.getbuyerpromotionnum.output.OmsGetBuyerPromotionNumOutputData;
import cn.tootoo.soa.oms.ordercancel.output.OmsOrderCancelResultEnum;
import cn.tootoo.soa.oms.setbuyerpromotionnum.input.OmsSetBuyerPromotionNumGoodsListElementI;
import cn.tootoo.soa.oms.setbuyerpromotionnum.input.OmsSetBuyerPromotionNumInputData;
import cn.tootoo.soa.oms.setbuyerpromotionnum.output.OmsSetBuyerPromotionNumOutputData;
import cn.tootoo.soa.oms.subordercancel.input.OmsSubOrderCancelInputData;
import cn.tootoo.soa.oms.subordercancel.output.OmsSubOrderCancelOutputData;
import cn.tootoo.soa.oms.subordercancel.output.OmsSubOrderCancelResultEnum;
import cn.tootoo.soa.oms.utils.LogUtils4Oms;
import cn.tootoo.soa.payment.cancel.input.PaymentCancelInputData;
import cn.tootoo.soa.payment.cancel.input.PaymentCancelOrderInfoListElementI;
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
 * 接口description：子订单取消。
 * 接口remark：
 */
public final class SubOrderCancelDelegate extends AbstractSubOrderCancelDelegate implements Cloneable {
    
    static {
        SubOrderCancelDelegateFactory.registPrototype(new SubOrderCancelDelegate());
    }
    
    /**
     * 克隆方法。
     */
    @Override
    public AbstractSubOrderCancelDelegate clone() throws CloneNotSupportedException {
        return new SubOrderCancelDelegate();
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
    public void doInit(final BaseInputHead inputHead, final OmsSubOrderCancelInputData inputData, final BaseOutputHead outputHead, final OmsSubOrderCancelOutputData outputData) throws OmsDelegateException {}
    
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
    public boolean doCheck(final BaseInputHead inputHead, final OmsSubOrderCancelInputData inputData, final BaseOutputHead outputHead, final OmsSubOrderCancelOutputData outputData) {
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
    public void doExecute(final BaseInputHead inputHead, final OmsSubOrderCancelInputData inputData, final BaseOutputHead outputHead, final OmsSubOrderCancelOutputData outputData) throws OmsDelegateException {
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
            throw new OmsDelegateException(OmsSubOrderCancelResultEnum.CANCEL_FAIL.getResultID(), "提交作废orderId为空", null);
        }
        
        OsOrderPO orderPo = osOrderDao.findOsOrderPOByID(orderId, false, false, false, true);
        if (orderPo == null) {
            LogUtils4Oms.info(logger, uuid, "订单不存在！", "orderId", orderId);
            throw new OmsDelegateException(OmsSubOrderCancelResultEnum.CANCEL_FAIL.getResultID(), "订单不存在！", null);
        }
        
        try {
            boolean lock = LockUtil.lock(logger, uuid, orderPo.getOrderCode(), 1, "子订单作废");
            if (!lock) {
                LogUtils4Oms.info(logger, uuid, "加锁失败，不能同时取消一个子订单！", "orderPo", orderPo);
            }
        } catch (Exception e1) {
            LogUtils4Oms.error(logger, uuid, "子订单作废加锁失败", e1, "orderCode", orderPo.getOrderCode());
        }
        
        try {
            // 子订单作废
            cancelSubOrder(orderPo, userId, caller, isRefund, cancelReason, cancelReasonOther, outputHead);
            LogUtils4Oms.info(logger, uuid, "子订单取消成功！", "orderId", orderId);
            Global.getOutputHead(BaseResultEnum.SUCCESS.getResultID(), null, outputHead);
            // 提交
            osOrderDao.commit();
        } catch (OmsDelegateException e) {
            LogUtils4Oms.info(logger, uuid, "子订单作废失败", e);
            Global.getOutputHead(e.getResultID(), null, outputHead);
            // 回滚
            osOrderDao.rollback();
        } catch (Exception e) {
            LogUtils4Oms.info(logger, uuid, "提交作废失败!", e);
            Global.getOutputHead(OmsSubOrderCancelResultEnum.CANCEL_FAIL.getResultID(), null, outputHead);
            // 回滚
            osOrderDao.rollback();
            throw new OmsDelegateException(OmsSubOrderCancelResultEnum.CANCEL_FAIL.getResultID(), "提交作废失败!", e);
        } finally {
            try {
                LogUtils4Oms.info(logger, uuid, "解锁", "orderCode", orderPo.getOrderCode());
                LockUtil.unlock(logger, uuid, orderPo.getOrderCode());
            } catch (Exception e) {
                LogUtils4Oms.error(logger, uuid, "解锁出错", e, "orderCode", orderPo.getOrderCode());
            }
        }
    }
    
    // 子订单作废
    private void cancelSubOrder(OsOrderPO orderPo, Long userId, Long caller, Long isRefund, Long cancelReason, String cancelReasonOther, final BaseOutputHead outputHead) throws OmsDelegateException, Exception {
        if (OrderStatusEnum.CANCEL == OrderStatusEnum.get(orderPo.getOrderStatus())) {// 如果订单作废状态，直接返回
            LogUtils4Oms.info(logger, uuid, "需要作废的订单已经是作废状态！", "orderCode", orderPo.getOrderCode());
            Global.getOutputHead(BaseResultEnum.SUCCESS.getResultID(), null, outputHead);
            return;
        }
        
        
        if ("x".equals(orderPo.getOrderStatus()) && isRefund != 0L && isRefund != 1L) {
            LogUtils4Oms.info(logger, uuid, "异常订单作废，【是否退款】参数错误！", "orderCode", orderPo.getOrderCode(), "isRefund", isRefund);
            throw new OmsDelegateException(BaseResultEnum.REQUEST_PARAM_ERROR.getResultID(), "异常订单作废，【是否退款】参数错误！", null);
        } else if ((!"x".equals(orderPo.getOrderStatus())) && isRefund != null) {
            LogUtils4Oms.info(logger, uuid, "普通订单作废，【是否退款】参数必须为空！", "orderCode", orderPo.getOrderCode(), "isRefund", isRefund);
            throw new OmsDelegateException(BaseResultEnum.REQUEST_PARAM_ERROR.getResultID(), "普通订单作废，【是否退款】参数必须为空！", null);
        }
        
        
        
        // "5", "实物提货订单";"6", "虚拟提货订单" 所有兄弟提货子订单全部作废
        if (OrderTypeEnum.TIHUO_ORDER == OrderTypeEnum.get(orderPo.getOrderType())
                        || OrderTypeEnum.VIRTUAL_TIHUO_ORDER == OrderTypeEnum.get(orderPo.getOrderType())) {
            cancelTakingOrder(orderPo, userId, caller, isRefund, cancelReason, cancelReasonOther);
        }else if(OrderTypeEnum.GIFT_CARD_ORDER == OrderTypeEnum.get(orderPo.getOrderType())){
            // 卡订单不退库存
            cancelGiftCardOrder(orderPo, userId, caller, isRefund, cancelReason, cancelReasonOther);
        }else if(OrderTypeEnum.ONLINE_CARD_VIRTUAL == OrderTypeEnum.get(orderPo.getOrderType())){ 
            // 电子卡订单不允许作废
            LogUtils4Oms.info(logger, uuid, "电子卡订单不允许作废！", "orderPo", orderPo);
            throw new OmsDelegateException(OmsSubOrderCancelResultEnum.CANCEL_FAIL.getResultID(), "电子卡订单不允许作废！", null);
        }/*else if ("1".equals(orderPo.getIsContainGift())) {
            // 子订单包含礼盒商品
            cancelGiftOrder(orderPo, userId, caller, isRefund, cancelReason, cancelReasonOther);
        }*/else {
            // 常规订单作废
            cancelCommonOrder(orderPo, userId, caller, isRefund, cancelReason, cancelReasonOther);
        }
    }
    
    // *********************提货订单***********************
    
    // 提货订单作废 并且作废所有兄弟提货订单
    private void cancelTakingOrder(OsOrderPO orderPo, Long userId, Long caller, Long isRefund, Long cancelReason, String cancelReasonOther) throws OmsDelegateException, Exception {
        
        // 查找出父订单下的所有子订单（兄弟订单）
        List<OsOrderPO> orderList = osOrderDao.findOsOrderPOListBySql(" parent_code = '" + orderPo.getParentCode() + "' and ORDER_STATUS <> 'Z'", false, false, true);
        if (orderList == null) {
            LogUtils4Oms.info(logger, uuid, "订单根据订单parentCode查找异常不存在！", "parentCode", orderPo.getParentCode());
            throw new OmsDelegateException(OmsSubOrderCancelResultEnum.CANCEL_FAIL.getResultID(), "订单根据订单parentCode查找异常不存在！", null);
        }
        for (OsOrderPO order : orderList) {
            // 订单状态分别为已审核、配货中、作废中 才可以作废
            if (!OrderStatusEnum.canBeCanceled(OrderStatusEnum.get(order.getOrderStatus()))) {
                LogUtils4Oms.info(logger, uuid, "兄弟订单状态非法，只有“已审核”或“配货中”的订单才能作废！", "orderCode", order.getOrderCode(), "orderStatus", order.getOrderStatus());
                throw new OmsDelegateException(OmsSubOrderCancelResultEnum.CANCEL_FAIL.getResultID(), "订单状态非法，只有“已审核”或“配货中”的订单才能作废！", null);
            }
        }
        // 作废全部子订单
        // cancelSubTakingOrder(orderList, userId, caller, 1L, cancelReason, cancelReasonOther);
        // 没有库存，需归还运力
        cancelOrderCommonmethod(false, true, true, orderList, userId, caller, cancelReason, cancelReasonOther);
    }
    
    // 作废提货子订单
    /*private void cancelSubTakingOrder(List<OsOrderPO> orderList, Long userId, Long caller, Long isRefund, Long cancelReason, String cancelReasonOther) throws OmsDelegateException, Exception {
        // 没有库存，需归还运力
        cancelOrderCommonmethod(false, true, orderList, userId, caller, isRefund, cancelReason, cancelReasonOther);
    }*/
    
    // 作废售卡子订单
    private void cancelGiftCardOrder(OsOrderPO orderPo, Long userId, Long caller, Long isRefund, Long cancelReason, String cancelReasonOther) throws OmsDelegateException, Exception {
        // 异常拦截订单
        String realOrderStatus = orderPo.getOrderStatus();
        
        boolean returnFee = true;
        if("x".equals(realOrderStatus)){
            if(isRefund == 0L){
                returnFee = false; // 异常拦截订单，是否退款
            }
            List<Object[]> bindOrderConditions = new ArrayList<Object[]>();
            bindOrderConditions.add(new Object[]{"STATUS", "=", "0"});
            bindOrderConditions.add(new Object[]{"IS_PARENT", "=", "0"});
            bindOrderConditions.add(new Object[]{"ORDER_CODE", "=", orderPo.getOrderCode()});
            List<TBindOrderPO> bindOrderList = tBindOrderDao.findTBindOrderPOListByCondition(bindOrderConditions, false, false, true);
            if (bindOrderList == null || bindOrderList.size() != 1){
                LogUtils4Oms.info(logger, uuid, "异常订单查询历史状态错误!", "orderCode", orderPo.getOrderCode(), "bindOrderList", bindOrderList);
                throw new OmsDelegateException(OmsSubOrderCancelResultEnum.CANCEL_FAIL.getResultID(), "异常订单查询历史状态错误!", null);
            }
            LogUtils4Oms.info(logger, uuid, "订单拦截表信息", "tBindOrderPO", bindOrderList.get(0));
            realOrderStatus = bindOrderList.get(0).getOrderStatus();
        }
        
        // 订单状态分别为已审核、配货中、作废中 才可以作废
        if (!OrderStatusEnum.canBeCanceled(OrderStatusEnum.get(realOrderStatus))) {
            LogUtils4Oms.info(logger, uuid, "订单状态非法，只有“已审核”或“配货中”的订单才能作废！", "orderCode", orderPo.getOrderCode(), "orderStatus", realOrderStatus);
            throw new OmsDelegateException(OmsSubOrderCancelResultEnum.CANCEL_FAIL.getResultID(), "订单状态非法，只有“已审核”或“配货中”的订单才能作废！", null);
        }
        
        // 没有库存，需归还运力
        List<OsOrderPO> orderList = new ArrayList<OsOrderPO>();
        orderList.add(orderPo);
        cancelOrderCommonmethod(false, true, returnFee, orderList, userId, caller, cancelReason, cancelReasonOther);
    }
    
    // ***********************************************************
    // **************************** 礼盒商品 ********************
    // 作废含礼盒商品的订单
    /*private void cancelGiftOrder(OsOrderPO order, Long userId, Long caller, Long isRefund, Long cancelReason, String cancelReasonOther) throws OmsDelegateException, Exception {
        // 包含礼盒商品关联的全部子订单
        List<OsOrderPO> orderList = getOrderGiftBoxList(order);
        if (orderList == null) {
            LogUtils4Oms.info(logger, uuid, "需要作废的订单不包含礼盒订单！", "order", order);
            throw new OmsDelegateException(OmsSubOrderCancelResultEnum.CANCEL_FAIL.getResultID(), "需要作废的订单不包含礼盒订单！", null);
        }
        for (OsOrderPO orderVO : orderList) {
            // 异常拦截订单
            String realOrderStatus = orderVO.getOrderStatus();
            if("x".equals(realOrderStatus)){
                List<Object[]> bindOrderConditions = new ArrayList<Object[]>();
                bindOrderConditions.add(new Object[]{"STATUS", "=", "0"});
                bindOrderConditions.add(new Object[]{"IS_PARENT", "=", "0"});
                bindOrderConditions.add(new Object[]{"ORDER_CODE", "=", orderVO.getOrderCode()});
                List<TBindOrderPO> bindOrderList = tBindOrderDao.findTBindOrderPOListByCondition(bindOrderConditions, false, false, true);
                if (bindOrderList == null || bindOrderList.size() != 1){
                    LogUtils4Oms.info(logger, uuid, "异常订单查询历史状态错误!", "orderCode", orderVO.getOrderCode(), "bindOrderList", bindOrderList);
                    throw new OmsDelegateException(OmsSubOrderCancelResultEnum.CANCEL_FAIL.getResultID(), "异常订单查询历史状态错误!", null);
                }
                LogUtils4Oms.info(logger, uuid, "订单拦截表信息", "tBindOrderPO", bindOrderList.get(0));
                realOrderStatus = bindOrderList.get(0).getOrderStatus();
            }
            if (!OrderStatusEnum.canBeCanceled(OrderStatusEnum.get(realOrderStatus))) {
                LogUtils4Oms.info(logger, uuid, "兄弟订单状态非法，只有“已审核”或“配货中”的订单才能作废！", "orderCode", orderVO.getOrderCode(), "orderStatus", realOrderStatus);
                throw new OmsDelegateException(OmsSubOrderCancelResultEnum.CANCEL_FAIL.getResultID(), "订单状态非法，只有“已审核”或“配货中”的订单才能作废！", null);
            }
        }
        switch (OrderTypeEnum.get(order.getOrderType())) {
            case JIELONG_GROUPBUY:
            case BAOYOU_TUANGOU_ORDER:
                cancelTGBYOrder(orderList, userId, caller, isRefund, cancelReason, cancelReasonOther);
                break;
            default:
                cancelCommonGiftOrder(orderList, userId, caller, isRefund, cancelReason, cancelReasonOther);
        }
    }*/
    
    // 得到包含礼盒商品关联的全部子订单
    /*private List<OsOrderPO> getOrderGiftBoxList(OsOrderPO order) {
        
        // 父订单的全部礼盒
        List<OsOrderItemsPO> orderItemPOs = osOrderItemsDao.findOsOrderItemsPOListBySql("order_id IN (SELECT oo.order_id FROM os_order oo INNER JOIN t_os_parent_order op ON op.order_id=oo.parent_id and op.order_id="
                        + order.getParentId() + ")");
        // 礼盒关联的子订单
        List<Long> orders = new ArrayList<Long>();
        
        // 子订单与礼盒的对应关系
        Map<Long, Set<Long>> orderGiftBoxMap = new HashMap<Long, Set<Long>>();
        // 礼盒与子订单的对应关系
        Map<Long, Set<Long>> giftBoxOrderMap = new HashMap<Long, Set<Long>>();
        // 礼盒名称Map
        Map<Long, String> giftBoxNameMap = new HashMap<Long, String>();
        Set<Long> tempSet;
        
        for (OsOrderItemsPO temp : orderItemPOs) {
            tempSet = orderGiftBoxMap.get(temp.getOrderId());
            if (null == tempSet) tempSet = new HashSet<Long>();
            tempSet.add(temp.getGoodsId());
            orderGiftBoxMap.put(temp.getOrderId(), tempSet);
            
            tempSet = giftBoxOrderMap.get(temp.getGoodsId());
            if (null == tempSet) tempSet = new HashSet<Long>();
            tempSet.add(temp.getOrderId());
            giftBoxOrderMap.put(temp.getGoodsId(), tempSet);
            
            giftBoxNameMap.put(temp.getGoodsId(), temp.getGoodsName());
        }
        
        // 得到该子订单的所有礼盒集合
        Set<Long> childOrderContainsAllGiftBoxIdList = orderGiftBoxMap.get(order.getOrderId());
        // 不包含礼盒
        if (childOrderContainsAllGiftBoxIdList != null) {
            Object[] arr = rec(order.getOrderId(), childOrderContainsAllGiftBoxIdList, giftBoxOrderMap, orderGiftBoxMap, null);
            orders.addAll((List<Long>)arr[1]);
        }
        orders.add(order.getOrderId());// 礼盒关联的子订单包含自己
        StringBuffer orderIds = new StringBuffer();
        for (int i = 0; i < orders.size(); i++) {
            orderIds.append(orders.get(i));
            if (i < orders.size() - 1) {
                orderIds.append(",");
            }
        }
        return osOrderDao.findOsOrderPOListBySql("order_id in (" + orderIds
                        + ")");
    }*/
    
    /**
     * @param currChildOrderId 当前子订单ID
     * @param childOrderContainsAllGiftBoxIdList 当前子订单的所有礼盒集合
     * @param giftBoxOrderMap 礼盒与订单的对应关系
     * @param orderGiftBoxMap 订单与礼盒的对应关系
     * @param excludeGiftBoxList 已经被找的礼盒，当一个礼盒已经被找，在另一张子订单中又出现，则不再被找
     *            这样就不会出现死循环
     * @return
     *         [0]：boolean类型，一个礼盒的所有商品是否只在当前订单里；
     *         [1]：List<Integer>类型，关联的子订单ID集合；
     *         [2]：List<Integer>类型，关联的礼盒ID集合；
     */
    /*private Object[] rec(Long currChildOrderId, Set<Long> childOrderContainsAllGiftBoxIdList, Map<Long, Set<Long>> giftBoxOrderMap, Map<Long, Set<Long>> orderGiftBoxMap, Set<Long> excludeGiftBoxList) {
        Object[] ret = new Object[]{true, new ArrayList<Long>(), new ArrayList<Long>()};
        Object[] tempRet;
        List tempList;
        if (null == excludeGiftBoxList) excludeGiftBoxList = new HashSet<Long>();
        
        // 一个礼盒对应的所有订单集合
        Set<Long> currOrderIdSet;
        for (Long goodsId : childOrderContainsAllGiftBoxIdList) {
            if (excludeGiftBoxList.contains(goodsId)) continue;
            
            currOrderIdSet = giftBoxOrderMap.get(goodsId);
            
            // 如果一个礼盒只包含在当前子订单中
            if (1 == currOrderIdSet.size()) continue;
            
            ((List)ret[2]).add(goodsId);
            
            // 如果一个礼盒存在于多个子订单中，则该子订单不能做整体操作
            ret[0] = false;
            excludeGiftBoxList.add(goodsId);
            for (Long currOrderId : currOrderIdSet) {
                // 将当前子订单去掉
                if (currChildOrderId.equals(currOrderId)) continue;
                ((List)ret[1]).add(currOrderId);
                
                tempRet = rec(currOrderId, orderGiftBoxMap.get(currOrderId), giftBoxOrderMap, orderGiftBoxMap, excludeGiftBoxList);
                
                tempList = (List)tempRet[1];
                if (0 != tempList.size()) ((List)ret[1]).addAll(tempList);
                tempList = (List)tempRet[2];
                if (0 != tempList.size()) ((List)ret[2]).addAll(tempList);
            }
        }
        
        return ret;
    }*/
    
    /*private void cancelCommonGiftOrder(List<OsOrderPO> list, Long userId, Long caller, Long isRefund, Long cancelReason, String cancelReasonOther) throws OmsDelegateException, Exception {
        cancelDefaultOrder(list, userId, caller, isRefund, cancelReason, cancelReasonOther);
    }*/
    
    // ***************************************************
    // ******************** 常规订单**********************
    // 常规订单作废
    private void cancelCommonOrder(OsOrderPO order, Long userId, Long caller, Long isRefund, Long cancelReason, String cancelReasonOther) throws OmsDelegateException, Exception {
        // 异常拦截订单
        String realOrderStatus = order.getOrderStatus();
        
        boolean returnFee = true; 
        if("x".equals(realOrderStatus)){
            if(isRefund == 0L){
                returnFee = false; // 异常拦截订单，是否退款
            }
            
            List<Object[]> bindOrderConditions = new ArrayList<Object[]>();
            bindOrderConditions.add(new Object[]{"STATUS", "=", "0"});
            bindOrderConditions.add(new Object[]{"IS_PARENT", "=", "0"});
            bindOrderConditions.add(new Object[]{"ORDER_CODE", "=", order.getOrderCode()});
            List<TBindOrderPO> bindOrderList = tBindOrderDao.findTBindOrderPOListByCondition(bindOrderConditions, false, false, true);
            if (bindOrderList == null || bindOrderList.size() != 1){
                LogUtils4Oms.info(logger, uuid, "异常订单查询历史状态错误!", "orderCode", order.getOrderCode(), "bindOrderList", bindOrderList);
                throw new OmsDelegateException(OmsSubOrderCancelResultEnum.CANCEL_FAIL.getResultID(), "异常订单查询历史状态错误!", null);
            }
            LogUtils4Oms.info(logger, uuid, "订单拦截表信息", "tBindOrderPO", bindOrderList.get(0));
            realOrderStatus = bindOrderList.get(0).getOrderStatus();
        }
        
        // 判断订单状态
        if (!OrderStatusEnum.canBeCanceled(OrderStatusEnum.get(realOrderStatus))) {
            LogUtils4Oms.info(logger, uuid, "订单状态非法，只有“已审核”或“配货中”的订单才能作废！", "orderCode", order.getOrderCode(), "orderStatus", realOrderStatus);
            throw new OmsDelegateException(OmsSubOrderCancelResultEnum.CANCEL_FAIL.getResultID(), "订单状态非法，只有“已审核”或“配货中”的订单才能作废！", null);
        }
        List<OsOrderPO> orderList = new ArrayList<OsOrderPO>();
        orderList.add(order);
        switch (OrderTypeEnum.get(order.getOrderType())) {
            case JIELONG_GROUPBUY:
            case BAOYOU_TUANGOU_ORDER:
                //cancelTGBYOrder(orderList, userId, caller, isRefund, cancelReason, cancelReasonOther);
                cancelOrderCommonmethod(true, false, returnFee, orderList, userId, caller, cancelReason, cancelReasonOther);
                break;
            default:
                // cancelDefaultOrder(orderList, userId, caller, isRefund, cancelReason, cancelReasonOther);
                if (null != order.getActiveId() && "1".equals(order.getActiveType())) {
                    cancelOrderCommonmethod(true, false, returnFee, orderList, userId, caller, cancelReason, cancelReasonOther);
                } else {
                    cancelOrderCommonmethod(true, true, returnFee, orderList, userId, caller, cancelReason, cancelReasonOther);
                }
        }
    }
    
    // **********************************************************
    
    // 更新订单状态为作废
    private int cancelOrder(List<OsOrderPO> orderList) {
        int resultNum = 0;
        for (OsOrderPO order : orderList) {
            List<Object[]> subChangeList = new ArrayList<Object[]>();
            subChangeList.add(new Object[]{"ORDER_STATUS", OrderStatusEnum.CANCEL.getC()});
            subChangeList.add(new Object[]{"SYSTEM_REMARK", (null == order.getSystemRemark()?"":"\n") + "订单被作废！"});
            List<Object[]> subWhereList = new ArrayList<Object[]>();
            subWhereList.add(new Object[]{"ORDER_CODE", "=", order.getOrderCode()});
            subWhereList.add(new Object[]{"ORDER_STATUS", "=", order.getOrderStatus()});
            Log.info(logger, uuid, "更新子订单状态为作废", "subChangeList", StringUtil.transferObjectList(subChangeList), "subWhereList", StringUtil.transferObjectList(subWhereList));
            resultNum += osOrderDao.updateOsOrderPOByCondition(subChangeList, subWhereList);
        }
        return resultNum;
    }
    
    // 增加订单操作日志
    private int addOrderOpt(List<OsOrderPO> orderList, Long userId, Long caller, Long cancelReason, String cancelReasonOther) {
        int resultNum = 0;
        for (OsOrderPO order : orderList) {
            OsOrderOptPO optVo = new OsOrderOptPO();
            optVo.setOptDt(new Timestamp(System.currentTimeMillis()));
            optVo.setOrderId(order.getOrderId());
            optVo.setUserId(userId);
            optVo.setOrderStatus(OrderStatusEnum.CANCEL.getC());
            optVo.setOrderPayStatus(order.getPayStatus());
            optVo.setNotifyStatus("0");
            optVo.setIsShow("1");
            optVo.setRemark("您的订单已被取消，详情请咨询400-898-9797");
            optVo.setRemarkEn("Your order has been cancelled. Please contact 400-898-9797 for further information.");
            optVo.setCancelType(caller);
            optVo.setCancelReason(cancelReason);
            if(!StringUtil.isEmpty(cancelReasonOther)){
                optVo.setCancelReasonOther(cancelReasonOther);
            }
            LogUtils4Oms.info(logger, uuid, "增加订单操作日志", "optVo", optVo);
            resultNum += osOrderOptDao.addOsOrderOptPO(optVo);
        }
        return resultNum;
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
    	return  buildingQualificationDao.updateBuildingQualificationPOByCondition(buildingCondition, whereCondition);
    }
    
    public boolean userHavedQualification(OsOrderPO order){
    	List<Object[]> qualifyCondition = new ArrayList<Object[]>();
    	qualifyCondition.add(new Object[]{"ACTIVITY_ID","=", order.getActiveId()});
    	qualifyCondition.add(new Object[]{"BUYER_ID","=",order.getBuyerId()});
    	List<BuildingQualificationPO> qualificationPOList =  buildingQualificationDao.findBuildingQualificationPOListByCondition(qualifyCondition);
    	if(null==qualificationPOList || qualificationPOList.size()<=0){
    		return false;
    	}
    	return true;
    }
    
    // 包邮团购订单
    /*private void cancelTGBYOrder(List<OsOrderPO> orderList, Long userId, Long caller, Long isRefund, Long cancelReason, String cancelReasonOther) throws OmsDelegateException, Exception {
        cancelOrderCommonmethod(true, false, orderList, userId, caller, isRefund, cancelReason, cancelReasonOther);
    }*/
    
    // 作废普通订单
    /*private void cancelDefaultOrder(List<OsOrderPO> orderList, Long userId, Long caller, Long isRefund, Long cancelReason, String cancelReasonOther) throws OmsDelegateException, Exception {
        OsOrderPO orderTemp = orderList.get(0);
        if (null != orderTemp.getActiveId() && "1".equals(orderTemp.getActiveType())) {
            cancelOrderCommonmethod(true, false, orderList, userId, caller, isRefund, cancelReason, cancelReasonOther);
        } else {
            cancelOrderCommonmethod(true, true, orderList, userId, caller, isRefund, cancelReason, cancelReasonOther);
        }
    }*/
    
    /**
     * Description:<br>
     * 作废订单时公用方法
     * 
     * @param returnCanSaleNum：是否退库存
     * @param returnShip：是否退运力
     * @param returnFee：是否退款
     * @param orderList
     * @param userId
     * @param caller
     * @param cancelReason
     * @param cancelReasonOther
     * @throws OmsDelegateException
     * @throws Exception
     */
    private void cancelOrderCommonmethod(boolean returnCanSaleNum, boolean returnShip, boolean returnFee, List<OsOrderPO> orderList, Long userId, Long caller, Long cancelReason, String cancelReasonOther) throws OmsDelegateException, Exception {
        OsOrderPO orderTemp = orderList.get(0);
        
        if(!"x".equals(orderTemp.getOrderStatus())){
            // 更新订单状态
            int resultNum = cancelOrder(orderList);
            if (resultNum != orderList.size()) {
                LogUtils4Oms.info(logger, uuid, "更新订单状态为作废失败", "resultNum", resultNum, "orderList.size", orderList.size());
                throw new OmsDelegateException(OmsSubOrderCancelResultEnum.CANCEL_FAIL.getResultID(), "更新订单状态为作废失败！", null);
            }
            // 添加日志
            resultNum = addOrderOpt(orderList, userId, caller, cancelReason, cancelReasonOther);
            if(resultNum != orderList.size()){
                LogUtils4Oms.info(logger, uuid, "增加订单操作日志失败", "resultNum", resultNum, "orderList.size", orderList.size());
                throw new OmsDelegateException(OmsSubOrderCancelResultEnum.CANCEL_FAIL.getResultID(), "增加订单操作日志失败！", null);
            }
        }
        
        
        // 恢复用户参加楼宇资格
        if(null != orderTemp.getActiveId() && "1".equals(orderTemp.getActiveType())){
            //判断是否有资格，没有资格直接略过,恢复资格步骤，进行退库存、退运力
            LogUtils4Oms.info(logger, uuid, "归还用户楼宇活动参与资格开始！");
            if(userHavedQualification(orderTemp)){
                LogUtils4Oms.info(logger, uuid, "用户曾获得过资格！");
                if(checkBrotherOrderISAllCancle(orderTemp)){
                    int resultNum  = updateBuildingQualification(orderTemp.getActiveId(),orderTemp.getBuyerId());
                    if(resultNum != 1){
                        LogUtils4Oms.info(logger, uuid, "恢复楼宇活动用户的参与资格失败", "resultNum", resultNum, "orderID", orderTemp.getOrderId());
                        throw new OmsDelegateException(OmsOrderCancelResultEnum.CANCEL_FAIL.getResultID(), "恢复楼宇活动用户的参与资格失败！", null);
                    }
                    LogUtils4Oms.info(logger, uuid, ",子订单全部作废,恢复楼宇活动用户的【参与资格】成功", "resultNum", resultNum, "orderID", orderTemp.getOrderId());
                }else{
                    LogUtils4Oms.info(logger, uuid, "子订单没有全部作废,不能恢复资格", "orderID", orderTemp.getOrderId());
                }
            }
        }
        
        
        //恢复兑换券资格
        //子订单使用了兑换券，作废时才恢复兑换券资格
        LogUtils4Oms.info(logger, uuid, "恢复用户兑换券资格开始！");
        Set<String> vocherSet = haveUsedSubOrderVocher(orderTemp.getOrderId());
        if(vocherSet!=null && vocherSet.size()>0){
        	if(!updateVoucher(orderTemp.getBuyerId(),vocherSet)){
        		throw new OmsDelegateException(OmsOrderCancelResultEnum.CANCEL_FAIL.getResultID(), "恢复用户兑换券失败！", null);
            }
        	LogUtils4Oms.info(logger,uuid, "恢复用户兑换券成功！");
        }else {
        	LogUtils4Oms.info(logger, uuid, "子订单未使用兑换券，不需要恢复兑换券资格！");
		}
        
        this.updateFindChannelNum(orderTemp);
        
        /********************用户首单标识*********************/
        if("1".equals(orderTemp.getIsFirst()) && checkBrotherOrderISAllCancle(orderTemp)){
            int firstNum = BuyerFirstOrderUtil.deleteFirstOrder(orderTemp.getBuyerId(), orderTemp.getParentCode(), logger, uuid, new BuyerFirstOrderDao(uuid, logger));
            if(firstNum != 1){
                Log.info(logger, uuid, "逻辑删除用户首单关系数据失败", "firstNum", firstNum);
            }
        }
        /********************用户首单标识*********************/
        
        if (returnCanSaleNum) {// 库存
            for (OsOrderPO order : orderList) {
                StockModifyLogicStockInputData stockInputData = new StockModifyLogicStockInputData();
                stockInputData.setRequestType("1");
                stockInputData.setRequestorType("0");
                stockInputData.setRequestorCode(order.getParentCode());
                stockInputData.setRelatedRequestorCode(order.getOrderCode());
                StockModifyLogicStockModifyInfoElementI modifyInfo = new StockModifyLogicStockModifyInfoElementI();
                List<StockModifyLogicStockModifyListElementI> modifyList = new ArrayList<StockModifyLogicStockModifyListElementI>();
                List<OsOrderItemsPO> orderItemsList = osOrderItemsDao.findOsOrderItemsPOListBySql("ORDER_ID="
                                + order.getOrderId());
                if (orderItemsList == null || orderItemsList.size() <= 0) {
                    LogUtils4Oms.info(logger, uuid, "退库存时，订单没有明细信息！", "ORDER_ID", order.getOrderId());
                    throw new OmsDelegateException(OmsSubOrderCancelResultEnum.CANCEL_FAIL.getResultID(), "退库存时，订单没有明细信息！", null);
                }
                for (OsOrderItemsPO orderItem : orderItemsList) {
                    if ("1".equals(orderItem.getIsGiftbox())) continue; // 过滤掉礼盒，传礼盒内的商品
                    StockModifyLogicStockModifyListElementI stockElement = new StockModifyLogicStockModifyListElementI();
                    stockElement.setSubstationID(order.getSubstationId());
                    stockElement.setWarehouseID(order.getWarehouseId());
                    stockElement.setGoodsBasicID(orderItem.getBasicId());
                    
                    // 如果是下架促销，退残品库存
                    if(OrderTypeEnum.PULLOFF_SHELVES.getC().equals(order.getOrderType())){
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
                        LogUtils4Oms.info(logger, uuid, json, "订单作废退库存成功！", "order", order);
                    } else {
                        LogUtils4Oms.info(logger, uuid, json, baseBean.getOutputHead().getResultMessage(), "库存接口resultId", baseBean.getOutputHead().getResultID());
                        throw new OmsDelegateException(OmsSubOrderCancelResultEnum.CANCEL_FAIL.getResultID(), "订单作废调用退库存接口失败，"
                                        + baseBean.getOutputHead().getResultMessage(), null);
                    }
                } catch (OmsDelegateException e) {
                    LogUtils4Oms.info(logger, uuid, "订单作废调用退库存接口失败！", "json", json, e);
                    throw e;
                } catch (Exception e) {
                    LogUtils4Oms.info(logger, uuid, json, "订单作废库存接口调用失败！", "json", json);
                    throw e;
                }
            }
        }
        if (returnShip) {// 运力
            for (OsOrderPO order : orderList) {
                ShippingOccupyShippingPowerInputData shippingOccupyInputData = new ShippingOccupyShippingPowerInputData();
                shippingOccupyInputData.setIsRelease("1");
                boolean isAll = true;
                List<OsOrderPO> listOrder = osOrderDao.findOsOrderPOListBySql("PARENT_ID="
                                + order.getParentId(), false, false, true);
                for (OsOrderPO o : listOrder) {
                    if (o.getOrderId().compareTo(order.getOrderId()) == 0) continue;
                    if (!"z".equals(o.getOrderStatus())
                                    && order.getDcId().compareTo(o.getDcId()) == 0
                                    && order.getWarehouseId().compareTo(o.getWarehouseId()) == 0) {
                        isAll = false;
                        break;
                    }
                }
                if (isAll) {
                    shippingOccupyInputData.setIsAllCancel("1");
                } else {
                    shippingOccupyInputData.setIsAllCancel("0");
                }
                shippingOccupyInputData.setOrderCode(order.getParentCode());
                shippingOccupyInputData.setRelatedCode(order.getOrderCode());
                
                ShippingOccupyShippingPowerOccupyInfoElementI occupyInfo = new ShippingOccupyShippingPowerOccupyInfoElementI();
                List<ShippingOccupyShippingPowerOccupyListElementI> occupyList = new ArrayList<ShippingOccupyShippingPowerOccupyListElementI>();
                ShippingOccupyShippingPowerOccupyListElementI occupy = new ShippingOccupyShippingPowerOccupyListElementI();
                occupy.setWarehouseID(order.getWarehouseId());
                occupy.setShippingCompanyID(order.getDcId());
                occupyList.add(occupy);
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
                        LogUtils4Oms.info(logger, uuid, "订单作废退运力成功！", "order", order);
                    } else {
                        LogUtils4Oms.info(logger, uuid, baseBean.getOutputHead().getResultMessage(), "运力接口resultId", baseBean.getOutputHead().getResultID());
                        throw new OmsDelegateException(OmsSubOrderCancelResultEnum.CANCEL_FAIL.getResultID(), "订单作废调用退运力接口失败,"
                                        + baseBean.getOutputHead().getResultMessage(), null);
                    }
                } catch (OmsDelegateException e) {
                    LogUtils4Oms.info(logger, uuid, "订单作废调用退运力接口失败！", "json", json, e);
                    throw e;
                } catch (Exception e) {
                    LogUtils4Oms.info(logger, uuid, json, "订单作废退运力接口调用失败！", "json", json);
                    throw e;
                }
            }
        }
        
        
        if (returnFee) {
            // 退款
            PaymentCancelInputData paymentCancelInputData = new PaymentCancelInputData();
            List<PaymentCancelOrderInfoListElementI> cancelOrderInfoList = new ArrayList<PaymentCancelOrderInfoListElementI>();
            for (OsOrderPO order : orderList) {
                PaymentCancelOrderInfoListElementI cancelOrderInfo = new PaymentCancelOrderInfoListElementI();
                cancelOrderInfo.setOrderId(order.getOrderId());
                cancelOrderInfo.setOrderCode(order.getOrderCode());
                cancelOrderInfoList.add(cancelOrderInfo);
            }
            paymentCancelInputData.setOrderInfoList(cancelOrderInfoList);
            paymentCancelInputData.setUserID(userId);
            if ("x".equals(orderTemp.getOrderStatus())){ //异常拦截订单不退优惠券
                paymentCancelInputData.setIsRefundCoupon(BooleanEnum.FALSE.getV());
            }
            HashMap<String, String> params = (HashMap<String, String>)AuthorizeClient.getVerifyInfo(this.httpRequest);
            Map<String, String> omsCat = (Map<String, String>)params.clone();
            omsCat.put("method", "cancel");
            String json = null;
            BaseOutputBean baseBean = new BaseOutputBean();
            try {
                json = paymentCancelInputData.toJson();
                omsCat.put("req_str", json);
                baseBean = TootooService.callServer("payment", omsCat, "post", new OmsSubOrderCancelOutputData());
                if (BaseResultEnum.SUCCESS.getResultID() == baseBean.getOutputHead().getResultID().intValue()) {
                    LogUtils4Oms.info(logger, uuid, json, "订单作废退款成功！", "orderList", orderList);
                } else {
                    LogUtils4Oms.info(logger, uuid, baseBean.getOutputHead().getResultMessage(), "退款接口resultId", baseBean.getOutputHead().getResultID());
                    throw new OmsDelegateException(OmsSubOrderCancelResultEnum.CANCEL_FAIL.getResultID(), "订单作废调用支付接口失败" + "，"
                                    + baseBean.getOutputHead().getResultMessage(), null);
                }
            } catch (OmsDelegateException e) {
                LogUtils4Oms.info(logger, uuid, "订单作废调用支付接口失败！", "json", json, e);
                throw e;
            } catch (Exception e) {
                LogUtils4Oms.info(logger, uuid, "订单作废退款接口调用失败！", "json", json);
                throw e;
            }
        }
        
    }
    
    public void updateFindChannelNum(OsOrderPO osOrder) {
        try{
            if (ActiveTypeEnum.isFindChannel(osOrder.getActiveType())) {
                List<OsOrderItemsPO> orderItemsList = osOrderItemsDao.findOsOrderItemsPOListBySql("ORDER_ID=" + osOrder.getOrderId());
                
                long promotionId = 0L;
                for (OsOrderItemsPO item : orderItemsList) {
                    if (item.getGiftboxId() != 0 || StringUtil.isEmpty(item.getPromoteId()) || "0".equals(item.getPromoteId())) {
                        continue;
                    }
                    promotionId = Long.valueOf(item.getPromoteId());
                }
                
                Log.info(logger, uuid, "发现频道作废减冻结数");
                if (promotionId != 0 && OrderTypeEnum.BAOYOU_TUANGOU_ORDER.getC().equals(osOrder.getOrderType())) {// 团购订单
                    Log.info(logger, uuid, "组装promotion服务increamentTuangouCount方法所需参数开始");
                    PromotionIncreamentTuangouCountInputData increamentInputData = new PromotionIncreamentTuangouCountInputData();
                    increamentInputData.setScope("11101");
                    increamentInputData.setPromotionId(promotionId);
                    increamentInputData.setFlag(1L); // 删除
                    for (OsOrderItemsPO item : orderItemsList) {
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
                    if (!TootooService.checkService(promotionOutputBean, "promotion", "increamentTuangouCount", null)) {
                        Log.error(logger, uuid, "调用promotion服务increamentTuangouCount方法失败", null);
                    }
                }
                
                if (promotionId != 0 && OrderTypeEnum.JIELONG_GROUPBUY.getC().equals(osOrder.getOrderType())) {// 接龙团购
                    Log.info(logger, uuid, "组装promotion服务updateWeixinjielongBuyHistory方法所需参数开始");
                    PromotionUpdateWeixinjielongBuyHistoryInputData jielongInputData = new PromotionUpdateWeixinjielongBuyHistoryInputData();
                    jielongInputData.setScope("11101");
                    jielongInputData.setPromotionId(promotionId);
                    jielongInputData.setFlag(1L); // 删除
                    for (OsOrderItemsPO item : orderItemsList) {
                        if (item.getGiftboxId() != 0 || StringUtil.isEmpty(item.getPromoteId()) || "0".equals(item.getPromoteId())) {
                            continue;
                        }
                        PromotionUpdateWeixinjielongBuyHistoryBuyHistoryElementI jielongElement = new PromotionUpdateWeixinjielongBuyHistoryBuyHistoryElementI();
                        jielongElement.setCreateTime(new SimpleDateFormat("yyyy-MM-dd").format(osOrder.getCreateDt()));
                        jielongElement.setGoodsId(item.getGoodsId() + "");
                        jielongElement.setNickname(null); // 再议
                        jielongElement.setNum(item.getGoodsNumber().toString());
                        jielongInputData.getBuyHistory().add(jielongElement);
                    }
                    Map<String, String> jielongParams = new HashMap<String, String>();
                    jielongParams.put("r", "TUpdateWeixinjielongBuyHistory");
                    jielongParams.put("req_str", jielongInputData.toJson());
                    Log.info(logger, uuid, "调用promotion服务updateWeixinjielongBuyHistory方法开始", "jielongParams", jielongParams);
                    BaseOutputBean promotionOutputBean = TootooService.callServer(logger, uuid, "promotion", jielongParams, "post", new PromotionUpdateWeixinjielongBuyHistoryOutputData());
                    Log.info(logger, uuid, "调用promotion服务updateWeixinjielongBuyHistory方法结束", "promotionOutputBean", promotionOutputBean);
                    if (!TootooService.checkService(promotionOutputBean, "promotion", "updateWeixinjielongBuyHistory", null)) {
                        Log.error(logger, uuid, "调用promotion服务updateWeixinjielongBuyHistory方法失败", null);
                    }
                    
                }
            
                
                Log.info(logger, uuid, "发现频道作废减历史购买数（如果有）");
                // 先查询作废的商品是否有存购买数量（设置了历史购买数）
                OmsGetBuyerPromotionNumInputData getBuyerNumI = new OmsGetBuyerPromotionNumInputData();
                getBuyerNumI.setScope("11101");
                getBuyerNumI.setBuyerId(osOrder.getBuyerId());
                List<OmsGetBuyerPromotionNumPromotionListElementI> promotionListI = new ArrayList<OmsGetBuyerPromotionNumPromotionListElementI>();
                OmsGetBuyerPromotionNumPromotionListElementI promotionI = new OmsGetBuyerPromotionNumPromotionListElementI();
                promotionI.setPromotionId(promotionId);
                List<OmsGetBuyerPromotionNumGoodsListElementI> getGoodsList = new ArrayList<OmsGetBuyerPromotionNumGoodsListElementI>();
                for (OsOrderItemsPO item : orderItemsList) {
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
                if (!TootooService.checkService(getBuyerNumOutputBean, "oms", "getBuyerPromotionNum", null)) {
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
                setBuyerNumInputData.setScope("11101");
                setBuyerNumInputData.setBuyerId(osOrder.getBuyerId());
                setBuyerNumInputData.setPromotionId(promotionId);
                List<OmsSetBuyerPromotionNumGoodsListElementI> setGoodsList = new ArrayList<OmsSetBuyerPromotionNumGoodsListElementI>();
                for (OsOrderItemsPO item : orderItemsList) {
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
                
                if (!TootooService.checkService(setBuyerNumOutputBean, "oms", "setBuyerPromotionNum", null)) {
                    Log.error(logger, uuid, "调用oms服务setBuyerPromotionNum方法失败", null);
                }
            }
        
        } catch (RuntimeException r) {
            Log.error(logger, uuid, "运行时异常：+'方法名updateCache:'", r);
        } catch (Exception e) {
            Log.error(logger, uuid, "团购订单更新缓存失败:+'方法名updateCache:'", e);
        }
    }
    
    public boolean checkBrotherOrderISAllCancle(OsOrderPO order){
	    boolean allCancel = true;
    	List<OsOrderPO> listOrder = osOrderDao.findOsOrderPOListBySql("PARENT_ID=" + order.getParentId(), false, false, true);
    	for (OsOrderPO orderPO : listOrder) {
    		if(!OrderStatusEnum.isCanceled(orderPO.getOrderStatus())){
    			allCancel = false;
    			break;
    		}
    		
    	}
    	return allCancel;
   }
    
    public Set<String> haveUsedSubOrderVocher(Long orderId){
    	Set<String> set = new HashSet<String>();
    	List<Object[]> suborderItemCondition = new ArrayList<Object[]>();
    	suborderItemCondition.add(new Object[]{"ORDER_ID","=", orderId});
    	List<OsOrderItemsPO> orderItemList = osOrderItemsDao.findOsOrderItemsPOListByCondition(suborderItemCondition);
    	for (OsOrderItemsPO item : orderItemList) {
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
    public void doClose(final BaseInputHead inputHead, final OmsSubOrderCancelInputData inputData, final BaseOutputHead outputHead, final OmsSubOrderCancelOutputData outputData) throws OmsDelegateException {
        
    }
}
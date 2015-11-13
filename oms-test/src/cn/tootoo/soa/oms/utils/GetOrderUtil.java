/**
 * Title：GetOrderUtil.java<br>
 * Date：2014-11-26 下午3:13:21<br>
 */
package cn.tootoo.soa.oms.utils;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import cn.tootoo.db.egrocery.ospayplan.OsPayPlanDao;
import cn.tootoo.db.egrocery.ospayplan.OsPayPlanPO;
import cn.tootoo.soa.base.enums.OrderStatusEnum;
import cn.tootoo.soa.base.enums.OrderTypeEnum;
import cn.tootoo.soa.base.enums.ParentOrderStatusEnum;
import cn.tootoo.soa.base.enums.PayStatusEnum;
import cn.tootoo.soa.base.enums.PayTypeEnum;
import cn.tootoo.soa.base.global.DictionaryData;
import cn.tootoo.utils.StringUtil;

/**
 * Description:<br>
 * 
 * @author chuan
 * @version 1.0
 */
public class GetOrderUtil {
    
    
    // 增加虚拟礼盒的ID(固定的为1034964L)
    public static final Long VIRTUAL_GIFT = 1034964L;
    
    
    /**
     * 父订单是否已作废:
     * 作废状态有三种： [-1,-2,-3]
     * Description:<br>
     * 
     * @param orderStatus
     * @return
     */
    public static boolean getParentIsCanceled(Long orderStatus){
        if (ParentOrderStatusEnum.getFrontCanceled().contains(orderStatus.intValue())) {
            return true;
        }
        return false;
    }
    
    /**
     * 子订单是否已作废:
     * 作废状态有一种：z
     * Description:<br>
     * 
     * @param orderStatus
     * @return
     */
    public static boolean getSubIsCanceled(String orderStatus){
        if(OrderStatusEnum.CANCEL.getC().equals(orderStatus)){
            return true;
        }
        return false;
    }
    
    /**
     * 是否显示订单金额:
     * 订单类型不为提货订单时(order_type not in ['5','6')])，显示订单金额
     * Description:<br>
     * 
     * @param orderType
     * @return
     */
    public static boolean getIsShowOrderCallFee(String orderType){
        if (!OrderTypeEnum.isTiHuoOrder(orderType)) {
            return true;
        }
        return false;
    }
    
    /**
     * 是否显示去支付按钮,只针对父订单，满足条件：
     *  1.订单状态不为作废 
     *  2.支付类型为线上支付 (从原始支付计划中查询支付类型为网关支付的)
     *  3.支付状态不为已支付
     * Description:<br>
     * 
     * @param orderStatus
     * @param payType
     * @param payStatus
     * @return
     */
    public static boolean getParentIsShowToPay(Long orderStatus, String payStatus, String payType, Long orderId, OsPayPlanDao osPayPlanDao, Logger logger, String uuid) {
        if(ParentOrderStatusEnum.getFrontCanceled().contains(orderStatus.intValue())){
            return false;
        }
        if(PayStatusEnum.YES.getC().equals(payStatus)){
            return false;
        }
        
        OsPayPlanPO payPlan = osPayPlanDao.findOsPayPlanPOByID(orderId);
        if (payPlan == null || StringUtil.isEmpty(payPlan.getPayInfo())) {
            // 针对无支付计划的老数据容错
            if(PayTypeEnum.ONLINE.getC().equals(payType)){
                return true;
            }else{
                return false;
            }
        } else {
            Map<Long, Long> payMethodMap = DictionaryData.getPayMethodPidMap(logger, uuid);
            boolean onlinePayMethod = false; // 是否包含网关支付(去支付按钮)
            String[] payInfoArray = payPlan.getPayInfo().split("\\^");
            for (String s : payInfoArray) {
                String[] array = s.split("_");
                Long key = payMethodMap.get(Long.valueOf(array[0]));
                if(key == 0L){
                    key = Long.valueOf(array[0]);
                }
                if(key.intValue() == 5){
                    onlinePayMethod = true;
                }
            }
            
            return onlinePayMethod;
            
        }
        
        
    }
    
    
    /**
     * 是否显示价格,满足条件是：
     * 1. 该明细价格不为0
     * 2.订单类型不为提货订单(order_type in ['5','6')])
     * Description:<br>
     * 
     * @param goodsPrice
     * @param orderType
     * @return
     */
    public static boolean getIsShowPrice(BigDecimal goodsPrice, String orderType){
        if (goodsPrice != null && !BigDecimal.ZERO.equals(goodsPrice) && !OrderTypeEnum.isTiHuoOrder(orderType)) {
            return true;
        }
        return false;
    }
    
    /**
     * 是否提供链接,满足条件是：
     * 1.该明细并非N元Y件礼盒的专用商品(goods_id==1034964)  
     * 2.该订单不为团购包邮订单
     * Description:<br>
     * 
     * @param goodsId
     * @param orderType
     * @return
     */
    public static boolean getHasLink(Long goodsId, String orderType){
        if (goodsId != VIRTUAL_GIFT && !OrderTypeEnum.BAOYOU_TUANGOU_ORDER.getC().equals(orderType)
                        && !OrderTypeEnum.JIELONG_GROUPBUY.getC().equals(orderType)) {
            return true;
        }
        return false;
    }
    
    /**
     * 父订单是否显示分享按钮,满足条件是：
     * 1. 订单属于中文站
     * 2.订单状态不为作废(父订单作废状态有三种： [-1,-2,-3] 子订单作废状态有一种：z)
     * 3.订单类型不为团购包邮订单
     * 4.该明细不是赠品
     * Description:<br>
     * 
     * @param scope
     * @param orderStatus
     * @param orderType
     * @param isGift
     * @return
     */
    public static boolean getParentIsShowShare(String scope, Long orderStatus, String orderType, Byte isGift){
        if (!"21101".equals(scope) && !ParentOrderStatusEnum.getFrontCanceled().contains(orderStatus.intValue())
                        && !OrderTypeEnum.BAOYOU_TUANGOU_ORDER.getC().equals(orderType)
                        && !OrderTypeEnum.JIELONG_GROUPBUY.getC().equals(orderType)
                        && (isGift == null || isGift != 1)) {
            return true;
        }
        return false;
    }
    
    /**
     * 子订单是否显示分享按钮,满足条件是：
     * 1. 订单属于中文站
     * 2.订单状态不为作废(父订单作废状态有三种： [-1,-2,-3] 子订单作废状态有一种：z)
     * 3.订单类型不为团购包邮订单 
     * 4.该明细不是赠品
     * Description:<br>
     * 
     * @param scope
     * @param orderStatus
     * @param orderType
     * @param isGift
     * @return
     */
    public static boolean getSubIsShowShare(String scope, String orderStatus, String orderType, Byte isGift) {
        if (!"21101".equals(scope) && !OrderStatusEnum.CANCEL.getC().equals(orderStatus)
                        && !OrderTypeEnum.BAOYOU_TUANGOU_ORDER.getC().equals(orderType)
                        && !OrderTypeEnum.JIELONG_GROUPBUY.getC().equals(orderType)
                        && (isGift == null || isGift != 1)) { return true; }
        return false;
    }
    
    /**
     * 是否显示评论按钮,满足条件是：
     * 1.该明细属于子订单而非父订单
     * 2.该明细所属子订单状态范围是已发货和已签收 (order_status in [5,6,7,A,B])
     * 3.该明细不是赠品
     * 4.同一个子订单的同一商品最多只能评论一次(可根据review_id来判断，为0表示未评论，大于0表示已评论)
     * 5.只能对在180天内购买的商品进行评价+当前时间必须大于receive_dt
     * Description:<br>
     * 
     * @param osOrderItemsPO
     * @param osOrderPO
     */
    public static boolean getIsShowReview(String orderStatus, Timestamp receiveDt, Byte isGift, Long reviewId, String isGiftBox) {
        boolean orderStatusFlag = false;
        if (OrderStatusEnum.SHIPPING.getC().equals(orderStatus) || OrderStatusEnum.ALL_SIGNED.getC().equals(orderStatus)
                        || OrderStatusEnum.PART_SIGNED.getC().equals(orderStatus) || OrderStatusEnum.ALL_SIGNED_RETURN.getC().equals(orderStatus)
                        || OrderStatusEnum.PART_SIGNED_RETURN.getC().equals(orderStatus)){
            orderStatusFlag = true;
        }
        
        boolean isGiftFlag = false;
        if (isGift==null || isGift != 1) {
            isGiftFlag = true;
        }
        
        boolean reviewIdFlag = false;
        if (reviewId <= 0L) {
            reviewIdFlag = true;
        }
        
        boolean receiveDtFlag = false;
        Long curDt = new Timestamp(System.currentTimeMillis()).getTime();
        if ((((curDt - receiveDt.getTime()) / (1000 * 60 * 60 * 24)) <= 180) && (curDt > receiveDt.getTime()) ) {
            receiveDtFlag = true;
        }
        
        if (orderStatusFlag && isGiftFlag && reviewIdFlag && receiveDtFlag) { return true; }
        return false;
        
    }
    
    /**
     * 父订单“跟踪“按钮（电子卡订单不提供跟踪操作）
     * 当订单状态为待审核、待支付和待收货三个状态时才显示“跟踪“操作。
     * Description:<br>
     * 
     * @param orderStatus
     * @param orderType
     * @return
     */
    public static boolean getParentIsShowOpt(int orderStatus, String orderType) {
        // 电子卡订单不显示跟踪操作
        if(OrderTypeEnum.ONLINE_CARD_VIRTUAL.getC().equals(orderType)){
            return false;
        }
        // 父订单 为待审核、待支付和待收货 显示跟踪操作
        if (ParentOrderStatusEnum.getFrontWaitCheck(PayTypeEnum.OFFLINE.getC()).contains(orderStatus)
                        || ParentOrderStatusEnum.getFrontWaitPay(PayTypeEnum.ONLINE.getC()).contains(orderStatus)
                        || ParentOrderStatusEnum.getFrontWaitReceive().contains(orderStatus)){
            return true;
        }
        return false;
    }
    
    /**
     * 子订单“跟踪“按钮（电子卡订单不提供跟踪操作）
     * 当订单状态为待审核、待支付和待收货三个状态时才显示“跟踪“操作。
     * Description:<br>
     * 
     * @param orderStatus
     * @param orderType
     * @return
     */
    public static boolean getSubIsShowOpt(String orderStatus, String orderType) {
        // 电子卡订单不显示跟踪操作
        if(OrderTypeEnum.ONLINE_CARD_VIRTUAL.getC().equals(orderType)){
            return false;
        }
        // 子订单 待收货时 显示跟踪操作
        if (OrderStatusEnum.getFrontWaitReceive().contains(orderStatus)) {
            return true;
        }
        return false;
    }
    
    /**
     * 根据goodsId拼商品url，注意区分线上还是测试
     * Description:<br>
     * 
     * @param scope
     * @param goodsId
     * @return
     */
    public static String getWebUrl(String scope, Long goodsId, String promotionId) {
        // N元Y件礼盒的专用商品(goods_id==1034964)
        if (goodsId == VIRTUAL_GIFT) { return "sale/ny/" + promotionId + ".html"; }
        if ("21101".equals(scope)) {
            return "product-" + goodsId + "-en.html";
        } else {
            return "product-" + goodsId + "-zh_cn.html";
        }
    }
    
    /**
     * Description:<br>
     * 父订单：
     * 是否显示收货日期，当订单作废时不显示，电子卡不显示
     * 其他情况，
     * 沱沱惠、接龙团购订单-团购订单：[发货日期：YYYY-MM-DD陆续发货]
     * 私享团-团购订单：[发货日期：预计两天内发货]
     * 非团购订单,用户可指定日期SpecifiedShippingdate=1时：[预计收货日期：receiveDt]
     * 非团购订单,用户不可指定日期SpecifiedShippingdate!=1时：[发货日期：预计两天内发货]
     * @param orderStatus
     * @return
     */
    /*public static Map<String, String> getParentReceiveDt(Logger logger, String uuid, String scope, int orderStatus, String orderType, String deliveryType, Long subStationId, Long teamId, String specifiedShippingdate, Timestamp receiveDt){
        Map<String, String> map = new HashMap<String, String>();
        try {
            String receiveDtStr = DateUtil.dateToStr(receiveDt);
            
            if (ParentOrderStatusEnum.getFrontCanceled().contains(orderStatus) || OrderTypeEnum.ONLINE_CARD_VIRTUAL.equals(orderType)) {
                map.put("isShowReceiveDt", "0");
                return map;
            }
            
            Timestamp springStart = DateUtil.strToTimestamp("2015-02-17 23:59:59");
            Timestamp springEnd = DateUtil.strToTimestamp("2015-02-24 00:00:00");
            if (OrderTypeEnum.NORMAL.getC().equals(orderType) && DeliveryTypeEnum.THIRD.getC().equals(deliveryType) 
                            && !(SubstationEnum.SH.getC() == subStationId && "1".equals(specifiedShippingdate)) // 不是上海路上货运
                            && receiveDt.after(springStart) && receiveDt.before(springEnd)){
                // 春节第三方收货日期
                if("21101".equals(scope)){ // 英文站
                    map.put("isShowReceiveDt", "1");
                    map.put("receiveMsg", "Distribution Date");
                    map.put("receiveDt", "2015-02-24 Deliver Successively");
                }else{
                    map.put("isShowReceiveDt", "1");
                    map.put("receiveMsg", "发货日期");
                    map.put("receiveDt", "2015-02-24 陆续发货");
                }
                return map;
            }
            
            if (OrderTypeEnum.BAOYOU_TUANGOU_ORDER.getC().equals(orderType) || OrderTypeEnum.JIELONG_GROUPBUY.getC().equals(orderType)) {
                if("21101".equals(scope)){ // 英文站
                    map.put("isShowReceiveDt", "0");
                } else if (teamId != null && teamId != 0) { // 私享团
                    map.put("isShowReceiveDt", "1");
                    map.put("receiveMsg", "发货日期");
                    map.put("receiveDt", "预计两天内发货");
                } else {
                    map.put("isShowReceiveDt", "1");
                    map.put("receiveMsg", "发货日期");
                    map.put("receiveDt", receiveDtStr + " 陆续发货");
                }
                return map;
            }
            
            if ("1".equals(specifiedShippingdate)) {
                if("21101".equals(scope)){ // 英文站
                    map.put("isShowReceiveDt", "1");
                    map.put("receiveMsg", "Estimated Receiving Date");
                    map.put("receiveDt", receiveDtStr);
                }else{
                    map.put("isShowReceiveDt", "1");
                    map.put("receiveMsg", "预计收货日期");
                    map.put("receiveDt", receiveDtStr);
                }
                return map;
            }
            
            if("21101".equals(scope)){ // 英文站
                map.put("isShowReceiveDt", "1");
                map.put("receiveMsg", "Distribution Date");
                map.put("receiveDt", "Expect to distribute within 2 days");
            }else{
                map.put("isShowReceiveDt", "1");
                map.put("receiveMsg", "发货日期");
                map.put("receiveDt", "预计两天内发货");
            }
            return map;
            
        } catch (Exception e) {
            Log.error(logger, uuid, "发货日期设置错误", e);
            map.put("isShowReceiveDt", "0");
            return map;
        }
    }*/
    
    /**
     * Description:<br>
     * 子订单：
     * 是否显示收货日期，当订单作废时不显示，电子卡不显示
     * 其他情况，
     * 沱沱惠、接龙团购订单-团购订单：[发货日期：YYYY-MM-DD陆续发货]
     * 私享团-团购订单：[发货日期：预计两天内发货]
     * 非团购订单用户可指定日期SpecifiedShippingdate=1时：[预计收货日期：receiveDt]
     * 非团购订单用户不可指定日期SpecifiedShippingdate!=1时：[发货日期：预计两天内发货]
     * @param orderStatus
     * @return
     */
    /*public static Map<String, String> getSubReceiveDt(Logger logger, String uuid, String scope, String orderStatus, String orderType, String deliveryType, Long subStationId, Long teamId, String specifiedShippingdate, Timestamp receiveDt){
        Map<String, String> map = new HashMap<String, String>();
        try {
            String receiveDtStr = DateUtil.dateToStr(receiveDt);
            
            if(OrderStatusEnum.getFrontCanceled().equals(orderStatus) || OrderTypeEnum.ONLINE_CARD_VIRTUAL.getC().equals(orderType)){
                map.put("isShowReceiveDt", "0");
                return map;
            }
            
            Timestamp springStart = DateUtil.strToTimestamp("2015-02-17 23:59:59");
            Timestamp springEnd = DateUtil.strToTimestamp("2015-02-24 00:00:00");
            if (OrderTypeEnum.NORMAL.getC().equals(orderType) && DeliveryTypeEnum.THIRD.getC().equals(deliveryType)
                            && !(SubstationEnum.SH.getC() == subStationId && "1".equals(specifiedShippingdate)) // 不是上海路上货运
                            && receiveDt.after(springStart) && receiveDt.before(springEnd)){
                // 春节第三方收货日期
                if("21101".equals(scope)){ // 英文站
                    map.put("isShowReceiveDt", "1");
                    map.put("receiveMsg", "Distribution Date");
                    map.put("receiveDt", "2015-02-24 Deliver Successively");
                }else{
                    map.put("isShowReceiveDt", "1");
                    map.put("receiveMsg", "发货日期");
                    map.put("receiveDt", "2015-02-24 陆续发货");
                }
                return map;
            }
    
            if (OrderTypeEnum.BAOYOU_TUANGOU_ORDER.getC().equals(orderType) || OrderTypeEnum.JIELONG_GROUPBUY.getC().equals(orderType)) {
                if("21101".equals(scope)){ // 英文站
                    map.put("isShowReceiveDt", "0");
                }else if (teamId != null && teamId != 0) { // 私享团
                    map.put("isShowReceiveDt", "1");
                    map.put("receiveMsg", "发货日期");
                    map.put("receiveDt", "预计两天内发货");
                } else {
                    map.put("isShowReceiveDt", "1");
                    map.put("receiveMsg", "发货日期");
                    map.put("receiveDt", receiveDtStr + " 陆续发货");
                }
                return map;
            }
            
            if ("1".equals(specifiedShippingdate)) {
                if("21101".equals(scope)){ // 英文站
                    map.put("isShowReceiveDt", "1");
                    map.put("receiveMsg", "Estimated Receiving Date");
                    map.put("receiveDt", receiveDtStr);
                }else{
                    map.put("isShowReceiveDt", "1");
                    map.put("receiveMsg", "预计收货日期");
                    map.put("receiveDt", receiveDtStr);
                }
                return map;
            }
            
            
            if("21101".equals(scope)){ // 英文站
                map.put("isShowReceiveDt", "1");
                map.put("receiveMsg", "Distribution Date");
                map.put("receiveDt", "Expect to distribute within 2 days");
            }else{
                map.put("isShowReceiveDt", "1");
                map.put("receiveMsg", "发货日期");
                map.put("receiveDt", "预计两天内发货");
            }
            return map;
        
        } catch (Exception e) {
            Log.error(logger, uuid, "发货日期设置错误", e);
            map.put("isShowReceiveDt", "0");
            return map;
        }
    }*/
    
    
    /**
     * 将list转为sql语句in格式
     * ('a','b','12')
     * Description:<br>
     * 
     * @param list
     * @return
     */
    public static String list2Sql(List list) {
        StringBuffer sb = new StringBuffer();
        sb.append(" (");
        for (int i = 0; i < list.size(); i++) {
            sb.append("'" + list.get(i) + "'");
            if (i != list.size() - 1) {
                sb.append(",");
            }
        }
        sb.append(") ");
        return sb.toString();
    }
    
    
}

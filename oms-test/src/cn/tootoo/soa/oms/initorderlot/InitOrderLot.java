/**
 * Title：InitOrderLot.java<br>
 * Date：2014-9-12 下午4:47:53<br>
 */
package cn.tootoo.soa.oms.initorderlot;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import cn.tootoo.db.egrocery.tosorderlot.TOsOrderLotDao;
import cn.tootoo.db.egrocery.tosorderlot.TOsOrderLotPO;
import cn.tootoo.enums.BooleanEnum;
import cn.tootoo.soa.base.enums.DeliveryTypeEnum;
import cn.tootoo.soa.base.enums.LotTypeEnum;
import cn.tootoo.soa.base.enums.OrderFromEnum;
import cn.tootoo.soa.base.enums.OrderTypeEnum;
import cn.tootoo.utils.Log;
import cn.tootoo.utils.StringUtil;

/**
 * Description:<br>
 * 
 * @author nekohy
 * @version 1.0
 */
public class InitOrderLot {
    
    public synchronized int initOrderLot(Logger logger, String uuid, Long substationId, String receiveDt, TOsOrderLotDao orderLotDao) throws Exception {
        java.text.DecimalFormat df = new java.text.DecimalFormat("00");
        
        TOsOrderLotPO lot = null;
        List<TOsOrderLotPO> lots = new ArrayList<TOsOrderLotPO>();
        if (!this.isDate(receiveDt) || receiveDt.length() != 8) {
            return 0;
        }
        
        for (LotTypeEnum lotType : LotTypeEnum.values()) {// 波次类型
            if (!isExist(logger, uuid, orderLotDao, substationId, receiveDt, lotType.getC())) {// 已存在的波次不初始化
                Timestamp createTime = new Timestamp(System.currentTimeMillis());
                
                lot = new TOsOrderLotPO();
                lot.setLotSubstation(substationId + "1");
                lot.setLotDate(receiveDt);
                lot.setLotType(lotType.getC());
                lot.setLotNum(1L);
                lot.setLotCode(lot.getLotSubstation() + lot.getLotDate().substring(2) + "1" + lot.getLotType() + df.format(lot.getLotNum()));
                lot.setMaxNum(Long.valueOf(lotType.getN()));
                lot.setCurrNum(0L);
                lot.setCreateTime(createTime);
                lot.setStatus(BooleanEnum.TRUE.getV());
                lots.add(lot);
                
                lot = new TOsOrderLotPO();
                lot.setLotSubstation(substationId + "1");
                lot.setLotDate(receiveDt);
                lot.setLotType(lotType.getC());
                lot.setLotNum(Long.valueOf(lotType.getLotMaxNum()));
                lot.setLotCode(lot.getLotSubstation() + lot.getLotDate().substring(2) + "1" + lot.getLotType() + df.format(lot.getLotNum()));
                lot.setMaxNum(Long.valueOf(lotType.getN()));
                lot.setCurrNum(0L);
                lot.setCreateTime(createTime);
                lot.setStatus(BooleanEnum.TRUE.getV());
                lots.add(lot);
            }
            
        }
        
        if(lots.isEmpty()){
            return 0;
        }
        int resultNum = orderLotDao.addTOsOrderLotPOList(lots);
        if(resultNum==lots.size()){
            orderLotDao.commit();
            return resultNum;
        }else{
            orderLotDao.rollback();
            return -1;
        }
    }
    
    
    public LotTypeEnum getOrderLotTypeEnum(String deliveryTimeType, String orderType, String deliveryType, String orderFrom, boolean purchaseFlag) {
        
        // 电子卡单独一个波次
        if (OrderTypeEnum.ONLINE_CARD_VIRTUAL.getC().equals(orderType)) {// 电子卡
            return LotTypeEnum.DIANZIKA;
        }
        
        // 当日达单独一个波次
        if ("1".equals(deliveryTimeType)) {
            return LotTypeEnum.DANGRIDA;
        }
        
        // 下架促销订单,内部销售,内部订单
        if (OrderTypeEnum.PULLOFF_SHELVES.getC().equals(orderType)
                        || OrderTypeEnum.NEIBU_SALE.getC().equals(orderType)
                        || OrderTypeEnum.NEIBU_ORDER.getC().equals(orderType)) {
            return LotTypeEnum.DAIKEXIADAN;
        }
        
        // 团购包邮订单,接龙团购
        if (OrderTypeEnum.BAOYOU_TUANGOU_ORDER.getC().equals(orderType) || OrderTypeEnum.JIELONG_GROUPBUY.getC().equals(orderType)) {
            return LotTypeEnum.BAOYOU;
        }
        
        
        if (DeliveryTypeEnum.TTCN.getC().equals(deliveryType)) { //沱沱自营配送
            if (OrderTypeEnum.NORMAL.getC().equals(orderType) && !OrderFromEnum.isOuterOrder(orderFrom) && !purchaseFlag) {
                // 订单类型为“普通订单”且订单来源不为“京东订单”、“天猫订单”、“微信订单”的订单中，
                // 并且订单商品中不包含预定商品或者设置增量阀值的订单归入普通波次。
                return LotTypeEnum.NORMAL;
            }
            
            if (OrderTypeEnum.NORMAL.getC().equals(orderType) && !OrderFromEnum.isOuterOrder(orderFrom) && purchaseFlag) {
                // 订单类型为“普通订单”且订单来源不为“京东订单”、“天猫订单”、“微信订单”的订单中，
                // 并且订单商品中包含预定商品或者设置增量阀值的订单，归入预订商品波次。
                return LotTypeEnum.PURCHASE;
            }
            
            if ((OrderTypeEnum.DAIKE_ORDER.getC().equals(orderType) || OrderTypeEnum.GIFT_CARD_ORDER.getC().equals(orderType)
                            || OrderTypeEnum.LOTTERY_ORDER.getC().equals(orderType) || OrderTypeEnum.SCORE_ORDER.getC().equals(orderType))
                            && !purchaseFlag) {
                // 订单类型为“代客下单”、“礼品卡订单”、“抽奖订单”、“积分兑换订单”的订单中，
                // 并且订单商品中不包含预定商品或者设置增量阀值的订单归入普通波次。
                return LotTypeEnum.NORMAL;
            }
            
            if ((OrderTypeEnum.DAIKE_ORDER.getC().equals(orderType) || OrderTypeEnum.GIFT_CARD_ORDER.getC().equals(orderType)
                            || OrderTypeEnum.LOTTERY_ORDER.getC().equals(orderType) || OrderTypeEnum.SCORE_ORDER.getC().equals(orderType))
                            && purchaseFlag) {
                // 订单类型为“代客下单”、“礼品卡订单”、“抽奖订单”、“积分兑换订单”的订单中，
                // 并且订单商品中包含预定商品或者设置增量阀值的订单，归入预订商品波次。
                return LotTypeEnum.PURCHASE;
            }
            
            if(OrderTypeEnum.TIHUO_ORDER.getC().equals(orderType) || OrderTypeEnum.VIRTUAL_TIHUO_ORDER.getC().equals(orderType)){
                // 订单类型为“提货订单”（实物提货订单、虚拟提货订单），则把订单归入提货波次。
                return LotTypeEnum.TIHUO;
            }
            
            if (OrderTypeEnum.NORMAL.getC().equals(orderType) && OrderFromEnum.isOuterOrder(orderFrom)) {
                // 订单类型为“普通订单”且订单来源为“京东订单”、“天猫订单”、“微信订单”的订单，则直接把该订单归入第三方平台波次。
                return LotTypeEnum.OUTERORDER;
            }
        }
        
        
        if (DeliveryTypeEnum.THIRD.getC().equals(deliveryType)) { //第三方配送
            if (OrderTypeEnum.NORMAL.getC().equals(orderType) || OrderTypeEnum.DAIKE_ORDER.getC().equals(orderType)
                            || OrderTypeEnum.TIHUO_ORDER.getC().equals(orderType) || OrderTypeEnum.VIRTUAL_TIHUO_ORDER.getC().equals(orderType)
                            || OrderTypeEnum.GIFT_CARD_ORDER.getC().equals(orderType) || OrderTypeEnum.LOTTERY_ORDER.getC().equals(orderType)
                            || OrderTypeEnum.SCORE_ORDER.getC().equals(orderType)){
                // 订单类型为“普通订单”、“代客下单”、“实物提货订单”、“虚拟提货订单”、“礼品卡订单”、“抽奖订单”、“积分兑换订单”，则把订单归入第三方配送波次。
                return LotTypeEnum.SANFANG;
            }
        }
        
        
        return null;
    }
    
    private boolean isDate(String date) {
        
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
            dateFormat.setLenient(false);
            dateFormat.parse(date);
            return true;
        } catch (ParseException e) {
            
        }
        return false;
        
    }
    
    private boolean isExist(Logger logger, String uuid, TOsOrderLotDao orderLotDao, Long substationId, String receiveDt, String lotType) {
        
        List<Object[]> condition = new ArrayList<Object[]>();
        condition.add(new Object[]{"STATUS", "=", "1"});
        condition.add(new Object[]{"LOT_SUBSTATION", "=", substationId + "1"});
        condition.add(new Object[]{"LOT_DATE", "=", receiveDt});
        condition.add(new Object[]{"LOT_TYPE", "=", lotType});
        Log.info(logger, uuid, "获得条件", "condition", StringUtil.transferObjectList(condition));

        List<TOsOrderLotPO> lotList = orderLotDao.findTOsOrderLotPOListByCondition(condition, false, false, true);
        
        if (lotList == null || lotList.isEmpty()) { return false; }
        return true;
        
    }
    
    public synchronized String getOrderLotCode(Logger logger, String uuid, Long substationId, String receiveDt, LotTypeEnum lotTypeEnum, TOsOrderLotDao orderLotDao) {
        List<Object[]> condition = new ArrayList<Object[]>();
        condition.add(new Object[]{"STATUS", "=", "1"});
        condition.add(new Object[]{"LOT_SUBSTATION", "=", substationId + "1"});
        condition.add(new Object[]{"LOT_DATE", "=", receiveDt});
        condition.add(new Object[]{"LOT_TYPE", "=", lotTypeEnum.getC()});
        Log.info(logger, uuid, "获得条件", "condition", StringUtil.transferObjectList(condition));
        List<TOsOrderLotPO> lotList = orderLotDao.findTOsOrderLotPOListByCondition(condition, false, false, true);

        
        TOsOrderLotPO newOrderLotPO = new TOsOrderLotPO(); //最新波次，不包含最大波次99
        TOsOrderLotPO maxOrderLotPO = new TOsOrderLotPO(); //最大波次99
        Long newLotNum = 0L;
        for (TOsOrderLotPO orderLotPO : lotList) {
            Long lotNum = orderLotPO.getLotNum();
            if (lotNum > newLotNum && lotNum != lotTypeEnum.getLotMaxNum()) {
                newOrderLotPO = orderLotPO;
                newLotNum = lotNum;
            }
            if (lotNum == lotTypeEnum.getLotMaxNum()) {
                maxOrderLotPO = orderLotPO;
            }
        }
        Log.info(logger, uuid, "最新波次", "newOrderLotPO", newOrderLotPO);
        Log.info(logger, uuid, "最大波次", "maxOrderLotPO", maxOrderLotPO);
        
        if (newLotNum == (lotTypeEnum.getLotMaxNum() - 1) && newOrderLotPO.getCurrNum().compareTo(newOrderLotPO.getMaxNum()) >= 0) {
            // 如果最新波次是98，且该波次的订单数已满，则返回最大波次99
            return maxOrderLotPO.getLotCode();
        } else if (newOrderLotPO.getCurrNum().compareTo(newOrderLotPO.getMaxNum()) >= 0){
            // 如果最新波次订单数已满，且最新波次不是98，则插入一条新的波次号并返回
            TOsOrderLotPO lot = new TOsOrderLotPO();
            lot.setLotSubstation(newOrderLotPO.getLotSubstation());
            lot.setLotDate(newOrderLotPO.getLotDate());
            lot.setLotType(newOrderLotPO.getLotType());
            lot.setLotNum(newOrderLotPO.getLotNum() + 1);
            
            // 分站库房+日期+波次类型+波次序号(eg:11+150121+11+99)
            java.text.DecimalFormat df = new java.text.DecimalFormat("00");
            lot.setLotCode(lot.getLotSubstation() + lot.getLotDate().substring(2) + "1" + lot.getLotType() + df.format(lot.getLotNum()));
            lot.setMaxNum(newOrderLotPO.getMaxNum());
            lot.setCurrNum(0L);
            lot.setCreateTime(new Timestamp(System.currentTimeMillis()));
            lot.setStatus(BooleanEnum.TRUE.getV());
            
            int resultNum = orderLotDao.addTOsOrderLotPO(lot);
            if (resultNum == 1){
                orderLotDao.commit();
                return lot.getLotCode();
            }else{
                orderLotDao.rollback();
                return "";
            }
        }else{
            return newOrderLotPO.getLotCode();
        }
        
    }
    
    
}

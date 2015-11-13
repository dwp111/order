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

import cn.tootoo.enums.BooleanEnum;
import cn.tootoo.soa.base.bean.GiftItemsBean;
import cn.tootoo.soa.base.bean.GoodsNumberBean;
import cn.tootoo.soa.base.enums.OrderTypeEnum;
import cn.tootoo.soa.base.enums.TimeShipTypeEnum;
import cn.tootoo.soa.goods.getgoodsinfo.output.GoodsGetGoodsInfoGoodsInfoElementO;
import cn.tootoo.soa.goods.getgoodsinfo.output.GoodsGetGoodsInfoSendDaysElementO;
import cn.tootoo.soa.shipping.getgoodsshippingfee.input.ShippingGetGoodsShippingFeeGiftItemsElementI;
import cn.tootoo.soa.shipping.getgoodsshippingfee.input.ShippingGetGoodsShippingFeeGoodsListElementI;
import cn.tootoo.soa.shipping.getgoodsshippingfee.input.ShippingGetGoodsShippingFeeInputData;
import cn.tootoo.soa.shipping.getgoodsshippinginfos.input.ShippingGetGoodsShippingInfosGeoListElementI;
import cn.tootoo.soa.shipping.getgoodsshippinginfos.input.ShippingGetGoodsShippingInfosGiftItemsElementI;
import cn.tootoo.soa.shipping.getgoodsshippinginfos.input.ShippingGetGoodsShippingInfosGoodsListElementI;
import cn.tootoo.soa.shipping.getgoodsshippinginfos.input.ShippingGetGoodsShippingInfosInputData;
import cn.tootoo.soa.shipping.getgoodsshippinginfos.input.ShippingGetGoodsShippingInfosShippingDatesElementI;
import cn.tootoo.utils.Log;
import cn.tootoo.utils.StringUtil;

public class BaseRPC {

    protected String uuid;
    
    protected Logger logger;
    
    public BaseRPC(String uuid, Logger logger){
        this.uuid = uuid;
        this.logger = logger;
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
            if (entry.getValue().getBuyNumber() > 0 || entry.getValue().getExNumber() > 0) {// 不是纯赠品
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
            
            if (sendDateList.isEmpty()) {// 不可同天送
                return null;
            }
            
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
            if (dateSet.isEmpty()) {// 非当日达的去掉当天的日期，如果为空，不可同天送
                return null;
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
     * Description:<br>
     * 
     * @param isGiftOK
     * @param giftGoodsId
     * @param scope
     * @param orderType
     * @param timeShipType
     * @param substationID
     * @param geoID
     * @param orderFee
     * @param goodsCount
     * @param goodsInfo
     * @return
     * @throws Exception
     */
    public ShippingGetGoodsShippingFeeInputData getGoodsItemsShipfee(boolean isGiftOK, Long giftGoodsId, String scope, String orderType, String timeShipType, Long substationID, Long geoID, BigDecimal orderFee, HashMap<Long, GoodsNumberBean> goodsCount, Map<Long, GoodsGetGoodsInfoGoodsInfoElementO> goodsInfo) throws Exception {
        
        ShippingGetGoodsShippingFeeInputData shippingFeeInputData = new ShippingGetGoodsShippingFeeInputData();
        
        shippingFeeInputData.setScope(scope);
        shippingFeeInputData.setOrderType(orderType);
        shippingFeeInputData.setTimeShipType(timeShipType);// 查询当日达，前台通过日期判断是否显示服务费
        shippingFeeInputData.setSubstationID(substationID);
        shippingFeeInputData.setGeoID(geoID);
        
        boolean b = false;
        GoodsNumberBean beanGift = null;
        Log.info(logger, uuid, "测试信息11", "goodsCount", goodsCount);
        if (isGiftOK) {
            if (goodsCount.containsKey(giftGoodsId)) {
                b = true;
                goodsCount.get(giftGoodsId).setGiftNumber(goodsCount.get(giftGoodsId).getGiftNumber() - 1);
                if (goodsCount.get(giftGoodsId).getBuyNumber() <= 0
                                && goodsCount.get(giftGoodsId).getGiftNumber() <= 0) {
                    beanGift = goodsCount.get(giftGoodsId);
                    goodsCount.remove(giftGoodsId);
                }
            }
        }
        Log.info(logger, uuid, "测试信息12", "goodsCount", goodsCount);
        for (Map.Entry<Long, GoodsNumberBean> entry : goodsCount.entrySet()) {
            
            ShippingGetGoodsShippingFeeGoodsListElementI goodsListElement = new ShippingGetGoodsShippingFeeGoodsListElementI();
            goodsListElement.setGoodsID(entry.getKey());
            goodsListElement.setGoodsBuyNum(BigDecimal.valueOf(entry.getValue().getBuyNumber()
                            + entry.getValue().getGiftNumber()
                            + entry.getValue().getExNumber()));
            goodsListElement.setShippingModeID("0".equals(entry.getValue().getGiftType())?null:goodsInfo.get(entry.getKey()).getShippingModeID());
            goodsListElement.setGiftType(entry.getValue().getGiftType());
            if (entry.getValue().getGiftItems() != null
                            && !entry.getValue().getGiftItems().isEmpty()) {// 礼盒
                goodsListElement.setIsGift(BooleanEnum.TRUE.getV());
                for (GiftItemsBean bean : entry.getValue().getGiftItems()) {
                    ShippingGetGoodsShippingFeeGiftItemsElementI giftItemElement = new ShippingGetGoodsShippingFeeGiftItemsElementI();
                    giftItemElement.setGoodsID(bean.getGoodsId());
                    giftItemElement.setGoodsWeight(goodsInfo.get(bean.getGoodsId()).getSkuInfo().getUnitWeight());
                    giftItemElement.setShippingModeID(goodsInfo.get(bean.getGoodsId()).getShippingModeID());
                    giftItemElement.setAmount(BigDecimal.valueOf(bean.getCount()));
                    goodsListElement.getGiftItems().add(giftItemElement);
                }
            } else {
                goodsListElement.setIsGift(BooleanEnum.FALSE.getV());
                goodsListElement.setGoodsWeight(goodsInfo.get(entry.getKey()).getSkuInfo().getUnitWeight());
            }
            shippingFeeInputData.getGoodsList().add(goodsListElement);
        }
        shippingFeeInputData.setOrderFee(orderFee);
        
        if (b) {
            Log.info(logger, uuid, "测试信息13", "goodsCount", goodsCount);
            if (goodsCount.containsKey(giftGoodsId)) {
                Log.info(logger, uuid, "测试信息14", "goodsCount", goodsCount);
                goodsCount.get(giftGoodsId).setGiftNumber(goodsCount.get(giftGoodsId).getGiftNumber() + 1);
            } else {
                Log.info(logger, uuid, "测试信息15", "goodsCount", goodsCount);
                goodsCount.put(giftGoodsId, beanGift);
                goodsCount.get(giftGoodsId).setGiftNumber(goodsCount.get(giftGoodsId).getGiftNumber() + 1);
                Log.info(logger, uuid, "测试信息16", "goodsCount", goodsCount);
            }
        }
        
        return shippingFeeInputData;
    }
}

/**
 * Title：BuyerHaveOrderService.java<br>
 * Date：2014-10-9 下午01:50:07<br>
 */
package cn.tootoo.soa.oms.buyerbuyscoreservice;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import cn.tootoo.db.egrocery.bsbuyer.BsBuyerDao;
import cn.tootoo.db.egrocery.bsbuyer.BsBuyerPO;
import cn.tootoo.db.egrocery.bsbuyerbuyfee.BsBuyerBuyFeeDao;
import cn.tootoo.db.egrocery.bsbuyerbuyfee.BsBuyerBuyFeePO;
import cn.tootoo.db.egrocery.bsbuyerscore.BsBuyerScoreDao;
import cn.tootoo.db.egrocery.bsbuyerscore.BsBuyerScorePO;
import cn.tootoo.db.egrocery.bsbuyerscorelog.BsBuyerScoreLogDao;
import cn.tootoo.db.egrocery.bsbuyerscorelog.BsBuyerScoreLogPO;
import cn.tootoo.db.egrocery.osorder.OsOrderPO;
import cn.tootoo.db.egrocery.returnpointsrule.ReturnPointsRuleDao;
import cn.tootoo.db.egrocery.returnpointsrule.ReturnPointsRulePO;
import cn.tootoo.soa.base.enums.BuyerScoreLogOptTypeEnum;
import cn.tootoo.soa.base.enums.BuyerScoreStatusEnum;
import cn.tootoo.soa.base.enums.BuyerScoreTypeEnum;
import cn.tootoo.soa.base.enums.OrderFromEnum;
import cn.tootoo.soa.base.enums.OrderTypeEnum;
import cn.tootoo.soa.base.enums.YesOrNoEnum;
import cn.tootoo.utils.DateUtil;
import cn.tootoo.utils.Log;

/**
 * Description:<br>
 * 
 * @author tenbaoxin
 * @version 1.0
 */
public class BuyerBuyScoreService {
    
    private Logger logger;
    private String uuid;
    private BsBuyerDao bsBuyerDao;
    private ReturnPointsRuleDao returnPointsRuleDao;
    private BsBuyerBuyFeeDao bsBuyerBuyFeeDao;
    private BsBuyerScoreDao bsBuyerScoreDao;
    private BsBuyerScoreLogDao bsBuyerScoreLogDao;
    
    
    public BuyerBuyScoreService(Logger logger, String uuid) {
        this.logger = logger;
        this.uuid = uuid;
        this.bsBuyerDao = new BsBuyerDao(uuid, logger);
        this.returnPointsRuleDao = new ReturnPointsRuleDao(uuid, logger);
        this.bsBuyerBuyFeeDao = new BsBuyerBuyFeeDao(uuid, logger);
        this.bsBuyerScoreDao = new BsBuyerScoreDao(uuid, logger);
        this.bsBuyerScoreLogDao = new BsBuyerScoreLogDao(uuid, logger);
    }
    
    /**
     * 更新第一次下单
     * Description:<br>
     * 
     * @param buyerId
     * @throws Exception
     */
    public void updateHaveOrder(Long buyerId) {
        List<Object[]> updateObj = new ArrayList<Object[]>();
        updateObj.add(new String[]{"HAVE_ORDER", YesOrNoEnum.YES.getC()});
        List<Object[]> condition = new ArrayList<Object[]>();
        condition.add(new String[]{"HAVE_ORDER", "<>", YesOrNoEnum.YES.getC()});
        condition.add(new Object[]{"BUYER_ID", "=", buyerId});
        bsBuyerDao.updateBsBuyerPOByCondition(updateObj, condition);
    }

    /**
     * 积分规则
     * Description:<br>
     * 
     * @param order
     * @return
     */
    public BigDecimal returnPoints(OsOrderPO order) {
        List<Object[]> condition = new ArrayList<Object[]>();
        // condition.add(new Object[]{"PROMOTIONFROM","=",order.getPromotionFrom()});
        condition.add(new Object[]{"STATUS", "=", "1"});
        condition.add(new Object[]{"START_DATE", "<=", order.getCreateDt()});
        condition.add(new Object[]{"END_DATE", ">=", order.getCreateDt()});
        
        List<ReturnPointsRulePO> list = returnPointsRuleDao.findReturnPointsRulePOListByCondition(condition);
        BigDecimal multiple = new BigDecimal(1);
        if (list != null && list.size() != 0) {
            for (ReturnPointsRulePO po : list) {
                if (po.getMultiple().compareTo(multiple) > 0) {
                    multiple = po.getMultiple();
                }
            }
        }
        return multiple;
    }

    /**
     * Description:<br>
     * 修改用户当月累计消费金额
     * 
     * @param order
     * @param buyer
     * @param amount
     * @param cardFee
     */
    public void updateBuyerBuyFee(OsOrderPO order, BsBuyerPO buyer, BigDecimal amount, BigDecimal cardFee){
        // 如果 优惠券有钱，则需要减去
        // BigDecimal cashFee = (null == order.getCouponFee()) ? order.getOrderPackfreshFee() : order.getOrderPackfreshFee().subtract(order.getCouponFee());
        // 活动优惠金额有钱，也需要减去
        // cashFee = (null == order.getDiscountFee()) ? cashFee : cashFee.subtract(order.getDiscountFee());
        BigDecimal cashFee = (cardFee == null) ? amount : amount.subtract(cardFee);
        
        List<BsBuyerBuyFeePO> list = bsBuyerBuyFeeDao.findBsBuyerBuyFeePOListBySql("buyer_id='" + buyer.getBuyerId() + "' and '"
                        + DateUtil.dateToFormatStr(new Date(), "yyyy-MM") + "'=to_char(buy_fee_date,'YYYY-MM')");
        
        if (list != null && list.size() > 0) {
            BsBuyerBuyFeePO bsBuyerBuyFeePO = list.get(0);
            
            bsBuyerBuyFeePO.setBuyerTypeId(buyer.getBuyerType());
            bsBuyerBuyFeePO.setBuyFeeDate(new Timestamp(System.currentTimeMillis()));
            if (amount.compareTo(BigDecimal.ZERO) >= 0) {
                bsBuyerBuyFeePO.setVariationTotalFee(amount); // 总消费金额（线上+线下,包括礼品卡）
            }
            bsBuyerBuyFeePO.setVariationCashFee(cashFee); // 总消费金额 - 礼品卡
            bsBuyerBuyFeePO.setVariationCardFee(cardFee); // 礼品卡金额
            
            Log.info(logger, uuid, "签收：update(加消费金额)", "buyerId", bsBuyerBuyFeePO.getBuyerId(), "variationTotalFee", bsBuyerBuyFeePO.getVariationTotalFee(), "variationCashFee", bsBuyerBuyFeePO.getVariationCashFee(), "variationCardFee", bsBuyerBuyFeePO.getVariationCardFee());
            bsBuyerBuyFeeDao.updateBsBuyerBuyFeePO(bsBuyerBuyFeePO);
            
        } else {
            BsBuyerBuyFeePO bsBuyerBuyFeePO = new BsBuyerBuyFeePO();
            
            Long id = bsBuyerBuyFeeDao.findSeqNextVal("SEQ_BS_BUYER_BUY_FEE_PK");
            bsBuyerBuyFeePO.setId(id);
            bsBuyerBuyFeePO.setBuyerId(buyer.getBuyerId());
            bsBuyerBuyFeePO.setBuyerTypeId(buyer.getBuyerType());
            bsBuyerBuyFeePO.setBuyFeeDate(new Timestamp(System.currentTimeMillis()));
            bsBuyerBuyFeePO.setTotalFee(amount); // 总消费金额（线上+线下,包括礼品卡）
            bsBuyerBuyFeePO.setCashFee(cashFee); // 总消费金额 - 礼品卡
            bsBuyerBuyFeePO.setCardFee(cardFee); // 礼品卡金额
            
            Log.info(logger, uuid, "签收：insert(加消费金额)", "buyerId", bsBuyerBuyFeePO.getBuyerId(), "variationTotalFee", bsBuyerBuyFeePO.getVariationTotalFee(), "variationCashFee", bsBuyerBuyFeePO.getVariationCashFee(), "variationCardFee", bsBuyerBuyFeePO.getVariationCardFee());
            bsBuyerBuyFeeDao.addBsBuyerBuyFeePO(bsBuyerBuyFeePO);
        }
    }
    
    /**
     * Description:<br>
     * 积分处理，添加积分
     * 
     * @param order
     * @param amount
     * @param cardFee
     */
    public void updateScore(OsOrderPO order,BigDecimal amount,BigDecimal cardFee){
        try {
            // （1） 只有“订单类型”为：“普通订单”、“代客下单”、“包邮团购订单”、“接龙团购”的订单赠送消费积分；
            // （2）“订单来源”为：“天猫订单”、“淘宝订单”、“库巴订单”、“微信订单”、“社区下单”、京东、一号店、
            // “内部销售”、“内部下单”、“下架促销” 的订单不赠送消费积分，其他来源订单均赠送消费积分
            String orderType = order.getOrderType();
            String orderFrom = order.getOrderFrom();
            Log.info(logger, uuid, "updateScore积分处理开始", "order", order, "amount", amount, "cardFee", cardFee);
            if ((!OrderTypeEnum.NORMAL.getC().equals(orderType) && !OrderTypeEnum.DAIKE_ORDER.getC().equals(orderType)
                            && !OrderTypeEnum.BAOYOU_TUANGOU_ORDER.getC().equals(orderType) && !OrderTypeEnum.JIELONG_GROUPBUY.getC().equals(orderType))
                            || OrderFromEnum.TMALL.getC().equals(orderFrom)
                            || OrderFromEnum.TAOBAO.getC().equals(orderFrom)
                            || OrderFromEnum.COO8.getC().equals(orderFrom)
                            || OrderFromEnum.WEI_XIN.getC().equals(orderFrom)
                            || OrderFromEnum.COMMUNITY.getC().equals(orderFrom)
                            || OrderFromEnum.JINGDONG.getC().equals(orderFrom)
                            || OrderFromEnum.YIHAODIAN.getC().equals(orderFrom)
                            || OrderFromEnum.NEIBU_SALE.getC().equals(orderFrom)
                            || OrderFromEnum.NEIBU_ORDER.getC().equals(orderFrom)
                            || OrderFromEnum.PULLOFF_SHELVES.getC().equals(orderFrom)) {
                // 不处理积分
                updateHaveOrder(order.getBuyerId());
            } else {
                BsBuyerPO buyer = bsBuyerDao.findBsBuyerPOByID(order.getBuyerId(), false, false, false, false);
                BigDecimal multiple = returnPoints(order);
                BigDecimal jifen = amount.multiply(multiple);
                Log.info(logger, uuid, "updateScore积分处理开始", "buyer.getBuyerBuyScore()", buyer.getBuyerBuyScore(), "jifen", jifen, "multiple", multiple);
                
                List<Object[]> updateObj = new ArrayList<Object[]>();
                updateObj.add(new String[]{"BUYER_BUY_SCORE", jifen.toString(), "+"});
                updateObj.add(new String[]{"HAVE_ORDER", YesOrNoEnum.YES.getC()});
                List<Object[]> buyerCondition = new ArrayList<Object[]>();
                buyerCondition.add(new Object[]{"BUYER_ID", "=", order.getBuyerId()});
                bsBuyerDao.updateBsBuyerPOByCondition(updateObj, buyerCondition);
                // buyer.setBuyerBuyScore(buyer.getBuyerBuyScore() + jifen.intValue());
                // buyer.setHaveOrder(YesOrNoEnum.YES.getC());
                // bsBuyerDao.updateBsBuyerPO(buyer);
                
                
                // 增加积分日志
                List<Object[]> condition = new ArrayList<Object[]>();
                condition.add(new Object[]{"BUYER_ID", "=", order.getBuyerId()});
                condition.add(new Object[]{"TYPE", "=", BuyerScoreTypeEnum.ORDER_SCORE.getC()});
                List<BsBuyerScorePO> buyerScoreList = bsBuyerScoreDao.findBsBuyerScorePOListByCondition(condition,false,false,true);
                Timestamp now = new Timestamp(DateUtil.getNow());
                BsBuyerScorePO buyerScore;
                if (buyerScoreList == null || buyerScoreList.size() == 0) {
                    buyerScore = new BsBuyerScorePO();
                    buyerScore.setBuyerId(order.getBuyerId());
                    buyerScore.setStatus(BuyerScoreStatusEnum.STATUS1.getC());
                    buyerScore.setType(BuyerScoreTypeEnum.ORDER_SCORE.getC());
                    buyerScore.setCreateDate(now);
                    buyerScore.setLastUpdateDate(now);
                    buyerScore.setBalance(jifen.longValue());
                    Long id = bsBuyerScoreDao.findSeqNextVal("SEQ_BS_BUYER_SCORE_PK");
                    buyerScore.setId(id);
                    bsBuyerScoreDao.addBsBuyerScorePO(buyerScore);
                } else {
                    buyerScore = buyerScoreList.get(0);
                    buyerScore.setBalance(buyerScore.getBalance() + jifen.longValue());
                    buyerScore.setLastUpdateDate(now);
                    bsBuyerScoreDao.updateBsBuyerScorePO(buyerScore);
                }
                BsBuyerScoreLogPO log = new BsBuyerScoreLogPO();
                log.setId(bsBuyerScoreLogDao.findSeqNextVal("SEQ_BS_BUYER_SCORE_LOG_PK"));
                log.setScoreId(buyerScore.getId());
                log.setBuyerId(order.getBuyerId());
                log.setAmount(jifen.longValue());
                log.setBalance(buyerScore.getBalance());
                log.setOptType(BuyerScoreLogOptTypeEnum.TYPE_10.getC());
                log.setOptRemark(order.getOrderCode() + " 订单消费赠送积分");
                // 操作人的IP ....
                log.setOptById(1L);
                log.setOptByName("SYSTEM");
                log.setOptTime(now);
                bsBuyerScoreLogDao.addBsBuyerScoreLogPO(log);
              
                // 修改用户当月累计消费金额
                updateBuyerBuyFee(order, buyer, amount, cardFee);
              
                Log.info(logger, uuid, "updateScore积分处理", "buyer", buyer.toJson());
                bsBuyerBuyFeeDao.commit();
                bsBuyerScoreDao.commit();
                bsBuyerScoreLogDao.commit();
            }
            bsBuyerDao.commit();
        } catch (Exception e) {
            bsBuyerDao.rollback();
            bsBuyerBuyFeeDao.rollback();
            bsBuyerScoreDao.rollback();
            bsBuyerScoreLogDao.rollback();
            Log.info(logger, uuid, "updateScore积分处理失败");
        }
   }
   
   /**
    * Description:<br>
    * 退积分处理，退积分
    * 
    * @param order
    * @param amount
    * @param cardFee
    */
    public void updateRefundScore(OsOrderPO order, BigDecimal amount, BigDecimal cardFee) {
        try {
            // （1） 只有“订单类型”为：“普通订单”、“代客下单”、“包邮团购订单”、“接龙团购”订单赠送消费积分；
            // （2）“订单来源”为：“天猫订单”、“淘宝订单”、“库巴订单”、“微信订单”、“社区下单”、京东、一号店、
            // “内部销售”、“内部下单”、“下架促销” 的订单不赠送消费积分，其他来源订单均赠送消费积分
            String orderType = order.getOrderType();
            String orderFrom = order.getOrderFrom();
            Log.info(logger, uuid, "updateScore退积分处理开始", "order", order, "amount", amount, "cardFee", cardFee);
            if ((!OrderTypeEnum.NORMAL.getC().equals(orderType) && !OrderTypeEnum.DAIKE_ORDER.getC().equals(orderType)
                            && !OrderTypeEnum.BAOYOU_TUANGOU_ORDER.getC().equals(orderType) && !OrderTypeEnum.JIELONG_GROUPBUY.getC().equals(orderType))
                            || OrderFromEnum.TMALL.getC().equals(orderFrom)
                            || OrderFromEnum.TAOBAO.getC().equals(orderFrom)
                            || OrderFromEnum.COO8.getC().equals(orderFrom)
                            || OrderFromEnum.WEI_XIN.getC().equals(orderFrom)
                            || OrderFromEnum.COMMUNITY.getC().equals(orderFrom)
                            || OrderFromEnum.JINGDONG.getC().equals(orderFrom)
                            || OrderFromEnum.YIHAODIAN.getC().equals(orderFrom)
                            || OrderFromEnum.NEIBU_SALE.getC().equals(orderFrom)
                            || OrderFromEnum.NEIBU_ORDER.getC().equals(orderFrom)
                            || OrderFromEnum.PULLOFF_SHELVES.getC().equals(orderFrom)) {
                // 不处理积分
            } else {
                BsBuyerPO buyer = bsBuyerDao.findBsBuyerPOByID(order.getBuyerId(), false, false, false, false);
                BigDecimal multiple = returnPoints(order);
                BigDecimal jifen = amount.multiply(multiple);
                
                List<Object[]> updateObj = new ArrayList<Object[]>();
                updateObj.add(new String[]{"BUYER_BUY_SCORE", jifen.negate().toString(), "-"});
                updateObj.add(new String[]{"HAVE_ORDER", YesOrNoEnum.YES.getC()});
                List<Object[]> buyerCondition = new ArrayList<Object[]>();
                buyerCondition.add(new Object[]{"BUYER_ID", "=", order.getBuyerId()});
                bsBuyerDao.updateBsBuyerPOByCondition(updateObj, buyerCondition);
                // buyer.setBuyerBuyScore(buyer.getBuyerBuyScore() - jifen.intValue());
                // bsBuyerDao.updateBsBuyerPO(buyer);
                
                // 增加积分日志
                List<Object[]> condition = new ArrayList<Object[]>();
                condition.add(new Object[]{"BUYER_ID", "=", order.getBuyerId()});
                condition.add(new Object[]{"TYPE", "=", BuyerScoreTypeEnum.ORDER_SCORE.getC()});
                List<BsBuyerScorePO> buyerScoreList = bsBuyerScoreDao.findBsBuyerScorePOListByCondition(condition);
                Timestamp now = new Timestamp(DateUtil.getNow());
                BsBuyerScorePO buyerScore;
                long shikoujifen = 0L;
                if (buyerScoreList == null || buyerScoreList.size() == 0) {
                    
                } else {
                    buyerScore = buyerScoreList.get(0);
                    buyerScore.setBalance(buyerScore.getBalance() - jifen.longValue());
                    if (buyerScore.getBalance() < 0) {
                        buyerScore.setBalance(0L);
                        shikoujifen = buyerScore.getBalance();
                    }
                    buyerScore.setLastUpdateDate(now);
                    bsBuyerScoreDao.updateBsBuyerScorePO(buyerScore);
                    BsBuyerScoreLogPO log = new BsBuyerScoreLogPO();
                    log.setId(bsBuyerScoreLogDao.findSeqNextVal("SEQ_BS_BUYER_SCORE_LOG_PK"));
                    log.setScoreId(buyerScore.getId());
                    log.setBuyerId(order.getBuyerId());
                    if (shikoujifen == 0) {
                        log.setAmount(-jifen.longValue());
                        log.setOptRemark(order.getOrderCode() + " 订单签收后退货扣除积分");
                    } else {
                        log.setAmount(-shikoujifen);
                        log.setOptRemark(order.getOrderCode() + " 订单签收后退货扣除积分，应扣除" + jifen.longValue() + "，实际扣除" + shikoujifen);
                    }
                    
                    log.setBalance(buyerScore.getBalance());
                    log.setOptType(BuyerScoreLogOptTypeEnum.TYPE_10.getC());
                    // 操作人的IP ....
                    log.setOptById(1L);
                    log.setOptByName("SYSTEM");
                    log.setOptTime(now);
                    bsBuyerScoreLogDao.addBsBuyerScoreLogPO(log);
                    bsBuyerScoreDao.commit();
                    bsBuyerScoreLogDao.commit();
                }
                
                // 退款时减去相应用户当月累计消费金额
                updateRefundBuyerBuyFee(order, buyer, amount, cardFee);
                
                Log.info(logger, uuid, "updateRefundScore积分处理", "buyer", buyer.toJson());
                bsBuyerBuyFeeDao.commit();
            }
            bsBuyerDao.commit();
        } catch (Exception e) {
            bsBuyerDao.rollback();
            bsBuyerBuyFeeDao.rollback();
            bsBuyerScoreDao.rollback();
            bsBuyerScoreLogDao.rollback();
            Log.info(logger, uuid, "updateRefundScore积分处理失败" + e.getMessage());
        }
    }
   
    /**
     * Description:<br>
     * 退款时减去相应用户当月累计消费金额
     * 
     * @param order
     * @param buyer
     * @param amount
     * @param cardFee
     */
    public void updateRefundBuyerBuyFee(OsOrderPO order, BsBuyerPO buyer, BigDecimal amount, BigDecimal cardFee) {
        // 如果 优惠券有钱，则需要减去
        // BigDecimal cashFee = (null == order.getCouponFee())?order.getOrderPackfreshFee():order.getOrderPackfreshFee().subtract(order.getCouponFee());
        // 活动优惠金额有钱，也需要减去
        // cashFee = (null == order.getDiscountFee())?cashFee:cashFee.subtract(order.getDiscountFee());
        BigDecimal cashFee = (cardFee == null) ? amount : amount.subtract(cardFee);
        
        List<BsBuyerBuyFeePO> list = bsBuyerBuyFeeDao.findBsBuyerBuyFeePOListBySql("buyer_id='" + buyer.getBuyerId() + "' and '"
                        + DateUtil.dateToFormatStr(new Date(), "yyyy-MM") + "'=to_char(buy_fee_date,'YYYY-MM')");
        
        if (list != null && list.size() > 0) {
            BsBuyerBuyFeePO bsBuyerBuyFeePO = list.get(0);
            
            bsBuyerBuyFeePO.setBuyerTypeId(buyer.getBuyerType());
            bsBuyerBuyFeePO.setBuyFeeDate(new Timestamp(System.currentTimeMillis()));
            if (amount.compareTo(BigDecimal.ZERO) >= 0){
                bsBuyerBuyFeePO.setVariationTotalFee(amount.negate()); // 总消费金额（线上+线下,包括礼品卡）
            }
            bsBuyerBuyFeePO.setVariationCashFee(cashFee.negate()); // 总消费金额 - 礼品卡
            bsBuyerBuyFeePO.setVariationCardFee(cardFee.negate()); // 礼品卡金额
            
            Log.info(logger, uuid, "退货退款：update(减退款金额)", "buyerId", bsBuyerBuyFeePO.getBuyerId(), "variationTotalFee", bsBuyerBuyFeePO.getVariationTotalFee(), "variationCashFee", bsBuyerBuyFeePO.getVariationCashFee(), "variationCardFee", bsBuyerBuyFeePO.getVariationCardFee());
            bsBuyerBuyFeeDao.updateBsBuyerBuyFeePO(bsBuyerBuyFeePO);
        } else {
            BsBuyerBuyFeePO bsBuyerBuyFeePO = new BsBuyerBuyFeePO();
            
            Long id = bsBuyerBuyFeeDao.findSeqNextVal("SEQ_BS_BUYER_BUY_FEE_PK");
            bsBuyerBuyFeePO.setId(id);
            bsBuyerBuyFeePO.setBuyerId(buyer.getBuyerId());
            bsBuyerBuyFeePO.setBuyerTypeId(buyer.getBuyerType());
            bsBuyerBuyFeePO.setBuyFeeDate(new Timestamp(System.currentTimeMillis()));
            bsBuyerBuyFeePO.setTotalFee(amount.negate()); // 总消费金额（线上+线下,包括礼品卡）
            bsBuyerBuyFeePO.setCashFee(cashFee.negate()); // 总消费金额 - 礼品卡
            bsBuyerBuyFeePO.setCardFee(cardFee.negate()); // 礼品卡金额
            
            Log.info(logger, uuid, "退货退款：insert(负退款金额)", "buyerId", bsBuyerBuyFeePO.getBuyerId(), "variationTotalFee", bsBuyerBuyFeePO.getVariationTotalFee(), "variationCashFee", bsBuyerBuyFeePO.getVariationCashFee(), "variationCardFee", bsBuyerBuyFeePO.getVariationCardFee());
            bsBuyerBuyFeeDao.addBsBuyerBuyFeePO(bsBuyerBuyFeePO);
        }
    }
    
}

/**
 * Title：ThCreateOrderBaseService.java<br>
 * Date：2015-8-6 上午11:56:57<br>
 */
package cn.tootoo.soa.oms.createorder;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import cn.tootoo.db.egrocery.bsbuyer.BsBuyerDao;
import cn.tootoo.db.egrocery.bsbuyer.BsBuyerPO;
import cn.tootoo.db.egrocery.bsbuyerlog.BsBuyerLogDao;
import cn.tootoo.db.egrocery.bsbuyerlog.BsBuyerLogPO;
import cn.tootoo.db.egrocery.bsshipaddr.BsShipAddrDao;
import cn.tootoo.db.egrocery.bsshipaddr.BsShipAddrPO;
import cn.tootoo.db.egrocery.fsgl.FsGlDao;
import cn.tootoo.db.egrocery.fsgl.FsGlPO;
import cn.tootoo.db.egrocery.ospayplan.OsPayPlanDao;
import cn.tootoo.db.egrocery.ospayplan.OsPayPlanPO;
import cn.tootoo.db.egrocery.sscat.SsCatDao;
import cn.tootoo.db.egrocery.sscat.SsCatPO;
import cn.tootoo.db.egrocery.ssgoods.SsGoodsDao;
import cn.tootoo.db.egrocery.ssgoods.SsGoodsPO;
import cn.tootoo.db.egrocery.sssku.SsSkuDao;
import cn.tootoo.db.egrocery.sssku.SsSkuPO;
import cn.tootoo.db.egrocery.sysdicdata.SysDicDataDao;
import cn.tootoo.db.egrocery.sysdicdata.SysDicDataPO;
import cn.tootoo.db.egrocery.tosparentorder.TOsParentOrderDao;
import cn.tootoo.db.egrocery.tosparentorder.TOsParentOrderPO;
import cn.tootoo.db.egrocery.tosparentorderitems.TOsParentOrderItemsDao;
import cn.tootoo.db.egrocery.tosparentorderitems.TOsParentOrderItemsPO;
import cn.tootoo.db.egrocery.tosparentorderopt.TOsParentOrderOptDao;
import cn.tootoo.db.egrocery.tosparentorderopt.TOsParentOrderOptPO;
import cn.tootoo.db.egrocery.tsyscardproduct.TSysCardProductDao;
import cn.tootoo.db.egrocery.tsyscardproduct.TSysCardProductPO;
import cn.tootoo.db.egrocery.tsysthcard.TSysThcardDao;
import cn.tootoo.db.egrocery.tsysthcard.TSysThcardPO;
import cn.tootoo.db.egrocery.tsysthcardkind.TSysThcardKindDao;
import cn.tootoo.db.egrocery.tsysthcardkind.TSysThcardKindPO;
import cn.tootoo.db.egrocery.tsysthcardorder.TSysThcardOrderDao;
import cn.tootoo.db.egrocery.tsysthcardorder.TSysThcardOrderPO;
import cn.tootoo.db.egrocery.tsysthcardorderlog.TSysThcardOrderLogDao;
import cn.tootoo.db.egrocery.tsysthcardorderlog.TSysThcardOrderLogPO;
import cn.tootoo.enums.BaseResultEnum;
import cn.tootoo.http.bean.BaseOutputBean;
import cn.tootoo.soa.base.enums.BaseOrderResultEnum;
import cn.tootoo.soa.base.enums.DeliveryTypeEnum;
import cn.tootoo.soa.base.enums.GLTypeEnum;
import cn.tootoo.soa.base.enums.OrderFromEnum;
import cn.tootoo.soa.base.enums.OrderTypeEnum;
import cn.tootoo.soa.base.enums.ParentOrderStatusEnum;
import cn.tootoo.soa.base.enums.PayStatusEnum;
import cn.tootoo.soa.base.enums.PayTypeEnum;
import cn.tootoo.soa.base.enums.ThcardOrderTypeEnum;
import cn.tootoo.soa.base.enums.ThcardStatusEnum;
import cn.tootoo.soa.base.enums.ThcardTypeEnum;
import cn.tootoo.soa.base.enums.YesOrNoEnum;
import cn.tootoo.soa.base.global.DictionaryData;
import cn.tootoo.soa.oms.createorder.output.OmsCreateOrderOutputData;
import cn.tootoo.soa.oms.createthorderboss.output.OmsCreateThOrderBossOutputData;
import cn.tootoo.soa.oms.exceptions.OmsDelegateException;
import cn.tootoo.soa.oms.utils.CalVO;
import cn.tootoo.soa.oms.utils.CalculateTypeForTHEnum;
import cn.tootoo.soa.oms.utils.CardProductThTypeEnum;
import cn.tootoo.soa.oms.utils.MathUtils;
import cn.tootoo.soa.oms.utils.SqlUtil;
import cn.tootoo.soa.oms.utils.ThcardOrderBean;
import cn.tootoo.utils.DateUtil;
import cn.tootoo.utils.Log;
import cn.tootoo.utils.MD5Util;
import cn.tootoo.utils.ResponseUtil;
import cn.tootoo.utils.StringUtil;

/**
 * Description:<br>
 * 
 * @author chuan
 * @version 1.0
 */
public class ThCreateOrderBaseService {
    
    protected String uuid;
    
    protected Logger logger;
    
    public ThCreateOrderBaseService(String uuid, Logger logger) {
        super();
        this.uuid = uuid;
        this.logger = logger;
    }
    
    public BaseOutputBean createThcardOrder(ThcardOrderBean thCardOrderBean, Map<String, Map<Long, BigDecimal>> map, String scope, BaseOutputBean outputBean, boolean isBoss, OmsCreateOrderOutputData outputDataPhp, OmsCreateThOrderBossOutputData outputDataBoss) {
        
        TSysThcardDao tSysThcardDao = new TSysThcardDao(uuid, logger);
        TSysThcardKindDao tSysThcardKindDao = new TSysThcardKindDao(uuid, logger);
        TSysCardProductDao tSysCardProductDao = new TSysCardProductDao(uuid, logger);
        TSysThcardOrderDao tSysThcardOrderDao = new TSysThcardOrderDao(uuid, logger);
        TSysThcardOrderLogDao tSysThcardOrderLogDao = new TSysThcardOrderLogDao(uuid, logger);
        BsBuyerDao bsBuyerDao = new BsBuyerDao(uuid, logger);
        BsShipAddrDao bsShipAddrDao = new BsShipAddrDao(uuid, logger);
        FsGlDao fsGlDao = new FsGlDao(uuid, logger);
        BsBuyerLogDao bsBuyerLogDao = new BsBuyerLogDao(uuid, logger);
        TOsParentOrderDao tOsParentOrderDao = new TOsParentOrderDao(uuid, logger);
        TOsParentOrderItemsDao tOsParentOrderItemsDao = new TOsParentOrderItemsDao(uuid, logger);
        TOsParentOrderOptDao tOsParentOrderOptDao = new TOsParentOrderOptDao(uuid, logger);
        OsPayPlanDao osPayPlanDao = new OsPayPlanDao(uuid, logger);
        SsGoodsDao ssGoodsDao = new SsGoodsDao(uuid, logger);
        SsSkuDao ssSkuDao = new SsSkuDao(uuid, logger);
        SsCatDao ssCatDao = new SsCatDao(uuid, logger);
        SysDicDataDao sysDicDataDao = new SysDicDataDao(uuid, logger);
        
        try {
            
            List<Object[]> thCardConditions = new ArrayList<Object[]>();
            thCardConditions.add(new Object[]{"THCARD_SN", "=", thCardOrderBean.getThCardCode()});
            thCardConditions.add(new Object[]{"STATUS", "!=", ThcardStatusEnum.DELETE.getC()});
            List<TSysThcardPO> thcardList = tSysThcardDao.findTSysThcardPOListByCondition(thCardConditions, false, false, false);
            Log.info(logger, uuid, "查询提货卡结果", "thcardList", thcardList);
            if (thcardList == null || thcardList.size() <= 0) {
                Log.info(logger, uuid, "提货卡不存在！", "thcardList", thcardList);
                outputBean = ResponseUtil.getBaseOutputBean(BaseOrderResultEnum.TH_NOT_EXISTS.getResultID(), null, scope);
                Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
                return outputBean;
            }
            TSysThcardPO thCard = thcardList.get(0);
            if (!thCard.getStatus().equals(ThcardStatusEnum.OPEN.getC()) && !thCard.getStatus().equals(ThcardStatusEnum.USE.getC())) {
                Log.info(logger, uuid, "提领次数已满，不能再提领", "thCard.getStatus()", thCard.getStatus());
                outputBean = ResponseUtil.getBaseOutputBean(BaseOrderResultEnum.TH_TIME_OVER.getResultID(), null, scope);
                Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
                return outputBean;
            }
            
            
            TSysThcardKindPO thcardKindPO = tSysThcardKindDao.findTSysThcardKindPOByID(thCardOrderBean.getThcardKdId(), false, false, false, false);        
            
            Map thCardMap = new HashMap();
            thCardMap.put("thcardKdValue", new BigDecimal(thcardKindPO.getValue()));
            
            if(StringUtil.isEmpty(thCardOrderBean.getOrderFrom())){
                TSysCardProductPO cp = tSysCardProductDao.findTSysCardProductPOByID(thCard.getCpId(), false, false, false, false);
                thCardMap.put("thType", cp.getThType());
                
                if (CardProductThTypeEnum.isOnece(cp.getThType())) {
                    // 订单来源 - 提货订单
                    thCardOrderBean.setOrderFrom(OrderFromEnum.BOSS.getC());
                } else {
                    // 订单来源 - 宅配订单
                    thCardOrderBean.setOrderFrom(OrderFromEnum.BOSS_ZP.getC());
                }
            }
            
            
            // 生成提货订单
            // createThcardOrder(thCardOrderBean, map, thCard);
            // ...创建网站父订单.......................................................................................
            BsBuyerPO bsBuyer;
            BsShipAddrPO shipAddr;
            // 网站会员
            if (thCardOrderBean.getBuyerFlag().equals(YesOrNoEnum.YES.getC())) {
                bsBuyer = bsBuyerDao.findBsBuyerPOByID(thCardOrderBean.getBuyerId(), false, false, false, false);
    
                if (null == thCardOrderBean.getShipAddrId()) {
                    List<Object[]> shipAddrConditions = new ArrayList<Object[]>();
                    shipAddrConditions.add(new Object[]{"BUYER_ID", "=", thCardOrderBean.getBuyerId()});
                    shipAddrConditions.add(new Object[]{"IS_DEFAULT", "=", "1"});
                    List<BsShipAddrPO> shipAddrList = bsShipAddrDao.findBsShipAddrPOListByCondition(shipAddrConditions, false, false, false);
                    if(shipAddrList == null || shipAddrList.size()<=0){
                        Log.info(logger, uuid, "查询网站会员默认地址为空", "shipAddrList", shipAddrList);
                        shipAddr = null;
                    }else{
                        shipAddr = shipAddrList.get(0);
                    }
                } else {
                    shipAddr = bsShipAddrDao.findBsShipAddrPOByID(thCardOrderBean.getShipAddrId(), false, false, false, false);
                }
            } else {
                // 非网站会员，即提货卡卡号为用户名
                
                List<Object[]> buyerConditions = new ArrayList<Object[]>();
                buyerConditions.add(new Object[]{"BUYER_EMAIL", "=", thCardOrderBean.getBuyerEmail()});
                List<BsBuyerPO> buyerList = bsBuyerDao.findBsBuyerPOListByCondition(buyerConditions, false, false, false);
                if (buyerList == null || buyerList.size() <= 0) {
                    bsBuyer = null;
                } else {
                    bsBuyer = buyerList.get(0);
                }
                
                if (bsBuyer == null) {
                    // ... 创建用户信息
                    bsBuyer = new BsBuyerPO();
                    Long buyerId = bsBuyerDao.findSeqNextVal("SEQ_BS_BUYER_PK");
                    if (buyerId.intValue() == -1) {
                        Log.info(logger, uuid, "获得用户主键失败", "buyerId", buyerId);
                        outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.INSIDE_ERROR.getResultID(), null, scope);
                        Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
                        return outputBean;
                    }
                    Log.info(logger, uuid, "获得用户主键成功", "buyerId", buyerId);
                    bsBuyer.setBuyerId(buyerId);
                    bsBuyer.setBuyerEmail(thCardOrderBean.getBuyerEmail());
                    bsBuyer.setKey(MathUtils.generateString(10));// 取得1个10位的随机数做key
                    bsBuyer.setBuyerPasswd(MD5Util.md5Encode(MD5Util.md5Encode(thCardOrderBean.getBuyerEmail()) + bsBuyer.getKey()));// 密码
                    bsBuyer.setRegDate(new Timestamp(System.currentTimeMillis()));
                    bsBuyer.setRegIp(thCardOrderBean.getIp());
                    bsBuyer.setBuyerStatus("1");
                    bsBuyer.setIsactived("0");
                    bsBuyer.setBuyerScore(0L);
                    bsBuyer.setBuyerBuyScore(0L);
                    bsBuyer.setBuyerLevel(2L);
                    bsBuyer.setNickname(thCardOrderBean.getBuyerEmail());// 昵称
                    bsBuyer.setBuyerType(1L); // 普通会员
                    bsBuyer.setRegFrom("boss_THCard");
                    bsBuyer.setHaveOrder("0");
                    bsBuyer.setIsSendMessage("0");
                    bsBuyer.setMobile("0");
                    bsBuyer.setInviteLevel(1);
                    bsBuyer.setBuyerFlag(0L);
                    int resultNum = bsBuyerDao.addBsBuyerPO(bsBuyer);
                    if(resultNum != 1){
                        Log.info(logger, uuid, "创建用户失败！", "resultNum", resultNum, "bsBuyer", bsBuyer);
                        bsBuyerDao.rollback();
                        bsBuyerLogDao.rollback();
                        outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.INSIDE_ERROR.getResultID(), null, scope);
                        Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
                        return outputBean;
                    }
                    // ..............................................................................................................................
    
                    // ... 日志 .......................................................
                    BsBuyerLogPO buyerLog = new BsBuyerLogPO();
                    buyerLog.setBuyerId(bsBuyer.getBuyerId());
                    buyerLog.setDt(bsBuyer.getRegDate());
                    buyerLog.setIp(thCardOrderBean.getIp());
                    buyerLog.setOptType("2");
                    buyerLog.setOptRemark("boss_THCard");
                    resultNum = bsBuyerLogDao.addBsBuyerLogPO(buyerLog);
                    if(resultNum != 1){
                        Log.info(logger, uuid, "添加用户日志失败！", "resultNum", resultNum, "buyerLog", buyerLog);
                        bsBuyerDao.rollback();
                        bsBuyerLogDao.rollback();
                        outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.INSIDE_ERROR.getResultID(), null, scope);
                        Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
                        return outputBean;
                    }
                    // ................................................................
    
                    // .. 创建账户信息 ................................................
                    // 创建 买家主账户
                    FsGlPO gl1 = new FsGlPO();
                    gl1.setGlName(GLTypeEnum.USER_ZHU.getS());
                    gl1.setUserId(bsBuyer.getBuyerId());
                    gl1.setGlAmount(new BigDecimal(0));
                    gl1.setGlType(GLTypeEnum.USER_ZHU.getC());
                    gl1.setGlStatus("1"); //正常
                    gl1.setGlRisklevel("0");
                    Log.info(logger, uuid, "买家主账户", "gl1", gl1);

                    // 创建 买家副账户
                    FsGlPO gl2 = new FsGlPO();
                    gl2.setGlName(GLTypeEnum.USER_FU.getS());
                    gl2.setUserId(bsBuyer.getBuyerId());
                    gl2.setGlAmount(new BigDecimal(0));
                    gl2.setGlType(GLTypeEnum.USER_FU.getC());
                    gl2.setGlStatus("1"); //正常
                    gl2.setGlRisklevel("0");
                    Log.info(logger, uuid, "买家副账户", "gl2", gl2);

                    // 创建 买家提现账户
                    FsGlPO gl3  = new FsGlPO();
                    gl3.setGlName(GLTypeEnum.USER_TI_XIAN.getS());
                    gl3.setUserId(bsBuyer.getBuyerId());
                    gl3.setGlAmount(new BigDecimal(0));
                    gl3.setGlType(GLTypeEnum.USER_TI_XIAN.getC());
                    gl3.setGlStatus("1"); //正常
                    gl3.setGlRisklevel("0");
                    Log.info(logger, uuid, "买家提现账户", "gl3", gl3);

                    // 创建 买家应付账户
                    FsGlPO gl4 = new FsGlPO();
                    gl4.setGlName(GLTypeEnum.USER_YING_FU.getS());
                    gl4.setUserId(bsBuyer.getBuyerId());
                    gl4.setGlAmount(new BigDecimal(0));
                    gl4.setGlType(GLTypeEnum.USER_YING_FU.getC());
                    gl4.setGlStatus("1"); //正常
                    gl4.setGlRisklevel("0");
                    Log.info(logger, uuid, "买家应付账户", "gl4", gl4);
                    
                    
                    List<FsGlPO> fsGlAddList = new ArrayList<FsGlPO>();
                    fsGlAddList.add(gl1);
                    fsGlAddList.add(gl2);
                    fsGlAddList.add(gl3);
                    fsGlAddList.add(gl4);
                    resultNum = fsGlDao.addFsGlPOList(fsGlAddList);
                    
                    if(resultNum != 4){
                        Log.info(logger, uuid, "创建买家账户失败", "resultNum", resultNum, "fsGlAddList", fsGlAddList);
                        bsBuyerDao.rollback();
                        fsGlDao.rollback();
                        outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.INSIDE_ERROR.getResultID(), null, scope);
                        Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
                        return outputBean;
                    }
                    // ................................................................
                    
                }
                
                shipAddr = new BsShipAddrPO();
                shipAddr.setAddrLine1(thCardOrderBean.getAddr());
                shipAddr.setReceiver(thCardOrderBean.getReceiver());
                shipAddr.setPostalCode(thCardOrderBean.getPos());
                shipAddr.setPhoneNumber(thCardOrderBean.getPhonenum());
                shipAddr.setMobileNumber(thCardOrderBean.getMobilenum());
                
                //添加地址信息
                String province = StringUtil.isEmpty(thCardOrderBean.getProvince()) ? "" : thCardOrderBean.getProvince();
                String city = StringUtil.isEmpty(thCardOrderBean.getCity()) ? "" : thCardOrderBean.getCity();
                String district = StringUtil.isEmpty(thCardOrderBean.getDistrict()) ? "" : thCardOrderBean.getDistrict();
                String area = StringUtil.isEmpty(thCardOrderBean.getArea()) ? "0" : thCardOrderBean.getArea();
                
                shipAddr.setProvince(Long.parseLong(province));
                shipAddr.setCity(Long.parseLong(city));
                shipAddr.setDistrict(Long.parseLong(district));
                shipAddr.setArea(Long.parseLong(area));
                
                shipAddr.setBuyerId(bsBuyer.getBuyerId());
                shipAddr.setSdcCode(-1L);
                shipAddr.setSdcId(-1L);
                shipAddr.setIsDefault(YesOrNoEnum.YES.getC());
                
                
                int resultNum = bsShipAddrDao.addBsShipAddrPO(shipAddr);
                if (resultNum != 1) {
                    Log.info(logger, uuid, "非网站会员添加地址失败", "resultNum", resultNum, "shipAddr", shipAddr);
                    bsBuyerDao.rollback();
                    bsShipAddrDao.rollback();
                    outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.INSIDE_ERROR.getResultID(), null, scope);
                    Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
                    return outputBean;
                }
                
            }
            
            // 转换VO类型，兼容预约生成父订单
            /*BsShipAddrPO shipAddrRetVO = new BsShipAddrPO();
            shipAddrRetVO.setPostalCode(shipAddr.getPostalCode());// 邮编
            shipAddrRetVO.setReceiver(shipAddr.getReceiver());// 收货人
            shipAddrRetVO.setPhoneNumber(shipAddr.getPhoneNumber());// 电话
            shipAddrRetVO.setMobileNumber(shipAddr.getMobileNumber());// 手机
            shipAddrRetVO.setShipAddrId(shipAddr.getShipAddrId());*/
            
            
            Long orderId = tOsParentOrderDao.findSeqNextVal("SEQ_OS_PARENT_ORDER_PK");
            if (orderId.intValue() == -1) {
                Log.info(logger, uuid, "获得父订单主键失败", "orderId", orderId);
                outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.INSIDE_ERROR.getResultID(), null, scope);
                Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
                return outputBean;
            }
            Log.info(logger, uuid, "获得父订单主键成功", "orderId", orderId);
            
            String orderCode = SqlUtil.getParentOrderCodeLoop(logger, uuid, tOsParentOrderDao.getWriteConnectionName());
            if (StringUtil.isEmpty(orderCode)) {
                Log.info(logger, uuid, "获得父订单的订单号失败", "orderCode", orderCode);
                outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.INSIDE_ERROR.getResultID(), null, scope);
                Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
                return outputBean;
            }
            Log.info(logger, uuid, "获得父订单的订单号", "orderCode", orderCode);
            
            TOsParentOrderPO parentOrderPo = generalParentOrderByTH(thCardOrderBean, thCard, thCardMap, bsBuyer, shipAddr, orderCode, orderId, scope);
            Log.info(logger, uuid, "获得父订单数据完成", "parentOrderPo", parentOrderPo);
            
            
            List<TOsParentOrderItemsPO> parentOrderItemsPoList = generalParentOrderItemsByTH(thCardOrderBean, map, thCard, parentOrderPo, ssGoodsDao, ssSkuDao, ssCatDao, sysDicDataDao);
            Log.info(logger, uuid, "获得父订单详情数据", "parentOrderItemsPoList", parentOrderItemsPoList);
            if (parentOrderItemsPoList == null) {
                Log.info(logger, uuid, "获得父订单详情错误");
                outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.INSIDE_ERROR.getResultID(), null, scope);
                Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
                return outputBean;
            }
            
            
            
            Log.info(logger, uuid, "插入父订单数据开始");
            int num = tOsParentOrderDao.addTOsParentOrderPO(parentOrderPo);
            if (num != 1) {
                tOsParentOrderDao.rollback();
                bsBuyerDao.rollback();
                Log.info(logger, uuid, "插入父订单失败", "num", num);
                outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.INSIDE_ERROR.getResultID(), null, scope);
                Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
                return outputBean;
            }
            Log.info(logger, uuid, "插入父订单数据结束");
            
            
            Log.info(logger, uuid, "插入父订单详情数据开始");
            num = tOsParentOrderItemsDao.addTOsParentOrderItemsPOList(parentOrderItemsPoList);
            if (num != parentOrderItemsPoList.size()) {
                tOsParentOrderItemsDao.rollback();
                bsBuyerDao.rollback();
                Log.info(logger, uuid, "插入父订单详情数据失败", "num", num);
                outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.INSIDE_ERROR.getResultID(), null, scope);
                Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
                return outputBean;
            }
            Log.info(logger, uuid, "插入父订单详情数据结束");
            
            
            
            // ... 增加订单操作日志流水
            // ..................................................................................................
            // ... 增加订单操作日志流水
            // ..................................................................................................
            Log.info(logger, uuid, "组装父订单日志表数据开始");
            TOsParentOrderOptPO opt = new TOsParentOrderOptPO();
            opt.setOrderId(parentOrderPo.getOrderId());
            opt.setOptDt(parentOrderPo.getCreateDt());
            opt.setUserId(parentOrderPo.getBuyerId());
            opt.setOrderStatus(Long.valueOf(ParentOrderStatusEnum.ORIGINAL.getStatus()));// 待审核
            opt.setRemark("This is system added!");
            opt.setIsShow("0");
            Log.info(logger, uuid, "组装父订单日志表数据结束");
            
            Log.info(logger, uuid, "插入父订单日志表开始");
            num = tOsParentOrderOptDao.addTOsParentOrderOptPO(opt);
            if (num != 1) {
                tOsParentOrderOptDao.rollback();
                bsBuyerDao.rollback();
                Log.info(logger, uuid, "插入父订单日志表失败", "num", num);
                outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.INSIDE_ERROR.getResultID(), null, scope);
                Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
                return outputBean;
            }
            Log.info(logger, uuid, "插入父订单日志表结束");
            
            
            Log.info(logger, uuid, "组装支付原始请求表数据开始");
            //订单金额
            // String amt = parentOrderPo.getGoodsFee().add(parentOrderPo.getShipFee()).toString();
            String amt = parentOrderPo.getGoodsFee().toString();
            //准备支付信息
            StringBuilder payInfo = new StringBuilder();
            payInfo.append("3_");
            payInfo.append(amt + "_");
            payInfo.append(thCardOrderBean.getThCardCode() + "#" + thCardOrderBean.getGiftBoxId() + "|" + parentOrderPo.getGoodsFee());
            //创建支付计划对象
            OsPayPlanPO payPlan = new OsPayPlanPO();
            payPlan.setOrderId(parentOrderPo.getOrderId());
            payPlan.setBuyerId(parentOrderPo.getBuyerId());
            payPlan.setMarketId(-1L);
            payPlan.setOrderType(parentOrderPo.getOrderType());
            payPlan.setPayInfo(payInfo.toString());
            //payPlan.setPlanAmt(parentOrderPo.getGoodsFee().add(parentOrderPo.getShipFee()));
            payPlan.setPlanAmt(parentOrderPo.getGoodsFee());
            payPlan.setCreateDate(new Timestamp(System.currentTimeMillis()));
            //保存支付计划
            Log.info(logger, uuid, "插入支付原始请求表开始");
            num = osPayPlanDao.addOsPayPlanPO(payPlan);
            if (num != 1) {
                osPayPlanDao.rollback();
                bsBuyerDao.rollback();
                Log.info(logger, uuid, "插入原始支付计划表失败", "num", num);
                outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.INSIDE_ERROR.getResultID(), null, scope);
                Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
                return outputBean;
            }
            Log.info(logger, uuid, "插入支付原始请求表结束");
    
            //回写数据
            /*thCardOrderBean.setOrderID(parentOrderPo.getOrderId().intValue());
            thCardOrderBean.setOrderCode(parentOrderPo.getOrderCode());
            thCardOrderBean.setPayInfo(payInfo.toString());
            thCardOrderBean.setSendDate(DateUtil.dateToStr(parentOrderPo.getReceiveDt()));
            */
            
            
            // 创建提货卡与礼盒的关系
            Long thCardOrderId = tSysThcardOrderDao.findSeqNextVal("SEQ_T_SYS_THCARD_ORDER_PK");
            if (thCardOrderId.intValue() == -1) {
                Log.info(logger, uuid, "获得提货卡与礼盒关系表主键失败", "thCardOrderId", thCardOrderId);
                outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.INSIDE_ERROR.getResultID(), null, scope);
                Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
                return outputBean;
            }
            Log.info(logger, uuid, "获得提货卡与礼盒的关系主键成功", "thCardOrderId", thCardOrderId);
            TSysThcardOrderPO thcardOrderPO = new TSysThcardOrderPO();
            thcardOrderPO.setId(thCardOrderId);
            thcardOrderPO.setBuyerId(thCardOrderBean.getBuyerId());
            thcardOrderPO.setBuyerEmail(thCardOrderBean.getBuyerEmail());
            thcardOrderPO.setType(ThcardOrderTypeEnum.BOSS.getC());
            thcardOrderPO.setThTime(new Timestamp(System.currentTimeMillis()));
            thcardOrderPO.setThIp(thCardOrderBean.getIp());
            thcardOrderPO.setThcardKdId(thCardOrderBean.getThcardKdId());
            thcardOrderPO.setThcardSn(thCardOrderBean.getThCardCode());
            thcardOrderPO.setOrderSn(parentOrderPo.getOrderCode());
            thcardOrderPO.setOrderId(parentOrderPo.getOrderId());
            thcardOrderPO.setGiftboxId(Long.valueOf(thCardOrderBean.getGiftBoxId()));
            
            Log.info(logger, uuid, "插入提货订单关系表开始");
            num = tSysThcardOrderDao.addTSysThcardOrderPO(thcardOrderPO);
            if (num != 1) {
                tSysThcardOrderDao.rollback();
                bsBuyerDao.rollback();
                Log.info(logger, uuid, "插入提货订单关系表失败", "num", num);
                outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.INSIDE_ERROR.getResultID(), null, scope);
                Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
                return outputBean;
            }
            Log.info(logger, uuid, "插入提货订单关系表结束");
            
            // ...提货订单日志...
            Long thCardOrderLogId = tSysThcardOrderDao.findSeqNextVal("SEQ_T_SYS_THCARD_ORDER_LOG_PK");
            if (thCardOrderLogId.intValue() == -1) {
                Log.info(logger, uuid, "获得提货卡与礼盒关系日志表主键失败", "thCardOrderLogId", thCardOrderLogId);
                outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.INSIDE_ERROR.getResultID(), null, scope);
                Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
                return outputBean;
            }
            Log.info(logger, uuid, "获得提货卡与礼盒关系日志表主键成功", "thCardOrderLogId", thCardOrderLogId);
            TSysThcardOrderLogPO thcardOrderLog = new TSysThcardOrderLogPO();
            thcardOrderLog.setLogId(thCardOrderLogId);
            thcardOrderLog.setOpByUser(thCardOrderBean.getBuyerId());
            thcardOrderLog.setOpIp(thCardOrderBean.getIp());
            thcardOrderLog.setOpTime(new Timestamp(System.currentTimeMillis()));
            thcardOrderLog.setThcardOrderId(thcardOrderPO.getId());
            thcardOrderLog.setCardId(thCard.getId());
            thcardOrderLog.setOrderId(parentOrderPo.getOrderId());
            thcardOrderLog.setOrderStatus(parentOrderPo.getOrderStatus());
            Log.info(logger, uuid, "插入提提货订单日志间表开始");
            num = tSysThcardOrderLogDao.addTSysThcardOrderLogPO(thcardOrderLog);
            if (num != 1) {
                tSysThcardOrderLogDao.rollback();
                bsBuyerDao.rollback();
                Log.info(logger, uuid, "插入提提货订单日志间表失败", "num", num);
                outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.INSIDE_ERROR.getResultID(), null, scope);
                Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
                return outputBean;
            }
            Log.info(logger, uuid, "插入提提货订单日志间表结束");
            
            
            
    
            // ...........................................................................................................................
            tOsParentOrderDao.commit();
            bsBuyerDao.commit();
            
            if (isBoss) {
                outputDataBoss.setOrderID(parentOrderPo.getOrderId());
                outputDataBoss.setOrderCode(parentOrderPo.getOrderCode());
                outputDataBoss.setSendDate(DateUtil.dateToStr(parentOrderPo.getReceiveDt()));
                outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.SUCCESS.getResultID(), outputDataBoss, scope);
            } else {
                outputDataPhp.setOrderID(parentOrderPo.getOrderId());
                outputDataPhp.setOrderCode(parentOrderPo.getOrderCode());
                outputDataPhp.setPayInfo(thCardOrderBean.getPayInfo());
                outputDataPhp.setSendDate(DateUtil.dateToStr(parentOrderPo.getReceiveDt()));
                outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.SUCCESS.getResultID(), outputDataPhp, scope);
            }

            Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
            return outputBean;
        
        } catch (Exception e) {
            bsBuyerDao.rollback();
            tOsParentOrderDao.rollback();
            Log.error(logger, "boss后台下提货订单失败", e);
            outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.INSIDE_ERROR.getResultID(), null, scope);
            Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
            return outputBean;
        }
    }
    
    
    
    
    
    
    
    
    
    
    /**
     * 生成提货订单的网站订单
     * @param vo
     * @param card
     * @param buyer
     * @param shipAddr
     * @return
     * @throws Exception
     */
    private TOsParentOrderPO generalParentOrderByTH(ThcardOrderBean thCardOrderBean, TSysThcardPO thCard, Map thCardMap, BsBuyerPO buyer, BsShipAddrPO shipAddr, String orderCode, Long orderId, String scope) throws Exception {
        // ... 创建订单对象
        // .............................................................................................
        TOsParentOrderPO parentOrder = new TOsParentOrderPO();
        
        parentOrder.setOrderId(orderId);

        parentOrder.setCreateDt(new Timestamp(System.currentTimeMillis()));// 创建时间
        parentOrder.setCreateIp(thCardOrderBean.getIp());// 创建IP
        parentOrder.setOrderStatus(Long.valueOf(ParentOrderStatusEnum.ORIGINAL.getStatus()));// 订单状态：待审核
        
        BigDecimal thcardKdValue = (BigDecimal)thCardMap.get("thcardKdValue");
        parentOrder.setGoodsFee(thcardKdValue);// 商品总金额
        parentOrder.setGoodsCallFee(thcardKdValue);
        parentOrder.setShipFee(new BigDecimal(thCardOrderBean.getShipfee()));// 运费
        parentOrder.setShipCallFee(new BigDecimal(thCardOrderBean.getShipfee()));

        // parentOrder.setOrderFee(thcardKdValue.add(parentOrder.getShipFee()));
        parentOrder.setOrderFee(thcardKdValue);
        // parentOrder.setOrderCallFee(thcardKdValue.add(parentOrder.getShipFee()));// 订单总金额
        parentOrder.setOrderCallFee(thcardKdValue);// 订单总金额
//        parentOrder.setOrderFinalFee(thcardKdValue.add(parentOrder.getShipFee()));// 最终金额
        parentOrder.setOnlinePayFee(new BigDecimal(0));
//        parentOrder.setOfflineRecvCash(new BigDecimal(0));
//        parentOrder.setScoreFee(new BigDecimal(0));
        parentOrder.setPromotionId(0L);
        parentOrder.setPayStatus(PayStatusEnum.NO.getC());// 支付状态: 未支付
        //...........................................................................

        //...支付方法 和 订单类型.........................................................
        String payingMethod = "";
        String orderType = "";
        if(thCard.getType().equals(ThcardTypeEnum.VIRTUAL.getC())){
            payingMethod = payingMethod + "虚拟提货卡:";
            orderType = OrderTypeEnum.VIRTUAL_TIHUO_ORDER.getC();
        }else{
            payingMethod = payingMethod + "实物提货卡:";
            orderType = OrderTypeEnum.TIHUO_ORDER.getC();
        }
        parentOrder.setPayingMethod(payingMethod + thCardOrderBean.getThCardCode()+"(￥"+parentOrder.getOrderCallFee()+")");// 支付方法
        parentOrder.setOrderType(orderType);// 订单类型:提货订单

        //...........................................................................

        parentOrder.setVipflag("0");// VIP标识
        // parentOrder.setNeedPayFee(thcardKdValue.add(parentOrder.getShipFee()));
        parentOrder.setNeedPayFee(thcardKdValue);
//        parentOrder.setScanningStatus("0");
        parentOrder.setOfflinePayFee(BigDecimal.ZERO);
        parentOrder.setPayType(PayTypeEnum.ONLINE.getC());// 支付方式
//        parentOrder.setOutstockFee(new BigDecimal(0));// 出库金额
//        parentOrder.setReturnFee(new BigDecimal(0));// 退货金额
//        parentOrder.setRecvCashStatus("0");
        parentOrder.setIsEnOrder("0");
        parentOrder.setCouponFee(new BigDecimal(0));

        parentOrder.setOrderFrom(thCardOrderBean.getOrderFrom());//订单来源

        if(null == thCardOrderBean.getSpecifiedShippingDate()){
            parentOrder.setDeliveryType(DeliveryTypeEnum.THIRD.getC());
        }else {
            Map<Long, String> shippingCompanyTypeMap = DictionaryData.shippingCompanyTypeMap;
            if(shippingCompanyTypeMap == null || shippingCompanyTypeMap.get(Long.valueOf(thCardOrderBean.getDcId())) == null){
                Log.info(logger, uuid, "shippingCompanyTypeMap不存在或dcId不存在", "dcId", thCardOrderBean.getDcId(), "shippingCompanyTypeMap", shippingCompanyTypeMap);
                throw new OmsDelegateException(BaseResultEnum.INSIDE_ERROR.getResultID(), "shippingCompanyTypeMap不存在或dcId不存在！", null);
            }
            parentOrder.setDeliveryType(shippingCompanyTypeMap.get(Long.valueOf(thCardOrderBean.getDcId())));
        }
        
        parentOrder.setReceiveDt(DateUtil.strToTimestamp(thCardOrderBean.getPlanDate()));// 订单收获日期
        parentOrder.setSpecifiedShippingdate(thCardOrderBean.getSpecifiedShippingDate());//是否考虑运力
        //***********************************************************************************************************

//        parentOrder.setDcId(thCardOrderBean.getDcId());
//        parentOrder.setSsspId(0);// 提货订单没有分运力，此字段设为0
        parentOrder.setRemark(thCardOrderBean.getRemark());
        parentOrder.setSdcId(-1L);// SDCID

        ////
        //modify：提货订单异地配，从页面取值
        StringBuilder addr = new StringBuilder(thCardOrderBean.getAddr());
        if (!StringUtil.isEmpty(thCardOrderBean.getAreaName())) {
            addr.insert(0, thCardOrderBean.getAreaName());
        }
        if (!StringUtil.isEmpty(thCardOrderBean.getDistrictName())) {
            addr.insert(0, thCardOrderBean.getDistrictName());
        }
        if (!StringUtil.isEmpty(thCardOrderBean.getCityName())) {
            addr.insert(0, thCardOrderBean.getCityName());
        }
        if (!StringUtil.isEmpty(thCardOrderBean.getProvinceName())) {
            addr.insert(0, thCardOrderBean.getProvinceName());
        }
        parentOrder.setShipAddr(addr.toString());
        ////

        parentOrder.setZipcode(shipAddr.getPostalCode());// 邮编
        parentOrder.setReceiver(shipAddr.getReceiver());// 收货人
        parentOrder.setTel(shipAddr.getPhoneNumber());// 电话
        parentOrder.setMobile(shipAddr.getMobileNumber());// 手机
        parentOrder.setShipProvince(thCardOrderBean.getProvinceName());//省名称
        parentOrder.setShipCity(thCardOrderBean.getCityName());//市名称
        parentOrder.setShipDistrict(thCardOrderBean.getDistrictName());//区名称

        // //
        // Add by zhangyu 2011-12-02
        // cuz: 添加“买家收货地址信息ID”
        parentOrder.setAddrId(shipAddr.getShipAddrId());
        // //

        parentOrder.setBuyerId(buyer.getBuyerId());

        parentOrder.setOrderCode(orderCode);
        parentOrder.setSplitStatus(0L);
        parentOrder.setEmail(buyer.getBuyerEmail());
        parentOrder.setSubstationId(thCard.getSubstationId());//分站id
        parentOrder.setScope(scope);//调用平台标识
        parentOrder.setReceiveTimeSeg("00:00-24:00");
        
        
        parentOrder.setDeliveryTimeType("0");
        parentOrder.setDiscountFee(BigDecimal.ZERO);
        parentOrder.setIsFirst("0");
        parentOrder.setReceiptShowAmount("1");
        parentOrder.setOrderCount(0L);
        parentOrder.setChgPay("1");
        
        // ..............................................................................................................
        return parentOrder;
    }
    
    
    
    
    private List<TOsParentOrderItemsPO> generalParentOrderItemsByTH(ThcardOrderBean thCardOrderBean, Map<String, Map<Long, BigDecimal>> map, TSysThcardPO thCard, TOsParentOrderPO parentOrderPo, SsGoodsDao ssGoodsDao, SsSkuDao ssSkuDao, SsCatDao ssCatDao, SysDicDataDao sysDicDataDao) throws Exception  {
        // ... 创建订单明细 ...............................................................................

        //礼盒转订单商品明细
        TOsParentOrderItemsPO giftBoxItem = getParentOrderItemsByGiftBoxForTHCard(parentOrderPo, thCardOrderBean.getGiftBoxId(), 0, new BigDecimal(1), thCardOrderBean, ssGoodsDao, ssSkuDao, ssCatDao, sysDicDataDao);
        
        List<TOsParentOrderItemsPO> items = new ArrayList<TOsParentOrderItemsPO>();
        items.add(giftBoxItem);
        
        Date purchaseDate = null;
        //礼盒中商品转订单商品明细
        List<TOsParentOrderItemsPO> thItems = getGiftBoxItemsForTHCard(parentOrderPo, giftBoxItem, map.get(thCardOrderBean.getGiftBoxId()+""), thCardOrderBean,
                        ssGoodsDao, ssSkuDao, ssCatDao, sysDicDataDao);
        items.addAll(thItems);

        // 获得礼盒中最后配货期
        if (null == purchaseDate) {
            purchaseDate = giftBoxItem.getPurchaseDate();
        } else if (null != giftBoxItem.getPurchaseDate()) {
            purchaseDate = giftBoxItem.getPurchaseDate().getTime()
                    - purchaseDate.getTime() > 0 ? giftBoxItem
                    .getPurchaseDate() : purchaseDate;
        }
        ////

        // 礼盒中商品和礼盒的预计采购日期相同
        for (TOsParentOrderItemsPO item : items) {
            item.setPurchaseDate(new Timestamp(purchaseDate.getTime()));
        }
        
        return items;
    }
    
    
    /**
     * 礼盒转订单明细
     *
     * @param parentOrder
     * @param goodsId
     * @param giftboxId
     * @param goodsNumber
     * @param vo
     * @return
     * @throws Exception
     */
    private TOsParentOrderItemsPO getParentOrderItemsByGiftBoxForTHCard(TOsParentOrderPO  parentOrderPo,
                                                                     Long goodsId, Integer giftboxId,
                                                                     BigDecimal goodsNumber, ThcardOrderBean thCardOrderBean,
                                                                     SsGoodsDao ssGoodsDao, SsSkuDao ssSkuDao, SsCatDao ssCatDao, SysDicDataDao sysDicDataDao) throws Exception {
        
        TOsParentOrderItemsPO item = getOrderItemsForTHCard(parentOrderPo, goodsId, YesOrNoEnum.YES, giftboxId, goodsNumber, thCardOrderBean, ssGoodsDao, ssSkuDao, ssCatDao, sysDicDataDao);
        item.setGoodsPrice(parentOrderPo.getOrderCallFee());
        item.setGoodsAmount(item.getGoodsNumber()
                .multiply(item.getGoodsPrice()));
        /*if (item.getGoodsType().equals(GoodsTypeEnum.STANDARD.getC())
                || item.getGoodsType().equals(
                GoodsTypeEnum.DELIVERY_CARD.getC())) {*/
            item.setGoodsAmount(item.getGoodsNumber().multiply(item.getGoodsPrice()));
        /*} else if (item.getGoodsType().equals(GoodsTypeEnum.NONSTANDARD.getC())) {
            item.setGoodsAmount(item
                    .getUnitWeight()
                    .multiply(item.getGoodsPrice())
                    .divide(item.getGoodsWeight(), 2,
                            BigDecimal.ROUND_HALF_EVEN)
                    .multiply(item.getGoodsNumber()));
            // 计算明细总额=((记重单位*单价)/记价单位)*数量
        }*/

        return item;
        
    }
    
    
    private TOsParentOrderItemsPO getOrderItemsForTHCard(TOsParentOrderPO parentOrder,
                    Long goodsId, YesOrNoEnum e, Integer giftboxId,
                    BigDecimal goodsNumber, ThcardOrderBean thCardOrderBean,
                    SsGoodsDao ssGoodsDao, SsSkuDao ssSkuDao, SsCatDao ssCatDao, SysDicDataDao sysDicDataDao) throws Exception {
        Long itemId = ssGoodsDao.findSeqNextVal("SEQ_OS_PARENT_ORDER_ITEMS_PK");
        if (itemId.intValue() == -1) {
            Log.info(logger, uuid, "获得父订单详情表主键失败", "itemId", itemId);
            return null;
        }
        
        SsGoodsPO goods = ssGoodsDao.findSsGoodsPOByID(goodsId.longValue(), false, false, false, false);
        
        List<Object[]> skuConditions = new ArrayList<Object[]>();
        skuConditions.add(new Object[]{"GOODS_ID", "=", goods.getGoodsId()});
        List<SsSkuPO> skuList = ssSkuDao.findSsSkuPOListByCondition(skuConditions, false, false, false);
        Log.info(logger, uuid, "获得skuList", "skuList", skuList);
        if (skuList == null || skuList.isEmpty()) {
            Log.info(logger, uuid, "skuList不存在", "skuList", skuList); 
            throw new OmsDelegateException(BaseResultEnum.INSIDE_ERROR.getResultID(), "获得skuList失败！", null);
        }
        SsSkuPO sku = skuList.get(0);
        
        SsCatPO ssCatPO = ssCatDao.findSsCatPOByID(goods.getCatId(), false, false, false, false);

        
        TOsParentOrderItemsPO item = new TOsParentOrderItemsPO();
        item.setItemId(itemId);
        item.setOrderId(parentOrder.getOrderId());
//        item.setOrder(parentOrder);
        item.setSku(sku.getSkuId());
//        item.setSsGoods(goods);
        if (null != goods.getGoodsEan()) {
            item.setGoodsEan(goods.getGoodsEan());
        }
        item.setGoodsId(goods.getGoodsId());
        item.setGoodsSn(goods.getGoodsSn());
        item.setCatId(goods.getCatId());
        item.setCatName(ssCatPO.getCatName());
        item.setGoodsNumber(goodsNumber);
        item.setShopPrice(sku.getShopPrice());

        item.setPromoteId("0");
//        item.setSaleTax(new BigDecimal(0.17));
        item.setGoodsName(goods.getGoodsName());
        item.setGoodsType(goods.getGoodsType());
        item.setGoodsWeight(sku.getGoodsWeight());
        item.setUnitWeight(sku.getUnitWeight());
        item.setUnitName(getUnitName(sku, goods, sysDicDataDao));
        ////
        // item.setRealOutNum(new BigDecimal(0));
        item.setIsGiftbox(e.getC());
        item.setGiftboxId(giftboxId);
        item.setIsGift(new Byte("0"));
        item.setBasicId(goods.getBasicId());//设置商品基础id

        // //
        // add by zhangyu 2012-08-28
        // 预计采购日期=配送日期-提货礼盒备货期-1天
        if (null != goods.getDaysPrepare() && goods.getDaysPrepare() > 0) {
            Date purchaseDate = calculationPurchaseDate(thCardOrderBean.getPlanDate(), goods.getDaysPrepare().intValue());
            item.setPurchaseDate(new Timestamp(purchaseDate.getTime()));
        }
        // //

//        if(null != goods.getShipCatId() && 0 < goods.getShipCatId().intValue()){
//            ShipCatRetVO shipCatRetVO = getHandler(ShipCatHandler.class).findById(goods.getShipCatId());
//            item.setWarmzoneId(shipCatRetVO.getWarnZonePropsId());//设置商品温区
//        }
        item.setWarmzoneId(goods.getWarmZonePropsId());//管理分区ID
        item.setShipCapacity(thCardOrderBean.getSpecifiedShippingDate());//是否考虑运力：取是否指定日期
        item.setWarehouseId(thCardOrderBean.getWarehouseId().longValue());//库房
        item.setDcId(thCardOrderBean.getDcId().longValue());//配送公司
        
        item.setReviewId(0L);
        item.setStatus("1");

        return item;
    }
    
    
  //...设置商品的单位..............................................................................
    /*1、MARKETING_UNIT  != 11, 12, 13, 14 && 非标品 ==》 斤
        * 2、DATA_NAME
        * 3、SHOW_MARKETING_UNIT
        */
    public String getUnitName(SsSkuPO sku, SsGoodsPO goods, SysDicDataDao sysDicDataDao) throws Exception {
        String unitName = "";
        
        SysDicDataPO sysDicDataPo = sysDicDataDao.findSysDicDataPOByID(sku.getMarketingUnit());
        if (sysDicDataPo != null) {
            unitName = sysDicDataPo.getDataName();
        } else {
            unitName = sku.getMarketingUnit() + "";
        }
        
        Log.info(logger, uuid, "获得unitName", "unitName", unitName);
        

        /*if(goods.getGoodsType().equals(GoodsTypeEnum.STANDARD.getC())){
            unitName = getSysDicService().getData(sku.getMarketingUnit()).getDataName();
            if(StringUtil.isEmpty(unitName)){
                unitName = sku.getShowMarketingUnit();
            }
        } else if(MarketingUnitSpecialEnum.isNonStandardUnit(sku.getMarketingUnit() + "")){
            unitName = sku.getShowMarketingUnit();
            if(StringUtils.isEmpty(unitName))
                    unitName = MarketingUnitEnum.UNIT12.getS();//斤
        }else {
            unitName = sku.getShowMarketingUnit();
            if(StringUtils.isEmpty(unitName))
                unitName = getSysDicService().getData(sku.getMarketingUnit()).getDataName();
        }*/

        return unitName;
    }
    
    /**
     * 预计采购日期=配送日期-提货礼盒备货期-1天
     * @param planDate
     * @param daysPrepare
     * @return
     */
    private Date calculationPurchaseDate(String planDate, int daysPrepare) {
        // Date d = DateUtils.format(planDate, DateUtils.DATE);
        Date d = DateUtil.strToDate(planDate);
//        BigDecimal temp = daysPrepare.add(BigDecimal.ONE);
        int temp = daysPrepare + 1;
        
        // return DateUtils.dayOffsets(d, temp.intValue() * -1);
        return DateUtil.getDateBeforeOrAfterDays(d, temp * -1);
    }
    
    
    // 得到提货卡订单所有商品明细，其中 不包括 提货卡礼盒本身
    private List<TOsParentOrderItemsPO> getGiftBoxItemsForTHCard(
            TOsParentOrderPO parentOrder, TOsParentOrderItemsPO giftboxItem,
            Map<Long, BigDecimal> map, ThcardOrderBean thCardOrderBean,
            SsGoodsDao ssGoodsDao, SsSkuDao ssSkuDao, SsCatDao ssCatDao, SysDicDataDao sysDicDataDao)
            throws Exception {
        List<TOsParentOrderItemsPO> result = new ArrayList<TOsParentOrderItemsPO>();

        BigDecimal totalGiftboxItemsAmount = BigDecimal.ZERO;
        TOsParentOrderItemsPO item;
        for (Long giftboxGoodsId : map.keySet()) {
            /*
             * 明细商品id：giftboxGoodsId 明细商品数量：map.get(giftboxGoodsId)
             */
            item = this.getOrderItemsForTHCard(parentOrder, giftboxGoodsId, YesOrNoEnum.NO, giftboxItem.getItemId().intValue(), map.get(giftboxGoodsId), thCardOrderBean, ssGoodsDao, ssSkuDao, ssCatDao, sysDicDataDao);

            //...计算标品 和 非标品 金额（包括换货商品）...............................................................
            CalVO calVO = getCalVO(item, null, null, CalculateTypeForTHEnum.GOODS_AMOUNT.getC());
            // BigDecimal giftboxItemAmount = CalculationAmountForTH.getInstance(calVO).getResult();
            BigDecimal giftboxItemAmount = StandardCalculationGoodsAmountForTHImpl(calVO);
            totalGiftboxItemsAmount = totalGiftboxItemsAmount.add(giftboxItemAmount);
            //..................................................................

            result.add(item);
        }

        //...反算礼盒商品明细（包括换货商品）........................................................................
        reverseGiftboxItems(result, giftboxItem.getGoodsAmount(), totalGiftboxItemsAmount);
        //...........................................................................


        return result;
    }
    
    private CalVO getCalVO(TOsParentOrderItemsPO item, BigDecimal totalGiftboxItemsAmount, BigDecimal giftboxAmount, String calculateType) {
        CalVO calVO = new CalVO();
        calVO.setPrice(item.getShopPrice());// 初始为shopPrice
        calVO.setNumber(item.getGoodsNumber());
        calVO.setUnitWeight(item.getUnitWeight());
        calVO.setGoodsWeight(item.getGoodsWeight());
        calVO.setType(item.getGoodsType());
        calVO.setGoodsAmount(giftboxAmount);
        calVO.setTotalGoodsItemAmount(totalGiftboxItemsAmount);
        calVO.setCalculateType(calculateType);
        return calVO;
    }
    
    
    /**
     * Description:<br>
     * 计算商品总价-标品
     * 
     * @param vo
     * @return
     */
    private BigDecimal StandardCalculationGoodsAmountForTHImpl(CalVO calVO){
        // price*number
        return calVO.getPrice().multiply(calVO.getNumber());
    }
    
    
    /**
     * Description:<br>
     * 反算商品总价 - 标品
     * 
     * @param calVO
     * @return
     */
    private BigDecimal StandardCalculationReverseGoodsAmountForTHImpl(CalVO calVO){
        // 标品：( (price*number) / totalItemAmount) * totalAmount
        BigDecimal result = calVO
                        .getPrice()
                        .multiply(calVO.getNumber())
                        .divide(calVO.getTotalGoodsItemAmount(), 8,
                                BigDecimal.ROUND_HALF_EVEN)
                        .multiply(calVO.getGoodsAmount());
                return result.setScale(2, BigDecimal.ROUND_HALF_EVEN);
    }
    
    /**
     * Description:<br>
     * 反算单价-标品
     * 
     * @param calVO
     * @return
     */
    private BigDecimal StandardCalculationReverseGoodsPriceForTHImpl(CalVO calVO){
        // goodsItemAmount / number
        BigDecimal result = calVO.getGoodsItemAmount().divide(calVO.getNumber(), 8,
                        BigDecimal.ROUND_HALF_EVEN);
        return result.setScale(2, BigDecimal.ROUND_HALF_EVEN);
    }
    
    
    
    /**
     * 礼盒商品转订单明细
     *
     * @param items
     * @param totalGiftboxItemsAmount
     * @param giftboxAmount
     */
    private void reverseGiftboxItems(List<TOsParentOrderItemsPO> items, BigDecimal giftboxAmount, BigDecimal totalGiftboxItemsAmount) {
        for(TOsParentOrderItemsPO item : items){

            //...反算礼盒商品金额 和 单价....................................................................
            CalVO calVO = getCalVO(item, totalGiftboxItemsAmount, giftboxAmount, CalculateTypeForTHEnum.REVERSE_GOODS_AMOUNT.getC());

            //商品总阶
            // BigDecimal amount = CalculationAmountForTH.getInstance(calVO).getResult();
            BigDecimal amount = StandardCalculationReverseGoodsAmountForTHImpl(calVO);

            calVO.setGoodsItemAmount(amount);
            calVO.setCalculateType(CalculateTypeForTHEnum.REVERSE_PRICE.getC());
            // BigDecimal price = CalculationAmountForTH.getInstance(calVO).getResult();
            BigDecimal price = StandardCalculationReverseGoodsPriceForTHImpl(calVO);
            //.......................................................................

            item.setGoodsAmount(amount);
            item.setGoodsPrice(price);
        }
    }
    
    
}

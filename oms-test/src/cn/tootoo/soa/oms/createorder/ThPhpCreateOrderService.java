package cn.tootoo.soa.oms.createorder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import cn.tootoo.db.egrocery.bsshipaddr.BsShipAddrDao;
import cn.tootoo.db.egrocery.bsshipaddr.BsShipAddrPO;
import cn.tootoo.db.egrocery.geobelongsubstation.GeoBelongSubstationDao;
import cn.tootoo.db.egrocery.geobelongsubstation.GeoBelongSubstationPO;
import cn.tootoo.db.egrocery.ssgiftboxitem.SsGiftboxItemDao;
import cn.tootoo.db.egrocery.ssgiftboxitem.SsGiftboxItemPO;
import cn.tootoo.db.egrocery.ssgoods.SsGoodsDao;
import cn.tootoo.db.egrocery.ssgoods.SsGoodsPO;
import cn.tootoo.db.egrocery.sysgeo.SysGeoPO;
import cn.tootoo.db.egrocery.tareatemplatedetail.TAreaTemplateDetailDao;
import cn.tootoo.db.egrocery.tareatemplatedetail.TAreaTemplateDetailPO;
import cn.tootoo.db.egrocery.tsysthcard.TSysThcardDao;
import cn.tootoo.db.egrocery.tsysthcard.TSysThcardPO;
import cn.tootoo.db.egrocery.tsysthcardkind.TSysThcardKindDao;
import cn.tootoo.db.egrocery.tsysthcardkind.TSysThcardKindPO;
import cn.tootoo.db.egrocery.warehouseshippower.WarehouseShipPowerDao;
import cn.tootoo.db.egrocery.warehouseshippower.WarehouseShipPowerPO;
import cn.tootoo.enums.BaseResultEnum;
import cn.tootoo.enums.BooleanEnum;
import cn.tootoo.http.AuthorizeClient;
import cn.tootoo.http.TootooService;
import cn.tootoo.http.bean.BaseInputBean;
import cn.tootoo.http.bean.BaseOutputBean;
import cn.tootoo.soa.address.updatedefaultaddress.input.AddressUpdateDefaultAddressInputData;
import cn.tootoo.soa.address.updatedefaultaddress.output.AddressUpdateDefaultAddressOutputData;
import cn.tootoo.soa.base.enums.BaseOrderResultEnum;
import cn.tootoo.soa.base.enums.ThcardStatusEnum;
import cn.tootoo.soa.base.enums.YesOrNoEnum;
import cn.tootoo.soa.base.global.SpecialInfos;
import cn.tootoo.soa.oms.createorder.input.OmsCreateOrderInputData;
import cn.tootoo.soa.oms.createorder.input.OmsCreateOrderPayListElementI;
import cn.tootoo.soa.oms.createorder.input.OmsCreateOrderTihuoGoodsListElementI;
import cn.tootoo.soa.oms.createorder.output.OmsCreateOrderOutputData;
import cn.tootoo.soa.oms.utils.ThcardOrderBean;
import cn.tootoo.utils.DateUtil;
import cn.tootoo.utils.Log;
import cn.tootoo.utils.MD5Util;
import cn.tootoo.utils.ResponseUtil;
import cn.tootoo.utils.StringUtil;

/**
 * 提货卡订单Service
 * @author tenbaoxin
 *
 */
public class ThPhpCreateOrderService extends ThCreateOrderBaseService{
	
	public ThPhpCreateOrderService(String uuid, Logger logger) {
        super(uuid, logger);
    }

	public BaseOutputBean createTiHuoCreateOrder(
			BaseInputBean inputBean,OmsCreateOrderInputData inputData ,
			BaseOutputBean outputBean,OmsCreateOrderOutputData outputData,
			HashMap<String, String> params,boolean haveOnlinePay,
			String isCheckCOD,Long payMethodId) throws Exception {
	    // 提货订单
        boolean tihuoIsOk = true;
        //------------------------------------------开始
        TSysThcardDao tSysThcardDao = new TSysThcardDao(uuid, logger);
        TSysThcardKindDao tSysThcardKindDao = new TSysThcardKindDao(uuid, logger);
        // BsBuyerDao bsBuyerDao = new BsBuyerDao(uuid, logger);
        SsGoodsDao ssGoodsDao = new SsGoodsDao(uuid, logger);
        BsShipAddrDao bsShipAddrDao = new BsShipAddrDao(uuid, logger);
        SsGiftboxItemDao ssGiftboxItemDao = new SsGiftboxItemDao(uuid, logger);
        // TSysCardProductDao tSysCardProductDao = new TSysCardProductDao(uuid, logger);
        TAreaTemplateDetailDao tAreaTemplateDetailDao = new TAreaTemplateDetailDao(uuid, logger);
        WarehouseShipPowerDao warehouseShipPowerDao = new WarehouseShipPowerDao(uuid, logger);
        // TSysCpGbDao tSysCpGbDao = new TSysCpGbDao(uuid, logger);
        GeoBelongSubstationDao geoBelongSubstationDao = new GeoBelongSubstationDao(uuid, logger);
        
        String buyerId = params.get(AuthorizeClient.COOKIE_BUYER_ID);
        String buyerEmail = params.get(AuthorizeClient.PARAM_BUYER_NAME);
        String ip = inputBean.getInputHead().getIp();
        
        
        // 判断是否是‘17：00’以后,如果是则“提货礼盒备货期+2天”，否则“提货礼盒备货期+1天”
        // 运力给定日期从下一天开始，所以这里+1天
        String curr = DateUtil.dateToFormatStr(new Date(), "HH");
        String thresholdStr = "17";
        BigDecimal threshold = BigDecimal.ZERO;// 备货期阀值
        if (Integer.parseInt(curr) >= Integer.parseInt(thresholdStr)) {
            threshold = BigDecimal.ONE;
        }
        
        if (inputData.getAccount() == null || StringUtil.isEmpty(inputData.getAccount().trim())) {
            Log.info(logger, uuid, "提货卡号不能为空！", "inputData.getAccount()", inputData.getAccount());
            outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.REQUEST_PARAM_ERROR.getResultID(), null, inputData.getScope());
            Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
            return outputBean;
        }
        inputData.setAccount(inputData.getAccount().trim());
        if (inputData.getTihuoGoodsList() == null || inputData.getTihuoGoodsList().isEmpty()) {
            Log.info(logger, uuid, "礼盒信息不能为空！", "inputData.getTihuoGoodsList()", inputData.getTihuoGoodsList());
            outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.REQUEST_PARAM_ERROR.getResultID(), null, inputData.getScope());
            Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
            return outputBean;
        }
        if(inputData.getPayList() == null || inputData.getPayList().isEmpty()){
            Log.info(logger, uuid, "支付计划不能为空!", "inputData.getPayList()", inputData.getPayList());
            outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.REQUEST_PARAM_ERROR.getResultID(), null, inputData.getScope());
            Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
            return outputBean;
        }
        if(StringUtil.isEmpty(buyerId) || StringUtil.isEmpty(ip)){
            Log.info(logger, uuid, "用户ID或用户IP不能为空！", "buyerId", buyerId, "ip", "ip");
            outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.REQUEST_PARAM_ERROR.getResultID(), null, inputData.getScope());
            Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
            return outputBean;
        }
        OmsCreateOrderTihuoGoodsListElementI thGiftboxI = inputData.getTihuoGoodsList().get(0);
        if (thGiftboxI.getGoodsID() == null) {
            Log.info(logger, uuid, "礼盒ID不能为空！", "thGiftboxI.getGoodsID()", thGiftboxI.getGoodsID());
            outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.REQUEST_PARAM_ERROR.getResultID(), null, inputData.getScope());
            Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
            return outputBean;
        }
        if(inputData.getReceiveAddrID() == null){
            Log.info(logger, uuid, "用户地址ID不能为空", "inputData.getReceiveAddrID()", inputData.getReceiveAddrID());
            outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.REQUEST_PARAM_ERROR.getResultID(), null, inputData.getScope());
            Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
            return outputBean;
        }
        
        
        
        
        Log.info(logger, uuid, "检查提货卡");
        List<Object[]> thCardConditions = new ArrayList<Object[]>();
        thCardConditions.add(new Object[]{"THCARD_SN", "=", inputData.getAccount()});
        thCardConditions.add(new Object[]{"STATUS", "!=", ThcardStatusEnum.DELETE.getC()});
        List<TSysThcardPO> thcardList = tSysThcardDao.findTSysThcardPOListByCondition(thCardConditions, false, false, true);
        Log.info(logger, uuid, "查询提货卡结果", "thcardList", thcardList);
        if (thcardList == null || thcardList.size() <= 0) {
            Log.info(logger, uuid, "提货卡不存在！", "thcardList", thcardList);
            outputBean = ResponseUtil.getBaseOutputBean(BaseOrderResultEnum.TH_NOT_EXISTS.getResultID(), null, inputData.getScope());
            Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
            return outputBean;
        }
        TSysThcardPO thCard = thcardList.get(0);
        if (!thCard.getThcardPasswd().equalsIgnoreCase(MD5Util.md5(inputData.getPassword()))){
            Log.info(logger, uuid, "提货卡密码错误", "MD5Util.md5(inputData.getPassword())", MD5Util.md5(inputData.getPassword()), "thCard.getThcardPasswd()", thCard.getThcardPasswd());
            outputBean = ResponseUtil.getBaseOutputBean(BaseOrderResultEnum.TH_PWD_ERROR.getResultID(), null, inputData.getScope());
            Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
            return outputBean;
        }
        
        
        Log.info(logger, uuid, "检查提货种类");
        List<Object[]> cardKindConditions = new ArrayList<Object[]>();
        cardKindConditions.add(new Object[]{"CARD_ID", "=", thCard.getId()});
        List<TSysThcardKindPO> cardKindList = tSysThcardKindDao.findTSysThcardKindPOListByCondition(cardKindConditions, false, false, false);
        Log.info(logger, uuid, "查询提货种类结果", "cardKindList", cardKindList);
        if (cardKindList == null){
            Log.info(logger, uuid, "提货种类没有找到", "cardKindList", cardKindList);
            outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.REQUEST_PARAM_ERROR.getResultID(), null, inputData.getScope());
            Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
            return outputBean;
        }
        if (cardKindList.size() > 1){
            Log.info(logger, uuid, "提货种类大于1种，不能确定本次的提货种类", "cardKindList", cardKindList);
            outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.REQUEST_PARAM_ERROR.getResultID(), null, inputData.getScope());
            Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
            return outputBean;
        }
        
        // 检查用户
        /*BsBuyerPO buyer = bsBuyerDao.findBsBuyerPOByID(Long.valueOf(buyerId));
        Log.info(logger, uuid, "查询用户结果", "buyer", buyer);
        if (buyer == null) {
            Log.info(logger, uuid, "用户不存在", "buyer", buyer);
            outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.REQUEST_PARAM_ERROR.getResultID(), null, inputData.getScope());
            Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
            return outputBean;
        }*/


        Log.info(logger, uuid, "检查礼盒");
        SsGoodsPO giftBox = ssGoodsDao.findSsGoodsPOByID(thGiftboxI.getGoodsID(), false, false, false, false);
        if(giftBox == null){
            Log.info(logger, uuid, "礼盒不存在", "giftBox", giftBox);
            outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.REQUEST_PARAM_ERROR.getResultID(), null, inputData.getScope());
            Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
            return outputBean;
        }
        
        Log.info(logger, uuid, "检查用户地址");
        BsShipAddrPO shipAddr = bsShipAddrDao.findBsShipAddrPOByID(inputData.getReceiveAddrID(), false, false, false, false);        
        if(shipAddr == null){
            Log.info(logger, uuid, "没有找到收货地址");
            outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.REQUEST_PARAM_ERROR.getResultID(), null, inputData.getScope());
            Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
            return outputBean;
        }
        Log.info(logger, uuid, "查询地址结束", "shipAddr", shipAddr);
        
        Map<Long, SysGeoPO> geoMap = SpecialInfos.SysGeoMap;
        Log.info(logger, uuid, "四级地址map", "geoMap.size", geoMap == null?null:geoMap.size());
        if(geoMap == null){
            outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.INSIDE_ERROR.getResultID(), null, inputData.getScope());
            return outputBean;
        }
        
        if(StringUtil.isEmpty(shipAddr.getReceiver())){
            Log.info(logger, uuid, "收货人名称不能为空");
            outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.REQUEST_PARAM_ERROR.getResultID(), null, inputData.getScope());
            Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
            return outputBean;
        }
        
        String provinceName = geoMap.get(shipAddr.getProvince()).getGeoName();
        String cityName = geoMap.get(shipAddr.getCity()).getGeoName();
        String districtName = geoMap.get(shipAddr.getDistrict()).getGeoName();
        String areaName = (shipAddr.getArea() == 0L || shipAddr.getArea() == null)?"":geoMap.get(shipAddr.getArea()).getGeoName();
        if(StringUtil.isEmpty(provinceName)){
            Log.info(logger, uuid, "省不存在");
            outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.REQUEST_PARAM_ERROR.getResultID(), null, inputData.getScope());
            Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
            return outputBean;
        }
        if (StringUtil.isEmpty(cityName)) {
            Log.info(logger, uuid, "市不存在");
            outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.REQUEST_PARAM_ERROR.getResultID(), null, inputData.getScope());
            Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
            return outputBean;
        }
        if (StringUtil.isEmpty(districtName)) {
            Log.info(logger, uuid, "区不存在");
            outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.REQUEST_PARAM_ERROR.getResultID(), null, inputData.getScope());
            Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
            return outputBean;
        }
        if (StringUtils.isEmpty(shipAddr.getAddrLine1())) {
            Log.info(logger, uuid, "详细地址不能为空");
            outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.REQUEST_PARAM_ERROR.getResultID(), null, inputData.getScope());
            Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
            return outputBean;
        }
        if (StringUtils.isEmpty(shipAddr.getPhoneNumber()) && StringUtils.isEmpty(shipAddr.getMobileNumber())) {
            Log.info(logger, uuid, "电话或手机至少填写一个");
            outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.REQUEST_PARAM_ERROR.getResultID(), null, inputData.getScope());
            Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
            return outputBean;
        }
        
        
        Log.info(logger, uuid, "检查礼盒明细");
        List<Object[]> giftBoxConditions = new ArrayList<Object[]>();
        giftBoxConditions.add(new Object[]{"P_GOODS_ID", "=", giftBox.getGoodsId()});
        List<SsGiftboxItemPO> giftboxList = ssGiftboxItemDao.findSsGiftboxItemPOListByCondition(giftBoxConditions, false, false, false);
        Log.info(logger, uuid, "检查礼盒明细", "giftboxList", giftboxList);
        if (giftboxList == null || giftboxList.isEmpty()) {
            Log.info(logger, uuid, "礼盒明细不存在");
            outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.INSIDE_ERROR.getResultID(), null, inputData.getScope());
            Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
            return outputBean;
        }
        
        
        // 商品ID, 数量
        Map<Long, BigDecimal> map = new HashMap<Long, BigDecimal>();
        for (SsGiftboxItemPO vo : giftboxList) {
            map.put(vo.getGoodsId(), vo.getAmount());
        }
        
        StringBuffer sb = new StringBuffer();
        sb.append(" (");
        for (int i = 0; i < giftboxList.size(); i++) {
            sb.append("'" + giftboxList.get(i).getGoodsId() + "'");
            if (i != giftboxList.size() - 1) {
                sb.append(",");
            }
        }
        sb.append(") ");
        List<Object[]> goodsConditions = new ArrayList<Object[]>();
        goodsConditions.add(new Object[]{"GOODS_ID IN " + sb.toString(), "", ""});
        List<SsGoodsPO> goodsList = ssGoodsDao.findSsGoodsPOListByCondition(goodsConditions, false, false, false);
        BigDecimal daysPrepare = BigDecimal.ZERO;
        for (SsGoodsPO ssGoodsPO : goodsList) {
            daysPrepare = daysPrepare.compareTo(new BigDecimal(ssGoodsPO.getDaysPrepare())) > 0?daysPrepare:new BigDecimal(ssGoodsPO.getDaysPrepare());
        }
        
        
        Log.info(logger, uuid, "获取未级地址ID");
        Long geoId = shipAddr.getArea();
        if (null == geoId || 0 >= geoId) {
            geoId = shipAddr.getDistrict();
        }
        if(null == geoId || 0 >= geoId) {
            Log.info(logger, uuid, "没有找到未级地址");
            outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.REQUEST_PARAM_ERROR.getResultID(), null, inputData.getScope());
            Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
            return outputBean;
        }
        
        
        Log.info(logger, uuid, "根据地址末级ID获得归属分站");
        List<Object[]> geoBelongConditions = new ArrayList<Object[]>();
        geoBelongConditions.add(new Object[]{"GEO_ID", "=", geoId});
        geoBelongConditions.add(new Object[]{"GEO_BELONG_SUBSTATION_STATUS", "=", "1"});
        Log.info(logger, uuid, "查询geoBelongSubstation表条件", "geoBelongConditions", StringUtil.transferObjectList(geoBelongConditions));
        List<GeoBelongSubstationPO> geoBelongGbList = geoBelongSubstationDao.findGeoBelongSubstationPOListByCondition(geoBelongConditions, false, false, false);
        Log.info(logger, uuid, "获得geoBelongSubstation表数据", "geoBelongGbList", geoBelongGbList);
        if (geoBelongGbList == null || geoBelongGbList.isEmpty() || geoBelongGbList.get(0).getSubstationId() == null) {
            Log.info(logger, uuid, "geoBelongSubstation表归属分站不存在");
            outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.INSIDE_ERROR.getResultID(), null, inputData.getScope());
            Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
            return outputBean;
        }
        Long geoSubstationId = geoBelongGbList.get(0).getSubstationId();
        Log.info(logger, uuid, "获得归属分站", "geoSubstationId", geoSubstationId);
        
        
        SsGoodsPO ssgoodsPo = ssGoodsDao.findSsGoodsPOByID(giftBox.getGoodsId(), false, false, false, false);
        Log.info(logger, uuid, "提货礼盒商品信息！", "SsGoodsPO", ssgoodsPo);
        
        // *** 检查送货日期 **************************************************
        // if (null == ssgoodsPo || null == ssgoodsPo.getSubstationId() || 0 >= ssgoodsPo.getSubstationId()) {
        if (null == ssgoodsPo){
            Log.info(logger, uuid, "没有找到礼盒", "ssgoodsPo", ssgoodsPo);
            outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.REQUEST_PARAM_ERROR.getResultID(), null, inputData.getScope());
            Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
            return outputBean;
        }
        if (null == ssgoodsPo || null == ssgoodsPo.getTemplateId() || 0 >= ssgoodsPo.getTemplateId()) {
            Log.info(logger, uuid, "礼盒没有设置配送模板");
            outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.REQUEST_PARAM_ERROR.getResultID(), null, inputData.getScope());
            Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
            return outputBean;
        }
        
        Log.info(logger, uuid, "获得配送模板");
        List<Object[]> templateConditions = new ArrayList<Object[]>();
        templateConditions.add(new Object[]{"TEMPLATE_ID", "=", ssgoodsPo.getTemplateId()});
        templateConditions.add(new Object[]{"SUBSTATION_ID", "=", geoSubstationId});
        templateConditions.add(new Object[]{"GEO_ID", "=", geoId});
        
        List<TAreaTemplateDetailPO> templateList = tAreaTemplateDetailDao.findTAreaTemplateDetailPOListByCondition(templateConditions, false, false, false);
        Log.info(logger, uuid, "获得配送模板", "templateList", templateList);
        if (templateList == null || templateList.isEmpty()) {
            Log.info(logger, uuid, "配送模板不存在");
            outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.REQUEST_PARAM_ERROR.getResultID(), null, inputData.getScope());
            Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
            return outputBean;
        }
        TAreaTemplateDetailPO areaTemplate = templateList.get(0);
        
        Log.info(logger, uuid, "检查配送模板");
        if (null == areaTemplate) {
            Log.info(logger, uuid, "提货卡礼盒上没有找到对应配送模板");
            outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.REQUEST_PARAM_ERROR.getResultID(), null, inputData.getScope());
            Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
            return outputBean;
        }
        if (StringUtils.isEmpty(inputData.getReceiveDate()) && YesOrNoEnum.YES.getC().equals(areaTemplate.getSpecifiedShippingDate())) {
            // 如果模板需要“指定配送日期”且 订单未指定配送日期，则记录错误信息
            Log.info(logger, uuid, "提货订单未指定配送日期");
            outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.REQUEST_PARAM_ERROR.getResultID(), null, inputData.getScope());
            Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
            return outputBean;
        }
        if (StringUtils.isNotEmpty(inputData.getReceiveDate())) {
            Log.info(logger, uuid, "订单“配送日期”不为空，则为自营配送，检查运力有效性");
            List<Object[]> wareConditions = new ArrayList<Object[]>();
            wareConditions.add(new Object[]{"WAREHOUSE_ID", "=", areaTemplate.getWarehouseId()});
            wareConditions.add(new Object[]{"SUBSTATION_ID", "=", areaTemplate.getSubstationId()});
            wareConditions.add(new Object[]{"SHIPPING_COMPANY_ID", "=", areaTemplate.getShippingCompanyId()});
            wareConditions.add(new Object[]{"SHIP_POWER_TYPE", "=", "3"}); // 提货
            wareConditions.add(new Object[]{"SHIP_POWER_DATE", "=", inputData.getReceiveDate()});
            
            List<WarehouseShipPowerPO> shipList = warehouseShipPowerDao.findWarehouseShipPowerPOListByCondition(wareConditions, false, false, false);
            Log.info(logger, uuid, "检查运力有效性", "shipList", shipList);
            if(null == shipList || shipList.isEmpty() ) {
                Log.info(logger, uuid, "提货运力没有找到");
                outputBean = ResponseUtil.getBaseOutputBean(BaseOrderResultEnum.SHIPPINGDATE_INVALID.getResultID(), null, inputData.getScope());
                Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
                return outputBean;
            } else if (0 >= (shipList.get(0).getMaxShipPowerAmount() - shipList.get(0).getOccupiedShipPowerAmount())) {
                Log.info(logger, uuid, "提货运力不足");
                outputBean = ResponseUtil.getBaseOutputBean(BaseOrderResultEnum.SHIPPINGDATE_INVALID.getResultID(), null, inputData.getScope());
                Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
                return outputBean;
            }
            
        } else {
            Log.info(logger, uuid, "非沱沱自营取“礼盒最早的可配送日期”，（即“配送日期”=“下单日期”+“礼盒配货期”+1）");
            Date dcDate = DateUtil.getDateBeforeOrAfterDays(new Date(), daysPrepare.add(threshold).add(BigDecimal.ONE).intValue());
            inputData.setReceiveDate(DateUtil.dateToStr(dcDate));
        }
        // ***************************************************************************************************
        
        // 支付计划
        OmsCreateOrderPayListElementI payI = inputData.getPayList().get(0);
        
        //构建thcardOrderBean
        ThcardOrderBean thcardOrderBean = new ThcardOrderBean();
        thcardOrderBean.setThCardCode(inputData.getAccount().trim());//thcardSn
        thcardOrderBean.setThcardKdId(cardKindList.get(0).getId());//提货种类id
        thcardOrderBean.setShipAddrId(inputData.getReceiveAddrID());//收货地址ID
        // thcardOrderBean.setMarketID(inputData.getMarketID());//订单平台，（废弃，写死为线上支付）
        thcardOrderBean.setOrderFrom(inputData.getOrderFrom().trim());//订单来源
        thcardOrderBean.setOrderType(inputData.getOrderType().trim());//订单类型
        thcardOrderBean.setSubstationID(inputData.getSubstationID());//分站ID
        thcardOrderBean.setPayMethodID(payI.getPayMethodID());//支付方式ID
        thcardOrderBean.setPayAmount(payI.getPayAmount());//支付金额
        thcardOrderBean.setBuyerId(Long.valueOf(buyerId));//用户id
        thcardOrderBean.setBuyerEmail(buyerEmail);//用户email
        thcardOrderBean.setBuyerFlag(YesOrNoEnum.YES.getC());//是否网站用户
        thcardOrderBean.setGiftBoxId(giftBox.getGoodsId());//礼盒id
        thcardOrderBean.setShipfee("0");//运费
        thcardOrderBean.setIp(inputBean.getInputHead().getIp());//ip
        thcardOrderBean.setReceiver(shipAddr.getReceiver().trim());//收货人
        thcardOrderBean.setProvinceName(provinceName);//省
        thcardOrderBean.setCityName(cityName);//市
        thcardOrderBean.setDistrictName(districtName);//区
        thcardOrderBean.setAreaName(areaName);//街
        thcardOrderBean.setAddr(shipAddr.getAddrLine1().trim());//详细地址
        thcardOrderBean.setPhonenum(null == shipAddr.getPhoneNumber() ? "" : shipAddr.getPhoneNumber().trim());//电话
        thcardOrderBean.setMobilenum(null == shipAddr.getMobileNumber() ? "" : shipAddr.getMobileNumber().trim());//手机
        thcardOrderBean.setPos(null == shipAddr.getPostalCode() ? "" : shipAddr.getPostalCode().trim());//邮编
        thcardOrderBean.setPlanDate(inputData.getReceiveDate().trim());//配送时间
        thcardOrderBean.setWarehouseId(areaTemplate.getWarehouseId());//库房
        thcardOrderBean.setDcId(areaTemplate.getShippingCompanyId());//配送公司
        thcardOrderBean.setConsiderShippingCapacity(areaTemplate.getConsiderShippingCapacity());//是否考虑运力
        thcardOrderBean.setSpecifiedShippingDate(areaTemplate.getSpecifiedShippingDate());//是否指定配送日期
        
        
        
        // 得到实际的商品ID集合
        Map<String, Map<Long, BigDecimal>> giftBoxDetailMap = new HashMap<String, Map<Long, BigDecimal>>();
        // 存放礼盒中商品id，数量
        giftBoxDetailMap.put(thcardOrderBean.getGiftBoxId() + "", map);
        
        // 生成提货订单
        outputBean = createThcardOrder(thcardOrderBean, giftBoxDetailMap, inputData.getScope(), outputBean, false, outputData, null);
        // ------------------------------------------结束        
        
        if (outputBean != null && outputBean.getOutputHead() != null && outputBean.getOutputHead().getResultID() != null
                        && outputBean.getOutputHead().getResultID() != BaseResultEnum.SUCCESS.getResultID()) {
            tihuoIsOk = false;
        }
        
        
        
        /*********************************************************************** 调用地址服务开始(修改为默认地址) **********************************************************************************/
        try {
            // 前台选择了设置默认地址
            if (tihuoIsOk&& BooleanEnum.TRUE.getV().equals(inputData.getIsSetDefault())) {
                
                Log.info(logger, uuid, "组装address服务updateDefaultAddress方法所需参数开始");
                AddressUpdateDefaultAddressInputData addressUpdateDefaultInputData = new AddressUpdateDefaultAddressInputData();
                addressUpdateDefaultInputData.setScope(inputData.getScope());
                addressUpdateDefaultInputData.setShipAddrID(inputData.getReceiveAddrID());
                
				if (haveOnlinePay|| BooleanEnum.TRUE.getV().equals(isCheckCOD)) {// 选择了线上或者线下
					addressUpdateDefaultInputData.setPayID(payMethodId);
                }
                addressUpdateDefaultInputData.setNeedReturn(BooleanEnum.FALSE.getV());
                HashMap<String, String> addressUpdateDefaultServiceParams = (HashMap<String, String>)params.clone();
                addressUpdateDefaultServiceParams.put("method", "updateDefaultAddress");
                addressUpdateDefaultServiceParams.put("req_str", addressUpdateDefaultInputData.toJson());
                Log.info(logger, uuid, "组装address服务updateDefaultAddress方法所需参数结束");
                
                Log.info(logger, uuid, "调用address服务updateDefaultAddress方法开始", "addressUpdateDefaultServiceParams", addressUpdateDefaultServiceParams);
                outputBean = TootooService.callServer("address", addressUpdateDefaultServiceParams, "post", new AddressUpdateDefaultAddressOutputData());
                Log.info(logger, uuid, "调用address服务updateDefaultAddress方法结束", "outputBean", outputBean);
                
                if (!TootooService.checkService(outputBean, "address", "updateDefaultAddress", inputData.getScope())) {
                    Log.info(logger, uuid, "调用address服务updateDefaultAddress方法失败,接口返回", "outputBean", outputBean);
                }
                // 如果调用失败,不影响下单
            }
        } catch (Exception e) {
            Log.error(logger, "修改为默认地址失败", e);
        }
        /*********************************************************************** 调用地址服务结束(修改为默认地址) **********************************************************************************/
        
        Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
        return outputBean;
	}
}

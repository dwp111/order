package cn.tootoo.soa.oms.createthorderboss;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import cn.tootoo.enums.BaseResultEnum;
import cn.tootoo.http.bean.BaseInputBean;
import cn.tootoo.http.bean.BaseOutputBean;
import cn.tootoo.servlet.BaseService;
import cn.tootoo.soa.oms.createorder.ThCreateOrderBaseService;
import cn.tootoo.soa.oms.createthorderboss.input.OmsCreateThOrderBossInputData;
import cn.tootoo.soa.oms.createthorderboss.input.OmsCreateThOrderBossTihuoGoodsListElementI;
import cn.tootoo.soa.oms.createthorderboss.output.OmsCreateThOrderBossOutputData;
import cn.tootoo.soa.oms.utils.LogUtils4Oms;
import cn.tootoo.soa.oms.utils.ThcardOrderBean;
import cn.tootoo.utils.ResponseUtil;

/**
 * 服务接口方法的实现类。
 * 
 * 服务description：订单服务。
 * 服务remark：订单服务。
 * 接口description：boss后台下提货订单。
 * 接口remark：boss后台下提货订单。
 */
public final class CreateThOrderBossImpl extends BaseService {
    
    
    /**
     * 日志对象。
     */
    private Logger logger = Logger.getLogger(CreateThOrderBossImpl.class);
    
    @Override
    public BaseOutputBean doService(BaseInputBean inputBean) throws Exception {
        BaseOutputBean outputBean = new BaseOutputBean();
        try {
            LogUtils4Oms.info(logger, uuid, "执行服务开始，传入bean", "inputBean", inputBean);
            OmsCreateThOrderBossOutputData outputData = new OmsCreateThOrderBossOutputData();
            if (inputBean.getInputData() == null) {
                LogUtils4Oms.info(logger, uuid, "参数req_str为空", "inputBean", inputBean);
                outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.REQUEST_PARAM_ERROR.getResultID(), null, "");
                return outputBean;
            }
            OmsCreateThOrderBossInputData inputData = null;
            try {
                inputData = (OmsCreateThOrderBossInputData)inputBean.getInputData();
            } catch (Exception e) {
                LogUtils4Oms.error(logger, uuid, "接口实现类转换错误", e, "className", OmsCreateThOrderBossInputData.class.getName());
                outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.CLASS_TRANSFER_ERROR.getResultID(), null, "");
                return outputBean;
            }
            
            
            // 构建thcardOrderBean
            ThcardOrderBean thcardOrderBean = new ThcardOrderBean();
            thcardOrderBean.setThCardCode(inputData.getThcardSn());
            thcardOrderBean.setThcardKdId(inputData.getThcardKdId());
            thcardOrderBean.setBuyerEmail(inputData.getBuyerEmail());
            thcardOrderBean.setBuyerFlag(inputData.getBuyerFlag());
            thcardOrderBean.setBuyerId(inputData.getBuyerId());
            thcardOrderBean.setProvince(inputData.getProvince());
            thcardOrderBean.setCity(inputData.getCity());
            thcardOrderBean.setDistrict(inputData.getDistrict());
            thcardOrderBean.setArea(inputData.getArea());
            thcardOrderBean.setReceiver(inputData.getReceiver());
            thcardOrderBean.setPos(inputData.getPos());
            thcardOrderBean.setPhonenum(inputData.getPhonenum());
            thcardOrderBean.setMobilenum(inputData.getMobilenum());
            thcardOrderBean.setAddr(inputData.getAddr());
            thcardOrderBean.setShipAddrId(inputData.getShipAddrId());
            thcardOrderBean.setGiftBoxId(inputData.getGiftBoxId());
            thcardOrderBean.setProvinceName(inputData.getProvinceName());
            thcardOrderBean.setCityName(inputData.getCityName());
            thcardOrderBean.setDistrictName(inputData.getDistrictName());
            thcardOrderBean.setAreaName(inputData.getAreaName());
            thcardOrderBean.setWarehouseId(inputData.getWarehouseId());
            thcardOrderBean.setConsiderShippingCapacity(inputData.getConsiderShippingCapacity());
            thcardOrderBean.setSpecifiedShippingDate(inputData.getSpecifiedShippingDate());
            thcardOrderBean.setRemark(inputData.getRemark());
            thcardOrderBean.setShipfee("0");
            thcardOrderBean.setDcId(inputData.getDcId());
            thcardOrderBean.setPlanDate(inputData.getPlanDate());
            thcardOrderBean.setSubstationID(inputData.getSubstationID());
            thcardOrderBean.setIp(inputBean.getInputHead().getIp());
            
            
            
            // 商品ID, 数量
            Map<Long, BigDecimal> map = new HashMap<Long, BigDecimal>();
            for (OmsCreateThOrderBossTihuoGoodsListElementI tihuoGood : inputData.getTihuoGoodsList()) {
                map.put(tihuoGood.getGoodsID(), new BigDecimal(tihuoGood.getGoodsCount()));
            }
            // Map<礼盒Id, Map<商品明细Id, 商品明细数量>>
            Map<String, Map<Long, BigDecimal>> giftBoxDetailMap = new HashMap<String, Map<Long, BigDecimal>>();
            // 存放礼盒中商品id，数量
            giftBoxDetailMap.put(thcardOrderBean.getGiftBoxId() + "", map);
            
            
            ThCreateOrderBaseService thCreateOrderBaseService = new ThCreateOrderBaseService(uuid, logger);
            return thCreateOrderBaseService.createThcardOrder(thcardOrderBean, giftBoxDetailMap, inputData.getScope(), outputBean, true, null, outputData);
            

        } catch (Exception e) {
            LogUtils4Oms.error(logger, uuid, "boss后台下提货订单出错", e);
            outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.INSIDE_ERROR.getResultID(), null, "");
            LogUtils4Oms.info(logger, uuid, "接口返回", "outputBean", outputBean);
            return outputBean;
        }
    }
    
    @Override
    public BaseService clone() throws CloneNotSupportedException {
        return new CreateThOrderBossImpl();
    }
    
}
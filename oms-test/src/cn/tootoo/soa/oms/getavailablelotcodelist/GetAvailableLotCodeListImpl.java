package cn.tootoo.soa.oms.getavailablelotcodelist;

import java.util.List;

import org.apache.log4j.Logger;

import cn.tootoo.db.egrocery.osorder.OsOrderDao;
import cn.tootoo.db.egrocery.osorder.OsOrderPO;
import cn.tootoo.db.egrocery.tosorderlot.TOsOrderLotDao;
import cn.tootoo.db.egrocery.tosorderlot.TOsOrderLotPO;
import cn.tootoo.db.egrocery.tosparentorder.TOsParentOrderDao;
import cn.tootoo.db.egrocery.tosparentorder.TOsParentOrderPO;
import cn.tootoo.enums.BaseResultEnum;
import cn.tootoo.http.bean.BaseInputBean;
import cn.tootoo.http.bean.BaseOutputBean;
import cn.tootoo.servlet.BaseService;
import cn.tootoo.soa.base.enums.LotTypeEnum;
import cn.tootoo.soa.oms.getavailablelotcodelist.input.OmsGetAvailableLotCodeListInputData;
import cn.tootoo.soa.oms.getavailablelotcodelist.output.OmsGetAvailableLotCodeListLotListElementO;
import cn.tootoo.soa.oms.getavailablelotcodelist.output.OmsGetAvailableLotCodeListOutputData;
import cn.tootoo.utils.Log;
import cn.tootoo.utils.ResponseUtil;

public class GetAvailableLotCodeListImpl extends BaseService {
    
    private Logger logger = Logger.getLogger(this.getClass());
    
    @Override
    public BaseOutputBean doService(BaseInputBean inputBean) throws Exception {
        
        BaseOutputBean outputBean = new BaseOutputBean();
        
        try {
            
            TOsParentOrderDao parentOrderDao = new TOsParentOrderDao(uuid, logger);
            OsOrderDao osOrderDao = new OsOrderDao(uuid, logger);
            TOsOrderLotDao lotDao = new TOsOrderLotDao(uuid, logger);
            
            Log.info(logger, uuid, "执行服务开始，传入bean", "inputBean", inputBean);
            OmsGetAvailableLotCodeListOutputData outputData = new OmsGetAvailableLotCodeListOutputData();
            if (inputBean.getInputData() == null) {
                Log.info(logger, uuid, "参数req_str为空", "inputBean", inputBean);
                outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.REQUEST_PARAM_ERROR.getResultID(), null, "");
                return outputBean;
            }
            
            OmsGetAvailableLotCodeListInputData inputData = null;
            try {
                inputData = (OmsGetAvailableLotCodeListInputData)inputBean.getInputData();
            } catch (Exception e) {
                Log.error(logger, "接口实现类转换错误", e, "className", OmsGetAvailableLotCodeListInputData.class.getName());
                outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.CLASS_TRANSFER_ERROR.getResultID(), null, "");
                return outputBean;
            }
            

            String lotSubstation = "";
            if("1".equals(inputData.getType())){
                List<TOsParentOrderPO> parentOrderList = parentOrderDao.findTOsParentOrderPOListBySql(" ORDER_CODE = '"
                                + inputData.getOrderCode() + "'", false, false, true);
                if (parentOrderList == null || parentOrderList.size() != 1) {
                    Log.info(logger, uuid, "无此父订单", "orderCode", inputData.getOrderCode());
                    outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.INSIDE_ERROR.getResultID(), null, "");
                    Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
                    return outputBean;
                }
                TOsParentOrderPO parentOrderPo = parentOrderList.get(0);
                lotSubstation = parentOrderPo.getSubstationId() + "1";
            }else{
                List<OsOrderPO> orderList = osOrderDao.findOsOrderPOListBySql(" ORDER_CODE = '"
                                + inputData.getOrderCode() + "'", false, false, true);
                if (orderList == null || orderList.size() != 1) {
                    Log.info(logger, uuid, "无此子订单", "orderCode", inputData.getOrderCode());
                    outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.INSIDE_ERROR.getResultID(), null, "");
                    Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
                    return outputBean;
                }
                OsOrderPO osOrder = orderList.get(0);
                lotSubstation = osOrder.getSubstationId() + "1";
            }
            
            StringBuffer sqlCondition = new StringBuffer(" STATUS = '1' and LOT_DATE = '" + inputData.getReceiveDt() + "' ");
            sqlCondition.append(" and LOT_NUM = " + LotTypeEnum.NORMAL.getLotMaxNum());
            sqlCondition.append(" and LOT_SUBSTATION = '" + lotSubstation + "'");
            Log.info(logger, uuid, "执行sql", "sqlCondition", sqlCondition);
            List<TOsOrderLotPO> orderLotList = lotDao.findTOsOrderLotPOListBySql(sqlCondition.toString());
            if (orderLotList != null && !orderLotList.isEmpty()) {
                for (TOsOrderLotPO orderLot : orderLotList) {
                    OmsGetAvailableLotCodeListLotListElementO lotElement = new OmsGetAvailableLotCodeListLotListElementO();
                    lotElement.setLotId(orderLot.getLotId());
                    lotElement.setLotCode(orderLot.getLotCode());
                    lotElement.setLotDate(orderLot.getLotDate());
                    lotElement.setLotType(orderLot.getLotType());
                    lotElement.setLotNum(orderLot.getLotNum());
                    lotElement.setMaxNum(orderLot.getMaxNum());
                    lotElement.setCurrNum(orderLot.getCurrNum());
                    outputData.getLotList().add(lotElement);
                }
            }
            
            outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.SUCCESS.getResultID(), outputData, "");
            Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
            return outputBean;
            
        } catch (Exception e) {
            Log.error(logger, uuid, "获得可用波次号出错", e);
            outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.INSIDE_ERROR.getResultID(), null, "");
            Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
            return outputBean;
        }
        
    }
    
    @Override
    public BaseService clone() throws CloneNotSupportedException {
        return new GetAvailableLotCodeListImpl();
    }
}
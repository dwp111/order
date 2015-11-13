package cn.tootoo.soa.oms.updatelotcode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import cn.tootoo.soa.base.enums.OrderStatusEnum;
import cn.tootoo.soa.base.enums.ParentOrderStatusEnum;
import cn.tootoo.soa.base.util.LockUtil;
import cn.tootoo.soa.oms.updatelotcode.input.OmsUpdateLotCodeInputData;
import cn.tootoo.soa.oms.updatelotcode.output.OmsUpdateLotCodeOutputData;
import cn.tootoo.utils.Log;
import cn.tootoo.utils.ResponseUtil;
import cn.tootoo.utils.StringUtil;

public class UpdateLotCodeImpl extends BaseService {
    
    private Logger logger = Logger.getLogger(this.getClass());
    
    @Override
    public BaseOutputBean doService(BaseInputBean inputBean) throws Exception {
        
        BaseOutputBean outputBean = new BaseOutputBean();
        
        try{
            
            OsOrderDao orderDao = new OsOrderDao(uuid, logger);
            TOsOrderLotDao lotDao = new TOsOrderLotDao(uuid, logger);
            TOsParentOrderDao parentOrderDao = new TOsParentOrderDao(uuid, logger);
            
            Log.info(logger, uuid, "执行服务开始，传入bean", "inputBean", inputBean);
            OmsUpdateLotCodeOutputData outputData = new OmsUpdateLotCodeOutputData();
            if (inputBean.getInputData() == null) {
                Log.info(logger, uuid, "参数req_str为空", "inputBean", inputBean);
                outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.REQUEST_PARAM_ERROR.getResultID(), null, "");
                return outputBean;
            }
            
            OmsUpdateLotCodeInputData inputData = null;
            try {
                inputData = (OmsUpdateLotCodeInputData)inputBean.getInputData();
            } catch (Exception e) {
                Log.error(logger, "接口实现类转换错误", e, "className", OmsUpdateLotCodeInputData.class.getName());
                outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.CLASS_TRANSFER_ERROR.getResultID(), null, "");
                return outputBean;
            }
            
            if (inputData.getType().equals("1")) {// 父订单
                
                Log.info(logger, uuid, "修改父订单波次号");
                try {
                    Log.info(logger, uuid, "锁表", "orderCode", inputData.getOrderCode());
                    boolean lock = LockUtil.lock(logger, uuid, inputData.getOrderCode(), 1, "修改父订单波次号");
                    if (!lock) {
                        Log.info(logger, uuid, "加锁失败", "orderCode", inputData.getOrderCode());
                        outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.INSIDE_ERROR.getResultID(), null, "");
                        Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
                        return outputBean;
                    }
                } catch (Exception e1) {
                    Log.error(logger, "加锁失败", e1, "orderCode", inputData.getOrderCode());
                    outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.INSIDE_ERROR.getResultID(), null, "");
                    Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
                    return outputBean;
                }
                
                try {
                    List<TOsParentOrderPO> parentOrderList = parentOrderDao.findTOsParentOrderPOListBySql(" ORDER_CODE = '"
                                    + inputData.getOrderCode() + "'", false, true, false);
                    if (parentOrderList == null || parentOrderList.size() != 1) {
                        Log.info(logger, uuid, "无此订单", "orderCode", inputData.getOrderCode());
                        outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.INSIDE_ERROR.getResultID(), null, "");
                        Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
                        return outputBean;
                    }
                    TOsParentOrderPO parentOrderPo = parentOrderList.get(0);
                    if (!ParentOrderStatusEnum.canUpdateLotCode(parentOrderPo.getOrderStatus().intValue())) {// 不是已分拨
                        Log.info(logger, uuid, "此订单不是已分波订单", "orderCode", inputData.getOrderCode(), "orderStatus", parentOrderPo.getOrderStatus());
                        outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.INSIDE_ERROR.getResultID(), null, "");
                        Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
                        return outputBean;
                    }
                    if(parentOrderPo.getLotCode().equals(inputData.getLotCode())){//不需要修改波次号
                        Log.info(logger, uuid, "此父订单已经是该波次号", "orderCode", inputData.getOrderCode(), "lotCode", parentOrderPo.getLotCode());
                        outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.INSIDE_ERROR.getResultID(), null, "");
                        Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
                        return outputBean;
                    }
                    
                    String sqlCondition = "STATUS='1' and LOT_DATE = '"
                                    + new SimpleDateFormat("yyyyMMdd").format(parentOrderPo.getReceiveDt())
                                    + "' and LOT_CODE = '"
                                    + inputData.getLotCode()
                                    + "' and LOT_NUM=" + LotTypeEnum.NORMAL.getLotMaxNum()
                                    + " and LOT_SUBSTATION='" + parentOrderPo.getSubstationId() + "1'";
                    Log.info(logger, uuid, "执行sql", "sqlCondition", sqlCondition);
                    List<TOsOrderLotPO> orderLotList = lotDao.findTOsOrderLotPOListBySql(sqlCondition, false, true, false);
                    if (orderLotList == null || orderLotList.size() != 1) {// 找不到此波次或者此波次不是最大波次或波次分站与订单分站不一致
                        Log.info(logger, uuid, "找不到此波次或者此波次不是最大波次或波次分站与订单分站不一致", "orderCode", inputData.getOrderCode());
                        outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.INSIDE_ERROR.getResultID(), null, "");
                        Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
                        return outputBean;
                    }
                    List<TOsOrderLotPO> orderLotListOld = lotDao.findTOsOrderLotPOListBySql(" LOT_CODE = '"
                                    + parentOrderPo.getLotCode() + "'", false, true, false);
                    if (orderLotListOld == null || orderLotListOld.size() != 1) {
                        Log.info(logger, uuid, "找不到此订单的波次", "orderCode", inputData.getOrderCode());
                        outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.INSIDE_ERROR.getResultID(), null, "");
                        Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
                        return outputBean;
                    }
                    
                    TOsOrderLotPO orderLotPo = orderLotList.get(0);
                    TOsOrderLotPO orderLotPoOld = orderLotListOld.get(0);
                    orderLotPo.setVariationCurrNum(1L);
                    if(orderLotPoOld.getCurrNum().intValue() > 0){
                        orderLotPoOld.setVariationCurrNum(-1L);
                    }
                    
                    List<TOsOrderLotPO> conditionList = new ArrayList<TOsOrderLotPO>();
                    conditionList.add(orderLotPo);
                    conditionList.add(orderLotPoOld);
                    int resultNum = lotDao.updateTOsOrderLotPOList(conditionList);
                    if (resultNum != conditionList.size()) {// 修改失败
                        Log.info(logger, uuid, "修改波次失败", "orderCode", inputData.getOrderCode());
                        lotDao.rollback();
                        outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.INSIDE_ERROR.getResultID(), null, "");
                        Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
                        return outputBean;
                    }
                    
                    List<Object[]> updateSet = new ArrayList<Object[]>();
                    Object[] orderStatusObj = {"LOT_CODE", inputData.getLotCode()};
                    updateSet.add(orderStatusObj);
                    List<Object[]> updateCondition = new ArrayList<Object[]>();
                    Object[] orderIdConditionObj = {"ORDER_ID", "=", parentOrderPo.getOrderId()};
                    updateCondition.add(orderIdConditionObj);
                    Object[] orderStatusConditionObj = {"ORDER_STATUS", "=", ParentOrderStatusEnum.SPLIT_LOT_SUCCESS.getStatus()};
                    updateCondition.add(orderStatusConditionObj);
                    Log.info(logger, uuid, "获得条件", "updateSet", StringUtil.transferObjectList(updateSet), "updateCondition", StringUtil.transferObjectList(updateCondition));
                    
                    resultNum = parentOrderDao.updateTOsParentOrderPOByCondition(updateSet, updateCondition);
                    if (resultNum != 1) {// 修改失败
                        Log.info(logger, uuid, "修改订单的波次号失败", "orderCode", inputData.getOrderCode());
                        parentOrderDao.rollback();
                        outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.INSIDE_ERROR.getResultID(), null, "");
                        Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
                        return outputBean;
                    }
                    parentOrderDao.commit();
                    
                    outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.SUCCESS.getResultID(), outputData, "");
                    Log.info(logger, uuid, "修改成功,接口返回", "outputBean", outputBean);
                    return outputBean;
                    
                } catch (Exception e) {
                    Log.error(logger, "修改父订单波次号失败", e, "orderCode", inputData.getOrderCode());
                    parentOrderDao.rollback();
                    outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.INSIDE_ERROR.getResultID(), null, "");
                    return outputBean;
                } finally {
                    try {
                        Log.info(logger, uuid, "解锁", "orderCode", inputData.getOrderCode());
                        LockUtil.unlock(logger, uuid, inputData.getOrderCode());
                    } catch (Exception e) {
                        Log.error(logger, "解锁出错", e, "orderCode", inputData.getOrderCode());
                    }
                }
            } else {// 子订单
            
                Log.info(logger, uuid, "修改子订单波次号");
                
                try {
                    List<OsOrderPO> orderList = orderDao.findOsOrderPOListBySql(" ORDER_CODE = '"
                                    + inputData.getOrderCode() + "'", false, true, false);
                    if (orderList == null || orderList.size() != 1) {
                        Log.info(logger, uuid, "无此子订单", "orderCode", inputData.getOrderCode());
                        outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.INSIDE_ERROR.getResultID(), null, "");
                        Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
                        return outputBean;
                    }
                    OsOrderPO orderPo = orderList.get(0);
                    if (!OrderStatusEnum.canUpdateLotCode(orderPo.getOrderStatus())) {// 不允许修改子订单波次号
                        Log.info(logger, uuid, "子订单状态不允许修改波次号", "orderCode", inputData.getOrderCode(), "orderStatus", orderPo.getOrderStatus());
                        outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.INSIDE_ERROR.getResultID(), null, "");
                        Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
                        return outputBean;
                    }
                    if(orderPo.getLotCode().equals(inputData.getLotCode())){//不需要修改波次号
                        Log.info(logger, uuid, "此子订单已经是该波次号", "orderCode", inputData.getOrderCode(), "lotCode", orderPo.getLotCode());
                        outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.INSIDE_ERROR.getResultID(), null, "");
                        Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
                        return outputBean;
                    }
                    
                    String sqlCondition = "STATUS='1' and LOT_DATE = '"
                                    + new SimpleDateFormat("yyyyMMdd").format(orderPo.getReceiveDt())
                                    + "' and LOT_CODE = '"
                                    + inputData.getLotCode()
                                    + "' and LOT_NUM=" + LotTypeEnum.NORMAL.getLotMaxNum()
                                    + " and LOT_SUBSTATION='" + orderPo.getSubstationId() + "1'";
                    Log.info(logger, uuid, "执行sql", "sqlCondition", sqlCondition);
                    List<TOsOrderLotPO> orderLotList = lotDao.findTOsOrderLotPOListBySql(sqlCondition, false, true, false);
                    if (orderLotList == null || orderLotList.size() != 1) {// 找不到此波次或者此波次不是最大波次或波次分站与订单分站不一致
                        Log.info(logger, uuid, "找不到此波次或者此波次不是最大波次或波次分站与订单分站不一致", "orderCode", inputData.getOrderCode());
                        outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.INSIDE_ERROR.getResultID(), null, "");
                        Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
                        return outputBean;
                    }
                    
                    List<TOsOrderLotPO> orderLotListOld = lotDao.findTOsOrderLotPOListBySql(" LOT_CODE = '"
                                    + orderPo.getLotCode() + "'", false, true, false);
                    if (orderLotListOld == null || orderLotListOld.size() != 1) {
                        Log.info(logger, uuid, "找不到此订单的波次", "orderCode", inputData.getOrderCode());
                        outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.INSIDE_ERROR.getResultID(), null, "");
                        Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
                        return outputBean;
                    }
                    
                    List<Object[]> updateSet = new ArrayList<Object[]>();
                    Object[] orderStatusObj = {"LOT_CODE", inputData.getLotCode()};
                    updateSet.add(orderStatusObj);
                    List<Object[]> updateCondition = new ArrayList<Object[]>();
                    Object[] orderIdConditionObj = {"ORDER_ID", "=", orderPo.getOrderId()};
                    updateCondition.add(orderIdConditionObj);
                    Object[] orderStatusConditionObj = {"ORDER_STATUS", "=", orderPo.getOrderStatus()};
                    updateCondition.add(orderStatusConditionObj);
                    Log.info(logger, uuid, "获得条件", "updateSet", StringUtil.transferObjectList(updateSet), "updateCondition", StringUtil.transferObjectList(updateCondition));
                    
                    int resultNum = orderDao.updateOsOrderPOByCondition(updateSet, updateCondition);
                    if (resultNum != 1) {// 修改失败
                        Log.info(logger, uuid, "修改订单的波次号失败", "orderCode", inputData.getOrderCode());
                        orderDao.rollback();
                        outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.INSIDE_ERROR.getResultID(), null, "");
                        Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
                        return outputBean;
                    }
                    orderDao.commit();
                    
                    outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.SUCCESS.getResultID(), outputData, "");
                    Log.info(logger, uuid, "修改成功,接口返回", "outputBean", outputBean);
                    return outputBean;
                    
                } catch (Exception e) {
                    Log.error(logger, "修改子订单波次号失败", e, "orderCode", inputData.getOrderCode());
                    orderDao.rollback();
                    outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.INSIDE_ERROR.getResultID(), null, "");
                    return outputBean;
                }
            }
            
        }catch(Exception e){
            Log.error(logger, uuid, "修改波次号出错", e);
            outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.INSIDE_ERROR.getResultID(), null, "");
            Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
            return outputBean;
        }
        
    }
    
    @Override
    public BaseService clone() throws CloneNotSupportedException {
        return new UpdateLotCodeImpl();
    }
}
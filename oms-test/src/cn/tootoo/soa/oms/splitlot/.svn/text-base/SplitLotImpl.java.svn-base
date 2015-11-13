package cn.tootoo.soa.oms.splitlot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import cn.tootoo.db.egrocery.osorder.OsOrderDao;
import cn.tootoo.db.egrocery.osorder.OsOrderPO;
import cn.tootoo.db.egrocery.tosorderlot.TOsOrderLotDao;
import cn.tootoo.db.egrocery.tosparentorder.TOsParentOrderDao;
import cn.tootoo.db.egrocery.tosparentorder.TOsParentOrderPO;
import cn.tootoo.db.egrocery.tosparentorderitems.TOsParentOrderItemsDao;
import cn.tootoo.db.egrocery.tosparentorderitems.TOsParentOrderItemsPO;
import cn.tootoo.enums.BaseResultEnum;
import cn.tootoo.http.bean.BaseInputBean;
import cn.tootoo.http.bean.BaseOutputBean;
import cn.tootoo.servlet.BaseService;
import cn.tootoo.soa.base.enums.LotTypeEnum;
import cn.tootoo.soa.base.enums.ParentOrderStatusEnum;
import cn.tootoo.soa.oms.initorderlot.InitOrderLot;
import cn.tootoo.soa.oms.splitlot.input.OmsSplitLotInputData;
import cn.tootoo.soa.oms.splitlot.output.OmsSplitLotOutputData;
import cn.tootoo.utils.Log;
import cn.tootoo.utils.ResponseUtil;
import cn.tootoo.utils.StringUtil;

public class SplitLotImpl extends BaseService {
    
    private Logger logger = Logger.getLogger(this.getClass());
    
    @Override
    public BaseOutputBean doService(BaseInputBean inputBean) throws Exception {
        
        BaseOutputBean outputBean = new BaseOutputBean();
        
        try{
            
            Log.info(logger, uuid, "执行服务开始，传入bean", "inputBean", inputBean);
            OmsSplitLotOutputData outputData = new OmsSplitLotOutputData();
            if (inputBean.getInputData() == null) {
                Log.info(logger, uuid, "参数req_str为空", "inputBean", inputBean);
                outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.REQUEST_PARAM_ERROR.getResultID(), null, "");
                return outputBean;
            }
            
            OmsSplitLotInputData inputData = null;
            try {
                inputData = (OmsSplitLotInputData)inputBean.getInputData();
            } catch (Exception e) {
                Log.error(logger, "接口实现类转换错误", e, "className", OmsSplitLotInputData.class.getName());
                outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.CLASS_TRANSFER_ERROR.getResultID(), null, "");
                return outputBean;
            }
            
            TOsParentOrderDao parentOrderDao = new TOsParentOrderDao(uuid, logger);
            TOsParentOrderItemsDao parentOrderItemsDao = new TOsParentOrderItemsDao(uuid, logger);
            OsOrderDao orderDao = new OsOrderDao(uuid, logger);
            TOsOrderLotDao orderLotDao = new TOsOrderLotDao(uuid, logger);
            
            List<TOsParentOrderPO> parentOrderList = parentOrderDao.findTOsParentOrderPOListBySql(" ORDER_CODE = '"
                            + inputData.getOrderCode() + "'", false, false, true);
            if (parentOrderList == null || parentOrderList.size() != 1) {
                Log.info(logger, uuid, "无此父订单", "orderCode", inputData.getOrderCode());
                outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.INSIDE_ERROR.getResultID(), null, "");
                Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
                return outputBean;
            }
            TOsParentOrderPO parentOrderPo = parentOrderList.get(0);
            
            
            boolean purchaseFlag = false;
            if("1".equals(parentOrderPo.getSalenumAdjust())){
                purchaseFlag = true;
            }else{
                List<Object[]> itemCondition = new ArrayList<Object[]>();
                itemCondition.add(new Object[]{"ORDER_ID", "=", parentOrderPo.getOrderId()});
                List<TOsParentOrderItemsPO> parentItemList = parentOrderItemsDao.findTOsParentOrderItemsPOListByCondition(itemCondition, false, false, true);
                for (TOsParentOrderItemsPO item : parentItemList) {
                    if (item.getPurchaseDate() != null) {
                        purchaseFlag = true; // 预定商品
                        break;
                    }
                }
            }
            
            
            if(inputData.getType().equals("0")){//定时任务调用
                
                if (parentOrderPo.getOrderStatus().intValue() == ParentOrderStatusEnum.SPLIT_LOT_SUCCESS.getStatus()
                                || parentOrderPo.getOrderStatus().intValue() >= ParentOrderStatusEnum.SPLIT_ORDER_SUCCESS.getStatus()) {
                    Log.info(logger, uuid, "此父订单已分波成功", "orderCode", inputData.getOrderCode(), "orderStatus", parentOrderPo.getOrderStatus());
                    outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.SUCCESS.getResultID(), outputData, "");
                    Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
                    return outputBean;
                }

                if (parentOrderPo.getOrderStatus().intValue() == ParentOrderStatusEnum.SDC_FAILED.getStatus()
                                || parentOrderPo.getOrderStatus().intValue() <= ParentOrderStatusEnum.PAY_FAILED.getStatus()) {
                    Log.info(logger, uuid, "此父订单状态非法,不能分波", "orderCode", inputData.getOrderCode(), "orderStatus", parentOrderPo.getOrderStatus());
                    outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.INSIDE_ERROR.getResultID(), null, "");
                    Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
                    return outputBean;
                }
                
                if(!StringUtil.isEmpty(parentOrderPo.getLotCode())){
                    Log.info(logger, uuid, "此父订单已分波成功", "orderCode", inputData.getOrderCode(), "orderStatus", parentOrderPo.getOrderStatus(), "lotCode", parentOrderPo.getLotCode());
                    outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.SUCCESS.getResultID(), outputData, "");
                    Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
                    return outputBean;
                }
                
                
                Log.info(logger, uuid, "初始化波次开始", "orderCode", inputData.getOrderCode());
                InitOrderLot initOrderLot = new InitOrderLot();
                int resultNum = initOrderLot.initOrderLot(logger, uuid, parentOrderPo.getSubstationId(), new SimpleDateFormat("yyyyMMdd").format(parentOrderPo.getReceiveDt()), orderLotDao);
                if (resultNum < 0) {
                    Log.info(logger, uuid, "初始化波次失败", "orderCode", inputData.getOrderCode());
                    outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.INSIDE_ERROR.getResultID(), null, "");
                    Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
                    return outputBean;
                }
                Log.info(logger, uuid, "初始化波次结束", "orderCode", inputData.getOrderCode());
                
                
                Log.info(logger, uuid, "获得波次号开始", "orderCode", inputData.getOrderCode());
                String newLotCode = "";
                LotTypeEnum typeEnum = initOrderLot.getOrderLotTypeEnum(parentOrderPo.getDeliveryTimeType(), parentOrderPo.getOrderType(), parentOrderPo.getDeliveryType(), parentOrderPo.getOrderFrom(), purchaseFlag);
                if (typeEnum != null) {
                    newLotCode = initOrderLot.getOrderLotCode(logger, uuid, parentOrderPo.getSubstationId(), new SimpleDateFormat("yyyyMMdd").format(parentOrderPo.getReceiveDt()), typeEnum, orderLotDao);
                }
                if (newLotCode == null || "".equals(newLotCode)) {// 获得新波次号失败
                    Log.info(logger, uuid, "获得波次号失败", "orderCode", inputData.getOrderCode());
                    parentOrderDao.rollback();
                    outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.INSIDE_ERROR.getResultID(), null, "");
                    Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
                    return outputBean;
                }
                Log.info(logger, uuid, "获得波次号结束", "orderCode", inputData.getOrderCode(), "newLotCode", newLotCode);
                
                
                Log.info(logger, uuid, "修改父订单信息开始", "orderCode", inputData.getOrderCode());
                List<Object[]> updateSet = new ArrayList<Object[]>();
                Object[] lotCodeObj = {"LOT_CODE", newLotCode};
                updateSet.add(lotCodeObj);
                List<Object[]> updateCondition = new ArrayList<Object[]>();
                Object[] orderIdConditionObj = {"ORDER_ID", "=", parentOrderPo.getOrderId()};
                updateCondition.add(orderIdConditionObj);
                Object[] orderStatusConditionObj = {"ORDER_STATUS", "=", parentOrderPo.getOrderStatus()};
                updateCondition.add(orderStatusConditionObj);
                Log.info(logger, uuid, "获得条件", "updateSet", StringUtil.transferObjectList(updateSet), "updateCondition", StringUtil.transferObjectList(updateCondition));
                
                resultNum = parentOrderDao.updateTOsParentOrderPOByCondition(updateSet, updateCondition);
                if (resultNum != 1) {// 操作数据库失败
                    Log.info(logger, uuid, "操作数据库修改父订单信息失败", "orderCode", inputData.getOrderCode(), "resultNum", resultNum);
                    parentOrderDao.rollback();
                    outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.INSIDE_ERROR.getResultID(), null, "");
                    Log.info(logger, uuid, "接口返回", "orderCode", inputData.getOrderCode(), "outputBean", outputBean);
                    return outputBean;
                }
                Log.info(logger, uuid, "修改父订单结束", "orderCode", inputData.getOrderCode());
                
                
                Log.info(logger, uuid, "修改波次表+1开始", "orderCode", inputData.getOrderCode());
                List<Object[]> updateSetLot = new ArrayList<Object[]>();
                updateSetLot.add(new Object[]{"CURR_NUM", 1, "+"});
                List<Object[]> updateConditionLot = new ArrayList<Object[]>();
                updateConditionLot.add(new Object[]{"LOT_CODE", "=", newLotCode});
                updateConditionLot.add(new Object[]{"CURR_NUM", "<>", LotTypeEnum.NORMAL.getN()});
                Log.info(logger, uuid, "获得条件", "updateSetLot", StringUtil.transferObjectList(updateSetLot), "updateConditionLot", StringUtil.transferObjectList(updateConditionLot));
                
                resultNum = orderLotDao.updateTOsOrderLotPOByCondition(updateSetLot, updateConditionLot);
                if (resultNum != 1) {// 操作数据库失败
                    Log.info(logger, uuid, "修改波次表+1失败,可能原因波次满时并发修改", "orderCode", inputData.getOrderCode(), "resultNum", resultNum);
                    orderLotDao.rollback();
                    outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.INSIDE_ERROR.getResultID(), null, "");
                    Log.info(logger, uuid, "接口返回", "orderCode", inputData.getOrderCode(), "outputBean", outputBean);
                    return outputBean;
                }
                Log.info(logger, uuid, "修改波次表+1结束", "orderCode", inputData.getOrderCode());
                
                
                parentOrderDao.commit();
                
                outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.SUCCESS.getResultID(), outputData, "");
                return outputBean;
                
            }else{
                 
                if(inputData.getType().equals("1")){//boss后台调用(父订单取消当日达)
                    if (parentOrderPo.getOrderStatus().intValue() == ParentOrderStatusEnum.SPLIT_ORDER_SUCCESS.getStatus()
                                    || parentOrderPo.getOrderStatus().intValue() >= ParentOrderStatusEnum.SPLIT_PAY_SUCCESS.getStatus()) {
                        Log.info(logger, uuid, "此父订单状态非法,不能分波", "orderCode", inputData.getOrderCode(), "orderStatus", parentOrderPo.getOrderStatus());
                        outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.INSIDE_ERROR.getResultID(), null, "");
                        Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
                        return outputBean;
                    }

                    if (parentOrderPo.getOrderStatus().intValue() != ParentOrderStatusEnum.SPLIT_LOT_SUCCESS.getStatus()
                                    && parentOrderPo.getOrderStatus().intValue() != ParentOrderStatusEnum.SPLIT_ORDER_FAILED.getStatus()) {
                        Log.info(logger, uuid, "此父订单还没进行分波,返回操作成功,等待自动分波", "orderCode", inputData.getOrderCode(), "orderStatus", parentOrderPo.getOrderStatus());
                        outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.SUCCESS.getResultID(), outputData, "");
                        Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
                        return outputBean;
                    }
                }
                
                Log.info(logger, uuid, "初始化波次开始", "orderCode", inputData.getOrderCode());
                InitOrderLot initOrderLot = new InitOrderLot();
                int resultNum = initOrderLot.initOrderLot(logger, uuid, parentOrderPo.getSubstationId(), new SimpleDateFormat("yyyyMMdd").format(parentOrderPo.getReceiveDt()), orderLotDao);
                if (resultNum < 0) {
                    Log.info(logger, uuid, "初始化波次失败", "orderCode", inputData.getOrderCode());
                    outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.INSIDE_ERROR.getResultID(), null, "");
                    Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
                    return outputBean;
                }
                Log.info(logger, uuid, "初始化波次结束", "orderCode", inputData.getOrderCode());
                
                Log.info(logger, uuid, "获得波次号开始", "orderCode", inputData.getOrderCode());
                String newLotCode = "";
                LotTypeEnum typeEnum = initOrderLot.getOrderLotTypeEnum(parentOrderPo.getDeliveryTimeType(), parentOrderPo.getOrderType(), parentOrderPo.getDeliveryType(), parentOrderPo.getOrderFrom(), purchaseFlag);
                if (typeEnum != null) {
                    newLotCode = initOrderLot.getOrderLotCode(logger, uuid, parentOrderPo.getSubstationId(), new SimpleDateFormat("yyyyMMdd").format(parentOrderPo.getReceiveDt()), typeEnum, orderLotDao);
                }
                if (newLotCode == null || "".equals(newLotCode)) {// 获得新波次号失败
                    Log.info(logger, uuid, "获得波次号失败", "orderCode", inputData.getOrderCode());
                    outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.INSIDE_ERROR.getResultID(), null, "");
                    Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
                    return outputBean;
                }
                Log.info(logger, uuid, "获得波次号结束", "orderCode", inputData.getOrderCode(), "newLotCode", newLotCode);
                
                if(newLotCode.equals(parentOrderPo.getLotCode())){//新波次号与老波次号相同
                    Log.info(logger, uuid, "新波次号与老波次号相同", "orderCode", inputData.getOrderCode());
                    outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.SUCCESS.getResultID(), outputData, "");
                    Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
                    return outputBean;
                }
                
                Log.info(logger, uuid, "修改父订单信息开始", "orderCode", inputData.getOrderCode());
                List<Object[]> updateSet = new ArrayList<Object[]>();
                Object[] lotCodeObj = {"LOT_CODE", newLotCode};
                updateSet.add(lotCodeObj);
                List<Object[]> updateCondition = new ArrayList<Object[]>();
                Object[] orderIdConditionObj = {"ORDER_ID", "=", parentOrderPo.getOrderId()};
                updateCondition.add(orderIdConditionObj);
                Object[] orderStatusConditionObj = {"ORDER_STATUS", "=", parentOrderPo.getOrderStatus()};
                updateCondition.add(orderStatusConditionObj);
                Log.info(logger, uuid, "获得条件", "updateSet", StringUtil.transferObjectList(updateSet), "updateCondition", StringUtil.transferObjectList(updateCondition));
                
                resultNum = parentOrderDao.updateTOsParentOrderPOByCondition(updateSet, updateCondition);
                if (resultNum != 1) {// 操作数据库失败
                    Log.info(logger, uuid, "操作数据库修改父订单信息失败", "orderCode", inputData.getOrderCode(), "resultNum", resultNum);
                    parentOrderDao.rollback();
                    outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.INSIDE_ERROR.getResultID(), null, "");
                    Log.info(logger, uuid, "接口返回", "orderCode", inputData.getOrderCode(), "outputBean", outputBean);
                    return outputBean;
                }
                Log.info(logger, uuid, "修改父订单结束", "orderCode", inputData.getOrderCode());
                
                Log.info(logger, uuid, "修改波次表+1开始", "orderCode", inputData.getOrderCode());
                List<Object[]> updateSetLot = new ArrayList<Object[]>();
                updateSetLot.add(new Object[]{"CURR_NUM", 1, "+"});
                List<Object[]> updateConditionLot = new ArrayList<Object[]>();
                updateConditionLot.add(new Object[]{"LOT_CODE", "=", newLotCode});
                updateConditionLot.add(new Object[]{"CURR_NUM", "<>", LotTypeEnum.NORMAL.getN()});
                Log.info(logger, uuid, "获得条件", "updateSetLot", StringUtil.transferObjectList(updateSetLot), "updateConditionLot", StringUtil.transferObjectList(updateConditionLot));
                
                resultNum = orderLotDao.updateTOsOrderLotPOByCondition(updateSetLot, updateConditionLot);
                if (resultNum != 1) {// 操作数据库失败
                    Log.info(logger, uuid, "修改波次表+1失败,可能原因波次满时并发修改", "orderCode", inputData.getOrderCode(), "resultNum", resultNum);
                    orderLotDao.rollback();
                    outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.INSIDE_ERROR.getResultID(), null, "");
                    Log.info(logger, uuid, "接口返回", "orderCode", inputData.getOrderCode(), "outputBean", outputBean);
                    return outputBean;
                }
                Log.info(logger, uuid, "修改波次表+1结束", "orderCode", inputData.getOrderCode());
                
                if(parentOrderPo.getLotCode()!=null && !parentOrderPo.getLotCode().equals("")){
                    Log.info(logger, uuid, "修改波次表老的当日达波次-1开始", "orderCode", inputData.getOrderCode());
                    List<Object[]> updateSetLotOld = new ArrayList<Object[]>();
                    updateSetLotOld.add(new Object[]{"CURR_NUM", -1, "+"});
                    List<Object[]> updateConditionLotOld = new ArrayList<Object[]>();
                    Object[] lotCodeObjLotOld = {"LOT_CODE", "=", parentOrderPo.getLotCode()};
                    updateConditionLotOld.add(lotCodeObjLotOld);
                    Log.info(logger, uuid, "获得条件", "orderLotPoOld", StringUtil.transferObjectList(updateSetLotOld), "updateConditionLotOld", StringUtil.transferObjectList(updateConditionLotOld));
                    
                    orderLotDao.updateTOsOrderLotPOByCondition(updateSetLotOld, updateConditionLotOld);
                    Log.info(logger, uuid, "修改波次表老的当日达波次-1结束", "orderCode", inputData.getOrderCode());
                    
                    
                    
                    if(inputData.getType().equals("2")){//子订单取消当日达
                        List<OsOrderPO> subOrderList = orderDao.findOsOrderPOListBySql(" PARENT_CODE = '" + parentOrderPo.getOrderCode() + "'", false, false, true);
                        if(subOrderList==null || subOrderList.isEmpty()){
                            Log.info(logger, uuid, "此父订单没有子订单", "orderCode", inputData.getOrderCode());
                            parentOrderDao.rollback();
                            outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.INSIDE_ERROR.getResultID(), null, "");
                            Log.info(logger, uuid, "接口返回", "orderCode", inputData.getOrderCode(), "outputBean", outputBean);
                            return outputBean;
                        }
                        
                        for(OsOrderPO os : subOrderList){
                            List<Object[]> updateSetSubOrder = new ArrayList<Object[]>();
                            Object[] subOrderLot = {"LOT_CODE", newLotCode};
                            updateSetSubOrder.add(subOrderLot);
                            List<Object[]> updateConditionSubOrder = new ArrayList<Object[]>();
                            Object[] orderIdObj = {"ORDER_ID", "=", os.getOrderId()};
                            updateConditionSubOrder.add(orderIdObj);
                            Object[] orderStatusObj = {"ORDER_STATUS", "=", os.getOrderStatus()};
                            updateConditionSubOrder.add(orderStatusObj);
                            Log.info(logger, uuid, "获得条件", "updateSetSubOrder", StringUtil.transferObjectList(updateSetSubOrder), "updateConditionSubOrder", StringUtil.transferObjectList(updateConditionSubOrder));
                            resultNum = orderDao.updateOsOrderPOByCondition(updateSetSubOrder, updateConditionSubOrder);
                            if (resultNum != 1) {// 操作数据库失败
                                Log.info(logger, uuid, "操作数据库修改子订单信息失败", "orderCode", inputData.getOrderCode(), "subOrderCode", os.getOrderCode(), "resultNum", resultNum);
                                parentOrderDao.rollback();
                                outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.INSIDE_ERROR.getResultID(), null, "");
                                Log.info(logger, uuid, "接口返回", "orderCode", inputData.getOrderCode(), "outputBean", outputBean);
                                return outputBean;
                            }
                        }
                    }
                }
                
                parentOrderDao.commit();
                
                outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.SUCCESS.getResultID(), outputData, "");
                return outputBean;
                
            }
            
        }catch(Exception e){
            Log.error(logger, uuid, "分波出错", e);
            outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.INSIDE_ERROR.getResultID(), null, "");
            Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
            return outputBean;
        }
        
        
        
        
    }
    
    @Override
    public BaseService clone() throws CloneNotSupportedException {
        return new SplitLotImpl();
    }
    
}
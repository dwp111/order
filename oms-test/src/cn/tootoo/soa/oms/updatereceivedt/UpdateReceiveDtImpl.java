package cn.tootoo.soa.oms.updatereceivedt;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import cn.tootoo.db.egrocery.osorder.OsOrderDao;
import cn.tootoo.db.egrocery.osorder.OsOrderPO;
import cn.tootoo.db.egrocery.osorderitems.OsOrderItemsDao;
import cn.tootoo.db.egrocery.osorderitems.OsOrderItemsPO;
import cn.tootoo.db.egrocery.osorderopt.OsOrderOptDao;
import cn.tootoo.db.egrocery.osorderopt.OsOrderOptPO;
import cn.tootoo.db.egrocery.tosorderlot.TOsOrderLotDao;
import cn.tootoo.db.egrocery.tosorderlot.TOsOrderLotPO;
import cn.tootoo.db.egrocery.tosparentorder.TOsParentOrderDao;
import cn.tootoo.db.egrocery.tosparentorder.TOsParentOrderPO;
import cn.tootoo.db.egrocery.tosparentorderitems.TOsParentOrderItemsPO;
import cn.tootoo.db.egrocery.tosparentorderopt.TOsParentOrderOptDao;
import cn.tootoo.db.egrocery.tosparentorderopt.TOsParentOrderOptPO;
import cn.tootoo.enums.BaseResultEnum;
import cn.tootoo.http.bean.BaseInputBean;
import cn.tootoo.http.bean.BaseOutputBean;
import cn.tootoo.servlet.BaseService;
import cn.tootoo.soa.base.enums.LotTypeEnum;
import cn.tootoo.soa.base.enums.OrderStatusEnum;
import cn.tootoo.soa.base.enums.ParentOrderStatusEnum;
import cn.tootoo.soa.base.util.LockUtil;
import cn.tootoo.soa.oms.initorderlot.InitOrderLot;
import cn.tootoo.soa.oms.updatereceivedt.input.OmsUpdateReceiveDtInputData;
import cn.tootoo.soa.oms.updatereceivedt.output.OmsUpdateReceiveDtOutputData;
import cn.tootoo.utils.Log;
import cn.tootoo.utils.ResponseUtil;
import cn.tootoo.utils.StringUtil;

public class UpdateReceiveDtImpl extends BaseService {
    
    private Logger logger = Logger.getLogger(this.getClass());
    
    @Override
    public BaseOutputBean doService(BaseInputBean inputBean) throws Exception {
        
        BaseOutputBean outputBean = new BaseOutputBean();
        
        try{
            
            OsOrderDao orderDao = new OsOrderDao(uuid, logger);
            OsOrderItemsDao orderItemsDao = new OsOrderItemsDao(uuid, logger);
            OsOrderOptDao optDao = new OsOrderOptDao(uuid, logger);
            TOsOrderLotDao orderLotDao = new TOsOrderLotDao(uuid, logger);
            TOsParentOrderDao parentOrderDao = new TOsParentOrderDao(uuid, logger);
            TOsParentOrderOptDao parentOptDao = new TOsParentOrderOptDao(uuid, logger);
            
            Log.info(logger, uuid, "执行服务开始，传入bean", "inputBean", inputBean);
            OmsUpdateReceiveDtOutputData outputData = new OmsUpdateReceiveDtOutputData();
            if (inputBean.getInputData() == null) {
                Log.info(logger, uuid, "参数req_str为空", "inputBean", inputBean);
                outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.REQUEST_PARAM_ERROR.getResultID(), null, "");
                return outputBean;
            }
            
            OmsUpdateReceiveDtInputData inputData = null;
            try {
                inputData = (OmsUpdateReceiveDtInputData)inputBean.getInputData();
            } catch (Exception e) {
                Log.error(logger, "接口实现类转换错误", e, "className", OmsUpdateReceiveDtInputData.class.getName());
                outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.CLASS_TRANSFER_ERROR.getResultID(), null, "");
                return outputBean;
            }
            
            if (inputData.getType().equals("1")) {// 父订单
            
                Log.info(logger, uuid, "父订单修改配送日期");
                try {
                    Log.info(logger, uuid, "锁表", "orderCode", inputData.getOrderCode());
                    boolean lock = LockUtil.lock(logger, uuid, inputData.getOrderCode(), 1, "修改父订单配送日期");
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
                        Log.info(logger, uuid, "无此父订单", "orderCode", inputData.getOrderCode());
                        outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.INSIDE_ERROR.getResultID(), null, "");
                        Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
                        return outputBean;
                    }
                    TOsParentOrderPO parentOrderPo = parentOrderList.get(0);
                    if (!ParentOrderStatusEnum.canUpdateDt(parentOrderPo.getOrderStatus().intValue())) {
                        Log.info(logger, uuid, "此父订单状态非法", "orderCode", inputData.getOrderCode(), "orderStatus", parentOrderPo.getOrderStatus());
                        outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.INSIDE_ERROR.getResultID(), null, "");
                        Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
                        return outputBean;
                    }
                    if(new SimpleDateFormat("yyyyMMdd").format(parentOrderPo.getReceiveDt()).equals(inputData.getReceiveDt())){
                        Log.info(logger, uuid, "此父订单日期已经是该日期,不需要修改", "orderCode", inputData.getOrderCode(), "receiveDt", new SimpleDateFormat("yyyyMMdd").format(parentOrderPo.getReceiveDt()));
                        outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.SUCCESS.getResultID(), null, "");
                        Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
                        return outputBean;
                    }
                    
                    List<Object[]> updateSet = new ArrayList<Object[]>();
                    Object[] orderStatusObj = {"RECEIVE_DT", new Timestamp(new SimpleDateFormat("yyyyMMdd").parse(inputData.getReceiveDt()).getTime())};
                    updateSet.add(orderStatusObj);
                    List<Object[]> updateCondition = new ArrayList<Object[]>();
                    Object[] orderIdConditionObj = {"ORDER_ID", "=", parentOrderPo.getOrderId()};
                    updateCondition.add(orderIdConditionObj);
                    Object[] orderStatusConditionObj = {"ORDER_STATUS", "=", parentOrderPo.getOrderStatus()};
                    updateCondition.add(orderStatusConditionObj);
                    Log.info(logger, uuid, "获得条件", "updateSet", StringUtil.transferObjectList(updateSet), "updateCondition", StringUtil.transferObjectList(updateCondition));
                    
                    int resultNum = parentOrderDao.updateTOsParentOrderPOByCondition(updateSet, updateCondition);
                    if (resultNum != 1) {// 修改失败
                        Log.info(logger, uuid, "修改父订单的配送日期失败", "orderCode", inputData.getOrderCode());
                        parentOrderDao.rollback();
                        outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.INSIDE_ERROR.getResultID(), null, "");
                        Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
                        return outputBean;
                    }
                    
                    Log.info(logger, uuid, "插入操作流水表开始", "orderCode", inputData.getOrderCode());
                    TOsParentOrderOptPO opt = this.getParentOrderOpt(parentOrderPo.getOrderId(), inputData.getUserId(), Long.valueOf(ParentOrderStatusEnum.TO_PAY.getStatus()), "您的订单配送日期已修改为【"
                                    + inputData.getReceiveDt()
                                    + "】，详情请咨询400-898-9797", inputData.getUserName(), "1", "Your order has been changed to deliver on 【"
                                    + inputData.getReceiveDt()
                                    + "】. Please contact 400-898-9797 for more information.");
                    resultNum = parentOptDao.addTOsParentOrderOptPO(opt);
                    if (resultNum != 1) {
                        Log.info(logger, uuid, "插入操作流水表失败", "orderCode", inputData.getOrderCode(), "resultNum", resultNum);
                        parentOrderDao.rollback();
                        outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.INSIDE_ERROR.getResultID(), null, "");
                        Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
                        return outputBean;
                    }
                    Log.info(logger, uuid, "插入操作流水表结束", "orderCode", inputData.getOrderCode());
                    
                    parentOrderDao.commit();
                    
                    outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.SUCCESS.getResultID(), outputData, "");
                    Log.info(logger, uuid, "修改成功,接口返回", "outputBean", outputBean);
                    return outputBean;
                    
                } catch (Exception e) {
                    Log.error(logger, "修改父订单配送日期失败", e, "orderCode", inputData.getOrderCode());
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
                Log.info(logger, uuid, "子订单修改配送日期");
                try {
                    
                    List<OsOrderPO> orderList = orderDao.findOsOrderPOListBySql(" ORDER_CODE = '" + inputData.getOrderCode() + "'", false, false, true);
                    if (orderList == null || orderList.size() != 1) {
                        Log.info(logger, uuid, "无此子订单", "orderCode", inputData.getOrderCode());
                        outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.INSIDE_ERROR.getResultID(), null, "");
                        Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
                        return outputBean;
                    }
                    OsOrderPO orderPo = orderList.get(0);
                    if (!OrderStatusEnum.canUpdateLotCode(orderPo.getOrderStatus())) {// 不允许修改子订单配送日期
                        Log.info(logger, uuid, "子订单状态不允许修改配送日期", "orderCode", inputData.getOrderCode(), "orderStatus", orderPo.getOrderStatus());
                        outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.INSIDE_ERROR.getResultID(), null, "");
                        Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
                        return outputBean;
                    }
                    if(new SimpleDateFormat("yyyyMMdd").format(orderPo.getReceiveDt()).equals(inputData.getReceiveDt())){
                        Log.info(logger, uuid, "此子订单日期已经是该日期,不需要修改", "orderCode", inputData.getOrderCode(), "receiveDt", new SimpleDateFormat("yyyyMMdd").format(orderPo.getReceiveDt()));
                        outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.SUCCESS.getResultID(), null, "");
                        Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
                        return outputBean;
                    }
                    Log.info(logger, uuid, "初始化波次", "orderCode", inputData.getOrderCode());
                    InitOrderLot initOrderLot = new InitOrderLot();
                    int resultNum = initOrderLot.initOrderLot(logger, uuid, orderPo.getSubstationId(), inputData.getReceiveDt(), orderLotDao);
                    if (resultNum == -1){//初始化波次失败
                        Log.info(logger, uuid, "初始化波次失败", "orderCode", inputData.getOrderCode());
                        outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.INSIDE_ERROR.getResultID(), null, "");
                        Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
                        return outputBean;
                    }
                    
                    
                    boolean purchaseFlag = false;
                    if("1".equals(orderPo.getSalenumAdjust())){
                        purchaseFlag = true;
                    }else{
                        List<Object[]> itemCondition = new ArrayList<Object[]>();
                        itemCondition.add(new Object[]{"ORDER_ID", "=", orderPo.getOrderId()});
                        List<OsOrderItemsPO> orderItemList = orderItemsDao.findOsOrderItemsPOListByCondition(itemCondition, false, false, true);
                        for (OsOrderItemsPO item : orderItemList) {
                            if (item.getPurchaseDate() != null) {
                                purchaseFlag = true; // 预定商品
                                break;
                            }
                        }
                    }
                    
                    String newLotCode = "";
                    LotTypeEnum typeEnum = initOrderLot.getOrderLotTypeEnum(orderPo.getDeliveryTimeType(), orderPo.getOrderType(), orderPo.getDeliveryType(), orderPo.getOrderFrom(), purchaseFlag);
                    if (typeEnum != null) {
                        List<Object[]> condition = new ArrayList<Object[]>();
                        condition.add(new Object[]{"STATUS", "=", "1"});
                        condition.add(new Object[]{"LOT_SUBSTATION", "=", orderPo.getSubstationId() + "1"});
                        condition.add(new Object[]{"LOT_DATE", "=", inputData.getReceiveDt()});
                        condition.add(new Object[]{"LOT_TYPE", "=", typeEnum.getC()});
                        condition.add(new Object[]{"LOT_NUM", "=", typeEnum.getLotMaxNum()});
                        Log.info(logger, uuid, "获得条件", "condition", StringUtil.transferObjectList(condition));

                        List<TOsOrderLotPO> lotList = orderLotDao.findTOsOrderLotPOListByCondition(condition, false, false, true);
                        if (lotList == null || lotList.size() != 1) {
                            Log.info(logger, uuid, "查询最大波次号失败", "orderCode", inputData.getOrderCode(), "lotList", lotList);
                            outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.INSIDE_ERROR.getResultID(), null, "");
                            Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
                            return outputBean;
                        }
                        newLotCode = lotList.get(0).getLotCode();
                    }
                    
                    if (newLotCode == null || "".equals(newLotCode)) {// 获得新波次号失败
                        Log.info(logger, uuid, "获得新波次号失败", "orderCode", inputData.getOrderCode());
                        outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.INSIDE_ERROR.getResultID(), null, "");
                        Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
                        return outputBean;
                    }
                    

                    List<Object[]> updateSet = new ArrayList<Object[]>();
                    Object[] lotCodeObj = {"LOT_CODE", newLotCode};
                    updateSet.add(lotCodeObj);
                    Object[] receiveDtObj = {"RECEIVE_DT", new Timestamp(new SimpleDateFormat("yyyyMMdd").parse(inputData.getReceiveDt()).getTime())};
                    updateSet.add(receiveDtObj);
                    List<Object[]> updateCondition = new ArrayList<Object[]>();
                    Object[] orderIdConditionObj = {"ORDER_ID", "=", orderPo.getOrderId()};
                    updateCondition.add(orderIdConditionObj);
                    Object[] orderStatusConditionObj = {"ORDER_STATUS", "=", orderPo.getOrderStatus()};
                    updateCondition.add(orderStatusConditionObj);
                    Log.info(logger, uuid, "获得条件", "updateSet", StringUtil.transferObjectList(updateSet), "updateCondition", StringUtil.transferObjectList(updateCondition));
                    
                    resultNum = orderDao.updateOsOrderPOByCondition(updateSet, updateCondition);
                    if (resultNum != 1) {// 修改失败
                        Log.info(logger, uuid, "修改子订单的配送日期失败", "orderCode", inputData.getOrderCode());
                        orderDao.rollback();
                        outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.INSIDE_ERROR.getResultID(), null, "");
                        Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
                        return outputBean;
                    }
                    Log.info(logger, uuid, "插入操作流水表开始", "orderCode", inputData.getOrderCode());
                    OsOrderOptPO opt = this.getOrderOpt(orderPo.getOrderId(), inputData.getUserId(), orderPo.getOrderStatus(), orderPo.getExceptionStatus(), orderPo.getDisputeStatus(), "您的订单配送日期已修改为【"
                                    + inputData.getReceiveDt()
                                    + "】，详情请咨询400-898-9797", orderPo.getPayStatus(), "0", inputData.getUserName(), "1", "Your order has been changed to deliver on 【"
                                    + inputData.getReceiveDt()
                                    + "】. Please contact 400-898-9797 for more information.");
                    resultNum = optDao.addOsOrderOptPO(opt);
                    if (resultNum != 1) {
                        Log.info(logger, uuid, "插入操作流水表失败", "orderCode", inputData.getOrderCode(), "resultNum", resultNum);
                        orderDao.rollback();
                        outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.INSIDE_ERROR.getResultID(), null, "");
                        Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
                        return outputBean;
                    }
                    Log.info(logger, uuid, "插入操作流水表结束", "orderCode", inputData.getOrderCode());
                    
                    orderDao.commit();
                    
                    outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.SUCCESS.getResultID(), outputData, "");
                    Log.info(logger, uuid, "修改成功,接口返回", "outputBean", outputBean);
                    return outputBean;
                    
                } catch (Exception e) {
                    Log.error(logger, "修改子订单配送日期失败", e, "orderCode", inputData.getOrderCode());
                    orderDao.rollback();
                    outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.INSIDE_ERROR.getResultID(), null, "");
                    return outputBean;
                }
                
            }
            
        }catch(Exception e){
            Log.error(logger, uuid, "修改配送日期出错", e);
            outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.INSIDE_ERROR.getResultID(), null, "");
            Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
            return outputBean;
        }
        
    }
    
    @Override
    public BaseService clone() throws CloneNotSupportedException {
        return new UpdateReceiveDtImpl();
    }
    
    private TOsParentOrderOptPO getParentOrderOpt(Long orderId, Long userId, Long orderStatus, String remark, String userName, String isShow, String remarkEn) {
        TOsParentOrderOptPO opt = new TOsParentOrderOptPO();
        opt.setOrderId(orderId);
        opt.setOptDt(new Timestamp(System.currentTimeMillis()));
        opt.setUserId(userId);// PRS_USER中的user_id
        opt.setOrderStatus(orderStatus);
        opt.setRemark(remark);
        opt.setUserName(userName);
        opt.setIsShow(isShow);
        opt.setRemarkEn(remarkEn);
        return opt;
    }
    
    private OsOrderOptPO getOrderOpt(Long orderId, Long userId, String orderStatus, String exceptionStatus, String disputeStatus, String remark, String payStatus, String notifyStatus, String userName, String isShow, String remarkEn) {
        OsOrderOptPO opt = new OsOrderOptPO();
        opt.setOrderId(orderId);
        opt.setOptDt(new Timestamp(System.currentTimeMillis()));
        opt.setUserId(userId);// PRS_USER中的user_id
        opt.setOrderStatus(orderStatus);
        opt.setOrderExceptionStatus(exceptionStatus);
        opt.setOrderDisputeStatus(disputeStatus);
        opt.setRemark(remark);
        opt.setOrderPayStatus(payStatus);
        opt.setNotifyStatus(notifyStatus);
        opt.setUserName(userName);
        opt.setIsShow(isShow);
        opt.setRemarkEn(remarkEn);
        return opt;
    }
}
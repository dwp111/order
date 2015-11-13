package cn.tootoo.soa.oms.ordercancelbyuser;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import cn.tootoo.db.egrocery.osorder.OsOrderDao;
import cn.tootoo.db.egrocery.osorder.OsOrderPO;
import cn.tootoo.db.egrocery.toscancelorder.TOsCancelOrderDao;
import cn.tootoo.db.egrocery.toscancelorder.TOsCancelOrderPO;
import cn.tootoo.db.egrocery.tosparentorder.TOsParentOrderDao;
import cn.tootoo.db.egrocery.tosparentorder.TOsParentOrderPO;
import cn.tootoo.enums.BaseResultEnum;
import cn.tootoo.http.AuthorizeClient;
import cn.tootoo.http.bean.BaseInputBean;
import cn.tootoo.http.bean.BaseOutputBean;
import cn.tootoo.servlet.BaseService;
import cn.tootoo.soa.base.enums.BaseOrderResultEnum;
import cn.tootoo.soa.base.enums.OrderStatusEnum;
import cn.tootoo.soa.base.enums.OrderTypeEnum;
import cn.tootoo.soa.oms.ordercancel.output.OmsOrderCancelResultEnum;
import cn.tootoo.soa.oms.ordercancelbyuser.input.OmsOrderCancelByUserInputData;
import cn.tootoo.soa.oms.ordercancelbyuser.output.OmsOrderCancelByUserOutputData;
import cn.tootoo.soa.oms.utils.LogUtils4Oms;
import cn.tootoo.utils.ResponseUtil;
import cn.tootoo.utils.StringUtil;

/**
 * 服务接口方法的实现类。
 * 
 * 服务description：订单服务。
 * 服务remark：订单服务。
 * 接口description：用户自助取消订单
 * 接口remark：
 */
public final class OrderCancelByUserImpl extends BaseService {
    
    /**
     * 日志对象。
     */
    private Logger logger = Logger.getLogger(OrderCancelByUserImpl.class);
    
    @Override
    public BaseOutputBean doService(BaseInputBean inputBean) throws Exception {
        BaseOutputBean outputBean = new BaseOutputBean();
        try{
            LogUtils4Oms.info(logger, uuid, "执行服务开始，传入bean", "inputBean", inputBean);
            OmsOrderCancelByUserOutputData outputData = new OmsOrderCancelByUserOutputData();
            if (inputBean.getInputData() == null) {
                LogUtils4Oms.info(logger, uuid, "参数req_str为空", "inputBean", inputBean);
                outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.REQUEST_PARAM_ERROR.getResultID(), null, "");
                return outputBean;
            }
            OmsOrderCancelByUserInputData inputData = null;
            try {
                inputData = (OmsOrderCancelByUserInputData)inputBean.getInputData();
            } catch (Exception e) {
                LogUtils4Oms.error(logger, uuid, "接口实现类转换错误", e, "className", OmsOrderCancelByUserInputData.class.getName());
                outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.CLASS_TRANSFER_ERROR.getResultID(), null, "");
                return outputBean;
            }
            
            
            // 登录验证信息
            String userID = "";
            String scope = inputData.getScope();
            try {
                LogUtils4Oms.info(logger, uuid, "用户自助取消订单服务,进行验证！");
                this.request.setAttribute(AuthorizeClient.ATTRIB_NEED_AUTH, AuthorizeClient.NEED_AUTH_YES);
                this.request.setAttribute(AuthorizeClient.ATTRIB_CHECK_LEVEL, AuthorizeClient.AUTH_GENERAL_LEVEL);
                Map<String, String> tempMap = AuthorizeClient.getVerifyInfo(this.request);
                tempMap.put(AuthorizeClient.COOKIE_SCOPE, inputData.getScope());
                LogUtils4Oms.info(logger, uuid, "验证前，传入的Map信息", "tempMap", tempMap.toString());
                if (AuthorizeClient.CHECK_OK != AuthorizeClient.verifySession(tempMap)) {
                    LogUtils4Oms.info(logger, uuid, "验证失败", "tempMap", tempMap.toString());
                    outputBean = ResponseUtil.getBaseOutputBean(BaseOrderResultEnum.NOT_LOGIN.getResultID(), null, scope);
                    return outputBean;
                }
                userID = tempMap.get(AuthorizeClient.COOKIE_BUYER_ID).toString();
                if (null == userID || "".equals(userID)) {
                    LogUtils4Oms.info(logger, uuid, "从cookie中获取用户信息失败！");
                    outputBean = ResponseUtil.getBaseOutputBean(BaseOrderResultEnum.NOT_LOGIN.getResultID(), null, scope);
                    return outputBean;
                }
                LogUtils4Oms.info(logger, uuid, "验证成功！", "userID", userID);
            } catch (Exception e) {
                LogUtils4Oms.error(logger, uuid, "验证失败！");
                outputBean = ResponseUtil.getBaseOutputBean(BaseOrderResultEnum.NOT_LOGIN.getResultID(), null, scope);
                return outputBean;
            }
            
            
            TOsCancelOrderDao tOsCancelOrderDao = new TOsCancelOrderDao(uuid, logger);
            TOsParentOrderDao tOsParentOrderDao = new TOsParentOrderDao(uuid, logger);
            OsOrderDao osOrderDao = new OsOrderDao(uuid, logger);
            
            String orderCode = inputData.getOrderCode();
            String isBeforeCheck = inputData.getIsBeforeCheck();
            Long cancelReason = inputData.getCancelReason();
            if ("0".equals(isBeforeCheck) && cancelReason == null){
                // 如果不是弹框前验证，那么cancelReason为必传
                outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.REQUEST_PARAM_ERROR.getResultID(), null, "");
                LogUtils4Oms.info(logger, uuid, "接口返回", "outputBean", outputBean);
                return outputBean;
            }
            
            List<Object[]> cancelOrderConditions = new ArrayList<Object[]>();
            cancelOrderConditions.add(new Object[]{"ORDER_CODE", "=", orderCode});
            LogUtils4Oms.info(logger, uuid, "查询条件", "cancelOrderConditions", StringUtil.transferObjectList(cancelOrderConditions));
            List<TOsCancelOrderPO> cancelOrderList = tOsCancelOrderDao.findTOsCancelOrderPOListByCondition(cancelOrderConditions);
            if (cancelOrderList != null && cancelOrderList.size() >= 1) {
                TOsCancelOrderPO cancelOrder = cancelOrderList.get(0);
                if("0".equals(cancelOrder.getCancelStatus())){
                    // 取消中
                    LogUtils4Oms.info(logger, uuid, "已提交过申请", "orderCode", cancelOrder.getOrderCode());
                    outputData.setResultStatus("2");
                    outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.SUCCESS.getResultID(), outputData, scope);
                    return outputBean;
                }
                if("1".equals(cancelOrder.getCancelStatus())){
                    // 已取消
                    LogUtils4Oms.info(logger, uuid, "该订单已取消成功", "orderCode", cancelOrder.getOrderCode());
                    outputData.setResultStatus("4");
                    outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.SUCCESS.getResultID(), outputData, scope);
                    return outputBean;
                }
                if ("2".equals(cancelOrder.getCancelStatus())){
                    // 取消失败
                    LogUtils4Oms.info(logger, uuid, "该订单取消失败", "orderCode", cancelOrder.getOrderCode());
                    outputData.setResultStatus("3");
                    outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.SUCCESS.getResultID(), outputData, scope);
                    return outputBean;
                }
            }
            
            if("1".equals(isBeforeCheck)){
                LogUtils4Oms.info(logger, uuid, "弹框前验证通过，此订单不在请求表中", "orderCode", orderCode);
                outputData.setResultStatus("0");
                outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.SUCCESS.getResultID(), outputData, scope);
                return outputBean;
            }
            
            
            List<Object[]> parentOrderConditions = new ArrayList<Object[]>();
            parentOrderConditions.add(new Object[]{"BUYER_ID", "=", userID});
            parentOrderConditions.add(new Object[]{"ORDER_CODE", "=", orderCode});
            LogUtils4Oms.info(logger, uuid, "查询条件", "parentOrderConditions", StringUtil.transferObjectList(parentOrderConditions));
            List<TOsParentOrderPO> parentOrderList = tOsParentOrderDao.findTOsParentOrderPOListByCondition(parentOrderConditions);
            if (parentOrderList == null || parentOrderList.size() <= 0) {
                // 没有查询到订单数据
                LogUtils4Oms.info(logger, uuid, "查询父订单数据为空", "parentOrderList", parentOrderList);
                outputBean = ResponseUtil.getBaseOutputBean(OmsOrderCancelResultEnum.CANCEL_FAIL.getResultID(), null, scope);
                return outputBean;
            }
            TOsParentOrderPO parentOrder = parentOrderList.get(0);
            
            // 查看父子订单状态是否符合取消机制
//            if (OrderTypeEnum.ONLINE_CARD_VIRTUAL.getC().equals(parentOrder.getOrderType())){
//                LogUtils4Oms.info(logger, uuid, "电子卡订单不允许作废", "orderCode", orderCode);
//                outputData.setResultStatus("3");
//                outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.SUCCESS.getResultID(), outputData, scope);
//                return outputBean;
//            }
            Long orderStatus = parentOrder.getOrderStatus();
            if (orderStatus >= 1 && orderStatus <= 120 && orderStatus != 110) {
                LogUtils4Oms.info(logger, uuid, "父订单符合取消机制，插入取消申请表", "orderCode", orderCode);
                int resultNum = this.addTOsCancelOrderPO(tOsCancelOrderDao, parentOrder.getOrderId(), orderCode, Long.parseLong(userID), cancelReason);
                if (resultNum == 1) {
                    tOsCancelOrderDao.commit();
                    LogUtils4Oms.info(logger, uuid, "订单提交取消申请成功！", "orderCode", orderCode);
                    outputData.setResultStatus("1");
                    outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.SUCCESS.getResultID(), outputData, scope);
                    return outputBean;
                }
                tOsCancelOrderDao.rollback();
                LogUtils4Oms.info(logger, uuid, "订单提交取消申请失败", "orderCode", orderCode, "resultNum", resultNum);
                outputBean = ResponseUtil.getBaseOutputBean(OmsOrderCancelResultEnum.CANCEL_FAIL.getResultID(), null, scope);
                return outputBean;
                
            } else if (orderStatus >= 125 || orderStatus == 110){
                List<Object[]> orderConditions = new ArrayList<Object[]>();
                orderConditions.add(new Object[]{"PARENT_ID", "=", parentOrder.getOrderId()});
                orderConditions.add(new Object[]{"ORDER_STATUS", "<>", OrderStatusEnum.REPALCE_GOODS_CANCEL.getC()});
                List<OsOrderPO> orderList = osOrderDao.findOsOrderPOListByCondition(orderConditions);
                for (OsOrderPO osOrder : orderList) {
                    OrderStatusEnum orderStatusEnum = OrderStatusEnum.get(osOrder.getOrderStatus());
                    if (orderStatusEnum == null || !OrderStatusEnum.canBeCanceled(osOrder.getOrderStatus())) {
                        LogUtils4Oms.info(logger, uuid, "存在不符合取消机制的子订单状态，订单提交取消申请失败！", "orderCode", orderCode, "subOrderCode", osOrder.getOrderCode(), "subOrderStatus", osOrder.getOrderStatus());
                        outputData.setResultStatus("3");
                        outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.SUCCESS.getResultID(), outputData, scope);
                        return outputBean;
                    }
                }
                
                LogUtils4Oms.info(logger, uuid, "所有子订单都符合取消机制，插入取消申请表", "orderCode", orderCode);
                int resultNum = this.addTOsCancelOrderPO(tOsCancelOrderDao, parentOrder.getOrderId(), orderCode, Long.parseLong(userID), cancelReason);
                if (resultNum == 1) {
                    tOsCancelOrderDao.commit();
                    LogUtils4Oms.info(logger, uuid, "订单提交取消申请成功！", "orderCode", orderCode);
                    outputData.setResultStatus("1");
                    outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.SUCCESS.getResultID(), outputData, scope);
                    return outputBean;
                }
                tOsCancelOrderDao.rollback();
                LogUtils4Oms.info(logger, uuid, "订单提交取消申请失败", "orderCode", orderCode, "resultNum", resultNum);
                outputBean = ResponseUtil.getBaseOutputBean(OmsOrderCancelResultEnum.CANCEL_FAIL.getResultID(), null, scope);
                return outputBean;
                
            }else{
                LogUtils4Oms.info(logger, uuid, "该父订单不符合取消机制", "orderCode", orderCode, "orderStatus", orderStatus);
                outputData.setResultStatus("3");
                outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.SUCCESS.getResultID(), outputData, scope);
                return outputBean;
            }
        
        }catch(Exception e){
            LogUtils4Oms.error(logger, uuid, "用户自助取消订单请求出错", e);
            outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.INSIDE_ERROR.getResultID(), null, "");
            LogUtils4Oms.info(logger, uuid, "接口返回", "outputBean", outputBean);
            return outputBean;
        }
    }
    
    /**
     * Description:<br>
     * 插入父订单作废请求数据
     * 
     * @param tOsCancelOrderDao
     * @param orderId
     * @param orderCode
     * @param userID
     * @param cancelReason
     * @return
     */
    private int addTOsCancelOrderPO(TOsCancelOrderDao tOsCancelOrderDao, Long orderId, String orderCode, Long userID,Long cancelReason){
        TOsCancelOrderPO cancelOrder = new TOsCancelOrderPO();
        cancelOrder.setOrderId(orderId);
        cancelOrder.setOrderCode(orderCode);
        cancelOrder.setCancelStatus("0");
        cancelOrder.setCancelUser(userID);
        cancelOrder.setCancelTime(new Timestamp(System.currentTimeMillis()));
        cancelOrder.setCancelReason(cancelReason);
        LogUtils4Oms.info(logger, uuid, "插入待取消的父订单信息", "cancelOrder", cancelOrder);
        return tOsCancelOrderDao.addTOsCancelOrderPO(cancelOrder);
    }
    
    @Override
    public BaseService clone() throws CloneNotSupportedException {
        return new OrderCancelByUserImpl();
    }
    
}
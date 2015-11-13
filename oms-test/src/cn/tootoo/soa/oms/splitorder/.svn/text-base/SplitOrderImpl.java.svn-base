package cn.tootoo.soa.oms.splitorder;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import cn.tootoo.db.egrocery.osorder.OsOrderDao;
import cn.tootoo.db.egrocery.osorder.OsOrderPO;
import cn.tootoo.db.egrocery.osorderitems.OsOrderItemsDao;
import cn.tootoo.db.egrocery.osorderitems.OsOrderItemsPO;
import cn.tootoo.db.egrocery.osorderopt.OsOrderOptDao;
import cn.tootoo.db.egrocery.osorderopt.OsOrderOptPO;
import cn.tootoo.db.egrocery.tosparentorder.TOsParentOrderDao;
import cn.tootoo.db.egrocery.tosparentorder.TOsParentOrderPO;
import cn.tootoo.db.egrocery.tosparentorderitems.TOsParentOrderItemsDao;
import cn.tootoo.db.egrocery.tosparentorderitems.TOsParentOrderItemsPO;
import cn.tootoo.enums.BaseResultEnum;
import cn.tootoo.enums.BooleanEnum;
import cn.tootoo.http.bean.BaseInputBean;
import cn.tootoo.http.bean.BaseOutputBean;
import cn.tootoo.servlet.BaseService;
import cn.tootoo.soa.base.enums.DeliveryTypeEnum;
import cn.tootoo.soa.base.enums.OrderStatusEnum;
import cn.tootoo.soa.base.enums.ParentOrderStatusEnum;
import cn.tootoo.soa.base.global.DictionaryData;
import cn.tootoo.soa.base.util.JobUtil;
import cn.tootoo.soa.oms.splitorder.input.OmsSplitOrderInputData;
import cn.tootoo.soa.oms.splitorder.output.OmsSplitOrderOutputData;
import cn.tootoo.soa.oms.utils.SqlUtil;
import cn.tootoo.utils.Log;
import cn.tootoo.utils.ResponseUtil;
import cn.tootoo.utils.StringUtil;

/**
 * 服务接口方法的实现类。
 * 服务description：订单服务。
 * 服务remark：订单服务。
 * 接口description：拆订单
 * 接口remark：拆订单
 */

public class SplitOrderImpl extends BaseService {
    
    private Logger logger = Logger.getLogger(this.getClass());
    
    @Override
    public BaseOutputBean doService(BaseInputBean inputBean) throws Exception {
        
        BaseOutputBean outputBean = new BaseOutputBean();
        
        try {
            
            Log.info(logger, uuid, "执行服务开始，传入bean", "inputBean", inputBean);
            OmsSplitOrderOutputData outputData = new OmsSplitOrderOutputData();
            if (inputBean.getInputData() == null) {
                Log.info(logger, uuid, "参数req_str为空", "inputBean", inputBean);
                outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.REQUEST_PARAM_ERROR.getResultID(), null, "");
                return outputBean;
            }
            
            OmsSplitOrderInputData inputData = null;
            try {
                inputData = (OmsSplitOrderInputData)inputBean.getInputData();
            } catch (Exception e) {
                Log.error(logger, "接口实现类转换错误", e, "className", OmsSplitOrderInputData.class.getName());
                outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.CLASS_TRANSFER_ERROR.getResultID(), null, "");
                return outputBean;
            }
            
            TOsParentOrderDao parentOrderDao = new TOsParentOrderDao(uuid, logger);
            TOsParentOrderItemsDao parentOrderItemDao = new TOsParentOrderItemsDao(uuid, logger);
            OsOrderDao osOrderDao = new OsOrderDao(uuid, logger);
            OsOrderItemsDao osOrderItemDao = new OsOrderItemsDao(uuid, logger);
            OsOrderOptDao subOptDao = new OsOrderOptDao(uuid, logger);
            
            if (inputData.getSplitType().equals("1")) {// 父订单拆单
            
                Log.info(logger, uuid, "父订单拆单", "orderCode", inputData.getOrderCode());
                
                List<TOsParentOrderPO> parentOrderList = parentOrderDao.findTOsParentOrderPOListBySql(" ORDER_CODE = '"
                                + inputData.getOrderCode() + "'", false, false, true);
                if (parentOrderList == null || parentOrderList.size() != 1) {
                    Log.info(logger, uuid, "无此父订单", "orderCode", inputData.getOrderCode());
                    outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.INSIDE_ERROR.getResultID(), null, "");
                    Log.info(logger, uuid, "接口返回", "orderCode", inputData.getOrderCode(), "outputBean", outputBean);
                    return outputBean;
                }
                TOsParentOrderPO parentOrderPo = parentOrderList.get(0);
                
                if (parentOrderPo.getOrderStatus().intValue() == ParentOrderStatusEnum.SPLIT_ORDER_SUCCESS.getStatus()
                                || parentOrderPo.getOrderStatus().intValue() >= ParentOrderStatusEnum.SPLIT_PAY_SUCCESS.getStatus()) {
                    Log.info(logger, uuid, "此父订单已拆单成功", "orderCode", inputData.getOrderCode(), "orderStatus", parentOrderPo.getOrderStatus());
                    outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.SUCCESS.getResultID(), outputData, "");
                    Log.info(logger, uuid, "接口返回", "orderCode", inputData.getOrderCode(), "outputBean", outputBean);
                    return outputBean;
                }
                
                if (parentOrderPo.getOrderStatus().intValue() == ParentOrderStatusEnum.SPLIT_LOT_FAILED.getStatus()
                                || parentOrderPo.getOrderStatus().intValue() <= ParentOrderStatusEnum.SDC_FAILED.getStatus()) {
                    Log.info(logger, uuid, "此父订单状态非法,不能拆单", "orderCode", inputData.getOrderCode(), "orderStatus", parentOrderPo.getOrderStatus());
                    outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.INSIDE_ERROR.getResultID(), null, "");
                    Log.info(logger, uuid, "接口返回", "orderCode", inputData.getOrderCode(), "outputBean", outputBean);
                    return outputBean;
                }
                
                List<TOsParentOrderItemsPO> parentOrderItemList = parentOrderItemDao.findTOsParentOrderItemsPOListBySql(" ORDER_ID = "
                                + parentOrderPo.getOrderId());
                if (parentOrderItemList == null
                                || parentOrderItemList.isEmpty()) {
                    Log.info(logger, uuid, "此父订单没有详情信息", "orderCode", inputData.getOrderCode());
                    outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.INSIDE_ERROR.getResultID(), null, "");
                    Log.info(logger, uuid, "接口返回", "orderCode", inputData.getOrderCode(), "outputBean", outputBean);
                    return outputBean;
                }
                
                if (parentOrderPo.getSplitStatus().intValue() == 1) {// 已经拆单
                    Log.info(logger, uuid, "此父订单已拆单成功", "orderCode", inputData.getOrderCode(), "orderStatus", parentOrderPo.getOrderStatus(), "splitStatus", parentOrderPo.getSplitStatus());
                    outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.SUCCESS.getResultID(), outputData, "");
                    Log.info(logger, uuid, "接口返回", "orderCode", inputData.getOrderCode(), "outputBean", outputBean);
                    return outputBean;
                }
                
                Log.info(logger, uuid, "拆单开始", "orderCode", inputData.getOrderCode());
                Map<String, List<OsOrderItemsPO>> osOrderItemMap = this.getOsOrderItem(parentOrderItemList, osOrderItemDao);
                
                List<OsOrderItemsPO> orderItemList = new ArrayList<OsOrderItemsPO>();
                List<OsOrderPO> osOrderList = this.getOsOrder(osOrderItemMap, osOrderDao, orderItemList, parentOrderPo, DictionaryData.shippingCompanyTypeMap);
                if (osOrderItemMap.isEmpty() || orderItemList.isEmpty()
                                || osOrderList.isEmpty()) {
                    Log.info(logger, uuid, "拆单失败", "orderCode", inputData.getOrderCode());
                    outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.INSIDE_ERROR.getResultID(), null, "");
                    Log.info(logger, uuid, "接口返回", "orderCode", inputData.getOrderCode(), "outputBean", outputBean);
                    return outputBean;
                }
                Log.info(logger, uuid, "拆单结束", "orderCode", inputData.getOrderCode());
                
                Log.info(logger, uuid, "插入子订单数据开始", "orderCode", inputData.getOrderCode());
                int num = osOrderDao.addOsOrderPOList(osOrderList);
                if (num != osOrderList.size()) {
                    osOrderDao.rollback();
                    Log.info(logger, uuid, "插入子订单失败", "orderCode", inputData.getOrderCode(), "num", num);
                    outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.INSIDE_ERROR.getResultID(), null, "");
                    Log.info(logger, uuid, "接口返回", "orderCode", inputData.getOrderCode(), "outputBean", outputBean);
                    return outputBean;
                }
                Log.info(logger, uuid, "插入子订单数据结束", "orderCode", inputData.getOrderCode());
                
                Log.info(logger, uuid, "插入子订单详情数据开始", "orderCode", inputData.getOrderCode());
                num = osOrderItemDao.addOsOrderItemsPOList(orderItemList);
                if (num != orderItemList.size()) {
                    osOrderDao.rollback();
                    Log.info(logger, uuid, "插入子订单详情数据失败", "orderCode", inputData.getOrderCode(), "num", num);
                    outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.INSIDE_ERROR.getResultID(), null, "");
                    Log.info(logger, uuid, "接口返回", "orderCode", inputData.getOrderCode(), "outputBean", outputBean);
                    return outputBean;
                }
                Log.info(logger, uuid, "插入子订单详情数据结束", "orderCode", inputData.getOrderCode());
                
                Log.info(logger, uuid, "修改父订单信息(split_status,order_count)开始", "orderCode", inputData.getOrderCode());
                List<Object[]> updateSet = new ArrayList<Object[]>();
                Object[] splitStatusObj = {"SPLIT_STATUS", "1"};
                updateSet.add(splitStatusObj);
                Object[] orderCountObj = {"ORDER_COUNT", osOrderList.size()};
                updateSet.add(orderCountObj);
                List<Object[]> updateCondition = new ArrayList<Object[]>();
                Object[] orderIdConditionObj = {"ORDER_ID", "=", parentOrderPo.getOrderId()};
                updateCondition.add(orderIdConditionObj);
                Object[] orderStatusConditionObj = {"ORDER_STATUS", "=", parentOrderPo.getOrderStatus()};
                updateCondition.add(orderStatusConditionObj);
                Log.info(logger, uuid, "获得条件", "updateSet", StringUtil.transferObjectList(updateSet), "updateCondition", StringUtil.transferObjectList(updateCondition));
                
                num = parentOrderDao.updateTOsParentOrderPOByCondition(updateSet, updateCondition);
                if (num != 1) {
                    parentOrderDao.rollback();
                    Log.info(logger, uuid, "修改父订单信息(split_status,order_count)失败", "orderCode", inputData.getOrderCode(), "num", num);
                    outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.INSIDE_ERROR.getResultID(), null, "");
                    Log.info(logger, uuid, "接口返回", "orderCode", inputData.getOrderCode(), "outputBean", outputBean);
                    return outputBean;
                }
                Log.info(logger, uuid, "修改父订单信息(split_status,order_count)结束", "orderCode", inputData.getOrderCode());
                
                Log.info(logger, uuid, "插入子订单操作流水表开始", "orderCode", inputData.getOrderCode());
                List<OsOrderOptPO> optList = new ArrayList<OsOrderOptPO>();
                for (OsOrderPO subOrder : osOrderList) {
                    OsOrderOptPO subOpt = this.getOrderOpt(parentOrderPo.getCreateDt(), subOrder.getOrderId(), 3L, "E", subOrder.getExceptionStatus(), subOrder.getDisputeStatus(), "您已提交了订单，请等待系统审核", subOrder.getPayStatus(), "0", "客户", "1", "Your order has been lodged. It is under review.");
                    optList.add(subOpt);
                    subOpt = this.getOrderOpt(null, subOrder.getOrderId(), 3L, subOrder.getOrderStatus(), subOrder.getExceptionStatus(), subOrder.getDisputeStatus(), "已拆单", subOrder.getPayStatus(), "0", "", "0", "");
                    optList.add(subOpt);
                }
                int resultNum = subOptDao.addOsOrderOptPOList(optList);
                if (resultNum != optList.size()) {
                    Log.info(logger, uuid, "插入子订单操作流水表失败", "orderCode", inputData.getOrderCode(), "resultNum", resultNum);
                    parentOrderDao.rollback();
                    outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.INSIDE_ERROR.getResultID(), null, "");
                    Log.info(logger, uuid, "接口返回", "orderCode", inputData.getOrderCode(), "outputBean", outputBean);
                    return outputBean;
                }
                Log.info(logger, uuid, "插入子订单操作流水表结束", "orderCode", inputData.getOrderCode());
                
                parentOrderDao.commit();
                
                outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.SUCCESS.getResultID(), outputData, "");
                return outputBean;
                
            } else {// 子订单拆单
            
                Log.info(logger, uuid, "子订单拆单", "subOrderCode", inputData.getOrderCode());
                
                List<OsOrderPO> osOrderList = osOrderDao.findOsOrderPOListBySql(" ORDER_CODE = '"
                                + inputData.getOrderCode() + "'", false, false, true);
                
                if (osOrderList == null || osOrderList.size() != 1) {
                    Log.info(logger, uuid, "无此子订单", "subOrderCode", inputData.getOrderCode());
                    outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.INSIDE_ERROR.getResultID(), null, "");
                    Log.info(logger, uuid, "接口返回", "subOrderCode", inputData.getOrderCode(), "outputBean", outputBean);
                    return outputBean;
                }
                
                OsOrderPO osOrderPo = osOrderList.get(0);
                
                List<TOsParentOrderPO> parentOrderList = parentOrderDao.findTOsParentOrderPOListBySql(" ORDER_CODE = '"
                                + osOrderPo.getParentCode() + "'", false, false, false);
                if (parentOrderList == null || parentOrderList.size() != 1) {
                    Log.info(logger, uuid, "无此父订单", "subOrderCode", inputData.getOrderCode());
                    outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.INSIDE_ERROR.getResultID(), null, "");
                    Log.info(logger, uuid, "接口返回", "subOrderCode", inputData.getOrderCode(), "outputBean", outputBean);
                    return outputBean;
                }
                TOsParentOrderPO parentOrderPo = parentOrderList.get(0);
                
                if (osOrderPo.getOrderStatus().equals(OrderStatusEnum.CHECKED.getC())
                                || osOrderPo.getOrderStatus().equals(OrderStatusEnum.REPALCE_GOODS_CANCEL.getC())) {// 是已审核或者作废,返回成功
                    Log.info(logger, uuid, "此子订单状态是已审核或者作废", "subOrderCode", inputData.getOrderCode(), "orderStatus", osOrderPo.getOrderStatus());
                    outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.SUCCESS.getResultID(), outputData, "");
                    Log.info(logger, uuid, "接口返回", "subOrderCode", inputData.getOrderCode(), "outputBean", outputBean);
                    return outputBean;
                }
                
                if (!osOrderPo.getOrderStatus().equals(OrderStatusEnum.WAIT_REPLACING.getC())) {// 不是换货中状态
                    Log.info(logger, uuid, "此子订单状态不是换货中,不能拆单", "subOrderCode", inputData.getOrderCode(), "orderStatus", osOrderPo.getOrderStatus());
                    outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.INSIDE_ERROR.getResultID(), null, "");
                    Log.info(logger, uuid, "接口返回", "subOrderCode", inputData.getOrderCode(), "outputBean", outputBean);
                    return outputBean;
                }
                
                List<OsOrderItemsPO> osOrderItemList = osOrderItemDao.findOsOrderItemsPOListBySql(" ORDER_ID = "
                                + osOrderPo.getOrderId());
                if (osOrderItemList == null || osOrderItemList.isEmpty()) {
                    Log.info(logger, uuid, "此子订单没有详情信息", "subOrderCode", inputData.getOrderCode());
                    outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.INSIDE_ERROR.getResultID(), null, "");
                    Log.info(logger, uuid, "接口返回", "subOrderCode", inputData.getOrderCode(), "outputBean", outputBean);
                    return outputBean;
                }
                
                Set<Long> tZoneIdSet = new HashSet<Long>();
                for (OsOrderItemsPO osItem : osOrderItemList) {
                    if (BooleanEnum.FALSE.getV().equals(osItem.getIsGiftbox())) {// 不是礼盒
                        tZoneIdSet.add(osItem.getWarmzoneId());
                    }
                }
                if (tZoneIdSet.size() == 1) {// 不需要拆单
                
                    Log.info(logger, uuid, "所换商品为相同温区,不需要拆单,修改子订单状态为已审核开始", "subOrderCode", inputData.getOrderCode());
                    List<Object[]> updateSet = new ArrayList<Object[]>();
                    Object[] splitStatusObj = {"ORDER_STATUS", OrderStatusEnum.CHECKED.getC()};
                    updateSet.add(splitStatusObj);
                    // 子订单只有一个商品时，本子订单也需要更改温区
                    Long zoneId = 0L;
                    for (Long tZoneId : tZoneIdSet) {
                        zoneId = tZoneId;
                    }
                    Object[] tZoneIdObj = {"TZONE_ID", zoneId};
                    updateSet.add(tZoneIdObj);
                    List<Object[]> updateCondition = new ArrayList<Object[]>();
                    Object[] orderIdConditionObj = {"ORDER_ID", "=", osOrderPo.getOrderId()};
                    updateCondition.add(orderIdConditionObj);
                    Object[] orderStatusConditionObj = {"ORDER_STATUS", "=", osOrderPo.getOrderStatus()};
                    updateCondition.add(orderStatusConditionObj);
                    Log.info(logger, uuid, "获得条件", "updateSet", StringUtil.transferObjectList(updateSet), "updateCondition", StringUtil.transferObjectList(updateCondition));
                    
                    int num = osOrderDao.updateOsOrderPOByCondition(updateSet, updateCondition);
                    if (num != 1) {
                        osOrderDao.rollback();
                        Log.info(logger, uuid, "修改子订单状态为已审核失败", "subOrderCode", inputData.getOrderCode(), "num", num);
                        outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.INSIDE_ERROR.getResultID(), null, "");
                        Log.info(logger, uuid, "接口返回", "subOrderCode", inputData.getOrderCode(), "outputBean", outputBean);
                        return outputBean;
                    }
                    Log.info(logger, uuid, "修改子订单状态为已审核结束", "subOrderCode", inputData.getOrderCode());
                    
                    Log.info(logger, uuid, "插入子订单操作流水表开始", "subOrderCode", osOrderPo.getOrderCode());
                    OsOrderOptPO subOpt = getOrderOpt(null, osOrderPo.getOrderId(), 3L, OrderStatusEnum.CHECKED.getC(), osOrderPo.getExceptionStatus(), osOrderPo.getDisputeStatus(), "已换货", osOrderPo.getPayStatus(), "0", "", "0", "");
                    num = subOptDao.addOsOrderOptPO(subOpt);
                    if (num != 1) {
                        osOrderDao.rollback();
                        Log.info(logger, uuid, "插入子订单操作流水表失败", "subOrderCode", inputData.getOrderCode(), "num", num);
                        outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.INSIDE_ERROR.getResultID(), null, "");
                        Log.info(logger, uuid, "接口返回", "subOrderCode", inputData.getOrderCode(), "outputBean", outputBean);
                        return outputBean;
                    }
                    Log.info(logger, uuid, "插入子订单操作流水表结束", "subOrderCode", osOrderPo.getOrderCode());
                    
                    osOrderDao.commit();
                    
                    outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.SUCCESS.getResultID(), outputData, "");
                    Log.info(logger, uuid, "接口返回", "subOrderCode", inputData.getOrderCode(), "outputBean", outputBean);
                    return outputBean;
                    
                }
                
                Log.info(logger, uuid, "子订单拆单开始", "subOrderCode", inputData.getOrderCode());
                Map<String, List<OsOrderItemsPO>> osOrderItemMap = this.getOsOrderItemFromSubOrder(osOrderItemList, osOrderPo, osOrderItemDao);
                List<OsOrderItemsPO> orderItemList = new ArrayList<OsOrderItemsPO>();
                StringBuffer orderCodes = new StringBuffer("");
                String newOrderCodes = "";
                List<OsOrderPO> osOrderListNew = this.getOsOrderFromSubOrder(osOrderItemMap, osOrderDao, orderItemList, osOrderPo, orderCodes);
                if (osOrderItemMap.isEmpty() || orderItemList.isEmpty()
                                || osOrderListNew.isEmpty()) {
                    Log.info(logger, uuid, "拆单失败", "subOrderCode", inputData.getOrderCode());
                    outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.INSIDE_ERROR.getResultID(), null, "");
                    Log.info(logger, uuid, "接口返回", "subOrderCode", inputData.getOrderCode(), "outputBean", outputBean);
                    return outputBean;
                }
                if (orderCodes.toString().endsWith("-")) {
                    newOrderCodes = StringUtil.removeLast(orderCodes.toString());
                }
                Log.info(logger, uuid, "拆单结束", "subOrderCode", inputData.getOrderCode());
                
                Log.info(logger, uuid, "插入子订单数据开始", "subOrderCode", inputData.getOrderCode());
                int num = osOrderDao.addOsOrderPOList(osOrderListNew);
                if (num != osOrderListNew.size()) {
                    osOrderDao.rollback();
                    Log.info(logger, uuid, "插入子订单失败", "subOrderCode", inputData.getOrderCode(), "num", num);
                    outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.INSIDE_ERROR.getResultID(), null, "");
                    Log.info(logger, uuid, "接口返回", "subOrderCode", inputData.getOrderCode(), "outputBean", outputBean);
                    return outputBean;
                }
                Log.info(logger, uuid, "插入子订单数据结束", "subOrderCode", inputData.getOrderCode());
                
                Log.info(logger, uuid, "插入子订单详情数据开始", "subOrderCode", inputData.getOrderCode());
                num = osOrderItemDao.addOsOrderItemsPOList(orderItemList);
                if (num != orderItemList.size()) {
                    osOrderDao.rollback();
                    Log.info(logger, uuid, "插入子订单详情数据失败", "subOrderCode", inputData.getOrderCode(), "num", num);
                    outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.INSIDE_ERROR.getResultID(), null, "");
                    Log.info(logger, uuid, "接口返回", "subOrderCode", inputData.getOrderCode(), "outputBean", outputBean);
                    return outputBean;
                }
                Log.info(logger, uuid, "插入子订单详情数据结束", "subOrderCode", inputData.getOrderCode());
                
                Log.info(logger, uuid, "修改父订单信息(order_count)开始", "subOrderCode", inputData.getOrderCode());
                List<Object[]> updateSet = new ArrayList<Object[]>();
                Object[] orderCountObj = {"ORDER_COUNT", parentOrderPo.getOrderCount()
                                + osOrderListNew.size() - 1};
                updateSet.add(orderCountObj);
                List<Object[]> updateCondition = new ArrayList<Object[]>();
                Object[] orderIdConditionObj = {"ORDER_ID", "=", parentOrderPo.getOrderId()};
                updateCondition.add(orderIdConditionObj);
                Object[] orderStatusConditionObj = {"ORDER_STATUS", "=", parentOrderPo.getOrderStatus()};
                updateCondition.add(orderStatusConditionObj);
                Log.info(logger, uuid, "获得条件", "updateSet", StringUtil.transferObjectList(updateSet), "updateCondition", StringUtil.transferObjectList(updateCondition));
                
                num = parentOrderDao.updateTOsParentOrderPOByCondition(updateSet, updateCondition);
                if (num != 1) {
                    osOrderDao.rollback();
                    Log.info(logger, uuid, "修改父订单信息(order_count)失败", "subOrderCode", inputData.getOrderCode(), "num", num);
                    outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.INSIDE_ERROR.getResultID(), null, "");
                    Log.info(logger, uuid, "接口返回", "subOrderCode", inputData.getOrderCode(), "outputBean", outputBean);
                    return outputBean;
                }
                Log.info(logger, uuid, "修改父订单信息(order_count)结束", "subOrderCode", inputData.getOrderCode());
                
                Log.info(logger, uuid, "修改原子订单状态为换货拆支付失败开始", "subOrderCode", inputData.getOrderCode());
                List<Object[]> updateSetNew = new ArrayList<Object[]>();
                Object[] splitStatusObj = {"ORDER_STATUS", OrderStatusEnum.REPLACE_SPLITPAY_FAILED.getC()};
                updateSetNew.add(splitStatusObj);
                Object[] systemRemarkObj = {"SYSTEM_REMARK", ((osOrderPo.getSystemRemark() == null || osOrderPo.getSystemRemark().equals("null"))?"":osOrderPo.getSystemRemark())
                                + "#%#" + newOrderCodes};
                updateSetNew.add(systemRemarkObj);
                List<Object[]> updateConditionNew = new ArrayList<Object[]>();
                Object[] orderIdConditionObjNew = {"ORDER_ID", "=", osOrderPo.getOrderId()};
                updateConditionNew.add(orderIdConditionObjNew);
                Object[] orderStatusConditionObjNew = {"ORDER_STATUS", "=", osOrderPo.getOrderStatus()};
                updateConditionNew.add(orderStatusConditionObjNew);
                
                Log.info(logger, uuid, "获得条件", "updateSet", StringUtil.transferObjectList(updateSetNew), "updateCondition", StringUtil.transferObjectList(updateConditionNew));
                
                num = osOrderDao.updateOsOrderPOByCondition(updateSetNew, updateConditionNew);
                if (num != 1) {
                    osOrderDao.rollback();
                    Log.info(logger, uuid, "修改子订单状态为换货拆支付失败失败", "subOrderCode", inputData.getOrderCode(), "num", num);
                    outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.INSIDE_ERROR.getResultID(), null, "");
                    Log.info(logger, uuid, "接口返回", "subOrderCode", inputData.getOrderCode(), "outputBean", outputBean);
                    return outputBean;
                }
                Log.info(logger, uuid, "修改原子订单状态为换货拆支付失败结束", "subOrderCode", inputData.getOrderCode());
                
                osOrderDao.commit();
                
                /********************************************************************** 拆支付开始 ****************************************************************************/
                Log.info(logger, uuid, "拆支付开始", "subOrderCode", inputData.getOrderCode());
                boolean b = JobUtil.splitPaySubOrder("1", logger, uuid, osOrderPo, osOrderListNew, osOrderDao, osOrderItemDao, subOptDao);
                if (!b) {
                    osOrderDao.rollback();
                } else {
                    osOrderDao.commit();
                }
                Log.info(logger, uuid, "拆支付结束", "subOrderCode", inputData.getOrderCode());
                /********************************************************************** 拆支付结束 ****************************************************************************/
                
                outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.SUCCESS.getResultID(), outputData, "");
                return outputBean;
                
            }
            
        } catch (Exception e) {
            Log.error(logger, uuid, "拆单出错", e);
            outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.INSIDE_ERROR.getResultID(), null, "");
            Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
            return outputBean;
        }
        
    }
    
    @Override
    public BaseService clone() throws CloneNotSupportedException {
        return new SplitOrderImpl();
    }
    
    private Map<String, List<OsOrderItemsPO>> getOsOrderItem(List<TOsParentOrderItemsPO> parentOrderItemList, OsOrderItemsDao osOrderItemDao) throws Exception {
        
        // 将礼盒内部商品组成map
        Map<Long, List<TOsParentOrderItemsPO>> giftBoxMap = new HashMap<Long, List<TOsParentOrderItemsPO>>();
        for (TOsParentOrderItemsPO item : parentOrderItemList) {
            if (item.getGiftboxId().intValue() != 0) {
                if (giftBoxMap.containsKey(Long.valueOf(item.getGiftboxId()))) {
                    giftBoxMap.get(Long.valueOf(item.getGiftboxId())).add(item);
                } else {
                    List<TOsParentOrderItemsPO> items = new ArrayList<TOsParentOrderItemsPO>();
                    items.add(item);
                    giftBoxMap.put(Long.valueOf(item.getGiftboxId()), items);
                }
            }
        }
        
        Map<String, List<OsOrderItemsPO>> splitMap = new HashMap<String, List<OsOrderItemsPO>>();
        
        for (TOsParentOrderItemsPO item : parentOrderItemList) {
            if (item.getGiftboxId().intValue() == 0) {// 除去礼盒内部商品
                if (item.getWarehouseId() == null || item.getDcId() == null) {
                    Log.info(logger, uuid, "有商品库房或者配送公司为空,不能拆单", "parentOrderId", item.getOrderId(), "goodsId", item.getGoodsId());
                    return new HashMap<String, List<OsOrderItemsPO>>();
                }
                String key = "warehouse:" + item.getWarehouseId() + "-DC:"
                                + item.getDcId();
                if (BooleanEnum.TRUE.getV().equals(item.getIsGiftbox())) {// 礼盒
                    // 遍历礼盒内部商品，按照温区分开
                    Map<String, List<OsOrderItemsPO>> giftBoxInsideMap = new HashMap<String, List<OsOrderItemsPO>>();
                    for (TOsParentOrderItemsPO giftBoxItem : giftBoxMap.get(item.getItemId())) {
                        if (giftBoxItem.getWarmzoneId() == null) {
                            Log.info(logger, uuid, "礼盒内部商品温区为空,不能拆单", "parentOrderId", item.getOrderId(), "goodsId", giftBoxItem.getGoodsId());
                            return new HashMap<String, List<OsOrderItemsPO>>();
                        }
                        String giftBoxKey = key + "-warmZone:"
                                        + giftBoxItem.getWarmzoneId();
                        OsOrderItemsPO osOrderItem = new OsOrderItemsPO();
                        osOrderItem.setShopPrice(giftBoxItem.getShopPrice());
                        osOrderItem.setGoodsPrice(giftBoxItem.getGoodsPrice());
                        this.setCommonOsOrderItem(osOrderItem, giftBoxItem);
                        
                        if (giftBoxInsideMap.containsKey(giftBoxKey)) {
                            giftBoxInsideMap.get(giftBoxKey).add(osOrderItem);
                        } else {
                            List<OsOrderItemsPO> giftBoxList = new ArrayList<OsOrderItemsPO>();
                            giftBoxList.add(osOrderItem);
                            giftBoxInsideMap.put(giftBoxKey, giftBoxList);
                        }
                    }
                    // 增加礼盒信息
                    
                    int i = 1;
                    BigDecimal goodsAmountSum = BigDecimal.ZERO;
                    BigDecimal goodsPriceSum = BigDecimal.ZERO;
                    
                    for (Map.Entry<String, List<OsOrderItemsPO>> entry : giftBoxInsideMap.entrySet()) {
                        Long itemId = osOrderItemDao.findSeqNextVal("seq_os_order_items_pk");
                        if (itemId == -1) {
                            Log.info(logger, uuid, "获得子订单详情表主键失败");
                            return new HashMap<String, List<OsOrderItemsPO>>();
                        }
                        OsOrderItemsPO osOrderItem = new OsOrderItemsPO();
                        osOrderItem.setItemId(itemId);
                        BigDecimal shopPrice = BigDecimal.ZERO;
                        BigDecimal goodsPrice = BigDecimal.ZERO;
                        BigDecimal discountFee = BigDecimal.ZERO;
                        BigDecimal otherDiscountFee = BigDecimal.ZERO;
                        BigDecimal goodsAmount = BigDecimal.ZERO;
                        for (OsOrderItemsPO itemPo : entry.getValue()) {
                            itemPo.setGiftboxId(itemId.intValue());
                            shopPrice = shopPrice.add(itemPo.getShopPrice().multiply(itemPo.getGoodsNumber()));
                            goodsPrice = goodsPrice.add(itemPo.getGoodsPrice().multiply(itemPo.getGoodsNumber()));
                            if (itemPo.getDiscountFee() != null) {
                                discountFee = discountFee.add(itemPo.getDiscountFee());
                            }
                            if (itemPo.getOtherDiscountFee() != null) {
                                otherDiscountFee = otherDiscountFee.add(itemPo.getOtherDiscountFee());
                            }
                            if (itemPo.getGoodsAmount() != null) {
                                goodsAmount = goodsAmount.add(itemPo.getGoodsAmount());
                            }
                        }
                        osOrderItem.setShopPrice(shopPrice.divide(item.getGoodsNumber(), 2, BigDecimal.ROUND_HALF_UP));
                        
                        osOrderItem.setGiftboxId(Integer.valueOf(0));
                        this.setCommonOsOrderItem(osOrderItem, item);
                        osOrderItem.setDiscountFee(discountFee);
                        osOrderItem.setOtherDiscountFee(otherDiscountFee);
                        /****增加原始订单折扣数据 by zhaochunna  2015-7-17 ****/
                        osOrderItem.setOrigDiscountFee(discountFee);
                        osOrderItem.setOrigOtherDiscountFee(otherDiscountFee);
                        
                        if (i == giftBoxInsideMap.size()) {// 最后一个
                            osOrderItem.setGoodsAmount(item.getGoodsAmount().subtract(goodsAmountSum));
                            osOrderItem.setGoodsPrice(item.getGoodsPrice().subtract(goodsPriceSum));
                        } else {
                            osOrderItem.setGoodsAmount(goodsAmount);
                            osOrderItem.setGoodsPrice(goodsPrice.divide(item.getGoodsNumber(), 2, BigDecimal.ROUND_HALF_UP));
                        }
                        
                        entry.getValue().add(osOrderItem);
                        if (splitMap.containsKey(entry.getKey())) {
                            splitMap.get(entry.getKey()).addAll(entry.getValue());
                        } else {
                            splitMap.put(entry.getKey(), entry.getValue());
                        }
                        
                        goodsAmountSum = goodsAmountSum.add(goodsAmount);
                        goodsPriceSum = goodsPriceSum.add(osOrderItem.getGoodsPrice());
                        i++;
                        
                    }
                    
                } else {// 不是礼盒且不是礼盒内部商品, 直接构建
                    if (item.getWarmzoneId() == null) {
                        Log.info(logger, uuid, "有商品温区为空,不能拆单", "goodsId", item.getGoodsId());
                        return new HashMap<String, List<OsOrderItemsPO>>();
                    }
                    String keyNotGiftBox = key + "-warmZone:"
                                    + item.getWarmzoneId();
                    OsOrderItemsPO osOrderItem = new OsOrderItemsPO();
                    osOrderItem.setShopPrice(item.getShopPrice());
                    osOrderItem.setGoodsPrice(item.getGoodsPrice());
                    osOrderItem.setGiftboxId(Integer.valueOf(0));
                    this.setCommonOsOrderItem(osOrderItem, item);
                    
                    if (splitMap.containsKey(keyNotGiftBox)) {
                        splitMap.get(keyNotGiftBox).add(osOrderItem);
                    } else {
                        List<OsOrderItemsPO> giftBoxList = new ArrayList<OsOrderItemsPO>();
                        giftBoxList.add(osOrderItem);
                        splitMap.put(keyNotGiftBox, giftBoxList);
                    }
                }
            }
        }
        return splitMap;
        
    }
    
    private List<OsOrderPO> getOsOrder(Map<String, List<OsOrderItemsPO>> osOrderItemMap, OsOrderDao osOrderDao, List<OsOrderItemsPO> orderItemList, TOsParentOrderPO parentOrderPo, Map<Long, String> companyMap) throws Exception {
        
        List<OsOrderPO> osOrderList = new ArrayList<OsOrderPO>();
        int i = 1;
        BigDecimal shipFeeSum = BigDecimal.ZERO;
        BigDecimal shipCallFeeSum = BigDecimal.ZERO;
        BigDecimal orderFeeSum = BigDecimal.ZERO;
        BigDecimal orderCallFeeSum = BigDecimal.ZERO;
        BigDecimal discountFeeSum = BigDecimal.ZERO;
        for (Map.Entry<String, List<OsOrderItemsPO>> entry : osOrderItemMap.entrySet()) {
            String[] keyArray = entry.getKey().split("-");
            OsOrderPO osOrder = new OsOrderPO();
            Long orderId = osOrderDao.findSeqNextVal("seq_os_order_pk");
            if (orderId == -1) {
                Log.info(logger, uuid, "获得子订单表主键失败");
                return new ArrayList<OsOrderPO>();
            }
            
            BigDecimal goodsFee = BigDecimal.ZERO;
            BigDecimal goodsCallFee = BigDecimal.ZERO;
            BigDecimal discountFee = BigDecimal.ZERO;
            String isContainGift = BooleanEnum.FALSE.getV();
            for (OsOrderItemsPO orderItem : entry.getValue()) {
                orderItem.setOrderId(orderId);
                if (orderItem.getGiftboxId().intValue() == 0) {// 不是礼盒内部商品(包括普通商品和礼盒本身)
                    goodsFee = goodsFee.add(orderItem.getShopPrice().multiply(orderItem.getGoodsNumber()));
                    goodsCallFee = goodsCallFee.add(orderItem.getGoodsAmount());
                    BigDecimal fee = BigDecimal.ZERO;
                    if (orderItem.getDiscountFee() != null) {
                        fee = fee.add(orderItem.getDiscountFee());
                    }
                    if (orderItem.getOtherDiscountFee() != null) {
                        fee = fee.add(orderItem.getOtherDiscountFee());
                    }
                    discountFee = discountFee.add(fee);
                }
                if (BooleanEnum.TRUE.getV().equals(orderItem.getIsGiftbox())) {// 包含礼盒
                    isContainGift = BooleanEnum.TRUE.getV();
                }
            }
            osOrder.setGoodsFee(goodsFee);
            osOrder.setGoodsCallFee(goodsCallFee);
            if (i == osOrderItemMap.size()) {// 最后一个
                osOrder.setShipFee(parentOrderPo.getShipFee().subtract(shipFeeSum));
                osOrder.setShipCallFee(parentOrderPo.getShipCallFee().subtract(shipCallFeeSum));
                osOrder.setOrderFee(parentOrderPo.getOrderFee().subtract(orderFeeSum));
                osOrder.setOrderCallFee(parentOrderPo.getOrderCallFee().subtract(orderCallFeeSum));
                osOrder.setDiscountFee(parentOrderPo.getDiscountFee().subtract(discountFeeSum));
            } else {
                osOrder.setShipFee(goodsCallFee.multiply(parentOrderPo.getShipFee()).divide(parentOrderPo.getGoodsCallFee().compareTo(BigDecimal.ZERO) == 0?parentOrderPo.getGoodsFee():parentOrderPo.getGoodsCallFee(), 2, BigDecimal.ROUND_HALF_UP));
                osOrder.setShipCallFee(goodsCallFee.multiply(parentOrderPo.getShipCallFee()).divide(parentOrderPo.getGoodsCallFee().compareTo(BigDecimal.ZERO) == 0?parentOrderPo.getGoodsFee():parentOrderPo.getGoodsCallFee(), 2, BigDecimal.ROUND_HALF_UP));
                osOrder.setOrderFee(osOrder.getGoodsFee().add(osOrder.getShipFee()));
                osOrder.setOrderCallFee(osOrder.getGoodsCallFee().add(osOrder.getShipCallFee()));
                osOrder.setDiscountFee(discountFee);
            }
            osOrder.setOrderId(orderId);
            String orderCode = SqlUtil.getOsOrderCodeLoop(logger, uuid, osOrderDao.getWriteConnectionName());
            if (StringUtil.isEmpty(orderCode)) {
                Log.info(logger, uuid, "得不到子订单的code");
                return new ArrayList<OsOrderPO>();
            }
            Log.info(logger, uuid, "获得子订单的订单号", "orderCode", orderCode);
            osOrder.setOrderCode(orderCode);
            
            osOrder.setOnlinePayFee(BigDecimal.ZERO);
            osOrder.setOfflinePayFee(BigDecimal.ZERO);
            
            osOrder.setDcId(Long.valueOf(keyArray[1].split(":")[1]));
            osOrder.setWarehouseId(Long.valueOf(keyArray[0].split(":")[1]));
            osOrder.setTzoneId(Long.valueOf(keyArray[2].split(":")[1]));
            osOrder.setIsContainGift(isContainGift);
            // 拆单后把不需要调度的子订单的调度信息抹掉
            osOrder.setSdcId(DeliveryTypeEnum.TTCN.getC().equals(companyMap.get(osOrder.getDcId()))?parentOrderPo.getSdcId():Long.valueOf(-1));
            osOrder.setSdcName(DeliveryTypeEnum.TTCN.getC().equals(companyMap.get(osOrder.getDcId()))?parentOrderPo.getSdcName():"未指定");
            osOrder.setDeliveryType(companyMap.get(osOrder.getDcId()));
            
            this.setCommonOsOrder(osOrder, parentOrderPo);
            
            osOrderList.add(osOrder);
            orderItemList.addAll(entry.getValue());
            
            shipFeeSum = shipFeeSum.add(osOrder.getShipFee());
            shipCallFeeSum = shipCallFeeSum.add(osOrder.getShipCallFee());
            orderFeeSum = orderFeeSum.add(osOrder.getOrderFee());
            orderCallFeeSum = orderCallFeeSum.add(osOrder.getOrderCallFee());
            discountFeeSum = discountFeeSum.add(osOrder.getDiscountFee());
            i++;
        }
        
        return osOrderList;
        
    }
    
    private void setCommonOsOrderItem(OsOrderItemsPO osOrderItem, TOsParentOrderItemsPO giftBoxItem) {
        
        osOrderItem.setSku(giftBoxItem.getSku());
        osOrderItem.setGoodsId(giftBoxItem.getGoodsId());
        osOrderItem.setGoodsEan(giftBoxItem.getGoodsEan());
        osOrderItem.setGoodsSn(giftBoxItem.getGoodsSn());
        osOrderItem.setGoodsNumber(giftBoxItem.getGoodsNumber());
        osOrderItem.setGoodsAmount(giftBoxItem.getGoodsAmount());
        osOrderItem.setPromoteId(giftBoxItem.getPromoteId());
        osOrderItem.setGoodsName(giftBoxItem.getGoodsName());
        osOrderItem.setGoodsType(giftBoxItem.getGoodsType());
        osOrderItem.setBrandId(giftBoxItem.getBrandId());
        osOrderItem.setBrandName(giftBoxItem.getBrandName());
        osOrderItem.setCatId(giftBoxItem.getCatId());
        osOrderItem.setCatName(giftBoxItem.getCatName());
        osOrderItem.setGoodsUnit(giftBoxItem.getGoodsUnit());
        osOrderItem.setGoodsDesc(giftBoxItem.getGoodsDesc());
        osOrderItem.setGoodsBrief(giftBoxItem.getGoodsBrief());
        osOrderItem.setPicPath(giftBoxItem.getPicPath());
        osOrderItem.setGoodsWeight(giftBoxItem.getGoodsWeight());
        osOrderItem.setUnitWeight(giftBoxItem.getUnitWeight());
        osOrderItem.setUnitName(giftBoxItem.getUnitName());
        osOrderItem.setGoodsNameEn(giftBoxItem.getGoodsNameEn());
        osOrderItem.setBrandNameEn(giftBoxItem.getBrandNameEn());
        osOrderItem.setGoodsUnitEn(giftBoxItem.getGoodsUnitEn());
        osOrderItem.setUnitNameEn(giftBoxItem.getUnitNameEn());
        osOrderItem.setIsGiftbox(giftBoxItem.getIsGiftbox());
        osOrderItem.setBuyFrom(giftBoxItem.getBuyFrom());
        osOrderItem.setIsGift(giftBoxItem.getIsGift());
        osOrderItem.setGiftFrom(giftBoxItem.getGiftFrom());
        osOrderItem.setProduceDt(giftBoxItem.getProduceDt());
        osOrderItem.setPurchaseDate(giftBoxItem.getPurchaseDate());
        osOrderItem.setReviewId(giftBoxItem.getReviewId());
        osOrderItem.setDiscountFee(giftBoxItem.getDiscountFee());
        osOrderItem.setOtherDiscountFee(giftBoxItem.getOtherDiscountFee());
        osOrderItem.setStatus(giftBoxItem.getStatus());
        osOrderItem.setWarmzoneId(giftBoxItem.getWarmzoneId());
        osOrderItem.setBasicId(giftBoxItem.getBasicId());
        osOrderItem.setDistributedNum(BigDecimal.ZERO);
        osOrderItem.setShopAmount(osOrderItem.getGoodsNumber().multiply(osOrderItem.getShopPrice()));
        //添加兑换券信息
        if(!StringUtil.isEmpty(giftBoxItem.getExCouponSn()))
        	osOrderItem.setExCouponSn(giftBoxItem.getExCouponSn());
        
        /****增加原始订单折扣数据 by zhaochunna  2015-7-17 ****/
        osOrderItem.setOrigDiscountFee(giftBoxItem.getOrigDiscountFee());
        osOrderItem.setOrigOtherDiscountFee(giftBoxItem.getOrigOtherDiscountFee());
        
    }
    
    private void setCommonOsOrder(OsOrderPO osOrder, TOsParentOrderPO parentOrderPo) {
        
        osOrder.setBuyerId(parentOrderPo.getBuyerId());
        osOrder.setCreateDt(parentOrderPo.getCreateDt());
        osOrder.setPayDt(parentOrderPo.getPayDt());
        osOrder.setCreateIp(parentOrderPo.getCreateIp());
        osOrder.setOrderStatus(OrderStatusEnum.DEFAULT.getC());
        osOrder.setShipProvince(parentOrderPo.getShipProvince());
        osOrder.setShipCity(parentOrderPo.getShipCity());
        osOrder.setShipDistrict(parentOrderPo.getShipDistrict());
        osOrder.setShipAddr(parentOrderPo.getShipAddr());
        osOrder.setShipDesc(parentOrderPo.getShipDesc());
        osOrder.setZipcode(parentOrderPo.getZipcode());
        osOrder.setReceiver(parentOrderPo.getReceiver());
        osOrder.setTel(parentOrderPo.getTel());
        osOrder.setMobile(parentOrderPo.getMobile());
        osOrder.setEmail(parentOrderPo.getEmail());
        osOrder.setReceiveDt(parentOrderPo.getReceiveDt());
        osOrder.setReceiveTimeSeg(parentOrderPo.getReceiveTimeSeg());
        osOrder.setPromotionId(parentOrderPo.getPromotionId());
        osOrder.setPayStatus(parentOrderPo.getPayStatus());
        osOrder.setRemark(parentOrderPo.getRemark());
        osOrder.setVipflag(parentOrderPo.getVipflag());
        osOrder.setPayingMethod("");
        osOrder.setSystemRemark(parentOrderPo.getSystemRemark());
        osOrder.setActName(parentOrderPo.getActName());
        osOrder.setPayType(parentOrderPo.getPayType());
        osOrder.setOrderType(parentOrderPo.getOrderType());
        osOrder.setIsEnOrder(parentOrderPo.getIsEnOrder());
        osOrder.setOrderFrom(parentOrderPo.getOrderFrom());
        osOrder.setCreateByName(parentOrderPo.getCreateByName());
        osOrder.setPromotionFrom(parentOrderPo.getPromotionFrom());
        osOrder.setAddrId(parentOrderPo.getAddrId());
        osOrder.setIsFirst(parentOrderPo.getIsFirst());
        osOrder.setOrderMarks(parentOrderPo.getOrderMarks());
        osOrder.setInsourceFrom(parentOrderPo.getInsourceFrom());
        osOrder.setReceiptShowAmount(parentOrderPo.getReceiptShowAmount());
        osOrder.setPayReceiveStatus(parentOrderPo.getPayReceiveStatus());
        osOrder.setPaidMethodIds(parentOrderPo.getPaidMethodIds());
        osOrder.setParentId(parentOrderPo.getOrderId());
        osOrder.setParentCode(parentOrderPo.getOrderCode());
        osOrder.setLotCode(parentOrderPo.getLotCode());
        osOrder.setShelfType(null);
        osOrder.setSplitDt(new Timestamp(System.currentTimeMillis()));
        osOrder.setSplitStatus(0L);
        osOrder.setNotifyStatus(null);
        osOrder.setDeliveryTimeType(parentOrderPo.getDeliveryTimeType());
        osOrder.setDeliveryTimeFee(null);
        osOrder.setSubstationId(parentOrderPo.getSubstationId());
        osOrder.setCourierId(null);
        osOrder.setDelayReceiveDt(null);
        osOrder.setCheckUserId(null);
        osOrder.setCheckDt(null);
        osOrder.setPackageNum(0L);
        osOrder.setNeedPayFee(osOrder.getOrderCallFee().subtract(osOrder.getDiscountFee()));
        osOrder.setCouponFee(BigDecimal.ZERO);
        osOrder.setSpecifiedShippingdate(parentOrderPo.getSpecifiedShippingdate());
        osOrder.setTeamId(parentOrderPo.getTeamId());
        osOrder.setActivityId(parentOrderPo.getActivityId());
        osOrder.setActiveType(parentOrderPo.getActiveType());
        osOrder.setActiveId(parentOrderPo.getActiveId());
        osOrder.setChgPay(parentOrderPo.getChgPay());
        osOrder.setSalenumAdjust(parentOrderPo.getSalenumAdjust());
        osOrder.setReceiveDtMsg(parentOrderPo.getReceiveDtMsg());
        
        //发现频道新增订单归属类型和订单归属商家 by zhaochunna at 2015-10-27 start
        osOrder.setOrderPtype(parentOrderPo.getOrderPtype());
        osOrder.setSellerId(parentOrderPo.getSellerId());
        //发现频道新增订单归属类型和订单归属商家 by zhaochunna at 2015-10-27 end
        
        osOrder.setBusinessLine(parentOrderPo.getBusinessLine());
        
    }
    
    private Map<String, List<OsOrderItemsPO>> getOsOrderItemFromSubOrder(List<OsOrderItemsPO> osOrderItemList, OsOrderPO osOrderPo, OsOrderItemsDao osOrderItemDao) throws Exception {
        
        // 将礼盒内部商品组成map
        Map<Long, List<OsOrderItemsPO>> giftBoxMap = new HashMap<Long, List<OsOrderItemsPO>>();
        for (OsOrderItemsPO item : osOrderItemList) {
            if (item.getGiftboxId().intValue() != 0) {
                if (giftBoxMap.containsKey(Long.valueOf(item.getGiftboxId()))) {
                    giftBoxMap.get(Long.valueOf(item.getGiftboxId())).add(item);
                } else {
                    List<OsOrderItemsPO> items = new ArrayList<OsOrderItemsPO>();
                    items.add(item);
                    giftBoxMap.put(Long.valueOf(item.getGiftboxId()), items);
                }
            }
        }
        
        Map<String, List<OsOrderItemsPO>> splitMap = new HashMap<String, List<OsOrderItemsPO>>();
        
        String key = "warehouse:" + osOrderPo.getWarehouseId() + "-DC:"
                        + osOrderPo.getDcId();
        
        for (OsOrderItemsPO item : osOrderItemList) {
            if (item.getGiftboxId().intValue() == 0) {// 除去礼盒内部商品
            
                if (BooleanEnum.TRUE.getV().equals(item.getIsGiftbox())) {// 礼盒
                    // 遍历礼盒内部商品，按照温区分开
                    Map<String, List<OsOrderItemsPO>> giftBoxInsideMap = new HashMap<String, List<OsOrderItemsPO>>();
                    for (OsOrderItemsPO giftBoxItem : giftBoxMap.get(item.getItemId())) {
                        if (giftBoxItem.getWarmzoneId() == null) {
                            Log.info(logger, uuid, "礼盒内部商品温区为空,不能拆单", "parentOrderId", item.getOrderId(), "goodsId", giftBoxItem.getGoodsId());
                            return new HashMap<String, List<OsOrderItemsPO>>();
                        }
                        String giftBoxKey = key + "-warmZone:"
                                        + giftBoxItem.getWarmzoneId();
                        OsOrderItemsPO osOrderItem = new OsOrderItemsPO();
                        osOrderItem.setShopPrice(giftBoxItem.getShopPrice());
                        osOrderItem.setGoodsPrice(giftBoxItem.getGoodsPrice());
                        this.setCommonOsOrderItemFromSubOrder(osOrderItem, giftBoxItem);
                        
                        if (giftBoxInsideMap.containsKey(giftBoxKey)) {
                            giftBoxInsideMap.get(giftBoxKey).add(osOrderItem);
                        } else {
                            List<OsOrderItemsPO> giftBoxList = new ArrayList<OsOrderItemsPO>();
                            giftBoxList.add(osOrderItem);
                            giftBoxInsideMap.put(giftBoxKey, giftBoxList);
                        }
                    }
                    // 增加礼盒信息
                    int i = 1;
                    BigDecimal goodsAmountSum = BigDecimal.ZERO;
                    BigDecimal goodsPriceSum = BigDecimal.ZERO;
                    
                    for (Map.Entry<String, List<OsOrderItemsPO>> entry : giftBoxInsideMap.entrySet()) {
                        Long itemId = osOrderItemDao.findSeqNextVal("seq_os_order_items_pk");
                        if (itemId == -1) {
                            Log.info(logger, uuid, "获得子订单详情表主键失败");
                            return new HashMap<String, List<OsOrderItemsPO>>();
                        }
                        OsOrderItemsPO osOrderItem = new OsOrderItemsPO();
                        osOrderItem.setItemId(itemId);
                        BigDecimal shopPrice = BigDecimal.ZERO;
                        BigDecimal goodsPrice = BigDecimal.ZERO;
                        BigDecimal discountFee = BigDecimal.ZERO;
                        BigDecimal otherDiscountFee = BigDecimal.ZERO;
                        BigDecimal goodsAmount = BigDecimal.ZERO;
                        for (OsOrderItemsPO itemPo : entry.getValue()) {
                            itemPo.setGiftboxId(itemId.intValue());
                            shopPrice = shopPrice.add(itemPo.getShopPrice().multiply(itemPo.getGoodsNumber()));
                            goodsPrice = goodsPrice.add(itemPo.getGoodsPrice().multiply(itemPo.getGoodsNumber()));
                            if (itemPo.getDiscountFee() != null) {
                                discountFee = discountFee.add(itemPo.getDiscountFee());
                            }
                            if (itemPo.getOtherDiscountFee() != null) {
                                otherDiscountFee = otherDiscountFee.add(itemPo.getOtherDiscountFee());
                            }
                            if (itemPo.getGoodsAmount() != null) {
                                goodsAmount = goodsAmount.add(itemPo.getGoodsAmount());
                            }
                        }
                        osOrderItem.setShopPrice(shopPrice.divide(item.getGoodsNumber(), 2, BigDecimal.ROUND_HALF_UP));
                        osOrderItem.setGiftboxId(Integer.valueOf(0));
                        this.setCommonOsOrderItemFromSubOrder(osOrderItem, item);
                        osOrderItem.setDiscountFee(discountFee);
                        osOrderItem.setOtherDiscountFee(otherDiscountFee);
                        /****增加原始订单折扣数据 by zhaochunna  2015-7-17 ****/
                        osOrderItem.setOrigDiscountFee(discountFee);
                        osOrderItem.setOrigOtherDiscountFee(otherDiscountFee);
                        
                        if (i == giftBoxInsideMap.size()) {// 最后一个
                            osOrderItem.setGoodsAmount(item.getGoodsAmount().subtract(goodsAmountSum));
                            osOrderItem.setGoodsPrice(item.getGoodsPrice().subtract(goodsPriceSum));
                        } else {
                            osOrderItem.setGoodsAmount(goodsAmount);
                            osOrderItem.setGoodsPrice(goodsPrice.divide(item.getGoodsNumber(), 2, BigDecimal.ROUND_HALF_UP));
                        }
                        
                        entry.getValue().add(osOrderItem);
                        if (splitMap.containsKey(entry.getKey())) {
                            splitMap.get(entry.getKey()).addAll(entry.getValue());
                        } else {
                            splitMap.put(entry.getKey(), entry.getValue());
                        }
                        
                        goodsAmountSum = goodsAmountSum.add(goodsAmount);
                        goodsPriceSum = goodsPriceSum.add(osOrderItem.getGoodsPrice());
                        i++;
                        
                    }
                    
                } else {// 不是礼盒且不是礼盒内部商品, 直接构建
                    if (item.getWarmzoneId() == null) {
                        Log.info(logger, uuid, "有商品温区为空,不能拆单", "goodsId", item.getGoodsId());
                        return new HashMap<String, List<OsOrderItemsPO>>();
                    }
                    String keyNotGiftBox = key + "-warmZone:"
                                    + item.getWarmzoneId();
                    OsOrderItemsPO osOrderItem = new OsOrderItemsPO();
                    osOrderItem.setShopPrice(item.getShopPrice());
                    osOrderItem.setGoodsPrice(item.getGoodsPrice());
                    osOrderItem.setGiftboxId(Integer.valueOf(0));
                    this.setCommonOsOrderItemFromSubOrder(osOrderItem, item);
                    
                    if (splitMap.containsKey(keyNotGiftBox)) {
                        splitMap.get(keyNotGiftBox).add(osOrderItem);
                    } else {
                        List<OsOrderItemsPO> giftBoxList = new ArrayList<OsOrderItemsPO>();
                        giftBoxList.add(osOrderItem);
                        splitMap.put(keyNotGiftBox, giftBoxList);
                    }
                }
            }
        }
        return splitMap;
        
    }
    
    private List<OsOrderPO> getOsOrderFromSubOrder(Map<String, List<OsOrderItemsPO>> osOrderItemMap, OsOrderDao osOrderDao, List<OsOrderItemsPO> orderItemList, OsOrderPO osOrderPo, StringBuffer orderCodes) throws Exception {
        
        List<OsOrderPO> osOrderList = new ArrayList<OsOrderPO>();
        int i = 1;
        BigDecimal shipFeeSum = BigDecimal.ZERO;
        BigDecimal shipCallFeeSum = BigDecimal.ZERO;
        BigDecimal orderFeeSum = BigDecimal.ZERO;
        BigDecimal orderCallFeeSum = BigDecimal.ZERO;
        BigDecimal discountFeeSum = BigDecimal.ZERO;
        for (Map.Entry<String, List<OsOrderItemsPO>> entry : osOrderItemMap.entrySet()) {
            String[] keyArray = entry.getKey().split("-");
            OsOrderPO osOrder = new OsOrderPO();
            Long orderId = osOrderDao.findSeqNextVal("seq_os_order_pk");
            if (orderId == -1) {
                Log.info(logger, uuid, "获得子订单表主键失败");
                return new ArrayList<OsOrderPO>();
            }
            
            BigDecimal goodsFee = BigDecimal.ZERO;
            BigDecimal goodsCallFee = BigDecimal.ZERO;
            BigDecimal discountFee = BigDecimal.ZERO;
            String isContainGift = BooleanEnum.FALSE.getV();
            for (OsOrderItemsPO orderItem : entry.getValue()) {
                orderItem.setOrderId(orderId);
                if (orderItem.getGiftboxId().intValue() == 0) {// 不是礼盒内部商品(包括普通商品和礼盒本身)
                    goodsFee = goodsFee.add(orderItem.getShopPrice().multiply(orderItem.getGoodsNumber()));
                    goodsCallFee = goodsCallFee.add(orderItem.getGoodsAmount());
                    BigDecimal fee = BigDecimal.ZERO;
                    if (orderItem.getDiscountFee() != null) {
                        fee = fee.add(orderItem.getDiscountFee());
                    }
                    if (orderItem.getOtherDiscountFee() != null) {
                        fee = fee.add(orderItem.getOtherDiscountFee());
                    }
                    discountFee = discountFee.add(fee);
                }
                if (BooleanEnum.TRUE.getV().equals(orderItem.getIsGiftbox())) {// 包含礼盒
                    isContainGift = BooleanEnum.TRUE.getV();
                }
            }
            osOrder.setGoodsFee(goodsFee);
            osOrder.setGoodsCallFee(goodsCallFee);
            if (i == osOrderItemMap.size()) {// 最后一个
                osOrder.setShipFee(osOrderPo.getShipFee().subtract(shipFeeSum));
                osOrder.setShipCallFee(osOrderPo.getShipCallFee().subtract(shipCallFeeSum));
                osOrder.setOrderFee(osOrderPo.getOrderFee().subtract(orderFeeSum));
                osOrder.setOrderCallFee(osOrderPo.getOrderCallFee().subtract(orderCallFeeSum));
                osOrder.setDiscountFee(osOrderPo.getDiscountFee().subtract(discountFeeSum));
            } else {
                osOrder.setShipFee(goodsCallFee.multiply(osOrderPo.getShipFee()).divide(osOrderPo.getGoodsCallFee().compareTo(BigDecimal.ZERO) == 0?osOrderPo.getGoodsFee():osOrderPo.getGoodsCallFee(), 2, BigDecimal.ROUND_HALF_UP));
                osOrder.setShipCallFee(goodsCallFee.multiply(osOrderPo.getShipCallFee()).divide(osOrderPo.getGoodsCallFee().compareTo(BigDecimal.ZERO) == 0?osOrderPo.getGoodsFee():osOrderPo.getGoodsCallFee(), 2, BigDecimal.ROUND_HALF_UP));
                osOrder.setOrderFee(osOrder.getGoodsFee().add(osOrder.getShipFee()));
                osOrder.setOrderCallFee(osOrder.getGoodsCallFee().add(osOrder.getShipCallFee()));
                osOrder.setDiscountFee(discountFee);
            }
            osOrder.setOrderId(orderId);
            String orderCode = SqlUtil.getOsOrderCodeLoop(logger, uuid, osOrderDao.getWriteConnectionName());
            if (StringUtil.isEmpty(orderCode)) {
                Log.info(logger, uuid, "得不到子订单的code");
                return new ArrayList<OsOrderPO>();
            }
            Log.info(logger, uuid, "获得子订单的订单号", "orderCode", orderCode);
            osOrder.setOrderCode(orderCode);
            orderCodes.append(orderCode);
            orderCodes.append("-");
            
            osOrder.setOnlinePayFee(BigDecimal.ZERO);
            osOrder.setOfflinePayFee(BigDecimal.ZERO);
            
            osOrder.setDcId(Long.valueOf(keyArray[1].split(":")[1]));
            osOrder.setWarehouseId(Long.valueOf(keyArray[0].split(":")[1]));
            osOrder.setTzoneId(Long.valueOf(keyArray[2].split(":")[1]));
            osOrder.setIsContainGift(isContainGift);
            // 拆单后把不需要调度的子订单的调度信息抹掉
            osOrder.setSdcId(osOrderPo.getSdcId());
            osOrder.setSdcName(osOrderPo.getSdcName());
            osOrder.setDeliveryType(osOrderPo.getDeliveryType());
            
            this.setCommonOsOrderFromSubOrder(osOrder, osOrderPo);
            
            osOrderList.add(osOrder);
            orderItemList.addAll(entry.getValue());
            
            shipFeeSum = shipFeeSum.add(osOrder.getShipFee());
            shipCallFeeSum = shipCallFeeSum.add(osOrder.getShipCallFee());
            orderFeeSum = orderFeeSum.add(osOrder.getOrderFee());
            orderCallFeeSum = orderCallFeeSum.add(osOrder.getOrderCallFee());
            discountFeeSum = discountFeeSum.add(osOrder.getDiscountFee());
            i++;
        }
        
        return osOrderList;
        
    }
    
    private void setCommonOsOrderItemFromSubOrder(OsOrderItemsPO osOrderItem, OsOrderItemsPO giftBoxItem) {
        
        osOrderItem.setSku(giftBoxItem.getSku());
        osOrderItem.setGoodsId(giftBoxItem.getGoodsId());
        osOrderItem.setGoodsEan(giftBoxItem.getGoodsEan());
        osOrderItem.setGoodsSn(giftBoxItem.getGoodsSn());
        osOrderItem.setGoodsNumber(giftBoxItem.getGoodsNumber());
        osOrderItem.setGoodsAmount(giftBoxItem.getGoodsAmount());
        osOrderItem.setPromoteId(giftBoxItem.getPromoteId());
        osOrderItem.setGoodsName(giftBoxItem.getGoodsName());
        osOrderItem.setGoodsType(giftBoxItem.getGoodsType());
        osOrderItem.setBrandId(giftBoxItem.getBrandId());
        osOrderItem.setBrandName(giftBoxItem.getBrandName());
        osOrderItem.setCatId(giftBoxItem.getCatId());
        osOrderItem.setCatName(giftBoxItem.getCatName());
        osOrderItem.setGoodsUnit(giftBoxItem.getGoodsUnit());
        osOrderItem.setGoodsDesc(giftBoxItem.getGoodsDesc());
        osOrderItem.setGoodsBrief(giftBoxItem.getGoodsBrief());
        osOrderItem.setPicPath(giftBoxItem.getPicPath());
        osOrderItem.setGoodsWeight(giftBoxItem.getGoodsWeight());
        osOrderItem.setUnitWeight(giftBoxItem.getUnitWeight());
        osOrderItem.setUnitName(giftBoxItem.getUnitName());
        osOrderItem.setGoodsNameEn(giftBoxItem.getGoodsNameEn());
        osOrderItem.setBrandNameEn(giftBoxItem.getBrandNameEn());
        osOrderItem.setGoodsUnitEn(giftBoxItem.getGoodsUnitEn());
        osOrderItem.setUnitNameEn(giftBoxItem.getUnitNameEn());
        osOrderItem.setIsGiftbox(giftBoxItem.getIsGiftbox());
        osOrderItem.setBuyFrom(giftBoxItem.getBuyFrom());
        osOrderItem.setIsGift(giftBoxItem.getIsGift());
        osOrderItem.setGiftFrom(giftBoxItem.getGiftFrom());
        osOrderItem.setProduceDt(giftBoxItem.getProduceDt());
        osOrderItem.setPurchaseDate(giftBoxItem.getPurchaseDate());
        osOrderItem.setReviewId(giftBoxItem.getReviewId());
        osOrderItem.setDiscountFee(giftBoxItem.getDiscountFee());
        osOrderItem.setOtherDiscountFee(giftBoxItem.getOtherDiscountFee());
        osOrderItem.setStatus(giftBoxItem.getStatus());
        osOrderItem.setWarmzoneId(giftBoxItem.getWarmzoneId());
        osOrderItem.setBasicId(giftBoxItem.getBasicId());
        osOrderItem.setDistributedNum(BigDecimal.ZERO);
        osOrderItem.setShopAmount(osOrderItem.getGoodsNumber().multiply(osOrderItem.getShopPrice()));
        //添加兑换券码
        if(!StringUtil.isEmpty(giftBoxItem.getExCouponSn()))
        	osOrderItem.setExCouponSn(giftBoxItem.getExCouponSn());
        
        /****增加原始订单折扣数据 by zhaochunna  2015-7-17 ****/
        osOrderItem.setOrigDiscountFee(giftBoxItem.getOrigDiscountFee());
        osOrderItem.setOrigOtherDiscountFee(giftBoxItem.getOrigOtherDiscountFee());
    }
    
    private void setCommonOsOrderFromSubOrder(OsOrderPO osOrder, OsOrderPO osOrderPo) {
        
        osOrder.setBuyerId(osOrderPo.getBuyerId());
        osOrder.setCreateDt(osOrderPo.getCreateDt());
        osOrder.setPayDt(osOrderPo.getPayDt());
        osOrder.setCreateIp(osOrderPo.getCreateIp());
        osOrder.setOrderStatus(OrderStatusEnum.DEFAULT.getC());
        osOrder.setShipProvince(osOrderPo.getShipProvince());
        osOrder.setShipCity(osOrderPo.getShipCity());
        osOrder.setShipDistrict(osOrderPo.getShipDistrict());
        osOrder.setShipAddr(osOrderPo.getShipAddr());
        osOrder.setShipDesc(osOrderPo.getShipDesc());
        osOrder.setZipcode(osOrderPo.getZipcode());
        osOrder.setReceiver(osOrderPo.getReceiver());
        osOrder.setTel(osOrderPo.getTel());
        osOrder.setMobile(osOrderPo.getMobile());
        osOrder.setEmail(osOrderPo.getEmail());
        osOrder.setReceiveDt(osOrderPo.getReceiveDt());
        osOrder.setReceiveTimeSeg(osOrderPo.getReceiveTimeSeg());
        osOrder.setPromotionId(osOrderPo.getPromotionId());
        osOrder.setPayStatus(osOrderPo.getPayStatus());
        osOrder.setRemark(osOrderPo.getRemark());
        osOrder.setVipflag(osOrderPo.getVipflag());
        osOrder.setPayingMethod("");
        osOrder.setSystemRemark(osOrderPo.getSystemRemark());
        osOrder.setActName(osOrderPo.getActName());
        osOrder.setPayType(osOrderPo.getPayType());
        osOrder.setOrderType(osOrderPo.getOrderType());
        osOrder.setIsEnOrder(osOrderPo.getIsEnOrder());
        osOrder.setOrderFrom(osOrderPo.getOrderFrom());
        osOrder.setCreateByName(osOrderPo.getCreateByName());
        osOrder.setPromotionFrom(osOrderPo.getPromotionFrom());
        osOrder.setAddrId(osOrderPo.getAddrId());
        osOrder.setIsFirst(osOrderPo.getIsFirst());
        osOrder.setOrderMarks(osOrderPo.getOrderMarks());
        osOrder.setInsourceFrom(osOrderPo.getInsourceFrom());
        osOrder.setReceiptShowAmount(osOrderPo.getReceiptShowAmount());
        osOrder.setPayReceiveStatus(osOrderPo.getPayReceiveStatus());
        osOrder.setPaidMethodIds(osOrderPo.getPaidMethodIds());
        osOrder.setParentId(osOrderPo.getParentId());
        osOrder.setParentCode(osOrderPo.getParentCode());
        osOrder.setLotCode(osOrderPo.getLotCode());
        osOrder.setShelfType(null);
        osOrder.setSplitDt(new Timestamp(System.currentTimeMillis()));
        osOrder.setSplitStatus(0L);
        osOrder.setNotifyStatus(null);
        osOrder.setDeliveryTimeType(osOrderPo.getDeliveryTimeType());
        osOrder.setDeliveryTimeFee(null);
        osOrder.setSubstationId(osOrderPo.getSubstationId());
        osOrder.setCourierId(null);
        osOrder.setDelayReceiveDt(null);
        osOrder.setCheckUserId(null);
        osOrder.setCheckDt(null);
        osOrder.setPackageNum(0L);
        osOrder.setNeedPayFee(osOrder.getOrderCallFee().subtract(osOrder.getDiscountFee()));
        osOrder.setCouponFee(BigDecimal.ZERO);
        osOrder.setSpecifiedShippingdate(osOrderPo.getSpecifiedShippingdate());
        osOrder.setTeamId(osOrderPo.getTeamId());
        osOrder.setActivityId(osOrderPo.getActivityId());
        osOrder.setActiveType(osOrderPo.getActiveType());
        osOrder.setActiveId(osOrderPo.getActiveId());
        osOrder.setChgPay(osOrderPo.getChgPay());
        osOrder.setSalenumAdjust(osOrderPo.getSalenumAdjust());
        osOrder.setReceiveDtMsg(osOrderPo.getReceiveDtMsg());
        
        //发现频道新增订单归属类型和订单归属商家 by zhaochunna at 2015-10-27 start
        osOrder.setOrderPtype(osOrderPo.getOrderPtype());
        osOrder.setSellerId(osOrderPo.getSellerId());
       //发现频道新增订单归属类型和订单归属商家 by zhaochunna at 2015-10-27 end
        
        osOrder.setBusinessLine(osOrderPo.getBusinessLine());
        
    }
    
    private OsOrderOptPO getOrderOpt(Timestamp optDate, Long orderId, Long userId, String orderStatus, String exceptionStatus, String disputeStatus, String remark, String payStatus, String notifyStatus, String userName, String isShow, String remarkEn) {
        OsOrderOptPO opt = new OsOrderOptPO();
        opt.setOrderId(orderId);
        if (optDate == null) {
            opt.setOptDt(new Timestamp(System.currentTimeMillis()));
        } else {
            opt.setOptDt(optDate);
        }
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
package cn.tootoo.soa.oms.thirdordercancel.delegate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.tootoo.db.egrocery.osorder.OsOrderPO;
import cn.tootoo.db.egrocery.teipsuborder.TEipSubOrderPO;
import cn.tootoo.db.egrocery.tosparentorder.TOsParentOrderPO;
import cn.tootoo.db.egrocery.ttsmail.TtsMailPO;
import cn.tootoo.enums.BaseResultEnum;
import cn.tootoo.global.Global;
import cn.tootoo.http.AuthorizeClient;
import cn.tootoo.http.TootooService;
import cn.tootoo.http.bean.BaseInputHead;
import cn.tootoo.http.bean.BaseOutputBean;
import cn.tootoo.http.bean.BaseOutputHead;
import cn.tootoo.soa.base.enums.OrderFromEnum;
import cn.tootoo.soa.base.enums.OrderStatusEnum;
import cn.tootoo.soa.oms.exceptions.OmsDelegateException;
import cn.tootoo.soa.oms.ordercancel.input.OmsOrderCancelInputData;
import cn.tootoo.soa.oms.subordercancel.input.OmsSubOrderCancelInputData;
import cn.tootoo.soa.oms.subordercancel.output.OmsSubOrderCancelOutputData;
import cn.tootoo.soa.oms.thirdordercancel.input.OmsThirdOrderCancelInputData;
import cn.tootoo.soa.oms.thirdordercancel.output.OmsThirdOrderCancelOutputData;
import cn.tootoo.soa.oms.thirdordercancel.output.OmsThirdOrderCancelResultEnum;
import cn.tootoo.soa.oms.utils.LogUtils4Oms;
import cn.tootoo.utils.MailUtil;

/**
 * 服务委托者实现类。
 * 服务description：订单服务。
 * 服务remark：订单服务。
 * 接口description：第三方订单取消。
 * 接口remark：
 */
public final class ThirdOrderCancelDelegate extends AbstractThirdOrderCancelDelegate implements Cloneable {
    
    static {
        ThirdOrderCancelDelegateFactory.registPrototype(new ThirdOrderCancelDelegate());
    }
    
    /**
     * 克隆方法。
     */
    @Override
    public AbstractThirdOrderCancelDelegate clone() throws CloneNotSupportedException {
        return new ThirdOrderCancelDelegate();
    }
    
    /**
     * 初始化方法。
     * 该方法中通常实现如下功能：验证登录、加锁、初始化DAO对象等等。
     * 
     * @param inputHead 该接口的请求参数头。
     * @param inputData 该接口的请求参数数据体。
     * @param outputHead 该接口的返回结果头。
     * @param outputData 该接口的返回结果数据体。
     */
    @Override
    public void doInit(final BaseInputHead inputHead, final OmsThirdOrderCancelInputData inputData, final BaseOutputHead outputHead, final OmsThirdOrderCancelOutputData outputData) throws OmsDelegateException {
        
    }
    
    /**
     * 初始检查方法。
     * 该方法在实际执行服务功能之前进行检查，默认返回true。
     * 
     * @param inputHead 该接口的请求参数头。
     * @param inputData 该接口的请求参数数据体。
     * @param outputHead 该接口的返回结果头。
     * @param outputData 该接口的返回结果数据体。
     * @return boolean 检查是否通过。
     */
    @Override
    public boolean doCheck(final BaseInputHead inputHead, final OmsThirdOrderCancelInputData inputData, final BaseOutputHead outputHead, final OmsThirdOrderCancelOutputData outputData) {
        try {
            return true;
        } catch (Exception e) {
            LogUtils4Oms.info(logger, uuid, "初始检查方法出现异常，返回false！", e);
            return false;
        }
    }
    
    /**
     * 功能执行方法。
     * 该方法中执行实际的服务功能。
     * 
     * @param inputHead 该接口的请求参数头。
     * @param inputData 该接口的请求参数数据体。
     * @param outputHead 该接口的返回结果头。
     * @param outputData 该接口的返回结果数据体。
     */
    @Override
    public void doExecute(final BaseInputHead inputHead, final OmsThirdOrderCancelInputData inputData, final BaseOutputHead outputHead, final OmsThirdOrderCancelOutputData outputData) throws OmsDelegateException {
        // 接受参数
        Long orderId = inputData.getOrderId();
        Long userId = inputData.getUserId();
        Long caller = inputData.getCaller();
        Long cancelReason = inputData.getCancelReason();
        String cancelReasonOther = inputData.getCancelReasonOther();
        try {
            // 第三方订单作废
            cancelParentOrder(orderId, userId, caller, cancelReason, cancelReasonOther, outputHead);
            LogUtils4Oms.info(logger, uuid, "三方父订单取消成功！", "orderId", orderId);
            Global.getOutputHead(BaseResultEnum.SUCCESS.getResultID(), null, outputHead);
        } catch (OmsDelegateException e) {
            LogUtils4Oms.info(logger, uuid, "三方父订单作废失败", e);
            outputHead.setResultID(e.getResultID());
            outputHead.setResultMessage(e.getMessage());
            // throw e; 
            Global.getOutputHead(e.getResultID(), null, outputHead);
        } catch (Exception e) {
            LogUtils4Oms.info(logger, uuid, "三方父订单取消失败!", e);
            Global.getOutputHead(OmsThirdOrderCancelResultEnum.CANCEL_FAIL.getResultID(), null, outputHead);
            throw new OmsDelegateException(OmsThirdOrderCancelResultEnum.CANCEL_FAIL.getResultID(), "三方父订单取消失败!", e);
        }
    }
    
    // 第三方订单作废
    private void cancelParentOrder(Long orderId, Long userId, Long caller, Long cancelReason, String cancelReasonOther, final BaseOutputHead outputHead) throws OmsDelegateException, Exception {
        TOsParentOrderPO parentOrder = null;
        if (orderId == null) {
            LogUtils4Oms.info(logger, uuid, "三方订单作废参数orderId为空", "orderId", orderId);
            throw new OmsDelegateException(OmsThirdOrderCancelResultEnum.CANCEL_FAIL.getResultID(), "订单号为空！", null);
        }
        if (caller > -1 || caller < -3) {
            LogUtils4Oms.info(logger, uuid, "三方订单作废参数caller不正确!", "caller", caller);
            throw new OmsDelegateException(OmsThirdOrderCancelResultEnum.CANCEL_FAIL.getResultID(), "参数caller不正确！", null);
        }
        parentOrder = tOsParentOrderDao.findTOsParentOrderPOByID(orderId);
        if (parentOrder == null) {
            LogUtils4Oms.info(logger, uuid, "三方作废订单不存在!", "orderId", orderId);
            throw new OmsDelegateException(OmsThirdOrderCancelResultEnum.CANCEL_FAIL.getResultID(), "三方订单不存在！", null);
        }
        if (!isOtherOrder(parentOrder)) {
            LogUtils4Oms.info(logger, uuid, "父订单非三方订单来源！", "orderFrom", parentOrder.getOrderFrom());
            throw new OmsDelegateException(OmsThirdOrderCancelResultEnum.CANCEL_FAIL.getResultID(), "父订单非第三方来源！", null);
        }
        Long orderStatus = parentOrder.getOrderStatus();
        if (orderStatus <= -1 && orderStatus >= -3) {// 订单状态为作废，则直接返回
            LogUtils4Oms.info(logger, uuid, "三方父订单已经作废!", "orderId", orderId);
            Global.getOutputHead(BaseResultEnum.SUCCESS.getResultID(), null, outputHead);
            return;
        }
        if (parentOrder.getOrderStatus() == -4) {
            LogUtils4Oms.info(logger, uuid, "三方父订单状态为异常状态，不能进行作废!", "orderId", orderId, "orderStatus", parentOrder.getOrderStatus());
            throw new OmsDelegateException(OmsThirdOrderCancelResultEnum.CANCEL_FAIL.getResultID(), "三方父订单状态异常！", null);
        }
        if (orderStatus >= 110 && orderStatus != 120) {// 拆单成功，作废子订单，否则作废父订单
            List<OsOrderPO> subOrders = osOrderDao.findOsOrderPOListBySql(" parent_code='"
                            + parentOrder.getOrderCode() + "'");
            if (subOrders == null) {
                LogUtils4Oms.info(logger, uuid, "拆单的第三方父订单不存在子订单！", "order_id", orderId, "parent_code", parentOrder.getOrderCode());
                throw new OmsDelegateException(OmsThirdOrderCancelResultEnum.CANCEL_FAIL.getResultID(), "拆单的第三方父订单不存在子订单！", null);
            }
            for (OsOrderPO subOrder : subOrders) {
                if (OrderStatusEnum.CANCEL == OrderStatusEnum.get(subOrder.getOrderStatus())) continue;
                // 作废的子订单信息 按照普通子订单流程作废，直接调用作废子订单服务
                OmsSubOrderCancelInputData subOrderCancelInputData = new OmsSubOrderCancelInputData();
                subOrderCancelInputData.setOrderId(subOrder.getOrderId());
                subOrderCancelInputData.setUserId(userId);
                subOrderCancelInputData.setCaller(caller);
                subOrderCancelInputData.setCancelReason(cancelReason);
                subOrderCancelInputData.setCancelReasonOther(cancelReasonOther);
                
                HashMap<String, String> params = (HashMap<String, String>)AuthorizeClient.getVerifyInfo(this.httpRequest);
                Map<String, String> omsCat = (Map<String, String>)params.clone();
                omsCat.put("method", "subOrderCancel");
                String json = null;
                BaseOutputBean baseBean = new BaseOutputBean();
                try {
                    json = subOrderCancelInputData.toJson();
                    omsCat.put("req_str", json);
                    baseBean = TootooService.callServer("oms", omsCat, "post", new OmsSubOrderCancelOutputData());
                    if (BaseResultEnum.SUCCESS.getResultID() == baseBean.getOutputHead().getResultID().intValue()) {
                        LogUtils4Oms.info(logger, uuid, "第三方父订单作废子订单成功！", "order", subOrder);
                    } else {
                        LogUtils4Oms.info(logger, uuid, "三方父订单作废子订单失败！", "subOrder", subOrder);
                        throw new OmsDelegateException(OmsThirdOrderCancelResultEnum.CANCEL_FAIL.getResultID(), "三方订单作废调用退子订单接口返回失败结果,"
                                        + "，"
                                        + baseBean.getOutputHead().getResultMessage(), null);
                    }
                } catch (OmsDelegateException e1) {
                    sendEmail(parentOrder, subOrder);
                    LogUtils4Oms.info(logger, json, "三方订单作废调用退子订单接口返回失败结果！", "json", json, e1);
                    throw e1;
                } catch (Exception e) {
                    sendEmail(parentOrder, subOrder);
                    LogUtils4Oms.info(logger, uuid, "三方父订单调用作废子订单接口失败！", "json", json);
                    throw e;
                }
            }
        } else {
            // 取消父订单
            OmsOrderCancelInputData orderCancelInputData = new OmsOrderCancelInputData();
            orderCancelInputData.setOrderId(orderId);
            orderCancelInputData.setUserId(userId);
            orderCancelInputData.setCaller(caller);
            orderCancelInputData.setCancelReason(cancelReason);
            orderCancelInputData.setCancelReasonOther(cancelReasonOther);
            HashMap<String, String> params = (HashMap<String, String>)AuthorizeClient.getVerifyInfo(this.httpRequest);
            Map<String, String> omsCat = (Map<String, String>)params.clone();
            omsCat.put("method", "orderCancel");
            String json = null;
            BaseOutputBean baseBean = new BaseOutputBean();
            try {
                json = orderCancelInputData.toJson();
                omsCat.put("req_str", json);
                baseBean = TootooService.callServer("oms", omsCat, "post", new OmsSubOrderCancelOutputData());
                if (BaseResultEnum.SUCCESS.getResultID() == baseBean.getOutputHead().getResultID().intValue()) {
                    LogUtils4Oms.info(logger, uuid, "三方父订单作废成功！", "parentOrder", parentOrder);
                } else {
                    LogUtils4Oms.info(logger, uuid, "三方父订单作废失败！", "parentOrder", parentOrder);
                    throw new OmsDelegateException(OmsThirdOrderCancelResultEnum.CANCEL_FAIL.getResultID(), "三方订单作废调用退父订单接口返回失败结果"
                                    + "，"
                                    + baseBean.getOutputHead().getResultMessage(), null);
                }
            } catch (OmsDelegateException e1) {
                LogUtils4Oms.info(logger, json, "三方订单作废调用退父订单接口返回失败结果！", "json", json, e1);
                throw e1;
            } catch (Exception e) {
                LogUtils4Oms.info(logger, uuid, "三方父订单作废调用作废父订单接口失败！", "json", json);
                throw e;
            }
        }
    }
    
    // 判断是否是第三方订单
    private boolean isOtherOrder(TOsParentOrderPO parentOrder) {
        switch (OrderFromEnum.get(parentOrder.getOrderFrom())) {
            case YIHAODIAN:
            case YIQIFA:
            case MANZUO:
            case JINGDONG:
            case WEIYI:
            case TMALL:
            case TAOBAO:
            case DAIKE_ORDER:
                return true;
            default:
                return false;
        }
    }
    
    // 发送邮件
    private void sendEmail(TOsParentOrderPO parentOrder, OsOrderPO order) {
        // 主题
        String subject = "";
        StringBuffer content = new StringBuffer();
        content.append("作废失败子订单如下\r\n\n");
        if (OrderFromEnum.get(parentOrder.getOrderFrom()) == OrderFromEnum.JINGDONG) {
            subject = "第三方京东的网站子订单作废失败";
            content.append("京东订单号          ");
        } else if (OrderFromEnum.get(parentOrder.getOrderFrom()) == OrderFromEnum.TMALL) {
            subject = "第三方天猫的网站子订单作废失败";
            content.append("天猫订单号     ");
        } else if (OrderFromEnum.get(parentOrder.getOrderFrom()) == OrderFromEnum.YIHAODIAN) {
            subject = "第三方一号店的网站子订单作废失败";
            content.append("一号店订单号     ");
        }
        content.append("网站子订单号    ");
        content.append("网站子订单状态    ");
        content.append("收货人姓名  ");
        content.append("收货地址                                                                   ");
        content.append("手机\r\n");
        // 三方订单的订单号
        List<Object[]> condition = new ArrayList<Object[]>();
        condition.add(new Object[]{"ORDER_ID", "=", parentOrder.getOrderId()});
        List<TEipSubOrderPO> list = tEipSubOrderDao.findTEipSubOrderPOListByCondition(condition);
        if (list.size() > 0) {
            TEipSubOrderPO tEipSubOrderPO = list.get(0);
            content.append(tEipSubOrderPO.getEipOrderId()).append("     ");
        } else {
            LogUtils4Oms.info(logger, uuid, "三方父订单作废发送邮件失败！", "TEipSubOrderPO", list);
        }
        content.append(order.getOrderCode() + "    ");
        content.append(OrderStatusEnum.get(order.getOrderStatus()).getS()).append("           ");
        content.append(order.getReceiver()).append("        ");
        content.append(order.getShipAddr()).append("     ");
        content.append(order.getMobile() + "\r\n");
        
        content.append("\r\n\n请终止对这些作废失败子订单的操作：配货/拣货、配送。如订单已出库，请配送部门给与拦截。谢谢！！");
        condition.clear();
        condition.add(new Object[]{"TYPE", "=", parentOrder.getOrderFrom()});
        List<TtsMailPO> listMailArr = ttsMailDao.findTtsMailPOListByCondition(condition);
        if (listMailArr.size() > 0) {
            MailUtil.sendEmail(logger, uuid, listMailArr.get(0).getToEmail().split("\\,"), subject, content.toString());
        }
    }
    
    /**
     * 关闭方法。
     * 该方法中通常实现如下功能：解锁、提交或回滚事务等等。
     * 
     * @param inputHead 该接口的请求参数头。
     * @param inputData 该接口的请求参数数据体。
     * @param outputHead 该接口的返回结果头。
     * @param outputData 该接口的返回结果数据体。
     */
    @Override
    public void doClose(final BaseInputHead inputHead, final OmsThirdOrderCancelInputData inputData, final BaseOutputHead outputHead, final OmsThirdOrderCancelOutputData outputData) throws OmsDelegateException {
        
    }
}
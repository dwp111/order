package cn.tootoo.soa.oms.getorderopt.delegate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.tootoo.db.egrocery.osorder.OsOrderDao;
import cn.tootoo.db.egrocery.osorder.OsOrderPO;
import cn.tootoo.db.egrocery.osorderopt.OsOrderOptPO;
import cn.tootoo.db.egrocery.tosparentorderopt.TOsParentOrderOptPO;
import cn.tootoo.enums.BaseResultEnum;
import cn.tootoo.global.Global;
import cn.tootoo.http.AuthorizeClient;
import cn.tootoo.http.bean.BaseInputHead;
import cn.tootoo.http.bean.BaseOutputHead;
import cn.tootoo.soa.base.enums.BaseOrderResultEnum;
import cn.tootoo.soa.base.enums.DeliveryTypeEnum;
import cn.tootoo.soa.base.enums.OrderStatusEnum;
import cn.tootoo.soa.base.enums.ShippingCompanyEnum;
import cn.tootoo.soa.oms.exceptions.OmsDelegateException;
import cn.tootoo.soa.oms.getorderopt.input.OmsGetOrderOptInputData;
import cn.tootoo.soa.oms.getorderopt.output.OmsGetOrderOptOrderOptListElementO;
import cn.tootoo.soa.oms.getorderopt.output.OmsGetOrderOptOutputData;
import cn.tootoo.soa.oms.getorderopt.output.OmsGetOrderOptResultEnum;
import cn.tootoo.soa.oms.utils.LogUtils4Oms;
import cn.tootoo.utils.DateUtil;
import cn.tootoo.utils.StringUtil;

/**
 * 服务委托者实现类。
 * 
 * 服务description：订单服务。
 * 服务remark：订单服务。
 * 接口description：返回订单流水
 * 接口remark：第三方配送的流水，除了首尾两条流水固定显示，中间还需插入第三方网关接口提供的配送进度数据
 */
public final class GetOrderOptDelegate extends AbstractGetOrderOptDelegate implements Cloneable {
    
    private String scope = "";
    static {
        GetOrderOptDelegateFactory.registPrototype(new GetOrderOptDelegate());
    }
    
    /**
     * 克隆方法。
     */
    @Override
    public AbstractGetOrderOptDelegate clone() throws CloneNotSupportedException {
        return new GetOrderOptDelegate();
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
    public void doInit(final BaseInputHead inputHead, final OmsGetOrderOptInputData inputData, final BaseOutputHead outputHead, final OmsGetOrderOptOutputData outputData) throws OmsDelegateException {
        
    }
    
    /**
     * 初始检查方法。
     * 该方法在实际执行服务功能之前进行检查，默认返回true。
     * 
     * @param inputHead 该接口的请求参数头。
     * @param inputData 该接口的请求参数数据体。
     * @param outputHead 该接口的返回结果头。
     * @param outputData 该接口的返回结果数据体。
     * 
     * @return boolean 检查是否通过。
     */
    @Override
    public boolean doCheck(final BaseInputHead inputHead, final OmsGetOrderOptInputData inputData, final BaseOutputHead outputHead, final OmsGetOrderOptOutputData outputData) {
        try {
            return true;
        } catch (Exception e) {
            LogUtils4Oms.error(logger, uuid, "初始检查方法出现异常，返回false！", e);
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
    public void doExecute(final BaseInputHead inputHead, final OmsGetOrderOptInputData inputData, final BaseOutputHead outputHead, final OmsGetOrderOptOutputData outputData) throws OmsDelegateException {
        // 登录验证信息
        String userID = "";
        scope = inputData.getScope();
        try {
            LogUtils4Oms.info(logger, uuid, "获取订单流水服务,进行验证！");
            this.httpRequest.setAttribute(AuthorizeClient.ATTRIB_NEED_AUTH, AuthorizeClient.NEED_AUTH_YES);
            this.httpRequest.setAttribute(AuthorizeClient.ATTRIB_CHECK_LEVEL, AuthorizeClient.AUTH_GENERAL_LEVEL);
            Map<String, String> tempMap = AuthorizeClient.getVerifyInfo(this.httpRequest);
            tempMap.put(AuthorizeClient.COOKIE_SCOPE, inputData.getScope());
            LogUtils4Oms.info(logger, uuid, "验证前，传入的Map信息", "tempMap", tempMap.toString());
            if (AuthorizeClient.CHECK_OK != AuthorizeClient.verifySession(tempMap)) {
                LogUtils4Oms.info(logger, uuid, "验证失败", "tempMap", tempMap.toString());
                Global.getOutputHead(BaseOrderResultEnum.NOT_LOGIN.getResultID(), scope, outputHead);
                return;
            }
            userID = tempMap.get(AuthorizeClient.COOKIE_BUYER_ID).toString();
            if (null == userID || "".equals(userID)) {
                LogUtils4Oms.info(logger, uuid, "从cookie中获取用户信息失败！");
                Global.getOutputHead(BaseOrderResultEnum.NOT_LOGIN.getResultID(), scope, outputHead);
                return;
            }
            LogUtils4Oms.info(logger, uuid, "验证成功！", "userID", userID);
        } catch (Exception e) {
            LogUtils4Oms.error(logger, uuid, "验证失败！");
            Global.getOutputHead(BaseOrderResultEnum.NOT_LOGIN.getResultID(), scope, outputHead);
            return;
        }
        
            
        LogUtils4Oms.info(logger, uuid, "获取订单流水服务开始", "inputData", inputData);
        String orderGeneration = inputData.getOrderGeneration();
        String orderId = inputData.getOrderId();
        
        if(StringUtil.isEmpty(orderId)){
            LogUtils4Oms.info(logger, uuid, "参数orderId非法", "orderId", orderId);
            Global.getOutputHead(BaseResultEnum.REQUEST_PARAM_ERROR.getResultID(), scope, outputHead);
            return;
        }
        
        if ("1".equals(orderGeneration)) {
            // 父订单
            String sqlCondition = "ORDER_ID = " + orderId +" AND IS_SHOW = '1' ORDER BY OPT_ID";
            List<TOsParentOrderOptPO> parentOrderOptList = tOsParentOrderOptDao.findTOsParentOrderOptPOListBySql(sqlCondition);
            
            // 去除相邻重复项
            List<TOsParentOrderOptPO> newParentOrderOptList = new ArrayList<TOsParentOrderOptPO>();
            for (int i = 0; i < parentOrderOptList.size(); i++) {
                TOsParentOrderOptPO parentOrderOpt = parentOrderOptList.get(i);
                if (i == (parentOrderOptList.size() - 1)) {
                    newParentOrderOptList.add(parentOrderOpt);
                    break;
                }
                TOsParentOrderOptPO nextParentOrderOpt = parentOrderOptList.get(i + 1);
                if (parentOrderOpt.getRemark() != null && !parentOrderOpt.getRemark().equals(nextParentOrderOpt.getRemark())) {
                    newParentOrderOptList.add(parentOrderOpt);
                }
            }
            
            List<OmsGetOrderOptOrderOptListElementO> outOrderOptList = new ArrayList<OmsGetOrderOptOrderOptListElementO>();
            for (TOsParentOrderOptPO tOsParentOrderOptPO : newParentOrderOptList) {
                OmsGetOrderOptOrderOptListElementO outOrderOpt = new OmsGetOrderOptOrderOptListElementO();
                outOrderOpt.setOptDt(DateUtil.dateTimeToStr(tOsParentOrderOptPO.getOptDt()));
                if(!"21101".equals(scope)){
                    outOrderOpt.setOptRemark(tOsParentOrderOptPO.getRemark());
                }else{
                    outOrderOpt.setOptRemark(tOsParentOrderOptPO.getRemarkEn());
                }
                outOrderOptList.add(outOrderOpt);
            }
            
            outputData.setOrderOptList(outOrderOptList);
            Global.getOutputHead(BaseResultEnum.SUCCESS.getResultID(), scope, outputHead);
            
        } else if ("2".equals(orderGeneration)) {
            // 子订单
            String sqlCondition = "ORDER_ID = " + orderId + " AND IS_SHOW = '1' ORDER BY OPT_ID";
            List<OsOrderOptPO> osOrderOptList = osOrderOptDao.findOsOrderOptPOListBySql(sqlCondition);
            
            // 去除相邻重复项
            List<OsOrderOptPO> newOrderOptList = new ArrayList<OsOrderOptPO>();
            for (int i = 0; i < osOrderOptList.size(); i++) {
                OsOrderOptPO osOrderOptPO = osOrderOptList.get(i);
                if (i == (osOrderOptList.size() - 1)) {
                    newOrderOptList.add(osOrderOptPO);
                    break;
                }
                OsOrderOptPO nextOrderOptPO = osOrderOptList.get(i + 1);
                if (osOrderOptPO.getRemark() != null && !osOrderOptPO.getRemark().equals(nextOrderOptPO.getRemark())) {
                    newOrderOptList.add(osOrderOptPO);
                }
            }
            
            
            // 得到最后一条配送公司的流水ID
            Long lastOptId = 0L;
            if(!scope.startsWith("112") ){
                for (OsOrderOptPO osOrderOptPO : newOrderOptList) {
                    if (OrderStatusEnum.THIRD_SEND.getC().equals(osOrderOptPO.getOrderStatus())){
                        lastOptId = osOrderOptPO.getOptId();
                    }
                }
            }
            List<OmsGetOrderOptOrderOptListElementO> outOrderOptList = new ArrayList<OmsGetOrderOptOrderOptListElementO>();
            for (OsOrderOptPO osOrderOptPO : newOrderOptList) {
                OmsGetOrderOptOrderOptListElementO outOrderOpt = new OmsGetOrderOptOrderOptListElementO();
                outOrderOpt.setOptDt(DateUtil.dateTimeToStr(osOrderOptPO.getOptDt()));
                
                String remark = "";
                if (!"21101".equals(scope)) {
                    remark = osOrderOptPO.getRemark();
                    
                    if (lastOptId.equals(osOrderOptPO.getOptId())){
                        OsOrderPO osOrder = osOrderDao.findOsOrderPOByID(Long.parseLong(orderId));
                        Long dcId = osOrder.getDcId();
                        
                        ShippingCompanyEnum shippingCompanyEnum = ShippingCompanyEnum.get(dcId.intValue());
                        if (shippingCompanyEnum != null && !StringUtil.isEmpty(shippingCompanyEnum.getH())) {
                            remark += "。若未返回跟踪信息，请到<a href=\"" + shippingCompanyEnum.getH() + "\" target=\"_blank\">&nbsp;" + shippingCompanyEnum.getS()
                                            + "&nbsp;</a>官网查询";
                        }
                    }
                } else {
                    remark = osOrderOptPO.getRemarkEn();
                    
                    if (lastOptId.equals(osOrderOptPO.getOptId())) {
                        OsOrderPO osOrder = osOrderDao.findOsOrderPOByID(Long.parseLong(orderId));
                        Long dcId = osOrder.getDcId();
                        
                        ShippingCompanyEnum shippingCompanyEnum = ShippingCompanyEnum.get(dcId.intValue());
                        if (shippingCompanyEnum != null && !StringUtil.isEmpty(shippingCompanyEnum.getG())
                                        && !StringUtil.isEmpty(shippingCompanyEnum.getH())) {
                            remark += ". If you do not get the feedback of express tracking information, Please go to<a href=\"" + shippingCompanyEnum.getH() + "\" target=\"_blank\">&nbsp;"
                                            + shippingCompanyEnum.getG() + "&nbsp;</a>Website search";
                        }
                    }
                }
                outOrderOpt.setOptRemark(remark);
                outOrderOptList.add(outOrderOpt);
            }
            
            outputData.setOrderOptList(outOrderOptList);
            Global.getOutputHead(BaseResultEnum.SUCCESS.getResultID(), scope, outputHead);
            
        } else {
            LogUtils4Oms.info(logger, uuid, "参数orderGeneration非法", "orderGeneration", orderGeneration);
            Global.getOutputHead(BaseResultEnum.REQUEST_PARAM_ERROR.getResultID(), scope, outputHead);
            return;
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
    public void doClose(final BaseInputHead inputHead, final OmsGetOrderOptInputData inputData, final BaseOutputHead outputHead, final OmsGetOrderOptOutputData outputData) throws OmsDelegateException {
        
    }
}
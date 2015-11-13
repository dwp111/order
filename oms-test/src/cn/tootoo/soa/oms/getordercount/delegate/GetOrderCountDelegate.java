package cn.tootoo.soa.oms.getordercount.delegate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.tootoo.db.egrocery.tosparentorder.TOsParentOrderPO;
import cn.tootoo.enums.BaseResultEnum;
import cn.tootoo.global.Global;
import cn.tootoo.http.AuthorizeClient;
import cn.tootoo.http.bean.BaseInputHead;
import cn.tootoo.http.bean.BaseOutputHead;
import cn.tootoo.soa.base.enums.BaseOrderResultEnum;
import cn.tootoo.soa.base.enums.OrderStatusEnum;
import cn.tootoo.soa.base.enums.OrderTypeEnum;
import cn.tootoo.soa.base.enums.ParentOrderStatusEnum;
import cn.tootoo.soa.base.enums.PayTypeEnum;
import cn.tootoo.soa.oms.exceptions.OmsDelegateException;
import cn.tootoo.soa.oms.getordercount.input.OmsGetOrderCountInputData;
import cn.tootoo.soa.oms.getordercount.output.OmsGetOrderCountOutputData;
import cn.tootoo.soa.oms.utils.GetOrderUtil;
import cn.tootoo.soa.oms.utils.LogUtils4Oms;

/**
 * 服务委托者实现类。
 * 
 * 服务description：订单服务。
 * 服务remark：订单服务。
 * 接口description：返回订单计数
 * 接口remark：
 */
public final class GetOrderCountDelegate extends AbstractGetOrderCountDelegate implements Cloneable {
    
    private String scope = "";
    static {
        GetOrderCountDelegateFactory.registPrototype(new GetOrderCountDelegate());
    }
    
    /**
     * 克隆方法。
     */
    @Override
    public AbstractGetOrderCountDelegate clone() throws CloneNotSupportedException {
        return new GetOrderCountDelegate();
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
    public void doInit(final BaseInputHead inputHead, final OmsGetOrderCountInputData inputData, final BaseOutputHead outputHead, final OmsGetOrderCountOutputData outputData) throws OmsDelegateException {
        
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
    public boolean doCheck(final BaseInputHead inputHead, final OmsGetOrderCountInputData inputData, final BaseOutputHead outputHead, final OmsGetOrderCountOutputData outputData) {
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
    /**
     * Description:<br>
     * 
     * @param inputHead
     * @param inputData
     * @param outputHead
     * @param outputData
     * @throws OmsDelegateException
     * @see cn.tootoo.soa.oms.getordercount.delegate.AbstractGetOrderCountDelegate#doExecute(cn.tootoo.http.bean.BaseInputHead, cn.tootoo.soa.oms.getordercount.input.OmsGetOrderCountInputData, cn.tootoo.http.bean.BaseOutputHead, cn.tootoo.soa.oms.getordercount.output.OmsGetOrderCountOutputData)
     */
    @Override
    public void doExecute(final BaseInputHead inputHead, final OmsGetOrderCountInputData inputData, final BaseOutputHead outputHead, final OmsGetOrderCountOutputData outputData) throws OmsDelegateException {
        // 登录验证信息
        String userID = "";
        scope = inputData.getScope();
        try {
            LogUtils4Oms.info(logger, uuid, "获取订单计数服务,进行验证！");
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

        
        LogUtils4Oms.info(logger, uuid, "获取订单计数服务开始", "inputData", inputData);
        
        // 待支付
        List<Object[]> waitPayConditions = new ArrayList<Object[]>();
        waitPayConditions.add(new Object[]{"BUYER_ID", "=", userID});
        
        if (!"11101".equals(scope) && !"21101".equals(scope)) { // 在线售卡只PC端显示
            waitPayConditions.add(new Object[]{"ORDER_TYPE", "<>", OrderTypeEnum.ONLINE_CARD_VIRTUAL.getC()});
        }
        
        if ("21101".equals(scope)) { // 英文站订单
            waitPayConditions.add(new Object[]{"IS_EN_ORDER", "=", "1"});
        }else{
            waitPayConditions.add(new Object[]{"IS_EN_ORDER", "<>", "1"});
        }
        waitPayConditions.add(new Object[]{"PAY_TYPE IN ('1', '3')", "", ""});
        String sqlStatus = GetOrderUtil.list2Sql(ParentOrderStatusEnum.getFrontWaitPay(PayTypeEnum.ONLINE.getC())); 
        waitPayConditions.add(new Object[]{"ORDER_STATUS IN" + sqlStatus, "", ""});
        int waitPayCount = 0;
        List<TOsParentOrderPO> waitPayList = tOsParentOrderDao.findTOsParentOrderPOListByCondition(waitPayConditions);
        if (waitPayList != null) {
            waitPayCount = waitPayList.size();
        }
        
        // 待收货
        List<Object[]> waitReceiveConditions = new ArrayList<Object[]>();
        waitReceiveConditions.add(new Object[]{"BUYER_ID", "=", userID});
        
        if (!"11101".equals(scope) && !"21101".equals(scope)) { // 在线售卡只PC端显示
            waitReceiveConditions.add(new Object[]{"ORDER_TYPE", "<>", OrderTypeEnum.ONLINE_CARD_VIRTUAL.getC()});
        }
        
        if ("21101".equals(scope)) { // 英文站订单
            waitReceiveConditions.add(new Object[]{"IS_EN_ORDER", "=", "1"});
        }else{
            waitReceiveConditions.add(new Object[]{"IS_EN_ORDER", "<>", "1"});
        }
        String parentSqlStatus = GetOrderUtil.list2Sql(ParentOrderStatusEnum.getFrontWaitReceive());
        String subSqlStatus = GetOrderUtil.list2Sql(OrderStatusEnum.getFrontWaitReceive());
        waitReceiveConditions.add(new Object[]{"(    ((ORDER_STATUS<110 or ORDER_STATUS=120) and ORDER_STATUS IN" + parentSqlStatus + ")  or  ((ORDER_STATUS>=110 and ORDER_STATUS!=120) and EXISTS (SELECT 1 FROM OS_ORDER A WHERE A.PARENT_ID = T_OS_PARENT_ORDER.ORDER_ID AND A.ORDER_STATUS IN " + subSqlStatus + "))    )", "", ""});
        int waitReceiveCount = 0;
        List<TOsParentOrderPO> waitReceiveList = tOsParentOrderDao.findTOsParentOrderPOListByCondition(waitReceiveConditions);
        if (waitReceiveList != null) {
            waitReceiveCount = waitReceiveList.size();
        }
        
        outputData.setWaitPayCount(waitPayCount);
        outputData.setWaitReceiveCount(waitReceiveCount);
        Global.getOutputHead(BaseResultEnum.SUCCESS.getResultID(), scope, outputHead);
    
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
    public void doClose(final BaseInputHead inputHead, final OmsGetOrderCountInputData inputData, final BaseOutputHead outputHead, final OmsGetOrderCountOutputData outputData) throws OmsDelegateException {
        
    }
}
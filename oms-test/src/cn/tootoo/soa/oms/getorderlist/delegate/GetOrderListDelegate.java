package cn.tootoo.soa.oms.getorderlist.delegate;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.tootoo.db.egrocery.osorder.OsOrderPO;
import cn.tootoo.db.egrocery.osorderitems.OsOrderItemsPO;
import cn.tootoo.db.egrocery.ospayplan.OsPayPlanPO;
import cn.tootoo.db.egrocery.tosparentorder.TOsParentOrderPO;
import cn.tootoo.db.egrocery.tosparentorderitems.TOsParentOrderItemsPO;
import cn.tootoo.db.egrocery.tsysonlinecardgoods.TSysOnlineCardGoodsPO;
import cn.tootoo.enums.BaseResultEnum;
import cn.tootoo.global.Global;
import cn.tootoo.http.AuthorizeClient;
import cn.tootoo.http.bean.BaseInputHead;
import cn.tootoo.http.bean.BaseOutputHead;
import cn.tootoo.soa.base.enums.BaseOrderResultEnum;
import cn.tootoo.soa.base.enums.OrderStatusEnum;
import cn.tootoo.soa.base.enums.OrderTypeEnum;
import cn.tootoo.soa.base.enums.ParentOrderStatusEnum;
import cn.tootoo.soa.base.enums.PayMethodEnum;
import cn.tootoo.soa.base.enums.PayStatusEnum;
import cn.tootoo.soa.base.enums.PayTypeEnum;
import cn.tootoo.soa.base.enums.SubstationEnum;
import cn.tootoo.soa.base.global.DictionaryData;
import cn.tootoo.soa.base.util.OrderUtil;
import cn.tootoo.soa.oms.exceptions.OmsDelegateException;
import cn.tootoo.soa.oms.getordercommon.GetOrderCommonService;
import cn.tootoo.soa.oms.getorderlist.input.OmsGetOrderListInputData;
import cn.tootoo.soa.oms.getorderlist.output.OmsGetOrderListChildItemListElementO;
import cn.tootoo.soa.oms.getorderlist.output.OmsGetOrderListChildListElementO;
import cn.tootoo.soa.oms.getorderlist.output.OmsGetOrderListGiftBoxItemListElementO;
import cn.tootoo.soa.oms.getorderlist.output.OmsGetOrderListOrderItemListElementO;
import cn.tootoo.soa.oms.getorderlist.output.OmsGetOrderListOrderListElementO;
import cn.tootoo.soa.oms.getorderlist.output.OmsGetOrderListOutputData;
import cn.tootoo.soa.oms.utils.GetOrderUtil;
import cn.tootoo.soa.oms.utils.LogUtils4Oms;
import cn.tootoo.utils.DateUtil;
import cn.tootoo.utils.Log;
import cn.tootoo.utils.StringUtil;

/**
 * 服务委托者实现类。
 * 
 * 服务description：订单服务。
 * 服务remark：订单服务。
 * 接口description：返回订单列表
 * 接口remark：以下几点在此统一说明，不一一标注了 ：   一、如果字段没有取值，则取值null，但保留key  二、布尔值字段格式为：1.是 0.否。
 */
public final class GetOrderListDelegate extends AbstractGetOrderListDelegate implements Cloneable {
    
    private String scope = "";
    private Set<Long> goodsIdSet = new HashSet<Long>();
    static {
        GetOrderListDelegateFactory.registPrototype(new GetOrderListDelegate());
    }
    
    /**
     * 克隆方法。
     */
    @Override
    public AbstractGetOrderListDelegate clone() throws CloneNotSupportedException {
        return new GetOrderListDelegate();
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
    public void doInit(final BaseInputHead inputHead, final OmsGetOrderListInputData inputData, final BaseOutputHead outputHead, final OmsGetOrderListOutputData outputData) throws OmsDelegateException {
        
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
    public boolean doCheck(final BaseInputHead inputHead, final OmsGetOrderListInputData inputData, final BaseOutputHead outputHead, final OmsGetOrderListOutputData outputData) {
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
    public void doExecute(final BaseInputHead inputHead, final OmsGetOrderListInputData inputData, final BaseOutputHead outputHead, final OmsGetOrderListOutputData outputData) throws OmsDelegateException {
        // 登录验证信息
        String userID = "";
        scope = inputData.getScope();
        try {
            LogUtils4Oms.info(logger, uuid, "获取订单列表服务,进行验证！");
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
        
        
        LogUtils4Oms.info(logger, uuid, "获取订单列表服务开始", "inputData", inputData);
        String orderStatus = inputData.getOrderStatus();
        int isNeedItems = inputData.getIsNeedItems();
        int pageNo = inputData.getPageNo();
        int pageSize = inputData.getPageSize();
        
        // 分页限制最大值，如果每页数量超过20，自动设置为20
        pageSize = pageSize > 20?20:pageSize;
        
        List<Object[]> parentOrderConditions = this.getParentOrderConditions(inputData, userID);
        
        // 分页查询数据
        String sortCondition = "ORDER BY CREATE_DT DESC";
        Map<String, Object> parentOrderMap = tOsParentOrderDao.findPageByCondition(parentOrderConditions, pageNo, pageSize, sortCondition);
        List<TOsParentOrderPO> parentOrderList = null;
        int count = 0; // 计数,分页用的订单总数
        int totalPage = 0;
        if (parentOrderMap != null) {
            parentOrderList = (List<TOsParentOrderPO>)parentOrderMap.get("list");
            count = (Integer)parentOrderMap.get("rows"); // 所有记录条数
            totalPage = (Integer)parentOrderMap.get("total"); // 共几页
        }
        
        if (parentOrderList == null || parentOrderList.size() <= 0) {
            // 没有查询到订单数据
            outputData.setCount(0);
            outputData.setTotalPage(0);
            outputData.setOrderList(new ArrayList<OmsGetOrderListOrderListElementO>());
            LogUtils4Oms.info(logger, uuid, "查询parentOrderList为空", "parentOrderList", parentOrderList);
            Global.getOutputHead(BaseResultEnum.SUCCESS.getResultID(), scope, outputHead);
            return;
        }
        
        List<OmsGetOrderListOrderListElementO> outOrderList = new ArrayList<OmsGetOrderListOrderListElementO>();
        for (TOsParentOrderPO tOsParentOrderPO : parentOrderList) {
            List<OmsGetOrderListOrderItemListElementO> outItemList = new ArrayList<OmsGetOrderListOrderItemListElementO>();
            String hasChild = null;
            List<OmsGetOrderListChildListElementO> outChildList = new ArrayList<OmsGetOrderListChildListElementO>();
            
            // 查询子订单
            List<Object[]> orderConditions = this.getSubOrderConditions(orderStatus, tOsParentOrderPO.getOrderId());
            List<OsOrderPO> orderList = osOrderDao.findOsOrderPOListByCondition(orderConditions);
            
            
            // 开始查询订单明细
            boolean isPurchase = false;
            if ((orderList == null || orderList.size() <= 0) && isNeedItems == 1) {
                // 如果子订单未拆分，则查询父订单明细
                hasChild = "0"; // 无子订单
                List<Object[]> parentOrderItemsConditions = new ArrayList<Object[]>();
                parentOrderItemsConditions.add(new Object[]{"ORDER_ID", "=", tOsParentOrderPO.getOrderId()});
                List<TOsParentOrderItemsPO> parentOrderItemsList = tOsParentOrderItemsDao.findTOsParentOrderItemsPOListByCondition(parentOrderItemsConditions);
                
                // 礼盒明细Map[Key:itemId,Value:礼盒明细列表]
                Map<Long, List<OmsGetOrderListGiftBoxItemListElementO>> outGiftBoxItemMap = new HashMap<Long, List<OmsGetOrderListGiftBoxItemListElementO>>();
                
                for (TOsParentOrderItemsPO tOsParentOrderItemsPO : parentOrderItemsList) {
                    OmsGetOrderListOrderItemListElementO outItem = this.setOrderItemElementO(tOsParentOrderItemsPO, tOsParentOrderPO, outGiftBoxItemMap);
                    if(outItem != null){
                        outItemList.add(outItem);
                    }
                    if (tOsParentOrderItemsPO.getPurchaseDate() != null) {
                        isPurchase = true; // 预定商品
                    }
                }
                
                // 遍历outItemList,为其set礼盒明细列表
                for (OmsGetOrderListOrderItemListElementO outItem : outItemList) {
                    List<OmsGetOrderListGiftBoxItemListElementO> outGiftBoxItemList = outGiftBoxItemMap.get(outItem.getItemID());
                    if (outGiftBoxItemList != null && outGiftBoxItemList.size() > 0) {
                        outItem.setGiftBoxItemList(outGiftBoxItemList);
                    }
                }
                
            } else {
                // 已拆分，则需子订单和子订单明细
                hasChild = "1"; // 有子订单
                for (OsOrderPO osOrderPO : orderList) {
                    List<OmsGetOrderListChildItemListElementO> outChildItemList = new ArrayList<OmsGetOrderListChildItemListElementO>();
                    
                    if (isNeedItems == 1) {
                        List<Object[]> orderItemsConditions = new ArrayList<Object[]>();
                        orderItemsConditions.add(new Object[]{"ORDER_ID", "=", osOrderPO.getOrderId()});
                        List<OsOrderItemsPO> orderItemsList = osOrderItemsDao.findOsOrderItemsPOListByCondition(orderItemsConditions);
                        
                        // 礼盒明细Map[Key:itemId,Value:礼盒明细列表]
                        Map<Long, List<OmsGetOrderListGiftBoxItemListElementO>> outGiftBoxItemMap = new HashMap<Long, List<OmsGetOrderListGiftBoxItemListElementO>>();
                        
                        for (OsOrderItemsPO osOrderItemsPO : orderItemsList) {
                            OmsGetOrderListChildItemListElementO outChildItem = this.setChildItemElementO(osOrderItemsPO, osOrderPO, outGiftBoxItemMap);
                            if(outChildItem != null){
                                outChildItemList.add(outChildItem);
                            }
                        }
                        
                        // 遍历outChildItemList,为其set礼盒明细列表
                        for (OmsGetOrderListChildItemListElementO outChildItem : outChildItemList) {
                            List<OmsGetOrderListGiftBoxItemListElementO> outGiftBoxItemList = outGiftBoxItemMap.get(outChildItem.getItemID());
                            if (outGiftBoxItemList != null && outGiftBoxItemList.size() > 0) {
                                outChildItem.setGiftBoxItemList(outGiftBoxItemList);
                            }
                        }
                        
                    }
                    outChildList.add(this.setChildListElementO(osOrderPO, outChildItemList));
                }
            }
            outOrderList.add(this.setOrderListElementO(isPurchase, tOsParentOrderPO, outItemList, hasChild, outChildList));
            
        }
        
        // 调用商品接口，为outOrderList设置是否可售，是否可再次购买
        this.setGoodsCanSale(outOrderList);
        
        outputData.setCount(count);
        outputData.setTotalPage(totalPage);
        outputData.setOrderList(outOrderList);
        Global.getOutputHead(BaseResultEnum.SUCCESS.getResultID(), scope, outputHead);
        return;
            
    }
    
    
    /**
     * 获取父订单查询条件Conditions
     * Description:<br>
     * 
     * @param inputData
     * @param userID
     * @return
     * @throws OmsDelegateException
     * @throws Exception
     */
    private List<Object[]> getParentOrderConditions(OmsGetOrderListInputData inputData, String userID) throws OmsDelegateException{
        String orderStatus = inputData.getOrderStatus();
        String orderType = inputData.getOrderType();
        String startTime = inputData.getStartTime();
        String endTime = inputData.getEndTime();
        String keyword = inputData.getKeyword();
        String orderCode = inputData.getOrderCode();
        
        List<Object[]> parentOrderConditions = new ArrayList<Object[]>();
        parentOrderConditions.add(new Object[]{"BUYER_ID", "=", userID});
        
        if (!"11101".equals(scope) && !"21101".equals(scope)) { // 在线售卡只PC端显示
            parentOrderConditions.add(new Object[]{"ORDER_TYPE", "<>", OrderTypeEnum.ONLINE_CARD_VIRTUAL.getC()});
        }
        
        if ("21101".equals(scope)) { // 英文站订单
            parentOrderConditions.add(new Object[]{"IS_EN_ORDER", "=", "1"});
        }else{
            parentOrderConditions.add(new Object[]{"IS_EN_ORDER", "<>", "1"});
        }
        
        if ("1".equals(orderType)) {
            // 普通订单
            parentOrderConditions.add(new Object[]{"ORDER_TYPE NOT IN ('1','x')", "", ""});
        } else if ("2".equals(orderType)) {
            // 卡订单 ，1：实物卡订单，2：电子卡订单
            parentOrderConditions.add(new Object[]{"ORDER_TYPE IN ('1','x')", "", ""});
        }
        
        // 时间如果未传，默认半年(适应手机端)
        if (StringUtil.isEmpty(startTime) || StringUtil.isEmpty(endTime)) {
            parentOrderConditions.add(new Object[]{"CREATE_DT", ">=", new Timestamp(DateUtil.getDateBegin(DateUtil.getDateBeforeOrAfterMonthes(new Date(), -6)).getTime())});
            parentOrderConditions.add(new Object[]{"CREATE_DT", "<=", new Timestamp(DateUtil.getDateEnd(new Date()).getTime())});
        }else{
            parentOrderConditions.add(new Object[]{"CREATE_DT", ">=", new Timestamp(DateUtil.strToDate(startTime).getTime())});
            parentOrderConditions.add(new Object[]{"CREATE_DT", "<=", new Timestamp(DateUtil.strToDateTime(endTime + " 23:59:59").getTime())});
        }
        
        // 关键词
        if (!StringUtil.isEmpty(keyword)) {
            parentOrderConditions.add(new Object[]{"EXISTS (SELECT 1 FROM T_OS_PARENT_ORDER_ITEMS A WHERE A.ORDER_ID=T_OS_PARENT_ORDER.ORDER_ID AND A.GOODS_NAME LIKE '%" + keyword + "%')", "", ""});
        }
        
        // 订单号
        if (!StringUtil.isEmpty(orderCode)) {
            parentOrderConditions.add(new Object[]{"(ORDER_CODE = " + orderCode + " OR EXISTS( SELECT 1 FROM OS_ORDER A WHERE A.PARENT_ID=T_OS_PARENT_ORDER.ORDER_ID AND A.ORDER_CODE=" + orderCode + "))", "", ""});
        }
        
        if ("0".equals(orderStatus)) {
            // 全部状态
            parentOrderConditions.add(new Object[]{"ORDER_STATUS", "<>", ParentOrderStatusEnum.DAIXIADAN.getStatus()});
            
        } else if ("10".equals(orderStatus)) {
            // 待支付
            parentOrderConditions.add(new Object[]{"PAY_TYPE in ('1', '3')", "", ""});
            String sqlStatus = GetOrderUtil.list2Sql(ParentOrderStatusEnum.getFrontWaitPay(PayTypeEnum.ONLINE.getC())); 
            parentOrderConditions.add(new Object[]{"ORDER_STATUS IN" + sqlStatus, "", ""});
            
        } else if ("20".equals(orderStatus)) {
            // 待收货 （如果未拆单按父订单状态查询，如果已拆单按子订单状态查询）
            String parentSqlStatus = GetOrderUtil.list2Sql(ParentOrderStatusEnum.getFrontWaitReceive());
            String subSqlStatus = GetOrderUtil.list2Sql(OrderStatusEnum.getFrontWaitReceive());
            parentOrderConditions.add(new Object[]{"(    ((ORDER_STATUS<110 or ORDER_STATUS=120) and ORDER_STATUS IN" + parentSqlStatus + ")  or  ((ORDER_STATUS>=110 and ORDER_STATUS!=120) and EXISTS (SELECT 1 FROM OS_ORDER A WHERE A.PARENT_ID = T_OS_PARENT_ORDER.ORDER_ID AND A.ORDER_STATUS IN " + subSqlStatus + "))    )", "", ""});
            
        } else if ("30".equals(orderStatus)) {
            // 已完成
            String parentSqlStatus = GetOrderUtil.list2Sql(ParentOrderStatusEnum.getFrontFinish());
            String subSqlStatus = GetOrderUtil.list2Sql(OrderStatusEnum.getFrontFinish());
            parentOrderConditions.add(new Object[]{"(ORDER_STATUS IN " + parentSqlStatus + " OR EXISTS (SELECT 1 FROM OS_ORDER A WHERE A.PARENT_ID = T_OS_PARENT_ORDER.ORDER_ID AND A.ORDER_STATUS IN " + subSqlStatus + "))", "", ""});
            
        } else if ("40".equals(orderStatus)) {
            // 已取消
            String parentSqlStatus = GetOrderUtil.list2Sql(ParentOrderStatusEnum.getFrontCanceled());
            String subSqlStatus = OrderStatusEnum.getFrontCanceled();
            parentOrderConditions.add(new Object[]{"(ORDER_STATUS IN " + parentSqlStatus + " OR EXISTS (SELECT 1 FROM OS_ORDER A WHERE A.PARENT_ID = T_OS_PARENT_ORDER.ORDER_ID AND A.ORDER_STATUS = '"+subSqlStatus+"'))", "", ""});
            
        } else {
            LogUtils4Oms.info(logger, uuid, "参数orderStatus非法", "orderStatus", orderStatus);
            throw new OmsDelegateException(BaseResultEnum.REQUEST_PARAM_ERROR.getResultID(), "参数orderStatus非法", null);
        }
        
        return parentOrderConditions;
    }
    
    
    /**
     * 获取子订单查询条件Conditions
     * Description:<br>
     * 
     * @param orderStatus
     * @param parentId
     * @return
     */
    private List<Object[]> getSubOrderConditions(String orderStatus, Long parentId){
        List<Object[]> orderConditions = new ArrayList<Object[]>();
        orderConditions.add(new Object[]{"PARENT_ID", "=", parentId});
        orderConditions.add(new Object[]{"ORDER_STATUS", "<>", OrderStatusEnum.REPALCE_GOODS_CANCEL.getC()});
        if ("0".equals(orderStatus)) {
            // 全部状态
            // orderConditions.add(new Object[]{"ORDER_STATUS", "<>", OrderStatusEnum.PART_SIGNED.getC()});

        } else if ("20".equals(orderStatus)) {
            // 待收货
            String sqlStatus = GetOrderUtil.list2Sql(OrderStatusEnum.getFrontWaitReceive());
            orderConditions.add(new Object[]{"ORDER_STATUS IN " + sqlStatus, "", ""});
            
        } else if ("30".equals(orderStatus)) {
            // 已完成
            String sqlStatus = GetOrderUtil.list2Sql(OrderStatusEnum.getFrontFinish());
            orderConditions.add(new Object[]{"ORDER_STATUS IN " + sqlStatus, "", ""});
            
        } else if ("40".equals(orderStatus)) {
            // 已取消
            String sqlStatus = OrderStatusEnum.getFrontCanceled();
            orderConditions.add(new Object[]{"ORDER_STATUS", "=", sqlStatus});
        }
        
        return orderConditions;
    }
    
    
    
    /**
     * 拼子订单明细输出list
     * Description:<br>
     * 
     * @param osOrderItemsPO
     * @return
     */
    private OmsGetOrderListChildItemListElementO setChildItemElementO(OsOrderItemsPO osOrderItemsPO, OsOrderPO osOrderPO, Map<Long, List<OmsGetOrderListGiftBoxItemListElementO>> outGiftBoxItemMap) {
        OmsGetOrderListChildItemListElementO outChildItem = new OmsGetOrderListChildItemListElementO();
        outChildItem.setItemID(osOrderItemsPO.getItemId());
        Long goodsId = osOrderItemsPO.getGoodsId();
        outChildItem.setGoodsID(goodsId);
        
        // 如果订单类型是电子卡订单，则需返回卡类型
        if(OrderTypeEnum.ONLINE_CARD_VIRTUAL.getC().equals(osOrderPO.getOrderType())){
            List<Object[]> onlineCardConditions = new ArrayList<Object[]>();
            onlineCardConditions.add(new Object[]{"GOODS_ID", "=", goodsId});
            List<TSysOnlineCardGoodsPO> onlineCardList = tSysOnlineCardGoodsDao.findTSysOnlineCardGoodsPOListByCondition(onlineCardConditions);
            if (onlineCardList != null && onlineCardList.size() >= 1){
                TSysOnlineCardGoodsPO onlineCardGoods = onlineCardList.get(0);
                outChildItem.setCardType(onlineCardGoods.getCardType());
            }else{
                LogUtils4Oms.error(logger, uuid, "查询电子卡订单卡类型为空", "goodsId", goodsId, "onlineCardList", onlineCardList);
            }
        }
        
        outChildItem.setSkuID(osOrderItemsPO.getSku());
        outChildItem.setGoodsNumber(osOrderItemsPO.getGoodsNumber().longValue());
        
        // 是否可售暂不处理，待循环结束后统一处理
        // outChildItem.setCanSale(null);
        goodsIdSet.add(goodsId);
        
        if("21101".equals(scope)){
            // 英文站
            if(StringUtil.isEmpty(osOrderItemsPO.getGoodsNameEn())){
                outChildItem.setGoodsName(osOrderItemsPO.getGoodsName());
            }else{
                outChildItem.setGoodsName(osOrderItemsPO.getGoodsNameEn());
            }
            
            if(StringUtil.isEmpty(osOrderItemsPO.getGoodsUnitEn())){
                outChildItem.setGoodsUnit(osOrderItemsPO.getGoodsUnit());
            }else{
                outChildItem.setGoodsUnit(osOrderItemsPO.getGoodsUnitEn());
            }
            
            if(StringUtil.isEmpty(osOrderItemsPO.getUnitNameEn())){
                outChildItem.setUnitName(osOrderItemsPO.getUnitName());
            }else{
                outChildItem.setUnitName(osOrderItemsPO.getUnitNameEn());
            }
        }else{
            // 非英文站
            outChildItem.setGoodsName(osOrderItemsPO.getGoodsName());
            outChildItem.setGoodsUnit(osOrderItemsPO.getGoodsUnit());
            outChildItem.setUnitName(osOrderItemsPO.getUnitName());
        }
        outChildItem.setGoodsWeight(osOrderItemsPO.getGoodsWeight());
        
        BigDecimal goodsPrice = osOrderItemsPO.getGoodsPrice();
        outChildItem.setShopPrice(osOrderItemsPO.getShopPrice());
        outChildItem.setGoodsPrice(goodsPrice);
        outChildItem.setGoodsAmount(osOrderItemsPO.getGoodsAmount());
        
        String picPath = osOrderItemsPO.getPicPath();
        if(picPath != null){
            picPath = picPath.split(",")[0];
        }
        outChildItem.setGoodsPic(picPath);
        
        // 商品链接
        String webUrl = GetOrderUtil.getWebUrl(scope, goodsId, osOrderItemsPO.getPromoteId());
        outChildItem.setWebUrl(webUrl);
        
        // 是否显示价格
        String orderType = osOrderPO.getOrderType();
        String isShowPrice = "0";
        if(GetOrderUtil.getIsShowPrice(goodsPrice, orderType)){
            isShowPrice = "1";
        }
        outChildItem.setIsShowPrice(isShowPrice);
        
        // 是否提供链接
        String hasLink = "0";
        if(GetOrderUtil.getHasLink(goodsId, orderType)){
            hasLink = "1";
        }
        outChildItem.setHasLink(hasLink);
        
        // 子订单是否显示分享按钮
        String orderStatus = osOrderPO.getOrderStatus();
        Byte isGift = osOrderItemsPO.getIsGift();
        String isShowShare = "0";
        if(GetOrderUtil.getSubIsShowShare(scope, orderStatus, orderType, isGift)){
            isShowShare = "1";
        }
        outChildItem.setIsShowShare(isShowShare);
        
        // 是否显示评论按钮(父订单不显示)
        String isShowReview = "0";
        if (GetOrderUtil.getIsShowReview(osOrderPO.getOrderStatus(), osOrderPO.getReceiveDt(), isGift, osOrderItemsPO.getReviewId(), osOrderItemsPO.getIsGiftbox())) {
            isShowReview = "1";
        }
        outChildItem.setIsShowReview(isShowReview);
        
        // 是否赠品
        if (isGift == null || isGift != 1) {
            outChildItem.setIsGift("0");
        } else {
            outChildItem.setIsGift("1");
        }

        // 是否礼盒
        outChildItem.setIsGiftBox(osOrderItemsPO.getIsGiftbox());
        
        //是否是兑换商品  
        /* *****add by zhaochunn 2015-6-30 start***** */
        if(StringUtil.isEmpty(osOrderItemsPO.getExCouponSn())){
        	outChildItem.setIsExGood("0");
        }else{
        	outChildItem.setIsExGood("1");
        }
        /* *****add by zhaochunn 2015-6-30 end***** */
        
        //如果是礼盒的明细（所属礼盒ID不是0），则存入明细map里
        if(osOrderItemsPO.getGiftboxId() != 0){
            OmsGetOrderListGiftBoxItemListElementO giftBoxItem = new OmsGetOrderListGiftBoxItemListElementO();
            giftBoxItem.setItemID(outChildItem.getItemID());
            giftBoxItem.setGoodsID(outChildItem.getGoodsID());
            giftBoxItem.setCardType(outChildItem.getCardType());
            giftBoxItem.setSkuID(outChildItem.getSkuID());
            giftBoxItem.setGoodsName(outChildItem.getGoodsName());
            giftBoxItem.setGoodsNumber(outChildItem.getGoodsNumber());
            giftBoxItem.setGoodsUnit(outChildItem.getGoodsUnit());
            giftBoxItem.setUnitName(outChildItem.getUnitName());
            giftBoxItem.setGoodsWeight(outChildItem.getGoodsWeight());
            giftBoxItem.setShopPrice(outChildItem.getShopPrice());
            giftBoxItem.setGoodsPrice(outChildItem.getGoodsPrice());
            giftBoxItem.setGoodsAmount(outChildItem.getGoodsAmount());
            giftBoxItem.setGoodsPic(outChildItem.getGoodsPic());
            // giftBoxItem.setWebUrl(outChildItem.getWebUrl());
            // giftBoxItem.setCanSale(outChildItem.getCanSale());
            giftBoxItem.setIsShowPrice(outChildItem.getIsShowPrice());
            giftBoxItem.setHasLink(outChildItem.getHasLink());
            giftBoxItem.setIsShowShare(outChildItem.getIsShowShare());
            giftBoxItem.setIsShowReview(outChildItem.getIsShowReview());
            giftBoxItem.setIsGift(outChildItem.getIsGift());
            
            List<OmsGetOrderListGiftBoxItemListElementO> temp = outGiftBoxItemMap.get(osOrderItemsPO.getGiftboxId().longValue());
            if (temp == null || temp.size() <= 0) {
                temp = new ArrayList<OmsGetOrderListGiftBoxItemListElementO>();
            }
            temp.add(giftBoxItem);
            outGiftBoxItemMap.put(osOrderItemsPO.getGiftboxId().longValue(), temp);
            
            return null;
        }
        
        return outChildItem;
    }
    
    
    
    /**
     * 拼子订单输出list
     * Description:<br>
     * 
     * @param osOrderPO
     * @param outChildItemList
     * @return
     */
    private OmsGetOrderListChildListElementO setChildListElementO(OsOrderPO osOrderPO, List<OmsGetOrderListChildItemListElementO> outChildItemList) {
        OmsGetOrderListChildListElementO outChild = new OmsGetOrderListChildListElementO();
        outChild.setOrderID(osOrderPO.getOrderId());
        outChild.setOrderCode(osOrderPO.getOrderCode());
        outChild.setSubstationID(osOrderPO.getSubstationId());
        outChild.setSubstationName(SubstationEnum.get(osOrderPO.getSubstationId().intValue()));
        outChild.setBuyerID(osOrderPO.getBuyerId());
        outChild.setCreateDt(DateUtil.dateTimeToStr(osOrderPO.getCreateDt()));
        String orderType = osOrderPO.getOrderType();
        outChild.setOrderType(orderType);
        outChild.setOrderFrom(osOrderPO.getOrderFrom());
        
        // 子订单状态
        String orderStatus = osOrderPO.getOrderStatus();
        outChild.setOrderStatus(orderStatus);
        // 子订单状态对应前台中文显示
        outChild.setOrderStatusMsg(OrderStatusEnum.getFrontOrderStatus(scope, orderStatus));
        
        // 支付状态
        String payStatus = osOrderPO.getPayStatus();
        outChild.setPayStatus(payStatus);
        outChild.setPayStatusMsg(PayStatusEnum.get(payStatus).getS());
        
        outChild.setReceiver(osOrderPO.getReceiver());
        outChild.setOrderCallFee(osOrderPO.getOrderCallFee().subtract(osOrderPO.getCouponFee() == null?BigDecimal.ZERO:osOrderPO.getCouponFee()).subtract(osOrderPO.getDiscountFee() == null?BigDecimal.ZERO:osOrderPO.getDiscountFee()));
        
        // 子订单是否已作废
        String isCanceled = "0";
        if(GetOrderUtil.getSubIsCanceled(orderStatus)){
            isCanceled = "1";
        }
        outChild.setIsCanceled(isCanceled);
        
        // 可否再次购买,满足条件是： 订单中的商品（不包括赠品is_gift）必须至少有一个是可见的visible_flag
        // 可否再次购买暂不处理，待循环结束后统一处理
        // outChild.setCanBuyAG(null);
        
        // 是否显示订单金额
        String isShowOrderCallFee = "0";
        if(GetOrderUtil.getIsShowOrderCallFee(orderType)){
            isShowOrderCallFee = "1";
        }
        outChild.setIsShowOrderCallFee(isShowOrderCallFee);
        
        // 是否可修改支付方式
        outChild.setCanChgPay("0");
        
        // 是否显示去支付按钮,只针对父订单
        outChild.setIsShowToPay("0");
        
        // 是否当日达
        String isDrd = "0";
        if("1".equals(osOrderPO.getDeliveryTimeType())){
            isDrd = "1";
        }
        outChild.setIsDrd(isDrd);
        
        // 是否显示跟踪按钮
        String isShowOpt = "0";
        if(GetOrderUtil.getSubIsShowOpt(orderStatus, orderType)){
            isShowOpt = "1";
        }
        outChild.setIsShowOpt(isShowOpt);
        
        
        if(outChildItemList != null && outChildItemList.size()>0){
            outChild.setChildItemList(outChildItemList);
        }
        return outChild;
    }
    
    /**
     * 拼父订单明细输出list
     * Description:<br>
     * 
     * @param tOsParentOrderItemsPO
     * @param tOsParentOrderPO
     * @param outGiftBoxItemMap
     * @return
     */
    private OmsGetOrderListOrderItemListElementO setOrderItemElementO(TOsParentOrderItemsPO tOsParentOrderItemsPO, TOsParentOrderPO tOsParentOrderPO, Map<Long, List<OmsGetOrderListGiftBoxItemListElementO>> outGiftBoxItemMap) {
        OmsGetOrderListOrderItemListElementO outItem = new OmsGetOrderListOrderItemListElementO();
        outItem.setItemID(tOsParentOrderItemsPO.getItemId());
        Long goodsId = tOsParentOrderItemsPO.getGoodsId();
        outItem.setGoodsID(goodsId);
        
        // 如果订单类型是电子卡订单，则需返回卡类型
        if(OrderTypeEnum.ONLINE_CARD_VIRTUAL.getC().equals(tOsParentOrderPO.getOrderType())){
            List<Object[]> onlineCardConditions = new ArrayList<Object[]>();
            onlineCardConditions.add(new Object[]{"GOODS_ID", "=", goodsId});
            List<TSysOnlineCardGoodsPO> onlineCardList = tSysOnlineCardGoodsDao.findTSysOnlineCardGoodsPOListByCondition(onlineCardConditions);
            if (onlineCardList != null && onlineCardList.size() >= 1){
                TSysOnlineCardGoodsPO onlineCardGoods = onlineCardList.get(0);
                outItem.setCardType(onlineCardGoods.getCardType());
            }else{
                LogUtils4Oms.error(logger, uuid, "查询电子卡订单卡类型为空", "goodsId", goodsId, "onlineCardList", onlineCardList);
            }
        }
        
        outItem.setSkuID(tOsParentOrderItemsPO.getSku());
        outItem.setGoodsNumber(tOsParentOrderItemsPO.getGoodsNumber().longValue());

        // 是否可售暂不处理，待循环结束后统一处理
        // outItem.setCanSale(null);
        goodsIdSet.add(goodsId);
        
        if("21101".equals(scope)){
            // 英文站
            if(StringUtil.isEmpty(tOsParentOrderItemsPO.getGoodsNameEn())){
                outItem.setGoodsName(tOsParentOrderItemsPO.getGoodsName());
            }else{
                outItem.setGoodsName(tOsParentOrderItemsPO.getGoodsNameEn());
            }
            
            if(StringUtil.isEmpty(tOsParentOrderItemsPO.getGoodsUnitEn())){
                outItem.setGoodsUnit(tOsParentOrderItemsPO.getGoodsUnit());
            }else{
                outItem.setGoodsUnit(tOsParentOrderItemsPO.getGoodsUnitEn());
            }
            
            if(StringUtil.isEmpty(tOsParentOrderItemsPO.getUnitNameEn())){
                outItem.setUnitName(tOsParentOrderItemsPO.getUnitName());
            }else{
                outItem.setUnitName(tOsParentOrderItemsPO.getUnitNameEn());
            }
        }else{
            // 非英文站
            outItem.setGoodsName(tOsParentOrderItemsPO.getGoodsName());
            outItem.setGoodsUnit(tOsParentOrderItemsPO.getGoodsUnit());
            outItem.setUnitName(tOsParentOrderItemsPO.getUnitName());
        }
        outItem.setGoodsWeight(tOsParentOrderItemsPO.getGoodsWeight());
        
        BigDecimal goodsPrice = tOsParentOrderItemsPO.getGoodsPrice();
        outItem.setShopPrice(tOsParentOrderItemsPO.getShopPrice());
        outItem.setGoodsPrice(goodsPrice);
        outItem.setGoodsAmount(tOsParentOrderItemsPO.getGoodsAmount());
        
        String picPath = tOsParentOrderItemsPO.getPicPath();
        if(picPath != null){
            picPath = picPath.split(",")[0];
        }
        outItem.setGoodsPic(picPath);

        
        // 商品链接
        String webUrl = GetOrderUtil.getWebUrl(scope, goodsId, tOsParentOrderItemsPO.getPromoteId());
        outItem.setWebUrl(webUrl);
        
        // 是否显示价格
        String orderType = tOsParentOrderPO.getOrderType();
        String isShowPrice = "0";
        if(GetOrderUtil.getIsShowPrice(goodsPrice, orderType)){
            isShowPrice = "1";
        }
        outItem.setIsShowPrice(isShowPrice);
        
        // 是否提供链接
        String hasLink = "0";
        if(GetOrderUtil.getHasLink(goodsId, orderType)){
            hasLink = "1";
        }
        outItem.setHasLink(hasLink);
        
        // 父订单是否显示分享按钮
        Byte isGift = tOsParentOrderItemsPO.getIsGift();
        String isShowShare = "0";
        if(GetOrderUtil.getParentIsShowShare(scope, tOsParentOrderPO.getOrderStatus(), orderType, isGift)){
            isShowShare = "1";
        }
        outItem.setIsShowShare(isShowShare);
        
        // 是否显示评论按钮(父订单不显示)
        outItem.setIsShowReview("0");
        
        // 是否赠品
        if (isGift == null || isGift != 1) {
            outItem.setIsGift("0");
        } else {
            outItem.setIsGift("1");
        }
        
        // 是否礼盒
        outItem.setIsGiftBox(tOsParentOrderItemsPO.getIsGiftbox());
        
        //是否是兑换商品  
        /* *****add by zhaochunn 2015-6-30 start***** */
        if(StringUtil.isEmpty(tOsParentOrderItemsPO.getExCouponSn())){
        	outItem.setIsExGood("0");
        }else{
        	outItem.setIsExGood("1");
        }
        /* *****add by zhaochunn 2015-6-30 end***** */
        
        //如果是礼盒的明细（所属礼盒ID不是0），则存入明细map里
        if(tOsParentOrderItemsPO.getGiftboxId() != 0){
            OmsGetOrderListGiftBoxItemListElementO giftBoxItem = new OmsGetOrderListGiftBoxItemListElementO();
            giftBoxItem.setItemID(outItem.getItemID());
            giftBoxItem.setGoodsID(outItem.getGoodsID());
            giftBoxItem.setCardType(outItem.getCardType());
            giftBoxItem.setSkuID(outItem.getSkuID());
            giftBoxItem.setGoodsName(outItem.getGoodsName());
            giftBoxItem.setGoodsNumber(outItem.getGoodsNumber());
            giftBoxItem.setGoodsUnit(outItem.getGoodsUnit());
            giftBoxItem.setUnitName(outItem.getUnitName());
            giftBoxItem.setGoodsWeight(outItem.getGoodsWeight());
            giftBoxItem.setShopPrice(outItem.getShopPrice());
            giftBoxItem.setGoodsPrice(outItem.getGoodsPrice());
            giftBoxItem.setGoodsAmount(outItem.getGoodsAmount());
            giftBoxItem.setGoodsPic(outItem.getGoodsPic());
            // giftBoxItem.setWebUrl(outItem.getWebUrl());
            // giftBoxItem.setCanSale(outItem.getCanSale());
            giftBoxItem.setIsShowPrice(outItem.getIsShowPrice());
            giftBoxItem.setHasLink(outItem.getHasLink());
            giftBoxItem.setIsShowShare(outItem.getIsShowShare());
            giftBoxItem.setIsShowReview(outItem.getIsShowReview());
            giftBoxItem.setIsGift(outItem.getIsGift());
            
            List<OmsGetOrderListGiftBoxItemListElementO> temp = outGiftBoxItemMap.get(tOsParentOrderItemsPO.getGiftboxId().longValue());
            if (temp == null || temp.size() <= 0) {
                temp = new ArrayList<OmsGetOrderListGiftBoxItemListElementO>();
            }
            temp.add(giftBoxItem);
            outGiftBoxItemMap.put(tOsParentOrderItemsPO.getGiftboxId().longValue(), temp);
            
            return null;
        }
        
        
        return outItem;
    }
    
    /**
     * 拼父订单输出list
     * Description:<br>
     * 
     * @param tOsParentOrderPO
     * @param outItemList
     * @param hasChild
     * @param outChildList
     * @return
     */
    private OmsGetOrderListOrderListElementO setOrderListElementO(boolean isPurchase, TOsParentOrderPO tOsParentOrderPO, List<OmsGetOrderListOrderItemListElementO> outItemList, String hasChild, List<OmsGetOrderListChildListElementO> outChildList) {
        OmsGetOrderListOrderListElementO outOrder = new OmsGetOrderListOrderListElementO();
        outOrder.setOrderID(tOsParentOrderPO.getOrderId());
        outOrder.setOrderCode(tOsParentOrderPO.getOrderCode());
        outOrder.setSubstationID(tOsParentOrderPO.getSubstationId());
        outOrder.setSubstationName(SubstationEnum.get(tOsParentOrderPO.getSubstationId().intValue()));
        outOrder.setBuyerID(tOsParentOrderPO.getBuyerId());
        outOrder.setCreateDt(DateUtil.dateTimeToStr(tOsParentOrderPO.getCreateDt()));
        String orderType = tOsParentOrderPO.getOrderType();
        outOrder.setOrderType(orderType);
        outOrder.setOrderFrom(tOsParentOrderPO.getOrderFrom());
        
        // 支付方式
        String payType = tOsParentOrderPO.getPayType();
        outOrder.setPayType(payType);
        
        // 父订单状态
        Long orderStatus = tOsParentOrderPO.getOrderStatus();
        outOrder.setOrderStatus(orderStatus.toString());
        // 父订单状态对应前台中文显示
        outOrder.setOrderStatusMsg(ParentOrderStatusEnum.getFrontParentOrderStatus(scope, orderStatus.intValue(), payType));
        
        // 支付状态
        String payStatus = tOsParentOrderPO.getPayStatus();
        outOrder.setPayStatus(payStatus);
        outOrder.setPayStatusMsg(PayStatusEnum.get(payStatus).getS());
        
        outOrder.setReceiver(tOsParentOrderPO.getReceiver());
        
        // 如果拆单了，父订单的订单金额为子订单订单金额之和，防止换货导致金额不一致
        BigDecimal orderCallFee = BigDecimal.ZERO;
        if("1".equals(hasChild) && outChildList != null && outChildList.size() > 0){
            for (OmsGetOrderListChildListElementO childListO : outChildList) {
                orderCallFee = orderCallFee.add(childListO.getOrderCallFee() == null?BigDecimal.ZERO:childListO.getOrderCallFee());
            }
        }else{
            orderCallFee = tOsParentOrderPO.getOrderCallFee().subtract(tOsParentOrderPO.getCouponFee() == null?BigDecimal.ZERO:tOsParentOrderPO.getCouponFee()).subtract(tOsParentOrderPO.getDiscountFee() == null?BigDecimal.ZERO:tOsParentOrderPO.getDiscountFee());
        }
        outOrder.setOrderCallFee(orderCallFee);
        
        // 父订单是否已作废
        String isCanceled = "0";
        if(GetOrderUtil.getParentIsCanceled(orderStatus)){
            isCanceled = "1";
        }
        outOrder.setIsCanceled(isCanceled);
        
        
        // 可否再次购买,满足条件是： 订单中的商品（不包括赠品is_gift）必须至少有一个是可见的visible_flag
        // 可否再次购买暂不处理，待循环结束后统一处理
        // outOrder.setCanBuyAG(null);
        
        
        // 是否显示订单金额
        String isShowOrderCallFee = "0";
        if(GetOrderUtil.getIsShowOrderCallFee(orderType)){
            isShowOrderCallFee = "1";
        }
        outOrder.setIsShowOrderCallFee(isShowOrderCallFee);
        
        
        // 是否可修改支付方式
        outOrder.setCanChgPay(tOsParentOrderPO.getChgPay());
        
        
        // 是否显示去支付按钮,只针对父订单
        String isShowToPay = "0";
        if(GetOrderUtil.getParentIsShowToPay(tOsParentOrderPO.getOrderStatus(), payStatus, tOsParentOrderPO.getPayType(), tOsParentOrderPO.getOrderId(), osPayPlanDao, logger, uuid)){
            isShowToPay = "1";
        }
        outOrder.setIsShowToPay(isShowToPay);
        
        
        if("1".equals(isShowToPay)){
            Map<String, String> receiveMap = OrderUtil.getReceiveDt(true, tOsParentOrderPO.getOrderStatus().intValue(), "", tOsParentOrderPO.getOrderType(), tOsParentOrderPO.getDeliveryType(), tOsParentOrderPO.getSubstationId(), tOsParentOrderPO.getTeamId(), tOsParentOrderPO.getSpecifiedShippingdate(), tOsParentOrderPO.getReceiveDt(), tOsParentOrderPO.getReceiveDtMsg(), logger, uuid, scope);
            outOrder.setIsShowReceiveDt(receiveMap.get("isShowReceiveDt"));
            outOrder.setReceiveMsg(receiveMap.get("receiveMsg"));
            outOrder.setReceiveDt(receiveMap.get("receiveDt"));
            if(isPurchase){
                outOrder.setIsPurchase(1L);
            }else{
                outOrder.setIsPurchase(0L);
            }
            
            Log.info(logger, uuid, "查询原始订单计划开始", "orderCode", tOsParentOrderPO.getOrderCode());
            OsPayPlanPO payPlan = osPayPlanDao.findOsPayPlanPOByID(tOsParentOrderPO.getOrderId());
            Log.info(logger, uuid, "查询原是支付计划完毕", "orderCode", tOsParentOrderPO.getOrderCode(), "payPlan", payPlan);
            if (payPlan == null || StringUtil.isEmpty(payPlan.getPayInfo())) {
                Log.info(logger, uuid, "此订单支付计划有误,数据错误", "orderCode", tOsParentOrderPO.getOrderCode(), "payPlan", payPlan);
                outOrder.setOrderPayFee(BigDecimal.ZERO);
                outOrder.setOnlinePayFee(BigDecimal.ZERO);
                outOrder.setOfflinePayFee(BigDecimal.ZERO);
            }else{
                Map<Long, Long> payMethodMap = DictionaryData.getPayMethodPidMap(logger, uuid);
                Log.info(logger, uuid, "支付方式", "payMethodMap", payMethodMap);
                BigDecimal onlinePayFee = BigDecimal.ZERO;
                BigDecimal offlinePayFee = BigDecimal.ZERO;
                BigDecimal couponFee = BigDecimal.ZERO;
                Long defaultPayMethodId = 0L;
                String[] payInfoArray = payPlan.getPayInfo().split("\\^");
                for (String s : payInfoArray) {
                    String[] array = s.split("_");
                    BigDecimal bd = new BigDecimal(array[1]);
                    
                    Long key = payMethodMap.get(Long.valueOf(array[0]));
                    if(key == 0L){
                        key = Long.valueOf(array[0]);
                    }
                    
                    if(PayMethodEnum.ONLINE.getC() == key.intValue()){
                        defaultPayMethodId = Long.valueOf(array[0]);
                        onlinePayFee = onlinePayFee.add(bd);
                    }else if(PayMethodEnum.OFFLINE.getC() == key.intValue()){
                        offlinePayFee = offlinePayFee.add(bd);
                    }else if(PayMethodEnum.COUPON.getC() == key.intValue()){
                        couponFee = couponFee.add(bd);
                    }
                }
                
                outOrder.setOrderPayFee(payPlan.getPlanAmt().subtract(couponFee));
                outOrder.setOnlinePayFee(onlinePayFee);
                outOrder.setOfflinePayFee(offlinePayFee);
                
                if (onlinePayFee.compareTo(BigDecimal.ZERO) != 0) {
                    outOrder.setPayMethodId(defaultPayMethodId);
                    if("21101".equals(scope)){
                        Map<Long, String> payMethodTitleEnMap = DictionaryData.getPayMethodTitleEnMap(logger, uuid);
                        outOrder.setPayMethodTile(payMethodTitleEnMap.get(defaultPayMethodId));
                    }else{
                        Map<Long, String> payMethodTitleMap = DictionaryData.getPayMethodTitleMap(logger, uuid);
                        outOrder.setPayMethodTile(payMethodTitleMap.get(defaultPayMethodId));
                    }
                }
            }
        }
        
        
        
        // 是否当日达
        String isDrd = "0";
        if("1".equals(tOsParentOrderPO.getDeliveryTimeType())){
            isDrd = "1";
        }
        outOrder.setIsDrd(isDrd);
        
        // 是否显示跟踪按钮
        String isShowOpt = "0";
        if(GetOrderUtil.getParentIsShowOpt(orderStatus.intValue(), orderType)){
            isShowOpt = "1";
        }
        outOrder.setIsShowOpt(isShowOpt);
        
        // 是否显示用户自助取消订单按钮
        if (OrderTypeEnum.ONLINE_CARD_VIRTUAL.getC().equals(tOsParentOrderPO.getOrderType())) {
            if((orderStatus >= 1 && orderStatus <= 30) || orderStatus == 60){
                outOrder.setIsShowCancel("1");
            }else{
                outOrder.setIsShowCancel("0");
            }
        } else if (OrderTypeEnum.BAOYOU_TUANGOU_ORDER.getC().equals(tOsParentOrderPO.getOrderType())
                        && new Timestamp(System.currentTimeMillis()).after(DateUtil.getDateBeforeOrAfterDays(tOsParentOrderPO.getReceiveDt(), -1))) {
            Log.info(logger, uuid, "包邮团购订单从发货日前1天开始，前台不允许取消订单", "orderCode", tOsParentOrderPO.getOrderCode(), "receiveDt", tOsParentOrderPO.getReceiveDt());
            outOrder.setIsShowCancel("0");
        } else if (orderStatus >= 1 && orderStatus <= 120 && orderStatus != 110) {
            outOrder.setIsShowCancel("1");
        } else if (orderStatus >= 125 || orderStatus == 110) {
            outOrder.setIsShowCancel("1");
            List<Object[]> orderConditions = new ArrayList<Object[]>();
            orderConditions.add(new Object[]{"PARENT_ID", "=", tOsParentOrderPO.getOrderId()});
            orderConditions.add(new Object[]{"ORDER_STATUS", "<>", OrderStatusEnum.REPALCE_GOODS_CANCEL.getC()});
            List<OsOrderPO> orderList = osOrderDao.findOsOrderPOListByCondition(orderConditions);
            for (OsOrderPO osOrder : orderList) {
                LogUtils4Oms.info(logger, uuid, "自助取消按钮判断子订单状态", "subOrderCode", osOrder.getOrderCode(), "orderStatus", osOrder.getOrderStatus());
                OrderStatusEnum orderStatusEnum = OrderStatusEnum.get(osOrder.getOrderStatus());
                if (orderStatusEnum == null || !OrderStatusEnum.canBeCanceled(orderStatusEnum)) {
                    outOrder.setIsShowCancel("0");
                    break;
                }
            }
        } else {
            outOrder.setIsShowCancel("0");
        }
        
        
        outOrder.setShipCallFee(tOsParentOrderPO.getShipCallFee());
        outOrder.setShipProvince(tOsParentOrderPO.getShipProvince());
        outOrder.setShipCity(tOsParentOrderPO.getShipCity());
        outOrder.setShipDistrict(tOsParentOrderPO.getShipDistrict());
        
        
        if (outItemList != null && outItemList.size() > 0) {
            outOrder.setOrderItemList(outItemList);
        }
        outOrder.setHasChild(hasChild);
        if (outChildList != null && outChildList.size() > 0) {
            outOrder.setChildList(outChildList);
        }
        
        return outOrder;
    }
    
    
    
    /**
     * 调用商品接口查询对应商品是否上架并且可见的
     * 然后设置商品是否可见，订单是否可再次购买
     * Description:<br>
     * 
     * @param goodsIds
     * @return
     */
    @SuppressWarnings("unchecked")
    private void setGoodsCanSale(List<OmsGetOrderListOrderListElementO> outOrderList) {
        GetOrderCommonService getOrderCommonService = new GetOrderCommonService(logger, uuid);
        Map<String, Map> goodsInfo = getOrderCommonService.getGoodsInfo(goodsIdSet, scope, httpRequest);
        if (goodsInfo == null || goodsInfo.isEmpty()) {
            LogUtils4Oms.error(logger, uuid, "调用获取商品详情接口获取Map:goodsInfo异常", null, "goodsInfo", goodsInfo);
            return;
        }
        
        try {
            Map<Long, String> goodsCanSaleMap = goodsInfo.get("goodsCanSaleMap");
            Map<Long, Long> goodsMaxBuyMap = goodsInfo.get("goodsMaxBuyMap");
            Map<Long, Long> goodsMinBuyMap = goodsInfo.get("goodsMinBuyMap");
            
            for (OmsGetOrderListOrderListElementO orderListElementO : outOrderList) {
                String hasChild = orderListElementO.getHasChild();
                if ("0".equals(hasChild)) { // 未拆分成子订单
                    
                    String orderType = orderListElementO.getOrderType();
                    // 沱沱惠订单、接龙团购订单、提货订单和卡订单不提供再次购买功能
                    if (OrderTypeEnum.BAOYOU_TUANGOU_ORDER.getC().equals(orderType) || OrderTypeEnum.JIELONG_GROUPBUY.getC().equals(orderType) 
                                    || OrderTypeEnum.isTiHuoOrder(orderType)
                                    || OrderTypeEnum.GIFT_CARD_ORDER.getC().equals(orderType) || OrderTypeEnum.ONLINE_CARD_VIRTUAL.getC().equals(orderType)){
                        orderListElementO.setCanBuyAG("0");
                        
                    }else{
                        // 可否再次购买:订单中的商品（不包括赠品is_gift）必须至少有一个是可见的
                        String canBuyAg = "0"; 
                        List<OmsGetOrderListOrderItemListElementO> orderItemListElementO = orderListElementO.getOrderItemList();
                        // 循环为商品明细设置是否可见
                        for (OmsGetOrderListOrderItemListElementO orderItemElementO : orderItemListElementO) {
                            if ("1".equals(orderItemElementO.getIsGiftBox())){ // 如果是礼盒，需礼盒下面的所有商品都可售，此礼盒才可售
                                String giftBoxCanSale = "1";
                                List<OmsGetOrderListGiftBoxItemListElementO> giftBoxItemListElementO = orderItemElementO.getGiftBoxItemList();
                                for (OmsGetOrderListGiftBoxItemListElementO giftBoxItemElementO : giftBoxItemListElementO) {
                                    String giftCanSale = goodsCanSaleMap.get(giftBoxItemElementO.getGoodsID());
                                    if ("1".equals(giftCanSale)) {
                                        giftBoxItemElementO.setCanSale("1");
                                        giftBoxItemElementO.setMaxBuyNum(goodsMaxBuyMap.get(giftBoxItemElementO.getGoodsID()));
                                        giftBoxItemElementO.setMinBuyNum(goodsMinBuyMap.get(giftBoxItemElementO.getGoodsID()));
                                    } else {
                                        giftBoxItemElementO.setCanSale("0");
                                        giftBoxItemElementO.setMaxBuyNum(0L);
                                        giftBoxItemElementO.setMinBuyNum(0L);
                                        giftBoxCanSale = "0";
                                    }
                                }
                                orderItemElementO.setCanSale(giftBoxCanSale);
                                orderItemElementO.setMaxBuyNum(goodsMaxBuyMap.get(orderItemElementO.getGoodsID()));
                                orderItemElementO.setMinBuyNum(goodsMinBuyMap.get(orderItemElementO.getGoodsID()));
                                if("1".equals(giftBoxCanSale)){
                                    canBuyAg = "1";
                                }
                                
                            }else{ // 普通商品，非礼盒
                                String canSale = goodsCanSaleMap.get(orderItemElementO.getGoodsID());
                                if ("1".equals(canSale)) {
                                    orderItemElementO.setCanSale("1");
                                    orderItemElementO.setMaxBuyNum(goodsMaxBuyMap.get(orderItemElementO.getGoodsID()));
                                    orderItemElementO.setMinBuyNum(goodsMinBuyMap.get(orderItemElementO.getGoodsID()));
                                    if ("0".equals(orderItemElementO.getIsGift())) {
                                        canBuyAg = "1";
                                    }
                                } else {
                                    orderItemElementO.setCanSale("0");
                                    orderItemElementO.setMaxBuyNum(0L);
                                    orderItemElementO.setMinBuyNum(0L);
                                }
                            }
                        }
                        orderListElementO.setCanBuyAG(canBuyAg);
                    }
                    
                } else { // 已拆分成子订单
                    // 订单中的商品（不包括赠品is_gift）必须至少有一个是可见的visible_flag
                    String parentCanBuyAg = "0";
                    List<OmsGetOrderListChildListElementO> childListElementO = orderListElementO.getChildList();
                    for (OmsGetOrderListChildListElementO childElementO : childListElementO) {
                        
                        String orderType = childElementO.getOrderType();
                        // 沱沱惠订单、接龙团购订单、提货订单和卡订单不提供再次购买功能
                        if (OrderTypeEnum.BAOYOU_TUANGOU_ORDER.getC().equals(orderType) || OrderTypeEnum.JIELONG_GROUPBUY.getC().equals(orderType) 
                                        || OrderTypeEnum.isTiHuoOrder(orderType)
                                        || OrderTypeEnum.GIFT_CARD_ORDER.getC().equals(orderType) || OrderTypeEnum.ONLINE_CARD_VIRTUAL.getC().equals(orderType)){
                            childElementO.setCanBuyAG("0");
                            
                        }else{
                            List<OmsGetOrderListChildItemListElementO> childItemListElementO = childElementO.getChildItemList();
                            
                            String canBuyAg = "0";
                            // 循环为商品明细设置是否可见
                            for (OmsGetOrderListChildItemListElementO childItemElementO : childItemListElementO) {
                                if("1".equals(childItemElementO.getIsGiftBox())){ // 如果是礼盒，需礼盒下面的所有商品都可售，此礼盒才可售
                                    String giftBoxCanSale = "1";
                                    List<OmsGetOrderListGiftBoxItemListElementO> giftBoxItemListElementO = childItemElementO.getGiftBoxItemList();
                                    for (OmsGetOrderListGiftBoxItemListElementO giftBoxItemElementO : giftBoxItemListElementO) {
                                        String giftCanSale = goodsCanSaleMap.get(giftBoxItemElementO.getGoodsID());
                                        if ("1".equals(giftCanSale)) {
                                            giftBoxItemElementO.setCanSale("1");
                                            giftBoxItemElementO.setMaxBuyNum(goodsMaxBuyMap.get(giftBoxItemElementO.getGoodsID()));
                                            giftBoxItemElementO.setMinBuyNum(goodsMinBuyMap.get(giftBoxItemElementO.getGoodsID()));
                                        } else {
                                            giftBoxItemElementO.setCanSale("0");
                                            giftBoxItemElementO.setMaxBuyNum(0L);
                                            giftBoxItemElementO.setMinBuyNum(0L);
                                            giftBoxCanSale = "0";
                                        }
                                    }
                                    childItemElementO.setCanSale(giftBoxCanSale);
                                    childItemElementO.setMaxBuyNum(goodsMaxBuyMap.get(childItemElementO.getGoodsID()));
                                    childItemElementO.setMinBuyNum(goodsMinBuyMap.get(childItemElementO.getGoodsID()));
                                    if("1".equals(giftBoxCanSale)){
                                        canBuyAg = "1";
                                        parentCanBuyAg = "1";
                                    }
                                    
                                }else{ // 普通商品，非礼盒
                                    String canSale = goodsCanSaleMap.get(childItemElementO.getGoodsID());
                                    if ("1".equals(canSale)) {
                                        childItemElementO.setCanSale("1");
                                        childItemElementO.setMaxBuyNum(goodsMaxBuyMap.get(childItemElementO.getGoodsID()));
                                        childItemElementO.setMinBuyNum(goodsMinBuyMap.get(childItemElementO.getGoodsID()));
                                        if ("0".equals(childItemElementO.getIsGift())) {
                                            canBuyAg = "1";
                                            parentCanBuyAg = "1";
                                        }
                                    } else {
                                        childItemElementO.setCanSale("0");
                                        childItemElementO.setMaxBuyNum(0L);
                                        childItemElementO.setMinBuyNum(0L);
                                    }
                                }
                            }
                            childElementO.setCanBuyAG(canBuyAg);
                            
                        }
                        
                    }
                    orderListElementO.setCanBuyAG(parentCanBuyAg);
                }
            }
            
        } catch (Exception e) {
            LogUtils4Oms.error(logger, uuid, "设置商品可售和再次购买错误", e, "goodsInfo", goodsInfo);
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
    public void doClose(final BaseInputHead inputHead, final OmsGetOrderListInputData inputData, final BaseOutputHead outputHead, final OmsGetOrderListOutputData outputData) throws OmsDelegateException {
        
    }
}
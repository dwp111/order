package cn.tootoo.soa.oms.getorderitemlist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import cn.tootoo.db.egrocery.tosparentorder.TOsParentOrderDao;
import cn.tootoo.db.egrocery.tosparentorder.TOsParentOrderPO;
import cn.tootoo.db.egrocery.tosparentorderitems.TOsParentOrderItemsDao;
import cn.tootoo.db.egrocery.tosparentorderitems.TOsParentOrderItemsPO;
import cn.tootoo.enums.BaseResultEnum;
import cn.tootoo.http.AuthorizeClient;
import cn.tootoo.http.TootooService;
import cn.tootoo.http.bean.BaseInputBean;
import cn.tootoo.http.bean.BaseOutputBean;
import cn.tootoo.servlet.BaseService;
import cn.tootoo.soa.base.enums.BaseOrderResultEnum;
import cn.tootoo.soa.goods.getgoodssalecats.input.GoodsGetGoodsSaleCatsGoodsIDsElementI;
import cn.tootoo.soa.goods.getgoodssalecats.input.GoodsGetGoodsSaleCatsInputData;
import cn.tootoo.soa.goods.getgoodssalecats.output.GoodsGetGoodsSaleCatsGoodsSaleCatInfosElementO;
import cn.tootoo.soa.goods.getgoodssalecats.output.GoodsGetGoodsSaleCatsOutputData;
import cn.tootoo.soa.oms.getorderitemlist.input.OmsGetOrderItemListInputData;
import cn.tootoo.soa.oms.getorderitemlist.output.OmsGetOrderItemListOrderItemListElementO;
import cn.tootoo.soa.oms.getorderitemlist.output.OmsGetOrderItemListOutputData;
import cn.tootoo.soa.oms.utils.LogUtils4Oms;
import cn.tootoo.utils.Log;
import cn.tootoo.utils.ResponseUtil;
import cn.tootoo.utils.StringUtil;

/**
 * 服务接口方法的实现类。
 * 
 * 服务description：订单服务。
 * 服务remark：订单服务。
 * 接口description：获取父订单明细信息
 * 接口remark：
 */
public final class GetOrderItemListImpl extends BaseService {
    
    
    /**
     * 日志对象。
     */
    private Logger logger = Logger.getLogger(GetOrderItemListImpl.class);
    
    
    @Override
    public BaseOutputBean doService(BaseInputBean inputBean) throws Exception {
        BaseOutputBean outputBean = new BaseOutputBean();
        try{
            LogUtils4Oms.info(logger, uuid, "执行服务开始，传入bean", "inputBean", inputBean);
            OmsGetOrderItemListOutputData outputData = new OmsGetOrderItemListOutputData();
            if (inputBean.getInputData() == null) {
                LogUtils4Oms.info(logger, uuid, "参数req_str为空", "inputBean", inputBean);
                outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.REQUEST_PARAM_ERROR.getResultID(), null, "");
                return outputBean;
            }
            OmsGetOrderItemListInputData inputData = null;
            try {
                inputData = (OmsGetOrderItemListInputData)inputBean.getInputData();
            } catch (Exception e) {
                LogUtils4Oms.error(logger, uuid, "接口实现类转换错误", e, "className", OmsGetOrderItemListInputData.class.getName());
                outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.CLASS_TRANSFER_ERROR.getResultID(), null, "");
                return outputBean;
            }
            
            
            // 登录验证信息
            String userID = "";
            String scope = inputData.getScope();
            try {
                LogUtils4Oms.info(logger, uuid, "获取父订单明细信息服务,进行验证！");
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
            
            
            LogUtils4Oms.info(logger, uuid, "获取父订单明细信息", "inputData", inputData);
            TOsParentOrderDao tOsParentOrderDao = new TOsParentOrderDao(uuid, logger);
            TOsParentOrderItemsDao tOsParentOrderItemsDao = new TOsParentOrderItemsDao(uuid, logger);
            String orderCode = inputData.getOrderCode();
            
            
            List<Object[]> pOrderConditions = new ArrayList<Object[]>();
            pOrderConditions.add(new Object[]{"BUYER_ID", "=", userID});
            pOrderConditions.add(new Object[]{"ORDER_CODE", "=", orderCode});
            List<TOsParentOrderPO> parentOrderList = tOsParentOrderDao.findTOsParentOrderPOListByCondition(pOrderConditions);
            if (parentOrderList == null || parentOrderList.size() <= 0) {
                LogUtils4Oms.info(logger, uuid, "查询父订单表为空", "pOrderConditions", StringUtil.transferObjectList(pOrderConditions));
                outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.SUCCESS.getResultID(), outputData, scope);
                return outputBean;
            }
            
            
            List<Object[]> pOrderItemsConditions = new ArrayList<Object[]>();
            pOrderItemsConditions.add(new Object[]{"ORDER_ID", "=", parentOrderList.get(0).getOrderId()});
            List<TOsParentOrderItemsPO> parentOrderItemsList = tOsParentOrderItemsDao.findTOsParentOrderItemsPOListByCondition(pOrderItemsConditions);
            if (parentOrderItemsList == null || parentOrderItemsList.size() <= 0) {
                LogUtils4Oms.info(logger, uuid, "查询父订单明细表为空", "pOrderItemsConditions", StringUtil.transferObjectList(pOrderItemsConditions));
                outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.SUCCESS.getResultID(), outputData, scope);
                return outputBean;
            }
            
            
            Log.info(logger, uuid, "组装goods服务getGoodsSaleCats方法所需参数开始");
            GoodsGetGoodsSaleCatsInputData getGoodsSaleCatsI = new GoodsGetGoodsSaleCatsInputData();
            getGoodsSaleCatsI.setScope(scope);
            List<GoodsGetGoodsSaleCatsGoodsIDsElementI> goodsIdListI = new ArrayList<GoodsGetGoodsSaleCatsGoodsIDsElementI>();
            for (TOsParentOrderItemsPO tOsParentOrderItemsPO : parentOrderItemsList) {
                GoodsGetGoodsSaleCatsGoodsIDsElementI goodsIdI = new GoodsGetGoodsSaleCatsGoodsIDsElementI();
                goodsIdI.setGoodsID(tOsParentOrderItemsPO.getGoodsId());
                goodsIdListI.add(goodsIdI);
            }
            getGoodsSaleCatsI.setGoodsIDs(goodsIdListI);
            Map<String, String> getGoodsSaleCatsParams = new HashMap<String, String>();
            getGoodsSaleCatsParams.put("method", "getGoodsSaleCats");
            getGoodsSaleCatsParams.put("req_str", getGoodsSaleCatsI.toJson());
            Log.info(logger, uuid, "组装goods服务getGoodsSaleCats方法所需参数结束");
            
            
            Log.info(logger, uuid, "调用goods服务getGoodsSaleCats方法开始", "getGoodsSaleCatsParams", getGoodsSaleCatsParams);
            outputBean = TootooService.callServer("goods", getGoodsSaleCatsParams, "post", new GoodsGetGoodsSaleCatsOutputData());
            Log.info(logger, uuid, "调用goods服务getGoodsSaleCats方法结束", "outputBean", outputBean);
            
            Map<Long, Long> salCatMap = new HashMap<Long, Long>();
            if (!TootooService.checkService(outputBean, "goods", "getGoodsSaleCats", inputData.getScope())) {
                Log.info(logger, uuid, "调用goods服务getGoodsSaleCats方法失败,接口返回", "outputBean", outputBean);
            }else{
                GoodsGetGoodsSaleCatsOutputData getGoodsSaleCatsOutputData = (GoodsGetGoodsSaleCatsOutputData)outputBean.getOutputData();
                List<GoodsGetGoodsSaleCatsGoodsSaleCatInfosElementO> getGoodsSaleCatsListO = getGoodsSaleCatsOutputData.getGoodsSaleCatInfos();
                if(getGoodsSaleCatsListO != null){
                    for (GoodsGetGoodsSaleCatsGoodsSaleCatInfosElementO getGoodsSaleCatsO : getGoodsSaleCatsListO) {
                        if(getGoodsSaleCatsO.getSaleCatInfos() != null && getGoodsSaleCatsO.getSaleCatInfos().size()>=1){
                            salCatMap.put(getGoodsSaleCatsO.getGoodsID(), getGoodsSaleCatsO.getSaleCatInfos().get(0).getRootID());
                        }
                    }
                }
            }
            Log.info(logger, uuid, "调用goods服务获取rootId结束", "salCatMap", salCatMap);
            
            
            List<OmsGetOrderItemListOrderItemListElementO> orderItemListO = new ArrayList<OmsGetOrderItemListOrderItemListElementO>();
            for (TOsParentOrderItemsPO tOsParentOrderItemsPO : parentOrderItemsList) {
                if(tOsParentOrderItemsPO.getGiftboxId() != 0){
                    continue;
                }
                OmsGetOrderItemListOrderItemListElementO orderItemO = new OmsGetOrderItemListOrderItemListElementO();
                orderItemO.setItemID(tOsParentOrderItemsPO.getItemId());
                orderItemO.setIsGift(tOsParentOrderItemsPO.getIsGift() + "");
                orderItemO.setIsGiftBox(tOsParentOrderItemsPO.getIsGiftbox());
                orderItemO.setGoodsID(tOsParentOrderItemsPO.getGoodsId());
                orderItemO.setGoodsAmount(tOsParentOrderItemsPO.getGoodsAmount());
                orderItemO.setGoodsNumber(tOsParentOrderItemsPO.getGoodsNumber().longValue());
                orderItemO.setGoodsPrice(tOsParentOrderItemsPO.getGoodsPrice());
                orderItemO.setSkuID(tOsParentOrderItemsPO.getSku());
                if (tOsParentOrderItemsPO.getIsGift() != 1){
                    orderItemO.setMainCat(salCatMap.get(tOsParentOrderItemsPO.getGoodsId()));
                }
                if("21101".equals(parentOrderList.get(0).getScope())){
                    orderItemO.setGoodsName(tOsParentOrderItemsPO.getGoodsNameEn());
                }else{
                    orderItemO.setGoodsName(tOsParentOrderItemsPO.getGoodsName());
                }
                orderItemO.setCatId(tOsParentOrderItemsPO.getCatId());
                orderItemListO.add(orderItemO);
            }
            
            outputData.setOrderItemList(orderItemListO);
            
            LogUtils4Oms.info(logger, uuid, "查询成功", "orderItemListO", orderItemListO);
            outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.SUCCESS.getResultID(), outputData, scope);
            return outputBean;
            
        }catch(Exception e){
            LogUtils4Oms.error(logger, uuid, "获取父订单明细信息请求出错", e);
            outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.INSIDE_ERROR.getResultID(), null, "");
            LogUtils4Oms.info(logger, uuid, "接口返回", "outputBean", outputBean);
            return outputBean;
        }
    }
    
    @Override
    public BaseService clone() throws CloneNotSupportedException {
        return new GetOrderItemListImpl();
    }
    
}
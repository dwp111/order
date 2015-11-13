package cn.tootoo.soa.oms.getbuyerpromotionnum;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import cn.tootoo.db.egrocery.buyerpromotionnum.BuyerPromotionNumDao;
import cn.tootoo.db.egrocery.buyerpromotionnum.BuyerPromotionNumPO;
import cn.tootoo.enums.BaseResultEnum;
import cn.tootoo.http.AuthorizeClient;
import cn.tootoo.http.bean.BaseInputBean;
import cn.tootoo.http.bean.BaseOutputBean;
import cn.tootoo.servlet.BaseService;
import cn.tootoo.soa.base.enums.BaseOrderResultEnum;
import cn.tootoo.soa.oms.getbuyerpromotionnum.input.OmsGetBuyerPromotionNumGoodsListElementI;
import cn.tootoo.soa.oms.getbuyerpromotionnum.input.OmsGetBuyerPromotionNumInputData;
import cn.tootoo.soa.oms.getbuyerpromotionnum.input.OmsGetBuyerPromotionNumPromotionListElementI;
import cn.tootoo.soa.oms.getbuyerpromotionnum.output.OmsGetBuyerPromotionNumGoodsListElementO;
import cn.tootoo.soa.oms.getbuyerpromotionnum.output.OmsGetBuyerPromotionNumOutputData;
import cn.tootoo.soa.oms.getbuyerpromotionnum.output.OmsGetBuyerPromotionNumPromotionListElementO;
import cn.tootoo.soa.oms.utils.LogUtils4Oms;
import cn.tootoo.utils.Log;
import cn.tootoo.utils.Memcached;
import cn.tootoo.utils.ResponseUtil;
import cn.tootoo.utils.StringUtil;

/**
 * 服务接口方法的实现类。
 * 
 * 服务description：订单服务。
 * 服务remark：订单服务。
 * 接口description：获取促销商品会员购买数
 * 接口remark：
 */
public final class GetBuyerPromotionNumImpl extends BaseService {
    
    /**
     * 日志对象。
     */
    private Logger logger = Logger.getLogger(GetBuyerPromotionNumImpl.class);
    
    @Override
    public BaseOutputBean doService(BaseInputBean inputBean) throws Exception {
        BaseOutputBean outputBean = new BaseOutputBean();
        try{
            LogUtils4Oms.info(logger, uuid, "执行服务开始，传入bean", "inputBean", inputBean);
            OmsGetBuyerPromotionNumOutputData outputData = new OmsGetBuyerPromotionNumOutputData();
            if (inputBean.getInputData() == null) {
                LogUtils4Oms.info(logger, uuid, "参数req_str为空", "inputBean", inputBean);
                outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.REQUEST_PARAM_ERROR.getResultID(), null, "");
                return outputBean;
            }
            OmsGetBuyerPromotionNumInputData inputData = null;
            try {
                inputData = (OmsGetBuyerPromotionNumInputData)inputBean.getInputData();
            } catch (Exception e) {
                LogUtils4Oms.error(logger, uuid, "接口实现类转换错误", e, "className", OmsGetBuyerPromotionNumInputData.class.getName());
                outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.CLASS_TRANSFER_ERROR.getResultID(), null, "");
                return outputBean;
            }
            
            
            // 登录验证信息
            String userID = inputData.getBuyerId() + "";
            String scope = inputData.getScope();
            if(StringUtil.isEmpty(userID)){
                try {
                    LogUtils4Oms.info(logger, uuid, "getBuyerPromotionNum服务,进行验证！");
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
            }
            

            BuyerPromotionNumDao buyerPromotionNumDao = new BuyerPromotionNumDao(uuid, logger);
            
            List<OmsGetBuyerPromotionNumPromotionListElementI> promotionListI = inputData.getPromotionList();
            List<OmsGetBuyerPromotionNumPromotionListElementO> promotionListO = new ArrayList<OmsGetBuyerPromotionNumPromotionListElementO>();
            for (OmsGetBuyerPromotionNumPromotionListElementI omsGetBuyerPromotionNumPromotionListElementI : promotionListI) {
                List<OmsGetBuyerPromotionNumGoodsListElementO> goodsListO = new ArrayList<OmsGetBuyerPromotionNumGoodsListElementO>();

                Long promotionId = omsGetBuyerPromotionNumPromotionListElementI.getPromotionId();
                List<OmsGetBuyerPromotionNumGoodsListElementI> goodsListI = omsGetBuyerPromotionNumPromotionListElementI.getGoodsList();
                for (OmsGetBuyerPromotionNumGoodsListElementI omsGetBuyerPromotionNumGoodsListElementI : goodsListI) {
                    OmsGetBuyerPromotionNumGoodsListElementO omsGetBuyerPromotionNumGoodsListElementO = new OmsGetBuyerPromotionNumGoodsListElementO();
                    omsGetBuyerPromotionNumGoodsListElementO.setGoodsId(omsGetBuyerPromotionNumGoodsListElementI.getGoodsId());
                    
                    String key = "findChannel-" + userID + "-" + promotionId + "-" + omsGetBuyerPromotionNumGoodsListElementI.getGoodsId();
                    Object buyerNum = Memcached.get(key);
                    if (buyerNum != null) { 
                        Log.info(logger, uuid, "从缓存中取到了值", "buyerNum", buyerNum);
                        omsGetBuyerPromotionNumGoodsListElementO.setNum((Long)buyerNum);
                    } else { 
                        Log.info(logger, uuid, "从缓存中没取到值，查库", "buyerNum", buyerNum);
                        List<Object[]> condition = new ArrayList<Object[]>();
                        condition.add(new Object[]{"PROMOTION_ID", "=", promotionId});
                        condition.add(new Object[]{"BUYER_ID", "=", userID});
                        condition.add(new Object[]{"GOODS_ID", "=", omsGetBuyerPromotionNumGoodsListElementI.getGoodsId()});
                        List<BuyerPromotionNumPO> buyerNumList = buyerPromotionNumDao.findBuyerPromotionNumPOListByCondition(condition);
                        if (buyerNumList == null || buyerNumList.size() <= 0) {
                            Log.info(logger, uuid, "从库中没取到值，返回0");
                            omsGetBuyerPromotionNumGoodsListElementO.setNum(0L);
                        } else {
                            Log.info(logger, uuid, "从库中取到了值", "buyerNum", buyerNumList.get(0).getNum());
                            omsGetBuyerPromotionNumGoodsListElementO.setNum(buyerNumList.get(0).getNum());
                            // 重新放入缓存中
                            Memcached.set(key, buyerNumList.get(0).getNum());
                        }
                    }
                    goodsListO.add(omsGetBuyerPromotionNumGoodsListElementO);
                }
                OmsGetBuyerPromotionNumPromotionListElementO promotionO = new OmsGetBuyerPromotionNumPromotionListElementO();
                promotionO.setPromotionId(promotionId);
                promotionO.setGoodsList(goodsListO);
                promotionListO.add(promotionO);
            }
            outputData.setPromotionList(promotionListO);
            outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.SUCCESS.getResultID(), outputData, scope);
            return outputBean;

        }catch(Exception e){
            LogUtils4Oms.error(logger, uuid, "获取促销商品会员购买数服务出错，返回结果：", e, "outputBean", outputBean);
            outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.INSIDE_ERROR.getResultID(), null, "");
            return outputBean;
        }
    }
    
    @Override
    public BaseService clone() throws CloneNotSupportedException {
        return new GetBuyerPromotionNumImpl();
    }
    
}
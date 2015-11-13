package cn.tootoo.soa.oms.setbuyerpromotionnum;

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
import cn.tootoo.soa.oms.setbuyerpromotionnum.input.OmsSetBuyerPromotionNumGoodsListElementI;
import cn.tootoo.soa.oms.setbuyerpromotionnum.input.OmsSetBuyerPromotionNumInputData;
import cn.tootoo.soa.oms.setbuyerpromotionnum.output.OmsSetBuyerPromotionNumOutputData;
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
 * 接口description：设置促销商品会员购买数
 * 接口remark：
 */
public final class SetBuyerPromotionNumImpl extends BaseService {
    
    /**
     * 日志对象。
     */
    private Logger logger = Logger.getLogger(SetBuyerPromotionNumImpl.class);
    
    @Override
    public BaseOutputBean doService(BaseInputBean inputBean) throws Exception {
        BaseOutputBean outputBean = new BaseOutputBean();
        try{
            LogUtils4Oms.info(logger, uuid, "执行服务开始，传入bean", "inputBean", inputBean);
            OmsSetBuyerPromotionNumOutputData outputData = new OmsSetBuyerPromotionNumOutputData();
            if (inputBean.getInputData() == null) {
                LogUtils4Oms.info(logger, uuid, "参数req_str为空", "inputBean", inputBean);
                outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.REQUEST_PARAM_ERROR.getResultID(), null, "");
                return outputBean;
            }
            OmsSetBuyerPromotionNumInputData inputData = null;
            try {
                inputData = (OmsSetBuyerPromotionNumInputData)inputBean.getInputData();
            } catch (Exception e) {
                LogUtils4Oms.error(logger, uuid, "接口实现类转换错误", e, "className", OmsSetBuyerPromotionNumInputData.class.getName());
                outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.CLASS_TRANSFER_ERROR.getResultID(), null, "");
                return outputBean;
            }
            
            
            // 登录验证信息
            String userID = inputData.getBuyerId() + "";
            String scope = inputData.getScope();
            if(StringUtil.isEmpty(userID)){
                try {
                    LogUtils4Oms.info(logger, uuid, "setBuyerPromotionNum服务,进行验证！");
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
            
            Long promotionId = inputData.getPromotionId();
            List<OmsSetBuyerPromotionNumGoodsListElementI> goodsListI = inputData.getGoodsList();
            for (OmsSetBuyerPromotionNumGoodsListElementI omsSetBuyerPromotionNumGoodsListElementI : goodsListI) {
                String key = "findChannel-" + userID + "-" + promotionId + "-" + omsSetBuyerPromotionNumGoodsListElementI.getGoodsId();

                List<Object[]> queryCondition = new ArrayList<Object[]>();
                queryCondition.add(new Object[]{"BUYER_ID", "=", userID});
                queryCondition.add(new Object[]{"PROMOTION_ID", "=", promotionId});
                queryCondition.add(new Object[]{"GOODS_ID", "=", omsSetBuyerPromotionNumGoodsListElementI.getGoodsId()});
                List<BuyerPromotionNumPO> buyerNumList = buyerPromotionNumDao.findBuyerPromotionNumPOListByCondition(queryCondition);
                if (buyerNumList == null || buyerNumList.size() <= 0) {
                    BuyerPromotionNumPO buyerPromotionNumPO = new BuyerPromotionNumPO();
                    Long id = buyerPromotionNumDao.findSeqNextVal("SEQ_BUYER_PROMOTION_NUM_PK");
                    buyerPromotionNumPO.setId(id);
                    buyerPromotionNumPO.setBuyerId(Long.valueOf(userID));
                    buyerPromotionNumPO.setPromotionId(promotionId);
                    buyerPromotionNumPO.setGoodsId(omsSetBuyerPromotionNumGoodsListElementI.getGoodsId());
                    buyerPromotionNumPO.setNum(omsSetBuyerPromotionNumGoodsListElementI.getBuyerNum());
                    int resultNum = buyerPromotionNumDao.addBuyerPromotionNumPO(buyerPromotionNumPO);
                    if(resultNum != 1){
                        Log.error(logger, uuid, "插入用户购买促销商品数失败！", null, "buyerPromotionNumPO", buyerPromotionNumPO);
                    }
                    
                    Memcached.set(key, omsSetBuyerPromotionNumGoodsListElementI.getBuyerNum());
                } else {
                    List<Object[]> changeList = new ArrayList<Object[]>();
                    changeList.add(new Object[]{"NUM", omsSetBuyerPromotionNumGoodsListElementI.getBuyerNum(), "+"});
                    List<Object[]> whereList = new ArrayList<Object[]>();
                    whereList.add(new Object[]{"BUYER_ID", "=", userID});
                    whereList.add(new Object[]{"PROMOTION_ID", "=", promotionId});
                    whereList.add(new Object[]{"GOODS_ID", "=", omsSetBuyerPromotionNumGoodsListElementI.getGoodsId()});
                    int resultNum = buyerPromotionNumDao.updateBuyerPromotionNumPOByCondition(changeList, whereList);
                    if(resultNum != 1){
                        Log.error(logger, uuid, "修改用户购买促销商品数失败！", null, "changeList", StringUtil.transferObjectList(changeList), "whereList", StringUtil.transferObjectList(whereList));
                    }
                    
                    Memcached.set(key, buyerNumList.get(0).getNum() + omsSetBuyerPromotionNumGoodsListElementI.getBuyerNum());
                }
            }
            buyerPromotionNumDao.commit();
            
        
            outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.SUCCESS.getResultID(), outputData, scope);
            return outputBean;

        }catch(Exception e){
            LogUtils4Oms.error(logger, uuid, "设置促销商品会员购买数服务出错，返回结果：", e, "outputBean", outputBean);
            outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.INSIDE_ERROR.getResultID(), null, "");
            return outputBean;
        }
    }
    
    @Override
    public BaseService clone() throws CloneNotSupportedException {
        return new SetBuyerPromotionNumImpl();
    }
    
}
/**
 * Title：BuyerBuyScoreService.java<br>
 * Date：2014-11-27 下午5:01:41<br>
 */
package cn.tootoo.soa.oms.getordercommon;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import cn.tootoo.enums.BaseResultEnum;
import cn.tootoo.http.AuthorizeClient;
import cn.tootoo.http.TootooService;
import cn.tootoo.http.bean.BaseOutputBean;
import cn.tootoo.soa.goods.getgoodsinfo.input.GoodsGetGoodsInfoGoodsListElementI;
import cn.tootoo.soa.goods.getgoodsinfo.input.GoodsGetGoodsInfoInputData;
import cn.tootoo.soa.goods.getgoodsinfo.output.GoodsGetGoodsInfoGoodsInfoElementO;
import cn.tootoo.soa.goods.getgoodsinfo.output.GoodsGetGoodsInfoOutputData;
import cn.tootoo.soa.goods.getgoodsinfo.output.GoodsGetGoodsInfoSkuInfoElementO;
import cn.tootoo.soa.oms.utils.LogUtils4Oms;

/**
 * Description:<br>
 * 
 * @author chuan
 * @version 1.0
 */
public class GetOrderCommonService {
    
    private Logger logger;
    
    private String uuid;
    
    public GetOrderCommonService(Logger logger, String uuid) {
        this.logger = logger;
        this.uuid = uuid;
    }
    
    /**
     * 根据父订单orderId集合，取出所有父订单明细和子订单明细里的商品是否可见，放入map
     * Description:<br>
     * 
     * @param orderIdList
     */
    public Map<String, Map> getGoodsInfo(Set<Long> goodsIdSet, String scope, HttpServletRequest httpRequest) {
        Map<String, Map> goodsInfo = new HashMap<String, Map>();
        
        Map<Long, String> goodsCanSaleMap = new HashMap<Long, String>();
        Map<Long, Long> goodsMaxBuyMap = new HashMap<Long, Long>();
        Map<Long, Long> goodsMinBuyMap = new HashMap<Long, Long>();
        try {
            GoodsGetGoodsInfoInputData input = new GoodsGetGoodsInfoInputData();
            input.setScope(scope);
            List<GoodsGetGoodsInfoGoodsListElementI> inputGoodsList = new ArrayList<GoodsGetGoodsInfoGoodsListElementI>();
            for (Long goodsId : goodsIdSet) {
                GoodsGetGoodsInfoGoodsListElementI inputGoods = new GoodsGetGoodsInfoGoodsListElementI();
                inputGoods.setCallType(0);
                inputGoods.setGoodsID(goodsId);
                inputGoodsList.add(inputGoods);
            }
            input.setGoodsList(inputGoodsList);
            input.setWithGoodsDesc("0");
            input.setWithExtendsInfo("0");
            input.setWithSaleCatInfo("0");
            input.setWithSavInfo("0");
            input.setWithPicInfo("0");
            input.setWithSpecialInfo("0");
            input.setWithPriceInfo("0");
            
            HashMap<String, String> params = (HashMap<String, String>)AuthorizeClient.getVerifyInfo(httpRequest);
            Map<String, String> paramsMap = (Map<String, String>)params.clone();
            paramsMap.put("method", "getGoodsInfo");
            paramsMap.put("req_str", input.toJson());
            GoodsGetGoodsInfoOutputData getGoodsInfoOutputData = new GoodsGetGoodsInfoOutputData();
            BaseOutputBean baseBean = TootooService.callServer("goods", paramsMap, "post", getGoodsInfoOutputData);
            if (baseBean.getOutputHead().getResultID().equals(BaseResultEnum.SUCCESS.getResultID())) {
                GoodsGetGoodsInfoOutputData output = (GoodsGetGoodsInfoOutputData)baseBean.getOutputData();
                LogUtils4Oms.info(logger, uuid, "调用获取商品详情接口成功", "input", input.toJson(), "baseBean", baseBean);
                
                List<GoodsGetGoodsInfoGoodsInfoElementO> goodsInfoList = output.getGoodsInfo();
                for (GoodsGetGoodsInfoGoodsInfoElementO goods : goodsInfoList) {
                    // 当商品状态为6上架，并且可见时才可售
                    if ("6".equals(goods.getGoodsStatus()) && "1".equals(goods.getVisibleFlag())) {
                        goodsCanSaleMap.put(goods.getGoodsID(), "1");
                    } else {
                        goodsCanSaleMap.put(goods.getGoodsID(), "0");
                    }
                    
                    GoodsGetGoodsInfoSkuInfoElementO skuInfo = goods.getSkuInfo();
                    Long maxBuyNum = 0L;
                    if (skuInfo.getMaxBuyNum() != null && skuInfo.getMaxBuyNum().compareTo(BigDecimal.ZERO) == 0) {
                        maxBuyNum = 999L;
                    }else if(skuInfo.getMaxBuyNum() != null){
                        maxBuyNum = skuInfo.getMaxBuyNum().longValue();
                    }
                    Long minBuyNum = skuInfo.getMinBuyNum() == null ? 0L : skuInfo.getMinBuyNum().longValue();
                    
                    goodsMaxBuyMap.put(goods.getGoodsID(), maxBuyNum);
                    goodsMinBuyMap.put(goods.getGoodsID(), minBuyNum);
                    
                    goodsInfo.put("goodsCanSaleMap", goodsCanSaleMap);
                    goodsInfo.put("goodsMaxBuyMap", goodsMaxBuyMap);
                    goodsInfo.put("goodsMinBuyMap", goodsMinBuyMap);
                }
            } else {
                LogUtils4Oms.error(logger, uuid, "调用获取商品详情接口失败", "input", input.toJson(), "baseBean", baseBean);
            }
            
        } catch (Exception e) {
            LogUtils4Oms.error(logger, uuid, "调用获取商品详情接口异常", e);
        }
        return goodsInfo;
    }
    
    
}

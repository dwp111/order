package cn.tootoo.soa.oms.createorder;

import cn.tootoo.db.egrocery.tosparentorder.TOsParentOrderDao;
import cn.tootoo.http.bean.BaseInputBean;
import cn.tootoo.http.bean.BaseOutputBean;
import cn.tootoo.soa.address.getaddressbyid.output.AddressGetAddressByIDOutputData;
import cn.tootoo.soa.base.bean.GoodsNumberBean;
import cn.tootoo.soa.base.bean.GoodsWarehouseAndDcBean;
import cn.tootoo.soa.base.util.OrderUtil;
import cn.tootoo.soa.goods.getgoodsinfo.output.GoodsGetGoodsInfoGoodsInfoElementO;
import cn.tootoo.soa.oms.createorder.input.OmsCreateOrderInputData;
import cn.tootoo.soa.oms.createorder.output.OmsCreateOrderOutputData;
import cn.tootoo.soa.promotion.getpromotionbyid.output.PromotionGetPromotionByIdDetailElementO;
import cn.tootoo.soa.promotion.getpromotioninfo.output.PromotionGetPromotionInfoOutputData;
import cn.tootoo.soa.shipping.getgoodsshippinginfos.output.ShippingGetGoodsShippingInfosShippingRulesElementO;
import cn.tootoo.utils.DateUtil;
import cn.tootoo.utils.Log;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;

/**
 * 电子卡订单Service
 *
 * @author author
 */
public class DianZiKaCreateOrderService extends CreateOrderBaseService {

    public DianZiKaCreateOrderService(String uuid, Logger logger) {
        super(uuid, logger);
    }

    public BaseOutputBean createNormalOrder(
            HttpServletRequest request,
            BaseInputBean inputBean, OmsCreateOrderInputData inputData,
            BaseOutputBean outputBean, OmsCreateOrderOutputData outputData,
            TOsParentOrderDao parentOrderDao,
            Map<String, BigDecimal> discountMap, Map<String, BigDecimal> otherDiscountMap,
            Map<Long, PromotionGetPromotionByIdDetailElementO> groupMap,
            HashMap<String, String> params, HashMap<Long, GoodsNumberBean> goodsCount,
            boolean canReserve, Map<Long, GoodsGetGoodsInfoGoodsInfoElementO> goodsInfo,
            String isIncludeSpecial, String isCheckCOD, boolean isNeedGift, Long giftGoodsId,
            String couponSN, int giftGoodsNum, Long payMethodId, boolean haveOnlinePay,
            Map<String, String> buyFromMap,
            StringBuffer goodsIDMax, List<Long> redemptionList
    ) throws Exception {


        AddressGetAddressByIDOutputData addressOutputData = null;

        Map<Long, Map<Long, List<ShippingGetGoodsShippingInfosShippingRulesElementO>>> goodsWarehouseRule = new HashMap<Long, Map<Long, List<ShippingGetGoodsShippingInfosShippingRulesElementO>>>();

        String receiveDate = DateUtil.dateToFormatStr(new Date(), "yyyy-MM-dd");
        Log.info(logger, uuid, "电子卡收货日期是当前时间","receiveDate",receiveDate);

        outputData.setSendDate(receiveDate);

        Map<Long, Set<GoodsWarehouseAndDcBean>> goodsWarehouseDc = OrderUtil.getWarehouseAndDcList(false, goodsWarehouseRule, isCheckCOD, receiveDate);
        Log.info(logger, uuid, "根据确定的日期和是否选择了COD,找出所有的库房和配送公司组合", "goodsWarehouseDc", goodsWarehouseDc);

        Map<Long, GoodsWarehouseAndDcBean> goodsWarehouseAndDcBean = OrderUtil.getWarehouseAndDc(goodsWarehouseDc);
        Log.info(logger, uuid, "按照最少拆单原则,指定商品的库房和配送公司开始", "goodsWarehouseAndDcBean", goodsWarehouseAndDcBean);

        // 商品在本库房本配送公司下，是否考虑运力
        Map<Long, String> goodsShipCapacityMap = new HashMap<Long, String>();


        /****************************************************************************** 调用促销服务开始 ********************************************************************************************/
        PromotionGetPromotionInfoOutputData promotionOutputData = null;
        /****************************************************************************** 调用促销服务结束 ********************************************************************************************/

        BigDecimal orderFee = getOrderFee(inputData, promotionOutputData, goodsCount, goodsInfo, groupMap);
        Log.info(logger, uuid, "获得订单的订单金额", "orderFee", orderFee);


        BigDecimal shipFee = BigDecimal.ZERO;
        BigDecimal shipTotalFee = BigDecimal.ZERO;
        BigDecimal deliveryTimeFee = BigDecimal.ZERO;

        /****************************************************************************** 调用运费服务开始 ********************************************************************************************/

        /****************************************************************************** 调用运费服务结束 ********************************************************************************************/
        BaseOutputBean outputBeanNomal = createOrder(request, inputBean, inputData, outputBean, outputData, params, goodsCount, groupMap, goodsInfo, buyFromMap, goodsWarehouseAndDcBean, goodsShipCapacityMap, discountMap, otherDiscountMap, addressOutputData, promotionOutputData, parentOrderDao, new StringBuffer(""), goodsIDMax, isIncludeSpecial, isCheckCOD, couponSN, receiveDate, BigDecimal.ZERO, shipFee, shipTotalFee, deliveryTimeFee, BigDecimal.ZERO, payMethodId, canReserve, haveOnlinePay, false, null, null);

        return outputBeanNomal;

    }

}

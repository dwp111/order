package cn.tootoo.soa.oms.getbuyeridbyorder;

import java.util.Map;

import org.apache.log4j.Logger;

import cn.tootoo.db.egrocery.tosparentorder.TOsParentOrderDao;
import cn.tootoo.db.egrocery.tosparentorder.TOsParentOrderPO;
import cn.tootoo.enums.BaseResultEnum;
import cn.tootoo.http.AuthorizeClient;
import cn.tootoo.http.bean.BaseInputBean;
import cn.tootoo.http.bean.BaseOutputBean;
import cn.tootoo.servlet.BaseService;
import cn.tootoo.soa.base.enums.BaseOrderResultEnum;
import cn.tootoo.soa.oms.getbuyeridbyorder.input.OmsGetBuyerIDByOrderInputData;
import cn.tootoo.soa.oms.getbuyeridbyorder.output.OmsGetBuyerIDByOrderOutputData;
import cn.tootoo.soa.oms.getbuyeridbyorder.output.OmsGetBuyerIDByOrderResultEnum;
import cn.tootoo.soa.oms.utils.LogUtils4Oms;
import cn.tootoo.utils.ResponseUtil;

/**
 * 服务委托者实现类。
 * 
 * 服务description：订单服务。
 * 服务remark：订单服务。
 * 接口description：根据负订单id获得用户id
 * 接口remark：
 */
public final class GetBuyerIDByOrderImpl  extends BaseService {
	
	private Logger logger = Logger.getLogger(this.getClass());
	
    @Override
    public BaseOutputBean doService(BaseInputBean inputBean) throws Exception {
    	LogUtils4Oms.info(logger, uuid, "根据负订单id获得用户id开始！");
        BaseOutputBean outputBean = new BaseOutputBean();
        OmsGetBuyerIDByOrderOutputData outputData = new OmsGetBuyerIDByOrderOutputData();
        //验证是否登录
        OmsGetBuyerIDByOrderInputData inputData = null;
        
        try {
            inputData = (OmsGetBuyerIDByOrderInputData)inputBean.getInputData();
        } catch (Exception e) {
        	LogUtils4Oms.error(logger,"uuid","接口实现类转换错误", e, "className", OmsGetBuyerIDByOrderInputData.class.getName());
            outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.CLASS_TRANSFER_ERROR.getResultID(), null, "");
            LogUtils4Oms.info(logger, uuid, "接口返回", "outputBean", outputBean);
            return outputBean;
        }
        Long orderID = inputData.getOrderID();
        if(orderID==null){
        	LogUtils4Oms.info(logger,"uuid","参数错误！输入的orderID为空！");
            outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.REQUEST_PARAM_ERROR.getResultID(), null, "");
            return outputBean;
        }
        
        //验证是否登录
        String userID = "";
        request.setAttribute(AuthorizeClient.ATTRIB_NEED_AUTH, AuthorizeClient.NEED_AUTH_YES);
        request.setAttribute(AuthorizeClient.ATTRIB_CHECK_LEVEL, AuthorizeClient.AUTH_EASY_LEVEL);
        Map tempMap = AuthorizeClient.getVerifyInfo(request);
        if(tempMap!=null){
        	userID = tempMap.get(AuthorizeClient.COOKIE_BUYER_ID) + "";
        }
        
        try {
            LogUtils4Oms.info(logger, uuid, "开始验证！");
            tempMap.put(AuthorizeClient.COOKIE_SCOPE, inputData.getScope());
            
            if (AuthorizeClient.CHECK_OK !=AuthorizeClient.verifySession(tempMap)) {
                outputBean = ResponseUtil.getBaseOutputBean(BaseOrderResultEnum.NOT_LOGIN.getResultID(), null, inputData.getScope());
                LogUtils4Oms.info(logger, uuid, "验证失败","tempMap",tempMap.toString());
                return outputBean;
            }
            LogUtils4Oms.info(logger, uuid, "验证通过!","userID",userID);
        }catch (Exception e) {
        	outputBean = ResponseUtil.getBaseOutputBean(BaseOrderResultEnum.NOT_LOGIN.getResultID(), null, inputData.getScope());
            LogUtils4Oms.info(logger, uuid, "验证失败","tempMap",tempMap.toString());
            return outputBean;
        }
        
       try {
    	   //查询父订单表，得到buyerID
           TOsParentOrderDao pOrderDao = new TOsParentOrderDao();
           TOsParentOrderPO  orderPO = pOrderDao.findTOsParentOrderPOByID(orderID);
           if(null==orderPO){
           	LogUtils4Oms.info(logger,"uuid","根据orderID未查找到订单！","inputBean",inputBean);
               outputBean = ResponseUtil.getBaseOutputBean(OmsGetBuyerIDByOrderResultEnum.ORDER_INEXIST.getResultID(), null, "");
               return outputBean;
           }
           LogUtils4Oms.info(logger, uuid, "查到的父订单信息！","order_id",inputData.getOrderID(),"buyer_id",orderPO.getBuyerId());
           outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.SUCCESS.getResultID(), null, "");
           outputData.setBuyerID(orderPO.getBuyerId());
           outputBean.setOutputData(outputData);
           LogUtils4Oms.info(logger, uuid, "根据负订单id获得用户id完成！");
           return outputBean;
		} catch (Exception e) {
			LogUtils4Oms.info(logger, uuid, "根据负订单id获得用户id失败！", "inputBean", inputBean);
            outputBean = ResponseUtil.getBaseOutputBean(OmsGetBuyerIDByOrderResultEnum.GET_BUYERID_BY_ORDER_FAIL.getResultID(), null, "");
            return outputBean;
		}
        
    }
    
    @Override
    public BaseService clone() throws CloneNotSupportedException {
        return new GetBuyerIDByOrderImpl();
    }
    
}
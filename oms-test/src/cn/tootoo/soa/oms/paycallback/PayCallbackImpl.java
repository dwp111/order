package cn.tootoo.soa.oms.paycallback;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import cn.tootoo.db.egrocery.ospayplan.OsPayPlanDao;
import cn.tootoo.db.egrocery.sppaymethod.SpPayMethodDao;
import cn.tootoo.db.egrocery.tosparentorder.TOsParentOrderDao;
import cn.tootoo.db.egrocery.tosparentorder.TOsParentOrderPO;
import cn.tootoo.db.egrocery.tosparentorderitems.TOsParentOrderItemsDao;
import cn.tootoo.db.egrocery.tosparentorderopt.TOsParentOrderOptDao;
import cn.tootoo.enums.BaseResultEnum;
import cn.tootoo.http.bean.BaseInputBean;
import cn.tootoo.http.bean.BaseOutputBean;
import cn.tootoo.servlet.BaseService;
import cn.tootoo.soa.base.global.DictionaryData;
import cn.tootoo.soa.base.util.LockUtil;
import cn.tootoo.soa.base.util.PayCallbackUtil;
import cn.tootoo.soa.oms.paycallback.input.OmsPayCallbackInputData;
import cn.tootoo.soa.oms.paycallback.output.OmsPayCallbackResultEnum;
import cn.tootoo.utils.Log;
import cn.tootoo.utils.ResponseUtil;
import cn.tootoo.utils.StringUtil;

public class PayCallbackImpl extends BaseService {
    
    private Logger logger = Logger.getLogger(this.getClass());
    
    @Override
    public BaseOutputBean doService(BaseInputBean inputBean) throws Exception {
        
        BaseOutputBean outputBean = new BaseOutputBean();
        
        try{
            
            /************************************************************************ 接口本身验证开始 ************************************************************************************/
            Log.info(logger, uuid, "执行服务开始，传入bean", "inputBean", inputBean);
            if (inputBean.getInputData() == null) {
                Log.info(logger, uuid, "参数req_str为空", "inputBean", inputBean);
                outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.REQUEST_PARAM_ERROR.getResultID(), null, "");
                Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
                return outputBean;
            }
            
            OmsPayCallbackInputData inputData = null;
            
            try {
                inputData = (OmsPayCallbackInputData)inputBean.getInputData();
            } catch (Exception e) {
                Log.error(logger, "接口实现类转换错误", e, "className", OmsPayCallbackInputData.class.getName());
                outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.CLASS_TRANSFER_ERROR.getResultID(), null, "");
                Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
                return outputBean;
            }
            /************************************************************************ 接口本身验证结束 ************************************************************************************/
            
            /************************************************************************** 查询数据开始 **************************************************************************************/
            TOsParentOrderDao parentOrderDao = new TOsParentOrderDao(uuid, logger);
            TOsParentOrderOptDao optDao = new TOsParentOrderOptDao(uuid, logger);
            TOsParentOrderItemsDao parentOrderItemDao = new TOsParentOrderItemsDao(uuid, logger);
            SpPayMethodDao payMethodDao = new SpPayMethodDao(uuid, logger);
            OsPayPlanDao osPayPlanDao = new OsPayPlanDao(uuid, logger);
            
            Log.info(logger, uuid, "查询订单开始", "orderId", inputData.getOrderID(), "buyerId", inputData.getBuyerID());
            List<Object[]> selectCondition = new ArrayList<Object[]>();
            Object[] orderIdObj = {"ORDER_ID", "=", inputData.getOrderID()};
            selectCondition.add(orderIdObj);
            Object[] buyIdObj = {"BUYER_ID", "=", inputData.getBuyerID()};
            selectCondition.add(buyIdObj);
            Log.info(logger, uuid, "获得条件", "selectCondition", StringUtil.transferObjectList(selectCondition));
            
            List<TOsParentOrderPO> parentOrderList = parentOrderDao.findTOsParentOrderPOListByCondition(selectCondition, false, false, false);
            Log.info(logger, uuid, "查询订单结束", "orderId", inputData.getOrderID(), "buyerId", inputData.getBuyerID(), "parentOrderList", parentOrderList);
            
            if (parentOrderList == null || parentOrderList.isEmpty()
                            || parentOrderList.size() != 1) {
                outputBean = ResponseUtil.getBaseOutputBean(OmsPayCallbackResultEnum.NO_ORDER.getResultID(), null, "");
                Log.info(logger, uuid, "无此订单信息,接口返回", "outputBean", outputBean);
                return outputBean;
            }
            
            /************************************************************************** 查询数据结束 **************************************************************************************/
            
            /**************************************************************************** 加锁开始 ****************************************************************************************/
            try {
                Log.info(logger, uuid, "锁表", "orderCode", parentOrderList.get(0).getOrderCode());
                boolean lock = LockUtil.lock(logger, uuid, parentOrderList.get(0).getOrderCode(), 1, "支付回调");
                if (!lock) {
                    Log.info(logger, uuid, "加锁失败", "orderCode", parentOrderList.get(0).getOrderCode());
                    outputBean = ResponseUtil.getBaseOutputBean(OmsPayCallbackResultEnum.ORDER_BUSY.getResultID(), null, "");
                    Log.info(logger, uuid, "加锁失败,接口返回", "orderCode", parentOrderList.get(0).getOrderCode(), "outputBean", outputBean);
                    return outputBean;
                }
            } catch (Exception e1) {
                Log.error(logger, "加锁失败", e1, "orderCode", parentOrderList.get(0).getOrderCode());
                outputBean = ResponseUtil.getBaseOutputBean(OmsPayCallbackResultEnum.ORDER_BUSY.getResultID(), null, "");
                Log.info(logger, uuid, "加锁失败,接口返回", "orderCode", parentOrderList.get(0).getOrderCode(), "outputBean", outputBean);
                return outputBean;
            }
            /**************************************************************************** 加锁结束 ****************************************************************************************/
            
            /************************************************************************** 业务处理开始 **************************************************************************************/
            try {
                
                StringBuffer sb = new StringBuffer("");
                
                outputBean = PayCallbackUtil.doPay(logger, uuid, inputData.getOrderID(), inputData.getBuyerID(), sb, inputData.getPayDetail(), DictionaryData.payMethodPidMap, inputData.getPayStatus(), inputData.getActualFee(), parentOrderDao, optDao, payMethodDao, osPayPlanDao, parentOrderItemDao);
                if (outputBean != null && !sb.toString().equals("ignore")) {
                    parentOrderDao.rollback();
                    return outputBean;
                }
                parentOrderDao.commit();
                
                if (sb.equals("part")) {// 部分支付
                    outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.SUCCESS.getResultID(), null, "");
                    Log.info(logger, uuid, "操作成功,返回部分支付成功,接口返回", "orderCode", parentOrderList.get(0).getOrderCode(), "outputBean", outputBean);
                    return outputBean;
                }
                // 支付成功或支付失败
                outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.SUCCESS.getResultID(), null, "");
                Log.info(logger, uuid, "操作成功(不需要继续发),接口返回", "orderCode", parentOrderList.get(0).getOrderCode(), "outputBean", outputBean);
                return outputBean;
                
            } catch (Exception e) {
                Log.error(logger, "服务内部错误", e);
                parentOrderDao.rollback();
                outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.INSIDE_ERROR.getResultID(), null, "");
                return outputBean;
            } finally {
                LockUtil.unlock(logger, uuid, parentOrderList.get(0).getOrderCode());
            }
            /************************************************************************** 业务处理结束 **************************************************************************************/
            
        }catch(Exception e){
            Log.error(logger, uuid, "支付回调出错", e);
            outputBean = ResponseUtil.getBaseOutputBean(BaseResultEnum.INSIDE_ERROR.getResultID(), null, "");
            Log.info(logger, uuid, "接口返回", "outputBean", outputBean);
            return outputBean;
        }
        
    }
    
    @Override
    public BaseService clone() throws CloneNotSupportedException {
        return new PayCallbackImpl();
    }
    
}

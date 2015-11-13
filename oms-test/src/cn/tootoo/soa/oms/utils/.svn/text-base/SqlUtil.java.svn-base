/**
 * Title：OmsUtil.java<br>
 * Date：2014-10-21 下午4:40:48<br>
 */
package cn.tootoo.soa.oms.utils;

import java.sql.CallableStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import oracle.jdbc.OracleTypes;

import org.apache.log4j.Logger;

import cn.tootoo.db.DAORowMapper;
import cn.tootoo.db.JdbcUtil;
import cn.tootoo.soa.promotion.getpromotioninfo.output.PromotionGetPromotionInfoGoodsListElementO;
import cn.tootoo.utils.DateUtil;
import cn.tootoo.utils.Log;
import cn.tootoo.utils.Memcached;
import cn.tootoo.utils.StringUtil;

/**
 * Description:<br>
 * 
 * @author nekohy
 * @version 1.0
 */
public class SqlUtil {
    
    @SuppressWarnings("static-access")
    public static String getParentOrderCodeLoop(Logger logger, String uuid, String connectionName) throws Exception {
        String orderCode = "";
        for (int i = 0; i < 6; i++) {
            orderCode = getParentOrderCode(logger, uuid, connectionName);
            if (!StringUtil.isEmpty(orderCode) && !orderCode.equals("null")) {
                if (i > 0) Log.info(logger, uuid, "获取父订单orderCode次数", "count", i);
                return orderCode;
            }
            Thread.currentThread().sleep(150);
        }
        Log.info(logger, uuid, "获取父订单6次仍然失败");
        return "";
    }
    
    @SuppressWarnings("static-access")
    public static String getOsOrderCodeLoop(Logger logger, String uuid, String connectionName) throws Exception {
        String orderCode = "";
        for (int i = 0; i < 6; i++) {
            orderCode = getOsOrderCode(logger, uuid, connectionName);
            if (!StringUtil.isEmpty(orderCode) && !orderCode.equals("null")) {
                if (i > 0) Log.info(logger, uuid, "获取子订单orderCode次数", "count", i);
                return orderCode;
            }
            Thread.currentThread().sleep(150);
        }
        Log.info(logger, uuid, "获取子订单6次仍然失败");
        return "";
    }
    
    private static String getParentOrderCode(Logger logger, String uuid, String connectionName) throws Exception {
        
        CallableStatement call;
        String code = "";
        String err = "";
        try {
            call = JdbcUtil.getConnection(connectionName).prepareCall("{call gen_order_code_package.get_order_code(?, ?)}");
            call.registerOutParameter(1, OracleTypes.VARCHAR);
            call.registerOutParameter(2, OracleTypes.VARCHAR);
            call.execute();
            code = call.getString(1);
            err = call.getString(2);
            if (!(err == null || err.trim().equals("null") || err.trim().equals(""))) { return ""; }
        } catch (SQLException e) {
            Log.error(logger, uuid, "调用存储过程失败,call gen_order_code_package.get_order_code", e, "error", err);
            return "";
        }
        return code;
        
    }
    
    private static String getOsOrderCode(Logger logger, String uuid, String connectionName) throws Exception {
        
        CallableStatement call;
        String code = "";
        String err = "";
        try {
            call = JdbcUtil.getConnection(connectionName).prepareCall("{call gen_order_code_package.get_order_code_child(?, ?)}");
            call.registerOutParameter(1, OracleTypes.VARCHAR);
            call.registerOutParameter(2, OracleTypes.VARCHAR);
            call.execute();
            code = call.getString(1);
            err = call.getString(2);
            if (!(err == null || err.trim().equals("null") || err.trim().equals(""))) { return ""; }
        } catch (SQLException e) {
            Log.error(logger, uuid, "调用存储过程失败,call gen_order_code_package.get_order_code_child", e, "error", err);
            return "";
        }
        return code;
        
    }
    
    public static boolean checkUserOrder(Logger logger, String uuid, String connectionName, String buyerId) {
        
        String sql = "select count(0) as num,min(sysdate-create_dt)*24*60*60 as dou from t_os_parent_order t "
                        + " where sysdate-create_dt < 1/24 and order_type not in ('e','f','g','h') and order_from not in ('5','7','8','d','j','k')"
                        + " and buyer_id = " + buyerId;
        Log.info(logger, uuid, "验证用户sql", "sql", sql);
        List<CheckOrderBean> list = JdbcUtil.executeQuery(connectionName, sql, new DAORowMapper<CheckOrderBean>(CheckOrderBean.class));
        Log.info(logger, uuid, "获得结果", "list", list);
        if (list != null && list.size() > 0) {
            CheckOrderBean bean = list.get(0);
            if (bean.getDou() == null) { return true; }
            if (bean.getNum() > 20 || bean.getDou() < 30) { return false; }
        }
        return true; 
        
    }
    
    
    public static List<Long> getFlushBuyPromotionId(Logger logger, String uuid, String connectionName) {
        
        String sql = "select lot_sn from tmp_draw_active where active_id = 105 AND status = '1'";
        Log.info(logger, uuid, "验证用户sql", "sql", sql);
        List<FlushBuyPromotionBean> list = JdbcUtil.executeQuery(connectionName, sql, new DAORowMapper<FlushBuyPromotionBean>(FlushBuyPromotionBean.class));
        Log.info(logger, uuid, "获得结果", "list", list);
        
        List<Long> promotionIdList = new ArrayList<Long>();
        if (list != null && list.size() > 0) {
        	for(FlushBuyPromotionBean bean : list){
        		promotionIdList.add(bean.getLotSn());
        	}
        }
        
        return promotionIdList;
        
    }
    
    
    
    public static boolean checkUserMobile(Logger logger, String uuid, String connectionName, String buyerId, StringBuffer mobile){
        
        //insert into TTS_SPECIAL_GROUPBUY values(1,1)
        
        String sql = "select mobile from bs_buyer where BUYER_ID = " + buyerId;
        Log.info(logger, uuid, "执行sql", "sql", sql);
        List<CheckUserBean> list = JdbcUtil.executeQuery(connectionName, sql, new DAORowMapper<CheckUserBean>(CheckUserBean.class));
        Log.info(logger, uuid, "返回结果", "list", list);
        if(list!=null && list.size()>0){
            CheckUserBean bean = list.get(0);
            if(!StringUtil.isEmpty(bean.getMobile())){
                mobile.append(bean.getMobile());
                return true;
            }
        }
        
        return false;
        
    }
    
    
    
    public static boolean checkUserGroup(Logger logger, String uuid, String connectionName, String buyerId, Long groupPromotionId){
        
        //insert into TTS_SPECIAL_GROUPBUY values(1,1)
        
        String sql = "select count(*) as num from TTS_SPECIAL_GROUPBUY where BS_BUYER_ID = " + buyerId;
        List<CheckOrderBean> list = JdbcUtil.executeQuery(connectionName, sql, new DAORowMapper<CheckOrderBean>(CheckOrderBean.class));
        
        if(list!=null && list.size()>0){
            CheckOrderBean bean = list.get(0);
            if(bean.getNum().intValue()==0){
                return true;
            }
        }
        
        return false;
        
    }
    
    
    public static boolean insertSpecialGroupBuy(Logger logger, String uuid, String connectionName, String buyerId, Long groupPromotionId){
        
        String sqlInsert = "INSERT INTO TTS_SPECIAL_GROUPBUY(BS_BUYER_ID, GROUP_PROMOTION_ID) values (?,?)";
        List<Object> list = new ArrayList<Object>();
        list.add(Integer.valueOf(buyerId));
        list.add(groupPromotionId);
        int count = JdbcUtil.executeUpdate(connectionName, sqlInsert, list.toArray());
        if(count!=1){
            return false;
        }

        return true;
    }
    
    /**
     * 修改私享团的下单量 
     * @return
     */
    public static boolean updatePrivateTeamOrderNum(Logger logger, String uuid, String connectionName, Long teamId){
        String sqlUpdate = "UPDATE TTS_PRIVATE_TEAM SET  ORDER_NUM=ORDER_NUM+1 WHERE TEAM_ID="+teamId;
        Log.info(logger, uuid, "执行sql", "sql", sqlUpdate);
        int count = JdbcUtil.executeUpdate(connectionName, sqlUpdate);
        if(count!=1){
            return false;
        }
        Log.info(logger, uuid, "执行sql count", "count", count);
        return true;
    }
    
    /**
     * //新增秒抢商品只能购买一件校验        
     * @return
     */
    public static int checkPromotionInfoGoodsList(String buyerId, Logger logger, String uuid, String connectionName, List<PromotionGetPromotionInfoGoodsListElementO> promotionInfoGoodsList){
    	
    	Date begin = DateUtil.strToDateTime("2015-04-23 00:00:00");
    	Date end = DateUtil.strToDateTime("2015-05-03 23:59:59");
		long now = DateUtil.getNow();
		if (end.getTime()<now||begin.getTime()>now){
			Log.info(logger, uuid, "执行秒抢商品只能购买一件校验 时间不在","end",end,"begin",begin);				
			return 0;
		}
		
    	if(promotionInfoGoodsList==null||promotionInfoGoodsList.size()==0){
    		Log.info(logger, uuid, "执行秒抢商品只能购买一件校验 promotionInfoGoodsList","promotionInfoGoodsList" ,promotionInfoGoodsList);
    		return 0;
        }
    	 
    	
    	List<Long> allPromotionId = new ArrayList<Long>();
    	Map<Long, List<GoodsCountBean>> promotionMap = new HashMap<Long, List<GoodsCountBean>>();
    	for(PromotionGetPromotionInfoGoodsListElementO promotionElement : promotionInfoGoodsList){
    		if(promotionElement.getPromotionId()!=null && promotionElement.getPromotionId().intValue() != 0){
    			allPromotionId.add(promotionElement.getPromotionId());
    			
    			if(promotionMap.containsKey(promotionElement.getPromotionId())){
    				promotionMap.get(promotionElement.getPromotionId()).add(new GoodsCountBean(promotionElement.getGoodsId(), promotionElement.getAmount()));
    			}else{
    				List<GoodsCountBean> goodsCountBeanList = new ArrayList<GoodsCountBean>();
    				goodsCountBeanList.add(new GoodsCountBean(promotionElement.getGoodsId(), promotionElement.getAmount()));
    				promotionMap.put(promotionElement.getPromotionId(), goodsCountBeanList);
    			}
    		}
    	}
    	Log.info(logger, uuid, "获得所有商品的促销ID", "allPromotionId", allPromotionId);
    	
    	List<Long> promotionIds = getFlushBuyPromotionId(logger, uuid, connectionName);
    	Log.info(logger, uuid, "获得所有秒抢活动中的促销ID", "promotionIds", promotionIds);
    	
    	allPromotionId.retainAll(promotionIds);
    	Log.info(logger, uuid, "获得有交集的促销ID", "retainPromotionId", allPromotionId);
    	
    	if(allPromotionId.isEmpty()){
    		Log.info(logger, uuid, "没有交集");
    		return 0;
    	}
    	
    	List<Long> goodsIdList = new ArrayList<Long>();
    	for(Long promotionId : allPromotionId){
    		for(GoodsCountBean bean : promotionMap.get(promotionId)){
    			for(int i = 0; i< bean.getCount(); i++){
    				goodsIdList.add(bean.getGoodsId());
    			}
    		}
    	}
    	
    	if(goodsIdList.size()>1){
    		Log.info(logger, uuid, "秒抢活动购买了多个商品或者同个商品购买了多个", "goodsIdList", goodsIdList);
    		return -2;
    	}
    	if(Memcached.getOK("flush"+buyerId)!=null && Memcached.getOK("flush"+buyerId).equals("flushBuy")){
    		Log.info(logger, uuid, "本用户已经购买过秒抢活动商品", "buyerId", buyerId);
    		return -1;
    	}
        
        return 1;
    }

}



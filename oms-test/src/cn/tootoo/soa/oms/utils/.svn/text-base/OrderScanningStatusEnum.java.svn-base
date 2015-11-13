package cn.tootoo.soa.oms.utils;

/**
 * Created by Liu Yong
 * Desp:
 * Date: 2010-1-19
 * Time: 15:08:51
 */
public enum OrderScanningStatusEnum {
    NONE("0","未扫描"),PART("1","部分扫描"),FINISH("2","扫描完成");

    OrderScanningStatusEnum(String c, String s) {
        this.c = c;
        this.s = s;
    }

    private final String c;
    private final String s;

    public String getC() {
        return c;
    }

    public String getS() {
        return s;
    }

    public static OrderScanningStatusEnum get(String c) {
        OrderScanningStatusEnum brandStatusEnum = null;

        for(OrderScanningStatusEnum o : OrderScanningStatusEnum.values()) {
            if(c.equals(o.c)) brandStatusEnum = o;
        }

        return brandStatusEnum;
    }
}

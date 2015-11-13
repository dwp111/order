package cn.tootoo.soa.oms.getorderlist.delegate;

/**
 * 服务委托者工厂。
 * 
 * 服务description：订单服务。
 * 服务remark：订单服务。
 * 接口description：获取订单列表
 * 接口remark：以下几点在此统一说明，不一一标注了 ：   一、如果字段没有取值，则取值null，但保留key  二、布尔值字段格式为：1.是 0.否。
 */
public final class GetOrderListDelegateFactory {
    
    /**
     * 服务委托者原型对象。
     */
    private static AbstractGetOrderListDelegate prototype = null;
    
    /**
     * 服务委托者原型注册方法。
     */
    public static final void registPrototype(AbstractGetOrderListDelegate inPrototype) {
        if (inPrototype == null) {
            throw new RuntimeException("待注册的服务委托原型对象为空！");
        }
        if (prototype != null) {
            throw new RuntimeException("服务委托原型对象已被注册，不可重复注册。已注册的原型：" + prototype.getClass().getName() + "；待注册的原型：" + inPrototype.getClass().getName());
        }
        prototype = inPrototype;
    }
    
    /**
     * 根据服务委托者原型生成新的服务委托者对象。
     */
    public static final AbstractGetOrderListDelegate createDelegate() {
        if (prototype == null) {
            throw new RuntimeException("工厂中的服务委托原型对象为空！");
        }
        try {
            return prototype.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("工厂中的服务委托原型对象不可以克隆新对象！");
        }
    }
}

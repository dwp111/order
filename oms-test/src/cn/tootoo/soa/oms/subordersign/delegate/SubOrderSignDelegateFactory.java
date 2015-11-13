package cn.tootoo.soa.oms.subordersign.delegate;

/**
 * 服务委托者工厂。
 * 
 * 服务description：订单服务。
 * 服务remark：订单服务。
 * 接口description：子订单签收
 * 接口remark：
 */
public final class SubOrderSignDelegateFactory {
    
    /**
     * 服务委托者原型对象。
     */
    private static AbstractSubOrderSignDelegate prototype = null;
    
    /**
     * 服务委托者原型注册方法。
     */
    public static final void registPrototype(AbstractSubOrderSignDelegate inPrototype) {
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
    public static final AbstractSubOrderSignDelegate createDelegate() {
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

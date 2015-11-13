package cn.tootoo.soa.oms.utils;

import java.math.BigDecimal;

public class CalVO {
	private BigDecimal price = BigDecimal.ZERO;//单价
	private BigDecimal number = BigDecimal.ZERO;//数量
	private BigDecimal unitWeight = BigDecimal.ZERO;//售卖重量
	private BigDecimal goodsWeight = BigDecimal.ZERO;//计价重量
	private BigDecimal totalGoodsItemAmount = BigDecimal.ZERO;//所有明细总额
	private BigDecimal goodsAmount = BigDecimal.ZERO;//总额
	private BigDecimal goodsItemAmount = BigDecimal.ZERO;//单个商品总额
	private String type;//标品/非标品
	private String calculateType;//计算公式类型
	
	
	public BigDecimal getPrice() {
		return price;
	}
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	public BigDecimal getNumber() {
		return number;
	}
	public void setNumber(BigDecimal number) {
		this.number = number;
	}
	public BigDecimal getUnitWeight() {
		return unitWeight;
	}
	public void setUnitWeight(BigDecimal unitWeight) {
		this.unitWeight = unitWeight;
	}
	public BigDecimal getGoodsWeight() {
		return goodsWeight;
	}
	public void setGoodsWeight(BigDecimal goodsWeight) {
		this.goodsWeight = goodsWeight;
	}
	public BigDecimal getTotalGoodsItemAmount() {
		return totalGoodsItemAmount;
	}
	public void setTotalGoodsItemAmount(BigDecimal totalGoodsItemAmount) {
		this.totalGoodsItemAmount = totalGoodsItemAmount;
	}
	public BigDecimal getGoodsAmount() {
		return goodsAmount;
	}
	public void setGoodsAmount(BigDecimal goodsAmount) {
		this.goodsAmount = goodsAmount;
	}
	public BigDecimal getGoodsItemAmount() {
		return goodsItemAmount;
	}
	public void setGoodsItemAmount(BigDecimal goodsItemAmount) {
		this.goodsItemAmount = goodsItemAmount;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getCalculateType() {
		return calculateType;
	}
	public void setCalculateType(String calculateType) {
		this.calculateType = calculateType;
	}
	
}

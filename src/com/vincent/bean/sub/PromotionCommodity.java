package com.vincent.bean.sub;

/**
 * 与 product区分，用来表示 服务器保存的数据格式
 * 
 * @author WenSen
 * @date 2018年9月12日 下午6:07:50
 *
 */
public class PromotionCommodity {

	private String name;

	private String code;

	public PromotionCommodity() {
		super();
	}

	public PromotionCommodity(String name, String code) {
		super();
		this.name = name;
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

}

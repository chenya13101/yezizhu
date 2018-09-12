package com.vincent.workflow;

import java.util.List;

import com.vincent.DataFactory;
import com.vincent.bean.Coupon;

public class WorkFlow {

	List<WorkStep> workSteps;

	public static void main(String[] args) {
		List<Coupon> couponList = DataFactory.getCoupons();
		if (couponList == null)
			return;

		//List<Product> productList = DataFactory.getProducts();
		WorkFlow workFlow = new WorkFlow();

		workFlow.start();
		workFlow.showResult();
	}

	private void showResult() {
		// TODO Auto-generated method stub
		
	}

	public void start() {
		this.getWorkSteps().get(0).run();
	}

	public List<WorkStep> getWorkSteps() {
		return workSteps;
	}

	public void setWorkSteps(List<WorkStep> workSteps) {
		this.workSteps = workSteps;
	}

}

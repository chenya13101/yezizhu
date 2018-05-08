package com.vincent.workflow;

import java.util.List;

import com.vincent.bean.CalculateUnit;
import com.vincent.bean.Condition;
import com.vincent.common.ResultMessage;

public class WorkStep {

	private Condition condition;

	private List<CalculateUnit> calculateUnits;

	private WorkStep nextStep;

	private WorkStep previousStep;

	public WorkStep getNextStep() {
		return nextStep;
	}

	public void setNextStep(WorkStep nextStep) {
		this.nextStep = nextStep;
	}

	public WorkStep getPreviousStep() {
		return previousStep;
	}

	public void setPreviousStep(WorkStep previousStep) {
		this.previousStep = previousStep;
	}

	public Condition getCondition() {
		return condition;
	}

	public void setCondition(Condition condition) {
		this.condition = condition;
	}

	public List<CalculateUnit> getCalculateUnits() {
		return calculateUnits;
	}

	public void setCalculateUnits(List<CalculateUnit> calculateUnits) {
		this.calculateUnits = calculateUnits;
	}

	private int test = 0;

	private ResultMessage check() {
		ResultMessage message = new ResultMessage();
		boolean success = test++ % 2 == 0;
		message.setSuccess(success);
		if (!success) {

		}

		return message;
	}

	private void work() {
		// TODO 除了condition还有正常的业务运算
		if (condition != null) {
			System.out.println(condition.getQrCode());
		} else {
			System.out.println(System.nanoTime() / 1000000);
		}
	}

	public void run() {
		this.work();
		ResultMessage result = this.check();
		if (result.isSuccess()) {
			if (nextStep != null) {
				nextStep.run();
			}
		} else {
			result.getAddPriceProductCodes().forEach(System.out::println);
		}

	}
}

/**
 * 
 */

package com.jx.service.workflow.engine.listener;


import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.impl.el.FixedValue;


/**
 * simple introduction
 *
 * <p>detailed comment</p>
 * @author chuxuebao 2015年9月8日
 * @see
 * @since 1.0
 */

@SuppressWarnings("serial")
public class LvTaskCompleteListener implements TaskListener {

	private FixedValue lvTaskDefKey;

	private FixedValue lvTaskAction;
	@Override
	public void notify(DelegateTask delegateTask) {

	}

	public FixedValue getLvTaskDefKey() {
		return lvTaskDefKey;
	}

	public void setLvTaskDefKey(FixedValue lvTaskDefKey) {
		this.lvTaskDefKey = lvTaskDefKey;
	}

	public FixedValue getLvTaskAction() {
		return lvTaskAction;
	}

	public void setLvTaskAction(FixedValue lvTaskAction) {
		this.lvTaskAction = lvTaskAction;
	}
}

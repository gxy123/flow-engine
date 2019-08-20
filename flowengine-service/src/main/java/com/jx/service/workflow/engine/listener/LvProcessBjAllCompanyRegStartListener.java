/**
 * 
 */

package com.jx.service.workflow.engine.listener;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;


/**
 * simple introduction
 *
 * <p>detailed comment</p>
 * @author chuxuebao 2015年10月21日
 * @see
 * @since 1.0
 */

public class LvProcessBjAllCompanyRegStartListener implements ExecutionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void notify(DelegateExecution execution){
		// 检测同一公司是否有国地税流程，如果有则暂停
/*		Object variable = execution.getVariable(ILvTaskQueryService.TASK_ENTERPRISE_ID_KEY);
		if(variable != null){
			String enterpriseId = variable.toString();
			ProcessInstanceQuery createProcessInstanceQuery = ProcessEngineUtils.getRuntimeService().createProcessInstanceQuery();
			List<ProcessInstance> list = createProcessInstanceQuery.variableValueEquals(ILvTaskQueryService.TASK_ENTERPRISE_ID_KEY, Long.parseLong(enterpriseId)).active().list();
			if(list != null && !list.isEmpty()){
				for(ProcessInstance processInstance:list){
					if((Lists.newArrayList(WFUtils.bjRegExclusionProcessKeyArray)).contains(processInstance.getProcessDefinitionKey())){
						// 暂停服务
						ProcessEngineUtils.getTaskService().addComment("", processInstance.getProcessInstanceId(), "processSuspend", "enterpriseId_|_" + enterpriseId);
						ProcessEngineUtils.getRuntimeService().suspendProcessInstanceById(processInstance.getProcessInstanceId());
					}
				}
			}
		}*/
	}
	
}

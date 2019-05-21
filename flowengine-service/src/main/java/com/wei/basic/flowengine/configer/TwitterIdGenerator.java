/**
 * 
 */

package com.wei.basic.flowengine.configer;

import com.wei.basic.flowengine.wrapper.IDWrapper;
import org.activiti.engine.impl.cfg.IdGenerator;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


/**
 * simple introduction
 *
 * <p>detailed comment</p>
 * @author chuxuebao 2015年8月21日
 * @see
 * @since 1.0
 */
@Component
public class TwitterIdGenerator implements IdGenerator {
	@Resource
	private IDWrapper idWrapper;
	@Override
	public String getNextId() {
		return String.valueOf(idWrapper.getSmallId());
	}

}

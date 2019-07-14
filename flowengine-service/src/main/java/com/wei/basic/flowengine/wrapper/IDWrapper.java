package com.wei.basic.flowengine.wrapper;

import com.wei.common.util.DateUtil;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @version 1.0
 * @author: wangqiaobin
 * @date : 2018/7/1
 */
@Component
public class IDWrapper {

    @Resource
    private RedisWrapper redisWrapper;

    public Long getSmallId() {
        Long id = redisWrapper.increment("increment-id");
        Date now = new Date();
        String yyMMdd = DateUtil.dateToString(now, "yyMMdd");
        String auto = String.format("%09d", id);
        return Long.valueOf(yyMMdd + auto);
    }
}

package com.wei.basic.flowengine.wrapper;

import com.wei.common.util.ByteUtil;
import com.wei.common.util.DateUtil;
import com.wei.common.util.DistribID;
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
    private DistribID distribID = new DistribID();

    @Resource
    private RedisWrapper redisWrapper;

    public String getHexId() {
        byte[] bytes = ByteUtil.long2Bytes(distribID.nextId());
        return ByteUtil.bytes2Hex(bytes);
    }

    public String toHex(Long id) {
        byte[] bytes = ByteUtil.long2Bytes(id);
        return ByteUtil.bytes2Hex(bytes);
    }

    public Long toLong(String hex) {
        byte[] bytes = ByteUtil.hex2Bytes(hex);
        return ByteUtil.bytes2Long(bytes);
    }

    public Long getLongId() {
        return distribID.nextId();
    }

    public Long getSmallId() {
        Long id = redisWrapper.increment("increment-id");
        Date now = new Date();
        String yyMMdd = DateUtil.dateToString(now, "yyMMdd");
        String auto = String.format("%09d", id);
        return Long.valueOf(yyMMdd + auto);
    }


}

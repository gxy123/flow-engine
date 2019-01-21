package com.wei.basic.flowengine.client.domain;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName MapData
 * @Author guoxiaoyu
 * @Date 2019/1/2110:09
 **/
@Data
public class MapData {
    private Map<String, Object> variables=new HashMap<>();
}

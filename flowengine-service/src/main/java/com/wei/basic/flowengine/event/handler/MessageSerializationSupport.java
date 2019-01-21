package com.wei.basic.flowengine.event.handler;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;

@Slf4j
public abstract class MessageSerializationSupport {

    protected String serialize(Object serializable) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss"));
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        String message = "";
        try {
            message = mapper.writeValueAsString(serializable);
        } catch (JsonProcessingException e) {
            log.error("serialize fail", e);
        }
        return message;
    }

}

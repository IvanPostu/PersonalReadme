package ipostu.mongo.demo.common.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

public final class ObjectMapperProvider {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private ObjectMapperProvider() {
    }


    public static ObjectMapper getInstance() {
        return OBJECT_MAPPER;
    }
}

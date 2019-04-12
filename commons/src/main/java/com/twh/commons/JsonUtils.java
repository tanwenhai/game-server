package com.twh.commons;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

/**
 * @author tanwenhai@gusoftware.com
 */
public class JsonUtils {
    private static final Logger log = LoggerFactory.getLogger(JsonUtils.class);

    public final static String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";

    private final static ObjectMapper objectMapper = new ObjectMapper();

    static {
        // 空对象也可以正常序列化
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        // 反序列化时忽略未知字段
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        // 设置日期序列化的格式
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS,false);
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        dateFormat.setTimeZone(TimeZone.getTimeZone("CTT"));
        objectMapper.setDateFormat(dateFormat);
    }

    public static ObjectMapper copyInstance() {
        return objectMapper.copy();
    }

    /**
     * 对象转成json
     * @param o
     * @return
     */
    public static String obj2str(Object o) {
        try {
            return objectMapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            log.warn("Object Type {} Serialization Exception as {}", o.getClass(), e);
            return null;
        }
    }

    /**
     * json转Java对象
     * @param json
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T json2pojo(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (IOException e) {
            log.warn("JSON {} Deserialization To {} Exception as {}", json, clazz, e);
            return null;
        }
    }

    public static Map json2map(String json) {
        return json2pojo(json, Map.class);
    }

    public static <T> T map2pojo(Map<?, ?> map, Class<T> clazz) {
        return objectMapper.convertValue(map, clazz);
    }

    /**
     * json使用TypeReference转复杂类型
     * @param json
     * @param typeReference
     * @param <T>
     * @return
     */
    public static <T> T json2Type(String json, TypeReference<T> typeReference) {
        try {
            return objectMapper.readValue(json, typeReference);
        } catch (IOException e) {
            log.warn("JSON {} Deserialization To {} Exception as {}", json, typeReference.getType(), e);
            return null;
        }
    }

    /**
     * json使用JavaType转复杂类型
     * @param json
     * @param javaType
     * @param <T>
     * @return
     */
    public static <T> T json2Type(String json, JavaType javaType) {
        try {
            return objectMapper.readValue(json, javaType);
        } catch (IOException e) {
            log.warn("JSON {} Deserialization To {} Exception as {}", json, javaType, e);
            return null;
        }
    }

    /**
     * json转list
     * @param json
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> List<T> json2list(String json, Class<T> clazz) {
        JavaType javaType = objectMapper.getTypeFactory().constructParametricType(List.class, clazz);
        return json2Type(json, javaType);
    }
}

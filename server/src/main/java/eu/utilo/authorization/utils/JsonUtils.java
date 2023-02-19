package eu.utilo.authorization.utils;

import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

/**
 * Json Helper Klasse
 */
public class JsonUtils {
    private static ObjectMapper mapper = new ObjectMapper();

    static {

        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

        mapper.configure(Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);

        mapper.configure(Feature.ALLOW_SINGLE_QUOTES, true);

        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        mapper.setTimeZone(TimeZone.getTimeZone("Europe/Vienna"));

    }

    /**
     * object json
     *
     * @param obj
     * @return java.lang.String
     */
    public static String objectToJson(Object obj) {
        String json = null;
        try {
            json = mapper.writeValueAsString(obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }

    /**
     * json Map
     *
     * @param json
     * @return java.util.Map<java.lang.String, java.lang.Object>
     */
    public static Map<String, Object> jsonToMap(String json) {
        Map<String, Object> map = null;
        try {
            map = mapper.readValue(json, new TypeReference<Map<String, Object>>() {
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * json list
     *
     * @param json
     * @param beanType
     * @return java.util.List<T>
     */
    public static <T> List<T> jsonToList(String json, Class<T> beanType) {
        List<T> list = null;
        try {
            JavaType javaType =
                    mapper.getTypeFactory().constructParametricType(List.class, beanType);
            list = mapper.readValue(json, javaType);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * json
     *
     * @param resData
     * @param resPro
     * @return java.lang.String
     */
    public static String findValue(String resData, String resPro) {
        String result = null;
        try {
            JsonNode node = mapper.readTree(resData);
            JsonNode resProNode = node.get(resPro);
            result = JsonUtils.objectToJson(resProNode);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * json
     *
     * @param data
     * @return java.lang.String
     */
    public static String jsonString(Object data) {
        if (null == data) {
            return null;
        }
        String json = null;
        try {
            json = mapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return json;
    }

    /**
     * List
     *
     * @param srcList
     * @param clz
     * @return java.util.List<T>
     */
    public static <T> List<T> srcList2ObjList(List<?> srcList, Class<T> clz) {
        String s = jsonString(srcList);
        if (s == null) {
            return new ArrayList<>();
        }
        return jsonToList(s, clz);
    }

    /**
     * json cls
     *
     * @param json
     * @param cls
     * @return T
     */
    public static <T> T jsonToBean(String json, Class<T> cls) {
        T t = null;
        try {
            t = mapper.readValue(json, cls);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return t;
    }

    /**
     *
     * @param src
     * @param clz
     * @return T
     */
    public static <T> T src2Obj(Object src, Class<T> clz) {
        String s = jsonString(src);
        if (s == null) {
            return null;
        }
        return jsonToBean(s, clz);
    }

}

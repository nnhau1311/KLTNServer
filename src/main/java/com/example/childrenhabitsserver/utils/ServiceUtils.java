package com.example.childrenhabitsserver.utils;

import com.example.childrenhabitsserver.common.constant.TypeOfFinishCourse;
import org.springframework.stereotype.Component;

@Component
public class ServiceUtils {

    private static Object parseValue(String type, Object value) {
        Object result;
        switch (type) {
//            case TypeOfFinishCourse.BOOLEAN:
//                result = value instanceof String ? Boolean.parseBoolean((String) value) : value;
//                break;
//            case TypeOfFinishCourse.NUMBER:
            case TypeOfFinishCourse.PERIOD:
            case TypeOfFinishCourse.PERCENTAGE:
                result = convertNumber(value);
                break;
            default:
                result = value;
                break;
        }
        return result;
    }

    private static Object convertNumber(Object value) {
        if (value == null) {
            return "0.0";
        }
        if (value instanceof String) {
            return Double.parseDouble((String) value);
        }
        if (value instanceof Long) {
            return ((Long) value).doubleValue();
        }
        if (value instanceof Integer) {
            return new Double((Integer) value);
        }
        return value;
    }
}

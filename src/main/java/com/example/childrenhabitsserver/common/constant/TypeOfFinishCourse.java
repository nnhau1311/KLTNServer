package com.example.childrenhabitsserver.common.constant;

import java.util.HashMap;
import java.util.Map;

public class TypeOfFinishCourse {

    public static final String PERIOD = "period";
    public static final String PERCENTAGE = "percentage";
    public static final Map<String, String> listMapTypeName;
    static {
        listMapTypeName = new HashMap<>();
        listMapTypeName.put(PERIOD, "Giai đoạn");
        listMapTypeName.put(PERCENTAGE, "Phần trăm");
    }
}

package com.example.childrenhabitsserver.common.constant;

import java.util.HashMap;
import java.util.Map;

public class TypeOfFinishCourse {

    public static final Integer PERIOD = 1;
    public static final Integer PERCENT = 2;
    public static final Map<Integer, String> listMapTypeName;
    static {
        listMapTypeName = new HashMap<>();
        listMapTypeName.put(PERIOD, "Giai đoạn");
        listMapTypeName.put(PERCENT, "Phần trăm");
    }
}

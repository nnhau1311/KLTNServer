package com.example.childrenhabitsserver.base.database;

public class JPACustomType {
    public final static String STRING_ARRAY = "string-array"; // for @type declare
    public final static String STRING_ARRAY_DEF = "text[]"; // for definition in @Column

    public final static String INT_ARRAY = "int-array";
    public final static String INT_ARRAY_DEF = "int[]";
    public final static String LONG_ARRAY = "int-array";

    public final static String LIST_ARRAY = "list-array";
    public final static String LIST_TEXT_ARRAY_DEF = "text[]";
    public final static String LIST_INT_ARRAY_DEF = "int[]";

    public static final String JSON = "json";
    public static final String TEXT = "text";
    public static final String JSON_DEF = "json";
    public static final String JSONB = "jsonb";
    public static final String JSONB_DEF = "jsonb";

    public static final String ENUM = "enum";

    private JPACustomType() {}
}

package org.imango.spring.util;


import com.alibaba.fastjson.JSON;

public class FastJsonUtil {

    public static String ObjectToString(Object o) {
        String s = JSON.toJSONString(o);
        return TrimDoubleQuote(s);
    }

    private static String TrimDoubleQuote(String s) {
        if (s.startsWith("\"") && s.endsWith("\"")) {
            s = s.substring(1, s.length());
            s = s.substring(0, s.length() - 1);
        }
        return s;
    }
}
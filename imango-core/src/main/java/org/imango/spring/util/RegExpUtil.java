package org.imango.spring.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegExpUtil  {
    static String tableRegExp = "getCollection\\('(\\w+?)'\\)";
    static String queryRegExp = "\\.find\\((.*?)\\)";
    static String columnNameRegExp = "'(\\w+?)':";
    static String createRegExp = "\\.save\\((.*?)\\)";
    static String removeRegExp = "\\.remove\\((.*?)\\)";
    static String updateRegExp = "\\.update\\((.*?)\\)";

    static public String getObject(String str, int index) {
        int len = str.length();
        char[] chars = str.toCharArray();
        Stack<Character> stack = new Stack<Character>();
        int begin = 0, end = 0, i = 0, curIndex = 0;
        while (i < len && curIndex <= index) {
            // '{'入栈
            while (i < len&&stack.empty()) {
                if (chars[i] == '{') {
                    stack.push(chars[i]);
                    begin = i;
                }
                ++i;
            }
            if (stack.empty()) {
                return "";
            }
            // 第curIndex个object
            while (!stack.empty() && i < len) {
                if (chars[i] == '{') {
                    stack.push(chars[i]);

                }
                if (chars[i] == '}') {
                    if (!stack.empty() && stack.peek() == '{') {
                        stack.pop();
                    }
                }
                ++i;
            }
            if (stack.empty()) {
                end = i - 1;
                if (curIndex == index)
                    return str.substring(begin, end+1);
            } else {
                return "";
            }

            ++curIndex;
        }
        return "";
    }

    public static List<String> getColumnNames(String sql) {
        return GetWithRegExps(sql, columnNameRegExp);
    }

    public static String getTableName(String sql) {
        return GetWithRegExp(sql, tableRegExp);
    }

    public static String getQuery(String sql) {
        return GetWithRegExp(sql, queryRegExp);
    }

    public static String getCreate(String sql) {
        return GetWithRegExp(sql, createRegExp);
    }

    public static String getUpdate(String sql) {
        return GetWithRegExp(sql, updateRegExp);
    }

    public static String getRemove(String sql) {
        return GetWithRegExp(sql, removeRegExp);
    }

    private static String GetWithRegExp(String s, String regExp) {
        Pattern r = Pattern.compile(regExp);
        // 现在创建 matcher 对象
        Matcher m = r.matcher(s);
        if (m.find()) {
            return m.group(1);
        }

        return "";
    }

    private static List<String> GetWithRegExps(String s, String regExp) {
        List<String> ss = new ArrayList<String>();
        Pattern r = Pattern.compile(regExp);
        // 现在创建 matcher 对象
        Matcher m = r.matcher(s);
        while (m.find()) {
            ss.add(m.group(1));
        }

        return ss;
    }
}

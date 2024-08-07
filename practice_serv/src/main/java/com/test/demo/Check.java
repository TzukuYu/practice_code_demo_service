package com.test.demo;

import com.test.demo.util.DBUtil;

public class Check {
    public static void main(String[] args) {
        // 117, 259
        DBUtil.selectAll(args[0])
                .forEach(it -> System.out.printf("%s:: %s\n", it.getCodeId(), it.getCodeTitle()));
    }
}

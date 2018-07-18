package com.github.mitschi;

public class Example {
    public int multByAddition(int a, int b) {
        int result=0;
        if(b>0) {
            for (int i = 0; i < b; i++) {
                result += a;
            }
        }
        return result;
    }
}

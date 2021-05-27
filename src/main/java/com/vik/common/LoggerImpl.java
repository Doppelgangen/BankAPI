package com.vik.common;

public class LoggerImpl implements Logger {
    @Override
    public void write(String s){
        System.out.println(s);
    }
}

package com.efl.server.serialException;

public class NoPort extends Exception {
    private static final long serialVersionUID = 1L;

    public NoPort(){};

    @Override
    public String toString() {
        return "没有找到对应端口！！！";
    }
}

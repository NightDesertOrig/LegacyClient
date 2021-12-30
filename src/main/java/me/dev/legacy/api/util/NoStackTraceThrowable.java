package me.dev.legacy.api.util;

import me.dev.legacy.HWIDAuthMod;

public class NoStackTraceThrowable extends RuntimeException {
    public NoStackTraceThrowable(String msg) {
        super(msg);
        this.setStackTrace(new StackTraceElement[0]);
    }

    public String toString() {
        return "" + HWIDAuthMod.getVersion();
    }

    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}

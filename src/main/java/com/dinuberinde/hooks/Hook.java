package com.dinuberinde.hooks;

public class Hook {
    private final String tag;
    private final Object dataOut;
    private final Exception exception;

    public Hook(String tag) {
        this(tag, null, null);
    }

    public Hook(String tag, Object dataOut) {
        this(tag, dataOut, null);
    }

    public Hook(String tag, Object dataOut, Exception exception) {
        this.tag = tag;
        this.dataOut = dataOut;
        this.exception = exception;
    }

    public String getTag() {
        return tag;
    }

    public Object getDataOut() {
        return dataOut;
    }

    public Exception getException() {
        return exception;
    }
}

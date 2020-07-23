package com.xiaoyv.editor.common;

public class Flag {
    private boolean state = false;

    synchronized public final void set() {
        state = true;
    }

    synchronized public final void clear() {
        state = false;
    }

    synchronized public final boolean isSet() {
        return state;
    }
}

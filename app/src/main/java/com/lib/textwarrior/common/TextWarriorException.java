/*
 * Copyright (c) 2013 Tah Wei Hoon.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License Version 2.0,
 * with full text available at http://www.apache.org/licenses/LICENSE-2.0.html
 *
 * This software is provided "as is". Use at your own risk.
 */
package com.lib.textwarrior.common;


public class TextWarriorException extends Exception {
    private static final boolean NDEBUG = false;
    private static final long serialVersionUID = -8393914265675038931L;

    public TextWarriorException(String msg) {
        super(msg);
    }

    static public void fail(final String details) {
        assertVerbose(false, details);
    }

    static public void assertVerbose(boolean condition, final String details) {
        if (NDEBUG) {
            return;
        }

        if (!condition) {
            System.err.print(" TextWarrior断言失败： ");
            System.err.println(details);
        }
    }
}

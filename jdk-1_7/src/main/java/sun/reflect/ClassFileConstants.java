/*
 * Copyright (c) 2001, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package sun.reflect;

/**
 * Minimal set of class file constants for assembly of field and
 * method accessors.
 */

interface ClassFileConstants {
    // Constants
    byte opc_aconst_null = (byte) 0x1;
    byte opc_sipush = (byte) 0x11;
    byte opc_ldc = (byte) 0x12;

    // Local variable loads and stores
    byte opc_iload_0 = (byte) 0x1a;
    byte opc_iload_1 = (byte) 0x1b;
    byte opc_iload_2 = (byte) 0x1c;
    byte opc_iload_3 = (byte) 0x1d;
    byte opc_lload_0 = (byte) 0x1e;
    byte opc_lload_1 = (byte) 0x1f;
    byte opc_lload_2 = (byte) 0x20;
    byte opc_lload_3 = (byte) 0x21;
    byte opc_fload_0 = (byte) 0x22;
    byte opc_fload_1 = (byte) 0x23;
    byte opc_fload_2 = (byte) 0x24;
    byte opc_fload_3 = (byte) 0x25;
    byte opc_dload_0 = (byte) 0x26;
    byte opc_dload_1 = (byte) 0x27;
    byte opc_dload_2 = (byte) 0x28;
    byte opc_dload_3 = (byte) 0x29;
    byte opc_aload_0 = (byte) 0x2a;
    byte opc_aload_1 = (byte) 0x2b;
    byte opc_aload_2 = (byte) 0x2c;
    byte opc_aload_3 = (byte) 0x2d;
    byte opc_aaload = (byte) 0x32;
    byte opc_astore_0 = (byte) 0x4b;
    byte opc_astore_1 = (byte) 0x4c;
    byte opc_astore_2 = (byte) 0x4d;
    byte opc_astore_3 = (byte) 0x4e;

    // Stack manipulation
    byte opc_pop = (byte) 0x57;
    byte opc_dup = (byte) 0x59;
    byte opc_dup_x1 = (byte) 0x5a;
    byte opc_swap = (byte) 0x5f;

    // Conversions
    byte opc_i2l = (byte) 0x85;
    byte opc_i2f = (byte) 0x86;
    byte opc_i2d = (byte) 0x87;
    byte opc_l2i = (byte) 0x88;
    byte opc_l2f = (byte) 0x89;
    byte opc_l2d = (byte) 0x8a;
    byte opc_f2i = (byte) 0x8b;
    byte opc_f2l = (byte) 0x8c;
    byte opc_f2d = (byte) 0x8d;
    byte opc_d2i = (byte) 0x8e;
    byte opc_d2l = (byte) 0x8f;
    byte opc_d2f = (byte) 0x90;
    byte opc_i2b = (byte) 0x91;
    byte opc_i2c = (byte) 0x92;
    byte opc_i2s = (byte) 0x93;

    // Control flow
    byte opc_ifeq = (byte) 0x99;
    byte opc_if_icmpeq = (byte) 0x9f;
    byte opc_goto = (byte) 0xa7;

    // Return instructions
    byte opc_ireturn = (byte) 0xac;
    byte opc_lreturn = (byte) 0xad;
    byte opc_freturn = (byte) 0xae;
    byte opc_dreturn = (byte) 0xaf;
    byte opc_areturn = (byte) 0xb0;
    byte opc_return = (byte) 0xb1;

    // Field operations
    byte opc_getstatic = (byte) 0xb2;
    byte opc_putstatic = (byte) 0xb3;
    byte opc_getfield = (byte) 0xb4;
    byte opc_putfield = (byte) 0xb5;

    // Method invocations
    byte opc_invokevirtual = (byte) 0xb6;
    byte opc_invokespecial = (byte) 0xb7;
    byte opc_invokestatic = (byte) 0xb8;
    byte opc_invokeinterface = (byte) 0xb9;

    // Array length
    byte opc_arraylength = (byte) 0xbe;

    // New
    byte opc_new = (byte) 0xbb;

    // Athrow
    byte opc_athrow = (byte) 0xbf;

    // Checkcast and instanceof
    byte opc_checkcast = (byte) 0xc0;
    byte opc_instanceof = (byte) 0xc1;

    // Ifnull and ifnonnull
    byte opc_ifnull = (byte) 0xc6;
    byte opc_ifnonnull = (byte) 0xc7;

    // Constant pool tags
    byte CONSTANT_Class = (byte) 7;
    byte CONSTANT_Fieldref = (byte) 9;
    byte CONSTANT_Methodref = (byte) 10;
    byte CONSTANT_InterfaceMethodref = (byte) 11;
    byte CONSTANT_NameAndType = (byte) 12;
    byte CONSTANT_String = (byte) 8;
    byte CONSTANT_Utf8 = (byte) 1;

    // Access flags
    short ACC_PUBLIC = (short) 0x0001;
}

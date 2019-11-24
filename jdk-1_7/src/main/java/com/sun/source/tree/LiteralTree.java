/*
 * Copyright (c) 2005, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */

package com.sun.source.tree;

/**
 * A tree node for a literal expression.
 * Use {@link #getKind getKind} to determine the kind of literal.
 * <p>
 * For example:
 * <pre>
 *   <em>value</em>
 * </pre>
 *
 * @author Peter von der Ah&eacute;
 * @author Jonathan Gibbons
 * @jls section 15.28
 * @since 1.6
 */
public interface LiteralTree extends ExpressionTree {
    Object getValue();
}

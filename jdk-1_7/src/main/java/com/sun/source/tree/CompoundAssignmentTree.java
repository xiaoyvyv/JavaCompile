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
 * A tree node for compound assignment operator.
 * Use {@link #getKind getKind} to determine the kind of operator.
 * <p>
 * For example:
 * <pre>
 *   <em>variable</em> <em>operator</em> <em>expression</em>
 * </pre>
 *
 * @author Peter von der Ah&eacute;
 * @author Jonathan Gibbons
 * @jls section 15.26.2
 * @since 1.6
 */
public interface CompoundAssignmentTree extends ExpressionTree {
    ExpressionTree getVariable();

    ExpressionTree getExpression();
}

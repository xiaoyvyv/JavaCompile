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
 * A tree node for an assignment expression.
 * <p>
 * For example:
 * <pre>
 *   <em>variable</em> = <em>expression</em>
 * </pre>
 *
 * @author Peter von der Ah&eacute;
 * @author Jonathan Gibbons
 * @jls section 15.26.1
 * @since 1.6
 */
public interface AssignmentTree extends ExpressionTree {
    ExpressionTree getVariable();

    ExpressionTree getExpression();
}

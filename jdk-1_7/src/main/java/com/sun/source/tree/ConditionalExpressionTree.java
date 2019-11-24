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
 * A tree node for the conditional operator ? :.
 * <p>
 * For example:
 * <pre>
 *   <em>condition</em> ? <em>trueExpression</em> : <em>falseExpression</em>
 * </pre>
 *
 * @author Peter von der Ah&eacute;
 * @author Jonathan Gibbons
 * @jls section 15.25
 * @since 1.6
 */
public interface ConditionalExpressionTree extends ExpressionTree {
    ExpressionTree getCondition();

    ExpressionTree getTrueExpression();

    ExpressionTree getFalseExpression();
}

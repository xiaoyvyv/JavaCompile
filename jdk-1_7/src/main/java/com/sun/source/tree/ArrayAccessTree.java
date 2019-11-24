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
 * A tree node for an array access expression.
 * <p>
 * For example:
 * <pre>
 *   <em>expression</em> [ <em>index</em> ]
 * </pre>
 *
 * @author Peter von der Ah&eacute;
 * @author Jonathan Gibbons
 * @jls section 15.13
 * @since 1.6
 */
public interface ArrayAccessTree extends ExpressionTree {
    ExpressionTree getExpression();

    ExpressionTree getIndex();
}

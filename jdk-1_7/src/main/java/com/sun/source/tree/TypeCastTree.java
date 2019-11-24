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
 * A tree node for a type cast expression.
 * <p>
 * For example:
 * <pre>
 *   ( <em>type</em> ) <em>expression</em>
 * </pre>
 *
 * @author Peter von der Ah&eacute;
 * @author Jonathan Gibbons
 * @jls section 15.16
 * @since 1.6
 */
public interface TypeCastTree extends ExpressionTree {
    Tree getType();

    ExpressionTree getExpression();
}

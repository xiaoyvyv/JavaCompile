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
 * A tree node for a 'return' statement.
 * <p>
 * For example:
 * <pre>
 *   return;
 *   return <em>expression</em>;
 * </pre>
 *
 * @author Peter von der Ah&eacute;
 * @author Jonathan Gibbons
 * @jls section 14.17
 * @since 1.6
 */
public interface ReturnTree extends StatementTree {
    ExpressionTree getExpression();
}

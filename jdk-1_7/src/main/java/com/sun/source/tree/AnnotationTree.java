/*
 * Copyright (c) 2005, 2006, Oracle and/or its affiliates. All rights reserved.
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

import java.util.List;

/**
 * A tree node for an annotation.
 * <p>
 * For example:
 * <pre>
 *    {@code @}<em>annotationType</em>
 *    {@code @}<em>annotationType</em> ( <em>arguments</em> )
 * </pre>
 *
 * @author Peter von der Ah&eacute;
 * @author Jonathan Gibbons
 * @jls section 9.7
 * @since 1.6
 */
public interface AnnotationTree extends ExpressionTree {
    Tree getAnnotationType();

    List<? extends ExpressionTree> getArguments();
}

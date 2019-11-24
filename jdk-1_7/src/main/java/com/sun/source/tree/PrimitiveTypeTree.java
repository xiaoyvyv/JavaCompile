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

import javax.lang.model.type.TypeKind;

/**
 * A tree node for a primitive type.
 * <p>
 * For example:
 * <pre>
 *   <em>primitiveTypeKind</em>
 * </pre>
 *
 * @author Peter von der Ah&eacute;
 * @author Jonathan Gibbons
 * @jls section 4.2
 * @since 1.6
 */
public interface PrimitiveTypeTree extends Tree {
    TypeKind getPrimitiveTypeKind();
}

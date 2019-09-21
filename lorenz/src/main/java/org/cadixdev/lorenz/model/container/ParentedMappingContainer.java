/*
 * This file is part of Lorenz, licensed under the MIT License (MIT).
 *
 * Copyright (c) Jamie Mansfield <https://www.jamierocks.uk/>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.cadixdev.lorenz.model.container;

import org.cadixdev.lorenz.model.Mapping;

/**
 * A {@link MappingContainer container} of {@link Mapping mappings}
 * identified by signature, and parented by another object.
 *
 * <p><strong>A parented container should only be used where the
 * container is separate from the parent object!</strong>
 *
 * @param <M> The type of mapping contained
 * @param <S> The type of the signature, representing the mapping
 * @param <P> The type of the parent object
 *
 * @see SimpleParentedMappingContainer
 * @see FieldContainer
 * @see MethodContainer
 * @see InnerClassContainer
 *
 * @author Jamie Mansfield
 * @since 0.6.0
 */
public interface ParentedMappingContainer<M extends Mapping<?, ?>, S, P> extends MappingContainer<M, S> {

    /**
     * Gets the parent object, of the mappings contained in the
     * container.
     *
     * <p><em>This is named back, as it is used heavily in fluent
     * chain operations.</em>
     *
     * @return The parent object
     */
    P back();

}

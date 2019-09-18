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

import java.util.Collection;
import java.util.Optional;

/**
 * A container of {@link Mapping mappings} identified by a signature.
 *
 * <p>Mapping Containers allow for easy code reuse, reducing code,
 * and making convenience methods more easily visible (and therefore
 * maintainable).
 *
 * <p>If the container is used a field, rather than apart the model it
 * backs, then {@link ParentedMappingContainer} should be used to allow
 * for fluent operations.
 *
 * @param <M> The type of mapping contained
 * @param <S> The type of the signature, representing the mapping
 *
 * @see ParentedMappingContainer
 * @see SimpleMappingContainer
 * @see SimpleParentedMappingContainer
 * @see ClassContainer
 * @see MethodContainer
 *
 * @author Jamie Mansfield
 * @since 0.6.0
 */
public interface MappingContainer<M extends Mapping<?, ?>, S> {

    /**
     * Gets an <strong>immutable</strong> view of all the mappings in
     * the container.
     *
     * @return The mappings
     */
    Collection<M> getAll();

    /**
     * Checks whether the container contains a mapping of the given
     * signature.
     *
     * @param signature The signature of the mapping
     * @return {@code true} if there is a mapping for the signature;
     *         {@code false} otherwise
     */
    boolean has(final S signature);

    /**
     * Gets the mapping, should it exist, of the given signature.
     *
     * @param signature The signature of the mapping
     * @return The mapping, wrapped in an {@link Optional}
     */
    Optional<M> get(final S signature);

    /**
     * Gets, or creates if it doesn't exist, a mapping for the given
     * signature.
     *
     * @param signature The signature of the mapping
     * @return The mapping
     */
    M getOrCreate(final S signature);

}

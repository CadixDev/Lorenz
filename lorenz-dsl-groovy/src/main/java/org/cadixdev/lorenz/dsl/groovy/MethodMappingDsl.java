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

package org.cadixdev.lorenz.dsl.groovy;

import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import org.cadixdev.lorenz.model.ClassMapping;
import org.cadixdev.lorenz.model.MethodMapping;
import org.cadixdev.lorenz.model.MethodParameterMapping;

/**
 * A DSL to simplify the manipulation of {@link MethodMapping}s in Groovy.
 *
 * @author Jamie Mansfield
 * @since 0.6.0
 */
public class MethodMappingDsl extends MappingDsl<MethodMapping, ClassMapping> {

    public MethodMappingDsl(final MethodMapping mapping) {
        super(mapping);
    }

    /**
     * Creates a method parameter mapping for the given index,
     * and applies the given {@link Closure} to it.
     *
     * @param index The index of the parameter
     * @param script The closure to use
     * @return The mapping
     * @see MethodMapping#getOrCreateParameterMapping(int)
     */
    public MethodParameterMapping param(
            final int index,
            @DelegatesTo(strategy = DslUtil.RESOLVE_STRATEGY, value = MappingDsl.class) final Closure<?> script) {
        return DslUtil.delegate(
                this.mapping.getOrCreateParameterMapping(index),
                MappingDsl::new, script);
    }

}

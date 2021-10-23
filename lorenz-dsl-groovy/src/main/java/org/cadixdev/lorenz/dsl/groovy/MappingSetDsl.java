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
import org.cadixdev.lorenz.MappingSet;
import org.cadixdev.lorenz.model.TopLevelClassMapping;

/**
 * A DSL to simplify the creation of {@link MappingSet}s in Groovy.
 *
 * @author Jamie Mansfield
 * @since 0.6.0
 */
public class MappingSetDsl {

    /**
     * Creates a mapping set, and applies the given {@link Closure} to it.
     *
     * @param script The closure to use
     * @return The mapping set
     * @see MappingSet#MappingSet()
     */
    public static MappingSet create(@DelegatesTo(strategy = DslUtil.RESOLVE_STRATEGY, value = MappingSetDsl.class) final Closure<?> script) {
        return DslUtil.delegate(new MappingSet(), MappingSetDsl::new, script);
    }

    private final MappingSet mappings;

    public MappingSetDsl(final MappingSet mappings) {
        this.mappings = mappings;
    }

    /**
     * Creates a top-level class mapping of the given name, and applies
     * the given {@link Closure} to it.
     *
     * @param name The obfuscated name of the class
     * @param script The closure to use
     * @return The mapping
     * @see MappingSet#getOrCreateTopLevelClassMapping(String)
     */
    public TopLevelClassMapping klass(
            final String name,
            @DelegatesTo(strategy = DslUtil.RESOLVE_STRATEGY, value = ClassMappingDsl.class) final Closure<?> script) {
        return DslUtil.delegate(
                this.mappings.getOrCreateTopLevelClassMapping(name),
                ClassMappingDsl::new, script);
    }

}

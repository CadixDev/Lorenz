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

package me.jamiemansfield.lorenz.model;

import static me.jamiemansfield.lorenz.MappingSet.ALPHABETISE_MAPPINGS;

import me.jamiemansfield.lorenz.cadix.MappingVisitor;

/**
 * Represents a de-obfuscation mapping for a top-level class.
 *
 * @author Jamie Mansfield
 * @since 0.1.0
 */
public interface TopLevelClassMapping extends ClassMapping<TopLevelClassMapping> {

    @Override
    default String getFullObfuscatedName() {
        return this.getObfuscatedName();
    }

    @Override
    default String getFullDeobfuscatedName() {
        return this.getDeobfuscatedName();
    }

    @Override
    default void accept(final MappingVisitor visitor) {
        if (visitor != null && this.hasMappings()) {
            visitor.visit(this.getObfuscatedName(), this.getDeobfuscatedName());

            this.getFieldMappings().stream()
                    .sorted(ALPHABETISE_MAPPINGS)
                    .forEach(mapping -> mapping.accept(visitor));

            this.getMethodMappings().stream()
                    .sorted(ALPHABETISE_MAPPINGS)
                    .forEach(mapping -> mapping.accept(visitor));

            this.getInnerClassMappings().stream()
                    .sorted(ALPHABETISE_MAPPINGS)
                    .forEach(mapping -> mapping.accept(visitor));

            visitor.end();
        }
    }

}

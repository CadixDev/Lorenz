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

package org.cadixdev.lorenz.util;

import org.cadixdev.lorenz.MappingSet;
import org.cadixdev.lorenz.model.ClassMapping;
import org.cadixdev.lorenz.model.Mapping;

import java.util.Optional;

/**
 * Utilities for working with binary names on {@link Mapping mappings}
 * and {@link MappingSet mapping sets}.
 *
 * @author Jamie Mansfield
 * @since 0.6.0
 */
public final class BinaryTool {

    /**
     * Gets the class mapping of the given <em>binary</em> obfuscated name.
     *
     * @param mappings The mapping set
     * @param obfuscatedName The obfuscated name
     * @return The class mapping, wrapped in an {@link Optional}
     */
    public static Optional<? extends ClassMapping<?, ?>> getClassMapping(final MappingSet mappings, final String obfuscatedName) {
        final int lastIndex = obfuscatedName.lastIndexOf('$');
        if (lastIndex == -1) return mappings.getTopLevelClassMapping(obfuscatedName);

        // Split the obfuscated name, to fetch the parent class name, and inner class name
        final String parentClassName = obfuscatedName.substring(0, lastIndex);
        final String innerClassName = obfuscatedName.substring(lastIndex + 1);

        // Get the parent class
        return getClassMapping(mappings, parentClassName)
                // Get and return the inner class
                .flatMap(parentClassMapping -> parentClassMapping.getInnerClassMapping(innerClassName));
    }

    private BinaryTool() {
    }

}

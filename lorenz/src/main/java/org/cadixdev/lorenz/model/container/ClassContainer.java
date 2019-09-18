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

import org.cadixdev.lorenz.model.ClassMapping;
import org.cadixdev.lorenz.model.InnerClassMapping;
import org.cadixdev.lorenz.model.TopLevelClassMapping;

import java.util.Optional;

/**
 * A {@link MappingContainer container} of {@link ClassMapping class mappings}
 * identified by their names.
 *
 * @param <M> The type of the class mapping, either {@link TopLevelClassMapping} or
 *            {@link InnerClassMapping}
 *
 * @author Jamie Mansfield
 * @since 0.6.0
 */
public interface ClassContainer<M extends ClassMapping<?, ?>> extends MappingContainer<M, String> {

    /**
     * Gets, if it exists, the class mapping of the given obfuscated name.
     *
     * @param obfuscatedName The obfuscated name
     * @return The class mapping, wrapped in an {@link Optional}
     */
    default Optional<? extends ClassMapping<?, ?>> resolve(final String obfuscatedName) {
        final int lastIndex = obfuscatedName.lastIndexOf('$');
        if (lastIndex == -1) return this.get(obfuscatedName);

        // Split the obfuscated name, to fetch the parent class name, and inner class name
        final String parentClassName = obfuscatedName.substring(0, lastIndex);
        final String innerClassName = obfuscatedName.substring(lastIndex + 1);

        // Get the parent class
        return this.resolve(parentClassName)
                // Get and return the inner class
                // TODO: rewrite for containers when they're used in the mappings themselves
                .flatMap(parentClassMapping -> parentClassMapping.getInnerClassMapping(innerClassName));
    }

    /**
     * Attempts to locate a class mapping for the given obfuscated name.
     *
     * <p>This is equivalent to calling {@link #resolve(String)},
     * except that it will insert a new inner class mapping in case a
     * class mapping for the outer class exists.</p>
     *
     * <p>This method exists to simplify remapping, where it is important
     * to keep inner classes a part of the outer class.</p>
     *
     * @param obfuscatedName The obfuscated name
     * @return The class mapping, wrapped in an {@link Optional}
     */
    default Optional<? extends ClassMapping<?, ?>> compute(final String obfuscatedName) {
        final int lastIndex = obfuscatedName.lastIndexOf('$');
        if (lastIndex == -1) return this.get(obfuscatedName);

        // Split the obfuscated name, to fetch the parent class name, and inner class name
        final String parentClassName = obfuscatedName.substring(0, lastIndex);
        final String innerClassName = obfuscatedName.substring(lastIndex + 1);

        // Get the parent class
        return this.resolve(parentClassName)
                // Get and return the inner class
                // TODO: rewrite for containers when they're used in the mappings themselves
                .map(parentClassMapping -> parentClassMapping.getOrCreateInnerClassMapping(innerClassName));
    }

    /**
     * Gets, or creates should it not exist, a class mapping, of the given
     * obfuscated name.
     *
     * @param obfuscatedName The obfuscated name of the class mapping
     * @return The class mapping
     */
    default ClassMapping<?, ?> resolveOrCreate(final String obfuscatedName) {
        final int lastIndex = obfuscatedName.lastIndexOf('$');
        if (lastIndex == -1) return this.getOrCreate(obfuscatedName);

        // Split the obfuscated name, to fetch the parent class name, and inner class name
        final String parentClassName = obfuscatedName.substring(0, lastIndex);
        final String innerClassName = obfuscatedName.substring(lastIndex + 1);

        // Get the parent class
        final ClassMapping parentClass = this.resolveOrCreate(parentClassName);

        // Get the inner class
        // TODO: rewrite for containers when they're used in the mappings themselves
        return parentClass.getOrCreateInnerClassMapping(innerClassName);
    }

}

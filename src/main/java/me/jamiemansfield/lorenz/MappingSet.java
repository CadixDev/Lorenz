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

package me.jamiemansfield.lorenz;

import me.jamiemansfield.lorenz.impl.MappingSetImpl;
import me.jamiemansfield.lorenz.model.ClassMapping;
import me.jamiemansfield.lorenz.model.InnerClassMapping;
import me.jamiemansfield.lorenz.model.TopLevelClassMapping;
import me.jamiemansfield.lorenz.model.jar.FieldTypeProvider;

import java.util.Collection;
import java.util.Optional;

/**
 * Represents a container for a set of mappings.
 *
 * @author Jamie Mansfield
 * @since 0.1.0
 */
public interface MappingSet {

    /**
     * Creates a mapping set, using the default Lorenz model implementation.
     *
     * @return The mapping set
     */
    static MappingSet create() {
        return new MappingSetImpl(null);
    }

    /**
     * Creates a mapping set, using the default Lorenz model implementation.
     *
     * @param fieldTypeProvider The field type provider to use (if applicable)
     * @return The mapping set
     * @since 0.3.0
     */
    static MappingSet create(final FieldTypeProvider fieldTypeProvider) {
        return new MappingSetImpl(fieldTypeProvider);
    }

    /**
     * Gets an immutable collection of all of the top-level class
     * mappings of the mapping set.
     *
     * @return The top-level class mappings
     */
    Collection<TopLevelClassMapping> getTopLevelClassMappings();

    /**
     * Creates a top-level class mapping with the given obfuscated and de-obfuscated
     * names.
     *
     * @param obfuscatedName The obfuscated name of the top-level class
     * @param deobfuscatedName The de-obfuscated name of the top-level class
     * @return The top-level class mapping, to allow for chaining
     */
    TopLevelClassMapping createTopLevelClassMapping(final String obfuscatedName, final String deobfuscatedName);

    /**
     * Gets the top-level class mapping of the given obfuscated name of the
     * class mapping, should it exist.
     *
     * @param obfuscatedName The obfuscated name of the top-level class mapping
     * @return The top-level class mapping, wrapped in an {@link Optional}
     */
    Optional<TopLevelClassMapping> getTopLevelClassMapping(final String obfuscatedName);

    /**
     * Gets, or creates should it not exist, a top-level class mapping of the
     * given obfuscated name.
     *
     * @param obfuscatedName The obfuscated name of the top-level class mapping
     * @return The top-level class mapping
     */
    default TopLevelClassMapping getOrCreateTopLevelClassMapping(final String obfuscatedName) {
        return this.getTopLevelClassMapping(obfuscatedName)
                .orElseGet(() -> this.createTopLevelClassMapping(obfuscatedName, obfuscatedName));
    }

    /**
     * Establishes whether the mapping set contains a top-level class
     * mapping of the given obfuscated name.
     *
     * @param obfuscatedName The obfuscated name of the top-level class
     *                       mapping
     * @return {@code true} should a top-level class mapping of the
     *         given obfuscated name exist in the mapping set;
     *         {@code false} otherwise
     */
    boolean hasTopLevelClassMapping(final String obfuscatedName);

    /**
     * Gets the class mapping of the given obfuscated name.
     *
     * @param obfuscatedName The obfuscated name
     * @return The class mapping, wrapped in an {@link Optional}
     */
    default Optional<ClassMapping<?>> getClassMapping(final String obfuscatedName) {
        if (!obfuscatedName.contains("$")) return Optional.ofNullable(this.getTopLevelClassMapping(obfuscatedName).orElse(null));

        // Split the obfuscated name, to fetch the parent class name, and inner class name
        final int lastIndex = obfuscatedName.lastIndexOf('$');
        final String parentClassName = obfuscatedName.substring(0, lastIndex);
        final String innerClassName = obfuscatedName.substring(lastIndex + 1);

        // Get the parent class
        final Optional<ClassMapping<?>> parentClassMapping = this.getClassMapping(parentClassName);
        if (!parentClassMapping.isPresent()) return Optional.empty();

        // Get and return the inner class
        final Optional<InnerClassMapping> innerClassMapping = parentClassMapping.get().getInnerClassMapping(innerClassName);
        return Optional.ofNullable(innerClassMapping.orElse(null));
    }

    /**
     * Gets, or creates should it not exist, a class mapping, of the given
     * obfuscated name.
     *
     * @param obfuscatedName The obfuscated name of the class mapping
     * @return The class mapping
     */
    default ClassMapping getOrCreateClassMapping(final String obfuscatedName) {
        if (!obfuscatedName.contains("$")) return this.getOrCreateTopLevelClassMapping(obfuscatedName);

        // Split the obfuscated name, to fetch the parent class name, and inner class name
        final int lastIndex = obfuscatedName.lastIndexOf('$');
        final String parentClassName = obfuscatedName.substring(0, lastIndex);
        final String innerClassName = obfuscatedName.substring(lastIndex + 1);

        // Get the parent class
        final ClassMapping parentClass = this.getOrCreateClassMapping(parentClassName);

        // Get the inner class
        return parentClass.getOrCreateInnerClassMapping(innerClassName);
    }

    /**
     * Gets the field type provider in use for this set of mappings.
     *
     * @return The field type provider
     * @since 0.3.0
     */
    Optional<FieldTypeProvider> getFieldTypeProvider();

    /**
     * Sets the field type provider in use for this set of mappings.
     *
     * @param fieldTypeProvider The field type provider
     * @since 0.3.0
     */
    void setFieldTypeProvider(final FieldTypeProvider fieldTypeProvider);

}

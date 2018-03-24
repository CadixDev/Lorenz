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

import me.jamiemansfield.lorenz.model.ClassMapping;
import me.jamiemansfield.lorenz.model.InnerClassMapping;
import me.jamiemansfield.lorenz.model.TopLevelClassMapping;
import me.jamiemansfield.lorenz.model.impl.TopLevelClassMappingImpl;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Represents a container for a set of mappings.
 */
public final class MappingSet {

    private final Map<String, TopLevelClassMapping> topLevelClasses = new HashMap<>();

    /**
     * Creates a new mapping set.
     */
    public MappingSet() {
    }

    /**
     * Gets an immutable collection of all of the top-level class
     * mappings of the mapping set.
     *
     * @return The top-level class mappings
     */
    public Collection<TopLevelClassMapping> getTopLevelClassMappings() {
        return Collections.unmodifiableCollection(this.topLevelClasses.values());
    }

    /**
     * Adds the given {@link TopLevelClassMapping} to the mapping set.
     *
     * @param mapping The top-level class mapping to add
     * @return The top-level class mapping, to allow for chaining
     */
    public TopLevelClassMapping addTopLevelClassMapping(final TopLevelClassMapping mapping) {
        this.topLevelClasses.put(mapping.getObfuscatedName(), mapping);
        return mapping;
    }

    /**
     * Establishes whether the mapping set contains a top-level class
     * mapping of the given obfuscated name.
     *
     * @param obfuscatedName The obfuscated name of the top-level class
     *                       mapping
     * @return {@code True} should a top-level class mapping of the
     *         given obfuscated name exist in the mapping set,
     *         else {@code false}
     */
    public boolean hasTopLevelClassMapping(final String obfuscatedName) {
        return this.topLevelClasses.containsKey(obfuscatedName);
    }

    /**
     * Gets the top-level class mapping of the given obfuscated name of the
     * class mapping, should it exist.
     *
     * @param obfuscatedName The obfuscated name of the top-level class mapping
     * @return The top-level class mapping, wrapped in an {@link Optional}
     */
    public Optional<TopLevelClassMapping> getTopLevelClassMapping(final String obfuscatedName) {
        return Optional.ofNullable(this.topLevelClasses.get(obfuscatedName));
    }

    /**
     * Gets, or creates should it not exist, a top-level class mapping of the
     * given obfuscated name.
     *
     * @param obfuscatedName The obfuscated name of the top-level class mapping
     * @return The top-level class mapping
     */
    public TopLevelClassMapping getOrCreateTopLevelClassMapping(final String obfuscatedName) {
        return this.getTopLevelClassMapping(obfuscatedName)
                .orElseGet(() -> this.addTopLevelClassMapping(new TopLevelClassMappingImpl(this, obfuscatedName, obfuscatedName)));
    }

    /**
     * Gets the class mapping of the given obfuscated name.
     *
     * @param obfuscatedName The obfuscated name
     * @return The class mapping, wrapped in an {@link Optional}
     */
    public Optional<ClassMapping<?>> getClassMapping(final String obfuscatedName) {
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
    public ClassMapping getOrCreateClassMapping(final String obfuscatedName) {
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

}

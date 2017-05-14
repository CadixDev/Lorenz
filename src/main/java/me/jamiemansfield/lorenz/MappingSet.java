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

import com.google.common.collect.Maps;
import me.jamiemansfield.lorenz.model.ClassMapping;
import me.jamiemansfield.lorenz.model.InnerClassMapping;
import me.jamiemansfield.lorenz.model.TopLevelClassMapping;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

/**
 * Represents a container for a set of mappings.
 */
public final class MappingSet {

    private final Map<String, TopLevelClassMapping> topLevelClasses = Maps.newHashMap();

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
                .orElseGet(() -> this.addTopLevelClassMapping(new TopLevelClassMapping(this, obfuscatedName, obfuscatedName)));
    }

    /**
     * Gets the class mapping of the given obfuscated name.
     *
     * @param obfuscatedName The obfuscated name
     * @return The class mapping, wrapped in an {@link Optional}
     */
    public Optional<ClassMapping> getClassMapping(final String obfuscatedName) {
        if (!obfuscatedName.contains("$")) return Optional.ofNullable(this.getTopLevelClassMapping(obfuscatedName).orElse(null));

        // Split the obfuscated name, to fetch the parent class name, and inner class name
        final int lastIndex = obfuscatedName.lastIndexOf('$');
        final String parentClassName = obfuscatedName.substring(0, lastIndex - 1);
        final String innerClassName = obfuscatedName.substring(lastIndex);

        // Get the parent class
        final Optional<ClassMapping> parentClassMapping = this.getClassMapping(parentClassName);
        if (!parentClassMapping.isPresent()) return Optional.empty();

        // Get the inner class
        final Optional<InnerClassMapping> innerClassMapping = parentClassMapping.get().getInnerClassMapping(innerClassName);
        if (!innerClassMapping.isPresent()) return Optional.empty();

        // Return the inner class
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
        final String parentClassName = obfuscatedName.substring(0, lastIndex - 1);
        final String innerClassName = obfuscatedName.substring(lastIndex);

        // Get the parent class
        final ClassMapping parentClass = this.getOrCreateClassMapping(parentClassName);

        // Get the inner class
        return parentClass.getOrCreateInnerClassMapping(innerClassName);
    }

    /**
     * De-obfuscates the given obfuscated obfuscated signature.
     *
     * @param obfuscatedSignature The obfuscated method signature
     * @return A de-obfuscated method signature
     */
    public String deobfuscateMethodSignature(final String obfuscatedSignature) {
        final String innerContent = obfuscatedSignature.substring(obfuscatedSignature.indexOf("(") + 1, obfuscatedSignature.indexOf(")"));
        final String outerContent = obfuscatedSignature.substring(obfuscatedSignature.indexOf(")") + 1);

        final StringBuilder typeBuilder = new StringBuilder();
        typeBuilder.append("(");

        boolean isParsingObject = false;
        StringBuilder objectBuilder = new StringBuilder();
        for (final char c : innerContent.toCharArray()) {
            if (isParsingObject) {
                if (c == ';') {
                    isParsingObject = false;
                    typeBuilder.append(this.deobfuscateType(objectBuilder.toString()));
                    objectBuilder = new StringBuilder();
                } else {
                    objectBuilder.append(c);
                }
            } else {
                // Objects
                if (c == 'L') {
                    isParsingObject = true;
                } else {
                    typeBuilder.append(c);
                }
            }
        }

        typeBuilder.append(")");

        if (outerContent.startsWith("L")) {
            final String returnType = outerContent.substring(1, outerContent.length() - 1);
            typeBuilder.append(this.deobfuscateType(returnType));
        } else {
            typeBuilder.append(outerContent);
        }

        return typeBuilder.toString();
    }

    /**
     * De-obfuscates the given type.
     * Types can be provided as <pre>Ltype;</pre> or just plain <pre>type</pre>.
     *
     * @param rawType The raw type
     * @return The de-obfuscated type
     */
    public String deobfuscateType(final String rawType) {
        final String type;
        if (rawType.startsWith("L")) type = rawType.substring(1, rawType.length() - 1);
        else type = rawType;

        final Optional<ClassMapping> typeMapping = this.getClassMapping(type);
        final StringBuilder typeBuilder = new StringBuilder();

        typeBuilder.append("L");
        if (typeMapping.isPresent()) {
            typeBuilder.append(typeMapping.get().getFullDeobfuscatedName());
        } else {
            typeBuilder.append(type);
        }
        typeBuilder.append(";");
        return typeBuilder.toString();
    }

}

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

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a {@link BaseMapping} for a class.
 */
public abstract class ClassMapping extends BaseMapping {

    private final Map<String, FieldMapping> fieldMappings = new HashMap<>();
    private final Map<MethodSignature, MethodMapping> methodMappings = new HashMap<>();
    private final Map<String, InnerClassMapping> innerClassMappings = new HashMap<>();

    /**
     * Constructs a new {@link ClassMapping} with the given parameters.
     *
     * @param obfuscated The obfuscated name of the class
     * @param deobfuscated The deobfuscated name of the class
     */
    public ClassMapping(final String obfuscated, final String deobfuscated) {
        super(obfuscated, deobfuscated);
    }

    /**
     * Gets a clone of the {@link FieldMapping}s.
     *
     * @return A clone of the {@link FieldMapping}s
     */
    public Map<String, FieldMapping> getFieldMappings() {
        return new HashMap<>(this.fieldMappings);
    }

    /**
     * Gets a clone of the {@link MethodMapping}s.
     *
     * @return A clone of the {@link MethodMapping}s
     */
    public Map<MethodSignature, MethodMapping> getMethodMappings() {
        return new HashMap<>(this.methodMappings);
    }

    /**
     * Gets a clone of the {@link InnerClassMapping}s.
     *
     * @return A clone of the {@link InnerClassMapping}s
     */
    public Map<String, InnerClassMapping> getInnerClassMappings() {
        return new HashMap<>(this.innerClassMappings);
    }

    /**
     * Adds the given {@link FieldMapping} to this {@link ClassMapping}.
     *
     * @param mapping The {@link FieldMapping} to add
     */
    public void addFieldMapping(final FieldMapping mapping) {
        this.fieldMappings.put(mapping.getObfuscatedName(), mapping);
    }

    /**
     * Removes the {@link FieldMapping} with the given signature from this
     * {@link ClassMapping}.
     *
     * @param name The signature of the field to remove the mapping of
     */
    public void removeFieldMapping(final String name) {
        this.fieldMappings.remove(name);
    }

    /**
     * Adds the given {@link MethodMapping} to this {@link ClassMapping}.
     *
     * @param mapping The {@link MethodMapping} to add
     */
    public void addMethodMapping(final MethodMapping mapping) {
        this.methodMappings.put(mapping.getSignature(), mapping);
    }

    /**
     * Removes the {@link MethodMapping} with the given signature from this
     * {@link ClassMapping}.
     *
     * @param name The signature of the method to remove the mapping of
     */
    public void removeMethodMapping(final String name) {
        this.methodMappings.remove(name);
    }

    /**
     * Adds the given {@link InnerClassMapping} to this {@link ClassMapping}.
     *
     * @param mapping The {@link InnerClassMapping} to add
     */
    public void addInnerClassMapping(final InnerClassMapping mapping) {
        this.innerClassMappings.put(mapping.getObfuscatedName(), mapping);
    }

    /**
     * Removes the {@link InnerClassMapping} with the given signature from this
     * {@link ClassMapping}.
     *
     * @param name The name of the inner class to remove the mapping of
     */
    public void removeInnerClassMapping(final String name) {
        this.innerClassMappings.remove(name);
    }

}

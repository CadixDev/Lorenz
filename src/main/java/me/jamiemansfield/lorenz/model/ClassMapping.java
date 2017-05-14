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

import com.google.common.base.MoreObjects;
import com.google.common.collect.Maps;
import me.jamiemansfield.lorenz.MappingSet;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Represents a de-obfuscation mapping for classes.
 *
 * This class will likely not be of too great use, and most will find
 * the child classes for top-level, and inner classes more useful.
 *
 * @see TopLevelClassMapping
 * @see InnerClassMapping
 */
public abstract class ClassMapping extends Mapping {

    private final Map<String, FieldMapping> fields = Maps.newHashMap();
    private final Map<MethodDescriptor, MethodMapping> methods = Maps.newHashMap();
    private final Map<String, InnerClassMapping> innerClasses = Maps.newHashMap();

    /**
     * Creates a new class mapping, from the given parameters.
     *
     * @param mappings The mappings set, this mapping belongs to
     * @param obfuscatedName The obfuscated name
     * @param deobfuscatedName The de-obfuscated name
     */
    protected ClassMapping(final MappingSet mappings, final String obfuscatedName, final String deobfuscatedName) {
        super(mappings, obfuscatedName, deobfuscatedName);
    }

    /**
     * Gets an immutable collection of all of the field mappings
     * of the class mapping.
     *
     * @return The field mappings
     */
    public Collection<FieldMapping> getFieldMappings() {
        return Collections.unmodifiableCollection(this.fields.values());
    }

    /**
     * Adds the given {@link FieldMapping} to the class mapping.
     *
     * @param mapping The field mapping to add
     * @return The field mapping, to allow for chaining
     */
    public FieldMapping addFieldMapping(final FieldMapping mapping) {
        this.fields.put(mapping.getObfuscatedName(), mapping);
        return mapping;
    }

    /**
     * Establishes whether the class mapping contains a field mapping
     * of the given obfuscated name.
     *
     * @param obfuscatedName The obfuscated name of the field mapping
     * @return {@code True} should a field mapping of the given
     *         obfuscated name exist in the class mapping, else
     *         {@code false}
     */
    public boolean hasFieldMapping(final String obfuscatedName) {
        return this.fields.containsKey(obfuscatedName);
    }

    /**
     * Gets the field mapping of the given obfuscated name of the
     * class mapping, should it exist.
     *
     * @param obfuscatedName The obfuscated name of the field mapping
     * @return The field mapping, wrapped in an {@link Optional}
     */
    public Optional<FieldMapping> getFieldMapping(final String obfuscatedName) {
        return Optional.ofNullable(this.fields.get(obfuscatedName));
    }

    /**
     * Gets, or creates should it not exist, a field mapping of the
     * given obfuscated name.
     *
     * @param obfuscatedName The obfuscated name of the field mapping
     * @return The field mapping
     */
    public FieldMapping getOrCreateFieldMapping(final String obfuscatedName) {
        return this.getFieldMapping(obfuscatedName)
                .orElseGet(() -> this.addFieldMapping(new FieldMapping(this, obfuscatedName, obfuscatedName)));
    }

    /**
     * Gets an immutable collection of all of the method mappings
     * of the class mapping.
     *
     * @return The method mappings
     */
    public Collection<MethodMapping> getMethodMappings() {
        return Collections.unmodifiableCollection(this.methods.values());
    }

    /**
     * Adds the given {@link MethodMapping} to the class mapping.
     *
     * @param mapping The method mapping to add
     * @return The method mapping, to allow for chaining
     */
    public MethodMapping addMethodMapping(final MethodMapping mapping) {
        this.methods.put(mapping.getObfuscatedDescriptor(), mapping);
        return mapping;
    }

    /**
     * Establishes whether the class mapping contains a method mapping
     * of the given obfuscated name.
     *
     * @param obfuscatedDescriptor The obfuscated descriptor of the method
     *                             mapping
     * @return {@code True} should a method mapping of the given
     *         obfuscated name exist in the class mapping, else
     *         {@code false}
     */
    public boolean hasMethodMapping(final MethodDescriptor obfuscatedDescriptor) {
        return this.methods.containsKey(obfuscatedDescriptor);
    }

    /**
     * Gets the method mapping of the given obfuscated name of the
     * class mapping, should it exist.
     *
     * @param obfuscatedDescriptor The obfuscated descriptor of the method
     *                             mapping
     * @return The method mapping, wrapped in an {@link Optional}
     */
    public Optional<MethodMapping> getMethodMapping(final MethodDescriptor obfuscatedDescriptor) {
        return Optional.ofNullable(this.methods.get(obfuscatedDescriptor));
    }

    /**
     * Gets, or creates should it not exist, a method mapping of the
     * given obfuscated descriptor.
     *
     * @param obfuscatedDescriptor The obfuscated descriptor of the method
     *                             mapping
     * @return The method mapping
     */
    public MethodMapping getOrCreateMethodMapping(final MethodDescriptor obfuscatedDescriptor) {
        return this.getMethodMapping(obfuscatedDescriptor)
                .orElseGet(() -> this.addMethodMapping(new MethodMapping(this, obfuscatedDescriptor, obfuscatedDescriptor.getName())));
    }

    /**
     * Gets, or creates should it not exist, a method mapping of the
     * given obfuscated name, and signature.
     *
     * @param obfuscatedName The obfuscated name of the method mapping
     * @param obfuscatedSignature The obfuscated signature of the method mapping
     * @return The method mapping
     */
    public MethodMapping getOrCreateMethodMapping(final String obfuscatedName, final String obfuscatedSignature) {
        return this.getOrCreateMethodMapping(new MethodDescriptor(obfuscatedName, obfuscatedSignature));
    }

    /**
     * Gets an immutable collection of all of the inner class
     * mappings of the class mapping.
     *
     * @return The inner class mappings
     */
    public Collection<InnerClassMapping> getInnerClassMappings() {
        return Collections.unmodifiableCollection(this.innerClasses.values());
    }

    /**
     * Adds the given {@link InnerClassMapping} to the class mapping.
     *
     * @param mapping The inner class mapping to add
     * @return The inner class mapping, to allow for chaining
     */
    public InnerClassMapping addInnerClassMapping(final InnerClassMapping mapping) {
        this.innerClasses.put(mapping.getObfuscatedName(), mapping);
        return mapping;
    }

    /**
     * Establishes whether the class mapping contains a inner class
     * mapping of the given obfuscated name.
     *
     * @param obfuscatedName The obfuscated name of the inner class
     *                       mapping
     * @return {@code True} should a inner class mapping of the
     *         given obfuscated name exist in the class mapping,
     *         else {@code false}
     */
    public boolean hasInnerClassMapping(final String obfuscatedName) {
        return this.innerClasses.containsKey(obfuscatedName);
    }

    /**
     * Gets the inner class mapping of the given obfuscated name of the
     * class mapping, should it exist.
     *
     * @param obfuscatedName The obfuscated name of the inner class mapping
     * @return The inner class mapping, wrapped in an {@link Optional}
     */
    public Optional<InnerClassMapping> getInnerClassMapping(final String obfuscatedName) {
        return Optional.ofNullable(this.innerClasses.get(obfuscatedName));
    }

    /**
     * Gets, or creates should it not exist, a inner class mapping of the
     * given obfuscated name.
     *
     * @param obfuscatedName The obfuscated name of the inner class mapping
     * @return The inner class mapping
     */
    public InnerClassMapping getOrCreateInnerClassMapping(final String obfuscatedName) {
        return this.getInnerClassMapping(obfuscatedName)
                .orElseGet(() -> this.addInnerClassMapping(new InnerClassMapping(this, obfuscatedName, obfuscatedName)));
    }

    /**
     * Establishes whether the class mapping has a de-obfuscation mapping, or
     * has some mappings within it.
     *
     * @return {@code True} if the class mappings has mappings,
     *         {@code false} otherwise
     */
    public boolean hasMappings() {
        return this.hasDeobfuscatedName() || (
                this.getFieldMappings().stream()
                        .filter(Mapping::hasDeobfuscatedName)
                        .count() != 0
                && this.getMethodMappings().stream()
                        .filter(Mapping::hasDeobfuscatedName)
                        .count() != 0
                && this.getInnerClassMappings().stream()
                        .filter(ClassMapping::hasMappings)
                        .count() != 0
                );
    }

    @Override
    protected MoreObjects.ToStringHelper buildToString() {
        return super.buildToString()
                .add("fields", this.getFieldMappings())
                .add("methods", this.getMethodMappings())
                .add("innerClasses", this.getInnerClassMappings());
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) return false;
        if (!(obj instanceof ClassMapping)) return false;
        final ClassMapping that = (ClassMapping) obj;
        return Objects.equals(this.fields, that.fields) &&
                Objects.equals(this.methods, that.methods) &&
                Objects.equals(this.innerClasses, that.innerClasses);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.fields, this.methods, this.innerClasses);
    }

}

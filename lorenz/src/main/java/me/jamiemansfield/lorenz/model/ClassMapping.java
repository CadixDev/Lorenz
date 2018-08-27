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

import me.jamiemansfield.bombe.type.FieldType;
import me.jamiemansfield.bombe.type.signature.FieldSignature;
import me.jamiemansfield.bombe.type.signature.MethodSignature;

import java.util.Collection;
import java.util.Optional;

/**
 * Represents a de-obfuscation mapping for classes.
 *
 * @param <M> The type of the mapping
 *
 * @author Jamie Mansfield
 * @since 0.1.0
 */
public interface ClassMapping<M extends ClassMapping> extends Mapping<M> {

    /**
     * {@inheritDoc}
     * @see Class#getSimpleName()
     * @see <a href="https://docs.oracle.com/javase/specs/jls/se10/html/jls-13.html#jls-13.1">Specification</a>
     */
    @Override
    String getSimpleObfuscatedName();

    /**
     * {@inheritDoc}
     * @see Class#getSimpleName()
     * @see <a href="https://docs.oracle.com/javase/specs/jls/se10/html/jls-13.html#jls-13.1">Specification</a>
     */
    @Override
    String getSimpleDeobfuscatedName();

    /**
     * Gets an immutable collection of all of the field mappings
     * of the class mapping.
     *
     * @return The field mappings
     */
    Collection<FieldMapping> getFieldMappings();

    /**
     * Gets the field mapping of the given signature of the
     * class mapping, should it exist.
     *
     * @param signature The signature of the field
     * @return The field mapping, wrapped in an {@link Optional}
     * @since 0.4.0
     */
    Optional<FieldMapping> getFieldMapping(final FieldSignature signature);

    /**
     * Gets the field mapping of the given obfuscated name of the
     * class mapping, should it exist.
     *
     * @param obfuscatedName The obfuscated name of the field mapping
     * @return The field mapping, wrapped in an {@link Optional}
     */
    default Optional<FieldMapping> getFieldMapping(final String obfuscatedName) {
        return this.getFieldMapping(new FieldSignature(obfuscatedName, (FieldType) null));
    }

    /**
     * Creates a new field mapping, attached to this class mapping, using
     * the given signature and de-obfuscated name.
     *
     * @param signature The signature of the field
     * @param deobfuscatedName The de-obfuscated name of the field
     * @return The field mapping
     * @since 0.4.0
     */
    FieldMapping createFieldMapping(final FieldSignature signature, final String deobfuscatedName);

    /**
     * Creates a new field mapping, attached to this class mapping, using
     * the given obfuscated and de-obfuscated name.
     *
     * @param obfuscatedName The obfuscated name of the field
     * @param deobfuscatedName The de-obfuscated name of the field
     * @return The field mapping
     */
    default FieldMapping createFieldMapping(final String obfuscatedName, final String deobfuscatedName) {
        return this.createFieldMapping(new FieldSignature(obfuscatedName, (FieldType) null), deobfuscatedName);
    }

    /**
     * Creates a new field mapping, attached to this class mapping, using
     * the given obfuscated name.
     *
     * @param signature The signature of the field
     * @return The field mapping
     * @since 0.4.0
     */
    default FieldMapping createFieldMapping(final FieldSignature signature) {
        return this.createFieldMapping(signature, signature.getName());
    }

    /**
     * Creates a new field mapping, attached to this class mapping, using
     * the given obfuscated name.
     *
     * @param obfuscatedName The obfuscated name of the field
     * @return The field mapping
     */
    default FieldMapping createFieldMapping(final String obfuscatedName) {
        return this.createFieldMapping(obfuscatedName, obfuscatedName);
    }

    /**
     * Gets, or creates should it not exist, a field mapping of the
     * given signature.
     *
     * @param signature The signature of the field mapping
     * @return The field mapping
     * @since 0.4.0
     */
    default FieldMapping getOrCreateFieldMapping(final FieldSignature signature) {
        return this.getFieldMapping(signature)
                .orElseGet(() -> this.createFieldMapping(signature));
    }

    /**
     * Gets, or creates should it not exist, a field mapping of the
     * given obfuscated name.
     *
     * @param obfuscatedName The obfuscated name of the field mapping
     * @return The field mapping
     */
    default FieldMapping getOrCreateFieldMapping(final String obfuscatedName) {
        return this.getFieldMapping(obfuscatedName)
                .orElseGet(() -> this.createFieldMapping(obfuscatedName));
    }

    /**
     * Gets, or creates should it not exist, a field mapping of the
     * given signature.
     *
     * @param obfuscatedName The obfuscated name of the field mapping
     * @param obfuscatedDescriptor The obfuscated descriptor of the field mapping
     * @return The field mapping
     * @since 0.4.0
     */
    default FieldMapping getOrCreateFieldMapping(final String obfuscatedName, final String obfuscatedDescriptor) {
        return this.getOrCreateFieldMapping(new FieldSignature(obfuscatedName, FieldType.of(obfuscatedDescriptor)));
    }

    /**
     * Establishes whether the class mapping contains a field mapping
     * of the given signature.
     *
     * @param signature The signature of the field mapping
     * @return {@code true} should a field mapping of the given
     *         signature exists in the class mapping;
     *         {@code false} otherwise
     * @since 0.4.0
     */
    boolean hasFieldMapping(final FieldSignature signature);

    /**
     * Establishes whether the class mapping contains a field mapping
     * of the given obfuscated name.
     *
     * @param obfuscatedName The obfuscated name of the field mapping
     * @return {@code true} should a field mapping of the given
     *         obfuscated name exists in the class mapping;
     *         {@code false} otherwise
     */
    default boolean hasFieldMapping(final String obfuscatedName) {
        return this.hasFieldMapping(new FieldSignature(obfuscatedName, (FieldType) null));
    }

    /**
     * Gets an immutable collection of all of the method mappings
     * of the class mapping.
     *
     * @return The method mappings
     */
    Collection<MethodMapping> getMethodMappings();

    /**
     * Gets the method mapping of the given method signature of the
     * class mapping, should it exist.
     *
     * @param signature The signature of the method mapping
     * @return The method mapping, wrapped in an {@link Optional}
     */
    Optional<MethodMapping> getMethodMapping(final MethodSignature signature);

    /**
     * Creates a new method mapping, attached to this class mapping, using
     * the given method signature and de-obfuscated name.
     *
     * @param signature The method signature
     * @param deobfuscatedName The de-obfuscated name of the method
     * @return The method mapping
     */
    MethodMapping createMethodMapping(final MethodSignature signature, final String deobfuscatedName);

    /**
     * Creates a new method mapping, attached to this class mapping, using
     * the given method signature.
     *
     * @param signature The method signature
     * @return The method mapping
     */
    default MethodMapping createMethodMapping(final MethodSignature signature) {
        return this.createMethodMapping(signature, signature.getName());
    }

    /**
     * Creates a new method mapping, attached to this class mapping, using
     * the given obfuscated method name and descriptor.
     *
     * @param obfuscatedName The obfuscated method name
     * @param obfuscatedDescriptor The obfuscated method descriptor
     * @return The method mapping
     */
    default MethodMapping createMethodMapping(final String obfuscatedName, final String obfuscatedDescriptor) {
        return this.createMethodMapping(new MethodSignature(obfuscatedName, obfuscatedDescriptor));
    }

    /**
     * Gets, or creates should it not exist, a method mapping of the
     * given obfuscated signature.
     *
     * @param signature The signature of the method mapping
     * @return The method mapping
     */
    default MethodMapping getOrCreateMethodMapping(final MethodSignature signature) {
        return this.getMethodMapping(signature)
                .orElseGet(() -> this.createMethodMapping(signature));
    }

    /**
     * Gets, or creates should it not exist, a method mapping of the
     * given obfuscated name, and descriptor.
     *
     * @param obfuscatedName The obfuscated name of the method mapping
     * @param obfuscatedDescriptor The obfuscated descriptor of the method mapping
     * @return The method mapping
     */
    default MethodMapping getOrCreateMethodMapping(final String obfuscatedName, final String obfuscatedDescriptor) {
        return this.getOrCreateMethodMapping(new MethodSignature(obfuscatedName, obfuscatedDescriptor));
    }

    /**
     * Establishes whether the class mapping contains a method mapping
     * of the given obfuscated name.
     *
     * @param signature The signature of the method mapping
     * @return {@code true} should a method mapping of the given
     *         obfuscated name exist in the class mapping;
     *         {@code false} otherwise
     */
    boolean hasMethodMapping(final MethodSignature signature);

    /**
     * Gets an immutable collection of all of the inner class
     * mappings of the class mapping.
     *
     * @return The inner class mappings
     */
    Collection<InnerClassMapping> getInnerClassMappings();

    /**
     * Gets the inner class mapping of the given obfuscated name of the
     * class mapping, should it exist.
     *
     * @param obfuscatedName The obfuscated name of the inner class mapping
     * @return The inner class mapping, wrapped in an {@link Optional}
     */
    Optional<InnerClassMapping> getInnerClassMapping(final String obfuscatedName);

    /**
     * Creates a new inner class mapping, attached to this class mapping, using
     * the given obfuscated and de-obfuscated class name.
     *
     * @param obfuscatedName The obfuscated class name
     * @param deobfuscatedName The de-obfuscated class name
     * @return The class mapping
     */
    InnerClassMapping createInnerClassMapping(final String obfuscatedName, final String deobfuscatedName);

    /**
     * Creates a new inner class mapping, attached to this class mapping, using
     * the given obfuscated class name.
     *
     * @param obfuscatedName The obfuscated class name
     * @return The class mapping
     */
    default InnerClassMapping createInnerClassMapping(final String obfuscatedName) {
        return this.createInnerClassMapping(obfuscatedName, obfuscatedName);
    }

    /**
     * Gets, or creates should it not exist, a inner class mapping of the
     * given obfuscated name.
     *
     * @param obfuscatedName The obfuscated name of the inner class mapping
     * @return The inner class mapping
     */
    default InnerClassMapping getOrCreateInnerClassMapping(final String obfuscatedName) {
        return this.getInnerClassMapping(obfuscatedName)
                .orElseGet(() -> this.createInnerClassMapping(obfuscatedName));
    }

    /**
     * Establishes whether the class mapping contains a inner class
     * mapping of the given obfuscated name.
     *
     * @param obfuscatedName The obfuscated name of the inner class
     *                       mapping
     * @return {@code true} should a inner class mapping of the
     *         given obfuscated name exist in the class mapping;
     *         {@code false} otherwise
     */
    boolean hasInnerClassMapping(final String obfuscatedName);

    /**
     * Establishes whether the class mapping has a de-obfuscation mapping, or
     * has some mappings within it.
     *
     * @return {@code true} if the class mappings has mappings;
     *         {@code false} otherwise
     */
    default boolean hasMappings() {
        return this.hasDeobfuscatedName() ||
                this.getFieldMappings().stream().anyMatch(Mapping::hasDeobfuscatedName) ||
                this.getMethodMappings().stream().anyMatch(Mapping::hasDeobfuscatedName) ||
                this.getInnerClassMappings().stream().anyMatch(ClassMapping::hasMappings);
    }

}

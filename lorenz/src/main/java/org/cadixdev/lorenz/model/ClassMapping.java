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

package org.cadixdev.lorenz.model;

import org.cadixdev.bombe.analysis.InheritanceCompletable;
import org.cadixdev.bombe.analysis.InheritanceProvider;
import org.cadixdev.bombe.type.FieldType;
import org.cadixdev.bombe.type.signature.FieldSignature;
import org.cadixdev.lorenz.model.container.InnerClassContainer;
import org.cadixdev.lorenz.model.container.MappingContainer;
import org.cadixdev.lorenz.model.container.MethodContainer;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

/**
 * Represents a de-obfuscation mapping for classes.
 *
 * @param <M> The type of the mapping
 * @param <P> The type of the parent
 *
 * @author Jamie Mansfield
 * @since 0.1.0
 */
public interface ClassMapping<M extends ClassMapping<M, P>, P> extends Mapping<M, P>, InheritanceCompletable {

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
     * Gets the obfuscated package name of this class mapping.
     *
     * <p>Default package is represented using an empty string.</p>
     *
     * @return The obfuscated package name
     * @since 0.4.0
     */
    String getObfuscatedPackage();

    /**
     * Gets the de-obfuscated package name of this class mapping.
     *
     * <p>Default package is represented using an empty string.</p>
     *
     * @return The de-obfuscated package name
     * @since 0.4.0
     */
    String getDeobfuscatedPackage();

    /**
     * Gets an immutable collection of all of the field mappings
     * of the class mapping.
     *
     * @return The field mappings
     */
    Collection<FieldMapping> getFieldMappings();

    /**
     * Gets an immutable map of all of the field mappings
     * of the class mapping, by the field name.
     *
     * @return The field mappings by name
     * @since 0.4.0
     */
    Map<String, FieldMapping> getFieldsByName();

    /**
     * Gets the field mapping of the given signature of the
     * class mapping, should it exist.
     *
     * <p><strong>Note:</strong> The field signature is looked up as-is,
     * so if the loaded mappings use field types, looking up a signature
     * without type will fail. Consider using {@link #getFieldMapping(String)}
     * or {@link #computeFieldMapping(FieldSignature)}.</p>
     *
     * @param signature The signature of the field
     * @return The field mapping, wrapped in an {@link Optional}
     * @since 0.4.0
     * @see #getFieldMapping(String)
     * @see #computeFieldMapping(FieldSignature)
     */
    Optional<FieldMapping> getFieldMapping(final FieldSignature signature);

    /**
     * Gets a field mapping of the given obfuscated name of the
     * class mapping, should it exist. If multiple fields mappings with
     * the same name (but different types) exist, only one of them will
     * be returned.
     *
     * <p><strong>Note:</strong> This is <strong>not</strong> equivalent
     * to calling {@link #getFieldMapping(FieldSignature)} with a
     * {@code null} field type. Use {@link #computeFieldMapping(FieldSignature)}
     * to flexibly lookup field signatures with or without type.</p>
     *
     * @param obfuscatedName The obfuscated name of the field mapping
     * @return The field mapping, wrapped in an {@link Optional}
     * @see #getFieldMapping(FieldSignature)
     * @see #computeFieldMapping(FieldSignature)
     */
    Optional<FieldMapping> getFieldMapping(final String obfuscatedName);

    /**
     * Attempts to locate a field mapping for the given obfuscated field
     * signature. Unlike {@link #getFieldMapping(FieldSignature)} this method
     * will attempt to match the field signature with or without type:
     *
     * <p>If {@link FieldSignature#getType()} is empty,
     * {@link #getFieldMapping(String)} is returned.
     * Otherwise, the signature is looked up with type. If that fails, the
     * signature is looked up again without type. Note that it will insert
     * a new {@link FieldMapping} with the specified type for caching purposes.</p>
     *
     * @param signature The (obfuscated) signature of the field
     * @return The field mapping, wrapped in an {@link Optional}
     * @see #getFieldMapping(FieldSignature)
     * @see #getFieldMapping(String)
     */
    Optional<FieldMapping> computeFieldMapping(final FieldSignature signature);

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
        return this.createFieldMapping(new FieldSignature(obfuscatedName), deobfuscatedName);
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
     * Gets, or creates should it not exist, a field mapping of the
     * given signature.
     *
     * @param obfuscatedName The obfuscated name of the field mapping
     * @param obfuscatedDescriptor The obfuscated descriptor of the field mapping
     * @return The field mapping
     * @since 0.6.0
     */
    default FieldMapping getOrCreateFieldMapping(final String obfuscatedName, final FieldType obfuscatedDescriptor) {
        return this.getOrCreateFieldMapping(new FieldSignature(obfuscatedName, obfuscatedDescriptor));
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
        return this.hasFieldMapping(new FieldSignature(obfuscatedName));
    }

    /**
     * Gets the {@link MappingContainer mapping container} for {@link FieldMapping
     * field mappings}.
     *
     * @return The method mapping container
     * @since 0.6.0
     */
    MethodContainer<M> methods();

    /**
     * Gets the {@link MappingContainer mapping container} for {@link
     * InnerClassMapping inner class mappings}.
     *
     * @return The inner class mapping container
     * @since 0.6.0
     */
    InnerClassContainer<M> innerClasses();

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
                this.methods().getAll().stream().anyMatch(MethodMapping::hasMappings) ||
                this.innerClasses().getAll().stream().anyMatch(ClassMapping::hasMappings);
    }

    @Override
    default Optional<InheritanceProvider.ClassInfo> provideInheritance(final InheritanceProvider provider, final Object context) {
        return provider.provide(this.getFullObfuscatedName(), context);
    }

}

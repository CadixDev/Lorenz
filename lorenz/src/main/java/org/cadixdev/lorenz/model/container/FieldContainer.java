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

import org.cadixdev.bombe.type.signature.FieldSignature;
import org.cadixdev.lorenz.impl.model.FieldMappingImpl;
import org.cadixdev.lorenz.model.ClassMapping;
import org.cadixdev.lorenz.model.FieldMapping;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * A {@link MappingContainer container} of {@link FieldMapping field mappings},
 * identified by signature, or name.
 *
 * @param <P> The type of class mapping, the parent is
 *
 * @author Jamie Mansfield
 * @since 0.6.0
 */
public class FieldContainer<P extends ClassMapping<?, ?>>
        extends SimpleParentedMappingContainer<FieldMapping, FieldSignature, P> {

    private final Map<String, FieldMapping> byName = new HashMap<>();

    /**
     * Creates a field mapping container.
     *
     * @param parent The parent mapping
     */
    public FieldContainer(final P parent) {
        super(parent);
    }

    @Override
    protected FieldMapping create(final FieldSignature signature) {
        final FieldMapping mapping = new FieldMappingImpl(this.parent, signature, signature.getName());
        this.byName.put(signature.getName(), mapping);
        return mapping;
    }

    /**
     * Gets an immutable map of all of the field mappings
     * of the class mapping, by the field name.
     *
     * @return The field mappings by name
     */
    public Map<String, FieldMapping> getAllByName() {
        return Collections.unmodifiableMap(this.byName);
    }

    /**
     * {@inheritDoc}
     *
     * <p><strong>Note:</strong> The field signature is looked up as-is,
     * so if the loaded mappings use field types, looking up a signature
     * without type will fail. Consider using {@link #get(String)}
     * or {@link #compute(FieldSignature)}.</p>
     *
     * @see #get(String)
     * @see #compute(FieldSignature)
     */
    @Override
    public Optional<FieldMapping> get(final FieldSignature signature) {
        return super.get(signature);
    }

    @Override
    public FieldMapping getOrCreate(final FieldSignature signature) {
        return super.getOrCreate(signature);
    }

    /**
     * Gets a field mapping of the given obfuscated name of the
     * class mapping, should it exist. If multiple fields mappings with
     * the same name (but different types) exist, only one of them will
     * be returned.
     *
     * <p><strong>Note:</strong> This is <strong>not</strong> equivalent
     * to calling {@link #get(FieldSignature)} with a {@code null} field
     * type. Use {@link #compute(FieldSignature)} to flexibly lookup field
     * signatures with or without type.</p>
     *
     * @param obfuscatedName The obfuscated name of the field mapping
     * @return The field mapping, wrapped in an {@link Optional}
     *
     * @see #get(FieldSignature)
     * @see #compute(FieldSignature)
     */
    public Optional<FieldMapping> get(final String obfuscatedName) {
        return Optional.ofNullable(this.byName.get(obfuscatedName));
    }

    /**
     * Attempts to locate a field mapping for the given obfuscated field
     * signature. Unlike {@link #get(FieldSignature)} this method will
     * attempt to match the field signature with or without type:
     *
     * <p>If {@link FieldSignature#getType()} is empty, {@link #get(String)}
     * is returned. Otherwise, the signature is looked up with type. If that
     * fails, the signature is looked up again without type. Note that it will
     * insert a new {@link FieldMapping} with the specified type for caching
     * purposes.</p>
     *
     * @param signature The (obfuscated) signature of the field
     * @return The field mapping, wrapped in an {@link Optional}
     *
     * @see #get(FieldSignature)
     * @see #get(String)
     */
    public Optional<FieldMapping> compute(final FieldSignature signature) {
        // If the field type is not provided, lookup up only the field name
        if (!signature.getType().isPresent()) {
            return this.get(signature.getName());
        }

        // Otherwise, look up the signature as-is, but attempt falling back to a signature without type
        // Note: We cannot use fieldsByName here, because we'd eventually return FieldMappings with the wrong type
        return Optional.ofNullable(this.mappings.computeIfAbsent(signature, (sig) -> {
            final FieldMapping mapping = this.mappings.get(new FieldSignature(sig.getName()));
            return mapping != null ?
                    new FieldMappingImpl(mapping.getParent(), sig, mapping.getDeobfuscatedName()) : null;
        }));
    }

}

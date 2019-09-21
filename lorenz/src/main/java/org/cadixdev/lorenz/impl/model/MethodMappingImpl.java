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

package org.cadixdev.lorenz.impl.model;

import org.cadixdev.lorenz.model.ClassMapping;
import org.cadixdev.lorenz.model.MethodMapping;
import org.cadixdev.lorenz.model.MethodParameterMapping;
import org.cadixdev.bombe.type.signature.MethodSignature;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.stream.Collectors;

/**
 * A basic implementation of {@link MethodMapping}.
 *
 * @author Jamie Mansfield
 * @since 0.2.0
 */
public class MethodMappingImpl
        extends AbstractMemberMappingImpl<MethodMapping, ClassMapping<?, ?>>
        implements MethodMapping {

    private final MethodSignature signature;
    private final MethodParameterMapping[] parameters;

    /**
     * Creates a new method mapping, from the given parameters.
     *
     * @param parentClass The class mapping, this mapping belongs to
     * @param signature The signature
     * @param deobfuscatedName The de-obfuscated name
     */
    public MethodMappingImpl(final ClassMapping parentClass, final MethodSignature signature,
            final String deobfuscatedName) {
        super(parentClass, signature.getName(), deobfuscatedName);
        this.signature = signature;
        this.parameters = new MethodParameterMapping[signature.getDescriptor().getParamTypes().size()];
    }

    @Override
    public MethodSignature getSignature() {
        return this.signature;
    }

    @Override
    public Collection<MethodParameterMapping> getParameterMappings() {
        return Collections.unmodifiableList(Arrays.stream(this.parameters)
                .filter(Objects::nonNull).collect(Collectors.toList()));
    }

    private void checkIndex(final int index) {
        if (index < 0 || index >= this.parameters.length) {
            throw new IndexOutOfBoundsException(String.valueOf(index));
        }
    }

    @Override
    public MethodParameterMapping createParameterMapping(final int index, final String deobfuscatedName) {
        this.checkIndex(index);
        final MethodParameterMapping mapping = this.parameters[index];
        if (mapping != null) {
            return mapping.setDeobfuscatedName(deobfuscatedName);
        }
        else {
            return this.parameters[index] = this.getMappings().getModelFactory().createMethodParameterMapping(this, index, deobfuscatedName);
        }
    }

    @Override
    public Optional<MethodParameterMapping> getParameterMapping(final int index) {
        this.checkIndex(index);
        return Optional.ofNullable(this.parameters[index]);
    }

    @Override
    public boolean hasParameterMapping(final int index) {
        this.checkIndex(index);
        return this.parameters[index] != null;
    }

    @Override
    protected StringJoiner buildToString() {
        return super.buildToString()
                .add("obfuscatedSignature=" + this.getObfuscatedDescriptor())
                .add("deobfuscatedSignature=" + this.getDeobfuscatedDescriptor());
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) return true;
        if (!super.equals(obj) || !(obj instanceof MethodMapping)) return false;
        final MethodMapping that = (MethodMapping) obj;
        return Objects.equals(this.signature, that.getSignature());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.signature);
    }

}

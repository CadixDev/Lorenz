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

package me.jamiemansfield.lorenz.impl.model;

import com.google.common.base.MoreObjects;
import me.jamiemansfield.lorenz.MappingSet;
import me.jamiemansfield.lorenz.model.ClassMapping;
import me.jamiemansfield.lorenz.model.FieldMapping;
import me.jamiemansfield.lorenz.model.InnerClassMapping;
import me.jamiemansfield.lorenz.model.MethodMapping;
import me.jamiemansfield.lorenz.model.jar.signature.MethodSignature;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * A basic implementation of {@link ClassMapping}.
 *
 * @param <M> The type of the class mapping
 *
 * @author Jamie Mansfield
 * @since 0.2.0
 */
public abstract class AbstractClassMappingImpl<M extends ClassMapping>
        extends AbstractMappingImpl<M> implements ClassMapping<M> {

    private final Map<String, FieldMapping> fields = new HashMap<>();
    private final Map<MethodSignature, MethodMapping> methods = new HashMap<>();
    private final Map<String, InnerClassMapping> innerClasses = new HashMap<>();

    /**
     * Creates a new class mapping, from the given parameters.
     *
     * @param mappings The mappings set, this mapping belongs to
     * @param obfuscatedName The obfuscated name
     * @param deobfuscatedName The de-obfuscated name
     */
    protected AbstractClassMappingImpl(final MappingSet mappings, final String obfuscatedName, final String deobfuscatedName) {
        super(mappings, obfuscatedName, deobfuscatedName);
    }

    @Override
    public Collection<FieldMapping> getFieldMappings() {
        return Collections.unmodifiableCollection(this.fields.values());
    }

    @Override
    public Optional<FieldMapping> getFieldMapping(final String obfuscatedName) {
        return Optional.ofNullable(this.fields.get(obfuscatedName));
    }

    @Override
    public FieldMapping createFieldMapping(final String obfuscatedName, final String deobfuscatedName) {
        return this.fields.compute(obfuscatedName, (name, existingMapping) -> {
            if (existingMapping != null) return existingMapping.setDeobfuscatedName(deobfuscatedName);
            return this.getMappings().getModelFactory().createFieldMapping(this, obfuscatedName, deobfuscatedName);
        });
    }

    @Override
    public boolean hasFieldMapping(final String obfuscatedName) {
        return this.fields.containsKey(obfuscatedName);
    }

    @Override
    public Collection<MethodMapping> getMethodMappings() {
        return Collections.unmodifiableCollection(this.methods.values());
    }

    @Override
    public Optional<MethodMapping> getMethodMapping(final MethodSignature signature) {
        return Optional.ofNullable(this.methods.get(signature));
    }

    @Override
    public MethodMapping createMethodMapping(final MethodSignature signature, final String deobfuscatedName) {
        return this.methods.compute(signature, (desc, existingMapping) -> {
            if (existingMapping != null) return existingMapping.setDeobfuscatedName(deobfuscatedName);
            return this.getMappings().getModelFactory().createMethodMapping(this, signature, deobfuscatedName);
        });
    }

    @Override
    public boolean hasMethodMapping(final MethodSignature signature) {
        return this.methods.containsKey(signature);
    }

    @Override
    public Collection<InnerClassMapping> getInnerClassMappings() {
        return Collections.unmodifiableCollection(this.innerClasses.values());
    }

    @Override
    public Optional<InnerClassMapping> getInnerClassMapping(final String obfuscatedName) {
        return Optional.ofNullable(this.innerClasses.get(obfuscatedName));
    }

    @Override
    public InnerClassMapping createInnerClassMapping(final String obfuscatedName, final String deobfuscatedName) {
        return this.innerClasses.compute(obfuscatedName, (name, existingMapping) -> {
            if (existingMapping != null) return existingMapping.setDeobfuscatedName(deobfuscatedName);
            return this.getMappings().getModelFactory().createInnerClassMapping(this, obfuscatedName, deobfuscatedName);
        });
    }

    @Override
    public boolean hasInnerClassMapping(final String obfuscatedName) {
        return this.innerClasses.containsKey(obfuscatedName);
    }

    @Override
    protected MoreObjects.ToStringHelper buildToString() {
        return super.buildToString()
                .add("fields", this.getFieldMappings())
                .add("methods", this.getMethodMappings())
                .add("innerClasses", this.getInnerClassMappings());
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) return true;
        if (!super.equals(obj)) return false;
        if (!(obj instanceof AbstractClassMappingImpl)) return false;

        final AbstractClassMappingImpl that = (AbstractClassMappingImpl) obj;
        return Objects.equals(this.fields, that.fields) &&
                Objects.equals(this.methods, that.methods) &&
                Objects.equals(this.innerClasses, that.innerClasses);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.fields, this.methods, this.innerClasses);
    }

}

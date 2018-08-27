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

import me.jamiemansfield.bombe.type.signature.FieldSignature;
import me.jamiemansfield.bombe.type.signature.MethodSignature;
import me.jamiemansfield.lorenz.MappingSet;
import me.jamiemansfield.lorenz.model.ClassMapping;
import me.jamiemansfield.lorenz.model.FieldMapping;
import me.jamiemansfield.lorenz.model.InnerClassMapping;
import me.jamiemansfield.lorenz.model.MethodMapping;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;

/**
 * A basic implementation of {@link ClassMapping}.
 *
 * @param <M> The type of the class mapping
 *
 * @author Jamie Mansfield
 * @since 0.2.0
 */
public abstract class AbstractClassMappingImpl<M extends ClassMapping>
        extends AbstractMappingImpl<M>
        implements ClassMapping<M> {

    private final Map<FieldSignature, FieldMapping> fields = new HashMap<>();
    private final Map<String, FieldMapping> fieldsByName = new HashMap<>();
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
    public Map<String, FieldMapping> getFieldsByName() {
        return Collections.unmodifiableMap(this.fieldsByName);
    }

    @Override
    public Optional<FieldMapping> getFieldMapping(final FieldSignature signature) {
        // If the field type is not provided, lookup up only the field name
        if (!signature.getType().isPresent()) {
            return this.getFieldMapping(signature.getName());
        }

        // Otherwise, look up the signature as-is, but attempt falling back to a signature without type
        // Note: We cannot use fieldsByName here, because we'd eventually return FieldMappings with the wrong type
        return Optional.ofNullable(this.fields.computeIfAbsent(signature, (sig) -> {
            final FieldMapping mapping = this.fields.get(new FieldSignature(sig.getName()));
            return mapping != null ?
                    this.getMappings().getModelFactory().createFieldMapping(mapping.getParent(), sig, mapping.getDeobfuscatedName()) : null;
        }));
    }

    @Override
    public Optional<FieldMapping> getFieldMapping(final String obfuscatedName) {
        return Optional.ofNullable(this.fieldsByName.get(obfuscatedName));
    }

    @Override
    public FieldMapping createFieldMapping(final FieldSignature signature, final String deobfuscatedName) {
        return this.fields.compute(signature, (sig, existingMapping) -> {
            if (existingMapping != null) return existingMapping.setDeobfuscatedName(deobfuscatedName);
            final FieldMapping mapping = this.getMappings().getModelFactory().createFieldMapping(this, sig, deobfuscatedName);
            this.fieldsByName.put(sig.getName(), mapping);
            return mapping;
        });
    }

    @Override
    public boolean hasFieldMapping(final FieldSignature signature) {
        return this.getFieldMapping(signature).isPresent();
    }

    @Override
    public boolean hasFieldMapping(final String obfuscatedName) {
        return this.fieldsByName.containsKey(obfuscatedName);
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
    protected StringJoiner buildToString() {
        return super.buildToString()
                .add("fields=" + this.getFieldMappings())
                .add("methods=" + this.getMethodMappings())
                .add("innerClasses=" + this.getInnerClassMappings());
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) return true;
        if (!super.equals(obj)) return false;
        if (!(obj instanceof ClassMapping)) return false;

        final ClassMapping that = (ClassMapping) obj;
        return Objects.equals(this.getFieldMappings(), that.getFieldMappings()) &&
                Objects.equals(this.getMethodMappings(), that.getMethodMappings()) &&
                Objects.equals(this.getInnerClassMappings(), that.getInnerClassMappings());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.fields, this.methods, this.innerClasses);
    }

}

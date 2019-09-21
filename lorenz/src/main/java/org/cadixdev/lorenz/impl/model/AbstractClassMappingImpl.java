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

import org.cadixdev.bombe.analysis.InheritanceProvider;
import org.cadixdev.bombe.type.signature.FieldSignature;
import org.cadixdev.lorenz.MappingSet;
import org.cadixdev.lorenz.model.ClassMapping;
import org.cadixdev.lorenz.model.FieldMapping;
import org.cadixdev.lorenz.model.MethodMapping;
import org.cadixdev.lorenz.model.container.InnerClassContainer;
import org.cadixdev.lorenz.model.container.MethodContainer;

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
 * @param <P> The type of the parent
 *
 * @author Jamie Mansfield
 * @since 0.2.0
 */
public abstract class AbstractClassMappingImpl<M extends ClassMapping<M, P>, P>
        extends AbstractMappingImpl<M, P>
        implements ClassMapping<M, P> {

    private final Map<FieldSignature, FieldMapping> fields = new HashMap<>();
    private final Map<String, FieldMapping> fieldsByName = new HashMap<>();
    private final MethodContainer<M> methods = new MethodContainer<>((M) this); // todo: look into types
    private final InnerClassContainer<M> innerClasses = new InnerClassContainer<>((M) this);
    private boolean complete;

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
        return Optional.ofNullable(this.fields.get(signature));
    }

    @Override
    public Optional<FieldMapping> getFieldMapping(final String obfuscatedName) {
        return Optional.ofNullable(this.fieldsByName.get(obfuscatedName));
    }

    @Override
    public Optional<FieldMapping> computeFieldMapping(final FieldSignature signature) {
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
    public MethodContainer<M> methods() {
        return this.methods;
    }

    @Override
    public InnerClassContainer<M> innerClasses() {
        return this.innerClasses;
    }

    @Override
    protected StringJoiner buildToString() {
        return super.buildToString()
                .add("fields=" + this.getFieldMappings())
                .add("methods=" + this.methods.getAll())
                .add("innerClasses=" + this.innerClasses.getAll());
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) return true;
        if (!super.equals(obj)) return false;
        if (!(obj instanceof ClassMapping)) return false;

        final ClassMapping that = (ClassMapping) obj;
        return Objects.equals(this.getFieldMappings(), that.getFieldMappings()) &&
                Objects.equals(this.methods(), that.methods()) && // TODO: Implement equals in containers
                Objects.equals(this.innerClasses(), that.innerClasses());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.fields, this.methods, this.innerClasses);
    }

    @Override
    public boolean isComplete() {
        return this.complete;
    }

    @Override
    public void complete(final InheritanceProvider provider, final InheritanceProvider.ClassInfo info) {
        if (this.complete) {
            return;
        }

        for (final InheritanceProvider.ClassInfo parent : info.provideParents(provider)) {
            final ClassMapping<?, ?> parentMappings = this.getMappings().resolveOrCreate(parent.getName());
            parentMappings.complete(provider, parent);

            for (final FieldMapping mapping : parentMappings.getFieldMappings()) {
                if (parent.canInherit(info, mapping.getSignature())) {
                    this.fields.putIfAbsent(mapping.getSignature(), mapping);
                }
            }

            for (final MethodMapping mapping : parentMappings.methods().getAll()) {
                this.methods.complete(parent, info, mapping);
            }
        }

        this.complete = true;
    }

}

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

package org.cadixdev.lorenz;

import org.cadixdev.bombe.type.ArrayType;
import org.cadixdev.bombe.type.FieldType;
import org.cadixdev.bombe.type.MethodDescriptor;
import org.cadixdev.bombe.type.ObjectType;
import org.cadixdev.bombe.type.Type;
import org.cadixdev.lorenz.impl.MappingSetImpl;
import org.cadixdev.lorenz.model.ClassMapping;
import org.cadixdev.lorenz.model.TopLevelClassMapping;
import org.cadixdev.lorenz.model.container.ClassContainer;
import org.cadixdev.lorenz.model.jar.CascadingFieldTypeProvider;
import org.cadixdev.lorenz.model.jar.FieldTypeProvider;
import org.cadixdev.lorenz.util.Reversible;

import java.util.stream.Collectors;

/**
 * The container for {@link TopLevelClassMapping}s, allowing for their creating and
 * locating any {@link ClassMapping}.
 *
 * @author Jamie Mansfield
 * @since 0.1.0
 */
public interface MappingSet extends ClassContainer<TopLevelClassMapping>, Reversible<MappingSet, MappingSet> {

    /**
     * Creates a mapping set, using the default Lorenz model implementation.
     *
     * @return The mapping set
     * @since 0.2.0
     */
    static MappingSet create() {
        return new MappingSetImpl();
    }

    /**
     * Creates a mapping set, using the given model factory.
     *
     * @param modelFactory The model factory to use
     * @return The mapping set
     * @since 0.3.0
     */
    static MappingSet create(final MappingSetModelFactory modelFactory) {
        return new MappingSetImpl(modelFactory);
    }

    /**
     * Gets the underlying model factory, that is used to construct
     * the implementation classes for all the models.
     *
     * @return The model factory
     * @since 0.3.0
     */
    MappingSetModelFactory getModelFactory();

    /**
     * Gets the field type provider in use for this set of mappings.
     *
     * @return The field type provider
     * @since 0.4.0
     */
    CascadingFieldTypeProvider getFieldTypeProvider();

    /**
     * Adds the given {@link FieldTypeProvider} to this set of mappings.
     *
     * @param fieldTypeProvider The field type provider
     * @return {@code this}, for chaining
     * @see CascadingFieldTypeProvider#add(FieldTypeProvider)
     * @since 0.4.0
     */
    default MappingSet addFieldTypeProvider(final FieldTypeProvider fieldTypeProvider) {
        this.getFieldTypeProvider().add(fieldTypeProvider);
        return this;
    }

    /**
     * Removes the given {@link FieldTypeProvider} to this set of mappings.
     *
     * @param fieldTypeProvider The field type provider
     * @return {@code this}, for chaining
     * @see CascadingFieldTypeProvider#remove(FieldTypeProvider)
     * @since 0.4.0
     */
    default MappingSet removeFieldTypeProvider(final FieldTypeProvider fieldTypeProvider) {
        this.getFieldTypeProvider().remove(fieldTypeProvider);
        return this;
    }

    /**
     * Gets the de-obfuscated view of the given type.
     *
     * @param type The type to de-obfuscate
     * @return The de-obfuscated type
     * @since 0.5.0
     */
    default Type deobfuscate(final Type type) {
        if (type instanceof FieldType) {
            return this.deobfuscate(this.deobfuscate((FieldType) type));
        }
        return type;
    }

    /**
     * Gets the de-obfuscated view of the given field type.
     *
     * @param type The type to de-obfuscate
     * @return The de-obfuscated type
     * @since 0.5.0
     */
    default FieldType deobfuscate(final FieldType type) {
        if (type instanceof ArrayType) {
            final ArrayType arr = (ArrayType) type;
            final FieldType component = this.deobfuscate(arr.getComponent());
            return component == arr.getComponent() ?
                    arr :
                    new ArrayType(arr.getDimCount(), component);
        }
        else if (type instanceof ObjectType) {
            final ObjectType obj = (ObjectType) type;
            return this.get(obj.getClassName())
                    .map(m -> new ObjectType(m.getFullDeobfuscatedName()))
                    .orElse(obj);
        }
        return type;
    }

    /**
     * Gets the de-obfuscated descriptor of the method.
     *
     * @param descriptor The descriptor to de-obfuscate
     * @return The de-obfuscated descriptor
     * @since 0.5.0
     */
    default MethodDescriptor deobfuscate(final MethodDescriptor descriptor) {
        return new MethodDescriptor(
                descriptor.getParamTypes().stream()
                        .map(this::deobfuscate)
                        .collect(Collectors.toList()),
                this.deobfuscate(descriptor.getReturnType())
        );
    }

    /**
     * Produces a new mapping set that is a reverse copy of the original.
     *
     * @return The reversed set
     * @since 0.5.0
     */
    default MappingSet reverse() {
        return this.reverse(MappingSet.create());
    }

    @Override
    default MappingSet reverse(final MappingSet parent) {
        this.getAll().forEach(klass -> klass.reverse(parent));
        return parent;
    }

    /**
     * Produces a new mapping set, that is a merged copy with the provided
     * mappings.
     *
     * @param with The set to merge with
     * @return The merged set
     * @since 0.5.0
     */
    default MappingSet merge(final MappingSet with) {
        return this.merge(with, MappingSet.create());
    }

    /**
     * Produces a new mapping set, that is a merged copy with the provided
     * mappings.
     *
     * @param with The set to merge with
     * @param parent The set to create entries
     * @return The merged set
     * @since 0.5.0
     */
    default MappingSet merge(final MappingSet with, final MappingSet parent) {
        this.getAll().forEach(klass -> {
            final TopLevelClassMapping klassWith = with.getOrCreate(klass.getDeobfuscatedName());
            klass.merge(klassWith, parent);
        });
        return parent;
    }

    /**
     * Produces a new mapping set, which is a clone copy of the original.
     *
     * @return The cloned set
     * @since 0.5.0
     */
    default MappingSet copy() {
        final MappingSet mappings = MappingSet.create();
        this.getAll().forEach(klass -> klass.copy(mappings));
        return mappings;
    }

}

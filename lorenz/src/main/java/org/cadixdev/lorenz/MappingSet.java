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
import org.cadixdev.lorenz.impl.MappingSetModelFactoryImpl;
import org.cadixdev.lorenz.merge.MappingSetMerger;
import org.cadixdev.lorenz.model.ClassMapping;
import org.cadixdev.lorenz.model.InnerClassMapping;
import org.cadixdev.lorenz.model.TopLevelClassMapping;
import org.cadixdev.lorenz.model.jar.CompositeFieldTypeProvider;
import org.cadixdev.lorenz.model.jar.FieldTypeProvider;
import org.cadixdev.lorenz.util.BinaryTool;
import org.cadixdev.lorenz.util.Reversible;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * The container for {@link TopLevelClassMapping}s, allowing for their creating and
 * locating any {@link ClassMapping}.
 *
 * @author Jamie Mansfield
 * @since 0.1.0
 */
public class MappingSet implements Reversible<MappingSet, MappingSet>, Iterable<TopLevelClassMapping> {

    /**
     * Creates a mapping set, using the default Lorenz model implementation.
     *
     * @return The mapping set
     * @since 0.2.0
     * @deprecated Use {@link MappingSet#MappingSet()} instead
     */
    @Deprecated
    public static MappingSet create() {
        return new MappingSet();
    }

    /**
     * Creates a mapping set, using the given model factory.
     *
     * @param modelFactory The model factory to use
     * @return The mapping set
     * @since 0.3.0
     * @deprecated Use {@link MappingSet#MappingSet(MappingSetModelFactory)} instead
     */
    @Deprecated
    public static MappingSet create(final MappingSetModelFactory modelFactory) {
        return new MappingSet(modelFactory);
    }

    private final MappingSetModelFactory modelFactory;
    private final Map<String, TopLevelClassMapping> topLevelClasses = new ConcurrentHashMap<>();
    private final CompositeFieldTypeProvider fieldTypeProvider = new CompositeFieldTypeProvider();

    /**
     * Creates a mapping set using the default {@link MappingSetModelFactory}.
     */
    public MappingSet() {
        this(MappingSetModelFactoryImpl.INSTANCE);
    }

    /**
     * Creates a mapping set using the provided {@link MappingSetModelFactory}.
     *
     * @param modelFactory The model factory to use
     */
    public MappingSet(final MappingSetModelFactory modelFactory) {
        this.modelFactory = modelFactory;
    }

    /**
     * Gets the underlying model factory, that is used to construct
     * the implementation classes for all the models.
     *
     * @return The model factory
     */
    public MappingSetModelFactory getModelFactory() {
        return this.modelFactory;
    }

    /**
     * Gets an immutable collection of all of the top-level class
     * mappings of the mapping set.
     *
     * @return The top-level class mappings
     */
    public Collection<TopLevelClassMapping> getTopLevelClassMappings() {
        return Collections.unmodifiableCollection(this.topLevelClasses.values());
    }

    /**
     * Creates a top-level class mapping with the given obfuscated and de-obfuscated
     * names.
     *
     * @param obfuscatedName The obfuscated name of the top-level class
     * @param deobfuscatedName The de-obfuscated name of the top-level class
     * @return The top-level class mapping, to allow for chaining
     */
    public TopLevelClassMapping createTopLevelClassMapping(final String obfuscatedName, final String deobfuscatedName) {
        return this.topLevelClasses.compute(obfuscatedName.replace('.', '/'), (name, existingMapping) -> {
            if (existingMapping != null) return existingMapping.setDeobfuscatedName(deobfuscatedName);
            return this.getModelFactory().createTopLevelClassMapping(this, name, deobfuscatedName);
        });
    }

    /**
     * Gets the top-level class mapping of the given obfuscated name of the
     * class mapping, should it exist.
     *
     * @param obfuscatedName The obfuscated name of the top-level class mapping
     * @return The top-level class mapping, wrapped in an {@link Optional}
     */
    public Optional<TopLevelClassMapping> getTopLevelClassMapping(final String obfuscatedName) {
        return Optional.ofNullable(this.topLevelClasses.get(obfuscatedName.replace('.', '/')));
    }

    /**
     * Gets, or creates should it not exist, a top-level class mapping of the
     * given obfuscated name.
     *
     * @param obfuscatedName The obfuscated name of the top-level class mapping
     * @return The top-level class mapping
     */
    public TopLevelClassMapping getOrCreateTopLevelClassMapping(final String obfuscatedName) {
        return this.getTopLevelClassMapping(obfuscatedName)
                .orElseGet(() -> this.createTopLevelClassMapping(obfuscatedName, obfuscatedName));
    }

    /**
     * Establishes whether the mapping set contains a top-level class
     * mapping of the given obfuscated name.
     *
     * @param obfuscatedName The obfuscated name of the top-level class
     *                       mapping
     * @return {@code true} should a top-level class mapping of the
     *         given obfuscated name exist in the mapping set;
     *         {@code false} otherwise
     */
    public boolean hasTopLevelClassMapping(final String obfuscatedName) {
        return this.topLevelClasses.containsKey(obfuscatedName.replace('.', '/'));
    }

    /**
     * Gets the class mapping of the given obfuscated name.
     *
     * @param obfuscatedName The obfuscated name
     * @return The class mapping, wrapped in an {@link Optional}
     */
    public Optional<? extends ClassMapping<?, ?>> getClassMapping(final String obfuscatedName) {
        final int lastIndex = obfuscatedName.lastIndexOf('$');
        if (lastIndex == -1) return this.getTopLevelClassMapping(obfuscatedName);

        // Split the obfuscated name, to fetch the parent class name, and inner class name
        final String parentClassName = obfuscatedName.substring(0, lastIndex);
        final String innerClassName = obfuscatedName.substring(lastIndex + 1);

        // Get the parent class
        return this.getClassMapping(parentClassName)
                // Get and return the inner class
                .flatMap(parentClassMapping -> parentClassMapping.getInnerClassMapping(innerClassName));
    }

    /**
     * Remove the class mapping for the given obfuscated name.
     *
     * @param obfuscatedName The class name to remove.
     */
    public void removeClassMapping(final String obfuscatedName) {
        this.getClassMapping(obfuscatedName).ifPresent(this::removeClassMapping);
    }

    /**
     * Remove the given {@link ClassMapping}.
     *
     * @param mapping The mapping to remove.
     */
    public void removeClassMapping(final ClassMapping<?, ?> mapping) {
        if (mapping instanceof InnerClassMapping) {
            ((InnerClassMapping) mapping).getParent().removeInnerClassMapping(mapping);
        } else {
            this.topLevelClasses.values().remove(mapping);
        }
    }

    /**
     * Attempts to locate a class mapping for the given obfuscated name.
     *
     * <p>This is equivalent to calling {@link #getClassMapping(String)},
     * except that it will insert a new inner class mapping in case a
     * class mapping for the outer class exists.</p>
     *
     * <p>This method exists to simplify remapping, where it is important
     * to keep inner classes a part of the outer class.</p>
     *
     * @param obfuscatedName The obfuscated name
     * @return The class mapping, wrapped in an {@link Optional}
     */
    public Optional<? extends ClassMapping<?, ?>> computeClassMapping(final String obfuscatedName) {
        final int lastIndex = obfuscatedName.lastIndexOf('$');
        if (lastIndex == -1) return this.getTopLevelClassMapping(obfuscatedName);

        // Split the obfuscated name, to fetch the parent class name, and inner class name
        final String parentClassName = obfuscatedName.substring(0, lastIndex);
        final String innerClassName = obfuscatedName.substring(lastIndex + 1);

        // Get the parent class
        return this.getClassMapping(parentClassName)
                // Get and return the inner class
                .map(parentClassMapping -> parentClassMapping.getOrCreateInnerClassMapping(innerClassName));
    }

    /**
     * Gets, or creates should it not exist, a class mapping, of the given
     * obfuscated name.
     *
     * @param obfuscatedName The obfuscated name of the class mapping
     * @return The class mapping
     */
    public ClassMapping<?, ?> getOrCreateClassMapping(final String obfuscatedName) {
        final int lastIndex = obfuscatedName.lastIndexOf('$');
        if (lastIndex == -1) return this.getOrCreateTopLevelClassMapping(obfuscatedName);

        // Split the obfuscated name, to fetch the parent class name, and inner class name
        final String parentClassName = obfuscatedName.substring(0, lastIndex);
        final String innerClassName = obfuscatedName.substring(lastIndex + 1);

        // Get the parent class
        final ClassMapping parentClass = this.getOrCreateClassMapping(parentClassName);

        // Get the inner class
        return parentClass.getOrCreateInnerClassMapping(innerClassName);
    }

    /**
     * Gets the field type provider in use for this set of mappings.
     *
     * @return The field type provider
     * @since 0.4.0
     */
    public CompositeFieldTypeProvider getFieldTypeProvider() {
        return this.fieldTypeProvider;
    }

    /**
     * Adds the given {@link FieldTypeProvider} to this set of mappings.
     *
     * @param fieldTypeProvider The field type provider
     * @return {@code this}, for chaining
     * @see CompositeFieldTypeProvider#add(FieldTypeProvider)
     * @since 0.4.0
     */
    public MappingSet addFieldTypeProvider(final FieldTypeProvider fieldTypeProvider) {
        this.getFieldTypeProvider().add(fieldTypeProvider);
        return this;
    }

    /**
     * Removes the given {@link FieldTypeProvider} to this set of mappings.
     *
     * @param fieldTypeProvider The field type provider
     * @return {@code this}, for chaining
     * @see CompositeFieldTypeProvider#remove(FieldTypeProvider)
     * @since 0.4.0
     */
    public MappingSet removeFieldTypeProvider(final FieldTypeProvider fieldTypeProvider) {
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
    public Type deobfuscate(final Type type) {
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
    public FieldType deobfuscate(final FieldType type) {
        if (type instanceof ArrayType) {
            final ArrayType arr = (ArrayType) type;
            final FieldType component = this.deobfuscate(arr.getComponent());
            return component == arr.getComponent() ?
                    arr :
                    new ArrayType(arr.getDimCount(), component);
        }
        else if (type instanceof ObjectType) {
            final ObjectType obj = (ObjectType) type;

            final String[] name = BinaryTool.from(obj.getClassName());

            ClassMapping<?, ?> currentClass = this.getClassMapping(name[0]).orElse(null);
            if (currentClass == null) {
                return type;
            }

            for (int i = 1; i < name.length; i++) {
                final ClassMapping<?, ?> thisClass = currentClass.getInnerClassMapping(name[i]).orElse(null);
                if (thisClass == null) {
                    final String[] result = new String[name.length - i + 1];
                    result[0] = currentClass.getFullDeobfuscatedName();
                    System.arraycopy(name, i, result, 1, name.length - i);

                    return new ObjectType(BinaryTool.to(result));
                }
                currentClass = thisClass;
            }

            return new ObjectType(currentClass.getFullDeobfuscatedName());
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
    public MethodDescriptor deobfuscate(final MethodDescriptor descriptor) {
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
    public MappingSet reverse() {
        return this.reverse(this.createMappingSet());
    }

    @Override
    public MappingSet reverse(final MappingSet parent) {
        this.getTopLevelClassMappings().forEach(klass -> klass.reverse(parent));
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
    public MappingSet merge(final MappingSet with) {
        return this.merge(with, this.createMappingSet());
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
    public MappingSet merge(final MappingSet with, final MappingSet parent) {
        return MappingSetMerger.create(this, with).merge(parent);
    }

    /**
     * Produces a new mapping set, which is a clone copy of the original.
     *
     * @return The cloned set
     * @since 0.5.0
     */
    public MappingSet copy() {
        final MappingSet mappings = this.createMappingSet();
        this.getTopLevelClassMappings().forEach(klass -> klass.copy(mappings));
        return mappings;
    }

    @Override
    public Iterator<TopLevelClassMapping> iterator() {
        return this.topLevelClasses.values().iterator();
    }

    protected MappingSet createMappingSet() {
        return new MappingSet(this.modelFactory);
    }

}

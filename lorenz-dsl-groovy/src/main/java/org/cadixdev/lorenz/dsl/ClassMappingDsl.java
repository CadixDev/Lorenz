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

package org.cadixdev.lorenz.dsl;

import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import org.cadixdev.bombe.type.FieldType;
import org.cadixdev.bombe.type.MethodDescriptor;
import org.cadixdev.bombe.type.signature.FieldSignature;
import org.cadixdev.bombe.type.signature.MethodSignature;
import org.cadixdev.lorenz.model.ClassMapping;
import org.cadixdev.lorenz.model.FieldMapping;
import org.cadixdev.lorenz.model.InnerClassMapping;
import org.cadixdev.lorenz.model.MethodMapping;

/**
 * A DSL to simplify the manipulation of {@link ClassMapping}s in Groovy.
 *
 * @param <T> The type of the class mapping
 * @author Jamie Mansfield
 * @since 0.6.0
 */
public class ClassMappingDsl<T extends ClassMapping<?, ?>> extends MappingDsl<T> {

    public ClassMappingDsl(final T mapping) {
        super(mapping);
    }

    /**
     * Creates a field mapping from the given name and field type, and applies
     * the given {@link Closure} to it.
     *
     * @param name The name of the field
     * @param type The type of the field
     * @param script The closure to use
     * @return The mapping
     * @see ClassMapping#getOrCreateFieldMapping(FieldSignature)
     */
    public FieldMapping field(
            final String name, final FieldType type,
            @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = MappingDsl.class) final Closure<?> script) {
        final FieldMapping mapping = this.mapping.getOrCreateFieldMapping(new FieldSignature(name, type));
        script.setResolveStrategy(Closure.DELEGATE_FIRST);
        script.setDelegate(new MappingDsl<>(mapping));
        script.call();
        return mapping;
    }

    /**
     * Creates a field mapping from the given name and field type, and applies
     * the given {@link Closure} to it.
     *
     * @param name The name of the field
     * @param type The type of the field
     * @param script The closure to use
     * @return The mapping
     * @see ClassMappingDsl#field(String, FieldType, Closure)
     * @see FieldType#of(String)
     */
    public FieldMapping field(
            final String name, final String type,
            @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = MappingDsl.class) final Closure<?> script) {
        return this.field(name, FieldType.of(type), script);
    }

    /**
     * Creates a field mapping from the given name, and applies the given
     * {@link Closure} to it.
     *
     * @param name The name of the field
     * @param script The closure to use
     * @return The mapping
     * @see ClassMapping#getOrCreateFieldMapping(String)
     */
    public FieldMapping field(
            final String name,
            @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = MappingDsl.class) final Closure<?> script) {
        return this.field(name, (FieldType) null, script);
    }

    /**
     * Creates a method mapping for the given name and descriptor, and applies
     * the given {@link Closure} to it.
     *
     * @param name The obfuscated name of the method
     * @param desc The descriptor of the method
     * @param script The closure to use
     * @return The mapping
     * @see ClassMapping#getOrCreateMethodMapping(MethodSignature)
     */
    public MethodMapping method(
            final String name, final MethodDescriptor desc,
            @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = MethodMappingDsl.class) final Closure<?> script) {
        final MethodMapping mapping = this.mapping.getOrCreateMethodMapping(new MethodSignature(name, desc));
        script.setResolveStrategy(Closure.DELEGATE_FIRST);
        script.setDelegate(new MethodMappingDsl(mapping));
        script.call();
        return mapping;
    }

    /**
     * Creates a method mapping for the given name and descriptor, and applies
     * the given {@link Closure} to it.
     *
     * @param name The obfuscated name of the method
     * @param desc The descriptor of the method
     * @param script The closure to use
     * @return The mapping
     * @see ClassMappingDsl#method(String, MethodDescriptor, Closure)
     * @see MethodDescriptor#of(String)
     */
    public MethodMapping method(
            final String name, final String desc,
            @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = MethodMappingDsl.class) final Closure<?> script) {
        return this.method(name, MethodDescriptor.of(desc), script);
    }

    /**
     * Creates a inner class mapping of the given name, and applies
     * the given {@link Closure} to it.
     *
     * @param name The obfuscated name of the class
     * @param script The closure to use
     * @return The mapping
     * @see ClassMapping#getOrCreateInnerClassMapping(String)
     */
    public InnerClassMapping klass(
            final String name,
            @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = ClassMappingDsl.class) final Closure<?> script) {
        final InnerClassMapping mapping = this.mapping.getOrCreateInnerClassMapping(name);
        script.setResolveStrategy(Closure.DELEGATE_FIRST);
        script.setDelegate(new ClassMappingDsl<>(mapping));
        script.call();
        return mapping;
    }

}

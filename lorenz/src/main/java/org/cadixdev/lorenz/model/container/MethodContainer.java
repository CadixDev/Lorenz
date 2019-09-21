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

import org.cadixdev.bombe.analysis.InheritanceProvider;
import org.cadixdev.bombe.type.MethodDescriptor;
import org.cadixdev.bombe.type.signature.MethodSignature;
import org.cadixdev.lorenz.impl.model.MethodMappingImpl;
import org.cadixdev.lorenz.model.ClassMapping;
import org.cadixdev.lorenz.model.MethodMapping;

import java.util.Optional;

/**
 * A container of {@link MethodMapping method mappings}, identified
 * by their {@link MethodSignature signature}, and parented by a
 * {@link ClassMapping class mapping}.
 *
 * @param <P> The type of class mapping, the parent is
 *
 * @author Jamie Mansfield
 * @since 0.6.0
 */
public class MethodContainer<P extends ClassMapping<?, ?>>
        extends SimpleParentedMappingContainer<MethodMapping, MethodSignature, P> {

    /**
     * Creates a method mapping container.
     *
     * @param parent The parent mapping
     */
    public MethodContainer(final P parent) {
        super(parent);
    }

    @Override
    protected MethodMapping create(final MethodSignature signature) {
        return new MethodMappingImpl(this.parent, signature, signature.getName());
    }

    /**
     * Gets, should it exist, the method mapping of the given obfuscated
     * name and {@link MethodDescriptor descriptor}.
     *
     * @param obfuscatedName The name of the method
     * @param obfuscatedDescriptor The descriptor of the method
     * @return The mapping, wrapped in an {@link Optional}
     *
     * @see MappingContainer#get(Object)
     */
    public Optional<MethodMapping> get(final String obfuscatedName, final MethodDescriptor obfuscatedDescriptor) {
        return this.get(new MethodSignature(obfuscatedName, obfuscatedDescriptor));
    }

    /**
     * Gets, should it exist, the method mapping of the given obfuscated
     * name and {@link MethodDescriptor descriptor}.
     *
     * @param obfuscatedName The name of the method
     * @param obfuscatedDescriptor The descriptor of the method
     * @return The mapping, wrapped in an {@link Optional}
     *
     * @see MappingContainer#get(Object)
     */
    public Optional<MethodMapping> get(final String obfuscatedName, final String obfuscatedDescriptor) {
        return this.get(obfuscatedName, MethodDescriptor.of(obfuscatedDescriptor));
    }

    /**
     * Gets, or creates if needed, the method mapping of the given
     * obfuscated name and {@link MethodDescriptor descriptor}.
     *
     * @param obfuscatedName The name of the method
     * @param obfuscatedDescriptor The descriptor of the method
     * @return The mapping
     * 
     * @see MappingContainer#getOrCreate(Object)
     */
    public MethodMapping getOrCreate(final String obfuscatedName, final MethodDescriptor obfuscatedDescriptor) {
        return this.getOrCreate(new MethodSignature(obfuscatedName, obfuscatedDescriptor));
    }

    /**
     * Gets, or creates if needed, the method mapping of the given
     * obfuscated name and {@link MethodDescriptor descriptor}.
     *
     * @param obfuscatedName The name of the method
     * @param obfuscatedDescriptor The descriptor of the method
     * @return The mapping
     *
     * @see MappingContainer#getOrCreate(Object)
     */
    public MethodMapping getOrCreate(final String obfuscatedName, final String obfuscatedDescriptor) {
        return this.getOrCreate(obfuscatedName, MethodDescriptor.of(obfuscatedDescriptor));
    }

    public void complete(final InheritanceProvider.ClassInfo parent, final InheritanceProvider.ClassInfo info, final MethodMapping mapping) {
        if (parent.canInherit(info, mapping.getSignature())) {
            this.mappings.putIfAbsent(mapping.getSignature(), mapping);
        }
    }

}

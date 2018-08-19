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

package me.jamiemansfield.lorenz.asm;

import me.jamiemansfield.bombe.analysis.InheritanceProvider;
import me.jamiemansfield.bombe.type.signature.MethodSignature;
import me.jamiemansfield.lorenz.MappingSet;
import me.jamiemansfield.lorenz.model.Mapping;
import org.objectweb.asm.commons.Remapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * A simple implementation of {@link Remapper} to remap based
 * on a {@link MappingSet}.
 *
 * @author Jamie Mansfield
 * @since 0.4.0
 */
public class LorenzRemapper extends Remapper {

    private final MappingSet mappings;
    private final InheritanceProvider inheritance;

    public LorenzRemapper(final MappingSet mappings, final InheritanceProvider inheritance) {
        this.mappings = mappings;
        this.inheritance = inheritance;
    }

    @Override
    public String map(final String typeName) {
        return this.mappings.getClassMapping(typeName)
                .map(Mapping::getFullDeobfuscatedName)
                .orElse(typeName);
    }

    /**
     * Gets a de-obfuscated field name, wrapped in an {@link Optional}, with respect to inheritance.
     *
     * @param owner The owner of the field
     * @param name The name of the field
     * @return The de-obfuscated field name, wrapped in an {@link Optional}
     */
    private Optional<String> getFieldMapping(final String owner, final String name) {
        // First, check the current class
        final Optional<String> fieldName = this.mappings.getClassMapping(owner)
                .flatMap(mapping -> mapping.getFieldMapping(name)
                        .map(Mapping::getDeobfuscatedName));
        if (fieldName.isPresent()) {
            return fieldName;
        }

        // Now, check parent classes and interfaces
        final Optional<InheritanceProvider.ClassInfo> info = this.inheritance.provide(owner);
        if (info.isPresent()) {
            final List<String> parents = new ArrayList<String>() {
                {
                    if (info.get().getSuperName() != null) {
                        this.add(info.get().getSuperName());
                    }
                    this.addAll(info.get().getInterfaces());
                }
            };

            for (final String parent : parents) {
                final Optional<String> mapping = this.getFieldMapping(parent, name);
                if (mapping.isPresent()) {
                    return mapping;
                }
            }
        }

        // The field seemingly has no mapping
        return Optional.empty();
    }

    @Override
    public String mapFieldName(final String owner, final String name, final String desc) {
        return this.getFieldMapping(owner, name).orElse(name);
    }

    /**
     * Gets a de-obfuscated method name, wrapped in an {@link Optional}, with respect to inheritance.
     *
     * @param owner The owner of the method
     * @param signature The signature of the method
     * @return The de-obfuscated method name, wrapped in an {@link Optional}
     */
    private Optional<String> getMethodMapping(final String owner, final MethodSignature signature) {
        // First, check the current class
        final Optional<String> methodName = this.mappings.getClassMapping(owner)
                .flatMap(mapping -> mapping.getMethodMapping(signature)
                        .map(Mapping::getDeobfuscatedName));
        if (methodName.isPresent()) {
            return methodName;
        }

        // Now, check the parent classes
        final Optional<InheritanceProvider.ClassInfo> info = this.inheritance.provide(owner);
        if (info.isPresent()) {
            final List<String> parents = new ArrayList<String>() {
                {
                    if (info.get().getSuperName() != null) {
                        this.add(info.get().getSuperName());
                    }
                    this.addAll(info.get().getInterfaces());
                }
            };

            for (final String parent : parents) {
                final Optional<String> name = this.getMethodMapping(parent, signature);
                if (name.isPresent()) {
                    return name;
                }
            }
        }

        // The method seemingly has no mapping
        return Optional.empty();
    }

    @Override
    public String mapMethodName(final String owner, final String name, final String desc) {
        return this.getMethodMapping(owner, new MethodSignature(name, desc)).orElse(name);
    }

}

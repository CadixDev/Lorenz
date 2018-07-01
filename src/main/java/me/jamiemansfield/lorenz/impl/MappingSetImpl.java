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

package me.jamiemansfield.lorenz.impl;

import me.jamiemansfield.lorenz.MappingSet;
import me.jamiemansfield.lorenz.MappingSetModelFactory;
import me.jamiemansfield.lorenz.model.TopLevelClassMapping;
import me.jamiemansfield.lorenz.model.jar.FieldTypeProvider;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * A basic implementation of {@link MappingSet}.
 *
 * @author Jamie Mansfield
 * @since 0.2.0
 */
public class MappingSetImpl implements MappingSet {

    private final Map<String, TopLevelClassMapping> topLevelClasses = new HashMap<>();
    private final MappingSetModelFactory modelFactory;
    private FieldTypeProvider fieldTypeProvider;

    public MappingSetImpl() {
        this((FieldTypeProvider) null);
    }

    public MappingSetImpl(final FieldTypeProvider fieldTypeProvider) {
        this(new MappingSetModelFactoryImpl(), fieldTypeProvider);
    }

    public MappingSetImpl(final MappingSetModelFactory modelFactory) {
        this(modelFactory, null);
    }

    public MappingSetImpl(final MappingSetModelFactory modelFactory, final FieldTypeProvider fieldTypeProvider) {
        this.modelFactory = modelFactory;
        this.fieldTypeProvider = fieldTypeProvider;
    }

    @Override
    public MappingSetModelFactory getModelFactory() {
        return this.modelFactory;
    }

    @Override
    public Collection<TopLevelClassMapping> getTopLevelClassMappings() {
        return Collections.unmodifiableCollection(this.topLevelClasses.values());
    }

    @Override
    public TopLevelClassMapping createTopLevelClassMapping(final String obfuscatedName, final String deobfuscatedName) {
        return this.topLevelClasses.compute(obfuscatedName, (name, existingMapping) -> {
            if (existingMapping != null) return existingMapping.setDeobfuscatedName(deobfuscatedName);
            return this.getModelFactory().createTopLevelClassMapping(this, obfuscatedName, deobfuscatedName);
        });
    }

    @Override
    public Optional<TopLevelClassMapping> getTopLevelClassMapping(final String obfuscatedName) {
        return Optional.ofNullable(this.topLevelClasses.get(obfuscatedName));
    }

    @Override
    public boolean hasTopLevelClassMapping(final String obfuscatedName) {
        return this.topLevelClasses.containsKey(obfuscatedName);
    }

    @Override
    public Optional<FieldTypeProvider> getFieldTypeProvider() {
        return Optional.ofNullable(this.fieldTypeProvider);
    }

    @Override
    public void setFieldTypeProvider(final FieldTypeProvider fieldTypeProvider) {
        this.fieldTypeProvider = fieldTypeProvider;
    }

}

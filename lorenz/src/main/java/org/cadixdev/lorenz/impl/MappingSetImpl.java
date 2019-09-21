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

package org.cadixdev.lorenz.impl;

import org.cadixdev.lorenz.MappingSet;
import org.cadixdev.lorenz.MappingSetModelFactory;
import org.cadixdev.lorenz.impl.model.TopLevelClassMappingImpl;
import org.cadixdev.lorenz.model.TopLevelClassMapping;
import org.cadixdev.lorenz.model.container.SimpleMappingContainer;
import org.cadixdev.lorenz.model.jar.CascadingFieldTypeProvider;

/**
 * A basic implementation of {@link MappingSet}.
 *
 * @author Jamie Mansfield
 * @since 0.2.0
 */
public class MappingSetImpl
        extends SimpleMappingContainer<TopLevelClassMapping, String>
        implements MappingSet {

    private final MappingSetModelFactory modelFactory;
    private CascadingFieldTypeProvider fieldTypeProvider = new CascadingFieldTypeProvider();

    public MappingSetImpl() {
        this(new MappingSetModelFactoryImpl());
    }

    public MappingSetImpl(final MappingSetModelFactory modelFactory) {
        this.modelFactory = modelFactory;
    }

    @Override
    public MappingSetModelFactory getModelFactory() {
        return this.modelFactory;
    }

    @Override
    public CascadingFieldTypeProvider getFieldTypeProvider() {
        return this.fieldTypeProvider;
    }

    @Override
    protected TopLevelClassMapping create(final String signature) {
        return new TopLevelClassMappingImpl(this, signature, signature);
    }

}

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

package me.jamiemansfield.lorenz.model.jar;

import com.google.common.base.Strings;
import me.jamiemansfield.lorenz.MappingSet;

/**
 * Represents an array type within Java.
 *
 * @see <a href="http://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-ArrayType">ArrayType</a>
 */
public class ArrayType implements Type {

    private final String arrayDims;
    private final Type component;
    private final String obfuscatedView;

    /**
     * Creates a new array type, of the specified array
     * dimensions, and {@link Type} component.
     *
     * @param arrayDims The array dimensions count
     * @param component The component type
     */
    public ArrayType(final int arrayDims, final Type component) {
        this.arrayDims = Strings.repeat("[", arrayDims);
        this.component = component;
        this.obfuscatedView = this.arrayDims + component.getObfuscated();
    }

    @Override
    public String getObfuscated() {
        return this.obfuscatedView;
    }

    @Override
    public String getDeobfuscated(final MappingSet mappings) {
        return this.arrayDims + this.component.getDeobfuscated(mappings);
    }

}

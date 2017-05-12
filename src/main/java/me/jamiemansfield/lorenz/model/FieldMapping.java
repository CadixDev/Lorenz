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

package me.jamiemansfield.lorenz.model;

import java.util.Objects;

/**
 * Represents a de-obfuscation mapping for fields.
 */
public class FieldMapping extends Mapping {

    private final ClassMapping parentClass;

    /**
     * Creates a new field mapping, from the given parameters.
     *
     * @param parentClass The class mapping, this mapping belongs to
     * @param obfuscatedName The obfuscated name
     * @param deobfuscatedName The de-obfuscated name
     */
    public FieldMapping(final ClassMapping parentClass, final String obfuscatedName, final String deobfuscatedName) {
        super(parentClass.getMappings(), obfuscatedName, deobfuscatedName);
        this.parentClass = parentClass;
    }

    @Override
    public String getFullObfuscatedName() {
        return String.format("%s/%s", this.parentClass.getFullObfuscatedName(), this.getObfuscatedName());
    }

    @Override
    public String getFullDeobfuscatedName() {
        return String.format("%s/%s", this.parentClass.getFullDeobfuscatedName(), this.getDeobfuscatedName());
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) return false;
        if (!(obj instanceof FieldMapping)) return false;
        final FieldMapping that = (FieldMapping) obj;
        return Objects.equals(this.parentClass, that.parentClass);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.parentClass);
    }

}

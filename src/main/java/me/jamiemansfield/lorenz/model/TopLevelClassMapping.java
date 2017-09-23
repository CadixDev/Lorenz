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

import me.jamiemansfield.lorenz.MappingSet;

/**
 * Represents a de-obfuscation mapping for a top-level class.
 */
public class TopLevelClassMapping extends ClassMapping {

    /**
     * Creates a new top-level class mapping, from the given parameters.
     *
     * @param mappings The mappings set, this mapping belongs to
     * @param obfuscatedName The obfuscated name
     * @param deobfuscatedName The de-obfuscated name
     */
    public TopLevelClassMapping(final MappingSet mappings, final String obfuscatedName, final String deobfuscatedName) {
        super(mappings, obfuscatedName, deobfuscatedName);
    }

    @Override
    public String getFullObfuscatedName() {
        return this.getObfuscatedName();
    }

    @Override
    public String getFullDeobfuscatedName() {
        return this.getDeobfuscatedName();
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) return false;
        if (!(obj instanceof TopLevelClassMapping)) return false;
        final TopLevelClassMapping that = (TopLevelClassMapping) obj;
        return super.equals(that);
    }

}

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

import me.jamiemansfield.lorenz.MappingsContainer;

/**
 * Represents a single obfuscation mapping for a particular member.
 */
public abstract class BaseMapping {

    private final String obfuscated;
    private String deobfuscated;

    /**
     * Constructs a new mapping with the given parameters.
     *
     * @param obfuscated The obfuscated name of the mapped member
     * @param deobfuscated The deobfuscated name of the mapped member
     */
    protected BaseMapping(final String obfuscated, final String deobfuscated) {
        this.obfuscated = obfuscated;
        this.deobfuscated = deobfuscated;
    }

    /**
     * Returns the obfuscated name of this {@link BaseMapping}.
     *
     * @return The obfuscated name of this {@link BaseMapping}
     */
    public String getObfuscatedName() {
        return this.obfuscated;
    }

    /**
     * Returns the deobfuscated name of this {@link BaseMapping}.
     *
     * @return The deobfuscated name of this {@link BaseMapping}
     */
    public String getDeobfuscatedName() {
        return this.deobfuscated;
    }

    /**
     * Sets the deobfuscated name of this {@link BaseMapping}.
     *
     * @param name The new deobfuscated name of this {@link BaseMapping}
     */
    public void setDeobfuscatedName(final String name) {
        this.deobfuscated = name;
    }

    /**
     * Returns the full obfuscated name of this {@link BaseMapping}.
     *
     * @return The full obfuscated name of this {@link BaseMapping}
     */
    public abstract String getFullObfuscatedName();

    /**
     * Returns the full deobfuscated name of this {@link BaseMapping}.
     *
     * @return The full deobfuscated name of this {@link BaseMapping}
     */
    public abstract String getFullDeobfuscatedName();

    /**
     * Gets the {@link MappingsContainer} which owns this {@link BaseMapping}.
     *
     * @return The {@link MappingsContainer} which owns this {@link BaseMapping}
     */
    public abstract MappingsContainer getMappings();

}

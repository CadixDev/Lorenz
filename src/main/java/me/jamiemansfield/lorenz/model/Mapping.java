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
 * Represents a de-obfuscation mapping for mappable constructs of the Java
 * class format - e.g. classes, fields, and methods.
 *
 * @param <M> The type of the mapping, used for chaining
 */
public interface Mapping<M extends Mapping> {

    /**
     * Gets the obfuscated name of the member being represented.
     *
     * @return The obfuscated name
     */
    String getObfuscatedName();

    /**
     * Gets the de-obfuscated name of the member being represented.
     * This can be altered through use of {@link #setDeobfuscatedName(String)}.
     *
     * @return The de-obfuscated name
     */
    String getDeobfuscatedName();

    /**
     * Sets the de-obfuscated name of the member.
     *
     * @param deobfuscatedName The new de-obfuscated name
     * @return {@code this} for chaining
     */
    M setDeobfuscatedName(final String deobfuscatedName);

    /**
     * Gets the fully-qualified obfuscated name of the member.
     *
     * @return The fully-qualified obfuscated name
     */
    String getFullObfuscatedName();

    /**
     * Gets the fully-qualified de-obfuscated name of the member.
     *
     * @return The fully-qualified de-obfuscated name
     */
    String getFullDeobfuscatedName();

    /**
     * Establishes whether the mapping has had a de-obfuscated name set.
     *
     * @return {@code True} if the mapping is mapped, {@code false} otherwise
     */
    boolean hasDeobfuscatedName();

    /**
     * Gets the {@link MappingSet} that the mappings belongs to.
     *
     * @return The owning mapping set
     */
    MappingSet getMappings();

}

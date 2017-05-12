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

import com.google.common.base.MoreObjects;
import me.jamiemansfield.lorenz.MappingSet;

import java.util.Objects;

/**
 * Represents a de-obfuscation mapping for software that runs on the JVM.
 */
public abstract class Mapping {

    private final MappingSet mappings;
    private final String obfuscatedName;
    private String deobfuscatedName;

    /**
     * Creates a new de-obfuscation mapping, based on the given obfuscated name
     * and de-obfuscated name.
     *
     * @param mappings The mappings set, this mapping belongs to
     * @param obfuscatedName The obfuscated name
     * @param deobfuscatedName The de-obfuscated name
     */
    protected Mapping(final MappingSet mappings, final String obfuscatedName, final String deobfuscatedName) {
        this.mappings = mappings;
        this.obfuscatedName = obfuscatedName;
        this.deobfuscatedName = deobfuscatedName;
    }

    /**
     * Gets the obfuscated name of the member being represented.
     *
     * @return The obfuscated name
     */
    public String getObfuscatedName() {
        return this.obfuscatedName;
    }

    /**
     * Gets the de-obfuscated name of the member being represented.
     * This can be altered through use of {@link #setDeobfuscatedName(String)}.
     *
     * @return The de-obfuscated name
     */
    public String getDeobfuscatedName() {
        return this.deobfuscatedName;
    }

    /**
     * Sets the de-obfuscated name of the member.
     *
     * @param deobfuscatedName The new de-obfuscated name
     */
    public void setDeobfuscatedName(final String deobfuscatedName) {
        this.deobfuscatedName = deobfuscatedName;
    }

    /**
     * Gets the fully-qualified obfuscated name of the member.
     *
     * @return The fully-qualified obfuscated name
     */
    public abstract String getFullObfuscatedName();

    /**
     * Gets the fully-qualified de-obfuscated name of the member.
     *
     * @return The fully-qualified de-obfuscated name
     */
    public abstract String getFullDeobfuscatedName();

    /**
     * Establishes whether the mapping has had a de-obfuscated name set.
     *
     * @return {@code True} if the mapping is mapped, {@link false} otherwise
     */
    public boolean hasDeobfuscatedName() {
        return !Objects.equals(this.obfuscatedName, this.deobfuscatedName);
    }

    /**
     * Gets the {@link MappingSet} that the mappings belongs to.
     *
     * @return The owning mapping set
     */
    public MappingSet getMappings() {
        return this.mappings;
    }

    protected MoreObjects.ToStringHelper buildToString() {
        return MoreObjects.toStringHelper(this)
                .add("obfuscatedName", this.obfuscatedName)
                .add("deobfuscatedName", this.deobfuscatedName);
    }

    @Override
    public String toString() {
        return this.buildToString().toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Mapping)) return false;
        final Mapping that = (Mapping) obj;
        return Objects.equals(this.obfuscatedName, that.obfuscatedName) &&
                Objects.equals(this.deobfuscatedName, that.deobfuscatedName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.obfuscatedName, this.deobfuscatedName);
    }

}

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

import java.util.Objects;

/**
 * Represents a de-obfuscation mapping for methods.
 */
public class MethodMapping extends Mapping {

    private final ClassMapping parentClass;
    private final MethodDescriptor obfuscatedDescriptor;

    /**
     * Creates a new method mapping, from the given parameters.
     *
     * @param parentClass The class mapping, this mapping belongs to
     * @param obfuscatedDescriptor The obfuscated descriptor of the method
     * @param deobfuscatedName The de-obfuscated name
     */
    public MethodMapping(final ClassMapping parentClass, final MethodDescriptor obfuscatedDescriptor,
            final String deobfuscatedName) {
        super(parentClass.getMappings(), obfuscatedDescriptor.getName(), deobfuscatedName);
        this.parentClass = parentClass;
        this.obfuscatedDescriptor = obfuscatedDescriptor;
    }

    /**
     * Gets the obfuscated signature of the method.
     *
     * @return The obfuscated signature
     */
    public String getObfuscatedSignature() {
        return this.obfuscatedDescriptor.getSignature();
    }

    /**
     * Gets the obfuscated {@link MethodDescriptor} of the method.
     *
     * @return The obfuscated method descriptor
     */
    public MethodDescriptor getObfuscatedDescriptor() {
        return this.obfuscatedDescriptor;
    }

    /**
     * Gets the de-obfuscated signature of the method.
     *
     * @return The de-obfuscated signature
     */
    public String getDeobfuscatedSignature() {
        return this.getMappings().deobfuscateMethodSignature(this.getObfuscatedSignature());
    }

    /**
     * Gets the de-obfuscated {@link MethodDescriptor} of the method.
     *
     * @return The de-obfuscated method descriptor
     */
    public MethodDescriptor getDeobfuscatedDescriptor() {
        return new MethodDescriptor(this.getDeobfuscatedName(), this.getDeobfuscatedSignature());
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
    protected MoreObjects.ToStringHelper buildToString() {
        return super.buildToString()
                .add("obfuscatedDescriptor", this.obfuscatedDescriptor)
                .add("deobfuscatedDescriptor", this.getDeobfuscatedDescriptor());
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) return false;
        if (!(obj instanceof MethodMapping)) return false;
        final MethodMapping that = (MethodMapping) obj;
        return super.equals(that) &&
                Objects.equals(this.parentClass, that.parentClass) &&
                Objects.equals(this.obfuscatedDescriptor, that.obfuscatedDescriptor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.parentClass, this.obfuscatedDescriptor);
    }

}

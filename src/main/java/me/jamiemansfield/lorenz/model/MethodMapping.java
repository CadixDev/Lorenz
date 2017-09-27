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
import me.jamiemansfield.lorenz.model.jar.MethodDescriptor;
import me.jamiemansfield.lorenz.model.jar.Signature;

import java.util.Objects;

/**
 * Represents a de-obfuscation mapping for methods.
 */
public class MethodMapping extends Mapping {

    private final ClassMapping parentClass;
    private final MethodDescriptor descriptor;

    /**
     * Creates a new method mapping, from the given parameters.
     *
     * @param parentClass The class mapping, this mapping belongs to
     * @param descriptor The descriptor of the method
     * @param deobfuscatedName The de-obfuscated name
     */
    public MethodMapping(final ClassMapping parentClass, final MethodDescriptor descriptor,
            final String deobfuscatedName) {
        super(parentClass.getMappings(), descriptor.getName(), deobfuscatedName);
        this.parentClass = parentClass;
        this.descriptor = descriptor;
    }

    /**
     * Gets the {@link MethodDescriptor} of the method.
     *
     * @return The method descriptor
     */
    public MethodDescriptor getDescriptor() {
        return this.descriptor;
    }

    /**
     * Gets the {@link Signature} of the method.
     *
     * @return The method signature
     * @see MethodDescriptor#getSignature()
     */
    public Signature getSignature() {
        return this.descriptor.getSignature();
    }

    /**
     * Gets the obfuscated signature of the method.
     *
     * @return The obfuscated signature
     * @see MethodDescriptor#getSignature()
     * @see Signature#getObfuscated()
     */
    public String getObfuscatedSignature() {
        return this.descriptor.getSignature().getObfuscated();
    }

    /**
     * Gets the de-obfuscated signature of the method.
     *
     * @return The de-obfuscated signature
     * @see MethodDescriptor#getSignature()
     * @see Signature#getDeobfuscated(MappingSet)
     */
    public String getDeobfuscatedSignature() {
        return this.descriptor.getSignature().getDeobfuscated(this.getMappings());
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
                .add("obfuscatedSignature", this.getObfuscatedSignature())
                .add("deobfuscatedSignature", this.getDeobfuscatedSignature());
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) return false;
        if (!(obj instanceof MethodMapping)) return false;
        final MethodMapping that = (MethodMapping) obj;
        return super.equals(that) &&
                Objects.equals(this.parentClass, that.parentClass) &&
                Objects.equals(this.descriptor, that.descriptor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.parentClass, this.descriptor);
    }

}

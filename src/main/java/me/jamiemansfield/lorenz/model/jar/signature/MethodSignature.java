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

package me.jamiemansfield.lorenz.model.jar.signature;

import com.google.common.base.MoreObjects;
import me.jamiemansfield.lorenz.model.jar.MethodDescriptor;

import java.util.Objects;

/**
 * Represents a method within a class.
 *
 * @author Jamie Mansfield
 * @since 0.2.0
 */
public class MethodSignature extends MemberSignature {

    private final MethodDescriptor descriptor;

    /**
     * Creates a method signature, with the given name and {@link MethodDescriptor}.
     *
     * @param name The method name
     * @param descriptor The method descriptor
     */
    public MethodSignature(final String name, final MethodDescriptor descriptor) {
        super(name);
        this.descriptor = descriptor;
    }

    /**
     * Creates a method descriptor, with the given method name and raw descriptor.
     *
     * @param name The method name
     * @param descriptor The method's raw descriptor
     */
    public MethodSignature(final String name, final String descriptor) {
        this(name, MethodDescriptor.compile(descriptor));
    }

    /**
     * Gets the descriptor of the method.
     *
     * @return The descriptor
     */
    public MethodDescriptor getDescriptor() {
        return this.descriptor;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("name", this.name)
                .add("descriptor", this.descriptor)
                .toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof MethodSignature)) return false;
        final MethodSignature that = (MethodSignature) obj;
        return Objects.equals(this.name, that.name) &&
                Objects.equals(this.descriptor, that.descriptor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.name, this.descriptor);
    }

}

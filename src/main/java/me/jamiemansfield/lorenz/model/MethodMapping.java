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
import me.jamiemansfield.lorenz.model.jar.MethodDescriptor;
import me.jamiemansfield.lorenz.model.jar.Signature;

/**
 * Represents a de-obfuscation mapping for methods.
 */
public interface MethodMapping extends MemberMapping<MethodMapping> {

    /**
     * Gets the {@link MethodDescriptor} of the method.
     *
     * @return The method descriptor
     */
    MethodDescriptor getDescriptor();

    /**
     * Gets the {@link Signature} of the method.
     *
     * @return The method signature
     * @see MethodDescriptor#getSignature()
     */
    default Signature getSignature() {
        return this.getDescriptor().getSignature();
    }

    /**
     * Gets the obfuscated signature of the method.
     *
     * @return The obfuscated signature
     * @see MethodDescriptor#getSignature()
     * @see Signature#getObfuscated()
     */
    default String getObfuscatedSignature() {
        return this.getSignature().getObfuscated();
    }

    /**
     * Gets the de-obfuscated signature of the method.
     *
     * @return The de-obfuscated signature
     * @see MethodDescriptor#getSignature()
     * @see Signature#getDeobfuscated(MappingSet)
     */
    default String getDeobfuscatedSignature() {
        return this.getSignature().getDeobfuscated(this.getMappings());
    }

    @Override
    default String getFullObfuscatedName() {
        return String.format("%s/%s", this.getParentClass().getFullObfuscatedName(), this.getObfuscatedName());
    }

    @Override
    default String getFullDeobfuscatedName() {
        return String.format("%s/%s", this.getParentClass().getFullDeobfuscatedName(), this.getDeobfuscatedName());
    }

}

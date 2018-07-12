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

/**
 * All members within Java have a unique signature that they can be identified with,
 * classes that inherit from this class are a representation of those unique signatures.
 *
 * @see FieldSignature
 * @see MethodSignature
 *
 * @author Jamie Mansfield
 * @since 0.2.0
 */
public abstract class MemberSignature {

    protected final String name;

    /**
     * Creates a member signature, with the given name.
     *
     * @param name The name of the member
     */
    protected MemberSignature(final String name) {
        this.name = name;
    }

    /**
     * Gets the name of the member.
     *
     * @return The name
     */
    public String getName() {
        return this.name;
    }

    protected MoreObjects.ToStringHelper buildToString() {
        return MoreObjects.toStringHelper(this)
                .add("name", this.name);
    }

    @Override
    public String toString() {
        return this.buildToString().toString();
    }

}

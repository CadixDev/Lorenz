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
import me.jamiemansfield.lorenz.model.jar.Type;

import java.util.Objects;
import java.util.Optional;

/**
 * Represents a field within a class, by its name and descriptor.
 *
 * @author Jamie Mansfield
 * @since 0.2.0
 */
public class FieldSignature extends MemberSignature {

    private final Type type;

    /**
     * Creates a field signature, with the given name and type.
     *
     * @param name The name of the field
     * @param type The type of the field
     */
    public FieldSignature(final String name, final Type type) {
        super(name);
        this.type = type;
    }

    /**
     * Gets the {@link Type} of the field, if present.
     *
     * @return The field's type, wrapped in an {@link Optional}
     */
    public Optional<Type> getType() {
        return Optional.ofNullable(this.type);
    }

    @Override
    protected MoreObjects.ToStringHelper buildToString() {
        return super.buildToString()
                .add("type", this.type);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof FieldSignature)) return false;
        final FieldSignature that = (FieldSignature) obj;
        return Objects.equals(this.name, that.name) &&
                Objects.equals(this.type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.name, this.type);
    }

}

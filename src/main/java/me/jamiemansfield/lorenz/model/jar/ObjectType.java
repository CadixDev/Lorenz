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

package me.jamiemansfield.lorenz.model.jar;

import me.jamiemansfield.lorenz.MappingSet;
import me.jamiemansfield.lorenz.model.ClassMapping;
import me.jamiemansfield.lorenz.model.Mapping;

import java.util.Objects;
import java.util.Optional;

/**
 * Represents an object type within Java.
 *
 * @see <a href="http://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-ObjectType">ObjectType</a>
 *
 * @author Jamie Mansfield
 * @since 0.1.0
 */
public class ObjectType implements Type {

    private final String className;
    private final String obfuscatedView;

    /**
     * Creates a new object type, of the given class name.
     *
     * @param className The class name
     */
    public ObjectType(final String className) {
        this.className = className;
        this.obfuscatedView = "L" + className + ";";
    }

    @Override
    public String getObfuscated() {
        return this.obfuscatedView;
    }

    @Override
    public String getDeobfuscated(final MappingSet mappings) {
        final Optional<ClassMapping<?>> typeMapping = mappings.getClassMapping(this.className);
        return "L" + typeMapping.map(Mapping::getFullDeobfuscatedName).orElse(this.className) + ";";
    }

    @Override
    public String toString() {
        return this.getObfuscated();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof ObjectType)) return false;
        final ObjectType that = (ObjectType) obj;
        return Objects.equals(this.className, that.className);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.className);
    }

}

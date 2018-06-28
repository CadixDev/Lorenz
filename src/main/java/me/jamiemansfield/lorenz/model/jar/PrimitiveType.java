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

import java.util.Arrays;

/**
 * Represents a primitive type within Java.
 *
 * @see <a href="http://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-BaseType">BaseType</a>
 *
 * @author Jamie Mansfield
 * @since 0.1.0
 */
public enum PrimitiveType implements Type {

    BYTE('B'),
    CHAR('C'),
    DOUBLE('D'),
    FLOAT('F'),
    INT('I'),
    LONG('J'),
    SHORT('S'),
    BOOLEAN('Z'),
    VOID('V'),
    ;

    private final char key;
    private final String obfuscatedView;

    /**
     * Creates a new primitive type, with the given character type.
     *
     * @param key The character key
     */
    PrimitiveType(final char key) {
        this.key = key;
        this.obfuscatedView = "" + key;
    }

    @Override
    public String getObfuscated() {
        return this.obfuscatedView;
    }

    @Override
    public String getDeobfuscated(final MappingSet mappings) {
        return this.obfuscatedView;
    }

    @Override
    public String toString() {
        return this.getObfuscated();
    }

    /**
     * Establishes whether the given key, is a valid primitive
     * key.
     *
     * @param key The key
     * @return {@code True} if the key represents a primitive,
     *         {@code false} otherwise
     */
    public static boolean isValidPrimitive(final char key) {
        return Arrays.stream(values())
                .anyMatch(type -> type.key == key);
    }

    /**
     * Gets the {@link PrimitiveType} from the given key.
     *
     * @param key The key
     * @return The primitive type
     */
    public static PrimitiveType getFromKey(final char key) {
        return Arrays.stream(values())
                .filter(type -> type.key == key)
                .findFirst().orElse(null);
    }

}

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

/**
 * Represents a type within Java.
 *
 * @see PrimitiveType
 * @see ObjectType
 * @see ArrayType
 */
public interface Type {

    /**
     * Gets the appropriate {@link Type} for the given type.
     *
     * @param type The type
     * @return The type
     */
    static Type of(final String type) {
        if (type.startsWith("L")) {
            // Skim off the 'L' and the ';'.
            final String className = type.substring(1, type.length() - 1);

            return new ObjectType(className);
        }
        else if (type.startsWith("[")) {
            // Get the array dimensions count
            final int arrayDims = type.lastIndexOf('[') + 1;

            return new ArrayType(arrayDims, Type.of(type.substring(arrayDims)));
        }
        else if (type.length() == 1 && PrimitiveType.isValidPrimitive(type.charAt(0))) {
            return PrimitiveType.getFromKey(type.charAt(0));
        }
        throw new RuntimeException("Invalid type: " + type);
    }

    /**
     * Gets the obfuscated raw view of the type.
     *
     * @return The obfuscated raw view
     */
    String getObfuscated();

    /**
     * Gets the de-obfuscated raw view of the type.
     *
     * @param mappings The mappings set, for de-obfuscation
     * @return The de-obfuscated raw view
     */
    String getDeobfuscated(final MappingSet mappings);

}

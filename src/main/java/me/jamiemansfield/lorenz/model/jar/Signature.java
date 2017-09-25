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

import com.google.common.collect.Lists;
import me.jamiemansfield.lorenz.MappingSet;

import java.util.List;

/**
 * Represents the signature of a method.
 */
public final class Signature {

    private final List<Type> paramTypes;
    private final Type returnType;

    /**
     * Compiles a {@link Signature} for the given raw signature, and {@link MappingSet}.
     *
     * @param signature The raw method signature
     * @return The signature
     */
    public static Signature compile(final String signature) {
        // Grab the raw parameters and return from the signature
        final String rawParams = signature.substring(signature.indexOf('(') + 1, signature.indexOf(')'));
        final String rawReturn = signature.substring(signature.indexOf(')') + 1);

        // Param Types
        final List<Type> paramTypes = Lists.newArrayList();

        boolean isParsingObject = false;
        StringBuilder objectBuilder = new StringBuilder();

        boolean isParsingArray = false;
        int arrayDim = 0;

        for (final char c : rawParams.toCharArray()) {
            if (isParsingObject) {
                // We're parsing an object
                if (c == ';') {
                    // This symbol is the end of an object
                    final ObjectType componentType = new ObjectType(objectBuilder.toString());

                    if (isParsingArray) {
                        paramTypes.add(new ArrayType(arrayDim, componentType));

                        // Return parsingArray state back to normal
                        isParsingArray = false;
                        arrayDim = 0;
                    } else {
                        paramTypes.add(componentType);
                    }

                    // Return parsingObject state back to normal
                    isParsingObject = false;
                    objectBuilder = new StringBuilder();
                }
                else {
                    // Still parsing the object
                    objectBuilder.append(c);
                }
            } else {
                if (c == 'L') {
                    // This symbol is the start of an object
                    isParsingObject = true;
                }
                else if (c == '[') {
                    // This symbol is an array dimension
                    isParsingArray = true;
                    arrayDim++;
                }
                else if(PrimitiveType.isValidPrimitive(c)) {
                    // This symbol is a primitive
                    final PrimitiveType componentType = PrimitiveType.getFromKey(c);

                    if (isParsingArray) {
                        paramTypes.add(new ArrayType(arrayDim, componentType));

                        // Return parsingArray state back to normal
                        isParsingArray = false;
                        arrayDim = 0;
                    } else {
                        paramTypes.add(componentType);
                    }
                }
                else {
                    throw new RuntimeException("Invalid type: " + c);
                }
            }
        }

        return new Signature(paramTypes, Type.of(rawReturn));
    }

    /**
     * Creates a signature from the given param types, and return type.
     *
     * @param paramTypes The parameter types of the method
     * @param returnType The return type of the method
     */
    public Signature(final List<Type> paramTypes, final Type returnType) {
        this.paramTypes = paramTypes;
        this.returnType = returnType;
    }

    /**
     * Gets the obfuscated signature of the method.
     *
     * @return The obfuscated signature
     */
    public String getObfuscated() {
        final StringBuilder typeBuilder = new StringBuilder();

        typeBuilder.append("(");
        this.paramTypes.forEach(type -> typeBuilder.append(type.getObfuscated()));
        typeBuilder.append(")");
        typeBuilder.append(this.returnType.getObfuscated());

        return typeBuilder.toString();
    }

    /**
     * Gets the de-obfuscated signature of the method.
     *
     * @param mappings The mappings set, for de-obfuscation
     * @return The de-obfuscated signature
     */
    public String getDeobfuscated(final MappingSet mappings) {
        final StringBuilder typeBuilder = new StringBuilder();

        typeBuilder.append("(");
        this.paramTypes.forEach(type -> typeBuilder.append(type.getDeobfuscated(mappings)));
        typeBuilder.append(")");
        typeBuilder.append(this.returnType.getDeobfuscated(mappings));

        return typeBuilder.toString();
    }

    @Override
    public String toString() {
        return this.getObfuscated();
    }

}

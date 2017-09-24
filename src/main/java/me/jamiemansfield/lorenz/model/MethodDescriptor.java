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
import com.google.common.collect.Lists;
import me.jamiemansfield.lorenz.MappingSet;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Represents a method within a class.
 */
public final class MethodDescriptor {

    private final String name;
    private final Signature signature;

    /**
     * Creates a method descriptor, with the given name and signature.
     *
     * @param mappings The mappings set
     * @param name The method name
     * @param signature The method signature
     */
    public MethodDescriptor(final MappingSet mappings, final String name, final String signature) {
        this.name = name;
        this.signature = Signature.compile(mappings, signature);
    }

    /**
     * Gets the name of the method.
     *
     * @return The name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Gets the signature of the method.
     *
     * @return The signature
     */
    public Signature getSignature() {
        return this.signature;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("name", this.name)
                .add("signature", this.signature)
                .toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof MethodDescriptor)) return false;
        final MethodDescriptor that = (MethodDescriptor) obj;
        return Objects.equals(this.name, that.name) &&
                Objects.equals(this.signature, that.signature);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.name, this.signature);
    }

    /**
     * Represents the signature of a method.
     */
    public static final class Signature {

        private final List<Type> paramTypes;
        private final Type returnType;

        /**
         * Compiles a {@link Signature} for the given raw signature, and {@link MappingSet}.
         *
         * @param mappings The mapping set, for de-obfuscating
         * @param signature The raw method signature
         * @return The signature
         */
        public static Signature compile(final MappingSet mappings, final String signature) {
            // Grab the raw parameters and return from the signature
            final String rawParams = signature.substring(signature.indexOf('(') + 1, signature.indexOf(')'));
            final String rawReturn = signature.substring(signature.indexOf(')') + 1);

            // Param Types
            final List<Type> paramTypes = Lists.newArrayList();
            boolean isParsingObject = false;
            StringBuilder objectBuilder = new StringBuilder();
            for (final char c : rawParams.toCharArray()) {
                if (isParsingObject) {
                    // We're parsing an object
                    if (c == ';') {
                        // This symbol is the end of an object
                        isParsingObject = false;
                        objectBuilder.append(c);
                        paramTypes.add(new Type(mappings, objectBuilder.toString()));
                        objectBuilder = new StringBuilder();
                    } else {
                        // Still parsing the object
                        objectBuilder.append(c);
                    }
                } else {
                    if (c == 'L') {
                        // This symbol is the start of an object
                        isParsingObject = true;
                        objectBuilder.append(c);
                    } else {
                        // No object is being parsed, and it is not the start of
                        // one - it must be a primitive
                        paramTypes.add(new Type(mappings, "" + c));
                    }
                }
            }

            return new Signature(paramTypes, new Type(mappings, rawReturn));
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
         * @return The de-obfuscated signature
         */
        public String getDeobfuscated() {
            final StringBuilder typeBuilder = new StringBuilder();

            typeBuilder.append("(");
            this.paramTypes.forEach(type -> typeBuilder.append(type.getDeobfuscated()));
            typeBuilder.append(")");
            typeBuilder.append(this.returnType.getDeobfuscated());

            return typeBuilder.toString();
        }

        @Override
        public String toString() {
            return this.getObfuscated();
        }

    }

    /**
     * Represents a type within Java.
     */
    public static final class Type {

        private final MappingSet mappings;
        private final String obfuscatedType;
        private final boolean primitive;

        /**
         * Creates a new type, with the provided raw type.
         *
         * @param mappings The mappings set, for de-obfuscation
         * @param type The raw type
         */
        public Type(final MappingSet mappings, final String type) {
            this.mappings = mappings;
            this.obfuscatedType = type;
            this.primitive = !type.startsWith("L");
        }

        /**
         * Gets the obfuscated raw view of the type.
         *
         * @return The obfuscated raw view
         */
        public String getObfuscated() {
            return this.obfuscatedType;
        }

        /**
         * Gets the de-obfuscated raw view of the type.
         *
         * @return The de-obfuscated raw view
         */
        public String getDeobfuscated() {
            if (primitive) return this.obfuscatedType;

            // Skim off the 'L' and the ';'.
            final String searchType = this.obfuscatedType.substring(1, this.obfuscatedType.length() - 1);

            final Optional<ClassMapping> typeMapping = this.mappings.getClassMapping(searchType);
            final StringBuilder typeBuilder = new StringBuilder();

            typeBuilder.append("L");
            if (typeMapping.isPresent()) {
                typeBuilder.append(typeMapping.get().getFullDeobfuscatedName());
            } else {
                typeBuilder.append(searchType);
            }
            typeBuilder.append(";");
            return typeBuilder.toString();
        }

        /**
         * Gets whether the type is a primitive.
         *
         * @return {@code True} if the type is primitive,
         *         {@code false} otherwise
         */
        public boolean isPrimitive() {
            return this.primitive;
        }

        @Override
        public String toString() {
            return this.getObfuscated();
        }

    }

}

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

package org.cadixdev.lorenz.io.enigma;

import org.cadixdev.bombe.type.ArrayType;
import org.cadixdev.bombe.type.FieldType;
import org.cadixdev.bombe.type.MethodDescriptor;
import org.cadixdev.bombe.type.ObjectType;
import org.cadixdev.bombe.type.Type;
import org.cadixdev.lorenz.MappingSet;
import org.cadixdev.lorenz.io.MappingsReader;
import org.cadixdev.lorenz.io.TextMappingsReader;
import org.cadixdev.lorenz.model.ClassMapping;
import org.cadixdev.lorenz.model.Mapping;
import org.cadixdev.lorenz.model.MethodMapping;

import java.io.Reader;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.stream.Collectors;

/**
 * An implementation of {@link MappingsReader} for the Enigma format.
 *
 * @author Jamie Mansfield
 * @since 0.4.0
 */
public class EnigmaReader extends TextMappingsReader {

    public EnigmaReader(final Reader reader, final boolean handleNone) {
        super(reader, mappings -> new Processor(mappings, handleNone));
    }

    public static class Processor extends TextMappingsReader.Processor {

        private static final String CLASS_MAPPING_KEY = "CLASS";
        private static final String FIELD_MAPPING_KEY = "FIELD";
        private static final String METHOD_MAPPING_KEY = "METHOD";
        private static final String PARAM_MAPPING_KEY = "ARG";

        private static final int CLASS_MAPPING_ELEMENT_WITH_DEOBF_COUNT = 3;
        private static final int CLASS_MAPPING_ELEMENT_WITHOUT_DEOBF_COUNT = 2;
        private static final int FIELD_MAPPING_ELEMENT_COUNT = 4;
        private static final int METHOD_MAPPING_ELEMENT_WITH_DEOBF_COUNT = 4;
        private static final int METHOD_MAPPING_ELEMENT_WITHOUT_DEOBF_COUNT = 3;
        private static final int PARAM_MAPPING_ELEMENT_COUNT = 3;

        private static int getIndentLevel(final String line) {
            int indentLevel = 0;
            for (int i = 0; i < line.length(); i++) {
                if (line.charAt(i) != '\t') break;
                indentLevel++;
            }
            return indentLevel;
        }

        private final Deque<Mapping<?, ?>> stack = new ArrayDeque<>();
        private final boolean handleNone;

        public Processor(final MappingSet mappings, final boolean handleNone) {
            super(mappings);
            this.handleNone = handleNone;
        }

        public Processor(final boolean handleNone) {
            this(new MappingSet(), handleNone);
        }

        @Override
        public void accept(final String rawLine) {
            final int indentLevel = getIndentLevel(rawLine);

            // If there is a change in the indentation level, we will need to pop the stack
            // as needed
            while (indentLevel < this.stack.size()) {
                this.stack.pop();
            }

            final String line = EnigmaConstants.removeComments(rawLine).trim();
            if (line.isEmpty()) return;

            // Split up the line, for further processing
            final String[] split = SPACE.split(line);
            final int len = split.length;

            // Establish the type of mapping
            final String key = split[0];
            if (key.equals(CLASS_MAPPING_KEY) && len == CLASS_MAPPING_ELEMENT_WITHOUT_DEOBF_COUNT) {
                final String obfName = this.handleNonePrefix(split[1]);
                this.stack.push(this.mappings.getOrCreateClassMapping(obfName));
            }
            else if (key.equals(CLASS_MAPPING_KEY) && len == CLASS_MAPPING_ELEMENT_WITH_DEOBF_COUNT) {
                final String obfName = this.handleNonePrefix(split[1]);
                final String deobfName = this.handleNonePrefix(split[2]);
                this.stack.push(this.mappings.getOrCreateClassMapping(obfName)
                        .setDeobfuscatedName(deobfName));
            }
            else if (key.equals(FIELD_MAPPING_KEY) && len == FIELD_MAPPING_ELEMENT_COUNT) {
                final String obfName = split[1];
                final String deobfName = split[2];
                final String type = this.handleNonePrefix(FieldType.of(split[3])).toString();
                this.peekClass().getOrCreateFieldMapping(obfName, type)
                        .setDeobfuscatedName(deobfName);
            }
            else if (key.equals(METHOD_MAPPING_KEY) && len == METHOD_MAPPING_ELEMENT_WITHOUT_DEOBF_COUNT) {
                final String obfName = split[1];
                final String descriptor = this.handleNonePrefix(MethodDescriptor.of(split[2])).toString();
                this.stack.push(this.peekClass().getOrCreateMethodMapping(obfName, descriptor));
            }
            else if (key.equals(METHOD_MAPPING_KEY) && len == METHOD_MAPPING_ELEMENT_WITH_DEOBF_COUNT) {
                final String obfName = split[1];
                final String deobfName = split[2];
                final String descriptor = this.handleNonePrefix(MethodDescriptor.of(split[3])).toString();
                this.stack.push(this.peekClass().getOrCreateMethodMapping(obfName, descriptor)
                        .setDeobfuscatedName(deobfName));
            }
            else if (key.equals(PARAM_MAPPING_KEY) && len == PARAM_MAPPING_ELEMENT_COUNT) {
                final int index = Integer.parseInt(split[1]);
                final String deobfName = split[2];
                this.peekMethod().getOrCreateParameterMapping(index)
                        .setDeobfuscatedName(deobfName);
            }
        }

        protected ClassMapping<?, ?> peekClass() {
            if (!(this.stack.peek() instanceof ClassMapping)) throw new UnsupportedOperationException("Not a class on the stack!");
            return (ClassMapping<?, ?>) this.stack.peek();
        }

        protected MethodMapping peekMethod() {
            if (!(this.stack.peek() instanceof MethodMapping)) throw new UnsupportedOperationException("Not a method on the stack!");
            return (MethodMapping) this.stack.peek();
        }

        private String handleNonePrefix(final String descriptor) {
            if (!this.handleNone) return descriptor;

            if (descriptor.startsWith("none/")) {
                return descriptor.substring("none/".length());
            }
            return descriptor;
        }

        private Type handleNonePrefix(final Type type) {
            if (!this.handleNone) return type;

            if (type instanceof FieldType) {
                return this.handleNonePrefix((FieldType) type);
            }
            return type;
        }

        private FieldType handleNonePrefix(final FieldType type) {
            if (!this.handleNone) return type;

            if (type instanceof ArrayType) {
                final ArrayType arr = (ArrayType) type;
                return new ArrayType(arr.getDimCount(), this.handleNonePrefix(arr.getComponent()));
            }
            if (type instanceof ObjectType) {
                final ObjectType obj = (ObjectType) type;
                return new ObjectType(this.handleNonePrefix(obj.getClassName()));
            }
            return type;
        }

        private MethodDescriptor handleNonePrefix(final MethodDescriptor descriptor) {
            if (!this.handleNone) return descriptor;

            return new MethodDescriptor(
                    descriptor.getParamTypes().stream()
                            .map(this::handleNonePrefix)
                            .collect(Collectors.toList()),
                    this.handleNonePrefix(descriptor.getReturnType())
            );
        }

    }

}

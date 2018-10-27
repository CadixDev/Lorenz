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

package me.jamiemansfield.lorenz.io.enigma;

import me.jamiemansfield.bombe.type.ArrayType;
import me.jamiemansfield.bombe.type.FieldType;
import me.jamiemansfield.bombe.type.MethodDescriptor;
import me.jamiemansfield.bombe.type.ObjectType;
import me.jamiemansfield.bombe.type.Type;
import me.jamiemansfield.lorenz.MappingSet;
import me.jamiemansfield.lorenz.io.MappingsReader;
import me.jamiemansfield.lorenz.io.TextMappingsReader;
import me.jamiemansfield.lorenz.model.ClassMapping;
import me.jamiemansfield.lorenz.model.MethodMapping;

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

    public EnigmaReader(final Reader reader) {
        super(reader, Processor::new);
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

        private static String handleNonePrefix(final String descriptor) {
            if (descriptor.startsWith("none/")) {
                return descriptor.substring("none/".length());
            }
            return descriptor;
        }

        private static Type handleNonePrefix(final Type type) {
            if (type instanceof FieldType) {
                return handleNonePrefix((FieldType) type);
            }
            return type;
        }

        private static FieldType handleNonePrefix(final FieldType type) {
            if (type instanceof ArrayType) {
                final ArrayType arr = (ArrayType) type;
                return new ArrayType(arr.getDimCount(), handleNonePrefix(arr.getComponent()));
            }
            if (type instanceof ObjectType) {
                final ObjectType obj = (ObjectType) type;
                return new ObjectType(handleNonePrefix(obj.getClassName()));
            }
            return type;
        }

        private static MethodDescriptor handleNonePrefix(final MethodDescriptor descriptor) {
            return new MethodDescriptor(
                    descriptor.getParamTypes().stream()
                            .map(Processor::handleNonePrefix)
                            .collect(Collectors.toList()),
                    handleNonePrefix(descriptor.getReturnType())
            );
        }

        private final Deque<ClassMapping<?>> stack = new ArrayDeque<>();
        private MethodMapping currentMethod = null;
        private int lastIndentLevel = 0;

        public Processor(final MappingSet mappings) {
            super(mappings);
        }

        public Processor() {
            this(MappingSet.create());
        }

        @Override
        public void accept(final String rawLine) {
            final int indentLevel = getIndentLevel(rawLine);

            // If there is a change in the indentation level, we will need to alter the
            // state as need be.
            final int classLevel = indentLevel - (this.currentMethod == null ? 0 : 1);
            if (classLevel < this.stack.size()) {
                final int difference = this.stack.size() - classLevel;
                final int indentDifference = this.lastIndentLevel - indentLevel;

                // as the stack is exclusive to classes, don't pop anything when a method
                // is the container
                if (!(this.currentMethod != null && indentDifference == 1)) {
                    for (int i = 0; i < difference; i++) {
                        this.stack.pop();
                    }
                }

                // wipe the current method
                this.currentMethod = null;
            }

            final String line = EnigmaConstants.removeComments(rawLine).trim();
            if (line.isEmpty()) return;

            // Split up the line, for further processing
            final String[] split = SPACE.split(line);
            final int len = split.length;

            // Establish the type of mapping
            final String key = split[0];
            if (key.equals(CLASS_MAPPING_KEY) && len == CLASS_MAPPING_ELEMENT_WITHOUT_DEOBF_COUNT) {
                final String obfName = handleNonePrefix(split[1]);
                this.stack.push(this.mappings.getOrCreateClassMapping(obfName));
            }
            else if (key.equals(CLASS_MAPPING_KEY) && len == CLASS_MAPPING_ELEMENT_WITH_DEOBF_COUNT) {
                final String obfName = handleNonePrefix(split[1]);
                final String deobfName = handleNonePrefix(split[2]);
                this.stack.push(this.mappings.getOrCreateClassMapping(obfName)
                        .setDeobfuscatedName(deobfName));
            }
            else if (key.equals(FIELD_MAPPING_KEY) && len == FIELD_MAPPING_ELEMENT_COUNT) {
                final String obfName = split[1];
                final String deobfName = split[2];
                final String type = handleNonePrefix(FieldType.of(split[3])).toString();
                this.stack.peek().getOrCreateFieldMapping(obfName, type)
                        .setDeobfuscatedName(deobfName);
            }
            else if (key.equals(METHOD_MAPPING_KEY) && len == METHOD_MAPPING_ELEMENT_WITHOUT_DEOBF_COUNT) {
                final String obfName = split[1];
                final String descriptor = handleNonePrefix(MethodDescriptor.of(split[2])).toString();
                this.currentMethod = this.stack.peek().getOrCreateMethodMapping(obfName, descriptor);
            }
            else if (key.equals(METHOD_MAPPING_KEY) && len == METHOD_MAPPING_ELEMENT_WITH_DEOBF_COUNT) {
                final String obfName = split[1];
                final String deobfName = split[2];
                final String descriptor = handleNonePrefix(MethodDescriptor.of(split[3])).toString();
                this.currentMethod = this.stack.peek().getOrCreateMethodMapping(obfName, descriptor)
                        .setDeobfuscatedName(deobfName);
            }
            else if (key.equals(PARAM_MAPPING_KEY) && len == PARAM_MAPPING_ELEMENT_COUNT) {
                final int index = Integer.parseInt(split[1]);
                final String deobfName = split[2];
                this.currentMethod.getOrCreateParameterMapping(index)
                        .setDeobfuscatedName(deobfName);
            }

            this.lastIndentLevel = indentLevel;
        }

    }

}

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

package org.cadixdev.lorenz.io.jam;

import org.cadixdev.lorenz.MappingSet;
import org.cadixdev.lorenz.io.MappingsReader;
import org.cadixdev.lorenz.io.TextMappingsReader;

import java.io.Reader;

/**
 * An implementation of {@link MappingsReader} for the JAM format.
 *
 * @author Jamie Mansfield
 * @since 0.4.0
 */
public class JamReader extends TextMappingsReader {

    public JamReader(final Reader reader) {
        super(reader, Processor::new);
    }

    public static class Processor extends TextMappingsReader.Processor {

        private static final String CLASS_MAPPING_KEY = "CL";
        private static final String FIELD_MAPPING_KEY = "FD";
        private static final String METHOD_MAPPING_KEY = "MD";
        private static final String PARAM_MAPPING_KEY = "MP";

        private static final int CLASS_MAPPING_ELEMENT_COUNT = 3;
        private static final int FIELD_MAPPING_ELEMENT_COUNT = 5;
        private static final int METHOD_MAPPING_ELEMENT_COUNT = 5;
        private static final int PARAM_MAPPING_ELEMENT_COUNT = 6;

        public Processor(final MappingSet mappings) {
            super(mappings);
        }

        /**
         * Creates a mappings parser for the JAM format.
         */
        public Processor() {
            this(new MappingSet());
        }

        @Override
        public void accept(final String rawLine) {
            final String line = JamConstants.removeComments(rawLine).trim();
            if (line.isEmpty()) return;

            if (line.length() < 4) {
                throw new IllegalArgumentException("Faulty JAM mapping encountered: `" + line + "`!");
            }

            // Split up the line, for further processing
            final String[] split = SPACE.split(line);
            final int len = split.length;

            // Establish the type of mapping
            final String key = split[0];
            if (key.equals(CLASS_MAPPING_KEY) && len == CLASS_MAPPING_ELEMENT_COUNT) {
                final String obfName = split[1];
                final String deobfName = split[2];

                this.mappings.getOrCreateClassMapping(obfName)
                        .setDeobfuscatedName(deobfName);
            }
            else if (key.equals(FIELD_MAPPING_KEY) && len == FIELD_MAPPING_ELEMENT_COUNT) {
                final String owningClass = split[1];
                final String obfName = split[2];
                final String obfDescriptor = split[3];
                final String deobfName = split[4];

                this.mappings.getOrCreateClassMapping(owningClass)
                        .getOrCreateFieldMapping(obfName, obfDescriptor)
                        .setDeobfuscatedName(deobfName);
            }
            else if (key.equals(METHOD_MAPPING_KEY) && len == METHOD_MAPPING_ELEMENT_COUNT) {
                final String owningClass = split[1];
                final String obfName = split[2];
                final String obfDescriptor = split[3];
                final String deobfName = split[4];

                this.mappings.getOrCreateClassMapping(owningClass)
                        .getOrCreateMethodMapping(obfName, obfDescriptor)
                        .setDeobfuscatedName(deobfName);
            }
            else if (key.equals(PARAM_MAPPING_KEY) && len == PARAM_MAPPING_ELEMENT_COUNT) {
                final String owningClass = split[1];
                final String owningMethod = split[2];
                final String owningMethodDescriptor = split[3];
                final int index;
                try {
                    index = Integer.parseInt(split[4]);
                }
                catch (final Exception ex) {
                    throw new IllegalArgumentException("'" + split[4] + "' is not an integer!");
                }
                final String deobfName = split[5];

                this.mappings.getOrCreateClassMapping(owningClass)
                        .getOrCreateMethodMapping(owningMethod, owningMethodDescriptor)
                        .getOrCreateParameterMapping(index)
                        .setDeobfuscatedName(deobfName);
            }
        }

    }

}

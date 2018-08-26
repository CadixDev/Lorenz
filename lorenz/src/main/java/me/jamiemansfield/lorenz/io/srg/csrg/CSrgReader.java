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

package me.jamiemansfield.lorenz.io.srg.csrg;

import me.jamiemansfield.lorenz.MappingSet;
import me.jamiemansfield.lorenz.io.MappingsReader;
import me.jamiemansfield.lorenz.io.TextMappingsReader;
import me.jamiemansfield.lorenz.io.srg.SrgConstants;

import java.io.IOException;
import java.io.InputStream;

/**
 * An implementation of {@link MappingsReader} for the CSRG format.
 *
 * @author Jamie Mansfield
 * @since 0.4.0
 */
public class CSrgReader extends TextMappingsReader {

    /**
     * Creates a new CSRG mappings reader, for the given {@link InputStream}.
     *
     * @param stream The input stream
     */
    public CSrgReader(final InputStream stream) {
        super(stream, Processor::new);
    }

    /**
     * The mappings processor for the CSRG format.
     */
    public static class Processor extends TextMappingsReader.Processor {

        private static final int CLASS_MAPPING_ELEMENT_COUNT = 2;
        private static final int FIELD_MAPPING_ELEMENT_COUNT = 3;
        private static final int METHOD_MAPPING_ELEMENT_COUNT = 4;

        /**
         * Creates a mappings parser for the CSRG format, with the provided {@link MappingSet}.
         *
         * @param mappings The mappings set
         */
        public Processor(final MappingSet mappings) {
            super(mappings);
        }

        /**
         * Creates a mappings parser for the CSRG format.
         */
        public Processor() {
            this(MappingSet.create());
        }

        @Override
        public void accept(final String rawLine) {
            final String line = SrgConstants.removeComments(rawLine).trim();
            if (line.isEmpty()) return;

            if (line.length() < 4) {
                throw new IllegalArgumentException("Faulty CSRG mapping encountered: `" + line + "`!");
            }

            // Split up the line, for further processing
            final String[] split = SPACE.split(line);
            final int len = split.length;

            // Process class mappings
            if (len == CLASS_MAPPING_ELEMENT_COUNT) {
                final String obfuscatedName = split[0];
                final String deobfuscatedName = split[1];

                // Get mapping, and set de-obfuscated name
                this.mappings.getOrCreateClassMapping(obfuscatedName)
                        .setDeobfuscatedName(deobfuscatedName);
            }
            // Process field mapping
            else if (len == FIELD_MAPPING_ELEMENT_COUNT) {
                final String parentClass = split[0];
                final String obfuscatedName = split[1];
                final String deobfuscatedName = split[2];

                // Get mapping, and set de-obfuscated name
                this.mappings.getOrCreateClassMapping(parentClass)
                        .getOrCreateFieldMapping(obfuscatedName)
                        .setDeobfuscatedName(deobfuscatedName);
            }
            // Process method mapping
            else if (len == METHOD_MAPPING_ELEMENT_COUNT) {
                final String parentClass = split[0];
                final String obfuscatedName = split[1];
                final String obfuscatedSignature = split[2];
                final String deobfuscatedName = split[3];

                // Get mapping, and set de-obfuscated name
                this.mappings.getOrCreateClassMapping(parentClass)
                        .getOrCreateMethodMapping(obfuscatedName, obfuscatedSignature)
                        .setDeobfuscatedName(deobfuscatedName);
            }
            else {
                throw new IllegalArgumentException("Failed to process line: `" + line + "`!");
            }
        }

    }

}

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

package me.jamiemansfield.lorenz.io.reader;

import me.jamiemansfield.lorenz.MappingSet;
import me.jamiemansfield.lorenz.model.ClassMapping;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.stream.Stream;

/**
 * An implementation of {@link MappingsReader} for the TSRG format.
 *
 * @author Jamie Mansfield
 * @since 0.2.0
 */
public class TSrgReader extends MappingsReader {

    /**
     * Creates a new TSRG mappings reader, for the given {@link BufferedReader}.
     *
     * @param reader The buffered reader
     */
    public TSrgReader(final BufferedReader reader) {
        super(reader, Processor::new);
    }

    /**
     * The mappings processor for the TSRG format.
     *
     * @since 0.4.0
     */
    public static class Processor extends MappingsReader.Processor {

        private static final int CLASS_MAPPING_ELEMENT_COUNT = 2;
        private static final int FIELD_MAPPING_ELEMENT_COUNT = 2;
        private static final int METHOD_MAPPING_ELEMENT_COUNT = 3;

        private ClassMapping currentClass;

        /**
         * Creates a mappings parser for the TSRG format, with the provided {@link MappingSet}.
         *
         * @param mappings The mappings set
         */
        public Processor(final MappingSet mappings) {
            super(mappings);
        }

        /**
         * Creates a mappings parser for the TSRG format.
         */
        public Processor() {
            this(MappingSet.create());
        }

        @Override
        public boolean processLine(final String rawLine) throws IOException {
            Stream.of(rawLine)
                    // Handle comments, by removing them.
                    // This implementation will allow comments to be placed anywhere
                    .map(SrgReader.Processor::removeComments)
                    // Filter out empty lines
                    .filter(line -> !line.isEmpty())
                    // Process line
                    .forEach(line -> {
                        if (line.length() < 4) {
                            System.out.println("Faulty TSRG mapping encountered: `" + line + "` - ignoring");
                            return;
                        }
                        // Split up the line, for further processing
                        final String[] split = SPACE.split(line);
                        final int len = split.length;

                        // Process class mappings
                        if (!split[0].startsWith("\t") && len == CLASS_MAPPING_ELEMENT_COUNT) {
                            final String obfuscatedName = split[0];
                            final String deobfuscatedName = split[1];

                            // Get mapping, and set de-obfuscated name
                            this.currentClass = this.mappings.getOrCreateClassMapping(obfuscatedName);
                            this.currentClass.setDeobfuscatedName(deobfuscatedName);
                        }
                        else if (split[0].startsWith("\t") && this.currentClass != null) {
                            final String obfuscatedName = split[0].replace("\t", "");

                            // Process field mapping
                            if (len == FIELD_MAPPING_ELEMENT_COUNT) {
                                final String deobfuscatedName = split[1];

                                // Get mapping, and set de-obfuscated name
                                this.currentClass
                                        .getOrCreateFieldMapping(obfuscatedName)
                                        .setDeobfuscatedName(deobfuscatedName);
                            }
                            // Process method mapping
                            else if (len == METHOD_MAPPING_ELEMENT_COUNT) {
                                final String obfuscatedSignature = split[1];
                                final String deobfuscatedName = split[2];

                                // Get mapping, and set de-obfuscated name
                                this.currentClass
                                        .getOrCreateMethodMapping(obfuscatedName, obfuscatedSignature)
                                        .setDeobfuscatedName(deobfuscatedName);
                            }
                        } else {
                            System.out.println("Failed to process line: " + line);
                        }
                    });
            return true;
        }

    }

}

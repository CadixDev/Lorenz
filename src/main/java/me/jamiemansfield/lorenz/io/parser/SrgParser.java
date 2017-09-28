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

package me.jamiemansfield.lorenz.io.parser;

import me.jamiemansfield.lorenz.MappingSet;

import java.io.IOException;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * The mappings parser for the SRG format.
 */
public class SrgParser extends MappingsParser {

    /**
     * A regex expression used to remove comments from lines.
     */
    private static final Pattern HASH_COMMENT = Pattern.compile("#.+");

    /**
     * Removes present comments, from the given {@link String} line.
     *
     * @param line The line
     * @return The comment-omitted line
     */
    public static String removeComments(final String line) {
        return HASH_COMMENT.matcher(line).replaceAll("");
    }

    private static final String CLASS_MAPPING_KEY = "CL:";
    private static final String FIELD_MAPPING_KEY = "FD:";
    private static final String METHOD_MAPPING_KEY = "MD:";

    private static final int CLASS_MAPPING_ELEMENT_COUNT = 3;
    private static final int FIELD_MAPPING_ELEMENT_COUNT = 3;
    private static final int METHOD_MAPPING_ELEMENT_COUNT = 5;

    /**
     * Creates a mappings parser for the SRG format, with the provided {@link MappingSet}.
     *
     * @param mappings The mappings set
     */
    public SrgParser(final MappingSet mappings) {
        super(mappings);
    }

    /**
     * Creates a mappings parser for the SRG format.
     */
    public SrgParser() {
        this(new MappingSet());
    }

    @Override
    public boolean processLine(final String rawLine) throws IOException {
        Stream.of(rawLine)
                // Handle comments, by removing them.
                // This implementation will allow comments to be placed anywhere
                .map(SrgParser::removeComments)
                // Trim the line
                .map(String::trim)
                // Filter out empty lines
                .filter(line -> !line.isEmpty())
                // Process line
                .forEach(line -> {
                    if (line.length() < 4) {
                        System.out.println("Faulty SRG mapping encountered: `" + line + "` - ignoring");
                        return;
                    }
                    // Split up the line, for further processing
                    final String[] split = SPACE.split(line);
                    final int len = split.length;

                    // Establish the type of mapping
                    final String key = split[0];
                    if (key.equals(CLASS_MAPPING_KEY) && len == CLASS_MAPPING_ELEMENT_COUNT) {
                        final String obfuscatedName = split[1];
                        final String deobfuscatedName = split[2];

                        // Get mapping, and set de-obfuscated name
                        this.mappings.getOrCreateClassMapping(obfuscatedName)
                                .setDeobfuscatedName(deobfuscatedName);
                    } else if (key.equals(FIELD_MAPPING_KEY) && len == FIELD_MAPPING_ELEMENT_COUNT) {
                        final String fullObfuscatedName = split[1];
                        final String fullDeobfuscatedName = split[2];
                        final int lastIndex = fullObfuscatedName.lastIndexOf('/');
                        final String owningClass = fullObfuscatedName.substring(0, lastIndex);
                        final String obfuscatedName = fullObfuscatedName.substring(lastIndex + 1);
                        final String deobfuscatedName = fullDeobfuscatedName.substring(fullDeobfuscatedName.lastIndexOf('/') + 1);

                        // Get mapping, and set de-obfuscated name
                        this.mappings.getOrCreateClassMapping(owningClass)
                                .getOrCreateFieldMapping(obfuscatedName)
                                .setDeobfuscatedName(deobfuscatedName);
                    } else if (key.equals(METHOD_MAPPING_KEY) && len == METHOD_MAPPING_ELEMENT_COUNT) {
                        final String fullObfuscatedName = split[1];
                        final String obfuscatedSignature = split[2];
                        final String fullDeobfuscatedName = split[3];
                        final String deobfuscatedSignature = split[4];
                        final int lastIndex = fullObfuscatedName.lastIndexOf('/');
                        final String owningClass = fullObfuscatedName.substring(0, lastIndex);
                        final String obfuscatedName = fullObfuscatedName.substring(lastIndex + 1);
                        final String deobfuscatedName = fullDeobfuscatedName.substring(fullDeobfuscatedName.lastIndexOf('/') + 1);

                        // Get mapping, and set de-obfuscated name
                        this.mappings.getOrCreateClassMapping(owningClass)
                                .getOrCreateMethodMapping(obfuscatedName, obfuscatedSignature)
                                .setDeobfuscatedName(deobfuscatedName);
                    } else {
                        System.out.println("Found unrecognised key: `" + key + "` - ignoring");
                    }
                });
        return true;
    }

}

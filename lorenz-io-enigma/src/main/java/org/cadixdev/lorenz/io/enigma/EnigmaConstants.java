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

import java.util.regex.Pattern;

/**
 * A collection of constants and utilities specific to
 * the Enigma mapping format.
 *
 * @author Jamie Mansfield
 * @since 0.4.0
 */
public final class EnigmaConstants {

    /**
     * A regex expression used to remove comments from lines.
     */
    private static final Pattern HASH_COMMENT = Pattern.compile("#.*");

    /**
     * Removes present comments, from the given {@link String} line.
     *
     * @param line The line
     * @return The comment-omitted line
     */
    public static String removeComments(final String line) {
        return HASH_COMMENT.matcher(line).replaceAll("");
    }

    private EnigmaConstants() {
    }

    /**
     * A collection of file extensions frequently used by
     * consumers of the Enigma format.
     *
     * @author Jamie Mansfield
     * @since 0.6.0
     */
    public static final class FileExtensions {

        /**
         * The <code>"mapping"</code> file extension, as used by both
         * cuchaz's mapping project and Fabric's Yarn mappings.
         */
        public static final String MAPPING = "mapping";

        /**
         * The <code>"enigma"</code> file extension.
         * <p>
         * This exists for largely historical reasons, as it was erroneously
         * used as the standard file extension for Enigma by Lorenz prior to
         * Lorenz 0.6.
         */
        public static final String ENIGMA = "enigma";

        private FileExtensions() {
        }

    }

}

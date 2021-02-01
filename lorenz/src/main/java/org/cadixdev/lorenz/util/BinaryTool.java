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

package org.cadixdev.lorenz.util;

/**
 * A utility for working with binary names in Lorenz.
 *
 * @author Jamie Mansfield
 * @since 0.5.7
 */
public final class BinaryTool {

    /**
     * Gets the split hierarchy of a binary name.
     * <p>
     * For example, calling {@code from("a$b$c"}} would produce
     * {@code ["a", "b", "c"]}.
     *
     * @param binaryName The binary name
     * @return The name hierarchy
     */
    public static String[] from(final String binaryName) {
        return binaryName.split("\\$");
    }

    /**
     * Gets the binary name for a split hierarchy.
     *
     * @param name The hierarchy
     * @return The binary name
     */
    public static String to(final String[] name) {
        final StringBuilder builder = new StringBuilder();

        for (int i = 0; i < name.length; i++) {
            builder.append(name[i]);

            if (i != name.length - 1) {
                builder.append('$');
            }
        }

        return builder.toString();
    }

    private BinaryTool() {
    }

}

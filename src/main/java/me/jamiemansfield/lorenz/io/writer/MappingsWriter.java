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

package me.jamiemansfield.lorenz.io.writer;

import me.jamiemansfield.lorenz.MappingSet;
import me.jamiemansfield.lorenz.model.Mapping;

import java.io.Closeable;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.Collectors;

/**
 * Represents a writer, that is capable of writing de-obfuscation
 * mappings to a {@link PrintWriter}.
 *
 * Each mappings writer will be designed for a specific mapping
 * format, and intended to be used with try-for-resources.
 *
 * @see SrgWriter
 * @see CSrgWriter
 * @see TSrgWriter
 *
 * @author Jamie Mansfield
 * @since 0.1.0
 */
public abstract class MappingsWriter implements Closeable {

    /**
     * A {@link Comparator} used to alphabetise a collection of {@link Mapping}s.
     */
    protected static final Comparator<Mapping> ALPHABETISE_MAPPINGS =
            (o1, o2) -> o1.getFullObfuscatedName().compareToIgnoreCase(o2.getFullObfuscatedName());

    /**
     * Indents the given String at each newline, with 4 spaces.
     *
     * @param str The String to indent
     * @return The indented String
     */
    protected static String indentSpaces(final String str) {
        return Arrays.stream(str.split("\n"))
                .map(line -> "    " + line + "\n")
                .collect(Collectors.joining());
    }

    /**
     * Indents the given String at each newline, with a tab.
     *
     * @param str The String to indent
     * @return The indented String
     */
    protected static String indentTab(final String str) {
        return Arrays.stream(str.split("\n"))
                .map(line -> "\t" + line + "\n")
                .collect(Collectors.joining());
    }

    protected final PrintWriter writer;

    /**
     * Creates a new mappings writer, from the given {@link PrintWriter}.
     *
     * @param writer The print writer, to write to
     */
    protected MappingsWriter(final PrintWriter writer) {
        this.writer = writer;
    }

    /**
     * Writes the given mappings to the previously given {@link PrintWriter}.
     *
     * @param mappings The mapping set
     */
    public abstract void write(final MappingSet mappings);

    @Override
    public void close() throws IOException {
        this.writer.close();
    }

}

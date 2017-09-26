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

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.util.function.Function;

/**
 * Represents a parser, that parses mappings from a {@link BufferedReader}.
 *
 * @see SrgParser
 */
public abstract class MappingsParser implements Closeable {

    protected final BufferedReader reader;
    protected final Function<MappingSet, MappingsProcessor> processor;

    /**
     * Creates a new mappings parser, from the given {@link BufferedReader}.
     *
     * @param reader The buffered reader
     * @param processor The function to create a {@link MappingsProcessor} for the format
     */
    protected MappingsParser(final BufferedReader reader, final Function<MappingSet, MappingsProcessor> processor) {
        this.reader = reader;
        this.processor = processor;
    }

    /**
     * Parses mappings from the previously given {@link BufferedReader}, to
     * a new {@link MappingSet}
     *
     * @return The mapping set
     */
    public MappingSet parse() {
        return this.parse(new MappingSet());
    }

    /**
     * Parses mappings from the previously given {@link BufferedReader}, to
     * the given {@link MappingSet}.
     *
     * @param mappings The mapping set
     * @return The mapping set, to allow for chaining
     */
    public MappingSet parse(final MappingSet mappings) {
        final MappingsProcessor processor = this.processor.apply(mappings);
        this.reader.lines()
                // Process line
                .forEach(line -> {
                    try {
                        processor.processLine(line);
                    } catch (final IOException ignored) {
                    }
                });
        return processor.getResult();
    }

    @Override
    public void close() throws IOException {
        this.reader.close();
    }

}

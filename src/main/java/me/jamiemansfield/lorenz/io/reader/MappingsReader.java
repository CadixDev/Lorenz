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

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.util.function.Function;

/**
 * Represents a reader that parses mappings from a {@link BufferedReader},
 * using a {@link MappingsProcessor}.
 *
 * @see SrgReader
 */
public abstract class MappingsReader implements Closeable {

    protected final BufferedReader reader;
    protected final Function<MappingSet, MappingsProcessor> parser;

    /**
     * Creates a new mappings reader, for the given {@link BufferedReader}.
     *
     * @param reader The buffered reader
     * @param parser The function to create a {@link MappingsProcessor} for the format
     */
    protected MappingsReader(final BufferedReader reader, final Function<MappingSet, MappingsProcessor> parser) {
        this.reader = reader;
        this.parser = parser;
    }

    /**
     * Parses mappings from the previously given {@link BufferedReader}, to
     * a new {@link MappingSet}
     *
     * @return The mapping set
     */
    public MappingSet parse() {
        return this.parse(MappingSet.create());
    }

    /**
     * Parses mappings from the previously given {@link BufferedReader}, to
     * the given {@link MappingSet}.
     *
     * @param mappings The mapping set
     * @return The mapping set, to allow for chaining
     */
    public MappingSet parse(final MappingSet mappings) {
        final MappingsProcessor processor = this.parser.apply(mappings);
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

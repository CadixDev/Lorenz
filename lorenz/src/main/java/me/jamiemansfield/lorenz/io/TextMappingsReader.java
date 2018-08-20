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

package me.jamiemansfield.lorenz.io;

import com.google.common.io.LineProcessor;
import me.jamiemansfield.lorenz.MappingSet;
import me.jamiemansfield.lorenz.io.enigma.EnigmaReader;
import me.jamiemansfield.lorenz.io.jam.JamReader;
import me.jamiemansfield.lorenz.io.srg.csrg.CSrgReader;
import me.jamiemansfield.lorenz.io.srg.SrgReader;
import me.jamiemansfield.lorenz.io.srg.tsrg.TSrgReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * An implementation of {@link MappingsReader} designed to aid
 * with the implementation of mapping readers for text-based
 * mapping formats.
 *
 * @see EnigmaReader
 * @see JamReader
 * @see SrgReader
 * @see CSrgReader
 * @see TSrgReader
 *
 * @author Jamie Mansfield
 * @since 0.4.0
 */
public abstract class TextMappingsReader extends MappingsReader {

    protected final BufferedReader reader;
    protected final Function<MappingSet, Processor> processor;

    /**
     * Creates a new mappings reader, for the given {@link InputStream}.
     *
     * @param stream The input stream
     * @param processor The line processor to use for reading the lines
     */
    protected TextMappingsReader(final InputStream stream, final Function<MappingSet, Processor> processor) {
        super(stream);
        this.reader = new BufferedReader(new InputStreamReader(stream));
        this.processor = processor;
    }

    @Override
    public MappingSet read(final MappingSet mappings) {
        final Processor processor = this.processor.apply(mappings);
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

    /**
     * A parser for a given mappings format, that is built upon
     * Guava's {@link LineProcessor}.
     *
     * @see SrgReader.Processor
     * @see CSrgReader.Processor
     * @see TSrgReader.Processor
     *
     * @since 0.4.0
     */
    public static abstract class Processor implements LineProcessor<MappingSet> {

        /**
         * A regular expression used to split {@link String}s at spaces.
         */
        protected static final Pattern SPACE = Pattern.compile(" ", Pattern.LITERAL);

        protected final MappingSet mappings;

        /**
         * Creates a mappings parser, to process the lines in a
         * mappings file.
         *
         * @param mappings The mappings set
         */
        protected Processor(final MappingSet mappings) {
            this.mappings = mappings;
        }

        @Override
        public MappingSet getResult() {
            return this.mappings;
        }

    }

}

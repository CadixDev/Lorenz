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
import me.jamiemansfield.lorenz.model.jar.FieldTypeProvider;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

/**
 * Represents a reader that reads de-obfuscation mappings from a
 * given {@link InputStream}.
 *
 * @param <S> The type of the stream
 *
 * @see TextMappingsReader
 * @see BinaryMappingsReader
 *
 * @author Jamie Mansfield
 * @since 0.2.0
 */
public abstract class MappingsReader<S extends InputStream> implements Closeable {

    protected final S stream;

    /**
     * Creates a new mappings reader, for the given {@link InputStream}.
     *
     * @param stream The input stream
     */
    protected MappingsReader(final S stream) {
        this.stream = stream;
    }

    /**
     * Parses mappings from the previously given {@link InputStream}, to
     * a new {@link MappingSet}.
     *
     * @return The mapping set
     */
    public MappingSet parse() {
        return this.parse(MappingSet.create());
    }

    /**
     * Parses mappings from the previously given {@link InputStream}, to
     * a new {@link MappingSet} using the given {@link FieldTypeProvider}.
     *
     * @param fieldTypeProvider The field type provider to use
     * @return The mapping set
     * @since 0.3.0
     */
    public MappingSet parse(final FieldTypeProvider fieldTypeProvider) {
        return this.parse(MappingSet.create(fieldTypeProvider));
    }

    /**
     * Parses mappings from the previously given {@link InputStream}, to
     * the given {@link MappingSet}.
     *
     * @param mappings The mapping set
     * @return The mapping set, to allow for chaining
     */
    public abstract MappingSet parse(final MappingSet mappings);

    @Override
    public void close() throws IOException {
        this.stream.close();
    }

}

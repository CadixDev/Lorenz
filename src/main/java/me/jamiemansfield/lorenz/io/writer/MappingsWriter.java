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

import java.io.Closeable;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Represents a writer, that writes mappings.
 *
 * This class will likely not be of too great use, and most will find
 * the child classes for SRG, etc mappings more useful.
 *
 * @see SrgWriter
 */
public abstract class MappingsWriter implements Closeable {

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

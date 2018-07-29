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

import me.jamiemansfield.lorenz.MappingSet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

/**
 * A representation of a de-obfuscation mapping format.
 *
 * @author Jamie Mansfield
 * @since 0.4.0
 */
public interface MappingFormat {

    /**
     * Creates a {@link MappingsReader} from the given {@link InputStream}
     * for the mapping format.
     *
     * @param stream The input stream
     * @return The mapping reader
     * @throws IOException Should an I/O issue occur
     * @throws UnsupportedOperationException If the format does not support reading
     */
    MappingsReader createReader(final InputStream stream) throws IOException;

    /**
     * Reads a mappings file into the given {@link MappingSet}.
     *
     * @param mappings The mapping set to read in to
     * @param path The path of the mappings file
     * @return The mappings
     * @throws IOException Should an I/O issue occur
     */
    default MappingSet read(final MappingSet mappings, final Path path) throws IOException {
        try (final MappingsReader reader = this.createReader(Files.newInputStream(path))) {
            reader.read(mappings);
        }
        return mappings;
    }

    /**
     * Reads a mappings file into a {@link MappingSet}.
     *
     * @param path The path of the mappings file
     * @return The mappings
     * @throws IOException Should an I/O issue occur
     */
    default MappingSet read(final Path path) throws IOException {
        return this.read(MappingSet.create(), path);
    }

    /**
     * Creates a {@link MappingsWriter} from the given {@link OutputStream}
     * for the mapping format.
     *
     * @param stream The output stream
     * @return The mapping writer
     * @throws IOException Should an I/O issue occur
     * @throws UnsupportedOperationException If the format does not support reading
     */
    MappingsWriter createWriter(final OutputStream stream) throws IOException;

    /**
     * Writes a mapping set to file.
     *
     * @param mappings The mapping set to write
     * @param path The path of the mappings file
     * @throws IOException Should an I/O issue occur
     */
    default void write(final MappingSet mappings, final Path path) throws IOException {
        try (final MappingsWriter writer = this.createWriter(Files.newOutputStream(path))) {
            writer.write(mappings);
        }
    }

    /**
     * Gets the typically used file extension for the format, if available.
     *
     * @return The standard file extension
     */
    Optional<String> getStandardFileExtension();

}

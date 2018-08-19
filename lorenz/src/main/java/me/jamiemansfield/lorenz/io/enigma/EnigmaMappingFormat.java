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

package me.jamiemansfield.lorenz.io.enigma;

import me.jamiemansfield.lorenz.io.MappingFormat;
import me.jamiemansfield.lorenz.io.MappingsReader;
import me.jamiemansfield.lorenz.io.MappingsWriter;
import me.jamiemansfield.lorenz.io.jam.JamConstants;
import me.jamiemansfield.lorenz.io.jam.JamReader;
import me.jamiemansfield.lorenz.io.jam.JamWriter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Optional;

/**
 * The Enigma mapping format.
 *
 * @author Jamie Mansfield
 * @since 0.4.0
 */
public class EnigmaMappingFormat implements MappingFormat {

    @Override
    public MappingsReader createReader(final InputStream stream) throws IOException {
        return new EnigmaReader(stream);
    }

    @Override
    public MappingsWriter createWriter(final OutputStream stream) throws IOException {
        return new EnigmaWriter(stream);
    }

    @Override
    public Optional<String> getStandardFileExtension() {
        return Optional.of(EnigmaConstants.STANDARD_EXTENSION);
    }

}

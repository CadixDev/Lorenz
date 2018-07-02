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

package me.jamiemansfield.lorenz.test.io.reader;

import me.jamiemansfield.lorenz.MappingSet;
import me.jamiemansfield.lorenz.io.reader.MappingsReader;
import org.junit.Ignore;

import java.util.concurrent.Callable;

/**
 * A variety of unit tests pertaining to {@link MappingsReader}.
 */
@Ignore
public class AbstractMappingsReaderTest extends ProcessorTest {

    protected AbstractMappingsReaderTest(final Callable<MappingsReader> readerFunction) throws Exception {
        super(((Callable<MappingSet>) () -> {
            final MappingsReader reader = readerFunction.call();
            final MappingSet mappings = reader.parse();
            reader.close();
            return mappings;
        }).call());
    }

}

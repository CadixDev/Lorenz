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

import com.google.common.io.LineProcessor;
import me.jamiemansfield.lorenz.MappingSet;

import java.util.regex.Pattern;

/**
 * A parser for a given mappings format, that is built upon
 * Guava's {@link LineProcessor}.
 *
 * @see SrgProcessor
 * @see CSrgProcessor
 * @see TSrgProcessor
 *
 * @author Jamie Mansfield
 * @since 0.2.0
 */
public abstract class MappingsProcessor implements LineProcessor<MappingSet> {

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
    protected MappingsProcessor(final MappingSet mappings) {
        this.mappings = mappings;
    }

    @Override
    public MappingSet getResult() {
        return this.mappings;
    }

}

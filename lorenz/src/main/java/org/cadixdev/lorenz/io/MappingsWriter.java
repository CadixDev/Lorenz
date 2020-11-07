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

package org.cadixdev.lorenz.io;

import org.cadixdev.bombe.type.FieldType;
import org.cadixdev.lorenz.MappingSet;
import org.cadixdev.lorenz.model.FieldMapping;
import org.cadixdev.lorenz.model.Mapping;
import org.cadixdev.lorenz.model.MethodMapping;

import java.io.Closeable;
import java.io.IOException;
import java.util.Comparator;
import java.util.function.Function;

/**
 * Represents a writer, that is capable of writing de-obfuscation
 * mappings.
 * <p>
 * Each mappings writer will be designed for a specific mapping
 * format, and intended to be used with try-for-resources.
 *
 * @see TextMappingsWriter
 * @see BinaryMappingsWriter
 *
 * @author Jamie Mansfield
 * @since 0.4.0
 */
public abstract class MappingsWriter implements Closeable {

    protected MappingsWriterConfig config = MappingsWriterConfig.builder().build();

    /**
     * Gets the active {@link MappingsWriterConfig writer configuration} for
     * this mappings writer.
     *
     * @return The writer configuration
     * @since 0.5.5
     */
    public MappingsWriterConfig getConfig() {
        return this.config;
    }

    /**
     * Sets the active {@link MappingsWriterConfig writer configuration} for
     * this mappings writer - allowing the output of the writer to be fine-tuned
     * to fit the environment in use.
     *
     * @param config The writer configuration
     * @throws NullPointerException If {@code config} is {@code null}
     * @since 0.5.5
     */
    public void setConfig(final MappingsWriterConfig config) {
        if (config == null) {
            throw new NullPointerException("config cannot be null!");
        }

        this.config = config;
    }

    /**
     * Writes the given mappings to the previously given output.
     *
     * @param mappings The mapping set
     * @throws IOException Should an IO issue occur
     */
    public abstract void write(final MappingSet mappings) throws IOException;

}

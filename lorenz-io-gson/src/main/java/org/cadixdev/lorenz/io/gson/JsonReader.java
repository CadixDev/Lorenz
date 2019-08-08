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

package org.cadixdev.lorenz.io.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.cadixdev.lorenz.MappingSet;
import org.cadixdev.lorenz.io.MappingsReader;

import java.io.IOException;
import java.io.Reader;

/**
 * An implementation of {@link MappingsReader} for the JSON format.
 *
 * @author Jamie Mansfield
 * @since 0.6.0
 */
public class JsonReader extends MappingsReader {

    private final Reader reader;

    public JsonReader(final Reader reader) {
        this.reader = reader;
    }

    @Override
    public MappingSet read(final MappingSet mappings) throws IOException {
        final Gson GSON = new GsonBuilder()
            // Needs to be registerTypeHierarchyAdapter, as its an interface
            .registerTypeHierarchyAdapter(MappingSet.class, new MappingSetTypeAdapter(mappings))
            .create();
        return GSON.fromJson(this.reader, MappingSet.class);
    }

    @Override
    public void close() throws IOException {
        this.reader.close();
    }

}

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

package me.jamiemansfield.lorenz.io.srg;

import com.google.common.collect.Lists;
import me.jamiemansfield.lorenz.MappingSet;
import me.jamiemansfield.lorenz.io.MappingsWriter;
import me.jamiemansfield.lorenz.io.TextMappingsWriter;
import me.jamiemansfield.lorenz.model.ClassMapping;
import me.jamiemansfield.lorenz.model.FieldMapping;
import me.jamiemansfield.lorenz.model.Mapping;
import me.jamiemansfield.lorenz.model.MethodMapping;

import java.io.OutputStream;
import java.util.List;

/**
 * An implementation of {@link MappingsWriter} for the SRG format.
 *
 * @author Jamie Mansfield
 * @since 0.4.0
 */
public class SrgWriter extends TextMappingsWriter {

    private final List<String> classes = Lists.newArrayList();
    private final List<String> fields = Lists.newArrayList();
    private final List<String> methods = Lists.newArrayList();

    /**
     * Creates a new SRG mappings writer, from the given {@link OutputStream}.
     *
     * @param stream The output stream, to write to
     */
    public SrgWriter(final OutputStream stream) {
        super(stream);
    }

    @Override
    public void write(final MappingSet mappings) {
        // Write class mappings
        mappings.getTopLevelClassMappings().stream()
                .filter(ClassMapping::hasMappings)
                .sorted(ALPHABETISE_MAPPINGS)
                .forEach(this::writeClassMapping);

        // Write everything to the print writer
        this.classes.forEach(this.writer::println);
        this.fields.forEach(this.writer::println);
        this.methods.forEach(this.writer::println);

        // Clear out the lists, to ensure that mappings aren't written twice (or more)
        this.classes.clear();
        this.fields.clear();
        this.methods.clear();
    }

    /**
     * Writes the given {@link ClassMapping}, alongside its member mappings.
     *
     * @param mapping The class mapping
     */
    protected void writeClassMapping(final ClassMapping<?> mapping) {
        // Check if the mapping should be written, and if so write it
        if (mapping.hasDeobfuscatedName()) {
            this.classes.add(String.format("CL: %s %s", mapping.getFullObfuscatedName(), mapping.getFullDeobfuscatedName()));
        }

        // Write inner class mappings
        mapping.getInnerClassMappings().stream()
                .filter(ClassMapping::hasMappings)
                .sorted(ALPHABETISE_MAPPINGS)
                .forEach(this::writeClassMapping);

        // Write field mappings
        mapping.getFieldMappings().stream()
                .filter(Mapping::hasDeobfuscatedName)
                .sorted(ALPHABETISE_MAPPINGS)
                .forEach(this::writeFieldMapping);

        // Write method mappings
        mapping.getMethodMappings().stream()
                .filter(Mapping::hasDeobfuscatedName)
                .sorted(ALPHABETISE_MAPPINGS)
                .forEach(this::writeMethodMapping);
    }

    /**
     * Writes the given {@link FieldMapping}.
     *
     * @param mapping The field mapping
     */
    protected void writeFieldMapping(final FieldMapping mapping) {
        // The SHOULD_WRITE test should have already have been performed, so we're good
        this.fields.add(String.format("FD: %s %s", mapping.getFullObfuscatedName(), mapping.getFullDeobfuscatedName()));
    }

    /**
     * Writes the given {@link MethodMapping}.
     *
     * @param mapping The method mapping
     */
    protected void writeMethodMapping(final MethodMapping mapping) {
        // The SHOULD_WRITE test should have already have been performed, so we're good
        this.methods.add(String.format("MD: %s %s %s %s",
                mapping.getFullObfuscatedName(), mapping.getObfuscatedDescriptor(),
                mapping.getFullDeobfuscatedName(), mapping.getDeobfuscatedDescriptor()));
    }

}

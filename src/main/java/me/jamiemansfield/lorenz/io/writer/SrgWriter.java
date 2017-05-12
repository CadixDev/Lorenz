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

import me.jamiemansfield.poppy.mapping.MappingSet;
import me.jamiemansfield.poppy.mapping.model.ClassMapping;
import me.jamiemansfield.poppy.mapping.model.FieldMapping;
import me.jamiemansfield.poppy.mapping.model.Mapping;
import me.jamiemansfield.poppy.mapping.model.MethodMapping;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Comparator;

/**
 * An implementation of {@link MappingsWriter} for the SRG format.
 */
public class SrgWriter extends MappingsWriter {

    /**
     * A {@link Comparator} used to alphabetise a collection of {@link Mapping}s.
     */
    private static final Comparator<Mapping> ALPHABETISE_MAPPINGS =
            (o1, o2) -> o1.getFullObfuscatedName().compareToIgnoreCase(o2.getFullObfuscatedName());

    private final ByteArrayOutputStream clOut = new ByteArrayOutputStream();
    private final ByteArrayOutputStream fdOut = new ByteArrayOutputStream();
    private final ByteArrayOutputStream mdOut = new ByteArrayOutputStream();

    private final PrintWriter clWriter = new PrintWriter(clOut);
    private final PrintWriter fdWriter = new PrintWriter(fdOut);
    private final PrintWriter mdWriter = new PrintWriter(mdOut);

    /**
     * Creates a new SRG mappings writer, from the given {@link PrintWriter}.
     *
     * @param writer The print writer, to write to
     */
    public SrgWriter(final PrintWriter writer) {
        super(writer);
    }

    @Override
    public void write(final MappingSet mappings) {
        // Write class mappings
        mappings.getTopLevelClassMappings().stream()
                .sorted(ALPHABETISE_MAPPINGS)
                .forEach(this::writeClassMapping);

        // Write everything to the print writer
        this.writer.write(this.clOut.toString());
        this.writer.write(this.fdOut.toString());
        this.writer.write(this.mdOut.toString());
    }

    /**
     * Writes the given {@link ClassMapping}, alongside its member mappings.
     *
     * @param mapping The class mapping
     */
    protected void writeClassMapping(final ClassMapping mapping) {
        // Check if the mapping should be written, and if so: write it
        if (mapping.hasDeobfuscatedName()) {
            this.clWriter.format("CL: %s %s\n", mapping.getFullObfuscatedName(), mapping.getFullDeobfuscatedName());
        }

        // Write inner class mappings
        mapping.getInnerClassMappings().stream()
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
        this.fdWriter.format("FD: %s %s\n", mapping.getFullObfuscatedName(), mapping.getFullDeobfuscatedName());
    }

    /**
     * Writes the given {@link MethodMapping}.
     *
     * @param mapping The method mapping
     */
    protected void writeMethodMapping(final MethodMapping mapping) {
        // The SHOULD_WRITE test should have already have been performed, so we're good
        this.mdWriter.format("MD: %s %s %s %s\n",
                mapping.getFullObfuscatedName(), mapping.getObfuscatedSignature(),
                mapping.getFullDeobfuscatedName(), mapping.getDeobfuscatedSignature());
    }

    @Override
    public void close() throws IOException {
        this.clWriter.close();
        this.fdWriter.close();
        this.mdWriter.close();
        this.clOut.close();
        this.fdOut.close();
        this.mdOut.close();
        super.close();
    }

}

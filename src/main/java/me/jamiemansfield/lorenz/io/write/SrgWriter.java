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

package me.jamiemansfield.lorenz.io.write;

import me.jamiemansfield.lorenz.MappingsContainer;
import me.jamiemansfield.lorenz.model.BaseMapping;
import me.jamiemansfield.lorenz.model.ClassMapping;
import me.jamiemansfield.lorenz.model.FieldMapping;
import me.jamiemansfield.lorenz.model.MethodMapping;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Comparator;
import java.util.function.Predicate;

/**
 * The mappings writer, for the SRG format.
 */
public class SrgWriter extends MappingsWriter {

    private static final Predicate<BaseMapping> NOT_USELESS
            = mapping -> !mapping.getObfuscatedName().equals(mapping.getDeobfuscatedName());
    private static final Comparator<BaseMapping> ALPHABET_SORTER =
            (o1, o2) -> o1.getFullObfuscatedName().compareToIgnoreCase(o2.getFullObfuscatedName());

    private final ByteArrayOutputStream clOut = new ByteArrayOutputStream();
    private final ByteArrayOutputStream fdOut = new ByteArrayOutputStream();
    private final ByteArrayOutputStream mdOut = new ByteArrayOutputStream();

    private final PrintWriter clWriter = new PrintWriter(clOut);
    private final PrintWriter fdWriter = new PrintWriter(fdOut);
    private final PrintWriter mdWriter = new PrintWriter(mdOut);

    /**
     * Constructs a new {@link SrgWriter} which outputs to the given
     * {@link PrintWriter}.
     *
     * @param out The {@link PrintWriter} to output to
     */
    public SrgWriter(PrintWriter out) {
        super(out);
    }

    @Override
    public void writeMappings(final MappingsContainer mappings) {
        mappings.getClassMappings().values().stream()
                .sorted(ALPHABET_SORTER)
                .forEach(this::writeClassMapping);
        this.clWriter.close();
        this.fdWriter.close();
        this.mdWriter.close();
        this.writer.write(clOut.toString());
        this.writer.write(fdOut.toString());
        this.writer.write(mdOut.toString());
        this.writer.close();
        try {
            this.clOut.close();
            this.fdOut.close();
            this.mdOut.close();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void writeClassMapping(final ClassMapping classMapping) {
        if (!classMapping.getObfuscatedName().equals(classMapping.getDeobfuscatedName())) {
            this.clWriter.format("CL: %s %s\n",
                    classMapping.getFullObfuscatedName(), classMapping.getFullDeobfuscatedName());
        }

        classMapping.getInnerClassMappings().values().stream()
                .sorted(ALPHABET_SORTER)
                .forEach(this::writeClassMapping);
        classMapping.getFieldMappings().values().stream().filter(NOT_USELESS)
                .sorted(ALPHABET_SORTER)
                .forEach(this::writeFieldMapping);
        classMapping.getMethodMappings().values().stream().filter(NOT_USELESS)
                .sorted(ALPHABET_SORTER)
                .forEach(this::writeMethodMapping);
    }

    private void writeFieldMapping(final FieldMapping fieldMapping) {
        this.fdWriter.format("FD: %s %s\n",
                fieldMapping.getFullObfuscatedName(),
                fieldMapping.getFullDeobfuscatedName());
    }

    private void writeMethodMapping(final MethodMapping mapping) {
        this.mdWriter.format("MD: %s %s %s %s\n",
                mapping.getFullObfuscatedName(),
                mapping.getObfuscatedDescriptor(),
                mapping.getFullDeobfuscatedName(),
                mapping.getDeobfuscatedDescriptor());
    }
}

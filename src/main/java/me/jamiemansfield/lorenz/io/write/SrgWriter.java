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
import me.jamiemansfield.lorenz.model.InnerClassMapping;
import me.jamiemansfield.lorenz.model.TopLevelClassMapping;
import me.jamiemansfield.lorenz.model.ClassMapping;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The mappings writer, for the SRG format.
 */
public class SrgWriter extends MappingsWriter {

    /**
     * Constructs a new {@link SrgWriter} which outputs to the given
     * {@link PrintWriter}.
     *
     * @param writer The {@link PrintWriter} to output to
     */
    public SrgWriter(final PrintWriter writer) {
        super(writer);
    }

    @Override
    public void writeMappings(final MappingsContainer mappings) {
        final List<String> classLines = new ArrayList<>();
        final List<String> fieldLines = new ArrayList<>();
        final List<String> methodLines = new ArrayList<>();

        for (final TopLevelClassMapping classMapping : mappings.getClassMappings().values()) {
            if (!classMapping.getFullObfuscatedName().equals(classMapping.getFullDeobfuscatedName())) {
                classLines.add(String.format("CL: %s %s",
                        classMapping.getFullObfuscatedName(), classMapping.getFullDeobfuscatedName()));
            }
            classLines.addAll(this.getClassLinesFromInnerClasses(classMapping));

            fieldLines.addAll(classMapping.getFieldMappings().values().stream()
                    .map(fieldMapping -> String.format("FD: %s %s",
                            fieldMapping.getFullObfuscatedName(), fieldMapping.getFullDeobfuscatedName()))
                    .collect(Collectors.toList()));
            fieldLines.addAll(this.getFieldLinesFromInnerClasses(classMapping));

            methodLines.addAll(classMapping.getMethodMappings().values().stream()
                    .map(methodMapping -> String.format("MD: %s %s %s %s",
                            methodMapping.getFullObfuscatedName(), methodMapping.getObfuscatedDescriptor(),
                            methodMapping.getFullDeobfuscatedName(), methodMapping.getDeobfuscatedDescriptor()))
                    .collect(Collectors.toList()));
            methodLines.addAll(this.getMethodLinesFromInnerClasses(classMapping));
        }

        classLines.forEach(this.writer::println);
        fieldLines.forEach(this.writer::println);
        methodLines.forEach(this.writer::println);
    }

    private List<String> getFieldLinesFromInnerClasses(final ClassMapping classMapping) {
        final List<String> fieldLines = new ArrayList<>();

        for (final InnerClassMapping innerClassMapping : classMapping.getInnerClassMappings().values()) {
            fieldLines.addAll(innerClassMapping.getFieldMappings().values().stream()
                    .map(fieldMapping -> String.format("FD: %s %s",
                            fieldMapping.getFullObfuscatedName(), fieldMapping.getFullDeobfuscatedName()))
                    .collect(Collectors.toList()));
            fieldLines.addAll(this.getFieldLinesFromInnerClasses(innerClassMapping));
        }

        return fieldLines;
    }

    private List<String> getMethodLinesFromInnerClasses(final ClassMapping classMapping) {
        final List<String> methodLines = new ArrayList<>();

        for (final InnerClassMapping innerClassMapping : classMapping.getInnerClassMappings().values()) {
            methodLines.addAll(innerClassMapping.getMethodMappings().values().stream()
                    .map(methodMapping -> String.format("MD: %s %s %s %s",
                            methodMapping.getFullObfuscatedName(), methodMapping.getObfuscatedDescriptor(),
                            methodMapping.getFullDeobfuscatedName(), methodMapping.getDeobfuscatedDescriptor()))
                    .collect(Collectors.toList()));
            methodLines.addAll(this.getMethodLinesFromInnerClasses(innerClassMapping));
        }

        return methodLines;
    }

    private List<String> getClassLinesFromInnerClasses(final ClassMapping classMapping) {
        final List<String> classLines = new ArrayList<>();

        for (final InnerClassMapping innerClassMapping : classMapping.getInnerClassMappings().values()) {
            if (!innerClassMapping.getFullObfuscatedName().equals(innerClassMapping.getFullDeobfuscatedName())) {
                classLines.add(String.format("CL: %s %s",
                        innerClassMapping.getFullObfuscatedName(), innerClassMapping.getFullDeobfuscatedName()));
            }
            classLines.addAll(this.getClassLinesFromInnerClasses(innerClassMapping));
        }

        return classLines;
    }

}

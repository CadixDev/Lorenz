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

import me.jamiemansfield.lorenz.Mappings;
import me.jamiemansfield.lorenz.model.InnerClassMapping;
import me.jamiemansfield.lorenz.model.TopLevelClassMapping;
import me.jamiemansfield.lorenz.model.ClassMapping;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The mappings writer for SRG mappings.
 */
public class SrgWriter extends MappingsWriter {

    public SrgWriter(PrintWriter writer) {
        super(writer);
    }

    @Override
    public void writeMappings(Mappings mappings) {
        List<String> classLines = new ArrayList<>();
        List<String> fieldLines = new ArrayList<>();
        List<String> methodLines = new ArrayList<>();

        for (TopLevelClassMapping classMapping : mappings.getClassMappings().values()) {
            classLines.add(String.format("CL: %s %s",
                    classMapping.getFullObfuscatedName(), classMapping.getFullDeobfuscatedName()));
            classLines.addAll(this.getClassLinesFromInnerClasses(classMapping));

            fieldLines.addAll(classMapping.getFieldMappings().values().stream()
                    .map(fieldMapping -> String.format("FD: %s %s",
                            fieldMapping.getFullObfuscatedName(), fieldMapping.getFullDeobfuscatedName()))
                    .collect(Collectors.toList()));
            fieldLines.addAll(this.getFieldLinesFromInnerClasses(classMapping));

            methodLines.addAll(classMapping.getMethodMappings().values().stream()
                    .map(methodMapping -> String.format("MD: %s %s %s %s",
                            methodMapping.getFullObfuscatedName(), methodMapping.getObfuscatedSignature(),
                            methodMapping.getFullDeobfuscatedName(), methodMapping.getDeobfuscatedSignature()))
                    .collect(Collectors.toList()));
            methodLines.addAll(this.getMethodLinesFromInnerClasses(classMapping));
        }

        classLines.stream().forEach(this.getWriter()::println);
        fieldLines.stream().forEach(this.getWriter()::println);
        methodLines.stream().forEach(this.getWriter()::println);
    }

    private List<String> getFieldLinesFromInnerClasses(ClassMapping classMapping) {
        List<String> fieldLines = new ArrayList<>();

        for (InnerClassMapping innerClassMapping : classMapping.getInnerClassMappings().values()) {
            fieldLines.addAll(innerClassMapping.getFieldMappings().values().stream()
                    .map(fieldMapping -> String.format("FD: %s %s",
                            fieldMapping.getFullObfuscatedName(), fieldMapping.getFullDeobfuscatedName()))
                    .collect(Collectors.toList()));
            fieldLines.addAll(this.getFieldLinesFromInnerClasses(innerClassMapping));
        }

        return fieldLines;
    }

    private List<String> getMethodLinesFromInnerClasses(ClassMapping classMapping) {
        List<String> methodLines = new ArrayList<>();

        for (InnerClassMapping innerClassMapping : classMapping.getInnerClassMappings().values()) {
            methodLines.addAll(innerClassMapping.getMethodMappings().values().stream()
                    .map(methodMapping -> String.format("MD: %s %s %s %s",
                            methodMapping.getFullObfuscatedName(), methodMapping.getObfuscatedSignature(),
                            methodMapping.getFullDeobfuscatedName(), methodMapping.getDeobfuscatedSignature()))
                    .collect(Collectors.toList()));
            methodLines.addAll(this.getMethodLinesFromInnerClasses(innerClassMapping));
        }

        return methodLines;
    }

    private List<String> getClassLinesFromInnerClasses(ClassMapping classMapping) {
        List<String> classLines = new ArrayList<>();

        for (InnerClassMapping innerClassMapping : classMapping.getInnerClassMappings().values()) {
            if (!innerClassMapping.getFullObfuscatedName().equals(innerClassMapping.getFullDeobfuscatedName())) {
                classLines.add(String.format("CL: %s %s",
                        innerClassMapping.getFullObfuscatedName(), innerClassMapping.getFullDeobfuscatedName()));
            }
            classLines.addAll(this.getClassLinesFromInnerClasses(innerClassMapping));
        }

        return classLines;
    }

}

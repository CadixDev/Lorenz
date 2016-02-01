/*
 * This file is part of Lorenz, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2016, Lexteam <http://www.lexteam.xyz/>
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
package xyz.lexteam.lorenz.io.write;

import xyz.lexteam.lorenz.Mappings;
import xyz.lexteam.lorenz.model.ClassMapping;
import xyz.lexteam.lorenz.model.InnerClassMapping;
import xyz.lexteam.lorenz.model.TopLevelClassMapping;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

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
        }
    }

    private List<String> getClassLinesFromInnerClasses(ClassMapping classMapping) {
        List<String> classLines = new ArrayList<>();

        for (InnerClassMapping innerClassMapping : classMapping.getInnerClassMappings().values()) {
            classLines.add(String.format("CL: %s %s",
                    innerClassMapping.getFullObfuscatedName(), innerClassMapping.getFullDeobfuscatedName()));
            classLines.addAll(this.getClassLinesFromInnerClasses(innerClassMapping));
        }

        return classLines;
    }
}

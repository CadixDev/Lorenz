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

package me.jamiemansfield.lorenz.io.parse;

import me.jamiemansfield.lorenz.MappingsContainer;
import me.jamiemansfield.lorenz.model.InnerClassMapping;
import me.jamiemansfield.lorenz.model.MethodMapping;
import me.jamiemansfield.lorenz.model.TopLevelClassMapping;
import me.jamiemansfield.lorenz.model.ClassMapping;
import me.jamiemansfield.lorenz.model.FieldMapping;
import me.jamiemansfield.lorenz.util.Constants;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The mappings reader, for the RGS format.
 */
public class RgsParser extends MappingsParser {

    public RgsParser(final BufferedReader reader) {
        super(reader);
    }

    @Override
    public MappingsContainer parseMappings() {
        final MappingsContainer mappings = new MappingsContainer();

        final List<String> topLevelClassMappings = new ArrayList<>();
        final List<String> innerClassMappings = new ArrayList<>();
        final List<String> fieldMappings = new ArrayList<>();
        final List<String> methodMappings = new ArrayList<>();

        for (final String line : this.reader.lines().collect(Collectors.toList())) {
            if (line.startsWith(".class_map ")) {
                if (line.contains(Constants.INNER_CLASS_SEPARATOR)) {
                    innerClassMappings.add(line);
                } else {
                    topLevelClassMappings.add(line);
                }
            } else if (line.startsWith(".field_map ")) {
                fieldMappings.add(line);
            } else if (line.startsWith(".method_map ")) {
                methodMappings.add(line);
            }
        }

        innerClassMappings.sort(Comparator.comparingInt(this::getClassNestingLevel));

        for (final String topLevelClassMapping : topLevelClassMappings) {
            this.genClassMapping(mappings, topLevelClassMapping);
        }

        for (final String innerClassMapping : innerClassMappings) {
            this.genClassMapping(mappings, innerClassMapping);
        }

        for (String fieldMapping : fieldMappings) {
            fieldMapping = fieldMapping.replace(".field_map ", "");
            final String[] split = fieldMapping.split(" ");

            final String obfuscated = split[0].substring(split[0].lastIndexOf("/") + 1);
            final String deobfuscated = split[1];

            final String parentClassName = split[0].substring(0, (split[0].length() -
                    obfuscated.length()) - 1);

            ClassMapping classMapping = this.getClassMapping(mappings, parentClassName);
            if (classMapping == null) {
                classMapping = this.genClassMapping(mappings,
                        String.format(".class_map %s %s", parentClassName, parentClassName));
            }
            classMapping.addFieldMapping(new FieldMapping(classMapping, obfuscated, deobfuscated));
        }

        for (String methodMapping : methodMappings) {
            methodMapping = methodMapping.replace(".method_map ", "");
            final String[] split = methodMapping.split(" ");

            final String obfuscated = split[0].substring(split[0].lastIndexOf("/") + 1);
            final String obfuscatedType = split[1];
            final String deobfuscated = split[2];

            final String parentClassName = split[0].substring(0, (split[0].length() -
                    obfuscated.length()) - 1);

            ClassMapping classMapping = this.getClassMapping(mappings, parentClassName);
            if (classMapping == null) {
                classMapping = this.genClassMapping(mappings,
                        String.format(".class_map %s %s", parentClassName, parentClassName));
            }
            classMapping.addMethodMapping(new MethodMapping(classMapping, obfuscated, obfuscatedType, deobfuscated));
        }

        return mappings;
    }

    private ClassMapping genClassMapping(final MappingsContainer mappings, String line) {
        if (line.contains(Constants.INNER_CLASS_SEPARATOR)) {
            line = line.replace(".class_map ", "");
            final String[] split = line.split(" ");
            final String[] obfSplit = split[0].split("\\$");
            final String[] deobfSplit = split[1].split("\\$");

            final String obfInnerClassName = obfSplit[obfSplit.length - 1];
            final String obfParentClassName = split[0].substring(0, (split[0].length() - obfInnerClassName.length()) - 1);
            final String deobfInnerClassName = deobfSplit[deobfSplit.length - 1];

            String deobfParentClassName = "";
            if (deobfSplit.length == 1) {
                try  {
                    deobfParentClassName = deobfSplit[1];
                } catch (Exception e) {
                    for (String s : deobfSplit) {
                        System.out.println(s);
                    }
                }
            } else {
                deobfParentClassName = split[1].substring(0, (split[1].length() - deobfInnerClassName.length()) - 1);
            }

            ClassMapping parentClass = this.getClassMapping(mappings, obfParentClassName);
            if (parentClass == null) {
                parentClass = genClassMapping(mappings, String.format(".class_map %s %s", obfParentClassName,
                        deobfParentClassName));
            }
            final InnerClassMapping gen = new InnerClassMapping(parentClass, obfInnerClassName, deobfInnerClassName);
            parentClass.addInnerClassMapping(gen);
            return gen;
        } else {
            line = line.replace(".class_map ", "");
            final String[] split = line.split(" ");

            final TopLevelClassMapping classMapping = new TopLevelClassMapping(mappings,
                    split[0].replace(".", "/"), split[1].replace(".", "/"));
            mappings.addMapping(classMapping);
            return classMapping;
        }
    }

    private ClassMapping getClassMapping(final MappingsContainer mappings, final String fullName) {
        if (fullName.contains(Constants.INNER_CLASS_SEPARATOR)) {
            final String[] split = fullName.split("\\$");

            final String innerName = split[split.length - 1];
            final String parentClass = fullName.substring(0, fullName.length() - innerName.length());

            ClassMapping classMapping = this.getClassMapping(mappings, parentClass);
            if (classMapping == null) {
                classMapping = this.genClassMapping(mappings,
                        String.format(".class_map %s %s", parentClass, parentClass));
            }
            return classMapping.getInnerClassMappings().get(innerName);
        } else {
            return mappings.getClassMappings().get(fullName);
        }
    }

}

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
package xyz.lexteam.lorenz.io.parse;

import xyz.lexteam.lorenz.Mappings;
import xyz.lexteam.lorenz.model.ClassMapping;
import xyz.lexteam.lorenz.model.FieldMapping;
import xyz.lexteam.lorenz.model.InnerClassMapping;
import xyz.lexteam.lorenz.model.MethodMapping;
import xyz.lexteam.lorenz.model.TopLevelClassMapping;
import xyz.lexteam.lorenz.util.Constants;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mappings parser for RGS mappings.
 */
public class RgsParser extends MappingsParser {

    public RgsParser(BufferedReader reader) {
        super(reader);
    }

    @Override
    public Mappings parseMappings() {
        Mappings mappings = new Mappings();

        List<String> topLevelClassMappings = new ArrayList<>();
        List<String> innerClassMappings = new ArrayList<>();
        List<String> fieldMappings = new ArrayList<>();
        List<String> methodMappings = new ArrayList<>();

        for (String line : this.getReader().lines().collect(Collectors.toList())) {
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

        innerClassMappings.sort((o1, o2) -> getClassNestingLevel(o1) - getClassNestingLevel(o2));

        for (String topLevelClassMapping : topLevelClassMappings) {
            this.genClassMapping(mappings, topLevelClassMapping);
        }

        for (String innerClassMapping : innerClassMappings) {
            this.genClassMapping(mappings, innerClassMapping);
        }

        for (String fieldMapping : fieldMappings) {
            fieldMapping = fieldMapping.replace(".field_map ", "");
            String[] split = fieldMapping.split(" ");

            String obfuscated = split[0].substring(split[0].lastIndexOf("/") + 1);
            String deobfuscated = split[1];

            String parentClassName = split[0].substring(0, (split[0].length() -
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
            String[] split = methodMapping.split(" ");

            String obfuscated = split[0].substring(split[0].lastIndexOf("/") + 1);
            String obfuscatedType = split[1];
            String deobfuscated = split[2];

            String parentClassName = split[0].substring(0, (split[0].length() -
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

    private ClassMapping genClassMapping(Mappings mappings, String line) {
        if (line.contains(Constants.INNER_CLASS_SEPARATOR)) {
            line = line.replace(".class_map ", "");
            String[] split = line.split(" ");
            String[] obfSplit = split[0].split("\\$");
            String[] deobfSplit = split[1].split("\\$");

            String obfInnerClassName = obfSplit[obfSplit.length - 1];
            String obfParentClassName = split[0].substring(0, (split[0].length() - obfInnerClassName.length()) - 1);
            String deobfInnerClassName = deobfSplit[deobfSplit.length - 1];

            String deobfParentClassName;
            if (deobfSplit.length == 1) {
                deobfParentClassName = deobfSplit[0];
            } else {
                deobfParentClassName = split[1].substring(0, (split[1].length() - deobfInnerClassName.length()) - 1);
            }

            ClassMapping parentClass = this.getClassMapping(mappings, obfParentClassName);
            if (parentClass == null) {
                parentClass = genClassMapping(mappings, String.format(".class_map %s %s", obfParentClassName,
                        deobfParentClassName));
            }
            InnerClassMapping gen = new InnerClassMapping(parentClass, obfInnerClassName, deobfInnerClassName);
            parentClass.addInnerClassMapping(gen);
            return gen;
        } else {
            line = line.replace(".class_map ", "");
            String[] split = line.split(" ");

            TopLevelClassMapping classMapping = new TopLevelClassMapping(mappings,
                    split[0].replace(".", "/"), split[1].replace(".", "/"));
            mappings.addMapping(classMapping);
            return classMapping;
        }
    }

    private ClassMapping getClassMapping(Mappings mappings, String fullName) {
        if (fullName.contains(Constants.INNER_CLASS_SEPARATOR)) {
            String[] split = fullName.split("\\$");

            String innerName = split[split.length - 1];
            String parentClass = fullName.substring(0, fullName.length() - innerName.length());

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

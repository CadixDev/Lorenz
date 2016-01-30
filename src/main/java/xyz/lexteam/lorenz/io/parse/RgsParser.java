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
import xyz.lexteam.lorenz.model.MethodMapping;
import xyz.lexteam.lorenz.model.TopLevelClassMapping;
import xyz.lexteam.lorenz.util.Constants;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mappings parser for RGS mappings.
 */
public class RgsParser extends MappingsParser {

    public RgsParser(BufferedReader reader) {
        super(reader);
    }

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
            topLevelClassMapping = topLevelClassMapping.replace(".class_map ", "");
            String[] split = topLevelClassMapping.split(" ");

            mappings.addMapping(new TopLevelClassMapping(mappings,
                    split[0].replace(".", "/"), split[1].replace(".", "/")));
        }

        // TODO: inner classes

        for (String fieldMapping : fieldMappings) {
            fieldMapping = fieldMapping.replace(".field_map ", "");
            String[] split = fieldMapping.split(" ");

            String obfuscated = split[0].substring(split[0].lastIndexOf("/"));
            String deobfuscated = split[1];

            ClassMapping classMapping = this.getClassMapping(mappings, split[0].substring(obfuscated.length()));
            classMapping.addFieldMapping(new FieldMapping(classMapping, obfuscated, deobfuscated));
        }

        for (String methodMapping : methodMappings) {
            methodMapping = methodMapping.replace(".method_map ", "");
            String[] split = methodMapping.split(" ");

            String obfuscated = split[0].substring(split[0].lastIndexOf("/"));
            String obfuscatedType = split[1];
            String deobfuscated = split[2];

            ClassMapping classMapping = this.getClassMapping(mappings, split[0].substring(obfuscated.length()));
            classMapping.addMethodMapping(new MethodMapping(classMapping, obfuscated, obfuscatedType, deobfuscated));
        }

        return mappings;
    }

    private ClassMapping getClassMapping(Mappings mappings, String fullName) {
        if (fullName.contains(Constants.INNER_CLASS_SEPARATOR)) {
            String[] split = fullName.split(" ");

            String innerName = split[split.length - 1];
            String parentClass = fullName.substring(0, fullName.length() - innerName.length());

            ClassMapping classMapping = this.getClassMapping(mappings, parentClass);
            return classMapping.getInnerClassMappings().get(innerName);
        } else {
            return mappings.getClassMappings().get(fullName);
        }
    }
}

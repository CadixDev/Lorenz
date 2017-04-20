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
import me.jamiemansfield.lorenz.model.ClassMapping;
import me.jamiemansfield.lorenz.model.FieldMapping;
import me.jamiemansfield.lorenz.model.InnerClassMapping;
import me.jamiemansfield.lorenz.model.MethodMapping;
import me.jamiemansfield.lorenz.model.TopLevelClassMapping;
import me.jamiemansfield.lorenz.util.Constants;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The mappings parser, for the SRG format.
 */
public class SrgParser extends MappingsParser {

    private static final String CLASS_MAPPING_KEY = "CL:";
    private static final String FIELD_MAPPING_KEY = "FD:";
    private static final String METHOD_MAPPING_KEY = "MD:";

    private static final int CLASS_MAPPING_ELEMENT_COUNT = 3;
    private static final int FIELD_MAPPING_ELEMENT_COUNT = 3;
    private static final int METHOD_MAPPING_ELEMENT_COUNT = 5;

    public SrgParser(final BufferedReader reader) {
        super(reader);
    }

    @Override
    public MappingsContainer parseMappings() {
        final MappingsContainer mappings = new MappingsContainer();

        final List<String> rawClassMappings = new ArrayList<>();
        final List<String> rawFieldMappings = new ArrayList<>();
        final List<String> rawMethodMappings = new ArrayList<>();

        for (final String line : reader.lines().collect(Collectors.toList())) {
            final String trim = line.trim();
            if (trim.charAt(0) == '#' || trim.isEmpty()) {
                continue;
            }

            if (line.length() < 4) {
                throw new RuntimeException("Found bogus line in mappings file - ignoring");
            }

            final int len = Constants.SPACE_PATTERN.split(line).length;

            final String key = line.substring(0, 3);
            if (key.equals(CLASS_MAPPING_KEY) && len == CLASS_MAPPING_ELEMENT_COUNT) {
                rawClassMappings.add(line);
            } else if (key.equals(FIELD_MAPPING_KEY) && len == FIELD_MAPPING_ELEMENT_COUNT) {
                rawFieldMappings.add(line);
            } else if (key.equals(METHOD_MAPPING_KEY) && len == METHOD_MAPPING_ELEMENT_COUNT) {
                rawMethodMappings.add(line);
            } else {
                throw new RuntimeException("Discovered unrecognized key \"" + key + "\" in mappings file - ignoring");
            }
        }

        // we need to sort the class mappings in order of ascending nesting level
        rawClassMappings.sort(Comparator.comparingInt(this::getClassNestingLevel));

        this.genClassMappings(mappings, rawClassMappings);
        this.genFieldMappings(mappings, rawFieldMappings);
        this.genMethodMappings(mappings, rawMethodMappings);

        return mappings;
    }

    private void genClassMappings(final MappingsContainer context, final List<String> classMappings) {
        classMappings.forEach(mapping -> this.genClassMapping(context, mapping));
    }

    private ClassMapping genClassMapping(final MappingsContainer context, final String line) {
        // Remove 'CL: ' from the line, and split it at every space
        // split[0] = obfuscated name
        // split[1] = deobfuscated name
        final String[] split = Constants.SPACE_PATTERN.split(line.substring(4));
        final String obfuscatedName = split[0];
        final String deobfuscatedName = split[1];

        // The mapping is for an inner class
        if (line.contains(Constants.INNER_CLASS_SEPARATOR)) {
            // Split the obfuscated and deobfuscated named at every INNER_CLASS_SEPARATOR
            // This will allow them to be processed as so that the parent class can be grabbed
            // It is possible the parent class will also be an inner class, as the parser will
            // ring them back through this method, it will handle it fine, no complexity needed
            final String[] obfSplit = Constants.INNER_CLASS_SEPARATOR_PATTERN.split(obfuscatedName);
            final String[] deobfSplit = Constants.INNER_CLASS_SEPARATOR_PATTERN.split(deobfuscatedName);

            // Get the obfuscated and deobfuscated name of this inner class
            final String obfInnerClassName = obfSplit[obfSplit.length - 1];
            final String deobfInnerClassName = deobfSplit[deobfSplit.length - 1];

            // Get the obfuscated and deobfuscated name of the parent class
            final String obfParentClassName = obfuscatedName.substring(0, (obfuscatedName.length() - obfInnerClassName.length()) - 1);
            final String deobfParentClassName = deobfuscatedName.substring(0, (deobfuscatedName.length() - deobfInnerClassName.length()) - 1);

            ClassMapping parentClass = this.getClassMapping(context, obfParentClassName);
            if (parentClass == null) {
                parentClass = this.genClassMapping(context, String.format("CL: %s %s", obfParentClassName,
                        deobfParentClassName));
            }

            final InnerClassMapping gen = new InnerClassMapping(parentClass, obfInnerClassName, deobfInnerClassName);
            parentClass.addInnerClassMapping(gen);
            return gen;
        }
        // The mapping is for a top level class
        else {
            final TopLevelClassMapping classMapping = new TopLevelClassMapping(context, obfuscatedName, deobfuscatedName);
            context.addMapping(classMapping);
            return classMapping;
        }
    }

    private ClassMapping getClassMapping(final MappingsContainer mappings, final String fullName) {
        // NOTE: this method is designed to return null, genClassMappings can be used when this can't

        // The mapping is for an inner class
        if (fullName.contains(Constants.INNER_CLASS_SEPARATOR)) {
            // Split the name at every INNER_CLASS_SEPARATOR
            // This will allow them to be processed as so that the parent class can be grabbed
            // It is possible the parent class will also be an inner class, as the parser will
            // ring them back through this method, it will handle it fine, no complexity needed
            final String[] split = Constants.INNER_CLASS_SEPARATOR_PATTERN.split(fullName);

            // Get the name of this inner class
            final String innerName = split[split.length - 1];

            // Get the name of the parent class
            final String parentClass = fullName.substring(0, fullName.length() - innerName.length());

            // Gets the ClassMapping for the parent of this inner class
            ClassMapping parentClassMapping = this.getClassMapping(mappings, parentClass);
            if (parentClassMapping == null) {
                parentClassMapping = this.genClassMapping(mappings,
                        String.format("CL: %s %s", parentClass, parentClass));
            }

            return parentClassMapping.getInnerClassMappings().get(innerName);
        }
        // The mapping is for a top level class
        else {
            return mappings.getClassMappings().get(fullName);
        }
    }

    private void genFieldMappings(final MappingsContainer context, final List<String> fieldMappings) {
        for (final String line : fieldMappings) {
            // Remove 'FD: ' from the line, and split it at every space
            // split[0] = obfuscated name
            // split[1] = deobfuscated name
            final String[] split = Constants.SPACE_PATTERN.split(line.substring(4));
            final String obfuscatedFullName = split[0];
            final String deobfuscatedFullName = split[1];

            // Extract the obfuscated and deobfuscated field name from the full names
            final String obfuscatedFieldName = obfuscatedFullName.substring(obfuscatedFullName.lastIndexOf("/") + 1);
            final String deobfuscatedFieldName = deobfuscatedFullName.substring(deobfuscatedFullName.lastIndexOf("/") + 1);

            // Get the parent class name
            final String obfuscatedParentClassName = obfuscatedFullName.substring(0, (obfuscatedFullName.length() -
                    obfuscatedFieldName.length()) - 1);
            final String deobfuscatedParentClassName = deobfuscatedFullName.substring(0, (deobfuscatedFullName.length() -
                    deobfuscatedFieldName.length()) - 1);

            // Get the parent class
            ClassMapping classMapping = this.getClassMapping(context, obfuscatedParentClassName);
            if (classMapping == null) {
                classMapping = this.genClassMapping(context,
                        String.format("CL: %s %s", obfuscatedParentClassName, deobfuscatedParentClassName));
            }

            // Make the field mapping
            classMapping.addFieldMapping(new FieldMapping(classMapping, obfuscatedFieldName, deobfuscatedFieldName));
        }
    }

    private void genMethodMappings(final MappingsContainer context, final List<String> methodMappings) {
        for (final String line : methodMappings) {
            // Remove 'MD: ' from the line, and split it at every space
            // split[0] = obfuscated name
            // split[1] = obfuscated signature
            // split[2] = deobfuscated name
            // split[3] = deobfuscated signature
            final String[] split = Constants.SPACE_PATTERN.split(line.substring(4));
            final String obfuscatedFullName = split[0];
            final String obfuscatedSignature = split[1];
            final String deobfuscatedFullName = split[2];
            final String deobfuscatedSignature = split[3];

            // Extract the obfuscated and deobfuscated method name from the full names
            final String obfuscatedMethodName = obfuscatedFullName.substring(obfuscatedFullName.lastIndexOf("/") + 1);
            final String deobfuscatedMethodName = deobfuscatedFullName.substring(deobfuscatedFullName.lastIndexOf("/") + 1);

            // Get the parent class name
            final String obfuscatedParentClassName = obfuscatedFullName.substring(0, (obfuscatedFullName.length() -
                    obfuscatedMethodName.length()) - 1);
            final String deobfuscatedParentClassName = deobfuscatedFullName.substring(0, (deobfuscatedFullName.length() -
                    deobfuscatedMethodName.length()) - 1);

            // Get the parent class
            ClassMapping classMapping = this.getClassMapping(context, obfuscatedParentClassName);
            if (classMapping == null) {
                classMapping = this.genClassMapping(context,
                        String.format("CL: %s %s", obfuscatedParentClassName, deobfuscatedParentClassName));
            }

            // Make the method mapping
            classMapping.addMethodMapping(new MethodMapping(classMapping, obfuscatedMethodName, obfuscatedSignature, deobfuscatedMethodName));
        }
    }

}

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

package org.cadixdev.lorenz.io.enigma;

import org.cadixdev.lorenz.MappingSet;
import org.cadixdev.lorenz.io.MappingsWriter;
import org.cadixdev.lorenz.io.TextMappingsWriter;
import org.cadixdev.lorenz.model.ClassMapping;
import org.cadixdev.lorenz.model.FieldMapping;
import org.cadixdev.lorenz.model.InnerClassMapping;
import org.cadixdev.lorenz.model.Mapping;
import org.cadixdev.lorenz.model.MethodMapping;
import org.cadixdev.lorenz.model.MethodParameterMapping;
import org.cadixdev.bombe.type.ArrayType;
import org.cadixdev.bombe.type.FieldType;
import org.cadixdev.bombe.type.MethodDescriptor;
import org.cadixdev.bombe.type.ObjectType;
import org.cadixdev.bombe.type.Type;

import java.io.IOException;
import java.io.Writer;
import java.util.Optional;

/**
 * An implementation of {@link MappingsWriter} for the Enigma format.
 *
 * @author Jamie Mansfield
 * @since 0.4.0
 */
public class EnigmaWriter extends TextMappingsWriter {

    private static String handleNonePrefix(final String descriptor) {
        if (!descriptor.contains("/")) {
            return "none/" + descriptor;
        }
        return descriptor;
    }

    private static FieldType handleNonePrefix(final FieldType type) {
        if (type instanceof ArrayType) {
            final ArrayType arr = (ArrayType) type;
            return new ArrayType(arr.getDimCount(), handleNonePrefix(arr.getComponent()));
        }
        if (type instanceof ObjectType) {
            final ObjectType obj = (ObjectType) type;
            return new ObjectType(handleNonePrefix(obj.getClassName()));
        }
        return type;
    }

    private static Type handleNonePrefix(final Type type) {
        if (type instanceof FieldType) {
            return handleNonePrefix((FieldType) type);
        }
        return type;
    }

    private static String handleNonePrefix(final MethodDescriptor descriptor) {
        final StringBuilder typeBuilder = new StringBuilder();
        typeBuilder.append("(");
        descriptor.getParamTypes().forEach(type -> typeBuilder.append(handleNonePrefix(type)));
        typeBuilder.append(")");
        typeBuilder.append(handleNonePrefix(descriptor.getReturnType()));
        return typeBuilder.toString();
    }

    public EnigmaWriter(final Writer writer) {
        super(writer);
    }

    @Override
    public void write(final MappingSet mappings) throws IOException {
        mappings.getTopLevelClassMappings().stream()
                .filter(ClassMapping::hasMappings)
                .sorted(ALPHABETISE_MAPPINGS)
                .forEach(klass -> this.writeClassMapping(klass, 0));
    }

    private void writeClassMapping(final ClassMapping<?, ?> klass, final int indent) {
        final String obfName = handleNonePrefix(klass.getFullObfuscatedName());
        if (klass.hasDeobfuscatedName()) {
            final String deobfName = klass instanceof InnerClassMapping ?
                    klass.getDeobfuscatedName() :
                    handleNonePrefix(klass.getDeobfuscatedName());
            this.printIndentedLine(indent, "CLASS " + obfName + " " + deobfName);
        }
        else {
            this.printIndentedLine(indent, "CLASS " + obfName);
        }

        // Write inner class mappings
        klass.getInnerClassMappings().stream()
                .filter(ClassMapping::hasMappings)
                .sorted(ALPHABETISE_MAPPINGS)
                .forEach(inner -> this.writeClassMapping(inner, indent + 1));

        // Write field mappings
        klass.getFieldMappings().stream()
                .filter(Mapping::hasDeobfuscatedName)
                .sorted(ALPHABETISE_MAPPINGS)
                .forEach(field -> this.writeFieldMapping(field, indent + 1));

        // Write method mappings
        klass.getMethodMappings().stream()
                .filter(MethodMapping::hasMappings)
                .sorted(ALPHABETISE_MAPPINGS)
                .forEach(method -> this.writeMethodMapping(method, indent + 1));
    }

    private void writeFieldMapping(final FieldMapping field, final int indent) {
        // The SHOULD_WRITE test should have already have been performed, so we're good
        final Optional<FieldType> fieldType = field.getType();
        fieldType.ifPresent(type -> {
            this.printIndentedLine(indent, String.format("FIELD %s %s %s",
                    field.getObfuscatedName(),
                    field.getDeobfuscatedName(),
                    field.getMappings().deobfuscate(type)
            ));
        });
        // TODO: throw an exception if the type is unknown / WriterResult container
    }

    private void writeMethodMapping(final MethodMapping method, final int indent) {
        // The SHOULD_WRITE test should have already have been performed, so we're good
        if (method.hasDeobfuscatedName()) {
            this.printIndentedLine(indent, String.format("METHOD %s %s %s",
                    method.getObfuscatedName(),
                    method.getDeobfuscatedName(),
                    handleNonePrefix(method.getDescriptor())
            ));
        }
        else {
            this.printIndentedLine(indent, String.format("METHOD %s %s",
                    method.getObfuscatedName(),
                    handleNonePrefix(method.getDescriptor())
            ));
        }
        for (final MethodParameterMapping param : method.getParameterMappings()) {
            this.printIndentedLine(indent + 1, String.format("ARG %s %s",
                    param.getIndex(),
                    param.getDeobfuscatedName()
            ));
        }
    }

    private void printIndentedLine(final int indent, final String line) {
        for (int i = 0; i < indent; i++) {
            this.writer.println('\t');
        }
        this.writer.println(line);
    }

}

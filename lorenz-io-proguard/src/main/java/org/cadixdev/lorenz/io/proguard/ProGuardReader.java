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

package org.cadixdev.lorenz.io.proguard;

import org.cadixdev.bombe.type.FieldType;
import org.cadixdev.bombe.type.MethodDescriptor;
import org.cadixdev.bombe.type.Type;
import org.cadixdev.lorenz.MappingSet;
import org.cadixdev.lorenz.io.TextMappingsReader;
import org.cadixdev.lorenz.model.ClassMapping;

import java.io.Reader;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ProGuardReader extends TextMappingsReader {

    public ProGuardReader(final Reader reader) {
        super(reader, Processor::new);
    }

    @Override
    public MappingSet read(final MappingSet mappings) {
        return super.read(mappings);
    }

    public static class Processor extends TextMappingsReader.Processor {

        private ClassMapping currentClass;

        public Processor(final MappingSet mappings) {
            super(mappings);
        }

        @Override
        public void accept(final String raw) {
            // Ignore comments
            if (raw.startsWith("#")) return;

            final String[] params = raw.trim().split(" ");

            if (params.length == 3 && params[1].equals("->")) {
                final String obf = params[0].replace('.', '/');
                // remove the trailing :
                final String deobf = params[2].substring(0, params[2].length() - 1).replace('.', '/');

                this.currentClass = this.mappings.getOrCreateClassMapping(obf)
                        .setDeobfuscatedName(deobf);
            }

            if (params.length == 4 && params[2].equals("->")) {
                final String returnTypeRaw = params[0];
                final String obf = params[1];
                final String deobf = params[3];

                // method
                if (obf.contains("(")) {
                    // remove any line numbers
                    final int index = returnTypeRaw.lastIndexOf(':');
                    final String returnCleanRaw = index != -1 ?
                            returnTypeRaw.substring(index + 1) :
                            returnTypeRaw;
                    final Type returnClean = new PGTypeReader(returnCleanRaw).readType();

                    final String obfName = obf.substring(0, obf.indexOf('('));
                    final String[] obfParams = obf.substring(obf.indexOf('(') + 1, obf.length() - 1).split(",");
                    final List<FieldType> paramTypes = Arrays.stream(obfParams)
                            .filter(line -> !line.isEmpty())
                            .map(PGTypeReader::new)
                            .map(PGTypeReader::readFieldType)
                            .collect(Collectors.toList());

                    this.currentClass.getOrCreateMethodMapping(obfName, new MethodDescriptor(paramTypes, returnClean))
                            .setDeobfuscatedName(deobf);
                }
                // field
                else {
                    this.currentClass.getOrCreateFieldMapping(obf)
                            .setDeobfuscatedName(deobf);
                }
            }
        }

    }

}

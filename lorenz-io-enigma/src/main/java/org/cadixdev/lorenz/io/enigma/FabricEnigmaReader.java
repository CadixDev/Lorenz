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

import org.cadixdev.bombe.type.FieldType;
import org.cadixdev.bombe.type.MethodDescriptor;
import org.cadixdev.bombe.type.Type;
import org.cadixdev.lorenz.MappingSet;
import org.cadixdev.lorenz.io.MappingsReader;
import org.cadixdev.lorenz.io.TextMappingsReader;
import org.cadixdev.lorenz.model.ClassMapping;
import org.cadixdev.lorenz.model.Mapping;

import java.io.Reader;

/**
 * A {@link MappingsReader mappings reader} for Fabric's fork of the Enigma
 * format.
 *
 * @author Jamie Mansfield
 * @since 0.6.0
 */
public class FabricEnigmaReader extends TextMappingsReader {

    public FabricEnigmaReader(final Reader reader) {
        super(reader, Processor::new);
    }

    public static class Processor extends EnigmaReader.Processor {

        public Processor(final MappingSet mappings) {
            super(mappings);
        }

        public Processor() {
            super();
        }

        @Override
        protected ClassMapping<?, ?> readClassMapping(final String obfName) {
            // Fabric's fork of the Enigma format doesn't use full de-obfuscated
            // names when printing classes (practically this affects inner classes).
            final Mapping<?, ?> mapping = this.stack.peek();
            if (mapping == null) {
                return this.mappings.getOrCreateTopLevelClassMapping(obfName);
            }
            if (!(mapping instanceof ClassMapping)) {
                throw new UnsupportedOperationException("Not a class on the stack!");
            }

            return ((ClassMapping<?, ?>) mapping).getOrCreateInnerClassMapping(obfName);
        }

        @Override
        protected String convertClassName(final String descriptor) {
            // Fabric's fork of the Enigma format doesn't add a 'none/' prefix
            // to un-packaged classes.
            return descriptor;
        }

        @Override
        protected Type convertType(final Type type) {
            // Fabric's fork of the Enigma format doesn't add a 'none/' prefix
            // to un-packaged classes.
            return type;
        }

        @Override
        protected FieldType convertFieldType(final FieldType type) {
            // Fabric's fork of the Enigma format doesn't add a 'none/' prefix
            // to un-packaged classes.
            return type;
        }

        @Override
        protected MethodDescriptor convertDescriptor(final MethodDescriptor descriptor) {
            // Fabric's fork of the Enigma format doesn't add a 'none/' prefix
            // to un-packaged classes.
            return descriptor;
        }

    }

}

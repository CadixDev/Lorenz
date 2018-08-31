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

package me.jamiemansfield.lorenz.io.kin;

import static me.jamiemansfield.lorenz.io.kin.KinConstants.MAGIC;
import static me.jamiemansfield.lorenz.io.kin.KinConstants.VERSION_ONE;

import me.jamiemansfield.bombe.type.FieldType;
import me.jamiemansfield.lorenz.MappingSet;
import me.jamiemansfield.lorenz.io.BinaryMappingsWriter;
import me.jamiemansfield.lorenz.io.MappingsWriter;
import me.jamiemansfield.lorenz.model.ClassMapping;
import me.jamiemansfield.lorenz.model.FieldMapping;
import me.jamiemansfield.lorenz.model.InnerClassMapping;
import me.jamiemansfield.lorenz.model.MethodMapping;
import me.jamiemansfield.lorenz.model.TopLevelClassMapping;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Optional;
import java.util.zip.GZIPOutputStream;

/**
 * An implementation of {@link MappingsWriter} for the Kin format.
 *
 * @author Jamie Mansfield
 * @since 0.4.0
 */
public class KinWriter extends BinaryMappingsWriter {

    public KinWriter(final OutputStream stream) throws IOException {
        super(new GZIPOutputStream(stream));
    }

    @Override
    public void write(final MappingSet mappings) throws IOException {
        this.stream.writeInt(MAGIC);
        this.stream.writeByte(VERSION_ONE);

        // fake package info
        this.stream.writeInt(0);

        // write classes
        this.stream.writeInt(mappings.getTopLevelClassMappings().size());
        for (final TopLevelClassMapping klass : mappings.getTopLevelClassMappings()) {
            this.writeClass(klass);
        }
    }

    private void writeClass(final ClassMapping<?> mapping) throws IOException {
        this.stream.writeUTF(mapping.getObfuscatedName());
        this.stream.writeUTF(mapping.getDeobfuscatedName());

        // write fields
        this.stream.writeInt(mapping.getFieldMappings().size());
        for (final FieldMapping field : mapping.getFieldMappings()) {
            this.stream.writeUTF(field.getObfuscatedName());
            final Optional<FieldType> type = field.getType();
            this.stream.writeBoolean(type.isPresent());
            if (type.isPresent()) this.stream.writeUTF(mapping.getMappings().deobfuscate(type.get()));
            this.stream.writeUTF(field.getDeobfuscatedName());
        }

        // write methods
        this.stream.writeInt(mapping.getMethodMappings().size());
        for (final MethodMapping method : mapping.getMethodMappings()) {
            this.stream.writeUTF(method.getObfuscatedName());
            this.stream.writeUTF(method.getObfuscatedDescriptor());
            this.stream.writeUTF(method.getDeobfuscatedName());
        }

        // write inner classes
        this.stream.writeInt(mapping.getInnerClassMappings().size());
        for (final InnerClassMapping inner : mapping.getInnerClassMappings()) {
            this.writeClass(inner);
        }
    }

}

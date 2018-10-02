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

package org.cadixdev.lorenz.io.kin;

import static org.cadixdev.lorenz.io.kin.KinConstants.MAGIC;
import static org.cadixdev.lorenz.io.kin.KinConstants.VERSION_ONE;
import static org.cadixdev.lorenz.io.kin.KinConstants.toHexString;

import org.cadixdev.lorenz.MappingSet;
import org.cadixdev.lorenz.io.BinaryMappingsReader;
import org.cadixdev.lorenz.io.MappingsReader;
import org.cadixdev.lorenz.model.ClassMapping;
import org.cadixdev.lorenz.model.FieldMapping;
import org.cadixdev.bombe.type.FieldType;
import org.cadixdev.bombe.type.signature.FieldSignature;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

/**
 * An implementation of {@link MappingsReader} for the Kin format.
 *
 * @author Jamie Mansfield
 * @since 0.4.0
 */
public class KinReader extends BinaryMappingsReader {

    public KinReader(final InputStream stream) throws IOException {
        super(new GZIPInputStream(stream));
    }

    /**
     * {@inheritDoc}
     * @throws IllegalStateException Should a state issue occur
     */
    @Override
    public MappingSet read(final MappingSet mappings) throws IOException {
        final int magic = this.stream.readInt();
        if (magic != MAGIC) throw new IllegalStateException("Invalid magic marker! '" + toHexString(magic) + "'");

        final byte version = this.stream.readByte();
        if (version != VERSION_ONE) throw new IllegalStateException("Invalid kin version! '" + version + "'");

        final int packageCount = this.stream.readInt();
        for (int i = 0; i < packageCount; i++) {
            final String obf = this.stream.readUTF();
            final String deobf = this.stream.readUTF();
            // TODO: support package mappings
        }

        final int classCount = this.stream.readInt();
        for (int i = 0; i < classCount; i++) {
            this.readClass(mappings.getOrCreateTopLevelClassMapping(this.stream.readUTF()));
        }

        return mappings;
    }

    private void readClass(final ClassMapping mapping) throws IOException {
        mapping.setDeobfuscatedName(this.stream.readUTF());

        final int classCount = this.stream.readInt();
        for (int i = 0; i < classCount; i++) {
            this.readClass(mapping.getOrCreateInnerClassMapping(this.stream.readUTF()));
        }

        final int fieldCount = this.stream.readInt();
        for (int i = 0; i < fieldCount; i++) {
            final FieldMapping field;
            final String obf = this.stream.readUTF();
            // has type info
            // todo: clean this up (introduce more convenience methods to ClassMapping)
            if (this.stream.readBoolean()) {
                field = mapping.getOrCreateFieldMapping(new FieldSignature(obf, FieldType.of(this.stream.readUTF())));
            }
            else {
                field = mapping.getOrCreateFieldMapping(obf);
            }
            field.setDeobfuscatedName(this.stream.readUTF());
        }

        final int methodCount = this.stream.readInt();
        for (int i = 0; i < methodCount; i++) {
            mapping.getOrCreateMethodMapping(this.stream.readUTF(), this.stream.readUTF())
                    .setDeobfuscatedName(this.stream.readUTF());
        }
    }

}

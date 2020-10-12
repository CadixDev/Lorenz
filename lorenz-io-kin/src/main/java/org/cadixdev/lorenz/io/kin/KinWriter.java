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

import org.cadixdev.bombe.type.FieldType;
import org.cadixdev.lorenz.MappingSet;
import org.cadixdev.lorenz.io.BinaryMappingsWriter;
import org.cadixdev.lorenz.io.MappingsWriter;
import org.cadixdev.lorenz.model.ClassMapping;
import org.cadixdev.lorenz.model.FieldMapping;
import org.cadixdev.lorenz.model.InnerClassMapping;
import org.cadixdev.lorenz.model.Mapping;
import org.cadixdev.lorenz.model.MethodMapping;
import org.cadixdev.lorenz.model.TopLevelClassMapping;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
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
        this.stream.writeInt(KinConstants.MAGIC);
        this.stream.writeByte(KinConstants.VERSION_ONE);

        // fake package info
        this.stream.writeInt(0);

        // write classes
        final List<TopLevelClassMapping> classes = getSortedAndFilteredList(
                mappings.getTopLevelClassMappings(),
                ALPHABETISE_MAPPINGS::compare,
                ClassMapping::hasMappings
        );
        this.stream.writeInt(classes.size());
        for (final TopLevelClassMapping klass : classes) {
            this.writeClass(klass);
        }
    }

    private void writeClass(final ClassMapping<?, ?> mapping) throws IOException {
        this.stream.writeUTF(mapping.getObfuscatedName());
        this.stream.writeUTF(mapping.getDeobfuscatedName());

        // write fields
        final List<FieldMapping> fields = getSortedAndFilteredList(
                mapping.getFieldMappings(),
                ALPHABETISE_FIELDS,
                Mapping::hasDeobfuscatedName
        );
        this.stream.writeInt(fields.size());
        for (final FieldMapping field : fields) {
            this.stream.writeUTF(field.getObfuscatedName());
            final Optional<FieldType> type = field.getType();
            this.stream.writeBoolean(type.isPresent());
            if (type.isPresent()) this.stream.writeUTF(mapping.getMappings().deobfuscate(type.get()).toString());
            this.stream.writeUTF(field.getDeobfuscatedName());
        }

        // write methods
        final List<MethodMapping> methods = getSortedAndFilteredList(
                mapping.getMethodMappings(),
                ALPHABETISE_METHODS,
                MethodMapping::hasMappings
        );
        this.stream.writeInt(methods.size());
        for (final MethodMapping method : methods) {
            this.stream.writeUTF(method.getObfuscatedName());
            this.stream.writeUTF(method.getObfuscatedDescriptor());
            this.stream.writeUTF(method.getDeobfuscatedName());
        }

        // write inner classes
        final List<InnerClassMapping> innerClases = getSortedAndFilteredList(
                mapping.getInnerClassMappings(),
                ALPHABETISE_MAPPINGS::compare,
                ClassMapping::hasMappings
        );
        this.stream.writeInt(innerClases.size());
        for (final InnerClassMapping inner : innerClases) {
            this.writeClass(inner);
        }
    }

    private static <T> List<T> getSortedAndFilteredList(
            final Collection<T> original,
            final Comparator<T> comparator,
            final Predicate<T> filter
    ) {
        final List<T> mappings = new ArrayList<>();
        for (final T mapping : original) {
            if (!filter.test(mapping)) continue;
            mappings.add(mapping);
        }
        mappings.sort(comparator);
        return mappings;
    }

}

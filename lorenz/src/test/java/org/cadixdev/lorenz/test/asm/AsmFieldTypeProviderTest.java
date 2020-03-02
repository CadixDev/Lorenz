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

package org.cadixdev.lorenz.test.asm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.cadixdev.lorenz.MappingSet;
import org.cadixdev.lorenz.asm.AsmFieldTypeProvider;
import org.cadixdev.lorenz.model.FieldMapping;
import org.cadixdev.bombe.type.FieldType;
import org.cadixdev.bombe.type.ObjectType;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

import java.util.Objects;
import java.util.Optional;

public final class AsmFieldTypeProviderTest {

    @Test
    public void fetchFieldType() {
        final MappingSet mappings = new MappingSet();
        final FieldMapping field = mappings.getOrCreateTopLevelClassMapping("ght")
                .getOrCreateFieldMapping("op");

        final ClassWriter writer = new ClassWriter(0);
        writer.visit(Opcodes.V1_5, Opcodes.ACC_PUBLIC, "ght", null, "java/lang/Object", null);
        writer.visitField(Opcodes.ACC_PUBLIC, "op", "Ljava/util/logging/Logger;", null, null);

        mappings.addFieldTypeProvider(new AsmFieldTypeProvider(klass -> {
            if (Objects.equals("ght", klass)) return writer.toByteArray();
            return null;
        }));

        final Optional<FieldType> type = field.getType();
        assertTrue(type.isPresent());
        assertTrue(type.get() instanceof ObjectType);
        assertEquals("java/util/logging/Logger", ((ObjectType) type.get()).getClassName());
    }

}

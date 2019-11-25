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

package org.cadixdev.lorenz.test.model.jar;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.cadixdev.lorenz.MappingSet;
import org.cadixdev.lorenz.model.jar.CompositeFieldTypeProvider;
import org.cadixdev.bombe.type.FieldType;
import org.cadixdev.bombe.type.ObjectType;
import org.junit.jupiter.api.Test;

import java.util.Optional;

public final class CompositeFieldTypeProviderTest {

    private static final MappingSet MAPPINGS = new MappingSet();
    private static final CompositeFieldTypeProvider PROVIDER = new CompositeFieldTypeProvider()
            .add(field -> {
                if ("demo".equals(field.getObfuscatedName())) {
                    return Optional.of(new ObjectType("java/util/logging/Logger"));
                }
                return Optional.empty();
            })
            .add(field -> {
                if ("uiop".equals(field.getObfuscatedName())) {
                    return Optional.of(new ObjectType("java/lang/String"));
                }
                return Optional.empty();
            });

    static {
        MAPPINGS.addFieldTypeProvider(PROVIDER);
    }

    @Test
    public void demo() {
        final Optional<FieldType> type = MAPPINGS.getOrCreateTopLevelClassMapping("Demo")
                .getOrCreateFieldMapping("demo")
                .getType();
        assertTrue(type.isPresent());
        assertTrue(type.get() instanceof ObjectType);
        assertEquals("java/util/logging/Logger", ((ObjectType) type.get()).getClassName());
    }

    @Test
    public void uiop() {
        final Optional<FieldType> type = MAPPINGS.getOrCreateTopLevelClassMapping("Demo")
                .getOrCreateFieldMapping("uiop")
                .getType();
        assertTrue(type.isPresent());
        assertTrue(type.get() instanceof ObjectType);
        assertEquals("java/lang/String", ((ObjectType) type.get()).getClassName());
    }

}

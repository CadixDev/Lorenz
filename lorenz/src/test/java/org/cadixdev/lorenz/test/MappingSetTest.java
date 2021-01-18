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

package org.cadixdev.lorenz.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.cadixdev.bombe.type.ObjectType;
import org.cadixdev.lorenz.MappingSet;
import org.cadixdev.lorenz.model.TopLevelClassMapping;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public final class MappingSetTest {

    @Test
    @DisplayName("de-obfuscate object type")
    public void deobfObjectType() {
        final MappingSet mappings = mappings();

        final ObjectType obfIn = new ObjectType("a");
        final ObjectType deobfOut = new ObjectType("Demo");

        assertEquals(deobfOut, mappings.deobfuscate(obfIn));
    }

    @Test
    @DisplayName("de-obfuscate object type of mapped inner class")
    public void deobfObjectTypeOfMappedInnerClass() {
        final MappingSet mappings = mappings();

        final ObjectType obfIn = new ObjectType("a$a");
        final ObjectType deobfOut = new ObjectType("Demo$Inner");

        assertEquals(deobfOut, mappings.deobfuscate(obfIn));
    }

    @Test
    @DisplayName("de-obfuscate object type of unmapped inner class")
    public void deobfObjectTypeOfUnmappedInnerClass() {
        final MappingSet mappings = mappings();

        final ObjectType obfIn = new ObjectType("a$b");
        final ObjectType deobfOut = new ObjectType("Demo$b");

        assertEquals(deobfOut, mappings.deobfuscate(obfIn));
    }

    private static MappingSet mappings() {
        final MappingSet mappings = MappingSet.create();

        final TopLevelClassMapping a = mappings.getOrCreateTopLevelClassMapping("a")
                .setDeobfuscatedName("Demo");
        a.getOrCreateInnerClassMapping("a")
                .setDeobfuscatedName("Inner");

        return mappings;
    }

}

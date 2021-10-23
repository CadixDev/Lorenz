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

import org.cadixdev.bombe.type.ArrayType;
import org.cadixdev.bombe.type.MethodDescriptor;
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

    @Test
    @DisplayName("de-obfuscate array of object type")
    public void deobfArrayOfObjectType() {
        final MappingSet mappings = mappings();

        final ArrayType in = new ArrayType(2,
                new ObjectType("a"));
        final ArrayType out = new ArrayType(2,
                new ObjectType("Demo"));

        assertEquals(out, mappings.deobfuscate(in));
    }

    @Test
    @DisplayName("de-obfuscate array of object type of mapped inner class")
    public void deobfArrayOfObjectTypeOfMappedInnerClass() {
        final MappingSet mappings = mappings();

        final ArrayType in = new ArrayType(2,
                new ObjectType("a$a"));
        final ArrayType out = new ArrayType(2,
                new ObjectType("Demo$Inner"));

        assertEquals(out, mappings.deobfuscate(in));
    }

    @Test
    @DisplayName("de-obfuscate array of object type of unmapped inner class")
    public void deobfArrayOfObjectTypeOfUnmappedInnerClass() {
        final MappingSet mappings = mappings();

        final ArrayType in = new ArrayType(2,
                new ObjectType("a$b"));
        final ArrayType out = new ArrayType(2,
                new ObjectType("Demo$b"));

        assertEquals(out, mappings.deobfuscate(in));
    }

    @Test
    @DisplayName("de-obfuscate method descriptor single object param")
    public void deobfMethodDescriptorSingleObjectParam() {
        final MappingSet mappings = mappings();

        final MethodDescriptor obf = MethodDescriptor.of("(La;)V");
        final MethodDescriptor deobf = MethodDescriptor.of("(LDemo;)V");

        assertEquals(deobf, mappings.deobfuscate(obf));
    }

    @Test
    @DisplayName("de-obfuscate method descriptor multiple object param")
    public void deobfMethodDescriptorMultipleObjectParam() {
        final MappingSet mappings = mappings();

        final MethodDescriptor obf = MethodDescriptor.of("(La;La;)V");
        final MethodDescriptor deobf = MethodDescriptor.of("(LDemo;LDemo;)V");

        assertEquals(deobf, mappings.deobfuscate(obf));
    }

    @Test
    @DisplayName("de-obfuscate method descriptor object return")
    public void deobfMethodDescriptorObjectReturn() {
        final MappingSet mappings = mappings();

        final MethodDescriptor obf = MethodDescriptor.of("()La;");
        final MethodDescriptor deobf = MethodDescriptor.of("()LDemo;");

        assertEquals(deobf, mappings.deobfuscate(obf));
    }

    @Test
    @DisplayName("de-obfuscate method descriptor object return and params")
    public void deobfMethodDescriptorObjectReturnAndParams() {
        final MappingSet mappings = mappings();

        final MethodDescriptor obf = MethodDescriptor.of("(La;La;)La;");
        final MethodDescriptor deobf = MethodDescriptor.of("(LDemo;LDemo;)LDemo;");

        assertEquals(deobf, mappings.deobfuscate(obf));
    }

    private static MappingSet mappings() {
        final MappingSet mappings = MappingSet.create();

        final TopLevelClassMapping a = mappings.getOrCreateTopLevelClassMapping("a")
                .setDeobfuscatedName("Demo");
        a.getOrCreateInnerClassMapping("a")
                .setDeobfuscatedName("Inner");

        final TopLevelClassMapping demo = mappings.getOrCreateTopLevelClassMapping("Demo")
                .setDeobfuscatedName("DoubleDeobf");

        return mappings;
    }

}

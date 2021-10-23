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

import org.cadixdev.lorenz.MappingSet;
import org.cadixdev.lorenz.asm.LorenzRemapper;
import org.cadixdev.lorenz.model.TopLevelClassMapping;
import org.cadixdev.bombe.analysis.InheritanceProvider;
import org.cadixdev.bombe.analysis.ReflectionInheritanceProvider;
import org.cadixdev.bombe.type.signature.MethodSignature;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.commons.ClassRemapper;
import org.objectweb.asm.tree.ClassNode;

import java.io.IOException;

public class LorenzRemapperInheritanceTest {

    private static final MappingSet MAPPINGS = new MappingSet();
    private static final InheritanceProvider INHERITANCE = new ReflectionInheritanceProvider(LorenzRemapperInheritanceTest.class.getClassLoader());
    private static final LorenzRemapper REMAPPER = new LorenzRemapper(MAPPINGS, INHERITANCE);

    static {
        final TopLevelClassMapping baseClass = MAPPINGS.getOrCreateTopLevelClassMapping("test/inheritance/a/BaseClass");
        baseClass.createMethodMapping(MethodSignature.of("helloWorld()V"), "bye");
    }

    @Test
    public void testRemapIfSuperClassWithoutMappingsMakesInheritableMethodVisible() throws IOException {
        ClassReader reader = new ClassReader("test.inheritance.TestClass");
        ClassNode node = new ClassNode();
        reader.accept(new ClassRemapper(node, REMAPPER), 0);

        assertEquals("bye", node.methods.get(1).name);
    }

}

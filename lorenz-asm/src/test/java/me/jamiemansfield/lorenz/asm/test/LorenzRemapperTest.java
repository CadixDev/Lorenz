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

package me.jamiemansfield.lorenz.asm.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import me.jamiemansfield.bombe.analysis.CascadingInheritanceProvider;
import me.jamiemansfield.bombe.analysis.InheritanceProvider;
import me.jamiemansfield.lorenz.MappingSet;
import me.jamiemansfield.lorenz.asm.LorenzRemapper;
import me.jamiemansfield.lorenz.model.InnerClassMapping;
import me.jamiemansfield.lorenz.model.TopLevelClassMapping;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.ClassRemapper;
import org.objectweb.asm.tree.ClassNode;

public final class LorenzRemapperTest {

    private static final MappingSet MAPPINGS = MappingSet.create();
    private static final InheritanceProvider INHERITANCE = new CascadingInheritanceProvider();
    private static final LorenzRemapper REMAPPER = new LorenzRemapper(MAPPINGS, INHERITANCE);

    static {
        final TopLevelClassMapping ght = MAPPINGS.getOrCreateTopLevelClassMapping("ght");
        final InnerClassMapping ght$hy = ght.getOrCreateInnerClassMapping("hy");

        // Mappings for #topLevelClass()
        ght.setDeobfuscatedName("Demo");

        // Mappings for #innerClass()
        ght$hy.setDeobfuscatedName("Inner");
    }

    @Test
    public void topLevelClass() {
        final ClassNode node = new ClassNode();
        final ClassRemapper remapper = new ClassRemapper(node, REMAPPER);
        remapper.visit(Opcodes.V1_5, Opcodes.ACC_PUBLIC, "ght", null, "java/lang/Object", null);
        assertEquals("Demo", node.name);
    }

    @Test
    public void innerClass() {
        final ClassNode node = new ClassNode();
        final ClassRemapper remapper = new ClassRemapper(node, REMAPPER);
        remapper.visit(Opcodes.V1_5, Opcodes.ACC_PUBLIC, "ght$hy", null, "java/lang/Object", null);
        assertEquals("Demo$Inner", node.name);
    }

}

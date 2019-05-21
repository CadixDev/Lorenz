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
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.cadixdev.lorenz.MappingSet;
import org.cadixdev.lorenz.model.FieldMapping;
import org.cadixdev.lorenz.model.InnerClassMapping;
import org.cadixdev.lorenz.model.MethodMapping;
import org.cadixdev.lorenz.model.TopLevelClassMapping;
import org.junit.jupiter.api.Test;

import java.util.Optional;

/**
 * Unit tests pertaining to the merge code throughout Lorenz.
 */
public final class MergeTest {

    private static final MappingSet ONE = MappingSet.create();
    private static final MappingSet TWO = MappingSet.create();

    static {
        final TopLevelClassMapping ab = ONE.getOrCreateTopLevelClassMapping("ab")
                .setDeobfuscatedName("Demo");
        ab.getOrCreateFieldMapping("ui")
                .setDeobfuscatedName("log");
        ab.getOrCreateMethodMapping("hhyg", "()V")
                .setDeobfuscatedName("main");
        ab.getOrCreateInnerClassMapping("gh")
                .setDeobfuscatedName("Boop");

        final TopLevelClassMapping demo = TWO.getOrCreateTopLevelClassMapping("Demo")
                .setDeobfuscatedName("Container");
        demo.getOrCreateFieldMapping("log")
                .setDeobfuscatedName("logger");
        demo.getOrCreateMethodMapping("main", "()V")
                .setDeobfuscatedName("run");
        demo.getOrCreateInnerClassMapping("Boop")
                .setDeobfuscatedName("Beep");
    }

    @Test
    public void merge() {
        final MappingSet merged = ONE.merge(TWO);

        // top level class
        final Optional<TopLevelClassMapping> demo = merged.getTopLevelClassMapping("ab");
        assertTrue(demo.isPresent(), "ab not present!");
        assertEquals("Container", demo.get().getDeobfuscatedName(), "ab has the wrong de-obf name!");

        // inner class
        final Optional<InnerClassMapping> boop = demo.get().getInnerClassMapping("gh");
        assertTrue(boop.isPresent(), "gh not present!");
        assertEquals("Beep", boop.get().getDeobfuscatedName(), "gh has the wrong de-obf name!");

        // field
        final Optional<FieldMapping> log = demo.get().getFieldMapping("ui");
        assertTrue(log.isPresent(), "ui not present!");
        assertEquals("logger", log.get().getDeobfuscatedName(), "ui has the wrong de-obf name!");

        // method
        final Optional<MethodMapping> main = demo.get().getMethodMapping("hhyg", "()V");
        assertTrue(main.isPresent(), "hhyg not present!");
        assertEquals("run", main.get().getDeobfuscatedName(), "hhyg has the wrong de-obf name!");
    }

}

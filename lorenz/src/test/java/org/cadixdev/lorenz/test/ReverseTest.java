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
import org.cadixdev.lorenz.util.Reversible;
import org.junit.jupiter.api.Test;

import java.util.Optional;

/**
 * Unit tests pertaining to {@link Reversible}.
 */
public final class ReverseTest {

    private static final MappingSet MAPPINGS = new MappingSet();

    static {
        final TopLevelClassMapping ab = MAPPINGS.getOrCreateTopLevelClassMapping("ab")
                .setDeobfuscatedName("Demo");
        ab.getOrCreateFieldMapping("ui")
                .setDeobfuscatedName("log");
        ab.getOrCreateMethodMapping("hhyg", "()V")
                .setDeobfuscatedName("main");
        ab.getOrCreateInnerClassMapping("gh")
                .setDeobfuscatedName("Boop");
    }

    @Test
    public void reverse() {
        final MappingSet reversed = MAPPINGS.reverse();

        // top level class
        final Optional<TopLevelClassMapping> demo = reversed.getTopLevelClassMapping("Demo");
        assertTrue(demo.isPresent(), "Demo not present!");
        assertEquals("ab", demo.get().getDeobfuscatedName(), "Demo has the wrong de-obf name!");

        // inner class
        final Optional<InnerClassMapping> boop = demo.get().getInnerClassMapping("Boop");
        assertTrue(boop.isPresent(), "Boop not present!");
        assertEquals("gh", boop.get().getDeobfuscatedName(), "Boop has the wrong de-obf name!");

        // field
        final Optional<FieldMapping> log = demo.get().getFieldMapping("log");
        assertTrue(log.isPresent(), "log not present!");
        assertEquals("ui", log.get().getDeobfuscatedName(), "log has the wrong de-obf name!");

        // method
        final Optional<MethodMapping> main = demo.get().getMethodMapping("main", "()V");
        assertTrue(main.isPresent(), "main not present!");
        assertEquals("hhyg", main.get().getDeobfuscatedName(), "main has the wrong de-obf name!");
    }

}

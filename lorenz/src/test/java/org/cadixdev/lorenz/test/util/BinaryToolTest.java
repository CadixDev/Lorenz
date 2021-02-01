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

package org.cadixdev.lorenz.test.util;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.cadixdev.lorenz.util.BinaryTool;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public final class BinaryToolTest {

    @Test
    @DisplayName("reads top level class binary name")
    public void readsTopLevelClass() {
        final String[] result = BinaryTool.from("a");

        assertArrayEquals(new String[]{
                "a"
        }, result);
    }

    @Test
    @DisplayName("reads inner class binary name")
    public void readsInnerClass() {
        final String[] result = BinaryTool.from("a$b$c");

        assertArrayEquals(new String[]{
                "a", "b", "c"
        }, result);
    }

    @Test
    @DisplayName("writes binary name")
    public void writeBinaryName() {
        assertEquals("a$b", BinaryTool.to(new String[] {
                "a", "b"
        }));
    }

}

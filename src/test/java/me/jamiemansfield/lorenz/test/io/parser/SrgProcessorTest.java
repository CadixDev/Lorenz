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

package me.jamiemansfield.lorenz.test.io.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import me.jamiemansfield.lorenz.MappingSet;
import me.jamiemansfield.lorenz.io.parser.SrgProcessor;
import me.jamiemansfield.lorenz.model.InnerClassMapping;
import me.jamiemansfield.lorenz.model.TopLevelClassMapping;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

/**
 * A variety of unit tests pertaining to {@link SrgProcessor}.
 */
public final class SrgProcessorTest {

    private MappingSet mappings;

    @Before
    public void initialise() throws IOException {
        final SrgProcessor processor = new SrgProcessor();

        // Feed in mappings
        processor.processLine("CL: ght uk/jamierocks/Test");
        processor.processLine("CL: ght$ds uk/jamierocks/Test$Example");

        this.mappings = processor.getResult();
    }

    @Test
    public void topLevelClass() {
        // 1. Check the class has been added to the mapping set
        assertTrue(this.mappings.hasTopLevelClassMapping("ght"));

        // 2. Get the class mapping, and check the obfuscated and de-obfuscated name
        final TopLevelClassMapping classMapping = this.mappings.getOrCreateTopLevelClassMapping("ght");
        assertEquals("ght", classMapping.getObfuscatedName());
        assertEquals("uk/jamierocks/Test", classMapping.getDeobfuscatedName());
    }

    @Test
    public void innerClass() {
        // 1. Check the /parent/ class has been added to the mapping set
        assertTrue(this.mappings.hasTopLevelClassMapping("ght"));

        // 2. Get the parent class mapping, and check the inner class mapping has been added to it
        final TopLevelClassMapping parentMapping = this.mappings.getOrCreateTopLevelClassMapping("ght");
        assertTrue(parentMapping.hasInnerClassMapping("ds"));

        // 3. Get the inner class mapping, and check the obfuscated, de-obfuscated name, and full de-obfuscated name
        final InnerClassMapping classMapping = parentMapping.getOrCreateInnerClassMapping("ds");
        assertEquals("ds", classMapping.getObfuscatedName());
        assertEquals("Example", classMapping.getDeobfuscatedName());
        assertEquals("uk/jamierocks/Test$Example", classMapping.getFullDeobfuscatedName());
    }

}

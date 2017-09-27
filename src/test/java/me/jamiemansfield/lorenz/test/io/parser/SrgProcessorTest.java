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
import me.jamiemansfield.lorenz.io.parser.SrgParser;
import me.jamiemansfield.lorenz.model.FieldMapping;
import me.jamiemansfield.lorenz.model.InnerClassMapping;
import me.jamiemansfield.lorenz.model.MethodMapping;
import me.jamiemansfield.lorenz.model.TopLevelClassMapping;
import me.jamiemansfield.lorenz.model.jar.MethodDescriptor;
import me.jamiemansfield.lorenz.model.jar.Signature;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

/**
 * A variety of unit tests pertaining to {@link SrgParser}.
 */
public final class SrgProcessorTest {

    private MappingSet mappings;

    @Before
    public void initialise() throws IOException {
        final SrgParser parser = new SrgParser();

        // Feed in mappings
        parser.processLine("CL: ght uk/jamierocks/Test");
        parser.processLine("CL: ght$ds uk/jamierocks/Test$Example");
        parser.processLine("CL: ght$ds$bg uk/jamierocks/Test$Example$Inner");
        parser.processLine("FD: ght/rft uk/jamierocks/Test/log");
        parser.processLine("FD: ght$ds/juh uk/jamierocks/Test$Example/server");
        parser.processLine("MD: ght/hyuip (I)Z uk/jamierocks/Test/isEven (I)Z");
        parser.processLine("MD: ght$ds/hyuip (I)Z uk/jamierocks/Test$Example/isOdd (I)Z");

        this.mappings = parser.getResult();
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

        // 3. Get the inner class mapping, and check the obfuscated, de-obfuscated, and full de-obfuscated name
        //    Also check the inner /inner/ class has been added to it
        final InnerClassMapping classMapping = parentMapping.getOrCreateInnerClassMapping("ds");
        assertEquals("ds", classMapping.getObfuscatedName());
        assertEquals("Example", classMapping.getDeobfuscatedName());
        assertEquals("ght$ds", classMapping.getFullObfuscatedName());
        assertEquals("uk/jamierocks/Test$Example", classMapping.getFullDeobfuscatedName());
        assertTrue(classMapping.hasInnerClassMapping("bg"));

        // 4. Get the inner /inner/ class mapping, and check the obfuscated, de-obfuscated, and full de-obfuscated name
        final InnerClassMapping innerClassMapping = classMapping.getOrCreateInnerClassMapping("bg");
        assertEquals("bg", innerClassMapping.getObfuscatedName());
        assertEquals("Inner", innerClassMapping.getDeobfuscatedName());
        assertEquals("ght$ds$bg", innerClassMapping.getFullObfuscatedName());
        assertEquals("uk/jamierocks/Test$Example$Inner", innerClassMapping.getFullDeobfuscatedName());
    }

    @Test
    public void field() {
        // 1. Check the /parent/ class has been added to the mapping set
        assertTrue(this.mappings.hasTopLevelClassMapping("ght"));

        // 2. Get the class mapping, and check the field mapping has been added to it
        final TopLevelClassMapping parentMapping = this.mappings.getOrCreateTopLevelClassMapping("ght");
        assertTrue(parentMapping.hasFieldMapping("rft"));

        // 3. Get the field mapping, and check the obfuscated, de-obfuscated, and full de-obfuscated name
        final FieldMapping fieldMapping = parentMapping.getOrCreateFieldMapping("rft");
        assertEquals("rft", fieldMapping.getObfuscatedName());
        assertEquals("log", fieldMapping.getDeobfuscatedName());
        assertEquals("ght/rft", fieldMapping.getFullObfuscatedName());
        assertEquals("uk/jamierocks/Test/log", fieldMapping.getFullDeobfuscatedName());

        // 4. Check the /inner/ class mapping has been added to the class mapping
        assertTrue(parentMapping.hasInnerClassMapping("ds"));

        // 5. Get the inner class mapping, and check the field mapping has been added to it
        final InnerClassMapping classMapping = parentMapping.getOrCreateInnerClassMapping("ds");
        assertTrue(classMapping.hasFieldMapping("juh"));

        // 6. Get the field mapping, and check the obfuscated, de-obfuscated, and full de-obfuscated name
        final FieldMapping innerFieldMapping = classMapping.getOrCreateFieldMapping("juh");
        assertEquals("juh", innerFieldMapping.getObfuscatedName());
        assertEquals("server", innerFieldMapping.getDeobfuscatedName());
        assertEquals("ght$ds/juh", innerFieldMapping.getFullObfuscatedName());
        assertEquals("uk/jamierocks/Test$Example/server", innerFieldMapping.getFullDeobfuscatedName());
    }

    @Test
    public void method() {
        // 1. Check the /parent/ class has been added to the mapping set
        assertTrue(this.mappings.hasTopLevelClassMapping("ght"));

        // 2. Get the class mapping, and check the method mapping has been added to it
        final TopLevelClassMapping parentMapping = this.mappings.getOrCreateTopLevelClassMapping("ght");
        final Signature isEvenSignature = Signature.compile("(I)Z");
        final MethodDescriptor isEvenDescriptor = new MethodDescriptor("hyuip", isEvenSignature);
        assertTrue(parentMapping.hasMethodMapping(isEvenDescriptor));

        // 3. Get the method mapping, and verify it
        final MethodMapping methodMapping = parentMapping.getOrCreateMethodMapping(isEvenDescriptor);
        assertEquals("hyuip", methodMapping.getObfuscatedName());
        assertEquals("isEven", methodMapping.getDeobfuscatedName());
        assertEquals("(I)Z", methodMapping.getObfuscatedSignature());
        assertEquals("(I)Z", methodMapping.getDeobfuscatedSignature());
        assertEquals("ght/hyuip", methodMapping.getFullObfuscatedName());
        assertEquals("uk/jamierocks/Test/isEven", methodMapping.getFullDeobfuscatedName());

        // 4. Check the /inner/ class mapping has been added to the class mapping
        assertTrue(parentMapping.hasInnerClassMapping("ds"));

        // 5. Get the inner class mapping, and check the field mapping has been added to it
        final InnerClassMapping classMapping = parentMapping.getOrCreateInnerClassMapping("ds");
        assertTrue(parentMapping.hasMethodMapping(isEvenDescriptor));

        // 6. Get the method mapping, and verify it
        final MethodMapping innerMethodMapping = classMapping.getOrCreateMethodMapping(isEvenDescriptor);
        assertEquals("hyuip", innerMethodMapping.getObfuscatedName());
        assertEquals("isOdd", innerMethodMapping.getDeobfuscatedName());
        assertEquals("(I)Z", innerMethodMapping.getObfuscatedSignature());
        assertEquals("(I)Z", innerMethodMapping.getDeobfuscatedSignature());
        assertEquals("ght$ds/hyuip", innerMethodMapping.getFullObfuscatedName());
        assertEquals("uk/jamierocks/Test$Example/isOdd", innerMethodMapping.getFullDeobfuscatedName());
    }

}

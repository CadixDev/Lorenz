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

package me.jamiemansfield.lorenz.test.io.enigma;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import me.jamiemansfield.bombe.type.MethodDescriptor;
import me.jamiemansfield.bombe.type.signature.FieldSignature;
import me.jamiemansfield.bombe.type.signature.MethodSignature;
import me.jamiemansfield.lorenz.MappingSet;
import me.jamiemansfield.lorenz.io.MappingsReader;
import me.jamiemansfield.lorenz.io.enigma.EnigmaConstants;
import me.jamiemansfield.lorenz.io.enigma.EnigmaReader;
import me.jamiemansfield.lorenz.model.FieldMapping;
import me.jamiemansfield.lorenz.model.InnerClassMapping;
import me.jamiemansfield.lorenz.model.MethodMapping;
import me.jamiemansfield.lorenz.model.MethodParameterMapping;
import me.jamiemansfield.lorenz.model.TopLevelClassMapping;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class EnigmaReaderTest {

    private final MappingSet mappings;

    public EnigmaReaderTest() throws IOException {
        final MappingsReader reader = new EnigmaReader(EnigmaReaderTest.class.getResourceAsStream("/test.enigma"));
        this.mappings = reader.read();
        reader.close();
    }

    @Test
    public void commentRemoval() {
        // 1. Check an all comments line
        final String emptyLine = "# This is a comment";
        assertEquals("", EnigmaConstants.removeComments(emptyLine).trim());

        // 2. Check a mixed line
        final String mixedLine = "blah blah blah # This is a comment";
        assertEquals("blah blah blah", EnigmaConstants.removeComments(mixedLine).trim());

        // 3. Check that SrgParser#processLine(String) won't accept comments
        assertFalse(this.mappings.hasTopLevelClassMapping("yu"));
        assertTrue(this.mappings.hasTopLevelClassMapping("uih"));
        assertFalse(this.mappings.hasTopLevelClassMapping("op"));
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
        final FieldSignature rftSignature = new FieldSignature("rft", "Ljava/util/logging/Logger;");
        assertTrue(parentMapping.hasFieldMapping(rftSignature));

        // 3. Get the field mapping, and check the obfuscated, de-obfuscated, and full de-obfuscated name
        final FieldMapping fieldMapping = parentMapping.getOrCreateFieldMapping(rftSignature);
        assertEquals("rft", fieldMapping.getObfuscatedName());
        assertEquals("log", fieldMapping.getDeobfuscatedName());
        assertEquals("ght/rft", fieldMapping.getFullObfuscatedName());
        assertEquals("uk/jamierocks/Test/log", fieldMapping.getFullDeobfuscatedName());

        // 4. Check the /inner/ class mapping has been added to the class mapping
        assertTrue(parentMapping.hasInnerClassMapping("ds"));

        // 5. Get the inner class mapping, and check the field mapping has been added to it
        final InnerClassMapping classMapping = parentMapping.getOrCreateInnerClassMapping("ds");
        final FieldSignature juhSignature = new FieldSignature("juh", "Luk/jamierocks/Server;");
        assertTrue(classMapping.hasFieldMapping(juhSignature));

        // 6. Get the field mapping, and check the obfuscated, de-obfuscated, and full de-obfuscated name
        final FieldMapping innerFieldMapping = classMapping.getOrCreateFieldMapping(juhSignature);
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
        final MethodDescriptor isEvenSignature = MethodDescriptor.compile("(I)Z");
        final MethodSignature isEvenDescriptor = new MethodSignature("hyuip", isEvenSignature);
        assertTrue(parentMapping.hasMethodMapping(isEvenDescriptor));

        // 3. Get the method mapping, and verify it
        final MethodMapping methodMapping = parentMapping.getOrCreateMethodMapping(isEvenDescriptor);
        assertEquals("hyuip", methodMapping.getObfuscatedName());
        assertEquals("isEven", methodMapping.getDeobfuscatedName());
        assertEquals("(I)Z", methodMapping.getObfuscatedDescriptor());
        assertEquals("(I)Z", methodMapping.getDeobfuscatedDescriptor());
        assertEquals("ght/hyuip", methodMapping.getFullObfuscatedName());
        assertEquals("uk/jamierocks/Test/isEven", methodMapping.getFullDeobfuscatedName());

        // 4. Check the parameter mapping is there
        assertTrue(methodMapping.hasParameterMapping(0));

        // 5. Verify the parameter mapping
        final MethodParameterMapping parameterMapping = methodMapping.getOrCreateParameterMapping(0);
        assertEquals("num", parameterMapping.getDeobfuscatedName());

        // 6. Check the /inner/ class mapping has been added to the class mapping
        assertTrue(parentMapping.hasInnerClassMapping("ds"));

        // 7. Get the inner class mapping, and check the field mapping has been added to it
        final InnerClassMapping classMapping = parentMapping.getOrCreateInnerClassMapping("ds");
        assertTrue(parentMapping.hasMethodMapping(isEvenDescriptor));

        // 8. Get the method mapping, and verify it
        final MethodMapping innerMethodMapping = classMapping.getOrCreateMethodMapping(isEvenDescriptor);
        assertEquals("hyuip", innerMethodMapping.getObfuscatedName());
        assertEquals("isOdd", innerMethodMapping.getDeobfuscatedName());
        assertEquals("(I)Z", innerMethodMapping.getObfuscatedDescriptor());
        assertEquals("(I)Z", innerMethodMapping.getDeobfuscatedDescriptor());
        assertEquals("ght$ds/hyuip", innerMethodMapping.getFullObfuscatedName());
        assertEquals("uk/jamierocks/Test$Example/isOdd", innerMethodMapping.getFullDeobfuscatedName());

        // 9. Check the parameter mapping is there
        assertTrue(innerMethodMapping.hasParameterMapping(0));

        // 10. Verify the parameter mapping
        final MethodParameterMapping innerParameterMapping = innerMethodMapping.getOrCreateParameterMapping(0);
        assertEquals("num", innerParameterMapping.getDeobfuscatedName());
    }

}

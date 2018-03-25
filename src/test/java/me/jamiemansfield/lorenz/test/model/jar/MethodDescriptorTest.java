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

package me.jamiemansfield.lorenz.test.model.jar;

import static org.junit.Assert.assertEquals;

import me.jamiemansfield.lorenz.MappingSet;
import me.jamiemansfield.lorenz.model.jar.MethodDescriptor;
import me.jamiemansfield.lorenz.model.jar.Type;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * A variety of unit tests pertaining to the de-obfuscation
 * pertaining to {@link MethodDescriptor}.
 */
public final class MethodDescriptorTest {

    private static MappingSet mappings;

    @BeforeClass
    public static void initialise() {
        mappings = MappingSet.create();
        mappings.getOrCreateTopLevelClassMapping("ght")
                .setDeobfuscatedName("uk/jamierocks/Test");
    }

    @Test
    public void deobfuscateType() {
        final String type = "Lhuy;";
        assertEquals(type, this.deobfRawType(type));

        final String primitiveType = "Z";
        assertEquals(primitiveType, this.deobfRawType(primitiveType));

        final String obfuscatedType = "Lght;";
        final String deobfuscatedType = "Luk/jamierocks/Test;";
        assertEquals(deobfuscatedType, this.deobfRawType(obfuscatedType));

        final String arrayType = "[[Ljava/lang/String;";
        assertEquals(arrayType, this.deobfRawType(arrayType));

        final String obfuscatedArrayType = "[[[Lght;";
        final String deobfuscatedArrayType = "[[[Luk/jamierocks/Test;";
        assertEquals(deobfuscatedArrayType, deobfRawType(obfuscatedArrayType));
    }

    @Test
    public void deobfuscateMethodDescriptor() {
        final String descriptor = "(Lhuy;)Lhuy;";
        assertEquals(descriptor, this.deobfRawDesc(descriptor));

        final String simpleObfuscatedDescriptor = "(Lght;)Lght;";
        final String simpleDeobfuscatedDescriptor = "(Luk/jamierocks/Test;)Luk/jamierocks/Test;";
        assertEquals(simpleDeobfuscatedDescriptor, this.deobfRawDesc(simpleObfuscatedDescriptor));

        final String primitivesObfuscatedDescriptor = "(Lght;Z)Lght;";
        final String primitivesDeobfuscatedDescriptor = "(Luk/jamierocks/Test;Z)Luk/jamierocks/Test;";
        assertEquals(primitivesDeobfuscatedDescriptor, this.deobfRawDesc(primitivesObfuscatedDescriptor));

        final String complexIshObfuscatedDescriptor = "(ZILght;II)Lhuy;";
        final String complexIshDeobfuscatedDescriptor = "(ZILuk/jamierocks/Test;II)Lhuy;";
        assertEquals(complexIshDeobfuscatedDescriptor, this.deobfRawDesc(complexIshObfuscatedDescriptor));
    }

    /**
     * A convenience method, to de-obfuscate a raw type using the
     * test's {@link MappingSet}.
     *
     * @param rawType The raw type, for de-obfuscation
     * @return The de-obfuscated type
     */
    private String deobfRawType(final String rawType) {
        return Type.of(rawType).getDeobfuscated(mappings);
    }

    /**
     * A convenience method, to de-obfuscate a raw descriptor using the
     * test's {@link MappingSet}.
     *
     * @param descriptor The raw descriptor, for de-obfuscation
     * @return The de-obfuscated descriptor
     */
    private String deobfRawDesc(final String descriptor) {
        return MethodDescriptor.compile(descriptor).getDeobfuscated(mappings);
    }

}

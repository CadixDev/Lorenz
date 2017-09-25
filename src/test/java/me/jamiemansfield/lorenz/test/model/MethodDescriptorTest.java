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

package me.jamiemansfield.lorenz.test.model;

import static org.junit.Assert.assertEquals;

import me.jamiemansfield.lorenz.MappingSet;
import me.jamiemansfield.lorenz.model.jar.MethodDescriptor;
import me.jamiemansfield.lorenz.model.jar.Signature;
import me.jamiemansfield.lorenz.model.jar.Type;
import org.junit.Before;
import org.junit.Test;

/**
 * A variety of unit tests pertaining to the de-obfuscation
 * methods in {@link MethodDescriptor}.
 */
public final class MethodDescriptorTest {

    private MappingSet mappings;

    @Before
    public void initialise() {
        this.mappings = new MappingSet();
        this.mappings.getOrCreateTopLevelClassMapping("ght")
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
    public void deobfuscateMethodSignature() {
        final String signature = "(Lhuy;)Lhuy;";
        assertEquals(signature, this.deobfRawSig(signature));

        final String simpleObfuscatedSignature = "(Lght;)Lght;";
        final String simpleDeobfuscatedSignature = "(Luk/jamierocks/Test;)Luk/jamierocks/Test;";
        assertEquals(simpleDeobfuscatedSignature, this.deobfRawSig(simpleObfuscatedSignature));

        final String primitivesObfuscatedSignature = "(Lght;Z)Lght;";
        final String primitivesDeobfuscatedSignature = "(Luk/jamierocks/Test;Z)Luk/jamierocks/Test;";
        assertEquals(primitivesDeobfuscatedSignature, this.deobfRawSig(primitivesObfuscatedSignature));

        final String complexIshObfuscatedSignature = "(ZILght;II)Lhuy;";
        final String complexIshDeobfuscatedSignature = "(ZILuk/jamierocks/Test;II)Lhuy;";
        assertEquals(complexIshDeobfuscatedSignature, this.deobfRawSig(complexIshObfuscatedSignature));
    }

    /**
     * A convenience method, to de-obfuscate a raw type using the
     * test's {@link MappingSet}.
     *
     * @param rawType The raw type, for de-obfuscation
     * @return The de-obfuscated type
     */
    private String deobfRawType(final String rawType) {
        return Type.of(rawType).getDeobfuscated(this.mappings);
    }

    /**
     * A convenience method, to de-obfuscate a raw signature using the
     * test's {@link MappingSet}.
     *
     * @param rawSig The raw signature, for de-obfuscation
     * @return The de-obfuscated signature
     */
    private String deobfRawSig(final String rawSig) {
        return Signature.compile(rawSig).getDeobfuscated(this.mappings);
    }

}

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
import static org.junit.Assert.assertTrue;

import me.jamiemansfield.lorenz.MappingSet;
import me.jamiemansfield.lorenz.model.jar.ArrayType;
import me.jamiemansfield.lorenz.model.jar.MethodDescriptor;
import me.jamiemansfield.lorenz.model.jar.ObjectType;
import me.jamiemansfield.lorenz.model.jar.PrimitiveType;
import me.jamiemansfield.lorenz.model.jar.Type;
import me.jamiemansfield.lorenz.model.jar.VoidType;
import me.jamiemansfield.lorenz.test.LorenzTests;
import org.junit.Test;

/**
 * A variety of unit tests pertaining to the de-obfuscation
 * pertaining to {@link MethodDescriptor}.
 */
public final class TypeTest {

    @Test
    public void arrayType() {
        final String raw = "[[I";
        final Type type = Type.of(raw);
        assertTrue("Type should be an ArrayType!", type instanceof ArrayType);
        assertEquals(raw, type.getObfuscated());
        assertEquals(raw, type.toString());
        final ArrayType array = (ArrayType) type;
        assertEquals(2, array.getDimCount());
        assertEquals(PrimitiveType.INT, array.getComponent());
    }

    @Test
    public void objectTest() {
        final String raw = "Lme/jamiemansfield/Test;";
        final Type type = Type.of(raw);
        assertTrue("Type should be an ObjectType!", type instanceof ObjectType);
        assertEquals(raw, type.getObfuscated());
        assertEquals(raw, type.toString());
    }

    @Test
    public void primitiveTest() {
        final String raw = "Z";
        final Type type = Type.of(raw);
        assertTrue("Type should be an PrimitiveType!", type instanceof PrimitiveType);
        assertEquals(PrimitiveType.BOOLEAN, type);
        assertEquals(raw, type.getObfuscated());
        assertEquals(raw, type.toString());
    }

    @Test
    public void voidTest() {
        final String raw = "V";
        final Type type = Type.of(raw);
        assertTrue("Type should be an VoidType!", type instanceof VoidType);
        assertEquals(VoidType.INSTANCE, type);
        assertEquals(raw, type.getObfuscated());
        assertEquals(raw, type.toString());
    }

    @Test(expected = RuntimeException.class)
    public void invalidTest() {
        Type.of("Jungle");
        Type.of("A");
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

    /**
     * A convenience method, to de-obfuscate a raw type using the
     * test's {@link MappingSet}.
     *
     * @param rawType The raw type, for de-obfuscation
     * @return The de-obfuscated type
     */
    private String deobfRawType(final String rawType) {
        return Type.of(rawType).getDeobfuscated(LorenzTests.BASIC_MAPPINGS);
    }

}

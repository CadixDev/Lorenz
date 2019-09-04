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

package org.cadixdev.lorenz.io.proguard;

import me.jamiemansfield.string.StringReader;
import org.cadixdev.bombe.type.ArrayType;
import org.cadixdev.bombe.type.BaseType;
import org.cadixdev.bombe.type.FieldType;
import org.cadixdev.bombe.type.ObjectType;
import org.cadixdev.bombe.type.Type;
import org.cadixdev.bombe.type.VoidType;

public class PGTypeReader extends StringReader {

    public PGTypeReader(final String source) {
        super(source);
    }

    public Type readType() {
        if (this.match("void")) return VoidType.INSTANCE;
        return this.readFieldType();
    }

    public FieldType readFieldType() {
        while (this.available() && this.peek() != '[') {
            this.advance();
        }
        final FieldType type = getType(this.substring(0, this.index()));
        if (!this.available()) return type;
        int dims = 0;
        while (this.available()) {
            if (this.advance() == '[') {
                dims++;
            }
        }
        return new ArrayType(dims, type);
    }

    private boolean match(final String raw) {
        for (int i = 0; i < raw.toCharArray().length; i++) {
            if (raw.toCharArray()[i] != this.peek(i)) {
                return false;
            }
        }
        return true;
    }

    private static FieldType getType(final String raw) {
        if ("byte".equals(raw)) {
            return BaseType.BYTE;
        }
        else if ("char".equals(raw)) {
            return BaseType.CHAR;
        }
        else if ("double".equals(raw)) {
            return BaseType.DOUBLE;
        }
        else if ("float".equals(raw)) {
            return BaseType.FLOAT;
        }
        else if ("int".equals(raw)) {
            return BaseType.INT;
        }
        else if ("long".equals(raw)) {
            return BaseType.LONG;
        }
        else if ("short".equals(raw)) {
            return BaseType.SHORT;
        }
        else if ("boolean".equals(raw)) {
            return BaseType.BOOLEAN;
        }
        else {
            // ObjectType will replace the full stops for forward slashes
            return new ObjectType(raw);
        }
    }

}

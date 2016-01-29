/*
 * This file is part of Lorenz, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2016, Lexteam <http://www.lexteam.xyz/>
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
package xyz.lexteam.lorenz.model;

/**
 * Represents a method mapping.
 */
public class MethodMapping extends BaseMapping {

    private final ClassMapping parent;
    private final String obfuscatedSignature;

    public MethodMapping(ClassMapping parent, String obfuscated, String obfuscatedSignature, String deobfuscated) {
        super(obfuscated, deobfuscated);
        this.parent = parent;
        this.obfuscatedSignature = obfuscatedSignature;
    }

    public ClassMapping getParent() {
        return this.parent;
    }

    public String getObfuscatedSignature() {
        return this.obfuscatedSignature;
    }

    public String getDeobfuscatedSignature() {
        String innerContent = this.obfuscatedSignature.substring(this.obfuscatedSignature.indexOf("(") + 1,
                this.obfuscatedSignature.indexOf(")"));
        String outerContent = this.obfuscatedSignature.substring(this.obfuscatedSignature.indexOf(")") + 1);

        String modifiedType = this.obfuscatedSignature;

        for (String type : innerContent.split(";")) {
            if (type.startsWith("L")) {
                String newType = type.substring(1);
                if (this.getParent().getParent().getClassMappings().containsKey(newType)) {
                    modifiedType = modifiedType.replace(newType,
                            this.getParent().getParent().getClassMappings().get(newType).getDeobfuscatedName());
                }
            }
        }

        if (outerContent.startsWith("L")) {
            String outerType = outerContent.substring(1, outerContent.length() - 1);
            if (this.getParent().getParent().getClassMappings().containsKey(outerType)) {
                modifiedType = modifiedType.replace(outerType,
                        this.getParent().getParent().getClassMappings().get(outerType).getDeobfuscatedName());
            }
        }

        return modifiedType;
    }
}

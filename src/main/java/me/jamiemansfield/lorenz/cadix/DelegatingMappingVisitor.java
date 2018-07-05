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

package me.jamiemansfield.lorenz.cadix;

import me.jamiemansfield.lorenz.model.jar.signature.MethodSignature;

/**
 * An implementation of {@link MappingVisitor} that delegates to a second
 * visitor.
 *
 * @author Jamie Mansfield
 * @since 0.4.0
 */
public class DelegatingMappingVisitor implements MappingVisitor {

    private final MappingVisitor visitor;

    public DelegatingMappingVisitor(final MappingVisitor visitor) {
        this.visitor = visitor;
    }

    @Override
    public void visit(final String obfuscatedName, final String deobfuscatedName) {
        if (this.visitor != null) {
            this.visitor.visit(obfuscatedName, deobfuscatedName);
        }
    }

    @Override
    public void visitField(final String obfuscatedName, final String deobfuscatedName) {
        if (this.visitor != null) {
            this.visitor.visitField(obfuscatedName, deobfuscatedName);
        }
    }

    @Override
    public void visitMethod(final MethodSignature signature, final String deobfuscatedName) {
        if (this.visitor != null) {
            this.visitor.visitMethod(signature, deobfuscatedName);
        }
    }

    @Override
    public void visitInnerClass(final String obfuscatedName, final String deobfuscatedName) {
        if (this.visitor != null) {
            this.visitor.visitInnerClass(obfuscatedName, deobfuscatedName);
        }
    }

    @Override
    public void end() {
        if (this.visitor != null) {
            this.visitor.end();
        }
    }

}

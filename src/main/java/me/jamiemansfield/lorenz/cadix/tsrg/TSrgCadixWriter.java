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

package me.jamiemansfield.lorenz.cadix.tsrg;

import me.jamiemansfield.lorenz.cadix.MappingVisitor;
import me.jamiemansfield.lorenz.model.jar.signature.MethodSignature;

import java.io.PrintWriter;

/**
 * A {@link MappingVisitor} that can produce a TSRG mappings file.
 *
 * @author Jamie Mansfield
 * @since 0.4.0
 */
public class TSrgCadixWriter implements MappingVisitor {

    private static String TAB = "\t";

    private final PrintWriter writer;
    private String currentClass;

    public TSrgCadixWriter(final PrintWriter writer) {
        this.writer = writer;
    }

    @Override
    public void visit(final String obfuscatedName, final String deobfuscatedName) {
        this.currentClass = obfuscatedName;
        this.writeClass(deobfuscatedName);
    }

    private void writeClass(final String deobfuscatedName) {
        this.writer.println(String.format("%s %s", this.currentClass, deobfuscatedName));
    }

    @Override
    public void visitField(final String obfuscatedName, final String deobfuscatedName) {
        if (this.currentClass != null) {
            this.writer.println(String.format("%s %s %s", TAB, obfuscatedName, deobfuscatedName));
        }
    }

    @Override
    public void visitMethod(final MethodSignature signature, final String deobfuscatedName) {
        if (this.currentClass != null) {
            this.writer.println(String.format("%s %s %s %s", TAB,
                    signature.getName(), signature.getDescriptor().getObfuscated(),
                    deobfuscatedName));
        }
    }

    @Override
    public void visitInnerClass(final String obfuscatedName, final String deobfuscatedName) {
        if (this.currentClass != null) {
            this.currentClass = String.format("%s$%s", this.currentClass, obfuscatedName);
            this.writeClass(deobfuscatedName);
        }
    }

    @Override
    public void end() {
        if (this.currentClass != null) {
            this.currentClass = null;
        }
    }

}

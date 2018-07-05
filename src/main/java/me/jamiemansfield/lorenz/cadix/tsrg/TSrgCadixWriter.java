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

import com.google.common.base.Strings;
import me.jamiemansfield.lorenz.cadix.MappingVisitor;
import me.jamiemansfield.lorenz.model.jar.signature.MethodSignature;

import java.io.PrintWriter;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A {@link MappingVisitor} that can produce a TSRG mappings file.
 *
 * @author Jamie Mansfield
 * @since 0.4.0
 */
public class TSrgCadixWriter implements MappingVisitor {

    private static String TAB = "\t";

    private final PrintWriter writer;
    private final AtomicInteger level = new AtomicInteger(1);

    public TSrgCadixWriter(final PrintWriter writer) {
        this.writer = writer;
    }

    @Override
    public void visit(final String obfuscatedName, final String deobfuscatedName) {
        this.writer.println(String.format("%s %s", obfuscatedName, deobfuscatedName));
    }

    @Override
    public void visitField(final String obfuscatedName, final String deobfuscatedName) {
        this.writer.println(String.format("%s %s %s",
                Strings.repeat(TAB, this.level.get()),
                obfuscatedName, deobfuscatedName));
    }

    @Override
    public void visitMethod(final MethodSignature signature, final String deobfuscatedName) {
        this.writer.println(String.format("%s %s %s %s",
                Strings.repeat(TAB, this.level.get()),
                signature.getName(), signature.getDescriptor().getObfuscated(),
                signature.getDescriptor().getDeobfuscated(null))); // TODO: rework descriptors
    }

    @Override
    public void visitInnerClass(final String obfuscatedName, final String deobfuscatedName) {
        this.writer.println(String.format("%s %s %s",
                Strings.repeat(TAB, this.level.getAndIncrement()),
                obfuscatedName, deobfuscatedName));
    }

    @Override
    public void endInnerClass() {
        this.level.decrementAndGet();
    }

    @Override
    public void endClass() {
    }

}

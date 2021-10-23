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

package org.cadixdev.lorenz.test.util

import org.cadixdev.lorenz.util.BinaryTool
import spock.lang.Specification

class BinaryToolSpec extends Specification {

    def 'reads top level class binary name'(final String className) {
        when:
        def result = BinaryTool.from(className)

        then:
        result.length == 1
        result[0] == className

        where:
        className | _
        'a' | _
        'org/cadixdev/example/Example' | _
    }

    def 'reads inner class binary name'(final String className, final String[] expected) {
        when:
        def result = BinaryTool.from(className)

        then:
        result == expected

        where:
        className | expected
        'a$b' | ['a', 'b'] as String[]
        'a$b$c' | ['a', 'b', 'c'] as String[]
        'org/cadixdev/example/Example$Inner' | ['org/cadixdev/example/Example', 'Inner'] as String[]
    }

    def 'writes binary name'(final String[] input, final String expected) {
        when:
        def result = BinaryTool.to(input)

        then:
        result == expected

        where:
        input | expected
        ['a', 'b'] as String[] | 'a$b'
        ['a', 'b', 'c'] as String[] | 'a$b$c'
        ['org/cadixdev/example/Example', 'Inner'] as String[] | 'org/cadixdev/example/Example$Inner'
    }

}

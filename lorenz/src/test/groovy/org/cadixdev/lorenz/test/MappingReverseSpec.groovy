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

package org.cadixdev.lorenz.test

import org.cadixdev.lorenz.MappingSet
import spock.lang.Specification

class MappingReverseSpec extends Specification {

    def 'reverses top level mapping'() {
        given:
        def originalClass = new MappingSet().getOrCreateTopLevelClassMapping('ab')
                .setDeobfuscatedName('Demo')
        def mappings = new MappingSet()

        when:
        originalClass.reverse(mappings)

        then:
        def copiedClass = mappings.getTopLevelClassMapping('Demo')
        copiedClass.isPresent()
        copiedClass.get().deobfuscatedName == 'ab'
    }

    def 'reverses field mapping'() {
        given:
        def originalField = new MappingSet().getOrCreateTopLevelClassMapping('ab')
                .getOrCreateFieldMapping('ui')
                .setDeobfuscatedName('log')
        def klassMapping = new MappingSet().getOrCreateTopLevelClassMapping('ab')

        when:
        originalField.reverse(klassMapping)

        then:
        def copiedField = klassMapping.getFieldMapping('log')
        copiedField.isPresent()
        copiedField.get().deobfuscatedName == 'ui'
    }

    def 'reverses method mapping'() {
        given:
        def originalMethod = new MappingSet().getOrCreateTopLevelClassMapping('ab')
                .getOrCreateMethodMapping('hhyg', '()V')
                .setDeobfuscatedName('main')
        def klassMapping = new MappingSet().getOrCreateTopLevelClassMapping('ab')

        when:
        originalMethod.reverse(klassMapping)

        then:
        def copiedMethod = klassMapping.getMethodMapping('main', '()V')
        copiedMethod.isPresent()
        copiedMethod.get().deobfuscatedName == 'hhyg'
    }

    def 'reverses inner mapping'() {
        given:
        def originalClass = new MappingSet().getOrCreateTopLevelClassMapping('ab')
                .getOrCreateInnerClassMapping('gh')
                .setDeobfuscatedName('Boop')
        def klassMapping = new MappingSet().getOrCreateTopLevelClassMapping('ab')

        when:
        originalClass.reverse(klassMapping)

        then:
        def copiedClass = klassMapping.getInnerClassMapping('Boop')
        copiedClass.isPresent()
        copiedClass.get().deobfuscatedName == 'gh'
    }

}

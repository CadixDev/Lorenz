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

class MappingCopySpec extends Specification {

    def 'copies mappings'() {
        given:
        def originalMappings = new MappingSet().with {
            it.getOrCreateTopLevelClassMapping('ab')
                    .setDeobfuscatedName('Demo')
            return it
        }

        when:
        def mappings = originalMappings.copy()

        then:
        mappings.getTopLevelClassMappings().size() == 1
        def classMapping = mappings.getClassMapping('ab')
        classMapping.isPresent()
        classMapping.get().deobfuscatedName == 'Demo'
    }

    def 'copies top level mapping'() {
        given:
        def originalClass = new MappingSet().getOrCreateTopLevelClassMapping('ab')
                .setDeobfuscatedName('Demo')
        def mappings = new MappingSet()

        when:
        originalClass.copy(mappings)

        then:
        def copiedClass = mappings.getTopLevelClassMapping('ab')
        copiedClass.isPresent()
        copiedClass.get().deobfuscatedName == 'Demo'
    }

    def 'copies field mapping'() {
        given:
        def originalField = new MappingSet().getOrCreateTopLevelClassMapping('ab')
                .getOrCreateFieldMapping('ui')
                .setDeobfuscatedName('log')
        def klassMapping = new MappingSet().getOrCreateTopLevelClassMapping('ab')

        when:
        originalField.copy(klassMapping)

        then:
        def copiedField = klassMapping.getFieldMapping('ui')
        copiedField.isPresent()
        copiedField.get().deobfuscatedName == 'log'
    }

    def 'copies method mapping'() {
        given:
        def originalMethod = new MappingSet().getOrCreateTopLevelClassMapping('ab')
                .getOrCreateMethodMapping('hhyg', '()V')
                .setDeobfuscatedName('main')
        def klassMapping = new MappingSet().getOrCreateTopLevelClassMapping('ab')

        when:
        originalMethod.copy(klassMapping)

        then:
        def copiedMethod = klassMapping.getMethodMapping('hhyg', '()V')
        copiedMethod.isPresent()
        copiedMethod.get().deobfuscatedName == 'main'
    }

    def 'copies param mapping'() {
        given:
        def originalParam = new MappingSet().getOrCreateTopLevelClassMapping('ab')
                .getOrCreateMethodMapping('jj', '(Ljava/lang/String;)V')
                .getOrCreateParameterMapping(0)
                .setDeobfuscatedName('name')
        def methodMapping = new MappingSet().getOrCreateTopLevelClassMapping('ab')
                .getOrCreateMethodMapping('jj', '(Ljava/lang/String;)V')

        when:
        originalParam.copy(methodMapping)

        then:
        def copiedParam = methodMapping.getParameterMapping(0)
        copiedParam.isPresent()
        copiedParam.get().deobfuscatedName == 'name'
    }

    def 'copies inner mapping'() {
        given:
        def originalClass = new MappingSet().getOrCreateTopLevelClassMapping('ab')
                .getOrCreateInnerClassMapping('gh')
                .setDeobfuscatedName('Boop')
        def klassMapping = new MappingSet().getOrCreateTopLevelClassMapping('ab')

        when:
        originalClass.copy(klassMapping)

        then:
        def copiedClass = klassMapping.getInnerClassMapping('gh')
        copiedClass.isPresent()
        copiedClass.get().deobfuscatedName == 'Boop'
    }

}

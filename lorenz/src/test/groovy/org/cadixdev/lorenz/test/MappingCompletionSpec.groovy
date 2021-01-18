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

import org.cadixdev.bombe.analysis.InheritanceProvider
import org.cadixdev.bombe.analysis.InheritanceType
import org.cadixdev.bombe.type.signature.MethodSignature
import org.cadixdev.lorenz.MappingSet
import spock.lang.Specification

class MappingCompletionSpec extends Specification {

    def 'override'(final String desc) {
        given:
        def mapping = basicMapping(desc)
        def inheritanceProvider = basicInheritanceProvider(desc, desc)

        when:
        mapping.complete(inheritanceProvider)

        then:
        def ghMapping = mapping.getMethodMapping('gh', desc)
        ghMapping.isPresent()
        ghMapping.get().deobfuscatedName == 'run'

        where:
        desc | _
        '()V' | _
        '(La;)V' | _
    }

    def 'override desc change'(final String aDesc, final String bDesc) {
        given:
        def mapping = basicMapping(aDesc)
        def inheritanceProvider = basicInheritanceProvider(aDesc, bDesc)

        when:
        mapping.complete(inheritanceProvider)

        then:
        def ghMapping = mapping.getMethodMapping('gh', bDesc)
        ghMapping.isPresent()
        ghMapping.get().deobfuscatedName == 'run'

        where:
        aDesc | bDesc
        '()La;' | '()Lb;'
        '(La;)La;' | '(La;)Lb;'
    }

    def 'not an override'(final String aDesc, final String bDesc) {
        given:
        def mapping = basicMapping(aDesc)
        def inheritanceProvider = basicInheritanceProvider(aDesc, bDesc)

        when:
        mapping.complete(inheritanceProvider)

        then:
        def ghMapping = mapping.getMethodMapping('gh', bDesc)
        !ghMapping.isPresent()

        where:
        aDesc | bDesc
        '()La;' | '()Lc;'
        '(La;)La;' | '(La;)Lc;'
        '(La;)V' | '(Lc;)V'
    }

    static def basicMapping(final String aDesc) {
        return new MappingSet().with {
            it.getOrCreateTopLevelClassMapping('a').with {
                it.getOrCreateMethodMapping('gh', aDesc)
                        .setDeobfuscatedName('run')
            }
            return it.getOrCreateTopLevelClassMapping('b')
        }
    }

    static def basicInheritanceProvider(final String aDesc, final String bDesc) {
        return new InheritanceProvider() {
            @Override
            Optional<InheritanceProvider.ClassInfo> provide(final String klass) {
                switch (klass) {
                    case 'a': {
                        return Optional.of(new InheritanceProvider.ClassInfo.Impl(
                                'a', false,
                                'java/lang/Object', Collections.emptyList(),
                                Collections.emptyMap(), Collections.emptyMap(),
                                Collections.singletonMap(
                                        MethodSignature.of('gh', aDesc),
                                        InheritanceType.PUBLIC
                                )
                        ))
                    }
                    case 'b': {
                        return Optional.of(new InheritanceProvider.ClassInfo.Impl(
                                'b', false,
                                'a', Collections.emptyList(),
                                Collections.emptyMap(), Collections.emptyMap(),
                                Collections.singletonMap(
                                        MethodSignature.of('gh', bDesc),
                                        InheritanceType.PUBLIC
                                )
                        ))
                    }
                }

                return Optional.empty()
            }
        }
    }

}

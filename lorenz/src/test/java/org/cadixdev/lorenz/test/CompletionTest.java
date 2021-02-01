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

package org.cadixdev.lorenz.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.cadixdev.bombe.analysis.InheritanceProvider;
import org.cadixdev.bombe.analysis.InheritanceType;
import org.cadixdev.bombe.type.BaseType;
import org.cadixdev.bombe.type.signature.FieldSignature;
import org.cadixdev.lorenz.MappingSet;
import org.cadixdev.lorenz.model.FieldMapping;
import org.cadixdev.lorenz.model.TopLevelClassMapping;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

public final class CompletionTest {

    @Test
    @DisplayName("inherits field mapping")
    public void inheritsFieldMapping() {
        // given:
        final MappingSet mappings = mappings();
        final TopLevelClassMapping Parent = mappings.getOrCreateTopLevelClassMapping("Parent");
        final TopLevelClassMapping Child2 = mappings.getOrCreateTopLevelClassMapping("Child2");
        final InheritanceProvider inheritanceProvider = inheritanceProvider();

        // when:
        Parent.complete(inheritanceProvider);
        Parent.computeFieldMapping(
                new FieldSignature("a", BaseType.INT)
        );
        Child2.complete(inheritanceProvider);

        // then:
        final Optional<FieldMapping> mapping = Child2.computeFieldMapping(
                new FieldSignature("a", BaseType.INT)
        );
        assertTrue(mapping.isPresent(), "Child2 hasn't inherited Parent/a");
        assertEquals("parentField", mapping.get().getDeobfuscatedName());
    }

    @Test
    @DisplayName("use correct field mapping")
    public void useCorrectFieldMapping() {
        // given:
        final MappingSet mappings = mappings();
        final TopLevelClassMapping Parent = mappings.getOrCreateTopLevelClassMapping("Parent");
        final TopLevelClassMapping Child1 = mappings.getOrCreateTopLevelClassMapping("Child1");
        final InheritanceProvider inheritanceProvider = inheritanceProvider();

        // when:
        Parent.complete(inheritanceProvider);
        Parent.computeFieldMapping(
                new FieldSignature("a", BaseType.INT)
        );
        Child1.complete(inheritanceProvider);

        // then:
        final Optional<FieldMapping> mapping = Child1.computeFieldMapping(
                new FieldSignature("a", BaseType.INT)
        );
        assertTrue(mapping.isPresent());
        assertEquals("childField", mapping.get().getDeobfuscatedName());
    }

    /*
    This is representing the following:

    Parent Parent
        a parentField

    Child1 Child1
        a childField
     */
    private static MappingSet mappings() {
        final MappingSet mappings = MappingSet.create();

        final TopLevelClassMapping Parent =
                mappings.getOrCreateTopLevelClassMapping("Parent");
        Parent.getOrCreateFieldMapping("a")
                .setDeobfuscatedName("parentField");

        final TopLevelClassMapping Child1 =
                mappings.getOrCreateTopLevelClassMapping("Child1");
        Child1.getOrCreateFieldMapping("a")
                .setDeobfuscatedName("childField");

        final TopLevelClassMapping Child2 =
                mappings.getOrCreateTopLevelClassMapping("Child2");

        return mappings;
    }

    /*
    This is representing the following:

    class Parent {
        int a;
    }
    class Child1 extends Parent {
        int a;
    }
    class Child2 extends Parent {
    }
     */
    private static InheritanceProvider inheritanceProvider() {
        return klass -> {
            switch (klass) {
                case "Parent": {
                    final Map<FieldSignature, InheritanceType> fields = Collections.singletonMap(
                            new FieldSignature("a", BaseType.INT), InheritanceType.PACKAGE_PRIVATE
                    );
                    final Map<String, InheritanceType> fieldsByName = Collections.singletonMap(
                            "a", InheritanceType.PACKAGE_PRIVATE
                    );

                    return Optional.of(new InheritanceProvider.ClassInfo.Impl(
                            "Parent", false,
                            "java/lang/Object", Collections.emptyList(),
                            fields, fieldsByName,
                            Collections.emptyMap()
                    ));
                }
                case "Child1": {
                    final Map<FieldSignature, InheritanceType> fields = Collections.singletonMap(
                            new FieldSignature("a", BaseType.INT), InheritanceType.PACKAGE_PRIVATE
                    );
                    final Map<String, InheritanceType> fieldsByName = Collections.singletonMap(
                            "a", InheritanceType.PACKAGE_PRIVATE
                    );

                    return Optional.of(new InheritanceProvider.ClassInfo.Impl(
                            "Child1", false,
                            "Parent", Collections.emptyList(),
                            fields, fieldsByName,
                            Collections.emptyMap()
                    ));
                }
                case "Child2": {
                    return Optional.of(new InheritanceProvider.ClassInfo.Impl(
                            "Child2", false,
                            "Parent", Collections.emptyList(),
                            Collections.emptyMap(), Collections.emptyMap(),
                            Collections.emptyMap()
                    ));
                }
            }

            return Optional.empty();
        };
    }

}

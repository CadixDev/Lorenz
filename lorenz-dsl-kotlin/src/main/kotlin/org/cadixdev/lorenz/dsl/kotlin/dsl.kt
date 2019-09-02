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

package org.cadixdev.lorenz.dsl.kotlin

import org.cadixdev.bombe.type.FieldType
import org.cadixdev.bombe.type.MethodDescriptor
import org.cadixdev.lorenz.MappingSet
import org.cadixdev.lorenz.model.ClassMapping
import org.cadixdev.lorenz.model.ExtensionKey
import org.cadixdev.lorenz.model.FieldMapping
import org.cadixdev.lorenz.model.InnerClassMapping
import org.cadixdev.lorenz.model.Mapping
import org.cadixdev.lorenz.model.MethodMapping
import org.cadixdev.lorenz.model.MethodParameterMapping
import org.cadixdev.lorenz.model.TopLevelClassMapping

/**
 * A DSL to simplify the creation of [MappingSet]s in Kotlin.
 *
 * @author Jamie Mansfield
 * @since 0.6.0
 */
class MappingSetDsl(val mappings: MappingSet) {

    companion object {
        fun create(init: MappingSetDsl.() -> Unit): MappingSet {
            val mappings = MappingSet.create()
            val dsl = MappingSetDsl(mappings)
            dsl.init()
            return mappings
        }
    }

    inline fun klass(name: String, init: ClassMappingDsl<TopLevelClassMapping, MappingSet>.() -> Unit): TopLevelClassMapping {
        val mapping = this.mappings.getOrCreateTopLevelClassMapping(name)
        val dsl = ClassMappingDsl(mapping)
        dsl.init()
        return mapping
    }

}

/**
 * A DSL to simplify the manipulation of [Mapping]s in Kotlin.
 *
 * @param T The type of the mapping
 * @param P The type of parent of the mapping
 * @author Jamie Mansfield
 * @since 0.6.0
 */
open class MappingDsl<T : Mapping<T, P>, P>(val mapping: T) {

    inline var deobf: String
        get() = this.mapping.deobfuscatedName
        set(value) {
            this.mapping.deobfuscatedName = value
        }

    fun <K> extension(key: ExtensionKey<K>, value: K) {
        this.mapping.set(key, value)
    }

}

/**
 * A DSL to simplify the manipulation of [ClassMapping]s in Kotlin.
 *
 * @param T The type of the class mapping
 * @param P The type of parent of the class mapping
 * @author Jamie Mansfield
 * @since 0.6.0
 */
class ClassMappingDsl<T : ClassMapping<T, P>, P>(mapping: T) : MappingDsl<T, P>(mapping) {

    inline fun field(name: String, type: FieldType?, init: MappingDsl<FieldMapping, ClassMapping<*, *>>.() -> Unit): FieldMapping {
        val mapping = this.mapping.getOrCreateFieldMapping(name, type)
        val dsl = MappingDsl(mapping)
        dsl.init()
        return mapping
    }

    inline fun field(name: String, type: String, init: MappingDsl<FieldMapping, ClassMapping<*, *>>.() -> Unit): FieldMapping {
        return this.field(name, FieldType.of(type), init)
    }

    inline fun field(name: String, init: MappingDsl<FieldMapping, ClassMapping<*, *>>.() -> Unit): FieldMapping {
        return this.field(name, null as FieldType?, init)
    }

    inline fun method(name: String, desc: MethodDescriptor, init: MethodMappingDsl.() -> Unit): MethodMapping {
        val mapping = this.mapping.getOrCreateMethodMapping(name, desc)
        val dsl = MethodMappingDsl(mapping)
        dsl.init()
        return mapping
    }

    inline fun method(name: String, desc: String, init: MethodMappingDsl.() -> Unit): MethodMapping {
        return this.method(name, MethodDescriptor.of(desc), init)
    }

    inline fun klass(name: String, init: ClassMappingDsl<InnerClassMapping, ClassMapping<*, *>>.() -> Unit): InnerClassMapping {
        val mapping = this.mapping.getOrCreateInnerClassMapping(name)
        val dsl = ClassMappingDsl(mapping)
        dsl.init()
        return mapping
    }

}

/**
 * A DSL to simplify the manipulation of [MethodMapping]s in Kotlin.
 *
 * @author Jamie Mansfield
 * @since 0.6.0
 */
class MethodMappingDsl(mapping: MethodMapping) : MappingDsl<MethodMapping, ClassMapping<*, *>>(mapping) {

    inline fun param(index: Int, init: MappingDsl<MethodParameterMapping, MethodMapping>.() -> Unit): MethodParameterMapping {
        val mapping = this.mapping.getOrCreateParameterMapping(index)
        val dsl = MappingDsl(mapping)
        dsl.init()
        return mapping
    }

}

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

package org.cadixdev.lorenz.io.gson;

import static me.jamiemansfield.gsonsimple.GsonObjects.getArray;
import static me.jamiemansfield.gsonsimple.GsonObjects.getObject;
import static me.jamiemansfield.gsonsimple.GsonObjects.getString;
import static me.jamiemansfield.gsonsimple.GsonRequirements.requireArray;
import static me.jamiemansfield.gsonsimple.GsonRequirements.requireObject;
import static me.jamiemansfield.gsonsimple.GsonRequirements.requireString;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import org.cadixdev.lorenz.MappingSet;
import org.cadixdev.lorenz.model.ClassMapping;
import org.cadixdev.lorenz.model.FieldMapping;
import org.cadixdev.lorenz.model.InnerClassMapping;
import org.cadixdev.lorenz.model.Mapping;
import org.cadixdev.lorenz.model.MethodMapping;
import org.cadixdev.lorenz.model.MethodParameterMapping;
import org.cadixdev.lorenz.model.TopLevelClassMapping;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * A GSON type adapter used for serialising and de-serialising {@link MappingSet}s.
 *
 * @author Jamie Mansfield
 * @since 0.6.0
 */
public class MappingSetTypeAdapter implements JsonSerializer<MappingSet>, JsonDeserializer<MappingSet> {

    // Common
    private static final String OBF = "obf";
    private static final String DEOBF = "deobf";

    // Classes
    private static final String FIELDS = "fields";
    private static final String METHODS = "methods";
    private static final String INNERS = "inners";

    // Fields
    private static final String TYPE = "type";

    // Methods
    private static final String DESCRIPTOR = "desc";
    private static final String PARAMS = "params";

    private final MappingSet mappings;

    public MappingSetTypeAdapter(final MappingSet mappings) {
        this.mappings = mappings;
    }

    @Override
    public MappingSet deserialize(final JsonElement json,
                                  final Type typeOfT,
                                  final JsonDeserializationContext context) throws JsonParseException {
        final JsonArray array = requireArray(json, "MappingSet");

        deserialiseClasses(array, this.mappings::getOrCreateTopLevelClassMapping);

        return this.mappings;
    }

    private static void deserialiseMapping(final JsonObject json, final Mapping<?, ?> mapping) {
        if (json.has(DEOBF)) {
            mapping.setDeobfuscatedName(getString(json, DEOBF));
        }
    }

    private static void deserialiseClass(final JsonObject json, final ClassMapping<?, ?> mapping) {
        deserialiseMapping(json, mapping);

        // Fields
        if (json.has(FIELDS)) {
            final JsonArray fields = getArray(json, FIELDS);
            deserialiseMappings(
                    fields,
                    obj -> {
                        final String obf = getString(obj, OBF);

                        if (obj.has(TYPE)) {
                            final String type = getString(obj, TYPE);
                            return mapping.getOrCreateFieldMapping(obf, type);
                        }
                        else {
                            return mapping.getOrCreateFieldMapping(obf);
                        }
                    },
                    // Fields don't have anything else, so not needed :)
                    MappingSetTypeAdapter::deserialiseMapping
            );
        }

        // Methods
        if (json.has(METHODS)) {
            final JsonArray methods = getArray(json, METHODS);
            deserialiseMethods(
                    methods,
                    obj -> mapping.getOrCreateMethodMapping(
                            getString(obj, OBF),
                            getString(obj, DESCRIPTOR)
                    )
            );
        }

        // Inner Classes
        if (json.has(INNERS)) {
            final JsonArray inners = getArray(json, INNERS);
            deserialiseClasses(inners, mapping::getOrCreateInnerClassMapping);
        }
    }

    private static void deserialiseMethod(final JsonObject json, final MethodMapping mapping) {
        deserialiseMapping(json, mapping);

        if (json.has(PARAMS)) {
            final JsonObject params = getObject(json, PARAMS);

            for (final Map.Entry<String, JsonElement> param : params.entrySet()) {
                final int index = Integer.parseInt(param.getKey());
                final String deobf = requireString(param.getValue(), "param deobf name");

                mapping.getOrCreateParameterMapping(index)
                        .setDeobfuscatedName(deobf);
            }
        }
    }

    private static <T extends Mapping<?, ?>> void deserialiseMappings(
            final JsonArray json,
            final Function<JsonObject, T> provider,
            final BiConsumer<JsonObject, T> deserialiser) {
        for (final JsonElement klass : json) {
            final JsonObject obj = requireObject(klass, "mapping");

            deserialiser.accept(obj, provider.apply(obj));
        }
    }

    private static void deserialiseClasses(
            final JsonArray json,
            final Function<String, ClassMapping<?, ?>> provider) {
        deserialiseMappings(
                json,
                obj -> provider.apply(getString(obj, OBF)),
                MappingSetTypeAdapter::deserialiseClass
        );
    }

    private static void deserialiseMethods(
            final JsonArray json,
            final Function<JsonObject, MethodMapping> provider) {
        deserialiseMappings(
                json,
                provider,
                MappingSetTypeAdapter::deserialiseMethod
        );
    }

    @Override
    public JsonElement serialize(final MappingSet src,
                                 final Type typeOfSrc,
                                 final JsonSerializationContext context) {
        final JsonArray array = new JsonArray();

        for (final TopLevelClassMapping srcKlass : src.getTopLevelClassMappings()) {
            array.add(serialiseClass(srcKlass));
        }

        return array;
    }

    private static JsonObject serialiseMapping(final Mapping<?, ?> mapping) {
        final JsonObject json = new JsonObject();
        json.addProperty(OBF, mapping.getObfuscatedName());
        json.addProperty(DEOBF, mapping.getDeobfuscatedName());
        return json;
    }

    private static JsonObject serialiseClass(final ClassMapping<?, ?> klass) {
        final JsonObject json = serialiseMapping(klass);

        // Fields
        final JsonArray fields = new JsonArray();
        for (final FieldMapping field : klass.getFieldMappings()) {
            fields.add(serialiseField(field));
        }
        json.add(FIELDS, fields);

        // Methods
        final JsonArray methods = new JsonArray();
        for (final MethodMapping method : klass.getMethodMappings()) {
            methods.add(serialiseMethod(method));
        }
        json.add(METHODS, methods);

        // Inner Classes
        final JsonArray inners = new JsonArray();
        for (final InnerClassMapping inner : klass.getInnerClassMappings()) {
            inners.add(serialiseClass(inner));
        }
        json.add(INNERS, inners);

        return json;
    }

    private static JsonObject serialiseField(final FieldMapping field) {
        final JsonObject json = serialiseMapping(field);
        field.getSignature().getType().ifPresent(type -> {
            json.addProperty(TYPE, type.toString());
        });
        return json;
    }

    private static JsonObject serialiseMethod(final MethodMapping method) {
        final JsonObject json = serialiseMapping(method);
        json.addProperty(DESCRIPTOR, method.getObfuscatedDescriptor());

        // Params
        final JsonObject params = new JsonObject();
        for (final MethodParameterMapping param : method.getParameterMappings()) {
            params.addProperty(param.getObfuscatedName(), param.getDeobfuscatedName());
        }
        json.add(PARAMS, params);

        return json;
    }

}

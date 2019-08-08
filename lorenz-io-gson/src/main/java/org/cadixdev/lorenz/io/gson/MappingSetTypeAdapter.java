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
        if (!json.isJsonArray()) throw new JsonParseException("MappingSet must be an array!");
        final JsonArray array = json.getAsJsonArray();

        deserialiseClasses(array, this.mappings::getOrCreateTopLevelClassMapping);

        return this.mappings;
    }

    private static void deserialiseMapping(final JsonObject json, final Mapping<?, ?> mapping) {
        if (json.has(DEOBF) && json.get(DEOBF).isJsonPrimitive()) {
            mapping.setDeobfuscatedName(json.get(DEOBF).getAsString());
        }
    }

    private static void deserialiseClass(final JsonObject json, final ClassMapping<?, ?> mapping) {
        deserialiseMapping(json, mapping);

        // Fields
        if (json.has(FIELDS)) {
            if (!json.get(FIELDS).isJsonArray()) throw new JsonParseException("Fields must be an array!");
            final JsonArray fields = json.getAsJsonArray(FIELDS);
            deserialiseMappings(
                    fields,
                    obj -> {
                        if (!obj.has(OBF) || !obj.get(OBF).isJsonPrimitive())
                            throw new JsonParseException("Method missing obfuscated name!");
                        final String obf = obj.get(OBF).getAsString();

                        if (obj.has(TYPE)) {
                            if (!obj.get(TYPE).isJsonPrimitive())
                                throw new JsonParseException("Field type must be a String!");
                            final String type = obj.get(TYPE).getAsString();

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
            if (!json.get(METHODS).isJsonArray()) throw new JsonParseException("Methods must be an array!");
            final JsonArray methods = json.getAsJsonArray(METHODS);
            deserialiseMethods(
                    methods,
                    obj -> {
                        if (!obj.has(OBF) || !obj.get(OBF).isJsonPrimitive())
                            throw new JsonParseException("Method missing obfuscated name!");
                        if (!obj.has(DESCRIPTOR) || !obj.get(DESCRIPTOR).isJsonPrimitive())
                            throw new JsonParseException("Method missing descriptor!");
                        return mapping.getOrCreateMethodMapping(
                                obj.get(OBF).getAsString(),
                                obj.get(DESCRIPTOR).getAsString()
                        );
                    }
            );
        }

        // Inner Classes
        if (json.has(INNERS)) {
            if (!json.get(INNERS).isJsonArray()) throw new JsonParseException("Inner classes must be an array!");
            final JsonArray inners = json.getAsJsonArray(INNERS);
            deserialiseClasses(inners, mapping::getOrCreateInnerClassMapping);
        }
    }

    private static void deserialiseMethod(final JsonObject json, final MethodMapping mapping) {
        deserialiseMapping(json, mapping);

        if (json.has(PARAMS)) {
            if (!json.get(PARAMS).isJsonObject()) throw new JsonParseException("Params must be an object!");
            final JsonObject params = json.get(PARAMS).getAsJsonObject();

            for (final Map.Entry<String, JsonElement> param : params.entrySet()) {
                final int index = Integer.parseInt(param.getKey());
                if (!param.getValue().isJsonPrimitive())
                    throw new JsonParseException("Param deobf name must be a String!");
                final String deobf = param.getValue().getAsString();

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
            if (!klass.isJsonObject()) throw new JsonParseException("Mapping must be an object!");
            final JsonObject obj = klass.getAsJsonObject();

            deserialiser.accept(obj, provider.apply(obj));
        }
    }

    private static void deserialiseClasses(
            final JsonArray json,
            final Function<String, ClassMapping<?, ?>> provider) {
        deserialiseMappings(
                json,
                obj -> {
                    if (!obj.has(OBF) || !obj.get(OBF).isJsonPrimitive())
                        throw new JsonParseException("Class missing obfuscated name!");
                    return provider.apply(obj.get(OBF).getAsString());
                },
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

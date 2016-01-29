/*
 * This file is part of Lorenz, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2016, Lexteam <http://www.lexteam.xyz/>
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
package xyz.lexteam.lorenz.model;

import xyz.lexteam.lorenz.Mappings;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a class mapping.
 */
public class ClassMapping extends BaseMapping {

    private final Map<String, FieldMapping> fieldMappings = new HashMap<>();
    private final Map<String, MethodMapping> methodMappings = new HashMap<>();
    private final Mappings parent;

    public ClassMapping(Mappings parent, String obfuscated, String deobfuscated) {
        super(obfuscated, deobfuscated);
        this.parent = parent;
    }

    public Map<String, FieldMapping> getFieldMappings() {
        return this.fieldMappings;
    }

    public Map<String, MethodMapping> getMethodMappings() {
        return this.methodMappings;
    }

    public void addFieldMapping(FieldMapping mapping) {
        this.fieldMappings.put(mapping.getObfuscatedName(), mapping);
    }

    public void removeFieldMapping(String name) {
        this.fieldMappings.remove(name);
    }

    public void addMethodMapping(MethodMapping mapping) {
        this.methodMappings.put(mapping.getObfuscatedName(), mapping);
    }

    public void removeMethodMapping(String name) {
        this.methodMappings.remove(name);
    }

    public Mappings getParent() {
        return this.parent;
    }
}

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

package org.cadixdev.lorenz.merge;

import java.util.Objects;

/**
 * Configuration object for {@link MappingSetMerger}. Create new instances via {@link #builder()}.
 *
 * @author Kyle Wood
 * @since 0.5.4
 */
public final class MergeConfig {

    private final MappingSetMergerHandler handler;

    private final MethodMergeStrategy methodMergeStrategy;
    private final FieldMergeStrategy fieldMergeStrategy;

    MergeConfig(final MappingSetMergerHandler handler, final MethodMergeStrategy methodMergeStrategy, final FieldMergeStrategy fieldMergeStrategy) {
        this.handler = Objects.requireNonNull(handler, "handler must not be null");
        this.methodMergeStrategy = Objects.requireNonNull(methodMergeStrategy, "methodMergeStrategy must not be null");
        this.fieldMergeStrategy = Objects.requireNonNull(fieldMergeStrategy, "fieldMergeStrategy must not be null");
    }

    /**
     * The merge handler for the merge session. Defaults to the default implementation when not specified.
     *
     * @return The merge handler to use for the merge operation. Never {@code null}.
     * @see MappingSetMergerHandler
     * @since 0.5.4
     */
    public MappingSetMergerHandler getHandler() {
        return this.handler;
    }

    /**
     * The merge strategy to use for merging method mappings for the merge session. Defaults to {@link MethodMergeStrategy#STRICT} when not specified.
     *
     * @return The merge strategy to use for merging method mappings. Never {@code null}.
     * @since 0.5.4
     */
    public MethodMergeStrategy getMethodMergeStrategy() {
        return this.methodMergeStrategy;
    }

    /**
     * The merge strategy to use for merging field mappings for the merge session. Defaults to {@link FieldMergeStrategy#LOOSE} when not specified.
     *
     * @return The merge strategy to use for merging field mappings. Never {@code null}.
     * @since 0.5.4
     */
    public FieldMergeStrategy getFieldMergeStrategy() {
        return this.fieldMergeStrategy;
    }

    /**
     * Create a new {@link Builder} to create new instances of {@link MergeConfig}.
     *
     * @return A new {@link Builder} instance, never {@code null}.
     * @since 0.5.4
     */
    public static Builder builder() {
        return new Builder();
    }

    @Override
    public String toString() {
        return "MergeConfig{" +
            "handler=" + this.handler +
            ", methodMergeStrategy=" + this.methodMergeStrategy +
            '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        final MergeConfig config = (MergeConfig) o;
        return Objects.equals(this.handler, config.handler) &&
            this.methodMergeStrategy == config.methodMergeStrategy;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.handler, this.methodMergeStrategy);
    }

    /**
     * This builder class creates instances for {@link MergeConfig}.
     * @see MergeConfig
     * @since 0.5.4
     */
    public static final class Builder {

        private MappingSetMergerHandler handler = new MappingSetMergerHandler() {};

        private MethodMergeStrategy methodMergeStrategy = MethodMergeStrategy.STRICT;

        private FieldMergeStrategy fieldMergeStrategy = FieldMergeStrategy.LOOSE;

        Builder() {}

        /**
         * Set the {@link MappingSetMergerHandler handler} for the merge session. Defaults to the default implementation.
         *
         * @param handler The merge handler to use. Must not be {@code null}.
         * @return {@code this} instance for chaining.
         * @see MergeConfig#getHandler()
         * @since 0.5.4
         */
        public Builder withMergeHandler(final MappingSetMergerHandler handler) {
            this.handler = Objects.requireNonNull(handler);
            return this;
        }

        /**
         * Set the {@link MethodMergeStrategy} for merging method mappings for the merge session. Defaults to {@link MethodMergeStrategy#STRICT}.
         *
         * @param methodMergeStrategy The method merge strategy to use. Must not be {@code null}.
         * @return {@code this} instance for chaining.
         * @see MergeConfig#getMethodMergeStrategy()
         * @since 0.5.4
         */
        public Builder withMethodMergeStrategy(final MethodMergeStrategy methodMergeStrategy) {
            this.methodMergeStrategy = Objects.requireNonNull(methodMergeStrategy);
            return this;
        }

        /**
         * Set the {@link FieldMergeStrategy} for merging method mappings for the merge session. Defaults to {@link FieldMergeStrategy#LOOSE}.
         *
         * @param fieldMergeStrategy The field merge strategy to use. Must not be {@code null}.
         * @return {@code this} instance for chaining.
         * @see MergeConfig#getFieldMergeStrategy()
         * @since 0.5.4
         */
        public Builder withFieldMergeStrategy(final FieldMergeStrategy fieldMergeStrategy) {
            this.fieldMergeStrategy = Objects.requireNonNull(fieldMergeStrategy);
            return this;
        }

        /**
         * Create the {@link MergeConfig} from this object.
         *
         * @return The merge config created from this builder. Never {@code null}.
         * @since 0.5.4
         */
        public MergeConfig build() {
            return new MergeConfig(this.handler, this.methodMergeStrategy, this.fieldMergeStrategy);
        }
    }
}

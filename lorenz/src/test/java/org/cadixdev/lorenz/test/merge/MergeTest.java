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

package org.cadixdev.lorenz.test.merge;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.cadixdev.lorenz.MappingSet;
import org.cadixdev.lorenz.io.MappingFormats;
import org.cadixdev.lorenz.merge.FieldMergeStrategy;
import org.cadixdev.lorenz.merge.MappingSetMerger;
import org.cadixdev.lorenz.merge.MappingSetMergerHandler;
import org.cadixdev.lorenz.merge.MergeConfig;
import org.cadixdev.lorenz.merge.MergeContext;
import org.cadixdev.lorenz.merge.MergeResult;
import org.cadixdev.lorenz.merge.MethodMergeStrategy;
import org.cadixdev.lorenz.model.ClassMapping;
import org.cadixdev.lorenz.model.InnerClassMapping;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

public final class MergeTest {

    @Test
    public void standardMergeTest() throws IOException {
        testCase(buildString(
            "foo/bar/A foo/bar/B",
            "\ta b"
        ), buildString(
            "foo/bar/B foo/bar/C",
            "\tb c"
        ), buildString(
            "foo/bar/A foo/bar/C",
            "\ta c"
        ));
    }

    @Test
    public void missingEntriesTest() throws IOException {
        testCase(buildString(
            "foo/bar/A foo/bar/B",
            "\ta b"
        ), buildString(
            "foo/bar/B foo/bar/C",
            "\ta1 c1"
        ), buildString(
            "foo/bar/A foo/bar/C",
            "\ta b",
            "\ta1 c1"
        ));
    }

    @Test
    public void typesThenMembersTest() throws IOException {
        testCase(buildString(
            "A foo/bar/B",
            "C bar/baz/D",
            "E foo/bar/F"
        ), buildString(
            "foo/bar/B foo/bar/B",
            "\ta (ILbar/baz/D;Lfoo/bar/F;J)V new_a"
        ), buildString(
            "A foo/bar/B",
            "\ta (ILC;LE;J)V new_a",
            "C bar/baz/D",
            "E foo/bar/F"
        ));
    }

    @Test
    public void duplicateMembersTest() throws IOException {
        testCase(buildString(
            "A foo/bar/B",
            "\ta b"
        ), buildString(
            "foo/bar/B foo/bar/C",
            "\ta c"
        ), buildString(
            "A foo/bar/C",
            "\ta c"
        ));
    }

    @Test
    public void overrideHandlerTest() throws IOException {
        testCase(buildString(
            "A foo/bar/B",
            "A$a foo/bar/B$b",
            "\ta b"
        ), buildString(
            "foo/bar/B$b foo/bar/B$b",
            "\tb c"
        ), buildString(
            "A foo/bar/B",
            "A$a foo/bar/B$a",
            "\ta c"
        ), MergeConfig.builder()
            .withMergeHandler(new MappingSetMergerHandler() {
                @Override
                public MergeResult<InnerClassMapping> mergeInnerClassMappings(
                    final InnerClassMapping left,
                    final InnerClassMapping right,
                    final ClassMapping<?, ?> target,
                    final MergeContext context
                ) {
                    return new MergeResult<>(target.createInnerClassMapping(left.getObfuscatedName(), left.getObfuscatedName()), right);
                }
            })
            .build());
    }

    @Test
    public void combinedCaseStrict() throws IOException {
        testCase(buildString(
            "A B",
            "A$1 B$1",
            "\ta ()LA; a1",
            "A$1$a B$1$b",
            "\tb (LA$1;)V b1"
        ), buildString(
            "B B",
            "\tb c",
            "\tb (III)V c",
            "B$1 B$2",
            "\ta1 ()LB; a2",
            "B$1$b B$2$c",
            "\tb1 (LA$1;)V b2"
        ), buildString(
            "A B",
            "\tb c",
            "\tb (III)V c",
            "A$1 B$2",
            "\ta ()LA; a2",
            "A$1$a B$2$c",
            "\tb (LA$1;)V b1", // Strict merge strategy can't handle this case
            "\tb1 (LA$1;)V b2"
        ), MergeConfig.builder()
            .withFieldMergeStrategy(FieldMergeStrategy.STRICT)
            .withMethodMergeStrategy(MethodMergeStrategy.STRICT)
            .build());
    }

    @Test
    public void combinedCaseLoose() throws IOException {
        testCase(buildString(
            "A B",
            "A$1 B$1",
            "\ta ()LA; a1",
            "A$1$a B$1$b",
            "\tb (LA$1;)V b1"
        ), buildString(
            "B B",
            "\tb c",
            "\tb (III)V c",
            "B$1 B$2",
            "\ta1 ()LB; a2",
            "B$1$b B$2$c",
            "\tb1 (LA$1;)V b2"
        ), buildString(
            "A B",
            "\tb c",
            "\tb (III)V c",
            "A$1 B$2",
            "\ta ()LA; a2",
            "A$1$a B$2$c",
            "\tb (LA$1;)V b2"
        ), MergeConfig.builder()
            .withFieldMergeStrategy(FieldMergeStrategy.LOOSE)
            .withMethodMergeStrategy(MethodMergeStrategy.LOOSE)
            .build());
    }

    @RepeatedTest(value = 5, name = "parallelMergeTest with " + RepeatedTest.CURRENT_REPETITION_PLACEHOLDER + " threads")
    public void parallelMergeTest(final RepetitionInfo info) throws IOException {
        System.out.println(info.getCurrentRepetition());
        testCase(buildString(
            "A foo/bar/B",
            "C bar/baz/D",
            "E foo/bar/F"
        ), buildString(
            "foo/bar/B foo/bar/B",
            "\ta (ILbar/baz/D;Lfoo/bar/F;J)V new_a"
        ), buildString(
            "A foo/bar/B",
            "\ta (ILC;LE;J)V new_a",
            "C bar/baz/D",
            "E foo/bar/F"
        ), MergeConfig.builder()
            .withParallelism(info.getCurrentRepetition())
            .build());
    }

    private static void testCase(final String left, final String right, final String result) throws IOException {
        testCase(left, right, result, MergeConfig.builder().build());
    }

    private static void testCase(final String left, final String right, final String result, final MergeConfig config) throws IOException {
        final MappingSet leftMappings = MappingFormats.TSRG.createReader(new StringReader(left)).read();
        final MappingSet rightMappings = MappingFormats.TSRG.createReader(new StringReader(right)).read();
        final MappingSet outputMappings = MappingFormats.TSRG.createReader(new StringReader(result)).read();

        // MappingSets aren't comparable, not directly
        // Instead we write them out to files to compare that way
        // We read the result and then immediately write it as a way of normalizing the input
        //
        // Doing it this way also makes reading the diff on failure a lot easier to see the output
        final StringWriter merged = new StringWriter();
        final StringWriter expected = new StringWriter();
        MappingFormats.TSRG.createWriter(merged).write(MappingSetMerger.create(leftMappings, rightMappings, config).merge());
        MappingFormats.TSRG.createWriter(expected).write(outputMappings);

        assertEquals(expected.toString(), merged.toString());
    }

    private static String buildString(final String... lines) {
        final StringBuilder sb = new StringBuilder();
        for (final String line : lines) {
            sb.append(line);
            sb.append("\n");
        }
        return sb.toString();
    }
}

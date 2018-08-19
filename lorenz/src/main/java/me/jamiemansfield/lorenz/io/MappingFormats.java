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

package me.jamiemansfield.lorenz.io;

import me.jamiemansfield.lorenz.io.enigma.EnigmaMappingFormat;
import me.jamiemansfield.lorenz.io.jam.JamMappingFormat;
import me.jamiemansfield.lorenz.io.kin.KinMappingFormat;
import me.jamiemansfield.lorenz.io.srg.csrg.CSrgMappingFormat;
import me.jamiemansfield.lorenz.io.srg.SrgMappingFormat;
import me.jamiemansfield.lorenz.io.srg.tsrg.TSrgMappingFormat;

/**
 * A psuedo-enum of the mapping formats implemented within Lorenz.
 *
 * @author Jamie Mansfield
 * @since 0.4.0
 */
public final class MappingFormats {

    /**
     * The SRG mapping format.
     */
    public static final MappingFormat SRG = new SrgMappingFormat();

    /**
     * The CSRG (compact SRG) mapping format.
     */
    public static final MappingFormat CSRG = new CSrgMappingFormat();

    /**
     * The TSRG (tiny SRG) mapping format.
     */
    public static final MappingFormat TSRG = new TSrgMappingFormat();

    /**
     * The Kin (/k/ashike b/in/ary) mapping format.
     */
    public static final MappingFormat KIN = new KinMappingFormat();

    /**
     * The JAM (Java Associated Mappings) mapping format.
     */
    public static final MappingFormat JAM = new JamMappingFormat();

    /**
     * The Enigma mapping format.
     */
    public static final MappingFormat ENIGMA = new EnigmaMappingFormat();

    private MappingFormats() {
    }

}

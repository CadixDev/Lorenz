Lorenz
======

Lorenz is a library intended for creating and altering de-obfuscation mappings for Java
programs (compiled or otherwise), this is done independent of the format being used. Lorenz
supports a variety of mapping formats:

- SRG and its variants (CSRG, TSRG, and XSRG)
- Enigma (through `lorenz-io-enigma`)
- JAM (through `lorenz-io-jam`)
- ProGuard (**reader only** through `lorenz-io-proguard`)

## Branches

Lorenz makes use of the [git-flow] branching structure, briefly put:

- **master** is the source for the latest released version
- **develop** is the source of the latest developments

All releases will also be tagged, with further descriptions in their GitHub release.
These descriptions will include information such as migration advice.

## Usage

Lorenz is centred around the `MappingSet`, the root container of mappings. A provided
implementation can be constructed through `MappingSet.create()`.

Lorenz releases can be obtained through Maven Central:

### Gradle

```groovy
implementation 'org.cadixdev:lorenz:0.5.7'
```

### Maven

```xml
<dependency>
    <groupId>org.cadixdev</groupId>
    <artifactId>lorenz</artifactId>
    <version>0.5.7</version>
</dependency>
```

You may also find snapshot artifacts on [Sonatype's OSS repository].

Versions prior to `0.5.0` were made under the `me.jamiemansfield` group, and initial
snapshot releases were made to my personal Maven  (`repo.jamiemansfield.me`).

## License

Lorenz is licensed under the MIT License, this was chosen for its permissive nature -
giving developers the freedom to do as they please with it, with no assurances from myself.

```
The MIT License (MIT)

Copyright (c) Jamie Mansfield <https://www.jamierocks.uk/>
Copyright (c) contributors

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
```

## Discuss

**Found an issue with Lorenz?** [Make an issue]! We'd rather close invalid
reports than have bugs go unreported :)

We have an IRC channel on [EsperNet], `#cadix`, which is available for all
[registered](https://esper.net/getting_started.php#registration) users to join
and discuss Lorenz and other Cadix projects.

[git-flow]: https://nvie.com/posts/a-successful-git-branching-model/
[Sonatype's OSS repository]: https://oss.sonatype.org/content/repositories/snapshots/
[Make an issue]: https://github.com/CadixDev/Lorenz/issues/new
[EsperNet]: https://esper.net/

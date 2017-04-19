Lorenz
======

Lorenz is a library for interacting with Java deobfuscation mappings, within the Java
programming language. Licensed MIT.

## Usage

Lorenz is available through my Maven repository (repo.jamiemansfield.me).

```gradle
repositories {
    mavenCentral()
    maven {
        name = 'jamiemansfield'
        url = 'https://repo.jamiemansfield.me/'
    }
}

dependencies {
    compile 'me.jamiemansfield:lorenz:0.0.1-SNAPSHOT'
}
```

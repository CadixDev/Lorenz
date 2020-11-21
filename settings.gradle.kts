val projectName: String by settings
rootProject.name = projectName

include(
    "lorenz",
    "lorenz-asm",
    "lorenz-io-enigma",
    "lorenz-io-jam",
    "lorenz-io-kin",
    "lorenz-io-proguard"
)

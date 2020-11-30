val projectName: String by settings
rootProject.name = projectName

include(
    "lorenz",
    "lorenz-dsl-groovy",
    "lorenz-io-enigma",
    "lorenz-io-gson",
    "lorenz-io-jam",
    "lorenz-io-kin",
    "lorenz-io-proguard"
)

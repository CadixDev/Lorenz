val projectName: String by settings
rootProject.name = projectName

include(
    "lorenz",
    "lorenz-dsl-groovy",
    "lorenz-io-enigma",
    "lorenz-io-jam",
    "lorenz-io-proguard"
)

fastlane documentation
----

# Installation

Make sure you have the latest version of the Xcode command line tools installed:

```sh
xcode-select --install
```

For _fastlane_ installation instructions, see [Installing _fastlane_](https://docs.fastlane.tools/#installing-fastlane)

# Available Actions

## Android

### android test

```sh
[bundle exec] fastlane android test
```

Runs all the tests

### android buildDebug

```sh
[bundle exec] fastlane android buildDebug
```

Build Debug App

### android buildRelease

```sh
[bundle exec] fastlane android buildRelease
```

Build Release App

### android buildBundle

```sh
[bundle exec] fastlane android buildBundle
```

Build Bundle Release App

### android increment_version

```sh
[bundle exec] fastlane android increment_version
```



### android deployBeta

```sh
[bundle exec] fastlane android deployBeta
```

Deploy latest version Beta Test to Google Play

### android deployRelease

```sh
[bundle exec] fastlane android deployRelease
```

Deploy latest version to Google Play

### android deployFirebaseBeta

```sh
[bundle exec] fastlane android deployFirebaseBeta
```

Deploy latest version Beta Test to Google Play

### android update_version_name

```sh
[bundle exec] fastlane android update_version_name
```

Update version name in build.gradle

### android generate_release_notes

```sh
[bundle exec] fastlane android generate_release_notes
```

Generate release notes from commits

### android deployFirebase

```sh
[bundle exec] fastlane android deployFirebase
```

Deploy a new version to Firebase

----

This README.md is auto-generated and will be re-generated every time [_fastlane_](https://fastlane.tools) is run.

More information about _fastlane_ can be found on [fastlane.tools](https://fastlane.tools).

The documentation of _fastlane_ can be found on [docs.fastlane.tools](https://docs.fastlane.tools).

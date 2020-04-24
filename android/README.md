# DevBricks X - Android
[![License](https://poser.pugx.org/dreamfactory/dreamfactory/license.svg)](http://www.apache.org/licenses/LICENSE-2.0) [![API](https://img.shields.io/badge/API-19%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=19)

## Quick Setup
To use **DevBricks X** Libraries in your application, follow steps below.

### Step 1: Installation
Libraries are distributed via jcenter. Add the following dependencies in build.gradle of your application.

```groovy
repositories { 
	jcenter()
}
   
dependencies {
	// Basic Library for Android development
	implementation "com.dailystudio:devbricksx:0.5.1"

	// Annotations and processors to generate low-level facilities, such as Dao, Database, etc.
	implementation "com.dailystudio:devbricksx-java-annotations:0.5.1"
	kapt "com.dailystudio:devbricksx-java-compiler:0.5.1"

	// Annotations and processors to generate high-level utils, such ViewModel, Fragment, etc.
	implementation "com.dailystudio:devbricksx-kotlin-annotations:0.5.1"
	kapt "com.dailystudio:devbricksx-kotlin-compiler:0.5.1"
}
```

## **PM2**

### Description
PM2 is the most advanced Poker Analytics Software available for Android Tablets.

Implemented in Scala and Java with the Android SDK (API19) and the NDK using C.

UI is handled by Scala/Java and the back engine in C implements the equity analysis algorithms as well as the Monte-Carlo
predictive implementation.

NDK code generated for various binary platforms and using LLVM clang3.4-obfuscator for security - NDK build files are supplied.
Security is also provided across the JNI barrier using basic encryption.

Tested on a wide variety of screen sizes/densities and should run on any API14+ (4.0 ICS) tablet of at least 6".

### Bugs:
Some UI issues relating to unknown devices. Was tested on Xoom 10, Nexus 7 2013, Amazon Kindle Fire HD and various GenyMotion implementations running ICS through KK.

### Versions:
Versions: final val VERSION:String = "Pro" can be changed in PrimaryActivity to switch versions to a "lite" version.

### TODO:
Needs a huge cleanup of the Scala/Java to follow common OOP idioms. Likely subtype polymorphism and coding to
interfaces would be the first major change coupled with proper variable scoping both in class and methods. Enums as well would make things clearer. Fix the leaky binder...


### Libraries used:

- aChartEngine-1.2.0 for charting
- bcprov-jdk15on-150 for bouncy castle crypto
- commons-io 2.4 for various stuff
- libGoogleAnalyticsServices (legacy analytics services)
- gson-2.2-4
- v4supportlib

- android SDK API19
- scala-sdk-2.9.2
- gcc and various C libraries

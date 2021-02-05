-keep public class * implements com.zl.weilu.saber.api.UnBinder{public <init>(**);}

-keep class com.zl.weilu.saber.** {*;}
-keepclasseswithmembernames class * { @com.zl.weilu.saber.annotation.* <methods>; }
-keepclasseswithmembernames class * { @com.zl.weilu.saber.annotation.* <fields>; }

-dontwarn com.jeremyliao.liveeventbus.**
-keep class com.jeremyliao.liveeventbus.** { *; }
-keep class android.arch.lifecycle.** { *; }
-keep class android.arch.core.** { *; }
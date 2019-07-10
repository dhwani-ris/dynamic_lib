##--------------- NOT REQUIRED--------------##
# Butterknife packages it's proguard rules with the library. Can be found at https://github.com/JakeWharton/butterknife/blob/master/butterknife/proguard-rules.txt


# Retain generated class which implement Unbinder.

#-keep public class * implements butterknife.Unbinder { public <init>(**, android.view.View); }


# Prevent obfuscation of types which use ButterKnife annotations since the simple name
# is used to reflectively look up the generated ViewBinding.

#-keep class butterknife.*
#-keepclasseswithmembernames class * { @butterknife.* <methods>; }
#-keepclasseswithmembernames class * { @butterknife.* <fields>; }
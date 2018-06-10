# DevTools

![LOGO_DevTools](./image/logo.PNG)

Store your development environment.<br>
Tested only in Windows10 64-bit environment.

# How to Run

Download the zip file at ./DevTools.zip

Execute the 'run.bat' file

# Setup for Development

Download Mysql Connector from https://dev.mysql.com/downloads/connector/j/ <br>
Download Jansi from http://fusesource.github.io/jansi/download.html

Include mysql-connector-java-8.0.11.jar and jansi-1.17.1.jar in your project class path (Add CLASSPATH variable as your environment variable for running with cmd)

Compile source code and run (packaging must be done if compiled using javac - include the src directory in your CLASSPATH or use the java -cp command)

Enter this command to compile and run DevTools for Test.

```aidl
cd C:\~\DevTools\src

//If you need to delete class file in src
del *.class

javac *.java -encoding UTF8
java Main
```

# Issue

* Password masking does not work on Eclipse/Intellij (must run manually in CMD)
* English is supported by default. Korean(Hangul) may cause problems.
* The representation of Unicode in CMD(or PowerShell) is different in Windows7 and Windows10. (In case of Windows7, Replace '─' to '-' in TableList.class)
* For readability, it may be helpful to run DevTools after settings the text color to White.<br>
In case of windows CMD, Enter this command.
```
Color F
```


# Open source

* Jansi (http://fusesource.github.io/jansi)
* TableList (https://github.com/therealfarfetchd/crogamp/blob/master/src/com/github/mrebhan/crogamp/cli/TableList.java)

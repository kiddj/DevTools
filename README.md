# DevTools

![LOGO_DevTools](./image/logo.PNG)

Store your development environment.<br>
Tested only in Windows10 64-bit environment.

# Setup

Download Mysql Connector from https://dev.mysql.com/downloads/connector/j/ <br>
Download Jansi from http://fusesource.github.io/jansi/download.html

Include mysql-connector-java-8.0.11.jar and jansi-1.17.1.jar in your project class path (Add CLASSPATH variable as your environment variable for running with cmd)

Compile source code and run (packaging must be done if compiled using javac - include the src directory in your CLASSPATH)


# Issue

* Password masking does not work on Eclipse/Intellij (must run manually in CMD)
* English is supported by default. Korean(Hangul) may cause problems.
* The representation of Unicode in CMD(or PowerShell) is different in Windows7 and Windows10. (In case of Windows7, Replace '─' to '-' in TableList.class)

# Open source

* Jansi (http://fusesource.github.io/jansi)
* TableList (https://github.com/therealfarfetchd/crogamp/blob/master/src/com/github/mrebhan/crogamp/cli/TableList.java)

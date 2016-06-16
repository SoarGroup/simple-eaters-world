# Notes

Once downloaded, move to a path that does not contain spaces and is short (e.g. `c:\temp\tutorial` or `/home/user/tutorial`).

The only required software is [Oracle Java 8](http://www.oracle.com/technetwork/java/javase/downloads/index-jsp-138363.html). The software should "just work" on Windows, Mac OS, and Ubuntu (>= 14). Some platform-specific notes:

## Windows

* To run: double-click `debugger.bat` or `eater.bat`
* Depending on existing applications, you may need to download and install the [Visual C++ Redistributable](https://www.microsoft.com/en-us/download/details.aspx?id=48145) for Visual Studio 2015
* Spaces or very long paths may cause the software not to run

## Mac OS

* To run: open the `Terminal` application, run `path/to/tutorial/eater.sh` or `debugger.sh` (you can also right-click, Open With, Other, select Terminal from Applications/Utilities)


## Ubuntu Linux

* To run: open the `Terminal` application, run `path/to/tutorial/eater.sh` or `debugger.sh`
* To install the Oracle JDK, execute the following commands in the terminal:
```
sudo add-apt-repository ppa:webupd8team/java
sudo apt-get update
sudo apt-get install oracle-java8-installer
```

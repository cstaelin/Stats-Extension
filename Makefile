# If this Makefile is not being run from the NetLogo-6.x.x/app/extensions/pathdir
# directory, NETLOGO must be set before the make to the location of the NetLogo
# package.  E.g., in Cygwin this would be something like 
# export NETLOGO = "c:/Program Files/NetLogo 6.0.1"

ifeq ($(origin JAVA_HOME), undefined)
	JAVA_HOME = /usr
endif

ifneq (,$(findstring CYGWIN,$(shell uname -s)))
  COLON=\;
#  JAVA_HOME := `cygpath -up "$(JAVA_HOME)"`
else
  COLON=:
endif

ifeq ($(origin NETLOGO), undefined)
	NETLOGO = ../../..
endif

# NetLogo.jar files are now modefied by the NetLogo version number,
# e.g., netlogo-6.0.1.jar. Thus we shell to the "find" command which 
# looks in the app forlder to get the .jar file name. I tried to 
# accomplish this with the wildcard function, but it doesn't seem to 
# handle cases where there are blanks in file/direcotry names, as is 
# common in Windows.
NETLOGO_JAR := "$(shell find "$(NETLOGO)"/app -name netlogo-*.jar)"
#NETLOGO_JAR := $(wildcard $(NETLOGO)/app/netlogo-*.jar)
JAVAC := "$(JAVA_HOME)/bin/javac"
SRCS := $(wildcard src/*.java)

StatsExtension.zip: stats.jar stats.jar.pack.gz Jama-1.0.3.jar Jama-1.0.3.jar.pack.gz colt.jar colt.jar.pack.gz README.md license.md Makefile src manifest.txt StatsExtension-v2.0.pdf StatsExample.nlogo
	rm -rf stats
	mkdir stats
	cp -rp stats.jar stats.jar.pack.gz Jama-1.0.3.jar Jama-1.0.3.jar.pack.gz colt.jar colt.jar.pack.gz README.md license.md Makefile src manifest.txt StatsExtension-v2.0.pdf StatsExample.nlogo stats
	zip -rv StatsExtension.zip stats
	rm -rf stats

stats.jar stats.jar.pack.gz: $(SRCS) Jama-1.0.3.jar Jama-1.0.3.jar.pack.gz colt.jar colt.jar.pack.gz Makefile manifest.txt
	rm -rf classes
	mkdir -p classes
	$(JAVAC) -g -deprecation -Xlint:all -Xlint:-serial -Xlint:-path -encoding us-ascii -source 1.8 -target 1.8 -classpath $(NETLOGO_JAR)$(COLON)Jama-1.0.3.jar$(COLON)colt.jar -d classes $(SRCS)
#	$(JAVAC) -g -deprecation -Xlint:all -Xlint:-serial -Xlint:-path -encoding us-ascii -source 1.8 -target 1.8 -classpath $(NETLOGO_JAR)\;Jama-1.0.3.jar -d classes $(SRCS)
	jar cmf manifest.txt stats.jar -C classes .
	pack200 --modification-time=latest --effort=9 --strip-debug --no-keep-file-order --unknown-attribute=strip stats.jar.pack.gz stats.jar
	rm -rf classes

Jama-1.0.3.jar Jama-1.0.3.jar.pack.gz:
	curl -f -s -S 'http://math.nist.gov/javanumerics/jama/Jama-1.0.3.jar' -o Jama-1.0.3.jar
	pack200 --modification-time=latest --effort=9 --strip-debug --no-keep-file-order --unknown-attribute=strip Jama-1.0.3.jar.pack.gz Jama-1.0.3.jar
	
colt.jar.pack.gz:
	pack200 --modification-time=latest --effort=9 --strip-debug --no-keep-file-order --unknown-attribute=strip colt.jar.pack.gz colt.jar


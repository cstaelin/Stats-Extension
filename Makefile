ifeq ($(origin JAVA_HOME), undefined)
  JAVA_HOME=/usr
endif

ifneq (,$(findstring CYGWIN,$(shell uname -s)))
  COLON=\;
  JAVA_HOME := `cygpath -up "$(JAVA_HOME)"`
else
  COLON=:
endif

ifeq ($(origin NETLOGO), undefined)
  NETLOGO=../..
endif

JAVAC=$(JAVA_HOME)/bin/javac

SRCS=$(wildcard src/*.java)

StatsExtension-v1.4.0.zip: stats.jar stats.jar.pack.gz Jama-1.0.3.jar Jama-1.0.3.jar.pack.gz colt.jar colt.jar.pack.gz README.md license.md Makefile src manifest.txt StatsExtension-v1.4.0.pdf StatsExample.nlogo
	rm -rf stats
	mkdir stats
	cp -rp stats.jar stats.jar.pack.gz Jama-1.0.3.jar Jama-1.0.3.jar.pack.gz colt.jar colt.jar.pack.gz README.md license.md Makefile src manifest.txt StatsExtension-v1.4.0.pdf StatsExample.nlogo stats
	zip -rv StatsExtension-v1.4.0.zip stats
	rm -rf stats

stats.jar stats.jar.pack.gz: $(SRCS) Jama-1.0.3.jar Jama-1.0.3.jar.pack.gz colt.jar colt.jar.pack.gz Makefile manifest.txt
	mkdir -p classes
	$(JAVAC) -g -encoding us-ascii -source 1.6 -target 1.6 -classpath $(NETLOGO)/NetLogoLite.jar$(COLON)Jama-1.0.3.jar$(COLON)colt.jar -d classes $(SRCS)
	jar cmf manifest.txt stats.jar -C classes .
	pack200 --modification-time=latest --effort=9 --strip-debug --no-keep-file-order --unknown-attribute=strip stats.jar.pack.gz stats.jar

Jama-1.0.3.jar Jama-1.0.3.jar.pack.gz:
	curl -f -s -S 'http://math.nist.gov/javanumerics/jama/Jama-1.0.3.jar' -o Jama-1.0.3.jar
	pack200 --modification-time=latest --effort=9 --strip-debug --no-keep-file-order --unknown-attribute=strip Jama-1.0.3.jar.pack.gz Jama-1.0.3.jar
	
colt.jar.pack.gz:
	pack200 --modification-time=latest --effort=9 --strip-debug --no-keep-file-order --unknown-attribute=strip colt.jar.pack.gz colt.jar


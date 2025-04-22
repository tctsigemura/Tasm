all:
	mkdir -p bin
	(cd src; make)
	jar -cmf manifest.mf Tasm7.jar -C bin .

install:
	mkdir -p /usr/local/bin/
	install -m 755 -o root -g wheel ./tasm7 /usr/local/bin/
	mkdir -p /usr/local/lib/tasm/
	install -m 644 -o root -g wheel ./Tasm7.jar /usr/local/lib/tasm/

clean:
	(cd src; make clean)
	rm -f *~ bin/*.class
#	rm -f Tasm7.jar
	rmdir bin

all: server.class client.class

server.class: server/server.java
	javac server/server.java

client.class: client/client.java
	javac client/client.java

server:
	java server

client:
	java client

clean:
	rm -f *.class

all: server.class client.class

server.class: server.java
	javac server.java
	
client.class: client.java
	javac client.java
	
server:
	java server
	
client:
	java client
	
clean:
	rm -f *.class

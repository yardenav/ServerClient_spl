CFLAGS:=-c -Wall -Weffc++ -g
LDFLAGS:=-lboost_system -lboost_locale -lboost_thread -lpthread

all: bin/encoder.o MultiThreadedClient
	g++ -o bin/The_Client bin/encoder.o bin/connectionHandler.o bin/MultiThreadedClient.o $(LDFLAGS) 

MultiThreadedClient: bin/connectionHandler.o bin/MultiThreadedClient.o
	
bin/connectionHandler.o: MultiThreadedClient/connectionHandler.cpp
	g++ $(CFLAGS) -o bin/connectionHandler.o MultiThreadedClient/connectionHandler.cpp

bin/MultiThreadedClient.o: MultiThreadedClient/MultiThreadedClient.cpp
	g++ $(CFLAGS) -o bin/MultiThreadedClient.o MultiThreadedClient/MultiThreadedClient.cpp
	
bin/encoder.o: encoder/encoder.cpp
	g++ $(CFLAGS) -o bin/encoder.o encoder/encoder.cpp
	
.PHONY: clean
clean:
	rm -f bin/*

#include <stdlib.h>
#include <boost/locale.hpp>
#include <boost/thread.hpp>
#include <pthread.h>
#include <boost/date_time.hpp>
#include "connectionHandler.h"
#include "../encoder/utf8.h"
#include "../encoder/encoder.h"
#include <queue>
#include "MultiThreadedClient.h"


/**
* This code assumes that the server replies the exact text the client sent it (as opposed to the practical session example)
*/




void keyboardInput(void* c)
{
	ConnectionHandler* conHandler = (ConnectionHandler*) c;
	while(stillWorks)
	{
		const short bufsize = 1024;
		char buf[bufsize];
		std::cin.getline(buf, bufsize);
		std::string line(buf);
		if (line != "bye")
		{
			if (!conHandler->sendLine(line)) {
				std::cout << "Disconnected. Exiting...\n" << std::endl;
				break;
			}
		}

		else
		{
			mutex.lock();
			stillWorks = false;
			mutex.unlock();
		}



	}
	std::cout << "keyboard Finished" << std::endl;

}

std::string nextUserInput()
{

		std::string ans;
		inputQueue.wait_and_pop(ans);

		return ans;

}

void terminate()
{
	stillWorks = false;
}


void socketHandler(void* c)
{
	ConnectionHandler* conHandler = (ConnectionHandler*) c;
	while (stillWorks)
	{

		std::string answer;

		if (!conHandler->getLine(answer)) {
					std::cout << "Disconnected. Exiting...\n" << std::endl;
					break;
				}
		if (answer == "SYSMSG QUIT ACCEPTED\n") {
			std::cout << "**Disconnected. Exiting...\n" << std::endl;
			break;
		}
		std::cout << "From server : " << answer << std::endl;
	}

}





int main (int argc, char *argv[]) {
	if (argc < 3) {
		std::cerr << "Usage: " << argv[0] << " host port" << std::endl << std::endl;
		return -1;
	}
	std::string host = argv[1];
	short port = atoi(argv[2]);

    ConnectionHandler connectionHandler(host, port);
    if (!connectionHandler.connect()) {
        std::cerr << "Cannot connect to " << host << ":" << port << std::endl;
        return 1;
    }




    // =========== Keyboard Thread declaration

	boost::thread keyboardHandlerThread(keyboardInput,&connectionHandler);



    // =========== Socket Thread declaration

	boost::thread socketThread(socketHandler,&connectionHandler);


    // =========== Keyboard Thread Join

    keyboardHandlerThread.join();


    // =========== Socket Thread Join

    socketThread.join();



	std::cout << "terminated" << std::endl;





	return 0;
}

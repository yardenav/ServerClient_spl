/*
 * MultiThreadedClient.h
 *
 *  Created on: Jan 8, 2016
 *      Author: yardenav
 */

#ifndef MULTITHREADEDCLIENT_H_
#define MULTITHREADEDCLIENT_H_
#include "concurrent_queue.cpp"

	//ConnectionHandler connectionHandler;
	//std::queue<std::string> inputQueue;
	concurrent_queue<std::string> inputQueue;
	bool stillWorks = true;
	boost::mutex mutex;



#endif /* MULTITHREADEDCLIENT_H_ */

# Sysc3303_project

Iteration 1

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.

### Prerequisites

Java (using preferably Eclipse IDE)

## Running
	1. Open 3 different consoles in eclipse.
        **To switch between read and write, Comment out line 242 and uncomment line 244 in Client.java (this will do a concurrent read and write). And vise versa**
	2. Run the Server; Type 1 for verbose and 0 for silent in the console; tyep "exit" whenever in the console to end the server
	3. Run the Intermediate Host; Type 1 for verbose and 0 for silent in the console
	4. Run the Client; Type 1 for verbose and 0 for silent in the console
	5. Switch each Console Window to show the Client, Intermediate Host, and Server Individually
	7. View results in External Window (i.e. JFrame)

### Testing concurrent File transfers
    1. This one needs a little modification from you. Delete all the files that were created from the previous file transfer tests (./Client should only have writeTest.txt and ./Server should only have readText.txt). 
    2. Start the server, intermediateHost following instructions under Running
    3. Start the client but do not choose the verbose yet
    4. Modify the interHostPort in the intermediateHost.java and client.java to another number that not 23 BUT the new port chosen must both be the same and cannot be 23 or 69
    5. Comment out line 242 and uncomment line 244 in Client.java (this will do a concurrent read and write)
    6. Start the modified intermediateHost with the changed port and choose verbose or non verbose
    7. Start the modified client
    8. Get both the client instances to be on seperate consoles and select verbose or non verbose for both

## Files
    1. Client.java; Contains the code that controls the client portal 
    2. ComFunctions.java; Contains common functions that are shared between the client, server and the IntermediateHost(A.K.A error simulator)
    3. IntermediateHost.java; Error Simulator, for now it just passed the messages between the serverWorkerThread/Server and client
    4. Server.java; Handles all the in initial incoming requests, then passes the job on to a sperate thread
    5. ServerExitListener.java; thread that listens for exit command in the console
    6. ServerWorker.java; Handles the reads and writes

    7. ./Client/writeTest.txt; sample file used for wrq
    8. ./Server/readTest.txt; sample file used for rrq


## Built With

	* Java
	* Eclipse

## Authors

	* Steven Chow
	* Hariprasadh Ravichandran Govindasamy
	* Tarun Kalikivaya
	* Yohannes Kussia

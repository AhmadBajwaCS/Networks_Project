
# Prerequisites:

- Java SDK 19.0.2 installed properly on your machine

# About:

This project demonstrates a simple TCP client/server architecture in Java. Test files are provided in the 
```./out/artifacts``` directory if you wish to skip the compilation steps.

# How to Compile:

The source code for the two jar files (Server.jar and Client.jar) are found in their respective
folders under the src directory. In order to compile these folders, do the following:

#### 1. Navigate to the directory containing the source code you would like to compile in a terminal window
#### 2. Run the command ```javac -d ./build {filename}.java``` where filename is the name of the file to compile
#### 3. If there are no compilation errors, next run the command ```jar cvf {jar-filename}.jar *```

You can name your jar files whatever you like, just make sure to make a note of your specific jar filenames
to use in the subsequent steps.

# How to Run:

*Note: Both the Client.jar file and the Server.jar file must be compiled correctly by this step*

### Running the Server:
#### 1. Navigate to the directory containing your Server.jar file
#### 2. Run the command ```java -jar {jar-filename}.jar```

Once the server is running, you can then initiate a client instance.

### Running Clients:
#### 1. In a new terminal window, navigate to the directory containing your Client.jar file
#### 2. Run the command ```java -jar {jar-filename}.jar```

Assuming the server is running properly, you should be prompted to enter a username before connecting.


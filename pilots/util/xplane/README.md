UDP to TCP Converter for xPlane and PILOTS
==========================================
Written by Alessandro Galli (agalli93@gmail.com) and Frederick Lee 

This program is the UDP to TCP Converter for xPlane and PILOTS for use with xPlane 9.0 and other versions with modifications, and the PILOTS programming language developed by the Worldwide Computing Lab at Rensselaer Polytechnic Institute. It is designed to receive data from xPLane over UDP, convert that data into a PILOTS readable format, and then send it to PILOTS over TCP.

#Current steps to use the software:

1. Determine your local IP address. 
2. Input your IP address into xPlane and a port of your choosing (the input port into the converter, `network.inputPort` in `config.ini`) which you specify (Settings->Net Connections->Advanced->IP for Data Output) 
3. Set the xPlane input port on (Settings->Net Connections-> UDP Port) (`network.xPlanePort` in the `config.ini`)
4. Write the PILOTS data streams equal to their xPlane counterpart in `userSelections.ini`.
5. Initialize PILOTS and run the UDP_TCPConverter. 
6. Done!

Note: For testing of the TCP output/UDP Input, there are files in the sub directory of `UDP and TCP Testing` that can be used for testing. UDP Input may be difficult since you would have to replicate the format of xPlane but TCP testing is easy just to ensure you're receivng the data.

#Files: 

###UDP_TCPConvterter.java

Contains all of the functions of the parser as well as the main function. 

*future work:* This probably should be broken into smaller files. 

###userSelections.ini

This file contains the user's selections for datastreams to be used in PILOTS and the ones requested from xPlane. 

###config.ini

This is the config file the user will use to specify certain frequently changing parameters. Details are shown in the config file. 

###sourceNames.txt

This is the file used to tell the Parser what the data stream names from xPlane are and their dataGroup/dataIndex locations. This file will be a one time generation and shouldn't need to be regularly edited. 

This file was created by the writing to disk feature of xPlane's data streams. All of the data streams were selected to be written to the disk. The file was then opened, and each data stream set to display on in the cockpit, about 15 at a time, and wherever there was a gap, a "null" string was written into the text file. This is required because names of the data streams were necessary and the write to disk feature doesn't put in gaps even though the UDP does. Writing these "null"s into the file is a one time task, as once you do this you save the file and never touch it again. The end of the data stream names is read by a carriage return. Currently, there needs to be a `sourceNames.txt` for each aircraft used since each aircraft outputs different data stream names. This is NOT because the indexes change cross aircraft, but because a B52 will have a throttle_8 setting while a Cessna 172 will only have a throttle_1 setting so the xPlane developers felt no need to write this null data stream to disk. 

Again, one solution to this is creating a `sourceNames.txt` style file for each aircraft you wish to obtain data from. Another option would require getting an aircraft or a set of aircraft to output each data stream and use a merging function between them to create a master `sourceNames.txt` that would have each data stream name and it would never need to be switched.

For now, the requirement is each aircraft will require it's own `sourceNames.txt` file as all of the data stream names are not present for each aircraft. 

#Functions: 

###public static String convertInputData(byte[] receiveData, int numDataStreams, String output, Initializations config, Vector<Pair<Integer> > streamVector)

This function is used to take the incoming data from xPlane and the convert it from a byte array representing a float (in little endian) to a float in the form of a string

* **receiveData {byte[]}** byte array with the recieved data
* **numDataStreams {int}** the number of datastreams that are being sent by xPlane 
* **output {String}** this is the string output that will go to PILOTS
* **config {Initializations}** the passed in config file variables class
* **streamVector {Vector\<Pair\<Integer> >}**: vector of the dataGroup/dataIndex pairs, determined by the readIngroupsList, and selected in `userSelections.ini`.

* **return {String}** output to Pilots

###private static void readInGroupsList(Properties prop, Map<String, Pair<Integer> > dataGroups) throws Exception

This function is used to read in the data stream names and determine their data group/data index numbers. This requires a full dataGroups file without any gaps of streams, there can't be a missing data group inbetween since the parser relies on them being sequential to determine their index number. 

* **prop {Properties}** properties list that is parsed from the `config.ini` file. 
* **dataGroups {Map\<String, Pair\<Integer> >}** map of the dataGroups written by xPlane 

### private static void init(Initializations config, Map<String, Pair<Integer> > dataGroups)

This function is used to write the initializations set by the user in config.ini into the initializations 
class to be referenced by the entire program. 

* **config {Initializations}** the passed in config file variables class
* **dataGroups {Map\<String, Pair\<Integer> >}** map of the dataGroups written by xPlane from the readInGroupsList

###private static void readInUserStreams(Initializations config, StringBuilder header, Map<String, Pair<Integer> > dataGroups, Vector<Pair<Integer> > streamVector, Set<Integer> dataGroupNums) throws Exception

This function takes in `userSelections.ini` and reads in the PILOTS specified data streams to create a header string of them, and the xPlane data streams they're equal to, to determine which dataGroup/dataIndex pairs are required to be read and written into PILOTS. 

* **config {Initializations}** the passed in config file variables class
* **header {StringBuilder}** header string that will be the first packet sent to PILOTS
* **dataGroups {Map\<String, Pair\<Integer> >}** map of the dataGroups written by xPlane from the readInGroupsList
* **streamVector {Vector\<Pair\<Integer> >}**: vector of the dataGroup/dataIndex pairs, determined by the readIngroupsList, and selected in `userSelections.ini`.
* **dataGroupNums {Set\<Integer>}** set of the dataGroups that are required by the streamVector.

###public static void deselectAllDataStreams(Initializations config) throws Exception

This basic function runs after the initialization and clears all of the currently selected data streams set by xPlane for transmission. For this function to work, it is required that the user correctly input the IP address of the xPlane computer and the port over which they will receive the data. The port can be set in "Settings -> Net Connections -> UDP Port -> port that we receive on"

* **config {Initializations}** the passed in config file variables class

###public static void selectRequestedStreams(Initializations config, Set<Integer> dataGroupNums) 

This function takes the data Group Numbers that were determed by the readInUserStreams function, converts them to a byte array and sends it to xPlane to be sent to the converter. The `deselectAllDataStreams` function should be run before this to clear all unnecessary data streams to ensure the converter only receives what it needs. For this function to work, it is required that the user correctly input the IP address of the xPlane computer and the port over which they will receive the data. The port can be set in "Settings -> Net Connections -> UDP Port -> port that we receive on"

* **config {Initializations}** the passed in config file variables class
* **dataGroupNums {Set\<Integer>}** set of the dataGroups that are required by the streamVector.

###Main
Reads in the `config.ini` file and processes the configs

if record to file: Create a new file to store the data taken in by the server

Build the pilots header by reading in the user selected data streams

Create the timestamp format and await input that the user has PILOTS set up to receive the data.

Send the deselect all command to xPlane and select the data streams requested by the user in `userSelections.ini`

If the user specified to write to TCP, set up the sockets

If the user specified to record the data, write the header to the file

while true:

* receive data from xPlane
* timestamp it
* convert the input data
* if record: write the data to file
* if write to TCP: send over TCP to PILOTS


#Future Work:

Break down the main file into smaller class files. 

Use the real time from xPlane to feed the time into Pilots (since UDP isn't ordered, time stamp of the conversion program may artificially order the data)

Control the frequency of the data sent to PILOTS. Possibly by setting a data frequency in config.ini 

create multiple sourceNames.txt files for multiple aircraft or merge them all into one comprehensive sourceNames file or determine how to get all of the index Names. 

#References

###Java References:

http://systembash.com/content/a-simple-java-udp-server-and-udp-client/

http://systembash.com/content/a-simple-java-tcp-server-and-tcp-client/

http://docs.oracle.com/javase/tutorial/essential/io/file.html#creating

http://docs.oracle.com/javase/8/docs/api/java/nio/file/StandardOpenOption.html

http://stackoverflow.com/questions/13469681/how-to-convert-4-bytes-array-to-float-in-java //converting function I used

###xPlane UDP References
http://www.nuclearprojects.com/xplane/info.shtml

http://www.jefflewis.net/XPlaneUDP_9.html

http://www.nuclearprojects.com/xplane/xplaneref.html

http://www.scadacore.com/field-applications/miscellaneous/online-hex-converter.html //testing of conversion with hex

###Config File URLs

http://stackoverflow.com/questions/1925305/best-config-file-format

http://www.gnu.org/prep/standards/html_node/Configuration.html

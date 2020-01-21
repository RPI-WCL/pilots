package pilots.util.xplane;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.nio.file.FileSystemException;
import java.nio.file.StandardOpenOption;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Date;
import java.util.*;
import java.text.*;
import java.util.Scanner;
import java.util.Properties;
import java.lang.Math;
import java.lang.StringBuilder;

class Pair<T>
{
   public Pair() { first = null; second = null; }
   public Pair(T first, T second) { this.first = first;  this.second = second; }

   public T getFirst() { return first; }
   public T getSecond() { return second; }

   public void setFirst(T newValue) { first = newValue; }
   public void setSecond(T newValue) { second = newValue; }

   private T first;
   private T second;
}

public class UDP_TCPConverter
{
   static boolean debug = false;
   private static void debugOut(String output) {if (debug) System.out.println(output);}

   private static class Initializations
   {
      //File Recording Options
      public boolean recordData;
      public Path filePath;

      //Network Configuration
      public int inputPort;
      public InetAddress outputIP;
      public int outputPort;
      public boolean writeToTCP;
      public InetAddress xPlaneIP;
      public int xPlanePort;

      public String userSelections;

      //Debug options set globally for ease of programming
   }

   //**Need to have it only take the data indexes that we want
   public static String convertInputData(byte[] receiveData, int numDataStreams, String output, Initializations config, Vector<Pair<Integer> > streamVector)
   {
      Iterator<Pair<Integer> > streamVectorItr = streamVector.iterator();
      Pair<Integer> nextDataIndex = streamVectorItr.next(); 
      for (int dataGroup = 0; dataGroup<numDataStreams; ++dataGroup) //For each data stream
      {

         int xPlaneIndex = receiveData[5+36*dataGroup];
         debugOut("Index: " + xPlaneIndex);
         if (nextDataIndex.getFirst() != xPlaneIndex) {
            System.out.println("The output dataGroups in xPlane were not properly selected. Please review the dataGroups to select and make sure " +
               "ONLY those are selected.");
            System.exit(1);
         }

         for(int dataIndex=0; dataIndex<8; ++dataIndex)
         {
            if (nextDataIndex.getSecond() != dataIndex){
               debugOut("Passing Index #" + dataIndex);
               continue;
            }

            byte[] floatBytes = {receiveData[(36*dataGroup)+(4*dataIndex)+9], //Offset by 9 because of 5 byte data and 4 byte xPlaneIndex
               receiveData[(36*dataGroup)+(4*dataIndex)+10],
               receiveData[(36*dataGroup)+(4*dataIndex)+11],
               receiveData[(36*dataGroup)+(4*dataIndex)+12]};
            //Testing
               // byte[] test1_array = {(byte)0x00, 0x40, (byte)0xF6, (byte)0x42};
               //AB, 67, 51, BF works for Little Endian
               //00, 40, F6, 42 also works for little endian
            float convertedNumber = ByteBuffer.wrap(floatBytes).order(ByteOrder.LITTLE_ENDIAN).getFloat();
            output = (output + Float.toString(convertedNumber) + ",");
            debugOut("Float "+dataGroup+","+dataIndex+": " + convertedNumber);
            if (!streamVectorItr.hasNext()) break; 
            nextDataIndex = streamVectorItr.next();
         }
      }
      output = (output.substring(0,output.length()-1) + '\n');
      return output;
   }

   //Function to read in the list of data groups from the file specified by the user
   //** Issue in that not all data streams are contained in the file (some data streams are located where there are nulls)
   private static void readInGroupsList(Properties prop, Map<String, Pair<Integer> > dataGroups) throws Exception
   {
      //Read in the list of source data groups and indexes
      String dataSourceNames = prop.getProperty("dict.source");
      InputStream is = new FileInputStream(dataSourceNames);

      //Error check file existance
      if (is == null) throw new FileNotFoundException("Source Names file, " + dataSourceNames + ", not found in current directory");

      DataInputStream dis = new DataInputStream(is);
      dis.readByte(); dis.readByte(); //Clears the first carriage return and linefeed
      int intDataGroup = 0;
      int intDataIndex = 0;
      String dataStreamName = new String();
      //While there are still chracters available
      while (dis.available()>0)
      {
         char c =(char)dis.readByte();
         //If delineator is reached, take that data group name and <dG,dI> and insert it into the map
         if (c == '|')
         {
            dataStreamName = dataStreamName.trim();// trim the whitespace
            Pair<Integer> groupAndIndex = new Pair<Integer>(intDataGroup,intDataIndex);
            dataGroups.put(dataStreamName, groupAndIndex);
            //debugOut(dataStreamName + " " + intDataGroup + " " + intDataIndex); 
            dataStreamName = new String();
            //If exceeded # of indexes in a group, increment group and reset index
            if (++intDataIndex == 8)
            {
               ++intDataGroup;
               intDataIndex = 0;
            }
            continue;
         }
         //If we've reached the end of the first line of data stream names, break
         else if (c == '\r') break;
         //If regular character, add it to the string and continue
         dataStreamName = dataStreamName + c;
      }
   }

   //Add to debug function with levels
   //Create Preconfigured datastreams.ini files for users who want certain data streams

   //Initializes the program with the config file and reads in the list of data groups
   private static void init(Initializations config, Map<String, Pair<Integer> > dataGroups) throws Exception
   {
      //Read in the config file
      Properties prop = new Properties();
      String fileName = "pilots/util/xplane/config.ini";
      InputStream is = new FileInputStream(fileName);

      if (is != null) prop.load(is);
      else throw new FileNotFoundException("Config File, " + fileName + ", not found in current directory");

      is.close();

      if (prop.getProperty("file.recordData").equals("true")) config.recordData = true;
      else config.recordData = false;

      config.filePath = Paths.get((prop.getProperty("file.directory")).trim());

      config.inputPort = Integer.parseInt(prop.getProperty("network.inputPort"));
      config.outputIP = InetAddress.getByName(prop.getProperty("network.outputIP"));
      config.outputPort = Integer.parseInt(prop.getProperty("network.outputPort"));
      config.xPlaneIP = InetAddress.getByName(prop.getProperty("network.xPlaneIP"));
      config.xPlanePort = Integer.parseInt(prop.getProperty("network.xPlanePort"));

      config.userSelections = prop.getProperty("selections.source");

      if (prop.getProperty("debug.consoleOutput").equals("true")) debug = true;
      else debug = false;

      if (prop.getProperty("network.writeToTCP").equals("true")) config.writeToTCP = true;
      else config.writeToTCP = false;

      readInGroupsList(prop, dataGroups);
   }

   //broken until readInGroupsList problem is fixed.
   private static void readInUserStreams(Initializations config, StringBuilder header, Map<String, Pair<Integer> > dataGroups, Vector<Pair<Integer> > streamVector, Set<Integer> dataGroupNums) throws Exception
   {
      //Load user selected data stream file
      Properties userStreams = new Properties();
      InputStream is = new FileInputStream(config.userSelections);
      //Error check to make sure file exists
      if (is != null) userStreams.load(is);
      else throw new FileNotFoundException("Data Stream Selections file, " + config.userSelections+ ", not found in current directory");
      is.close();

      // Declare set of strings that are the dataStreams wanted by the user
      Set<String> userKeys = userStreams.stringPropertyNames();
      Iterator<String> userKeysItr = userKeys.iterator();

      //Tracker variables for sorting the header and the streamVector.
      Pair<Integer> lowestGroupIndexPair = null;
      String lowestGroupIndexPairString = "";

      // Nested while loop to reorder the header in the order that the data will come from xPlane so the converter function will write it 
      // in the proper order. Necessary because properties library function stringPropertyNames will only return a set and sets are unordered. 
      
      //While there are still items left in the set of dataStreams
      while (userKeysItr.hasNext()){
         //Take the next one and set it as the current lowest
         String key = userKeysItr.next();
         Pair<Integer> groupIndexPair = dataGroups.get(userStreams.getProperty(key).trim());
         lowestGroupIndexPair = groupIndexPair;
         lowestGroupIndexPairString = key;

         //iterate through the rest of the set to find the next lowest element in terms of dataGroup # and then dataIndex # 
         while (userKeysItr.hasNext()) {
            key = userKeysItr.next();
            groupIndexPair = dataGroups.get((userStreams.getProperty(key)).trim());
            //Logic check to see if this element is lower than the current lowest. If so, set it as the lowest
            if ((lowestGroupIndexPair.getFirst() > groupIndexPair.getFirst())
            || (lowestGroupIndexPair.getFirst() == groupIndexPair.getFirst() && lowestGroupIndexPair.getSecond() > groupIndexPair.getSecond())){
               lowestGroupIndexPair = groupIndexPair;
               lowestGroupIndexPairString = key;
            }
         }

         debugOut(userStreams.getProperty(lowestGroupIndexPairString)+ "--Group #:" +lowestGroupIndexPair.getFirst() + " --Index#:" + lowestGroupIndexPair.getSecond() 
            + " --PILOTS Name: " + lowestGroupIndexPairString);
         
         //Since the current element in the lowestGroupIndexPairString has been determined to be the next lowest, 
            //Add it to the initial header line sent to PILTOS
         header.append(lowestGroupIndexPairString + ',');
            //Add it to the dataGroupNums set to be used for requesting dataStreams from xPlane
         dataGroupNums.add(lowestGroupIndexPair.getFirst()); 
            //Add it to the streamVector vector for picking which data to be written to the output in the convertData function
         streamVector.add(lowestGroupIndexPair);
         

         //Remove the current lowest value from the set so the next lowest value can be determined and reset the lowest tracker variables
         userKeys.remove(lowestGroupIndexPairString);
         userKeysItr = userKeys.iterator();
         lowestGroupIndexPair = null;
         lowestGroupIndexPairString = "";
      }

      //Removes the trailing comma
      header.deleteCharAt(header.length()-1);
      
      debugOut("Sanity check of streamVector");
      Iterator<Pair<Integer> > itr = streamVector.iterator();
      while(itr.hasNext() && debug){
         Pair<Integer> temp = itr.next();
         debugOut("Item " + temp.getFirst() + " "+ temp.getSecond());
      }
      
      // Below was necessary when transmissions to xPlane were partially functional kept it as a good sanity check
      // System.out.println("Set the below dataGroups and ONLY the below for internet transfer for the selected data streams written into userSelections.ini");
      // Iterator<Integer> dataGroupNumsItr = dataGroupNums.iterator();
      // while(dataGroupNumsItr.hasNext()) System.out.println(dataGroupNumsItr.next());
   }

   //Sends a command to xPlane to unselect all of the currently selected datastreams for transmission
   public static void deselectAllDataStreams(Initializations config) throws Exception{   
      //Create the byte array to send with the command USEL0 (unselect the following)
      byte[] fullCommand = new byte[(132 * 4) + 5];
      fullCommand[0] = (byte) 85; //U
      fullCommand[1] = (byte) 83; //S
      fullCommand[2] = (byte) 69; //E
      fullCommand[3] = (byte) 76; //L
      fullCommand[4] = (byte) 48; //0
         
      //For all of the data streams (132 of them,) convert their indexes into bytes and insert them into the byte array to be sent to xPlane to deselect
      for(int x = 0; x < 132; x++)
      {      
         //Ints are 4 bytes long, allocate 4 bytes of space for conversion.
         ByteBuffer bb = ByteBuffer.allocate(4);
         bb.order(ByteOrder.LITTLE_ENDIAN);
         bb.putInt(x);
         //for each byte in converted in the buffer, insert it into the command to be sent to xPlane
         for(int y = 0; y < 4; y++)
            fullCommand[(x * 4) + 5 + y] = bb.get(y);
      }

      // Create the socket and packet to be sent to xPlane and send it 
      DatagramSocket xPlaneSocket = new DatagramSocket();
      DatagramPacket sendPacket = new DatagramPacket(fullCommand, fullCommand.length, config.xPlaneIP, config.xPlanePort);
      xPlaneSocket.send(sendPacket);
      debugOut("Deselections Sent");
   }

   //Takes in the data groups requested by the user via the userSelections.ini and requests them from xPlane
   public static void selectRequestedStreams(Initializations config, Set<Integer> dataGroupNums) throws Exception{
      //Create the byte array with size of the num of data groups
      byte[] fullCommand = new byte[(dataGroupNums.size() * 4) + 5];
      fullCommand[0] = (byte) 68; //D
      fullCommand[1] = (byte) 83; //S
      fullCommand[2] = (byte) 69; //E
      fullCommand[3] = (byte) 76; //L
      fullCommand[4] = (byte) 48; //0

      //For each data group, convert it's number to bytes and insert it in the byte array
      Iterator<Integer> itr = dataGroupNums.iterator();
      for(int x = 0; x < dataGroupNums.size(); x++)
      {      
         //Allocate 4 bytes of space for the conversion
         int dataGroupNumber = itr.next();   
         ByteBuffer bb = ByteBuffer.allocate(4);
         bb.order(ByteOrder.LITTLE_ENDIAN);
         debugOut("Setting stream: " + dataGroupNumber);
         bb.putInt(dataGroupNumber);

         for(int y = 0; y < 4; y++)
            fullCommand[(x * 4) + 5 + y] = bb.get(y);
      }

      //Take the byte array and send it to xPlane
      DatagramSocket xPlaneSocket = new DatagramSocket();
      DatagramPacket sendPacket = new DatagramPacket(fullCommand, fullCommand.length, config.xPlaneIP, config.xPlanePort);
      xPlaneSocket.send(sendPacket);
      debugOut("Selections Sent");   
   }

   public static void main(String args[]) throws Exception
   {
      Map<String, Pair<Integer> > dataGroups = new HashMap<String, Pair<Integer> >();
      Initializations config = new Initializations();
      init(config, dataGroups);
      
      //Create a new file to store the data taken in by the server
      if(config.recordData)
      {
         config.filePath = Paths.get((config.filePath).toString() + "/Sim_data0.txt");
         debugOut(config.filePath.toString());
         int fileNum = 1;
         while (Files.exists(config.filePath)){
            int pathOffset = (config.filePath).toString().length()-5;
            double numOffset = -Math.floor(Math.log10(fileNum));
            String subStringFilePath = ((config.filePath).toString()).substring(0,pathOffset+(int)numOffset);
            config.filePath = Paths.get(subStringFilePath + ++fileNum + ".txt");
         }
         debugOut("File Path:"+((config.filePath).toString()));
         Files.createFile(config.filePath); // Once the next available file name has been found, create it
      }

      // Parsing of user requested data streams
      Integer numDataStreams;
      // care of by the readIn function below
      StringBuilder headerSb = new StringBuilder("#"); //Instantiated for the function
      Vector<Pair<Integer> > streamVector = new Vector<Pair<Integer> >(); // Vector of <dG,dI>'s requested by user
      Set<Integer> dataGroupNums = new HashSet<Integer>(); // a Set keeping record of unique data groups needed to pull (verified)
      readInUserStreams(config, headerSb, dataGroups, streamVector, dataGroupNums);
      String header = headerSb.substring(0,headerSb.length()-1); // Removes hanging comma
      debugOut("Header Test--" + header);

      //Date creation
      Date date = new Date();
      SimpleDateFormat ft = new SimpleDateFormat (":yyyy-MM-dd hhmmssSSSZ:");

      //Verify that data is ready to be transmitted to pilots
      Scanner in = new Scanner(System.in);
      System.out.println("Ready to transmit?");
      in.nextLine();

      //Deselect all datastreams from xPlane currently selected for transmission and select the currently selected ones set by the user in userSelecitons.ini
      deselectAllDataStreams(config);
      selectRequestedStreams(config, dataGroupNums);

      DataOutputStream outToServer = null;
      String outputHeader = (header.toString() + '\n');

      //Send header to PILOTS and file if the user specifies output to said stream
      if (config.writeToTCP)
      {
         Socket clientSocket = new Socket(config.outputIP, config.outputPort);
         outToServer = new DataOutputStream(clientSocket.getOutputStream());
         outToServer.writeBytes(outputHeader);
      }

      if (config.recordData) Files.write(config.filePath, outputHeader.getBytes(), StandardOpenOption.APPEND);

      //Open socket to start receiving data
      DatagramSocket serverSocket = new DatagramSocket(config.inputPort);

      //Begin Receiving Data
      while(true)
      {
         //Begin Reciving Data over UDP
         byte[] receiveData = new byte[41+36*(dataGroupNums.size()-1)]; //Number of bytes represents size of buffer in chars
         DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
         serverSocket.receive(receivePacket);
         //End Receivng data over UDP

         //Get the current date and time since the data has been received
         date = new Date();
         String output = ft.format(date);

         //Convert data from X-Plane format to PILOTS Format
         output = convertInputData(receiveData, dataGroupNums.size(), output, config, streamVector);

         //Network, Debug, and file outputs
         debugOut(output);
         if (config.recordData) Files.write(config.filePath, output.getBytes(), StandardOpenOption.APPEND);

         if(config.writeToTCP) outToServer.writeBytes(output + '\n');
         //End Sending data over TCP
      }
   }
}
/*
Input format is:
int Index //index of the list of variables
float data[8]//up to the 8 numbers output on the screen, not all 8 will be used

First 5 bytes are the Identifier (eg. DATA) (fifth bit is insignificant)
Second 4 bytes is the index as an int
After, 8 4-byte segments are the data outputs in floating point notation
int and float pattern will repeat for as many data outputs were set
*/

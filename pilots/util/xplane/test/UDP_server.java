import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.nio.file.FileSystemException;
import java.nio.file.StandardOpenOption;
import java.net.*;


class UDPServer
{
   public static void main(String args[]) throws Exception
   {
      //Create a new file to store the data taken in by the server
      Path file_path = Paths.get("C:\\users\\gallia\\dropbox\\linux\\Sim_data_dir\\Sim_data.txt");
      int file_num = 0;
      while (Files.exists(file_path)){
         file_path = Paths.get("C:\\users\\gallia\\dropbox\\linux\\Sim_data_dir\\Sim_data" + ++file_num + ".txt");
      }
      Files.createFile(file_path); // Once the next available file name has been found, create it

      DatagramSocket serverSocket = new DatagramSocket(9876);
      while(true)
      {
         //Begin Reciving Data over UDP
         byte[] receiveData = new byte[41]; //Number of bytes represents size of buffer in chars
         //byte[] sendData = new byte[41]; //Necessary only for Echo
         DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
         serverSocket.receive(receivePacket);
         String sentence = new String( receivePacket.getData());
         System.out.println("RECEIVED: " + sentence);
         Files.write(file_path, receiveData, StandardOpenOption.APPEND);//Writing received data to a file
            /*Sending back confirmation to the client
            InetAddress IPAddress = receivePacket.getAddress();
            int port = receivePacket.getPort();
            sendData = sentence.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
            serverSocket.send(sendPacket);*/
         //End Receivng data over UDP
      }
   }
}
/*
Current problem is that when the byte array is written into the file, all 1024 bytes are written, regardless of their value. Need to find a way
to determine how many bytes to actually record. X-plane might have a limit so we could just use that. For now, possibly binary searching the last
bit.
-Potentially Solved, limit is 41 bytes I seem to have found
-Potentially Not solved, limit could change with number of data outputs set in X-Plane, requires further testing

Input format is:
int Index //index of the list of variables
float data[8]//up to the 8 numbers output on the screen, not all 8 will be used

First 5 bytes are the Identifier (eg. DATA) (fifth bit is insignificant)
Second 4 bytes is the index as an int
After, 8 4-byte segments are the data outputs in floating point notation
int and float pattern will repeat for as many data outputs were set

Careful reading/sending data and converting it because it might need to be char 0 not int 0
*/

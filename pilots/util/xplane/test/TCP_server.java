import java.io.*;
import java.net.*;

class TCP_server
{
   public static void main(String argv[]) throws Exception
   {
      String clientSentence;
      String capitalizedSentence;
      ServerSocket welcomeSocket = new ServerSocket(6789);
      Socket connectionSocket = welcomeSocket.accept();
      while(true)
      {
         BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
         // DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
         clientSentence = inFromClient.readLine();
         System.out.println("Received: " + clientSentence);
         // outToClient.writeBytes(clientSentence + '\n');
      }
   }
}


package pilots.util;

import java.io.*;
import java.net.*;

public class FileInputProducer
{
    private static final String TARGET_HOST = "localhost";
    private static final int TARGET_PORT = 8888;

    String filename;
    Socket sock;
    OutputStream outputStream;
    PrintWriter writer;
    BufferedReader reader;
    

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage: ./java pilots.util.FileInputProducer <filename>");
            return;
        }            

        FileInputProducer client = new FileInputProducer(args[0]); // filename
        client.startSend();
    }

    public FileInputProducer(String filename) {
        this.filename = filename;
        
        try {
            this.sock = new Socket(TARGET_HOST, TARGET_PORT);
            this.outputStream = sock.getOutputStream();
            this.writer = new PrintWriter(outputStream);
            this.reader = new BufferedReader(new FileReader(filename));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void startSend() {
        String str;

        try {
            while ((str = reader.readLine()) != null) {
                writer.println(str);
                writer.flush();
            }
        
            outputStream.close();
            writer.close();
            reader.close();
            sock.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

import java.io.*;
import java.net.*;
import java.nio.*;
import java.util.*;

public class MyServer {
    public static void main(String[] args) throws IOException {
        
        //create a server socket
        DatagramSocket serverSocket = new DatagramSocket(Integer.parseInt(args[0]));
        System.out.println("Server is up....");

        //byte arrays for sending and recieving and datagram packets
        byte[] rd, sd ,payload,temp_payload;
        DatagramPacket rp, sp;
        rd = new byte[1024];
        payload = new byte[512];
        temp_payload = new byte[512];
        String message;
        String ack;

        //infinite loop to check for connections
        while(true){
            
            //get the recieved packet
            rp = new DatagramPacket( rd, rd.length );
            serverSocket.receive( rp );

            //get the request of the packet
            message = ByteBuffer.wrap(rp.getData( )).toString();
            System.out.println("client said: "+message);
            //extraction of filename to be done here
                //for the time being assuming filename to be 'file'

            // Get packet's IP and port
            InetAddress IPAddress = rp.getAddress();
            int port = rp.getPort();

            //dividing the data into chunks and sending and waiting for acks from client
            InputStream is = new FileInputStream("file");
            int end = 0;
            int seq = 0;
            //reading chunk
            
            while(true) {
                is.read(payload);
                temp_payload = payload.clone();
                end = is.read(payload);
                if(end == -1){
                    //creating the last consignment
                    String temp_payload_str = new String(temp_payload);
                    String consignment = "RDT"+Integer.toString(seq)+temp_payload_str+"end";
                    sd = consignment.getBytes();
                    sp=new DatagramPacket(sd,sd.length,IPAddress,port);
                    serverSocket.send(sp);
                    System.out.println("sent last consignment to client"+sd.toString());
                    break;
                }
                else{
                    //creating the normal consignment
                    String temp_payload_str = new String(temp_payload);
                    String consignment = "RDT"+Integer.toString(seq)+temp_payload_str;
                    sd = consignment.getBytes();
                    sp=new DatagramPacket(sd,sd.length,IPAddress,port);
                    serverSocket.send(sp);
                    System.out.println("sent consignment to client"+sd.toString());
                }
            //waiting for the ack from client to send the next message
            
            try{
            //sleeping for 30ms
            rp = new DatagramPacket( rd, rd.length );
            serverSocket.receive( rp );
            ack = ByteBuffer.wrap(rp.getData( )).toString();
            }catch(Exception ex){
                System.out.println(ex.getMessage());
            }

            seq += 1;
            }

        }

    }
}
import java.net.*;
import java.io.*;
import java.util.*;

public class MyClient {
		
	public static void main(String[] args) throws SocketException, UnknownHostException {

		//create a socket
		DatagramSocket socket = new DatagramSocket();
		socket.setSoTimeout(1000);
		
		//ip address and port
		InetAddress IPAddress = InetAddress.getByName(args[0]);
		int Port = Integer.parseInt(args[1]);
		
		//create two packets and byte buffer for receiving and sending
		DatagramPacket sp,rp;
		byte[] rd, sd;
		byte seq_no = 0;
		
		//for storing the data
		String data = "";
		 
		//server messages
		String reply,ack;
		String[] reply_split = new String[4];
		boolean end = false;
		
		//request from client
		String request = "REQUESTfile";	
		try{
		//initializing the communication
		sd = request.getBytes();
		sp = new DatagramPacket(sd,sd.length,IPAddress,Port);
		socket.send(sp);
		}
		catch(IOException ex){
		System.out.println(ex.getMessage());
		}
		
		try{
		//now recieving data
		while(!end){
			rd=new byte[1024];
			rp=new DatagramPacket(rd,rd.length); 
			socket.receive(rp);
			reply=new String(rp.getData());
			System.out.println(reply);
			reply_split[1] = "1";
			reply_split[2] = "payload";
			reply_split[3] = "end";
			
			if(Integer.parseInt(reply_split[1]) == seq_no){
				System.out.println("recieved seq_no: "+seq_no);
				data += reply_split[2];
				seq_no+=1;
				
				//sending acknowledgement to server
				ack = "ACK"+Byte.toString(seq_no);
				sd = ack.getBytes();
				sp = new DatagramPacket(sd,sd.length,IPAddress,Port);
				socket.send(sp);
				
				//finding if this is the end seq_packet
				if(reply_split[4].equals("end")){
				end = true;
				}
			}
			else{
				//add the timing calculations
			}
		}
		
		socket.close();
		
		}catch(IOException ex){
			System.out.println(ex.getMessage());
		}
		//printing the data received
		System.out.println(data);
	}
}

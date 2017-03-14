package Proxy;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.logging.LogManager;

import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.Receiver;
import org.jgroups.ReceiverAdapter;
import org.jgroups.util.UUID;

import Client.Client;
import Client.Menssagem;
import Client.ProxyMenssage;



public class Proxy extends ReceiverAdapter {
	
	 
		final List<String> state=new LinkedList<String>();
		JChannel channel;
		MapaArmazenamento ma;
		
		public Proxy(){
			this.ma = new MapaArmazenamento();
		}
		
		private void start() throws Exception {
			Random random = new Random();
			
            channel=new JChannel("udp.xml");
            channel.setReceiver(this);
            channel.setName("Proxy"+random.nextInt(200));
            channel.connect("balanceadorProxy");
            eventLoop();
        }
		
		 public void receive(Message msg) {
		       
	        	if (msg.getObject() instanceof ProxyMenssage){
	        		ProxyMenssage proxyMenssage = (ProxyMenssage)msg.getObject();
	        		 System.out.println(proxyMenssage.getFileName());
	        		 try {
						enviarArmazenamento(proxyMenssage);
					} catch (Exception e) {
						e.printStackTrace();
					}
	        	}
	           
	            
	      }
		
		 public void enviarArmazenamento(ProxyMenssage prom) throws Exception{
			 JChannel aChannel = new JChannel("udp.xml");
			 aChannel.connect("proxyArm");
			
			 int num = hash(prom.getFileName()).intValue();
			 
			 String[] listReplicas = ma.retornarReplicas(num);
			 
			 
			 if(prom.getOperacao().equals("w")){
				 for(String i: listReplicas){
					 
					 String nameArmazenamento = "armazenamento"+i;
					 
					 Message msg = new Message(UUID.getByName(nameArmazenamento), null, prom);
					 try{
						 aChannel.send(msg);
						 System.out.println(nameArmazenamento+" Connected");
						 
					 }catch(Exception e){
						 System.out.println("Error: "+e.getMessage());
					 }
					
				 }
			 }else{
				 String nameArmazenamento = "armazenamento"+listReplicas[0];
				 
				 Message msg = new Message(UUID.getByName(nameArmazenamento), null, prom);
				 try{
					 aChannel.send(msg);
					 System.out.println(nameArmazenamento+" Connecting");
					 
				 }catch(Exception e){
					 System.out.println("Error: "+e.getMessage());
				 }
				
			 }
			 
			 
			 
		
		 }
		 
		 private void eventLoop() {
	            BufferedReader in=new BufferedReader(new InputStreamReader(System.in));
	            System.out.println("Waiting...");
	            while(true) {
	                try {
	                    System.out.print("> "); System.out.flush();
	                    String line=in.readLine().toLowerCase();
	                    if(line.startsWith("quit") || line.startsWith("exit")) {
	                        break;
	                    }
	                }
	                catch(Exception e) {
	                }
	            }
	        }
		
		 public BigInteger hash(String file) throws NoSuchAlgorithmException{
             
	            MessageDigest m=MessageDigest.getInstance("MD5"); 
	            m.update(file.getBytes(), 0, file.length()); 
	            BigInteger b = new BigInteger(1,m.digest()); 
	            //System.out.println(b.mod(new BigInteger("3"))); // ID da partição 
	            //System.out.println("MD5: " + b.toString(16)); // Hash 
	            String num = String.valueOf(this.ma.getNumeroParticoes());
	            return b.mod(new BigInteger(num));
	        } 
		 
		public static void main(String[] args) throws Exception {
			LogManager.getLogManager().reset();
			new Proxy().start();
			
			
		}
}
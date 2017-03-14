package Client;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;
import org.jgroups.util.UUID;
import org.jgroups.util.Util;

public class Client extends ReceiverAdapter{
	 
	final List<String> state=new LinkedList<String>();
	JChannel channel;
	 List<ProxyMenssage> listResquisicoes = new ArrayList<ProxyMenssage>();
	
	public static void main(String[] args) throws Exception {
		LogManager.getLogManager().reset();
		new Client().start();
		
		
	}
	
	 public void receive(Message msg) {
	       
		 if (msg.getObject() instanceof Menssagem){
    		 Menssagem menssagem = (Menssagem)msg.getObject();
    		 if(menssagem.getAd() != null){
    			 System.out.println(menssagem.getFileName());
        		 
        		 try {
    				chamarProxy(menssagem);
    			} catch (Exception e) {
    				e.printStackTrace();
    			}
    		 }else{
    			 System.out.println("There`s no proxy to Connect");
    		 }
    		 
		 }
         
     }

	private void chamarProxy(Menssagem menssagem) throws Exception {
		JChannel pchannel = new JChannel("udp.xml");
		pchannel.setReceiver(this);
		pchannel.connect("balanceadorProxy");
		
		Address ad = menssagem.getAd();
		//menssagem.setAd(channel.getAddress());
		if(this.listResquisicoes.size() >0){
			ProxyMenssage prom = listResquisicoes.get(0);
			listResquisicoes.remove(0);
			
			prom.setAd(channel.getAddress());
			Message msg=new Message(UUID.getByName(menssagem.getOperacao()), null, prom);
			pchannel.send(msg);
		}else{
			System.out.println("There`s no requisitions");
		}
		
		
		
		
		
		
	}

	public void start() throws Exception{
		channel = new JChannel("udp.xml");
		
		Random rd = new Random();
		
		channel.setReceiver(this);
		channel.setName("Client"+rd.nextInt(120000));
        channel.connect("comunicationBalance");
		BufferedReader in=new BufferedReader(new InputStreamReader(System.in));
		
		while(true){
			System.out.println("Menu: ");
			System.out.println("To write a new file type:w ");
			System.out.println("To read a file type: r ");
			System.out.println("To exit type: e ");
			System.out.print("> "); System.out.flush();
			String choice = in.readLine().toLowerCase();
			 if(choice.startsWith("e")) {
                 break;
             }else{
            	 System.out.print("Type the name of the file:");
            	 String fileName = in.readLine();
            	 
            	 Path path = Paths.get(fileName);
            	 
            	 if(choice.equals("w")){
            		 try{
                		 byte[] data = Files.readAllBytes(path);
                		 
                		 ProxyMenssage pm = new ProxyMenssage(choice, fileName, data);
                		 this.listResquisicoes.add(pm);
                		 enviarMenssage();
                		 
                		 
                	 }catch(Exception e){
                		 System.out.println("The file "+fileName+" doesn`t exists");
                	 }
            		 
            	 }else{
            		 ProxyMenssage pm = new ProxyMenssage(choice, fileName);
            		 this.listResquisicoes.add(pm);
            		 enviarMenssage();
            	 }
            	 
           	
             }
			
		}

	}
	

	private  void enviarMenssage() throws Exception {	
		
		//String mensagem = choice+","+fileName;
        Menssagem menssagem = new Menssagem();  
        menssagem.setOperacao(channel.getAddressAsString());
        View v =  channel.getView();
        List<Address> list = v.getMembers();
        
        Random random = new Random();
        int index = random.nextInt(v.size());
       
        System.out.println("======================"+v.size());
        while(list.get(index).equals(channel.getAddress()) || !list.get(index).toString().contains("balanceador")){
        	index = random.nextInt(v.size());

        }
        
        Message msg=new Message(list.get(index), channel.getAddress(), menssagem);
        channel.send(msg);
       
		
	}

}

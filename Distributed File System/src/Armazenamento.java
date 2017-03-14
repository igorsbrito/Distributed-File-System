import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.LogManager;

import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import java.nio.file.Files;

import Client.ProxyMenssage;


public class Armazenamento extends ReceiverAdapter{
	
	final List<String> state=new LinkedList<String>();
	JChannel channel;
	String nome;
	
	public Armazenamento(String nome){
		this.nome = nome;
	}
	
	 public void receive(Message msg) {
	       
     	if (msg.getObject() instanceof ProxyMenssage){
     		ProxyMenssage proxyMenssage = (ProxyMenssage)msg.getObject();
     		if(proxyMenssage.getOperacao().equals("w")){
     			
     			Path  path = new File(this.nome+"/"+proxyMenssage.getFileName()).toPath();
         		byte[] arq = proxyMenssage.getFile();
         		 
         		try {
    				Files.write(path ,arq);
    				System.out.println("Successfully Written");
    			} catch (IOException e) {
    				System.out.println("Error: "+e.getMessage());
    			}
       			
     		}else{
     			Path  pathAux = new File("\\"+this.nome+"\\"+proxyMenssage.getFileName()).toPath();
				 Date data = new Date(System.currentTimeMillis());
				 
				 try {
					byte[] arqAux = Files.readAllBytes(pathAux);
					Path  pathR = new File(data.toString()+"-"+proxyMenssage.getFileName()).toPath();
					Files.write(pathR, arqAux);
					System.out.println("Read successfully");
				} catch (IOException e) {
					e.printStackTrace();
				}		
     		}
		 
     	}
      
   }
	
	private void start() throws Exception {
        channel=new JChannel("udp.xml");
        
        channel.setReceiver(this);
        channel.setName(this.nome);
        channel.connect("proxyArm");
        eventLoop();
    }

	private void eventLoop() {
        BufferedReader in=new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Esperando...");
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
	
	public static void main(String[] args) throws Exception {
		LogManager.getLogManager().reset();
		
		// When run the Armazenamaneto class, should pass a number as argument to be the number of the repository node
		String nome = "armazenamento"+args[0];
		
		File diretorio = new File(nome);
		if(diretorio.exists()){
			System.out.println("Directory alredy exists");
		}else{
			System.out.println("Directory not found");
			System.out.println("Directory "+nome+" will be created");
			diretorio.mkdirs();
		}
		
		new Armazenamento(nome).start();
		
		
	}
	
}

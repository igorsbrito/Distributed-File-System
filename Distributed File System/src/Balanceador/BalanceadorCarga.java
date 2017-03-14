package Balanceador;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;


import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;
import org.jgroups.Address;
import org.jgroups.util.UUID;
import org.jgroups.util.Util;

import Client.Menssagem;
import Client.ProxyMenssage;


public class BalanceadorCarga extends ReceiverAdapter{
     JChannel channel;
     final List<String> state=new LinkedList<String>();
     
     
     public void viewAccepted(View new_view) {
            System.out.println("** view: " + new_view);
        }


        public void receive(Message msg) {
       
        	if (msg.getObject() instanceof Menssagem){
        		 Menssagem menssagem = (Menssagem)msg.getObject();
        		// System.out.println(menssagem.getFileName());
        		
        		 try {
					enviarProxy(menssagem);
				} catch (Exception e) {
					e.printStackTrace();
				}
        		 
        	}
           
            
        }

        public void enviarProxy(Menssagem menssagem) throws Exception{
        	
        	Address ad = acharProxy();
        	
        	Address dest = UUID.getByName(menssagem.getOperacao());
        	menssagem.setAd(ad);
        	if(ad != null){
        		menssagem.setOperacao(ad.toString());
        	}
        
			
			
			Message msg = new Message(dest, channel.getAddress(), menssagem);
			channel.send(msg);
			
        }

        public void getState(OutputStream output) throws Exception {
            synchronized(state) {
                Util.objectToStream(state, new DataOutputStream(output));
            }
        }



        private void start() throws Exception {
            channel=new JChannel("udp.xml");
            channel.setReceiver(this);
            channel.setName("balanceador");
            channel.connect("comunicationBalance");
            eventLoop();
            
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


      public Address acharProxy() throws Exception{
            JChannel pchannel = new JChannel("udp.xml");
            pchannel.connect("balanceadorProxy");
            //pchannel.getState(null, 10000);
            View v =  pchannel.getView();
            List<Address> listAux = v.getMembers();
            
            List<Address> list = new ArrayList<Address>();
            
            Random random = new Random();
            int index = random.nextInt(v.size());
            for(Address a : listAux){
            	if(a.toString().contains("Proxy")){
            		list.add(a);
            	}
            }
           
            //System.out.println("======================"+v.size());
            if(list.size() > 0){
                 index = random.nextInt(list.size());
                 return list.get(index);
            	
            }else{
            	return null;
            }
            
           

        }


        public static void main(String[] args) throws Exception {
        	LogManager.getLogManager().reset();
            new BalanceadorCarga().start();
        }


}

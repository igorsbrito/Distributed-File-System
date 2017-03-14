package Proxy;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class MapaArmazenamento {
	
	int numeroParticoes;
	int numeroReplicas;
	List<String[]> replicas;
	
	public MapaArmazenamento(){
		
		this.replicas = new ArrayList<String[]>();
		lerArquivo();
		
	}
	
	
	public String[] retornarReplicas(int num){
		return replicas.get(num);
	}
	
	public void lerArquivo(){
		
		try {
		      FileReader arq = new FileReader("mapa.txt");
		      BufferedReader lerArq = new BufferedReader(arq);
		 
		      this.numeroParticoes = Integer.parseInt(lerArq.readLine()); 
		      
		      this.numeroReplicas = Integer.parseInt(lerArq.readLine());
		      
		      String linha = lerArq.readLine();
		      
		      String[] rep = linha.split(",");
		      
		      this.replicas.add(rep);
		      
		      while (linha != null) {
		        System.out.printf("%s\n", linha);
		 
		        linha = lerArq.readLine(); // lê da segunda até a última linha - Read from second to the last line
		        
		        if(linha != null){
		        	String[] repl = linha.split(",");
			        
			        this.replicas.add(repl);
		        }
		        
		        
		      }
		 
		      arq.close();
		    } catch (IOException e) {
		        System.err.printf("erro in the file opening: %s.\n",
		          e.getMessage());
		    }
	}
	
	
	
	public int getNumeroParticoes() {
		return numeroParticoes;
	}

	public void setNumeroParticoes(int numeroParticoes) {
		this.numeroParticoes = numeroParticoes;
	}

	public int getNumeroReplicas() {
		return numeroReplicas;
	}

	public void setNumeroReplicas(int numeroReplicas) {
		this.numeroReplicas = numeroReplicas;
	}

	public List<String[]> getReplicas() {
		return replicas;
	}

	public void setReplicas(List<String[]> replicas) {
		this.replicas = replicas;
	}

	public static void main(String[] args){
		MapaArmazenamento mp = new MapaArmazenamento();
		System.out.println(mp.getReplicas().get(0)[0]);
		System.out.println(mp.getNumeroParticoes());
		
	}
}

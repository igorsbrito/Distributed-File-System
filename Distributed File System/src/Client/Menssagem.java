package Client;
import java.io.Serializable;

import org.jgroups.Address;


public class Menssagem implements Serializable{

	String operacao;
	String fileName;
	Address ad;
	
	public Menssagem(){
		
		
	}
	
	public Menssagem(String operacao, String fileName){
		this.operacao = operacao;
		this.fileName = fileName;
		
	}
	
	public Menssagem(String operacao, String fileName, Address ad){
		this.operacao = operacao;
		this.fileName = fileName;
		this.ad = ad;
	}
	
	
	public String getOperacao() {
		return operacao;
	}

	public void setOperacao(String operacao) {
		this.operacao = operacao;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public Address getAd() {
		return ad;
	}

	public void setAd(Address ad) {
		this.ad = ad;
	}

	
	
}

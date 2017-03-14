package Client;

import java.io.Serializable;

import org.jgroups.Address;

public class ProxyMenssage implements Serializable{

	
	String operacao;
	String fileName;
	byte[] file;
	Address ad;
	String estado;
	
	public ProxyMenssage(String operacao, String fileName, byte[] data) {
		super();
		this.operacao = operacao;
		this.fileName = fileName;
		this.file = data;
	}
	
	
	
	public ProxyMenssage(String operacao, String fileName) {
		super();
		this.operacao = operacao;
		this.fileName = fileName;
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
	public byte[] getFile() {
		return file;
	}
	public void setFile(byte[] file) {
		this.file = file;
	}
	public Address getAd() {
		return ad;
	}
	public void setAd(Address ad) {
		this.ad = ad;
	}
	public String getEstado() {
		return estado;
	}
	public void setEstado(String estado) {
		this.estado = estado;
	} 
	
	
}

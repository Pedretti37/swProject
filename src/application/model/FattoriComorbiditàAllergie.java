package application.model;


public class FattoriComorbiditàAllergie {

	private String cf;
	private String tipo;
	private String nome;
	private String modificato;
	
	public FattoriComorbiditàAllergie(String cf, String tipo, String nome, String modificato) {
		this.cf = cf;
		this.tipo = tipo;
		this.nome = nome;
		this.modificato = modificato;
	}
	
	public String getCF() {
		return cf;
	}

	public String getTipo() {
		return tipo;
	}
	
	public String getNome() {
		return nome;
	}
	
	public String getModificato() {
		return modificato;
	}
}
package application.model;

import java.io.InputStream;
import java.sql.Blob;
import java.sql.SQLException;
import java.time.LocalDate;

public class Utente {

	private String cf;
	private String pw;
	private String ruolo;
	private String nomeCognome;
	private LocalDate dataDiNascita;
	private String sesso;
	private Blob foto;
	private String mail;
	private String diabetologoRif;
	
	public Utente(String cf, String pw, String ruolo, String nomeCognome, LocalDate dataDiNascita, String sesso, Blob foto, String mail, String diabetologoRif) {
		this.cf = cf;
		this.pw = pw;
		this.ruolo = ruolo;
		this.nomeCognome = nomeCognome;
		this.dataDiNascita = dataDiNascita;
		this.sesso = sesso;
		this.foto = foto;
		this.mail = mail;
		this.diabetologoRif = diabetologoRif;
	}
	
	public boolean checkPw(String pw) {
		return this.pw.equals(pw);
	}
	
	public String getCf() {
		return cf;
	}

	public String getPw() {
		return pw;
	}
	
	public String getRuolo() {
		return ruolo;
	}

	public boolean isDiabetologo() {
    	return "diabetologo".equals(this.ruolo);
	}

	public boolean isPaziente() {
		return "paziente".equals(this.ruolo);
	}
	
	public String getNomeCognome() {
		return nomeCognome;
	}
	
	public LocalDate getDataDiNascita() {
		return dataDiNascita;
	}
	
	public String getSesso() {
		return sesso;
	}
	
	public InputStream getFoto() {
		if (this.foto == null) return null; // Evita il crash se non c'è foto
		try{
			return this.foto.getBinaryStream();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public String getMail() {
		return mail;
	}
	
	public String getDiabetologoRif() {
		return diabetologoRif;
	}
}
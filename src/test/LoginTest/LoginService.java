package test.LoginTest;

//import static org.junit.Assert.assertEquals;
//import org.junit.Test;

import application.Amministratore;
import application.model.Utente;

public class LoginService {
	
	public static Utente login(String cf, String password) {
		if(cf==null || cf.isBlank() || password == null || password.isBlank())
			return null;
		
		if(!Amministratore.utenteEsiste(cf))
			return null;
		
		Utente utente = Amministratore.getUtente(cf);
		
		if(utente.checkPw(password))
			return utente;
		
		return null;
	}
}
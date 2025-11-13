package test.LoginTest;

import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.LocalDate;

import application.Amministratore;
import application.model.Utente;

public class LoginServiceTest {
    
    @BeforeClass
    public static void setup() {
        Amministratore.utenti.add(new Utente("a", "a", "paziente", "", LocalDate.now(), "", "", "", ""));
        Amministratore.utenti.add(new Utente("b", "b", "diabetologo", "", LocalDate.now(), "", "", "", ""));
    }
    
    @Test
    public void testLoginDiabetologo() {
        Utente utente = LoginService.login("b", "b");
        assertNotNull(utente);
        assertEquals("diabetologo", utente.getRuolo());
    }
    
    @Test
    public void testLoginPaziente() {
        Utente utente = LoginService.login("a", "a");
        assertNotNull(utente);
        assertEquals("paziente", utente.getRuolo());
    }
    
    @Test
    public void testLoginPasswordErrata() {
        Utente utente = LoginService.login("a", "b");
        assertNull(utente);
    }
    
    @Test
    public void testLoginUtenteNonEsistente() {
        Utente utente = LoginService.login("m", "a");
        assertNull(utente);
    }
    
    @Test
    public void testLoginCampiVuoti() {
        Utente utente = LoginService.login("", "");
        assertNull(utente);
    }
}
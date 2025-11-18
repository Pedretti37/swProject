package application.admin;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import application.dao.FattoriComorbiditàAllergieDAO;
import application.dao.GlicemiaDAO;
import application.dao.MailDAO;
import application.dao.PatologiaDAO;
import application.dao.QuestionarioDAO;
import application.dao.TerapiaConcomitanteDAO;
import application.dao.TerapiaDAO;
import application.dao.UtenteDAO;
import application.model.FattoriComorbiditàAllergie;
import application.model.Glicemia;
import application.model.Mail;
import application.model.Patologia;
import application.model.Questionario;
import application.model.Terapia;
import application.model.TerapiaConcomitante;
import application.model.Utente;

public class Amministratore {
	
	public static DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
	public static DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
	
	// LISTE
	public static List<Utente> utenti = new ArrayList<>();
	public static List<Utente> pazienti = new ArrayList<>();
	public static List<Utente> diabetologi = new ArrayList<>();
	public static List<Terapia> terapie = new ArrayList<>();
	public static List<FattoriComorbiditàAllergie> fattoriDiRischio = new ArrayList<>();
	public static List<FattoriComorbiditàAllergie> comorbidità = new ArrayList<>();
	public static List<FattoriComorbiditàAllergie> allergie = new ArrayList<>();
	public static List<Patologia> patologie = new ArrayList<>();
	public static List<TerapiaConcomitante> terapieConcomitanti = new ArrayList<>();
	public static List<Glicemia> glicemia = new ArrayList<>();
	public static List<Mail> mail = new ArrayList<>();
	public static List<Questionario> questionari = new ArrayList<>();

    // DAO
    public static final TerapiaDAO terapiaDAO = new TerapiaDAO();
    public static final UtenteDAO utenteDAO = new UtenteDAO();
	public static final FattoriComorbiditàAllergieDAO fattoriComorbiditàAllergieDAO = new FattoriComorbiditàAllergieDAO();
	public static final PatologiaDAO patologiaDAO = new PatologiaDAO();
	public static final TerapiaConcomitanteDAO terapiaConcomitanteDAO = new TerapiaConcomitanteDAO();
	public static final GlicemiaDAO glicemiaDAO = new GlicemiaDAO();
	public static final MailDAO mailDAO = new MailDAO();
	public static final QuestionarioDAO questDAO = new QuestionarioDAO();
	
	// CARICA UTENTI DAL DATABASE
	public static void loadUtentiFromDatabase() {
	    utenti.clear();
		utenti.addAll(utenteDAO.getAllUtenti());
		System.out.println("[Amministratore] Utenti caricati: " + utenti.size());

	}
	
	// CARICA TERAPIE DAL DATABASE
	public static void loadTerapieFromDatabase() {
		terapie.clear();
        terapie.addAll(terapiaDAO.getAllTerapie());
        System.out.println("[Amministratore] Terapie caricate: " + terapie.size());
	}
	
	// CARICA STORIA DATI DAL DATABASE
	public static void loadFattoriComorbiditàAllergieFromDatabase() {
		fattoriDiRischio.clear();
		comorbidità.clear();
		allergie.clear();
		for(FattoriComorbiditàAllergie elemento : fattoriComorbiditàAllergieDAO.getAllFattoriComorbiditàAllergie()) {
			switch(elemento.getTipo()) {
			case "Fattore Di Rischio":
				fattoriDiRischio.add(elemento);
				break;
			case "Comorbidità":
				comorbidità.add(elemento);
				break;
			case "Allergia": 
				allergie.add(elemento);
				break;
			}
		}
		System.out.println("[Amministratore] Fattori Di Rischio caricati: " + fattoriDiRischio.size());
		System.out.println("[Amministratore] Comorbidità caricate: " + comorbidità.size());
		System.out.println("[Amministratore] Allergie caricate: " + allergie.size());
	}
	
	// CARICA GLICEMIA DAL DATABASE
	public static void loadGlicemiaFromDatabase() {
		glicemia.clear();
		glicemia.addAll(glicemiaDAO.getAllGlicemia());
		System.out.println("[Amministratore] Glicemie caricate: " + glicemia.size());
	}
	
	//CARICA MAIL DAL DATABASE
	public static void loadMailFromDatabase() {
		mail.clear();
		mail.addAll(mailDAO.getAllMail());
		System.out.println("[Amministratore] Mail caricate: " + mail.size());
	}
	
	// CARICA PATOLOGIE DAL DATABASE
	public static void loadPatologieFromDatabase() {
		patologie.clear();
		patologie.addAll(patologiaDAO.getAllPatologie());
		System.out.println("[Amministratore] Patologie caricate: " + patologie.size());
	}
	
	// CARICA TERAPIE CONCOMITANTI DAL DATABASE
	public static void loadTerapieConcomitantiFromDatabase() {
		terapieConcomitanti.clear();
		terapieConcomitanti.addAll(terapiaConcomitanteDAO.getAllTerapieConcomitanti());
		System.out.println("[Amministratore] Terapie Concomitanti caricate: " + terapieConcomitanti.size());
	}
	
	// CARICA QUESTIONARIO DAL DATABASE
	public static void loadQuestionarioFromDatabase() {
		questionari.clear();
		questionari.addAll(questDAO.getAllQuestionario());
		System.out.println("[Amministratore] Questionari caricati: " + questionari.size());
	}
	
	// CREA LISTE PAZIENTI E DIABETOLOGI
	public static void creaListe(){
		pazienti = utenti.stream()
				.filter(utente -> utente.getRuolo().equals("paziente"))
				.toList();
		diabetologi = utenti.stream()
				.filter(utente -> utente.getRuolo().equals("diabetologo"))
				.toList();
	}
	
	// METODO GENERALE DI CARICAMENTO DATI
	public static void loadFromDatabase() {
		loadUtentiFromDatabase();
		loadTerapieFromDatabase();
		loadGlicemiaFromDatabase();
		loadMailFromDatabase();
		loadFattoriComorbiditàAllergieFromDatabase();
		loadPatologieFromDatabase();
		loadTerapieConcomitantiFromDatabase();
		loadQuestionarioFromDatabase();
		
		creaListe();
	}

	//------------------------------------------
	// METODI DI ACCESSO RAPIDO
	//------------------------------------------

	// CONTROLLO ESISTENZA UTENTE
	public static boolean utenteEsiste(String cf) {
		return utenti.stream()
			.anyMatch(utente -> utente.getCf().equals(cf));
	}	
	
	// RITORNA UTENTE
	public static Utente getUtenteByCf(String cf) {
		return utenti.stream()
				.filter(utente -> utente.getCf().equals(cf))
				.findFirst()
				.orElse(null);
	}
	
	// RITORNA NOME UTENTE
	public static String getNomeUtenteByCf(String cf) {
		Utente u = getUtenteByCf(cf);
		if(u != null) {
			return u.getNomeCognome();
		}
		return null;
	}

	// CONTA LE MAIL NON LETTE RELAVITE A UN CERTO DESTINATARIO
	public static long contatoreMailNonLette() {
		Utente u = Sessione.getInstance().getUtente();
		return mail.stream()
				.filter(mail -> u.getMail().equals(mail.getDestinatario()))
				.filter(mail -> !mail.getLetta())
				.count();
	}

	// RICAVA TERAPIE DI UN PAZIENTE
	public static List<Terapia> getTerapieByCF(String cf) {
        List<Terapia> list = new ArrayList<>();
        for (Terapia t : terapie) {
            if (t.getCf().equals(cf)) {
                list.add(t);
            }
        }
        return list;
    }

	// RICAVA FATTORI DI RISCHIO DI UN PAZIENTE
	public static List<FattoriComorbiditàAllergie> getFattoriDiRischioByCF(String cf) {
		List<FattoriComorbiditàAllergie> list = new ArrayList<>();
		for (FattoriComorbiditàAllergie fca : fattoriDiRischio) {
			if (fca.getCF().equals(cf)) {
				list.add(fca);
			}
		}
		return list;
	}

	// RICAVA COMORBIDITÀ DI UN PAZIENTE
	public static List<FattoriComorbiditàAllergie> getComorbiditàByCF(String cf) {
		List<FattoriComorbiditàAllergie> list = new ArrayList<>();
		for (FattoriComorbiditàAllergie fca : comorbidità) {
			if (fca.getCF().equals(cf)) {
				list.add(fca);
			}
		}
		return list;
	}

	// RICAVA ALLERGIE DI UN PAZIENTE
	public static List<FattoriComorbiditàAllergie> getAllergieByCF(String cf) {
		List<FattoriComorbiditàAllergie> list = new ArrayList<>();
		for (FattoriComorbiditàAllergie fca : allergie) {
			if (fca.getCF().equals(cf)) {
				list.add(fca);
			}
		}
		return list;
	}

	// RICAVA PATOLOGIE DI UN PAZIENTE
	public static List<Patologia> getPatologieByCF(String cf) {
		List<Patologia> list = new ArrayList<>();
		for (Patologia p : patologie) {
			if (p.getCf().equals(cf)) {
				list.add(p);
			}
		}
		return list;
	}

	// RICAVA TERAPIE CONCOMITANTI DI UN PAZIENTE
	public static List<TerapiaConcomitante> getTerapieConcomitantiByCF(String cf) {
		List<TerapiaConcomitante> list = new ArrayList<>();
		for (TerapiaConcomitante tc : terapieConcomitanti) {
			if (tc.getCf().equals(cf)) {
				list.add(tc);
			}
		}
		return list;
	}
}
package application.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

import application.Amministratore;
import application.Database;
import application.MessageUtils;
import application.Sessione;
import application.model.Terapia;
import application.model.Utente;
import application.view.Navigator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class NuovaTerapiaController {
	
	private Utente u;
	private Utente p;
	
	// FIELD
	@FXML private TextField farmacoField;
	@FXML private TextField dosiGiornaliereField;
	@FXML private TextField quantitàField;
	@FXML private DatePicker dataInizioField;
	@FXML private DatePicker dataFineField;
	@FXML private TextArea indicazioniField;
	
	// LABEL
	@FXML private Label labelPaziente;
	
	// VARIABILI
	private String nomeFarmaco;
	private int dosiGiornaliere;
	private int quantità;
	private LocalDate dataInizio;
	private LocalDate dataFine;
	private int id;
	
	@FXML
	private void initialize() {
		u = Sessione.getInstance().getUtente();
		p = Sessione.getInstance().getPazienteSelezionato();
		
		labelPaziente.setText(p.getNomeCognome() + " (" + p.getCf() + ")");
	}
	
	@FXML
	private void handleTerapia(ActionEvent event) throws IOException {
		LocalDate fine = Amministratore.terapie.stream()
							.filter(terapia -> terapia.getCf().equals(p.getCf()))
							.map(Terapia::getDataFine)
							.findFirst()
							.orElse(null);
	    
	    try {
	    		nomeFarmaco = farmacoField.getText();
	        dosiGiornaliere = Integer.parseInt(dosiGiornaliereField.getText());
	        quantità = Integer.parseInt(quantitàField.getText());
	        dataInizio = dataInizioField.getValue();
	        dataFine = dataFineField.getValue();

	        if (dosiGiornaliere < 1 || quantità < 1 ||
	            dataInizio.isBefore(LocalDate.now()) ||
	            dataFine.isBefore(dataInizio) ||
	            dataFine.isEqual(dataInizio) ||
	            dataFine.isBefore(LocalDate.now())) {

	        		MessageUtils.showError("Per favore compila tutti i campi correttamente.");
	            return;
	        }

	    } catch (NullPointerException n) {
	    		MessageUtils.showError("Per favore compila tutti i campi obbligatori.");
	        return;
	    } catch (NumberFormatException n) {
	    		MessageUtils.showError("Per favore inserisci solo numeri nei campi numerici.");
	        return;
	    }
	    
	    if(fine != null) {
	    		if(dataInizio.isBefore(fine) || dataInizio.isEqual(fine)) {
	    			MessageUtils.showError("La data di inizio deve essere successiva "
	    					+ "alla fine dell'ultima terapia.\nL'ultima terapia finisce: " + fine);
		    		return;
	    		}
	    }
	    
	    	String query = "INSERT INTO terapie (CF, nomeFarmaco, dosiGiornaliere, quantità, dataInizio, dataFine, indicazioni, modificato, visualizzata) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
	    	try (Connection conn = Database.getConnection(); 
	    		PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
	
	    		stmt.setString(1, p.getCf());
	    		stmt.setString(2, nomeFarmaco);
	    	    stmt.setInt(3, dosiGiornaliere);
	    	    stmt.setInt(4, quantità);
	    	    stmt.setDate(5, java.sql.Date.valueOf(dataInizio));
	        stmt.setDate(6, java.sql.Date.valueOf(dataFine));
	        stmt.setString(7, indicazioniField.getText());
	        stmt.setString(8, u.getNomeCognome());
	        stmt.setBoolean(9, false);
	
	    	        
	        int rows = stmt.executeUpdate();

	        if (rows > 0) {
		        	try (ResultSet rs = stmt.getGeneratedKeys()) {
		                if (rs.next()) {
		                    id = rs.getInt(1); // recupera l'id auto_increment
		                }
		    		}
		    		Amministratore.terapie.add(new Terapia(
		    				id,
		    				p.getCf(),
		    	   			nomeFarmaco,
		    	   			dosiGiornaliere,
		    	   			quantità,
		    	   			dataInizio,
		    	   			dataFine,
		    	   			indicazioniField.getText(),
		    	   			u.getNomeCognome(),
		    	   			false
		    			));
		        	Amministratore.loadTerapieFromDatabase();
		        	MessageUtils.showSuccess("Terapia inserita.");
	            switchToMostraDatiPaziente(event);
	        } else {
	        		MessageUtils.showError("Errore nell'inserimento della terapia.");
	        }
	
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	@FXML
	private void switchToMostraDatiPaziente(ActionEvent event) throws IOException {
		Navigator.getInstance().switchToMostraDatiPaziente(event);
	}
}
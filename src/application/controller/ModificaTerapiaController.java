package application.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import application.controller.NuovaTerapiaController.TerapiaResult;
import application.model.Terapia;
import application.model.Utente;
import application.service.AdminService;
import application.utils.MessageUtils;
import application.utils.Sessione;
import application.view.Navigator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class ModificaTerapiaController {
	
	// VARIABILI
	private Utente u;
	private Utente p;
	private Terapia t; // oggetto in sessione
	private Terapia terapia; // oggetto che si prova a creare
	private List<Terapia> terapie = new ArrayList<>();
	private StringBuilder msg;
	
	// FIELD
	@FXML private TextField farmacoField;
	@FXML private TextField dosiGiornaliereField;
	@FXML private TextField quantitàField;
	@FXML private DatePicker dataInizioField;
	@FXML private DatePicker dataFineField;
	@FXML private TextArea indicazioniField;
	
	// LABEL
	@FXML private Label labelPaziente;
	@FXML private Label nomeFarmacoLabel;
	
	@FXML
	private void initialize() {
		u = Sessione.getInstance().getUtente();
		p = Sessione.getInstance().getPazienteSelezionato();
		t = Sessione.getInstance().getTerapiaSelezionata();
		
		terapie = AdminService.loadTerapieByPaziente(p);

		labelPaziente.setText(p.getNomeCognome() + " (" + p.getCf() + ")");
		nomeFarmacoLabel.setText(t.getNomeFarmaco());
	}

	public TerapiaResult tryModificaTerapia(String dosiGiornaliere, String quantità, LocalDate dataInizio, LocalDate dataFine, String indicazioni) {
		if(dataInizio == null || dataFine == null) {
			return TerapiaResult.EMPTY_FIELDS;
		}

		int dosiGiornaliereInt;
		int quantitàInt;
		try {
	        dosiGiornaliereInt = Integer.parseInt(dosiGiornaliere);
	        quantitàInt = Integer.parseInt(quantità);
	    } catch (NumberFormatException n) {
	    	return TerapiaResult.INVALID_DATA;
	    }

		if(dataInizio.isBefore(t.getDataInizio()) ||
				dataFine.isBefore(dataInizio) ||
				dataFine.isEqual(dataInizio) ||
				dataFine.isBefore(LocalDate.now()) ||
				dosiGiornaliereInt < 1 || quantitàInt < 1) {
			return TerapiaResult.INVALID_DATA;
		}
		
		// LISTA TERAPIA IN CONFLITTO

		List<Terapia> conflitti = terapie.stream()
			.filter(terapia -> terapia.getId() != t.getId()) // esclusione della terapia che sto modificando
			.filter(terapia -> terapia.getNomeFarmaco().equalsIgnoreCase(t.getNomeFarmaco())) // filtro sul farmaco
			.filter(terapia -> { // filtro sulla sovrapposizione delle date
				LocalDate esistenteInizio = terapia.getDataInizio();
				LocalDate esistenteFine = terapia.getDataFine();

				boolean startOverlap = !dataInizio.isAfter(esistenteFine);
				boolean endOverlap = !dataFine.isBefore(esistenteInizio);

				return startOverlap && endOverlap;
			})
			.collect(Collectors.toList());
		
		if(!conflitti.isEmpty()) {
			msg = new StringBuilder("Terapie in conflitto:\n");
			conflitti.forEach(terapia ->
					msg.append("- ").append(terapia.getNomeFarmaco()).append(": ")
					   .append(terapia.getDataInizio().format(AdminService.dateFormatter)).append(" -> ")
					   .append(terapia.getDataFine().format(AdminService.dateFormatter)).append("\n")
			);
			return TerapiaResult.INVALID_DATE_RANGE;
		}
		
		// Modifica della terapia nel database
		terapia = new Terapia(t.getId(), t.getCf(), t.getNomeFarmaco(), dosiGiornaliereInt, quantitàInt, dataInizio, dataFine, indicazioniField.getText(), u.getCf(), false);
		boolean ok = AdminService.terapiaDAO.modificaTerapia(terapia);

		if(ok) {
			return TerapiaResult.SUCCESS;
		} else {
			return TerapiaResult.FAILURE;
		}
	}
	
	@FXML
	private void handleModificaTerapia(ActionEvent event) throws IOException {
		TerapiaResult result = tryModificaTerapia(dosiGiornaliereField.getText(), quantitàField.getText(), dataInizioField.getValue(), dataFineField.getValue(), indicazioniField.getText());

		switch(result) {
			case EMPTY_FIELDS -> MessageUtils.showError("Per favore, compila tutti i campi.");
			case INVALID_DATA -> MessageUtils.showError("Dati non validi. Controlla le date e i numeri inseriti.");
			case INVALID_DATE_RANGE -> MessageUtils.showError(msg.toString());
			case FAILURE -> MessageUtils.showError("Errore durante la creazione della terapia.");
			case SUCCESS -> {
				Sessione.getInstance().setTerapiaSelezionata(terapia);
				MessageUtils.showSuccess("Terapia modificata con successo.");
				Navigator.getInstance().switchToMostraDettagliTerapia(event);
			}
		}
	}
	
	// NAVIGAZIONE
	@FXML
	private void switchToMostraDettagliTerapia(ActionEvent event) throws IOException {
		Navigator.getInstance().switchToMostraDettagliTerapia(event);
	}
}
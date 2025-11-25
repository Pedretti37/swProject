package application.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

public class NuovaTerapiaController {
	
	// VARIABILI
	private Utente u;
	private Utente p;
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
	
	@FXML
	private void initialize() {
		u = Sessione.getInstance().getUtente();
		p = Sessione.getInstance().getPazienteSelezionato();
		
		terapie = AdminService.loadTerapieByPaziente(p);

		labelPaziente.setText(p.getNomeCognome() + " (" + p.getCf() + ")");
	}

	public enum TerapiaResult {
		SUCCESS,
		FAILURE,
		INVALID_DATA,
		INVALID_DATE_RANGE,
		EMPTY_FIELDS,
	}

	public TerapiaResult tryCreateTerapia(String nomeFarmaco, String dosiGiornaliere, String quantità, LocalDate dataInizio, LocalDate dataFine, String indicazioni) {
		if(nomeFarmaco == null || nomeFarmaco.isBlank() ||
		   dataInizio == null || dataFine == null ||
		   dosiGiornaliere == null || dosiGiornaliere.isBlank() ||
		   quantità == null || quantità.isBlank()){
			return TerapiaResult.EMPTY_FIELDS;
		}

		int dosiGiornaliereInt;
		int quantitàInt;
		try{
			dosiGiornaliereInt = Integer.parseInt(dosiGiornaliere);
			quantitàInt = Integer.parseInt(quantità);
		} catch (NumberFormatException n) {
			return TerapiaResult.INVALID_DATA;
		}

		if(dataInizio.isBefore(LocalDate.now()) ||
				dataFine.isBefore(dataInizio) ||
				dosiGiornaliereInt < 1 || quantitàInt < 1) {
			return TerapiaResult.INVALID_DATA;
		}
		
		List<Terapia> conflitti = terapie.stream()
			.filter(terapia -> terapia.getNomeFarmaco().equalsIgnoreCase(nomeFarmaco)) // filtro sul farmaco
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

		// Creazione della terapia nel database
		Terapia t = new Terapia(0, p.getCf(), nomeFarmaco, dosiGiornaliereInt, quantitàInt, dataInizio, dataFine, indicazioni, u.getCf(), false);
		boolean ok = AdminService.terapiaDAO.creaTerapia(t);

		if(ok) {
			return TerapiaResult.SUCCESS;
		} else {
			return TerapiaResult.FAILURE;
		}
	}
	
	@FXML
	private void handleTerapia(ActionEvent event) throws IOException {
		TerapiaResult result = tryCreateTerapia(farmacoField.getText(), dosiGiornaliereField.getText(), quantitàField.getText(), dataInizioField.getValue(), dataFineField.getValue(), indicazioniField.getText());

		switch(result) {
			case EMPTY_FIELDS -> MessageUtils.showError("Per favore, compila tutti i campi.");
			case INVALID_DATA -> MessageUtils.showError("Dati non validi. Controlla le date e i numeri inseriti.");
			case INVALID_DATE_RANGE -> MessageUtils.showError(msg.toString());
			case FAILURE -> MessageUtils.showError("Errore durante la creazione della terapia.");
			case SUCCESS -> {
				MessageUtils.showSuccess("Terapia creata con successo.");
				Navigator.getInstance().switchToMostraDatiPaziente(event);
			}
		} 
	}
	
	@FXML
	private void switchToMostraDatiPaziente(ActionEvent event) throws IOException {
		terapie.clear();
		Navigator.getInstance().switchToMostraDatiPaziente(event);
	}
}
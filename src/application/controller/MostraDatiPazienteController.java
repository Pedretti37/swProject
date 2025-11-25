package application.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import application.model.FattoriComorbiditàAllergie;
import application.model.Glicemia;
import application.model.Patologia;
import application.model.Questionario;
import application.model.Terapia;
import application.model.TerapiaConcomitante;
import application.model.Utente;
import application.service.AdminService;
import application.utils.MessageUtils;
import application.utils.Sessione;
import application.view.Navigator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class MostraDatiPazienteController {

	//VARIABILI
	private Utente p;
	private String scelta;
	private LocalDate date;
	private LocalDate date2;
	private List<Glicemia> glicemia = new ArrayList<>();
	private List<Terapia> terapie = new ArrayList<>();
	private List<Questionario> questionari = new ArrayList<>();
	private List<FattoriComorbiditàAllergie> fattoriComorbiditàAllergie = new ArrayList<>();
	private List<TerapiaConcomitante> terapieConcomitanti = new ArrayList<>();
	private List<Patologia> patologie = new ArrayList<>();

	
	// GRAFICO
	@FXML private LineChart<String, Number> grafico;
	XYChart.Series<String, Number> serie = new XYChart.Series<>();

	
	//LABEL
	@FXML private Label labelPaziente;
	@FXML private Label dataDiNascitaDato;
	@FXML private Label sessoDato;
	@FXML private Label mailDato;
	@FXML private Label medicoRifLabel;
	@FXML private ComboBox<String> sceltaVisualizza;
	@FXML private DatePicker dataVisualizza;
	@FXML private ImageView fotoProfilo;
	
	//LISTE
	@FXML public ListView<String> listaTerapiePaziente;
	ObservableList<String> listaTerapiePazienteAsObservable = FXCollections.observableArrayList();
	
	@FXML public ListView<String> listaFattori;
	ObservableList<String> listaFattoriAsObservable = FXCollections.observableArrayList();
	
	@FXML public ListView<String> listaComorbidità;
	ObservableList<String> listaComorbiditàAsObservable = FXCollections.observableArrayList();
	
	@FXML public ListView<String> listaAllergie;
	ObservableList<String> listaAllergieAsObservable = FXCollections.observableArrayList();
	
	@FXML public ListView<String> listaPatologie;
	ObservableList<String> listaPatologieAsObservable = FXCollections.observableArrayList();
	
	@FXML public ListView<String> listaTerapieConcomitanti;
	ObservableList<String> listaTerapieConcomitantiAsObservable = FXCollections.observableArrayList();
	
	@FXML public ListView<String> listaQuestionari;
	ObservableList<String> listaQuestionariAsObservable = FXCollections.observableArrayList();
	
	@FXML
	private void initialize() {
		p = Sessione.getInstance().getPazienteSelezionato();
		
		caricaDatiPaziente();

		labelPaziente.setText("Profilo clinico di " + p.getNomeCognome());
		labelPaziente.setFocusTraversable(true);
		dataDiNascitaDato.setText(p.getDataDiNascita().format(AdminService.dateFormatter));
		sessoDato.setText(p.getSesso());
		mailDato.setText(p.getMail());

		Image image = new Image(p.getFoto());
		fotoProfilo.setImage(image);
		
		medicoRifLabel.setText(AdminService.getNomeUtenteByCf(p.getDiabetologoRif()) + " (" + p.getDiabetologoRif() + ")");
			
		sceltaVisualizza.getItems().addAll("Settimana", "Mese");
		
		try {
			visualizzaDati();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void caricaDatiPaziente() {
		glicemia = AdminService.loadGlicemiaByPaziente(p);
		terapie = AdminService.loadTerapieByPaziente(p);
		questionari = AdminService.loadQuestionariByPaziente(p);
		fattoriComorbiditàAllergie = AdminService.loadFattoriComorbiditàAllergieByPaziente(p);
		terapieConcomitanti = AdminService.loadTerapieConcomitantiByPaziente(p);
		patologie = AdminService.loadPatologieByPaziente(p);
	}
	
	public void visualizzaDati() throws IOException {
		// TERAPIE
		listaTerapiePazienteAsObservable = FXCollections.observableArrayList(
			terapie.stream()
				.map(t -> t.getNomeFarmaco() + " (" + t.getDataInizio().format(AdminService.dateFormatter) + " / " + t.getDataFine().format(AdminService.dateFormatter) + ")")
				.toList()
		);
		listaTerapiePaziente.setItems(listaTerapiePazienteAsObservable);
		
		// ENTRA IN UNA SPECIFICA TERAPIA
		listaTerapiePaziente.setOnMouseClicked(e -> {
			String selectedTerapia = listaTerapiePaziente.getSelectionModel().getSelectedItem();
			if(selectedTerapia != null) {
				terapie.stream()
					.filter(t -> (t.getNomeFarmaco() + " (" + t.getDataInizio().format(AdminService.dateFormatter) + " / " + t.getDataFine().format(AdminService.dateFormatter) + ")").equals(selectedTerapia))
					.findAny()
					.ifPresent(t -> {
						Sessione.getInstance().setTerapiaSelezionata(t);
					});
				
				try {
					clearAll();
					Navigator.getInstance().switchToMostraDettagliTerapia(e);
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		});
	
		// FATTORI DI RISCHIO
		listaFattoriAsObservable = FXCollections.observableArrayList(
			fattoriComorbiditàAllergie.stream()
				.filter(f -> f.getTipo().equals("Fattore Di Rischio"))
				.map(f -> f.getNome() + " Aggiunto da: " + AdminService.getNomeUtenteByCf(f.getModificato()) + ")")
				.toList()
		);
		listaFattori.setItems(listaFattoriAsObservable);
		
		// COMORBIDITÀ
		listaComorbiditàAsObservable = FXCollections.observableArrayList(
			fattoriComorbiditàAllergie.stream()
				.filter(c -> c.getTipo().equals("Comorbidità"))
				.map(c -> c.getNome() + " Aggiunto da: " + AdminService.getNomeUtenteByCf(c.getModificato()) + ")")
				.toList()
		);
		listaComorbidità.setItems(listaComorbiditàAsObservable);
		
		// ALLERGIE
		listaAllergieAsObservable = FXCollections.observableArrayList(
			fattoriComorbiditàAllergie.stream()
				.filter(a -> a.getTipo().equals("Allergia"))
				.map(a -> a.getNome() + " Aggiunto da: " + AdminService.getNomeUtenteByCf(a.getModificato()) + ")")
				.toList()
		);
		listaAllergie.setItems(listaAllergieAsObservable);
		
		// PATOLOGIE
		listaPatologieAsObservable = FXCollections.observableArrayList(
			patologie.stream()
				.map(p -> p.getNome())
				.toList()
		);
		listaPatologie.setItems(listaPatologieAsObservable);
		
		// ENTRA IN UNA SPECIFICA PATOLOGIA
		listaPatologie.setOnMouseClicked(e -> {
			String selectedPatologia = listaPatologie.getSelectionModel().getSelectedItem();
			if(selectedPatologia != null) {
				patologie.stream()
					.filter(patologia -> patologia.getNome().equals(selectedPatologia))
					.findFirst()
					.ifPresent(patologia -> {
						Sessione.getInstance().setPatologiaSelezionata(patologia);
					});
				
				try {
					clearAll();
					Navigator.getInstance().switchToMostraPatologia(e);
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		});
		
		// TERAPIE CONCOMITANTI
		listaTerapieConcomitantiAsObservable = FXCollections.observableArrayList(
			terapieConcomitanti.stream()
				.map(tc -> tc.getNome() + " (" + tc.getDataInizio().format(AdminService.dateFormatter) + ")")
				.toList()
		);
		listaTerapieConcomitanti.setItems(listaTerapieConcomitantiAsObservable);
		
		// ENTRA IN UNA SPECIFICA TERAPIA CONCOMITANTE
		listaTerapieConcomitanti.setOnMouseClicked(e -> {
			String selectedTerapiaConcomitante = listaTerapieConcomitanti.getSelectionModel().getSelectedItem();
			if(selectedTerapiaConcomitante != null) {
				terapieConcomitanti.stream()
					.filter(tc -> (tc.getNome() + " (" + tc.getDataInizio().format(AdminService.dateFormatter) + ")").equals(selectedTerapiaConcomitante))
					.findAny()
					.ifPresent(tc -> {
						Sessione.getInstance().setTerapiaConcomitanteSelezionata(tc);
					});
				
				try {
					clearAll();
					Navigator.getInstance().switchToMostraTerapiaConcomitante(e);
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		});
		
		// LISTA QUESTIONARI
		listaQuestionariAsObservable.addAll(
			questionari.stream()
				.filter(quest -> quest.getCf().equals(p.getCf()))
				.map(quest -> quest.getNomeFarmaco() + " (" + quest.getGiornoCompilazione().format(AdminService.dateFormatter) + ")")
				.toList()
			);
		listaQuestionari.setItems(listaQuestionariAsObservable);
		
		// ENTRA IN UNO SPECIFICO QUESTIONARIO
		listaQuestionari.setOnMouseClicked(e -> {
			String selectedQuestionario = listaQuestionari.getSelectionModel().getSelectedItem();
			if(selectedQuestionario != null) {
				questionari.stream()
					.filter(quest -> (quest.getNomeFarmaco() + " (" + quest.getGiornoCompilazione().format(AdminService.dateFormatter) + ")").equals(selectedQuestionario))
					.findAny()
					.ifPresent(quest -> {
						Sessione.getInstance().setQuestionarioSelezionato(quest);
					});
				
				try {
					clearAll();
					Navigator.getInstance().switchVediQuestionario(e);
				} catch (IOException ex) {
					ex.printStackTrace();
				}	
			}
		});
	}
	
	public enum SceltaResult {
		EMPTY_FIELD,
		DATE_IN_FUTURE,
		OK
	}

	public SceltaResult tryScelta(String scelta, LocalDate date) {
		if(date == null || scelta == null) {
			return SceltaResult.EMPTY_FIELD;
		} else if(date.isAfter(LocalDate.now())) {
			return SceltaResult.DATE_IN_FUTURE;
		}
		return SceltaResult.OK;
	}

	@FXML
	private void handleScelta() throws IOException {
		SceltaResult result = tryScelta(sceltaVisualizza.getValue(), dataVisualizza.getValue());

		switch(result) {
			case EMPTY_FIELD -> MessageUtils.showError("Scegli data e periodo.");
			case DATE_IN_FUTURE -> MessageUtils.showError("Scegliere una data antecedente a:\n" + LocalDate.now());
			case OK -> {
				scelta = sceltaVisualizza.getValue();
				date = dataVisualizza.getValue();
				if("Settimana".equals(scelta)) {
					date2 = date.plusDays(7);
					if(date2.isAfter(LocalDate.now()))
						date2 = LocalDate.now();
				} else if("Mese".equals(scelta)) {
					date2 = date.plusMonths(1);
					if(date2.isAfter(LocalDate.now()))
						date2 = LocalDate.now();
				}
					
				grafico.getData().clear(); // svuota il grafico
				serie.getData().clear();   // svuota la serie
				
				serie.getData().addAll(
					glicemia.stream()
					.filter(g -> g.getCf().equals(p.getCf())
								&& g.getGiorno().isAfter(date.minusDays(1))
								&& g.getGiorno().isBefore(date2.plusDays(1)))
					.sorted(Comparator.comparing(Glicemia::getGiorno))
					.map(g -> new XYChart.Data<String, Number>(g.getGiorno().toString(), g.getValore()))
					.toList()
				);
				
				grafico.getData().add(serie);
			}
		}
	}
	
	// SVUOTA LISTE
	private void clearAll() {
		terapie.clear();
		glicemia.clear();
		questionari.clear();
		fattoriComorbiditàAllergie.clear();
		patologie.clear();
		terapieConcomitanti.clear();
	}

	// NAVIGAZIONE
	@FXML
	private void switchToDiabetologoPage(ActionEvent event) throws IOException {
		clearAll();
		Sessione.getInstance().setPazienteSelezionato(null);
		Navigator.getInstance().switchToDiabetologoPage(event);
	}
	
	@FXML
	private void switchToNuovaTerapia(ActionEvent event) throws IOException {
		clearAll();
		Navigator.getInstance().switchToNuovaTerapia(event);
	}
	
	@FXML
	private void switchToStoriaDatiPaziente(ActionEvent event) throws IOException {
		clearAll();
		Navigator.getInstance().switchToStoriaDatiPaziente(event);
	}
}
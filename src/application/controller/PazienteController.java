package application.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import application.model.Glicemia;
import application.model.Mail;
import application.model.Questionario;
import application.model.Terapia;
import application.model.Utente;
import application.service.AdminService;
import application.utils.MessageUtils;
import application.utils.Sessione;
import application.view.Navigator;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class PazienteController {

	// VARIABILI
	private Utente p;
	private int compilato = 0;
	private int terapieAttive = 0;
	private List<Glicemia> glicemia = new ArrayList<>();
	private List<Terapia> terapie = new ArrayList<>();
	private List<Questionario> questionari = new ArrayList<>();
	private List<Mail> mailRicevute = new ArrayList<>();
	
	// GRAFICO
	@FXML private LineChart<String, Number> graficoGlicemia;
	@FXML private TextField valoreField;
	@FXML private TextField oraField;
	@FXML private TextField minutiField;
	@FXML private ComboBox<String> indicazioniBox;

	// FXML PAGINA
	@FXML private Label welcomeLabel;
	@FXML private Label statoQuestionarioOdierno;
	@FXML private Button mailButton;
	@FXML private Button questButton;

	// PROFILO
	@FXML private Label nomeLabel;
	@FXML private Label ddnLabel;
	@FXML private Label sessoLabel;
	@FXML private Label diabetologoLabel;
	@FXML private ImageView fotoProfilo;

	// TERAPIE CORRENTI
	@FXML private Label terapiaCorrente;
	@FXML private ListView<Terapia> terapieCorrenti;

	// QUESTIONARI
	@FXML private ListView<String> listaQuestionari;
	
	@FXML
	private void initialize() throws IOException {
		p = Sessione.getInstance().getUtente();
		
		caricaDatiPaziente();
		setUpInterfaccia();
		setUpTerapieInCorso();
		setUpQuestionari();
		setUpCompilazioneQuest();
	    visualizzaGrafico();
	    javafx.application.Platform.runLater(() -> notificaTerapia());
	} // FINE INITIALIZE ---------------------------------------------
	
	// CARICAMENTO E VISUALIZZAZIONE DATI
	private void caricaDatiPaziente() {
		glicemia = AdminService.loadGlicemiaByPaziente(p);
		terapie = AdminService.loadTerapieByPaziente(p);
		questionari = AdminService.loadQuestionariByPaziente(p);
		mailRicevute = AdminService.loadMailRicevute(p);
	}

	private void setUpInterfaccia() {
		welcomeLabel.setText("Ciao, " + p.getNomeCognome());
		welcomeLabel.setFocusTraversable(true);

		nomeLabel.setText(p.getNomeCognome());
		ddnLabel.setText(p.getDataDiNascita().format(AdminService.dateFormatter));
		sessoLabel.setText(p.getSesso());
		diabetologoLabel.setText(AdminService.getNomeUtenteByCf(p.getDiabetologoRif()));
		Image image = new Image(p.getFoto());
		fotoProfilo.setImage(image);

		mailButton.setText(AdminService.contatoreMailNonLette(mailRicevute) > 0 ? AdminService.contatoreMailNonLette(mailRicevute) + " Mail" : "Mail");
	    mailButton.setStyle(AdminService.contatoreMailNonLette(mailRicevute) > 0 ? "-fx-text-fill: red;" : "-fx-text-fill: black;");
	    
		indicazioniBox.getItems().clear();
		indicazioniBox.getItems().addAll("Pre pasto", "Post pasto");
	}

	private void notificaTerapia() {
		List<Terapia> terapieDaNotificare = terapie.stream()
			.filter(t -> !t.getVisualizzata() 
					&& !t.getDataInizio().isAfter(LocalDate.now()) 
					&& !t.getDataFine().isBefore(LocalDate.now()))
			.toList();

		boolean serveRicaricare = false;

		for (Terapia t : terapieDaNotificare) {
			Optional<ButtonType> result = MessageUtils.showConferma("Inizio terapia", "È iniziata una nuova terapia: " + t.getNomeFarmaco());

			if (result.isPresent() && result.get() == ButtonType.OK) {
				boolean ok = AdminService.terapiaDAO.notificaTerapia(t);
				if (ok) {
					serveRicaricare = true;
				} else {
					MessageUtils.showError("Errore nella lettura della notifica.");
				}
			}
		}

		if (serveRicaricare) {
			terapie = AdminService.loadTerapieByPaziente(Sessione.getInstance().getUtente());
			setUpTerapieInCorso();
		}
	}
	
	private void visualizzaGrafico() {
	    XYChart.Series<String, Number> serie = new XYChart.Series<>();
	    serie.setName("Glicemia giornaliera");

	    for(Glicemia glicemia : glicemia) {
	    	if(glicemia.getCf().equals(p.getCf()) && glicemia.getGiorno().isEqual(LocalDate.now())) {
	    		
	    		final int valore = glicemia.getValore();
	            final String orario = glicemia.getOrario();
	            final String indicazioni = glicemia.getIndicazioni();
	            //final LocalDate giorno = glicemia.getGiorno();
	    		
	    		XYChart.Data<String, Number> punto = new XYChart.Data<>(orario, valore);
	    		
	    		punto.nodeProperty().addListener((obs, oldNode, newNode) -> {
	                if (newNode != null) {
	                    if(indicazioni.equals("Pre pasto")) {
	                    	if(valore < 80 || valore > 130)
		                        newNode.setStyle("-fx-background-color: red;");
		                    else
		                        newNode.setStyle("-fx-background-color: green;");
	                    } else if(indicazioni.equals("Post pasto")) {
	                    	if(valore > 180)
	                    		newNode.setStyle("-fx-background-color: red;");
	                    	else
	                    		newNode.setStyle("-fx-background-color: green;");
	                    }
	                }
	            });
	    		
	    		serie.getData().add(punto);
	    	}
	    }
	    
	    graficoGlicemia.getData().clear(); //cancella la precedente
        graficoGlicemia.getData().add(serie);
	}
	
	private void setUpTerapieInCorso() {
		List<Terapia> listaFiltrata = terapie.stream()
				.filter(t -> !t.getDataInizio().isAfter(LocalDate.now()) && !t.getDataFine().isBefore(LocalDate.now()))
				.collect(Collectors.toList());
		terapieCorrenti.setItems(FXCollections.observableArrayList(listaFiltrata));

		terapieCorrenti.setCellFactory(e -> new ListCell<Terapia>() {
		    protected void updateItem(Terapia t, boolean empty) {
		        super.updateItem(t, empty);
		        
		        if (empty || t == null) {
		            setText(null);
		            setStyle("");
		        } else {
		        	setText("Farmaco: " + t.getNomeFarmaco() + "\nData inizio: " + t.getDataInizio().format(AdminService.dateFormatter) + "\nData fine: " + t.getDataFine().format(AdminService.dateFormatter));
		        }
		    }
		});

		terapieCorrenti.setOnMouseClicked(e -> {
			Terapia selectedTerapia = terapieCorrenti.getSelectionModel().getSelectedItem();
			if(selectedTerapia != null) {
				Sessione.getInstance().setTerapiaSelezionata(selectedTerapia);
				
				try {
					Navigator.getInstance().switchToMostraDettagliTerapia(e);
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		});

		terapieAttive = listaFiltrata.size();
	}

	private void setUpQuestionari() {
		List<String> quest = questionari.stream()
				.map(q -> q.getNomeFarmaco() + " (" + q.getGiornoCompilazione().format(AdminService.dateFormatter) + ")")
				.toList();
		listaQuestionari.setItems(FXCollections.observableArrayList(quest));
		// ENTRA IN UNO SPECIFICO QUESTIONARIO
		listaQuestionari.setOnMouseClicked(e -> {
			String selectedQuestionario = listaQuestionari.getSelectionModel().getSelectedItem();
			if(selectedQuestionario != null) {
				questionari.stream()
					.filter(q -> (q.getNomeFarmaco() + " (" + q.getGiornoCompilazione().format(AdminService.dateFormatter) + ")").equals(selectedQuestionario))
					.findAny()
					.ifPresent(q -> {
						Sessione.getInstance().setQuestionarioSelezionato(q);
					});
				
				try {
					Navigator.getInstance().switchVediQuestionario(e);
				} catch (IOException ex) {
					ex.printStackTrace();
				}	
			}
		});
	}

	private void setUpCompilazioneQuest() {
		for(Questionario q : questionari) {
			if(q.getGiornoCompilazione().equals(LocalDate.now())) {
				compilato++;
			}
		}

		if(terapieAttive > 0) {
			if(compilato < terapieAttive)
				statoQuestionarioOdierno.setText("Questionario odierno da compilare!");
			else if(compilato == terapieAttive) {
				statoQuestionarioOdierno.setText("Questionari odierni compilati!");
				questButton.setDisable(true);
			}
		}
		else {
			questButton.setDisable(true);
			statoQuestionarioOdierno.setText("Nessun questionario da compilare!");
		}
	}

	// GESTIONE GLICEMIA PAZIENTE
	public enum GlicemiaResult {
		EMPTY_FIELDS,
		INVALID_DATA,
		SUCCESS,
		FAILURE
	}
	public GlicemiaResult tryCreateGlicemia(String valore, String ora, String minuti, String indicazioni) {
		if (valore.isEmpty() || ora.isEmpty() || minuti.isEmpty() || indicazioni == null) {
	    	return GlicemiaResult.EMPTY_FIELDS;
	    }
		
		int oraInt, minutiInt, valoreInt;
		try {
			oraInt = Integer.parseInt(ora);
	        minutiInt = Integer.parseInt(minuti);
	        valoreInt = Integer.parseInt(valore);
		} catch (NumberFormatException e) {
	        return GlicemiaResult.INVALID_DATA;
	    }

		if (oraInt < 0 || oraInt > 23) {
	        return GlicemiaResult.INVALID_DATA;
	    }
		if (ora.length() == 1) ora = "0" + ora;

	    if (minutiInt < 0 || minutiInt > 59) {
			return GlicemiaResult.INVALID_DATA;
	    }
	    if (minuti.length() == 1) minuti = "0" + minuti;

		String orario = ora + ":" + minuti;

	    Glicemia g = new Glicemia(p.getCf(), valoreInt, LocalDate.now(), orario, indicazioni);
		boolean ok = AdminService.glicemiaDAO.creaGlicemia(g);
		if(ok) {
			if(!graficoGlicemia.getData().isEmpty()) {
				XYChart.Series<String, Number> serie = graficoGlicemia.getData().get(0);

				XYChart.Data<String, Number> dataPoint = new XYChart.Data<>(orario, valoreInt);

				dataPoint.nodeProperty().addListener((obs, oldNode, newNode) -> {
					if (newNode != null) {
						if("Pre pasto".equals(indicazioni)) {
							if(valoreInt < 80 || valoreInt > 130)
								newNode.setStyle("-fx-background-color: red;");
							else
								newNode.setStyle("-fx-background-color: green;");
						} else if("Post pasto".equals(indicazioni)) {
							if(valoreInt > 180)
								newNode.setStyle("-fx-background-color: red;");
							else
								newNode.setStyle("-fx-background-color: green;");
						}
					}
				});

				serie.getData().add(dataPoint);
			}
			valoreField.clear();
			oraField.clear();
			minutiField.clear();
			glicemia.add(g); // aggiungo la glicemia appena creata alla lista, senza andare a richiamare il db per caricarle tutte nuovamente
			return GlicemiaResult.SUCCESS;
		}
		else {
			return GlicemiaResult.FAILURE;
		}
	}
	@FXML
	private void handleGlicemia(ActionEvent event) throws IOException {
		GlicemiaResult result = tryCreateGlicemia(valoreField.getText().trim(), oraField.getText().trim(), minutiField.getText().trim(), indicazioniBox.getValue());

		switch(result) {
			case EMPTY_FIELDS -> MessageUtils.showError("Per favore, compila tutti i campi.");
			case INVALID_DATA -> MessageUtils.showError("Compila i dati correttamente.");
			case FAILURE -> MessageUtils.showError("Errore durante l'inserimento della glicemia.");
			case SUCCESS -> {
				MessageUtils.showSuccess("Glicemia aggiunta con successo!");
				indicazioniBox.getItems().clear();
				indicazioniBox.getItems().addAll("Pre pasto", "Post pasto");
			}
		}
	}

	// SVUOTA LISTE
	private void clearAll() {
		glicemia.clear();
		terapie.clear();
		questionari.clear();
		mailRicevute.clear();
	}

	// NAVIGAZIONE
	@FXML
	private void switchToLogin(ActionEvent event) throws IOException {
		clearAll();
		Sessione.getInstance().logout();
		Navigator.getInstance().switchToLogin(event);
	}
	
	@FXML
	private void switchToMailPage(ActionEvent event) throws IOException {
		clearAll();
		Navigator.getInstance().switchToMailPage(event);
	}
	
	@FXML
	private void switchToQuestionarioPage(ActionEvent event) throws IOException {
		clearAll();
		Navigator.getInstance().switchToQuestionarioPage(event);
	}
}
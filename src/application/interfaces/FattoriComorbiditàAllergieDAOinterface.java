package application.interfaces;

import java.util.List;

import application.model.FattoriComorbiditàAllergie;

public interface FattoriComorbiditàAllergieDAOinterface {
    public List<FattoriComorbiditàAllergie> getAllFattoriComorbiditàAllergie();
    public boolean creaFattoreComorbiditàAllergia(FattoriComorbiditàAllergie fca);
    public boolean eliminaFattoreComorbiditàAllergia(FattoriComorbiditàAllergie fca);
}
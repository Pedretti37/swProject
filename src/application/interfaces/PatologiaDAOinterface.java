package application.interfaces;

import java.util.List;

import application.model.Patologia;

public interface PatologiaDAOinterface {
    public List<Patologia> getAllPatologie();
    public boolean creaPatologia(Patologia patologia);
    public boolean eliminaPatologia(Patologia Patologia);
}
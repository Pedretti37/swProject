package application.interfaces;

import java.util.List;

import application.model.Glicemia;

public interface GlicemiaDAOinterface {
    public List<Glicemia> getAllGlicemia();
    public boolean creaGlicemia(Glicemia glicemia);
}
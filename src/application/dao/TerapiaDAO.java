package application.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import application.admin.Amministratore;
import application.admin.Database;
import application.model.Terapia;

public class TerapiaDAO implements application.interfaces.TerapiaDAOinterface {

    @Override
    public boolean creaTerapia(Terapia t) {
        // Implementazione del metodo per creare una terapia nel database
        String query = "INSERT INTO terapie (CF, nomeFarmaco, dosiGiornaliere, quantità, dataInizio, dataFine, indicazioni, diabetologo, visualizzata) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
		try (Connection conn = Database.getConnection(); 
			PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

			stmt.setString(1, t.getCf());
			stmt.setString(2, t.getNomeFarmaco());
			stmt.setInt(3, t.getDosiGiornaliere());
			stmt.setInt(4, t.getQuantità());
			stmt.setDate(5, java.sql.Date.valueOf(t.getDataInizio()));
			stmt.setDate(6, java.sql.Date.valueOf(t.getDataFine()));
			stmt.setString(7, t.getIndicazioni());
			stmt.setString(8, t.getDiabetologo());
			stmt.setBoolean(9, t.getVisualizzata());

				
			int rows = stmt.executeUpdate();
			if (rows > 0) {
				try (ResultSet rs = stmt.getGeneratedKeys()) {
					if (rs.next()) {
						int id = rs.getInt(1); // recupera l'id auto_increment
						t.setId(id); // imposta l'id nell'oggetto Terapia
					}
				}
				Amministratore.loadTerapieFromDatabase();
				return true;
			} else {
				return false;
			}
	
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
    }

    @Override
    public boolean eliminaTerapia(Terapia t) {
        // Implementazione del metodo per rimuovere una terapia dal database
        String sql = "DELETE FROM terapie WHERE id = ?";
        try (Connection conn = Database.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, t.getId());  // id della riga da eliminare
            int rowsDeleted = stmt.executeUpdate();

            if (rowsDeleted > 0) {
                Amministratore.loadTerapieFromDatabase();
                return true;
            } else {
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean modificaTerapia(Terapia t) {
        // Implementazione del metodo per modificare una terapia nel database
        String query = "UPDATE terapie SET dosiGiornaliere = ?, quantità = ?, dataInizio = ?, dataFine = ?, indicazioni = ?, diabetologo = ? WHERE id = ?";
		try (Connection conn = Database.getConnection(); 
			PreparedStatement stmt = conn.prepareStatement(query)) {

	        stmt.setInt(1, t.getDosiGiornaliere());
	        stmt.setInt(2, t.getQuantità());
	        stmt.setDate(3, java.sql.Date.valueOf(t.getDataInizio()));
	        stmt.setDate(4, java.sql.Date.valueOf(t.getDataFine()));
	        stmt.setString(5, t.getIndicazioni());
	        stmt.setString(6, t.getDiabetologo());
	        stmt.setInt(7, t.getId());
	        
	        int rows = stmt.executeUpdate();
	        if (rows > 0) {
				Amministratore.loadTerapieFromDatabase();
				return true;
	        } else {
	        	return false;
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
            return false;
	    }
    }

	public List<Terapia> getAllTerapie() {
		// Implementazione del metodo per ottenere tutte le terapie dal database
		List<Terapia> lista = new ArrayList<>();
		String query = "SELECT * FROM terapie ORDER BY dataInizio DESC";
		try (Connection conn = Database.getConnection(); 
			PreparedStatement stmt = conn.prepareStatement(query);
			ResultSet rs = stmt.executeQuery()) {

			while (rs.next()) {
				Terapia terapia = new Terapia(
					rs.getInt("id"),
					rs.getString("cf"),
					rs.getString("nomeFarmaco"),
					rs.getInt("dosiGiornaliere"),
					rs.getInt("quantità"),
					rs.getDate("dataInizio") != null ? rs.getDate("dataInizio").toLocalDate() : null,
                    rs.getDate("dataFine") != null ? rs.getDate("dataFine").toLocalDate() : null,
					rs.getString("indicazioni"),
					rs.getString("diabetologo"),
					rs.getBoolean("visualizzata")
				);
				lista.add(terapia);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return lista;
	}
}
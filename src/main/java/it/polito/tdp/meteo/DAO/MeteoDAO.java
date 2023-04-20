package it.polito.tdp.meteo.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polito.tdp.meteo.model.Citta;
import it.polito.tdp.meteo.model.Rilevamento;

public class MeteoDAO {
	/**
	 * 
	 * @return tutti i rilevamenti presenti nel database per data decrescente 
	 */
	public List<Rilevamento> getAllRilevamenti() {

		final String sql = "SELECT Localita, Data, Umidita FROM situazione ORDER BY data ASC";

		List<Rilevamento> rilevamenti = new ArrayList<Rilevamento>();

		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);

			ResultSet rs = st.executeQuery();

			while (rs.next()) {

				Rilevamento r = new Rilevamento(rs.getString("Localita"), rs.getDate("Data"), rs.getInt("Umidita"));
				rilevamenti.add(r);
			}

			conn.close();
			return rilevamenti;

		} catch (SQLException e) {

			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public List<Rilevamento> getAllRilevamentiLocalitaMese(int mese, String localita) {

		final String sql = "SELECT s.* "
				+ "FROM situazione s "
				+ "WHERE MONTH(Data)  = ? AND s.Localita = ? ";

		List<Rilevamento> rilevamentiLocalitaMese = new ArrayList<Rilevamento>();

		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			
			st.setInt(1, mese);
			st.setString(2, localita);
			
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				
				Rilevamento r = new Rilevamento(rs.getString("Localita"), rs.getDate("Data"), rs.getInt("Umidita"));
				rilevamentiLocalitaMese.add(r);
			}
			st.close();
			rs.close();
			conn.close();
			return rilevamentiLocalitaMese;

		} catch (SQLException e) {

			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public List<Citta> getAllCitta () {

		final String sql = "SELECT DISTINCT localita "
				+ "FROM situazione ";

		List<Citta> allCitta = new ArrayList<Citta>();

		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			
			
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				
				Citta c = new Citta(rs.getString("localita"));
				allCitta.add(c);
			}
			st.close();
			rs.close();
			conn.close();
			return allCitta;

		} catch (SQLException e) {

			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

}

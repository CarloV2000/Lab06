package it.polito.tdp.meteo.DAO;

import java.util.List;

import it.polito.tdp.meteo.model.Rilevamento;

public class TestMeteoDAO {

	public static void main(String[] args) {
		
		MeteoDAO dao = new MeteoDAO();

		List<Rilevamento> list = dao.getAllRilevamenti();
		//List<Double>umiditaMedie = ;

		// STAMPA: localita, giorno, mese, anno, umidita (%)
		for (Rilevamento r : list) {
			System.out.format("%-10s %2td/%2$2tm/%2$4tY %3d%%\n", r.getLocalita(), r.getData(), r.getUmidita());
			
		}
		
	System.out.println(dao.getAllRilevamentiLocalitaMese(7, "Genova"));//stampa solo le umidita perche e stato modificato il toString()
	//System.out.println(dao.getAvgRilevamentiLocalitaMese(1, "Genova"));	
	System.out.println(dao.getAllRilevamentiLocalitaMese(7, "Milano"));
	//System.out.println(dao.getAvgRilevamentiLocalitaMese(5, "Milano"));	
	System.out.println(dao.getAllRilevamentiLocalitaMese(7, "Torino"));
	//System.out.println(dao.getAvgRilevamentiLocalitaMese(5, "Torino"));
		

	}

}

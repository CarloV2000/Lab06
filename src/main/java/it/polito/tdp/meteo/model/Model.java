package it.polito.tdp.meteo.model;

import java.sql.Date;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import it.polito.tdp.meteo.DAO.MeteoDAO;

public class Model {
	
	private final static int COST = 100;
	private final static int NUMERO_GIORNI_CITTA_CONSECUTIVI_MIN = 3;
	private final static int NUMERO_GIORNI_CITTA_MAX = 6;
	private final static int NUMERO_GIORNI_TOTALI = 15;
	private MeteoDAO meteoDAO;
	private List<Rilevamento>rilevamenti;
	private List<Rilevamento>rilevamentiDatiMeseELocalita;
	List<Citta>citta;
	List<Citta>best;
	
	public Model() {
		this.meteoDAO = new MeteoDAO();
		this.rilevamenti = this.getAllRilevamenti();
		this.citta = this.meteoDAO.getAllCitta(); 
		
	}
		
	/**
	 * metodo che ritorna tutte le umidita
	 * @return
	 */
	public List<Rilevamento> getAllRilevamenti() {
		return meteoDAO.getAllRilevamenti();
	}
	
	/**
	 * metodo che ritorna tutte le umidita nel mese e nella localita data
	 * @return
	 */
	public List<Rilevamento> getAllRilevamentiLocalitaMese(int mese, String localita) {
		return meteoDAO.getAllRilevamentiLocalitaMese(mese, localita);
	}
	
	/**
	 * metodo per calcolo umidita nel mese selezionato
	 * @param mese è il mese scelto dall'utente
	 * @return lista di tre double (umidita media nel mese selezionato in ordine TO, MI, GE)
	 */
	public List<Double> getUmiditaMedia(int mese) {
		double mediaTO = 0.0;
		double mediaMI = 0.0;
		double mediaGE = 0.0;
		int sommaTO = 0;
		int numeroRilevamentiTO = 0;
		int sommaMI = 0;
		int numeroRilevamentiMI = 0;
		int sommaGE = 0;
		int numeroRilevamentiGE = 0;
		
		List<Double>result = new ArrayList<Double>();
		for(Rilevamento x : this.rilevamenti) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(x.getData());
			int month = cal.get(Calendar.MONTH)+1;
					
			if(month==mese/*il mese è lo stesso*/) {
				if(x.getLocalita().strip().compareTo("Torino")==0) {
					sommaTO += x.getUmidita();
					numeroRilevamentiTO++;
				}
				if(x.getLocalita().strip().compareTo("Milano")==0) {
					sommaMI += x.getUmidita();
					numeroRilevamentiMI++;
				}
				if(x.getLocalita().strip().compareTo("Genova")==0) {
					sommaGE += x.getUmidita();
					numeroRilevamentiGE++;				
				}
			}
		}
		mediaTO = sommaTO/numeroRilevamentiTO;
		mediaMI = sommaMI/numeroRilevamentiMI;
		mediaGE = sommaGE/numeroRilevamentiGE;
		result.add(mediaTO);
		result.add(mediaMI);
		result.add(mediaGE);
		return result;
	}

	
	
	
	/**
	 * metodo che chiama la ricorsione per il calcolo del percorso a costo minimo
	 * @param mese è il mese inserito dall'utente
	 * @return una lista di Citta rappresentanti le citta in cui stare giorno per giorno 
	 */
	public List<Citta> trovaSequenza(int mese) {
		List<Citta>parziale = new ArrayList<Citta>();
		this.best = null;
		
		for(Citta x : this.citta) {
			x.setRilevamenti(this.getAllRilevamentiLocalitaMese(mese, x.getNome()));
		}
		cerca(parziale, 0);
		return best;
	}
	/**
	 * Procedura ricorsiva per il calcolo delle città ottimali
	 * Per informazioni sull'impostazione della ricorsione, vedere il file logica_della_ricorsione.txt
	 * nella cartella di progetto 
	 * @param parziale soluzione parziale in via di costruzione
	 * @param livello livello della ricorsione, cioè il giorno a cui si sta cercando di definire la città
	 */
	private void cerca(List<Citta> parziale, int livello) {
		
		if (livello == NUMERO_GIORNI_TOTALI) {//caso terminale
			Double costo = calcolaCosto(parziale);
			if (best == null || costo < calcolaCosto(best)) {
				best = new ArrayList<>(parziale);
			}
		}
		else {
			//caso intermedio
			for (Citta prova: citta) {
				if (aggiuntaValida(prova,parziale)) {
					parziale.add(prova);
					cerca(parziale, livello+1);
					parziale.remove(parziale.size()-1);
				}
			}			
		}
	}
	
	/**
	 * metodo per il calcolo del costo giornaliero nel mese e nella localita selezionata
	 * @param mese
	 * @param localita
	 * @return un double indicante il costo giornaliero
	 */
	public double calcolaCosto(List<Citta>parziale) {
		Double costo = 0.0;
		for (int giorno=1; giorno<=NUMERO_GIORNI_TOTALI; giorno++) {
			//dove mi trovo
			Citta c = parziale.get(giorno-1);
			//che umidità ho in quel giorno in quella città?
			double umid = c.getRilevamenti().get(giorno-1).getUmidita();
			costo+=umid;
		}
		//poi devo sommare 100*numero di volte in cui cambio città
		for (int giorno=2; giorno<=NUMERO_GIORNI_TOTALI; giorno++) {
			//dove mi trovo
			if(!parziale.get(giorno-1).equals(parziale.get(giorno-2))) {
				costo +=COST;
			}
		}
		return costo;
	}
	
private boolean aggiuntaValida(Citta prova, List<Citta> parziale) {
		
		//verifica giorni massimi
		//contiamo quante volte la città 'prova' era già apparsa nell'attuale lista costruita fin qui
		int conta = 0;
		for (Citta precedente:parziale) {
			if (precedente.equals(prova))
				conta++; 
		}
		if (conta >=NUMERO_GIORNI_CITTA_MAX)
			return false;
		
		// verifica dei giorni minimi
		if (parziale.size()==0) //primo giorno posso inserire qualsiasi città
				return true;
		if (parziale.size()==1 || parziale.size()==2) {
			//siamo al secondo o terzo giorno, non posso cambiare
			//quindi l'aggiunta è valida solo se la città di prova coincide con la sua precedente
			return parziale.get(parziale.size()-1).equals(prova); 
		}
		//nel caso generale, se ho già passato i controlli sopra, non c'è nulla che mi vieta di rimanere nella stessa città
		//quindi per i giorni successivi ai primi tre posso sempre rimanere
		if (parziale.get(parziale.size()-1).equals(prova))
			return true; 
		// se cambio città mi devo assicurare che nei tre giorni precedenti sono rimasto fermo 
		if (parziale.get(parziale.size()-1).equals(parziale.get(parziale.size()-2)) 
		&& parziale.get(parziale.size()-2).equals(parziale.get(parziale.size()-3)))
			return true;
			
		return false;
		
	}

	

}

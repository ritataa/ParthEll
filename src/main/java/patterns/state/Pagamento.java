package patterns.state;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import patterns.state.impl.DaPagareState;
import patterns.state.impl.PagamentoConfermatoState;
import patterns.state.impl.PagamentoState;

/*
 * LEGENDA: STANDARD DI DOCUMENTAZIONE JAVADOC
 * @author / @version: Tracciano paternita, responsabilita e manutenzione della classe.
 * @param: Definisce i vincoli di input richiesti da un metodo.
 * @return: Specifica l'output garantito dal metodo per il Client.
 */
/**
 * Rappresenta un pagamento con i dati anagrafici, l'importo e lo stato corrente.
 * Centralizza la gestione dello stato per distinguere tra pagamento da confermare e gia confermato.
 * Usa il pattern State per isolare il comportamento variabile dal modello dati.
 *
 * Pagamento è il contesto (l'oggetto che rappresenta il pagamento). 
 * Contiene i dati (id, mese, importo, promo, ecc.) e una variabile 'state' che punta a un PagamentoState. 
 * Non implementa direttamente il comportamento variabile: lo DELEGA allo 'state'
 * 
 * @author ParthEll Team
 * @version 1.0
 */

    
    
public class Pagamento {
    private final SimpleIntegerProperty id;         // SimpleIntegerProperty: È un numero intero (int) potenziato per JavaFX. 
                                                    // Serve a collegare il numero alla grafica: se il valore cambia nel codice, la tabella o lo schermo si aggiornano in automatico.
    private final SimpleStringProperty idAbbonato;  // SimpleStringProperty: È una Stringa potenziata per JavaFX. 
                                                    // Serve a collegare il testo alla grafica in modo intelligente: se modifico il testo qui nel codice, la scritta sullo schermo si aggiorna da sola.
    private final SimpleStringProperty mese;
    private final SimpleIntegerProperty anno;
    private final SimpleDoubleProperty importo;     // SimpleDoubleProperty: È un numero con la virgola (double) potenziato per JavaFX. 
                                                    // Serve a collegare valori come prezzi o saldi alla grafica, permettendo allo schermo di aggiornarsi da solo quando il valore cambia.
    private final SimpleStringProperty stato;
    private final SimpleStringProperty promo;
    private PagamentoState state;

    /**
     * Crea un pagamento completo e inizializza lo stato coerente con il valore ricevuto.
     * Il parametro stato viene normalizzato per mantenere una rappresentazione interna stabile.
     *
     * @param id identificativo numerico del pagamento.
     * @param idAbbonato identificativo dell'abbonato associato.
     * @param mese mese di riferimento non nullo.
     * @param anno anno di riferimento.
     * @param importo importo del pagamento.
     * @param stato stato iniziale atteso, preferibilmente "Da pagare" o "Pagato".
     * @param promo codice promozionale associato.
     */
    public Pagamento(int id, String idAbbonato, String mese, int anno, double importo, String stato, String promo) {
        this.id = new SimpleIntegerProperty(id);
        this.idAbbonato = new SimpleStringProperty(idAbbonato);
        this.mese = new SimpleStringProperty(mese);
        this.anno = new SimpleIntegerProperty(anno);
        this.importo = new SimpleDoubleProperty(importo);
        this.stato = new SimpleStringProperty(stato);
        this.promo = new SimpleStringProperty(promo);
        this.state = resolveState(stato);
    }

    /**
     * Restituisce l'identificativo numerico del pagamento.
     *
     * @return id del pagamento.
     */
    public int getId() {
        return id.get();
    }

    /**
     * Aggiorna l'identificativo del pagamento con un valore numerico valido.
     *
     * @param value nuovo identificativo da salvare.
     */
    public void setId(int value) {
        id.set(value);
    }

    /**
     * Restituisce l'identificativo dell'abbonato collegato al pagamento.
     *
     * @return id dell'abbonato, non nullo se il modello e coerente.
     */
    public String getIdAbbonato() {
        return idAbbonato.get();
    }

    /**
     * Aggiorna l'identificativo dell'abbonato associato.
     *
     * @param value nuovo id dell'abbonato.
     */
    public void setIdAbbonato(String value) {
        idAbbonato.set(value);
    }

    /**
     * Restituisce il mese di competenza del pagamento.
     *
     * @return mese associato al pagamento.
     */
    public String getMese() {
        return mese.get();
    }

    /**
     * Imposta il mese di riferimento del pagamento.
     *
     * @param value nuovo mese da memorizzare.
     */
    public void setMese(String value) {
        mese.set(value);
    }

    /**
     * Restituisce l'anno di competenza del pagamento.
     *
     * @return anno associato al pagamento.
     */
    public int getAnno() {
        return anno.get();
    }

    /**
     * Aggiorna l'anno di riferimento del pagamento.
     *
     * @param value nuovo anno da salvare.
     */
    public void setAnno(int value) {
        anno.set(value);
    }

    /**
     * Restituisce l'importo corrente del pagamento.
     *
     * @return importo registrato, espresso in doppia precisione.
     */
    public double getImporto() {
        return importo.get();
    }

    /**
     * Aggiorna l'importo del pagamento.
     *
     * @param value nuovo importo da salvare.
     */
    public void setImporto(double value) {
        importo.set(value);
    }

    /**
     * Restituisce il nome logico dello stato corrente.
     *
     * @return stato corrente non nullo, derivato dall'oggetto State.
     */
    public String getStato() {
        return state.getNome();
    }

    /**
     * Sincronizza lo stato interno a partire da una descrizione testuale.
     *
     * @param value stato esterno da interpretare; se non corrisponde a "Da pagare" viene considerato confermato.
     */
    public void setStato(String value) {
        // Normalizza il testo in uno stato concreto per evitare confronti sparsi nel codice.
        this.state = resolveState(value);
        stato.set(this.state.getNome());
    }

    /**
     * Restituisce il codice promozionale associato al pagamento.
     *
     * @return promo memorizzata nel modello.
     */
    public String getPromo() {
        return promo.get();
    }

    /**
     * Aggiorna il codice promozionale associato al pagamento.
     *
     * @param value nuovo codice promo.
     */
    public void setPromo(String value) {
        promo.set(value);
    }

    /**
     * Indica se il pagamento puo essere confermato nello stato attuale.
     *
     * @return true se lo stato consente il pagamento, false altrimenti.
     */
    public boolean isPagabile() {
        return state.canBePaid();
    }

    /**
     * Conferma il pagamento e aggiorna lo stato interno in modo coerente.
     * Se lo stato corrente non consente l'operazione, la logica e demandata all'implementazione concreta dello State.
     */
    public void confermaPagamentoState() {
        System.out.println("[ATTO 4 - 14. STATE PAGAMENTO] Richiesta conferma pagamento: transizione verso stato successivo.");
    

        state = state.pay();            // * Il Contesto non contiene strutture condizionali (if/else). Delega interamente 
                                        // * allo stato corrente la responsabilità di valutare la validità dell'azione 
                                        // * e di restituire l'istanza dello stato successivo. La variabile 'state' 
                                        // * si sovrascrive dinamicamente a runtime (es: da DaPagare a PagamentoConfermato).

        stato.set(state.getNome());     // * stato (con la 'o' finale) è la proprietà JavaFX di tipo SimpleStringProperty.
                                        // * Attraverso il metodo .set(), andiamo ad aggiornare il testo racchiuso in questa proprietà chiedendolo direttamente al nuovo stato appena installato (state.getNome()).
                                        // * Poiché la tabella o la UI della tua dashboard è "in ascolto" su questa proprietà, la scritta sullo schermo cambia istantaneamente da "Da pagare" a "Pagamento confermato" 
                                        // * senza bisogno di rinfrescare la pagina a mano.
    }

    // resolveState è private (metodo non virtuale), quindi non può essere sovrascritto da eventuali sottoclassi.
    // Questo rende la chiamata dal costruttore sicura (non si rischia che venga invocata una versione di sottoclasse non ancora inizializzata).
    private PagamentoState resolveState(String statoPagamento) {
        // Traduco il testo letto da persistence/UI in una strategia di stato precisa.
        if (statoPagamento != null && "Da pagare".equalsIgnoreCase(statoPagamento.trim())) {
            return new DaPagareState();
        }
        return new PagamentoConfermatoState();
    }
}

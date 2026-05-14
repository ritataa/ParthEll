package service;

import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.PianoTariffario;
import model.Promozione;
import model.Utilizzo;
import patterns.state.Pagamento;

/*
 * LEGENDA: STANDARD DI DOCUMENTAZIONE JAVADOC
 * @author / @version: tracciano paternità e manutenzione della classe.
 * @param: definisce i vincoli e il significato degli input.
 * @return: definisce l'output garantito o nullo per il chiamante.
 */

/**
    Coordina il caricamento dei dati necessari alla dashboard cliente.
    Centralizza le letture e i calcoli per mantenere la UI pulita e coerente. 
    Implementa il pattern Service Layer per gestire le "regole" dell'applicazione.
    Scopo: Fa da ponte tra l'interfaccia grafica (Controller) e i dati (Repository), contenendo la vera logica di business.
    Ruolo nell'MVC: Mantiene i Controller "puliti". Il Controller raccoglie le azioni dell'utente, le passa a questo Service che fa i calcoli o i controlli necessari, e poi il Service delega al Repository il salvataggio o la lettura nel database.
    Utilizza il Repository Pattern tramite TelecomRepository per query centralizzate.
    Scopo: Nascondere tutta la complessità delle query SQL (JDBC) al resto del programma.
    Ruolo nell'MVC: È il livello più profondo del Model. Si occupa di tradurre il linguaggio 
    del database (SQL) nel linguaggio del nostro programma (Oggetti Java), mantenendo i Service 
    e i Controller completamente ignari di come funziona il database.
 *
 * @author ParthEll Team
 * @version 1.0
 */
public class ClienteDataService {

    private final TelecomRepository repository;

    /**
     * Costruisce il servizio con il repository già pronto all'uso.
     *
     * @param repository repository non nullo usato per accedere ai dati.
     */
    public ClienteDataService(TelecomRepository repository) {
        this.repository = repository;
    }

    /**
     * Restituisce il nome del cliente associato all'email corrente.
     * Può restituire null se il repository non trova alcun record.
     *
     * @param email email da cercare, non vuota per ottenere un risultato utile.
     * @return nome del cliente oppure null se l'utente non esiste.
     */
    public String findNomeCliente(String email) {
        return repository.findNomeByEmail(email);
    }

    /**
     * Aggiorna il pagamento del mese corrente solo se l'email è valida.
     * In questo modo il service evita chiamate inutili verso il repository.
     *
     * @param email email del cliente, non vuota per attivare l'aggiornamento.
     */
    public void aggiornaPagamentoMeseCorrente(String email) {
        if (email != null && !email.isBlank()) {
            // Filtro minimo per evitare lavoro inutile sul database.
            repository.aggiornaPagamentoMeseCorrente(email);
        }
    }

    /**
     * Carica tutte le promozioni disponibili.
     *
     * @return lista delle promozioni presenti a database.
     */
    public List<Promozione> loadPromozioni() {
        return repository.findAllPromozioni();
    }

    /**
     * Carica lo storico pagamenti oppure una lista vuota se l'email non è valida.
     *
     * @param email email del cliente; se vuota viene evitata la query.
     * @return storico pagamenti osservabile o lista vuota come fallback sicuro.
     */
    public ObservableList<Pagamento> loadStoricoPagamenti(String email) {
        if (email == null || email.isBlank()) {
            return FXCollections.observableArrayList();
        }
        return repository.getStoricoPagamenti(email);
    }

    /**
     * Carica un'istantanea completa della situazione cliente per la UI.
     * Restituisce una snapshot con valori null quando l'email non è valida.
     *
     * @param email email del cliente da analizzare.
     * @return snapshot con utilizzo e piano tariffario, oppure valori null di fallback.
     */
    public ClienteSnapshot loadSnapshot(String email) {
        if (email == null || email.isBlank()) {
            return new ClienteSnapshot(null, null);
        }
        // Aggancio i due dati letti separatamente per semplificare la UI.
        Utilizzo utilizzo = repository.findUtilizzoByEmail(email);
        PianoTariffario pianoTariffario = repository.findPianoTariffarioByEmail(email);
        return new ClienteSnapshot(utilizzo, pianoTariffario);
    }

    /**
     * Calcola il totale mensile aggiornando prima il mese corrente.
     * Ritorna 0.0 quando l'email non è valida.
     *
     * @param email email del cliente da calcolare.
     * @return totale mensile calcolato oppure 0.0 come valore di sicurezza.
     */
    public double calcolaTotaleMensile(String email) {
        if (email == null || email.isBlank()) {
            return 0.0;
        }
        // Mantengo allineato lo stato pagamenti prima di esporre il totale.
        aggiornaPagamentoMeseCorrente(email);
        return repository.calcolaTotaleMensileByEmail(email);
    }

    /**
     * Conferma il pagamento del mese richiesto delegando al repository.
     *
     * @param email email del cliente.
     * @param mese mese da saldare.
     * @param anno anno del pagamento.
     * @return true se il saldo viene applicato, false se non esiste la riga.
     */
    public boolean saldaPagamento(String email, String mese, int anno) {
        return repository.saldaPagamento(email, mese, anno);
    }

    /**
     * DTO immutabile con utilizzo e piano tariffario correnti.
     * Serve a trasportare alla UI un blocco dati già coerente e facile da leggere.
     */
    public record ClienteSnapshot(Utilizzo utilizzo, PianoTariffario pianoTariffario) {
    }
}

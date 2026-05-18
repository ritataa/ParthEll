package patterns.proxy;

import java.util.List;

import javafx.collections.ObservableList;
import model.PianoTariffario;
import model.Promozione;
import model.Utilizzo;
import patterns.builder.Abbonato;
import patterns.state.Pagamento;

/**
 * Contratto comune tra il repository reale e il proxy.
 * Espone le stesse operazioni pubbliche di {@link service.TelecomRepository}
 * senza legare i client alla classe concreta.
 *
 * @author ParthEll Team
 * @version 1.0
 */
public interface ITelecomRepository {

    /**
     * Verifica se l'utente esiste come amministratore o come cliente.
     * Restituisce il ruolo trovato oppure null se le credenziali non sono valide.
     *
     * @param email email non vuota dell'utente da autenticare.
     * @param password password associata all'account.
     * @return "admin", "cliente" o null se nessuna corrispondenza è valida.
     * @throws RuntimeException se l'accesso al database fallisce.
     */
    String authenticate(String email, String password);

    /**
     * Carica tutti gli abbonati ordinati per cognome e nome.
     *
     * @return lista completa degli abbonati presenti a database.
     * @throws RuntimeException se la lettura JDBC fallisce.
     */
    List<Abbonato> findAllAbbonati();

    /**
     * Cerca un abbonato tramite email e ricostruisce anche il conto associato.
     * Restituisce null quando l'email non esiste o è vuota.
     *
     * @param email email non vuota da cercare.
     * @return l'abbonato trovato oppure null se assente.
     * @throws RuntimeException se il database non risponde correttamente.
     */
    Abbonato findAbbonatoByEmail(String email);

    /**
     * Restituisce tutti gli utilizzi con il riepilogo delle promozioni attive.
     *
     * @return lista di utilizzi ordinata per numero telefonico.
     * @throws RuntimeException se la query di aggregazione fallisce.
     */
    List<Utilizzo> findAllUtilizzi();

    /**
     * Restituisce tutte le promozioni presenti nel catalogo.
     *
     * @return lista completa delle promozioni disponibili.
     * @throws RuntimeException se la lettura del catalogo fallisce.
     */
    List<Promozione> findAllPromozioni();

    /**
     * Restituisce i nomi di tutti i piani tariffari ordinati alfabeticamente.
     *
     * @return lista dei nomi piano usabili dall'interfaccia.
     * @throws RuntimeException se la query fallisce.
     */
    List<String> findAllPianiTariffari();

    /**
     * Crea un nuovo cliente con valori base e numero telefonico generato.
     *
     * @param email email univoca del cliente.
     * @param password password da salvare per l'accesso.
     * @param nome nome del cliente.
     * @param cognome cognome del cliente.
     * @throws RuntimeException se l'inserimento o la sincronizzazione falliscono.
     */
    void addCliente(String email, String password, String nome, String cognome);

    /**
     * Registra un cliente completo verificando unicità di email e telefono.
     * Restituisce solo una eccezione RuntimeException in caso di dati non validi o errori DB.
     *
     * @param email email non già presente.
     * @param password password dell'account.
     * @param nome nome del cliente.
     * @param cognome cognome del cliente.
     * @param residenza indirizzo non vuoto.
     * @param numeroTelefono numero telefonico non già presente.
     * @param pianoTariffario piano tariffario esistente.
     * @param conto tipo conto valido.
     * @param numeroCarta numero carta opzionale.
     * @param scadenzaCarta scadenza carta opzionale.
     * @param cvvCarta cvv carta opzionale.
     * @param intestatarioCarta intestatario carta opzionale.
     * @throws RuntimeException se un vincolo viene violato o il database fallisce.
     */
    void registerCliente(
        String email,
        String password,
        String nome,
        String cognome,
        String residenza,
        String numeroTelefono,
        String pianoTariffario,
        String conto,
        String numeroCarta,
        String scadenzaCarta,
        String cvvCarta,
        String intestatarioCarta
    );

    /**
     * Variante compatta della registrazione con dati opzionali assenti.
     *
     * @param email email non già presente.
     * @param password password dell'account.
     * @param nome nome del cliente.
     * @param cognome cognome del cliente.
     * @param residenza indirizzo non vuoto.
     * @param numeroTelefono numero telefonico non già presente.
     * @param pianoTariffario piano tariffario esistente.
     * @param conto tipo conto valido.
     * @throws RuntimeException se i controlli di registrazione falliscono.
     */
    void registerCliente(
        String email,
        String password,
        String nome,
        String cognome,
        String residenza,
        String numeroTelefono,
        String pianoTariffario,
        String conto
    );

    /**
     * Variante minima della registrazione che usa il conto fisso di default.
     *
     * @param email email non già presente.
     * @param password password dell'account.
     * @param nome nome del cliente.
     * @param cognome cognome del cliente.
     * @param residenza indirizzo non vuoto.
     * @param numeroTelefono numero telefonico non già presente.
     * @param pianoTariffario piano tariffario esistente.
     * @throws RuntimeException se i controlli di registrazione falliscono.
     */
    void registerCliente(
        String email,
        String password,
        String nome,
        String cognome,
        String residenza,
        String numeroTelefono,
        String pianoTariffario
    );

    /**
     * Aderisce una promozione esistente per un abbonato.
     *
     * @param email email dell'abbonato già registrato.
     * @param nomePromozione nome della promozione da associare.
     * @return true se l'associazione viene inserita, false se era già presente.
     * @throws RuntimeException se la promozione non esiste o il database fallisce.
     */
    boolean aderisciPromozione(String email, String nomePromozione);

    /**
     * Rimuove l'associazione tra un abbonato e una promozione.
     *
     * @param email email dell'abbonato.
     * @param nomePromozione promozione da disdire.
     * @return true se almeno una riga è stata rimossa, false altrimenti.
     * @throws RuntimeException se l'operazione JDBC fallisce.
     */
    boolean disdiciPromozione(String email, String nomePromozione);

    /**
     * Aggiorna il saldo del conto dell'abbonato identificato dalla email.
     *
     * @param email email dell'abbonato da aggiornare.
     * @param nuovoSaldo nuovo saldo desiderato; i valori negativi vengono portati a zero.
     * @return true se il record è stato aggiornato, false se l'email non è valida o assente.
     * @throws RuntimeException se il database non consente l'update.
     */
    boolean aggiornaSaldoConto(String email, double nuovoSaldo);

    /**
     * Recupera l'utilizzo di un singolo abbonato, creando prima la riga se manca.
     *
     * @param email email dell'abbonato da cercare.
     * @return utilizzo trovato oppure un oggetto vuoto se non esiste alcun risultato.
     * @throws RuntimeException se il recupero o la sincronizzazione falliscono.
     */
    Utilizzo findUtilizzoByEmail(String email);

    /**
     * Inserisce una nuova promozione nel catalogo.
     *
     * @param nome nome univoco della promozione.
     * @param costo costo della promozione.
     * @param descrizione descrizione testuale del beneficio.
     * @throws RuntimeException se l'inserimento SQL fallisce.
     */
    void addPromozione(String nome, double costo, String descrizione);

    /**
     * Calcola il totale mensile dato piano tariffario e promozioni attive.
     *
     * @param email email dell'abbonato.
     * @return totale mensile calcolato, oppure 0.0 se non esiste alcun dato utile.
     * @throws RuntimeException se la query di aggregazione fallisce.
     */
    double calcolaTotaleMensileByEmail(String email);

    /**
     * Allinea o crea il pagamento del mese corrente per l'abbonato.
     *
     * @param email email dell'abbonato da aggiornare.
     * @throws RuntimeException se il recupero o l'aggiornamento dei pagamenti fallisce.
     */
    void aggiornaPagamentoMeseCorrente(String email);

    /**
     * Restituisce lo storico pagamenti dell'abbonato ordinato dal più recente.
     *
     * @param emailAbbonato email dell'abbonato di riferimento.
     * @return lista osservabile dei pagamenti associati all'utente.
     * @throws RuntimeException se la lettura dello storico fallisce.
     */
    ObservableList<Pagamento> getStoricoPagamenti(String emailAbbonato);

    /**
     * Salda il pagamento di un mese specifico dopo il controllo dei parametri.
     *
     * @param email deve identificare un abbonato e non puo' essere vuota.
     * @param mese deve contenere il mese da saldare.
     * @param anno deve essere maggiore di zero.
     * @return {@code true} se il pagamento viene saldato, altrimenti {@code false}.
     * @throws IllegalArgumentException se un input obbligatorio e' invalido.
     */
    boolean saldaPagamento(String email, String mese, int anno);

    /**
     * Inizializza lo storico del nuovo utente delegando al repository reale.
     *
     * @param email deve identificare l'utente da inizializzare e non puo' essere vuota.
     * @throws IllegalArgumentException se l'email non e' valida.
     */
    void inizializzaStoricoNuovoUtente(String email);

    /**
     * Restituisce l'elenco dei nomi promozione già associati all'abbonato.
     *
     * @param email email dell'abbonato.
     * @return lista ordinata di promozioni attive.
     * @throws RuntimeException se la lettura delle associazioni fallisce.
     */
    List<String> findPromozioniAttiveByEmail(String email);

    /**
     * Recupera il nome associato a una email dopo la normalizzazione dell'input.
     *
     * @param email deve identificare un abbonato; i valori vuoti vengono delegati come null.
     * @return il nome trovato oppure {@code null} se non disponibile.
     */
    String findNomeByEmail(String email);

    /**
     * Recupera il piano tariffario collegato alla email normalizzata.
     *
     * @param email deve identificare un abbonato; i valori vuoti vengono trasformati in null.
     * @return il piano tariffario associato oppure {@code null} se non disponibile.
     */
    PianoTariffario findPianoTariffarioByEmail(String email);

    /**
     * Registra una chiamata solo se email e minuti sono validi.
     *
     * @param email deve identificare un abbonato e non puo' essere vuota.
     * @param minuti deve essere maggiore di zero.
     * @throws IllegalArgumentException se l'email e' invalida o i minuti non sono positivi.
     */
    void registraChiamata(String email, int minuti);

    /**
     * Registra un SMS per l'utente dopo la validazione minimale dell'email.
     *
     * @param email deve identificare un abbonato e non puo' essere vuota.
     * @throws IllegalArgumentException se l'email non e' valida.
     */
    void registraSms(String email);

    /**
     * Registra il consumo dati solo se l'input e' coerente.
     *
     * @param email deve identificare un abbonato e non puo' essere vuota.
     * @param mb deve essere maggiore di zero.
     * @throws IllegalArgumentException se l'email e' invalida o i MB non sono positivi.
     */
    void registraDati(String email, int mb);
}
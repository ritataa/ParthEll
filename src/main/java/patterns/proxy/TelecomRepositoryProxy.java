package patterns.proxy;

import java.util.List;

import javafx.collections.ObservableList;
import model.PianoTariffario;
import model.Promozione;
import model.Utilizzo;
import patterns.builder.Abbonato;
import patterns.state.Pagamento;
import service.TelecomRepository;

/*
 * LEGENDA: STANDARD DI DOCUMENTAZIONE JAVADOC
 * @author / @version: Tracciano paternita' e manutenzione della classe.
 * @param: Definisce i vincoli di input che il chiamante deve rispettare.
 * @return: Definisce l'output garantito per la sicurezza del Client.
 * @throws: Esplicita le eccezioni gestibili dal chiamante.
 */

/**
 * Proxy del repository applicativo che intercetta le chiamate e le inoltra a
 * {@link TelecomRepository} dopo una normalizzazione minima dei dati.
 * Usa il pattern Proxy per centralizzare validazione, logging e policy senza
 * modificare i client che dipendono dal repository.
 *
 * @author ParthEll Team
 * @version 1.0
 */
public class TelecomRepositoryProxy extends TelecomRepository {

    private final TelecomRepository target = new TelecomRepository(); // Questo e' il repository vero a cui passo tutte le richieste.
                                                                      // Private perche' deve restare nascosto dentro il proxy, cosi' nessuno da fuori lo tocca direttamente e il proxy puo' controllare sempre il passaggio dei dati.


    // Questo metodo prende un messaggio e lo scrive negli errori per tenere traccia di cio' che fa il proxy.
    // Private perche' deve essere usato solo dentro questa classe, cosi' nessun altro puo' chiamarlo direttamente e il controllo dei messaggi resta qui dentro.
    private void log(String message) { 
        System.err.println("[TelecomRepositoryProxy] " + message);
    }

    // Questo controllo prende un testo obbligatorio, blocca i valori vuoti o mancanti e lo restituisce pulito.
    // Private perche' serve solo a questo proxy, cosi' la regola resta interna e non puo' essere usata o saltata dall'esterno
    private String requireText(String value, String fieldName) { 
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " non può essere vuoto");
        }
        return value.trim();
    }

    // Questo metodo pulisce un testo che puo' anche essere null: se arriva null restituisce null, altrimenti lo pulisce con trim(), cioè un metodo Java che rimuove gli spazi bianchi iniziali e finali di una stringa.
    // Private perche' serve solo dentro questo proxy per normalizzare i dati senza bloccare nulla, resto interno
    private String normalizeText(String value) { 
        if (value == null) {
            return null;
        } else {
            return value.trim();
        }
    }

    /**
     * Esegue l'autenticazione delegando al repository reale dopo il trim degli input.
     * Restituisce {@code null} se email o password sono mancanti o vuoti.
     *
     * @param email deve contenere un indirizzo valido, non vuoto dopo la normalizzazione.
     * @param password deve contenere una password valida, non vuota dopo la normalizzazione.
     * @return il ruolo autenticato oppure {@code null} se le credenziali non sono utilizzabili.
     */
    
    // Sicurezza: @Override obbliga Java a verificare che sto davvero ridefinendo authenticate() ereditato da TelecomRepository, evitando errori di firma.
    @Override

    // Questo blocco pulisce l'email e la password togliendo gli spazi, poi controlla che nessuno dei due sia vuoto: se uno e' vuoto torna null senza fare niente.
    // Se entrambi sono validi, scrive un messaggio di traccia e manda la richiesta al repository vero per fare il controllo autenticazione.
    public String authenticate(String email, String password) {
        String normalizedEmail = normalizeText(email);
        String normalizedPassword = normalizeText(password);
        if (normalizedEmail == null || normalizedEmail.isBlank() || normalizedPassword == null || normalizedPassword.isBlank()) {
            return null;
        }

        log("Autenticazione richiesta per " + normalizedEmail);
        return target.authenticate(normalizedEmail, normalizedPassword);
    }

    /**
     * Restituisce tutti gli abbonati delegando integralmente al repository reale.
     *
     * @return la lista degli abbonati letti dal database, mai filtrata dal proxy.
     */
    
    // Sicurezza: @Override conferma che il metodo viene da TelecomRepository e non da un overload locale.
    @Override

    // Questo metodo passa il controllo al repository vero e chiede di restituire TUTTI gli abbonati dal database.
    // <Abbonato> è il nome tra le parentesi angolate che dice: "Questa lista contiene SOLO Abbonati, non altri oggetti".
    // Così Java sa che tutti gli elementi dentro List sono di tipo Abbonato.
    // Il proxy non filtra nulla, solo riceve quello che il database invia e lo passa a chi lo chiede.
    public List<Abbonato> findAllAbbonati() {
        return target.findAllAbbonati();
    }

    /**
     * Cerca un abbonato tramite email normalizzando prima il valore.
     * Restituisce {@code null} quando l'email e' assente o non produce risultati.
     *
     * @param email deve essere una stringa che identifichi l'utente; i soli spazi portano a {@code null}.
     * @return l'abbonato trovato oppure {@code null} se non esiste.
     */
    
    
    // Sicurezza: @Override conferma l'implementazione del metodo findAbbonatoByEmail() ereditato da TelecomRepository.
    @Override

    // Questo metodo prende un'email e cerca UN SOLO abbonato dentro il database usando quella email.
    // Prima pulisce l'email con normalizeText() togliendo gli spazi, poi chiede a target di trovare l'abbonato.
    // target è il repository vero, cioè il database vero dove vivono i dati reali degli abbonati.
    // Se l'abbonato con quella email esiste nel database, il proxy lo restituisce; se non esiste, restituisce null.
    // Il proxy non fa il lavoro, delega tutto a target: è come un intermediario che pulisce l'email e poi passa la richiesta.
    public Abbonato findAbbonatoByEmail(String email) {
        return target.findAbbonatoByEmail(normalizeText(email));
    }

    /**
     * Restituisce tutti gli utilizzi registrati delegando al repository reale.
     *
     * @return la lista completa degli utilizzi letti dal database.
     */
    
    // Sicurezza: @Override collega il metodo alla versione definita in TelecomRepository.
    @Override

    // Questo metodo passa il controllo al repository vero e chiede di restituire TUTTI gli utilizzi dal database.
    // <Utilizzo> è il nome tra le parentesi angolate che dice: "Questa lista contiene SOLO Utilizzi, non altri oggetti".
    // Utilizzo è una classe che rappresenta un uso (come chiamate, SMS, dati) fatto da un abbonato nel database.
    // Così Java sa che tutti gli elementi dentro List sono di tipo Utilizzo.
    // Il proxy non filtra nulla, solo riceve quello che il database invia e lo passa a chi lo chiede.
    public List<Utilizzo> findAllUtilizzi() {
        return target.findAllUtilizzi();
    }

    /**
     * Restituisce tutte le promozioni disponibili senza aggiungere logica locale.
     *
     * @return la lista completa delle promozioni presenti nel repository.
     */
    
    // Sicurezza: @Override assicura che questo proxy stia ridefinendo findAllPromozioni() di TelecomRepository.
    @Override

    // Questo metodo passa il controllo al repository vero e chiede di restituire TUTTE le promozioni dal database.
    // <Promozione> è il nome tra le parentesi angolate che dice: "Questa lista contiene SOLO Promozioni, non altri oggetti".
    // Promozione è una classe che rappresenta un'offerta speciale (come sconti o piani agevolati) che il provider telefonico mette a disposizione nel database.
    // Così Java sa che tutti gli elementi dentro List sono di tipo Promozione.
    // Il proxy non filtra nulla, solo riceve quello che il database invia e lo passa a chi lo chiede.
    public List<Promozione> findAllPromozioni() {
        return target.findAllPromozioni();
    }

    /**
     * Restituisce l'elenco dei piani tariffari disponibili.
     *
     * @return la lista dei piani letti dal repository reale.
     */
    
    // Sicurezza: @Override conferma il contratto ereditato da TelecomRepository.
    @Override

    // Questo metodo passa il controllo al repository vero e chiede di restituire TUTTI i piani tariffari dal database.
    // <String> è il nome tra le parentesi angolate che dice: "Questa lista contiene SOLO stringhe (testi), non altri oggetti".
    // String è il tipo di Java che rappresenta un pezzo di testo, come il nome di un piano tariffario.
    // Così Java sa che tutti gli elementi dentro List sono di tipo String, cioè nomi di piani.
    // Il proxy non filtra nulla, solo riceve quello che il database invia e lo passa a chi lo chiede.
    public List<String> findAllPianiTariffari() {
        return target.findAllPianiTariffari();
    }

    /**
     * Registra un cliente con i soli dati base dopo averli validati e ripuliti.
     * Lancia {@link IllegalArgumentException} se uno dei campi obbligatori e' vuoto.
     *
     * @param email deve contenere un indirizzo non vuoto.
     * @param password deve contenere una password non vuota.
     * @param nome deve contenere il nome del cliente.
     * @param cognome deve contenere il cognome del cliente.
     * @param residenza deve contenere la residenza del cliente.
     * @param numeroTelefono deve contenere un numero di telefono non vuoto.
     * @param pianoTariffario deve contenere un piano tariffario non vuoto.
     * @throws IllegalArgumentException se uno dei parametri obbligatori e' nullo o vuoto.
     */
    // Sicurezza: @Override conferma che sto ridefinendo la firma di registerCliente() dichiarata in TelecomRepository.
    @Override
    public void registerCliente(
        String email,
        String password,
        String nome,
        String cognome,
        String residenza,
        String numeroTelefono,
        String pianoTariffario
    ) {
        String normalizedEmail = requireText(email, "Email");
        String normalizedPassword = requireText(password, "Password");
        String normalizedNome = requireText(nome, "Nome");
        String normalizedCognome = requireText(cognome, "Cognome");
        String normalizedResidenza = requireText(residenza, "Residenza");
        String normalizedNumeroTelefono = requireText(numeroTelefono, "Numero di telefono");
        String normalizedPiano = requireText(pianoTariffario, "Piano tariffario");

        log("Registrazione cliente base per " + normalizedEmail);
        target.registerCliente(
            normalizedEmail,
            normalizedPassword,
            normalizedNome,
            normalizedCognome,
            normalizedResidenza,
            normalizedNumeroTelefono,
            normalizedPiano
        );
    }

    /**
     * Registra un cliente con conto, validando i campi obbligatori prima della delega.
     * Lancia {@link IllegalArgumentException} se un dato richiesto non e' presente.
     *
     * @param email deve contenere un indirizzo non vuoto.
     * @param password deve contenere una password non vuota.
     * @param nome deve contenere il nome del cliente.
     * @param cognome deve contenere il cognome del cliente.
     * @param residenza deve contenere la residenza del cliente.
     * @param numeroTelefono deve contenere un numero di telefono non vuoto.
     * @param pianoTariffario deve contenere un piano tariffario non vuoto.
     * @param conto deve contenere il tipo di conto da associare.
     * @throws IllegalArgumentException se un parametro obbligatorio e' nullo o vuoto.
     */
    // Sicurezza: @Override conferma la sovrascrittura del metodo registerCliente() a 8 parametri ereditato da TelecomRepository.
    @Override
    public void registerCliente(
        String email,
        String password,
        String nome,
        String cognome,
        String residenza,
        String numeroTelefono,
        String pianoTariffario,
        String conto
    ) {
        System.out.println("[ATTO 1 - 4. PROXY REPOSITORY] Intercetto registrazione (conto) e delego il salvataggio al repository reale.");
        String normalizedEmail = requireText(email, "Email");
        String normalizedPassword = requireText(password, "Password");
        String normalizedNome = requireText(nome, "Nome");
        String normalizedCognome = requireText(cognome, "Cognome");
        String normalizedResidenza = requireText(residenza, "Residenza");
        String normalizedNumeroTelefono = requireText(numeroTelefono, "Numero di telefono");
        String normalizedPiano = requireText(pianoTariffario, "Piano tariffario");
        String normalizedConto = requireText(conto, "Conto");

        log("Registrazione cliente con conto per " + normalizedEmail);
        target.registerCliente(
            normalizedEmail,
            normalizedPassword,
            normalizedNome,
            normalizedCognome,
            normalizedResidenza,
            normalizedNumeroTelefono,
            normalizedPiano,
            normalizedConto
        );
    }

    /**
     * Registra un cliente con conto e dati carta, controllando i vincoli minimi di input.
     * Lancia {@link IllegalArgumentException} se un campo obbligatorio e' mancante o incoerente.
     *
     * @param email deve contenere un indirizzo non vuoto.
     * @param password deve contenere una password non vuota.
     * @param nome deve contenere il nome del cliente.
     * @param cognome deve contenere il cognome del cliente.
     * @param residenza deve contenere la residenza del cliente.
     * @param numeroTelefono deve contenere un numero di telefono non vuoto.
     * @param pianoTariffario deve contenere un piano tariffario non vuoto.
     * @param conto deve contenere il tipo di conto da associare.
     * @param numeroCarta deve contenere il numero della carta.
     * @param scadenzaCarta deve contenere la scadenza della carta.
     * @param cvvCarta deve contenere il CVV della carta.
     * @param intestatarioCarta deve contenere il nome dell'intestatario.
     * @throws IllegalArgumentException se un parametro obbligatorio e' nullo o vuoto.
     */
    // Sicurezza: @Override conferma la sovrascrittura del metodo registerCliente() piu' esteso definito in TelecomRepository.
    @Override
    public void registerCliente(
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
    ) {
        System.out.println("[ATTO 1 - 4. PROXY REPOSITORY] Intercetto registrazione con carta e delego il salvataggio al repository reale.");
        String normalizedEmail = requireText(email, "Email");
        String normalizedPassword = requireText(password, "Password");
        String normalizedNome = requireText(nome, "Nome");
        String normalizedCognome = requireText(cognome, "Cognome");
        String normalizedResidenza = requireText(residenza, "Residenza");
        String normalizedNumeroTelefono = requireText(numeroTelefono, "Numero di telefono");
        String normalizedPiano = requireText(pianoTariffario, "Piano tariffario");
        String normalizedConto = requireText(conto, "Conto");
        String normalizedNumeroCarta = requireText(numeroCarta, "Numero carta");
        String normalizedScadenzaCarta = requireText(scadenzaCarta, "Scadenza carta");
        String normalizedCvvCarta = requireText(cvvCarta, "CVV carta");
        String normalizedIntestatarioCarta = requireText(intestatarioCarta, "Intestatario carta");

        log("Registrazione cliente con carta per " + normalizedEmail);
        target.registerCliente(
            normalizedEmail,
            normalizedPassword,
            normalizedNome,
            normalizedCognome,
            normalizedResidenza,
            normalizedNumeroTelefono,
            normalizedPiano,
            normalizedConto,
            normalizedNumeroCarta,
            normalizedScadenzaCarta,
            normalizedCvvCarta,
            normalizedIntestatarioCarta
        );
    }

    /**
     * Inizializza lo storico del nuovo utente delegando al repository reale.
     *
     * @param email deve identificare l'utente da inizializzare e non puo' essere vuota.
     * @throws IllegalArgumentException se l'email non e' valida.
     */
    
    // Sicurezza: @Override conferma che il metodo arriva da TelecomRepository e non e' un helper locale.
    @Override

    // Questo blocco prende un'email e la valida con requireText(), che blocca se l'email è vuota o mancante.
    // Se l'email è valida, il proxy passa l'email pulita a target per inizializzare lo storico del nuovo utente nel database.
    // target è il repository vero, cioè il database vero dove vivono i dati reali degli abbonati e i loro storici.
    // Lo storico significa la cronologia, cioè il registro che tiene traccia di tutte le azioni future dell'utente.
    // Il proxy non fa il lavoro, delega tutto a target: è come un intermediario che pulisce l'email e poi passa la richiesta.
    public void inizializzaStoricoNuovoUtente(String email) {
        System.out.println("[ATTO 1 - 5. PROXY REPOSITORY] Inizializzo lo storico pagamenti del nuovo utente appena registrato.");
        target.inizializzaStoricoNuovoUtente(requireText(email, "Email"));
    }

    /**
     * Recupera il nome associato a una email dopo la normalizzazione dell'input.
     *
     * @param email deve identificare un abbonato; i valori vuoti vengono delegati come null.
     * @return il nome trovato oppure {@code null} se non disponibile.
     */
    // Sicurezza: @Override conferma la ridefinizione di findNomeByEmail() ereditata da TelecomRepository.
    @Override
    public String findNomeByEmail(String email) {
        return target.findNomeByEmail(normalizeText(email));
    }

    /**
     * Recupera l'utilizzo associato alla email dopo il trim dell'input.
     *
     * @param email deve identificare un abbonato; i valori vuoti vengono trasformati in null.
     * @return l'utilizzo trovato oppure {@code null} se il dato non esiste.
     */
    // Sicurezza: @Override conferma la ridefinizione del metodo findUtilizzoByEmail() di TelecomRepository.
    @Override
    public Utilizzo findUtilizzoByEmail(String email) {
        return target.findUtilizzoByEmail(normalizeText(email));
    }

    /**
     * Recupera il piano tariffario collegato alla email normalizzata.
     *
     * @param email deve identificare un abbonato; i valori vuoti vengono trasformati in null.
     * @return il piano tariffario associato oppure {@code null} se non disponibile.
     */
    // Sicurezza: @Override conferma che il metodo proviene da TelecomRepository.
    @Override
    public PianoTariffario findPianoTariffarioByEmail(String email) {
        return target.findPianoTariffarioByEmail(normalizeText(email));
    }

    /**
     * Registra una chiamata solo se email e minuti sono validi.
     * Lancia {@link IllegalArgumentException} se i minuti non sono positivi.
     *
     * @param email deve identificare un abbonato e non puo' essere vuota.
     * @param minuti deve essere maggiore di zero.
     * @throws IllegalArgumentException se l'email e' invalida o i minuti non sono positivi.
     */
    
    // Sicurezza: @Override conferma la ridefinizione di registraChiamata() ereditata da TelecomRepository.
    @Override

    // Questo blocco prende un'email e un numero di minuti e registra una chiamata telefonica per quell'utente.
    // Prima valida l'email con requireText() bloccando se è vuota o mancante.
    // Poi controlla che i minuti siano maggiori di zero: se sono zero o negativi, blocca subito e lancia un errore.
    // Se entrambi i valori sono validi, scrive un messaggio di traccia con log() e passa i dati a target.
    // target è il repository vero, cioè il database vero dove vivono i dati reali degli utenti e dove vengono salvate le loro chiamate.
    // Il proxy non registra la chiamata da solo, delega tutto a target: è come un intermediario che pulisce l'email, controlla i minuti e poi passa la richiesta al database vero.
    public void registraChiamata(String email, int minuti) {
        String normalizedEmail = requireText(email, "Email");
        if (minuti <= 0) {
            throw new IllegalArgumentException("I minuti devono essere maggiori di zero");
        }

        log("Registrazione chiamata per " + normalizedEmail + " di " + minuti + " minuti");
        target.registraChiamata(normalizedEmail, minuti);
    }

    /**
     * Registra un SMS per l'utente dopo la validazione minimale dell'email.
     *
     * @param email deve identificare un abbonato e non puo' essere vuota.
     * @throws IllegalArgumentException se l'email non e' valida.
     */
    
    // Sicurezza: @Override conferma che il metodo deriva da TelecomRepository.
    @Override

    // Questo blocco prende un'email e registra un SMS per quell'utente.
    // Prima valida l'email con requireText() bloccando se è vuota o mancante.
    // Se l'email è valida, scrive un messaggio di traccia con log() per tenere traccia dell'SMS.
    // Poi passa l'email a target per registrare l'SMS nel database vero.
    // target è il repository vero, cioè il database vero dove vivono i dati reali degli utenti e dove vengono salvati i loro SMS.
    // Il proxy non registra l'SMS da solo, delega tutto a target: è come un intermediario che pulisce l'email, scrive il messaggio di traccia e poi passa la richiesta al database vero.
    public void registraSms(String email) {
        String normalizedEmail = requireText(email, "Email");
        log("Registrazione SMS per " + normalizedEmail);
        target.registraSms(normalizedEmail);
    }

    /**
     * Registra il consumo dati solo se l'input e' coerente.
     * Lancia {@link IllegalArgumentException} quando i megabyte non sono positivi.
     *
     * @param email deve identificare un abbonato e non puo' essere vuota.
     * @param mb deve essere maggiore di zero.
     * @throws IllegalArgumentException se l'email e' invalida o i MB non sono positivi.
     */
    
    // Sicurezza: @Override conferma la ridefinizione di registraDati() proveniente da TelecomRepository.
    @Override

    // Questo blocco prende un'email e una quantità di MB (megabyte) e registra il consumo dati per quell'utente.
    // Prima valida l'email con requireText() bloccando se è vuota o mancante.
    // Poi controlla che i MB siano maggiori di zero: se sono zero o negativi, blocca subito e lancia un errore.
    // Se entrambi i valori sono validi, scrive un messaggio di traccia con log() con i dettagli del consumo.
    // Poi passa l'email e i MB a target per registrare il consumo dati nel database vero.
    // target è il repository vero, cioè il database vero dove vivono i dati reali degli utenti e dove vengono salvati i loro consumi di dati.
    // Il proxy non registra il consumo da solo, delega tutto a target: è come un intermediario che pulisce l'email, controlla i MB e poi passa la richiesta al database vero.
    public void registraDati(String email, int mb) {
        String normalizedEmail = requireText(email, "Email");
        if (mb <= 0) {
            throw new IllegalArgumentException("I MB devono essere maggiori di zero");
        }

        log("Registrazione dati per " + normalizedEmail + " di " + mb + " MB");
        target.registraDati(normalizedEmail, mb);
    }

    /**
     * Associa una promozione dopo avere ripulito i parametri testuali.
     *
     * @param email deve identificare un abbonato e non puo' essere vuota.
     * @param nomePromozione deve contenere il nome della promozione.
     * @return {@code true} se l'associazione viene creata, altrimenti {@code false}.
     * @throws IllegalArgumentException se uno dei parametri obbligatori e' nullo o vuoto.
     */
    // Sicurezza: @Override conferma che sto sovrascrivendo aderisciPromozione() definito in TelecomRepository.
    @Override

    // Questo blocco prende un'email e il nome di una promozione e attacca la promozione all'utente nel database.
    // Prima valida l'email con requireText() bloccando se è vuota o mancante.
    // Poi valida il nome della promozione con requireText() bloccando se è vuoto o mancante.
    // Se entrambi i valori sono validi, scrive un messaggio di traccia con log() per tenere traccia che qualcuno ha cercato di aderire a una promozione.
    // Poi passa l'email e il nome della promozione a target per associarli nel database vero.
    // target è il repository vero, cioè il database vero dove vivono i dati reali degli utenti e le loro promozioni.
    // Il metodo restituisce true se la promozione è stata associata con successo (cioè il database ha salvato la associazione), oppure false se qualcosa è andato male (per esempio se l'utente aveva già quella promozione).
    // Il proxy non associa la promozione da solo, delega tutto a target: è come un intermediario che pulisce l'email e il nome, scrive il messaggio di traccia e poi passa la richiesta al database vero.
    public boolean aderisciPromozione(String email, String nomePromozione) {
        System.out.println("[ATTO 3 - 3. PROXY REPOSITORY] Persisto adesione promozione '" + nomePromozione + "' su database.");
        String normalizedEmail = requireText(email, "Email");
        String normalizedPromozione = requireText(nomePromozione, "Nome promozione");
        log("Adesione promozione " + normalizedPromozione + " per " + normalizedEmail);
        return target.aderisciPromozione(normalizedEmail, normalizedPromozione);
    }

    /**
     * Rimuove l'associazione tra un utente e una promozione.
     *
     * @param email deve identificare un abbonato e non puo' essere vuota.
     * @param nomePromozione deve contenere il nome della promozione.
     * @return {@code true} se viene eliminata almeno una riga, altrimenti {@code false}.
     * @throws IllegalArgumentException se uno dei parametri obbligatori e' nullo o vuoto.
     */
    // Sicurezza: @Override conferma la ridefinizione di disdiciPromozione() ereditata da TelecomRepository.
    @Override
    
    // Questo blocco prende un'email e il nome di una promozione e rimuove l'associazione
    // tra l'utente e quella promozione nel database.
    // Prima valida l'email con requireText() e blocca se l'email e' nulla o vuota.
    // Poi valida il nome della promozione con requireText() e blocca se e' nullo o vuoto.
    // Se entrambi i valori sono validi, scrive un messaggio di traccia con log()
    // per indicare che qualcuno ha chiesto la disdetta della promozione.
    // Poi passa l'email e il nome della promozione a target per eliminarli dal database vero.
    // Il metodo restituisce true se e' stata eliminata almeno una riga (cioe' la promozione e' stata rimossa),
    // oppure false se non e' successo nulla (per esempio se l'associazione non esisteva).
    // "target" e' il repository vero, cioe' il database reale dove sono salvati gli utenti
    // e le loro promozioni. Il proxy non rimuove direttamente i dati: svolge i controlli,
    // registra l'operazione e delega tutto a target, che effettua la modifica nel database.
    public boolean disdiciPromozione(String email, String nomePromozione) {
        String normalizedEmail = requireText(email, "Email");
        String normalizedPromozione = requireText(nomePromozione, "Nome promozione");
        log("Disdetta promozione " + normalizedPromozione + " per " + normalizedEmail);
        return target.disdiciPromozione(normalizedEmail, normalizedPromozione);
    }

    /**
     * Aggiorna il saldo del conto solo se il nuovo importo e' valido.
     * Lancia {@link IllegalArgumentException} se il saldo e' negativo.
     *
     * @param email deve identificare un abbonato e non puo' essere vuota.
     * @param nuovoSaldo deve essere maggiore o uguale a zero.
     * @return {@code true} se l'aggiornamento va a buon fine, altrimenti {@code false}.
     * @throws IllegalArgumentException se l'email e' invalida o il saldo e' negativo.
     */
    // Sicurezza: @Override conferma la ridefinizione di aggiornaSaldoConto() dalla superclasse TelecomRepository.
    @Override
    
    // Questo blocco prende un'email e un valore numerico chiamato nuovoSaldo e prova
    // ad aggiornare il saldo del conto dell'utente nel database.
    // 1) Controlla l'email con requireText(): se l'email e' nulla o vuota, il metodo
    //    interrompe l'esecuzione lanciando un'eccezione, perche' l'email e' obbligatoria.
    // 2) Controlla che il nuovoSaldo non sia negativo: se e' negativo lancia
    //    IllegalArgumentException per evitare di mettere valori non validi nel conto.
    // 3) Se i controlli passano, scrive una riga di log con log() per tracciare
    //    l'operazione di aggiornamento e poi passa l'email pulita e il nuovoSaldo a
    //    target per effettuare l'aggiornamento reale nel database.
    // Il metodo restituisce true se l'aggiornamento nel database e' andato a buon
    // fine, false altrimenti.
    // "target" e' il repository vero, cioe' il database reale dove sono salvati
    // gli utenti e i loro saldi. Il proxy non cambia direttamente i dati: fa i
    // controlli, registra l'azione e delega l'effettiva modifica a target.
    public boolean aggiornaSaldoConto(String email, double nuovoSaldo) {
        System.out.println("[ATTO 4 - 5. PROXY REPOSITORY] Aggiorno saldo conto dopo ricarica: nuovo saldo = " + nuovoSaldo + " EUR.");
        String normalizedEmail = requireText(email, "Email");
        if (nuovoSaldo < 0) {
            throw new IllegalArgumentException("Il nuovo saldo non può essere negativo");
        }

        log("Aggiornamento saldo per " + normalizedEmail + " a " + nuovoSaldo);
        return target.aggiornaSaldoConto(normalizedEmail, nuovoSaldo);
    }

    /**
     * Ricalcola il pagamento del mese corrente per l'utente indicato.
     *
     * @param email deve identificare un abbonato e non puo' essere vuota.
     * @throws IllegalArgumentException se l'email non e' valida.
     */
    // Sicurezza: @Override conferma che aggiornaPagamentoMeseCorrente() arriva da TelecomRepository.
    @Override
    
    // Questo blocco prende un'email valida e ordina al database di ricalcolare
    // il pagamento del mese corrente per quell'utente.
    // 1) Usa requireText() per verificare che l'email non sia nulla o vuota; se
    //    l'email e' mancante il metodo lancia un'eccezione e si ferma.
    // 2) Se l'email e' valida, scrive una riga di log con log() per tracciare
    //    l'azione che sta per essere eseguita (utile per capire cosa e' successo).
    // 3) Passa l'email pulita a target, che e' il repository vero: il database
    //    reale dove sono salvati i dati degli utenti e dove si calcolano i pagamenti.
    // Il proxy non esegue il ricalcolo da solo: fa i controlli, registra la richiesta
    // e delega l'operazione a target, che aggiorna i dati nel database.
    public void aggiornaPagamentoMeseCorrente(String email) {
        String normalizedEmail = requireText(email, "Email");
        log("Aggiornamento pagamento mese corrente per " + normalizedEmail);
        target.aggiornaPagamentoMeseCorrente(normalizedEmail);
    }

    /**
     * Recupera lo storico pagamenti dell'abbonato richiesto.
     * Restituisce la lista del repository reale senza ulteriori trasformazioni.
     *
     * @param emailAbbonato deve identificare l'abbonato; i valori vuoti vengono passati come null.
     * @return la cronologia pagamenti associata all'utente.
     */
    // Sicurezza: @Override conferma la ridefinizione di getStoricoPagamenti() definita in TelecomRepository.
    @Override
    
    // Questo blocco chiede al database la lista dei pagamenti di un abbonato.
    // 1) Prende l'input `emailAbbonato` e lo normalizza con `normalizeText()`;
    //    se l'email e' nulla viene passata come null al repository reale.
    // 2) Chiama `target.getStoricoPagamenti(...)` per ottenere i dati dal
    //    repository vero: il database reale dove sono salvati i pagamenti.
    // 3) Restituisce direttamente la lista ricevuta dal database, senza
    //    modificarla.
    //
    // `<Pagamento>` e' il tipo generico della lista `ObservableList<Pagamento>`. 
    // Significa che ogni elemento dentro la lista e' un oggetto di tipo `Pagamento`. 
    // `Pagamento` rappresenta una singola voce di pagamento (cioe' un record che contiene le informazioni di un
    // pagamento effettuato). Java usa il simbolo tra parentesi angolate
    // per dire che la lista contiene SOLO oggetti `Pagamento`.
    // Il proxy non costruisce o cambia questi oggetti: li prende dal database
    // tramite `target` e li passa al chiamante.
    public ObservableList<Pagamento> getStoricoPagamenti(String emailAbbonato) {
        return target.getStoricoPagamenti(normalizeText(emailAbbonato));
    }

    /**
     * Salda il pagamento di un mese specifico dopo il controllo dei parametri.
     * Lancia {@link IllegalArgumentException} se mese o anno non sono validi.
     *
     * @param email deve identificare un abbonato e non puo' essere vuota.
     * @param mese deve contenere il mese da saldare.
     * @param anno deve essere maggiore di zero.
     * @return {@code true} se il pagamento viene saldato, altrimenti {@code false}.
     * @throws IllegalArgumentException se un input obbligatorio e' invalido.
     */
    // Sicurezza: @Override conferma la ridefinizione di saldaPagamento() ereditata da TelecomRepository.
    @Override
    
    // Questo blocco prende tre valori: l'email dell'abbonato, il mese e l'anno
    // e prova a segnare il pagamento come saldato per quel mese e anno.
    // 1) Usa requireText() per verificare che l'email non sia nulla o vuota;
    //    se e' vuota il metodo lancia un'eccezione e si ferma.
    // 2) Usa requireText() anche per il mese: il mese non puo' essere vuoto.
    // 3) Controlla che l'anno sia un numero positivo; se anno <= 0 lancia
    //    IllegalArgumentException perche' l'anno non e' valido.
    // 4) Se tutti i controlli passano, scrive una riga di log con log()
    //    per tracciare l'operazione di saldo e poi passa i valori a
    //    target per effettuare l'azione nel database vero.
    // Il metodo restituisce true se il database ha segnato il pagamento
    // come saldato (cioe' l'operazione e' andata a buon fine), false altrimenti.
    // "target" e' il repository vero: il database reale dove sono registrati
    // gli abbonati e i loro pagamenti. Il proxy non esegue il salvataggio
    // direttamente; fa i controlli e delega l'azione a target, che modifica
    // i dati nel database.
    public boolean saldaPagamento(String email, String mese, int anno) {
        System.out.println("[ATTO 4 - 9. PROXY REPOSITORY] Saldo il pagamento dello storico per " + mese + " " + anno + ".");
        String normalizedEmail = requireText(email, "Email");
        String normalizedMese = requireText(mese, "Mese");
        if (anno <= 0) {
            throw new IllegalArgumentException("L'anno non può essere negativo o nullo");
        }

        log("Saldo pagamento per " + normalizedEmail + " - " + normalizedMese + " " + anno);
        return target.saldaPagamento(normalizedEmail, normalizedMese, anno);
    }

    /**
     * Calcola il totale mensile associato alla email fornita.
     *
     * @param email deve identificare un abbonato; i valori vuoti vengono passati come null.
     * @return il totale mensile calcolato dal repository reale.
     */
    // Sicurezza: @Override conferma la ridefinizione di calcolaTotaleMensileByEmail() proveniente da TelecomRepository.
    @Override

    // Questo blocco calcola il totale mensile associato a un abbonato.
    // 1) Prende l'input `email` e lo normalizza con `normalizeText()`; se
    //    l'email e' nulla viene passata come null al repository reale.
    // 2) Chiama `target.calcolaTotaleMensileByEmail(...)` per delegare il
    //    calcolo al repository vero, che e' il database reale dove sono
    //    memorizzati gli utilizzi, i piani e i pagamenti.
    // 3) Restituisce il valore numerico che il database ha calcolato, senza
    //    alterarlo.
    // Il proxy fa piccole operazioni locali (normalizzazione e logging) ma non
    // esegue il calcolo vero: delega tutto a `target`, che legge i dati dal
    // database e restituisce il risultato.
    public double calcolaTotaleMensileByEmail(String email) {
        return target.calcolaTotaleMensileByEmail(normalizeText(email));
    }


    /**
     * Aggiunge una nuova promozione solo dopo aver validato nome, costo e descrizione.
     * Lancia {@link IllegalArgumentException} se il costo e' negativo o i testi sono vuoti.
     *
     * @param nome deve contenere il nome della promozione.
     * @param costo deve essere maggiore o uguale a zero.
     * @param descrizione deve contenere una descrizione non vuota.
     * @throws IllegalArgumentException se i parametri obbligatori non rispettano i vincoli.
     */
    // Sicurezza: @Override conferma la ridefinizione di addPromozione() ereditata da TelecomRepository.
    @Override

    // Questo blocco aggiunge una promozione nel sistema in modo controllato.
    // 1) Usa `requireText()` per assicurarsi che `nome` e `descrizione` non
    //    siano vuoti: se manca un testo lancia una IllegalArgumentException.
    // 2) Controlla che `costo` non sia negativo; se lo è lancia una eccezione
    //    per evitare dati incoerenti.
    // 3) Registra l'operazione con `log()` per tenere traccia dell'azione.
    // 4) Chiama `target.addPromozione(...)` per delegare l'inserimento al
    //    repository reale che salva i dati nel database.
    //
    // `target` è l'oggetto repository vero, cioè l'istanza che
    // parla con il database reale e svolge il lavoro concreto di leggere/scrivere
    // i dati. Il proxy fa controlli e normalizzazioni leggeri
    // e poi affida il lavoro a `target`.
    public void addPromozione(String nome, double costo, String descrizione) {
        String normalizedNome = requireText(nome, "Nome promozione");
        String normalizedDescrizione = requireText(descrizione, "Descrizione promozione");
        if (costo < 0) {
            throw new IllegalArgumentException("Il costo non può essere negativo");
        }

        log("Aggiunta promozione " + normalizedNome + " costo " + costo);
        target.addPromozione(normalizedNome, costo, normalizedDescrizione);
    }
}

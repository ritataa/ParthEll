package service;

import patterns.proxy.ITelecomRepository;

/*
 * LEGENDA: STANDARD DI DOCUMENTAZIONE JAVADOC
 * @param: descrive i vincoli e il significato degli input dei metodi pubblici.
 * @return: descrive l'output garantito al chiamante (OperationResult).
 * @author / @version: tracciano paternità e versione del file.
 */

/**
 * Gestisce la registrazione dell'utilizzo cliente (chiamate, SMS, dati).
 * Valida i parametri minimi e delega le operazioni al repository.
 * Usa un application-service per separare la logica di validazione dall'accesso ai dati.
 *
 * @author ParthEll Team
 * @version 1.0
 */
public class UsageRegistrationService {

    private final ITelecomRepository repository;

    public UsageRegistrationService(ITelecomRepository repository) {
        this.repository = repository;
    }

    /**
     * Registra una chiamata con durata positiva.
     *
     * @param email email dell'abbonato; non nulla per eseguire l'operazione.
     * @param minuti durata in minuti; deve essere > 0.
     * @param numeroDestinatario numero destinatario usato solo per il messaggio di conferma.
     * @return `OperationResult` con esito true/false e messaggio adatto all'interfaccia.
     */
    public OperationResult registraChiamata(String email, int minuti, String numeroDestinatario) {
        // Validazione minima: rifiuto durate non positive per evitare inserimenti non sensati.
        if (minuti <= 0) {
            return new OperationResult(false, "La durata deve essere maggiore di zero!");
        }
        // Delego l'aggiornamento al repository che aggiorna la riga di utilizzo.
        repository.registraChiamata(email, minuti);
        return new OperationResult(true, "Chiamata di " + minuti + " minuti al numero " + numeroDestinatario + " effettuata con successo!");
    }

    /**
     * Registra l'invio di un SMS per l'abbonato specificato.
     *
     * @param email email dell'abbonato.
     * @param numeroDestinatario numero destinatario usato per il messaggio di conferma.
     * @return `OperationResult` con esito della chiamata al repository.
     */
    public OperationResult registraSms(String email, String numeroDestinatario) {
        // Nessuna validazione aggiuntiva richiesta; delego direttamente al repository.
        repository.registraSms(email);
        return new OperationResult(true, "SMS inviato al numero " + numeroDestinatario + " con successo!");
    }

    /**
     * Registra traffico dati espresso in GB con quantità positiva.
     *
     * @param email email dell'abbonato.
     * @param giga quantità in GB; deve essere > 0.
     * @return `OperationResult` con esito true/false e messaggio adatto alla UI.
     */
    public OperationResult registraDati(String email, int giga) {
        // Evito inserimenti non sensati bloccando valori non positivi.
        if (giga <= 0) {
            return new OperationResult(false, "La quantità deve essere maggiore di zero!");
        }
        // Delego l'incremento dati al repository.
        repository.registraDati(email, giga);
        return new OperationResult(true, "Utilizzati " + giga + " GB con successo!");
    }
}

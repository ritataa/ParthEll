package service;

/**
 * Gestisce la registrazione dell'utilizzo cliente (chiamate, SMS, dati)
 * applicando le validazioni minime prima dell'accesso al repository.
 */
public class UsageRegistrationService {

    private final TelecomRepository repository;

    public UsageRegistrationService(TelecomRepository repository) {
        this.repository = repository;
    }

    /**
     * Registra una chiamata con durata positiva.
     */
    public OperationResult registraChiamata(String email, int minuti, String numeroDestinatario) {
        if (minuti <= 0) {
            return new OperationResult(false, "La durata deve essere maggiore di zero!");
        }
        repository.registraChiamata(email, minuti);
        return new OperationResult(true, "Chiamata di " + minuti + " minuti al numero " + numeroDestinatario + " effettuata con successo!");
    }

    /**
     * Registra l'invio di un SMS.
     */
    public OperationResult registraSms(String email, String numeroDestinatario) {
        repository.registraSms(email);
        return new OperationResult(true, "SMS inviato al numero " + numeroDestinatario + " con successo!");
    }

    /**
     * Registra traffico dati espresso in GB con quantita positiva.
     */
    public OperationResult registraDati(String email, int giga) {
        if (giga <= 0) {
            return new OperationResult(false, "La quantità deve essere maggiore di zero!");
        }
        repository.registraDati(email, giga);
        return new OperationResult(true, "Utilizzati " + giga + " GB con successo!");
    }
}

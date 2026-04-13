package service;

public class UsageRegistrationService {

    private final TelecomRepository repository;

    public UsageRegistrationService(TelecomRepository repository) {
        this.repository = repository;
    }

    public OperationResult registraChiamata(String email, int minuti, String numeroDestinatario) {
        if (minuti <= 0) {
            return new OperationResult(false, "La durata deve essere maggiore di zero!");
        }
        repository.registraChiamata(email, minuti);
        return new OperationResult(true, "Chiamata di " + minuti + " minuti al numero " + numeroDestinatario + " effettuata con successo!");
    }

    public OperationResult registraSms(String email, String numeroDestinatario) {
        repository.registraSms(email);
        return new OperationResult(true, "SMS inviato al numero " + numeroDestinatario + " con successo!");
    }

    public OperationResult registraDati(String email, int giga) {
        if (giga <= 0) {
            return new OperationResult(false, "La quantità deve essere maggiore di zero!");
        }
        repository.registraDati(email, giga);
        return new OperationResult(true, "Utilizzati " + giga + " GB con successo!");
    }
}

package patterns.proxy;

import java.util.List;

import javafx.collections.ObservableList;
import model.Abbonato;
import model.PianoTariffario;
import model.Promozione;
import model.Utilizzo;
import patterns.state.Pagamento;
import service.TelecomRepository;

/**
 * Proxy del repository applicativo.
 *
 * Delega in modo trasparente tutte le operazioni a un'istanza reale di
 * {@link TelecomRepository}, mantenendo un punto unico in cui introdurre in
 * futuro controlli, logging o policy trasversali senza toccare i client.
 */
public class TelecomRepositoryProxy extends TelecomRepository {

    private final TelecomRepository target = new TelecomRepository();

    private void log(String message) {
        System.err.println("[TelecomRepositoryProxy] " + message);
    }

    private String requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " non può essere vuoto");
        }
        return value.trim();
    }

    private String normalizeText(String value) {
        return value == null ? null : value.trim();
    }

    @Override
    public String authenticate(String email, String password) {
        String normalizedEmail = normalizeText(email);
        String normalizedPassword = normalizeText(password);
        if (normalizedEmail == null || normalizedEmail.isBlank() || normalizedPassword == null || normalizedPassword.isBlank()) {
            return null;
        }

        log("Autenticazione richiesta per " + normalizedEmail);
        return target.authenticate(normalizedEmail, normalizedPassword);
    }

    @Override
    public List<Abbonato> findAllAbbonati() {
        return target.findAllAbbonati();
    }

    @Override
    public Abbonato findAbbonatoByEmail(String email) {
        return target.findAbbonatoByEmail(normalizeText(email));
    }

    @Override
    public List<Utilizzo> findAllUtilizzi() {
        return target.findAllUtilizzi();
    }

    @Override
    public List<Promozione> findAllPromozioni() {
        return target.findAllPromozioni();
    }

    @Override
    public List<String> findAllPianiTariffari() {
        return target.findAllPianiTariffari();
    }

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

    @Override
    public void inizializzaStoricoNuovoUtente(String email) {
        target.inizializzaStoricoNuovoUtente(requireText(email, "Email"));
    }

    @Override
    public String findNomeByEmail(String email) {
        return target.findNomeByEmail(normalizeText(email));
    }

    @Override
    public Utilizzo findUtilizzoByEmail(String email) {
        return target.findUtilizzoByEmail(normalizeText(email));
    }

    @Override
    public PianoTariffario findPianoTariffarioByEmail(String email) {
        return target.findPianoTariffarioByEmail(normalizeText(email));
    }

    @Override
    public void registraChiamata(String email, int minuti) {
        String normalizedEmail = requireText(email, "Email");
        if (minuti <= 0) {
            throw new IllegalArgumentException("I minuti devono essere maggiori di zero");
        }

        log("Registrazione chiamata per " + normalizedEmail + " di " + minuti + " minuti");
        target.registraChiamata(normalizedEmail, minuti);
    }

    @Override
    public void registraSms(String email) {
        String normalizedEmail = requireText(email, "Email");
        log("Registrazione SMS per " + normalizedEmail);
        target.registraSms(normalizedEmail);
    }

    @Override
    public void registraDati(String email, int mb) {
        String normalizedEmail = requireText(email, "Email");
        if (mb <= 0) {
            throw new IllegalArgumentException("I MB devono essere maggiori di zero");
        }

        log("Registrazione dati per " + normalizedEmail + " di " + mb + " MB");
        target.registraDati(normalizedEmail, mb);
    }

    @Override
    public boolean aderisciPromozione(String email, String nomePromozione) {
        String normalizedEmail = requireText(email, "Email");
        String normalizedPromozione = requireText(nomePromozione, "Nome promozione");
        log("Adesione promozione " + normalizedPromozione + " per " + normalizedEmail);
        return target.aderisciPromozione(normalizedEmail, normalizedPromozione);
    }

    @Override
    public boolean disdiciPromozione(String email, String nomePromozione) {
        String normalizedEmail = requireText(email, "Email");
        String normalizedPromozione = requireText(nomePromozione, "Nome promozione");
        log("Disdetta promozione " + normalizedPromozione + " per " + normalizedEmail);
        return target.disdiciPromozione(normalizedEmail, normalizedPromozione);
    }

    @Override
    public boolean aggiornaSaldoConto(String email, double nuovoSaldo) {
        String normalizedEmail = requireText(email, "Email");
        if (nuovoSaldo < 0) {
            throw new IllegalArgumentException("Il nuovo saldo non può essere negativo");
        }

        log("Aggiornamento saldo per " + normalizedEmail + " a " + nuovoSaldo);
        return target.aggiornaSaldoConto(normalizedEmail, nuovoSaldo);
    }

    @Override
    public void aggiornaPagamentoMeseCorrente(String email) {
        String normalizedEmail = requireText(email, "Email");
        log("Aggiornamento pagamento mese corrente per " + normalizedEmail);
        target.aggiornaPagamentoMeseCorrente(normalizedEmail);
    }

    @Override
    public ObservableList<Pagamento> getStoricoPagamenti(String emailAbbonato) {
        return target.getStoricoPagamenti(normalizeText(emailAbbonato));
    }

    @Override
    public boolean saldaPagamento(String email, String mese, int anno) {
        String normalizedEmail = requireText(email, "Email");
        String normalizedMese = requireText(mese, "Mese");
        if (anno <= 0) {
            throw new IllegalArgumentException("L'anno non può essere negativo o nullo");
        }

        log("Saldo pagamento per " + normalizedEmail + " - " + normalizedMese + " " + anno);
        return target.saldaPagamento(normalizedEmail, normalizedMese, anno);
    }

    @Override
    public double calcolaTotaleMensileByEmail(String email) {
        return target.calcolaTotaleMensileByEmail(normalizeText(email));
    }

    @Override
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

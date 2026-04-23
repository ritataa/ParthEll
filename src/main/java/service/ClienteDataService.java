package service;

import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import patterns.state.Pagamento;
import model.PianoTariffario;
import model.Promozione;
import model.Utilizzo;

/**
 * Servizio applicativo dedicato al caricamento dati dell'area cliente.
 */
public class ClienteDataService {

    private final TelecomRepository repository;

    public ClienteDataService(TelecomRepository repository) {
        this.repository = repository;
    }

    /**
     * Restituisce il nome del cliente associato all'email corrente.
     */
    public String findNomeCliente(String email) {
        return repository.findNomeByEmail(email);
    }

    public void aggiornaPagamentoMeseCorrente(String email) {
        if (email != null && !email.isBlank()) {
            repository.aggiornaPagamentoMeseCorrente(email);
        }
    }

    public List<Promozione> loadPromozioni() {
        return repository.findAllPromozioni();
    }

    public ObservableList<Pagamento> loadStoricoPagamenti(String email) {
        if (email == null || email.isBlank()) {
            return FXCollections.observableArrayList();
        }
        return repository.getStoricoPagamenti(email);
    }

    /**
     * Carica un'istantanea completa della situazione cliente per la UI.
     */
    public ClienteSnapshot loadSnapshot(String email) {
        if (email == null || email.isBlank()) {
            return new ClienteSnapshot(null, null);
        }
        Utilizzo utilizzo = repository.findUtilizzoByEmail(email);
        PianoTariffario pianoTariffario = repository.findPianoTariffarioByEmail(email);
        return new ClienteSnapshot(utilizzo, pianoTariffario);
    }

    /**
     * Calcola il totale mensile aggiornando prima il mese corrente.
     */
    public double calcolaTotaleMensile(String email) {
        if (email == null || email.isBlank()) {
            return 0.0;
        }
        aggiornaPagamentoMeseCorrente(email);
        return repository.calcolaTotaleMensileByEmail(email);
    }

    public boolean saldaPagamento(String email, String mese, int anno) {
        return repository.saldaPagamento(email, mese, anno);
    }

    /**
     * DTO immutabile con utilizzo e piano tariffario correnti.
     */
    public record ClienteSnapshot(Utilizzo utilizzo, PianoTariffario pianoTariffario) {
    }
}

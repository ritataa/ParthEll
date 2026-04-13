package service;

import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Pagamento;
import model.PianoTariffario;
import model.Promozione;
import model.Utilizzo;

public class ClienteDataService {

    private final TelecomRepository repository;

    public ClienteDataService(TelecomRepository repository) {
        this.repository = repository;
    }

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

    public ClienteSnapshot loadSnapshot(String email) {
        if (email == null || email.isBlank()) {
            return new ClienteSnapshot(null, null);
        }
        Utilizzo utilizzo = repository.findUtilizzoByEmail(email);
        PianoTariffario pianoTariffario = repository.findPianoTariffarioByEmail(email);
        return new ClienteSnapshot(utilizzo, pianoTariffario);
    }

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

    public record ClienteSnapshot(Utilizzo utilizzo, PianoTariffario pianoTariffario) {
    }
}

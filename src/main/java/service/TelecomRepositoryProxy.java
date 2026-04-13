package service;

import java.util.List;

import javafx.collections.ObservableList;
import model.Abbonato;
import model.Pagamento;
import model.PianoTariffario;
import model.Promozione;
import model.Utilizzo;

public class TelecomRepositoryProxy extends TelecomRepository {

    private final TelecomRepository target = new TelecomRepository();

    @Override
    public String authenticate(String email, String password) {
        return target.authenticate(email, password);
    }

    @Override
    public List<Abbonato> findAllAbbonati() {
        return target.findAllAbbonati();
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
        target.registerCliente(email, password, nome, cognome, residenza, numeroTelefono, pianoTariffario);
    }

    @Override
    public void inizializzaStoricoNuovoUtente(String email) {
        target.inizializzaStoricoNuovoUtente(email);
    }

    @Override
    public String findNomeByEmail(String email) {
        return target.findNomeByEmail(email);
    }

    @Override
    public Utilizzo findUtilizzoByEmail(String email) {
        return target.findUtilizzoByEmail(email);
    }

    @Override
    public PianoTariffario findPianoTariffarioByEmail(String email) {
        return target.findPianoTariffarioByEmail(email);
    }

    @Override
    public void registraChiamata(String email, int minuti) {
        target.registraChiamata(email, minuti);
    }

    @Override
    public void registraSms(String email) {
        target.registraSms(email);
    }

    @Override
    public void registraDati(String email, int mb) {
        target.registraDati(email, mb);
    }

    @Override
    public boolean aderisciPromozione(String email, String nomePromozione) {
        return target.aderisciPromozione(email, nomePromozione);
    }

    @Override
    public boolean disdiciPromozione(String email, String nomePromozione) {
        return target.disdiciPromozione(email, nomePromozione);
    }

    @Override
    public void aggiornaPagamentoMeseCorrente(String email) {
        target.aggiornaPagamentoMeseCorrente(email);
    }

    @Override
    public ObservableList<Pagamento> getStoricoPagamenti(String emailAbbonato) {
        return target.getStoricoPagamenti(emailAbbonato);
    }

    @Override
    public boolean saldaPagamento(String email, String mese, int anno) {
        return target.saldaPagamento(email, mese, anno);
    }

    @Override
    public double calcolaTotaleMensileByEmail(String email) {
        return target.calcolaTotaleMensileByEmail(email);
    }

    @Override
    public void addPromozione(String nome, double costo, String descrizione) {
        target.addPromozione(nome, costo, descrizione);
    }
}

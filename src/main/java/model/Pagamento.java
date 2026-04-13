package model;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import model.state.DaPagareState;
import model.state.PagamentoConfermatoState;
import model.state.PagamentoState;

public class Pagamento {
    private final SimpleIntegerProperty id;
    private final SimpleStringProperty idAbbonato;
    private final SimpleStringProperty mese;
    private final SimpleIntegerProperty anno;
    private final SimpleDoubleProperty importo;
    private final SimpleStringProperty stato;
    private final SimpleStringProperty promo;
    private PagamentoState state;

    public Pagamento(int id, String idAbbonato, String mese, int anno, double importo, String stato, String promo) {
        this.id = new SimpleIntegerProperty(id);
        this.idAbbonato = new SimpleStringProperty(idAbbonato);
        this.mese = new SimpleStringProperty(mese);
        this.anno = new SimpleIntegerProperty(anno);
        this.importo = new SimpleDoubleProperty(importo);
        this.stato = new SimpleStringProperty(stato);
        this.promo = new SimpleStringProperty(promo);
        this.state = resolveState(stato);
    }

    public int getId() {
        return id.get();
    }

    public void setId(int value) {
        id.set(value);
    }

    public String getIdAbbonato() {
        return idAbbonato.get();
    }

    public void setIdAbbonato(String value) {
        idAbbonato.set(value);
    }

    public String getMese() {
        return mese.get();
    }

    public void setMese(String value) {
        mese.set(value);
    }

    public int getAnno() {
        return anno.get();
    }

    public void setAnno(int value) {
        anno.set(value);
    }

    public double getImporto() {
        return importo.get();
    }

    public void setImporto(double value) {
        importo.set(value);
    }

    public String getStato() {
        return state.getNome();
    }

    public void setStato(String value) {
        this.state = resolveState(value);
        stato.set(this.state.getNome());
    }

    public String getPromo() {
        return promo.get();
    }

    public void setPromo(String value) {
        promo.set(value);
    }

    public boolean isPagabile() {
        return state.canBePaid();
    }

    public void confermaPagamentoState() {
        state = state.pay();
        stato.set(state.getNome());
    }

    private PagamentoState resolveState(String statoPagamento) {
        if (statoPagamento != null && "Da pagare".equalsIgnoreCase(statoPagamento.trim())) {
            return new DaPagareState();
        }
        return new PagamentoConfermatoState();
    }
}

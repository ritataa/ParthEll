package model;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Pagamento {
    private final SimpleIntegerProperty id;
    private final SimpleStringProperty idAbbonato;
    private final SimpleStringProperty mese;
    private final SimpleIntegerProperty anno;
    private final SimpleDoubleProperty importo;
    private final SimpleStringProperty stato;
    private final SimpleStringProperty promo;

    public Pagamento(int id, String idAbbonato, String mese, int anno, double importo, String stato, String promo) {
        this.id = new SimpleIntegerProperty(id);
        this.idAbbonato = new SimpleStringProperty(idAbbonato);
        this.mese = new SimpleStringProperty(mese);
        this.anno = new SimpleIntegerProperty(anno);
        this.importo = new SimpleDoubleProperty(importo);
        this.stato = new SimpleStringProperty(stato);
        this.promo = new SimpleStringProperty(promo);
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
        return stato.get();
    }

    public void setStato(String value) {
        stato.set(value);
    }

    public String getPromo() {
        return promo.get();
    }

    public void setPromo(String value) {
        promo.set(value);
    }
}

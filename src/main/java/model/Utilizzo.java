package model;

public class Utilizzo {
    private final String numero;
    private final int chiamate;
    private final int sms;
    private final int dati;
    private final String promo;

    public Utilizzo(String numero, int chiamate, int sms, int dati, String promo) {
        this.numero = numero;
        this.chiamate = chiamate;
        this.sms = sms;
        this.dati = dati;
        this.promo = promo;
    }

    public String getNumero() { return numero; }
    public int getChiamate() { return chiamate; }
    public int getSms() { return sms; }
    public int getDati() { return dati; }
    public String getPromo() { return promo; }
}

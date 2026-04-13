package model;

public class Utilizzo {
    private final String numero;
    private final String nome;
    private final String cognome;
    private final String email;
    private final int chiamate;
    private final int sms;
    private final int dati;
    private final String promo;

    public Utilizzo(String numero, String nome, String cognome, String email, int chiamate, int sms, int dati, String promo) {
        this.numero = numero;
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.chiamate = chiamate;
        this.sms = sms;
        this.dati = dati;
        this.promo = promo;
    }

    public String getNumero() { return numero; }
    public String getNome() { return nome; }
    public String getCognome() { return cognome; }
    public String getEmail() { return email; }
    public int getChiamate() { return chiamate; }
    public int getSms() { return sms; }
    public int getDati() { return dati; }
    public String getPromo() { return promo; }
}

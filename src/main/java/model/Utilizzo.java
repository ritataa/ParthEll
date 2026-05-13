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

    /*
     * LEGENDA: STANDARD DI DOCUMENTAZIONE JAVADOC
     * @author: Traccia la paternità del file.
     * @version: Indica la versione del file.
     * @param: Definisce i vincoli e il formato degli argomenti dei costruttori.
     * @return: Descrive i valori restituiti dai metodi pubblici (getter).
     */

    /**
     * Rappresenta l'aggregato dei consumi (chiamate, sms, dati) per un utente.
     * Usato come DTO/Value object per mostrare lo storico consumi nella UI.
     * Implementa un semplice bean immutabile per sicurezza e facilità di lettura.
     *
     * @author ParthEll Team
     * @version 1.0
     */

    public Utilizzo(String numero, String nome, String cognome, String email, int chiamate, int sms, int dati, String promo) {
        // Assegna i valori ricevuti al valore immutabile dell'istanza.
        // Queste operazioni sono intenzionalmente semplici per mantenere il DTO leggero.
        this.numero = numero;
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.chiamate = chiamate;
        this.sms = sms;
        this.dati = dati;
        this.promo = promo;
    }
    /**
     * Restituisce il numero telefonico associato all'utilizzo.
     *
     * @return numero telefonico, può essere null se non disponibile.
     */
    public String getNumero() { // ritorna il numero memorizzato
        return numero; }

    /**
     * Restituisce il nome dell'intestatario dell'utilizzo.
     *
     * @return nome; può essere null per record incompleti.
     */
    public String getNome() { return nome; }

    /**
     * Restituisce il cognome dell'intestatario dell'utilizzo.
     *
     * @return cognome; può essere null per record incompleti.
     */
    public String getCognome() { return cognome; }

    /**
     * Restituisce l'email dell'intestatario.
     *
     * @return email; può essere null se non fornita.
     */
    public String getEmail() { return email; }

    /**
     * Numero di minuti/chiamate registrate.
     *
     * @return valore >= 0 rappresentante le chiamate; dominio applicativo assume intero naturale.
     */
    public int getChiamate() { return chiamate; }

    /**
     * Numero di SMS registrati.
     *
     * @return valore >= 0 rappresentante gli SMS.
     */
    public int getSms() { return sms; }

    /**
     * Volume dati registrato in MB.
     *
     * @return valore >= 0 rappresentante i dati consumati in MB.
     */
    public int getDati() { return dati; }

    /**
     * Restituisce il codice/nome della promozione applicata, se presente.
     *
     * @return promo; può essere null se nessuna promozione è attiva.
     */
    public String getPromo() { return promo; }
}

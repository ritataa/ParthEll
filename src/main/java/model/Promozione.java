package model;

public class Promozione {
    private String nome;
    private String descrizione;
    private double prezzo;

    /*
     * LEGENDA: STANDARD DI DOCUMENTAZIONE JAVADOC
     * @param: descrive i vincoli e il significato dei parametri dei costruttori e setter.
     * @return: descrive i valori restituiti dai getter e da toString().
     * @author / @version: tracciano paternità e versione del file.
     */

    /**
     * Rappresenta una promozione commerciale con nome, descrizione e prezzo.
     * È un semplice bean mutabile usato dal repository e dalla UI per leggere/scrivere dati.
     * Scelta mutabile per semplicità di binding con framework e costruttori vuoti.
     *
     * @author ParthEll Team
     * @version 1.0
     */

    // Costruttore vuoto
    /**
     * Costruttore vuoto richiesto da framework di serializzazione o binding.
     */
    public Promozione() {}

    // Costruttore con nome e descrizione
    /**
     * Crea una promozione con nome e descrizione.
     *
     * @param nome nome identificativo della promozione.
     * @param descrizione testo descrittivo della promozione.
     */
    public Promozione(String nome, String descrizione) {
        this.nome = nome;
        this.descrizione = descrizione;
    }

    // Costruttore completo
    /**
     * Crea una promozione completa con nome, descrizione e prezzo.
     *
     * @param nome nome identificativo della promozione.
     * @param descrizione testo descrittivo della promozione.
     * @param prezzo prezzo numerico della promozione.
     */
    public Promozione(String nome, String descrizione, double prezzo) {
        this.nome = nome;
        this.descrizione = descrizione;
        this.prezzo = prezzo;
    }

    // Getter e Setter
    /**
     * @return nome identificativo della promozione; può essere null.
     */
    public String getNome() { return nome; }
    /**
     * Imposta il nome della promozione.
     *
     * @param nome nuovo nome; può essere null per placeholder temporanei.
     */
    public void setNome(String nome) { this.nome = nome; }

    /**
     * @return descrizione testuale della promozione; può essere null.
     */
    public String getDescrizione() { return descrizione; }
    /**
     * Imposta la descrizione della promozione.
     *
     * @param descrizione testo descrittivo; può essere null.
     */
    public void setDescrizione(String descrizione) { this.descrizione = descrizione; }

    /**
     * @return prezzo numerico della promozione.
     */
    public double getPrezzo() { return prezzo; }
    /**
     * Imposta il prezzo della promozione.
     *
     * @param prezzo valore numerico; tipicamente >= 0.
     */
    public void setPrezzo(double prezzo) { this.prezzo = prezzo; }

    // Sicurezza: obbliga Java a verificare che sto davvero sovrascrivendo toString() di java.lang.Object
    @Override
    public String toString() {
        // Rappresentazione testuale utile per logging e debug nelle UI/console.
        return "Promozione{" +
                "nome='" + nome + '\'' +
                ", descrizione='" + descrizione + '\'' +
                ", prezzo=" + prezzo +
                '}';
    }
}

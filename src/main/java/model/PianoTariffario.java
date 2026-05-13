package model;

/*
 * LEGENDA: STANDARD DI DOCUMENTAZIONE JAVADOC
 * @param: descrive i vincoli e il significato dei parametri del costruttore.
 * @return: descrive l'output garantito dai metodi getter.
 * @author / @version: tracciano paternità e versione del file.
 */

/**
 * Rappresenta un piano tariffario con limiti e costo mensile.
 * Fornisce un DTO immutabile utilizzato dalla logica applicativa e dalla UI.
 * Implementa un semplice oggetto valore per centralizzare le proprietà del piano.
 *
 * @author ParthEll Team
 * @version 1.0
 */
public class PianoTariffario {
    private final String nome;
    private final Integer minutiMensili;
    private final Integer gigaMensili;
    private final boolean illimitatoMinuti;
    private final boolean illimitatoGiga;
    private final double costoMensile;

    public PianoTariffario(
        String nome,
        Integer minutiMensili,
        Integer gigaMensili,
        boolean illimitatoMinuti,
        boolean illimitatoGiga,
        double costoMensile
    ) {
        // Costruttore immutabile: assegna una volta i campi finali.
        this.nome = nome;
        this.minutiMensili = minutiMensili;
        this.gigaMensili = gigaMensili;
        this.illimitatoMinuti = illimitatoMinuti;
        this.illimitatoGiga = illimitatoGiga;
        this.costoMensile = costoMensile;
    }

    /**
     * Restituisce il nome identificativo del piano.
     *
     * @return nome del piano; può essere null se non specificato.
     */
    public String getNome() {
        // Accessor readonly
        return nome;
    }

    /**
     * Restituisce il numero di minuti inclusi al mese, se presente.
     *
     * @return minuti mensili oppure null se non definito (es. piano illimitato).
     */
    public Integer getMinutiMensili() {
        return minutiMensili;
    }

    /**
     * Restituisce i giga inclusi al mese, se presente.
     *
     * @return giga mensili oppure null se non definito (es. piano con illimitato dati).
     */
    public Integer getGigaMensili() {
        return gigaMensili;
    }

    /**
     * Indica se i minuti sono illimitati per questo piano.
     *
     * @return true se i minuti sono illimitati, false altrimenti.
     */
    public boolean isIllimitatoMinuti() {
        return illimitatoMinuti;
    }

    /**
     * Indica se i dati (giga) sono illimitati per questo piano.
     *
     * @return true se i giga sono illimitati, false altrimenti.
     */
    public boolean isIllimitatoGiga() {
        return illimitatoGiga;
    }

    /**
     * Restituisce il costo mensile del piano.
     *
     * @return costo mensile in valuta locale (double).
     */
    public double getCostoMensile() {
        return costoMensile;
    }
}
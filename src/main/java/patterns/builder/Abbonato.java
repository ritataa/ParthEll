package patterns.builder;

import model.TipoPiano;
import model.conto.Conto;
import model.conto.ContoFisso;
/*
* LEGENDA: STANDARD DI DOCUMENTAZIONE JAVADOC

* Questo file utilizza i seguenti tag Javadoc:
* - @author / @version: Tracciano paternità e manutenzione della classe.
 * - @param: Definisce i vincoli e il significato degli input dei metodi pubblici.
 * - @return: Definisce l'output garantito (inclusa la null-safety quando applicabile).
*/

/**
     * Rappresenta un abbonato costruito tramite il pattern Builder.
     * Il Builder separa la costruzione dell'oggetto (Builder) dalla sua rappresentazione (Abbonato),
     * permettendo configurazioni fluenti e valori di default per il `Conto`.
     *
     * @author ParthEll Team
     * @version 1.0
     */
/*
 * SCELTA ARCHITETTURALE: FLUENT BUILDER 
 * Utilizziamo l'implementazione "Fluent" tramite Static Inner Class e non il pattern 
 * classico della GoF (con Interfacce e Director). Questo perché non abbiamo la 
 * necessità di creare rappresentazioni diverse dell'oggetto usando lo stesso processo di costruzione. 
 * Il nostro obiettivo è risolvere il problema dei costruttori telescopici (troppi parametri opzionali), 
 * per garantire leggibilità e sicurezza.
 * 
 * Con quello classico avremmo dovuto creare 4 File/Entità: Product, Builder (Interfaccia), ConcreteBuilder, Director.
 * Qui invece creiamo un file: La classe principale (Product) che ospita una Static Inner Class.
 */

public class Abbonato {
    private String nome;
    private String cognome;
    private String email;
    private String residenza;
    private String numeroTelefono;
    private TipoPiano pianoTariffario;
    private Conto conto;    // POLIMORFISMO PER DATI: Conto può essere ContoFisso o ContoRicaricabile, ma Abbonato non si preoccupa di quale sia. 
                            // Etichetta "Conto", ma può essere qualsiasi implementazione di Conto (ContoFisso o ContoRicaricabile).
    private String numeroCarta;
    private String scadenzaCarta;
    private String cvvCarta;
    private String intestatarioCarta;

    
    // Costruttore completo con Conto
    private Abbonato(String nome, String cognome, String email, String residenza,       //privato perché vogliamo forzare l'uso del Builder per la creazione di Abbonati, evitando costruttori pubblici con molti parametri.
                   String numeroTelefono, TipoPiano pianoTariffario, Conto conto) {
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.residenza = residenza;
        this.numeroTelefono = numeroTelefono;
        this.pianoTariffario = pianoTariffario;
        // Se qualcuno crea un Abbonato e si dimentica di passargli un conto, il sistema gli assegna automaticamente un ContoFisso
        if (conto != null) {
            this.conto = conto;
        } else {
            this.conto = new ContoFisso(); 
        }
    }

    // GETTER E SETTER: Forniscono accesso controllato ai campi dell'abbonato. Alcuni campi (es. conto) garantiscono non-null restituendo un default se necessario.

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; } // assegna il nome (può essere null)

    public Conto getConto() { return conto; } // ritorna il conto (garantito non-null)
    /**
     * Restituisce il `Conto` associato all'abbonato.
     * Garantito non-null: se non è stato impostato viene restituito un `ContoFisso` di default.
     *
     * @return Conto non-null associato all'abbonato.
     */
    public void setConto(Conto conto) {
        // evita valori null sostituendo con default
        if (conto != null) {
            this.conto = conto;
        } else {
            this.conto = new ContoFisso();
        }
    }

    public String getCognome() { return cognome; } // ritorna il cognome
    public void setCognome(String cognome) { this.cognome = cognome; } // assegna il cognome

    public String getEmail() { return email; } // ritorna l'email
    public void setEmail(String email) { this.email = email; } // assegna l'email

    public String getResidenza() { return residenza; } // ritorna la residenza
    public void setResidenza(String residenza) { this.residenza = residenza; } // assegna la residenza

    public String getNumeroTelefono() { return numeroTelefono; } // ritorna il numero di telefono
    public void setNumeroTelefono(String numeroTelefono) { this.numeroTelefono = numeroTelefono; } // assegna il numero di telefono

    public TipoPiano getPianoTariffario() { return pianoTariffario; } // ritorna il piano tariffario
    public void setPianoTariffario(TipoPiano pianoTariffario) { this.pianoTariffario = pianoTariffario; } // assegna il piano tariffario

    public String getNumeroCarta() { return numeroCarta; } // ritorna il numero carta (può essere null)
    public void setNumeroCarta(String numeroCarta) { this.numeroCarta = numeroCarta; } // assegna il numero carta

    public String getScadenzaCarta() { return scadenzaCarta; } // ritorna la scadenza della carta
    public void setScadenzaCarta(String scadenzaCarta) { this.scadenzaCarta = scadenzaCarta; } // assegna la scadenza

    public String getCvvCarta() { return cvvCarta; } // ritorna il cvv 
    public void setCvvCarta(String cvvCarta) { this.cvvCarta = cvvCarta; } // assegna il cvv 

    public String getIntestatarioCarta() { return intestatarioCarta; } // ritorna l'intestatario della carta
    public void setIntestatarioCarta(String intestatarioCarta) { this.intestatarioCarta = intestatarioCarta; } // assegna l'intestatario

    /**
     * Fornisce la "carta d'identità" dell'oggetto per operazioni di logging e debugging.
     * * Sovrascrive (@Override) il comportamento di default di Java, che altrimenti 
     * stamperebbe un incomprensibile indirizzo di memoria (es. Abbonato@7a81197d).
     * Il metodo concatena le variabili di stato principali in una stringa leggibile, 
     * permettendo di ispezionare facilmente l'oggetto tramite System.out.println().
     * * ATTENZIONE ALLA SICUREZZA: Per policy di Data Protection, i dati sensibili 
     * (come CVV e numero di carta) sono stati intenzionalmente omessi.
     *
     * @return Stringa formattata e sicura contenente lo stato corrente dell'abbonato.
     */
    @Override
    public String toString() {
        // stringa compatta per logging/debug
        return "Abbonato{" +
                "nome='" + nome + '\'' +
                ", cognome='" + cognome + '\'' +
                ", email='" + email + '\'' +
                ", pianoTariffario='" + pianoTariffario + '\'' +
                ", conto=" + conto +
                '}';
    }

    /**
     * Classe statica interna Builder per costruire istanze di Abbonato.
     * Il Builder permette di configurare solo i campi desiderati, con valori di default per quelli non impostati (es. Conto).
     * Utilizzo: Abbonato abbonato = Abbonato.builder().nome("Mario").cognome("Rossi").email("m@example.com").build(); 
     * 
     * static = la classe Builder esiste indipendentemente da qualsiasi istanza di Abbonato, non ha bisogno di un riferimento a un Abbonato per essere usata.
     * Se non fosse static, per usarla dovremmo prima creare un'istanza di Abbonato (es. new Abbonato().new Builder()), il che è controintuitivo perché il Builder serve proprio a creare l'Abbonato.
     * final = costante e impedisce l'ereditabilità della classe Builder, garantendo che la struttura del Builder non possa essere modificata o estesa da altre classi.
     */

    public static final class Builder {
        private String nome;
        private String cognome;
        private String email;
        private String residenza;
        private String numeroTelefono;
        private TipoPiano pianoTariffario;
        private Conto conto;
        private String numeroCarta;
        private String scadenzaCarta;
        private String cvvCarta;
        private String intestatarioCarta;

        private Builder() {
            this.conto = new ContoFisso();
        }

        /**
         * Imposta il nome nel builder.
         *
         * @param nome valore del nome; può essere null ma allora il campo rimane unset.
         * @return questo Builder per chiamate fluenti.
         */
        public Builder nome(String nome) {
            this.nome = nome;
            // setta il campo nome e permette chiamate fluenti
            return this;
        }

        public Builder cognome(String cognome) {
            this.cognome = cognome;
            // setta il cognome per il builder
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            // setta l'email per il builder
            return this;
        }

        public Builder residenza(String residenza) {
            this.residenza = residenza;
            // setta la residenza
            return this;
        }

        public Builder numeroTelefono(String numeroTelefono) {
            this.numeroTelefono = numeroTelefono;
            // setta il numero di telefono
            return this;
        }

        public Builder pianoTariffario(TipoPiano pianoTariffario) {
            this.pianoTariffario = pianoTariffario;
            // imposta il piano tariffario
            return this;
        }

        public Builder conto(Conto conto) {
            // Se il chiamante passa null, manteniamo il ContoFisso di default
            if (conto != null) {
                this.conto = conto;
            } else {
                this.conto = new ContoFisso();
            }
            // imposta il conto (non-null garantito)
            return this;
        }

        public Builder numeroCarta(String numeroCarta) {
            this.numeroCarta = numeroCarta;
            // setta numero carta (può essere null)
            return this;
        }

        public Builder scadenzaCarta(String scadenzaCarta) {
            this.scadenzaCarta = scadenzaCarta;
            // setta scadenza carta
            return this;
        }

        public Builder cvvCarta(String cvvCarta) {
            this.cvvCarta = cvvCarta;
            // setta cvv 
            return this;
        }

        public Builder intestatarioCarta(String intestatarioCarta) {
            this.intestatarioCarta = intestatarioCarta;
            // setta intestatario della carta
            return this;
        }

        /**
         * Costruisce l'istanza di `Abbonato` a partire dai valori impostati sul Builder.
         * Restituisce un oggetto valido; alcuni campi (es. carte) possono essere null se non impostati.
         *
         * @return Istanza di Abbonato costruita; non lancia eccezioni.
         */
        public Abbonato build() {
            System.out.println("[ATTO 1 - 3. BUILDER ABBONATO BUILDER] Costruisco l'oggetto Abbonato con i dati raccolti nella registrazione.");
            // costruisce l'istanza usando i valori raccolti nel builder
            Abbonato abbonato = new Abbonato(nome, cognome, email, residenza, numeroTelefono, pianoTariffario, conto);
            abbonato.numeroCarta = this.numeroCarta;
            abbonato.scadenzaCarta = this.scadenzaCarta;
            abbonato.cvvCarta = this.cvvCarta;
            abbonato.intestatarioCarta = this.intestatarioCarta;
            return abbonato;
        }
    }

    /**
     * Metodo creato per comodità ed estetica, permette di iniziare la costruzione di un Abbonato con una sintassi fluida: 
     * Abbonato a = Abbonato.builder().nome("Mario").build();
     */

    public static Builder builder() {
        return new Builder();
    }
}
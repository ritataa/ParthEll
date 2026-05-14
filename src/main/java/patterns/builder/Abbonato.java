package patterns.builder;

import model.TipoPiano;
import model.conto.Conto;
import model.conto.ContoFisso;
/*
 LEGENDA: STANDARD DI DOCUMENTAZIONE JAVADOC

 Questo file utilizza i seguenti tag Javadoc:
 - @author / @version: Tracciano paternità e manutenzione della classe.
 - @param: Definisce i vincoli e il significato degli input dei metodi pubblici.
 - @return: Definisce l'output garantito (inclusa la null-safety quando applicabile).

 NOTE: La legenda include solo i tag effettivamente impiegati in questa classe.
*/

public class Abbonato {
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

    /**
     * Rappresenta un abbonato costruito tramite il pattern Builder.
     * Il Builder separa la costruzione dell'oggetto dalla sua rappresentazione,
     * permettendo configurazioni fluenti e valori di default per il `Conto`.
     *
     * @author ParthEll Team
     * @version 1.0
     */
    // Costruttore completo con Conto
    private Abbonato(String nome, String cognome, String email, String residenza,
                   String numeroTelefono, TipoPiano pianoTariffario, Conto conto) {
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.residenza = residenza;
        this.numeroTelefono = numeroTelefono;
        this.pianoTariffario = pianoTariffario;
        // Se non viene fornito alcun conto, usiamo un ContoFisso di default
        if (conto != null) {
            this.conto = conto;
        } else {
            this.conto = new ContoFisso();
        }
    }

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

    public static Builder builder() {
        return new Builder();
    }

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
}
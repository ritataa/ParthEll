package model;

import model.conto.Conto;
import model.conto.ContoFisso;

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

    // Costruttore completo con Conto
    private Abbonato(String nome, String cognome, String email, String residenza,
                   String numeroTelefono, TipoPiano pianoTariffario, Conto conto) {
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.residenza = residenza;
        this.numeroTelefono = numeroTelefono;
        this.pianoTariffario = pianoTariffario;
        this.conto = conto != null ? conto : new ContoFisso();
    }

    // Getter e Setter
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public Conto getConto() { return conto; }
    public void setConto(Conto conto) { this.conto = conto != null ? conto : new ContoFisso(); }

    public String getCognome() { return cognome; }
    public void setCognome(String cognome) { this.cognome = cognome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getResidenza() { return residenza; }
    public void setResidenza(String residenza) { this.residenza = residenza; }

    public String getNumeroTelefono() { return numeroTelefono; }
    public void setNumeroTelefono(String numeroTelefono) { this.numeroTelefono = numeroTelefono; }

    public TipoPiano getPianoTariffario() { return pianoTariffario; }
    public void setPianoTariffario(TipoPiano pianoTariffario) { this.pianoTariffario = pianoTariffario; }

    public String getNumeroCarta() { return numeroCarta; }
    public void setNumeroCarta(String numeroCarta) { this.numeroCarta = numeroCarta; }

    public String getScadenzaCarta() { return scadenzaCarta; }
    public void setScadenzaCarta(String scadenzaCarta) { this.scadenzaCarta = scadenzaCarta; }

    public String getCvvCarta() { return cvvCarta; }
    public void setCvvCarta(String cvvCarta) { this.cvvCarta = cvvCarta; }

    public String getIntestatarioCarta() { return intestatarioCarta; }
    public void setIntestatarioCarta(String intestatarioCarta) { this.intestatarioCarta = intestatarioCarta; }

    @Override
    public String toString() {
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

        public Builder nome(String nome) {
            this.nome = nome;
            return this;
        }

        public Builder cognome(String cognome) {
            this.cognome = cognome;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder residenza(String residenza) {
            this.residenza = residenza;
            return this;
        }

        public Builder numeroTelefono(String numeroTelefono) {
            this.numeroTelefono = numeroTelefono;
            return this;
        }

        public Builder pianoTariffario(TipoPiano pianoTariffario) {
            this.pianoTariffario = pianoTariffario;
            return this;
        }

        public Builder conto(Conto conto) {
            this.conto = conto != null ? conto : new ContoFisso();
            return this;
        }

        public Builder numeroCarta(String numeroCarta) {
            this.numeroCarta = numeroCarta;
            return this;
        }

        public Builder scadenzaCarta(String scadenzaCarta) {
            this.scadenzaCarta = scadenzaCarta;
            return this;
        }

        public Builder cvvCarta(String cvvCarta) {
            this.cvvCarta = cvvCarta;
            return this;
        }

        public Builder intestatarioCarta(String intestatarioCarta) {
            this.intestatarioCarta = intestatarioCarta;
            return this;
        }

        public Abbonato build() {
            Abbonato abbonato = new Abbonato(nome, cognome, email, residenza, numeroTelefono, pianoTariffario, conto);
            abbonato.numeroCarta = this.numeroCarta;
            abbonato.scadenzaCarta = this.scadenzaCarta;
            abbonato.cvvCarta = this.cvvCarta;
            abbonato.intestatarioCarta = this.intestatarioCarta;
            return abbonato;
        }
    }
}

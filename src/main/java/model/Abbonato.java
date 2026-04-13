package model;

public class Abbonato {
    private String nome;
    private String cognome;
    private String email;
    private String residenza;
    private String numeroTelefono;
    private String pianoTariffario;

    // Costruttore vuoto
    public Abbonato() {}

    // Costruttore completo
    public Abbonato(String nome, String cognome, String email, String residenza, 
                   String numeroTelefono, String pianoTariffario) {
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.residenza = residenza;
        this.numeroTelefono = numeroTelefono;
        this.pianoTariffario = pianoTariffario;
    }

    // Getter e Setter
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCognome() { return cognome; }
    public void setCognome(String cognome) { this.cognome = cognome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getResidenza() { return residenza; }
    public void setResidenza(String residenza) { this.residenza = residenza; }

    public String getNumeroTelefono() { return numeroTelefono; }
    public void setNumeroTelefono(String numeroTelefono) { this.numeroTelefono = numeroTelefono; }

    public String getPianoTariffario() { return pianoTariffario; }
    public void setPianoTariffario(String pianoTariffario) { this.pianoTariffario = pianoTariffario; }

    @Override
    public String toString() {
        return "Abbonato{" +
                "nome='" + nome + '\'' +
                ", cognome='" + cognome + '\'' +
                ", email='" + email + '\'' +
                ", pianoTariffario='" + pianoTariffario + '\'' +
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
        private String pianoTariffario;

        private Builder() {
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

        public Builder pianoTariffario(String pianoTariffario) {
            this.pianoTariffario = pianoTariffario;
            return this;
        }

        public Abbonato build() {
            return new Abbonato(nome, cognome, email, residenza, numeroTelefono, pianoTariffario);
        }
    }
}

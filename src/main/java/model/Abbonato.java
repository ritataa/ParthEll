package model;

public class Abbonato {
    private String nome;
    private String cognome;
    private String email;
    private String residenza;
    private String numeroTelefono;
    private String pianoTariffario;
    private String conto;

    // Costruttore vuoto
    public Abbonato() {}

    // Costruttore completo
    public Abbonato(String nome, String cognome, String email, String residenza, 
                   String numeroTelefono, String pianoTariffario, String conto) {
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.residenza = residenza;
        this.numeroTelefono = numeroTelefono;
        this.pianoTariffario = pianoTariffario;
        this.conto = conto;
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

    public String getConto() { return conto; }
    public void setConto(String conto) { this.conto = conto; }

    @Override
    public String toString() {
        return "Abbonato{" +
                "nome='" + nome + '\'' +
                ", cognome='" + cognome + '\'' +
                ", email='" + email + '\'' +
                ", pianoTariffario='" + pianoTariffario + '\'' +
                '}';
    }
}

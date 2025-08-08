package model;

public class Promozione {
    private String nome;
    private String descrizione;
    private double prezzo;

    // Costruttore vuoto
    public Promozione() {}

    // Costruttore con nome e descrizione
    public Promozione(String nome, String descrizione) {
        this.nome = nome;
        this.descrizione = descrizione;
    }

    // Costruttore completo
    public Promozione(String nome, String descrizione, double prezzo) {
        this.nome = nome;
        this.descrizione = descrizione;
        this.prezzo = prezzo;
    }

    // Getter e Setter
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDescrizione() { return descrizione; }
    public void setDescrizione(String descrizione) { this.descrizione = descrizione; }

    public double getPrezzo() { return prezzo; }
    public void setPrezzo(double prezzo) { this.prezzo = prezzo; }

    @Override
    public String toString() {
        return "Promozione{" +
                "nome='" + nome + '\'' +
                ", descrizione='" + descrizione + '\'' +
                ", prezzo=" + prezzo +
                '}';
    }
}

package model;

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
        this.nome = nome;
        this.minutiMensili = minutiMensili;
        this.gigaMensili = gigaMensili;
        this.illimitatoMinuti = illimitatoMinuti;
        this.illimitatoGiga = illimitatoGiga;
        this.costoMensile = costoMensile;
    }

    public String getNome() {
        return nome;
    }

    public Integer getMinutiMensili() {
        return minutiMensili;
    }

    public Integer getGigaMensili() {
        return gigaMensili;
    }

    public boolean isIllimitatoMinuti() {
        return illimitatoMinuti;
    }

    public boolean isIllimitatoGiga() {
        return illimitatoGiga;
    }

    public double getCostoMensile() {
        return costoMensile;
    }
}
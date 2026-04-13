package model.state;

public class DaPagareState implements PagamentoState {

    @Override
    public String getNome() {
        return "Da pagare";
    }

    @Override
    public boolean canBePaid() {
        return true;
    }

    @Override
    public PagamentoState pay() {
        return new PagamentoConfermatoState();
    }
}

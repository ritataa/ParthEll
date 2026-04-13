package model.state;

public class PagamentoConfermatoState implements PagamentoState {

    @Override
    public String getNome() {
        return "Pagamento confermato";
    }

    @Override
    public boolean canBePaid() {
        return false;
    }

    @Override
    public PagamentoState pay() {
        return this;
    }
}

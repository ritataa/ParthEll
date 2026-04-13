package model.state;

public interface PagamentoState {
    String getNome();

    boolean canBePaid();

    PagamentoState pay();
}

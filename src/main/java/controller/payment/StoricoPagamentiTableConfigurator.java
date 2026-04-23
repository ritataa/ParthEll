package controller.payment;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Region;
import javafx.util.Callback;
import patterns.state.Pagamento;
import service.UIFormatsService;

/**
 * Configura la tabella storico pagamenti con formatter e stili condizionali.
 */
public class StoricoPagamentiTableConfigurator {

    // Servizio centralizzato per formattazione importi e regole visuali.
    private final UIFormatsService formatsService;

    public StoricoPagamentiTableConfigurator(UIFormatsService formatsService) {
        this.formatsService = formatsService;
    }

    /**
     * Applica cell factory e row factory per visualizzazione importi, promo e stato.
     */
    public void configure(
        TableView<Pagamento> storicoPagamentiTable,
        TableColumn<Pagamento, String> mesePagamentoColumn,
        TableColumn<Pagamento, Integer> annoPagamentoColumn,
        TableColumn<Pagamento, Double> importoPagamentoColumn,
        TableColumn<Pagamento, String> promoPagamentoColumn,
        TableColumn<Pagamento, String> statoPagamentoColumn
    ) {
        if (mesePagamentoColumn != null) {
            mesePagamentoColumn.setCellValueFactory(new PropertyValueFactory<>("mese"));
        }
        if (annoPagamentoColumn != null) {
            annoPagamentoColumn.setCellValueFactory(new PropertyValueFactory<>("anno"));
        }
        if (importoPagamentoColumn != null) {
            importoPagamentoColumn.setCellValueFactory(new PropertyValueFactory<>("importo"));
            // Factory per colonna importo: formatta il valore in euro.
            importoPagamentoColumn.setCellFactory(new ImportoCellFactory());
        }
        if (promoPagamentoColumn != null) {
            promoPagamentoColumn.setCellValueFactory(new PropertyValueFactory<>("promo"));
            // Factory per colonna promo: abilita wrapping e allineamento a sinistra.
            promoPagamentoColumn.setCellFactory(new PromoCellFactory());
        }
        if (statoPagamentoColumn != null) {
            statoPagamentoColumn.setCellValueFactory(new PropertyValueFactory<>("stato"));
            // Factory per colonna stato: evidenzia in rosso i pagamenti da saldare.
            statoPagamentoColumn.setCellFactory(new StatoCellFactory());
        }
        if (storicoPagamentiTable != null) {
            storicoPagamentiTable.setFixedCellSize(-1);
            // Factory righe: adatta altezza e colora l'intera riga se lo stato e' "Da pagare".
            storicoPagamentiTable.setRowFactory(new StoricoRowFactory());
        }
    }

    // Restituisce celle formattate in valuta per la colonna importo.
    private class ImportoCellFactory implements Callback<TableColumn<Pagamento, Double>, TableCell<Pagamento, Double>> {
        @Override
        public TableCell<Pagamento, Double> call(TableColumn<Pagamento, Double> column) {
            return new TableCell<Pagamento, Double>() {
                @Override
                protected void updateItem(Double item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setStyle("");
                        return;
                    }
                    setText(formatsService.formatEuro(item));
                }
            };
        }
    }

    // Restituisce celle promo con testo multilinea e formato leggibile.
    private class PromoCellFactory implements Callback<TableColumn<Pagamento, String>, TableCell<Pagamento, String>> {
        @Override
        public TableCell<Pagamento, String> call(TableColumn<Pagamento, String> column) {
            return new TableCell<Pagamento, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setText(null);
                        setStyle("");
                        return;
                    }

                    setText(formatsService.formatPromoForCell(item));
                    setWrapText(true);
                    setStyle("-fx-alignment: CENTER-LEFT;");
                }
            };
        }
    }

    // Restituisce celle stato con evidenza visiva per i pagamenti non saldati.
    private class StatoCellFactory implements Callback<TableColumn<Pagamento, String>, TableCell<Pagamento, String>> {
        @Override
        public TableCell<Pagamento, String> call(TableColumn<Pagamento, String> column) {
            return new TableCell<Pagamento, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setStyle("");
                        return;
                    }
                    setText(item);
                    if (formatsService.isDaPagare(item)) {
                        setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                    } else {
                        setStyle("");
                    }
                }
            };
        }
    }

    // Restituisce righe tabella con altezza dinamica e stile condizionale sullo stato.
    private class StoricoRowFactory implements Callback<TableView<Pagamento>, TableRow<Pagamento>> {
        @Override
        public TableRow<Pagamento> call(TableView<Pagamento> tableView) {
            return new TableRow<Pagamento>() {
                @Override
                protected void updateItem(Pagamento item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setStyle("");
                        setPrefHeight(Region.USE_COMPUTED_SIZE);
                        return;
                    }
                    setPrefHeight(Region.USE_COMPUTED_SIZE);
                    if (formatsService.isDaPagare(item.getStato())) {
                        setStyle("-fx-text-fill: red;");
                    } else {
                        setStyle("");
                    }
                }
            };
        }
    }
}

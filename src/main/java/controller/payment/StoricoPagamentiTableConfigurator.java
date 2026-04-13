package controller.payment;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Region;
import model.Pagamento;
import service.UIFormatsService;

public class StoricoPagamentiTableConfigurator {

    private final UIFormatsService formatsService;

    public StoricoPagamentiTableConfigurator(UIFormatsService formatsService) {
        this.formatsService = formatsService;
    }

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
            importoPagamentoColumn.setCellFactory(col -> new TableCell<>() {
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
            });
        }
        if (promoPagamentoColumn != null) {
            promoPagamentoColumn.setCellValueFactory(new PropertyValueFactory<>("promo"));
            promoPagamentoColumn.setCellFactory(col -> new TableCell<>() {
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
            });
        }
        if (statoPagamentoColumn != null) {
            statoPagamentoColumn.setCellValueFactory(new PropertyValueFactory<>("stato"));
            statoPagamentoColumn.setCellFactory(col -> new TableCell<>() {
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
            });
        }
        if (storicoPagamentiTable != null) {
            storicoPagamentiTable.setFixedCellSize(-1);
            storicoPagamentiTable.setRowFactory(tv -> new javafx.scene.control.TableRow<>() {
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
            });
        }
    }
}

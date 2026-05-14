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

/*
 * LEGENDA: STANDARD DI DOCUMENTAZIONE JAVADOC
 * @author / @version: Tracciano paternità e manutenzione della classe.
 * @param: Definisce i vincoli di input richiesti dal metodo per un uso corretto.
 * @return: Esplicita l'output garantito o l'assenza di risultato, così il Client sa cosa può usare.
 */

/**
    Configura la tabella dello storico pagamenti con formattazione importi, promozioni e stato.
    Centralizza la configurazione delle celle e righe della tabella storico pagamenti. Utilizza: 
    - Factory Pattern tramite inner-class ImportoCellFactory, PromoCellFactory, StatoCellFactory, StoricoRowFactory per creare celle e righe specializzate con logiche di rendering diverse.
 *
 * @author ParthEll Team
 * @version 1.0
 */
public class StoricoPagamentiTableConfigurator {

    // Servizio centralizzato per formattazione importi e regole visuali.
    private final UIFormatsService formatsService;

    /**
     * Crea il configuratore con il servizio condiviso per i formati UI.
     * Il servizio non deve essere null perché viene usato da tutte le factory interne.
     *
     * @param formatsService servizio per formattazione e regole visive; non deve essere null.
     */
    public StoricoPagamentiTableConfigurator(UIFormatsService formatsService) {
        this.formatsService = formatsService;
    }

    /**
     * Applica cell factory e row factory per visualizzare importi, promozioni e stato.
     * Se una colonna o la tabella è null, il metodo salta solo quel pezzo di configurazione.
     *
     * @param storicoPagamentiTable tabella dello storico; può essere null se non disponibile.
     * @param mesePagamentoColumn colonna del mese; può essere null se non disponibile.
     * @param annoPagamentoColumn colonna dell'anno; può essere null se non disponibile.
     * @param importoPagamentoColumn colonna dell'importo; può essere null se non disponibile.
     * @param promoPagamentoColumn colonna della promozione; può essere null se non disponibile.
     * @param statoPagamentoColumn colonna dello stato; può essere null se non disponibile.
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
        // Sicurezza: obbliga Java a verificare che sto davvero implementando call() dell'interfaccia Callback, evitando errori di battitura.
        @Override
        public TableCell<Pagamento, Double> call(TableColumn<Pagamento, Double> column) {
            return new TableCell<Pagamento, Double>() {
                // Sicurezza: obbliga Java a verificare che sto davvero sovrascrivendo updateItem() della classe TableCell, evitando errori di battitura.
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
        // Sicurezza: obbliga Java a verificare che sto davvero implementando call() dell'interfaccia Callback, evitando errori di battitura.
        @Override
        public TableCell<Pagamento, String> call(TableColumn<Pagamento, String> column) {
            return new TableCell<Pagamento, String>() {
                // Sicurezza: obbliga Java a verificare che sto davvero sovrascrivendo updateItem() della classe TableCell, evitando errori di battitura.
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
        // Sicurezza: obbliga Java a verificare che sto davvero implementando call() dell'interfaccia Callback, evitando errori di battitura.
        @Override
        public TableCell<Pagamento, String> call(TableColumn<Pagamento, String> column) {
            return new TableCell<Pagamento, String>() {
                // Sicurezza: obbliga Java a verificare che sto davvero sovrascrivendo updateItem() della classe TableCell, evitando errori di battitura.
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
        // Sicurezza: obbliga Java a verificare che sto davvero implementando call() dell'interfaccia Callback, evitando errori di battitura.
        @Override
        public TableRow<Pagamento> call(TableView<Pagamento> tableView) {
            return new TableRow<Pagamento>() {
                // Sicurezza: obbliga Java a verificare che sto davvero sovrascrivendo updateItem() della classe TableRow, evitando errori di battitura.
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

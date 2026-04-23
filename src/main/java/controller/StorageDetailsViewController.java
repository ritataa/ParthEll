package controller;

import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import patterns.state.Pagamento;
import service.UIFormatsService;

/**
 * Gestisce il passaggio tra vista tabellare e dettaglio dello storico pagamenti.
 */
public class StorageDetailsViewController {

    private final VBox storicoClassicoPane;
    private final VBox storicoDettaglioPane;
    private final Label dettaglioMeseLabel;
    private final Label dettaglioAnnoLabel;
    private final Label dettaglioImportoLabel;
    private final Label dettaglioStatoLabel;
    private final TextArea dettaglioPromoArea;
    private final UIFormatsService uiFormatsService;

    public StorageDetailsViewController(
        VBox storicoClassicoPane,
        VBox storicoDettaglioPane,
        Label dettaglioMeseLabel,
        Label dettaglioAnnoLabel,
        Label dettaglioImportoLabel,
        Label dettaglioStatoLabel,
        TextArea dettaglioPromoArea,
        UIFormatsService uiFormatsService
    ) {
        this.storicoClassicoPane = storicoClassicoPane;
        this.storicoDettaglioPane = storicoDettaglioPane;
        this.dettaglioMeseLabel = dettaglioMeseLabel;
        this.dettaglioAnnoLabel = dettaglioAnnoLabel;
        this.dettaglioImportoLabel = dettaglioImportoLabel;
        this.dettaglioStatoLabel = dettaglioStatoLabel;
        this.dettaglioPromoArea = dettaglioPromoArea;
        this.uiFormatsService = uiFormatsService;
    }

    /**
     * Mostra il pannello dettaglio per il pagamento selezionato.
     *
     * @return true se il dettaglio e stato mostrato, false se la selezione e nulla
     */
    public boolean showDetails(Pagamento selezionato) {
        if (selezionato == null) {
            return false;
        }

        if (storicoClassicoPane != null) {
            storicoClassicoPane.setVisible(false);
            storicoClassicoPane.setManaged(false);
        }
        if (storicoDettaglioPane != null) {
            storicoDettaglioPane.setVisible(true);
            storicoDettaglioPane.setManaged(true);
        }

        if (dettaglioMeseLabel != null) {
            dettaglioMeseLabel.setText(selezionato.getMese());
        }
        if (dettaglioAnnoLabel != null) {
            dettaglioAnnoLabel.setText(String.valueOf(selezionato.getAnno()));
        }
        if (dettaglioImportoLabel != null) {
            dettaglioImportoLabel.setText(uiFormatsService.formatEuro(selezionato.getImporto()));
        }
        if (dettaglioStatoLabel != null) {
            dettaglioStatoLabel.setText(selezionato.getStato());
        }
        if (dettaglioPromoArea != null) {
            dettaglioPromoArea.setText(uiFormatsService.formatPromoDetails(selezionato.getPromo()));
        }

        return true;
    }

    /**
     * Ripristina la vista principale dello storico.
     */
    public void hideDetails() {
        if (storicoDettaglioPane != null) {
            storicoDettaglioPane.setVisible(false);
            storicoDettaglioPane.setManaged(false);
        }
        if (storicoClassicoPane != null) {
            storicoClassicoPane.setVisible(true);
            storicoClassicoPane.setManaged(true);
        }
    }
}

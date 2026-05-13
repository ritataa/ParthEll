package controller;

import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import patterns.state.Pagamento;
import service.UIFormatsService;

/*
 * LEGENDA: STANDARD DI DOCUMENTAZIONE JAVADOC
 * @author / @version: Tracciano paternità e manutenzione della classe.
 * @param: Definisce i vincoli di input richiesti dal metodo per un uso corretto.
 * @return: Esplicita l'output garantito o l'assenza di risultato, così il Client sa cosa può usare.
 */

/**
 * Gestisce il passaggio tra vista tabellare e vista di dettaglio dello storico pagamenti.
 * Centralizza la composizione dei dati mostrati per evitare duplicazione tra controller.
 * Usa un piccolo controller di supporto perché il cambio pannello è un compito UI autonomo.
 *
 * @author ParthEll Team
 * @version 1.0
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

    /**
     * Costruisce il controller con i nodi grafici da sincronizzare.
     * Tutti i riferimenti devono appartenere alla stessa schermata JavaFX.
     *
     * @param storicoClassicoPane pannello della vista tabellare; può essere null se non usato.
     * @param storicoDettaglioPane pannello della vista dettaglio; può essere null se non usato.
     * @param dettaglioMeseLabel label per il mese del pagamento; può essere null se non usata.
     * @param dettaglioAnnoLabel label per l'anno del pagamento; può essere null se non usata.
     * @param dettaglioImportoLabel label per l'importo formattato; può essere null se non usata.
     * @param dettaglioStatoLabel label per lo stato del pagamento; può essere null se non usata.
     * @param dettaglioPromoArea area testo per i dettagli promozione; può essere null se non usata.
     * @param uiFormatsService servizio usato per la formattazione dei valori; non deve essere null.
     */
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
     * Mostra il pannello dettaglio per il pagamento selezionato e nasconde la vista compatta.
     * Se la selezione è nulla, ritorna false senza modificare l'interfaccia.
     *
     * @param selezionato pagamento da visualizzare; se null il metodo non aggiorna la UI.
     * @return true se il dettaglio è stato mostrato, false se la selezione è nulla.
     */
    public boolean showDetails(Pagamento selezionato) {
        if (selezionato == null) {
            return false;
        }

        // Nascondo la tabella e apro il pannello dettaglio nello stesso passaggio.
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
     * Ripristina la vista principale dello storico e chiude il pannello di dettaglio.
     * Non restituisce valori perché agisce solo sulla visibilità dei nodi.
     *
     * @return nessun valore; aggiorna solo lo stato della UI.
     */
    public void hideDetails() {
        // Torno alla vista tabellare per far ripartire la selezione normale dello storico.
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

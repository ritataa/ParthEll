package service;

/**
 * Risultato immutabile di una operazione applicativa.
 *
 * @param success indica se l'operazione e andata a buon fine
 * @param message messaggio descrittivo destinato alla UI
 */
public record OperationResult(boolean success, String message) {
}

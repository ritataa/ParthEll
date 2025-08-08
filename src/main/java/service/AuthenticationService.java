package service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Singleton per la gestione dell'autenticazione degli utenti.
 * Gestisce il caricamento e la verifica delle credenziali per amministratori e clienti.
 * Implementa il pattern Singleton per garantire una sola istanza del servizio di autenticazione.
 * 
 * @author ParthEll Team
 * @version 1.0
 */
public class AuthenticationService {
    
    /** Istanza singleton del servizio di autenticazione */
    private static AuthenticationService instance;
    
    /** Mappa delle credenziali degli amministratori */
    private final Map<String, String> amministratori = new HashMap<>();
    
    /** Mappa delle credenziali degli abbonati */
    private final Map<String, String> abbonati = new HashMap<>();
    
    /**
     * Costruttore privato per implementare il pattern Singleton.
     * Carica automaticamente le credenziali dai file CSV.
     */
    private AuthenticationService() {
        caricaCredenziali();
    }
    
    /**
     * Restituisce l'istanza singleton del servizio di autenticazione.
     * 
     * @return l'istanza singleton di AuthenticationService
     */
    public static synchronized AuthenticationService getInstance() {
        if (instance == null) {
            instance = new AuthenticationService();
        }
        return instance;
    }
    
    /**
     * Carica le credenziali dai file CSV.
     * Metodo privato chiamato dal costruttore.
     */
    private void caricaCredenziali() {
        caricaAmministratori();
        caricaAbbonati();
    }
    
    /**
     * Carica le credenziali degli amministratori dal file CSV.
     */
    private void caricaAmministratori() {
        try (InputStream is = getClass().getResourceAsStream("/data/amministratore.csv");
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            
            String line;
            boolean isFirstLine = true;
            while ((line = reader.readLine()) != null) {
                // Salta l'header
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }
                String[] parts = line.split(",");
                if (parts.length >= 2) {
                    amministratori.put(parts[0].trim(), parts[1].trim());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Errore nel caricamento credenziali amministratori", e);
        }
    }
    
    /**
     * Carica le credenziali degli abbonati dal file CSV.
     */
    private void caricaAbbonati() {
        try (InputStream is = getClass().getResourceAsStream("/data/abbonato.csv");
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            
            String line;
            boolean isFirstLine = true;
            while ((line = reader.readLine()) != null) {
                // Salta l'header
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }
                String[] parts = line.split(",");
                if (parts.length >= 2) {
                    abbonati.put(parts[0].trim(), parts[1].trim());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Errore nel caricamento credenziali abbonati", e);
        }
    }
    
    /**
     * Autentica un utente verificando le credenziali.
     * 
     * @param email l'email dell'utente
     * @param password la password dell'utente
     * @return il tipo di utente ("admin", "cliente") o null se non autenticato
     */
    public String authenticate(String email, String password) {
        if (amministratori.containsKey(email) && amministratori.get(email).equals(password)) {
            return "admin";
        }
        if (abbonati.containsKey(email) && abbonati.get(email).equals(password)) {
            return "cliente";
        }
        return null;
    }
    
    /**
     * Verifica se un utente è un amministratore.
     * 
     * @param email l'email dell'utente
     * @param password la password dell'utente
     * @return true se è un amministratore, false altrimenti
     */
    public boolean isAdmin(String email, String password) {
        return "admin".equals(authenticate(email, password));
    }
    
    /**
     * Verifica se un utente è un cliente.
     * 
     * @param email l'email dell'utente
     * @param password la password dell'utente
     * @return true se è un cliente, false altrimenti
     */
    public boolean isCliente(String email, String password) {
        return "cliente".equals(authenticate(email, password));
    }
}

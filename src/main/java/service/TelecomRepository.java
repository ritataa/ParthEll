package service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.PianoTariffario;
import model.Promozione;
import model.TipoPiano;
import model.Utilizzo;
import model.conto.Conto;
import model.conto.ContoFisso;
import model.conto.ContoRicaricabile;
import patterns.builder.Abbonato;
import patterns.singleton.DatabaseManager;
import patterns.state.Pagamento;

/*
 * LEGENDA: STANDARD DI DOCUMENTAZIONE JAVADOC
 * @author / @version: tracciano paternità e manutenzione della classe.
 * @param: definisce i vincoli e il significato degli input.
 * @return: definisce l'output garantito o nullo per il chiamante.
 * @throws: esplicita le eccezioni gestibili dal chiamante.
 */

/**
 * Repository JDBC per leggere e aggiornare anagrafica, promozioni, utilizzi e pagamenti.
 * Centralizza l'accesso ai dati e riduce il coupling tra controller e database.
 * Usa il pattern Repository per isolare le query SQL dalla logica applicativa.
 *
 * @author ParthEll Team
 * @version 1.0
 */
public class TelecomRepository {

    private final DatabaseManager databaseManager = DatabaseManager.getInstance();

    /**
     * Verifica se l'utente esiste come amministratore o come cliente.
     * Restituisce il ruolo trovato oppure null se le credenziali non sono valide.
     *
     * @param email email non vuota dell'utente da autenticare.
     * @param password password associata all'account.
     * @return "admin", "cliente" o null se nessuna corrispondenza è valida.
     * @throws RuntimeException se l'accesso al database fallisce.
     */
    public String authenticate(String email, String password) {
        String adminSql = "SELECT 1 FROM amministratore WHERE email = ? AND password = ?";
        if (exists(adminSql, email, password)) {
            return "admin";
        }

        String clienteSql = "SELECT 1 FROM abbonato WHERE email = ? AND password = ?";
        if (exists(clienteSql, email, password)) {
            return "cliente";
        }

        return null;
    }

    /**
     * Carica tutti gli abbonati ordinati per cognome e nome.
     *
     * @return lista completa degli abbonati presenti a database.
     * @throws RuntimeException se la lettura JDBC fallisce.
     */
    public List<Abbonato> findAllAbbonati() {
        List<Abbonato> result = new ArrayList<>();
        String sql = """
            SELECT nome, cognome, email, residenza, numero_telefono, piano_tariffario
            FROM abbonato
            ORDER BY cognome, nome
            """;

        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                result.add(Abbonato.builder()
                    .nome(rs.getString("nome"))
                    .cognome(rs.getString("cognome"))
                    .email(rs.getString("email"))
                    .residenza(rs.getString("residenza"))
                    .numeroTelefono(rs.getString("numero_telefono"))
                    .pianoTariffario(toTipoPiano(rs.getString("piano_tariffario")))
                    .build());
            }
            return result;
        } catch (SQLException exception) {
            throw new RuntimeException("Errore lettura abbonati", exception);
        }
    }

    /**
     * Cerca un abbonato tramite email e ricostruisce anche il conto associato.
     * Restituisce null quando l'email non esiste o è vuota.
     *
     * @param email email non vuota da cercare.
     * @return l'abbonato trovato oppure null se assente.
     * @throws RuntimeException se il database non risponde correttamente.
     */
    public Abbonato findAbbonatoByEmail(String email) {
        if (email == null || email.isBlank()) {
            return null;
        }

        String sql = """
            SELECT nome, cognome, email, residenza, numero_telefono, piano_tariffario, conto, saldo, numero_carta, scadenza_carta, cvv_carta, intestatario_carta
            FROM abbonato
            WHERE email = ?
            """;

        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email.trim());
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return null;
                }

                // Ripristina il tipo di conto leggendo il valore salvato nel record.
                String contoDbValue = resultSet.getString("conto");
                double saldo = resultSet.getDouble("saldo");
                Conto conto = "ricaricabile".equalsIgnoreCase(contoDbValue)
                    ? new ContoRicaricabile(Math.max(0.0, saldo))
                    : new ContoFisso();

                return Abbonato.builder()
                    .nome(resultSet.getString("nome"))
                    .cognome(resultSet.getString("cognome"))
                    .email(resultSet.getString("email"))
                    .residenza(resultSet.getString("residenza"))
                    .numeroTelefono(resultSet.getString("numero_telefono"))
                    .pianoTariffario(toTipoPiano(resultSet.getString("piano_tariffario")))
                    .conto(conto)
                    .numeroCarta(resultSet.getString("numero_carta"))
                    .scadenzaCarta(resultSet.getString("scadenza_carta"))
                    .cvvCarta(resultSet.getString("cvv_carta"))
                    .intestatarioCarta(resultSet.getString("intestatario_carta"))
                    .build();
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Errore lettura abbonato", exception);
        }
    }

    /**
     * Restituisce tutti gli utilizzi con il riepilogo delle promozioni attive.
     *
     * @return lista di utilizzi ordinata per numero telefonico.
     * @throws RuntimeException se la query di aggregazione fallisce.
     */
    public List<Utilizzo> findAllUtilizzi() {
        List<Utilizzo> result = new ArrayList<>();
        String sql = """
            SELECT
                u.numero,
                u.nome,
                u.cognome,
                u.email,
                u.chiamate,
                u.sms,
                u.dati,
                COALESCE(GROUP_CONCAT(ap.promozione_nome, ', '), '') AS promo
            FROM utilizzo u
            LEFT JOIN abbonato_promozione ap ON ap.email = u.email
            GROUP BY u.numero, u.nome, u.cognome, u.email, u.chiamate, u.sms, u.dati
            ORDER BY u.numero
            """;

        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                // Unisco i dati aggregati in un DTO pronto per la UI.
                result.add(new Utilizzo(
                    rs.getString("numero"),
                    rs.getString("nome"),
                    rs.getString("cognome"),
                    rs.getString("email"),
                    rs.getInt("chiamate"),
                    rs.getInt("sms"),
                    rs.getInt("dati"),
                    rs.getString("promo")
                ));
            }
            return result;
        } catch (SQLException exception) {
            throw new RuntimeException("Errore lettura utilizzi", exception);
        }
    }

    /**
     * Restituisce tutte le promozioni presenti nel catalogo.
     *
     * @return lista completa delle promozioni disponibili.
     * @throws RuntimeException se la lettura del catalogo fallisce.
     */
    public List<Promozione> findAllPromozioni() {
        List<Promozione> result = new ArrayList<>();
        String sql = "SELECT nome, costo, descrizione FROM promozione ORDER BY nome";

        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                result.add(new Promozione(
                    rs.getString("nome"),
                    rs.getString("descrizione"),
                    rs.getDouble("costo")
                ));
            }
            return result;
        } catch (SQLException exception) {
            throw new RuntimeException("Errore lettura promozioni", exception);
        }
    }

    /**
     * Restituisce i nomi di tutti i piani tariffari ordinati alfabeticamente.
     *
     * @return lista dei nomi piano usabili dall'interfaccia.
     * @throws RuntimeException se la query fallisce.
     */
    public List<String> findAllPianiTariffari() {
        List<String> result = new ArrayList<>();
        String sql = "SELECT nome FROM piano_tariffario ORDER BY nome";

        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                result.add(rs.getString("nome"));
            }
            return result;
        } catch (SQLException exception) {
            throw new RuntimeException("Errore lettura piani tariffari", exception);
        }
    }

    /**
     * Crea un nuovo cliente con valori base e numero telefonico generato.
     *
     * @param email email univoca del cliente.
     * @param password password da salvare per l'accesso.
     * @param nome nome del cliente.
     * @param cognome cognome del cliente.
     * @throws RuntimeException se l'inserimento o la sincronizzazione falliscono.
     */
    public void addCliente(String email, String password, String nome, String cognome) {
        String sql = """
            INSERT INTO abbonato(email, password, nome, cognome, residenza, numero_telefono, piano_tariffario, conto, saldo)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            // Genero un numero progressivo semplice per il nuovo cliente.
            String numeroTelefono = generateNumeroTelefono(connection);
            statement.setString(1, email);
            statement.setString(2, password);
            statement.setString(3, nome);
            statement.setString(4, cognome);
            statement.setString(5, "N/D");
            statement.setString(6, numeroTelefono);
            statement.setString(7, "base");
            statement.setString(8, "Fisso");
            statement.setDouble(9, 0.0);
            statement.executeUpdate();
            createUtilizzoIfMissing(connection, numeroTelefono);
        } catch (SQLException exception) {
            throw new RuntimeException("Errore inserimento cliente", exception);
        }
    }

    /**
     * Registra un cliente completo verificando unicità di email e telefono.
     * Restituisce solo una eccezione RuntimeException in caso di dati non validi o errori DB.
     *
     * @param email email non già presente.
     * @param password password dell'account.
     * @param nome nome del cliente.
     * @param cognome cognome del cliente.
     * @param residenza indirizzo non vuoto.
     * @param numeroTelefono numero telefonico non già presente.
     * @param pianoTariffario piano tariffario esistente.
     * @param conto tipo conto valido.
     * @param numeroCarta numero carta opzionale.
     * @param scadenzaCarta scadenza carta opzionale.
     * @param cvvCarta cvv carta opzionale.
     * @param intestatarioCarta intestatario carta opzionale.
     * @throws RuntimeException se un vincolo viene violato o il database fallisce.
     */
    public void registerCliente(
        String email,
        String password,
        String nome,
        String cognome,
        String residenza,
        String numeroTelefono,
        String pianoTariffario,
        String conto,
        String numeroCarta,
        String scadenzaCarta,
        String cvvCarta,
        String intestatarioCarta
    ) {
        // Controllo preliminare: evito duplicati prima dell'INSERT.
        String checkEmailSql = "SELECT 1 FROM abbonato WHERE email = ?";
        String checkNumeroSql = "SELECT 1 FROM abbonato WHERE numero_telefono = ?";

        try (Connection connection = databaseManager.getConnection()) {
            // Verifico subito se l'email è già occupata.
            try (PreparedStatement checkEmail = connection.prepareStatement(checkEmailSql)) {
                checkEmail.setString(1, email.trim());
                try (ResultSet rs = checkEmail.executeQuery()) {
                    if (rs.next()) {
                        throw new RuntimeException("Errore registrazione cliente: email già esistente");
                    }
                }
            }

            // Verifico anche il numero di telefono per mantenere la chiave logica unica.
            try (PreparedStatement checkNumero = connection.prepareStatement(checkNumeroSql)) {
                checkNumero.setString(1, numeroTelefono.trim());
                try (ResultSet rs = checkNumero.executeQuery()) {
                    if (rs.next()) {
                        throw new RuntimeException("Errore registrazione cliente: numero di telefono già esistente");
                    }
                }
            }

            // Solo dopo i controlli eseguo l'inserimento definitivo.
            String sql = """
                INSERT INTO abbonato(email, password, nome, cognome, residenza, numero_telefono, piano_tariffario, conto, saldo, numero_carta, scadenza_carta, cvv_carta, intestatario_carta)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                String pianoNormalizzato = pianoTariffario.trim().toLowerCase();
                if (!existsPianoTariffario(connection, pianoNormalizzato)) {
                    throw new RuntimeException("Piano tariffario non valido");
                }

                String contoNormalizzato = normalizeContoValue(conto);
                statement.setString(1, email.trim());
                statement.setString(2, password);
                statement.setString(3, nome.trim());
                statement.setString(4, cognome.trim());
                statement.setString(5, residenza.trim());
                statement.setString(6, numeroTelefono.trim());
                statement.setString(7, pianoNormalizzato);
                statement.setString(8, contoNormalizzato);
                statement.setDouble(9, 0.0);
                statement.setString(10, numeroCarta);
                statement.setString(11, scadenzaCarta);
                statement.setString(12, cvvCarta);
                statement.setString(13, intestatarioCarta);
                statement.executeUpdate();
                createUtilizzoIfMissing(connection, numeroTelefono.trim());
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Errore registrazione cliente", exception);
        }
    }

    /**
     * Variante compatta della registrazione con dati opzionali assenti.
     *
     * @param email email non già presente.
     * @param password password dell'account.
     * @param nome nome del cliente.
     * @param cognome cognome del cliente.
     * @param residenza indirizzo non vuoto.
     * @param numeroTelefono numero telefonico non già presente.
     * @param pianoTariffario piano tariffario esistente.
     * @param conto tipo conto valido.
     * @throws RuntimeException se i controlli di registrazione falliscono.
     */
    public void registerCliente(
        String email,
        String password,
        String nome,
        String cognome,
        String residenza,
        String numeroTelefono,
        String pianoTariffario,
        String conto
    ) {
        registerCliente(email, password, nome, cognome, residenza, numeroTelefono, pianoTariffario, conto, null, null, null, null);
    }

    /**
     * Variante minima della registrazione che usa il conto fisso di default.
     *
     * @param email email non già presente.
     * @param password password dell'account.
     * @param nome nome del cliente.
     * @param cognome cognome del cliente.
     * @param residenza indirizzo non vuoto.
     * @param numeroTelefono numero telefonico non già presente.
     * @param pianoTariffario piano tariffario esistente.
     * @throws RuntimeException se i controlli di registrazione falliscono.
     */
    public void registerCliente(
        String email,
        String password,
        String nome,
        String cognome,
        String residenza,
        String numeroTelefono,
        String pianoTariffario
    ) {
        registerCliente(email, password, nome, cognome, residenza, numeroTelefono, pianoTariffario, "Fisso");
    }

    /**
     * Aderisce una promozione esistente per un abbonato.
     *
     * @param email email dell'abbonato già registrato.
     * @param nomePromozione nome della promozione da associare.
     * @return true se l'associazione viene inserita, false se era già presente.
     * @throws RuntimeException se la promozione non esiste o il database fallisce.
     */
    public boolean aderisciPromozione(String email, String nomePromozione) {
        String checkPromoSql = "SELECT 1 FROM promozione WHERE nome = ?";
        String insertSql = "INSERT OR IGNORE INTO abbonato_promozione(email, promozione_nome) VALUES (?, ?)";

        try (Connection connection = databaseManager.getConnection()) {
            try (PreparedStatement checkStatement = connection.prepareStatement(checkPromoSql)) {
                checkStatement.setString(1, nomePromozione);
                try (ResultSet resultSet = checkStatement.executeQuery()) {
                    if (!resultSet.next()) {
                        throw new RuntimeException("Promozione non trovata");
                    }
                }
            }

            try (PreparedStatement insertStatement = connection.prepareStatement(insertSql)) {
                insertStatement.setString(1, email);
                insertStatement.setString(2, nomePromozione);
                return insertStatement.executeUpdate() > 0;
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Errore adesione promozione", exception);
        }
    }

    /**
     * Rimuove l'associazione tra un abbonato e una promozione.
     *
     * @param email email dell'abbonato.
     * @param nomePromozione promozione da disdire.
     * @return true se almeno una riga è stata rimossa, false altrimenti.
     * @throws RuntimeException se l'operazione JDBC fallisce.
     */
    public boolean disdiciPromozione(String email, String nomePromozione) {
        String sql = "DELETE FROM abbonato_promozione WHERE email = ? AND promozione_nome = ?";
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email);
            statement.setString(2, nomePromozione);
            return statement.executeUpdate() > 0;
        } catch (SQLException exception) {
            throw new RuntimeException("Errore disdetta promozione", exception);
        }
    }

    /**
     * Aggiorna il saldo del conto dell'abbonato identificato dalla email.
     *
     * @param email email dell'abbonato da aggiornare.
     * @param nuovoSaldo nuovo saldo desiderato; i valori negativi vengono portati a zero.
     * @return true se il record è stato aggiornato, false se l'email non è valida o assente.
     * @throws RuntimeException se il database non consente l'update.
     */
    public boolean aggiornaSaldoConto(String email, double nuovoSaldo) {
        if (email == null || email.isBlank()) {
            return false;
        }
        String sql = "UPDATE abbonato SET saldo = ? WHERE email = ?";
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setDouble(1, Math.max(0.0, nuovoSaldo));
            statement.setString(2, email);
            return statement.executeUpdate() > 0;
        } catch (SQLException exception) {
            throw new RuntimeException("Errore aggiornamento saldo conto", exception);
        }
    }

    /**
     * Recupera l'utilizzo di un singolo abbonato, creando prima la riga se manca.
     *
     * @param email email dell'abbonato da cercare.
     * @return utilizzo trovato oppure un oggetto vuoto se non esiste alcun risultato.
     * @throws RuntimeException se il recupero o la sincronizzazione falliscono.
     */
    public Utilizzo findUtilizzoByEmail(String email) {
        String sql = """
            SELECT
                u.numero,
                u.nome,
                u.cognome,
                u.email,
                u.chiamate,
                u.sms,
                u.dati,
                COALESCE(GROUP_CONCAT(ap.promozione_nome, ', '), '') AS promo
            FROM abbonato a
            LEFT JOIN utilizzo u ON u.numero = a.numero_telefono
            LEFT JOIN abbonato_promozione ap ON ap.email = a.email
            WHERE a.email = ?
            GROUP BY u.numero, u.nome, u.cognome, u.email, u.chiamate, u.sms, u.dati
            """;

        try (Connection connection = databaseManager.getConnection()) {
            // Garantisco che l'utente abbia sempre una riga di utilizzo coerente.
            ensureUtilizzoByEmail(connection, email);
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, email);
                try (ResultSet rs = statement.executeQuery()) {
                    if (rs.next()) {
                        return new Utilizzo(
                            rs.getString("numero"),
                            rs.getString("nome"),
                            rs.getString("cognome"),
                            rs.getString("email"),
                            rs.getInt("chiamate"),
                            rs.getInt("sms"),
                            rs.getInt("dati"),
                            rs.getString("promo")
                        );
                    }
                }
            }
            return new Utilizzo("", "", "", "", 0, 0, 0, "");
        } catch (SQLException exception) {
            throw new RuntimeException("Errore lettura utilizzo cliente", exception);
        }
    }

    /**
     * Inserisce una nuova promozione nel catalogo.
     *
     * @param nome nome univoco della promozione.
     * @param costo costo della promozione.
     * @param descrizione descrizione testuale del beneficio.
     * @throws RuntimeException se l'inserimento SQL fallisce.
     */
    public void addPromozione(String nome, double costo, String descrizione) {
        String sql = "INSERT INTO promozione(nome, costo, descrizione) VALUES (?, ?, ?)";
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, nome);
            statement.setDouble(2, costo);
            statement.setString(3, descrizione);
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new RuntimeException("Errore inserimento promozione", exception);
        }
    }

    /**
     * Calcola il totale mensile dato piano tariffario e promozioni attive.
     *
     * @param email email dell'abbonato.
     * @return totale mensile calcolato, oppure 0.0 se non esiste alcun dato utile.
     * @throws RuntimeException se la query di aggregazione fallisce.
     */
    public double calcolaTotaleMensileByEmail(String email) {
        String sql = """
            SELECT
                COALESCE(p.costo_mensile, 0) + COALESCE(SUM(pr.costo), 0) AS totale
            FROM abbonato a
            LEFT JOIN piano_tariffario p ON p.nome = a.piano_tariffario
            LEFT JOIN abbonato_promozione ap ON ap.email = a.email
            LEFT JOIN promozione pr ON pr.nome = ap.promozione_nome
            WHERE a.email = ?
            GROUP BY p.costo_mensile
            """;

        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("totale");
                }
                return 0.0;
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Errore calcolo totale mensile", exception);
        }
    }

    /**
     * Allinea o crea il pagamento del mese corrente per l'abbonato.
     *
     * @param email email dell'abbonato da aggiornare.
     * @throws RuntimeException se il recupero o l'aggiornamento dei pagamenti fallisce.
     */
    public void aggiornaPagamentoMeseCorrente(String email) {
        double totaleCorrente = calcolaTotaleMensileByEmail(email);
        String promoCorrente = getPromozioniAttiveString(email);
        String selectSql = """
            SELECT mese, anno, stato
            FROM pagamenti
            WHERE id_abbonato = ?
            ORDER BY anno ASC,
                CASE mese
                    WHEN 'Gennaio' THEN 1
                    WHEN 'Febbraio' THEN 2
                    WHEN 'Marzo' THEN 3
                    WHEN 'Aprile' THEN 4
                    WHEN 'Maggio' THEN 5
                    WHEN 'Giugno' THEN 6
                    WHEN 'Luglio' THEN 7
                    WHEN 'Agosto' THEN 8
                    WHEN 'Settembre' THEN 9
                    WHEN 'Ottobre' THEN 10
                    WHEN 'Novembre' THEN 11
                    WHEN 'Dicembre' THEN 12
                    ELSE 99
                END ASC
            """;
        String insertSql = "INSERT INTO pagamenti(id_abbonato, mese, anno, importo, stato, promo) VALUES (?, ?, ?, ?, ?, ?)";
        String updateSql = "UPDATE pagamenti SET importo = ?, promo = ?, stato = 'Da pagare' WHERE id_abbonato = ? AND mese = ? AND anno = ?";

        try (Connection connection = databaseManager.getConnection();
             PreparedStatement selectStatement = connection.prepareStatement(selectSql);
             PreparedStatement insertStatement = connection.prepareStatement(insertSql);
             PreparedStatement updateStatement = connection.prepareStatement(updateSql)) {
            selectStatement.setString(1, email);

            String meseTarget = null;
            int annoTarget = 0;
            boolean rigaDaAggiornare = false;

            try (ResultSet rs = selectStatement.executeQuery()) {
                while (rs.next()) {
                    String stato = rs.getString("stato");
                    String meseCorrente = rs.getString("mese");
                    int annoCorrente = rs.getInt("anno");

                    if (rigaDaAggiornare) {
                        continue;
                    }

                    // Aggiorno la prima riga non ancora confermata, così recupero lo storico già aperto.
                    if (!isPagamentoConfermato(stato)) {
                        meseTarget = meseCorrente;
                        annoTarget = annoCorrente;
                        rigaDaAggiornare = true;
                    }
                }
            }

            if (rigaDaAggiornare) {
                updateStatement.setDouble(1, totaleCorrente);
                updateStatement.setString(2, promoCorrente);
                updateStatement.setString(3, email);
                updateStatement.setString(4, meseTarget);
                updateStatement.setInt(5, annoTarget);
                updateStatement.executeUpdate();
                return;
            }

            // Se non ci sono rate aperte, creo il mese successivo partendo dall'ultimo confermato.
            String[] ultimoPagamento = getUltimoPagamentoConfermato(email);
            String meseNuovo;
            int annoNuovo;
            if (ultimoPagamento == null) {
                meseNuovo = getMeseItaliano(java.time.LocalDate.now().getMonthValue());
                annoNuovo = java.time.LocalDate.now().getYear();
            } else {
                meseNuovo = getMeseSuccessivo(ultimoPagamento[0]);
                annoNuovo = Integer.parseInt(ultimoPagamento[1]);
                if ("Gennaio".equals(meseNuovo)) {
                    annoNuovo++;
                }
            }

            insertStatement.setString(1, email);
            insertStatement.setString(2, meseNuovo);
            insertStatement.setInt(3, annoNuovo);
            insertStatement.setDouble(4, totaleCorrente);
            insertStatement.setString(5, "Da pagare");
            insertStatement.setString(6, promoCorrente);
            insertStatement.executeUpdate();
        } catch (SQLException exception) {
            throw new RuntimeException("Errore aggiornamento pagamento mese corrente", exception);
        }
    }

    /**
     * Restituisce lo storico pagamenti dell'abbonato ordinato dal più recente.
     *
     * @param emailAbbonato email dell'abbonato di riferimento.
     * @return lista osservabile dei pagamenti associati all'utente.
     * @throws RuntimeException se la lettura dello storico fallisce.
     */
    public ObservableList<Pagamento> getStoricoPagamenti(String emailAbbonato) {
        ObservableList<Pagamento> storico = FXCollections.observableArrayList();
        String sql = """
            SELECT p.id, p.id_abbonato, p.mese, p.anno, p.importo, p.stato, p.promo
            FROM pagamenti p
            WHERE p.id_abbonato = ?
            ORDER BY p.anno DESC,
                CASE p.mese
                    WHEN 'Gennaio' THEN 1
                    WHEN 'Febbraio' THEN 2
                    WHEN 'Marzo' THEN 3
                    WHEN 'Aprile' THEN 4
                    WHEN 'Maggio' THEN 5
                    WHEN 'Giugno' THEN 6
                    WHEN 'Luglio' THEN 7
                    WHEN 'Agosto' THEN 8
                    WHEN 'Settembre' THEN 9
                    WHEN 'Ottobre' THEN 10
                    WHEN 'Novembre' THEN 11
                    WHEN 'Dicembre' THEN 12
                    ELSE 99
                END DESC
            """;

        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, emailAbbonato);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    storico.add(new Pagamento(
                        rs.getInt("id"),
                        rs.getString("id_abbonato"),
                        rs.getString("mese"),
                        rs.getInt("anno"),
                        rs.getDouble("importo"),
                        rs.getString("stato"),
                        rs.getString("promo")
                    ));
                }
            }
            return storico;
        } catch (SQLException exception) {
            throw new RuntimeException("Errore lettura storico pagamenti", exception);
        }
    }

    /**
     * Marca un pagamento come confermato per mese e anno specifici.
     *
     * @param email email dell'abbonato.
     * @param mese mese da saldare.
     * @param anno anno del pagamento.
     * @return true se la riga è stata aggiornata, false se non esiste.
     * @throws RuntimeException se l'update JDBC fallisce.
     */
    public boolean saldaPagamento(String email, String mese, int anno) {
        String sql = """
            UPDATE pagamenti
            SET stato = 'Pagamento confermato'
            WHERE id_abbonato = ? AND mese = ? AND anno = ?
            """;

        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email);
            statement.setString(2, mese);
            statement.setInt(3, anno);
            return statement.executeUpdate() > 0;
        } catch (SQLException exception) {
            throw new RuntimeException("Errore aggiornamento stato pagamento", exception);
        }
    }

    private String getPromozioniAttiveString(String email) {
        List<String> promozioni = findPromozioniAttiveByEmail(email);
        return String.join(", ", promozioni);
    }

    /**
     * Inizializza lo storico pagamenti del nuovo utente con il mese corrente.
     *
     * @param email email del nuovo abbonato.
     * @throws RuntimeException se il database non consente l'inserimento iniziale.
     */
    public void inizializzaStoricoNuovoUtente(String email) {
        String countSql = "SELECT COUNT(*) FROM pagamenti WHERE id_abbonato = ?";
        String insertSql = "INSERT INTO pagamenti(id_abbonato, mese, anno, importo, stato, promo) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = databaseManager.getConnection();
             PreparedStatement countStatement = connection.prepareStatement(countSql);
             PreparedStatement insertStatement = connection.prepareStatement(insertSql)) {
            countStatement.setString(1, email);
            try (ResultSet rs = countStatement.executeQuery()) {
                int count = rs.next() ? rs.getInt(1) : 0;
                if (count > 0) {
                    return;
                }
            }

            // Fotografo le promozioni attive al momento dell'attivazione del servizio.
            String promoSnapshot = getPromozioniAttiveString(email);
            int meseCorrente = java.time.LocalDate.now().getMonthValue();
            int annoCorrente = java.time.LocalDate.now().getYear();
            String meseItaliano = getMeseItaliano(meseCorrente);

            insertPagamento(insertStatement, email, meseItaliano, annoCorrente, 24.99, "Da pagare", promoSnapshot);
        } catch (SQLException exception) {
            throw new RuntimeException("Errore inizializzazione storico pagamenti", exception);
        }
    }

    private void insertPagamento(
        PreparedStatement statement,
        String idAbbonato,
        String mese,
        int anno,
        double importo,
        String stato,
        String promo
    ) throws SQLException {
        statement.setString(1, idAbbonato);
        statement.setString(2, mese);
        statement.setInt(3, anno);
        statement.setDouble(4, importo);
        statement.setString(5, stato);
        statement.setString(6, promo);
        statement.executeUpdate();
    }

    private String[] getUltimoPagamentoConfermato(String email) throws SQLException {
        String sql = """
            SELECT mese, anno
            FROM pagamenti
            WHERE id_abbonato = ?
            ORDER BY anno DESC,
                CASE mese
                    WHEN 'Gennaio' THEN 1
                    WHEN 'Febbraio' THEN 2
                    WHEN 'Marzo' THEN 3
                    WHEN 'Aprile' THEN 4
                    WHEN 'Maggio' THEN 5
                    WHEN 'Giugno' THEN 6
                    WHEN 'Luglio' THEN 7
                    WHEN 'Agosto' THEN 8
                    WHEN 'Settembre' THEN 9
                    WHEN 'Ottobre' THEN 10
                    WHEN 'Novembre' THEN 11
                    WHEN 'Dicembre' THEN 12
                    ELSE 99
                END DESC
            LIMIT 1
            """;
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return new String[] { rs.getString("mese"), String.valueOf(rs.getInt("anno")) };
                }
                return null;
            }
        }
    }

    private boolean isPagamentoConfermato(String stato) {
        return stato != null && "Pagamento confermato".equalsIgnoreCase(stato.trim());
    }

    private String getMeseSuccessivo(String mese) {
        return switch (mese) {
            case "Gennaio" -> "Febbraio";
            case "Febbraio" -> "Marzo";
            case "Marzo" -> "Aprile";
            case "Aprile" -> "Maggio";
            case "Maggio" -> "Giugno";
            case "Giugno" -> "Luglio";
            case "Luglio" -> "Agosto";
            case "Agosto" -> "Settembre";
            case "Settembre" -> "Ottobre";
            case "Ottobre" -> "Novembre";
            case "Novembre" -> "Dicembre";
            case "Dicembre" -> "Gennaio";
            default -> throw new IllegalArgumentException("Mese non valido: " + mese);
        };
    }

    private String getMeseItaliano(int month) {
        return switch (month) {
            case 1 -> "Gennaio";
            case 2 -> "Febbraio";
            case 3 -> "Marzo";
            case 4 -> "Aprile";
            case 5 -> "Maggio";
            case 6 -> "Giugno";
            case 7 -> "Luglio";
            case 8 -> "Agosto";
            case 9 -> "Settembre";
            case 10 -> "Ottobre";
            case 11 -> "Novembre";
            case 12 -> "Dicembre";
            default -> throw new IllegalArgumentException("Mese non valido: " + month);
        };
    }

    /**
     * Restituisce l'elenco dei nomi promozione già associati all'abbonato.
     *
     * @param email email dell'abbonato.
     * @return lista ordinata di promozioni attive.
     * @throws RuntimeException se la lettura delle associazioni fallisce.
     */
    public List<String> findPromozioniAttiveByEmail(String email) {
        String sql = "SELECT promozione_nome FROM abbonato_promozione WHERE email = ? ORDER BY promozione_nome";
        List<String> result = new ArrayList<>();

        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    result.add(rs.getString("promozione_nome"));
                }
            }
            return result;
        } catch (SQLException exception) {
            throw new RuntimeException("Errore lettura promozioni attive", exception);
        }
    }

    /**
     * Recupera il nome dell'abbonato associato a una email.
     * Restituisce null quando l'utente non esiste o la email è vuota.
     *
     * @param email email da cercare.
     * @return nome dell'abbonato oppure null se assente.
     * @throws RuntimeException se la query fallisce.
     */
    public String findNomeByEmail(String email) {
        if (email == null || email.isBlank()) {
            return null;
        }

        String sql = "SELECT nome FROM abbonato WHERE email = ?";
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("nome");
                }
                return null;
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Errore lettura nome cliente", exception);
        }
    }

    /**
     * Recupera il piano tariffario collegato all'abbonato.
     * Restituisce null se la email è vuota o non esiste alcun piano.
     *
     * @param email email dell'abbonato.
     * @return piano tariffario associato oppure null.
     * @throws RuntimeException se il database non risponde correttamente.
     */
    public PianoTariffario findPianoTariffarioByEmail(String email) {
        if (email == null || email.isBlank()) {
            return null;
        }

        String sql = """
            SELECT p.nome, p.minuti_mensili, p.giga_mensili, p.illimitato_minuti, p.illimitato_giga, p.costo_mensile
            FROM abbonato a
            JOIN piano_tariffario p ON p.nome = a.piano_tariffario
            WHERE a.email = ?
            """;

        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new PianoTariffario(
                        resultSet.getString("nome"),
                        getNullableInteger(resultSet, "minuti_mensili"),
                        getNullableInteger(resultSet, "giga_mensili"),
                        resultSet.getInt("illimitato_minuti") == 1,
                        resultSet.getInt("illimitato_giga") == 1,
                        resultSet.getDouble("costo_mensile")
                    );
                }
                return null;
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Errore lettura piano tariffario", exception);
        }
    }

    /**
     * Incrementa le chiamate associate all'utenza dell'abbonato.
     *
     * @param email email dell'abbonato.
     * @param minuti minuti da sommare; valori minori o uguali a zero vengono ignorati.
     * @throws RuntimeException se l'aggiornamento dell'utilizzo fallisce.
     */
    public void registraChiamata(String email, int minuti) {
        if (minuti <= 0) {
            return;
        }
        String sql = """
            UPDATE utilizzo
            SET chiamate = chiamate + ?
            WHERE numero = (SELECT numero_telefono FROM abbonato WHERE email = ?)
            """;
        incrementUtilizzo(email, sql, minuti);
    }

    /**
     * Incrementa di una unità il contatore SMS dell'abbonato.
     *
     * @param email email dell'abbonato.
     * @throws RuntimeException se l'aggiornamento dell'utilizzo fallisce.
     */
    public void registraSms(String email) {
        String sql = """
            UPDATE utilizzo
            SET sms = sms + ?
            WHERE numero = (SELECT numero_telefono FROM abbonato WHERE email = ?)
            """;
        incrementUtilizzo(email, sql, 1);
    }

    /**
     * Incrementa i megabyte associati all'utenza dell'abbonato.
     *
     * @param email email dell'abbonato.
     * @param mb megabyte da sommare; valori minori o uguali a zero vengono ignorati.
     * @throws RuntimeException se l'aggiornamento dell'utilizzo fallisce.
     */
    public void registraDati(String email, int mb) {
        if (mb <= 0) {
            return;
        }
        String sql = """
            UPDATE utilizzo
            SET dati = dati + ?
            WHERE numero = (SELECT numero_telefono FROM abbonato WHERE email = ?)
            """;
        incrementUtilizzo(email, sql, mb);
    }

    private boolean exists(String sql, String email, String password) {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email);
            statement.setString(2, password);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Errore autenticazione", exception);
        }
    }

    private String normalizeContoValue(String conto) {
        if (conto == null || conto.isBlank()) {
            return "Fisso";
        }

        String normalized = conto.trim();
        if ("ricaricabile".equalsIgnoreCase(normalized)) {
            return "Ricaricabile";
        }
        if ("fisso".equalsIgnoreCase(normalized)) {
            return "Fisso";
        }
        throw new RuntimeException("Tipo conto non valido");
    }

    private void incrementUtilizzo(String email, String updateSql, int value) {
        try (Connection connection = databaseManager.getConnection()) {
            // Allineo prima la riga di utilizzo, poi applico l'incremento richiesto.
            ensureUtilizzoByEmail(connection, email);
            try (PreparedStatement statement = connection.prepareStatement(updateSql)) {
                statement.setInt(1, value);
                statement.setString(2, email);
                statement.executeUpdate();
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Errore aggiornamento utilizzo", exception);
        }
    }

    private void ensureUtilizzoByEmail(Connection connection, String email) throws SQLException {
        String sql = """
            INSERT OR IGNORE INTO utilizzo(numero, nome, cognome, email, chiamate, sms, dati)
            SELECT numero_telefono, nome, cognome, email, 0, 0, 0
            FROM abbonato
            WHERE email = ?
            """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email);
            statement.executeUpdate();
        }

        // Sincronizzo i dati anagrafici del consumo con l'ultima versione dell'abbonato.
        String syncSql = """
            UPDATE utilizzo
            SET nome = (SELECT a.nome FROM abbonato a WHERE a.email = ?),
                cognome = (SELECT a.cognome FROM abbonato a WHERE a.email = ?),
                email = ?
            WHERE numero = (SELECT a.numero_telefono FROM abbonato a WHERE a.email = ?)
            """;
        try (PreparedStatement statement = connection.prepareStatement(syncSql)) {
            statement.setString(1, email);
            statement.setString(2, email);
            statement.setString(3, email);
            statement.setString(4, email);
            statement.executeUpdate();
        }
    }

    private void createUtilizzoIfMissing(Connection connection, String numeroTelefono) throws SQLException {
        String sql = """
            INSERT OR IGNORE INTO utilizzo(numero, nome, cognome, email, chiamate, sms, dati)
            SELECT numero_telefono, nome, cognome, email, 0, 0, 0
            FROM abbonato
            WHERE numero_telefono = ?
            """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, numeroTelefono);
            statement.executeUpdate();
        }
    }

    private String generateNumeroTelefono(Connection connection) throws SQLException {
        String sql = "SELECT COUNT(*) FROM abbonato";
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            int count = resultSet.next() ? resultSet.getInt(1) : 0;
            // Genero un numero progressivo semplice per il nuovo cliente.
            return "39" + String.format("%08d", count + 1);
        }
    }

    private boolean existsPianoTariffario(Connection connection, String nomePiano) throws SQLException {
        String sql = "SELECT 1 FROM piano_tariffario WHERE nome = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, nomePiano);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    private TipoPiano toTipoPiano(String value) {
        if (value == null) {
            return null;
        } else {
            return TipoPiano.from(value);
        }
    }

    private Integer getNullableInteger(ResultSet resultSet, String columnName) throws SQLException {
        int value = resultSet.getInt(columnName);
        return resultSet.wasNull() ? null : value;
    }
}

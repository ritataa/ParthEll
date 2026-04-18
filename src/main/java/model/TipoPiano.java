package model;

/**
 * Tipi di piano tariffario supportati dall'applicazione.
 */
public enum TipoPiano {
    BASE("base"),
    PLUS("plus");

    private final String dbValue;

    TipoPiano(String dbValue) {
        this.dbValue = dbValue;
    }

    public String getDbValue() {
        return dbValue;
    }

    @Override
    public String toString() {
        return dbValue;
    }

    public static TipoPiano from(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Tipo di abbonato non riconosciuto: null");
        }

        return switch (value.trim().toLowerCase()) {
            case "base", "basic" -> BASE;
            case "plus", "premium" -> PLUS;
            default -> throw new IllegalArgumentException("Tipo di abbonato non riconosciuto: " + value);
        };
    }
}

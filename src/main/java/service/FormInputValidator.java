package service;

public class FormInputValidator {

    public boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    public boolean areFilled(String... values) {
        if (values == null || values.length == 0) {
            return false;
        }
        for (String value : values) {
            if (isBlank(value)) {
                return false;
            }
        }
        return true;
    }

    public boolean isPositiveInteger(String value) {
        if (isBlank(value)) {
            return false;
        }
        try {
            return Integer.parseInt(value.trim()) > 0;
        } catch (NumberFormatException exception) {
            return false;
        }
    }

    public int parseInteger(String value) {
        return Integer.parseInt(value.trim());
    }

    public boolean isValidCardData(String intestatario, String numeroCarta, String scadenza, String cvv) {
        if (isBlank(intestatario)) {
            return false;
        }
        return numeroCarta != null
            && numeroCarta.matches("\\d{16}")
            && scadenza != null
            && scadenza.matches("(0[1-9]|1[0-2])/\\d{2}")
            && cvv != null
            && cvv.matches("\\d{3}");
    }
}

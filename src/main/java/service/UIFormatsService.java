package service;

public class UIFormatsService {

    public String formatEuro(double amount) {
        return String.format("%.2f EUR", amount);
    }

    public String formatPromoForCell(String promo) {
        if (promo == null || promo.isBlank()) {
            return "Nessuna promo";
        }
        return promo.replace(", ", "\n");
    }

    public boolean isDaPagare(String stato) {
        return stato != null && "Da pagare".equalsIgnoreCase(stato.trim());
    }

    public String formatPromoDetails(String promo) {
        if (promo == null || promo.isBlank()) {
            return "Nessuna promo";
        }
        return promo;
    }
}

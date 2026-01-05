package com.mannapay.common.model.enums;

import lombok.Getter;

/**
 * Supported currencies in MannaPay platform.
 * Based on ISO 4217 currency codes.
 */
@Getter
public enum Currency {
    // Major Currencies
    USD("US Dollar", "$", "United States"),
    EUR("Euro", "€", "Eurozone"),
    GBP("British Pound", "£", "United Kingdom"),

    // Asian Currencies
    CNY("Chinese Yuan", "¥", "China"),
    JPY("Japanese Yen", "¥", "Japan"),
    KRW("South Korean Won", "₩", "South Korea"),
    INR("Indian Rupee", "₹", "India"),
    PHP("Philippine Peso", "₱", "Philippines"),
    THB("Thai Baht", "฿", "Thailand"),
    VND("Vietnamese Dong", "₫", "Vietnam"),
    IDR("Indonesian Rupiah", "Rp", "Indonesia"),
    MYR("Malaysian Ringgit", "RM", "Malaysia"),
    SGD("Singapore Dollar", "S$", "Singapore"),

    // Middle Eastern Currencies
    SAR("Saudi Riyal", "﷼", "Saudi Arabia"),
    AED("UAE Dirham", "د.إ", "United Arab Emirates"),
    QAR("Qatari Riyal", "﷼", "Qatar"),
    KWD("Kuwaiti Dinar", "د.ك", "Kuwait"),

    // African Currencies
    ZAR("South African Rand", "R", "South Africa"),
    NGN("Nigerian Naira", "₦", "Nigeria"),
    KES("Kenyan Shilling", "KSh", "Kenya"),
    EGP("Egyptian Pound", "£", "Egypt"),
    GHS("Ghanaian Cedi", "₵", "Ghana"),
    ETB("Ethiopian Birr", "Br", "Ethiopia"),

    // Latin American Currencies
    MXN("Mexican Peso", "$", "Mexico"),
    BRL("Brazilian Real", "R$", "Brazil"),
    ARS("Argentine Peso", "$", "Argentina"),
    COP("Colombian Peso", "$", "Colombia"),

    // Other Currencies
    CAD("Canadian Dollar", "C$", "Canada"),
    AUD("Australian Dollar", "A$", "Australia"),
    NZD("New Zealand Dollar", "NZ$", "New Zealand"),
    CHF("Swiss Franc", "Fr", "Switzerland"),
    RUB("Russian Ruble", "₽", "Russia");

    private final String displayName;
    private final String symbol;
    private final String country;

    Currency(String displayName, String symbol, String country) {
        this.displayName = displayName;
        this.symbol = symbol;
        this.country = country;
    }

    /**
     * Get formatted amount with currency symbol
     */
    public String format(Double amount) {
        return String.format("%s %.2f", symbol, amount);
    }

    /**
     * Check if currency is a major currency (high liquidity)
     */
    public boolean isMajorCurrency() {
        return this == USD || this == EUR || this == GBP || this == JPY || this == CHF;
    }
}

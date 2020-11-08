package atomatus.util;

import atomatus.linq.CollectionHelper;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Helper to analyse and convert wrapper or decimal types to BigDecimal, currency or decimal
 * by discover Locale automatically or locale set it.
 * @author Carlos Matos
 */
public final class DecimalHelper {

    private static Locale lastLocaleByCurrency;
    private static Locale[] allAvaliableLocales;
    private static final String CURRENCY_SYMBOL_REGEX;
    private static final MathContext DEFAULT_MATH_CONTEXT;
    private static final int DEFAULT_SCALE;

    static {
        lastLocaleByCurrency    = LocaleHelper.getDefaultLocale();
        CURRENCY_SYMBOL_REGEX   = "(?<=^\\-)([^\\d\\s\\.\\,]+)|(^[^\\d\\s\\.\\,\\-]+)|([^\\d\\s\\.\\-]+$)";
        DEFAULT_MATH_CONTEXT    = MathContext.DECIMAL128;
        DEFAULT_SCALE           = 2;
    }

    /**
     * Check if current String is null, empty or whitespace.
     * @param value target string
     * @return
     */
    private static boolean isNullOrWhitespace(String value){
        return value == null || value.trim().isEmpty();
    }

    private static Locale getLocaleByCurrency(String value, Iterable<Locale> locales){
        if(isNullOrWhitespace(value)){
            return null;
        }

        Pattern p = Pattern.compile(CURRENCY_SYMBOL_REGEX);
        Matcher m = p.matcher(value);
        int invalidIndex  = -1;
        boolean hasSymbol = m.find();
        String curSymbol  = hasSymbol ? m.group() : null;
        Locale candidate  = null;

        for (Locale locale : locales) {
            DecimalFormat dFormat           = (DecimalFormat) NumberFormat.getInstance(locale);
            DecimalFormatSymbols symbols    = dFormat.getDecimalFormatSymbols();
            String currencySymbol           = symbols.getCurrencySymbol();
            char decimalSymbol              = symbols.getDecimalSeparator();
            char groupSymbol                = symbols.getGroupingSeparator();

            if(!hasSymbol || (currencySymbol != null && currencySymbol.equalsIgnoreCase(curSymbol))){
                int dsi = value.indexOf(decimalSymbol);
                int gsi = value.indexOf(groupSymbol);

                if(dsi > invalidIndex){
                    if(gsi == invalidIndex) {
                        if(!hasSymbol){
                            return locale;
                        } else{
                            candidate = locale;
                        }
                    } else if(gsi < dsi){
                        return locale;
                    }
                }
            }
        }

        return candidate;
    }

    /**
     * Get appropriate Locale to input (decimal or currency) value.
     * @param value decimal or currency value.
     * @return appropriate Locale found.
     */
    public static synchronized Locale getLocaleByCurrency(String value){
        Locale aux;
        return lastLocaleByCurrency =
                (aux = getLocaleByCurrency(value, LocaleHelper.getLocales(lastLocaleByCurrency))) != null ?
                        aux : lastLocaleByCurrency;
    }

    /**
     * Convert generic type to BigDecimal.
     * @param value target value
     * @param <T> type of target
     * @return a new bigDecimal from value.
     */
    public static <T> BigDecimal toBigDecimal(T value){
        if(value == null){
            return BigDecimal.ZERO;
        } else if(value instanceof String){
            return toBigDecimal((String) value);
        } else if(value instanceof BigDecimal){
            return ((BigDecimal) value).add(BigDecimal.ZERO, DEFAULT_MATH_CONTEXT);
        } else if(value instanceof BigInteger){
            return new BigDecimal((BigInteger) value, DEFAULT_MATH_CONTEXT);
        } else if(value instanceof Integer){
            return new BigDecimal((Integer) value, DEFAULT_MATH_CONTEXT);
        } else if(value instanceof Long){
            return new BigDecimal((Long) value, DEFAULT_MATH_CONTEXT);
        } else if(value instanceof Double || value instanceof Float) {
            String v = String.valueOf(value);
            return new BigDecimal(v, DEFAULT_MATH_CONTEXT)
                    .setScale(DEFAULT_SCALE, DEFAULT_MATH_CONTEXT.getRoundingMode());
        } else if(value instanceof Boolean) {
            return new BigDecimal((Boolean) value ? 1L : 0L, DEFAULT_MATH_CONTEXT);
        } else if(value instanceof char[]) {
            char[] cArr = (char[]) value;
            return CollectionHelper.all(cArr, Character::isDigit) ?
                    toBigDecimal(Long.parseLong(new String(cArr))) :
                    toBigDecimal(new String(cArr));
        } else if(value.getClass().isArray()) {
            Object[] arr = CollectionHelper.toArray(value);
            StringBuilder sb = CollectionHelper.reduce(arr, StringBuilder::append, new StringBuilder());
            return toBigDecimal(sb.toString());
        } else {
            return toBigDecimal(value.toString());
        }
    }

    /**
     * Convert input value to bigDecimal using locale.
     * @param value target decimal or currency value
     * @param locale locale target
     * @return new bigDecimal from value and locale.
     */
    public static BigDecimal toBigDecimal(String value, Locale locale) {
        return new BigDecimal(String.valueOf(toDouble(value, locale)), DEFAULT_MATH_CONTEXT)
                .setScale(DEFAULT_SCALE, DEFAULT_MATH_CONTEXT.getRoundingMode());
    }

    /**
     * Convert input value to bigDecimal using autodiscovery locale.
     * @param value target decimal or currency value.
     * @return new bigDecimal from value and locale (autodiscovery).
     */
    public static BigDecimal toBigDecimal(String value) {
        return new BigDecimal(String.valueOf(toDouble(value)), DEFAULT_MATH_CONTEXT)
                .setScale(DEFAULT_SCALE, DEFAULT_MATH_CONTEXT.getRoundingMode());
    }

    /**
     * Convert input value to bigDecimal using autodiscovery locale.
     * @param value target decimal or currency value.
     * @return new bigDecimal from value and locale (autodiscovery).
     */
    public static BigDecimal toBigDecimal(Number value) {
        return new BigDecimal(String.valueOf(value.doubleValue()), DEFAULT_MATH_CONTEXT)
                .setScale(DEFAULT_SCALE, DEFAULT_MATH_CONTEXT.getRoundingMode());
    }

    /**
     * Convert input value to bigDecimal (not scaling) using locale.
     * @param value target decimal or currency value
     * @param locale locale target
     * @return new bigDecimal from value and locale.
     */
    public static BigDecimal toBigDecimalNS(String value, Locale locale) {
        return new BigDecimal(String.valueOf(toDouble(value, locale)), DEFAULT_MATH_CONTEXT);
    }

    /**
     * Convert input value to bigDecimal (not scaling) using autodiscovery locale.
     * @param value target decimal or currency value.
     * @return new bigDecimal from value and locale (autodiscovery).
     */
    public static BigDecimal toBigDecimalNS(String value) {
        return new BigDecimal(String.valueOf(toDouble(value)), DEFAULT_MATH_CONTEXT);
    }

    /**
     * Convert input value to bigDecimal (not scaling) using autodiscovery locale.
     * @param value target decimal or currency value.
     * @return new bigDecimal from value and locale (autodiscovery).
     */
    public static BigDecimal toBigDecimalNS(Number value) {
        return new BigDecimal(String.valueOf(value.doubleValue()), DEFAULT_MATH_CONTEXT);
    }

    /**
     * Convert input value to number using locale.
     * @param value target decimal or currency value
     * @param locale locale target
     * @return new number from value and locale.
     */
    private static Number toNumber(String value, Locale locale){
        if(isNullOrWhitespace(value)){
            return 0;
        }

        try {
            value = value.replaceAll("\\s", "");
            Pattern p = Pattern.compile(CURRENCY_SYMBOL_REGEX);
            Matcher m = p.matcher(value);
            NumberFormat nFormat = NumberFormat.getInstance(locale);
            return nFormat.parse(m.find() ? value.replace(m.group(), "") : value);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Convert input value to double using locale.
     * @param value target decimal or currency value
     * @param locale locale target
     * @return new double from value and locale.
     */
    public static double toDouble(String value, Locale locale) {
        return toNumber(value, locale).doubleValue();
    }

    /**
     * Convert input value to double using autodiscovery locale.
     * @param value target decimal or currency value.
     * @return new double from value and locale (autodiscovery).
     */
    public static double toDouble(String value) {
        return toDouble(value, getLocaleByCurrency(value));
    }

    /**
     * Convert input value to string from bigDecimal using locale.
     * @param value target value
     * @param locale locale target
     * @return currency string from value and locale.
     */
    public static String toCurrency(BigDecimal value, Locale locale) {
        value = value == null ? BigDecimal.ZERO : value;
        NumberFormat cFormat = NumberFormat.getCurrencyInstance(locale);
        return cFormat.format(value.doubleValue());
    }

    /**
     * Convert input value to string from bigDecimal using autodiscovery locale.
     * @param value target value
     * @return currency string from value and locale (autodiscovery).
     */
    public static String toCurrency(BigDecimal value) {
        return toCurrency(value, lastLocaleByCurrency);
    }

    /**
     * Convert input value to string from bigDecimal using locale.
     * @param value target value
     * @param locale locale target
     * @return decimal string from value and locale.
     */
    public static String toDecimal(BigDecimal value, Locale locale) {
        value = value == null ? BigDecimal.ZERO : value;
        DecimalFormat dFormat = (DecimalFormat)NumberFormat.getInstance(locale);
        dFormat.setMinimumFractionDigits(value.scale());
        return dFormat.format(value.doubleValue());
    }

    /**
     * Convert input value to string from bigDecimal using autodiscovery locale.
     * @param value target value
     * @return decimal string from value and locale (autodiscovery).
     */
    public static String toDecimal(BigDecimal value) {
        return toDecimal(value, lastLocaleByCurrency);
    }
}

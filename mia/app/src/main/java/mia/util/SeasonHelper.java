package mia.util;

import java.time.LocalDate;
import java.time.Month;

/***
 * The SeasonHelper provides a simple way to get seasons by name or by meteorological date
 */
public class SeasonHelper {
    /***
     * Get season by meteorological date
     * @param date the date to get the season for
     * @return the season of the passed date
     */
    public static Season getSeasonFromDate(LocalDate date) {
        Month month = date.getMonth();
        return switch (month) {
            case DECEMBER, JANUARY, FEBRUARY -> Season.WINTER;
            case MARCH, APRIL, MAY -> Season.SPRING;
            case JUNE, JULY, AUGUST -> Season.SUMMER;
            case SEPTEMBER, OCTOBER, NOVEMBER -> Season.FALL;
            default -> Season.ERROR;
        };
    }

    /***
     * Get a season by name
     * @param seasonName the season name
     * @return the season corresponding to the passed name
     */
    public static Season getSeasonFromName(String seasonName) {
        seasonName = seasonName.toLowerCase();
        return switch (seasonName) {
            case "winter" -> Season.WINTER;
            case "spring" -> Season.SPRING;
            case "summer" -> Season.SUMMER;
            case "fall" -> Season.FALL;
            default -> Season.ERROR;
        };
    }
}

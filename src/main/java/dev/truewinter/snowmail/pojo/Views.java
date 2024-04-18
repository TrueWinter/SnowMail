package dev.truewinter.snowmail.pojo;

public class Views {
    public static abstract class View {}

    /**
     * Fields marked with Public will appear in:
     * - websites using SnowMail
     * - all views, except for DashboardSummary
     * <br>
     * This is the default.
     */
    public static class Public extends View {}

    /**
     * Fields marked with DashboardSummary will appear in:
     * - the dashboard HTTP responses
     * - DashboardFull view
     * - Hidden view
     * <br>
     * Use this for summary responses.
     */
    public static class DashboardSummary extends View {}

    /**
     * Fields marked with DashboardFull will appear in:
     * - the dashboard HTTP responses
     * - Hidden view
     * <br>
     * Use this for full responses.
     */
    public static class DashboardFull extends DashboardSummary {}

    /**
     * Fields marked with Hidden will not appear in any HTTP responses
     */
    public static class Hidden extends DashboardSummary {}
}

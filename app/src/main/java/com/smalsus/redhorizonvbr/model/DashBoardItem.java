package com.smalsus.redhorizonvbr.model;

public class DashBoardItem {
    private String dashboardTitle;
    private int dashboardItemIcon;
    private String dashboardItemUrl;

    public DashBoardItem(String dashboardTitle, int dashboardItemIcon, String dashboardItemUrl) {
        this.dashboardTitle = dashboardTitle;
        this.dashboardItemIcon = dashboardItemIcon;
        this.dashboardItemUrl = dashboardItemUrl;
    }

    public String getDashboardTitle() {
        return dashboardTitle;
    }

    public int getDashboardItemIcon() {
        return dashboardItemIcon;
    }

    public String getDashboardItemUrl() {return dashboardItemUrl;}
}
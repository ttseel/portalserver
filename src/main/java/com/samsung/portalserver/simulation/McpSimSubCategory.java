package com.samsung.portalserver.simulation;

public enum McpSimSubCategory {

    OCS_3("OCS3"), OCS_OCSSIM("OCS_OCSSIM"), OCS_MCPSIM("OCS_MCPSIM"), MCP("MCP");

    private final String value;

    McpSimSubCategory(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

package com.samsung.portalserver.simulation;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class McpSimSubCategoryTest {

    @Test
    void correctValueTest() throws Exception {
        assertEquals("OCS3", McpSimSubCategory.OCS_3.getValue());
        assertEquals("OCS_OCSSIM", McpSimSubCategory.OCS_OCSSIM.getValue());
        assertEquals("OCS_MCPSIM", McpSimSubCategory.OCS_MCPSIM.getValue());
        assertEquals("MCP", McpSimSubCategory.MCP.getValue());
    }
}
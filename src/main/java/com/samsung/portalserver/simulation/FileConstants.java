package com.samsung.portalserver.simulation;

public class FileConstants {

    public static final String EXTENSION_TXT = ".txt";
    public static final String EXTENSION_FSS = ".fsl";
    public static final String EXTENSION_FSL = ".fss";

    public static final String NAME_DELIMETER = "+";
    public static final String DIR_DELIMETER = "/";

    public static final String FILE_DIR_ROOT_PATH = "/Users/js.oh/Desktop/Developers/simportal";
    public static final String HISTORY_DIR_PATH = "/Users/js.oh/Desktop/Developers/simportal/history";
    public static final String SIMULATOR_DIR_PATH = "/Users/js.oh/Desktop/Developers/simportal/simulator";
    public static final String CONFIG_DIR_NAME = "config";

    public static final String MASTERDATA_DIR_PATH = "/Users/js.oh/Desktop/Developers/simportal/masterdata";
    public static final String MASTERDATA_MCPSIM_DIR_PATH =
        MASTERDATA_DIR_PATH + DIR_DELIMETER + "mcpsim";
    public static final String MCPSIM_DATA_DIR_PATH =
        MASTERDATA_MCPSIM_DIR_PATH + DIR_DELIMETER + "data";
    public static final String MCPSIM_INPUT_DIR_PATH =
        MASTERDATA_MCPSIM_DIR_PATH + DIR_DELIMETER + "input";
    public static final String TR_HISTORY_DIR_PATH =
        MASTERDATA_DIR_PATH + DIR_DELIMETER + "common" + DIR_DELIMETER + "history";
}

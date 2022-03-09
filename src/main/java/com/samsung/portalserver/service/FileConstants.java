package com.samsung.portalserver.service;

public class FileConstants {

    public static final String EXTENSION_ZIP = ".zip";
    public static final String EXTENSION_TXT = ".txt";
    public static final String EXTENSION_FSS = ".fsl";
    public static final String EXTENSION_FSL = ".fss";

    public static final String NAME_DELIMETER = "+";
    public static final String DIR_DELIMETER = "/";

    public static final String FILE_DIR_ROOT_PATH = "/Users/js.oh/Desktop/Developers/simportal";
    public static final String HISTORY_DIR_PATH =
        FILE_DIR_ROOT_PATH + DIR_DELIMETER + "user_history";
    public static final String SIMULATOR_DIR_PATH =
        FILE_DIR_ROOT_PATH + DIR_DELIMETER + "simulator";
    public static final String MASTERDATA_DIR_PATH =
        FILE_DIR_ROOT_PATH + DIR_DELIMETER + "masterdata";
    public static final String TEMP_DIR_PATH = FILE_DIR_ROOT_PATH + DIR_DELIMETER + "temp";

    public static final String CONFIG_DIR_NAME = "config";
    public static final String MASTERDATA_MCPSIM_DIR_PATH =
        MASTERDATA_DIR_PATH + DIR_DELIMETER + "mcpsim";
    public static final String MCPSIM_DATA_DIR_PATH =
        MASTERDATA_MCPSIM_DIR_PATH + DIR_DELIMETER + "data";
    public static final String MCPSIM_INPUT_DIR_PATH =
        MASTERDATA_MCPSIM_DIR_PATH + DIR_DELIMETER + "input";
    public static final String TR_HISTORY_DIR_PATH =
        MASTERDATA_DIR_PATH + DIR_DELIMETER + "common" + DIR_DELIMETER + "tr_history";
}

package com.samsung.portalserver.schedule;

import com.samsung.portalserver.schedule.job.Job;
import com.samsung.portalserver.schedule.job.SimulationJob;
import com.samsung.portalserver.schedule.job.SimulationJobList;
import com.samsung.portalserver.service.FileService;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.Format.TextMode;
import org.jdom2.output.LineSeparator;
import org.jdom2.output.XMLOutputter;

public class McpsimConfigBuilder implements ConfigBuilder {

    private FileService fileService = new FileService();
    SimulationJobList simulationJobList;

    public void build(Job job) throws IOException, JDOMException {
        simulationJobList = (SimulationJobList) job;

        try {
            // Modify Path Element of fsl
            modifyPathElementOfFsl();

            // Modify fss
            for (SimulationJob simulationJob : simulationJobList.getSimulationMap().values()) {
                String fssPath = simulationJob.getFssFilePath();

                // Modify Path Element of fss
                modifyPathElementOfFss(fssPath);

                // Read Ohtc Element after modify path
                Element ohtc = getOhtcFromFss(fssPath);

                // Download Transfer History
//                String[] historyDate = parsingTrHistoryDate(ohtc);
//                downloadTransferHistory(historyDate[0], historyDate[1]);

                // Download Line Config: Data, Input
//                downloadLineConfig(ohtc);
            }
        } catch (Exception e) {
            throw e;
        }
    }

    private Element getOhtcFromFss(String fssPath) throws IOException, JDOMException {
        Document fssXml = new SAXBuilder().build(new File(fssPath));

        Element scenario = fssXml.getRootElement();

        List<Element> ohtc = scenario.getChildren("ohtc");
        if (ohtc.size() != 1) {
            throw new IllegalStateException("fss xml has problem about ohtc element");
        }

        return ohtc.get(0);
    }

    private String[] parsingTrHistoryDate(Element ohtc) {
        String[] historyDate = new String[2];

        String[] splited = ohtc.getChild("history-path").getText().split("/");

        String[] period = splited[splited.length - 1].split("-");
        historyDate[0] = period[0];
        historyDate[1] = period[1];

        return historyDate;
    }

    //TODO: download Transfer History -> simcommon에 요청
    private void downloadTransferHistory(String start, String end) {
    }

    //TODO: download Line Config -> simcommon에 요청
    private void downloadLineConfig(Element ohtc) {
        String ohtcId = ohtc.getChild("ohtc-id").getText();
        String[] splited = ohtc.getChild("cfg-path").getText().split("/");
        String configDate = splited[splited.length - 1];
    }

    private void modifyPathElementOfFsl() throws IOException, JDOMException {
        Document fslXml = new SAXBuilder().build(new File(simulationJobList.getFslFilePath()));

        Element scenarioLists = fslXml.getRootElement();
        scenarioLists.removeChildren("xml-list", scenarioLists.getNamespace());

        simulationJobList.getSimulationMap().values().forEach(SimulationJob -> {
            Element e = new Element("xml-list", scenarioLists.getNamespace());
            e.setText(SimulationJob.getFssFilePath());
            scenarioLists.addContent(e);
        });

        saveXml(fslXml, simulationJobList.getFslFilePath());
    }

    private void modifyPathElementOfFss(String fssPath) throws IOException, JDOMException {
        Document fssXml = new SAXBuilder().build(new File(fssPath));

        Element scenario = fssXml.getRootElement();
        List<Element> ohtc = scenario.getChildren("ohtc");
        String ohtcId = ohtc.get(0).getChild("ohtc-id").getText();

        modifyCfgPathElement(ohtc.get(0), ohtcId);
        modifyInputPathElement(ohtc.get(0), ohtcId);
        modifyHistoryPathElement(ohtc.get(0), ohtcId);
        modifyTrGenConfigPathElement(ohtc.get(0));

        saveXml(fssXml, fssPath);
    }


    private void modifyCfgPathElement(Element ohtc, String ohtcId) {
        String[] splited = ohtc.getChild("cfg-path").getText().split("/");
        String configDate = splited[splited.length - 1];

        ohtc.getChild("cfg-path").setText(
            FileService.MCPSIM_DATA_DIR_PATH + FileService.DIR_DELIMETER + ohtcId
                + FileService.DIR_DELIMETER + configDate);
    }

    private void modifyInputPathElement(Element ohtc, String ohtcId) {
        ohtc.getChild("input-path")
            .setText(FileService.MCPSIM_INPUT_DIR_PATH + FileService.DIR_DELIMETER + ohtcId);
    }

    private void modifyHistoryPathElement(Element ohtc, String ohtcId) {
        String[] splited = ohtc.getChild("history-path").getText().split("/");
        String trHistoryFileName = splited[splited.length - 1];

        ohtc.getChild("history-path").setText(
            FileService.TR_HISTORY_DIR_PATH + FileService.DIR_DELIMETER + ohtcId
                + FileService.DIR_DELIMETER + trHistoryFileName);
    }

    private void modifyTrGenConfigPathElement(Element ohtc) {
        String[] splited = ohtc.getChild("tr-gen-config-path").getText().split("/");
        String trGenConfigFileName = splited[splited.length - 1];

        ohtc.getChild("tr-gen-config-path").setText(
            simulationJobList.getConfigDirPath() + FileService.DIR_DELIMETER + trGenConfigFileName);
    }

    private void setXmlFormat(XMLOutputter outputter) {
        Format format = outputter.getFormat();
        format.setIndent("\t");
        format.setLineSeparator(LineSeparator.DEFAULT);
        format.setTextMode(TextMode.NORMALIZE);
        outputter.setFormat(format);
    }

    private void saveXml(Document xmlDocument, String path) throws IOException {
        XMLOutputter outputter = new XMLOutputter();
        setXmlFormat(outputter);
        outputter.output(xmlDocument, new FileOutputStream(path));
    }
}

package com.samsung.portalserver.reference;

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
import org.junit.jupiter.api.Test;

class SimulationTest {

    @Test
    void modifyFssPathTest() throws IOException, JDOMException {
        String filePath = "src/main/java/com/samsung/portalserver/simulation/files/TestScenario2_22.02.07.fss";

        Document fssXml = new SAXBuilder().build(new File(filePath));

        Element scenario = fssXml.getRootElement();

//        send-fab-path 찾아서 수정
//        log-cfg-path
        List<Element> ohtc = scenario.getChildren("ohtc");

        if (ohtc.size() != 1) {
            throw new IllegalStateException("fss xml has problem about ohtc element");
        }
        ohtc.get(0).getChild("history-path").getText().split("/");

//        modifyPath(ohtc.get(0));

        XMLOutputter outputter = new XMLOutputter();
        Format format = outputter.getFormat();
        format.setIndent("\t");
        format.setLineSeparator(LineSeparator.DEFAULT);
        format.setTextMode(TextMode.NORMALIZE);
        outputter.setFormat(format);
        outputter.output(fssXml, new FileOutputStream(filePath));
    }

    void modifyPath(Element ohtc) {
        try {
            ohtc.getChild("cfg-path").setText("cfg-path");
            ohtc.getChild("input-path").setText("input-path");
            ohtc.getChild("history-path").setText("history-path");
            ohtc.getChild("tr-gen-config-path").setText("tr-gen-config-path");
        } catch (Exception e) {
            throw new IllegalStateException("modify path element of fss file has been failed");
        }
    }

}
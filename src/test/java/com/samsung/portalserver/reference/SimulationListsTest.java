package com.samsung.portalserver.reference;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.Format.TextMode;
import org.jdom2.output.LineSeparator;
import org.jdom2.output.XMLOutputter;
import org.junit.jupiter.api.Test;

class SimulationListsTest {

    @Test
    void marshalTest() {
        try {
            JAXBContext context = JAXBContext.newInstance(SimulationLists.class);
            Marshaller marshaller = context.createMarshaller();

            SimulationLists simulationLists = new SimulationLists();
            List<String> xmlList = new ArrayList<>();
            xmlList.add("Path1");
            xmlList.add("Path2");
            simulationLists.setXmlList(xmlList);

            // 보기 좋게 출력해주는 옵션
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            // 표준 출력으로 결과를 보여준다.
            marshaller.marshal(simulationLists, System.out);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    @Test
    void unmarshalTest() {
        try {
            JAXBContext context = JAXBContext.newInstance(SimulationLists.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            Marshaller marshaller = context.createMarshaller();
            SimulationLists simulationLists = (SimulationLists) unmarshaller.unmarshal(new File(
                "src/main/java/com/samsung/portalserver/simulation/files/Test_22.02.07.fsl"));

            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(simulationLists, System.out);

            JAXBContext context2 = JAXBContext.newInstance(Simulation.class);
            Unmarshaller unmarshaller2 = context2.createUnmarshaller();
            Marshaller marshaller2 = context2.createMarshaller();
            Simulation simulation = (Simulation) unmarshaller2.unmarshal(new File(
                "src/main/java/com/samsung/portalserver/simulation/files/TestScenario2_22.02.07.fss"));

            marshaller2.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller2.marshal(simulation, System.out);

        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    @Test
    void modifyFslPathTest() throws IOException, JDOMException {
        String filePath = "src/main/java/com/samsung/portalserver/simulation/files/Test_22.02.07.fsl";

        Document document = new SAXBuilder().build(new File(filePath));

        Element rootElement = document.getRootElement();
        rootElement.removeChildren("xml-list", rootElement.getNamespace());

        Element e = new Element("xml-list", rootElement.getNamespace());
        e.setText("가나다라마바사");
        rootElement.addContent(e);
//        scenarioElements.add(e);

        XMLOutputter outputter = new XMLOutputter();
        Format format = outputter.getFormat();
        format.setIndent("\t");
        format.setLineSeparator(LineSeparator.DEFAULT);
        format.setTextMode(TextMode.NORMALIZE);
        outputter.setFormat(format);
        outputter.output(document, new FileOutputStream(filePath));
    }
}
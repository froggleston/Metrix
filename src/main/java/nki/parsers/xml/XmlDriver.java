// Metrix - A server / client interface for Illumina Sequencing Metrics.
// Copyright (C) 2013 Bernd van der Veen

// This program comes with ABSOLUTELY NO WARRANTY;
// This is free software, and you are welcome to redistribute it
// under certain conditions; for more information please see LICENSE.txt

package nki.parsers.xml;

import java.io.File;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import nki.objects.Summary;

public class XmlDriver {

  private Summary summary;
  private String directory;
  private DocumentBuilderFactory documentBuilderFactory;
  private DocumentBuilder documentBuilder;
  private Document doc;

  public XmlDriver(String dir, Summary sum) {
    this.directory = dir;
    this.summary = sum;
  }

  public boolean parseRunInfo() throws SAXException, IOException, ParserConfigurationException {
    File xmlFile = new File(directory + "/RunInfo.xml");
    if (!xmlFile.isFile()) {
      return false;
    }
    documentBuilderFactory = DocumentBuilderFactory.newInstance();
    documentBuilder = documentBuilderFactory.newDocumentBuilder();
    doc = documentBuilder.parse(xmlFile);
    doc.getDocumentElement().normalize();

    setSummary(RunInfoHandler.parseAll(doc, summary));
    return summary.getXmlInfo();
  }

  private void setSummary(Summary sum) {
    this.summary = sum;
  }

  public Summary getSummary() {
    return summary;
  }

  public void closeAll() {
    this.documentBuilderFactory = null;
    this.documentBuilder = null;
    this.doc = null;
  }
}

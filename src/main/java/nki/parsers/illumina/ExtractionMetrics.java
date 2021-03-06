// Metrix - A server / client interface for Illumina Sequencing Metrics.
// Copyright (C) 2013 Bernd van der Veen

// This program comes with ABSOLUTELY NO WARRANTY;
// This is free software, and you are welcome to redistribute it
// under certain conditions; for more information please see LICENSE.txt

package nki.parsers.illumina;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

import nki.util.LoggerWrapper;

public class ExtractionMetrics extends GenericIlluminaParser {
  List<Integer> cycles = new ArrayList<>();

  // Instantiate Logger
  private static final LoggerWrapper metrixLogger = LoggerWrapper.getInstance();

  public ExtractionMetrics(String source, int state) {
    super(ExtractionMetrics.class, source, state);
  }

	/*
   * Binary structure:
	 *	byte 0: file version number (2)
	 *	byte 1: length of each record
	 *	bytes (N * 38 + 2) - (N *38 + 39): record:
	 *	2 bytes: lane number (uint16)
	 *	2 bytes: tile number (uint16)
	 *	2 bytes: cycle number (uint16)
	 *	4 x 4 bytes: fwhm scores (float) for channel [A, C, G, T] respectively
	 *	2 x 4 bytes: intensities (uint16) for channel [A, C, G, T] respectively
	 *	8 bytes: date/time of CIF creation
	 *
	 */

  public List<Integer> digestData() {
    if (fileMissing) {
      return cycles;
    }

    try {
      setVersion(leis.readByte());
      setRecordLength(leis.readByte());
    }
    catch (IOException ex) {
      ex.printStackTrace();
      metrixLogger.log.log(Level.SEVERE, "Error in parsing version number and recordlength: {0}", ex.toString());
    }

    try {
      while (true) {
        int laneNr = leis.readUnsignedShort();
        int tileNr = leis.readUnsignedShort();
        int cycleNr = leis.readUnsignedShort();

        float fA = leis.readFloat();
        float fC = leis.readFloat();
        float fG = leis.readFloat();
        float fT = leis.readFloat();

        int iA = leis.readUnsignedShort();
        int iC = leis.readUnsignedShort();
        int iG = leis.readUnsignedShort();
        int iT = leis.readUnsignedShort();

        long dateTime = leis.readLong();
      }
    }
    catch (IOException exMain) {
      exMain.printStackTrace();
      metrixLogger.log.log(Level.SEVERE, "Error in main parsing of metrics data: {0}", exMain.toString());
    }

    return cycles;
  }

  public List<Integer> getUniqueCycles() {
    try {
      leis.skipBytes(6);

      while (true) {
        int cycleNr = leis.readUnsignedShort();
        cycles.add(cycleNr);
        leis.skipBytes(36);
      }
    }
    catch (IOException ex) {
      ex.printStackTrace();
      metrixLogger.log.log(Level.SEVERE, "IOException in Unique Cycles {0}", ex.toString());
    }

    List<Integer> newList = new ArrayList<>(new HashSet<>(cycles));
    Collections.sort(newList);

    return newList;
  }

  public int getLastCycle() {
    try {
      if (leis != null) {
        leis.skipBytes(6);

        while (true) {
          int cycleNr = leis.readUnsignedShort();
          cycles.add(cycleNr);
          leis.skipBytes(36);
        }
      }
      if (leis != null) {
        leis.close();
      }
    }
    catch (IOException ex) {

    }

    int max = 0;

    for (int c : cycles) {
      if (c > max) {
        max = c;
      }
    }
    return max;
  }
}

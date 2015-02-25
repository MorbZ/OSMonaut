package net.morbz.osmonaut.binary;

//This software is released into the Public Domain.  See copying.txt for details.

import java.io.IOException;
import java.io.InputStream;

import org.openstreetmap.osmosis.osmbinary.file.BlockInputStream;

/** 
 * Glue code that implements a task that connects an InputStream a containing binary-format data to a Sink. 
 * @author crosby
 * @author MorbZ
 */
public class OsmonautReader implements Runnable {
    /**
     * Make a reader based on a target input stream. 
     */
    public OsmonautReader(InputStream input, OsmonautSink sink) {
        if(input == null) {
            throw new Error("Null input");
        }
        this.input = input;
        parser = new OsmonautBinaryParser(sink);
    }

    @Override
    public void run() {
        try {
            (new BlockInputStream(input, parser)).process();
        } catch (IOException e) {
            System.out.println("E: Unable to process PBF stream");
        }
    }
    
    /** Store the input stream we're using. */
    InputStream input;
    
    /** The binary parser object. */
    OsmonautBinaryParser parser;
}

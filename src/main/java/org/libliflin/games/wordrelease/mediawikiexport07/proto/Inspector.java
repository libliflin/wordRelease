/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.libliflin.games.wordrelease.mediawikiexport07.proto;

import com.google.protobuf.CodedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import org.libliflin.games.wordrelease.mediawikiexport07.MediaWikiExportV07Protos.MediaWiki;
import org.libliflin.games.wordrelease.mediawikiexport07.MediaWikiExportV07Protos.Page;

/**
 *
 * @author wjlaffin
 */
public class Inspector {
    // first task, why pb has 3302572 while wikt2english has 3136506
    // they are so frakking close. 
    // 3136506 0 the reason is that wikt2english only looks at ns0's
    // 23 secons to read the proto file compared to 27 seconds for the stax

    public static void main(String[] args) throws FileNotFoundException, IOException {
        System.out.println("Inspector");
        long time = System.currentTimeMillis();
        InputStream fin = new FileInputStream(ProtoConstants.PROTO_LOC);
        CodedInputStream is = CodedInputStream.newInstance(fin);
        is.setSizeLimit(0x7FFFFFFF);
        MediaWiki mediaWiki = MediaWiki.parseFrom(is);
        nsReport(mediaWiki);
        long timepassed = System.currentTimeMillis() - time;
        System.out.println("Read wiki in " + timepassed / 1000 + " seconds.");
    }

    public static void nsReport(MediaWiki mediaWiki) {
        Map<Integer, Integer> countArray = new HashMap<Integer, Integer>();
        for(Page page : mediaWiki.getPageList()){
            int ns = page.getNs();
            Integer n = countArray.get(ns);
            if(n == null){
                n = 0;
            }
            countArray.put(ns, n + 1);
        }
        System.out.println("count\tns");
        for(Integer ns : countArray.keySet()){
            System.out.println(countArray.get(ns) + "\t" + ns);
        }
    }
}

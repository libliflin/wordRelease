package org.libliflin.games.wordrelease;

import com.ximpleware.extended.AutoPilotHuge;
import com.ximpleware.extended.NavExceptionHuge;
import com.ximpleware.extended.VTDGenHuge;
import com.ximpleware.extended.VTDNavHuge;
import com.ximpleware.extended.XPathEvalExceptionHuge;
import com.ximpleware.extended.XPathParseExceptionHuge;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.URI;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;
import org.libliflin.games.wordrelease.mediawikiexport07.Page;
import org.libliflin.games.wordrelease.mediawikiexport07.StaxPageParser;
import org.libliflin.games.wordrelease.mediawikiexport07.VtdPageParser;

/**
 * Hello world!
 *
 */
public class Wikt2EnglishPage {

    public static String englishPagesFile = "E:\\Projects\\wiktionary\\englishPages.txt";
    public static final int START_PAGE = 1;
    public static final int END_PAGE = -1;

    //27 seconds to stax through the file turning them into pages.
    public static void main(String[] args) {
        try {
            String url = "file:///C:/enwiktionary-latest-pages-articles.xml.bz2.xml.bz2.xml";
//            String url = "file:///D:/enWikt_Samp.xml
            File file = new File(new URI(url));
            System.out.println(file.toURI());


//            fileLoader(file);
            staxReader(file);
//            vtdReader(file); vtd is where kittens go to die.
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void fileLoader(File file) throws FileNotFoundException, IOException {
        long startTime = System.currentTimeMillis();
        FileInputStream fis = new FileInputStream(file);
        BigFileInMemory bf = new BigFileInMemory(fis.getChannel().size());
        bf.read(fis);
        System.out.println("Read " + (fis.getChannel().size() / 1000000.) + " MB in " + (System.currentTimeMillis() - startTime) / 1000. + " seconds.");
    }

    private static void vtdReader(File file) throws XPathParseExceptionHuge, XPathEvalExceptionHuge, NavExceptionHuge {
        long startTime = System.currentTimeMillis();
        VTDGenHuge vgh = new VTDGenHuge();
        if (vgh.parseFile(file.getAbsolutePath(), true)) {
            VTDNavHuge vn = vgh.getNav();

            VtdPageParser pageParser = new VtdPageParser();

            AutoPilotHuge first_page_pilot = new AutoPilotHuge(vn);
            first_page_pilot.declareXPathNameSpace("mw", "http://www.mediawiki.org/xml/export-0.7/");
            first_page_pilot.selectXPath("/mw:mediawiki/mw:page[mw:ns=0]");

            AutoPilotHuge following_page_pilot = new AutoPilotHuge(vn);
            following_page_pilot.declareXPathNameSpace("mw", "http://www.mediawiki.org/xml/export-0.7/");
            following_page_pilot.selectXPath("following-sibling::mw:page[mw:ns=0]");


            int result = -1;
            int count = 0;
            result = first_page_pilot.evalXPath();
            do {
                count++;
                pageParser.parsePage(vn, result); // stores current 
                if (count > 100000) {
                    break;
                }
            } while ((result = following_page_pilot.evalXPath()) != -1);
            System.out.println("Parsed XML file in " + (System.currentTimeMillis() - startTime) / 1000. + " seconds.");
        } else {
            System.out.println("Could not parse File.");
        }
    }

    private static void staxReader(File file) throws XMLStreamException, FileNotFoundException, FactoryConfigurationError, IOException {
//        FileChannel fc = new FileInputStream(file).getChannel();
//        ByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
//        InputStream is = Channels.newInputStream(fc);//new FileInputStream(file);

        FileInputStream fis = new FileInputStream(file);
        Page[] ns0pages = StaxPageParser.pagesFromFile(fis);

//        reporter(ns0pages);
    }

    private static void reporter(Page[] ns0pages) {
        long startTime = System.currentTimeMillis();
//        pageLengthDistributionReport(ns0pages);
//        maxPageLenReport(ns0pages);
        englishReport2(ns0pages);
        System.out.println("In " + (System.currentTimeMillis() - startTime) / 1000. + " seconds.");
    }

    //There are 462112 articles with an English header.
    //In 0.354 seconds.
    private static void englishReport1(Page[] ns0pages) {
        String englishHeader = "==English==";
        int numEnglish = 0;
        for (int i = 0; i < ns0pages.length; i++) {
            if (ns0pages[i].text.contains(englishHeader)) {
                numEnglish++;
            }
        }
        System.out.println("There are " + numEnglish + " articles with an English header.");

    }

    private static void englishReport2(Page[] ns0pages) {
        String englishHeader = "==English==";
        Page[] englishPages = new Page[462112];
        int numEnglish = 0;
        for (int i = 0; i < ns0pages.length; i++) {
            if (ns0pages[i].text.contains(englishHeader)) {
                englishPages[numEnglish] = ns0pages[i];
                numEnglish++;
            }
        }

        ObjectOutputStream outputStream = null;
        try {
            outputStream = new ObjectOutputStream(new FileOutputStream(englishPagesFile));

            outputStream.writeObject(englishPages);

        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            //Close the ObjectOutputStream
            try {
                if (outputStream != null) {
                    outputStream.flush();
                    outputStream.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

    }

    // it's water.
    private static void maxPageLenReport(Page[] ns0pages) {
        int maxPage = 0;
        int maxPageLen = 0;

        for (int i = 0; i < ns0pages.length; i++) {
            int len = ns0pages[i].text.length();
            if (len > maxPageLen) {
                maxPageLen = len;
                maxPage = i;
            }
        }

        System.out.println("max Page: " + ns0pages[maxPage]);
        System.out.println("");
        System.out.println("");
        System.out.print(ns0pages[maxPage].title);
        System.out.println("");
        System.out.print(ns0pages[maxPage].text);
    }

    // long tail. see D:/projects/wictionary/textlen div 10 and number of articles with that count.txt
    public static void pageLengthDistributionReport(Page[] ns0pages) {
        int[] bucketCount = new int[10000];

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < ns0pages.length; i++) {
            bucketCount[ns0pages[i].text.length() / 10]++;

        }
        long endTime = System.currentTimeMillis();

        System.out.println("There are ");
        for (int i = 0; i < bucketCount.length; i++) {
            if (bucketCount[i] > 0) {
                System.out.println(i + "\t" + bucketCount[i]);
            }
        }
        System.out.println("And zero of all others.");
        System.out.println("In " + (System.currentTimeMillis() - startTime) / 1000. + " seconds.");
    }
}

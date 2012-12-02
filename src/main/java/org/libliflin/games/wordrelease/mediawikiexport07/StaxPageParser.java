/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.libliflin.games.wordrelease.mediawikiexport07;

import java.io.FileInputStream;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 *
 * @author wjlaffin
 */
public class StaxPageParser {

    private boolean badPage;
    private Page currentPage = null;
    private static int hashCode_ref_ns = "ns".hashCode();
    private static int hashCode_ref_page = "page".hashCode();
    private static int hashCode_ref_redirect = "redirect".hashCode();
    private static int hashCode_ref_restrictions = "restrictions".hashCode();
    private static int hashCode_ref_title = "title".hashCode();
    private static int hashCode_ref_text = "text".hashCode();
    private TagName currentTag;
    public int goodPages = 0;
    public Page[] pages;

    public StaxPageParser() {
        pages = new Page[3136506];
    }

    public static enum TagName {

        ns, page, redirect, restrictions,
        title, text;
    }

    public static Page[] pagesFromFile(FileInputStream fis) throws XMLStreamException, FactoryConfigurationError {
        //        BigFileInMemory bf = new BigFileInMemory(fis.getChannel().size());
        //        bf.read(fis);
        //        fis.close();
        long startTime = System.currentTimeMillis();
        XMLStreamReader xmlStreamReader = XMLInputFactory.newInstance().createXMLStreamReader(fis);
        int i = 0;

        StaxPageParser pageParser = new StaxPageParser();
        try {
            while (xmlStreamReader.hasNext()) {
                i++;
               

                int eventCode = xmlStreamReader.next();
                switch (eventCode) {
                    case XMLStreamConstants.START_ELEMENT:
                        pageParser.parseStart(xmlStreamReader);
                        break;
                    case XMLStreamConstants.END_ELEMENT:
                        pageParser.parseEnd(xmlStreamReader);
                        break;

                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
            System.out.println(t.getLocalizedMessage());
        }

        System.out.println("read " + pageParser.goodPages + " pages using " + i + " events in " + (System.currentTimeMillis() - startTime) / 1000. + " seconds.");
        return pageParser.pages;
    }

    private void parseStart(XMLStreamReader xmlParser) throws IllegalArgumentException {
//        if(goodPages > 1000000){
//            throw new RuntimeException("that's enough good Pages.");
//        }
        String name = xmlParser.getLocalName();
        if (isNotAValidTagName(name)) { // sets currentTag
            return;
        }
        switch (currentTag) {
            case page:
                badPage = false;
                currentPage = new Page();
                break;
            case ns:
                badPage = !("0".equals(getText(xmlParser)));
            case redirect:
                currentPage.redirect = true;
                break;
            case restrictions:
                currentPage.restrictions = getText(xmlParser);
                break;
            case title:
                currentPage.title = getText(xmlParser);
                break;
            case text:
                currentPage.text = currentPage.text + getText(xmlParser);
                break;
        }
    }

    private void parseEnd(XMLStreamReader xmlParser) throws IllegalArgumentException {

        String name = xmlParser.getLocalName();
        if (name.hashCode() == "page".hashCode()) {
            if (!badPage) { // we've loaded data for this page
                pages[goodPages] = currentPage;
                goodPages++;
            }
            currentPage = null;
        }
    }

    private String getText(XMLStreamReader xmlParser) {
        String text = "";
        try {
            text = xmlParser.getElementText();
        } catch (Throwable t) {
            System.out.println("PageParser Could not get Text." + xmlParser.getAttributeCount() + " " + t.getLocalizedMessage());
        }
        return text;
    }

    private boolean isNotAValidTagName(String name) {
        return isNotAValidTagNameHashCode(name);
    }

    private boolean isNotAValidTagNameHashCode(String name) {
        if (name.charAt(0) != 'p' && currentPage == null) {
            return true;
        }

        int name_hashCode = name.hashCode();
        boolean valid = false;

        if (name_hashCode == hashCode_ref_ns) {
            currentTag = TagName.ns;
            valid = true;
        } else if (name_hashCode == hashCode_ref_page) {
            currentTag = TagName.page;
            valid = true;
        } else if (name_hashCode == hashCode_ref_redirect) {
            currentTag = TagName.redirect;
            valid = true;
        } else if (name_hashCode == hashCode_ref_restrictions) {
            currentTag = TagName.restrictions;
            valid = true;
        } else if (name_hashCode == hashCode_ref_title) {
            currentTag = TagName.title;
            valid = true;
        } else if (name_hashCode == hashCode_ref_text) {
            currentTag = TagName.text;
            valid = true;
        }
        return !valid;

    }
}

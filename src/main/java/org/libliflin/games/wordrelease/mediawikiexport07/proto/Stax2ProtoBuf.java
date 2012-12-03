/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.libliflin.games.wordrelease.mediawikiexport07.proto;

import com.google.protobuf.AbstractMessage.Builder;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.DatatypeConverter;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.apache.commons.lang.StringUtils;
import org.libliflin.games.wordrelease.mediawikiexport07.MediaWikiExportV07Protos;
import org.libliflin.games.wordrelease.mediawikiexport07.MediaWikiExportV07Protos.Comment;
import org.libliflin.games.wordrelease.mediawikiexport07.MediaWikiExportV07Protos.Contributor;
import org.libliflin.games.wordrelease.mediawikiexport07.MediaWikiExportV07Protos.DiscussionThreadingInfo;
import org.libliflin.games.wordrelease.mediawikiexport07.MediaWikiExportV07Protos.LogItem;
import org.libliflin.games.wordrelease.mediawikiexport07.MediaWikiExportV07Protos.MediaWiki;
import org.libliflin.games.wordrelease.mediawikiexport07.MediaWikiExportV07Protos.Namespace;
import org.libliflin.games.wordrelease.mediawikiexport07.MediaWikiExportV07Protos.Namespaces;
import org.libliflin.games.wordrelease.mediawikiexport07.MediaWikiExportV07Protos.Page;
import org.libliflin.games.wordrelease.mediawikiexport07.MediaWikiExportV07Protos.Redirect;
import org.libliflin.games.wordrelease.mediawikiexport07.MediaWikiExportV07Protos.Revision;
import org.libliflin.games.wordrelease.mediawikiexport07.MediaWikiExportV07Protos.RevisionUpload;
import org.libliflin.games.wordrelease.mediawikiexport07.MediaWikiExportV07Protos.SiteInfo;
import org.libliflin.games.wordrelease.mediawikiexport07.MediaWikiExportV07Protos.Text;
import org.libliflin.games.wordrelease.mediawikiexport07.MediaWikiExportV07Protos.Upload;
import org.libliflin.util.protobuf.ProtoBufHelper;

/**
 *
 * @author wjlaffin
 */
public class Stax2ProtoBuf {

    private boolean badPage;
    private Page currentPage = null;
    public int goodPages = 0;
    private int START = XMLStreamConstants.START_ELEMENT;
    private int END = XMLStreamConstants.END_ELEMENT;

    public Stax2ProtoBuf() {
    }

    public static void main(String[] args) throws FileNotFoundException, XMLStreamException {
        String url = "C:\\enwiktionary-latest-pages-articles.xml.bz2.xml.bz2.xml";
        System.out.println("Stax2ProtoBuf");
        FileInputStream fin = new FileInputStream(url);
        pagesFromFile(fin);
    }

    private static Map<String, Builder> getBuilderMap() {
        Map<String, Builder> builderMap = new HashMap<String, Builder>();
        ProtoBufHelper.exploreInnerClassesAddingToBuilderMap(builderMap, MediaWikiExportV07Protos.class);
        return builderMap;
    }
    
    private int nextElement(XMLStreamReader xmlStreamReader){
        try {
            while(xmlStreamReader.hasNext()){
            
                int eventCode = xmlStreamReader.next();
                
                switch (eventCode) {
                    case XMLStreamConstants.START_ELEMENT:
                    case XMLStreamConstants.END_ELEMENT:
                        return eventCode;
                }
            }
        } catch (XMLStreamException ex) {
            Logger.getLogger(Stax2ProtoBuf.class.getName()).log(Level.SEVERE, null, ex);
        }
        return XMLStreamConstants.END_DOCUMENT;
    }
    private String getAttr(XMLStreamReader reader, String name){
        return reader.getAttributeValue(null, name);
    }
    private int getAttrInt(XMLStreamReader reader, String name) throws XMLStreamException{
        String intString = getAttr(reader, name);
        if(StringUtils.isNotBlank(intString)){
            return Integer.parseInt(intString);
        }else{
            return 0;
        }
    }
    private boolean hasAttr(XMLStreamReader reader, String attributeName){
        return reader.getAttributeValue(null, attributeName) == null;
    }
    
    /*
     * xsd dateTime type
     */
    private long readTimestamp(XMLStreamReader reader) throws XMLStreamException{
        String dateTimeText = readString(reader);
        Calendar parseDateTime = DatatypeConverter.parseDateTime(dateTimeText);
        if(parseDateTime != null){
            return parseDateTime.getTimeInMillis();
        }else{
            return 0L;
        }
    }
    private String readString(XMLStreamReader reader) throws XMLStreamException{
        String toReturn = reader.getElementText();
        return toReturn != null? toReturn : "";
    }
    private int readInt(XMLStreamReader reader) throws XMLStreamException{
        String intString = readString(reader);
        if(StringUtils.isNotBlank(intString)){
            return Integer.parseInt(intString);
        }else{
            return 0;
        }
    }
    /*
     * for the readXXX methods:::::
     * 
     * Assume you are getting a reader with a start element and
     * local name which is yours. reader is at your end node when
     * you return
     * 
     * getElementText() advances the reader to the end node.
     */
    private MediaWiki readMediaWiki(XMLStreamReader reader) throws XMLStreamException{
        MediaWiki.Builder builder = MediaWiki.newBuilder();
        builder.setVersion(getAttr(reader, "version"));
        while(nextElement(reader) == START){
            String name = reader.getLocalName();
            if(name.equals("page")){
                builder.addPage(readPage(reader));
            }else if(name.equals("siteinfo")){
                builder.setSiteInfo(readSiteInfo(reader));
//                System.out.println(builder.getSiteInfo());
//                System.exit(4);
            }else if(name.equals("logitem")){
                builder.addLogItem(readLogItem(reader));
            }else{
                System.out.println("unable to read Media wiki sub element "+ name);
            }
        }
        return builder.build();
    }
    private Page readPage(XMLStreamReader reader) throws XMLStreamException{
        Page.Builder builder = Page.newBuilder();
        while(nextElement(reader) == START){
            String name = reader.getLocalName();
            if(name.equals("title")){
                builder.setTitle(readString(reader));
            }else if(name.equals("ns")){
                builder.setNs(readInt(reader));
            }else if(name.equals("id")){
                builder.setId(readInt(reader));
            }else if(name.equals("redirect")){
                builder.setRedirect(readRedirect(reader));
            }else if(name.equals("restrictions")){
                builder.setRestrictions(readString(reader));
            }else if(name.equals("revision") || name.equals("upload") ){
                builder.addRevisionUpload(readRevisionUpload(reader));
            }else if(name.equals("discussionThreadingInfo")){
                builder.setDiscussionThreadingInfo(readDiscussionThreadingInfo(reader));
            }else{
                System.out.println("unable to read Page sub element "+ name);
            }
        }
        return builder.build();
    }
    private Redirect readRedirect(XMLStreamReader reader) throws XMLStreamException{
        Redirect.Builder builder = Redirect.newBuilder();
        builder.setTitle(getAttr(reader, "title"));
        builder.setRedirect(readString(reader));
        return builder.build();
    }
    private RevisionUpload readRevisionUpload(XMLStreamReader reader) throws XMLStreamException{
        RevisionUpload.Builder builder = RevisionUpload.newBuilder();
        // reader points to start of revision tag or start of upload tag
        // @see where this is called in readPage();
        String name = reader.getLocalName();
        if(name.equals("revision")){
            builder.setRevision(readRevision(reader));
        } else if(name.equals("upload")){
            builder.setUpload(readUpload(reader));
        }
        return builder.build();
    }
    private Revision readRevision(XMLStreamReader reader) throws XMLStreamException{
        Revision.Builder builder = Revision.newBuilder();
        while(nextElement(reader) == START){
            String name = reader.getLocalName();
            if(name.equals("id")){
                builder.setId(readInt(reader));
            }else if(name.equals("parentid")){
                builder.setParentid(readInt(reader));
            }else if(name.equals("timestamp")){
                builder.setTimestamp(readTimestamp(reader));
            }else if(name.equals("contributor")){
                builder.setContributor(readContributor(reader));
            }else if(name.equals("minor")){
                builder.setMinor(true);
            }else if(name.equals("comment")){
                builder.setComment(readComment(reader));
            }else if(name.equals("sha1")){
                builder.setSha1(readString(reader));
            }else if(name.equals("text")){
                builder.setText(readText(reader));
            }else{
                System.out.println("unable to read Revision sub element "+ name);
            }
        }
        return builder.build();
    }
    private Contributor readContributor(XMLStreamReader reader) throws XMLStreamException{
        Contributor.Builder builder = Contributor.newBuilder();
        builder.setDeleted(hasAttr(reader, "deleted"));
        while(nextElement(reader) == START){
            String name = reader.getLocalName();
            if(name.equals("id")){
                builder.setId(readInt(reader));
            }else if(name.equals("username")){
                builder.setUsername(readString(reader));
            }else if(name.equals("ip")){
                builder.setIp(readString(reader));
            }else{
                System.out.println("unable to read Contributor sub element "+ name);
            }
        }
        return builder.build();
    }
    private Comment readComment(XMLStreamReader reader) throws XMLStreamException{
        Comment.Builder builder = Comment.newBuilder();
        builder.setDeleted(hasAttr(reader, "deleted"));
        builder.setComment(readString(reader));
        return builder.build();
    }
    private Text readText(XMLStreamReader reader) throws XMLStreamException{
        Text.Builder builder = Text.newBuilder();
        builder.setDeleted(hasAttr(reader, "deleted"));
        builder.setId(getAttrInt(reader, "id"));
        builder.setNumBytes(getAttrInt(reader, "bytes"));
        builder.setText(readString(reader));
        return builder.build();
    }
    private Upload readUpload(XMLStreamReader reader) throws XMLStreamException{
        Upload.Builder builder = Upload.newBuilder();
        while(nextElement(reader) == START){
            String name = reader.getLocalName();
            if(name.equals("timestamp")){
                builder.setTimestamp(readTimestamp(reader));
            }else if(name.equals("contributor")){
                builder.setContributor(readContributor(reader));
            }else if(name.equals("comment")){
                builder.setComment(readString(reader));
            }else if(name.equals("filename")){
                builder.setFileName(readString(reader));
            }else if(name.equals("src")){
                builder.setSrc(readString(reader));
            }else if(name.equals("size")){
                builder.setSize(readInt(reader));
            }else{
                System.out.println("unable to read Upload sub element "+ name);
            }
        }
        return builder.build();
    }
    private DiscussionThreadingInfo readDiscussionThreadingInfo(XMLStreamReader reader) throws XMLStreamException{
        DiscussionThreadingInfo.Builder builder = DiscussionThreadingInfo.newBuilder();
        while(nextElement(reader) == START){
            String name = reader.getLocalName();
            if(name.equals("ThreadSubject")){
                builder.setThreadSubject(readString(reader));
            }else if(name.equals("ThreadParent")){
                builder.setThreadParent(readInt(reader));
            }else if(name.equals("ThreadAncestor")){
                builder.setThreadAncestor(readInt(reader));
            }else if(name.equals("ThreadPage")){
                builder.setThreadPage(readString(reader));
            }else if(name.equals("ThreadID")){
                builder.setThreadId(readInt(reader));
            }else if(name.equals("ThreadAuthor")){
                builder.setThreadAuthor(readString(reader));
            }else if(name.equals("ThreadEditStatus")){
                builder.setThreadEditStatus(readString(reader));
            }else if(name.equals("ThreadType")){
                builder.setThreadType(readString(reader));
            }else{
                System.out.println("unable to read DiscussionThreadingInfo sub element "+ name);
            }
        }
        return builder.build();
    }
    private SiteInfo readSiteInfo(XMLStreamReader reader) throws XMLStreamException{
        SiteInfo.Builder builder = SiteInfo.newBuilder();
        while(nextElement(reader) == START){
            String name = reader.getLocalName();
            if(name.equals("sitename")){
                builder.setSiteName(readString(reader));
            }else if(name.equals("base")){
                builder.setBase(readString(reader));
            }else if(name.equals("generator")){
                builder.setGenerator(readString(reader));
            }else if(name.equals("case")){
                builder.setCase(readString(reader));
            }else if(name.equals("namespaces")){
                builder.setNamespaces(readNamespaces(reader));
            }else{
                System.out.println("unable to read SiteInfo sub element "+ name);
            }
        }
        return builder.build();
    }
    private Namespaces readNamespaces(XMLStreamReader reader) throws XMLStreamException{
        Namespaces.Builder builder = Namespaces.newBuilder();
        while(nextElement(reader) == START){
            String name = reader.getLocalName();
            if(name.equals("namespace")){
                builder.addNamespace(readNamespace(reader));
            }else{
                System.out.println("unable to read Namespaces sub element "+ name);
            }
        }
        return builder.build();
    }
    private Namespace readNamespace(XMLStreamReader reader) throws XMLStreamException{
        Namespace.Builder builder = Namespace.newBuilder();
        builder.setKey(getAttrInt(reader, "key"));
        builder.setCase(getAttr(reader, "case"));
        builder.setNamespace(readString(reader));
        return builder.build();
    }
    private LogItem readLogItem(XMLStreamReader reader){
        LogItem.Builder builder = LogItem.newBuilder();
        return builder.build();
        
    }
    

    public static MediaWiki pagesFromFile(FileInputStream fis) throws XMLStreamException, FactoryConfigurationError {
        //        BigFileInMemory bf = new BigFileInMemory(fis.getChannel().size());
        //        bf.read(fis);
        //        fis.close();
        long startTime = System.currentTimeMillis();
        XMLStreamReader xmlStreamReader = XMLInputFactory.newInstance().createXMLStreamReader(fis);
        
        xmlStreamReader.next();
        
        if(xmlStreamReader.getLocalName().equals("mediawiki")){
            Stax2ProtoBuf parser = new Stax2ProtoBuf();
            MediaWiki readMediaWiki = parser.readMediaWiki(xmlStreamReader);
            
        }else{
            System.out.println("Unable to read xml file");
        }
        System.exit(4);
        int i = 0;

        Stax2ProtoBuf pageParser = new Stax2ProtoBuf();
        Map<String, ? extends Builder> protoBuilders = getBuilderMap();
        Stack<Builder> builderStack = new Stack<Builder>();
        Builder currentBuilder = null; // points to top of the stack
        try {
            int tab = 0;
            while (xmlStreamReader.hasNext()) {
                i++;
                if (i > 300) {
                    System.exit(1);
                }

                int eventCode = xmlStreamReader.next();
                String name;
                switch (eventCode) {
                    case XMLStreamConstants.START_ELEMENT:
                        name = xmlStreamReader.getLocalName();
                        tab++;
                        StringBuilder sb = new StringBuilder();
                        sb.append(name);
                        if (protoBuilders.containsKey(name)) {
                            //p(tab, name + "+");.append("+");
                            Builder builder = protoBuilders.get(name).clone();

                            builderStack.push(builder);
                            currentBuilder = builder;
                        } else {
                            sb.append("-");
                        }
                        int attributeCount = xmlStreamReader.getAttributeCount();
                        sb.append("{");
                        for (int j = 0; j < attributeCount; j++) {
                            sb.append(xmlStreamReader.getAttributeName(j));
                            sb.append("=");
                            sb.append(xmlStreamReader.getAttributeValue(j));
                            sb.append(";");
                        }
                        sb.append("}");
                        p(tab, sb.toString());
                        //pageParser.parseStart(xmlStreamReader);
                        break;
                    case XMLStreamConstants.END_ELEMENT:
                        name = xmlStreamReader.getLocalName();
                        tab--;
                        if (protoBuilders.containsKey(name)) {
                            p(tab, "/" + name + "+");
//                            Builder builder = protoBuilders.get(name);
//                            builderStack.push(builder);
//                            currentBuilder = builder;
                        } else {
                            p(tab, "/" + name + "-");
                        }
                        //pageParser.parseEnd(xmlStreamReader);
                        break;

                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
            System.out.println(t.getLocalizedMessage());
        }

        System.out.println("read " + pageParser.goodPages + " pages using " + i + " events in " + (System.currentTimeMillis() - startTime) / 1000. + " seconds.");
        return (MediaWiki) protoBuilders.get("mediawiki").build();
    }

    private static void p(int tab, String print) {
        System.out.println(
                String.format("%" + tab + "s", "")
                + print);

    }

    private void parseStart(XMLStreamReader xmlParser) throws IllegalArgumentException {
//        if(goodPages > 1000000){
//            throw new RuntimeException("that's enough good Pages.");
//        }
        String name = xmlParser.getLocalName();
        if (isNotAValidTagName(name)) { // sets currentTag
            return;
        }
//        switch (currentTag) {
//            case page:
//                badPage = false;
//                currentPage = new Page();
//                break;
//            case ns:
//                badPage = !("0".equals(getText(xmlParser)));
//            case redirect:
//                currentPage.redirect = true;
//                break;
//            case restrictions:
//                currentPage.restrictions = getText(xmlParser);
//                break;
//            case title:
//                currentPage.title = getText(xmlParser);
//                break;
//            case text:
//                currentPage.text = currentPage.text + getText(xmlParser);
//                break;
//        }
    }

    private void parseEnd(XMLStreamReader xmlParser) throws IllegalArgumentException {

        String name = xmlParser.getLocalName();
        if (name.hashCode() == "page".hashCode()) {
            if (!badPage) { // we've loaded data for this page
//                pages[goodPages] = currentPage;
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
        return false;
    }
//        if (name.charAt(0) != 'p' && currentPage == null) {
//            return true;
//        }
//
//        int name_hashCode = name.hashCode();
//        boolean valid = false;
//
//        if (name_hashCode == hashCode_ref_ns) {
//            currentTag = TagName.ns;
//            valid = true;
//        } else if (name_hashCode == hashCode_ref_page) {
//            currentTag = TagName.page;
//            valid = true;
//        } else if (name_hashCode == hashCode_ref_redirect) {
//            currentTag = TagName.redirect;
//            valid = true;
//        } else if (name_hashCode == hashCode_ref_restrictions) {
//            currentTag = TagName.restrictions;
//            valid = true;
//        } else if (name_hashCode == hashCode_ref_title) {
//            currentTag = TagName.title;
//            valid = true;
//        } else if (name_hashCode == hashCode_ref_text) {
//            currentTag = TagName.text;
//            valid = true;
//        }
//        return !valid;
//
//    }
}

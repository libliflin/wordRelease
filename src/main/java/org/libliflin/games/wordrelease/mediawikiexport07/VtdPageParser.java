/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.libliflin.games.wordrelease.mediawikiexport07;

import com.ximpleware.extended.BookMarkHuge;
import com.ximpleware.extended.NavExceptionHuge;
import com.ximpleware.extended.VTDNavHuge;

/**
 *
 * @author wjlaffin
 */
public class VtdPageParser {

    private static int ROOT = 0;
    private static int PARENT = 1;
    private static int FIRST_CHILD = 2;
    private static int LAST_CHILD = 3;
    private static int NEXT_SIBLING = 4;
    private static int PREV_SIBLING = 5;
    
    
    private static String intern_ref_minor = "minor".intern();
    private static String intern_ref_ns = "ns".intern();
    private static String intern_ref_page = "page".intern();
    private static String intern_ref_redirect = "redirect".intern();
    private static String intern_ref_restrictions = "restrictions".intern();
    private static String intern_ref_revision = "revision".intern();
    private static String intern_ref_title = "title".intern();
    private static String intern_ref_timestamp = "timestamp".intern();
    private static String intern_ref_text = "text".intern();

    public void parsePage(VTDNavHuge vn, int result) throws NavExceptionHuge {
        BookMarkHuge mark = new BookMarkHuge(vn);
        mark.recordCursorPosition();
        Page page = new Page();

        vn.toElement(FIRST_CHILD);
        do{
            int t = vn.getText();
            if(vn.matchElement(intern_ref_title)){
                page.title = vn.toNormalizedString(t);
            } else if(vn.matchElement(intern_ref_redirect)){
                page.redirect = true;
            } else if(vn.matchElement(intern_ref_restrictions)){
                page.restrictions = vn.toNormalizedString(t);
            } else if(vn.matchElement(intern_ref_revision)){
//                page.addRevision(this.parseRevision(vn));
            }
        }while(vn.toElement(NEXT_SIBLING));
        
//        System.out.println(page);


        mark.setCursorPosition();
    }
//
//    private Revision parseRevision(VTDNavHuge vn) throws NavExceptionHuge {
//        BookMarkHuge mark = new BookMarkHuge(vn);
//        mark.recordCursorPosition();
////        Revision revision = new Revision();
//        vn.toElement(FIRST_CHILD);
//        do{
//            int t = vn.getText();
//            if(vn.matchElement(intern_ref_minor)){
////                revision.minor = true;
//            } else if(vn.matchElement(intern_ref_timestamp)){
////                revision.timestamp = vn.toNormalizedString(t);
//            } else if(vn.matchElement(intern_ref_text)){
////                revision.text = vn.toNormalizedString(t);
//            } 
//        }while(vn.toElement(NEXT_SIBLING));
//        
//        mark.setCursorPosition();
//        return revision;
//    }
}

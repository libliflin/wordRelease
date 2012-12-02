/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.libliflin.games.wordrelease.mediawikiexport07;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author wjlaffin<complexType name="PageType"> <sequence> <!-- Title in text
 * form. (Using spaces, not underscores; with namespace ) --> <element
 * name="title" type="string"/> <!-- Namespace in canonical form --> <element
 * name="ns" type="nonNegativeInteger"/> <!-- optional page ID number -->
 * <element name="id" type="positiveInteger"/> <!-- flag if the current revision
 * is a redirect --> <element name="redirect" type="mw:RedirectType"
 * minOccurs="0" maxOccurs="1"/> <!-- comma-separated list of string tokens, if
 * present --> <element name="restrictions" type="string" minOccurs="0"/> <!--
 * Zero or more sets of revision or upload data --> <choice minOccurs="0"
 * maxOccurs="unbounded"> <element name="revision" type="mw:RevisionType"/>
 * <element name="upload" type="mw:UploadType"/> </choice> <!-- Zero or One sets
 * of discussion threading data --> <element name="discussionthreadinginfo"
 * minOccurs="0" maxOccurs="1" type="mw:DiscussionThreadingInfo"/> </sequence>
 * </complexType>
 */
public class Page implements Serializable {

    static final long serialVersionUID = 12493L;
    
    public String title;
    public boolean redirect;
    public String restrictions;
    public String text;

    @Override
    public String toString() {
        return "Page{" + "title=" + title + ", redirect=" + redirect + ", restrictions=" + restrictions + ", text.len=" +text.length() +'}';
    }
}

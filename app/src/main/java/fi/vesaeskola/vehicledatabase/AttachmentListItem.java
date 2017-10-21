/*++

Module Name:

AttachementListItem.java

Abstract:

This class is used to store and populate listview with attachement image information.

Environment:

Android

Copyright <C> 2016 Vesa Eskola.

--*/

package fi.vesaeskola.vehicledatabase;


public class AttachmentListItem {
    public int mId;
    public String mImagePath;
    public String mTitle;

    public AttachmentListItem(int id, String imagePath, String title) {
        this.mId = id;
        this.mImagePath = imagePath;
        this.mTitle = title;
    }

}



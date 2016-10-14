/*++

Module Name:

ActionListItem.java

Abstract:

This class is used to store and populate listview with action information. Action could be either
service, event or re-fueling.

Environment:

Android

Copyright <C> 2016 Vesa Eskola.

--*/

package fi.vesaeskola.vehicledatabase;


public class ActionListItem {
    public int mActionId;
    public String row1_col1;
    public String row1_col2;
    public String row2_col1;
    public String row2_col2;

    public ActionListItem(int actionId, String row1_col1, String row1_col2, String row2_col1, String row2_col2) {
        this.mActionId = actionId;
        this.row1_col1 = row1_col1;
        this.row1_col2 = row1_col2;
        this.row2_col1 = row2_col1;
        this.row2_col2 = row2_col2;
    }

}

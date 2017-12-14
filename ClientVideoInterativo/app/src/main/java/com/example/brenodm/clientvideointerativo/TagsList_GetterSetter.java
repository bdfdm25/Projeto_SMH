package com.example.brenodm.clientvideointerativo;

/**
 * Created by brenodm on 31/05/17.
 */

public class TagsList_GetterSetter {

    private int id;
    private String tag_item;
    private String time_item;





    public TagsList_GetterSetter(int id, String tag_item, String time_item) {
        super();
        this.id = id;
        this.tag_item = tag_item;
        this.time_item = time_item;
    }

    public TagsList_GetterSetter() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTag_item() {
        return tag_item;
    }

    public void setTag_item(String tag_item) {
        this.tag_item = tag_item;
    }

    public String getTime_item() {
        return time_item;
    }

    public void setTime_item(String time_item) {
        this.time_item = time_item;
    }

    public String toString() {
        return "Tag: " + tag_item + '\n' +
                "Time: " + time_item;
    }


}

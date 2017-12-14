package com.example.brenodm.clientvideointerativo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by brenodm on 31/05/17.
 */

public class TagsList_Adapter extends BaseAdapter {
    private List<TagsList_GetterSetter> tags_group;

    private LayoutInflater inflater;

    public TagsList_Adapter(Context context, List<TagsList_GetterSetter> new_group) {
        this.tags_group = new_group;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void addItem(final TagsList_GetterSetter item) {
        this.tags_group.add(item);

    }

    public int getCount() {
        return tags_group.size();
    }

    public Object getItem(int position) {
        return tags_group.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup viewGroup) {

        TagsList_GetterSetter tag_getterSetter = tags_group.get(position);
        TagsList_GetterSetter time_getterSetter = tags_group.get(position);

        convertView = inflater.inflate(R.layout.listview_mainscreen_itens, null);

        TextView tag = (TextView) convertView.findViewById(R.id.user_tag);
        TextView time = (TextView) convertView.findViewById(R.id.clip_time);


        //muscle_group.setText(exercise.getTag_item());

        tag.setText(tag_getterSetter.getTag_item());
        time.setText(time_getterSetter.getTime_item());

        return convertView;
    }
}

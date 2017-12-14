package com.example.brenodm.clientvideointerativo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainScreen extends ActionBarActivity {

    private EditText tag_text;
    private Button  save_button;
    private Button settings_button;
    private Button  update_button;
    private ListView tags_list = null;
    final Context context = this;



    String strTime = "";
    int i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);



        tag_text = (EditText) findViewById(R.id.input_tag);
        save_button = (Button) findViewById(R.id.save_button);
        settings_button = (Button) findViewById(R.id.settings_button);
        update_button = (Button) findViewById(R.id.update_button);

        tags_list = (ListView) findViewById(R.id.tag_list);

        tags_list.setOnItemClickListener(onItemClick_List);

        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewTag();
                addTagOnList();

            }
        });




        update_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTagOnList();
            }
        });




    }

    AdapterView.OnItemClickListener onItemClick_List = new AdapterView.OnItemClickListener(){
        public void onItemClick(AdapterView arg0, View view, int position, long index) {

            showToast(" " + tags_list.getItemAtPosition(position));
            onTimeClick(view, position); //adicionar aqui o evento que seleciona o video a partir do tempo salvo
        }
    };

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


    public void addNewTag(){

        String strChave = tag_text.getText().toString();
        if (!strChave.equals("")) {
            Client cl = new Client();

            try {

                strTime = cl.SendMsg("getTime");
                Log.d("Time: ", " " +strTime);

            } catch (InterruptedException ex) {
                Log.d("ERRO:", " " + ex);

            }

        }

        TagsList_GetterSetter new_tag = new TagsList_GetterSetter();

        new_tag.setId(i);
        new_tag.setTag_item(strChave);
        new_tag.setTime_item(strTime);

        DB_Helper dbHelper = new DB_Helper(this);
        dbHelper.insertTags(new_tag);

        i++;
    }


    public void addTagOnList(){


        DB_Helper dbHelper = new DB_Helper(this);
        List<TagsList_GetterSetter> listTags = dbHelper.selectTags();

        ArrayAdapter<TagsList_GetterSetter> newAdapter = new ArrayAdapter<TagsList_GetterSetter>(this, android.R.layout.simple_list_item_1, listTags);
        tags_list.setAdapter(newAdapter);


    }

    private void onTimeClick(View v, int item){

        String time = tags_list.getItemAtPosition(item).toString();
        time =time.substring(time.length()-11, time.length()-6);
        System.out.println(time.replace(":", "."));

        Client cl = new Client();
        try {
            strTime = cl.SendMsg("setTime " + time.replace(":", "."));
        } catch (InterruptedException ex) {
            Logger.getLogger(MainScreen.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


}

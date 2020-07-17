package com.example.todoapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.view.View;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import android.app.AlertDialog;
import android.os.Bundle;
import android.content.Context;

import com.example.todoapplication.common.TodoConstants;
import com.example.todoapplication.common.TodoPreferences;
import com.example.todoapplication.service.ApiManager;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

import com.example.todoapplication.model.TodoData;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class TodoActivity extends AppCompatActivity {

    private ListView listView;
    private ArrayList<TodoData> dataSet = new ArrayList();
    CustomAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo);
        adapter = new CustomAdapter( dataSet, this);
        listView = findViewById(R.id.listview);
        listView.setAdapter(adapter);
        fetchTodo();

        FloatingActionButton fab = findViewById(R.id.addTodo);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddTodoDialog();
            }
        });

    }

    //region Rest API Calls
    private void fetchTodo() {
        ApiManager.get(TodoConstants.GetTodoRelativeUrl, null,
                TodoPreferences.getAuthToken(),new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                try {
                    dataSet.clear();
                    for( int i = 0; i < response.length(); i++) {
                        TodoData todo = new TodoData(response.getJSONObject(i).getString("title"),
                                                    response.getJSONObject(i).getInt("id"));
                        dataSet.add(todo);

                    }
                    adapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, java.lang.Throwable throwable,
                                  org.json.JSONObject errorResponse){
                try {
                    JSONObject serverResp = new JSONObject(errorResponse.toString());
                    Toast.makeText(TodoActivity.this,
                            serverResp.getString("message"),
                            Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
    }

    private void postTodoItem(String item) {
        RequestParams rp = new RequestParams();
        rp.add("title", item);
        ApiManager.post(TodoConstants.AddTodoRelativeUrl, rp,
                TodoPreferences.getAuthToken(),new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            JSONObject serverResp = new JSONObject(response.toString());
                            TodoData todo = new TodoData(serverResp.getString("title"),
                                    serverResp.getInt("id"));
                            dataSet.add(todo);
                            adapter.notifyDataSetChanged();
                            Toast.makeText(TodoActivity.this,
                                    "Todo item added successfully",
                                    Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, java.lang.Throwable throwable,
                                          org.json.JSONObject errorResponse){
                        try {
                            JSONObject serverResp = new JSONObject(errorResponse.toString());
                            Toast.makeText(TodoActivity.this,
                                    serverResp.getString("message"),
                                    Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void deleteTodoItem(final TodoData todoData) {
        ApiManager.delete(TodoConstants.DeleteTodoRelativeUrl+"/"+todoData.getId(),
                null,
                TodoPreferences.getAuthToken(),new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        dataSet.remove(todoData);
                        adapter.notifyDataSetChanged();
                        Toast.makeText(TodoActivity.this,
                                "Todo item deleted successfully",
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, java.lang.Throwable throwable,
                                          org.json.JSONObject errorResponse){
                        try {
                            JSONObject serverResp = new JSONObject(errorResponse.toString());
                            Toast.makeText(TodoActivity.this,
                                    serverResp.getString("message"),
                                    Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                });
    }
    //endregion

    //region dialog
    private void showAddTodoDialog() {
        final EditText txtUrl = new EditText(this);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Add TODO")
                .setView(txtUrl)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String addedTodoItem = txtUrl.getText().toString();
                        postTodoItem(addedTodoItem);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private void showDeleteTodoDialog(final TodoData deleteTodo) {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Are you sure you want to delete " + deleteTodo.getItem())
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        deleteTodoItem(deleteTodo);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                })
                .show();
    }
    //endregion

    //region listview adapter
    public class CustomAdapter extends ArrayAdapter<TodoData> implements View.OnClickListener{

        private ArrayList<TodoData> dataSet;
        Context mContext;

        private class ViewHolder {
            TextView item;
            ImageView delete;
        }

        public CustomAdapter(ArrayList<TodoData> data, Context context) {
            super(context, R.layout.activity_listview, data);
            this.dataSet = data;
            this.mContext=context;
        }

        @Override
        public void onClick(View v) {

            int position=(Integer) v.getTag();
            Object object= getItem(position);
            TodoData deleteTodo =(TodoData)object;

            switch (v.getId())
            {
                case R.id.item_delete:
                   showDeleteTodoDialog(deleteTodo);
                    break;
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            TodoData todoItem = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            ViewHolder viewHolder; // view lookup cache stored in tag
            if (convertView == null) {

                viewHolder = new ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.activity_listview, parent, false);
                viewHolder.item = (TextView) convertView.findViewById(R.id.label);
                viewHolder.delete = (ImageView) convertView.findViewById(R.id.item_delete);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.item.setText(todoItem.getItem());
            viewHolder.delete.setOnClickListener(this);
            viewHolder.delete.setTag(position);
            // Return the completed view to render on screen
            return convertView;
        }
    }
    //endregion
}

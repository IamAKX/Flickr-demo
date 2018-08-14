package com.akashapplications.flicker;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.akashapplications.flicker.adapters.ImageAdapter;
import com.akashapplications.flicker.models.ImageModel;
import com.akashapplications.flicker.utilities.Constants;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.marlonlom.utilities.timeago.TimeAgo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Thread.sleep;

public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    ImageAdapter adapter;
    ArrayList<ImageModel> imageList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recyclerview);
        initRecycler();
        final EditText searchInput = findViewById(R.id.searchTag);
        searchInput.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        searchInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || event.getAction() == KeyEvent.ACTION_DOWN
                        && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    imageList.clear();
                    new FetchImage(searchInput.getText().toString().trim()).execute();
                    return true;
                }
                return false;
            }
        });

        findViewById(R.id.searchICon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new FetchImage(searchInput.getText().toString().trim()).execute();

            }
        });

        new FetchImage("nature").execute();
    }

    private void initRecycler() {
        adapter = new ImageAdapter(getBaseContext(),imageList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(25);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        recyclerView.invalidate();
    }

    private class FetchImage extends AsyncTask<String,String,String>{
        String searchTag;

        public FetchImage(String searchTag) {
            this.searchTag = searchTag;
            imageList.clear();
            adapter.notifyDataSetChanged();
            recyclerView.invalidate();
        }

        @Override
        protected String doInBackground(String... strings) {

            StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.IMAGE_URL+searchTag,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //If we are getting success from server
//                            Log.i("akx",response.substring(response.indexOf('{'),response.lastIndexOf('}')+1));
                            try {
                                JSONObject object = new JSONObject(response.substring(response.indexOf('{'),response.lastIndexOf('}')+1));
                                JSONArray array = object.getJSONArray("items");

                                for(int i = 0;i<array.length();i++)
                                {
                                    ImageModel model = new ImageModel();
                                    model.setTitle(array.getJSONObject(i).getString("title"));

                                    model.setMedia(array.getJSONObject(i).getJSONObject("media").getString("m"));
                                    model.setAuthor(array.getJSONObject(i).getString("author"));
                                    model.setTags(array.getJSONObject(i).getString("tags"));
                                    model.setDescription(array.getJSONObject(i).getString("description"));

                                    String timeStamp =  array.getJSONObject(i).getString("published");
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                                    Date d = sdf.parse(timeStamp);
                                    model.setUploadedDate("Uploaded "+ TimeAgo.using(d.getTime()));
                                    imageList.add(model);

                                    adapter.notifyDataSetChanged();
                                    recyclerView.invalidate();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //You can handle error here if you want

                            NetworkResponse networkResponse = error.networkResponse;
                            if(networkResponse != null && networkResponse.data != null)
                            {

                            }
                            Toast.makeText(getBaseContext(),"Error in loading image",Toast.LENGTH_SHORT).show();
                        }
                    }) {

                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    //Adding parameters to request
                    return params;
                }
            };

            //Adding the string request to the queue
            stringRequest.setShouldCache(false);
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    0,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            ));
            RequestQueue requestQueue = Volley.newRequestQueue(getBaseContext());
            requestQueue.getCache().clear();
            requestQueue.add(stringRequest);
            return null;
        }
    }
}

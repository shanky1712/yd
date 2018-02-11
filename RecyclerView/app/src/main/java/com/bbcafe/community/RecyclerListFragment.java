package com.bbcafe.community;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bbcafe.community.network.ServerRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class RecyclerListFragment extends Fragment {
    public static final String TAG = RecyclerListFragment.class.getSimpleName();

    private RecyclerView recyclerView;

    public RecyclerListFragment() {
    }


    public static RecyclerListFragment newInstance() {
        return new RecyclerListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_recycler_list, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        sendServerRequest();

        return view;
    }

    @SuppressWarnings("unchecked")
    private void sendServerRequest() {
        if (ServerRequest.isConnectedToInternet(getContext())) {
            final ProgressDialog progressDialog = new ProgressDialog();
            progressDialog.show(((AppCompatActivity)getActivity()).getSupportFragmentManager());
            ServerRequest.get("http://192.168.43.182/commune/utilities/view_all_resources", new ServerRequest.GetResult() {
                @Override
                public void onResult(String resultStringFromServer) {
                    progressDialog.cancel();
                    try {
                        if (!resultStringFromServer.isEmpty()) {
                            JSONObject jsonObject = new JSONObject(resultStringFromServer);//get your result json object here
                            Log.i(TAG,resultStringFromServer);
                            if (jsonObject.getInt("status")==1) {
                                recyclerView.setAdapter(new RecyclerAdapter(jsonObject.getJSONArray("items")));
                            } else {
                                Toast.makeText(getContext(), "Status: "+jsonObject.getInt("status"), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getContext(), "Connection Error", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

        private JSONArray jsonArray;

        RecyclerAdapter(JSONArray jsonArray) {
            this.jsonArray = jsonArray;
        }

        class ViewHolder extends RecyclerView.ViewHolder{
            private View itemContainer;

            private final AppCompatTextView title;
            //private final AppCompatTextView title, url, published, id;
            private final AppCompatImageView image;

            ViewHolder(View v) {
                super(v);
                itemContainer = v;
                title = v.findViewById(R.id.title);
                //url = v.findViewById(R.id.url);
                //published = v.findViewById(R.id.published);
                //id = v.findViewById(R.id.id);
                image = v.findViewById(R.id.imageView);
            }
        }
        @Override
        public RecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item, parent, false);

            return new ViewHolder(v);
        }



        @Override
        public void onBindViewHolder(RecyclerAdapter.ViewHolder holder, final int position) {
            try {
                final JSONObject jsonObject = jsonArray.getJSONObject(holder.getAdapterPosition());

                holder.title.setText(jsonObject.getString("title"));
                //holder.url.setText(jsonObject.getString("url"));
                //holder.published.setText(jsonObject.getString("published"));
                //holder.id.setText(jsonObject.getString("id"));
                Picasso.with(getContext()).load(jsonObject.getString("image")).into(holder.image);
                /*Log.i("Image",jsonObject.getString("image"));
                Log.i("Url",jsonObject.getString("url"));*/
                holder.itemContainer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            LoadFragmentActivity.start(getActivity(), jsonObject.getString("url"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        @Override
        public int getItemCount() {
            return jsonArray.length();
        }

    }

}

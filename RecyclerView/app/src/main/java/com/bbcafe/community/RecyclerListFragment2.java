package com.bbcafe.community;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bbcafe.community.network.ServerRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class RecyclerListFragment2 extends Fragment {
    public static final String TAG = RecyclerListFragment2.class.getSimpleName();

    private RecyclerView recyclerView;

    public RecyclerListFragment2() {
    }


    public static RecyclerListFragment2 newInstance() {
        return new RecyclerListFragment2();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_recycler_list2, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        sendServerRequest();

        return view;
    }

    @SuppressWarnings("unchecked")
    private void sendServerRequest() {
        if (ServerRequest.isConnectedToInternet(getContext())) {
            ServerRequest.post("ENTER_URL_HERE", new ServerRequest.GetResult() {
                @Override
                public void onResult(String resultStringFromServer) {
                    try {
                        if (!resultStringFromServer.isEmpty()) {
                            JSONObject jsonObject = new JSONObject(resultStringFromServer);//get your result json object here
                        } else {
                            Toast.makeText(getContext(), "Connection Error", Toast.LENGTH_SHORT).show();
                        }

                        recyclerView.setAdapter(new RecyclerAdapter(getJsonObject().getJSONArray("RESULT_ARRAY")));

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }, new Pair<>("PARAM_KEY", "PARAM_VALUE"), new Pair<>("PARAM_KEY", "PARAM_VALUE"));//use comma to add more params
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

    private JSONObject getJsonObject() throws JSONException {
        return new JSONObject("{\"RESULT_ARRAY\":[{\"text\":\"ListItem Google\", \"url\":\"http://www.google.co.in/\"},{\"text\":\"ListItem Amazon\", \"url\":\"http://www.amazon.in/\"},{\"text\":\"ListItem Flipkart\", \"url\":\"http://www.flipkart.com/\"},{\"text\":\"ListItem Yahoo\", \"url\":\"http://www.yahoo.in/\"},{\"text\":\"ListItem Sankar Prakash\", \"url\":\"https://www.facebook.com/sankarprakash.yadav?ref=br_rs\"}]}");
    }




    public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

        private JSONArray jsonArray;

        RecyclerAdapter(JSONArray jsonArray) {
            this.jsonArray = jsonArray;
        }

        class ViewHolder extends RecyclerView.ViewHolder{
            private View itemContainer;

            private final AppCompatTextView listText;

            ViewHolder(View v) {
                super(v);
                itemContainer = v;
                listText = (AppCompatTextView) v.findViewById(R.id.listText);
            }
        }
        @Override
        public RecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item2, parent, false);

            return new ViewHolder(v);
        }



        @Override
        public void onBindViewHolder(RecyclerAdapter.ViewHolder holder, final int position) {
            try {
                final JSONObject jsonObject = jsonArray.getJSONObject(holder.getAdapterPosition());

                holder.listText.setText(jsonObject.getString("text"));

                holder.itemContainer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            DetailsActivity.start(getActivity(), jsonObject.getString("text"));
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

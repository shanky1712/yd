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

import com.bbcafe.community.network.GetResult;
import com.bbcafe.community.network.ServerRequest;
import com.squareup.picasso.Picasso;

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
    @Override
    public void onResume() {
        super.onResume();

        // Tracking the screen view
        MyApplication.getInstance().trackScreenView("Contact List View");
    }
    @SuppressWarnings("unchecked")
    private void sendServerRequest() {
        if (ServerRequest.isConnectedToInternet(getContext())) {
            final ProgressDialog progressDialog = new ProgressDialog();
            progressDialog.show(((AppCompatActivity)getActivity()).getSupportFragmentManager());
            ServerRequest.get("http://commune.bestbloggercafe.com/utilities/view_all_contracts", new GetResult() {
                @Override
                public void onResult(String resultStringFromServer) {
                    progressDialog.cancel();
                    try {
                        if (!resultStringFromServer.isEmpty()) {
                            JSONObject jsonObject = new JSONObject(resultStringFromServer);//get your result json object here
                            Log.i(TAG,resultStringFromServer);
                            recyclerView.setAdapter(new RecyclerAdapter(jsonObject.getJSONArray("utilities")));
                            /*if (jsonObject.getInt("status")==1) {
                                recyclerView.setAdapter(new RecyclerAdapter(jsonObject.getJSONArray("utilities")));
                            } else {
                                Toast.makeText(getContext(), "Status: "+jsonObject.getInt("status"), Toast.LENGTH_SHORT).show();
                            }*/
                        } else {
                            Toast.makeText(getContext(), "Connection Error", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        MyApplication.getInstance().trackException(e);
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



    public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

        private JSONArray jsonArray;

        RecyclerAdapter(JSONArray jsonArray) {
            this.jsonArray = jsonArray;
        }

        class ViewHolder extends RecyclerView.ViewHolder{
            private View itemContainer;

            private final AppCompatTextView name, shopName, mobile;
            private final AppCompatImageView image;

            ViewHolder(View v) {
                super(v);
                itemContainer = v;
                name = v.findViewById(R.id.name);
                shopName = v.findViewById(R.id.shopName);
                mobile = v.findViewById(R.id.mobile);
                /*landLine = v.findViewById(R.id.landLine);*/
                image = v.findViewById(R.id.imageView);
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

                holder.name.setText(jsonObject.getString("name"));
                holder.shopName.setText(jsonObject.getString("shopname"));
                holder.mobile.setText(jsonObject.getString("mobile"));
                /*holder.landLine.setText(jsonObject.getString("landline"));*/
                Picasso.with(getContext()).load(jsonObject.getString("photo")).into(holder.image);

                holder.itemContainer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DetailsActivity.start(getActivity(), jsonObject.toString());
                    }
                });
            } catch (JSONException e) {
                MyApplication.getInstance().trackException(e);
                e.printStackTrace();
            }
        }
        @Override
        public int getItemCount() {
            return jsonArray.length();
        }

    }

}

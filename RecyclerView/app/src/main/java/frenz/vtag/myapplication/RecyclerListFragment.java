package frenz.vtag.myapplication;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bbcafe.community.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


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
        try {
            recyclerView.setAdapter(new RecyclerAdapter(getJsonArray()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private JSONArray getJsonArray() throws JSONException {
        return new JSONArray("[{\"text\":\"ListItem 1\"},{\"text\":\"ListItem 2\"},{\"text\":\"ListItem 3\"},{\"text\":\"ListItem 4\"},{\"text\":\"ListItem 5\"}]");
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
                    .inflate(R.layout.list_item, parent, false);

            return new ViewHolder(v);
        }



        @Override
        public void onBindViewHolder(RecyclerAdapter.ViewHolder holder, final int position) {
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(holder.getAdapterPosition());
                holder.listText.setText(jsonObject.getString("text"));

                holder.itemContainer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(getActivity(), NextActivity.class));
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

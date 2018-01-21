package frenz.vtag.myapplication;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bbcafe.community.R;


public class TextFragment extends Fragment {
    public static final String TAG = TextFragment.class.getSimpleName();

    private AppCompatTextView textView;

    private String textToDisplay;

    public TextFragment() {
        // Required empty public constructor
    }


    public static TextFragment newInstance(String textToDisplay) {
        TextFragment fragment = new TextFragment();
        Bundle args = new Bundle();
        args.putString("TEXT_TO_DISPLAY", textToDisplay);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            textToDisplay = getArguments().getString("TEXT_TO_DISPLAY");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_text, container, false);

        textView = (AppCompatTextView) view.findViewById(R.id.textView);
        textView.setText(textToDisplay);
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

}

package com.bbcafe.community;

import android.app.Fragment;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bbcafe.community.network.GetResult;
import com.bbcafe.community.network.ServerRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class SongsListFragment extends Fragment {
    public static final String TAG = SongsListFragment.class.getSimpleName();

    private JSONArray jsonArray;

    private RecyclerView recyclerView;

    public SongsListFragment() {
    }

    public static SongsListFragment newInstance(String songsJsonArrayString) {
        SongsListFragment fragment = new SongsListFragment();
        Bundle args = new Bundle();
        args.putString("SONGS_DATA", songsJsonArrayString);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createDirectory();
        try {
            jsonArray = new JSONArray(getArguments().getString("SONGS_DATA"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_recycler_list, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));



        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        recyclerView.setAdapter(new RecyclerAdapter(jsonArray));
    }

    private void createDirectory() {
        File directory = new File(Environment.getExternalStorageDirectory(), getString(R.string.app_name));

        if(!directory.exists()) {
            boolean isCreated = directory.mkdirs();
            Toast.makeText(getContext(), "File isCreated "+isCreated, Toast.LENGTH_SHORT).show();
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

    class DownloadTask extends AsyncTask<String, Integer, String> {

        private Context context;
        private PowerManager.WakeLock mWakeLock;
        private ProgressBar progressBar;
        private GetResult getResult;

        public DownloadTask(Context context, ProgressBar progressBar, GetResult getResult) {
            this.context = context;
            this.progressBar = progressBar;
            this.getResult = getResult;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // take CPU lock to prevent CPU from going off if the user
            // presses the power button during download
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    getClass().getName());
            mWakeLock.acquire();
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setIndeterminate(true);
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            // if we get here, length is known, now set indeterminate to false
            progressBar.setIndeterminate(false);
            progressBar.setMax(100);
            progressBar.setProgress(progress[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            mWakeLock.release();
            progressBar.setVisibility(View.GONE);
            if (result != null) {
                getResult.onResult(null);
                Toast.makeText(context, "Download error: " + result, Toast.LENGTH_LONG).show();
            } else {
                getResult.onResult("File downloaded");
                Toast.makeText(context, "File downloaded", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected String doInBackground(String... sUrl) {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            try {
                String fileName = sUrl[1];
                URL url = new URL(sUrl[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                // expect HTTP 200 OK, so we don't mistakenly save error report
                // instead of the file
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage();
                }

                // this will be useful to display download percentage
                // might be -1: server did not report the length
                int fileLength = connection.getContentLength();

                // download the file
                input = connection.getInputStream();
                output = new FileOutputStream(Environment.getExternalStorageDirectory()+"/"+getString(R.string.app_name)+"/"+fileName+".mp3");

                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    // allow canceling with back button
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;
                    // publishing the progress....
                    if (fileLength > 0) // only if total length is known
                        publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);
                }
            } catch (Exception e) {
                return e.toString();
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }

                if (connection != null)
                    connection.disconnect();
            }
            return null;
        }
    }

    private class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

        final MediaPlayer mediaPlayer;
        private JSONArray jsonArray;
        private int currentlyPlaying = -1;

        RecyclerAdapter(JSONArray jsonArray) {
            this.jsonArray = jsonArray;
            mediaPlayer = new MediaPlayer();
        }

        class ViewHolder extends RecyclerView.ViewHolder{
            private RelativeLayout itemContainer;

            private final AppCompatTextView textView;
            private final ProgressBar progressBar;
            private final FloatingActionButton fab;

            ViewHolder(View v) {
                super(v);
                itemContainer = v.findViewById(R.id.container);
                textView = v.findViewById(R.id.songName);
                progressBar = v.findViewById(R.id.fabProgress);
                fab = v.findViewById(R.id.fabButton);
            }
        }

        @Override
        public RecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_songs, parent, false);

            return new ViewHolder(v);
        }


        @Override
        public void onBindViewHolder(final RecyclerAdapter.ViewHolder holder, final int position) {
            try {
                final JSONObject jsonObject = jsonArray.getJSONObject(holder.getAdapterPosition());
                final String title = jsonObject.getString("title");
                final String url = jsonObject.getString("url");

                holder.textView.setText(title);

                final String filePath = Environment.getExternalStorageDirectory()+"/"+getString(R.string.app_name)+"/"+title+".mp3";
                File file = new File(filePath);
                if (file.exists()) {
                    holder.progressBar.setVisibility(View.GONE);
                    if (currentlyPlaying==position && mediaPlayer.isPlaying()) {
                        holder.fab.setImageResource(android.R.drawable.ic_media_pause);
                    } else {
                        holder.fab.setImageResource(android.R.drawable.ic_media_play);
                    }
                    holder.fab.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (currentlyPlaying!=position) {
                                currentlyPlaying = position;
                                mediaPlayer.reset();
                                try {
                                    mediaPlayer.setDataSource(filePath);
                                    mediaPlayer.prepare();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                mediaPlayer.start();
                            } else {
                                if (mediaPlayer.isPlaying()) {
                                    mediaPlayer.pause();
                                    holder.fab.setImageResource(android.R.drawable.ic_media_play);
                                } else {
                                    mediaPlayer.start();
                                    holder.fab.setImageResource(android.R.drawable.ic_media_pause);
                                }
                            }
                            notifyDataSetChanged();
                        }
                    });
                } else {
                    holder.progressBar.setVisibility(View.GONE);
                    holder.fab.setBackgroundResource(android.R.drawable.stat_sys_download);
                    holder.fab.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new DownloadTask(getContext(), holder.progressBar, new GetResult() {
                                @Override
                                public void onResult(String resultStringFromServer) {
                                    notifyDataSetChanged();
                                }
                            }).execute(url, title);
                        }
                    });
                }

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

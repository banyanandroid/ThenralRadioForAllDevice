package forall.banyan.com.thenralradioforalldevice;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import dmax.dialog.SpotsDialog;
import forall.banyan.com.thenralradioforalldevice.Global.ConnectionDetector;
import forall.banyan.com.thenralradioforalldevice.Global.Global;
import forall.banyan.com.thenralradioforalldevice.adapter.Log_Adapter;
import forall.banyan.com.thenralradioforalldevice.appcontroller.AppController;
import xyz.hanks.library.SmallBang;
import xyz.hanks.library.SmallBangListener;

/**************************************************
 * Author : Jothiprabhakar
 *
 * Date : 13/3/2017
 * **************************************************/

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Thenral FM";

    public static final String TAG_ID = "pgm_id";
    public static final String TAG_PGM_NAME = "pgm_name";
    public static final String TAG_PGM_TIME = "pgm_time";

    public static final String TAG_URL_ID = "url_id";
    public static final String TAG_URL_NAME = "url_name";

    public static final String TAG_IMAGE_URL = "add_image";

    public static final String TAG_IMAGE = "add_image";
    public static final String TAG_NAME = "add_name";

    String str_url = "http://66.55.145.43:7402", str_id = "", str_image = "", str_name = "";
    int i = 0;

    ArrayList<String> Arraylist_adds = null;

    SliderLayout sliderLayout;
    //HashMap<String, String> Hash_file_maps;

    ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    /********************
     * Media Player
     *********************/
    private MediaPlayer mediaPlayer;
    AudioManager audioManager;
    AlertDialog dialog;
    ProgressDialog pdialog;

    private PowerManager.WakeLock wl;
    ProgressBar progressBar;

    /***********************
     * Internet Checking
     ***********************/
    Boolean isInternetPresent = false;
    ConnectionDetector cd;

    /***
     * Program List Array List
     ***/
    static ArrayList<HashMap<String, String>> prgogram_list;

    HashMap<String, String> Hash_file_maps;

    HashMap<String, String> params = new HashMap<String, String>();

    Log_Adapter adapter;
    ListView list_log;

    public static RequestQueue queue;

    private SmallBang mSmallBang;
    ImageView img_play, img_stop, img_remind, img_remind_click, img_share_click, img_slide_play, img_slide_stop, img_share, img_slide_up, img_slide_down, img_slider;
    private TextView txt_current_pgm_name, txt_current_pgm_name_top;
    ImageView feedImageView;

    private SlidingUpPanelLayout mLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*** Fav Button ***/
        mSmallBang = SmallBang.attach2Window(this);

        /*** Hashmap array List ***/
        prgogram_list = new ArrayList<HashMap<String, String>>();
        Hash_file_maps = new HashMap<String, String>();
        /*** Tool bar ***/
        setSupportActionBar((Toolbar) findViewById(R.id.main_toolbar));
        mLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);

        img_play = (ImageView) findViewById(R.id.main_img_play);
        img_stop = (ImageView) findViewById(R.id.main_img_stop);
        img_remind = (ImageView) findViewById(R.id.main_img_reminder);
        img_share = (ImageView) findViewById(R.id.main_img_share);
        img_remind_click = (ImageView) findViewById(R.id.main_img_reminder_clicked);
        img_share_click = (ImageView) findViewById(R.id.main_img_share_clicked);
        img_slide_play = (ImageView) findViewById(R.id.main_slide_img_play);
        img_slide_stop = (ImageView) findViewById(R.id.main_slide_img_stop);


        img_slider = (ImageView) findViewById(R.id.main_img_slider);
        sliderLayout = (SliderLayout) findViewById(R.id.slider);

        list_log = (ListView) findViewById(R.id.listView);

        img_slide_up = (ImageView) findViewById(R.id.main_slide_img_up);
        img_slide_down = (ImageView) findViewById(R.id.main_slide_img_down);


        // feedImageView = (ImageView) findViewById(R.id.main_imageView);

        txt_current_pgm_name = (TextView) findViewById(R.id.main_txt_slide_pgm_name);
        txt_current_pgm_name_top = (TextView) findViewById(R.id.main_txt_slide_pgm_name1);
        txt_current_pgm_name.setSelected(true);
        txt_current_pgm_name_top.setSelected(true);

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "DoNjfdhotDimScreen");

        /************************************
         *  Audio Controller
         * *********************************/
        audioManager = (AudioManager) getSystemService(getBaseContext().AUDIO_SERVICE);
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int curVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

        SeekBar volControl = (SeekBar) findViewById(R.id.volbar);
        volControl.setMax(maxVolume);
        volControl.setProgress(curVolume);
        volControl.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onStartTrackingTouch(SeekBar arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
                // TODO Auto-generated method stub
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, arg1, 0);
            }
        });

        /**********************************
         *  Button Interfaces
         * *******************************/

        /*** Main Play Button ***/
        img_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                img_play.setVisibility(View.GONE);
                img_stop.setVisibility(View.VISIBLE);

                img_slide_play.setVisibility(View.GONE);
                img_slide_stop.setVisibility(View.VISIBLE);

                try {
                    mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);

                } catch (Exception e) {

                }

                System.out.println("URL : : " + str_url);

                isInternetPresent = cd.isConnectingToInternet();

                if (!(str_url.equals(""))) {

                    if (isInternetPresent) {

                        try {
                            System.out.println("Initialized");
                            System.out.println("URL TEST" + str_url);
                            new Stream_Radio().execute();
                        } catch (Exception e) {

                        }


                    } else {
                        showAlertDialog(MainActivity.this, "Oops!",
                                "You don't have internet connection Buddy !!!", false);

                        img_play.setVisibility(View.VISIBLE);
                        img_stop.setVisibility(View.GONE);

                        img_slide_play.setVisibility(View.VISIBLE);
                        img_slide_stop.setVisibility(View.GONE);

                    }

                } else {

                    img_play.setVisibility(View.VISIBLE);
                    img_stop.setVisibility(View.GONE);

                    img_slide_play.setVisibility(View.VISIBLE);
                    img_slide_stop.setVisibility(View.GONE);
                }

            }
        });

        /*** Main Pause Button ***/

        img_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PauseFm();

                img_stop.setVisibility(View.GONE);
                img_play.setVisibility(View.VISIBLE);

                img_slide_play.setVisibility(View.VISIBLE);
                img_slide_stop.setVisibility(View.GONE);

                try {
                    mLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);

                } catch (Exception e) {

                }
            }
        });

        /*** Main Share Button ***/
        img_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent share = new Intent(android.content.Intent.ACTION_SEND);
                share.setType("text/plain");
                share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                share.putExtra(Intent.EXTRA_SUBJECT, "Radio Thenal");
                share.putExtra(Intent.EXTRA_TEXT, "Hi Buddy i have found new Tamil Radio App on : https://play.google.com/store/apps/details?id=com.banyaninfotech.jo.thenralfm_model1  ");

                startActivity(Intent.createChooser(share, "Share link!"));

            }
        });

        /*** Main Reminder Button ***/
        img_remind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Remind(v);

            }
        });

        /*** Slide Stop Button ***/
        img_slide_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PauseFm();

                img_slide_stop.setVisibility(View.GONE);
                img_slide_play.setVisibility(View.VISIBLE);

                img_play.setVisibility(View.VISIBLE);
                img_stop.setVisibility(View.GONE);

                try {
                    mLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);

                } catch (Exception e) {

                }
            }
        });
        /*** Slide Play Button ***/
        img_slide_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                isInternetPresent = cd.isConnectingToInternet();

                if (isInternetPresent) {

                    img_slide_play.setVisibility(View.GONE);
                    img_slide_stop.setVisibility(View.VISIBLE);

                    img_play.setVisibility(View.GONE);
                    img_stop.setVisibility(View.VISIBLE);

                    try {
                        System.out.println("Initialized");
                        new Stream_Radio().execute();
                    } catch (Exception e) {

                    }

                } else {
                    showAlertDialog(MainActivity.this, "OOPS!",
                            "You don't have internet connection Buddy !!!", false);
                }

            }
        });

        /*********************************
         *  NETWORK CONNECTION DETECTOR
         * ******************************/
        cd = new ConnectionDetector(getApplicationContext());

        isInternetPresent = cd.isConnectingToInternet();

        if (isInternetPresent) {


            try {
                pdialog = new ProgressDialog(MainActivity.this);
                pdialog.setMessage("Please wait Buddy...");
                pdialog.setIndeterminate(false);
                pdialog.setCancelable(false);
                pdialog.show();
                pdialog.setContentView(R.layout.activity_loading);
                queue = Volley.newRequestQueue(this);

                makeJsonProgramRequest();

            } catch (Exception e) {
                // TODO: handle exception
            }

        } else {
            showAlertDialog(MainActivity.this, "Oops!",
                    "You don't have internet connection Buddy !!!", false);
        }


        /**********************************
         * Bottom Slide Menu
         * ********************************/

        mLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);

        mLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                Log.i(TAG, "onPanelSlide, offset " + slideOffset);
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                Log.i(TAG, "onPanelStateChanged " + newState);
            }
        });
        mLayout.setFadeOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        });


        /**************************************
         *  Slider Function
         * ************************************/
        final int[] slide = new int[]{R.drawable.s1, R.drawable.s2, R.drawable.s3, R.drawable.s4, R.drawable.s5, R.drawable.s6, R.drawable.s7, R.drawable.s8, R.drawable.s9, R.drawable.s10};

        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            int i = 0;

            public void run() {
                img_slider.setImageResource(slide[i]);
                i++;
                if (i > slide.length - 1) {
                    i = 0;
                }
                handler.postDelayed(this, 10000);  //for interval... slide changes
            }
        };
        handler.postDelayed(runnable, 10000); //for initial delay..


    }

    @Override
    public void onResume() {
        super.onResume();

    }

    /*****************************
     * Favorite Icon Function
     **************************/
    public void Remind(View view) {
        img_remind.setImageResource(R.drawable.heart_clicked);
        mSmallBang.bang(view);
        mSmallBang.setmListener(new SmallBangListener() {
            @Override
            public void onAnimationStart() {
            }

            @Override
            public void onAnimationEnd() {
                toast("Reminder");
            }
        });
    }

    private void toast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    /*****************************
     * Main Menu
     **************************/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.demo, menu);
        MenuItem item = menu.findItem(R.id.action_toggle);
        if (mLayout != null) {
            if (mLayout.getPanelState() == SlidingUpPanelLayout.PanelState.HIDDEN) {
                item.setTitle(R.string.action_show);

            } else {
                item.setTitle(R.string.action_hide);

            }
        }
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_toggle: {
                if (mLayout != null) {
                    if (mLayout.getPanelState() != SlidingUpPanelLayout.PanelState.HIDDEN) {
                        mLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
                        item.setTitle(R.string.action_show);
                    } else {
                        mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                        item.setTitle(R.string.action_hide);
                    }
                }
                return true;
            }
            case R.id.action_anchor: {
                if (mLayout != null) {
                    if (mLayout.getAnchorPoint() == 1.0f) {
                        mLayout.setAnchorPoint(0.7f);
                        mLayout.setPanelState(SlidingUpPanelLayout.PanelState.ANCHORED);
                        item.setTitle(R.string.action_anchor_disable);
                    } else {
                        mLayout.setAnchorPoint(1.0f);
                        mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                        item.setTitle(R.string.action_anchor_enable);
                    }
                }
                return true;
            }
            case R.id.action_about: {

                try {
                    AlertDialog_About();
                } catch (Exception e) {

                }
                return true;
            }
            case R.id.action_privacy: {

                try {
                    AlertDialog_Privacy();

                } catch (Exception e) {

                }


                return true;
            }

            case R.id.action_refresh: {

            }
        }
        return super.onOptionsItemSelected(item);
    }


    /***************************************
     * Program Listings Using Volley
     *************************************/

    private void makeJsonProgramRequest() {

        System.out.println("CAME 1");

        StringRequest request = new StringRequest(Request.Method.GET,
                Global.str_program_list, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, response.toString());
                try {

                    JSONObject obj = new JSONObject(response);
                    int success = obj.getInt("success");

                    if (success == 1) {

                        JSONArray arr1;

                        arr1 = obj.getJSONArray("pgms");

                        for (int i = 0; arr1.length() > i; i++) {
                            JSONObject obj1 = arr1.getJSONObject(i);

                            String id = obj1.getString(TAG_ID);
                            String name = obj1.getString(TAG_PGM_NAME);
                            String time = obj1.getString(TAG_PGM_TIME);

                            // creating new HashMap
                            HashMap<String, String> map = new HashMap<String, String>();

                            // adding each child node to HashMap key => value
                            map.put(TAG_ID, id);
                            map.put(TAG_PGM_NAME, name);
                            map.put(TAG_PGM_TIME, time);

                            prgogram_list.add(map);

                            try {
                                makeJsonURLRequest();
                            } catch (Exception e) {

                            }

                            adapter = new Log_Adapter(MainActivity.this,
                                    prgogram_list);
                            list_log.setAdapter(adapter);


                        }

                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                pdialog.hide();
                pdialog.dismiss();

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();


                return params;
            }

        };

        // Adding request to request queue
        queue.add(request);
    }

    /***************************************
     * Program ADDs Using Volley
     *************************************/

    private void makeJsonURLRequest() {

        StringRequest request = new StringRequest(Request.Method.GET,
                Global.str_adds, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, response.toString());
                try {

                    JSONObject obj = new JSONObject(response);
                    int success = obj.getInt("success");

                    if (success == 1) {

                        JSONArray arr1;

                        arr1 = obj.getJSONArray("pgms");

                        for (int i = 0; arr1.length() > i; i++) {
                            JSONObject obj1 = arr1.getJSONObject(i);

                            String img = obj1.getString(TAG_IMAGE);

                            Hash_file_maps.put("", img);

                            for (String name : Hash_file_maps.keySet()) {

                                TextSliderView textSliderView = new TextSliderView(MainActivity.this);
                                textSliderView
                                        .image(Hash_file_maps.get(name))
                                        .setScaleType(BaseSliderView.ScaleType.Fit);
                                textSliderView.bundle(new Bundle());
                                textSliderView.getBundle()
                                        .putString("extra", name);
                                sliderLayout.addSlider(textSliderView);
                            }

                        }
                        sliderLayout.setPresetTransformer(SliderLayout.Transformer.Background2Foreground);
                        sliderLayout.setCustomAnimation(new DescriptionAnimation());
                        sliderLayout.setDuration(5000);

                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                pdialog.hide();
                pdialog.dismiss();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();


                return params;
            }

        };

        // Adding request to request queue
        queue.add(request);
    }

    /*****************************************
     * Alert Dialog for Internet Connection
     *****************************************/

    public void showAlertDialog(Context context, String title, String message, Boolean status) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();

        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setIcon((status) ? R.drawable.wifi : R.drawable.wifi);

        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {


            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    /*****************************************
     * Custom AlertDialog Privacy
     *****************************************/
    public void AlertDialog_Privacy() {

        LayoutInflater li = LayoutInflater.from(MainActivity.this);
        View promptsView = li.inflate(R.layout.activity_alert_privacy, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                MainActivity.this);
        alertDialogBuilder.setTitle("Privacy Policy");
        alertDialogBuilder.setIcon(R.mipmap.ic_launcher);
        // alertDialogBuilder.setInverseBackgroundForced(#26A65B);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);


        alertDialogBuilder.setCancelable(false)

                .setNeutralButton("Done", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    /*****************************************
     * Custom AlertDialog About
     *****************************************/
    public void AlertDialog_About() {

        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Thenral Radio Tamil V1.0")
                .setMessage("Tenral radio Australia's first tamil online radio with cristal clear audio.\nApplication powered by The Banyan Infotech")
                .setNeutralButton("Done",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                // TODO Auto-generated method stub

                            }
                        }).show();
    }

    /*****************************
     * Asyntask Fun Radio Stream
     ****************************/
    class Stream_Radio extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            dialog = new SpotsDialog(MainActivity.this);
            dialog.show();
        }

        protected String doInBackground(String... args) {

            System.out.println("ASYN URL" + str_url);
            InitializeFm();
            return null;
        }

        protected void onPostExecute(String file_url) {

            dialog.dismiss();

        }
    }

    /*****************************
     * Fun Radio Starts Stream
     ****************************/

    public void InitializeFm() {

        try {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(str_url);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.prepare();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mediaPlayer.start();
                }
            });

        } catch (Exception e) {

        }
    }

    /*****************************
     * Fun Radio Pause Stream
     ****************************/

    public void PauseFm() {

        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;

            img_play.setVisibility(View.VISIBLE);
            img_stop.setVisibility(View.GONE);
        } else {
            try {
                InitializeFm();
            } catch (Exception e) {

            }
        }
    }


    /*****************************
     * Back Button Click Listener
     ****************************/
    @Override
    public void onBackPressed() {
        if (mLayout != null &&
                (mLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED || mLayout.getPanelState() == SlidingUpPanelLayout.PanelState.ANCHORED)) {
            mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        } else {
            super.onBackPressed();
        }
    }

}

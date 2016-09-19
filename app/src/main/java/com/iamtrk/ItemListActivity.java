package com.iamtrk;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.iamtrk.androidExplorer.Content;
import com.iamtrk.androidExplorer.FrequencyManager;
import com.iamtrk.androidExplorer.ProcessorManager;


/**
 * An activity representing a list of Items. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ItemDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 * <p>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link ItemListFragment} and the item details
 * (if present) is a {@link ItemDetailFragment}.
 * <p>
 * This activity also implements the required
 * {@link ItemListFragment.Callbacks} interface
 * to listen for item selections.
 */
public class ItemListActivity extends AppCompatActivity
        implements ItemListFragment.Callbacks {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private ActivityManager mgrActivity;
    private ProcessorManager mgrProcessor;
    private FrequencyManager mgrFrequency;
    TextView memInfo;
    TextView cpuInfo;
    ImageView usageImage;

    private boolean weHavePermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestForResultContactsPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, 123);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 123
                && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startApp();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Won't be able to show you IMEI")
                    .setTitle("Permissions Denied");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    startApp();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    private void requestPermissionFirst() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_PHONE_STATE)) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("PHONE: To find IMEI details.")
                    .setTitle("Permissions required");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    requestForResultContactsPermission();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        } else {
            requestForResultContactsPermission();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            if(!weHavePermission()) {
                requestPermissionFirst();
            } else {
                startApp();
            }
        } else {
            startApp();
        }
    }

    private int getCpuUsageImage(int dblPercent) {
        if(dblPercent<1) {
            return R.drawable.i0;
        } else if(dblPercent<2) {
            return R.drawable.i1;
        } else if(dblPercent<3) {
            return R.drawable.i2;
        } else if(dblPercent<4) {
            return R.drawable.i3;
        } else if(dblPercent<5) {
            return R.drawable.i4;
        } else if(dblPercent<6) {
            return R.drawable.i5;
        } else if(dblPercent<7) {
            return R.drawable.i6;
        } else if(dblPercent<8) {
            return R.drawable.i7;
        } else if(dblPercent<9) {
            return R.drawable.i8;
        } else if(dblPercent<10) {
            return R.drawable.i9;
        }

        return R.drawable.i0;
    }

    public void startApp() {
        Content.init(getApplicationContext());
        setContentView(R.layout.activity_item_list);
        memInfo = (TextView) findViewById(R.id.memory_usage_textView);
        cpuInfo = (TextView) findViewById(R.id.cpu_usage_textView);
        usageImage = (ImageView) findViewById(R.id.cpu_usage_image);

        mgrActivity = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        mgrProcessor = new ProcessorManager();
        mgrFrequency = new FrequencyManager();
        Double dblPercent = mgrProcessor.getUsage();
        String strClock = mgrFrequency.getFrequency();

        ActivityManager.MemoryInfo memInformation = new ActivityManager.MemoryInfo();
        mgrActivity.getMemoryInfo(memInformation);
        long lngFree = memInformation.availMem / 1048576L;
        long lngTotal = memInformation.totalMem / 1048576L;
        memInfo.setText(Long.valueOf(lngFree).toString()+"MB of"+ Long.valueOf(lngTotal).toString()+"MB memory free");
        cpuInfo.setText("CPU: "+dblPercent.intValue() + "% @ " + strClock);
        usageImage.setImageResource(getCpuUsageImage((int) (dblPercent / 10)));

        Thread t = new Thread() {
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ActivityManager.MemoryInfo memInformation = new ActivityManager.MemoryInfo();
                                mgrActivity.getMemoryInfo(memInformation);
                                long lngFree = memInformation.availMem / 1048576L;
                                long lngTotal = memInformation.totalMem / 1048576L;
                                memInfo.setText(Long.valueOf(lngFree).toString()+"MB of"+ Long.valueOf(lngTotal).toString()+"MB memory free");
                                Double dblPercent = mgrProcessor.getUsage();
                                String strClock = mgrFrequency.getFrequency();
                                cpuInfo.setText("CPU: "+dblPercent.intValue() + "% @ " + strClock);
                                usageImage.setImageResource(getCpuUsageImage((int) (dblPercent / 10)));
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };
        t.start();

        if (findViewById(R.id.item_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-large and
            // res/values-sw600dp). If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;

            // In two-pane mode, list items should be given the
            // 'activated' state when touched.
            /*((ItemListFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.item_list))
                    .setActivateOnItemClick(true);*/
        }

        // TODO: If exposing deep links into your app, handle intents here.
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.item_list, menu);
        // return true so that the menu pop up is opened
        return true;
    }*/

    /**
     * Callback method from {@link ItemListFragment.Callbacks}
     * indicating that the item with the given ID was selected.
     */
    @Override
    public void onItemSelected(String id) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(ItemDetailFragment.ARG_ITEM_ID, id);
            ItemDetailFragment fragment = new ItemDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.item_detail_container, fragment)
                    .commit();

        } else {
            // In single-pane mode, simply start the detail activity
            // for the selected item ID.
            Intent detailIntent = new Intent(this, ItemDetailActivity.class);
            detailIntent.putExtra(ItemDetailFragment.ARG_ITEM_ID, id);
            startActivity(detailIntent);
        }
    }
}

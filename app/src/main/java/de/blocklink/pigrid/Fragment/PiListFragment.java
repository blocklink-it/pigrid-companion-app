package de.blocklink.pigrid.Fragment;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import de.blocklink.pigrid.Adapter.PiItem;
import de.blocklink.pigrid.Adapter.SimpleItemRecyclerViewAdapter;
import de.blocklink.pigrid.Helper.ConnectionHelper;
import de.blocklink.pigrid.Helper.UrlHelper;
import de.blocklink.pigrid.MainActivity;
import de.blocklink.pigrid.R;
import io.resourcepool.ssdp.client.SsdpClient;
import io.resourcepool.ssdp.model.DiscoveryListener;
import io.resourcepool.ssdp.model.DiscoveryRequest;
import io.resourcepool.ssdp.model.SsdpService;
import io.resourcepool.ssdp.model.SsdpServiceAnnouncement;


/**
 * A fragment representing a list of Items.
 */
public class PiListFragment extends Fragment {

    private int TIME_OUT_NO_PI_SHORT = 100; // 100ms for quickly hiding

    List<PiItem> piItems = null;
    SimpleItemRecyclerViewAdapter myAdapter;
    SsdpClient client;
    private ProgressDialog pd;
    private ConnectivityChangeReceiver connectivityChangeReceiver = null;
    private boolean firstLoad = true;

    RecyclerView recyclerView;
    TextView emptyView;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PiListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // initializes broadcasting for connectivity changes
        connectivityChangeReceiver = new ConnectivityChangeReceiver();
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        intentFilter.setPriority(100);
        getActivity().registerReceiver(connectivityChangeReceiver, intentFilter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pie_list, container, false);
        Context context = view.getContext();
        recyclerView = view.findViewById(R.id.list);
        emptyView = view.findViewById(R.id.empty_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(linearLayoutManager); // sets layout Manager
        // sets divider in recycler view
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                linearLayoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        setupProgressDialog();
        myAdapter = new SimpleItemRecyclerViewAdapter(piItems);
        recyclerView.setAdapter(myAdapter);
        setupPiDiscovery();

        return view;
    }

    // function to setup Progress Dialog
    private void setupProgressDialog() {
        pd = new ProgressDialog(getActivity(), R.style.CustomAlertDialogStyle);
        pd.setTitle(getString(R.string.app_name));
        pd.setMessage(getString(R.string.searching_pi));
        pd.setCancelable(false);
        //Adds cancel button in progress Dialog
        pd.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (client != null) {
                    client.stopDiscovery(); // if cancel is clicked stop network discovery
                }
                pd.dismiss();
                hideShowNoPiFound(TIME_OUT_NO_PI_SHORT); // shows the pie not found message quickly
            }
        });
        pd.show();
    }

    // function to discover pi if wifi is connected
    private void setupPiDiscovery() {
        if (ConnectionHelper.isWiFiConnected(getActivity())) {
            pd.show();
            if (client != null) {
                client.stopDiscovery();
            }
            //20 seconds is the wait time for finding pie; if it excceded user will see no pie fund msg
            int TIME_OUT_NO_PI = 20000;
            hideShowNoPiFound(TIME_OUT_NO_PI);
            discoverPis(); // start pi discovery
        } else {
            pd.dismiss();
            Toast.makeText(getActivity(), getString(R.string.connect_message), Toast.LENGTH_LONG).show();
            ConnectionHelper.enableWifi(getActivity()); // enables wifi
            hideShowNoPiFound(TIME_OUT_NO_PI_SHORT);
        }
    }

    // function to discover pie, device will be returned onServiceDiscovered methods if found
    private void discoverPis() {

        piItems = new ArrayList<PiItem>();
        client = SsdpClient.create();
        DiscoveryRequest networkStorageDevice = DiscoveryRequest.builder()
                .serviceType(UrlHelper.serviceType)
                .build();
        client.discoverServices(networkStorageDevice, new DiscoveryListener() {
            @Override
            public void onServiceDiscovered(SsdpService service) {
                System.out.println("Found ip: " + service.getLocation());
                PiItem pi = new PiItem(service.getLocation(), service.getSerialNumber(), service.getServiceType(), service.getLocation());
                piItems.add(pi);

                getActivity().runOnUiThread(new Runnable() { // updates view on main thread
                    @Override
                    public void run() {
                        myAdapter.setData(piItems); // sets new data
                        myAdapter.notifyDataSetChanged(); // notify the adapter that data has been changed
                        pd.dismiss();
                        hideShowNoPiFound(TIME_OUT_NO_PI_SHORT);
                    }
                });
            }

            @Override
            public void onFailed(Exception ex) {
                System.out.println("Failed service: " + ex);
                pd.dismiss();
                Toast.makeText(getActivity(), getString(R.string.failed_pi), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onServiceAnnouncement(SsdpServiceAnnouncement announcement) {
                System.out.println("Service announced something: " + announcement);
            }
        });
    }

    // function to show pie list or no pie display msg
    private void hideShowNoPiFound(int timeOut) {

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (pd.isShowing()) {
                    pd.dismiss();
                }
                if (piItems == null || piItems.isEmpty()) {
                    recyclerView.setVisibility(View.GONE);
                    emptyView.setVisibility(View.VISIBLE);
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    emptyView.setVisibility(View.GONE);
                }
            }
        }, timeOut);
    }

    // function to clear the piList and reset adapter
    private void clearPiListView() {
        piItems = null;
        myAdapter.setData(piItems);
        myAdapter.notifyDataSetChanged();
        hideShowNoPiFound(TIME_OUT_NO_PI_SHORT);
    }

    public void onResume() {
        super.onResume();
        // Set title bar
        ((MainActivity) getActivity()).setActionBarTitle(getString(R.string.title_pie_list)); // setup Action bar title
        ((MainActivity) getActivity()).setMenuSelected(); // shows selected menu
    }

    @Override
    public void onStop() {
        super.onStop();
        if (client != null) {
            client.stopDiscovery();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (client != null) {
            client.stopDiscovery();
        }

        if (connectivityChangeReceiver != null) {
            getActivity().unregisterReceiver(connectivityChangeReceiver);
        }
    }


    public class ConnectivityChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int status = ConnectionHelper.getConnectivityStatusString(context);
            switch (status) {
                case ConnectionHelper.NETWORK_STATUS_NOT_CONNECTED:
                    Toast.makeText(getActivity(), getString(R.string.connection_lost), Toast.LENGTH_LONG).show();
                    clearPiListView();
                    break;
                case ConnectionHelper.NETWORK_STATUS_WIFI:
                    if (!firstLoad) {
                        setupPiDiscovery();
                        Toast.makeText(getActivity(), getString(R.string.wifi_connected), Toast.LENGTH_LONG).show();
                    }
                    firstLoad = false;
                    break;
                case ConnectionHelper.NETWORK_STATUS_MOBILE:
                    Toast.makeText(getActivity(), getString(R.string.data_connected), Toast.LENGTH_LONG).show();
                    clearPiListView();
                    break;
            }
        }
    }

}

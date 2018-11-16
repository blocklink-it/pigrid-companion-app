package de.blocklink.pgiri.pgd.Fragment;

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
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import de.blocklink.pgiri.pgd.Adapter.PiItem;
import de.blocklink.pgiri.pgd.Adapter.SimpleItemRecyclerViewAdapter;
import de.blocklink.pgiri.pgd.Helper.ConnectionHelper;
import de.blocklink.pgiri.pgd.MainActivity;
import de.blocklink.pgiri.pgd.R;
import io.resourcepool.ssdp.client.SsdpClient;
import io.resourcepool.ssdp.model.DiscoveryListener;
import io.resourcepool.ssdp.model.DiscoveryRequest;
import io.resourcepool.ssdp.model.SsdpService;
import io.resourcepool.ssdp.model.SsdpServiceAnnouncement;


/**
 * A fragment representing a list of Items.
 */
public class PiListFragment extends Fragment {

    private int mColumnCount = 1;

    private int TIME_OUT_NO_PI = 20000; // 20 seconds
    private int TIME_OUT_NO_PI_SHORT = 100; // 100ms

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
        recyclerView = (RecyclerView) view.findViewById(R.id.list);
        emptyView = (TextView) view.findViewById(R.id.empty_view);
        if (mColumnCount <= 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        }
        setupProgressDialog();
        myAdapter = new SimpleItemRecyclerViewAdapter(piItems);
        recyclerView.setAdapter(myAdapter);
        setupPiDiscovery();

        return view;
    }

    private void setupProgressDialog() {
        pd = new ProgressDialog(getActivity(), R.style.CustomAlertDialogStyle);
        pd.setTitle("PGD");
        pd.setMessage("Searching Pi...");
        pd.setCancelable(false);
        pd.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (client != null) {
                    client.stopDiscovery();
                }
                pd.dismiss();
                hideShowNoPiFound(TIME_OUT_NO_PI_SHORT);
            }
        });
        pd.show();
    }

    private void setupPiDiscovery() {
        if (ConnectionHelper.isWiFiConnected(getActivity())) {
            pd.show();
            if (client != null) {
                client.stopDiscovery();
            }
            hideShowNoPiFound(TIME_OUT_NO_PI);
            discoverPis();
        } else {
            pd.dismiss();
            Toast.makeText(getActivity(), "Connect your device to the wifi network to discover the Pi and click the search button", Toast.LENGTH_LONG).show();
            ConnectionHelper.enableWifi(getActivity());
            hideShowNoPiFound(TIME_OUT_NO_PI_SHORT);
        }
    }


    private void discoverPis() {

        piItems = new ArrayList<PiItem>();
        client = SsdpClient.create();
        DiscoveryRequest networkStorageDevice = DiscoveryRequest.builder()
                .serviceType("urn:blocklink:pigrid:web:0")
                .build();
        client.discoverServices(networkStorageDevice, new DiscoveryListener() {
            @Override
            public void onServiceDiscovered(SsdpService service) {
                System.out.println("Found ip: " + service.getLocation());
                PiItem pi = new PiItem("1", service.getSerialNumber(), service.getServiceType(), service.getLocation());
                piItems.add(pi);

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        myAdapter.setData(piItems);
                        myAdapter.notifyDataSetChanged();
                        pd.dismiss();
                        hideShowNoPiFound(TIME_OUT_NO_PI_SHORT);
                    }
                });
            }

            @Override
            public void onFailed(Exception ex) {
                System.out.println("Failed service: " + ex);
                pd.dismiss();
                Toast.makeText(getActivity(), "Something went wrong when searching for the pi.", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onServiceAnnouncement(SsdpServiceAnnouncement announcement) {
                System.out.println("Service announced something: " + announcement);
            }
        });
    }

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

    private void clearPiListView() {
        piItems = null;
        myAdapter.setData(piItems);
        myAdapter.notifyDataSetChanged();
        hideShowNoPiFound(TIME_OUT_NO_PI_SHORT);
    }

    public void onResume() {
        super.onResume();
        // Set title bar
        ((MainActivity) getActivity()).setActionBarTitle("Pis");
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
                    Toast.makeText(getActivity(), "Device Wifi connection lost", Toast.LENGTH_LONG).show();
                    clearPiListView();
                    break;
                case ConnectionHelper.NETWORK_STATUS_WIFI:
                    if (!firstLoad) {
                        setupPiDiscovery();
                        Toast.makeText(getActivity(), "Device connected to the wifi network", Toast.LENGTH_LONG).show();
                    }
                    firstLoad = false;
                    break;
                case ConnectionHelper.NETWORK_STATUS_MOBILE:
                    Toast.makeText(getActivity(), "Device connected to the mobile network. Please connect to the wifi network to discover the Pi", Toast.LENGTH_LONG).show();
                    clearPiListView();
                    break;
            }
        }
    }

    ;
}

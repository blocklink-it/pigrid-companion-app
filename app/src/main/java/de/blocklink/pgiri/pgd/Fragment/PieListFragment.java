package de.blocklink.pgiri.pgd.Fragment;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
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

import de.blocklink.pgiri.pgd.Adapter.PieItem;
import de.blocklink.pgiri.pgd.Adapter.SimpleItemRecyclerViewAdapter;
import de.blocklink.pgiri.pgd.Helper.ConnectionHelper;
import de.blocklink.pgiri.pgd.PieListActivity;
import de.blocklink.pgiri.pgd.R;
import io.resourcepool.ssdp.client.SsdpClient;
import io.resourcepool.ssdp.model.DiscoveryListener;
import io.resourcepool.ssdp.model.DiscoveryRequest;
import io.resourcepool.ssdp.model.SsdpService;
import io.resourcepool.ssdp.model.SsdpServiceAnnouncement;


/**
 * A fragment representing a list of Items.
 */
public class PieListFragment extends Fragment {

    private int mColumnCount = 1;

    private int TIME_OUT_NO_PIE = 30000; // 0.5 minute
    private int TIME_OUT_NO_PIE_SHORT = 100; // 100ms

    List<PieItem> pieItems = null;
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
    public PieListFragment() {
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
        myAdapter = new SimpleItemRecyclerViewAdapter(pieItems);
        recyclerView.setAdapter(myAdapter);
        setupPieDiscovery();

        return view;
    }

    private void setupProgressDialog() {
        pd = new ProgressDialog(getActivity(), R.style.CustomAlertDialogStyle);
        pd.setTitle("PGD");
        pd.setMessage("Looking for Pie...");
        pd.setCancelable(false);
        pd.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (client != null) {
                    client.stopDiscovery();
                }
                pd.dismiss();
                hideShowNoPieFound(TIME_OUT_NO_PIE_SHORT);
            }
        });
        pd.show();
    }

    private void setupPieDiscovery() {
        if (ConnectionHelper.isWiFiConnected(getActivity())) {
            pd.show();
            if (client != null) {
                client.stopDiscovery();
            }
            hideShowNoPieFound(TIME_OUT_NO_PIE);
            discoverPies();
        } else {
            pd.dismiss();
            Toast.makeText(getActivity(), "Connect your device to the wifi network to discover the Pi and click the search button", Toast.LENGTH_LONG).show();
            ConnectionHelper.enableWifi(getActivity());
            hideShowNoPieFound(TIME_OUT_NO_PIE_SHORT);
        }
    }


    private void discoverPies() {

        pieItems = new ArrayList<PieItem>();
        client = SsdpClient.create();
        DiscoveryRequest networkStorageDevice = DiscoveryRequest.builder()
                .serviceType("urn:blocklink:pigrid:web:0")
                .build();
        client.discoverServices(networkStorageDevice, new DiscoveryListener() {
            @Override
            public void onServiceDiscovered(SsdpService service) {
                System.out.println("Found ip: " + service.getLocation());
                PieItem pie = new PieItem("1", service.getSerialNumber(), service.getServiceType(), service.getLocation());
                pieItems.add(pie);

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        myAdapter.setData(pieItems);
                        myAdapter.notifyDataSetChanged();
                        pd.dismiss();
                        hideShowNoPieFound(TIME_OUT_NO_PIE_SHORT);
                    }
                });
            }

            @Override
            public void onFailed(Exception ex) {
                System.out.println("Failed service: " + ex);
                pd.dismiss();
                Toast.makeText(getActivity(), "Something went wrong when searching for the pie.", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onServiceAnnouncement(SsdpServiceAnnouncement announcement) {
                System.out.println("Service announced something: " + announcement);
            }
        });
    }

    private void hideShowNoPieFound(int timeOut) {

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (pd.isShowing()) {
                    pd.dismiss();
                }
                if (pieItems == null || pieItems.isEmpty()) {
                    recyclerView.setVisibility(View.GONE);
                    emptyView.setVisibility(View.VISIBLE);
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    emptyView.setVisibility(View.GONE);
                }
            }
        }, timeOut);
    }

    private void clearPieListView() {
        pieItems = null;
        myAdapter.setData(pieItems);
        myAdapter.notifyDataSetChanged();
        hideShowNoPieFound(TIME_OUT_NO_PIE_SHORT);
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
                    clearPieListView();
                    break;
                case ConnectionHelper.NETWORK_STATUS_WIFI:
                    if (!firstLoad) {
                        setupPieDiscovery();
                        Toast.makeText(getActivity(), "Device connected to the wifi network", Toast.LENGTH_LONG).show();
                    }
                    firstLoad = false;
                    break;

                case ConnectionHelper.NETWORK_STATUS_MOBILE:
                    Toast.makeText(getActivity(), "Device connected to the mobile network. Please connect to the wifi network to discover the Pie", Toast.LENGTH_LONG).show();
                    clearPieListView();
                    break;
            }
        }
    }

    ;
}

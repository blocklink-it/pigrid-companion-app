package de.blocklink.pgiri.pgd.Fragment;

import android.app.ProgressDialog;
import android.content.Context;
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


    // TODO: Customize parameters
    private int mColumnCount = 1;

    private int TIME_OUT_NO_PIE = 60000;
    private int TIME_OUT_NO_PIE_SHORT = 100;

    List<PieItem> pieItems = null;
    SimpleItemRecyclerViewAdapter myAdapter;
    SsdpClient client;

    RecyclerView recyclerView;
    TextView emptyView;
    private ProgressDialog pd;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PieListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

        pd = ProgressDialog.show(context, "PGD", "Looking for pi..", true);

        myAdapter = new SimpleItemRecyclerViewAdapter(pieItems);
        recyclerView.setAdapter(myAdapter);
        setupPieDiscovery();

        return view;
    }

    private void setupPieDiscovery() {
        if (ConnectionHelper.isWiFiConnected(getActivity())) {
            if (client != null) {
                client.stopDiscovery();
            }
            displayNoPieFound(TIME_OUT_NO_PIE);
            discoverPies();
        } else {
            pd.dismiss();
            Toast.makeText(getActivity(), "Connect your device to the wifi network to discover the Pi and click the search button", Toast.LENGTH_LONG).show();
            ConnectionHelper.enableWifi(getActivity());
            displayNoPieFound(TIME_OUT_NO_PIE_SHORT);
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
                    }
                });
            }

            @Override
            public void onFailed(Exception ex) {
                System.out.println("Failed service: " + ex);
                pd.dismiss();
            }

            @Override
            public void onServiceAnnouncement(SsdpServiceAnnouncement announcement) {
                System.out.println("Service announced something: " + announcement);
            }
        });
    }

    private void displayNoPieFound(int timeOut) {

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (pd.isShowing()) {
                    pd.dismiss();
                }
                if (pieItems == null) {
                    recyclerView.setVisibility(View.GONE);
                    emptyView.setVisibility(View.VISIBLE);
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    emptyView.setVisibility(View.GONE);
                }
            }
        }, timeOut);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (client != null) {
            client.stopDiscovery();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (client != null) {
            client.stopDiscovery();
        }
    }

}

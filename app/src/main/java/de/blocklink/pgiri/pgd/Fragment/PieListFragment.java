package de.blocklink.pgiri.pgd.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import de.blocklink.pgiri.pgd.Adapter.PieItem;
import de.blocklink.pgiri.pgd.Adapter.SimpleItemRecyclerViewAdapter;
import de.blocklink.pgiri.pgd.Helper.ConnectionHelper;
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

    public static List<PieItem> pieItems;
    RecyclerView recyclerView;
    SimpleItemRecyclerViewAdapter myAdapter;
    SsdpClient client;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PieListFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static PieListFragment newInstance(int columnCount) {
        PieListFragment fragment = new PieListFragment();
        Bundle args = new Bundle();
        //args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pie_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }

            this.myAdapter = new SimpleItemRecyclerViewAdapter(this.pieItems);
            recyclerView.setAdapter(this.myAdapter);
            this.setupPiesDiscovery();
        }
        return view;
    }

    private void setupPiesDiscovery() {
        if (ConnectionHelper.isWiFiConnected(getActivity())) {
            if (client != null) {
                client.stopDiscovery();
            }
            this.discoverPies();
        } else {
            Toast.makeText(getActivity(), "Connect your device to the wifi network to discover the Pie", Toast.LENGTH_LONG).show();
        }
    }


    private void discoverPies() {

        this.pieItems = new ArrayList<PieItem>();
        client = SsdpClient.create();
        DiscoveryRequest networkStorageDevice = DiscoveryRequest.builder()
                .serviceType("urn:blocklink:pigrid:web:0")
                .build();
        client.discoverServices(networkStorageDevice, new DiscoveryListener() {
            @Override
            public void onServiceDiscovered(SsdpService service) {
                System.out.println("Found ip: " + service.getLocation());
                PieItem pie = new PieItem("1", service.getSerialNumber(), service.getServiceType(), service.getLocation());
                PieListFragment.pieItems.add(pie);
                myAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailed(Exception ex) {
                System.out.println("Failed service: " + ex);
            }

            @Override
            public void onServiceAnnouncement(SsdpServiceAnnouncement announcement) {
                System.out.println("Service announced something: " + announcement);
            }
        });
    }
}

package de.blocklink.pgiri.pgd;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Toast;


import de.blocklink.pgiri.pgd.Adapter.PieItem;
import de.blocklink.pgiri.pgd.Adapter.SimpleItemRecyclerViewAdapter;
import de.blocklink.pgiri.pgd.Helper.ConnectionHelper;
import io.resourcepool.ssdp.client.SsdpClient;
import io.resourcepool.ssdp.model.DiscoveryListener;
import io.resourcepool.ssdp.model.DiscoveryRequest;
import io.resourcepool.ssdp.model.SsdpService;
import io.resourcepool.ssdp.model.SsdpServiceAnnouncement;

import java.util.ArrayList;
import java.util.List;

/**
 * An activity representing a list of Pies. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link PieDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class PieListActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    public static List<PieItem> pieItems;
    RecyclerView recyclerView;
    SimpleItemRecyclerViewAdapter myAdapter;
    SsdpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pie_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setupPiesDiscovery();
            }
        });

        this.recyclerView = findViewById(R.id.pie_list);
        assert this.recyclerView != null;
        //this.myAdapter = new SimpleItemRecyclerViewAdapter(this, this.pieItems);
        recyclerView.setAdapter(this.myAdapter);
        this.setupPiesDiscovery();
    }

    private void setupPiesDiscovery() {
        if (ConnectionHelper.isWiFiConnected(PieListActivity.this)) {
            if (client != null) {
                client.stopDiscovery();
            }
            this.discoverPies();
        } else {
            Toast.makeText(getApplicationContext(), "Connect your device to the wifi network to discover the Pie", Toast.LENGTH_LONG).show();
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
                PieListActivity.pieItems.add(pie);
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

    /*private void setupRecyclerView() {

        myAdapter = new SimpleItemRecyclerViewAdapter(this, this.pieItems);
        myAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(myAdapter);
    }
*/    @Override
    protected void onPause() {
        super.onPause();
        client.stopDiscovery();
    }
}

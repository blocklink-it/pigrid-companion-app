package de.blocklink.pgiri.pgd.Fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import de.blocklink.pgiri.pgd.Helper.PrefManager;
import de.blocklink.pgiri.pgd.R;

public class SettingFragment extends Fragment {


    public SettingFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_setting, container, false);

        Button button = (Button) view.findViewById(R.id.btnReset);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                PrefManager prefManager = new PrefManager(getActivity().getBaseContext());
                prefManager.clearPref();
                Intent intent = getActivity().getIntent();
                startActivity(intent);
                Toast.makeText(getActivity().getBaseContext(), "Your application has been Reset ! ", Toast.LENGTH_SHORT).show();
            }
        });

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

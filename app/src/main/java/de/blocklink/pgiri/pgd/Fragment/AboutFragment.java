package de.blocklink.pgiri.pgd.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.blocklink.pgiri.pgd.MainActivity;
import de.blocklink.pgiri.pgd.R;

public class AboutFragment extends Fragment {

    public AboutFragment() {
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
        return inflater.inflate(R.layout.fragment_about, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    public void onResume(){
        super.onResume();
        // Set title bar
        ((MainActivity) getActivity()).setActionBarTitle("About App");
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}

package de.blocklink.pigrid.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputFilter;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import de.blocklink.pigrid.FullscreenActivity;
import de.blocklink.pigrid.Helper.Helper;
import de.blocklink.pigrid.Helper.ValidIPAddressInputFilter;
import de.blocklink.pigrid.MainActivity;
import de.blocklink.pigrid.R;

public class ManualPiSearchFragment extends Fragment {

    private EditText etIP;
    Button btnManualSearch;


    public ManualPiSearchFragment() {
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
        View view = inflater.inflate(R.layout.fragment_manual_pi_search, container, false);
        setupUI(view.findViewById(R.id.mainContainer));
        etIP = view.findViewById(R.id.etManualIP);
        etIP.setFilters(new InputFilter[]{new ValidIPAddressInputFilter()});
        btnManualSearch = view.findViewById(R.id.btnManualSearch);
        btnManualSearch.setOnClickListener(mSearchOnClickListener);

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

    public void onResume() {
        super.onResume();
        // Set title bar
        ((MainActivity) getActivity()).setActionBarTitle(getString(R.string.manual_search)); // setup Action bar title
    }

    private final View.OnClickListener mSearchOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String ip = etIP.getText().toString();
            if (Helper.isEmpty(etIP)) {
                Toast.makeText(getActivity(), getString(R.string.manual_search_vmsg1), Toast.LENGTH_LONG).show();
            } else if (!Patterns.IP_ADDRESS.matcher(ip).matches()) {
                Toast.makeText(getActivity(), getString(R.string.manual_search_vmsg2), Toast.LENGTH_LONG).show();
            } else {
                Intent intent = new Intent(getActivity(), FullscreenActivity.class);
                intent.putExtra(FullscreenActivity.URL, "http://" + ip);
                startActivity(intent);
            }

        }
    };

    public void setupUI(View view) {

        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    Helper.hideSoftKeyboard(getActivity());
                    return false;
                }
            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
    }

}

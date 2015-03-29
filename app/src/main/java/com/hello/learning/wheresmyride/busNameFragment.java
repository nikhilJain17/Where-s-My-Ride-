package com.hello.learning.wheresmyride;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

/**
 * Created by test on 3/29/15.
 */
public class busNameFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog (final Bundle savedInstanceState) {

        final AlertDialog.Builder myBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        final View view = inflater.inflate(R.layout.bus_name_fragment, null);

        myBuilder.setView(view);

        myBuilder.setTitle("Create Bus");

        myBuilder.setPositiveButton("OK", null);

        // Do some backend here...
        // Store bus name, etc.

        return myBuilder.create();


    }

}

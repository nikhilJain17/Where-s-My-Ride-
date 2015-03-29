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
public class SearchForBus extends DialogFragment {

    @Override
    public Dialog onCreateDialog (final Bundle savedInstanceState) {

        AlertDialog.Builder myBuilder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();

        final View view = inflater.inflate(R.layout.search_for_bus, null);

        myBuilder.setView(view);

        return myBuilder.create();




        

    }

}

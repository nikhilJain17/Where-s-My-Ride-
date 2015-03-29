package com.hello.learning.wheresmyride;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

/**
 * Created by test on 3/29/15.
 */
public class addContactFragment extends DialogFragment {


    String streetaddress;
    String citystate;
    String zipcode;

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {

        final String SHAREDPREFSFILENAME = "mapPrefs";

        SharedPreferences mapPreferences = getActivity().getSharedPreferences(SHAREDPREFSFILENAME, 0);
        final SharedPreferences.Editor editor = mapPreferences.edit();

        /////////////////////////////////////////////////////////////
        final AlertDialog.Builder myBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        final View view = inflater.inflate(R.layout.add_contact_layout, null);
        myBuilder.setView(view);

        myBuilder.setTitle("New Address");
        myBuilder.setMessage("");
        myBuilder.setPositiveButton("Plot", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                EditText streetAddress = (EditText) view.findViewById(R.id.streetAddress);
                EditText cityState = (EditText) view.findViewById(R.id.city);
                EditText zipCode = (EditText) view.findViewById(R.id.zipCode);

//                                String streetaddress;
//                                String citystate;
//                                String zipcode;

                streetaddress = streetAddress.getText().toString();
                citystate = cityState.getText().toString();
                zipcode = zipCode.getText().toString();

                editor.putString("zip", zipcode);
                editor.putString("street", streetaddress);
                editor.putString("city", citystate);
                editor.putBoolean("clicked", true);
                editor.apply();


            }
        });

        return myBuilder.create();

    }
}

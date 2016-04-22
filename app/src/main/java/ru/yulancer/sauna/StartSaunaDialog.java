package ru.yulancer.sauna;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;

public class StartSaunaDialog extends DialogFragment implements DialogInterface.OnClickListener {

    private OnFragmentInteractionListener mListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View form = getActivity().getLayoutInflater().inflate(R.layout.activity_start_sauna_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        Dialog dialog = builder.setTitle("Заданные температуры").setView(form)
                .setPositiveButton(android.R.string.ok, this)
                .setNegativeButton(android.R.string.cancel, null).create();
        return dialog;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        mListener.onStartSauna(15);
    }

    public interface OnFragmentInteractionListener {
        void onStartSauna(long startDelay);
    }
}

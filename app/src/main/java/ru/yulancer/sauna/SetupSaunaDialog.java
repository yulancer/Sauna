package ru.yulancer.sauna;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.CheckBox;

public class SetupSaunaDialog extends DialogFragment implements DialogInterface.OnClickListener {

    private static final String ARG_PARAM = "setup_params";
    private SaunaSetupData mSaunaSetupData;
    CheckBox mcbReboot;

    private OnFragmentInteractionListener mListener;

    public static SetupSaunaDialog newInstance(SaunaSetupData setupData) {
        SetupSaunaDialog fragment = new SetupSaunaDialog();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PARAM, setupData);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            mSaunaSetupData = args.getParcelable(ARG_PARAM);
        }

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View form = getActivity().getLayoutInflater().inflate(R.layout.activity_setup_sauna_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        mcbReboot = (CheckBox) form.findViewById(R.id.cbReboot);

                Dialog dialog = builder.setTitle("Настройки системы").setView(form)
                .setPositiveButton(android.R.string.ok, this)
                .setNegativeButton(android.R.string.cancel, null).create();

        return dialog;
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
            mListener.setSetupSaunaDialog(this);
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener.setSetupSaunaDialog(null);
        mListener = null;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (mcbReboot != null)
            mSaunaSetupData.DoReboot = mcbReboot.isChecked();
        mListener.onSetupSauna(mSaunaSetupData);
    }

    public interface OnFragmentInteractionListener {
        void onSetupSauna(SaunaSetupData setupData);
        void setSetupSaunaDialog(SetupSaunaDialog setupSaunaDialog);
    }
}

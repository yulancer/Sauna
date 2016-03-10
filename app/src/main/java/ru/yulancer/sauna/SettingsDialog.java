package ru.yulancer.sauna;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.EditText;

public class SettingsDialog extends DialogFragment implements DialogInterface.OnClickListener {
    // the fragment initialization parameters, exception.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM = "settings";
    private View form = null;
    private SaunaSettings mSaunaSettings;

    private OnFragmentInteractionListener mListener;

    public SettingsDialog() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SettingsDialog.
     */
    public static SettingsDialog newInstance(SaunaSettings saunaSettings) {
        SettingsDialog fragment = new SettingsDialog();
        Bundle args = new Bundle();
        args.putFloatArray(ARG_PARAM, new float[]{saunaSettings.SaunaSetpoint, saunaSettings.BoilerSetpoint, saunaSettings.RoomSetpoint});
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            float[] data = args.getFloatArray(ARG_PARAM);
            if (data != null && data.length == 3)
                mSaunaSettings = new SaunaSettings(data[0], data[1], data[2]);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        form = getActivity().getLayoutInflater().inflate(R.layout.fragment_settings, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        Dialog dialog = builder.setTitle("Заданные температуры").setView(form)
                .setPositiveButton(android.R.string.ok, this)
                .setNegativeButton(android.R.string.cancel, null).create();

        EditText etSauna = (EditText) form.findViewById(R.id.etSauna);
        etSauna.setText(String.format("%.2f", mSaunaSettings.SaunaSetpoint));
        EditText etBoiler = (EditText) form.findViewById(R.id.etBoiler);
        etBoiler.setText(String.format("%.2f", mSaunaSettings.BoilerSetpoint));
        EditText etRoom = (EditText) form.findViewById(R.id.etRoom);
        etRoom.setText(String.format("%.2f", mSaunaSettings.RoomSetpoint));

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
        EditText etSauna = (EditText) form.findViewById(R.id.etSauna);
        EditText etBoiler = (EditText) form.findViewById(R.id.etBoiler);
        EditText etRoom = (EditText) form.findViewById(R.id.etRoom);
        try {
            float tempSauna = Float.parseFloat(etSauna.getText().toString());
            float tempBoiler = Float.parseFloat(etBoiler.getText().toString());
            float tempRoom = Float.parseFloat(etRoom.getText().toString());
            mSaunaSettings = new SaunaSettings(tempSauna, tempBoiler, tempRoom);
            mListener.onSaveSettings(mSaunaSettings);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onSaveSettings(SaunaSettings saunaSettings);
    }

}

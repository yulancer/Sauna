package ru.yulancer.sauna;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

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
        form = getActivity().getLayoutInflater().inflate(R.layout.fragment_settings_slider, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        Dialog dialog = builder.setTitle("Заданные температуры").setView(form)
                .setPositiveButton(android.R.string.ok, this)
                .setNegativeButton(android.R.string.cancel, null).create();

        SeekBar sbSauna = (SeekBar) form.findViewById(R.id.sbSauna);
        final TextView tvSauna = (TextView) form.findViewById(R.id.tvSetpointSaunaDialog);
        if (sbSauna != null){
            sbSauna.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (tvSauna != null)
                        tvSauna.setText(String.format("%d", progress));
                    mSaunaSettings.SaunaSetpoint = progress;
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
            sbSauna.setProgress((int) mSaunaSettings.SaunaSetpoint);
        }

        SeekBar sbBoiler = (SeekBar) form.findViewById(R.id.sbBoiler);
        final TextView tvBoiler = (TextView) form.findViewById(R.id.tvSetpointBoilerDialog);
        if (sbBoiler != null){
            sbBoiler.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (tvBoiler != null)
                        tvBoiler.setText(String.format("%d", progress));
                    mSaunaSettings.BoilerSetpoint = progress;
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
            sbBoiler.setProgress((int) mSaunaSettings.BoilerSetpoint);
        }

        SeekBar sbRoom = (SeekBar) form.findViewById(R.id.sbRoom);
        final TextView tvRoom = (TextView) form.findViewById(R.id.tvSetpointRoomDialog);
        if (sbRoom != null){
            sbRoom.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (tvRoom != null)
                        tvRoom.setText(String.format("%d", progress));
                    mSaunaSettings.RoomSetpoint = progress;
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
            sbRoom.setProgress((int) mSaunaSettings.RoomSetpoint);
        }
//        EditText etSauna = (EditText) form.findViewById(R.id.etSauna);
//        etSauna.setText(String.format("%.2f", mSaunaSettings.SaunaSetpoint));
//        EditText etBoiler = (EditText) form.findViewById(R.id.etBoiler);
//        etBoiler.setText(String.format("%.2f", mSaunaSettings.BoilerSetpoint));
//        EditText etRoom = (EditText) form.findViewById(R.id.etRoom);
//        etRoom.setText(String.format("%.2f", mSaunaSettings.RoomSetpoint));

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

    private Float ParseLocal(String strValue) throws ParseException {
        Locale currentLocale = getResources().getConfiguration().locale;
        NumberFormat nf = NumberFormat.getInstance(currentLocale);
        Number parsedNumber = nf.parse(strValue);
        return parsedNumber.floatValue();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        mListener.onSaveSettings(mSaunaSettings);
//        EditText etSauna = (EditText) form.findViewById(R.id.etSauna);
//        EditText etBoiler = (EditText) form.findViewById(R.id.etBoiler);
//        EditText etRoom = (EditText) form.findViewById(R.id.etRoom);
//        try {
//            float tempSauna = ParseLocal(etSauna.getText().toString());
//            float tempBoiler = ParseLocal(etBoiler.getText().toString());
//            float tempRoom = ParseLocal(etRoom.getText().toString());
//            mSaunaSettings = new SaunaSettings(tempSauna, tempBoiler, tempRoom);
//            mListener.onSaveSettings(mSaunaSettings);
//        } catch (Exception e) {
//            TextView tvException = (TextView) getActivity().findViewById(R.id.tvException);
//            tvException.setText(e.toString());
//            e.printStackTrace();
//        }
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

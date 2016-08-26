package com.example.carlrygart.schoolify;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.vision.text.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FilterDialogFragment extends DialogFragment {

    public static String LOG_TAG = "DIALOGFRAG";

    protected List<String> selectedPrograms;
    protected int chosenDistance;

    public interface FilterDialogListener {
        void onFinishDialog(List<String> selectedPrograms, int chosenDistance);
    }

    public static FilterDialogFragment newInstance(List<String> selectedPrograms, int chosenDistance) {
        FilterDialogFragment frag = new FilterDialogFragment();
        Bundle args = new Bundle();
        args.putStringArrayList("selectedPrograms", (ArrayList<String>) selectedPrograms);
        args.putInt("chosenDistance", chosenDistance);
        frag.setArguments(args);
        return frag;
    }

    /** The system calls this to get the DialogFragment's layout, regardless
     of whether it's being displayed as a dialog or an embedded fragment. */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout to use as dialog or embedded fragment
        final View view = inflater.inflate(R.layout.filter_dialog_content, container, false);

        selectedPrograms = getArguments().getStringArrayList("selectedPrograms");
        Log.d(LOG_TAG, String.valueOf(selectedPrograms.size()) + " - " + selectedPrograms.toString());
        chosenDistance = getArguments().getInt("chosenDistance");

        final TextView maxDistanceValue = (TextView) view.findViewById(R.id.max_distance_value);
        String distText = chosenDistance + " km";
        if (chosenDistance == 20) distText = ">20 km";
        maxDistanceValue.setText(distText);
        SeekBar maxDistance = (SeekBar) view.findViewById(R.id.max_distance_seek_bar);
        maxDistance.setProgress(chosenDistance);

        maxDistance.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                chosenDistance = i;
                String text = i + " km";
                if (i == 20) text = ">20 km";
                maxDistanceValue.setText(text);
                Log.d(LOG_TAG, "Chosen distance: " + String.valueOf(chosenDistance));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.program_list);
        setupRecyclerView(recyclerView);

        Button okButton = (Button) view.findViewById(R.id.ok_button);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FilterDialogListener activity = (FilterDialogListener) getActivity();
                activity.onFinishDialog(selectedPrograms, chosenDistance);
                dismiss();
            }
        });

        Button cancelButton = (Button) view.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        return view;
    }

    /** The system calls this only when creating the layout in a dialog. */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // The only reason you might override this method when using onCreateView() is
        // to modify any dialog characteristics. For example, the dialog includes a
        // title by default, but your custom layout might not need it. So here you can
        // remove the dialog title, but you must call the superclass to get the Dialog.
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        //dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setTitle("Filter");
        return dialog;
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(Schoolify.availablePrograms));
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<String> mValues;

        public SimpleItemRecyclerViewAdapter(List<String> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.program_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
            holder.mProgramNameView.setText(holder.mItem);

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (holder.mProgramNameView.isChecked()) {
                        holder.mProgramNameView.setChecked(false);
                    } else {
                        holder.mProgramNameView.setChecked(true);
                    }
                }
            });

            holder.mProgramNameView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b && !selectedPrograms.contains(holder.mItem)) {
                        selectedPrograms.add(holder.mItem);
                        Log.d("ADDED:", holder.mItem);
                    }
                    if (!b) {
                        selectedPrograms.remove(holder.mItem);
                        Log.d("REMOVED:", holder.mItem);
                    }
                    Log.d(LOG_TAG, String.valueOf(selectedPrograms.size()) + " - " + selectedPrograms.toString());
                    Log.d(LOG_TAG, "-------------------------");
                }
            });

            if (selectedPrograms.contains(holder.mItem)) {
                holder.mProgramNameView.setChecked(true);
                Log.d(LOG_TAG, "Checked: " + holder.mItem);
            } else {
                holder.mProgramNameView.setChecked(false);
                Log.d(LOG_TAG, "NOT Checked: " + holder.mItem);
            }
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final CheckBox mProgramNameView;
            public String mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mProgramNameView = (CheckBox) view.findViewById(R.id.program_name);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mProgramNameView.getText() + "'";
            }
        }
    }
}

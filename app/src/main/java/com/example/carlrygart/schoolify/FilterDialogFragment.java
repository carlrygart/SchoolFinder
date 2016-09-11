package com.example.carlrygart.schoolify;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment class managing the filter dialog, where the user can choose programs and
 * distance to schools.
 */
public class FilterDialogFragment extends DialogFragment {

    public static final String LOG_TAG = "DIALOGFRAG";

    protected List<String> selectedPrograms;
    protected int chosenDistance;

    /**
     * Interface managing the listener used when the user closes the dialog. SchoolListActivity is
     * implementing this interface.
     */
    public interface FilterDialogListener {
        void onFinishDialog(List<String> selectedPrograms, int chosenDistance);
    }

    /**
     * Used instead of constructor to be able to use arguments with the dialog.
     * @param selectedPrograms List of the last known selected programs.
     * @param chosenDistance Integer of the last known distance chosen.
     * @return The fragment object.
     */
    public static FilterDialogFragment newInstance(List<String> selectedPrograms, int chosenDistance) {
        FilterDialogFragment frag = new FilterDialogFragment();
        Bundle args = new Bundle();
        args.putStringArrayList("selectedPrograms", (ArrayList<String>) selectedPrograms);
        args.putInt("chosenDistance", chosenDistance);
        frag.setArguments(args);
        return frag;
    }

    /**
     * The system calls this to get the DialogFragment's layout, regardless
     * of whether it's being displayed as a dialog or an embedded fragment.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout to use as dialog or embedded fragment
        final View view = inflater.inflate(R.layout.filter_dialog_content, container, false);

        // Get the argument parameters.
        selectedPrograms = getArguments().getStringArrayList("selectedPrograms");
        chosenDistance = getArguments().getInt("chosenDistance");
        Log.d(LOG_TAG, String.valueOf(selectedPrograms.size()) + " - " + selectedPrograms.toString());

        // Sets the chosen distance text to TextView and the SeekBar.
        final TextView maxDistanceValue = (TextView) view.findViewById(R.id.max_distance_value);
        String distText = chosenDistance + " km";
        if (chosenDistance == 20) distText = ">20 km";
        maxDistanceValue.setText(distText);

        // Sets chosen distance to SeekBar and adds a on change listener.
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

        // Sets the recycler view with the offered programs.
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.program_list);
        setupRecyclerView(recyclerView);

        // Adds a listener to the OK-button.
        Button okButton = (Button) view.findViewById(R.id.ok_button);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FilterDialogListener activity = (FilterDialogListener) getActivity();
                activity.onFinishDialog(selectedPrograms, chosenDistance);
                dismiss();
            }
        });

        // Adds listener to Cancel-button.
        Button cancelButton = (Button) view.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        return view;
    }

    /**
     * The system calls this when creating the layout in a dialog. Adds the title to the dialog.
     * */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setTitle("Filter");
        return dialog;
    }

    /**
     * Sets up the recycler view.
     * @param recyclerView The view to be filled.
     */
    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(new SimpleProgramRecyclerViewAdapter(Schoolify.getAvailablePrograms()));
    }

    /**
     * Class used for binding the items (program represented as a String) to
     * holders in the RecyclerView.
     */
    public class SimpleProgramRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleProgramRecyclerViewAdapter.ProgramViewHolder> {

        private final List<String> mValues;

        public SimpleProgramRecyclerViewAdapter(List<String> items) {
            mValues = items;
        }

        /**
         * Inflates the right layout.
         */
        @Override
        public ProgramViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.program_list_content, parent, false);
            return new ProgramViewHolder(view);
        }

        /**
         * Method setting up the features for each holder in the school list.
         */
        @Override
        public void onBindViewHolder(final ProgramViewHolder holder, int position) {
            holder.mProgramName = mValues.get(position);
            holder.mProgramNameView.setText(holder.mProgramName);

            // Set listener for when the users press the holder.
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

            // Set listener for select or deselect the checkbox in the holder.
            // If the selected program is not in the selected programs list, it will get added.
            // If the selected programs list already contain the selected program, it will not
            // be added again.
            holder.mProgramNameView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b && !selectedPrograms.contains(holder.mProgramName)) {
                        selectedPrograms.add(holder.mProgramName);
                        Log.d("ADDED:", holder.mProgramName);
                    }
                    if (!b) {
                        selectedPrograms.remove(holder.mProgramName);
                        Log.d("REMOVED:", holder.mProgramName);
                    }
                    Log.d(LOG_TAG, String.valueOf(selectedPrograms.size()) + " - " + selectedPrograms.toString());
                    Log.d(LOG_TAG, "-------------------------");
                }
            });

            // Checks if the program name is in the list of selected programs, and in that case
            // sets the checkbox to true. This code is needed because the holder is bind again,
            // every time it is viewed in the scroll list.
            if (selectedPrograms.contains(holder.mProgramName)) {
                holder.mProgramNameView.setChecked(true);
                Log.d(LOG_TAG, "Checked: " + holder.mProgramName);
            } else {
                holder.mProgramNameView.setChecked(false);
                Log.d(LOG_TAG, "NOT Checked: " + holder.mProgramName);
            }
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        /**
         * Nested class for each program in the RecyclerView.
         */
        public class ProgramViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public String mProgramName;
            public final CheckBox mProgramNameView;

            public ProgramViewHolder(View view) {
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

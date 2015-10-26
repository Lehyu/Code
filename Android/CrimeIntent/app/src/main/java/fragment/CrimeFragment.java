package fragment;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.crimeintent.R;

import java.util.UUID;
import java.util.Date;

import model.Crime;
import model.CrimeLab;

/**
 * Created by lhy on 10/21/15.
 */
public class CrimeFragment extends Fragment {
    private static final String TAG = "CrimeFragment";
    public static final String EXTRA_CRIME = "com.crimeintent.crime.uuid";
    public static final int CRIME_FRAGMENT_CODE = 0;
    public static final String DATE_DIALOG = "date";
    private Crime crime;
    private EditText crimeTitle;
    private Button crimeDetails;
    private CheckBox crimeSolved;

    public static CrimeFragment newInstance(UUID uuid) {

        
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_CRIME, uuid);
        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);
        //Log.v(TAG, uuid == null ? "NULL":"Not NULL");
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Log.v(TAG, "I'm here");
        setHasOptionsMenu(true);
        UUID  uuid = (UUID) getArguments().getSerializable(EXTRA_CRIME);
        crime = CrimeLab.get(getActivity()).getCrime(uuid);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime_list,menu);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != Activity.RESULT_OK) return;
        if(requestCode == CRIME_FRAGMENT_CODE){
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            crime.setCrimeDate(date);
            crimeDetails.setText(crime.getCrimeDate().toString());
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        super.onCreateView(inflater, parent, savedInstanceState);
        View v = inflater.inflate(R.layout.frament_crime, parent, false);
        crimeTitle = (EditText) v.findViewById(R.id.crime_title);
        crimeTitle.setText(crime.getTitle());
        crimeDetails = (Button) v.findViewById(R.id.crime_date);
        crimeDetails.setText(android.text.format.DateFormat.format("E,M d, y", crime.getCrimeDate()));

        crimeDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance(crime.getCrimeDate());
                dialog.setTargetFragment(CrimeFragment.this, CRIME_FRAGMENT_CODE);
                dialog.show(fm, DATE_DIALOG);
            }
        });

        crimeSolved = (CheckBox) v.findViewById(R.id.crime_solved);
        crimeSolved.setChecked(crime.isSolved());
        crimeSolved.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                crime.setIsSolved(isChecked);
            }
        });
        crimeTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //if(!s.toString().equals(crime.getTitle()))
                  //  crimeTitle.setText(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        return  v;
    }
}

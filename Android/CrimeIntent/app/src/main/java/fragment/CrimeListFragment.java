package fragment;

import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.Activity.CrimePagerActivity;
import com.crimeintent.R;

import java.util.ArrayList;

import model.Crime;
import model.CrimeLab;

/**
 * Created by lhy on 10/21/15.
 */
public class CrimeListFragment extends ListFragment{
    private static final String TAG = "CrimeListFragment";
    private ArrayList<Crime> crimeList;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        getActivity().setTitle(R.string.crime_title);
        crimeList = CrimeLab.get(getActivity()).getCrimeList();

        CrimeAdapter adapter = new CrimeAdapter(crimeList);
        setListAdapter(adapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime_list, menu);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Crime crime = (Crime) getListAdapter().getItem(position);

        Intent intent = new Intent(getActivity(), CrimePagerActivity.class);
        intent.putExtra(CrimeFragment.EXTRA_CRIME, crime.getCrimeId());

        Log.v(TAG, "clicked");
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((CrimeAdapter)getListAdapter()).notifyDataSetChanged();
    }

    private class CrimeAdapter extends ArrayAdapter<Crime>{


        public CrimeAdapter(ArrayList<Crime> crimeList) {
            super(getActivity(), 0, crimeList);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null){
                convertView = getActivity().getLayoutInflater().
                        inflate(R.layout.list_item_crime, null);
            }

            Crime crime = getItem(position);
            TextView crime_list_item_title = (TextView) convertView.findViewById(R.id.crime_list_item_title);
            crime_list_item_title.setText(crime.getTitle());
            CheckBox crime_list_item_solved = (CheckBox) convertView.findViewById(R.id.crime_list_item_solved);
            crime_list_item_solved.setChecked(crime.isSolved());
            TextView crime_list_item_date = (TextView) convertView.findViewById(R.id.crime_list_item_date);
            crime_list_item_date.setText(crime.getCrimeDate().toString());

            return convertView;
        }
    }
}

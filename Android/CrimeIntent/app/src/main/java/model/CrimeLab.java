package model;

import android.content.Context;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by lhy on 10/21/15.
 */
public class CrimeLab {
    private ArrayList<Crime> crimeList;
    private static CrimeLab sCrimeLab;
    private Context crimeLabContext;


    private CrimeLab(Context crimeLabContext) {
        this.crimeLabContext = crimeLabContext;
        crimeList = new ArrayList<Crime>();
        for(int i = 0; i < 100; i++){
            Crime crime = new Crime();
            crime.setTitle("Crime #" + i);
            crime.setIsSolved(0 == i % 2);
            crimeList.add(crime);
        }
    }

    public static CrimeLab get(Context appContext){
        if(sCrimeLab == null){
            sCrimeLab = new CrimeLab(appContext);
        }
        return sCrimeLab;
    }


    public ArrayList<Crime> getCrimeList() {
        return crimeList;
    }

    public Crime getCrime(UUID uuid){
        for (Crime crime: crimeList){
            if(uuid.equals(crime.getCrimeId())){
                return crime;
            }
        }
        return  null;
    }

    public void addCrime(Crime crime){
        crimeList.add(crime);
    }

}

package model;

import java.util.Date;
import java.util.Random;
import java.util.UUID;

/**
 * Created by lhy on 10/21/15.
 */
public class Crime {
    private UUID crimeId;
    private String title;
    private Date crimeDate;
    private boolean isSolved;

    public void setTitle(String title) {
        this.title = title;
    }

    public UUID getCrimeId() {

        return crimeId;
    }

    public String getTitle() {
        return title;
    }

    public Crime() {
        crimeId = UUID.randomUUID();
        crimeDate = new Date();
    }

    public Date getCrimeDate() {
        return crimeDate;
    }

    public void setCrimeDate(Date crimeDate) {
        this.crimeDate = crimeDate;
    }

    public boolean isSolved() {
        return isSolved;
    }

    public void setIsSolved(boolean isSolved) {
        this.isSolved = isSolved;
    }

    @Override
    public String toString() {
        return title.toString();
    }
}

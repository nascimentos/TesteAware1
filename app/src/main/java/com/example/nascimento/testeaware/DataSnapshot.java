package com.example.nascimento.testeaware;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by nascimento on 13/02/17.
 */

public class DataSnapshot implements Parcelable {
    private Date startDate;
    private Date stopDate;
    private Location location;
    private Double radius;
    private Integer activity;
    private Integer headphoneState;
    private Integer condition;
    private Integer contextVerificationFrequencyInMilliseconds;
    private int numberOfContexts;
    private int[] contexts;
    public static final int AND = 0;
    public static final int OR = 1;

    public DataSnapshot() {
        this.condition = AND;
        this.contexts = new int[4];
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getStopDate() {
        return stopDate;
    }

    public void setDateInterval(Date startDate, Date stopDate) {
        this.startDate = startDate;
        this.stopDate = stopDate;
        setNumberOfContexts();
    }

    public Location getLocation() {
        return location;
    }

    public Double getRadius() {
        return radius;
    }

    public void setLocationAndRadius(Location location, Double radius) {
        this.location = location;
        this.radius = radius;
        setNumberOfContexts();
    }

    public Integer getActivity() {
        return activity;
    }

    public void setActivity(int activity) {
        this.activity = activity;
        setNumberOfContexts();
    }

    public Integer getHeadphoneState() {
        return headphoneState;
    }

    public void setHeadphoneState(int headphoneState) {
        this.headphoneState = headphoneState;
        setNumberOfContexts();
    }

    public Integer getCondition() {
        return condition;
    }

    public void setCondition(int condition) {
        this.condition = condition;
    }

    public Integer getContextVerificationFrequencyInMilliseconds() {
        return contextVerificationFrequencyInMilliseconds;
    }

    public void setContextVerificationFrequencyInMilliseconds(int contextVerificationFrequencyInMilliseconds) {
        this.contextVerificationFrequencyInMilliseconds = contextVerificationFrequencyInMilliseconds;
    }

    public int getNumberOfInitializedContexts() {
        return numberOfContexts;
    }

    private void setNumberOfContexts() {
        numberOfContexts = 0;

        if (startDate != null) {
            contexts[0] = 1;
        } else {
            contexts[0] = 0;
        }

        if (location != null) {
            contexts[1] = 1;
        } else {
            contexts[1] = 0;
        }

        if (activity != null) {
            contexts[2] = 1;
        } else {
            contexts[2] = 0;
        }

        if (headphoneState != null) {
            contexts[3] = 1;
        } else {
            contexts[3] = 0;
        }

        for (int i : contexts) {
            if (i == 1) {
                numberOfContexts++;
            }
        }
    }

    protected DataSnapshot(Parcel in) {
        long tmpStartDate = in.readLong();
        startDate = tmpStartDate != -1 ? new Date(tmpStartDate) : null;
        long tmpStopDate = in.readLong();
        stopDate = tmpStopDate != -1 ? new Date(tmpStopDate) : null;
        location = (Location) in.readValue(Location.class.getClassLoader());
        radius = in.readByte() == 0x00 ? null : in.readDouble();
        activity = in.readByte() == 0x00 ? null : in.readInt();
        headphoneState = in.readByte() == 0x00 ? null : in.readInt();
        condition = in.readByte() == 0x00 ? null : in.readInt();
        contextVerificationFrequencyInMilliseconds = in.readByte() == 0x00 ? null : in.readInt();
        numberOfContexts = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(startDate != null ? startDate.getTime() : -1L);
        dest.writeLong(stopDate != null ? stopDate.getTime() : -1L);
        dest.writeValue(location);
        if (radius == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeDouble(radius);
        }
        if (activity == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeInt(activity);
        }
        if (headphoneState == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeInt(headphoneState);
        }
        if (condition == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeInt(condition);
        }
        if (contextVerificationFrequencyInMilliseconds == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeInt(contextVerificationFrequencyInMilliseconds);
        }
        dest.writeInt(numberOfContexts);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<DataSnapshot> CREATOR = new Parcelable.Creator<DataSnapshot>() {
        @Override
        public DataSnapshot createFromParcel(Parcel in) {
            return new DataSnapshot(in);
        }

        @Override
        public DataSnapshot[] newArray(int size) {
            return new DataSnapshot[size];
        }
    };
}
/*public class DataSnapshot implements Serializable {
    private Date startDate;
    private Date stopDate;
    private Location location;
    private Double radius;
    private Integer activity;
    private Integer headphoneState;
    private Integer condition;
    private Integer contextVerificationFrequencyInMilliseconds;
    private int numberOfContexts;
    private int[] contexts;
    public static final int AND = 0;
    public static final int OR = 1;

    public DataSnapshot() {
        this.condition = AND;
        this.contexts = new int[4];
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getStopDate() {
        return stopDate;
    }

    public void setDateInterval(Date startDate, Date stopDate) {
        this.startDate = startDate;
        this.stopDate = stopDate;
        setNumberOfContexts();
    }

    public Location getLocation() {
        return location;
    }

    public void setLocationAndRadius(Location location) {
        this.location = location;
        setNumberOfContexts();
    }

    public Double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public Integer getActivity() {
        return activity;
    }

    public void setActivity(int activity) {
        this.activity = activity;
        setNumberOfContexts();
    }

    public Integer getHeadphoneState() {
        return headphoneState;
    }

    public void setHeadphoneState(int headphoneState) {
        this.headphoneState = headphoneState;
        setNumberOfContexts();
    }

    public Integer getCondition() {
        return condition;
    }

    public void setCondition(int condition) {
        this.condition = condition;
    }

    public Integer getContextVerificationFrequencyInMilliseconds() {
        return contextVerificationFrequencyInMilliseconds;
    }

    public void setContextVerificationFrequencyInMilliseconds(int contextVerificationFrequencyInMilliseconds) {
        this.contextVerificationFrequencyInMilliseconds = contextVerificationFrequencyInMilliseconds;
    }

    public int getNumberOfInitializedContexts() {
        return numberOfContexts;
    }

    private void setNumberOfContexts() {
        numberOfContexts = 0;

        if (startDate != null) {
            contexts[0] = 1;
        } else {
            contexts[0] = 0;
        }

        if (location != null) {
            contexts[1] = 1;
        } else {
            contexts[1] = 0;
        }

        if (activity != null) {
            contexts[2] = 1;
        } else {
            contexts[2] = 0;
        }

        if (headphoneState != null) {
            contexts[3] = 1;
        } else {
            contexts[3] = 0;
        }

        for (int i : contexts) {
            if (i == 1) {
                numberOfContexts++;
            }
        }
    }
}*/

package coalery.twitchbotcreator;

import android.os.Parcel;
import android.os.Parcelable;

public class SettingData implements Parcelable {
    String channel;
    String oauth;

    public SettingData(String channel, String oauth) {
        this.channel = channel;
        this.oauth = oauth;
    }

    public SettingData(Parcel src) {
        this.channel = src.readString();
        this.oauth = src.readString();
    }

    public static final Parcelable.Creator<SettingData> CREATOR = new Parcelable.Creator<SettingData>() {
        @Override
        public SettingData createFromParcel(Parcel parcel) {
            return new SettingData(parcel);
        }

        @Override
        public SettingData[] newArray(int size) {
            return new SettingData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(this.channel);
        parcel.writeString(this.oauth);
    }
}

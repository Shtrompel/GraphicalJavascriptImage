package com.igalblech.school.graphicaljavascriptcompiler.utils;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.RequiresApi;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserData implements Parcelable {

    @Getter @Setter private String username;
    @Getter @Setter private String password;
    @Getter @Setter private String email;
    @Getter @Setter private String phone;
    @Getter @Setter private boolean isVerified;
    @Getter @Setter private String verificationCode;

    public static final java.util.Random RANDOM = new java.util.Random();
    public static final char[] NUMBERS = "0123456789".toCharArray();

    public UserData(String username, String password, String email, String phone) {
        this.username = username;
        this.password = password;
        this.email = email.toLowerCase ();
        this.phone = phone.replace ( "-", "" ).replace ( " ", "" );
    }

    public UserData(String[] arr) {
        this.username = arr.length > 0 ? arr[0] : "";
        this.password = arr.length > 1 ? arr[1] : "";
        this.email = arr.length > 2 ? arr[2] : "";
        this.phone = arr.length > 3 ? arr[3] : "";
        this.isVerified = arr.length > 4 ? arr[4].equals("true") : false;
        this.verificationCode = arr.length > 5 ? arr[5] : "";
    }

    public UserData(String str) {
        this(str.split(" "));
    }

    public UserData() {

    }

    protected UserData(Parcel in) {
        username = in.readString();
        password = in.readString();
        email = in.readString();
        phone = in.readString();
    }

    @Deprecated
    public void generateVerificationCode() {
        char[] code = new char[8];
        for (int i = 0; i < code.length; i++)
            code[i] = NUMBERS[RANDOM.nextInt(NUMBERS.length)];
        verificationCode = String.valueOf(code);
    }

    public boolean loginValidation(String username, String  password) {
        boolean ret = true;
        ret &= this.password.equals(password);
        boolean user = false;
        user |= this.username.equals(username);
        user |= this.email.equals(username.toLowerCase ());
        user |= this.phone.equals(username.replaceAll ( "-", "" ).replace ( "", "" ));
        ret &= user;
        return ret;
    }

    public String[] getArray() {
        return new String[]{username, password, email, phone};
    }

    public String getStringArray() {
        return (username + " " + password + " " + email + " " + phone +
                " " + (isVerified ? "true" : "false") + " " +
                verificationCode
        );
    }

    public int checkForClashes(UserData other) {
        return UserDataValidator.checkForClashes ( this, other );
    }



    public boolean equals(UserData other) {
        boolean ret = true;
        ret &= this.username.equals(other.username);
        ret &= this.password.equals(other.password);
        ret &= this.email.equals(other.email);
        ret &= this.phone.equals(other.phone);
        return ret;
    }

    public String toString() {
        return String.format("[Username = %s, Password = %s, Email = %s, Phone = %s]", username, password, email, phone);
    }


    public static final Creator<UserData> CREATOR = new Creator<UserData>() {
        @Override
        public UserData createFromParcel(Parcel in) {
            return new UserData(in);
        }

        @Override
        public UserData[] newArray(int size) {
            return new UserData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(username);
        parcel.writeString(password);
        parcel.writeString(email);
        parcel.writeString(phone);
        parcel.writeBoolean(isVerified);
        parcel.writeString(verificationCode);
    }

}

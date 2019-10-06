package com.example.iNote;

import androidx.annotation.NonNull;

import java.util.Objects;

public class ContactListContract{
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ContactListContract)) return false;
        ContactListContract that = (ContactListContract) o;
        return Objects.equals(getContactName(), that.getContactName()) &&
                Objects.equals(getContactPhoneNumber(), that.getContactPhoneNumber());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getContactName(), getContactPhoneNumber());
    }

    @NonNull
    @Override
    public String toString() {
        return "Name : " + contactName + " Number : " + contactPhoneNumber
                +"\n";
    }

    String contactName;
    String contactPhoneNumber;

    public ContactListContract(String contactName,String contactPhoneNumber){
        this.contactName = contactName;
        this.contactPhoneNumber = contactPhoneNumber;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactPhoneNumber() {
        return contactPhoneNumber;
    }

    public void setContactPhoneNumber(String contactPhoneNumber) {
        this.contactPhoneNumber = contactPhoneNumber;
    }
}

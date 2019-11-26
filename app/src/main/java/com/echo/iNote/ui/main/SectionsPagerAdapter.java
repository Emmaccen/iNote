package com.echo.iNote.ui.main;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.echo.iNote.ContactList;
import com.echo.iNote.ContactListContract;
import com.echo.iNote.Messages;

import java.util.Set;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {
    Set<ContactListContract> contactList;

    private final Context mContext;
    private static final String[] TAB_TITLES = new String[]{"Contacts", "Messages"};

    public SectionsPagerAdapter(Context context, FragmentManager fm, Set<ContactListContract> contactList) {
        super(fm);
        mContext = context;
        this.contactList = contactList;

    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        if(position == 0){
            return new ContactList(contactList);
        }else{
return new Messages(contactList);
        }
//        return PlaceholderFragment.newInstance(position + 1);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return TAB_TITLES[position];
    }

    @Override
    public int getCount() {
        // Show 2 total pages.
        return TAB_TITLES.length;
    }
}
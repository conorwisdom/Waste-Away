package com.example.wasteawayapplication.User;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class MsgTabsAccessorAdapter extends FragmentPagerAdapter {

    public MsgTabsAccessorAdapter(@NonNull FragmentManager fm) {
        super(fm, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    @NonNull
    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case 0:
                MsgChatsFragment msgChatsFragment = new MsgChatsFragment();
                return msgChatsFragment;
            case 1:
                MsgContactsFragment msgContactsFragment = new MsgContactsFragment();
                return msgContactsFragment;
            case 2:
                MsgRequestsFragment msgRequestsFragment = new MsgRequestsFragment();
                return msgRequestsFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Chats";
            case 1:
                return "Contacts";
            case 2:
                return "Requests";
            default:
                return null;
        }

    }
}

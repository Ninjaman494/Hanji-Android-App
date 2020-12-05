package com.a494studios.koreanconjugator.utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

public class BaseDialogFragment extends DialogFragment {

    @Override
    public void show(@NonNull FragmentManager manager, @Nullable String tag) {
        if (!manager.isDestroyed() && !manager.isStateSaved()) {
            super.show(manager, tag);
        }
    }
}

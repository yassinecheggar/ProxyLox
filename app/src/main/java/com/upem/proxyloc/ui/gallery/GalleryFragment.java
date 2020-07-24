package com.upem.proxyloc.ui.gallery;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.upem.proxyloc.R;

public class GalleryFragment extends Fragment {

    private GalleryViewModel galleryViewModel;
    private Switch MSwitch;
    private Switch SSwitch;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        galleryViewModel =
                ViewModelProviders.of(this).get(GalleryViewModel.class);
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);
        SharedPreferences prefs = root.getContext().getSharedPreferences("ProxyLoxStatus", root.getContext().MODE_PRIVATE);

        MSwitch = root.findViewById(R.id.switchmal);
        SSwitch = root.findViewById(R.id.switchsym);

        String restoredText = prefs.getString("status", null);
        Log.e("gallery", "onCreateView: " + restoredText);
        if (restoredText != null) {

            if(restoredText.equals("1")) MSwitch.setChecked(true);
            if(restoredText.equals("2")) SSwitch.setChecked(true);

        }

        return root;
    }
}
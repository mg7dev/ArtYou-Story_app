package com.dw.artyou.fragments;


import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dw.artyou.R;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentMarketing extends Fragment {

    private TextView tv_setting_return_back;

    public FragmentMarketing() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_marketing, container, false);

        tv_setting_return_back = view.findViewById(R.id.tv_setting_return_back);
        tv_setting_return_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    Objects.requireNonNull(getActivity()).onBackPressed();
                }
            }
        });

        return view;
    }

}

package com.smalsus.redhorizonvbr.interfaces;

import com.smalsus.redhorizonvbr.model.GetEvent;

import java.util.ArrayList;

public interface CustomButtonListener {
    void onButtonClickListner(ArrayList<String> videolIDist, int id, GetEvent details);
}

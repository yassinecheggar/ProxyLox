package com.upem.proxyloc.adaptor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.upem.proxyloc.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class CardAdaptor  extends RecyclerView.Adapter<CardHolder> {


    List<JSONObject> list;
    private OnNoteListener mOnNoteListener;
    public CardAdaptor( List<JSONObject>objects ,OnNoteListener onNoteListener ) {

        list = objects;
        this.mOnNoteListener = onNoteListener;
    }


    @NonNull
    @Override
    public CardHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_cards,parent,false);
        return new CardHolder(view,mOnNoteListener);
    }

    @Override
    public void onBindViewHolder(@NonNull CardHolder holder, int position) {

        JSONObject myObject = list.get(position);
        try {
            holder.bind(myObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface OnNoteListener{
        void onNoteClick(int position);
    }
}

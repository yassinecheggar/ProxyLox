package com.upem.proxyloc.adaptor;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.upem.proxyloc.R;

import org.json.JSONException;
import org.json.JSONObject;

public class CardHolder  extends RecyclerView.ViewHolder  implements View.OnClickListener {
    private TextView textViewView;
    private ImageView imageView;
    CardAdaptor.OnNoteListener mOnNoteListener;

    public CardHolder(@NonNull View itemView , CardAdaptor.OnNoteListener onNoteListener) {
        super(itemView);

        textViewView =  itemView.findViewById(R.id.textV);
        imageView = itemView.findViewById(R.id.imageV);
        mOnNoteListener = onNoteListener;
        itemView.setOnClickListener(this);
    }

    public void bind(JSONObject myObject) throws JSONException {
        textViewView.setText(myObject.getString("text"));
        imageView.setImageResource(myObject.getInt("image"));
    }

    @Override
    public void onClick(View v) {

        mOnNoteListener.onNoteClick(getAdapterPosition());
    }
}

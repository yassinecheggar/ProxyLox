package com.upem.proxyloc.ui.Activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.upem.proxyloc.R;
import com.upem.proxyloc.adaptor.CardAdaptor;
import com.upem.proxyloc.ui.spinner.SpinnerFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ActivitiesFragment extends Fragment implements CardAdaptor.OnNoteListener {

    private Spinner spinner;
    private static final String[] paths = {"item 1", "item 2", "item 3"};
    private RecyclerView recyclerView;
    private AdapterView.OnItemClickListener onItemClickListener;

    private List<JSONObject> cities = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        try {
            cities = ajouterVilles();
        } catch (JSONException e) {
            e.printStackTrace();
        }


        View root = inflater.inflate(R.layout.fragment_share, container, false);
        recyclerView = root.findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(root.getContext()));
        recyclerView.setAdapter(new CardAdaptor(cities,this));

        recyclerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

       /* Spinner spinner = (Spinner) root.findViewById(R.id.planets_spinner);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(root.getContext(),
                R.array.planets_array, R.layout.spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(R.layout.spinner_dropdown);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);*/

        return root;
    }


    private List<JSONObject> ajouterVilles() throws JSONException {


        List<JSONObject> jsonObjects = new ArrayList<>();
        JSONObject obj1 = new JSONObject();
        obj1.put("text", "Low");
        obj1.put("image", R.drawable.loow);

        JSONObject obj2 = new JSONObject();
        obj2.put("text", "Low/Medium");
        obj2.put("image", R.drawable.low);

        JSONObject obj3 = new JSONObject();
        obj3.put("text", "Medium");
        obj3.put("image", R.drawable.high);

        JSONObject obj4 = new JSONObject();
        obj4.put("text", "Medium/High");
        obj4.put("image", R.drawable.hightch);

        JSONObject obj5 = new JSONObject();
        obj5.put("text", "High");
        obj5.put("image", R.drawable.extreem);

        jsonObjects.add(obj1);
        jsonObjects.add(obj2);
        jsonObjects.add(obj3);
        jsonObjects.add(obj4);
        jsonObjects.add(obj5);

        return jsonObjects;
    }

    @Override
    public void onNoteClick(int position) {
        Bundle result = new Bundle();
        result.putString("bundleKey", String.valueOf( position));

       SpinnerFragment fragment =  new SpinnerFragment();
       fragment.setArguments(result);
       getActivity().getSupportFragmentManager().beginTransaction()
                .replace(this.getId(),fragment )
               .addToBackStack("spinners")
                .commit();
    }
}
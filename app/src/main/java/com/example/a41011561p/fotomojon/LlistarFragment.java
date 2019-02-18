package com.example.a41011561p.fotomojon;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


/**
 * A simple {@link Fragment} subclass.
 */
public class LlistarFragment extends Fragment {


    public LlistarFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_llistar, container, false);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        DatabaseReference base = FirebaseDatabase.getInstance().getReference();

        DatabaseReference users = base.child("users");
        DatabaseReference uid = users.child(auth.getUid());
        DatabaseReference incidencies = uid.child("incidencies");


        FirebaseListOptions<Incidencia> options = new FirebaseListOptions.Builder<Incidencia>()
                .setQuery(incidencies, Incidencia.class)
                .setLayout(R.layout.lv_incidencies_item)
                .setLifecycleOwner(this)
                .build();


        FirebaseListAdapter<Incidencia> adapter = new FirebaseListAdapter<Incidencia>(options) {
            @Override
            protected void populateView(View v, Incidencia model, int position) {
                TextView txtDescripcio = v.findViewById(R.id.txtDescripcio);
                TextView txtAdreca = v.findViewById(R.id.txtAdreca);

                txtDescripcio.setText(model.getProblema());
                txtAdreca.setText(model.getDireccio());
            }
        };

        ListView lvIncidencies = view.findViewById(R.id.lvIncidencies);
        lvIncidencies.setAdapter(adapter);


        return view;
    }
}

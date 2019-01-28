package com.example.a41011561p.fotomojon;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    FragmentManager fm = getSupportFragmentManager();

    final Fragment fragment1 = new NotificarFragment();
    final Fragment fragment2 = new LlistarFragment();
    final Fragment fragment3 = new MapaFragment();

    Fragment active = fragment1;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
        switch (item.getItemId()) {
            case R.id.navigation_home:
                setTitle(R.string.title_notifications);
                fm.beginTransaction().hide(active).show(fragment1).commit();
                active = fragment1;
                return true;
            case R.id.navigation_llistat:
                setTitle(R.string.title_dashboard);
                fm.beginTransaction().hide(active).show(fragment2).commit();
                active = fragment2;
                return true;
            case R.id.navigation_mapa:
                setTitle(R.string.title_home);
                fm.beginTransaction().hide(active).show(fragment3).commit();
                active = fragment3;
                return true;
        }
        return false;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView nav = findViewById(R.id.navigation);
        nav.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        fm.beginTransaction()
                .add(R.id.fragment_seleccionat, fragment1, "1")
                .hide(fragment1)
                .commit();

        fm.beginTransaction()
                .add(R.id.fragment_seleccionat, fragment2, "2")
                .hide(fragment2)
                .commit();

        fm.beginTransaction()
                .add(R.id.fragment_seleccionat, fragment3, "3")
                .hide(fragment3)
                .commit();

        nav.setSelectedItemId(R.id.navigation_home);
    }

}

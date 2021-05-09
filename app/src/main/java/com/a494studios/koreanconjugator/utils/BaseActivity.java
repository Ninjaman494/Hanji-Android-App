package com.a494studios.koreanconjugator.utils;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import com.a494studios.koreanconjugator.CustomApplication;
import com.a494studios.koreanconjugator.R;
import com.a494studios.koreanconjugator.settings.SettingsActivity;

import org.rm3l.maoni.Maoni;

import static com.eggheadgames.aboutbox.activity.AboutActivity.launch;

public abstract class BaseActivity extends AppCompatActivity {

    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return setupMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.overflow_settings){
            startActivity(new Intent(getBaseContext(), SettingsActivity.class));
            return true;
        }else if(item.getItemId() == R.id.overflow_about){
            Utils.makeAboutBox(this);
            launch(this);
            return true;
        }else if(item.getItemId() == R.id.overflow_bug) {
            CustomApplication.getIdler().increment();

            Maoni maoni = Utils.makeMaoniActivity(this);
            if (maoni != null) {
                maoni.start(this);
            }

            CustomApplication.getIdler().decrement();
            return true;
        } else if(item.getItemId() == R.id.overflow_ad_free) {
            Logger.getInstance().logViewUpgrade();
            Utils.showAdFreeUpgrade(this);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(searchView != null) {
            searchView.setQuery("", false);
            searchView.setIconified(true);
            searchView.clearFocus();
        }
    }

    public boolean setupMenu(Menu menu) {
        return setupMenu(R.menu.search_menu, menu);
    }

    public boolean setupMenu(int menuId, Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(menuId, menu);

        MenuItem searchItem = menu.findItem(R.id.menu_search);
        if(searchItem != null) {
            SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
            searchView = (SearchView) searchItem.getActionView();
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        }

        if(Utils.isAdFree(this) != null && Utils.isAdFree(this)) {
            menu.findItem(R.id.overflow_ad_free).setVisible(false);
        }

        return true;
    }
}

package fr.android.carnetdevoyage;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import com.example.carnetdevoyage.R;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import fr.android.carnetdevoyage.database.DatabaseConnection;
import fr.android.carnetdevoyage.database.TravelEntry;
import fr.android.carnetdevoyage.fragments.AddEntryFragment;
import fr.android.carnetdevoyage.fragments.HomeFragment;
import fr.android.carnetdevoyage.utils.LocaleHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        DatabaseConnection.OnDatabaseOperationListener {

    private DrawerLayout drawer;
    private FloatingActionButton fabAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Appliquer la langue sauvegardée
        String language = LocaleHelper.getLanguage(this);
        LocaleHelper.setLocale(this, language);

        setContentView(R.layout.activity_main);

        // Configuration de la toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        // Configuration du drawer (menu latéral)
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // Configurer le bouton d'ajout
        fabAdd = findViewById(R.id.fab_add);
        fabAdd.setOnClickListener(v -> openAddEntryFragment());

        // Afficher le fragment d'accueil par défaut
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }
    }

    private void openAddEntryFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new AddEntryFragment())
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_language) {
            showLanguageDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showLanguageDialog() {
        String[] languages = {"English", "Français"};
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle(R.string.language);
        builder.setSingleChoiceItems(languages, -1, (dialog, which) -> {
            switch (which) {
                case 0: // Anglais
                    LocaleHelper.setLocale(MainActivity.this, "en");
                    break;
                case 1: // Français
                    LocaleHelper.setLocale(MainActivity.this, "fr");
                    break;
            }
            // Redémarrer l'activité pour appliquer la nouvelle langue
            recreate();
            dialog.dismiss();
        });
        builder.show();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Gérer les sélections de menu
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
        } else if (id == R.id.nav_map) {
            // Cette fonctionnalité serait implémentée par votre binôme
            Toast.makeText(this, "Fonctionnalité de carte non implémentée", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_gallery) {
            // Cette fonctionnalité serait implémentée par votre binôme
            Toast.makeText(this, "Fonctionnalité de galerie non implémentée", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_settings) {
            // Afficher les paramètres
            Toast.makeText(this, "Paramètres non implémentés", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_sync) {
            // Synchroniser avec la base de données externe
            Toast.makeText(this, R.string.sync_data, Toast.LENGTH_SHORT).show();
            // Cette opération est déjà gérée par le HomeFragment
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // Callbacks pour les opérations de base de données
    @Override
    public void onSelectComplete(List<TravelEntry> entries) {
        // Non utilisé ici, géré par les fragments
    }

    @Override
    public void onOperationComplete(boolean success) {
        // Non utilisé ici, géré par les fragments
    }
}
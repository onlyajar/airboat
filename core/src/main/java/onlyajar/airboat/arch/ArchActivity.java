package onlyajar.airboat.arch;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import onlyajar.airboat.R;

public class ArchActivity extends AppCompatActivity {
    private FragmentManager fragmentManager;
    private boolean onFragmentBackFinish = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentManager =  getSupportFragmentManager();
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public void addFragment(Fragment fragment, int containerId, boolean addToBackStack){
        addFragment(fragment, containerId, addToBackStack, false);
    }
    public void addFragment(Fragment fragment, int containerId, boolean addToBackStack, boolean onFragmentBackFinish){
        this.onFragmentBackFinish = onFragmentBackFinish;
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
        ft.replace(containerId, fragment);
        if(addToBackStack) ft.addToBackStack(fragment.getClass().getName());
        ft.setReorderingAllowed(true);
        ft.commit();
    }
    @Override
    public void onBackPressed() {
        if(fragmentManager.getBackStackEntryCount() == 1 || onFragmentBackFinish){
            finish();
        }else {
            super.onBackPressed();
        }
    }
    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
    public void hideSystemKeyboard(View v){
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }
}

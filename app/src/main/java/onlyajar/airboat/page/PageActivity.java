package onlyajar.airboat.page;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import onlyajar.airboat.app.R;
import onlyajar.airboat.arch.ArchActivity;

public class PageActivity extends ArchActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        addFragment(new InputAmountFragment(), R.id.fragment_container_view, true);
//        new Handler(Looper.getMainLooper()).postDelayed(()->{
//            addFragment(new InputTipFragment(), R.id.fragment_container_view, true);
//        }, 5000);
        
    }
}
package onlyajar.airboat.page;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import onlyajar.airboat.app.R;
import onlyajar.airboat.arch.ArchFragment;
import onlyajar.airboat.utils.DateUtils;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link InputAmountFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InputAmountFragment extends ArchFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private InputAmountViewModel viewModel;
    private Handler mainHandler = new Handler(Looper.getMainLooper());
    public InputAmountFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment InputAmountFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static InputAmountFragment newInstance(String param1, String param2) {
        InputAmountFragment fragment = new InputAmountFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(InputAmountViewModel.class);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        viewModel.dateTime.observe(getViewLifecycleOwner(), s -> {
            Log.d(TAG, "dateTime observe: " + s);
        });
        gogo();
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_input_amount, container, false);
    }

    private void gogo(){
        mainHandler.postDelayed(()->{
            String date = DateUtils.date();
            viewModel.update(date);
            Log.d(TAG, "gogo: update " + date);
            gogo();
        }, 2000);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView tvTip = view.findViewById(R.id.tv_input_amount);
        System.out.println("aaaaa");
        tvTip.setText("aaaaa");
    }
}
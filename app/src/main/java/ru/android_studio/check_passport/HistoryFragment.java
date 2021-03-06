package ru.android_studio.check_passport;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import ru.android_studio.check_passport.model.TypicalResponse;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HistoryFragment extends Fragment implements View.OnClickListener {
    private DatabaseHelper databaseHelper;
    private PassportActivity passportActivity;
    private PassportBaseAdapter adapter;
    private ListView listView;
    private List<Map<String, String>> historyList;
    private Button btnClearHistory;

    public HistoryFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof PassportActivity) {
            passportActivity = (PassportActivity) context;
            this.databaseHelper = new DatabaseHelper(passportActivity);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        // TODO сохранение в Bundle приметивов
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        btnClearHistory = (Button) view.findViewById(R.id.btnClearHistory);
        btnClearHistory.setOnClickListener(this);

        AdRequest adRequest = new AdRequest.Builder()
                //.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                //.addTestDevice("TEST_DEVICE_ID")
                .build();
        AdView mAdView = (AdView) view.findViewById(R.id.adView);
        if(mAdView != null) {
            mAdView.loadAd(adRequest);
        }

        initHistoryList();
        listView = (ListView) view.findViewById(R.id.listView);
        adapter = new PassportBaseAdapter(passportActivity, historyList);
        listView.setAdapter(adapter);
        listView.setEmptyView(view.findViewById(R.id.emptyElement));

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnClearHistory:
                databaseHelper.clear();
                clearHistoryList();
                adapter.clearList();
                adapter.notifyDataSetChanged();
                adapter.notifyDataSetInvalidated();
                listView.refreshDrawableState();
        }
    }

    private HashMap<String, String> getHashMapByPassport(@NonNull Passport passport) {
        HashMap<String, String> passportHashMap = new HashMap<>();
        passportHashMap.put(DatabaseHelper.COLUMN_SERIES, passport.getSeries());
        passportHashMap.put(DatabaseHelper.COLUMN_NUMBER, passport.getNumber());

        TypicalResponse typicalResponse = passport.getTypicalResponse();
        if (typicalResponse != null) {
            passportHashMap.put(DatabaseHelper.COLUMN_RESULT,
                    getString(typicalResponse.getDescription()));
        }
        return passportHashMap;
    }

    private void initHistoryList() {
        if (historyList == null) {
            historyList = new ArrayList<>();
            AbstractList<Passport> passports = databaseHelper.getAll();
            if (passports.isEmpty()) {
                btnClearHistory.setVisibility(View.GONE);
            } else {
                historyList.add(getHeaders());
                for (Passport passport : passports) {
                    historyList.add(getHashMapByPassport(passport));
                }
                btnClearHistory.setVisibility(View.VISIBLE);
            }
        }
    }

    private void clearHistoryList() {
        historyList.clear();
        btnClearHistory.setVisibility(View.GONE);
    }

    private HashMap<String, String> getHeaders() {
        HashMap<String, String> passportHashMap = new HashMap<>();
        passportHashMap.put(DatabaseHelper.COLUMN_SERIES, getString(R.string.passport_info));
        passportHashMap.put(DatabaseHelper.COLUMN_NUMBER, "");
        passportHashMap.put(DatabaseHelper.COLUMN_RESULT, getString(R.string.result));

        return passportHashMap;
    }

    public void addToHistoryList(Passport passport) {
        if (passport == null) {
            throw new IllegalArgumentException("passport can't be null");
        }

        if (historyList.isEmpty()) {
            historyList.add(getHeaders());
        }

        historyList.add(getHashMapByPassport(passport));

        adapter.notifyDataSetChanged();
        adapter.notifyDataSetInvalidated();

        btnClearHistory.setVisibility(View.VISIBLE);
    }
}
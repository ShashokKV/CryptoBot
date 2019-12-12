package com.chess.cryptobot.content.pairs;

import android.content.Context;

import androidx.fragment.app.Fragment;

import com.chess.cryptobot.content.ContextHolder;
import com.chess.cryptobot.content.Preferences;
import com.chess.cryptobot.content.balance.BalancePreferences;
import com.chess.cryptobot.model.Pair;
import com.chess.cryptobot.model.ViewItem;
import com.chess.cryptobot.task.AvailablePairsTask;
import com.chess.cryptobot.task.PairsUpdateTask;
import com.chess.cryptobot.task.SerialExecutor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PairsHolder extends ContextHolder {
    private List<String> invalidPairs;
    private List<String> availablePairs;
    private Map<String, Double> bittrexVolumes;
    private Map<String, Double> livecoinVolumes;
    private List<String> negativePercentPairs;
    private AllPairsPreferences allPairsPrefs;
    private SerialExecutor serialExecutor;

    public PairsHolder(Fragment fr) {
        super(fr);
        serialExecutor = new SerialExecutor();
    }

    @Override
    public void initFields() {
        super.initFields();
        this.invalidPairs = new ArrayList<>();
        this.availablePairs = new ArrayList<>();
        this.negativePercentPairs = new ArrayList<>();
        this.bittrexVolumes = new HashMap<>();
        this.livecoinVolumes = new HashMap<>();
    }


    public void initAvailablePairs() {
        AvailablePairsTask availablePairsTask = new AvailablePairsTask(this);
        availablePairsTask.executeOnExecutor(serialExecutor,0);
    }

    @Override
    public Preferences initPrefs(Context context) {
        allPairsPrefs = new AllPairsPreferences(context);
        return new PairsPreferences(context);
    }

    @Override
    public void initViewItems(Set<String> pairNames) {
        pairNames.forEach(pairName -> {
            if (isValidPair(pairName)) addItemToList(Pair.fromPairName(pairName));
        });
    }

    public void updateFromBalance() {
        BalancePreferences balancePreferences = new BalancePreferences(getContext());
        Set<String> coinNames = balancePreferences.getItems();
        List<ViewItem> balancePairs = new ArrayList<>();
        for (String baseName : coinNames) {
            for (String marketName : coinNames) {
                if (!baseName.equals(marketName)) {
                    Pair pair = new Pair(baseName, marketName);
                    String pairName = pair.getName();
                    if (isValidPair(pairName) && !negativePercentPairs.contains(pairName)) {
                        balancePairs.add(pair);
                    }
                }
            }
        }
        removeIfNotExists(balancePairs);
        addIfNotExists(balancePairs);
    }

    private boolean isValidPair(String pairName) {
        if (this.availablePairs.isEmpty()) {
            return !invalidPairs.contains(pairName);
        } else {
            return availablePairs.contains(pairName) && !invalidPairs.contains(pairName);
        }
    }

    private void addIfNotExists(List<ViewItem> balancePairs) {
        balancePairs.forEach(tradingPair -> {
            if (!getViewItems().contains(tradingPair)) {
                add(tradingPair);
            }
        });
    }

    private void removeIfNotExists(List<ViewItem> balancePairs) {
        this.retainAll(balancePairs);
    }

    public void removeInvalidPairs() {
        List<ViewItem> pairs = new ArrayList<>();
        availablePairs.forEach(pairName -> pairs.add(Pair.fromPairName(pairName)));
        this.retainAll(pairs);
    }

    public void setVolumes(Map<String, Double> bittrexVolumes, Map<String, Double> livecoinVolumes) {
        this.bittrexVolumes = bittrexVolumes;
        this.livecoinVolumes = livecoinVolumes;
    }

    @Override
    public void updateItem(ViewItem item) {
        Pair pair = (Pair) item;
        pair.setLivecoinVolume(livecoinVolumes.get(pair.getName()));
        pair.setBittrexVolume(bittrexVolumes.get(pair.getName()));
        PairsUpdateTask task = new PairsUpdateTask(this);
        task.executeOnExecutor(serialExecutor, pair);
    }

    public void addToInvalidPairs(Pair pair) {
        String pairName = pair.getName();
        if (!invalidPairs.contains(pairName)) invalidPairs.add(pairName);
    }

    public void addToNegativePercentPairs(Pair pair) {
        String pairName = pair.getName();
        if (!negativePercentPairs.contains(pairName)) negativePercentPairs.add(pairName);
    }

    public void resetNegativePercentPairs() {
        this.negativePercentPairs = new ArrayList<>();
    }

    public void setAvailablePairs(List<String> pairs) {
        if (pairs != null) {
            availablePairs = pairs;
            allPairsPrefs.setItems(new HashSet<>(pairs));
        }
    }
}

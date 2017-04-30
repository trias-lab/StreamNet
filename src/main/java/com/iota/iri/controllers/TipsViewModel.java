package com.iota.iri.controllers;

import com.iota.iri.model.Hash;
import com.iota.iri.model.Tip;
import com.iota.iri.storage.Tangle;

import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * Created by paul on 3/14/17 for iri-testnet.
 */
public class TipsViewModel {

    private static List<Hash> tips = new ArrayList<>();
    private static Set<Hash> nonSolidTips = new HashSet<>();
    private static SecureRandom seed = new SecureRandom();
    public static final Object sync = new Object();

    public static boolean addTipHash (Hash hash) throws ExecutionException, InterruptedException {
        synchronized (sync) {
            nonSolidTips.add(hash);
            return tips.add(hash);
        }
    }

    public static boolean removeTipHash (Hash hash) throws ExecutionException, InterruptedException {
        synchronized (sync) {
            nonSolidTips.remove(hash);
            return tips.remove(hash);
        }
    }

    public static boolean removeSolidHash(Hash hash) {
        synchronized (sync) {
            return nonSolidTips.remove(hash);
        }
    }

    public static Hash[] getNonSolidTips() {
        synchronized (sync) {
            return nonSolidTips.toArray(new Hash[nonSolidTips.size()]);
        }
    }

    public static Hash[] getTips() {
        Hash[] hashes;
        synchronized (sync) {
            hashes = tips.stream().toArray(Hash[]::new);
        }
        return hashes;
    }

    public static Hash getRandomTipHash() throws ExecutionException, InterruptedException {
        synchronized (sync) {
            return tips.size() != 0 ? tips.get(seed.nextInt(size())) : null;
        }
    }

    public static int size() {
        return tips.size();
    }

    public static void loadTipHashes() throws ExecutionException, InterruptedException {
        Hash[] hashes = Arrays.stream(Tangle.instance()
                .keysWithMissingReferences(Tip.class).get())
                .toArray(Hash[]::new);
        tips.addAll(Arrays.asList(hashes));
    }
}

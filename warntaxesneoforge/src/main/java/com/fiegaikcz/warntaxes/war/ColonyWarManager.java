package com.fiegaikcz.warntaxes.war;

import java.util.*;

public class ColonyWarManager {

    private static final Set<WarPair> activeWars = new HashSet<>();
    private static final Map<WarPair, Long> warStartTimes = new HashMap<>();
    private static final Map<WarPair, Long> warProposals = new HashMap<>();
    private static final Map<WarPair, Integer> proposedGuards = new HashMap<>();

    public static void proposeWar(UUID colonyA, UUID colonyB, long timeMillis, int guardCount) {
        WarPair pair = new WarPair(colonyA, colonyB);
        warProposals.put(pair, timeMillis);
        proposedGuards.put(pair, guardCount);
    }

    public static boolean hasProposal(UUID colonyA, UUID colonyB) {
        return warProposals.containsKey(new WarPair(colonyA, colonyB));
    }

    public static void acceptWar(UUID colonyA, UUID colonyB, long timeMillis) {
        WarPair pair = new WarPair(colonyA, colonyB);
        warProposals.remove(pair);
        proposedGuards.remove(pair);
        activeWars.add(pair);
        warStartTimes.put(pair, timeMillis);
    }

    public static void declineWar(UUID colonyA, UUID colonyB) {
        WarPair pair = new WarPair(colonyA, colonyB);
        warProposals.remove(pair);
        proposedGuards.remove(pair);
    }

    public static void endWar(UUID colonyA, UUID colonyB) {
        WarPair pair = new WarPair(colonyA, colonyB);
        activeWars.remove(pair);
        warStartTimes.remove(pair);
    }

    public static boolean isAtWar(UUID colonyA, UUID colonyB) {
        return activeWars.contains(new WarPair(colonyA, colonyB));
    }

    public static Long getStartTime(UUID colonyA, UUID colonyB) {
        return warStartTimes.get(new WarPair(colonyA, colonyB));
    }

    public static Long getProposalTime(UUID colonyA, UUID colonyB) {
        return warProposals.get(new WarPair(colonyA, colonyB));
    }

    public static Integer getProposedGuards(UUID colonyA, UUID colonyB) {
        return proposedGuards.get(new WarPair(colonyA, colonyB));
    }

    public static Set<WarPair> getActiveWars() {
        return activeWars;
    }

    public record WarPair(UUID a, UUID b) {
        public WarPair {
            if (a.compareTo(b) > 0) {
                UUID tmp = a;
                a = b;
                b = tmp;
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof WarPair that)) return false;
            return a.equals(that.a) && b.equals(that.b);
        }

        @Override
        public int hashCode() {
            return Objects.hash(a, b);
        }
    }
}

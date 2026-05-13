package me.arrow.checks.types;

import me.arrow.managers.profile.Profile;

// this is a trust factor system, really good if you don't know how to make bufferless combat checks, like me
// so you can get a dynamic trust factor

public class TrustFactor {
    private final Profile profile;
    private final double MIN = -100;
    private final double MAX = 100;

    public TrustFactor(Profile profile) {
        this.profile = profile;
    }

    public void increaseTrust() {
        double current = profile.getTrustScore();
        if (current < MAX) {
            profile.setTrustScore(current + 1);
        }
    }

    public void increaseTrustBy(double increase) {
        double current = profile.getTrustScore();
        if (current < MAX) {
            profile.setTrustScore(current + increase);
        }
    }

    public void decreaseTrust() {
        double current = profile.getTrustScore();
        if (current > MIN) {
            profile.setTrustScore(current - 1);
        }
    }

    public void decreaseTrustBy(double decrease)  {
        double current = profile.getTrustScore();
        if (current > MIN) {
            profile.setTrustScore(current - decrease);
        }
    }

    public double getTrust() {
        return profile.getTrustScore();
    }

    public TrustType getRank() {
        double current = getTrust();

        if (current <= -50) {
            return TrustType.SUPER_UNTRUSTWORTHY;
        } else if (current <= -20) {
            return TrustType.UNTRUSTWORTHY;
        } else if (current < 20) {
            return TrustType.SUSPICIOUS;
        } else if (current < 60) {
            return TrustType.NORMAL;
        } else if (current < 85) {
            return TrustType.TRUSTED;
        } else return TrustType.LEGIT;
    }

    public int getRequiredBuffer() {
        double current = getTrust();

        if (current < -20) {
            return 0;
        } else if (current < 20) {
            return 5;
        } else if (current < 80) {
            return 15;
        } else {
            return 20;
        }
    }
}

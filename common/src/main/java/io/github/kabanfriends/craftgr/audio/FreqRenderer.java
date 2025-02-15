package io.github.kabanfriends.craftgr.audio;

public class FreqRenderer {

    private static final double SMOOTHING_FACTOR = 0.60;
    private static final int MIN_BANDWIDTH = 200;

    private final AudioPlayer audioPlayer;

    private final int sampleRate;
    private final int octaves;
    private final int numBands;
    private final double[] smoothedBands;

    private double maxValue;
    private double minValue;

    public FreqRenderer(AudioPlayer audioPlayer, int sampleRate, int numBands) {
        this.audioPlayer = audioPlayer;
        this.sampleRate = sampleRate;
        this.numBands = numBands;

        int octaves = 1;
        double nyq = sampleRate / 2.0;
        while ((nyq /= 2) > FreqRenderer.MIN_BANDWIDTH) {
            octaves++;
        }
        this.octaves = octaves;
        this.smoothedBands = new double[numBands];
    }

    public double[] calculateBandsNow() {
        FreqSample sample = audioPlayer.getFreqSampleNow();
        if (sample == null) {
            // Failsafe
            return new double[numBands];
        }

        double[] averages = sample.calculateBands(numBands);

        for (int i = 0; i < numBands; i++) {
            double current = averages[i];

            smoothedBands[i] = SMOOTHING_FACTOR * smoothedBands[i] + ((1 - SMOOTHING_FACTOR) * current);

            if (smoothedBands[i] > maxValue) {
                maxValue = smoothedBands[i];
            }
            if (smoothedBands[i] < minValue) {
                minValue = smoothedBands[i];
            }
        }

        double range = maxValue - minValue;
        double scaleFactor = range + Double.MIN_VALUE;

        double[] out = new double[numBands];
        for (int i = 0; i < numBands; i++) {
            double value = ((smoothedBands[i] - minValue) / scaleFactor);
            out[i] = Math.clamp(value * amplifier(value), 0, 1);
        }

        return out;
    }

    public int getSampleRate() {
        return sampleRate;
    }

    public int getOctaves() {
        return octaves;
    }

    private static double amplifier(double value) {
        double a = 90;
        double b = 10;
        return value * a * Math.pow(Math.E, -b * value) + 1;
    }

    private static double toDb(double value) {
        return value == 0 ? 0 : 10 * Math.log10(value);
    }
}

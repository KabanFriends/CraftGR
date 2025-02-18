package io.github.kabanfriends.craftgr.audio;

public class FreqRenderer {

    private static final double BAND_SMOOTHING_FACTOR = 0.6000;
    private static final double MINMAX_SMOOTHING_FACTOR = 0.9999;
    private static final int MIN_BANDWIDTH = 200;

    private final AudioPlayer audioPlayer;

    private final int sampleRate;
    private final int octaves;
    private final int numBands;
    private final double[] smoothedBands;

    private double smoothedMax = -1;

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

        FreqSample.Bands bands = sample.calculateBands(numBands);

        for (int i = 0; i < numBands; i++) {
            smoothedBands[i] = BAND_SMOOTHING_FACTOR * smoothedBands[i] + (1 - BAND_SMOOTHING_FACTOR) * bands.values()[i];
        }

        if (bands.maxValue() > smoothedMax) {
            smoothedMax = bands.maxValue();
        } else {
            smoothedMax = MINMAX_SMOOTHING_FACTOR * smoothedMax + (1 - MINMAX_SMOOTHING_FACTOR) * bands.maxValue();
        }

        double[] out = new double[numBands];
        for (int i = 0; i < numBands; i++) {
            double value = amplify(smoothedBands[i] / (smoothedMax + Double.MIN_VALUE));
            out[i] = Math.clamp(value, 0, 1);
        }

        return out;
    }

    public int getSampleRate() {
        return sampleRate;
    }

    public int getOctaves() {
        return octaves;
    }

    private static double amplify(double x) {
        return 1 - Math.pow(1 - x, 3);
    }
}

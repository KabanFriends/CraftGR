package io.github.kabanfriends.craftgr.audio;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;

public class FreqSample {

    private static final FastFourierTransformer FFT = new FastFourierTransformer(DftNormalization.STANDARD);

    private final FreqRenderer renderer;
    private final short[] samples;
    private final double bandwidth;

    private final double[] spectrum;

    public FreqSample(FreqRenderer renderer, short[] samples) {
        this.renderer = renderer;
        this.samples = samples;
        this.bandwidth = (2.0 / samples.length) * (renderer.getSampleRate() / 2.0);

        Complex[] transform = FFT.transform(convertArray(samples), TransformType.FORWARD);
        spectrum = new double[transform.length];
        for (int i = 0; i < spectrum.length; i++) {
            spectrum[i] = Math.sqrt(transform[i].getReal() * transform[i].getReal() + transform[i].getImaginary() * transform[i].getImaginary());
        }
    }

    public short[] getSamples() {
        return samples;
    }

    public double getBandwidth() {
        return bandwidth;
    }

    public Bands calculateBands(int numBands) {
        int octaves = renderer.getOctaves();
        double bandsPerOctave = (numBands + 1.0) / octaves;

        double[] averages = new double[numBands];
        double maxValue = 0;

        int j = 0;
        for (int i = 0; i < octaves; i++) {
            double lowFreq, highFreq, freqStep;

            lowFreq = i == 0 ? 0 : (renderer.getSampleRate() / 2.0) / Math.pow(2, octaves - i);
            highFreq = (renderer.getSampleRate() / 2.0) / Math.pow(2, octaves - i - 1);
            freqStep = (highFreq - lowFreq) / bandsPerOctave;

            double f = lowFreq;
            for (; j < i * bandsPerOctave; j++) {
                averages[j] = calculateAverage(f, f + freqStep);
                f += freqStep;

                if (averages[j] > maxValue) {
                    maxValue = averages[j];
                }
            }
        }

        return new Bands(averages, maxValue);
    }

    private double calculateAverage(double lowFreq, double highFreq) {
        int lowBound = freqToIndex(lowFreq);
        int highBound = freqToIndex(highFreq);
        double avg = 0;

        for (int i = lowBound; i <= highBound; i++) {
            avg += spectrum[i];
        }
        return avg / (highBound - lowBound + 1);
    }

    private int freqToIndex(double freq) {
        if (freq < bandwidth / 2.0) {
            return 0;
        }
        if (freq > renderer.getSampleRate() / 2.0 - bandwidth / 2.0) {
            return spectrum.length - 1;
        }
        double fraction = freq / renderer.getSampleRate();
        return (int) Math.round(samples.length * fraction);
    }

    private static double[] convertArray(short[] array) {
        double[] newArray = new double[Integer.highestOneBit(array.length) << 1];
        for (int i = 0; i < array.length; i++) {
            newArray[i] = array[i];
        }
        return newArray;
    }

    public record Bands(double[] values, double maxValue) {}
}

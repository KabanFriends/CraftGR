package io.github.kabanfriends.craftgr.audio;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;

public class FreqSampleOld {

    private static final int MIN_BANDWIDTH = 200;
    private static final int BANDS_PER_OCTAVE = 10;

    private static final FastFourierTransformer FFT = new FastFourierTransformer(DftNormalization.STANDARD);

    private final short[] samples;
    private final int sampleRate;
    private final double bandwidth;

    private final double[] spectrum;

    public FreqSampleOld(short[] samples, int sampleRate) {
        this.samples = samples;
        this.sampleRate = sampleRate;
        this.bandwidth = (2.0 / samples.length) * (sampleRate / 2.0);

        Complex[] transform = FFT.transform(convertArray(samples), TransformType.FORWARD);
        spectrum = new double[transform.length];
        for (int i = 0; i < spectrum.length; i++) {
            spectrum[i] = Math.sqrt(transform[i].getReal() * transform[i].getReal() + transform[i].getImaginary() * transform[i].getImaginary());
        }
    }

    public double[] splitBands(int n) {
        int octaves = 1;
        double nyq = sampleRate / 2.0;
        while ((nyq /= 2) > MIN_BANDWIDTH) {
            octaves++;
        }
        double[] averages = new double[octaves * BANDS_PER_OCTAVE];

        for (int i = 0; i < octaves; i++) {
            double lowFreq, highFreq, freqStep;

            lowFreq = i == 0 ? 0 : (sampleRate / 2.0) / Math.pow(2, octaves - i);
            highFreq = (sampleRate / 2.0) / Math.pow(2, octaves - i - 1);
            freqStep = (highFreq - lowFreq) / BANDS_PER_OCTAVE;

            double f = lowFreq;
            for (int j = 0; j < BANDS_PER_OCTAVE; j++) {
                int offset = j + i * BANDS_PER_OCTAVE;
                averages[offset] = calculateAverage(f, f + freqStep);
                f += freqStep;
            }
        }

        return averages;
        /*
        double[] bands = new double[n];
        int samplesPerBand = spectrum.length / n;

        for (int i = 0; i < n; i++) {
            double sum = 0;
            for (int j = 0; j < samplesPerBand; j++) {
                sum += spectrum[i * samplesPerBand + j];
            }
            bands[i] = sum / samplesPerBand;
        }

        return bands;
        */
    }

    private double calculateAverage(double lowFreq, double highFreq) {
        int lowBound = freqToIndex(lowFreq);
        int highBound = freqToIndex(highFreq);
        double avg = 0;

        /*
        System.out.println("SR: " + sampleRate + " SL: " + samples.length);
        System.out.println("LF: " + lowFreq + " HF: " + highFreq);
        System.out.println("BW: " + bandwidth + " LB: " + lowBound + " HB: " + highBound);
        */
        for (int i = lowBound; i <= highBound; i++) {
            avg += spectrum[i];
        }
        return avg / (highBound - lowBound + 1);
    }

    private int freqToIndex(double freq) {
        if (freq < bandwidth / 2.0) {
            return 0;
        }
        if (freq > sampleRate / 2.0 - bandwidth / 2.0) {
            return spectrum.length - 1;
        }
        double fraction = freq / sampleRate;
        return (int) Math.round(samples.length * fraction);
    }

    private static double toDb(double value) {
        return value == 0 ? 0 : 10 * Math.log10(value);
    }

    private static double[] convertArray(short[] array) {
        double[] newArray = new double[Integer.highestOneBit(array.length) << 1];
        for (int i = 0; i < array.length; i++) {
            newArray[i] = array[i];
        }
        return newArray;
    }
}

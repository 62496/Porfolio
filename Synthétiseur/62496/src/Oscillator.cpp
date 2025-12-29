//
// Created by Reqzanod on 27-05-25.
//

#include "../Oscillator.h"
#include <cmath>

Oscillator::Oscillator(double sampleRate) : sampleRate(sampleRate) {}

void Oscillator::generate(float* buffer, size_t frames, double frequency, Waveform waveform) {
    double phaseStep = 2.0 * M_PI * frequency / sampleRate;

    for (size_t i = 0; i < frames; ++i) {
        double sample = 0.0;

        switch (waveform) {
            case Waveform::SINE:
                sample = std::cos(phase) * 0.5;
            break;
            case Waveform::SQUARE:
                sample = (phase < M_PI) ? 0.5f : -0.5f;
            break;
            case Waveform::SAW:
                sample = 2.0 * (phase / (2.0 * M_PI)) - 1.0;
            sample = std::max(-0.5, std::min(0.5, sample));
            break;
        }

        buffer[i] = static_cast<float>(sample);

        phase += phaseStep;
        if (phase >= 2.0 * M_PI)
            phase -= 2.0 * M_PI;
    }
}
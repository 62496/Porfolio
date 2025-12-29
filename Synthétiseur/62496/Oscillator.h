#ifndef OSCILLATOR_H
#define OSCILLATOR_H

#include "SynthParameters.h"

/// @class Oscillator
/// @brief Mono waveform generator (SINE, SAW, SQUARE) over a 256‐frame buffer.
///
/// On each generate(buffer, frames, frequency, waveform) call:
///  1. Compute phaseStep = 2π * frequency / sampleRate.
///  2. For each sample:
///     – Generate sample according to waveform:
///       · SINE:   cos(phase) * 0.5
///       · SQUARE: +0.5 if phase<π, −0.5 otherwise
///       · SAW:    (2*(phase/(2π)) − 1), then clamp to ±0.5
///     – Increment phase by phaseStep (wrap around 2π).
///  3. Write each sample into buffer[i].
class Oscillator {
public:
    /// @brief Constructs an oscillator at the given sample rate.
    /// @param sampleRate  Sampling frequency (e.g., 44100.0).
    Oscillator(double sampleRate);

    /// @brief Fills the buffer with `frames` samples at the given `frequency`.
    /// @param buffer      Float array of length ≥ frames.
    /// @param frames      Number of samples to generate.
    /// @param frequency   Note frequency (Hz), e.g., 440.0.
    /// @param waveform    Waveform type to generate (SINE, SAW, SQUARE).
    void generate(float* buffer, size_t frames, double frequency, Waveform waveform);

private:
    double phase = 0.0;    ///< Current phase [0.0 ; 2π), persistent across calls.
    double sampleRate;     ///< Sampling frequency (Hz).
};

#endif // OSCILLATOR_H

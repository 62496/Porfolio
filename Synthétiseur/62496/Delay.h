#ifndef DELAY_H
#define DELAY_H

#include <vector>
#include <cstddef>

/// @class Delay
/// @brief Implements a simple delay effect using a circular buffer.
class Delay {
public:
    /// @brief Constructs the delay for a given sampling rate.
    /// @param sampleRate  Sampling frequency (Hz), e.g., 44100.0.
    Delay(double sampleRate);

    /// @brief Updates the delay parameters (duration in seconds and wet/dry mix).
    /// @param delayTimeSec  Delay duration in seconds (clamped to buffer capacity).
    /// @param mix           Proportion of the delayed signal (0.0 = dry only, 1.0 = wet only).
    void setParams(float delayTimeSec, float mix);

    /// @brief Resets the internal buffer to zero and resets writeIndex to 0.
    void reset();

    /// @brief Processes a single mono sample and returns the mixed output (dry + wet).
    /// @param input  Input sample (float).
    /// @return Mixed output sample (dry/wet).
    float process(float input);

private:
    double sampleRate;             ///< Sampling frequency (Hz).
    std::vector<float> buffer;     ///< Circular buffer to hold past samples.
    std::size_t writeIndex;        ///< Position where the current sample is written.
    std::size_t delaySamples;      ///< Delay length (in samples) between writeIndex and readIndex.
    float mix;                     ///< Proportion of dry vs. wet (0.0 to 1.0).
};

#endif // DELAY_H

// Envelope.h

#ifndef ENVELOPE_H
#define ENVELOPE_H

#include <cstddef>

/// @class Envelope
/// @brief AR (Attack/Release) envelope generator applied to a mono buffer.
///
/// Functionality:
///  – When `gate == true`, the internal value rises from 0.0 to 1.0 over `attackS` seconds.
///  – When `gate == false`, it falls from its current value to 0.0 over `releaseS` seconds.
///  – Each sample in the provided buffer is multiplied by the current envelope value.
class Envelope {
public:
    /// @brief Constructs the envelope with the given sample rate.
    /// @param sampleRate  Sampling frequency (e.g., 44100.0).
    Envelope(double sampleRate);

    /// @brief Applies the AR envelope to a mono buffer.
    /// @param buffer    Pointer to the first sample (float).
    /// @param frames    Number of samples in the buffer.
    /// @param gate      True if noteOn or mouseOn (attack phase), false otherwise (release phase).
    /// @param attackS   Attack duration in seconds (>= 0).
    /// @param releaseS  Release duration in seconds (>= 0).
    void process(float* buffer,
                 std::size_t frames,
                 bool gate,
                 float attackS,
                 float releaseS);

private:
    double sampleRate; ///< Sampling frequency in Hz.
    double value;      ///< Current envelope value (0.0 to 1.0).
};

#endif // ENVELOPE_H

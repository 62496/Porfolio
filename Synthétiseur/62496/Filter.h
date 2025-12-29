// Filter.h

#ifndef FILTER_H
#define FILTER_H

/// @class Filter
/// @brief Resonant low-pass filter (biquad).
///
/// Operation:
///  – setParams(cutoffHz, resonance) computes coefficients a0, a1, a2, b1, b2 using the RBJ Cookbook.
///  – process(input) applies the difference equation:
///      y[n] = a0*x[n] + a1*x[n−1] + a2*x[n−2] − b1*y[n−1] − b2*y[n−2].
///  – reset() zeroes out x1, x2, y1, y2.
class Filter {
public:
    /// @brief Constructs a biquad low-pass filter with the given sample rate.
    /// @param sampleRate  Sampling frequency (e.g., 44100.0).
    Filter(double sampleRate);

    /// @brief Calculates internal coefficients from cutoff frequency and resonance.
    /// @param cutoffHz    Cutoff frequency (20 Hz to 20000 Hz).
    /// @param resonance   Resonance factor [0.0 ; 1.0), clamped below 1.0.
    void setParams(double cutoffHz, double resonance);

    /// @brief Resets the filter’s internal state (x1, x2, y1, y2 to zero).
    void reset();

    /// @brief Processes a single mono sample through the low-pass filter.
    /// @param input  Input sample (float).
    /// @return Filtered output sample (float).
    float process(float input);

private:
    double sampleRate;        ///< Sampling frequency in Hz.
    double a0{}, a1{}, a2{};   ///< Feedforward coefficients.
    double b1{}, b2{};        ///< Feedback coefficients.
    double x1{}, x2{};        ///< Previous input samples x[n−1], x[n−2].
    double y1{}, y2{};        ///< Previous output samples y[n−1], y[n−2].
};

#endif // FILTER_H

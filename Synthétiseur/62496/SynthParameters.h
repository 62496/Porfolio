#ifndef SYNTHPARAMETERS_H
#define SYNTHPARAMETERS_H

#pragma once

#include <atomic>

/// @enum Waveform
/// @brief Supported waveform types for the oscillator.
enum class Waveform {
    SINE = 0,  ///< Sine wave.
    SAW,       ///< Sawtooth wave.
    SQUARE     ///< Square wave.
};

/// @struct SynthParameters
/// @brief Contains all shared parameters between the UI (MainWindow) and the audio (AudioGenerator).
///
/// Each field is a std::atomic<...> so it can be read/written without a mutex in the audio callback:
/// – osc1_enabled/osc2_enabled: on/off toggles for the two oscillators.
/// – currentNote, noteOn, mouseOn: monophonic note triggering (latest note played).
/// – osc1_freq_offset, osc1_waveform, osc2_waveform: oscillator modulations.
/// – attack, release: AR envelope times.
/// – filter_cutoff, filter_resonance: low-pass filter parameters.
/// – delay_time, delay_mix: delay effect parameters.
struct SynthParameters {
    std::atomic<bool>   osc1_enabled    { true  };    ///< Enables/disables oscillator 1.
    std::atomic<bool>   osc2_enabled    { false };    ///< Enables/disables oscillator 2.
    std::atomic<int>    currentNote     { -1    };    ///< Current note index (0..12), −1 if none.
    std::atomic<bool>   noteOn          { false };    ///< True if a physical key is pressed.
    std::atomic<bool>   mouseOn         { false };    ///< True if a virtual key is pressed.

    std::atomic<float>  osc1_freq_offset{ 0.0f };    ///< Frequency offset (−5 Hz..+5 Hz) for osc1.
    std::atomic<Waveform> osc1_waveform { Waveform::SINE }; ///< Waveform for osc1 (SINE/SAW/SQUARE).
    std::atomic<Waveform> osc2_waveform { Waveform::SAW  }; ///< Waveform for osc2 (always SAW).

    std::atomic<float>  attack          { 0.01f };    ///< Attack time in seconds (0..1).
    std::atomic<float>  release         { 0.5f  };    ///< Release time in seconds (0..2).
    std::atomic<float>  filter_cutoff   { 20000.0f }; ///< Filter cutoff frequency (20..20000 Hz).
    std::atomic<float>  filter_resonance{ 0.0f  };    ///< Filter resonance (0..1).

    std::atomic<float>  delay_time      { 0.3f  };    ///< Delay time in seconds (0.1..2.0).
    std::atomic<float>  delay_mix       { 0.5f  };    ///< Delay mix (dry/wet) (0..1).
};

#endif // SYNTHPARAMETERS_H

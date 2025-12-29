#ifndef SIMPLE_SYNTH_AUDIOGENERATOR_H
#define SIMPLE_SYNTH_AUDIOGENERATOR_H

#include "Delay.h"
#include "Envelope.h"
#include "Filter.h"
#include "Oscillator.h"
#include "portaudio.h"
#include "SynthParameters.h"

/// @class AudioGenerator
/// @brief Manages the entire audio chain: oscillators, envelope, filter, delay, and PortAudio.
///
/// AudioGenerator opens a PortAudio stream in callback mode (stereo, float) upon creation.
/// In each callback (~256 samples), it:
///  1. Reads shared (atomic) parameters provided by the UI (SynthParameters).
///  2. Generates 0, 1, or 2 oscillators based on osc1_enabled/osc2_enabled.
///  3. Applies the AR envelope (Attack/Release).
///  4. Updates the low-pass filter if cutoff/resonance changed, then processes each sample.
///  5. Updates the stereo delay (Delay L/R) if delay_time/delay_mix changed, then processes each sample.
///  6. Writes the final samples in stereo to the PortAudio output buffer.
class AudioGenerator {
public:
    /// @brief Constructor: initializes all audio modules and starts the PortAudio stream.
    /// @param params      Pointer to shared (atomic) parameters updated by the UI.
    /// @param sampleRate  Sampling frequency (Hz), e.g., 44100.0.
    AudioGenerator(SynthParameters* params, double sampleRate);

    /// @brief Opens and starts the PortAudio stream if not already done.
    /// (Automatically called in the constructor.)
    void init();

private:
    /// @brief Static callback called by PortAudio for each buffer (~256 samples).
    /// @param inputBuffer       Pointer to the input buffer (unused, nullptr).
    /// @param outputBuffer      Pointer to the output buffer (float stereo).
    /// @param framesPerBuffer   Number of frames (256).
    /// @param timeInfo          Time info provided by PortAudio (unused).
    /// @param statusFlags       Status flags from PortAudio (unused).
    /// @param userData          Pointer to the AudioGenerator instance (this).
    /// @return paContinue to continue streaming.
    static int audioCallback(const void* inputBuffer, void* outputBuffer,
                              unsigned long framesPerBuffer,
                              const PaStreamCallbackTimeInfo* timeInfo,
                              PaStreamCallbackFlags statusFlags,
                              void* userData);

    SynthParameters* sharedParams;  ///< Pointer to the parameters structure (atomics) updated by the UI.
    double sampleRate;              ///< Sampling frequency (e.g., 44100.0).
    PaStream* stream{ nullptr };    ///< Pointer to the PortAudio stream (or nullptr if not opened).

    Oscillator osc1;   ///< Oscillator 1 (SINE/SQUARE/SAW according to sharedParams->osc1_waveform).
    Oscillator osc2;   ///< Oscillator 2 (fixed SAW).
    Envelope   env;    ///< AR envelope generator (Attack/Release).
    Filter     filterL;///< Low-pass filter for the left channel.
    Filter     filterR;///< Low-pass filter for the right channel.
    Delay      delayL; ///< Stereo delay for the left channel.
    Delay      delayR; ///< Stereo delay for the right channel.

    int    lastNote      = -1;    ///< Previous note number (0…12), to avoid unnecessary recalculation.
    double lastFreq      = 0.0;   ///< Frequency calculated for lastNote.
    float  lastAttack    = -1.0f; ///< Remembers previous attack for lazy update (not used if AR only).
    float  lastRelease   = -1.0f; ///< Remembers previous release for lazy update.
    float  lastCutoff    = -1.0f; ///< Remembers previous cutoff for lazy filter update.
    float  lastResonance = -1.0f; ///< Remembers previous resonance for lazy filter update.
    float  lastDelayTime = -1.0f; ///< Remembers previous delay_time for lazy delay update.
    float  lastDelayMix  = -1.0f; ///< Remembers previous delay_mix for lazy delay update.
};

#endif // SIMPLE_SYNTH_AUDIOGENERATOR_H

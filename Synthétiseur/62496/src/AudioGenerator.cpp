
#include <iostream>
#include <cstring>
#include <cmath>
#include "../AudioGenerator.h"

constexpr int   FRAMES_PER_BUFFER = 256;
constexpr float AMPLITUDE   = 1.0f;

AudioGenerator::AudioGenerator(SynthParameters* params, double sampleRate)
    : sharedParams(params),
      sampleRate(sampleRate),
      osc1(sampleRate),
      osc2(sampleRate),
      filterL(sampleRate),
      filterR(sampleRate),
      env(sampleRate),
      delayL(sampleRate),
      delayR(sampleRate)
{
    delayL.reset();
    delayR.reset();
    init();
}

void AudioGenerator::init() {
    PaError err = Pa_OpenDefaultStream(&stream,
                                       0,           // no input channels
                                       2,           // stereo output
                                       paFloat32,// 32-bit float
                                       sampleRate,
                                       FRAMES_PER_BUFFER,
                                       audioCallback,
                                       this);
    if (err != paNoError) {
        std::cerr << "Erreur Pa_OpenDefaultStream: "
                  << Pa_GetErrorText(err) << "\n";
        return;
    }
    Pa_StartStream(stream);
}

int AudioGenerator::audioCallback(const void * ,
                                  void *outputBuffer,
                                  unsigned long framesPerBuffer,
                                  const PaStreamCallbackTimeInfo* ,
                                  PaStreamCallbackFlags ,
                                  void *userData)
{
    auto *self = static_cast<AudioGenerator*>(userData);
    float *out = static_cast<float*>(outputBuffer);

    bool   osc1On      = self->sharedParams->osc1_enabled.load();
    bool   osc2On      = self->sharedParams->osc2_enabled.load();
    int    currentNote = self->sharedParams->currentNote.load();
    bool   noteOn      = self->sharedParams->noteOn.load();
    bool   mouseOn     = self->sharedParams->mouseOn.load();
    float  freqOffset  = self->sharedParams->osc1_freq_offset.load();
    Waveform waveform1       = self->sharedParams->osc1_waveform.load();
    Waveform waveform2       = self->sharedParams->osc2_waveform.load();

    float  attackS     = self->sharedParams->attack.load();
    float  releaseS    = self->sharedParams->release.load();
    float  cutoff      = self->sharedParams->filter_cutoff.load();
    float  resonance   = self->sharedParams->filter_resonance.load();
    float  delayTime   = self->sharedParams->delay_time.load();
    float  delayMix    = self->sharedParams->delay_mix.load();

    // === 2) If no oscillator is enabled, output silence ===
    int numOsc = (osc1On ? 1 : 0) + (osc2On ? 1 : 0);
    if (numOsc == 0) {
         std::fill(out, out + 2 * framesPerBuffer, 0.0f);
        return paContinue;
    }

    // === 3) Update frequency if note changed ===
    if (currentNote != self->lastNote) {
        self->lastFreq = 220.0 * std::pow(2.0, currentNote / 12.0);
        self->lastNote = currentNote;
    }
    double freq1 = self->lastFreq + freqOffset;
    double freq2 = self->lastFreq;

    // === 4) Lazy-update filter coefficients if cutoff/resonance changed ===
    if (cutoff != self->lastCutoff || resonance != self->lastResonance) {
        self->filterL.setParams(cutoff, resonance);
        self->filterR.setParams(cutoff, resonance);
        self->lastCutoff    = cutoff;
        self->lastResonance = resonance;
    }
    // === 5) Lazy-update delay parameters if delayTime/delayMix changed ===
    if (delayTime != self->lastDelayTime || delayMix != self->lastDelayMix) {
        // 5) Mettre à jour les paramètres du delay pour L et R
        self->delayL.setParams(delayTime, delayMix);
        self->delayR.setParams(delayTime, delayMix);
        self->lastDelayTime = delayTime;
        self->lastDelayMix   = delayMix;
    }
    // === 6) Generate mono buffers for each oscillator ===
    float buf1[FRAMES_PER_BUFFER] = {0.0f};
    float buf2[FRAMES_PER_BUFFER] = {0.0f};
    float mix [FRAMES_PER_BUFFER] = {0.0f};

    if (osc1On) self->osc1.generate(buf1, framesPerBuffer, freq1, waveform1);
    if (osc2On) self->osc2.generate(buf2, framesPerBuffer, freq2, waveform2);

    for (unsigned i = 0; i < framesPerBuffer; ++i) {
        mix[i] = buf1[i] + buf2[i];
        // mix[i] may reach [-1.0, +1.0] if both oscillators are active
    }
    // === 7) Apply AR envelope ===

    bool gate = noteOn || mouseOn;
    self->env.process(mix, framesPerBuffer, gate, attackS, releaseS);

    // === 8)  filter, delay, and write stereo output ===
    for (unsigned i = 0; i < framesPerBuffer; ++i) {

        float gain   = AMPLITUDE / static_cast<float>(numOsc);
        float sample = mix[i] * gain;// ∈ [-0.5, +0.5]
        float filteredL = self->filterL.process(sample);
        float filteredR = self->filterR.process(sample);
        // outputs Left and right.
        out[2*i    ] = self->delayL.process(filteredL);
        out[2*i + 1] = self->delayR.process(filteredR);

    }

    return paContinue;
}

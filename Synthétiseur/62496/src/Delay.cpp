//
// Created by Reqzanod on 01-06-25.
//

#include "../Delay.h"
#include <algorithm>

Delay::Delay(double sampleRate)
    : sampleRate(sampleRate),
      writeIndex(0),
      delaySamples(0),
      mix(0.5f)
{
    // Allocate a buffer big enough for 2 seconds of audio plus one extra sample
    std::size_t maxSize = static_cast<std::size_t>(sampleRate * 2.0) + 1;
    buffer.assign(maxSize, 0.0f);
}

void Delay::setParams(float delayTimeSec, float mixVal) {

    mix = std::clamp(mixVal, 0.0f, 1.0f);
    // Compute maximum possible delay in seconds based on buffer size
    float maxDelaySec = static_cast<float>(buffer.size()) / static_cast<float>(sampleRate);
    if (delayTimeSec < 0.0f) delayTimeSec = 0.0f;
    if (delayTimeSec > maxDelaySec) delayTimeSec = maxDelaySec;

    // Convert delayTimeSec to integer number of samples
    delaySamples = static_cast<std::size_t>(delayTimeSec * sampleRate);
    if (delaySamples >= buffer.size()) {
        delaySamples = buffer.size() - 1;
    }
}

void Delay::reset() {
    std::fill(buffer.begin(), buffer.end(), 0.0f);
    writeIndex = 0;
}

float Delay::process(float input) {
    // Calculate the read index for the delayed sample
    std::size_t bufSize = buffer.size();
    std::size_t readIndex = (writeIndex + bufSize - delaySamples) % bufSize;

    float delayedSample = buffer[readIndex];
    // Write the current input into the circular buffer
    buffer[writeIndex] = input;
    // Increment writeIndex with wrap-around
    writeIndex = (writeIndex + 1) % bufSize;
    // Mix dry (input) and wet (delayedSample)
    return input * (1.0f - mix) + delayedSample * mix;
}
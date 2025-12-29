//
// Created by Reqzanod on 27-05-25.
//

#include "../Envelope.h"
#include <algorithm>

Envelope::Envelope(double sampleRate)
        : sampleRate(sampleRate), value(0.0)
{};


void Envelope::process(float* buf,
                       std::size_t frames,
                       bool gate,
                       float attackS,
                       float releaseS)
{

    // Compute attack and release increments in units of value per sample
    // If attackS == 0 → immediate attack (value jumps to 1.0 on first sample)
    const double atkStep =
        attackS  > 0.0 ? 1.0 / (attackS  * sampleRate) : 1.0;
    // If releaseS == 0 → immediate release (value jumps to 0.0 on first sample)
    const double relStep =
        releaseS > 0.0 ? 1.0 / (releaseS * sampleRate) : 1.0;

    for (std::size_t i = 0; i < frames; ++i)
    {
        if (gate)
        {
            // Linear attack: ramp from current value up to 1.0
            value += atkStep;
            if (value > 1.0) value = 1.0;
        }
        else
        {
            // Linear release: ramp from current value down to 0.0
            value -= relStep;
            if (value < 0.0) value = 0.0;
        }

        buf[i] = static_cast<float>(buf[i] * value);
    }
}
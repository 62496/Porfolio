//
// Created by Reqzanod on 29-05-25.
//

#include "../Filter.h"
#include <math.h>
Filter::Filter(double sampleRate): sampleRate(sampleRate){reset();}

void Filter::reset() {
    x1 = x2 = y1 = y2 = 0.0;
}
void Filter::setParams(double cutoffHz, double resonance) {
    // q controls resonance;
    double q     = 0.5 / (1.0 - resonance);
    double omega = 2.0 * M_PI * cutoffHz / sampleRate;
    double alpha = std::sin(omega) / (2.0 * q);
    double cosw  = std::cos(omega);
    double norm  = 1.0 / (1.0 + alpha);
    // Standard RBJ low-pass coefficients
    a0 = (1.0 - cosw) * 0.5 * norm;
    a1 = (1.0 - cosw) * norm;
    a2 = (1.0 - cosw) * 0.5 * norm;
    b1 = -2.0 * cosw * norm;
    b2 = (1.0 - alpha) * norm;
}
float Filter::process(float input) {
    double output = a0*input + a1*x1 + a2*x2
                    - b1*y1 - b2*y2;
    x2 = x1;  x1 = input;
    y2 = y1;  y1 = output;
    return static_cast<float>(output);
}

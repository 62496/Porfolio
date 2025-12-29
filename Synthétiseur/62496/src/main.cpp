#include <iostream>

#include "../AudioGenerator.h"
#include "../MainWindow.h"

SynthParameters parameters;
int main() {
    // 1) Initialiser PortAudio
    PaError err = Pa_Initialize();
    if (err != paNoError) {
        std::cerr << "Erreur Pa_Initialize: " << Pa_GetErrorText(err) << "\n";
        return -1;
    }

    AudioGenerator audio(&parameters, 44100.0);
    MainWindow mainWindow(&parameters);
    mainWindow.init();
    mainWindow.run();

    // 4) Nettoyage PortAudio
    Pa_Terminate();
    return 0;
}


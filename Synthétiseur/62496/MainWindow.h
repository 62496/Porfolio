#ifndef TESTINSTRUCT_MAINWINDOW_H
#define TESTINSTRUCT_MAINWINDOW_H

#include <SDL3/SDL.h>
#include "AudioGenerator.h"

/// @class MainWindow
/// @brief Manages the graphical interface (SDL + Dear ImGui) and passes parameters to AudioGenerator.
///
/// MainWindow creates an SDL window, initializes ImGui, runs the main loop (SDL events + ImGui rendering),
/// and updates the atomic values in SynthParameters each frame, which the audio callback then reads.
class MainWindow {
public:
    /// @brief Constructor: simply stores the pointer to shared parameters.
    /// @param params  Pointer to SynthParameters (used in draw()).
    MainWindow(SynthParameters* params);

    /// @brief Initializes SDL and ImGui, and creates the window and renderer.
    void init();

    /// @brief Main loop: handles SDL events, updates ImGui, draws the UI, and presents the screen.
    void run();

    /// @brief Draws the ImGui controls (oscillators, envelope, filter, delay, virtual keyboard).
    void draw();

private:
    SDL_Window*   window   { nullptr };  ///< Pointer to the SDL window.
    SDL_Renderer* renderer { nullptr };  ///< Pointer to the SDL renderer.
    SynthParameters* sharedParams;       ///< Pointer to shared parameters (atomics).
};

#endif // TESTINSTRUCT_MAINWINDOW_H

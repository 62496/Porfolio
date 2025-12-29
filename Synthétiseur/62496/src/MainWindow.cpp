

#include "../MainWindow.h"

#include <iostream>
#include <thread>

#include "../AudioGenerator.h"
#include "imgui.h"
#include "imgui_impl_sdl3.h"
#include "imgui_impl_sdlrenderer3.h"

constexpr float FRAMERATE = 60.0f;
constexpr std::chrono::duration<double, std::milli> TARGET_FRAMETIME(1000.0 / FRAMERATE);
MainWindow::MainWindow(SynthParameters* params)
  : sharedParams(params) {}
void MainWindow::init() {

    // Setup SDL
    if (!SDL_Init(SDL_INIT_VIDEO | SDL_INIT_GAMEPAD)) {
        SDL_Log("Error: SDL_Init(): %s\n", SDL_GetError());
        return;
    }
    // Create window with SDL_Renderer graphics context
    //Uint32 window_flags = SDL_WINDOW_HIDDEN;
    Uint32 window_flags = SDL_WINDOW_RESIZABLE ;

    window = SDL_CreateWindow("", 384, 268, window_flags);
    if (nullptr == window) {
        SDL_Log("Error: SDL_CreateWindow(): %s\n", SDL_GetError());
        return;
    }
    renderer = SDL_CreateRenderer(window, nullptr);
    SDL_SetRenderVSync(renderer, 1);
    if (nullptr == renderer) {
        SDL_Log("Error: SDL_CreateRenderer(): %s\n", SDL_GetError());
        return;
    }
    SDL_SetWindowPosition(
            window, SDL_WINDOWPOS_CENTERED, SDL_WINDOWPOS_CENTERED);
    SDL_ShowWindow(window);

    // Setup Dear ImGui context
    IMGUI_CHECKVERSION();
    ImGui::CreateContext();
    ImGuiIO& io = ImGui::GetIO(); (void)io;
    io.ConfigFlags |= ImGuiConfigFlags_NavEnableKeyboard;
    io.ConfigFlags |= ImGuiConfigFlags_NavEnableGamepad;

    // Setup DearImGui style
    ImGui::StyleColorsDark();
    ImGui::GetStyle().WindowRounding = 0.0f;

    // Setup Platform/Renderer backends
    ImGui_ImplSDL3_InitForSDLRenderer(window, renderer);
    ImGui_ImplSDLRenderer3_Init(renderer);

}

void MainWindow::run() {

    const auto clear_color = ImVec4(0.45f, 0.55f, 0.60f, 1.00f);

    bool done { false };
    while (!done){
        auto frameStart = std::chrono::high_resolution_clock::now();

        SDL_Event event;
        while (SDL_PollEvent(&event)){
            ImGui_ImplSDL3_ProcessEvent(&event);
            if (SDL_EVENT_QUIT == event.type)
                done = true;
            if ((SDL_EVENT_WINDOW_CLOSE_REQUESTED == event.type)
                && (SDL_GetWindowID(window) == event.window.windowID))
                done = true;

            const SDL_Scancode keymap[12] = {
                SDL_SCANCODE_Q, SDL_SCANCODE_Z, SDL_SCANCODE_S,
                SDL_SCANCODE_E, SDL_SCANCODE_D, SDL_SCANCODE_F,
                SDL_SCANCODE_T, SDL_SCANCODE_G, SDL_SCANCODE_Y,
                SDL_SCANCODE_H, SDL_SCANCODE_U, SDL_SCANCODE_J
            };

            if (event.type == SDL_EVENT_KEY_DOWN) {
                for (int i = 0; i < 12; ++i) {
                    if (event.key.scancode == keymap[i]) {
                        printf("hello keyboard");
                        sharedParams->currentNote = i;
                        sharedParams->noteOn = true;
                        break;
                    }
                }
            }

            if (event.type == SDL_EVENT_KEY_UP) {
                for (int i = 0; i < 12; ++i) {
                    if (event.key.scancode == keymap[i]&& sharedParams->currentNote == i  ) {
                        printf("bye keyboard");
                        sharedParams->noteOn = false;
                        break;
                    }
                }
            }
        }


        // Start the Dear ImGui frame
        ImGui_ImplSDLRenderer3_NewFrame();
        ImGui_ImplSDL3_NewFrame();
        ImGui::NewFrame();

        // all the UI code description
        draw();

        // Rendering
        ImGui::Render();
        SDL_SetRenderDrawColorFloat(renderer,
                                    clear_color.x, clear_color.y, clear_color.z, clear_color.w);
        SDL_RenderClear(renderer);
        ImGui_ImplSDLRenderer3_RenderDrawData(ImGui::GetDrawData(), renderer);
        SDL_RenderPresent(renderer);

        // Calculate time spent and sleep if needed
        auto frameEnd = std::chrono::high_resolution_clock::now();
        auto frameDuration = frameEnd - frameStart;
        if (frameDuration < TARGET_FRAMETIME) {
            std::this_thread::sleep_for(TARGET_FRAMETIME - frameDuration);
        }
    }

    // Cleanup
    ImGui_ImplSDLRenderer3_Shutdown();
    ImGui_ImplSDL3_Shutdown();
    ImGui::DestroyContext();

    SDL_DestroyRenderer(renderer);
    SDL_DestroyWindow(window);
    SDL_Quit();
}

void MainWindow::draw() {
    ImGui::Begin("My Window");
    ImGui::Text("Synthétiseur - 4DEV4D");

    static bool osc1_enabled = true;
    static bool osc2_enabled = false;
    static int osc1_waveform = 0;
    static float osc1_freq_offset = 0.0f;

    static float attack = 0.1f;
    static float release = 0.5f;
    static float filter_cutoff = 20000.0f;
    static float filter_resonance = 0.0f;
    static float delay_time = 0.3f;
    static float delay_mix = 0.5f;

    ImGui::Checkbox("OSC 1", &osc1_enabled);
    const char* waveforms[] = { "SINE", "SAW", "SQUARE" };
    ImGui::Combo("OSC1 Waveform", &osc1_waveform, waveforms, IM_ARRAYSIZE(waveforms));
    ImGui::SliderFloat("OSC1 Frequency Offset", &osc1_freq_offset, -5.0f, 5.0f);

    ImGui::Checkbox("OSC 2", &osc2_enabled);

    ImGui::SliderFloat("Attack", &attack, 0.0f, 1.0f);
    ImGui::SliderFloat("Release", &release, 0.0f, 2.0f);
    ImGui::SliderFloat("Filter Cutoff", &filter_cutoff, 20.0f, 20000.0f);
    ImGui::SliderFloat("Filter Resonance", &filter_resonance, 0.0f, 1.0f);
    ImGui::SliderFloat("Delay Time", &delay_time, 0.1f, 2.0f);
    ImGui::SliderFloat("Delay Mix", &delay_mix, 0.0f, 1.0f);

    ImGui::Separator();
    ImGui::Text("Clavier Virtuel :");

    bool isActive = false;
    int  note = -1;
    for (int i = 0; i < 12; ++i) {
        ImGui::SameLine();
        char label[4];
        sprintf(label, "%d", i + 1);

        ImGui::PushID(i);
        ImGui::Button(label, ImVec2(30,0));
        bool isPress = ImGui::IsItemActive();
        ImGui::PopID();

        if (isPress) {
            isActive  = true;
            note = i;
        }
    }
    if (isActive) {
        if (sharedParams->noteOn) {
            sharedParams->noteOn = false;
        }
        sharedParams->currentNote = note;
        sharedParams->mouseOn       = true;
    } else {
        sharedParams->mouseOn       = false;
    }

    sharedParams->osc1_enabled = osc1_enabled;
    sharedParams->osc1_waveform = static_cast<Waveform>(osc1_waveform);
    sharedParams->osc2_enabled = osc2_enabled;
    sharedParams->osc1_freq_offset = osc1_freq_offset;
    sharedParams->attack  = attack;
    sharedParams->release = release;
    sharedParams->filter_cutoff    = filter_cutoff;
    sharedParams->filter_resonance = filter_resonance;
    sharedParams->delay_time = delay_time;
    sharedParams->delay_mix = delay_mix;
    ImGui::End();
}


# Voxel Craft

A simple voxel‐based terrain demo built in Java using LWJGL. Procedurally generates chunks of terrain, allows switching between third‐person (“free camera”) and first‐person (“player mode”), and supports basic collision, zoom, and rendering.

---

## Requirements

- Java 17 (or higher)
- Gradle 7.x (wrapper included)
- OpenGL 2.1+ compatible graphics card
- LWJGL 3 (bundled via Gradle)

---

## Getting Started

1. **Clone this repository**  
   ```bash
   git clone https://github.com/your-username/voxel-craft.git
   cd voxel-craft
Build & Run with Gradle
From the project root:
./gradlew run
This will compile sources, fetch LWJGL dependencies, and launch the game window.
Controls

Camera / Third‐Person View
W / S
Zoom the camera closer (W) or farther (S) from the focal point.
Hold Left Mouse Button + Move Mouse
Rotate the camera around the scene (pitch & yaw).
Keys 1 / 2
Adjust noise scale (terrain frequency).
1 decreases scale (finer/denser terrain).
2 increases scale (wider/shallower hills).
Keys 3 / 4
Adjust heightMag (terrain height).
3 decreases height magnitude (flatter terrain).
4 increases height magnitude (taller peaks).
Key 9
Toggle Player Mode on/off.
Player Mode (First‐Person)
W / A / S / D
Move forward, left, backward, and right (collision enabled).
SPACE
Jump (works only when “on ground”).
Mouse Movement
Look around (pitch & yaw).
Key 9
Toggle back to free‐camera mode.
Features

Procedural terrain generated via OpenSimplex noise.
Dynamic chunk loading/unloading based on player/camera position.
Basic collision & gravity for the player.
Real‐time adjustment of noise parameters:
Scale (keys 1/2)
Height Magnitude (keys 3/4)
Toggleable Player Mode (key 9) for first‐person exploration.
WASD + mouse‐look controls when in Player Mode.
Known Issues & TODOs

Chunk Unloading can sometimes leave holes or “black” blocks—needs better handling of partial‐chunk removal.
Performance drops when large numbers of chunks load at once; consider queuing chunk generation or spreading work over multiple frames.
Lighting / Shading improvements: currently uses a flat color sky and un‐lit blocks.
Shaders: no fog, no dynamic shadows; adding a post‐processing smoke/fog filter would improve realism.
Block Selection / Building: no block‐placing or breaking yet.
Audio: no ambient sounds or effects.
# electric_sheep

# Electric Dreams

<img align="right" src="https://i.imgur.com/5yP4YhP.gif" height="350">

This repo contains an example animation using Rive's new Android runtime.

## Overview

The animation acts as an intedeterminate loader, and its states can be toggled by interactions with the UI.

The codebase comprises of two activities, MainActivity, and LoadActivity.

LoadActivity runs an animation that lives in ElectricSheep.kt, that is custom View that loads the file using the Rive Android runtime APIs, and manages the lifecycle of the animation, reacting to state changes.

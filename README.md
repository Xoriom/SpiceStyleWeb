# SpiceStyle 🎶

SpiceStyle is an Android app that connects to Spotify and provides a customizable ui inspired by spicetify.  
The goal is to bring some of the freedom of the desktop tool Spicetify (https://spicetify.app/) to mobile.

---

## ✨ Features (Work in Progress)
- 🎵 See what’s currently playing
- 🎚 Control playback (play/pause, next, previous, shuffle, repeat)
- 🖼 Album artwork display with Glide
- ⏱ Seek bar with elapsed & total time
- 🔐 Spotify Web API integration (OAuth via Custom Tabs)
## Coming Soon
- Album art background
- Landscape mode
- customizable ui designs using xml

---

## 🚀 Getting Started

### Requirements
- **Android Studio (Giraffe/Koala or newer)**
- **Android SDK 34**
- **Java 17** (already configured in Gradle)
- A [Spotify Developer](https://developer.spotify.com/dashboard/) app with:
  - Redirect URI set to `spicestyle://callback`
  - Your Spotify account (and testers) added under **Users and Access**

---

### Building the App

Clone the repo:
```bash
git clone https://github.com/yourusername/SpiceStyle.git
cd SpiceStyle

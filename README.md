# 🧩 Channel blocker patches

Morphe patches for YouTube. Adds a **"Block channel"** button to the video
flyout (three-dot) menu; blocked channels are removed from search results and
recommendations.

## ❓ About

Patches for apps I like.

### How to use these patches

Click the button below, or manually add this repo url to Morphe Manager: https://github.com/needfish/channel-blocker-patches

<a href="https://morphe.software/add-source?github=7kitan/channel-blocker-patches">
  <img src=".github/assets/add-to-morphe.svg" alt="Add to Morphe" height="48">
</a>

## 🩹 Patches list

<!-- PATCHES_START EXPANDED -->
> **[v1.0.0-dev.1](https://github.com/needfish/channel-blocker-patches/releases/tag/v1.0.0-dev.1)**&nbsp;&nbsp;•&nbsp;&nbsp;`dev`&nbsp;&nbsp;•&nbsp;&nbsp;1 patches total
<details open>
<summary>📦 YouTube&nbsp;&nbsp;•&nbsp;&nbsp;1 patch</summary>
<br>

**Package name:** `com.google.android.youtube`

**🎯 Supported versions:**

| 21.30.216 | 21.28.208 | 21.23.492 |
| :---: | :---: | :---: |

| 💊&nbsp;Patch | 📜&nbsp;Description | ⚙️&nbsp;Options |
|----------|----------------|-----------|
| [Block channel](#block-channel) | Adds a 'Block channel' button to the video menu. Blocked channels are hidden from search results and video recommendations. |  |

</details>

<!-- PATCHES_END -->

### 🛠️ Building locally

- Run `./gradlew buildAndroid`
- The built patches .mpp file is found in `patches/build/libs/patches-*.mpp`
- Patch the mpp file using [Morphe-Desktop](https://github.com/MorpheApp/morphe-desktop)
  like any other patch bundle.

See the [Morphe documentation](https://github.com/MorpheApp/morphe-documentation) for more information.

## 📜 License

UserXYZ Patches are licensed under the [GNU General Public License v3.0](LICENSE)

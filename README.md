# C1 Deutsch — daily German programme

Android/Compose app combining general German practice with colloquial language, automotive engineering and youth-football coaching.

**Status:** working programme foundation with 28 fully authored, playable catalogue entries across seven stages. The content is original training material, not independently teacher-validated or an official C1 certification. The home screen distinguishes actual quiz results, self-reflection and unassessed competencies. Lesson completion never grants C1.

See [PROGRAMME_IMPLEMENTATION.md](PROGRAMME_IMPLEMENTATION.md) for the complete 28-unit map, progression rules, implementation inventory and unfinished work.

## Build locally

Requirements: JDK 17 and Android SDK 34 (platform/build tools). The Gradle wrapper is included. Set `sdk.dir` in your local, ignored `local.properties`.

Windows PowerShell:

```powershell
.\gradlew.bat :app:assembleDebug
.\gradlew.bat :app:testDebugUnitTest :app:lintDebug
```

Git Bash:

```sh
./gradlew :app:assembleDebug
./gradlew :app:testDebugUnitTest :app:lintDebug
```

The app is local-first and has no in-app AI service, account backend, or network dependency for the curriculum. Synthetic TTS and Android speech recognition depend on the device's configured providers. Do not remove debug signing settings just to build. Local SDK paths and `debug.keystore` are ignored.

Output: `app/build/outputs/apk/debug/app-debug.apk`.

## Use it

1. Open **Programm**. Choose **Einstellungen** for your name, app-interface language (German/English), app-wide Trackside/Silver/Pitch colors and compact layout. Course and practice content remains in German.
2. Choose a 20-, 60- or 90-minute time budget. These are plans, not measured timers.
3. Search the learning path by unit title, objective or context, and bookmark units to filter to saved lessons. On wide screens, primary navigation moves to a rail and dashboard content stays centered at a readable width.
4. Use the dashboard's prominent lesson button to start or continue a unit. Work through the reading and explicitly synthetic listening script; unfinished written responses autosave locally by lesson, task and context. Explicitly saving a response records it as self-reflection, not a grade.
5. Use the four context prompts to practise general, social, automotive and football communication. Every authored lesson now offers a solo application task for the selected context. The adaptive route advances after a correct objective quiz and two distinct unassisted text tasks with no currently detected pattern. That is a practice checkpoint, not mastery or a C1 result; all authored lessons remain available for free practice.
6. Review saved responses, due retrieval and timed text-practice sessions. In **Hör-Intelligenz Labor**, open **Öffentliche Originalquellen** for Easy German, DW, Deutschlandfunk and ARD Sounds; selected lessons also link directly to relevant Easy German episodes. **Deine Buchaufnahmen** opens audio you select from device storage or a cloud provider. For Sicher! C1/1, choose relevant `audio AB` files whose names include `_L02_`, `_L05_` or `_L06_`. For Aspekte neu C1, copy the owned Lehrbuch-CD folders to your phone and match the printed track number to the chapter's CD folder (`CD 1 K 1-3`, `CD 2 K 4-7`, or `CD 3 K 8-10`); the app intentionally does not guess mappings from generic `Titel NN` filenames. The app keeps the provider URI and plays the file in place; it does not copy, upload or bundle it. Lesson cards include optional, newly written prompts for relevant book chapters, with page and audio-folder references rather than reproduced book exercises.

The app's adaptive route combines objectively scored multiple-choice questions, a narrow set of deterministic German text-pattern checks and learner-reported real-life difficulties to choose practice and remediation. A route checkpoint is only a practice-navigation rule: clean text does not prove correctness or C1. Transfer prompts now include solo self-checks, but they are visible practice tasks—not protected unseen exams or independent evidence. Timed sessions use familiar authored prompts and remain practice, not Goethe exam simulations or protected assessments. Separate listening practice lets you choose your own audio and save notes; speaking practice can record your spoken response locally. These workflows are not automatically scored, do not assess acoustic speaking quality, and do not supply unfamiliar-task evidence. Feedback challenges are stored locally against the original response excerpt; they do not train or validate an automated grader. An optional feedback button prepares a correction-focused prompt and lets you inspect and edit the exact share text before Android opens its share sheet; the app does not call an AI provider, upload automatically, receive feedback or validate its conclusions. Do not share sensitive text. The separate conversation simulator provides authored base scenarios plus two deterministic multi-turn variations; TTS remains synthetic. The user-controlled JSON export includes attempts, assessments and text responses, feedback challenges, local review workflow records, personal entries, listening notes, drafts, preferences and retained legacy learning records. Audio bytes, audio-recording filenames and selected document URIs are intentionally excluded. Export and delete-all are local app operations; device/cloud backups are separate. The app does not download, bundle or transcribe publisher media. Existing speaking practice offers Android speech recognition, which measures transcript agreement—not acoustic pronunciation. German TTS voices and speech recognition may use the chosen Android provider's network services.

Each saved lesson response can also have a local human-review note with ten rubric dimensions. The entered name and scores are not verified, remain separate from the learner's attempt, and never change progression.

## Install and debug on a phone

Enable Developer options and USB debugging, connect the phone, unlock it and approve its debugging prompt.

```powershell
& "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe" devices
.\gradlew.bat :app:installDebug
```

Do not proceed while the device is `unauthorized`. Android Studio can alternatively launch the app on a configured emulator.

## Verified checks

- `:app:assembleDebug`: passed.
- `:app:testDebugUnitTest`: **223 tests across 35 classes, zero failures/errors/skips**.
- `:app:lintDebug`: passed with no errors; 81 warnings remain.
- `:app:minifyReleaseWithR8 :app:shrinkReleaseRes`: passed with R8/minification and resource shrinking enabled; R8 emitted 91 Kotlin-metadata compatibility warnings.
- A signed release APK was not built: only the debug keystore is present; no upload keystore was found.
- Robolectric UI tests cover dashboard labels, lesson navigation, preferences and session plans.
- Physical-device microphone/TTS behavior and emulator deployment remain unverified; no device was connected at the final ADB check.

## Known remaining work

- Verify user-selected book-audio playback, URI persistence and audio focus on a physical Android device; no publisher recording is bundled in the APK.
- Commission or provide application-authorized native multi-speaker recordings with verified transcripts; the owned-audio manifest is a rights-gated intake contract, not bundled content.
- If exam-like readiness is desired, implement genuine, independently scored listening/speaking assessment and protected unfamiliar tasks; timed sessions remain familiar text practice, with separate unscored local audio-practice workflows.
- Broaden and validate automatic diagnosis beyond the current narrow text checks; connected speech, pronunciation, fluency, implied meaning and multi-speaker comprehension remain unmeasured. External AI feedback is user-selected and unvalidated; it is not an in-app grader.
- Test export restore/round-trip and backup/restore; cross-device authentication/sync, crash reporting and Play compliance still require production services and credentials.
- Run the physical-device and four-week learner pilot, including microphone, German ASR/TTS, audio focus, accessibility and layout validation.

Qualified independent curriculum/rubric review and authentic protected assessment are required before formal graduation or any external C1-readiness claim. The graduation checker has a non-bypassable gap because the app cannot yet record or verify that validation. A learner pilot is still needed to evaluate usability and outcomes, but cannot replace expert review. None of these gates blocks ordinary private practice.

Tests use Robolectric SDK 35 with JDK 17. Java time desugaring supports the existing streak code on minSdk 24/25.

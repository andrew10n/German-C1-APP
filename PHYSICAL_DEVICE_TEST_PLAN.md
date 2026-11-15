# Physical Android Validation Plan

The current environment has produced a debug APK but has no usable connected device. This plan is ready to execute when a physical Android phone is connected.

## Installation and lifecycle

Install the debug APK, launch it, complete onboarding, close and reopen the app, rotate the device, send it to the background and kill/relaunch the process. Confirm that learner preferences, attempts, assessment responses, active task index and completed-session history survive each lifecycle event.

## Audio and microphone

Test German Android TTS availability, unavailable-TTS messaging, speech-recognition permission grant and denial, microphone interruption by a phone call or another audio app, Bluetooth headset routing, pause/resume, and audio focus. Confirm that synthetic TTS is labelled synthetic and that a failed microphone path leaves manual text entry available.

## Assessment integrity

Start a weekly integrated assessment, close the app between tasks, resume it, submit a response after the deadline, and verify that late responses are rejected. Confirm that task IDs, variation IDs, evidence type, unfamiliar flag and timestamps remain stable after resumption.

## Accessibility

Run TalkBack through onboarding, dashboard, settings, unit detail, assessment and delete confirmation. Test 200% font scale, bold text, high contrast, touch targets, keyboard navigation where supported, reduced motion and landscape layout. Verify that status is not conveyed by colour alone.

## Privacy and recovery

Export local data, inspect that the export contains no hidden audio bytes or access tokens, perform delete-all, relaunch and confirm that learner data is absent. Test Android backup/restore only with an approved test account and verify the documented retention and deletion behavior.

## Evidence capture

Record device model, Android version, app version, build hash, test date, tester role, pass/fail, reproduction steps and privacy impact for every failure. Do not attach learner responses, reviewer comments or recordings to ordinary crash tickets.

# C1 Deutsch — Production Readiness Package

## Current status

The local Android foundation builds successfully with `assembleDebug`, `testDebugUnitTest` and `lintDebug`. This document distinguishes implemented local behavior from production services that still require provider credentials, legal review, real devices or human operations.

Latest local verification is recorded in `README.md`. Room schema 14 and API 33+ segmented recognition code are compile/test validated; external storage providers and device recognition implementations still require physical-device checks.

Release minification and resource shrinking are enabled. `minifyReleaseWithR8` and `shrinkReleaseRes` completed successfully, but R8 reported Kotlin metadata compatibility warnings; the signed release artifact remains unverified because no upload keystore is present.

## Privacy and data handling

### Currently implemented

- Programme attempts, assessment sessions, reviews, challenges and real-life entries are stored locally in Room.
- JSON export is user-controlled.
- Delete-all requires explicit confirmation and clears local database tables and programme preferences.
- Synthetic TTS is disclosed as synthetic.
- The onboarding flow explains that practice data is not a formal CEFR result.
- Android backup and data-extraction rules are explicit rather than relying on templates.

### Before production

- Publish a privacy policy that names every data category, purpose, retention period, processor and deletion route.
- Decide whether microphone recordings are stored, transcribed only, or discarded after transcription.
- Obtain explicit consent before storing or sharing voice recordings.
- Add account-level deletion and export if cloud sync is introduced.
- Define retention and deletion for reviewer comments, reviewer identity and audit records.
- Complete Play Data Safety declarations and regional privacy review.
- Review the fictional practice content for personal-data and professional-advice risks.

## Authentication and synchronisation boundary

The current app is local-first and has no account or server identity. A production sync service must provide:

1. account creation, recovery and deletion;
2. authenticated reviewer and learner roles;
3. encrypted transport and encrypted server storage;
4. append-only evidence IDs and conflict-safe merges;
5. explicit device/session revocation;
6. server-side authorization for reviewer assignments;
7. export/delete jobs with completion receipts;
8. offline queueing with retry and duplicate protection;
9. audit timestamps in UTC;
10. migration/version compatibility with the local Room schema.

Do not sync raw microphone recordings by default. Prefer explicit consent, short-lived upload tokens and a documented retention policy.

## Crash and operational monitoring

Before release, select and configure one crash/ANR provider. The provider must be configured outside source control and must not receive learner response text, reviewer comments, microphone audio or access tokens in exception metadata. Add:

- release-version and build-channel tags;
- database migration failure diagnostics without response contents;
- offline-safe event buffering;
- opt-out/consent behavior where required;
- alert thresholds and on-call ownership;
- a documented rollback procedure;
- a data-subject deletion path.

## Release checklist

- [ ] Release keystore generated and stored in an approved secret manager.
- [ ] Release signing is separate from the debug keystore.
- [ ] No credentials, API keys or service-account files are in the repository.
- [ ] `assembleRelease`, unit tests and lint pass in a clean environment.
- [x] R8/minification and resource shrinking tasks complete.
- [ ] Resolve Kotlin-metadata compatibility warnings and verify the optimized artifact on a physical device.
- [ ] Room migrations are tested from every supported historical version.
- [ ] Backup restore and delete-all are tested on physical devices.
- [ ] Microphone permission denial and German TTS absence have usable fallbacks.
- [ ] TalkBack, font scaling, contrast and touch targets are tested.
- [ ] Privacy policy, terms and support contact are published.
- [ ] Play Data Safety and content declarations are complete.
- [ ] Crash/ANR monitoring is configured for the release application ID.
- [ ] Staged rollout, rollback and incident contacts are documented.
- [ ] Native-audio licenses and consent records are archived with the release.
- [ ] Four-week pilot findings are reviewed before any public claim.

## Security and evidence rules

Internal thresholds remain practice safeguards, not official CEFR certification. A production service must never allow a learner-controlled client to manufacture `HUMAN_REVIEW` evidence, alter immutable review history or claim a verified reviewer identity. The server must re-check authorization, assignment, rubric completeness and audit linkage.

## Required external inputs

This package is ready for provider selection and implementation, but it cannot create absent credentials, a backend account, a legal privacy decision, a physical device or an external crash-service project. Those inputs must be supplied before production integration is activated.

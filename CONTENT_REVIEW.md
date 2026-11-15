# German content review

## Review status

This is an editorial and research-backed review, not a certified CEFR assessment or a substitute for a native-speaker/domain expert. The content is original and should be described as practice material, not teacher-approved or exam-calibrated.

### Completed in this pass

- Reviewed the grammar note for all 28 authored catalogue entries for categorical claims, rule/example alignment, register distinctions, and overgeneralization.
- Checked the relevant claims against the CEFR Companion Volume, Goethe-Zertifikat C1 public format, Duden grammar guidance, and IDS grammis research.
- Corrected three overstatements or misleading formulations:
  - `s5-spontaneous`: replaced the unqualified claim that spoken *weil* commonly takes verb-second position and the invalid sentence-initial example. The new note limits this pattern to a marked, postposed spoken construction and distinguishes it from an integrated subordinate clause.
  - `s6-specialist`: changed “Dativ nach aufgrund … gilt als Fehler” to a register-aware recommendation: use genitive in formal writing; do not label regional/colloquial usage categorically ungrammatical.
  - `s6-checkpoint`: clarified that an *indem* clause can have the same or a different subject; only the illustrated *um … zu* construction is constrained to a shared subject.
- The close read is now in progress. Additional corrections made in this pass:
  - `s2-data`: removed the implication that one season-long or matched-team comparison would establish causation; the model now calls for planned repeated measurements, a comparison group where feasible, relevant covariates, and explicit residual uncertainty.
  - `s5-nuance`: made the explanations of *gell*, *schaffe*, and *Hock di na* less categorical and clarified that dialect spellings and pragmatic force vary by region and speaker.
  - `s5-revise`: replaced an awkward coaching instruction and clarified that modal particles should be assessed for function rather than automatically deleted.
  - `s4-argument` and `s3-disagree`: corrected *auf dem Kleinfeld* and framed multi-position development as this fictional team’s approach, not a universal rule for every U12 team.
  - `s3-negotiate`: rewrote an unnatural solo-practice instruction.
- Further subject-matter and legal-language corrections:
  - `s4-ethics`: rewrote the youth-sports data example so it presents safeguards as a discussion proposal, calls for checking purpose, data minimization and lawful basis, and says that consent alone does not make processing lawful. This matters especially for health-related values and minors; the lesson now avoids presenting an opt-out as a complete legal recipe.
  - `s2-summarise` (the FMEA comparison unit): clarified that the fictional source reports a high action priority but omits its S/O/D ratings, so learners cannot verify how that rating was derived from this excerpt.
  - `s6-specialist`: separated a formal AIAG/VDA Action Priority from the team's interim internal urgency. Where O and D are unassessed, the formal AP is explicitly undetermined.
- Adapted the core transfer prompts for the stated single-learner use case: removed requirements for another person to supply unseen materials or score the response, added concrete self-check questions, and relabeled stage 6 as independent transfer practice rather than verification. These tasks remain visible practice prompts, not protected unseen exams or evidence of C1.
- This is a review of the 28 lesson payloads in the active `CurriculumCatalogue` (two optional stage-0 orientation lessons and 26 sequenced core lessons). The separate legacy `core.CurriculumCatalog` is not the learner-facing catalogue and does not carry equivalent authored lessons; it remains a product/code-audit follow-up.
- Existing unit coverage claims remain bounded: TTS is synthetic, regional examples are practice prompts, and automotive/football scenarios are fictional.

## Research basis

- Council of Europe, [CEFR Companion Volume and descriptor resources](https://www.coe.int/en/web/common-european-framework-reference-languages/cefr-companion-volume-and-its-language-versions): C1 is described with communicative “can do” scales, including mediation and interaction; it is not a fixed list of grammar topics.
- Goethe-Institut, [Goethe-Zertifikat C1](https://www.goethe.de/ins/de/en/prf/gzc1.cfm): the public exam format covers listening, reading, writing, and speaking. The app can practise related skills but must not imply that unit completion establishes exam readiness.
- Leibniz-Institut für Deutsche Sprache, [Verbstellung nach *weil*](https://grammis.ids-mannheim.de/fragen/133) and [Subjunktoren mit Hauptsatz-Stellung](https://grammis.ids-mannheim.de/systematische-grammatik/1607): verb-second after *weil* is a real spoken-language construction with pragmatic and discourse conditions; it is not an unrestricted replacement for verb-final subordinate clauses.
- Duden, [Konjunktiv I oder II?](https://www.duden.de/sprachwissen/sprachratgeber/konjunktiv-1-oder-2): confirms the indirect-speech function of Konjunktiv I, Konjunktiv II for irrealis/potential and as a substitute where forms coincide.
- Duden, [*aufgrund*](https://www.duden.de/rechtschreibung/aufgrund_Praeposition) and [Genitive prepositions](https://www.duden.de/sprachwissen/sprachratgeber/Pr%C3%A4positionen-mit-Genitiv): supports genitive as the formal target for *aufgrund*.
- University of Tübingen, [Sprachatlas Baden-Württemberg](https://escience-center.uni-tuebingen.de/escience/sprachatlas/) and [research on regional speech in everyday life](https://uni-tuebingen.de/fakultaeten/wirtschafts-und-sozialwissenschaftliche-fakultaet/faecher/fachbereich-sozialwissenschaften/empirische-kulturwissenschaft/forschung/ta-sprache/forschung/sprachalltag-in-nord-baden-wuerttemberg/): supports treating regional speech as locally variable rather than as one uniform “Swabian” rule set.
- German Federal Commissioner for Data Protection, [DSGVO/BDSG guide](https://www.bfdi.bund.de/SharedDocs/Downloads/DE/Broschueren/INFO1.pdf?__blob=publicationFile&v=24): explains that consent has validity and voluntariness requirements, and that health data are special-category data. The youth-sports scenario is therefore framed as a question for qualified legal review, not legal advice.
- AIAG, [AIAG & VDA FMEA Handbook overview](https://www.aiag.org/training-and-resources/manuals/details/FMEAAV-1) and [Action Priority white paper](https://www.aiag.org/docs/default-source/quality-/new_aiag_vda_fmea_-whitepaper.pdf?sfvrsn=6923e9d_4): supports distinguishing the S/O/D-based AP method from an informal interim urgency label.

## Remaining review work

1. Continue a field-by-field close read of all readings, dialogues, expression lists, quiz distractors, model answers, and feedback examples. The grammar-note pass and targeted copy edits do not establish that every line is idiomatic or factually coherent.
2. Review all regional German examples against documented speech corpora and clearly identify which examples are dialect, regiolect, colloquial German, or context-sensitive pragmatic readings. Do not teach them as reliable promises about individual speakers.
3. Independently check automotive and youth-football vocabulary and scenario plausibility. Keep fictional examples and safety disclaimers visible.
4. Recheck the revised content in the app UI and update content QA so it catches language-specific risks, not just missing fields and superficial draft markers.
5. Keep an explicit list of uncertain or preference-sensitive language judgments instead of presenting them as settled corrections.
6. Resolve the duplicate active/legacy catalogue implementations and ensure the content inventory shown to learners matches the content actually delivered.

## Limitations

Written sources can verify grammar and public exam structure, but they cannot establish that a fictional dialogue sounds natural across German-speaking regions, that a voice is authentic, or that a prompt measures C1 performance reliably. Those claims need recordings, corpus sampling, and learner testing. The app currently has no licensed native multi-speaker audio, so its TTS remains listening practice only.

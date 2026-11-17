import type {
  AdaptiveControlDecision,
  ProgrammeLanguage,
  WeaknessProfile,
  WeaknessSignal,
} from '../types';
import { WeaknessEngine } from './weaknessEngine';

function t(language: ProgrammeLanguage, de: string, en: string): string {
  return language === 'EN' ? en : de;
}

export function taskLabel(task: string, language: ProgrammeLanguage = 'DE'): string {
  switch (task) {
    case 'listening':
      return t(language, 'Hören', 'Listening');
    case 'speaking':
      return t(language, 'Sprechen', 'Speaking');
    case 'writing':
      return t(language, 'Schreiben', 'Writing');
    case 'interaction':
      return t(language, 'Interaktion', 'Interaction');
    case 'controlled':
      return t(language, 'Formulieren', 'Formulation');
    case 'quiz':
      return t(language, 'Verstehen', 'Comprehension');
    case 'retrieval':
      return t(language, 'Abruf', 'Retrieval');
    case 'reading':
      return t(language, 'Lesen', 'Reading');
    default:
      return t(language, 'Transfer', 'Transfer');
  }
}

export function signalLabel(
  signal: WeaknessSignal,
  language: ProgrammeLanguage = 'DE',
): string {
  switch (signal) {
    case 'SOUND_RECOGNITION':
      return t(
        language,
        'Laute in natürlicher Sprache erkennen',
        'Recognizing sounds in natural speech',
      );
    case 'WORD_SEGMENTATION':
      return t(
        language,
        'Wörter im Redefluss trennen',
        'Segmenting words in connected speech',
      );
    case 'LISTENING_VOCABULARY':
      return t(
        language,
        'Wortschatz beim Hören abrufen',
        'Recalling vocabulary while listening',
      );
    case 'SITUATIONAL_INFERENCE':
      return t(
        language,
        'Situationen und Absichten deuten',
        'Interpreting situations and intent',
      );
    case 'INFORMATION_RETENTION':
      return t(
        language,
        'Informationen über Äußerungen hinweg behalten',
        'Retaining information across turns',
      );
    case 'ACTIVE_RETRIEVAL':
      return t(
        language,
        'Ausdrücke aktiv abrufen',
        'Actively retrieving expressions',
      );
    case 'SENTENCE_FORMULATION':
      return t(
        language,
        'Sätze spontan formulieren',
        'Formulating sentences spontaneously',
      );
    case 'GRAMMAR_UNDER_PRESSURE':
      return t(language, 'Grammatik unter Druck', 'Grammar under pressure');
    case 'NATURALNESS':
      return t(
        language,
        'Natürlich und idiomatisch formulieren',
        'Expressing ideas naturally and idiomatically',
      );
    case 'REGISTER':
      return t(
        language,
        'Register passend wählen',
        'Choosing an appropriate register',
      );
    case 'RESPONSE_INITIATION':
      return t(language, 'Antworten beginnen', 'Starting responses');
    case 'CONVERSATIONAL_REPAIR':
      return t(
        language,
        'Nachfragen und Missverständnisse reparieren',
        'Asking follow-up questions and repairing misunderstandings',
      );
    case 'INTERACTION_MANAGEMENT':
      return t(
        language,
        'Unerwartete Gesprächsbeiträge steuern',
        'Handling unexpected turns',
      );
    case 'IMPLIED_MEANING':
      return t(
        language,
        'Andeutungen und Ironie einordnen',
        'Interpreting implication and irony',
      );
    case 'MULTI_SPEAKER_TRACKING':
      return t(
        language,
        'Mehrere Sprecher verfolgen',
        'Following multiple speakers',
      );
  }
}

export function adaptiveExplanation(
  decision: AdaptiveControlDecision,
  profile: WeaknessProfile,
  language: ProgrammeLanguage = 'DE',
): string {
  if (!decision.primaryTaskId) {
    return t(
      language,
      'Der geführte Kursweg ist abgeschlossen; aktuell ist keine Lektion verfügbar.',
      'The guided course path is complete; no lesson is currently available.',
    );
  }
  const focus = taskLabel(decision.primaryTaskId, language);
  const signal = WeaknessEngine.top(profile).find(
    (s) => s.signal === decision.focusSignal,
  );

  switch (decision.reasonCode) {
    case 'PERSONAL_REPAIR_DUE':
      return t(
        language,
        `Ein persönlicher Übungsschritt ist fällig. Heute: ${focus}. Das ist Übungsnavigation, keine Kompetenzbewertung.`,
        `A personal practice step is due. Today's focus: ${focus}. This guides practice; it is not a proficiency rating.`,
      );
    case 'LESSON_REVIEW_DUE':
      return t(
        language,
        `Der Wiederholungsplan ist fällig. Heute beginnt die Lektion mit aktivem Abruf: ${focus}.`,
        `A scheduled review is due. Today's lesson starts with active retrieval: ${focus}.`,
      );
    case 'QUIZ_REPAIR':
      return t(
        language,
        'Die Verständnisprüfung braucht einen weiteren Versuch. Erst danach führt der Lernweg weiter.',
        'The comprehension check needs another attempt. The guided path continues after it.',
      );
    case 'RESPONSE_REPAIR':
      return t(
        language,
        `Ein lokaler Hinweis in einer gespeicherten Antwort führt zu gezielter Überarbeitung: ${focus}. Einzelne Textmuster belegen keine Gesamtkompetenz.`,
        `A local signal in a saved response calls for focused revision: ${focus}. One text pattern does not establish overall ability.`,
      );
    case 'FIRST_RETRIEVAL':
      return t(
        language,
        'Beginne ohne Vorlage mit aktivem Abruf. Danach führt dich der Plan Schritt für Schritt durch Input, Hören und Anwendung.',
        'Start with a no-notes retrieval attempt. The plan then guides you through input, listening, and application.',
      );
    case 'NEXT_INPUT':
      return t(
        language,
        `Dein erster Abruf ist gespeichert. Als Nächstes folgt der Lektionsinput: ${focus}.`,
        `Your first retrieval attempt is saved. Next comes the lesson input: ${focus}.`,
      );
    case 'NEXT_LISTENING':
      return t(
        language,
        `Der Lektionsinput ist bearbeitet. Als Nächstes folgt das integrierte Hörtraining: ${focus}.`,
        `The lesson input is complete. Next comes integrated listening practice: ${focus}.`,
      );
    case 'NEXT_COMPREHENSION_CHECK':
      return t(
        language,
        `Input und Hören sind bearbeitet. Prüfe jetzt das Textverständnis: ${focus}.`,
        `Input and listening are complete. Now check your comprehension: ${focus}.`,
      );
    case 'NEXT_CONTROLLED_PRACTICE':
      return t(
        language,
        `Das Verständnis ist gesichert. Jetzt folgt angeleitete Formulierung: ${focus}.`,
        `Comprehension is supported. Now move to guided formulation: ${focus}.`,
      );
    case 'NEXT_PRODUCTION':
      return t(
        language,
        `Jetzt folgt eine eigenständige Anwendung in einem weiteren Aufgabentyp: ${focus}.`,
        `Now apply the lesson independently in another task type: ${focus}.`,
      );
    case 'RECURRING_SIGNAL':
      if (!signal) {
        return t(
          language,
          `Ein wiederkehrendes Übungssignal lenkt die nächste Aufgabe: ${focus}.`,
          `A recurring practice signal guides the next task: ${focus}.`,
        );
      }
      return t(
        language,
        `Das Übungssignal „${signalLabel(signal.signal, language)}“ trat in ${signal.sampleCount} Einträgen auf. Deshalb folgt ${focus}. Das ist keine Kompetenzbewertung.`,
        `The practice signal “${signalLabel(signal.signal, language)}” appeared in ${signal.sampleCount} records, so next is ${focus}. This is not a proficiency rating.`,
      );
    case 'BALANCED_PRACTICE':
      return t(
        language,
        `Der Kernpfad ist bearbeitet. Als Nächstes wählen wir einen zuletzt weniger geübten Bereich: ${focus}.`,
        `The core path is complete. Next, practice an area used less recently: ${focus}.`,
      );
    case 'COURSE_COMPLETE':
      return t(
        language,
        'Der geführte Kursweg ist abgeschlossen; aktuell ist keine Lektion verfügbar.',
        'The guided course path is complete; no lesson is currently available.',
      );
  }
}

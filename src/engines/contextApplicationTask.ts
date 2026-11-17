import type { ContextTrack, CurriculumUnit } from '../types';

export const ContextApplicationTask = {
  build(unit: CurriculumUnit, context: ContextTrack): string {
    if (!unit.content) {
      throw new Error('Context application requires an authored unit.');
    }
    const scenario = unit.contexts[context];
    if (!scenario) {
      throw new Error(`Unit ${unit.id} has no authored scenario for ${context}.`);
    }
    return [
      `Einheit: ${unit.title}`,
      `Lernziele: ${unit.objectives.join('; ')}`,
      `Gewählter Kontext (${context}): ${scenario}`,
      'Aufgabe: Schreibe drei bis fünf neue Sätze und prüfe selbst Ziel, Adressat, Register sowie eine offene oder unsichere Stelle.',
      'Erfinde keine technischen oder sachlichen Angaben; kennzeichne Annahmen.',
    ].join('\n');
  },
};

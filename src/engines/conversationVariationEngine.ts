import type { ConversationScenario, ConversationTurn } from '../types';

function followUpFor(
  scenario: ConversationScenario,
  variation: number,
): [string, string] {
  const text = `${scenario.title.toLowerCase()} ${scenario.rolePartner.toLowerCase()}`;
  if (text.includes('sport') || text.includes('trainer') || text.includes('jugend')) {
    return variation === 1
      ? [
          'Und wie erklären Sie das jetzt den Kindern, ohne sie zu verunsichern?',
          'Passen Sie die Erklärung an Kinder an und geben Sie eine klare nächste Handlung.',
        ]
      : [
          'Ein anderer Trainer schlägt gerade das Gegenteil vor. Wie beziehen Sie ihn ein?',
          'Würdigen Sie den Einwand und sichern Sie eine gemeinsame, praktikable Entscheidung.',
        ];
  }
  if (
    text.includes('technik') ||
    text.includes('entwicklung') ||
    text.includes('ki-') ||
    text.includes('ingenieur')
  ) {
    return variation === 1
      ? [
          'Welche Messung würde Ihre Annahme am schnellsten bestätigen oder widerlegen?',
          'Benennen Sie eine konkrete Prüfung und trennen Sie Beobachtung von Schlussfolgerung.',
        ]
      : [
          'Die Datenlage ist noch lückenhaft. Was kommunizieren Sie jetzt an die Leitung?',
          'Formulieren Sie eine belastbare Zwischenmeldung mit Unsicherheit und nächstem Schritt.',
        ];
  }
  return variation === 1
    ? [
        'Was genau erwarten Sie jetzt von mir, damit wir weiterkommen?',
        'Klären Sie das Ziel und antworten Sie höflich, aber konkret.',
      ]
    : [
        'Ich sehe das anders. Welche Ihrer Annahmen ist für Sie am wenigsten sicher?',
        'Reagieren Sie auf den Widerspruch, ohne abzublocken, und markieren Sie Unsicherheit.',
      ];
}

export const ConversationVariationEngine = {
  expand(scenarios: ConversationScenario[]): ConversationScenario[] {
    return scenarios.flatMap((scenario) => [
      scenario,
      ConversationVariationEngine.vary(scenario, 1),
      ConversationVariationEngine.vary(scenario, 2),
    ]);
  },

  vary(scenario: ConversationScenario, variation: 1 | 2): ConversationScenario {
    const [partnerSpeech, expectedUserRolePrompt] = followUpFor(scenario, variation);
    const lastTurn = scenario.turns[scenario.turns.length - 1];
    const turn: ConversationTurn = {
      partnerSpeech,
      expectedUserRolePrompt,
      suggestedC1Connectors:
        variation === 1
          ? ['Wenn ich Sie richtig verstehe', 'Allerdings', 'Daraus folgt']
          : ['Das ist ein berechtigter Einwand', 'Gleichzeitig', 'Konkret bedeutet das'],
      targetRegister: lastTurn?.targetRegister ?? '',
      speakerProfile: 'FOLLOW_UP',
    };

    return {
      ...scenario,
      id: `${scenario.id}_v${variation}`,
      title: `${scenario.title} · Variation ${variation}`,
      openingStatement:
        variation === 1
          ? `${scenario.openingStatement} Ich möchte außerdem wissen, wie Sie mit einem unerwarteten Einwand umgehen.`
          : `${scenario.openingStatement} Bitte antworten Sie zunächst kurz und fragen Sie anschließend gezielt nach.`,
      turns: [...scenario.turns, turn],
      unexpectedInterruption: {
        ...scenario.unexpectedInterruption,
        triggerAfterSecond: Math.max(
          3,
          scenario.unexpectedInterruption.triggerAfterSecond - variation,
        ),
        partnerInterruptSpeech:
          variation === 1
            ? 'Entschuldigung, ich muss kurz nachhaken: Was wäre denn Ihr konkreter nächster Schritt?'
            : 'Moment, so einfach ist das nicht. Welche Information fehlt Ihnen noch für eine belastbare Entscheidung?',
        reactionObjective:
          variation === 1
            ? 'Greifen Sie den Einwand auf, strukturieren Sie Ihre Antwort und bleiben Sie beim Ziel.'
            : 'Bitten Sie gezielt um Klärung und formulieren Sie eine vorläufige, begründete Antwort.',
      },
      variationLabel: `Unvorhersehbare Variation ${variation}`,
    };
  },
};

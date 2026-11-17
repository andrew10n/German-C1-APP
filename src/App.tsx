import React, { Suspense, lazy } from 'react';
import {
  AlertCircle,
  ArrowLeft,
  BookOpen,
  Compass,
  Moon,
  Sun,
  UserCheck,
} from 'lucide-react';
import { AppProvider, useApp } from './state/AppContext';
import { ErrorIntelligenceScreen } from './screens/ErrorIntelligenceScreen';
import { IntelligenceProfileScreen } from './screens/IntelligenceProfileScreen';
import { ProgrammeScreen } from './screens/ProgrammeScreen';
import { VocabTrainingScreen } from './screens/VocabTrainingScreen';
import type { Screen } from './types';

const AdaptiveSessionScreen = lazy(() =>
  import('./screens/AdaptiveSessionScreen').then((m) => ({
    default: m.AdaptiveSessionScreen,
  })),
);
const BaselineDiagnosticScreen = lazy(() =>
  import('./screens/BaselineDiagnosticScreen').then((m) => ({
    default: m.BaselineDiagnosticScreen,
  })),
);
const ConversationSimulatorScreen = lazy(() =>
  import('./screens/ConversationSimulatorScreen').then((m) => ({
    default: m.ConversationSimulatorScreen,
  })),
);
const FootballCoachTrackScreen = lazy(() =>
  import('./screens/FootballCoachTrackScreen').then((m) => ({
    default: m.FootballCoachTrackScreen,
  })),
);
const ListeningLabScreen = lazy(() =>
  import('./screens/ListeningLabScreen').then((m) => ({
    default: m.ListeningLabScreen,
  })),
);
const MediationScreen = lazy(() =>
  import('./screens/MediationScreen').then((m) => ({
    default: m.MediationScreen,
  })),
);
const SpeakingPracticeScreen = lazy(() =>
  import('./screens/SpeakingPracticeScreen').then((m) => ({
    default: m.SpeakingPracticeScreen,
  })),
);

const TOP_LEVEL_DESTINATIONS: {
  screen: Screen;
  label: string;
  icon: React.FC<{ className?: string }>;
}[] = [
  { screen: 'Programme', label: 'Lektionen', icon: Compass },
  { screen: 'IntelligenceProfile', label: 'Auswertung', icon: UserCheck },
  { screen: 'ErrorIntelligence', label: 'Fehlerlog', icon: AlertCircle },
  { screen: 'VocabTraining', label: 'Wortschatz', icon: BookOpen },
];

const SCREEN_TITLES: Partial<Record<Screen, string>> = {
  DailySession: 'Tagestraining',
  ListeningLab: 'Hör-Labor',
  SpeakingLab: 'Aussprache-Labor',
  ConversationSimulator: 'Sprech-Simulator',
  Mediation: 'Sprachmittlung',
  BaselineDiagnostic: 'MCQ-Screening',
  FootballCoachTrack: 'Fußball-Trainer (U15)',
};

const ShellContent: React.FC = () => {
  const {
    currentScreen,
    navigateTo,
    navigateBack,
    canNavigateBack,
    isDark,
    toggleTheme,
  } = useApp();

  const isTopLevel =
    currentScreen === 'Programme' ||
    currentScreen === 'IntelligenceProfile' ||
    currentScreen === 'ErrorIntelligence' ||
    currentScreen === 'VocabTraining';

  const highlightedTab: Screen = isTopLevel ? currentScreen : 'Programme';

  const renderScreen = () => {
    switch (currentScreen) {
      case 'Programme':
        return <ProgrammeScreen />;
      case 'IntelligenceProfile':
        return <IntelligenceProfileScreen />;
      case 'ErrorIntelligence':
        return <ErrorIntelligenceScreen />;
      case 'VocabTraining':
        return <VocabTrainingScreen />;
      case 'DailySession':
        return <AdaptiveSessionScreen />;
      case 'ListeningLab':
        return <ListeningLabScreen />;
      case 'SpeakingLab':
        return <SpeakingPracticeScreen />;
      case 'ConversationSimulator':
        return <ConversationSimulatorScreen />;
      case 'Mediation':
        return <MediationScreen />;
      case 'BaselineDiagnostic':
        return <BaselineDiagnosticScreen />;
      case 'FootballCoachTrack':
        return <FootballCoachTrackScreen />;
    }
  };

  return (
    <div className="min-h-screen bg-[var(--bg)] text-[var(--ink)] md:flex">
      {/* Desktop / Tablet Navigation Rail (>= 768px / md) */}
      <aside className="hidden md:flex md:w-24 md:shrink-0 md:flex-col md:items-center md:justify-between md:border-r md:border-[var(--line)] md:bg-[var(--surface)] md:py-6">
        <div className="flex flex-col items-center gap-6">
          <button
            type="button"
            onClick={() => navigateTo('Programme')}
            className="flex flex-col items-center gap-1.5"
            aria-label="C1 Deutsch Startseite"
          >
            <img
              src="/images/img_app_icon.jpg"
              alt="C1 Deutsch"
              className="h-11 w-11 rounded-2xl border border-[var(--line)] object-cover"
            />
            <span className="font-display text-xs font-extrabold tracking-wider text-[var(--accent-ink)]">
              C1 DE
            </span>
          </button>

          <nav className="flex flex-col gap-3" aria-label="Hauptnavigation">
            {TOP_LEVEL_DESTINATIONS.map((dest) => {
              const Icon = dest.icon;
              const active = highlightedTab === dest.screen;
              return (
                <button
                  key={dest.screen}
                  type="button"
                  onClick={() => navigateTo(dest.screen)}
                  className={`flex w-20 flex-col items-center gap-1 rounded-2xl px-1 py-2.5 text-[11px] font-bold whitespace-nowrap transition ${
                    active
                      ? 'bg-[var(--accent-soft)] text-[var(--accent-ink)]'
                      : 'text-[var(--ink2)] hover:bg-[var(--surface2)] hover:text-[var(--ink)]'
                  }`}
                >
                  <Icon className="h-5 w-5" />
                  <span>{dest.label}</span>
                </button>
              );
            })}
          </nav>
        </div>

        <button
          type="button"
          onClick={toggleTheme}
          className="flex h-10 w-10 items-center justify-center rounded-2xl border border-[var(--line)] bg-[var(--surface2)] text-[var(--ink)]"
          aria-label="Farbschema wechseln"
        >
          {isDark ? <Sun className="h-4 w-4" /> : <Moon className="h-4 w-4" />}
        </button>
      </aside>

      {/* Main Content Area */}
      <div className="flex-1">
        {/* Top Bar for optional sub-screens */}
        {!isTopLevel && (
          <div className="sticky top-0 z-30 border-b border-[var(--line)] bg-[var(--surface)]/90 px-4 py-3 backdrop-blur-md">
            <div className="mx-auto flex max-w-4xl items-center justify-between">
              <button
                type="button"
                onClick={() => (canNavigateBack ? navigateBack() : navigateTo('Programme'))}
                className="inline-flex items-center gap-2 rounded-xl border border-[var(--line)] bg-[var(--surface2)] px-3 py-1.5 text-xs font-bold text-[var(--ink)] hover:border-[var(--accent)]"
              >
                <ArrowLeft className="h-4 w-4" />
                Zurück
              </button>
              <span className="font-display text-sm font-extrabold uppercase tracking-wider text-[var(--ink2)]">
                {SCREEN_TITLES[currentScreen] ?? 'C1 Deutsch'}
              </span>
              <button
                type="button"
                onClick={toggleTheme}
                className="flex h-8 w-8 items-center justify-center rounded-xl border border-[var(--line)] bg-[var(--surface2)] text-[var(--ink)] md:hidden"
                aria-label="Design wechseln"
              >
                {isDark ? <Sun className="h-3.5 w-3.5" /> : <Moon className="h-3.5 w-3.5" />}
              </button>
            </div>
          </div>
        )}

        <main
          className={`mx-auto max-w-4xl px-4 py-6 ${
            isTopLevel ? 'pb-28 md:pb-10' : 'pb-10'
          }`}
        >
          <Suspense
            fallback={
              <div className="rounded-3xl border border-[var(--line)] bg-[var(--surface)] p-8 text-center text-xs font-bold text-[var(--ink2)]">
                Lernmodul wird geladen...
              </div>
            }
          >
            {renderScreen()}
          </Suspense>
        </main>
      </div>

      {/* Mobile Floating Bottom Dock */}
      {isTopLevel && (
        <nav
          aria-label="Mobile Hauptnavigation"
          className="fixed bottom-4 left-1/2 z-40 flex -translate-x-1/2 items-center gap-1 rounded-3xl border border-[var(--line)] bg-[var(--surface)]/95 p-1.5 shadow-2xl backdrop-blur-md md:hidden"
        >
          {TOP_LEVEL_DESTINATIONS.map((dest) => {
            const Icon = dest.icon;
            const active = highlightedTab === dest.screen;
            return (
              <button
                key={dest.screen}
                type="button"
                onClick={() => navigateTo(dest.screen)}
                className={`flex items-center gap-1.5 rounded-2xl px-3 py-2 text-xs font-extrabold whitespace-nowrap transition ${
                  active
                    ? 'bg-[var(--accent)] text-[var(--on-accent)]'
                    : 'text-[var(--ink2)] hover:text-[var(--ink)]'
                }`}
              >
                <Icon className="h-4 w-4 shrink-0" />
                <span>{dest.label}</span>
              </button>
            );
          })}
        </nav>
      )}
    </div>
  );
};

export function App() {
  return (
    <AppProvider>
      <ShellContent />
    </AppProvider>
  );
}

export default App;

import berlinDiscourseImg from '../assets/images/context_berlin_discourse_1794869187951.jpg';
import evEngineeringImg from '../assets/images/context_ev_engineering_1794869189344.jpg';
import footballCoachingImg from '../assets/images/context_football_coaching_1794869189941.jpg';
import socialSwabiaImg from '../assets/images/context_social_swabia_1794869189202.jpg';
import type { ContextTrack } from '../types';

export interface ContextArtworkMeta {
  src: string;
  alt: string;
  kicker: string;
  subtitle: string;
}

export const CONTEXT_ARTWORK: Record<ContextTrack, ContextArtworkMeta> = {
  GENERAL: {
    src: berlinDiscourseImg,
    alt: 'Illustration der Berliner Architektur mit Kuppel und vernetzten Gesprächsbögen',
    kicker: 'Berlin & Diskurs',
    subtitle: 'Sachlich strukturieren, Standpunkte klären',
  },
  SOCIAL: {
    src: socialSwabiaImg,
    alt: 'Illustration eines gemeinsamen Vereinstischs mit zwei verbindenden Gesprächsströmen',
    kicker: 'Verein & Alltag (Schwaben)',
    subtitle: 'Zwischentöne deuten, freundlich nachfragen',
  },
  ENGINEERING: {
    src: evEngineeringImg,
    alt: 'Isometrische Illustration eines 800V-Batteriepakets mit Drehmoment-Kurve und Prüfstand',
    kicker: 'EV-Antrieb & Prüfstand',
    subtitle: 'Hypothesen, Daten und Nachweisgrenzen trennen',
  },
  COACHING: {
    src: footballCoachingImg,
    alt: 'Taktische Draufsicht auf ein Fußballfeld mit Passdreiecken und Halbraum-Laufwegen',
    kicker: 'Fußball-Coaching (U13–U15)',
    subtitle: 'Übungen anleiten, Regeln klar abgrenzen',
  },
};

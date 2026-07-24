# 📋 PLAN DE DÉVELOPPEMENT – My Places
> **Document destiné à l'agent IA développeur.**
> Lire ce fichier EN PREMIER, puis consulter `ARCHITECTURE_MYPLACES.md` pour les détails techniques de chaque composant.

---

## 🎯 Vue d'ensemble du projet

**My Places** est une application Android native de **journal intime géographique**. L'utilisateur épingle des lieux sur une carte Google Maps, les enrichit de photos, descriptions et émojis, et peut partager son journal avec des amis via un fichier JSON.

| Paramètre | Valeur |
|---|---|
| **Langage** | Kotlin |
| **UI** | Jetpack Compose + Material 3 |
| **Carte** | Google Maps SDK + Maps Compose |
| **Base de données** | Room (SQLite) |
| **Réseau** | Retrofit + api-adresse.data.gouv.fr |
| **Architecture** | MVVM (sans Hilt – DI manuelle via AppContainer) |
| **Min SDK** | API 26 (Android 8.0) |
| **Target SDK** | API 35 |
| **Package** | `com.myplaces.app` |

---

## ⚠️ Règles critiques pour l'agent IA

> **ATTENTION – Ces règles sont non négociables. Les violer cassera le projet.**

1. **PAS de Hilt** → DI uniquement via `AppContainer` instancié dans `MyPlacesApplication`
2. **PAS de Base64 pour les photos** → stocker uniquement le chemin fichier (`String`) dans Room
3. **PAS de multi-Activity** → une seule `MainActivity` avec un `NavHost` Compose
4. **PAS de Fragments** → 100% Jetpack Compose
5. **Toujours utiliser KSP** (pas KAPT) pour le compilateur Room
6. **Toujours utiliser le Version Catalog** `libs.versions.toml` pour les dépendances

---

## 🧑 Actions humaines préalables (BLOCKER)

> **IMPORTANT – Ces actions doivent être faites par un humain AVANT que l'agent démarre.**

| # | Action | Pourquoi c'est bloquant |
|---|---|---|
| 🧑1 | **Créer une clé API Google Maps** sur Google Cloud Console – activer Maps SDK for Android | Sans clé API, la carte ne s'affiche pas du tout |
| 🧑2 | Récupérer le **SHA-1** du keystore debug et le lier à la clé API | La carte ne fonctionne que si le SHA-1 correspond |
| 🧑3 | **Placer la clé API** dans `AndroidManifest.xml` (meta-data `com.google.android.geo.API_KEY`) | Requis au runtime |
| 🧑4 | **Tester sur un device physique** (API 26+) | GPS + CameraX mal simulés sur émulateur |

---

## 🗂️ Structure des fichiers à créer

```
com.myplaces.app/
│
├── MyPlacesApplication.kt
├── MainActivity.kt
│
├── di/
│   └── AppContainer.kt
│
├── data/
│   ├── local/
│   │   ├── entity/PlaceEntity.kt
│   │   ├── dao/PlaceDao.kt
│   │   └── AppDatabase.kt
│   ├── remote/
│   │   ├── api/GeocodingApiService.kt
│   │   └── model/GeocodingResponse.kt
│   └── repository/
│       └── PlaceRepository.kt
│
├── ui/
│   ├── navigation/
│   │   ├── Screen.kt
│   │   └── NavGraph.kt
│   ├── biometric/
│   │   └── BiometricLockScreen.kt
│   ├── map/
│   │   ├── MapScreen.kt
│   │   └── MapViewModel.kt
│   ├── addplace/
│   │   ├── AddPlaceScreen.kt
│   │   └── AddPlaceViewModel.kt
│   ├── camera/
│   │   └── CameraScreen.kt
│   ├── settings/
│   │   ├── SettingsScreen.kt
│   │   └── SettingsViewModel.kt
│   └── components/
│       ├── EmojiPicker.kt
│       ├── PlaceDetailSheet.kt
│       └── PlaceMarker.kt
│
└── util/
    ├── FileUtils.kt
    ├── JsonExporter.kt
    ├── JsonImporter.kt
    └── BiometricHelper.kt
```

---

## 📅 Phases de développement

### Phase 1 – Fondations (sans UI, sans réseau)
**Objectif** : Avoir un projet qui compile proprement avec toutes les dépendances.

- [ ] 1.1 Vérifier/mettre à jour `gradle/libs.versions.toml` (voir ARCHITECTURE §2.1)
- [ ] 1.2 Vérifier/mettre à jour `app/build.gradle.kts` (voir ARCHITECTURE §2.2)
- [ ] 1.3 Vérifier/mettre à jour `AndroidManifest.xml` avec toutes les permissions (§2.3)
- [ ] 1.4 Créer `MyPlacesApplication.kt` + déclarer dans le Manifest (`android:name=".MyPlacesApplication"`)
- [ ] 1.5 Créer `PlaceEntity.kt` – entité Room exacte (§4.1)
- [ ] 1.6 Créer `PlaceDao.kt` – toutes les méthodes DAO (§4.2)
- [ ] 1.7 Créer `AppDatabase.kt` – singleton Room (§4.3)
- [ ] 1.8 Créer `GeocodingApiService.kt` + `GeocodingResponse.kt` (§5.2–5.3)
- [ ] 1.9 Créer `PlaceRepository.kt` avec méthodes CRUD + `resolveAddress()` (§5.4)
- [ ] 1.10 Créer `AppContainer.kt` – wiring de tout (§12.1)
- [ ] 1.11 Mettre à jour `MyPlacesApplication` pour instancier `AppContainer` (§12.2)
- [ ] 1.12 Configurer DataStore + clés `user_uuid`, `user_name`, `biometric_enabled` (§13.1–13.2)

**Vérification Phase 1** : `./gradlew assembleDebug` doit compiler sans erreur.

---

### Phase 2 – Navigation + Écran Carte
**Objectif** : Avoir la carte Google Maps affichée avec navigation fonctionnelle.

- [ ] 2.1 Créer `Screen.kt` – sealed class des routes (§6.2)
- [ ] 2.2 Créer `NavGraph.kt` – NavHost avec toutes les routes (§6.2–6.3)
- [ ] 2.3 Mettre à jour `MainActivity.kt` – héberger NavHost, lire DataStore pour biométrie (§9.2)
- [ ] 2.4 Créer `MapViewModel.kt` – collecte `getAllPlaces()` en Flow
- [ ] 2.5 Créer `MapScreen.kt` – Google Maps plein écran, marqueurs basiques, FAB "+" (§6.3)

> NOTE : Utiliser des marqueurs par défaut pour commencer. Marqueurs emoji = Phase 7.

**Vérification Phase 2** : L'app se lance, la carte s'affiche, le FAB "+" est visible.

---

### Phase 3 – Ajout et détail d'un lieu
**Objectif** : Pouvoir ajouter un lieu et voir son détail.

- [ ] 3.1 Créer `EmojiPicker.kt` – `LazyVerticalGrid` cliquable (§11.1–11.2)
- [ ] 3.2 Créer `AddPlaceViewModel.kt` – state formulaire + `insertPlace()` + `resolveAddress()`
- [ ] 3.3 Créer `AddPlaceScreen.kt` – formulaire complet (titre, description, EmojiPicker) (§6.3)
- [ ] 3.4 Créer `PlaceDetailSheet.kt` – `ModalBottomSheet` infos + bouton supprimer (§6.3)
- [ ] 3.5 Connecter `MapScreen` : long press → navigate vers `AddPlaceScreen`
- [ ] 3.6 Connecter `MapScreen` : clic marqueur → ouvrir `PlaceDetailSheet`
- [ ] 3.7 Après validation : reverse geocoding → insert Room → retour carte avec nouveau marqueur

**Vérification Phase 3** : Long press → remplir formulaire → valider → marqueur sur carte → clic → BottomSheet correct.

---

### Phase 4 – Caméra et Photos
**Objectif** : Associer une photo à un lieu.

- [ ] 4.1 Créer `FileUtils.kt` – sauvegarde photo dans `context.filesDir/photos/` (§7.2)
- [ ] 4.2 Créer `CameraScreen.kt` – preview CameraX + capture + sauvegarde + retour chemin (§7.2)
- [ ] 4.3 Intégrer flux galerie dans `AddPlaceScreen` – `GetContent` + copie fichier (§7.3)
- [ ] 4.4 Afficher photo preview dans `AddPlaceScreen` avant validation
- [ ] 4.5 Afficher photo dans `PlaceDetailSheet` via `AsyncImage` Coil (§7.4)

**Vérification Phase 4** : Prendre une photo → aperçu dans formulaire → validé → visible dans détail.

---

### Phase 5 – Import / Export / Paramètres
**Objectif** : Partager son journal avec un ami.

- [ ] 5.1 Créer `JsonExporter.kt` – sérialisation Gson + Share Intent (§8.2)
- [ ] 5.2 Créer `JsonImporter.kt` – `OpenDocument` + désérialisation + anti-doublon (§8.3)
- [ ] 5.3 Créer `SettingsViewModel.kt` – DataStore biométrie + import/export
- [ ] 5.4 Créer `SettingsScreen.kt` – switch biométrie + boutons + Snackbar (§6.3)

**Vérification Phase 5** :
- Export → fichier JSON avec les bons champs (§8.1)
- Import → lieux visibles sur carte
- Ré-import → 0 doublon

---

### Phase 6 – Sécurité Biométrique
**Objectif** : Verrouiller l'app avec empreinte ou code PIN.

- [ ] 6.1 Créer `BiometricHelper.kt` – wrapper `BiometricPrompt` (§9.3)
- [ ] 6.2 Créer `BiometricLockScreen.kt` – écran verrou avec retry (§9.2)
- [ ] 6.3 Connecter dans `MainActivity` : si `biometric_enabled = true` → afficher verrou avant NavHost

**Vérification Phase 6** :
- Biométrie ON + relancer → prompt visible
- Biométrie OFF → accès direct à la carte

---

### Phase 7 – Polish & Finitions
**Objectif** : App robuste, belle, testée.

- [ ] 7.1 Permissions runtime avec Accompanist (location, caméra, galerie) (§10.2)
- [ ] 7.2 Marqueurs personnalisés avec émoji (custom `BitmapDescriptor` via Canvas)
- [ ] 7.3 Gestion états vides, loading spinners, erreurs réseau
- [ ] 7.4 Transitions de navigation animées
- [ ] 7.5 Thème Material 3 cohérent (couleurs, typographie)
- [ ] 7.6 Tests unitaires : `PlaceDaoTest`, `JsonExporterTest`, `JsonImporterTest`, `PlaceRepositoryTest` (§16)

**Vérification Phase 7** : `./gradlew testDebugUnitTest` passe. Tableau de vérification manuelle complet.

---

## 🧪 Tableau de vérification manuelle finale

| # | Test | Résultat attendu |
|---|---|---|
| 1 | Long press sur la carte | Ouvre `AddPlaceScreen` avec coordonnées pré-remplies |
| 2 | Remplir formulaire + valider | Marqueur avec l'émoji choisi apparaît sur la carte |
| 3 | Cliquer sur un marqueur | `PlaceDetailSheet` avec titre, date, adresse, émoji, photo, description |
| 4 | Bouton supprimer dans le détail | Le lieu disparaît de la carte |
| 5 | Prendre une photo | Photo visible dans formulaire ET dans détail |
| 6 | Choisir depuis galerie | Même comportement que la caméra |
| 7 | Exporter mon journal | Fichier `places_export.json` proposé au partage |
| 8 | Importer JSON d'un ami | Lieux de l'ami visibles sur carte, sans écraser les miens |
| 9 | Ré-importer le même JSON | 0 doublon créé |
| 10 | Activer biométrie + relancer | Prompt biométrique (empreinte ou PIN) bloque l'accès |
| 11 | Désactiver biométrie + relancer | Accès direct à la carte, pas de prompt |
| 12 | Coordonnées hors France | Adresse = null (pas de crash), le lieu est quand même créé |

---

## 📁 Documents de référence

| Document | Contenu |
|---|---|
| `ARCHITECTURE_MYPLACES.md` | Détail technique exhaustif : code complet de chaque classe, configs Gradle, schéma BDD, diagrammes de flux |
| `PLAN_DEVELOPPEMENT.md` | Ce fichier – roadmap actionnable par phases |

---

## 💡 Conseils pour l'agent IA

1. **Lire `ARCHITECTURE_MYPLACES.md` en entier** avant d'écrire la première ligne de code
2. **Ne pas sauter de phase** : chaque phase dépend de la précédente – compiler à la fin de chaque phase
3. **En cas de doute**, l'architecture contient le code exact à utiliser
4. **Marqueurs emoji** : créer un `BitmapDescriptor` depuis un `Canvas` Android, pas depuis un fichier image
5. **DataStore** : utiliser `context.dataStore` (extension créée dans `AppContainer`), jamais instancier directement
6. **Room** : toujours KSP (`ksp(libs.androidx.room.compiler)`), jamais KAPT
7. **Ne jamais stocker l'URI galerie** directement – toujours copier le fichier en interne d'abord
8. **`authorId`** de chaque lieu = UUID de l'utilisateur local (DataStore), généré une seule fois au 1er lancement

# 🛠️ Dépannage build — My Place

> Document destiné aux développeurs **et aux agents IA**.
> Chaque section = un problème rencontré + diagnostic + correctif appliqué + vérification.

---

## Problème #1 — `jlink executable ... does not exist` (JRE incomplète Red Hat)

**Première rencontre :** 2026‑08‑26 · **Statut :** résolu

### Symptôme

Au **Run ▶** dans Android Studio, à la **synchro Gradle**, ou sur `./gradlew ...` lancé depuis VS Code :

```
FAILURE: Build failed with an exception.
* What went wrong:
  ...
Cause: jlink executable C:\Users\<user>\.vscode\extensions\redhat.java-1.55.0-win32-x64\jre\21.0.11-win32-x86_64\bin\jlink.exe does not exist
```

Symptôme voisin possible (quand un JDK 25 est choisi comme rustine) :

```
Kotlin does not yet support 25 JDK target, falling back to Kotlin JVM_24 JVM target
```

### Cause racine

1. Le fichier **`gradle/gradle-daemon-jvm.properties`** (généré par `./gradlew updateDaemonJvm`) contient `toolchainVersion=21`. Il **force Gradle à trouver un JDK 21** pour lancer le daemon.
2. La détection automatique de Gradle scanne `~/.vscode/extensions/` et y trouve la **« JRE » livrée avec l'extension VS Code « Language Support for Java by Red Hat »** :
   `~/.vscode/extensions/redhat.java-*/jre/21.0.11-win32-x86_64/`
3. Cette « JRE » est un **JDK incomplet** : elle a `javac.exe`, `jar.exe`, `jshell.exe`… mais **pas `jlink.exe`** ni le dossier `jmods/`. Gradle la sélectionne (version 21 ✓) puis échoue dès que `jlink` est requis/validé.
4. **Android Studio aggrave le cas** : il enregistre cette JRE dans sa table de JDK sous le nom `"21"` :
   ```xml
   <!-- ~/AppData/Roaming/Google/AndroidStudio<version>/options/jdk.table.xml -->
   <jdk version="2">
     <name value="21" />
     <type value="JavaSDK" />
     <homePath value="$USER_HOME$/.vscode/extensions/redhat.java-1.55.0-win32-x64/jre/21.0.11-win32-x86_64" />
   </jdk>
   ```
   et `.idea/misc.xml` référence ce nom : `project-jdk-name="21"`. Android Studio utilise donc cette JRE cassée pour Gradle.

### Diagnostic (commandes de confirmation)

```bash
# 1. Le fichier qui force la recherche existe-t-il ?
cat gradle/gradle-daemon-jvm.properties          # -> toolchainVersion=21

# 2. La JRE Red Hat est-elle incomplète ? (jlink absent = cause confirmée)
ls ~/.vscode/extensions/redhat.java-*/jre/*/bin/jlink.exe    # -> No such file

# 3. Quels JDK complets sont dispo ?
ls "$HOME/.gradle/jdks/"                          # JDK auto-provisionnés par Gradle
ls "/c/Program Files/Android/Android Studio/jbr/bin/jlink.exe"   # JBR d'Android Studio (jlink présent)
java -version ; echo "JAVA_HOME=$JAVA_HOME"

# 4. Que choisit Android Studio ?
cat .idea/gradle.xml     # <option name="gradleJvm" ...>
cat .idea/misc.xml       # project-jdk-name="..."
grep -A3 '<name value="21"' ~/AppData/Roaming/Google/AndroidStudio*/options/jdk.table.xml
```

### Correctif appliqué

Deux JDK 21 **complets** sont disponibles sur cette machine :
| Rôle | Chemin | jlink | jmods |
|---|---|---|---|
| JDK provisionné par Gradle (Adoptium 21.0.7) | `C:\Users\<user>\.gradle\jdks\eclipse_adoptium-21-amd64-windows.2` | ✅ | ✅ |
| JBR d'Android Studio (JetBrains 21.0.10) | `C:\Program Files\Android\Android Studio\jbr` | ✅ | ❌ (suffisant ici) |
| ❌ JRE Red Hat (à éviter) | `C:\Users\<user>\.vscode\extensions\redhat.java-*\jre\21.0.11-win32-x86_64` | ❌ | ❌ |

**1. Global (machine, hors dépôt) — `C:\Users\<user>\.gradle\gradle.properties` :**
```properties
org.gradle.java.home=C:\\Users\\<user>\\.gradle\\jdks\\eclipse_adoptium-21-amd64-windows.2
org.gradle.java.installations.auto-detect=false
org.gradle.java.installations.paths=C:\\Users\\<user>\\.gradle\\jdks\\eclipse_adoptium-21-amd64-windows.2
```
- `auto-detect=false` empêche Gradle de re‑trouver la JRE Red Hat sous `.vscode`.
- `org.gradle.java.home` n'est honoré **que si `gradle-daemon-jvm.properties` est absent** (le fichier de critères a la priorité dans Gradle 9.x).

**2. Dépôt — supprimer `gradle/gradle-daemon-jvm.properties`.**
Ce fichier n'apporte rien (Android Studio fournit toujours son propre JDK ; le CLI utilise `org.gradle.java.home` ou le `java` du PATH) et c'est lui qui déclenche toute la mécanique de sélection hasardeuse.
```bash
git rm gradle/gradle-daemon-jvm.properties
git commit -m "chore: supprime gradle-daemon-jvm.properties (forcait un mauvais JDK)"
```

**3. Android Studio (`.idea/`, gitignoré donc machine‑local) :**
```xml
<!-- .idea/gradle.xml -->
<option name="gradleJvm" value="jbr-21" />
```
```xml
<!-- .idea/misc.xml -->
<component name="ProjectRootManager" ... project-jdk-name="jbr-21" project-jdk-type="JavaSDK">
```
Équivalent par l'UI : *Settings → Build, Execution, Deployment → Build Tools → Gradle → **Gradle JDK** → `jbr-21`*.
> ⚠️ Éditer `jdk.table.xml` ou `.idea/*.xml` **Android Studio fermé** (sinon AS peut réécrire par‑dessus). Ou passer par l'UI puis *Sync*.

**4. Annuler les fausses pistes** (un agent IA d'Android Studio les avait tentées, sans succès) :
- ❌ NE PAS mettre `org.gradle.java.home=C:\...` en dur dans le `gradle.properties` **du dépôt** → chemin machine‑spécifique, casse les coéquipiers, et provoque l'avertissement Kotlin « JVM_24 » si le JDK est en version 25.
- ❌ NE PAS commenter le plugin `org.gradle.toolchains.foojay-resolver-convention` dans `settings.gradle.kts` → sans rapport avec le bug.
```bash
git checkout -- gradle.properties settings.gradle.kts
```

### Vérification

```bash
./gradlew --stop
./gradlew clean :app:assembleDebug
```
Attendu :
- `BUILD SUCCESSFUL`
- **aucune** ligne `jlink executable ... does not exist`
- **aucune** ligne `Kotlin does not yet support 25 JDK target` (= le daemon tourne bien en 21)
- APK dans `app/build/outputs/apk/debug/app-debug.apk`
- `./gradlew --status` → daemon `9.5.0` actif

Puis dans Android Studio : *Reload* si proposé → *Sync Project with Gradle Files* → *Run ▶*.

### Correctif permanent (recommandé — à faire une fois)

Au choix :
- **Désinstaller l'extension VS Code « Language Support for Java by Red Hat »** (aucun développement Java ne se fait dans VS Code sur ce projet — tout est dans Android Studio). Supprime la JRE incomplète à la source.
- Ou, **Android Studio fermé**, éditer `~/AppData/Roaming/Google/AndroidStudio<version>/options/jdk.table.xml` et supprimer le bloc `<jdk>` dont `<name value="21" />` pointe vers `.vscode/extensions/redhat...`.

### Environnement de référence (machine où le bug est apparu)

| | |
|---|---|
| OS | Windows 11 Home |
| Android Studio | 2026.1.1 (JBR 21.0.10) |
| Gradle | 9.5.0 (wrapper) |
| AGP / Kotlin / KSP | 9.2.1 / 2.2.10 / 2.2.10-2.0.2 |
| JDK sur le PATH | Temurin **25**.0.3 (`~/AppData/Local/Programs/Eclipse Adoptium/jdk-25.0.3.9-hotspot`) |
| `JAVA_HOME` | non défini |
| Téléphone de test | Samsung SM‑A225F (ADB) |

---

## Rappel : lancer l'app

- **Android Studio** : brancher le téléphone en **USB** (accepter « Autoriser le débogage USB »), puis **Run ▶**.
  L'ADB **sans fil** se déconnecte souvent — si `adb devices` est vide alors qu'AS montrait l'appareil, rebrancher en USB.
- **CLI** (téléphone branché) :
  ```bash
  "C:\Users\<user>\AppData\Local\Android\Sdk\platform-tools\adb.exe" install -r app/build/outputs/apk/debug/app-debug.apk
  # lancement :
  "...\adb.exe" shell am start -n com.myplaces.app/.MainActivity
  ```

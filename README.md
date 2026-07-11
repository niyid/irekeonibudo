# Ìrèké Oníbùdó — From Shipwreck to Crown

An Android narrative RPG (Kotlin, Jetpack Compose) built around an original
story inspired by the world of D.O. Fagunwa's *Ìrèké Oníbùdó* — you play
Ìrèké, an orphan swept overboard in a storm, taken captive by the sea-queen
Arogidigba, and set on a path through a serpent-haunted town toward the
wisdom of Itanforiti and, eventually, a crown. Progress depends on reading
and understanding what each character tells you: courage alone gets you
killed more than once.

## Stack
Kotlin, Jetpack Compose (Material3), Room (save/continue), a Canvas-based
procedural scene renderer (no external art assets), single-Activity + StateFlow
architecture — no Hilt, kept intentionally lean.

## Run it
```
cd ireke-onibudo
gradle wrapper --gradle-version 8.7   # only needed once, to generate gradlew
./gradlew installDebug
```
Or open the folder directly in Android Studio (Koala+) and hit Run.

## Project layout
```
app/src/main/java/com/techducat/irekeonibudo/
  data/        StoryNode/Choice/Creature/Player models, StoryData.kt (all narrative content),
               Room save-game persistence, GameState
  viewmodel/   GameViewModel — story branching + turn-based combat engine
  ui/theme/    Colors, typography, dark forest/sea theme
  ui/components/  SceneCanvas (procedural scene art), StatBar, ChoiceButton, PlayerStatusBar
  ui/screens/  Title, Story, Encounter, Inventory, Ending
  navigation/  GameNavigation.kt — screen switch driven by GameState.screen
```

## Extending the story
Everything narrative lives in `data/StoryData.kt` as plain `StoryNode` /
`Choice` / `Creature` data. Add a node, point a `Choice.nextNodeId` at it,
done — no UI code changes needed. `Choice` supports stat gates
(`requirement`), item gates (`requiresCharm`), and flag gates (`requiresFlag`
/ `setsFlag`) for tracking what the player has learned.

## Note on source material
This is an original game narrative that borrows the setting and character
names associated with Fagunwa's novel (public knowledge, not the book's
copyrighted prose or any translation). No text from the novel is reproduced
here.

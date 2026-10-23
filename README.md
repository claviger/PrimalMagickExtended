# Primal Magick Extended

An add-on for [Primal Magick](https://github.com/daedalus4096/PrimalMagick) on Minecraft 26.1.2 / NeoForge.
It requires Primal Magick and extends it; Primal Magick itself is never modified.

This mod targets the Minecraft 26.1 port of Primal Magick at https://github.com/claviger/PrimalMagick
(branch `port26integration`), since the upstream mod still targets Minecraft 1.21.1.

## Features

### Familiars
- **Familiar Collar** (Ritual Magick, expert tier): a ritual crafted from the Summon, Creature and Infernal
  runes plus a bone, a feather and a raw fish, with a ritual bell and an incense brazier nearby. Requires expert ritual casting, those three runes, and the
  Polymorph and Healing spell payloads. Use it on a pet that is tamed by you (wolf, cat, parrot, horse, donkey, mule, llama,
  nautilus, zombie nautilus, or a fox that trusts you) to bind it as your familiar.
- **Find Familiar** (Sorcery, 2 theories after the collar): a spell payload that summons your familiar if it
  is absent and dismisses it if it is present, with Polymorph's animation and sound.
- A familiar can never be hurt: any damage dismisses it instead, with no drops. Its name, collar and data
  travel with you, across death, dimensions and relogs.
- While your familiar is in play within 100 blocks, you gain a modest mana discount of about five percent (a little over two levels of Mana Efficiency).
- You keep one familiar at a time; binding another releases the first unharmed as an ordinary pet.

Everything is documented in the Grimoire.

### Commands
Game master permission, usable from the console. `[players]` defaults to whoever runs the command, so from
the console a player must be named. `/pmx` is an alias for `/primalmagickextended`.
- `/primalmagickextended familiar summon [players]`: call a dismissed familiar back.
- `/primalmagickextended familiar dismiss [players]`: send a familiar in the world away (the bond is kept).
- `/primalmagickextended familiar clear [players]`: remove the bond, as if none had ever been made. A familiar
  in the world reverts to an ordinary, still-loyal pet; a dismissed one is called back first as an ordinary pet,
  so no pet is lost.

## Building

1. Build Primal Magick first, in its own checkout:
   ```
   cd C:/tmp/pm-integration && ./gradlew clean :common:jar :neoforge:jar --no-configuration-cache
   ```
   Exactly one `primalmagick-common-*.jar` and one `primalmagick-neoforge-*.jar` must be present in that
   checkout's `common/build/libs` and `neoforge/build/libs`; the PMX build checks this and tells you what to do.
2. Point this project at that checkout with `primalmagick_dir` in `gradle.properties` (default
   `C:/tmp/pm-integration`), or pass `-Pprimalmagick_dir=/path/to/PrimalMagick`, or set it in
   `~/.gradle/gradle.properties`.
3. Build: `./gradlew build`. The mod jar is `neoforge/build/libs/primalmagickextended-neoforge-26.1.2-<version>.jar`.
   Use `./gradlew clean build` for releases; the newest jar in `neoforge/build/libs` is the current one.

At runtime the mod needs Primal Magick and Common Networking installed alongside it.

## Development

- `./gradlew :neoforge:runClient`, `:neoforge:runServer`: dev runs with Primal Magick loaded from its built jar.
- `./gradlew :neoforge:runData`: regenerate `neoforge/src/generated/resources` (lang, models, recipes,
  research entries, tags, affinities, cat variants, test instances). Commit the output.
- `./gradlew :neoforge:runGameTestServer`: run the automated game tests (only this mod's namespace runs,
  via vanilla's `--tests primalmagickextended:*` selector).

The layout, conventions and service pattern mirror Primal Magick's: `common` holds platform-neutral code,
`neoforge` holds the loader-specific code, registry holders end in `PMX`, localization lives only in the
generated `en_us.json`, and every data file comes from datagen. Platform code under
`neoforge/.../platform/registries/AbstractRegistryServicePMXNeoforge.java` is adapted from Primal Magick (MIT).

## License

MIT, like Primal Magick. See `LICENSE`.

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

### Portals
- **Portal Travel** (Manaweaving, expert tier, after Expert Manaweaving and the discovery of Infernal): Portal
  Catalysts in all 16 dye colours, crafted two at a time at an Arcane Workbench from infernal essence shards,
  ender pearls, ender eyes and a dye. Any catalyst can be recoloured in an ordinary crafting table by combining it
  with a dye.
- **Making a portal**: build a closed frame from a single full-block material, in any plane and of any shape,
  holding up to 900 portal blocks. Use a catalyst on an empty spot inside it and the frame fills with portal of
  that colour.
- **Linking**: two portals link when they have the same colour and the same frame material and are within range
  (100 blocks without runes). Each portal links to only one other. A linked portal is lit; an unlinked one stays
  dark. Stand in a lit portal for 80 ticks (4 seconds) to travel to its partner; in creative mode the vanilla
  creative portal delay applies instead. The loading screen during travel is tinted with the portal's colour.
- **Runes** are placed on any face of a frame block and count for the portal whose frame they touch; a rune on
  either end of a pair applies to both. They are crafted at an Arcane Workbench and each needs its own research
  in Runeworking. A broken rune drops its item; an explosion may destroy it, as with most blocks.

  | Rune | Research | Effect |
  |---|---|---|
  | Enhancer Rune | Portal Enhancer Runes (expert) | Range of 1,000 blocks |
  | Strong Enhancer Rune | Strong Enhancer Runes (master) | Range of 10,000 blocks |
  | Infinity Rune | Infinite Enhancer Runes (master) | Unlimited range |
  | Gate Rune | Gate Runes (supreme) | Links across dimensions; Nether X and Z coordinates count ×8 for range |
  | Haste Rune | Haste Runes (supreme) | Travel takes 1 tick instead of 80 |

- **Arrival point**: sneak-use a portal block with an empty hand to make that block the portal's arrival point.
- **Breaking** a frame block (other than replacing it with the same material) collapses the portal and drops one
  catalyst of its colour at the arrival point.
- **Redstone**: by default a powered portal goes dark. This can be changed in the config (see below).
- A portal linked to the Nether occasionally spawns zombified piglins, and one linked to the End occasionally
  spawns endermen, at vanilla Nether portal odds.

Portal gameplay options are per world, in the `[portals]` section of
`serverconfig/primalmagickextended-server.toml`; the sound options are per player, in
`config/primalmagickextended-client.toml`. Both can also be edited from the Mods screen; the per-world options only while playing a single-player or
LAN-hosted world.

| Option | Default | Meaning |
|---|---|---|
| `unlimitedRange` | `false` | Portals link regardless of distance. |
| `alwaysInterdim` | `false` | Portals link across dimensions even without a Gate Rune. |
| `alwaysHaste` | `CREATIVE` | `YES`: every player travels instantly. `NO`: only Haste Runes speed travel up. `CREATIVE`: players in creative mode use the vanilla creative delay. |
| `privatePortals` | `false` | Portals link only if both were built by the same player. Useful for servers. |
| `redstone` | `OFF` | `OFF`: a powered portal goes dark. `ON`: a portal is lit only while powered. `NONE`: redstone has no effect. |
| `defaultRange` | `100` | Range in blocks without an Enhancer Rune. |
| `rangeWithEnhancer` | `1000` | Range in blocks with an Enhancer Rune. |
| `rangeWithStrongEnhancer` | `10000` | Range in blocks with a Strong Enhancer Rune. |
| `isWhitelist` | `false` | Whether `filteredBlocks` is a whitelist (`true`) or a blacklist (`false`). |
| `filteredBlocks` | `[]` | Block ids allowed or refused as frame material, e.g. `"minecraft:stone"`. |
| `muteAmbientSounds` (client) | `false` | Mute the ambient portal sounds. |
| `muteTeleportSounds` (client) | `false` | Mute the teleport sounds. |

Everything is documented in the Grimoire: Portal Travel under Manaweaving, and the five rune entries under
Runeworking once Portal Travel is known.

### Commands
Game master permission, usable from the console. `[players]` defaults to whoever runs the command, so from
the console a player must be named. `/pmx` is an alias for `/primalmagickextended`.
- `/primalmagickextended familiar summon [players]`: call a dismissed familiar back.
- `/primalmagickextended familiar dismiss [players]`: send a familiar in the world away (the bond is kept).
- `/primalmagickextended familiar clear [players]`: remove the bond, as if none had ever been made. A familiar
  in the world reverts to an ordinary, still-loyal pet; a dismissed one is called back first as an ordinary pet,
  so no pet is lost.

### Configuration
Whole features can be turned off in the `[features]` section of `config/primalmagickextended-common.toml`, which
applies to the whole installation (every world, and the server on a dedicated server). Both are on by default:

```toml
[features]
	familiars = true
	portals = true
```

- `familiars`: the Familiar Collar ritual and the Find Familiar spell.
- `portals`: Portal Travel, the portal catalysts and the five portal runes.

A disabled feature's Grimoire entries and recipes are not loaded: they do not appear in the Grimoire (not even as
upcoming research or on other entries' pages) or in the Arcane Workbench or crafting table recipe books, even for
players who had researched them, and the Find Familiar spell is no longer offered at the Spellcrafting Altar.
Changes take effect after restarting the game or server. Turning a feature off does not remove anything already in a
world: its blocks, items and creatures stay registered, keep working, and remain available from the creative menu
and `/give`. On a dedicated server, the server's setting applies to every player.

## Credits

Portal mechanics and textures are adapted from [Custom Portals](https://github.com/Palyon-dev/CustomPortals) by
Palyon (MIT licence), ported to NeoForge by Claviger.

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

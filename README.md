# Primal Magick Extended

An add-on for [Primal Magick](https://github.com/daedalus4096/PrimalMagick) on Minecraft 1.21.1 / NeoForge.
It requires Primal Magick and extends it; Primal Magick itself is never modified.

This branch (`mc/1.21.1`) is the Minecraft 1.21.1 version, built against the official Primal Magick 6.0.7 release.
The Minecraft 26.1.2 version, which will targets the upcoming 26.1 port of Primal Magick, lives on the `main` branch.

## Requirements

- Minecraft 1.21.1
- [NeoForge](https://neoforged.net/) 21.1.132 or later
- Primal Magick 6.0.7 or later
  ([GitHub release](https://github.com/daedalus4096/PrimalMagick/releases/tag/6.0.7),
  [Modrinth](https://modrinth.com/mod/primal-magick))
- [Common Networking](https://modrinth.com/mod/common-networking) 1.0.18 or later (Primal Magick needs it too)

### Differences from the 26.1 version

The features are the same as in 0.3.0 on Minecraft 26.1, except:
- Nautiluses and zombie nautiluses do not exist in Minecraft 1.21.1, so they cannot be familiars.
- Portal data is saved in the world's `data/primalmagickextended_portals.dat`.
- Primal Magick 6.0.7 covers every natural block of Minecraft 1.21.1, so Generated Affinities only fills in blocks
  of other mods.
- Primal Magick 6.0.7 sends affinities to players as they join, so after a `/reload` players already online keep the
  affinities they had until they rejoin, as with Primal Magick's own affinities.

## Features

### Familiars
- **Familiar Collar** (Ritual Magick, expert tier): a ritual crafted from the Summon, Creature and Infernal
  runes plus a bone, a feather and a raw fish, with a ritual bell and an incense brazier nearby. Requires expert ritual casting, those three runes, and the
  Polymorph and Healing spell payloads. Use it on a pet that is tamed by you (wolf, cat, parrot, horse, donkey, mule,
  llama, trader llama, or a fox that trusts you) to bind it as your familiar.
- **Find Familiar** (Sorcery, 2 theories after the collar): a spell payload that summons your familiar if it
  is absent and dismisses it if it is present, with Polymorph's animation and sound.
- A familiar can never be hurt: any damage dismisses it instead, with no drops. Its name, collar and data
  travel with you, across death, dimensions and relogs.
- While your familiar is in play within 32 blocks, you gain a modest mana discount of about five percent (a little over two levels of Mana Efficiency).
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

### Generated Affinities
Blocks added by biome mods (Biomes O' Plenty, Terralith and the like) have no Primal Magick affinity, so they
break down to nothing. Whenever the server loads its data (at start-up and on `/reload`), every block item that has
no affinity and no recipe gets one worked out from what it is and where world generation puts it, and Primal Magick
treats it like one from a data pack. Crafted blocks made from these (planks, slabs of sandstone, ...) then get
affinities through Primal Magick's usual recipe scan.
- **What it is** picks a vanilla block to start from: block tags (`#minecraft:logs`, `#minecraft:leaves`,
  `#minecraft:sand`, ...), then the block's class, then how world generation uses it (tree trunk, foliage, ground),
  then its name. A redwood log starts from the oak log, a new flower from the dandelion.
- **Where it generates** adjusts that: Nether plants lose sun and gain infernal and moon, End plants gain void and
  moon, cave plants trade sun for moon, plants in or by water gain sea, and biome tags of the biomes it generates in
  add sea (ocean, river, beach, swamp), sun (desert, badlands, savanna, dry), sky (snowy, icy, mountain) or moon
  (spooky, dead, mushroom, magical).
- Saplings and other items dropped by worldgen blocks, stripped logs, and buckets of worldgen fluids follow their
  source.
- Crafted blocks made from these whose recipe share rounds down to nothing (slabs, buttons, stairs of a weak
  material) get 1 of each source of their ingredients instead, as Primal Magick's own data does for the oak slab.
- An affinity from a data pack always wins; generated ones only fill gaps.
- Primal Magick's `/pm affinities lint` and `/pm affinities generateDatapack` still list items whose only affinity
  is a generated one, since those are what a data pack still needs to cover, and `/pm affinities explain` says when
  an item's affinity is generated.
- Each run writes a report of every generated affinity and the reasons for it to
  `primalmagickextended/generated_affinities.md` in the game (or server) directory, and the entries in Primal Magick's
  data pack format to `generated_affinities.json` beside it, ready to copy into a data pack and adjust.

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
applies to the whole installation (every world, and the server on a dedicated server). All are on by default:

```toml
[features]
	familiars = true
	portals = true
	generated_affinities = true
```

- `familiars`: the Familiar Collar ritual and the Find Familiar spell.
- `portals`: Portal Travel, the portal catalysts and the five portal runes.
- `generated_affinities`: affinities worked out for natural blocks that have none (see Generated Affinities).

A disabled feature's Grimoire entries and recipes are not loaded: they do not appear in the Grimoire (not even as
upcoming research or on other entries' pages) or in the Arcane Workbench or crafting table recipe books, even for
players who had researched them, and the Find Familiar spell is no longer offered at the Spellcrafting Altar.
Changes take effect after restarting the game or server. Turning a feature off does not remove anything already in a
world: its blocks, items and creatures stay registered, keep working, and remain available from the creative menu
and `/give`. On a dedicated server, the server's setting applies to every player.
Turning off `generated_affinities` stops the generation; blocks that only had a generated affinity have none again.

## Credits

Portal mechanics and textures are adapted from [Custom Portals](https://github.com/Palyon-dev/CustomPortals) by
Palyon (MIT licence), ported to NeoForge by Claviger.

## Building

You need Java 21. No Primal Magick checkout is needed: the build downloads the official Primal Magick 6.0.7
NeoForge release from Modrinth's maven repository, along with its other dependencies.

- `./gradlew build` builds the mod. The jar is `neoforge/build/libs/primalmagickextended-neoforge-1.21.1-<version>.jar`.
  Use `./gradlew clean build` for releases; the newest jar in `neoforge/build/libs` is the current one.
- `./gradlew :neoforge:runGameTestServer` runs the automated game tests.
- `./gradlew :neoforge:runData` regenerates the data files (see Development).

The version comes from the git tags. Release tags on this branch carry the Minecraft version as build metadata: the
tag `0.2.0+1.21.1` gives version `0.2.0+1.21.1` and the jar `primalmagickextended-neoforge-1.21.1-0.2.0.jar`.
Builds after a tag also carry the commit count and hash, for example `0.2.0-3-gabc1234+1.21.1`. CI
(`.github/workflows/build.yml`) runs `:neoforge:build` and `:neoforge:runGameTestServer` on Java 21 for every push
and pull request to `mc/1.21.1`.

At runtime the mod needs Primal Magick and Common Networking installed alongside it.

## Development

- `./gradlew :neoforge:runClient`, `:neoforge:runServer`: dev runs with Primal Magick, Common Networking and JEI
  loaded.
- `./gradlew :neoforge:runData`: regenerate `neoforge/src/generated/resources` (lang, blockstates, models, particle
  descriptions, recipes and their advancements, research entries, tags, affinities and loot tables). Commit the
  output.
- `./gradlew :neoforge:runGameTestServer`: run the automated game tests. Only this mod's tests run: the runs set
  `neoforge.enabledGameTestNamespaces=primalmagickextended`, which keeps only tests whose structure templates are in
  this mod's namespace.

The layout, conventions and service pattern mirror Primal Magick's: `common` holds platform-neutral code,
`neoforge` holds the loader-specific code, registry holders end in `PMX`, localization lives only in the
generated `en_us.json`, and every data file comes from datagen. Platform code under
`neoforge/.../platform/registries/AbstractRegistryServicePMXNeoforge.java` is adapted from Primal Magick (MIT).

## License

MIT, like Primal Magick. See `LICENSE`.

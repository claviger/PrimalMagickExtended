v0.3.0+1.21.1
-------------
* Backport of 0.3.0 (Generated Affinities) to Minecraft 1.21.1 / NeoForge 21.1 / Primal Magick 6.0.7
* Primal Magick 6.0.7 already covers every natural block of Minecraft 1.21.1, so only blocks of other mods get generated affinities
* After a /reload, players already online keep their affinities until they rejoin, as Primal Magick 6.0.7 only sends affinities as players join

v0.3.0
------
* Added Generated Affinities: natural blocks without a Primal Magick affinity (such as those of biome mods) get one worked out from what they are and where world generation puts them, regenerated at start-up and on /reload
* Slabs, buttons and other blocks crafted from generated-affinity blocks that Primal Magick's recipe scan rounds down to nothing get 1 of each source of their ingredients
* Each run writes a report of the generated affinities and their reasons, and the entries in data pack format, to primalmagickextended/ in the game directory
* Primal Magick's /pm affinities lint and generateDatapack still list items that only have a generated affinity, and /pm affinities explain reports generated affinities as such
* Generated affinities can be turned off with generated_affinities in the common config's [features] section

v0.2.0+1.21.1
-------------
* Backport of 0.2.0 to Minecraft 1.21.1 / NeoForge 21.1 / Primal Magick 6.0.7
* Requires NeoForge 21.1.132 or later, the official Primal Magick 6.0.7 release or later, and Common Networking
* Nautiluses and zombie nautiluses cannot be familiars, since they do not exist in Minecraft 1.21.1
* Portal data is saved in the world's data/primalmagickextended_portals.dat

v0.2.0
------
* Added Portal Travel (Manaweaving, expert tier): craft Portal Catalysts at the Arcane Workbench and use one inside a closed frame of any single block to open a coloured portal
* Portals link to a compatible portal of the same colour and frame material within 100 blocks
* Added Enhancer, Strong Enhancer and Infinity Runes to extend portal range to 1,000 blocks, 10,000 blocks and unlimited
* Added Gate Runes to link portals across dimensions, and Haste Runes for instant travel
* Portal behaviour is configurable per world (range, dimensions, haste, private portals, redstone, frame filter) and sounds per client
* Portal mechanics are adapted from Custom Portals by Palyon (MIT)
* Features (familiars, portals) can each be disabled in the common config, removing their Grimoire entries and recipes

v0.1.0
------
* Initial release for Minecraft 26.1.2 on NeoForge, as an add-on to the Primal Magick 26.1 port
* Added the Familiar Collar, a ritual that lets you bind a tamed pet as your familiar
* Added the Find Familiar spell payload, to summon and dismiss your familiar
* Added /primalmagickextended familiar summon|dismiss|clear (alias /pmx) for administrators
* Familiars cannot be harmed and grant a mana discount of about five percent while nearby

# Locks (Unofficial Legacy build)

The original **Locks** by Melonslise stopped building against current 1.12.2 tooling
— old ForgeGradle/Mixin version pairings, mostly — so this exists to keep it
compiling and playable without changing what it does. All design and code credit
goes to Melonslise; this isn't affiliated with or endorsed by the original author.

While rebuilding it, one real bug also got fixed: some mods (Vanilla Builders
Extension being the one that surfaced it) call `Block.getExplosionResistance` with a
null `World` during block construction, which used to crash. That's patched now.

## Requirements

- Minecraft 1.12.2, Forge 14.23.5.2864 or newer
- **MixinBooter** — required, the mod will not launch without it. Confirmed working
  against MixinBooter 11.15; older versions weren't tested, so if you're on
  something significantly older and hit problems, updating MixinBooter first is
  worth trying before filing an issue against this mod.

That second one is new compared to the original jar. The original shaded its own
private copy of the Mixin library inside the jar, so it never needed anything else
installed. This build doesn't do that — it relies on MixinBooter to own the shared
Mixin subsystem instead. Grab MixinBooter from CurseForge or Modrinth if you don't
already have it; a lot of 1.12.2 modpacks pull it in anyway as a dependency of other
mods.

If MixinBooter is missing, Forge will tell you plainly at launch rather than the game
just crashing with an unreadable stack trace.

## Installing

Drop the built jar in your `mods` folder alongside MixinBooter. That's it — no config
files need to exist beforehand, the mod creates its own on first run.

## Building from source

Standard ForgeGradle 3 workflow:

```
./gradlew build
```

(`gradlew.bat build` on Windows). The compiled jar shows up under `build/libs/`.

If you're setting up a dev environment in an IDE, the usual `genEclipseRuns` /
`genIntellijRuns` Gradle tasks work as expected — nothing unusual there.

## Known issues

See `KNOWN_ISSUES.md`.

## Credits

See `CREDITS.txt` for the full list of people whose earlier work and help made the
original mod possible.
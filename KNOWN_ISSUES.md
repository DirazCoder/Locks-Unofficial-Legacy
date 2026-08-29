# Known issues and rebuild notes

This file exists mostly for whoever touches this build next, including future-me.
Recompiling a decompiled 1.12.2 jar against current Forge/Mixin tooling hit a stack
of unrelated problems, and it's worth writing down what actually broke and why,
rather than leaving the next person to rediscover it.

## Requires MixinBooter now — didn't used to

The original jar shaded its own private copy of the Mixin library, so it bootstrapped
Mixin itself and never touched anything else on the classpath. Getting the annotation
processor to actually resolve MCP names correctly meant switching to a plain
`mixin:0.8` dependency instead (see the ForgeGradle/Mixin version pairing note below)
— which means this build no longer carries its own Mixin, and needs MixinBooter to
supply and own the shared Mixin subsystem at runtime.

One consequence of that: `MixinLoader`'s constructor used to call
`MixinBootstrap.init()` and `Mixins.addConfiguration("mixins.locks.json")` directly,
which was correct for a self-contained shaded build. In a MixinBooter environment
that's already been bootstrapped elsewhere, calling it again from here collided with
MixinBooter's own init and silently dropped the mixin registration — no error, no
crash, the mixins just never applied. `mixins.locks.json` never showed up in the log
next to every other mod's "Adding [...] mixin configuration" line, which is what
eventually gave it away. Fixed by leaving `MixinConfigs: mixins.locks.json` in the jar
manifest (which MixinBooter reads directly) and gutting the manual bootstrap call in
`MixinLoader` down to a no-op.

If MixinBooter is missing entirely, Forge now fails at launch with a normal "missing
required dependency" message (`required-after:mixinbooter` on the `@Mod` annotation)
instead of a bare `ClassNotFoundException: org.spongepowered.asm.launch.MixinTweaker`.

## Mixin/MixinGradle version pairing is exact, not approximate

`mixingradle:0.7-SNAPSHOT` only pairs correctly with `mixin:0.8`. Earlier attempts at
`0.7.11-SNAPSHOT` and `0.8.5-SNAPSHOT` both looked plausible and both failed in
different ways:

- `0.7.11-SNAPSHOT` compiled, but the AP silently never loaded any SRG/MCP mapping
  file at all — the giveaway is the compile log missing the `Note: Loading searge
  mappings from ...` / `Note: Loading notch mappings from ...` lines a working setup
  always prints right after `ObfuscationServiceMCP supports type: "searge"/"notch"`.
  Every `@Inject`/`@Shadow`/`@Invoker` target that needed real remapping failed with
  "No obfuscation mapping" — not because the mapping data was wrong (verified twice,
  down to the tsrg and the actual compiled classpath jar via `javap`), but because the
  AP-to-MixinGradle wiring itself never fired for that version combination.
- `0.8.5-SNAPSHOT` compiled but with all SRG names leaking through unmapped, since
  that line uses a different classifier convention (`:processor`) and different
  wiring than what `mixingradle:0.7-SNAPSHOT` expects.

`mixin:0.8` is the one MixinGradle's own README compatibility table actually lists
for `mixingradle:0.7-SNAPSHOT`. Don't swap either version without checking that table
again first.

## Source needed a full MCP remap before it would compile at all

The decompiled source this fork started from was full of raw SRG names
(`func_71410_x`, `field_71071_by`, etc.) instead of MCP names — not because the
original author wrote it that way, but because SRG names are what's actually baked
into compiled bytecode; MCP names only exist as a dev-time convenience layer that
gets thrown away at compile time. Decompiling a shipped jar naturally reconstructs
SRG names, since that's genuinely what's in the class files.

Fixed with a one-time mechanical remap against ForgeGradle's own cached
`mcp_snapshot-20171003-1.12.zip` / `srg_to_snapshot_20171003-1.12.tsrg`
(896 replacements across 47 files). If you ever pull in newer decompiled source for
this mod version, expect to need the same pass again.

## Decompiler-introduced generic erasure

Separately from the SRG naming, the decompiler reconstructed a lot of generics
incorrectly — bogus `(Object)` casts inserted wherever it couldn't recover the real
type parameter from erased bytecode, plus a few genuine type errors (a `byte[]`
narrowing conversion missing its cast, one real duplicate-import collision between
two different classes both named `PlayerEvent`). All of that's been cleaned up; if
you see a raw `List`/`Map` or a stray `(Object)` cast anywhere in this codebase going
forward, it's most likely new, not a repeat of this.
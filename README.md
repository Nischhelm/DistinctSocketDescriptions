# Distinct Socketed Descriptions
 Socketed Expansion for DDD

## Effects

| Effect Type | Fields | Description |
|---|---|---|
| `DDD Damage` | Damage Type, Amount, Operation | Modifies the damage of a damage type, see Operation |
| `DDD Resistance` | Damage Type, Amount | Adds resistance to a damage type (0.5 = +50%, negative values reduce resistance) |
| `DDD Apply Immunity` | Damage Type | Adds immunity to a damage type |
| `DDD Remove Immunity` | Damage Type | Removes immunity to a damage type |

All effects also take the shared fields:

- `Equipment Slot Type`: required, the Socketed slot type of the effect
- `Damage Type`: required, any registered DDD damage type, for example `ddd_fire`
- `Directly Activated`: optional, default `false`
  - `false`: the effect is active as long as the item is equipped in a valid slot
  - `true`: the effect only activates from the item that is used: the main hand weapon when attacking, or the hit armor pieces when attacked
- `Activation`: optional, `ATTACKING` or `ATTACKED`
  - `ATTACKING`: applies when the player attacks
  - `ATTACKED`: applies when the player gets attacked
  - If not set, it depends on the slot type: hand slots (`HAND`, `MAINHAND`, `OFFHAND`) are `ATTACKING`, body slots (`BODY`, `HEAD`, `CHEST`, `LEGS`, `FEET`) are `ATTACKED`. Any other slot type (for example `ALL`) uses the effects default: `ATTACKING` for DDD Damage and DDD Remove Immunity, `ATTACKED` for DDD Resistance and DDD Apply Immunity

`DDD Damage` additionally takes:

- `Operation`: optional, default `ADD`

| Operation | Amount | Example Tooltip | What it does |
|---|---|---|---|
| `ADD` | flat damage | +5 Fire Damage | Adds the amount to the damage of the type |
| `MULTIPLY_EXTRA` | 0.5 = 50% | +50% Total Damage as Fire | Adds the amount times the total damage as damage of the type |
| `CONVERT` | 0.3 = 30% | Turn 30% Damage to Fire | Turns the amount of the damage of every other type into the type, the total damage stays the same |
| `MULTIPLY_TYPE` | factor, 1.5 = x1.5 | x1.5 Fire Damage | Multiplies the damage of the type, does nothing if the hit doesn't deal damage of that type |

All damage effects active in one hit (from the attacker and the defender) get applied together in a fixed order, so the order of gems doesn't matter:

1. `CONVERT`: all converts read the damage from before any conversion. Converts to the same type add up. A type never converts to itself, so "Turn 30% Damage to Fire" leaves existing fire damage alone and takes 30% of every other type. If the converts a type gives up add up to more than 100% (for example 80% to Fire and 60% to Cold), they get scaled down proportionally (57% Fire, 43% Cold), so no damage gets created or lost
2. `ADD` and `MULTIPLY_EXTRA`: `MULTIPLY_EXTRA` uses the total damage after conversion and before any `ADD` or `MULTIPLY_EXTRA`, so multiple `MULTIPLY_EXTRA` effects don't feed into each other
3. `MULTIPLY_TYPE`: multipliers of the same type add up, two x1.5 make x2

Damage of a type can't go below 0. `CONVERT` and `MULTIPLY_TYPE` don't allow negative amounts.

Directly activated effects only ever run from the hand when attacking and from the body when attacked, so their Activation should match their slot type.

## What each effect does depending on Activation

It doesn't always make sense to use every effect with every activation, even though it is possible.

#### DDD Resistance

- `ATTACKED`: modifies the defending players resistances, so you can for example add +50% fire damage resistance
- `ATTACKING`: modifies the attacked targets resistances, so you can for example reduce the mobs fire resistance by -50%

#### DDD Damage

- `ATTACKING`: modifies the attacking players damage, so you can for example add +5 fire damage or turn 30% of the damage to fire
- `ATTACKED`: modifies the attacking mobs damage, so you could for example reduce their fire damage by -5 or multiply it by 0.5, which is a weird approach. Recommended is using DDD Resistance for that. This only works for non directly activated effects

#### DDD Apply Immunity / DDD Remove Immunity

- `ATTACKED`: adds/removes immunities of the defending player
- `ATTACKING`: adds/removes immunities of the attacked target, so you can for example remove a mobs fire immunity

## FirstAid

With FirstAid installed, directly activated armor effects only activate from the armor pieces of the body parts FirstAid actually distributes damage to.
